package com.app.wi_fi_direct.servers;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.app.wi_fi_direct.R;
import com.app.wi_fi_direct.adapters.FilesSendAdapter;

import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class TransferData extends AsyncTask<Void, String, Void> {
  private Context context;
  private FilesSendAdapter sendFilesAdapter;
  private ArrayList<Uri> uris;
  private ArrayList<String> fileNames;
  private InetAddress serverAddress;
  private WifiP2pManager manager;
  private WifiP2pManager.Channel channel;

  private ArrayList<Long> filesLength;
  private boolean needToUpdateIndex = false;

  public TransferData(Context context,
                      ArrayList<Uri> uris,
                      ArrayList<Long> filesLength,
                      ArrayList<String> fileNames,
                      FilesSendAdapter referenceSendFilesAdapter,
                      InetAddress serverAddress,
                      final WifiP2pManager manager,
                      final WifiP2pManager.Channel channel) {
    this.context = context;
    this.channel = channel;
    this.manager = manager;

    this.sendFilesAdapter = referenceSendFilesAdapter;

    this.uris = uris;
    this.fileNames = fileNames;
    this.filesLength = filesLength;

    this.serverAddress = serverAddress;

    Log.d(" DEBUG::::   ", serverAddress.getHostAddress());
  }

  private void sendData(Context context, ArrayList<Uri> uris) {

    int len = 0;
    byte buf[] = new byte[8192];

    Log.d("Data Transfer", "Transfer Starter");

    Socket socket = new Socket();

    try {
      socket.bind(null);
      Log.d("Client Address", socket.getLocalSocketAddress().toString());

      socket.connect(new InetSocketAddress(serverAddress, 8888));
      Log.d("Client", "Client Connected 8888");

      OutputStream outputStream = socket.getOutputStream();
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
      ContentResolver cr = context.getContentResolver();

      objectOutputStream.writeInt(uris.size());

      objectOutputStream.writeObject(fileNames);
      objectOutputStream.flush();

      objectOutputStream.writeObject(filesLength);
      objectOutputStream.flush();

      for (int i = 0; i < uris.size(); i++) {

        InputStream inputStream = cr.openInputStream(uris.get(i));

        while ((len = inputStream.read(buf)) != -1) {
          objectOutputStream.write(buf, 0, len);
          objectOutputStream.flush();
        }
        inputStream.close();
        publishProgress(fileNames.get(i));

        Log.d("TRANSFER", "Writing Data Final   -" + len);

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
  protected void onProgressUpdate(String... values) {
    super.onProgressUpdate(values);
    for (int i = 0; i < this.sendFilesAdapter.filesViewHolders.size(); i++) {
      if (this.sendFilesAdapter.filesViewHolders.get(i).fileModel.getFileName().equals(values[0])) {
        this.sendFilesAdapter.filesViewHolders
            .get(i).stateFile.setImageResource(R.drawable.d_icon_done);
      }
    }
  }

  @Override
  protected void onPostExecute(Void aVoid) {
    super.onPostExecute(aVoid);
    Log.d("Sender", "Finished!");
  }

  @Override
  protected void onCancelled() {
    super.onCancelled();
  }
}
