package com.app.wi_fi_direct.models;

public class FileModel {
  public static final int TYPE_PHOTO = 0;
  public static final int TYPE_APPLICATION = 1;
  public static final int TYPE_COMMON = 2;

  private String title;
  private int type;

  public FileModel(String title) {
    this.title = title;
  }

  public FileModel(String title, int type) {
    this.title = title;
    this.type = type;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

}
