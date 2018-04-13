package com.app.wi_fi_direct.services;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.app.wi_fi_direct.R;
import com.app.wi_fi_direct.helpers.callbacks.Callback;

public class NavService {
  public static final String TAB = "TAB";
  public static final int TAB_RECOMMENDATIONS = 0;
  public static final int TAB_SEND = 1;
  public static final int TAB_RECEIVE = 2;
  public static final int TAB_SETTINGS = 3;

  public static void set(Activity activity, int tab) {
    ImageView ivRecommendations = activity.findViewById(R.id.ivRecommendations);
    ImageView ivSend = activity.findViewById(R.id.ivSend);
    ImageView ivReceive = activity.findViewById(R.id.ivReceive);
    ImageView ivSetting = activity.findViewById(R.id.ivSettings);

    TextView tvRecommendations = activity.findViewById(R.id.tvRecommendations);
    TextView tvSend = activity.findViewById(R.id.tvSend);
    TextView tvReceive = activity.findViewById(R.id.tvReceive);
    TextView tvSetting = activity.findViewById(R.id.tvSettings);
    
    switch (tab) {
      case TAB_RECOMMENDATIONS: {
        ivRecommendations.setImageResource(R.drawable.d_bottom_nav_star_active);
        ivSend.setImageResource(R.drawable.d_bottom_nav_send);
        ivReceive.setImageResource(R.drawable.d_bottom_nav_download);
        ivSetting.setImageResource(R.drawable.d_bottom_nav_settings);

        tvRecommendations.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
        tvSend.setTextColor(activity.getResources().getColor(R.color.cTextGrey));
        tvReceive.setTextColor(activity.getResources().getColor(R.color.cTextGrey));
        tvSetting.setTextColor(activity.getResources().getColor(R.color.cTextGrey));
        break;
      }
      case TAB_SEND: {
        ivRecommendations.setImageResource(R.drawable.d_bottom_nav_star);
        ivSend.setImageResource(R.drawable.d_bottom_nav_send_active);
        ivReceive.setImageResource(R.drawable.d_bottom_nav_download);
        ivSetting.setImageResource(R.drawable.d_bottom_nav_settings);

        tvRecommendations.setTextColor(activity.getResources().getColor(R.color.cTextGrey));
        tvSend.setTextColor(activity.getResources().getColor(R.color.cTextPrimary));
        tvReceive.setTextColor(activity.getResources().getColor(R.color.cTextGrey));
        tvSetting.setTextColor(activity.getResources().getColor(R.color.cTextGrey));
        break;
      }
      case TAB_RECEIVE: {
        ivRecommendations.setImageResource(R.drawable.d_bottom_nav_star);
        ivSend.setImageResource(R.drawable.d_bottom_nav_send);
        ivReceive.setImageResource(R.drawable.d_bottom_nav_download_active);
        ivSetting.setImageResource(R.drawable.d_bottom_nav_settings);

        tvRecommendations.setTextColor(activity.getResources().getColor(R.color.cTextGrey));
        tvSend.setTextColor(activity.getResources().getColor(R.color.cTextGrey));
        tvReceive.setTextColor(activity.getResources().getColor(R.color.cTextPrimary));
        tvSetting.setTextColor(activity.getResources().getColor(R.color.cTextGrey));
        break;
      }
      case TAB_SETTINGS: {
        ivRecommendations.setImageResource(R.drawable.d_bottom_nav_star);
        ivSend.setImageResource(R.drawable.d_bottom_nav_send);
        ivReceive.setImageResource(R.drawable.d_bottom_nav_download);
        ivSetting.setImageResource(R.drawable.d_bottom_nav_settings_active);

        tvRecommendations.setTextColor(activity.getResources().getColor(R.color.cTextGrey));
        tvSend.setTextColor(activity.getResources().getColor(R.color.cTextGrey));
        tvReceive.setTextColor(activity.getResources().getColor(R.color.cTextGrey));
        tvSetting.setTextColor(activity.getResources().getColor(R.color.cTextPrimary));
        break;
      }
      default: {
        
      }
    }
  }

  public static void setupTopNav(Activity activity, int titleRes, boolean showSwitch) {
    TextView tvTopNavTitle = activity.findViewById(R.id.tvTopNavTitle);
    Switch swEnableWifiDirect = activity.findViewById(R.id.swEnableWifiDirect);
    tvTopNavTitle.setText(titleRes);
    if (showSwitch) {
      swEnableWifiDirect.setVisibility(View.VISIBLE);
    } else {
      swEnableWifiDirect.setVisibility(View.INVISIBLE);
    }
  }

  public static void init(
      Activity activity
      , Callback recommendationsTabAction
      , Callback sendTabAction
      , Callback receiveTabAction
      , Callback settingsTabAction
      , int activeTab
  ) {
    init(activity, recommendationsTabAction, sendTabAction, receiveTabAction, settingsTabAction);
    set(activity, activeTab);
  }

  public static void init(
      Activity activity
      , Callback recommendationsTabAction
      , Callback sendTabAction
      , Callback receiveTabAction
      , Callback settingsTabAction
  ) {
    LinearLayout llRecommendations = activity.findViewById(R.id.llRecommendations);
    llRecommendations.setOnClickListener(l -> {
      if (recommendationsTabAction != null) {
        recommendationsTabAction.call();
        set(activity, TAB_RECOMMENDATIONS);
      }
    });

    LinearLayout llSend = activity.findViewById(R.id.llSend);
    llSend.setOnClickListener(l -> {
      if (sendTabAction != null) {
        sendTabAction.call();
        set(activity, TAB_SEND);
      }
    });

    LinearLayout llReceive = activity.findViewById(R.id.llReceive);
    llReceive.setOnClickListener(l -> {
      if (receiveTabAction != null) {
        receiveTabAction.call();
        set(activity, TAB_RECEIVE);
      }
    });

    LinearLayout llSettings = activity.findViewById(R.id.llSettings);
    llSettings.setOnClickListener(l -> {
      if (settingsTabAction != null) {
        settingsTabAction.call();
        set(activity, TAB_SETTINGS);
      }
    });
  }
}
