# StickySwitch

[![License](http://img.shields.io/badge/license-MIT-green.svg?style=flat)]()
[![API](https://img.shields.io/badge/API-15%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=15)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-StickySwitch-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5433)
[![](https://jitpack.io/v/GwonHyeok/StickySwitch.svg)](https://jitpack.io/#GwonHyeok/StickySwitch)

StickySwitch library for android

this library is beautiful switch widget with sticky animation

![Image of Preview](./preview.gif)

## Requirements
- Android SDK 15+

## Usage

Add it in your root build.gradle at the end of repositories
```Groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency
```Groovy
dependencies {
    compile 'com.github.GwonHyeok:StickySwitch:0.0.11'
}
```

## How to use this library
Add `StickySwitch` to your xml layout
```xml
<io.ghyeok.stickyswitch.widget.StickySwitch
        android:id="@+id/sticky_switch"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"
        app:ss_animationDuration="600"
        app:ss_iconPadding="18dp"
        app:ss_iconSize="22dp"
        app:ss_leftIcon="@drawable/ic_male"
        app:ss_leftText="Male"
        app:ss_rightIcon="@drawable/ic_female"
        app:ss_rightText="Female"
        app:ss_selectedTextSize="14sp"
        app:ss_sliderBackgroundColor="@color/colorSliderBackground"
        app:ss_switchColor="@color/colorSwitchColor"
        app:ss_textColor="@color/colorTextColor"
        app:ss_textSize="12sp" 
        app:ss_animationType="line"/>
```

## Available attributes

|            Name           |    Type   |  Description  |
|:-------------------------:|:---------:|:-------------:|
|       ss_iconPadding      | dimension |       -       |
|        ss_iconSize        | dimension |       -       |
|        ss_leftIcon        |  integer  |       -       |
|        ss_leftText        |   string  |       -       |
|        ss_rightIcon       |  integer  |       -       |
|        ss_rightText       |   string  |       -       |
|    ss_selectedTextSize    | dimension |       -       |
|        ss_textSize        | dimension |       -       |
|       ss_switchColor      |   color   |       -       |
|  ss_sliderBackgroundColor |   color   |       -       |
|        ss_textColor       |   color   |       -       |
|    ss_animationDuration   |  integer  |       -       |
|      ss_textVisibility    |    enum   |       -       |
|    ss_animationType       |   enum    | line or curved|


## Status change notification
StickySwitch.Direction has two value (LEFT, RIGHT)

onSelectedChange method called when you touch the switch.

When the switch is moved to the right, the direction variable is `StickySwitch.Direction.RIGHT`

also switch is moved to the left, that variable is `StickySwitch.Direction.LEFT`

Java Code Example
```java
// Set Selected Change Listener
StickySwitch stickySwitch = (StickySwitch) findViewById(R.id.sticky_switch);
stickySwitch.setOnSelectedChangeListener(new StickySwitch.OnSelectedChangeListener() {
    @Override
    public void onSelectedChange(@NotNull StickySwitch.Direction direction, @NotNull String text) {
        Log.d(TAG, "Now Selected : " + direction.name() + ", Current Text : " + text);
    }
});
```

## Direction
### if you want switch button direction to left
```java
stickySwitch.setDirection(StickySwitch.Direction.LEFT);
```

### if you want switch button direction to right without animation
```java
stickySwitch.setDirection(StickySwitch.Direction.RIGHT, false);
```

### Get current Direction
```java
stickySwitch.getDirection(); // StickySwitch.Direction.LEFT
```

## Text
### get current status text
```java
stickySwitch.getText()
```

### get specific status text
```java
stickySwitch.getText(StickySwitch.Direction.LEFT) // Get leftText
```

### set left or right text
```java
stickySwitch.setLeftText("Left");
stickySwitch.setRightText("Right");
```

### set text typeface
```java
stickySwitch.setTypeFace(Typeface.DEFAULT_BOLD);
```

### text visibility
```java
stickySwitch.setTextVisibility(StickySwitch.TextVisibility.VISIBLE); // Visible Text
stickySwitch.setTextVisibility(StickySwitch.TextVisibility.INVISIBLE); // Invisible Text
stickySwitch.setTextVisibility(StickySwitch.TextVisibility.GONE); // GONE Text
```

## Icon
### set left icon with drawableRes
```java
stickySwitch.setLeftIcon(R.mipmap.ic_launcher);
```

### set left icon with drawable
```java
Drawable drawable = getDrawable(R.mipmap.ic_launcher);
stickySwitch.setLeftIcon(drawable);
```

## Color
### set colors
```java
// The color format must be (0xAARRGGBB)
stickySwitch.setSliderBackgroundColor(0xFF181821);
stickySwitch.setSwitchColor(0xFF2371FA);
stickySwitch.setTextColor(0xFFFFFFFF);
```

## Animation
### custom duration
```java
// Animation duration to 1000ms (default duration is 600ms)
stickySwitch.setAnimationDuration(1000);
```

### set different animation type
```java
// Set animation type to curved (default is line)
stickySwitch.setAnimationType(StickySwitch.AnimationType.CURVED);
```

# Reference
#### Talos Onboarding
   - [dribbble](https://dribbble.com/shots/3047204-Talos-Onboarding)
   - [material-uplabs](https://material.uplabs.com/posts/talos-onboarding)

# License
```
MIT License

Copyright (c) 2017 GwonHyeok

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
