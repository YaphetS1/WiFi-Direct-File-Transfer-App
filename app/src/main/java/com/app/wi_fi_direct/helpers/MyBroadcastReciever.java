package com.app.wi_fi_direct.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MyBroadcastReciever extends BroadcastReceiver {

  WifiP2pManager p2pManager;
  Context context;
  WifiP2pManager.Channel channel;
  WifiP2pManager.PeerListListener peerListListener;
  WifiP2pManager.ConnectionInfoListener infoListener;
  TextView textView;

  public MyBroadcastReciever(WifiP2pManager p2pManager, WifiP2pManager.Channel channel, Context context, WifiP2pManager.ConnectionInfoListener infoListener) {
    super();
    this.channel = channel;
    this.context = context;
    this.p2pManager = p2pManager;
    this.infoListener = infoListener;
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    String action = intent.getAction();
    //if(isInitialStickyBroadcast()) return;

    if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
      p2pManager.requestPeers(channel, peerListListener);
      Toast.makeText(context, WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION, Toast.LENGTH_SHORT).show();
    } else if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
      //Toast.makeText(context,WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION,Toast.LENGTH_SHORT).show();
    } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
      NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
      if (networkInfo.isConnected()) {
        //connected
        p2pManager.requestConnectionInfo(channel, infoListener);
      } else {
        //disconnected
      }

      Log.d("BroadCast", "WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION");

    } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
      if (textView == null) return;
      WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
      String thisDeviceName = device.deviceName;
//      textView.setText(thisDeviceName);
      //Toast.makeText(context,WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION,Toast.LENGTH_SHORT).show();
    }


  }

  public void setPeerListListener(WifiP2pManager.PeerListListener peerListListener) {
    this.peerListListener = peerListListener;
  }
}
