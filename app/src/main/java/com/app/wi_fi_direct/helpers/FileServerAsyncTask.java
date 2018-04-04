package com.app.wi_fi_direct.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.app.wi_fi_direct.adapters.FilesAdapter;

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
  private FilesAdapter fileList;

//  private TextView tvFileName;
//  private ProgressBar progressBar;

  public FileServerAsyncTask(Context contextWeakReference,
                             ServerSocket reference,
                             FilesAdapter fileListAdapterWeakReference) {
    this.context = contextWeakReference;
    this.serverSocket = reference;
    this.fileList = fileListAdapterWeakReference;
  }

  private void recieveData() {
    byte buf[] = new byte[1024];
    int len = 0;

    try {
      Log.d("Reciever", "Server Listening");
      Log.d("Reciever Address", serverSocket.getLocalSocketAddress().toString());
      Log.d("Reciever Port", String.valueOf(serverSocket.getLocalPort()));
      //serverSocket.setSoTimeout(1000000);
      client = serverSocket.accept();
      Log.d("Reciever", "Server Connected");
      if (isCancelled()) return;

      InputStream inputStream1 = client.getInputStream();
      ObjectInputStream inputStream = new ObjectInputStream(inputStream1);

      int sizeOfItems = inputStream.readInt();

      ArrayList<String> fileNames = (ArrayList<String>) inputStream.readObject();
      ArrayList<Long> fileSizes = (ArrayList<Long>) inputStream.readObject();

      for (int i = 0; i < sizeOfItems; i++) {

        String fileName = fileNames.get(i);
        fileSize = fileSizes.get(i);
        file = new File(Environment.getExternalStorageDirectory() + "/"
            + context.getApplicationContext().getPackageName() + "/" + fileName);
        Log.d("Reciever", file.getPath());


        File dir = file.getParentFile();
        if (!dir.exists()) dir.mkdirs();
        if (file.exists()) file.delete();
        if (file.createNewFile()) {
          Log.d("Reciever", "File Created");

        } else Log.d("Reciever", "File Not Created");

        OutputStream outputStream = new FileOutputStream(file);
        CustomObject progress = new CustomObject();
        progress.name = fileName;
        progress.dataIncrement = 0;
        progress.totalProgress = 0;

        try {
//          while (((len = inputStream.read(buf)) != -1))
          while (fileSize > 0 &&
              (len = inputStream.read(buf, 0, (int)Math.min(buf.length, fileSize))) != -1) {

            outputStream.write(buf, 0, len);
            fileSize -= len;

//            progress.dataIncrement = (long) len;
//            if (((int) (progress.totalProgress * 100 / fileSize)) ==
//                ((int) ((progress.totalProgress + progress.dataIncrement) * 100 / fileSize))) {
//              progress.totalProgress += progress.dataIncrement;
//              continue;
//            }
//
//            progress.totalProgress += progress.dataIncrement;
//            publishProgress(progress);
            if (this.isCancelled()) return;
//            Log.d("Reciever", "Writing Data IN WHILE   - " + len);
          }

          Log.d("Reciever", "Writing Data Final   -" + len);
        } catch (Exception e) {
          Log.d("Reciever", "oops");
          e.printStackTrace();
        }

        outputStream.flush();
        outputStream.close();

      }

      inputStream.close();
      //serverSocket.close();
      //client.close();

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

    //TODO: Add progress bar to any files
//    if (progressBar.getVisibility() != View.VISIBLE) progressBar.setVisibility(View.VISIBLE);
//    if (tvFileName.getText().equals("")) tvFileName.setText(values[0].name);
//    progressBar.setProgress((int) ((values[0].totalProgress * 100) / fileSize));
  }

  @Override
  protected void onPostExecute(Void aVoid) {
    super.onPostExecute(aVoid);
//    progressBar.setProgress(100);
    fileList.notifyAdapter();
    Toast.makeText(context, "File Transferred!", Toast.LENGTH_LONG).show();
    Log.d("Reciever", "onPostExecute");
    Log.d("Reciever", file.length() + " file size");
    try {
      serverSocket.close();
      client.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Override
  protected void onCancelled() {
    super.onCancelled();
    Toast.makeText(context, "Transfer Cancelled", Toast.LENGTH_LONG).show();
    Log.d("Reciever", "Transfer Cancelled");
    try {
      if (client.isConnected()) serverSocket.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

