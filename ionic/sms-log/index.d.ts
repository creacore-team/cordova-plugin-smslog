import { IonicNativePlugin } from '@ionic-native/core';
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
export declare class SmsLog extends IonicNativePlugin {
    /**
     * This function return the sms logs
     * @param filters {object[]} array of object to filter the query
     * Object must respect this structure {'name':'...', 'value': '...', 'operator': '=='}
     * (see https://github.com/creacore-team/cordova-plugin-smslog for more details)
     * @return {Promise<any>}
     */
    getSmsLog(filters: object[]): Promise<any>;
    /**
     * Check permission
     * @returns {Promise<any>}
     */
    hasReadPermission(): Promise<any>;
    /**
     * Request permission
     * @returns {Promise<any>}
     */
    requestReadPermission(): Promise<any>;
}
