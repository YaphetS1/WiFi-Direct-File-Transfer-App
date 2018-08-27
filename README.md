# WiFi-Direct-File-Transfer-App
WiFi Direct File Transfer is a experimental app that will allow sharing of data between Android devices running Android 4.0 or higher using a WiFi direct connection without the use of a WiFi access point. This will enable data transfer between devices without relying on any existing network infrastructure
<br>

![green horizontal](https://user-images.githubusercontent.com/34623610/42958461-7cfcda86-8b7d-11e8-8ec7-ea3242eb6862.png)

Currently available on the Play store.

<a href="https://play.google.com/store/apps/details?id=com.app.wi_fi_direct"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" height=36px /></a>

<br>

### Recommended Reading

[General Overview](http://developer.android.com/guide/topics/connectivity/wifip2p.html)  
[Service Discovery](http://developer.android.com/training/connect-devices-wirelessly/nsd-wifi-direct.html)  
[Power Consumption](http://www.drjukka.com/blog/wordpress/?p=95)  
[More Recommended Reading](http://www.drjukka.com/blog/wordpress/?p=81)

### WARNING

This app is currently in beta so functionality subject to change.

## Dependencies

This application is library independent

## Installation an Usage

Just build it and install it to your device :-)

WARNING: The application does not work in the emulator, because the emulator does not have the method WifiP2pManager.initialize()

## Requirements
- AndroidStudio

## Results

[![ScreenShot](https://img.youtube.com/vi/WJID7NYT0GY/0.jpg)](https://www.youtube.com/watch?v=WJID7NYT0GY)
[![ScreenShot](https://img.youtube.com/vi/NezpvBeRgrw/0.jpg)](https://www.youtube.com/watch?v=NezpvBeRgrw)

WiFi Direct File Transfer works with good performances.<br/>
The main problems are the "Discovery Phase" of this protocol and the Wi-Fi Direct's implementation in Android, in fact:<br/>
1. The discovery time is too high when the number of devices increases <br/>
2. After a certain time, a device is no longer discoverable from others, so you need to restart the Discovery Phase on all devices <br/>

This shows that it's possible to extend the Wi-Fi Direct protocol in Android in some particular and limited scenarios, for example a transfer files.

## News
- *09/04/2018* - **WiFi Direct File Transfer** Beta 1 public release
- *23/08/2018* - **WiFi Direct File Transfer** Beta 2 public release


## Features
You can:<br/>
1. show a list of nearby devices<br/>
2. manage connection and disconnection between devices<br/>
3. send message to dives ho are connected to you for check this device in peers list (not available in Android's official API)<br/>
4. sending multiple files on both of two devices<br/>
5. see what are you send/receive without disconnection, and open received files (just tap)<br/>
6. auto restarting servers of receiving files and device info

## Future extensions
- [x] Multiple files transfer
- [x] Progress bar in receive file
- [x] Who connected to your device check it in peer list
- [x] Fix corrupted files on specific devices (in fileServerAsyncTask)
- [x] Change path to receive
- [x] Change device name
- [x] Change transfer data between device, I mean read/write file by chunks
- [ ] Connect more clients (now only possible between 2 devices)
- [ ] and so on... ;)


## Images
<br/>

![alt tag](https://image.ibb.co/dH6ZUx/Screenshot_2018_04_09_00_29_52_964_com_app_wi_fi_direct.png)

![alt tag](https://image.ibb.co/hsOEUx/Screenshot_2018_04_09_00_30_08.png)

<br/>

## Usage
### General usage
1. Activate Wi-Fi on all devices
2. Open this app on all devices
3. App will auto discovering peers
4. Сhoose the device from peers list for connect
5. Wait until device are connecting
6. App will auto open file chooser
7. Choose the files what are you want to send

### Troubles

If you have a troubles with connection to peer:

- try to reconnect, if reconnection are successful, it will auto open file chooser
- if you have another issue just restart an app and wait about 1 min (recall it's beta app)
- if you have some troubles which are not described here, please open an issue or send email to 436910463q@gmail.com


## License

Copyright 2018 Dmitry Marinin

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

<br/>
**Created by Dmitry Marinin**
