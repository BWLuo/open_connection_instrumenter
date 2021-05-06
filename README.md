# Goal 
This program takes in Android APKs and instruments calls to `URL.openConnection` so
that during the execution of the APK, when a call to `openConnection` is made, it
displays information in logcat. 

# Requirements
You need to have installed
  - Python3 
  - Java

And have the following `ENV_VARIABLES` set 
  - `$ANDROID_HOME` points to where your android sdk is installed.
    (if you installed Android Studios it should already be set, but the default is 
    under `/home/usr/Android/Sdk`)
  - `$PATH` includes folder `$ANDROID_HOME/build-tools/` because

# Usage 
### Running the instrumenter
There is a pre-built version of the project in `scripts/`.
Run the script `scripts/run_instrumenter.py` which takes the arguments in order
  1. Android platforms directory (e.g. `$ANDROID_HOME/platforms`)
  2. Android API level to use (e.g. `30` if you have the folder
     `$ANDROID_HOME/platforms/android-30/`. You can install new api versions
     through android studio)
  3. Path of apk to instrument 
  4. Output directory. The tool will output a APK with the same name as the apk
     to instrument
     
### Signing the instrumented apk
After the apk is instrumenter, the apk needs to be signed before installation on
an emulator or device. Run `scripts/sign_apk.py` which takes the arguments in order
  1. Path of apk to sign 

### Example commands
From the root of the project run
```
python3 scripts/run_instrumenter.py \
    /home/usr/Android/Sdk/platforms 30 \
    /apk/my_program.apk \
    /apk/output
    
python3 scripts/sign_apk.py \
    /apk/output/my_program.apk
```

### Running the apk 
After installing the apk on an emulator and running it, you can then see the log 
output by running `adb logcat`. If an openConnection statement is being run within
the apk you will see output similar to 
```
    "OPENCONNECTION HAS BEEN CALLED: id=[$id]"
```
where each id corresponds to a different call-site of the openConnection method