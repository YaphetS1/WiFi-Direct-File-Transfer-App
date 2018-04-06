package com.app.wi_fi_direct.helpers;

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

public class TransferData extends AsyncTask<Void, Integer, Void> {
  private Context context;
  private FilesSendAdapter sendFilesAdapter;
  private ArrayList<Uri> uris;
  private ArrayList<String> fileNames;
  private InetAddress serverAddress;
  private WifiP2pManager manager;
  private WifiP2pManager.Channel channel;

  private ArrayList<Long> filesLength;
  private boolean needToUpdateIndex = false;

  public TransferData(Context context, FilesSendAdapter referenceSendFilesAdapter,
                      InetAddress serverAddress,
                      final WifiP2pManager manager,
                      final WifiP2pManager.Channel channel) {
    this.context = context;
    this.channel = channel;
    this.manager = manager;

    this.sendFilesAdapter = referenceSendFilesAdapter;

    this.uris = referenceSendFilesAdapter.uris;
    this.fileNames = referenceSendFilesAdapter.fileNames;
    this.filesLength = referenceSendFilesAdapter.filesLength;

    this.serverAddress = serverAddress;

    Log.d(" DEBUG::::   ", serverAddress.getHostAddress());

    Toast.makeText(context, "Transfer Started", Toast.LENGTH_SHORT).show();
  }

  private void sendData(Context context, ArrayList<Uri> uris) {

    int len;
    byte buf[] = new byte[1024];

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

//          Log.d("Sender", "Writing Data");
        }
        inputStream.close();
        publishProgress(i);
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
    if (this.sendFilesAdapter.isHaveNotTransferred()) {
      this.needToUpdateIndex = true;
      this.uris = new ArrayList<>(this.uris.subList(this.sendFilesAdapter.index, this.uris.size()));
      this.fileNames = new ArrayList<>(this.fileNames.subList(this.sendFilesAdapter.index, this.fileNames.size()));
      this.filesLength = new ArrayList<>(this.filesLength.subList(this.sendFilesAdapter.index, this.filesLength.size()));
      sendData(context, uris);
    }
    return null;
  }

  @Override
  protected void onProgressUpdate(Integer... values) {
    super.onProgressUpdate(values);
    if (needToUpdateIndex) {
      this.sendFilesAdapter.filesViewHolders
          .get(
              this.sendFilesAdapter.index + values[0]
          ).stateFile.setImageResource(R.drawable.d_icon_done);
    }
    else {
      this.sendFilesAdapter.filesViewHolders
          .get(
              values[0]
          ).stateFile.setImageResource(R.drawable.d_icon_done);
    }
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
