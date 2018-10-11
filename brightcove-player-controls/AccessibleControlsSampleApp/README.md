Accessible Controls Sample App
==============================

This is based on the Customized Controls Sample App but has changes for accessibility.


In MainActivity.java, the behaviour of the control bar is changed from its default of hiding after a three second timeout to not disappearing, if TalkBack is enabled.

```java
// If TalkBack is enabled, don't auto-hide the media controller
AccessibilityManager am = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
if (am.isTouchExplorationEnabled()) {
    BrightcoveMediaController controller = brightcoveVideoView.getBrightcoveMediaController();
    controller.setShowHideTimeout(0);
}
```

When TalkBack is enabled, a user needs to tap with two fingers to be passed through TalkBack as a single tap.

In default_activity_main.xml, an `android:contentDescription` is added to set text to be read when the view is focussed which tells the user to tap with two fingers.

```xml
<com.brightcove.player.view.BrightcoveExoPlayerVideoView
    ...
    android:contentDescription="@string/video_description"/>
```

In my_media_controller.xml, an `android:contentDescription` sets text to be read when controls are focussed. Some buttons use an icon font as which can't be read out, so the contentDescription takes precedence.

```xml
<Button
    ...
    android:text="@string/brightcove_controls_captions"
    android:contentDescription="@string/caption_controls_description"/>
<!-- Because the text is an icon font, set a contentDescription to be read instead -->
```

strings.xml contains the actual strings for the `android:contentDescription` attributes

```xml
<string name="video_description">Video. Tap with two fingers to toggle player controls</string>
```