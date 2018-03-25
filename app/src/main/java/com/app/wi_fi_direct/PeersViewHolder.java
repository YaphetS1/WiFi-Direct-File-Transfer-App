package com.app.wi_fi_direct;

import android.net.wifi.p2p.WifiP2pDevice;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PeersViewHolder extends RecyclerView.ViewHolder {

  private TextView peerName;

  public View peerView;
  public WifiP2pDevice device;


  public PeersViewHolder(View itemView) {
    super(itemView);
    peerName = itemView.findViewById(R.id.tvItemTitle);
    peerView = itemView;
  }

  public void setPeer(WifiP2pDevice peer) {
    device = peer;
    peerName.setText(peer.deviceName);
    Log.d("Set Peer", "setPeer");
  }
}
