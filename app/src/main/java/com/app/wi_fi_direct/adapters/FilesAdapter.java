package com.app.wi_fi_direct.adapters;

import android.content.Context;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.wi_fi_direct.R;
import com.app.wi_fi_direct.Variables;
import com.app.wi_fi_direct.models.FileModel;

import java.io.File;
import java.util.ArrayList;

public class FilesAdapter extends RecyclerView.Adapter<FilesViewHolder> {

  public File[] receivedFiles;
  public ArrayList<FilesViewHolder> filesViewHolders = new ArrayList<>();
  private Context context;

  public FilesAdapter(Context context) {
    this.receivedFiles = getFilesFromStorage(context);
    this.context = context;
  }

  @Override
  public FilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_item, parent, false);
    return new FilesViewHolder(view, context);
  }

  @Override
  public void onBindViewHolder(final FilesViewHolder holder, int position) {
    filesViewHolders.add(holder);

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
    File dir = new File(PreferenceManager.getDefaultSharedPreferences(context)
        .getString(Variables.APP_TYPE, Environment.getExternalStorageDirectory() + "/"
            + context.getApplicationContext().getPackageName()));

    File[] receivedFiles = dir.listFiles();

    if (receivedFiles == null) {
      return new File[]{};
    }
    return receivedFiles;
  }

}
