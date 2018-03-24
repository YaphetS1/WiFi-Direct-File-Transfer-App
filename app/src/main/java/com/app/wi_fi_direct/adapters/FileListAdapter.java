package com.app.wi_fi_direct.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.wi_fi_direct.R;
import com.app.wi_fi_direct.models.FileModel;

import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileCardItemHolder> {
  private List<FileModel> filesList;

  public FileListAdapter(List<FileModel> devices) {
    filesList = devices;
  }

  @Override
  public FileCardItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_item_new, parent, false);
    return new FileCardItemHolder(view);
  }

  @Override
  public void onBindViewHolder(FileCardItemHolder holder, int position) {
    FileModel tempDevice = filesList.get(position);
    holder.bind(tempDevice);
  }

  @Override
  public int getItemCount() {
    return filesList.size();
  }
}