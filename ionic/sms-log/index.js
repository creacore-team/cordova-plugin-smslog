var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
import { Injectable } from '@angular/core';
import { Plugin, Cordova, IonicNativePlugin } from '@ionic-native/core';
/**
 * @name Sms Log
 * @description
 * This plugin access the sms history on a device and that can be filtered
 *
 * @usage
 * ```typescript
 * import { SmsLog } from '@ionic-native/sms-log';
 *
 *
 * constructor(private smsLog: SmsLog) { }
 *
 * ...
 *
 */
var SmsLog = (function (_super) {
    __extends(SmsLog, _super);
    function SmsLog() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    /**
     * This function return the sms logs
     * @param filters {object[]} array of object to filter the query
     * Object must respect this structure {'name':'...', 'value': '...', 'operator': '=='}
     * (see https://github.com/creacore-team/cordova-plugin-smslog for more details)
     * @return {Promise<any>}
     */
    SmsLog.prototype.getSmsLog = function (filters) { return; };
    /**
     * Check permission
     * @returns {Promise<any>}
     */
    SmsLog.prototype.hasReadPermission = function () { return; };
    /**
     * Request permission
     * @returns {Promise<any>}
     */
    SmsLog.prototype.requestReadPermission = function () { return; };
    SmsLog.decorators = [
        { type: Injectable },
    ];
    /** @nocollapse */
    SmsLog.ctorParameters = function () { return []; };
    __decorate([
        Cordova(),
        __metadata("design:type", Function),
        __metadata("design:paramtypes", [Array]),
        __metadata("design:returntype", Promise)
    ], SmsLog.prototype, "getSmsLog", null);
    __decorate([
        Cordova({
            platforms: ['Android']
        }),
        __metadata("design:type", Function),
        __metadata("design:paramtypes", []),
        __metadata("design:returntype", Promise)
    ], SmsLog.prototype, "hasReadPermission", null);
    __decorate([
        Cordova({
            platforms: ['Android']
        }),
        __metadata("design:type", Function),
        __metadata("design:paramtypes", []),
        __metadata("design:returntype", Promise)
    ], SmsLog.prototype, "requestReadPermission", null);
    SmsLog = __decorate([
        Plugin({
            pluginName: 'SmsLog',
            plugin: 'cordova-plugin-smslog',
            pluginRef: 'plugins.smsLog',
            repo: 'https://github.com/creacore-team/cordova-plugin-smslog',
            platforms: ['Android']
        })
    ], SmsLog);
    return SmsLog;
}(IonicNativePlugin));
export { SmsLog };
//# sourceMappingURL=index.js.map