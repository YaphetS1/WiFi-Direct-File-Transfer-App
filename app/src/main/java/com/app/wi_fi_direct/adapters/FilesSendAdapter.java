package com.app.wi_fi_direct.adapters;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.wi_fi_direct.R;
import com.app.wi_fi_direct.models.FileModel;

import java.util.ArrayList;

public class FilesSendAdapter extends RecyclerView.Adapter<FilesSendViewHolder> {

  public ArrayList<Uri> uris = new ArrayList<>();
  public ArrayList<Long> filesLength = new ArrayList<>();
  public ArrayList<String> fileNames = new ArrayList<>();

  public ArrayList<FilesSendViewHolder> filesViewHolders = new ArrayList<>();
  public int index = -1;

  public FilesSendAdapter() {
  }

  @Override
  public FilesSendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_item, parent, false);
    return new FilesSendViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final FilesSendViewHolder holder, int position) {
    filesViewHolders.add(holder);

    holder.bind(new FileModel(uris.get(position),
        filesLength.get(position), fileNames.get(position)));
  }

  @Override
  public int getItemCount() {
    return uris.size();
  }

  public void notifyAdapter(ArrayList<Uri> uris,
                            ArrayList<Long> filesLength,
                            ArrayList<String> fileNames) {
    this.uris.addAll(uris);
    this.filesLength.addAll(filesLength);
    this.fileNames.addAll(fileNames);

    this.notifyDataSetChanged();
  }

  public boolean isHaveNotTransferred() {
    boolean needToTransfer = false;
    for (int i = 0; i < filesViewHolders.size(); i++) {
      if (!filesViewHolders.get(i).fileModel.isTransfered()) {
        needToTransfer = true;
        index = i;
        break;
      }
    }
    return needToTransfer;
  }
}
