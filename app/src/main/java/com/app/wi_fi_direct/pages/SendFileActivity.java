package com.app.wi_fi_direct.pages;

import android.content.ClipData;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.wi_fi_direct.R;
import com.app.wi_fi_direct.adapters.FilesAdapter;
import com.app.wi_fi_direct.adapters.PeersAdapter;
import com.app.wi_fi_direct.helpers.Callback;
import com.app.wi_fi_direct.helpers.ChooseFile;
import com.app.wi_fi_direct.helpers.DeviceInfoServerAsyncTask;
import com.app.wi_fi_direct.helpers.FileServerAsyncTask;
import com.app.wi_fi_direct.helpers.FilesUtil;
import com.app.wi_fi_direct.helpers.MyBroadcastReciever;
import com.app.wi_fi_direct.helpers.PathUtil;
import com.app.wi_fi_direct.helpers.TransferData;
import com.app.wi_fi_direct.helpers.TransferNameDevice;
import com.app.wi_fi_direct.services.NavService;

import java.io.File;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;

public class SendFileActivity extends AppCompatActivity {

  private RecyclerView rvDevicesList;
  private RecyclerView rvSendingFilesList;
  private RecyclerView rvReceivingFilesList;

  public WifiP2pManager p2pManager;
  public WifiP2pManager.Channel channel;
  public IntentFilter intentFilter;
  public MyBroadcastReciever myBroadcastReciever;
  public WifiP2pManager.PeerListListener peerListListener;
  public ArrayList peerList = new ArrayList();
  public PeersAdapter peersAdapter;
  public InetAddress serverAddress;
  public WifiP2pManager.ConnectionInfoListener infoListener;
  public ServerSocket serverSocket;
  public ServerSocket serverSocketDevice;
  public FileServerAsyncTask fileServerAsyncTask;
  public DeviceInfoServerAsyncTask deviceInfoServerAsyncTask;

  private TextView tvSendOrReceive;
  private Callback callbackReInitServers;
  public Callback callbackSendThisDeviceName;


  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_send_file);

    rvSendingFilesList = findViewById(R.id.rvSendingFilesList);
    LinearLayoutManager filesListLayoutManager = new LinearLayoutManager(
        this, LinearLayoutManager.VERTICAL, false);
    rvSendingFilesList.setLayoutManager(filesListLayoutManager);
