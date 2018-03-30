package com.app.wi_fi_direct.adapters;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.wi_fi_direct.R;
import com.app.wi_fi_direct.helpers.FilesUtil;
import com.app.wi_fi_direct.models.FileModel;

import java.io.File;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FilesViewHolder> {

  private File[] receivedFiles;
  private Context context;

  public FilesAdapter(Context context) {
    this.receivedFiles = getFilesFromStorage(context);
    this.context = context;
  }

  @Override
  public FilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_item_new, parent, false);
    return new FilesViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final FilesViewHolder holder, int position) {
    holder.bind(new FileModel(receivedFiles[position]));
  }

  @Override
  public int getItemCount() {
    return receivedFiles.length;
  }

  public void notifyAdapter() {
    this.receivedFiles = this.getFilesFromStorage(this.context);
    this.notifyDataSetChanged();
  }

  private File[] getFilesFromStorage(Context context) {
    File dir = new File(Environment.getExternalStorageDirectory() + "/"
        + context.getApplicationContext().getPackageName());

    File[] receivedFiles = dir.listFiles();

    if (receivedFiles == null) {
      return new File[]{};
    }
    return receivedFiles;
  }


  class FilesViewHolder extends RecyclerView.ViewHolder {

    private TextView tvItemTitle;
    private ImageView ivItemType;
    private ProgressBar progressBar;
    private View view;
    private FileModel fileModel;

    FilesViewHolder(View itemView) {
      super(itemView);
      view = itemView;

      tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
      ivItemType = itemView.findViewById(R.id.ivItemType);

//      progressBar = itemView.findViewById(R.id.receiveProgressBar);
//      progressBar.setVisibility(View.INVISIBLE);
    }

    void bind(FileModel fileModel) {
      this.fileModel = fileModel;

      tvItemTitle.setText(FilesUtil.getFileName(fileModel.getFile().getPath()));

      view.setOnClickListener(v -> {
        FilesUtil.openFile(context, this.fileModel.getFile());
      });

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
}
