# CircleBarButton

This is a Button with a circular bar around.

# Demo

![device-2015-12-02-211402](https://cloud.githubusercontent.com/assets/7608725/11531390/eb187b84-993f-11e5-97b3-49456d3e7b32.gif)

# How to use

## 1. build.gradle

Add dependencies in your build.gradle

## 2. layout.xml

Add your layout.xml like below.

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >
    
    <jp.gcreate.library.widget.circlebarbutton.CircleBarButton
        android:id="@+id/button"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        />
    
</RelativeLayout>
```

## 3. your code

Set degree to CircleBarButton.

```java
CircleBarButton button = (CircleBarButton) findViewById(R.id.button);
button.rewriteCircle(30f);
```

Reset bar.

```java
button.onFinishedToRestart();
```

# Customize

CircleBarButton can customize in layout.xml.

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:my="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    >

    <jp.gcreate.library.widget.circlebarbutton.CircleBarButton
        android:id="@+id/circle"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        my:debug="true"
        my:keep_aspect="true"
        my:margin="10dp"
        my:base_color="@color/grey_300"
        my:border_color="@color/colorAccent"
        my:base_width="5dp"
        my:border_width="10dp"
        my:button_text="start"
        my:button_text_color="@color/white"
        my:interpolator="@android:anim/accelerate_decelerate_interpolator"
        />

    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/button"
        android:text="reset"
        />
</LinearLayout>
```

You declared custom namespace in xml like "my" above. And then add options in your xml.


# License

Copyright 2015 G-CREATE

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.