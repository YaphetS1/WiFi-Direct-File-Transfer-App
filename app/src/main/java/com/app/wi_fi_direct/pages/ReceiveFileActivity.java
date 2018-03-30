package com.app.wi_fi_direct.pages;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.wi_fi_direct.R;
import com.app.wi_fi_direct.adapters.FilesAdapter;
import com.app.wi_fi_direct.adapters.PeersAdapter;
import com.app.wi_fi_direct.helpers.FileServerAsyncTask;
import com.app.wi_fi_direct.helpers.MyBroadcastReciever;

import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ReceiveFileActivity extends AppCompatActivity {

  private RecyclerView rvDevicesList;
  private RecyclerView rvFilesList;

  public WifiP2pManager.PeerListListener peerListListener;
  public ArrayList peerList;
  public PeersAdapter peersAdapter;

  public WifiP2pManager p2pManager;
  public WifiP2pManager.Channel channel;
  public IntentFilter intentFilter;
  public MyBroadcastReciever myBroadcastReciever;
  public FileServerAsyncTask fileServerAsyncTask;
  public static WifiP2pManager.ConnectionInfoListener infoListener;
  public ServerSocket serverSocket;

  @Override
  public void onStart() {
    super.onStart();

    ImageView ivBottomNavSend = findViewById(R.id.ivSend);
    ImageView ivBottomNavReceive = findViewById(R.id.ivReceive);
    ImageView ivBottomNavSetting = findViewById(R.id.ivSettings);
    TextView tvBottomNavSend = findViewById(R.id.tvSend);
    TextView tvBottomNavReceive = findViewById(R.id.tvReceive);
    TextView tvBottomNavSetting = findViewById(R.id.tvSettings);

    ivBottomNavSend.setOnClickListener(v -> {
      ReceiveFileActivity.this.finish();
      Intent intent = new Intent(ReceiveFileActivity.this, SendFileActivity.class);
      startActivity(intent);
    });

    ivBottomNavSetting.setOnClickListener(v -> {
    });

    ivBottomNavSend.setImageResource(R.drawable.d_bottom_nav_send);
    ivBottomNavReceive.setImageResource(R.drawable.d_bottom_nav_download_active);
    ivBottomNavSetting.setImageResource(R.drawable.d_bottom_nav_settings);

    tvBottomNavSend.setTextColor(getResources().getColor(R.color.cTextGrey));
    tvBottomNavReceive.setTextColor(getResources().getColor(R.color.cTextPrimary));
    tvBottomNavSetting.setTextColor(getResources().getColor(R.color.cTextGrey));

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_receive_file);


    rvDevicesList = findViewById(R.id.rvDevicesList);
    LinearLayoutManager devicesListLayoutManager = new LinearLayoutManager(
        this, LinearLayoutManager.VERTICAL, false);
    rvDevicesList.setLayoutManager(devicesListLayoutManager);

    peerListListener = peers -> {
//      peerList = new ArrayList();
      peerList.clear();
      peerList.addAll(peers.getDeviceList());
      peersAdapter.updateList(peerList);
      peersAdapter.notifyDataSetChanged();
      //Toast.makeText(getApplicationContext(),String.valueOf(peers.getDeviceList().size()),Toast.LENGTH_LONG).show();
    };



    rvFilesList = findViewById(R.id.rvFilesList);
    LinearLayoutManager filesListLayoutManager = new LinearLayoutManager(
        this, LinearLayoutManager.VERTICAL, false);
    rvFilesList.setLayoutManager(filesListLayoutManager);


    FilesAdapter filesAdapter = new FilesAdapter(ReceiveFileActivity.this);
    rvFilesList.setAdapter(filesAdapter);

    Log.d("Reciever", "first " + (serverSocket == null));

    try {
      serverSocket = new ServerSocket(8888);
    } catch (Exception e) {
      e.printStackTrace();
    }

    Log.d("Reciever", "onCreate");

    p2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);

    channel = p2pManager.initialize(this, getMainLooper(), () ->
        Log.d("Reciever", "Channel Disconnected!"));

    infoListener = info ->
        Log.d("Reciever", "infoListener");

    myBroadcastReciever = new MyBroadcastReciever(p2pManager, channel, this, null);

    intentFilter = new IntentFilter();
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

    registerReceiver(myBroadcastReciever, intentFilter);

    p2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
      @Override
      public void onSuccess() {
        Log.d("Reciever", "Groups Removed");
      }

      @Override
      public void onFailure(int reason) {
        Log.d("Reciever", "Groups Not Removed");
      }
    });
    deletePersistentGroups();

    Handler handler = new Handler();
    handler.post(() -> {
      p2pManager.createGroup(channel, new WifiP2pManager.ActionListener() {
        @Override
        public void onSuccess() {
          Log.d("Reciever", "Group Created");
          if (fileServerAsyncTask != null) {
            Log.d("Reciever", "Woah!");
            return;
          }
          fileServerAsyncTask = new FileServerAsyncTask(
              (ReceiveFileActivity.this),
              (serverSocket),
              (filesAdapter));

          fileServerAsyncTask.execute();
        }

        @Override
        public void onFailure(int reason) {
          Log.d("Reciever", "Group not Created" + reason);
        }
      });
    });


  }

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
  @Override
  protected void onDestroy() {
    super.onDestroy();

    Log.d("onDestroy", "yup");
    unregisterReceiver(myBroadcastReciever);
    p2pManager.cancelConnect(channel, null);
    p2pManager.stopPeerDiscovery(channel, null);

    new Handler().post(() -> {
      try {
        serverSocket.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    try {
      fileServerAsyncTask.cancel(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
    Log.d("Reciever", "End Reached");
    p2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
      @Override
      public void onSuccess() {
        Log.d("Reciever", "Groups Removed");
      }

      @Override
      public void onFailure(int reason) {
        Log.d("Reciever", "Groups Not Removed");
      }
    });
    deletePersistentGroups();
  }

  private void deletePersistentGroups() {
    try {
      Method[] methods = WifiP2pManager.class.getMethods();
      for (Method method : methods) {
        if (method.getName().equals("deletePersistentGroup")) {
          // Delete any persistent group
          for (int netid = 0; netid < 32; netid++) {
            method.invoke(p2pManager, channel, netid, null);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
