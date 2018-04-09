package com.app.wi_fi_direct.pages;

import android.content.ClipData;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.wi_fi_direct.R;
import com.app.wi_fi_direct.adapters.FilesAdapter;
import com.app.wi_fi_direct.adapters.FilesSendAdapter;
import com.app.wi_fi_direct.adapters.PeersAdapter;
import com.app.wi_fi_direct.helpers.Callback;
import com.app.wi_fi_direct.helpers.ChooseFile;
import com.app.wi_fi_direct.helpers.DeviceInfoServerAsyncTask;
import com.app.wi_fi_direct.helpers.FileServerAsyncTask;
import com.app.wi_fi_direct.helpers.FilesUtil;
import com.app.wi_fi_direct.helpers.MyBroadcastReciever;
import com.app.wi_fi_direct.helpers.OnBackPressedListener;
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

  int activeTab;

  protected OnBackPressedListener onBackPressedListener;

  private RecyclerView rvSendingFilesList;
  private RecyclerView rvReceivingFilesList;
  private TextView tvSendOrReceive;
  private Callback callbackReInitFileServer;
  private Callback callbackReInitDeviceServer;
  private FilesSendAdapter sendFilesAdapter;
  private FilesAdapter receiveFilesAdapter;

  private WifiP2pManager p2pManager;
  private WifiP2pManager.Channel channel;
  private MyBroadcastReciever myBroadcastReciever;
  private ArrayList peerList = new ArrayList();
  private PeersAdapter peersAdapter;
  private InetAddress serverAddress;
  private ServerSocket serverSocket;
  private ServerSocket serverSocketDevice;
  private FileServerAsyncTask fileServerAsyncTask;
  private DeviceInfoServerAsyncTask deviceInfoServerAsyncTask;
  private Callback callbackSendThisDeviceName;

  @Override
  public void onStart() {
    super.onStart();

    //init navigation
    this.initNav();

    this.onBackPressedListener = (() -> {
      Toast.makeText(SendFileActivity.this, "Please press again to exit", Toast.LENGTH_SHORT).show();
      SendFileActivity.this.onBackPressedListener = null;
    });
  }

  @Override
  public void onBackPressed() {
    if (onBackPressedListener != null)
      onBackPressedListener.doBack();
    else
      super.onBackPressed();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_send_file);

    rvSendingFilesList = findViewById(R.id.rvSendingFilesList);
    LinearLayoutManager filesListLayoutManager = new LinearLayoutManager(
        this, LinearLayoutManager.VERTICAL, false);
    rvSendingFilesList.setLayoutManager(filesListLayoutManager);
    sendFilesAdapter = new FilesSendAdapter();
    rvSendingFilesList.setAdapter(sendFilesAdapter);


    rvReceivingFilesList = findViewById(R.id.rvReceivingFilesList);
    LinearLayoutManager receiveFilesListLayoutManager = new LinearLayoutManager(
        this, LinearLayoutManager.VERTICAL, false);
    rvReceivingFilesList.setLayoutManager(receiveFilesListLayoutManager);
    receiveFilesAdapter = new FilesAdapter(this);

    rvReceivingFilesList.setAdapter(receiveFilesAdapter);

    this.initSockets();
    callbackReInitFileServer = SendFileActivity.this::initFileServer;
    callbackReInitDeviceServer = SendFileActivity.this::initDeviceInfoServers;

    this.initFileServer(); // Init file server for receiving data

    WifiP2pManager.PeerListListener peerListListener = peers -> {
      peerList.clear();
      peerList.addAll(peers.getDeviceList());
      peersAdapter.updateList(peerList);
      peersAdapter.notifyDataSetChanged();
    };

    callbackSendThisDeviceName = () -> {
      TransferNameDevice transferNameDevice = new TransferNameDevice(serverAddress);
      transferNameDevice.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    };

    WifiP2pManager.ConnectionInfoListener infoListener = info -> {
      serverAddress = info.groupOwnerAddress;
      if (serverAddress == null) return;
      callbackSendThisDeviceName.call();

      ChooseFile.fileChooser(SendFileActivity.this);
    };

    p2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
    channel = p2pManager.initialize(this, getMainLooper(), null);

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

    myBroadcastReciever = new MyBroadcastReciever(p2pManager, channel,
        this, infoListener);
    myBroadcastReciever.setPeerListListener(peerListListener);

    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

    registerReceiver(myBroadcastReciever, intentFilter);

    peersAdapter = new PeersAdapter(peerList, this,
        p2pManager, channel, this, infoListener);

    RecyclerView rvDevicesList = findViewById(R.id.rvDevicesList);
    rvDevicesList.setAdapter(peersAdapter);
    this.initDeviceInfoServers(); // Init Device info server for receiving device name who connected

    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(
        this, LinearLayoutManager.VERTICAL, false);
    rvDevicesList.setLayoutManager(mLayoutManager);

    p2pManager.discoverPeers(channel, null);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    p2pManager.cancelConnect(channel, null);
    unregisterReceiver(myBroadcastReciever);
    p2pManager.stopPeerDiscovery(channel, null);

    try {
      serverSocket.close();
      serverSocketDevice.close();
      fileServerAsyncTask.cancel(true);
      deviceInfoServerAsyncTask.cancel(true);
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
          sendFilesAdapter.notifyAdapter(uris, filesLength, fileNames);

          TransferData transferData = new TransferData(SendFileActivity.this,
              uris, filesLength, fileNames, (sendFilesAdapter), serverAddress, p2pManager, channel);
          transferData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
          e.printStackTrace();
        }
    }
  }

  private void initNav() {
    Intent intent = getIntent();
    activeTab = intent.getIntExtra(NavService.TAB, NavService.TAB_SEND);
    tvSendOrReceive = findViewById(R.id.tvSendOrReceive);
    NavService.setupTopNav(this, R.string.app_main_title, true);

    Callback recommendationsTabAction = () -> {
      Toast.makeText(SendFileActivity.this, "Some action will be here!", Toast.LENGTH_SHORT).show();
    };

    Callback sendTabAction = () -> {
      activeTab = NavService.TAB_SEND;
      tvSendOrReceive.setText(R.string.sending);
      rvSendingFilesList.setVisibility(View.VISIBLE);
      rvReceivingFilesList.setVisibility(View.INVISIBLE);
    };

    Callback receiveTabAction = () -> {
      activeTab = NavService.TAB_RECEIVE;
      tvSendOrReceive.setText(R.string.receiving);
      rvSendingFilesList.setVisibility(View.INVISIBLE);
      rvReceivingFilesList.setVisibility(View.VISIBLE);
    };

    Callback settingsTabAction = () -> {
      AlertDialog.Builder ad = new AlertDialog.Builder(SendFileActivity.this);
      ad.setTitle(R.string.ad_title);  // заголовок
      ad.setMessage(R.string.ad_message); // сообщение
      ad.setPositiveButton(R.string.ad_yes, (dialog, arg1) -> {
        startActivity(new Intent(SendFileActivity.this, SettingsActivity.class));
      });
      ad.setNegativeButton(R.string.ad_no, (dialog, arg1) -> NavService.set(SendFileActivity.this, activeTab));
      ad.setCancelable(true);
      ad.setOnCancelListener(dialog -> NavService.set(SendFileActivity.this, activeTab));
      ad.show();
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

  private void initDeviceInfoServers() {

    deviceInfoServerAsyncTask = new DeviceInfoServerAsyncTask(
        (serverSocketDevice),
        (SendFileActivity.this.peersAdapter), callbackReInitDeviceServer);
    deviceInfoServerAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
  }

  private void initFileServer() {

    fileServerAsyncTask = new FileServerAsyncTask(
        (SendFileActivity.this),
        (serverSocket),
        (SendFileActivity.this.receiveFilesAdapter), callbackReInitFileServer);
    fileServerAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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
