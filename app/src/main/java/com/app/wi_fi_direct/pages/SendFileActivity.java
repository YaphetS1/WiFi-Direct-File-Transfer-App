package com.app.wi_fi_direct.pages;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.wi_fi_direct.ChooseFile;
import com.app.wi_fi_direct.FilesUtil;
import com.app.wi_fi_direct.MyBroadcastReciever;
import com.app.wi_fi_direct.PathUtil;
import com.app.wi_fi_direct.PeersAdapter;
import com.app.wi_fi_direct.R;
import com.app.wi_fi_direct.TransferData;
import com.app.wi_fi_direct.adapters.DeviceListAdapter;
import com.app.wi_fi_direct.adapters.FileListAdapter;
import com.app.wi_fi_direct.models.DeviceModel;
import com.app.wi_fi_direct.models.FileModel;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class SendFileActivity extends AppCompatActivity {

  private RecyclerView rvDevicesList;

  private RecyclerView rvFilesList;
  private List<FileModel> filesList;
  private FileListAdapter fileListAdapter;


  public WifiP2pManager p2pManager;
  public WifiP2pManager.Channel channel;
  public IntentFilter intentFilter;
  public MyBroadcastReciever myBroadcastReciever;
  public WifiP2pManager.PeerListListener peerListListener;
  public List<WifiP2pDevice> peerList;
  public PeersAdapter peersAdapter;
  public InetAddress serverAddress;
  public WifiP2pManager.ConnectionInfoListener infoListener;

  @Override
  public void onStart() {
    super.onStart();

    ImageView ivBottomNavSend = findViewById(R.id.ivSend);
    ImageView ivBottomNavReceive = findViewById(R.id.ivReceive);
    ImageView ivBottomNavSetting = findViewById(R.id.ivSettings);
    TextView tvBottomNavSend = findViewById(R.id.tvSend);
    TextView tvBottomNavReceive = findViewById(R.id.tvReceive);
    TextView tvBottomNavSetting = findViewById(R.id.tvSettings);

    ivBottomNavReceive.setOnClickListener(v -> {
      SendFileActivity.this.finish();
      Intent intent = new Intent(SendFileActivity.this, ReceiveFileActivity.class);
      startActivity(intent);
    });

    ivBottomNavSetting.setOnClickListener(v -> {
    });

    ivBottomNavSend.setImageResource(R.drawable.d_bottom_nav_send_active);
    ivBottomNavReceive.setImageResource(R.drawable.d_bottom_nav_download);
    ivBottomNavSetting.setImageResource(R.drawable.d_bottom_nav_settings);

    tvBottomNavSend.setTextColor(getResources().getColor(R.color.cTextPrimary));
    tvBottomNavReceive.setTextColor(getResources().getColor(R.color.cTextGrey));
    tvBottomNavSetting.setTextColor(getResources().getColor(R.color.cTextGrey));

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_send_file);



//    LinearLayoutManager devicesListLayoutManager = new LinearLayoutManager(
//        this, LinearLayoutManager.VERTICAL, false);
//    rvDevicesList.setLayoutManager(devicesListLayoutManager);

//    devicesList = new ArrayList<>();
//    devicesList.add(new DeviceModel("Устройство 1"));
//    devicesList.add(new DeviceModel("Устройство 2"));
//    devicesList.add(new DeviceModel("Устройство 3"));
//    devicesList.add(new DeviceModel("Устройство 4"));
//    devicesList.add(new DeviceModel("Устройство 5"));
//    devicesList.add(new DeviceModel("Устройство 6"));
//    deviceListAdapter = new DeviceListAdapter(devicesList);
//    rvDevicesList.setAdapter(deviceListAdapter);


    rvFilesList = findViewById(R.id.rvFilesList);
    LinearLayoutManager filesListLayoutManager = new LinearLayoutManager(
        this, LinearLayoutManager.VERTICAL, false);
    rvFilesList.setLayoutManager(filesListLayoutManager);

    filesList = new ArrayList<>();
    filesList.add(new FileModel("Файл 1", FileModel.TYPE_COMMON));
    filesList.add(new FileModel("Файл 2", FileModel.TYPE_COMMON));
    filesList.add(new FileModel("Приложение 1", FileModel.TYPE_APPLICATION));
    filesList.add(new FileModel("Фото 1", FileModel.TYPE_PHOTO));
    filesList.add(new FileModel("Фото 2", FileModel.TYPE_PHOTO));
    filesList.add(new FileModel("Фото 3", FileModel.TYPE_PHOTO));
    filesList.add(new FileModel("Файл 3", FileModel.TYPE_COMMON));
    filesList.add(new FileModel("Приложение 2", FileModel.TYPE_APPLICATION));
    filesList.add(new FileModel("Файл 4", FileModel.TYPE_COMMON));
    filesList.add(new FileModel("Приложение 3", FileModel.TYPE_APPLICATION));
    fileListAdapter = new FileListAdapter(filesList);
    rvFilesList.setAdapter(fileListAdapter);




        /*if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/

    Log.d("Send Activity", "onCreate");

    peerList = new ArrayList();

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
      Toast.makeText(SendFileActivity.this, "Info Recieved " + serverAddress.toString(), Toast.LENGTH_LONG).show();
      Log.d("Server Data", info.toString());
      Toast.makeText(getApplicationContext(), "Info " + info.groupFormed, Toast.LENGTH_LONG).show();
      ChooseFile.fileChooser(SendFileActivity.this);
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

    rvDevicesList = findViewById(R.id.rvDevicesList);
    rvDevicesList.setAdapter(peersAdapter);

    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(
        this, LinearLayoutManager.VERTICAL, false);
    rvDevicesList.setLayoutManager(mLayoutManager);

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

}