//    FilesAdapter sendFilesAdapter = new FilesAdapter(SendFileActivity.this);
//    rvSendingFilesList.setAdapter(sendFilesAdapter);


    rvReceivingFilesList = findViewById(R.id.rvReceivingFilesList);
    LinearLayoutManager receiveFilesListLayoutManager = new LinearLayoutManager(
        this, LinearLayoutManager.VERTICAL, false);
    rvReceivingFilesList.setLayoutManager(receiveFilesListLayoutManager);
    FilesAdapter receiveFilesAdapter = new FilesAdapter(SendFileActivity.this);

    rvReceivingFilesList.setAdapter(receiveFilesAdapter);

    //init navigation
    this.initNav();

    this.initSockets();
    callbackReInitServers = () -> SendFileActivity.this.initServers(receiveFilesAdapter);

    peerListListener = peers -> {
      peerList.clear();
      peerList.addAll(peers.getDeviceList());
      peersAdapter.updateList(peerList);
      peersAdapter.notifyDataSetChanged();
    };

    callbackSendThisDeviceName = () -> {
      TransferNameDevice transferNameDevice = new TransferNameDevice(serverAddress);
      transferNameDevice.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    };

    infoListener = info -> {
      serverAddress = info.groupOwnerAddress;
      if (serverAddress == null) return;

      SendFileActivity.this.callbackSendThisDeviceName.call();
      ChooseFile.fileChooser(SendFileActivity.this);
    };

    p2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
    channel = p2pManager.initialize(this, getMainLooper(), null);
    peersAdapter = new PeersAdapter(peerList, this,
        p2pManager, channel, this, infoListener);
    this.initServers(receiveFilesAdapter);

    try {
      Class<?> wifiManager = Class
          .forName("android.net.wifi.p2p.WifiP2pManager");

      Method method = wifiManager
          .getMethod("enableP2p",
              WifiP2pManager.Channel.class);

      method.invoke(p2pManager, channel);

    } catch (Exception e) {
      e.printStackTrace();
    }

    p2pManager.removeGroup(channel, null);
    //Toast.makeText(this,channel.toString(),Toast.LENGTH_LONG).show();

    myBroadcastReciever = new MyBroadcastReciever(p2pManager, channel,
        this, infoListener);
    myBroadcastReciever.setPeerListListener(peerListListener);

    intentFilter = new IntentFilter();
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

    registerReceiver(myBroadcastReciever, intentFilter);


    rvDevicesList = findViewById(R.id.rvDevicesList);
    rvDevicesList.setAdapter(peersAdapter);

    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(
        this, LinearLayoutManager.VERTICAL, false);
    rvDevicesList.setLayoutManager(mLayoutManager);

    p2pManager.discoverPeers(channel, null);
  }

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

    try {
      serverSocket.close();
      fileServerAsyncTask.cancel(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
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

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case ChooseFile.FILE_TRANSFER_CODE:
        if (data == null) return;

        ArrayList<Uri> uris = new ArrayList<>();
        ArrayList<Long> filesLength = new ArrayList<>();
        ArrayList<String> fileNames = new ArrayList<>();

        try {
          ClipData clipData = data.getClipData();

          if (clipData != null) {
            for (int i = 0; i < clipData.getItemCount(); i++) {
              uris.add(clipData.getItemAt(i).getUri());

              String fileName =
                  PathUtil.getPath(getApplicationContext(), clipData.getItemAt(i).getUri());
              filesLength.add(new File(fileName).length());

              fileName = FilesUtil.getFileName(fileName);
              fileNames.add(fileName);

              Log.d("File URI", clipData.getItemAt(i).getUri().toString());
              Log.d("File Path", fileName);
            }
          } else {
            Uri uri = data.getData();
            uris.add(uri);

            String fileName = PathUtil.getPath(getApplicationContext(), uri);
            filesLength.add(new File(fileName).length());

            fileName = FilesUtil.getFileName(fileName);
            fileNames.add(fileName);
          }

          TransferData transferData = new TransferData(SendFileActivity.this,
              uris, fileNames, filesLength, serverAddress, p2pManager, channel);
          transferData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
          e.printStackTrace();
        }
    }
  }

  private void initNav() {
    Intent intent = getIntent();
    int activeTab = intent.getIntExtra(NavService.TAB, NavService.TAB_SEND);
    tvSendOrReceive = findViewById(R.id.tvSendOrReceive);
    NavService.setupTopNav(this, R.string.app_main_title, true);

    Callback recommendationsTabAction = () -> {
      Toast.makeText(SendFileActivity.this, "Some action will be here!", Toast.LENGTH_SHORT).show();
    };

    Callback sendTabAction = () -> {
      tvSendOrReceive.setText(R.string.sending);
      rvSendingFilesList.setVisibility(View.VISIBLE);
      rvReceivingFilesList.setVisibility(View.INVISIBLE);
    };

    Callback receiveTabAction = () -> {
      tvSendOrReceive.setText(R.string.receiving);
      rvSendingFilesList.setVisibility(View.INVISIBLE);
      rvReceivingFilesList.setVisibility(View.VISIBLE);
    };

    Callback settingsTabAction = () -> {
      Toast.makeText(SendFileActivity.this, "Some action will be here!", Toast.LENGTH_SHORT).show();
    };

    NavService.init(this
        , recommendationsTabAction
        , sendTabAction
        , receiveTabAction
        , settingsTabAction
        , activeTab
    );
    if (activeTab == NavService.TAB_RECEIVE) {
      receiveTabAction.call();
    }
  }

  private void initServers(FilesAdapter receiveFilesAdapter) {

    fileServerAsyncTask = new FileServerAsyncTask(
        (SendFileActivity.this),
        (serverSocket),
        (receiveFilesAdapter), callbackReInitServers);
    fileServerAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    deviceInfoServerAsyncTask = new DeviceInfoServerAsyncTask(
        (serverSocketDevice),
        (peersAdapter), callbackReInitServers);
    deviceInfoServerAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
  }

  private void initSockets() {
    try {
      serverSocketDevice = new ServerSocket(8887);
      serverSocket = new ServerSocket(8888);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
