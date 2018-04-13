package com.app.wi_fi_direct.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.app.wi_fi_direct.R;
import com.app.wi_fi_direct.Variables;
import com.app.wi_fi_direct.helpers.DirectoryChooserDialog;
import com.app.wi_fi_direct.services.NavService;

public class SettingsActivity extends AppCompatActivity {

  private ConstraintLayout clStoreLocation;
  private String chosenDir = "";
  private boolean newFolderEnabled = true;
  private SharedPreferences sharedPreferences;
  private SharedPreferences.Editor sharedPreferencesEditor;

  @Override
  protected void onStart() {
    super.onStart();
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
    initNav();

    clStoreLocation = findViewById(R.id.clStoreLocation);
    clStoreLocation.setOnClickListener(l -> {

      // Create DirectoryChooserDialog and register a callback
      DirectoryChooserDialog directoryChooserDialog =
          new DirectoryChooserDialog(SettingsActivity.this, chosenDir -> {
            this.chosenDir = chosenDir;
            Toast.makeText(SettingsActivity.this, "Chosen directory: " + chosenDir, Toast.LENGTH_LONG).show();

            sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putString(Variables.APP_TYPE, chosenDir);
            sharedPreferencesEditor.commit();
          });
      // Toggle new folder button enabling
      directoryChooserDialog.setNewFolderEnabled(this.newFolderEnabled);
      // Load directory chooser dialog for initial 'm_chosenDir' directory.
      // The registered callback will be called upon final directory selection.
      directoryChooserDialog.chooseDirectory(this.chosenDir);
      this.newFolderEnabled = !this.newFolderEnabled;
    });

  }

  private void initNav() {
    NavService.setupTopNav(this, R.string.settings, false);
    NavService.init(this
        , () -> {
          Toast.makeText(SettingsActivity.this, "Some action will be here!", Toast.LENGTH_SHORT).show();
        }
        , () -> {
          Intent intent = new Intent(SettingsActivity.this, FileActivity.class);
          intent.putExtra(NavService.TAB, NavService.TAB_SEND);
          startActivity(intent);
        }
        , () -> {
          Intent intent = new Intent(SettingsActivity.this, FileActivity.class);
          intent.putExtra(NavService.TAB, NavService.TAB_RECEIVE);
          startActivity(intent);
        }
        , () -> {

        }
        , NavService.TAB_SETTINGS
    );
  }
}
