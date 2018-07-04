package be.creacore.smslog;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

public class SmsLog extends CordovaPlugin {
    private static final String GET_SMS_LOG = "getSmsLog";
    private static final String HAS_READ_PERMISSION = "hasReadPermission";
    private static final String REQUEST_READ_PERMISSION = "requestReadPermission";

    private static final String[] CONTACT_PROJECTION =
    {
        ContactsContract.Data._ID,
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                ContactsContract.Data.DISPLAY_NAME_PRIMARY :
                ContactsContract.Data.DISPLAY_NAME,
        ContactsContract.Data.CONTACT_ID,
        ContactsContract.Data.PHOTO_URI,
        ContactsContract.Data.PHOTO_THUMBNAIL_URI
    };

    private static final String CONTACT_SELECTION = ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER + " LIKE ? ";

    private boolean withBody = false;
    private CallbackContext callback;
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        callback = callbackContext;

        if(GET_SMS_LOG.equals(action)) {
            // filterNumbers Arg
            ArrayList<ArrayList<Filter>> filters = new ArrayList<ArrayList<Filter>>();

            if(!args.isNull(1)) {
                withBody = args.getBoolean(1);
            }

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
                        filter.setOperator(filterObject.getString("operator"));

                        // Is an array of values ?
                        try {
                            JSONArray values = filterObject.getJSONArray("value");
                            if (values != null) {
                                ArrayList<Filter> subFilters = new ArrayList<Filter>();
                                for (int j = 0; j < values.length(); j++) {
                                    Filter f = new Filter();
                                    try {
                                        f.setName(filter.getName());
                                    } catch (Exception e) {
                                        callback.error(e.getMessage());
                                    }
                                    f.setOperator(filter.getOperator());
                                    f.setValue(values.getString(j));
                                    f.setOperation("OR");
                                    subFilters.add(f);
                                }
                                filters.add(subFilters);
                            }
                        }
                        // Single value
                        catch(JSONException e) {
                            ArrayList<Filter> subFilters = new ArrayList<Filter>();
                            filter.setValue(filterObject.getString("value"));
                            subFilters.add(filter);
                            filters.add(subFilters);
                        }
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
    private void getSmsLog(ArrayList<ArrayList<Filter>> filters)
    {
        if(smsLogPermissionGranted(Manifest.permission.READ_SMS) && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            List<String> fields = new ArrayList<String>();
            Hashtable<String, Cursor> contacts = new Hashtable<String, Cursor>();
            String[] fields_names = new String[]{
                    Telephony.TextBasedSmsColumns.ADDRESS,
                    Telephony.TextBasedSmsColumns.BODY,
                    Telephony.TextBasedSmsColumns.DATE,
                    Telephony.TextBasedSmsColumns.DATE_SENT,
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
                for(ArrayList<Filter> subfilters: filters) {
                    String mSelectionSubClause = null;
                    for(Filter f: subfilters) {
                        // Detect specific version
                        if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1 &&
                                f.getName() == Telephony.TextBasedSmsColumns.SUBSCRIPTION_ID) {
                            continue;
                        }

                        mSelectionSubClause = Utils.appendFilterToClause(f, mSelectionSubClause);
                        mSelectionArgs.add(f.getValue());
                    }

                    if(mSelectionClause == null)
                    {
                        mSelectionClause = '(' + mSelectionSubClause + ')';
                    }
                    else
                    {
                        mSelectionClause += " AND (" + mSelectionSubClause + ')';
                    }
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

                if (mCursor != null)
                {
                    Context context = this.cordova.getActivity();
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
                    String countryCode = tm.getSimCountryIso().toUpperCase();

                    while (mCursor.moveToNext()) {
                        JSONObject smsLogItem = new JSONObject();
                        smsLogItem.put("address", mCursor.getString(0));

                        if(withBody)
                        {
                            smsLogItem.put("body", mCursor.getString(1));
                        }
                        else
                        {
                            smsLogItem.put("body", "");
                        }
                        smsLogItem.put("bodyLength", mCursor.getString(1).length());
                        smsLogItem.put("date", mCursor.getLong(2));
                        smsLogItem.put("date_sent", mCursor.getLong(3));
                        smsLogItem.put("read", mCursor.getString(4));
                        smsLogItem.put("type", mCursor.getInt(5));
                        smsLogItem.put("threadId", mCursor.getString(6));

                        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                            smsLogItem.put("subscriptionId", mCursor.getString(7));
                        }

                        // Fill in contact name
                        smsLogItem.put("name", mCursor.getString(0));
                        smsLogItem.put("contact", "");
                        smsLogItem.put("photo", "");
                        smsLogItem.put("thumbPhoto", "");
                        Cursor mContactCursor;
                        if(contacts.containsKey(mCursor.getString(0)))
                        {
                            mContactCursor = contacts.get(mCursor.getString(0));
                        }
                        else
                        {
                            String number = mCursor.getString(0);
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && !countryCode.isEmpty()) {
                                number = PhoneNumberUtils.formatNumberToE164(number, countryCode);
                                number = PhoneNumberUtils.normalizeNumber(number);
                            }

                            String[] mSelectionArgsContact = { "%" + number + "%" };
                            mContactCursor = contentResolver.query(
                                    ContactsContract.Data.CONTENT_URI,
                                    CONTACT_PROJECTION,
                                    CONTACT_SELECTION,
                                    mSelectionArgsContact,
                                    null
                            );
                            contacts.put(mCursor.getString(0), mContactCursor);
                        }
                        if (mContactCursor.moveToFirst()) {
                            if(!mContactCursor.isAfterLast()) {
                                smsLogItem.put("name", mContactCursor.getString(1));
                                smsLogItem.put("contact", mContactCursor.getInt(2));
                                smsLogItem.put("photo", mContactCursor.getString(3));
                                smsLogItem.put("thumbPhoto", mContactCursor.getString(4));
                            }
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
