package com.app.wi_fi_direct.pages;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.wi_fi_direct.R;
import com.app.wi_fi_direct.helpers.Callback;
import com.app.wi_fi_direct.services.NavService;

public class SettingsActivity extends AppCompatActivity {

  private ConstraintLayout clStoreLocation;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
    initNav();

    clStoreLocation = findViewById(R.id.clStoreLocation);
    clStoreLocation.setOnClickListener(l -> {
      Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();
    });

  }

  private void initNav() {
    NavService.setupTopNav(this, R.string.settings, false);
    NavService.init(this
        , (Callback) () -> {
          Toast.makeText(SettingsActivity.this, "Some action will be here!", Toast.LENGTH_SHORT).show();
        }
        , (Callback) () -> {
            Intent intent = new Intent(SettingsActivity.this, FileActivity.class);
            intent.putExtra(NavService.TAB, NavService.TAB_SEND);
            startActivity(intent);
        }
        , (Callback) () -> {
          Intent intent = new Intent(SettingsActivity.this, FileActivity.class);
          intent.putExtra(NavService.TAB, NavService.TAB_RECEIVE);
          startActivity(intent);
        }
        , (Callback) () -> {

        }
        , NavService.TAB_SETTINGS
    );
  }
}
