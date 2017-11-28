#!/bin/bash

# ...
ant clean && ant build && ant install && adb shell uiautomator runtest OnceUxUiTest.jar -e because broken
