Styled Controls Sample App
==========================

This sample is meant to explain the options you have to style the Brightcove default media controller and the tv media controller.

The most important code in this sample can be found in the styles.xml file (res/values/styles.xml), where you will find the modified style attributes.

### Styling the media controller

You can style the default media controller (default_media_controller.xml) by modifying or adding attributes in each style.
The list of styles used is below:

1. **BrightcoveControlBar**. This style is applied to the whole media controller. The main purpose is to enable and disable buttons, as well as to change other parameters. These values are inherited from *BrightcoveControlBarDefault*.
    * *brightcove_audio_tracks*: Boolean (default true).
    * *brightcove_closed_captions*: Boolean (default true).
    * *brightcove_full_screen*: Boolean (default true).
    * *brightcove_live*: Boolean (default true).
    * *brightcove_play*: Boolean (default true).
    * *brightcove_rewind*: Boolean (default true).
    * *brightcove_seekbar*: Boolean (default true).
    * *brightcove_fast_forward*: Boolean (default false).
    * *brightcove_animation_style*: String “fade” or “slide”.
    * *brightcove_marker_color*: Color.
    * *brightcove_timeout*: Integer. The default delay, in milliseconds, from showing the control bar to automatically hiding it.
    * *brightcove_align*: Boolean. When set to true, the controller will automatically adapt to the video content base. Set to false, if you want to control the position of it.



2. **BrightcoveSeekBar**. This style is used for the progress/seek bar. Their values are inherited from *BrightcoveSeekBarDefault* which also inherit from *android:Widget.Holo.Light.SeekBar*. Please see [Android SeekBar Attributes](https://developer.android.com/reference/android/widget/SeekBar.html) to know what values can be changed.
3. **BorderlessButton**. This style is used for the control buttons. Their values are inherited from *BorderlessButtonDefault* which also inherit from *android:Widget.Button*. Please see [Android Button Attibutes](https://developer.android.com/reference/android/widget/Button.html) to know what values can be changed.

You can modify one or more default values to any of the styles listed above.
For example: you can modify the BrightcoveControlBar to hide the seek bar and the closed captions button as shown below:

In res/values/styles.xml
```xml
<resources xmlns:android="http://schemas.android.com/apk/res/android">
   <style name="BrightcoveControlBar" parent="BrightcoveControlBarDefault">
       <item name="brightcove_closed_captions">false</item>
       <item name="brightcove_rewind">false</item>
   </style>
</resources>
```
**Notes:**
  * It is important to add the parent ```parent="BrightcoveControlBarDefault"``` to inherit the rest of the default values.
  * It’s equally important to keep the same name as BrightcoveControlBar (the same applies for the rest of the styles), so that the default media controller layout will automatically pick up this style.

##### Styling Android TV media controller
There are some styles which are applied exclusively to the Brightcove media controller used in Android TV (tv_media_controller.xml).

In Android, you have the option to [inherit](https://developer.android.com/guide/topics/ui/themes.html#Inheritance) values from a previously defined style without the need to specify the parent attribute, using the Parent.Children pattern. The Android TV styles use this pattern: *“BrightcoveControlBar.TV”*.

1. **BrightcoveControlBar.TV**. This inherit from *BrightcoveControlBar* and there are only a few changes for TV.
     * *brightcove_full_screen*: Now this value is false by default.
     * *brightcove_fast_forward*: Now this value is true by default.
     * *brightcove_player_options*: This is a **new** value. Boolean (default true).
     * *brightcove_marker_color*: The ads marker color changed.
2. **BrightcoveSeekBar.TV**. You can modify any inherited value. Please see [SeekBar attributes](https://developer.android.com/reference/android/widget/SeekBar.html).
3. **BorderlessButton.TV**. You can modify any inherited value. Please see [Button attributes](https://developer.android.com/reference/android/widget/Button.html).
4. **BorderlessButton.TV.Live**. The live button on the Android TV controller has it’s own style. You can modify any inherited value. Please see [Button attributes](https://developer.android.com/reference/android/widget/Button.html).
5. **BrightcovePlayerOptions**. This style is used on the LinearLayout that contains the player options, such as the captions and audio tracks (tv_player_options.xml). Their values are inherited from *BrightcovePlayerOptionsDefault*. You can modify or add more values to it. Please see [LinearLayout attributes](https://developer.android.com/reference/android/widget/LinearLayout.html).
6. **BrightcovePlayerOptionsTitle**. This style is used on the TextView for the Player Options titles, as “Captions” and “Audio Tracks”. Their values are inherited from *BrightcovePlayerOptionsTitleDefault*. You can modify or add more values to it. Please see [TextView attributes](https://developer.android.com/reference/android/widget/TextView.html).
7. **BrightcovePlayerOptionsButton**. This style is used for the different radio button items under the player options (tv_player_options_item.xml). Their values are inherited from *BrightcovePlayerOptionsTitleDefault*. You can modify or add more values to it. Please see [RadioButton attributes](https://developer.android.com/reference/android/widget/RadioButton.html).

The exceptions to the “Parent.Children” pattern are the new styles created for tv-only items, such as *BrightcovePlayerOptions*, *BrightcovePlayerOptionsTitle* and *BrightcovePlayerOptionsButton*. If you want to customize these styles while inheriting their values, you do need to specify the parent attribute. This is the same way we customized styles for the BrightcoveControlBar example.
```xml
<resources xmlns:android="http://schemas.android.com/apk/res/android">
   <style name="BrightcovePlayerOptions" parent="BrightcovePlayerOptionsDefault">
       ...
   </style>
</resources>
```
