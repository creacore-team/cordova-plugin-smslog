package be.creacore.smslog;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.Telephony;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmsLog extends CordovaPlugin {
    private static final String GET_SMS_LOG = "getSmsLog";
    private static final String HAS_READ_PERMISSION = "hasReadPermission";
    private static final String REQUEST_READ_PERMISSION = "requestReadPermission";

    private CallbackContext callback;
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        callback = callbackContext;

        if(GET_SMS_LOG.equals(action)) {
            // filterNumbers Arg
            List<Filter> filters = new ArrayList<Filter>();
            if(!args.isNull(0)) {
                JSONArray tmpFilter = args.getJSONArray(0);
                if (tmpFilter.length() > 0) {
                    for (int i = 0; i < tmpFilter.length(); i++) {
                        Filter filter = new Filter();
                        JSONObject filterObject = tmpFilter.getJSONObject(i);
                        try {
                            filter.setName(filterObject.getString("name"));
                        } catch (Exception e) {
                            callback.error(e.getMessage());
                        }
                        filter.setValue(filterObject.getString("value"));
                        filter.setOperator(filterObject.getString("operator"));
                        filters.add(filter);
                    }
                }
            }

            getSmsLog(filters);
            return true;
        } else if(HAS_READ_PERMISSION.equals(action)) {
            hasReadPermission();
            return true;
        } else if(REQUEST_READ_PERMISSION.equals(action)) {
            requestReadPermission();
            return true;
        } else {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void getSmsLog(List<Filter> filters)
    {
        if(smsLogPermissionGranted(Manifest.permission.READ_SMS) && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            List<String> fields = new ArrayList<String>();
            String[] fields_names = new String[]{
                Telephony.TextBasedSmsColumns.ADDRESS,
                Telephony.TextBasedSmsColumns.BODY,
                Telephony.TextBasedSmsColumns.DATE,
                Telephony.TextBasedSmsColumns.READ,
                Telephony.TextBasedSmsColumns.TYPE,
                Telephony.TextBasedSmsColumns.THREAD_ID
            };
            Collections.addAll(fields, fields_names);

            // Detect specifics version
            if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                fields.add(Telephony.TextBasedSmsColumns.SUBSCRIPTION_ID);
            }

            List<String> mSelectionArgs = new ArrayList<String>();
            String mSelectionClause = null;

            // Filters parameter
            if(filters.size() > 0) {
                for(Filter f: filters) {
                    if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1 &&
                            f.getName() == Telephony.TextBasedSmsColumns.SUBSCRIPTION_ID) {
                        continue;
                    }

                    mSelectionClause = Utils.appendFilterToClause(f, mSelectionClause);
                    mSelectionArgs.add(f.getValue());
                }
            }

            try {
                ContentResolver contentResolver = cordova.getActivity().getContentResolver();
                Cursor mCursor = contentResolver.query(Telephony.Sms.CONTENT_URI,
                    fields.toArray(new String[0]),
                    mSelectionClause,
                    mSelectionArgs.toArray(new String[0]),
                    Telephony.Sms.DEFAULT_SORT_ORDER
                );

                JSONArray result = new JSONArray();

                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        JSONObject smsLogItem = new JSONObject();
                        smsLogItem.put("address", mCursor.getString(0));
                        smsLogItem.put("bodyLength", mCursor.getString(1).length());
                        smsLogItem.put("date", mCursor.getString(2));
                        smsLogItem.put("read", mCursor.getString(3));
                        smsLogItem.put("type", mCursor.getString(4));
                        smsLogItem.put("threadId", mCursor.getString(5));

                        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                            smsLogItem.put("subscriptionId", mCursor.getString(6));
                        }

                        result.put(smsLogItem);
                    }
                }
                callback.success(result);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void hasReadPermission() {
        this.callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, smsLogPermissionGranted(Manifest.permission.READ_SMS)));
    }

    private void requestReadPermission() {
        requestPermission(Manifest.permission.READ_SMS);
    }

    private boolean smsLogPermissionGranted(String type) {
        return cordova.hasPermission(type);
    }

    private void requestPermission(String type) {
        if (!smsLogPermissionGranted(type)) {
            cordova.requestPermission(this, 12345, type);
        } else {
            this.callback.success();
        }
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException
    {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            this.callback.success();
        } else {
            this.callback.error("Permission denied");
        }
    }
}
