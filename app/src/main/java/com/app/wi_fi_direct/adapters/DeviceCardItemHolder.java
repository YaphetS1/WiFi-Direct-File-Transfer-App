package com.app.wi_fi_direct.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.app.wi_fi_direct.R;
import com.app.wi_fi_direct.models.DeviceModel;

public class DeviceCardItemHolder extends RecyclerView.ViewHolder {
  private TextView tvItemTitle;
  private DeviceModel tempDevice;

  public DeviceCardItemHolder(View itemView) {
    super(itemView);
    tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
  }

  public void bind(DeviceModel device) {
    tempDevice = device;
    tvItemTitle.setText(tempDevice.getTitle());
  }
}