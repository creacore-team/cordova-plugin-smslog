# cordova-plugin-smslog

Android only
Cordova plugin to access the sms history on a device and that can be filtered

**This plugin is only compatible with Android API >= 19**

## Installation

    cordova plugin add cordova-plugin-smslog

## Methods

- getSmsLog(filters, callbackSuccess, callbackError);
- hasReadPermission(successCallback, errorCallback);
- requestReadPermission(successCallback, errorCallback);

## Usage

First of all you must check / request permissions with

    window.plugins.smsLog.hasReadPermission(...,...)
    window.plugins.smsLog.requestReadPermission(...,...)

Then you can use the main function getsmsLog(), here is an example:

    let filters = [{
        "name": "address",
        "value": "+32477000000",
        "operator": "==",
    },
    {
        "name": "date",
        "value": 1517266800000,
        "operator": ">="
    }];

    window.plugins.smsLog.getSmsLog(filters, function(data) {
         console.log(data);
    }, function() {
         // Error
    });

This will return all sms from/to the number +32477000000 since 2018-01-30

## Filter availables

- date : date in milliseconds since the epoch
- address : the address of the other party (generally phone number)
- type : type of message (see https://developer.android.com/reference/android/provider/Telephony.TextBasedSmsColumns.html#TYPE)
- subscription_id : id of the sim card (useful for dual sim)

## Returned values

getSmsLog() return an array of objects with these values
(see https://developer.android.com/reference/android/provider/Telephony.TextBasedSmsColumns.html)

- ADDRESS
- DATE
- READ
- TYPE

(Android API >= 22)
- SUBSCRIPTION_ID
