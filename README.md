# Intelligent Computation enabled Smartphone Imaging for Oximetry and Clinical Diagnosis of Cardiovascular Disease (ICSO)
A real-time, auxiliary-free system exploring cyanosis and local oxygen concentration as a convenient and rapid method for clinical diagnosis, including monitoring cardiovascular disease and anemia. <br>
<br>
See our manuscript and method tutorial for more details. DOI: 10.1021/acssensors.4c01588 <br>
## Quick start
You can simply and quickly run and use the demo test of our models including oxygen saturation measurement, disease diagnosis and skin tone-based correction (See Python demo below). And we supply some de-identified images for the demo test.<br>
* 2024.08 - Update: For ease of installation, we upload the app package (and app instruction & tutorial) into this [link](https://pan.baidu.com/s/1pnBrWSkrrsQIlFAOCgeIHg) (key: hzai). You can now install the app by downloading the installation package (note: if the package is not supported by your current smartphone, we recommend you to build it from the source code guided by the tutorial here).
### Requirements
Python --- 3.8.6

Keras --- 2.8.0

Tensorflow --- 2.8.0

Pandas --- 1.3.5

NumPy --- 1.21.2

Joblib ---1.1.0

### Usage
You can quickly run the demo test of our models as follows: e.g. run demo_oxy_measurement.py
```
python demo_oxy_measurement.py
```
Moreover, you can also simply run the demo using integrated development enviroment (IDE) "Pycharm (JetBrains, recommendation version --- PyCharm 2020.3.3 (Community Edition) for Windows)" as follows: 
```
Open -> "Demo" -> Run demo_XXX.py
```
Change the test model by replacing the code name with corresponding that like demo_XXX.py.<br>
See the folder ```./Demo``` to find corresponding models.<br>
See code annotation for more test details. Expected run time for demo on a "normal" desktop computer is within one minute.
## Installation and usage of the app
### Requirements
Java --- 13.0.2

Android Studio --- Bumblebee 2021.1.1 Patch 3 (Recommendation)

### Import the project to run and install the app
Corresponding models and algorithms have been integrated in the project (See Folder ```./ICSO-app/app/src/main``` for more details). You can quickly run and install the app using Android Studio as follows:

* Open -> "ICSO-app" -> Run 'app': ``` src/main/java/com/demo/icso/StartActivity.java ```

Now you can use the app in your smartphone (please ensure the device is connected and developer mode is turned on) or Android Emulator in Android Studio (Recommendation model: Pixel XL API 29).
* Click button "OXYGEN_SATURATION_MEASUREMENT" to perform oxygen saturation measurement.
* Click button "SBC_SAMPLE_MEASUREMENT" to perform sample measurement.<br>
* See ```./Sample``` for corresponding test images.
* We encourage users to add customized model and function into the project. Just import your trained tensorflow model ```XXX.tflite``` into the path ```src/main/ml```, and run inference.
### Usage the app in your project
1. Import files ```src/main/java/com/demo/icso/Clinic_Oxy_Activity.java``` and  ```src/main/java/com/demo/icso/CatchrgbActivity.java``` into your project for oxygen saturation measurement and sample measurement respectively, and add register code of activities into your ```AndroidManifest.xml ``` as follows:
```
<activity android:name=".Clinic_Oxy_Activity" />
<activity android:name=".CatchrgbActivity" />
```
And import ```src/main/java/com/demo/icso/Bean``` and ```src/main/java/com/demo/icso/Util``` into your project (Note: import path same as above), these include necessary tools to run the app.<br>

2. Import files ```src/main/res/layout/activity_clinic_oxy.xml```, ```src/main/res/layout/activity_rgb.xml``` and ```src/main/res/layout/activity_start.xml``` into your project: ```src/main/res/layout```<br>

3. Import file ```src/main/java/com/demo/icso/StartActivity.java``` into your project (same as ```part 1```) for starting activity, and add register code of activity into your ```AndroidManifest.xml ``` as follows:
```
<activity
            android:name=".StartActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
</activity>

 ```
4. In ```AndroidManifest.xml ```
* Add a content provider into your project as follows:
```
<provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="XXX.XXX.XXX.fileprovider" 
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
</provider>
```
Change ```android:authorities``` with your path e.g. ```com.demo.icso.fileprovider``` in our project.
Import ```src/main/res/xml/file_paths.xml``` into path ```src/main/res/xml``` in your project.
* And add corresponding root code:
```
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
5. Import the trained tensorflow model ```src/main/ml/clinic_model_oxy.tflite``` into path ```src/main/ml``` in your project.<br>
Import menu ```src/main/res/menu/my_menu.xml``` into path ```src/main/res/menu``` in your project.<br>
* Add dependencies
```
    implementation 'org.tensorflow:tensorflow-lite-support:0.1.0'
    implementation 'org.tensorflow:tensorflow-lite-metadata:0.1.0'
    implementation 'org.tensorflow:tensorflow-lite-gpu:2.3.0'
```
* We encourage users to customize the project. Just modifiy ```StartActivity.java``` and ```activity_start.xml```<br>
Now you can use the app function in your project.
