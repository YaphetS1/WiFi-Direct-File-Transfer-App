package com.app.wi_fi_direct.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.wi_fi_direct.R;
import com.app.wi_fi_direct.models.FileModel;

public class FileCardItemHolder extends RecyclerView.ViewHolder {
  private TextView tvItemTitle;
  private ImageView ivItemType;
  private FileModel tempFile;

  public FileCardItemHolder(View itemView) {
    super(itemView);
    tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
    ivItemType = itemView.findViewById(R.id.ivItemType);
  }

  public void bind(FileModel fileModel) {
    tempFile = fileModel;
    tvItemTitle.setText(tempFile.getTitle());
    switch (fileModel.getType()) {
      case FileModel.TYPE_PHOTO: {
        ivItemType.setImageResource(R.drawable.d_icon_photo);
        break;
      }
      case FileModel.TYPE_APPLICATION: {
        ivItemType.setImageResource(R.drawable.d_icon_app);
        break;
      }
      case FileModel.TYPE_COMMON: {
        ivItemType.setImageResource(R.drawable.d_icon_file);
        break;
      }
    }
  }
}