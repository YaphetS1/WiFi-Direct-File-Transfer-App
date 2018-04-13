package com.app.wi_fi_direct.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.wi_fi_direct.helpers.callbacks.ChosenDirectoryListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DirectoryChooserDialog {
  private boolean isNewFolderEnabled = true;
  private String sdcardDirectory;
  private Context context;
  private TextView titleView;

  private String dir = "";
  private List<String> subdirs = null;
  private ChosenDirectoryListener chosenDirectoryListener;
  private ArrayAdapter<String> listAdapter = null;


  public DirectoryChooserDialog(Context context, ChosenDirectoryListener chosenDirectoryListener) {
    this.context = context;
    this.sdcardDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
    this.chosenDirectoryListener = chosenDirectoryListener;

    try {
      this.sdcardDirectory = new File(sdcardDirectory).getCanonicalPath();
    } catch (IOException ignored) {
    }
  }

  // setNewFolderEnabled() - enable/disable new folder button
  public void setNewFolderEnabled(boolean isNewFolderEnabled) {
    this.isNewFolderEnabled = isNewFolderEnabled;
  }

  public boolean getNewFolderEnabled() {
    return isNewFolderEnabled;
  }

  // chooseDirectory() - load directory chooser dialog for initial
  // default root directory
  public void chooseDirectory() {
    // Initial directory is sdcard directory
    chooseDirectory(sdcardDirectory);
  }

  // chooseDirectory(String dir) - load directory chooser dialog for initial
  // input 'dir' directory
  public void chooseDirectory(String dir) {
    File dirFile = new File(dir);
    if (!dirFile.exists() || !dirFile.isDirectory()) {
      dir = sdcardDirectory;
    }

    try {
      this.dir = new File(dir).getCanonicalPath();
    } catch (IOException ioe) {
      return;
    }

    this.dir = dir;
    this.subdirs = getDirectories(dir);

    AlertDialog.Builder dialogBuilder = createDirectoryChooserDialog(dir, this.subdirs, (DialogInterface dialog, int item) -> {
          // Navigate into the sub-directory
          this.dir += "/" + ((AlertDialog) dialog).getListView().getAdapter().getItem(item);
          this.updateDirectory();
        });

    dialogBuilder.setPositiveButton("OK", (DialogInterface dialog, int which) -> {
      // Current directory chosen
      // Call registered listener supplied with the chosen directory
      this.chosenDirectoryListener.onChosenDir(this.dir);
    }).setNegativeButton("Cancel", null);

    final AlertDialog dirsDialog = dialogBuilder.create();

    dirsDialog.setOnKeyListener((dialog, keyCode, event) -> {
      if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
        // Back button pressed
        if (this.dir.equals(sdcardDirectory)) {
          // The very top level directory, do nothing
          return false;
        } else {
          // Navigate back to an upper directory
          this.dir = new File(this.dir).getParent();
          this.updateDirectory();
        }

        return true;
      } else {
        return false;
      }
    });

    // Show directory chooser dialog
    dirsDialog.show();
  }

  private boolean createSubDir(String newDir) {
    File newDirFile = new File(newDir);
    return !newDirFile.exists() && newDirFile.mkdir();
  }

  private List<String> getDirectories(String dir) {
    List<String> dirs = new ArrayList<>();

    try {
      File dirFile = new File(dir);
      if (!dirFile.exists() || !dirFile.isDirectory()) {
        return dirs;
      }

      for (File file : dirFile.listFiles()) {
        if (file.isDirectory()) {
          dirs.add(file.getName());
        }
      }
    } catch (Exception ignored) {
    }

    Collections.sort(dirs, String::compareTo);

    return dirs;
  }

  private AlertDialog.Builder createDirectoryChooserDialog(String title, List<String> listItems,
                                                           DialogInterface.OnClickListener onClickListener) {
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

    // Create custom view for AlertDialog title containing
    // current directory TextView and possible 'New folder' button.
    // Current directory TextView allows long directory path to be wrapped to multiple lines.
    LinearLayout titleLayout = new LinearLayout(context);
    titleLayout.setOrientation(LinearLayout.VERTICAL);

    titleView = new TextView(context);
    titleView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    titleView.setTextAppearance(context, android.R.style.TextAppearance_Large);
    titleView.setTextColor(context.getResources().getColor(android.R.color.white));
    titleView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
    titleView.setText(title);

    Button newDirButton = new Button(context);
    newDirButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    newDirButton.setText("New folder");
    newDirButton.setOnClickListener(v -> {
      final EditText input = new EditText(context);

      // Show new folder name input dialog
      new AlertDialog.Builder(context).
          setTitle("New folder name").
          setView(input).setPositiveButton("OK", (dialog, whichButton) -> {
        Editable newDir = input.getText();
        String newDirName = newDir.toString();
        // Create new directory
        if (createSubDir(dir + "/" + newDirName)) {
          // Navigate into the new directory
          dir += "/" + newDirName;
          updateDirectory();
        } else {
          Toast.makeText(
              context, "Failed to create '" + newDirName +
                  "' folder", Toast.LENGTH_SHORT).show();
        }
      }).setNegativeButton("Cancel", null).show();
    });

    if (!isNewFolderEnabled) {
      newDirButton.setVisibility(View.GONE);
    }

    titleLayout.addView(titleView);
    titleLayout.addView(newDirButton);

    dialogBuilder.setCustomTitle(titleLayout);

    this.listAdapter = createListAdapter(listItems);

    dialogBuilder.setSingleChoiceItems(this.listAdapter, -1, onClickListener);
    dialogBuilder.setCancelable(false);

    return dialogBuilder;
  }

  private void updateDirectory() {
    this.subdirs.clear();
    this.subdirs.addAll(getDirectories(this.dir));
    this.titleView.setText(this.dir);

    this.listAdapter.notifyDataSetChanged();
  }

  private ArrayAdapter<String> createListAdapter(List<String> items) {
    return new ArrayAdapter<String>(context, android.R.layout.select_dialog_item, android.R.id.text1, items) {

      @NonNull
      @Override
      public View getView(int position, View convertView,
                          ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        if (v instanceof TextView) {
          // Enable list item (directory) text wrapping
          TextView tv = (TextView) v;
          tv.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
          tv.setEllipsize(null);
        }
        return v;
      }
    };
  }
}
