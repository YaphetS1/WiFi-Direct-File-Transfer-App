package com.app.wi_fi_direct.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.wi_fi_direct.R;
import com.app.wi_fi_direct.services.ConnectPeer;

import java.util.ArrayList;
import java.util.List;

public class PeersAdapter extends RecyclerView.Adapter<PeersViewHolder> {

  public ArrayList<PeersViewHolder> peersViewHolders = new ArrayList<>();
  private PeersViewHolder tempHolder;

  List<WifiP2pDevice> peersList;
  Context context;
  WifiP2pManager manager;
  WifiP2pManager.Channel channel;
  WifiP2pManager.ActionListener listener;
  Activity activity;
  WifiP2pManager.ConnectionInfoListener infoListener;

  public PeersAdapter(List<WifiP2pDevice> peersList,
                      final Context context,
                      final WifiP2pManager manager,
                      final WifiP2pManager.Channel channel,
                      final Activity activity,
                      final WifiP2pManager.ConnectionInfoListener infoListener) {
    this.peersList = peersList;
    this.context = context;
    this.manager = manager;
    this.channel = channel;
    this.activity = activity;
    this.infoListener = infoListener;

    listener = new WifiP2pManager.ActionListener() {
      @Override
      public void onSuccess() {
        tempHolder.statePeer.setImageResource(R.drawable.d_icon_done);
        tempHolder.itemSyncing.setVisibility(View.INVISIBLE);

        manager.requestConnectionInfo(channel, infoListener);
        Log.d("ConnectPeer ","Success");
      }

      @Override
      public void onFailure(int reason) {
        Toast.makeText(context, "Connection Failed", Toast.LENGTH_LONG).show();
        Log.d("ConnectPeer ", "Fail");
      }
    };
  }

  @Override
  public PeersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.device_list_item, parent, false);
    return new PeersViewHolder(view);
  }

  @Override
  public void onBindViewHolder(PeersViewHolder holder, int position) {
    holder.setPeer(peersList.get(position));
    peersViewHolders.add(holder);

    try {
      final String deviceAddress = holder.device.deviceAddress;

      holder.peerView.setOnClickListener(v -> {
        ConnectPeer.connect(deviceAddress, manager, channel, context, listener);
        holder.statePeer.setImageResource(R.drawable.d_icon_refresh);
        holder.statePeer.setVisibility(View.VISIBLE);
        holder.itemSyncing.setVisibility(View.VISIBLE);

        tempHolder = holder;
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public int getItemCount() {
    return peersList.size();
  }

  public void updateList(List<WifiP2pDevice> devices) {
    peersList = devices;
    peersViewHolders.clear();

    Log.d("Adapter ", "ON UPDATE LIST");
  }
}
