package com.app.wi_fi_direct.helpers;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class TransferData extends AsyncTask<Void, Void, Void> {
  public Context context;
  public ArrayList<Uri> uris;
  public ArrayList<String> fileNames;
  public InetAddress serverAddress;

  private ArrayList<File> files;

  public TransferData(Context context, ArrayList<Uri> uris,
                      ArrayList<String> fileNames, ArrayList<File> files, InetAddress serverAddress) {
    this.context = context;

    this.uris = uris;
    this.fileNames = fileNames;
    this.files = files;

    this.serverAddress = serverAddress;

    Log.d(" DEBUG::::   ", serverAddress.getHostAddress());

    Toast.makeText(context, "Transfer Started", Toast.LENGTH_SHORT).show();
  }

  private void sendData(Context context, ArrayList<Uri> uris) {

    int len;
    byte buf[] = new byte[16 * 1024];

    Log.d("Data Transfer", "Transfer Starter");

    Socket socket = new Socket();

    try {
      socket.bind(null);
      Log.d("Client Address", socket.getLocalSocketAddress().toString());

      socket.connect(new InetSocketAddress(serverAddress, 8888));
      Log.d("Client", "Client Connected");

      OutputStream outputStream = socket.getOutputStream();
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
      ContentResolver cr = context.getContentResolver();

      objectOutputStream.writeInt(uris.size());

      for (int i = 0; i < uris.size(); i++) {

        InputStream inputStream = cr.openInputStream(uris.get(i));
        objectOutputStream.writeUTF(fileNames.get(i));
        objectOutputStream.writeLong(files.get(i).length());

        Log.d("Sender", (files.get(i).length() + "  file size"));

        while ((len = inputStream.read(buf)) != -1) {
          objectOutputStream.write(buf, 0, len);

          Log.d("Sender", "Writing Data");
        }
        inputStream.close();
      }

      objectOutputStream.close();
      socket.close();

    } catch (Exception e) {
      Log.d("Data Transfer", e.toString());
      e.printStackTrace();
    } finally {
      if (socket.isConnected()) {
        try {
          socket.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

  }

  @Override
  protected Void doInBackground(Void... params) {
    sendData(context, uris);
    return null;
  }

  @Override
  protected void onPostExecute(Void aVoid) {
    super.onPostExecute(aVoid);
    Toast.makeText(context, "Data Transferred!", Toast.LENGTH_SHORT).show();
    Log.d("Sender", "Finished!");
  }

  @Override
  protected void onCancelled() {
    super.onCancelled();
    Log.d("Sender", "Cancelled!");
  }
}
