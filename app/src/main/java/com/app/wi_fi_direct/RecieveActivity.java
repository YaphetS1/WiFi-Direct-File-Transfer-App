package com.app.wi_fi_direct;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;

public class RecieveActivity extends AppCompatActivity {

  public WifiP2pManager p2pManager;
  public WifiP2pManager.Channel channel;
  public IntentFilter intentFilter;
  public MyBroadcastReciever myBroadcastReciever;
  public static FileServerAsyncTask fileServerAsyncTask;
  public static WifiP2pManager.ConnectionInfoListener infoListener;
  public ServerSocket serverSocket;
  TextView deviceName;
  public ProgressBar progressBar;
  public TextView fileName, date;
  public LinearLayout currentFile;
  public RecyclerView filesRecyclerView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recieve);

    deviceName = findViewById(R.id.recieve_name);
    progressBar = findViewById(R.id.receiveProgressBar);
    fileName = findViewById(R.id.fileName);
    date = findViewById(R.id.dateAndTime);
    progressBar.setVisibility(View.INVISIBLE);
    currentFile = findViewById(R.id.currentFile);
    filesRecyclerView = findViewById(R.id.receivedRecycler);

    File dir = new File(Environment.getExternalStorageDirectory() + "/");
//        + getApplicationContext().getPackageName());
    File[] receivedFiles = dir.listFiles();

    FilesAdapter filesAdapter = new FilesAdapter(RecieveActivity.this, receivedFiles);
    filesRecyclerView.setAdapter(filesAdapter);
    filesRecyclerView.setLayoutManager(new LinearLayoutManager(RecieveActivity.this));

    Log.d("Reciever", "first " + (serverSocket == null));

    try {
      serverSocket = new ServerSocket(8888);
    } catch (Exception e) {
      e.printStackTrace();
    }

        /*if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/

    Log.d("Reciever", "onCreate");

    p2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);

    channel = p2pManager.initialize(this, getMainLooper(), new WifiP2pManager.ChannelListener() {
      @Override
      public void onChannelDisconnected() {
        Log.d("Reciever", "Channel Disconnected!");
      }
    });
    infoListener = new WifiP2pManager.ConnectionInfoListener() {
      @Override
      public void onConnectionInfoAvailable(WifiP2pInfo info) {
        Log.d("Reciever", "infoListener");
      }
    };

    myBroadcastReciever = new MyBroadcastReciever(p2pManager, channel, this, null, deviceName);

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
    handler.post(new Runnable() {
      @Override
      public void run() {
        p2pManager.createGroup(channel, new WifiP2pManager.ActionListener() {
          @Override
          public void onSuccess() {
            Log.d("Reciever", "Group Created");
            if (fileServerAsyncTask != null) {
              Log.d("Reciever", "Woah!");
              return;
            }
            fileServerAsyncTask = new FileServerAsyncTask((RecieveActivity.this), (serverSocket), (progressBar), (fileName), date, currentFile);
            fileServerAsyncTask.execute();
          }

          @Override
          public void onFailure(int reason) {
            Log.d("Reciever", "Group not Created" + reason);
          }
        });
      }
    });


  }

//  @Override
//  public boolean onCreateOptionsMenu(Menu menu) {
//    super.onCreateOptionsMenu(menu);
//    MenuInflater inflater = getMenuInflater();
//    inflater.inflate(R.menu.menu_res, menu);
//    return true;
//  }
//
//  @Override
//  public boolean onOptionsItemSelected(MenuItem item) {
//    super.onOptionsItemSelected(item);
//    switch (item.getItemId()) {
//      case (R.id.change_name):
//        Log.d("wololo", "wololo");
//        DeviceUtil.changeDeviceName(RecieveActivity.this, p2pManager, channel, null);
//        return true;
//    }
//    return true;
//  }

  public static class FileServerAsyncTask extends AsyncTask<Void, CustomObject, Void> {

    private Context context;
    private String fileName;
    private ServerSocket serverSocket;
    private Socket client;
    private File file;
    private Long fileSize;
    private ProgressBar progressBar;
    private TextView fileNameTW, date;
    private LinearLayout currentFile;

    public FileServerAsyncTask(Context contextWeakReference, ServerSocket reference, ProgressBar progressBarWeakReference, TextView fileName, TextView date, LinearLayout currentFile) {
      this.context = contextWeakReference;
      this.serverSocket = reference;
      this.progressBar = progressBarWeakReference;
      this.fileNameTW = fileName;
      this.date = date;
      this.currentFile = currentFile;
    }

    public void recieveData() {
      byte buf[] = new byte[1024];
      int len;

      try {
        Log.d("Reciever", "Server Listening");
        Log.d("Reciever Address", serverSocket.getLocalSocketAddress().toString());
        Log.d("Reciever Port", String.valueOf(serverSocket.getLocalPort()));
        //serverSocket.setSoTimeout(1000000);
        client = serverSocket.accept();
        Log.d("Reciever", "Server Connected");
        if (isCancelled()) return;

        InputStream inputStream1 = client.getInputStream();
        ObjectInputStream inputStream = new ObjectInputStream(inputStream1);
        fileName = inputStream.readUTF();
        fileSize = inputStream.readLong();
        file = new File(Environment.getExternalStorageDirectory() + "/" + context.getApplicationContext().getPackageName() + "/" + fileName);
        Log.d("Reciever", file.getPath());
        File dir = file.getParentFile();
        if (!dir.exists()) dir.mkdirs();
        if (file.exists()) file.delete();
        if (file.createNewFile()) {
          Log.d("Reciever", "File Created");
        } else Log.d("Reciever", "File Not Created");
        OutputStream outputStream = new FileOutputStream(file);
        CustomObject progress = new CustomObject();
        progress.name = fileName;
        progress.dataIncrement = 0;
        progress.totalProgress = 0;
        try {
          while (((len = inputStream.read(buf)) != -1)) {
            //len=inputStream.read(buf);
            outputStream.write(buf, 0, len);
            progress.dataIncrement = Long.valueOf(len);
            if (((int) (progress.totalProgress * 100 / fileSize.longValue())) == ((int) ((progress.totalProgress + progress.dataIncrement) * 100 / fileSize.longValue()))) {
              progress.totalProgress += progress.dataIncrement;
              continue;
            }
            progress.totalProgress += progress.dataIncrement;
            publishProgress(progress);
            if (this.isCancelled()) return;
            //Log.d("Reciever","Writing Data    -"+len);
          }
          Log.d("Reciever", "Writing Data Final   -" + len);
        } catch (Exception ee) {
          Log.d("Reciever", "oops");
          ee.printStackTrace();
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();
        //serverSocket.close();
        //client.close();

      } catch (Exception e) {
        e.printStackTrace();
      }


    }

    @Override
    protected Void doInBackground(Void... params) {

      recieveData();
      return null;

    }

    @Override
    protected void onProgressUpdate(CustomObject... values) {
      super.onProgressUpdate(values);
      if (progressBar.getVisibility() != View.VISIBLE) progressBar.setVisibility(View.VISIBLE);
      if (fileNameTW.getText().equals("")) fileNameTW.setText(values[0].name);
      progressBar.setProgress((int) ((values[0].totalProgress * 100) / fileSize.longValue()));
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);
      Calendar calendar = Calendar.getInstance();
      date.setText(calendar.getTime().toString());
      progressBar.setProgress(100);
      currentFile.setClickable(true);
      currentFile.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          FilesUtil.openFile(context, file);
        }
      });
      Toast.makeText(context, "File Transferred!", Toast.LENGTH_LONG).show();
      Log.d("Reciever", "onPostExecute");
      Log.d("Reciever", file.length() + " file size");
      try {
        serverSocket.close();
        client.close();
      } catch (Exception e) {
        e.printStackTrace();
      }

    }

    @Override
    protected void onCancelled() {
      super.onCancelled();
      Toast.makeText(context, "Transfer Cancelled", Toast.LENGTH_LONG).show();
      Log.d("Reciever", "Transfer Cancelled");
      try {
        if (client.isConnected()) serverSocket.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
  @Override
  protected void onDestroy() {
    super.onDestroy();
    Log.d("onDestroy", "yup");
    unregisterReceiver(myBroadcastReciever);
    p2pManager.cancelConnect(channel, null);
    p2pManager.stopPeerDiscovery(channel, null);
    new Handler().post(new Runnable() {
      @Override
      public void run() {
        try {
          serverSocket.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    try {
      fileServerAsyncTask.cancel(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
    fileServerAsyncTask = null;
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
      for (int i = 0; i < methods.length; i++) {
        if (methods[i].getName().equals("deletePersistentGroup")) {
          // Delete any persistent group
          for (int netid = 0; netid < 32; netid++) {
            methods[i].invoke(p2pManager, channel, netid, null);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
