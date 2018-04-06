package com.app.wi_fi_direct.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.wi_fi_direct.R;
import com.app.wi_fi_direct.helpers.FilesUtil;
import com.app.wi_fi_direct.models.FileModel;

public class FilesSendViewHolder extends RecyclerView.ViewHolder {

  public ProgressBar progressBar;
  public ImageView stateFile;
  public FileModel fileModel;

  private TextView tvItemTitle;
  private ImageView ivItemType;
  private View view;

  FilesSendViewHolder(View itemView) {
    super(itemView);
    this.view = itemView;

    tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
    ivItemType = itemView.findViewById(R.id.ivItemType);
    progressBar = itemView.findViewById(R.id.progressBar);
    stateFile = itemView.findViewById(R.id.ivItemStatus);
  }

  void bind(FileModel fileModel) {
    this.fileModel = fileModel;

    tvItemTitle.setText(FilesUtil.getFileName(fileModel.getFileName()));

//      switch (fileModel.getType()) {
//        case FileModel.TYPE_PHOTO: {
//          ivItemType.setImageResource(R.drawable.d_icon_photo);
//          break;
//        }
//        case FileModel.TYPE_APPLICATION: {
//          ivItemType.setImageResource(R.drawable.d_icon_app);
//          break;
//        }
//        case FileModel.TYPE_COMMON: {
//          ivItemType.setImageResource(R.drawable.d_icon_file);
//          break;
//        }
//      }
  }
}
