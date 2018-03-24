package com.app.wi_fi_direct;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.app.wi_fi_direct.pages.SendFileActivity;

public class MainActivity extends AppCompatActivity {

  public Button sendButton, recieveButton;
  private Button button;

  public int numbPermissions = 7;
  public String[] permissions = new String[numbPermissions];

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    button = findViewById(R.id.button);
    button.setOnClickListener(l -> {
      MainActivity.this.startActivity(new Intent(MainActivity.this, SendFileActivity.class));
    });

    permissions[0] = android.Manifest.permission.ACCESS_NETWORK_STATE;
    permissions[1] = android.Manifest.permission.ACCESS_WIFI_STATE;
    permissions[2] = android.Manifest.permission.CHANGE_WIFI_STATE;
    permissions[3] = android.Manifest.permission.INTERNET;
    permissions[4] = android.Manifest.permission.READ_EXTERNAL_STORAGE;
    permissions[5] = android.Manifest.permission.CHANGE_NETWORK_STATE;
    permissions[6] = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    sendButton = (Button) findViewById(R.id.sendButton);
    recieveButton = (Button) findViewById(R.id.recieveButton);
    sendButton.setOnClickListener(v -> {
//        if (!checkPermissions()) {
//          askPermissions();
//          return;
//        }
      ActivityCompat.requestPermissions(MainActivity.this, permissions, 49);

      Intent intent = new Intent(MainActivity.this, PeersActivity.class);
      MainActivity.this.startActivity(intent);
    });

    recieveButton.setOnClickListener(v -> {
//        if (!checkPermissions()) {
//          askPermissions();
//          return;
//        }
      ActivityCompat.requestPermissions(MainActivity.this, permissions, 49);
      Intent intent = new Intent(MainActivity.this, RecieveActivity.class);
      MainActivity.this.startActivity(intent);
    });

  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  public boolean checkPermissions() {
    for (int i = 0; i < numbPermissions; i++) {
      if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED)
        return false;
    }
    return true;
  }

  public void askPermissions() {
    @SuppressLint("StaticFieldLeak") AsyncTask task = new AsyncTask() {

      @Nullable
      @Override
      protected Object doInBackground(Object[] params) {
        for (int i = 0; i < numbPermissions; i++) {
          if (ContextCompat.checkSelfPermission(getApplicationContext(), permissions[i]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permissions[i]}, i);
          }
        }
        return null;
      }
    };
    task.execute();
  }
}
