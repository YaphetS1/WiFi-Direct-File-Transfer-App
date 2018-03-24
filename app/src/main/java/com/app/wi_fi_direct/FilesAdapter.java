package com.app.wi_fi_direct;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.Date;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FilesViewHolder> {

  File[] receivedFiles;
  Context context;

  public FilesAdapter(Context context, File[] receivedFiles) {
    this.receivedFiles = receivedFiles;
    this.context = context;
  }

  @Override
  public FilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    View view = LayoutInflater.from(context).inflate(R.layout.file_list_item, parent, false);

    FilesViewHolder filesViewHolder = new FilesViewHolder(view);

    return filesViewHolder;
  }

  @Override
  public void onBindViewHolder(final FilesViewHolder holder, int position) {
    holder.file = receivedFiles[position];
    holder.receivedFileName.setText(FilesUtil.getFileName(receivedFiles[position].getPath()));
    holder.receivedFileDate.setText(new Date(receivedFiles[position].lastModified()).toString());
    holder.linearLayout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        FilesUtil.openFile(context, holder.file);
      }
    });
  }

  @Override
  public int getItemCount() {
    return receivedFiles.length;
  }

  public class FilesViewHolder extends RecyclerView.ViewHolder {

    public TextView receivedFileName, receivedFileDate;
    public LinearLayout linearLayout;
    public File file;

    public FilesViewHolder(View itemView) {
      super(itemView);
      receivedFileName = itemView.findViewById(R.id.receivedFileName);
      receivedFileDate = itemView.findViewById(R.id.receivedFileDate);
      linearLayout = itemView.findViewById(R.id.receivedFile);
    }
  }

}
