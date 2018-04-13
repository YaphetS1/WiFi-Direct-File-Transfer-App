package com.app.wi_fi_direct.servers;

import android.os.AsyncTask;
import android.util.Log;

import com.app.wi_fi_direct.services.MyBroadcastReciever;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TransferNameDevice extends AsyncTask<Void, Void, Void> {
  private InetAddress serverAddress;

  public TransferNameDevice(InetAddress serverAddress) {
    this.serverAddress = serverAddress;
  }

  private void sendData() {

    Socket socket = new Socket();

    try {
      socket.bind(null);
      Log.d("Client Address", socket.getLocalSocketAddress().toString());

      socket.connect(new InetSocketAddress(serverAddress, 8887));
      Log.d("Client", "Client Connected 8887");

      OutputStream outputStream = socket.getOutputStream();
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

      objectOutputStream.writeObject(MyBroadcastReciever.thisDeviceName);
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
    sendData();
    return null;
  }

  @Override
  protected void onPostExecute(Void aVoid) {
    super.onPostExecute(aVoid);
    Log.d("Sender", "Finished!");
  }

  @Override
  protected void onCancelled() {
    super.onCancelled();
    Log.d("Sender", "Cancelled!");
  }
}
