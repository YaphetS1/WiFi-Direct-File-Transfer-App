package com.app.wi_fi_direct.servers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.app.wi_fi_direct.Variables;
import com.app.wi_fi_direct.adapters.FilesAdapter;
import com.app.wi_fi_direct.adapters.FilesViewHolder;
import com.app.wi_fi_direct.helpers.callbacks.Callback;
import com.app.wi_fi_direct.models.CustomObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by dmitryMarinin on 23.03.2018.
 */

public class FileServerAsyncTask extends AsyncTask<Void, CustomObject, Void> {

  private Context context;
  private ServerSocket serverSocket;
  private Socket client;
  private File file;
  private Long fileSize;
  private Long fileSizeOriginal;
  private FilesAdapter fileList;
  private Callback referenceCallback;
  private SharedPreferences sharedPreferences;


  public FileServerAsyncTask(Context contextWeakReference,
                             ServerSocket reference,
                             FilesAdapter fileListAdapterWeakReference, Callback callback) {
    this.context = contextWeakReference;
    this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(contextWeakReference);
    this.serverSocket = reference;
    this.fileList = fileListAdapterWeakReference;
    this.referenceCallback = callback;
  }

  private void recieveData() {

    byte buf[] = new byte[8192];
    int len = 0;

    try {
      Log.d("Reciever", "Server Listening");
      Log.d("Reciever Address", serverSocket.getLocalSocketAddress().toString());
      Log.d("Reciever Port", String.valueOf(serverSocket.getLocalPort()));
      client = serverSocket.accept();
      Log.d("Reciever", "Server Connected");
      if (isCancelled()) return;

      InputStream inputStream = client.getInputStream();
      ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

      //We get size of items to be receive
      int sizeOfItems = objectInputStream.readInt();

      //Get filenames and size for know to read file
      ArrayList<String> fileNames = (ArrayList<String>) objectInputStream.readObject();
      ArrayList<Long> fileSizes = (ArrayList<Long>) objectInputStream.readObject();

      for (int i = 0; i < sizeOfItems; i++) {

        String fileName = fileNames.get(i);
        fileSize = fileSizes.get(i);
        fileSizeOriginal = fileSizes.get(i);
        file = new File(sharedPreferences.getString(Variables.APP_TYPE, Environment.getExternalStorageDirectory() + "/"
            + context.getApplicationContext().getPackageName()) + "/" + fileName);
        Log.d("Reciever", file.getPath());


        File dir = file.getParentFile();
        if (!dir.exists()) dir.mkdirs();
        if (file.exists()) file.delete();
        if (file.createNewFile()) {
          Log.d("Reciever", "File Created");

        } else Log.d("Reciever", "File Not Created");

        OutputStream outputStream = new FileOutputStream(file);

        //customObject need for progress update
        CustomObject progress = new CustomObject();
        progress.name = fileName;
        progress.dataIncrement = 0;
        progress.totalProgress = 0;

        try {

          while (fileSize > 0 &&
              (len = objectInputStream.read(buf, 0, (int) Math.min(buf.length, fileSize))) != -1) {

            outputStream.write(buf, 0, len);
            outputStream.flush();

            fileSize -= len;

            progress.dataIncrement = (long) len;
            if (((int) (progress.totalProgress * 100 / fileSizeOriginal)) ==
                ((int) ((progress.totalProgress + progress.dataIncrement) * 100 / fileSizeOriginal))) {
              progress.totalProgress += progress.dataIncrement;
              continue;
            }

            progress.totalProgress += progress.dataIncrement;
            publishProgress(progress);
            if (this.isCancelled()) return;

          }

          Log.d("Reciever", "Writing Data Final   -" + len);
        } catch (Exception e) {
          Log.d("Reciever", "oops");
          e.printStackTrace();
        }

        outputStream.flush();
        outputStream.close();

      }

      objectInputStream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  protected Void doInBackground(Void... params) {
    recieveData();
    return null;
  }

  @Override
  protected void onProgressUpdate(CustomObject... values) {
    super.onProgressUpdate(values);
    boolean isNotContain = true;

    for (int i = 0; i < this.fileList.receivedFiles.length; i++) {
      if (this.fileList.receivedFiles[i].getName().equals(values[0].name)) {
        isNotContain = false;
        if (this.fileList.filesViewHolders.size() != this.fileList.receivedFiles.length) {
          break;
        }
        FilesViewHolder holder = this.fileList.filesViewHolders.get(i);
        if (holder.progressBar.getVisibility() != View.VISIBLE) {
          holder.progressBar.setVisibility(View.VISIBLE);
        }
        holder.progressBar.setProgress((int) ((values[0].totalProgress * 100) / fileSizeOriginal));
      }
    }
    if (isNotContain) {
      this.fileList.notifyAdapter();
    }
  }

  @Override
  protected void onPostExecute(Void aVoid) {
    super.onPostExecute(aVoid);
    this.fileList.notifyAdapter();

    Log.d("Reciever", "onPostExecute");
    try {
//      serverSocket.close();
      client.close();
      referenceCallback.call();
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      if (this.fileList.filesViewHolders.size() != 0) {
        this.fileList.filesViewHolders
            .get(this.fileList.filesViewHolders.size() - 1).progressBar.setVisibility(View.INVISIBLE);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void onCancelled() {
    super.onCancelled();
    Log.d("Reciever", "Transfer Cancelled");
    try {
//      if (client.isConnected()) serverSocket.close();
      client.close();
      referenceCallback.call();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

