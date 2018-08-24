package com.app.wi_fi_direct.pages;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.app.wi_fi_direct.R;
import com.app.wi_fi_direct.Variables;
import com.app.wi_fi_direct.helpers.DirectoryChooserDialog;
import com.app.wi_fi_direct.services.NavService;

public class SettingsActivity extends AppCompatActivity {

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

    ConstraintLayout clStoreLocation = findViewById(R.id.clStoreLocation);
    ConstraintLayout clSettingsOfWifiDirect = findViewById(R.id.clSettingsOfWifiDirect);
    ConstraintLayout clAbout = findViewById(R.id.clAbout);
    ConstraintLayout clMailTo = findViewById(R.id.clMailTo);

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

    clSettingsOfWifiDirect.setOnClickListener(l -> {

      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle(R.string.alert_dialog_title);
      View view = View.inflate(this, R.layout.alert_dialog, (ViewGroup) null);
      builder.setView(view);
      builder.setNegativeButton(R.string.ad_no, null);
      final EditText editText = view.findViewById(R.id.change_name_edittext);
      builder.setPositiveButton(R.string.ad_yes, (dialog, which) -> {
        String newName = editText.getText().toString();
        if (newName.isEmpty()) return;

        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(Variables.NAME_DEVICE, newName);
        sharedPreferencesEditor.commit();
      });
      builder.create().show();
    });

    clAbout.setOnClickListener(l -> {
      Intent browserIntent = new Intent(Intent.ACTION_VIEW,
          Uri.parse("https://github.com/YaphetS1/WiFi-Direct-File-Transfer-App"));
      startActivity(browserIntent);
    });

    clMailTo.setOnClickListener(l -> {
      Intent intent = new Intent(Intent.ACTION_SEND);
      intent.setType("plain/text");
      intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{
          "436910463q@gmail.com"
      });
      intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Feedback");
      intent.putExtra(android.content.Intent.EXTRA_TEXT, "Dear developer. ");

      /* Send it off to the Activity-Chooser */
      startActivity(Intent.createChooser(intent, "Send"));
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
