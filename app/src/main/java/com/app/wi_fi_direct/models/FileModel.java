package com.app.wi_fi_direct.models;

import java.io.File;

public class FileModel {
  public static final int TYPE_PHOTO = 0;
  public static final int TYPE_APPLICATION = 1;
  public static final int TYPE_COMMON = 2;

  private File file;
  private int type;

  public FileModel(File file) {
    this.file = file;
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

}
