SSAI Plugin
=============

Provides a sample app illustrating how to configure and run the *Brightcove Native Player for Android* **SSAI Plugin**.

## Installing and starting the sample app

To focus solely on the SSAI Plugin, execute these steps from the top level sample app directory:

    gradlew :BasicSsaiSampleApp:clean :BasicSsaiSampleApp:build :BasicSsaiSampleApp:installDebug

while a device is connected.  Then start the app by selecting it in the app folder on the device.

## Sample app code details

All sample apps follow a similar implementation pattern.  The **onCreate(Bundle)** method in the main activity class sets the content view, establishes the video view object and registers event handlers.  The per-plugin operations achieve the expected results for each individual plugin.  In the case of the SSAI Plugin, this simply boils down to creating a plugin instance and passing an SSAI URL to the **SSAIComponent#processVideo(String)** method:

        ...
        plugin = new SSAIComponent(this, brightcoveVideoView);
        View view = findViewById(R.id.ad_frame);
        if (view != null && view instanceof ViewGroup) {
            plugin.addCompanionContainer((ViewGroup) view);
        }
        plugin.processVideo(ssaiAdDataUrl);
        ...
 