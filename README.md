![Logo of the project](./images/logo.png)

# Musharing-Android &middot; [![Build Status](https://img.shields.io/travis/npm/npm/latest.svg?style=flat-square)](https://travis-ci.org/npm/npm) [![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com) [![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg?style=flat-square)](https://github.com/your/your-project/blob/master/LICENSE)

> A New Way of Music Sharing in the Internet Era

**Musharing** is a *Android* app for immediate sharing music via Internet with your friends. It offers your the possibility of simulcasting a song in different phones no metter how far apart they are.  More attractive, with musharing, you can chat with your friends in the background of real synchronized music playing.

Wondering more about **musharing** ? [Tap Here(Simplified Chinese)](./musharing.md).

> **This repositories is the *Android app of Musharing*, if you are seek the Back-End of Musharing please [click here]()**

## Getting started

Download Musharing apk [here](./relase) for test.

## Developing

The development of Android platform has realized [a primitive Demo](./relase/musharing-v0.0.0-demo.apk) that can display some functions of Musharing.

[Click here](./TODO.md) to peek what we are going to do as well as what we have already done.

### Prerequisites

Please ensure your  `Musharing/app/build.gradle` looking like this:

```gradle
apply plugin: 'com.android.application'

android {
    compileSdkVersion 28
    defaultConfig {
        applicationId "com.mine.musharing"
        minSdkVersion 22
        targetSdkVersion 28
        versionCode 1
        versionName "0.0.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility 1.8
        targetCompatibility 1.8
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support:design:28.0.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    implementation 'de.hdodenhof:circleimageview:3.0.0'
    implementation 'com.android.support:support-v4:28.0.0'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
    implementation "com.squareup.okhttp3:okhttp:3.14.2"
    implementation 'com.android.support:recyclerview-v7:28.0.0'
    implementation 'com.android.support:cardview-v7:28.0.0'
    implementation 'com.github.bumptech.glide:glide:4.9.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.9.0'
    implementation 'com.google.code.gson:gson:2.8.5'
}

```

## Licensing

MIT License

Copyright (c) 2019 Musharing Team

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