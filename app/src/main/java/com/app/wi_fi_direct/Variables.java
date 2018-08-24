package com.app.wi_fi_direct;

import android.app.Application;
import android.content.Context;

public class Variables extends Application {
  public static final String APP_TYPE = "com.app.wi_fi_direct";
  public static final String NAME_DEVICE = "com.app.wi_fi_direct.name";

  private static Variables instance;

  @Override
  public void onCreate() {
    instance = this;
    super.onCreate();
  }

  public static Variables getInstance() {
    return instance;
  }

  public static Context getContext() {
    return instance;
    // or return instance.getApplicationContext();
  }
}
