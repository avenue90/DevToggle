# Debug settings (DevToggle)

One tap to switch Developer options + USB debugging + Wireless debugging on, and one tap to
switch all three off again (so banking apps that refuse to run with Developer options enabled
work). Home-screen icon and a Quick Settings tile ("Wireless debug").

## One-time setup
```
./gradlew :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell pm grant com.ketan.devtoggle android.permission.WRITE_SECURE_SETTINGS
```
The grant survives reboots and app updates; repeat it only after uninstalling.
Wireless debugging turns on only while the phone is on Wi-Fi.
