![driller icon](url-driller.png)

[![Circle CI](https://circleci.com/gh/pubnative/advertising-id-client/tree/master.svg?style=shield)](https://circleci.com/gh/pubnative/advertising-id-client/tree/master)

# advertising-id-client


This repository contains a tool to navigate deep into an URL redirection and open it in background.

## Install

###jCenter

Add the following line to your build.gradle file

`compile "net.pubnative:advertising_id_client:1.0.0"`

###Manual

You can always download this repository and include it as a module in your project

## Usage

Create object of AdvertisingIdClient and set the listener

```java
AdvertisingIdClient advertisingIdClient = new AdvertisingIdClient();
advertisingIdClient.setListener(new AdvertisingIdClient.Listener() {
                                    
        @Override
        public void onAdvertisingIdClientStart() {
            // Callback when process starts
        }

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
Invoke the process to get advertising id
```java
advertisingIdClient.getAdvertisingId(context);
```
