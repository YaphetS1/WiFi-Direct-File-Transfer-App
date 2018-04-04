package com.app.wi_fi_direct.pages;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.wi_fi_direct.R;
import com.app.wi_fi_direct.helpers.Callback;
import com.app.wi_fi_direct.services.NavService;

public class SettingsActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
    initNav();
  }

  private void initNav() {
    NavService.setupTopNav(this, R.string.settings, false);
    NavService.init(this
        , (Callback) () -> {
          Toast.makeText(SettingsActivity.this, "Some action will be here!", Toast.LENGTH_SHORT).show();
        }
        , (Callback) () -> {
            Intent intent = new Intent(SettingsActivity.this, SendFileActivity.class);
            intent.putExtra(NavService.TAB, NavService.TAB_SEND);
            startActivity(intent);
        }
        , (Callback) () -> {
          Intent intent = new Intent(SettingsActivity.this, SendFileActivity.class);
          intent.putExtra(NavService.TAB, NavService.TAB_RECEIVE);
          startActivity(intent);
        }
        , (Callback) () -> {

        }
        , NavService.TAB_SETTINGS
    );
  }
}
