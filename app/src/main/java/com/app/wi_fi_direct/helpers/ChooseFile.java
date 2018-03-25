package com.app.wi_fi_direct.helpers;

import android.app.Activity;
import android.content.Intent;

public class ChooseFile {

  public static final int FILE_TRANSFER_CODE = 69;

  public static void fileChooser(Activity activity) {

    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    intent.setType("*/*");
    intent.addCategory(Intent.CATEGORY_OPENABLE);

    try {
      activity.startActivityForResult(Intent.createChooser(intent, "Choose File to Transfer"), FILE_TRANSFER_CODE);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
