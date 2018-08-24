package com.app.wi_fi_direct.helpers;

import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DeviceUtil {
  public static void callHiddenMethod(WifiP2pManager manager, WifiP2pManager.Channel channel, String newName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Class[] paramTypes = new Class[3];
    paramTypes[0] = WifiP2pManager.Channel.class;
    paramTypes[1] = String.class;
    paramTypes[2] = WifiP2pManager.ActionListener.class;
    Method setDeviceName = manager.getClass().getMethod(
        "setDeviceName", paramTypes);
    setDeviceName.setAccessible(true);

    Object arglist[] = new Object[3];
    arglist[0] = channel;
    arglist[1] = newName;
    arglist[2] = new WifiP2pManager.ActionListener() {

      @Override
      public void onSuccess() {
        Log.d("setDeviceName succeeded", "true");
      }

      @Override
      public void onFailure(int reason) {
        Log.d("setDeviceName failed", "true");
      }
    };
    setDeviceName.invoke(manager, arglist);
  }
}
