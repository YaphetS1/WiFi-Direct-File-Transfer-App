package com.app.wi_fi_direct.pages;

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
  }


  private void initNav() {

    NavService.setupTopNav(this, R.string.app_main_title, true);
    NavService.init(this
        , (Callback) () -> {
          Toast.makeText(SettingsActivity.this, "Some action will be here!", Toast.LENGTH_SHORT).show();
        }
        , (Callback) () -> {

        }
        , (Callback) () -> {

        }
        , (Callback) () -> {

        }
        , NavService.TAB_SETTINGS
    );
  }
}
