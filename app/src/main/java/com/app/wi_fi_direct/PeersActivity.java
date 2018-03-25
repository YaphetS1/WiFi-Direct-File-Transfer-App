package com.app.wi_fi_direct;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PeersActivity extends AppCompatActivity {

  public WifiP2pManager p2pManager;
  public WifiP2pManager.Channel channel;
  public IntentFilter intentFilter;
  public MyBroadcastReciever myBroadcastReciever;
  public WifiP2pManager.PeerListListener peerListListener;
  public List<WifiP2pDevice> peerList;
  public PeersAdapter peersAdapter;
  public RecyclerView recyclerView;
  public InetAddress serverAddress;
  public WifiP2pManager.ConnectionInfoListener infoListener;
  public TextView deviceName;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_peers);

    deviceName = findViewById(R.id.peers_name);

        /*if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/

    Log.d("Send Activity", "onCreate");

    peerList = new ArrayList();
        /*WifiP2pDevice device1=new WifiP2pDevice();
        device1.deviceAddress="one";
        device1.deviceName="Device 1";
        peerList.add(device1);
        WifiP2pDevice device2=new WifiP2pDevice();
        device2.deviceAddress="two";
        device2.deviceName="Device 2";
        peerList.add(device2);
        WifiP2pDevice device3=new WifiP2pDevice();
        device3.deviceAddress="three";
        device3.deviceName="Device 3";
        peerList.add(device3);*/

    peerListListener = peers -> {
      peerList = new ArrayList();
      peerList.clear();
      peerList.addAll(peers.getDeviceList());
      peersAdapter.updateList(peerList);
      peersAdapter.notifyDataSetChanged();
      //Toast.makeText(getApplicationContext(),String.valueOf(peers.getDeviceList().size()),Toast.LENGTH_LONG).show();
    };

    infoListener = info -> {
      serverAddress = info.groupOwnerAddress;
      if (serverAddress == null) return;
      Toast.makeText(getApplicationContext(), "Am I Group Owner" + String.valueOf(info.isGroupOwner), Toast.LENGTH_LONG).show();
      Toast.makeText(PeersActivity.this, "Info Recieved " + serverAddress.toString(), Toast.LENGTH_LONG).show();
      Log.d("Server Data", info.toString());
      Toast.makeText(getApplicationContext(), "Info " + info.groupFormed, Toast.LENGTH_LONG).show();
      ChooseFile.fileChooser(PeersActivity.this);
    };

    p2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
    channel = p2pManager.initialize(this, getMainLooper(), null);
    p2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
      @Override
      public void onSuccess() {
        Toast.makeText(getApplicationContext(), "SuccessrG", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onFailure(int reason) {
        Toast.makeText(getApplicationContext(), "FailrG", Toast.LENGTH_SHORT).show();
        Log.d("reason", reason + "");
      }
    });
    //Toast.makeText(this,channel.toString(),Toast.LENGTH_LONG).show();
    myBroadcastReciever = new MyBroadcastReciever(p2pManager, channel, this, infoListener);
    myBroadcastReciever.setPeerListListener(peerListListener);

    intentFilter = new IntentFilter();
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

    registerReceiver(myBroadcastReciever, intentFilter);

    peersAdapter = new PeersAdapter(peerList, this, p2pManager, channel, this, infoListener);
    recyclerView = findViewById(R.id.peersRecycler);
    recyclerView.setAdapter(peersAdapter);
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
    recyclerView.setLayoutManager(mLayoutManager);

    p2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
      @Override
      public void onSuccess() {
        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onFailure(int reason) {
        Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
      }
    });

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Log.d("DEBUG", String.valueOf(requestCode));
    switch (requestCode) {
      case ChooseFile.FILE_TRANSFER_CODE:
        if (data == null) return;
        Uri uri = data.getData();
        try {
          String fileName = PathUtil.getPath(getApplicationContext(), uri);
          File file = new File(fileName);
          fileName = FilesUtil.getFileName(fileName);
          Log.d("File Path", fileName);
          TransferData transferData = new TransferData(this, file, fileName, serverAddress);
          transferData.execute();
        } catch (Exception e) {
          e.printStackTrace();
        }
    }

  }

//  @Override
//  public boolean onCreateOptionsMenu(Menu menu) {
//    super.onCreateOptionsMenu(menu);
//    MenuInflater inflater = getMenuInflater();
//    inflater.inflate(R.menu.menu_res, menu);
//    return true;
//  }

//  @Override
//  public boolean onOptionsItemSelected(MenuItem item) {
//    super.onOptionsItemSelected(item);
//    switch (item.getItemId()) {
//      case (R.id.change_name):
//        DeviceUtil.changeDeviceName(PeersActivity.this, p2pManager, channel, null);
//        return true;
//    }
//    return true;
//  }

  @Override
  protected void onResume() {
    super.onResume();
    //registerReceiver(myBroadcastReciever,intentFilter);
    Log.d("Send Activity", "onResume");
  }

  @Override
  protected void onPause() {
    super.onPause();
    Log.d("Send Activity", "onPause");
  }

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
  @Override
  protected void onDestroy() {
    super.onDestroy();
    p2pManager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
      @Override
      public void onSuccess() {
        Toast.makeText(getApplicationContext(), "Disconnection successful", Toast.LENGTH_LONG).show();
      }

      @Override
      public void onFailure(int reason) {
        Toast.makeText(getApplicationContext(), "Disconnection Failed", Toast.LENGTH_LONG).show();
      }
    });
    unregisterReceiver(myBroadcastReciever);
    p2pManager.stopPeerDiscovery(channel, null);
    Log.d("Send Activity", "onDestroy");
  }

}
