package com.app.wi_fi_direct.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.wi_fi_direct.R;
import com.app.wi_fi_direct.models.DeviceModel;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceCardItemHolder> {
  private List<DeviceModel> devicesList;

  public DeviceListAdapter(List<DeviceModel> devices) {
    devicesList = devices;
  }

  @Override
  public DeviceCardItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_list_item, parent, false);
    return new DeviceCardItemHolder(view);
  }

  @Override
  public void onBindViewHolder(DeviceCardItemHolder holder, int position) {
    DeviceModel tempDevice = devicesList.get(position);
    holder.bind(tempDevice);
  }

  @Override
  public int getItemCount() {
    return devicesList.size();
  }
}