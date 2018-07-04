# cordova-plugin-smslog

Android only
Cordova plugin to access the sms history on a device and that can be filtered

**This plugin is only compatible with Android API >= 19**

## Installation

    cordova plugin add cordova-plugin-smslog

## Methods

- getSmsLog(filters, withBody, callbackSuccess, callbackError);
  - filters(array) filters object
  - withBody(boolean) include sms body if true
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

    window.plugins.smsLog.getSmsLog(filters, true, function(data) {
         console.log(data);
    }, function() {
         // Error
    });

This will return all sms from/to the number +32477000000 since 2018-01-30

## Filter availables

- address : the address of the other party (generally phone number)
- body : content of the message
- date : the date the message was received (in milliseconds since the epoch)
- date_sent : the date the message was sent (in milliseconds since the epoch)
- read: boolean if message is read
- subscription_id : id of the sim card (useful for dual sim)
- type : type of message (see https://developer.android.com/reference/android/provider/Telephony.TextBasedSmsColumns.html#TYPE)

## Operators available
\>, >=, <, <=, ==, like

Here is an example with the operator like

    let filters = [{
        "name": "address",
        "value": "+32%",
        "operator": "like",
    }]

This will return all calls from/to the numbers beginning with '+32'

## Returned values

getSmsLog() return an array of objects with these values
(see https://developer.android.com/reference/android/provider/Telephony.TextBasedSmsColumns.html)

- ADDRESS
- DATE
- DATE_SENT
- READ
- TYPE
- THREAD_ID
- BODY (filled if withBody is true)

(Android API >= 22)
- SUBSCRIPTION_ID

An additional value, calculated by the plugin, containing the number of characters in the text message
- bodyLength

(Android API >= 21)

A contact lookup is also performed on the phone number of each log item, which adds the following values (from https://developer.android.com/reference/android/provider/ContactsContract.Data) if found
- DISPLAY_NAME
- CONTACT_ID
- PHOTO_URI
- PHOTO_THUMBNAIL_URI