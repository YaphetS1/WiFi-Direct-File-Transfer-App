package com.app.wi_fi_direct.services;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

public class ConnectPeer {

  public static void connect(String deviceAddress, final WifiP2pManager manager, final WifiP2pManager.Channel channel, final Context context, WifiP2pManager.ActionListener listener) {
    final WifiP2pConfig config = new WifiP2pConfig();
    config.deviceAddress = deviceAddress;
    config.groupOwnerIntent = 0;

    Log.d("Connect", "initiated");
    manager.connect(channel, config, listener);

  }

  public static void disconnect(final WifiP2pManager manager, final WifiP2pManager.Channel channel) {
    manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
      @Override
      public void onSuccess() {
        Log.d("Disconnect PEER", "onSuccess");
      }

      @Override
      public void onFailure(int reason) {
        Log.d("Disconnect PEER", "onFailure" + String.valueOf(reason));
      }
    });
  }

}
