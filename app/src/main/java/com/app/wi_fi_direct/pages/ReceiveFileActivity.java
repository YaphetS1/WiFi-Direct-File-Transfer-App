package com.app.wi_fi_direct.pages;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.app.wi_fi_direct.R;
import com.app.wi_fi_direct.adapters.DeviceListAdapter;
import com.app.wi_fi_direct.adapters.FileListAdapter;
import com.app.wi_fi_direct.models.DeviceModel;
import com.app.wi_fi_direct.models.FileModel;

import java.util.ArrayList;
import java.util.List;

public class ReceiveFileActivity extends AppCompatActivity {

  private RecyclerView rvDevicesList;
  private List<DeviceModel> devicesList;
  private DeviceListAdapter deviceListAdapter;

  private RecyclerView rvFilesList;
  private List<FileModel> filesList;
  private FileListAdapter fileListAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_receive_file);

    rvDevicesList = findViewById(R.id.rvDevicesList);
    LinearLayoutManager devicesListLayoutManager = new LinearLayoutManager(
        this, LinearLayoutManager.VERTICAL, false);
    rvDevicesList.setLayoutManager(devicesListLayoutManager);

    devicesList = new ArrayList<>();
    devicesList.add(new DeviceModel("Устройство 1"));
    devicesList.add(new DeviceModel("Устройство 2"));
    devicesList.add(new DeviceModel("Устройство 3"));
    devicesList.add(new DeviceModel("Устройство 4"));
    devicesList.add(new DeviceModel("Устройство 5"));
    devicesList.add(new DeviceModel("Устройство 6"));
    deviceListAdapter = new DeviceListAdapter(devicesList);
    rvDevicesList.setAdapter(deviceListAdapter);


    rvFilesList = findViewById(R.id.rvFilesList);
    LinearLayoutManager filesListLayoutManager = new LinearLayoutManager(
        this, LinearLayoutManager.VERTICAL, false);
    rvFilesList.setLayoutManager(filesListLayoutManager);

    filesList = new ArrayList<>();
    filesList.add(new FileModel("Файл 1", FileModel.TYPE_COMMON));
    filesList.add(new FileModel("Файл 2", FileModel.TYPE_COMMON));
    filesList.add(new FileModel("Приложение 1", FileModel.TYPE_APPLICATION));
    filesList.add(new FileModel("Фото 1", FileModel.TYPE_PHOTO));
    filesList.add(new FileModel("Фото 2", FileModel.TYPE_PHOTO));
    filesList.add(new FileModel("Фото 3", FileModel.TYPE_PHOTO));
    filesList.add(new FileModel("Файл 3", FileModel.TYPE_COMMON));
    filesList.add(new FileModel("Приложение 2", FileModel.TYPE_APPLICATION));
    filesList.add(new FileModel("Файл 4", FileModel.TYPE_COMMON));
    filesList.add(new FileModel("Приложение 3", FileModel.TYPE_APPLICATION));
    fileListAdapter = new FileListAdapter(filesList);
    rvFilesList.setAdapter(fileListAdapter);
  }
}
