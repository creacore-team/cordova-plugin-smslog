module.exports = {
  getSmsLog: function(filters, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "SmsLog", "getSmsLog", [filters]);
  }
};
  