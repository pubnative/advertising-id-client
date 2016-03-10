[![Circle CI](https://circleci.com/gh/pubnative/advertising-id-client/tree/master.svg?style=shield)](https://circleci.com/gh/pubnative/advertising-id-client/tree/master)

# advertising-id-client
This repository contains useful tools to retrieve android advertising id without using google play services


##Contents

* [Requirements](#requirements)
* [Install](#install)
  * [Gradle](#gradle)   
  * [Manual](#manual)
* [Usage](#usage)
* [Misc](#misc)
  * [License](#misc_license)
  * [Contributing](#misc_contributing)

<a name="requirements"></a>
## Requirements

There are no specified requirements.

<a name="install"></a>
## Install

<a name="gradle"></a>
###Gradle

Add the following line to your build.gradle file

`compile "net.pubnative:advertising_id_client:1.0.0"`

<a name="manual"></a>
###Manual

You can always download this repository and include it as a module in your project

<a name="usage"></a>
## Usage

Invoke the process to get advertising id. This must be called from UI thread.

```java
AdvertisingIdClient.getAdvertisingId(<CONTEXT>, new AdvertisingIdClient.Listener() {
                                    
    @Override
    public void onAdvertisingIdClientFinish(AdvertisingIdClient.AdInfo adInfo) {
    // Callback when process is over
    }

    @Override
    public void onAdvertisingIdClientFail(Exception exception) {
        // Callback when process fails
    }
});
```

<a name="misc"></a>
##Misc

<a name="misc_license"></a>
###License

This code is distributed under the terms and conditions of the MIT license.

<a name="misc_contributing"></a>
###Contributing

**NB!** If you fix a bug you discovered or have development ideas, feel free to make a pull request.