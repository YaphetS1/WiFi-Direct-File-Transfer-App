package com.app.wi_fi_direct.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;

public class FilesUtil {
  public static void openFile(Context context, File file) {
    Uri uri = FileProvider.getUriForFile(context, "com.app.wi_fi_direct", file);

    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.addCategory(Intent.CATEGORY_BROWSABLE);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.setDataAndType(uri, "*/*");
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

    Log.d("Receiver uri", uri.toString());
    try {
      context.startActivity(intent);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @NonNull
  public static String getFileName(String fileName) {
    int len = fileName.length();
    int start = len - 1;
    char[] temp = fileName.toCharArray();
    while (true) {
      if (temp[start] == '/') break;
      start--;
      if (start == -1) break;
    }
    return fileName.substring(start + 1);
  }

}
