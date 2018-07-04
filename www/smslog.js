module.exports = {
  getSmsLog: function(filters, withBody, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "SmsLog", "getSmsLog", [filters, withBody]);
  }
};
  