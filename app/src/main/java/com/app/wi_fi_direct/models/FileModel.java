package com.app.wi_fi_direct.models;

import android.net.Uri;
import java.io.File;

public class FileModel {
  public static final int TYPE_PHOTO = 0;
  public static final int TYPE_APPLICATION = 1;
  public static final int TYPE_COMMON = 2;

  private File file;
  private int type;
  private Uri uri;
  private Long fileLength;
  private String fileName;
  private boolean isTransfered = false;

  public FileModel(File file) {
    this.file = file;
  }

  public FileModel(Uri uri, Long fileLength, String fileName) {
    this.uri = uri;
    this.fileLength = fileLength;
    this.fileName = fileName;
  }

  public FileModel(File file, int type) {
    this.file = file;
    this.type = type;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public Uri getUri() {
    return uri;
  }

  public void setUri(Uri uri) {
    this.uri = uri;
  }

  public Long getFileLength() {
    return fileLength;
  }

  public void setFileLength(Long fileLength) {
    this.fileLength = fileLength;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public boolean isTransfered() {
    return isTransfered;
  }

  public void setTransfered(boolean transfered) {
    isTransfered = transfered;
  }
}
