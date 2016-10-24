Customized Controls Sample App
==============================

The Brightcove Native SDK fot Android provides two layouts for the media controller:
  1. The controls are shown in two rows by default (layout folder).
  2. The controls are shown in one row when the minimum available width is 480dp ([layout-w480dp](https://developer.android.com/guide/practices/screens_support.html#NewQualifiers) folder in the Brightcove Native SDK).

There is a separate layout used only for the Android TV media controller (tv_media_controller.xml in layout folder), which will be picked automatically when the app is running on an Android TV. You can check if you are on TV mode by calling boolean *BrightcoveMediaController.checkTvMode(context)*.

To create your own customized media controller, use the pre-defined IDs listed below. This ensures proper integration with the Brightcove Native SDK for Android.

1. _**play**_. type:android.widget.Button
2. _**rewind**_. type:android.widget.Button
3. _**fast_forward**_. type:android.widget.Button
4. _**current_time**_. type:android.widget.TextView
5. _**time_separator**_. type:android.widget.TextView
6. _**end_time**_. type:android.widget.TextView
7. _**seek_bar**_. type:com.brightcove.player.mediacontroller.BrightcoveSeekBar
8. _**captions**_. type:android.widget.Button
9. _**full_screen**_. type:android.widget.Button
10. _**live**_. type:android.widget.Button
11. _**audio_tracks**_. type:android.widget.Button
12. _**player_options**_. type:android.widget.Button
13. _**one_line_spacer**_. type:android.view.View
14. _**two_line_spacer**_. type:android.view.View

By default, all the items which are of type Button, have a text image instead of a drawable background. This image is obtained from the fontawesome-webfont.ttf.

To start creating your media controller, you need to create an XML file layout (BrightcoveControlBar layout) and place it on res/layout or any of the variants, as shown here “[Supporting Multiple Screens Android guide](https://developer.android.com/guide/practices/screens_support.html)”. For a straightforward and easier implementation, name your BrightcoveControlBar layout as **default_media_controller.xml**, or **tv_media_controller.xml** for android tv. This way the Brightcove Native SDK for Android will pick up the layout automatically, and you could also override the default media controller.

Once the xml layout file is created,  you need to add the com.brightcove.player.mediacontroller.BrightcoveControlBar tag to your BrightcoveControlBar layout.
An example is shown below:
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.brightcove.player.mediacontroller.BrightcoveControlBar
   xmlns:android="http://schemas.android.com/apk/res/android"
   xmlns:bmc="http://schemas.android.com/apk/res-auto"
   xmlns:tools="http://schemas.android.com/tools"
   tools:ignore="Overdraw, InconsistentLayout"
   android:id="@+id/brightcove_control_bar"
   android:background="@color/bmc_background"
   android:layout_width="match_parent"
   android:layout_height="wrap_content"
   android:padding="8dp"
   android:orientation="horizontal"
   android:layout_gravity="bottom"
   style="@style/BrightcoveControlBar">
…
</com.brightcove.player.mediacontroller.BrightcoveControlBar>
```

Note that we added the style="@style/BrightcoveControlBar". It is recommended to keep the style.

Now add the views inside the BrightcoveControlBar tag with their corresponding IDs shown above. The Buttons style is "BorderlessButton" and the BrightcoveSeekBar is "BrightcoveSeekBar".

An example of the play button:
```xml
<Button
   android:id="@id/play"
   style="@style/BorderlessButton"
   android:layout_height="wrap_content"
   android:layout_width="wrap_content"
   android:visibility="gone"
   android:text="@string/brightcove_controls_play"/>
```
An example of the BrightcoveSeekBar:
```xml
<com.brightcove.player.mediacontroller.BrightcoveSeekBar
   android:id="@id/seek_bar"
   style="@style/BrightcoveSeekBar"
   android:layout_height="wrap_content"
   android:layout_width="0dp"
   android:layout_weight="1"
   android:indeterminateOnly="false"
   android:splitTrack="false"
   android:visibility="gone"
   bmc:brightcove_marker_color="@color/bmc_seekbar_marker"
   bmc:brightcove_marker_width="5.0"/>
```
###### What are *one_line_spacer* and *two_line_spacer*?
The *one_line_spacer* and *two_line_spacer* are used in the one row and two row media controller, respectively. Their purpose is to set the fill space between two sets of controller buttons. Some of the properties include *layout_height*, *layout_width* and *layout_weight*.
In the two row controller the *two_line_spacer is adding the space between the rewind and the fullscreen button. In the one row controller the *two_line_spacer* is between the BrightcoveSeekBar and the fullscreen button.
```xml
<View
   tools:ignore="InconsistentLayout"
   android:id="@id/one_line_spacer"
   android:layout_height="0dp"
   android:layout_width="0dp"
   android:layout_weight="1"
   android:visibility="gone"/>
```
By default, the views for the Media controller, including Buttons, TextView and the BrightcoveSeekBar, have the visibility set to "gone" in the XML file. They will be set to *View.VISIBLE* on the BrightcoveMediaController constructor and when the video is set (the *DID_SET_VIDEO* event was sent), depending on what is available for each video. For example, the caption button will only be visible if there are captions available for the video.

For the Android TV media controller, the captions and audio tracks buttons will not appear. Instead, if either of them is available, the player options button (gear icon) will appear. The available audio tracks and captions will be on the side menu after pressing the player options button.

**NOTE**:
You can choose a different name, but then you will need to create an instance of BrightcoveMediaController and set the layout id in the constructor. You will also need to set the controller to the video view.
Example:
```java
BrightcoveMediaController mediaController = new BrightcoveMediaController(brightcoveVideoView, R.layout.my_media_controller);
brightcoveVideoView.setMediaController(mediaController);
```
