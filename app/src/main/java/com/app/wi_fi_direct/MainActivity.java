package com.app.wi_fi_direct;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.app.wi_fi_direct.pages.FileActivity;

public class MainActivity extends AppCompatActivity {

  public int numbPermissions = 7;
  public String[] permissions = new String[numbPermissions];

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    permissions[0] = android.Manifest.permission.ACCESS_NETWORK_STATE;
    permissions[1] = android.Manifest.permission.ACCESS_WIFI_STATE;
    permissions[2] = android.Manifest.permission.CHANGE_WIFI_STATE;
    permissions[3] = android.Manifest.permission.INTERNET;
    permissions[4] = android.Manifest.permission.READ_EXTERNAL_STORAGE;
    permissions[5] = android.Manifest.permission.CHANGE_NETWORK_STATE;
    permissions[6] = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

    if (!checkPermissions()) {
      ActivityCompat.requestPermissions(MainActivity.this, permissions, 49);
    } else {
      Intent intent = new Intent(MainActivity.this, FileActivity.class);
//      intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
      this.startActivity(intent);
    }

  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String permissions[], @NonNull int[] grantResults) {
    switch (requestCode) {
      case 49: {
        if (!checkPermissions()) {
          ActivityCompat.requestPermissions(MainActivity.this, permissions, 49);
        } else {
          Intent intent = new Intent(MainActivity.this, FileActivity.class);
//          intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
          this.startActivity(intent);
        }
      }
    }
  }

  public boolean checkPermissions() {
    for (int i = 0; i < numbPermissions; i++) {
      if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED)
        return false;
    }
    return true;
  }
}
