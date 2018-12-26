# EventBus

## 概述
[![](https://jitpack.io/v/Brook007/StateLayout.svg)](https://github.com/Brook007/EventBus)
[![](https://img.shields.io/badge/Platform-Android-brightgreen.svg)](https://github.com/Brook007/EventBus)
[![](https://img.shields.io/badge/API_Live-14+-brightgreen.svg)](https://github.com/Brook007/EventBus)
[![](https://img.shields.io/badge/License-Apache_2-brightgreen.svg)](https://github.com/Brook007/EventBus/blob/master/LICENSE)
[![](https://img.shields.io/badge/%E4%BD%9C%E8%80%85-Brook007-orange.svg)](https://github.com/Brook007)

不会导致内存泄露的EventBus

## 引入依赖
### Gradle方式--适合Android Studio用户
在根项目的build.gradle中添加下面的代码
```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

然后在需要使用的模块的build.gradle中添加以下代码
```groovy
dependencies {
    implementation 'com.github.Brook007:EventBus:1.0.0'
}
```


## 开源协议  LICENSE

    Copyright (c) 2016-present, Brook007 Contributors.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
