package com.example.keval.keval.Utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.print.PrintManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.CursorLoader;
import android.support.v4.print.PrintHelper;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.keval.keval.Activity.AddProductToInvoice;
import com.example.keval.keval.Activity.AddRemainder;
import com.example.keval.keval.Activity.Invoice;
import com.example.keval.keval.Activity.Payment;
import com.example.keval.keval.Adapter.AdapterPrintAllData;
import com.example.keval.keval.Adapter.AdapterPrintInvoice;
import com.example.keval.keval.Alarm.AlarmReceiver;
import com.example.keval.keval.Alarm.AlarmUtil;
import com.example.keval.keval.DataImage.ShareResultActivity;
import com.example.keval.keval.R;
import com.mpt.storage.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.qqtheme.framework.picker.OptionPicker;

public class CommonUtils {
    private static ProgressDialog pDialog;

    public static void showProgressDialog(Context activity, String msg) {
        pDialog = new ProgressDialog(activity);
        pDialog.setMessage(msg);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public static void cancelProgressDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.cancel();
    }

    public static void showLog(Activity activity, JSONArray jsonArray) {
        Log.i("############     " + activity.getLocalClassName(), jsonArray.toString());
    }

    public static Intent alarmIntent(Context activity) {
        return new Intent(activity, AlarmReceiver.class);
    }

    public static JSONObject setAlarm(Context activity, JSONObject jsonObject) {

        JSONObject reminderObj = new JSONObject();
        String setDes;
        String setName = jsonObject.optString("customerName");
        int setId = jsonObject.optInt("dailyScheduleId");
        String setDateTime = jsonObject.optString("dailyScheduleDate") + " " + jsonObject.optString("dailyScheduleTime");

        if (TextUtils.isEmpty(jsonObject.optString("dailyScheduleAmount")))
            setDes = jsonObject.optString("dailyScheduleAddInfo");
        else
            setDes = "Amount to " + jsonObject.optString("dailyScheduleAmountType") + " " + activity.getResources().getString(R.string.currency_india) + jsonObject.optString("dailyScheduleAmount");

        try {
            reminderObj.put("Id", setId);
            reminderObj.put("description", setDes.trim());
            reminderObj.put("title", setName.trim());

            if (convertStringToDateTime(dateFormat(System.currentTimeMillis()) + " " + timeFormat(System.currentTimeMillis())).getTime() <= convertStringToDateTime(setDateTime).getTime())
                AlarmUtil.setAlarm(activity, alarmIntent(activity), setId, reminderObj, jsonObject, setAlarmCalender(setDateTime));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reminderObj;
    }

    private static Calendar setAlarmCalender(String setDateTime) {
        Calendar mCalendar1 = Calendar.getInstance();

        try {
            mCalendar1.setTime(new SimpleDateFormat("dd-MM-yyyy hh:mm aa").parse(setDateTime));
            return mCalendar1;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mCalendar1;
    }

    public static boolean isValidEmail(CharSequence strEmail) {
        return !TextUtils.isEmpty(strEmail) && android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches();
    }

    public static File getAppPath(Activity activity) {
        return new File(Environment.getExternalStorageDirectory() + "/" + activity.getResources().getString(R.string.app_name) + "/");
//        return new File(activity.getFilesDir(), "My Account");
    }

    public static String getPathFromURI(final Context context, final Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                } else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                } else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if (Constants.FILE_TYPE_IMAGE_STR.equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if (Constants.FILE_TYPE_VIDEO_STR.equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if (Constants.FILE_TYPE_Audio_STR.equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static String dateFormat(long date) {
        return new SimpleDateFormat("dd-MM-yyyy").format(date);
    }

    public static String timeFormat(long time) {
        return new SimpleDateFormat("hh:mm aa").format(time);
    }

    private static boolean dateEqualCompare(String fromCompare, String toCompare) {

        long selectDate = convertStringToDate(fromCompare).getTime();
        long systemDate = convertStringToDate(toCompare).getTime();

        return selectDate == systemDate;
    }

    private static boolean timeCompare(String fromCompare, String toCompare) {

        long selectTime = 0;
        long systemTime = 0;

        try {
            selectTime = new SimpleDateFormat("hh:mm aa").parse(fromCompare).getTime();
            systemTime = new SimpleDateFormat("hh:mm aa").parse(toCompare).getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return selectTime >= systemTime;
    }

    public static void closeKeyboard(Activity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager.isAcceptingText())
                inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri, Boolean bBoolean) {
        String[] projectData = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, projectData, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index;
            if (bBoolean)
                column_index =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            else
                column_index =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri, Boolean bBoolean) {
        String[] proj;

        int column_index;
        Cursor cursor;
        if (bBoolean) {
            proj = new String[]{MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        } else {
            proj = new String[]{MediaStore.Video.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static String dateAndIdCombine(String dateGet, String stringId) {

        String dateSet = new SimpleDateFormat("yyyyMMdd").format(convertStringToDate(dateGet));

        if (stringId.length() == 1)
            stringId = "000" + stringId;
        else if (stringId.length() == 2)
            stringId = "00" + stringId;
        else if (stringId.length() == 3)
            stringId = "0" + stringId;

        return dateSet + stringId;
    }

    public static void fullActivity(Activity activity) {
        Window window = activity.getWindow();

        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                window.setStatusBarColor(getResources().getColor(R.color.red1));
            }
            View decorView = window.getDecorView();
//          Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;

            decorView.setSystemUiVisibility(uiOptions);

//            getSupportActionBar().hide();

        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static JSONArray remove(final int idx, final JSONArray from) {
        final List<JSONObject> objs = asList(from);
        objs.remove(idx);
        final JSONArray ja = new JSONArray();
        for (final JSONObject obj : objs) {
            ja.put(obj);
        }
        return ja;
    }

    private static List<JSONObject> asList(final JSONArray ja) {
        final int len = ja.length();
        final ArrayList<JSONObject> result = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            final JSONObject obj = ja.optJSONObject(i);
            if (obj != null) {
                result.add(obj);
            }
        }
        return result;
    }

    public static JSONArray sorting(ArrayList<String> arrayList, JSONArray jsonArray, String compareName) {
        JSONArray saveData = new JSONArray();
        ArrayList<Integer> tempArray = new ArrayList<>();

        for (int i = 0; i < arrayList.size(); i++) {

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject jsonObject = jsonArray.optJSONObject(j);

                if (!tempArray.contains(j) && arrayList.get(i).equalsIgnoreCase(jsonObject.optString(compareName))) {
                    saveData.put(jsonObject);
                    tempArray.add(j);
                    break;
                }
            }
        }
        return saveData;
    }

    public static JSONArray searchByDate(Activity activity, final EditText etToDate, final EditText etFromDate, final JSONArray mainArray) {

        if (mainArray.length() > 0)
            if (TextUtils.isEmpty(etToDate.getText().toString())) {
                if (convertStringToDate(etFromDate.getText().toString()).getTime() <= mainArray.optJSONObject(0).optLong("dateLongLow"))
                    return mainArray;
                else
                    return assignNewArray(mainArray.optJSONObject(0).optLong("dateLongHigh"), mainArray, convertStringToDate(etFromDate.getText().toString()));
            } else {
                if (convertStringToDate(etFromDate.getText().toString()).getTime() <= mainArray.optJSONObject(0).optLong("dateLongLow"))
                    return assignNewArray(convertStringToDate(etToDate.getText().toString()).getTime(), mainArray, convertStringToDate(dateFormat(mainArray.optJSONObject(0).optLong("dateLongLow"))));
                else
                    return assignNewArray(convertStringToDate(etToDate.getText().toString()).getTime(), mainArray, convertStringToDate(etFromDate.getText().toString()));
            }
        else
            return new JSONArray();
    }

    public static JSONArray searchArray(JSONArray jsonArray, EditText editText) {

        JSONArray tempArray = new JSONArray();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);

            if (jsonObject.has("billNo")) {

                for (int k = 0; k < jsonObject.optString("billNo").length(); k++)
                    if (jsonObject.optString("billNo").equalsIgnoreCase(editText.getText().toString().trim()) ||
                            jsonObject.optString("customerName").toUpperCase().contains(editText.getText().toString().trim().toUpperCase()) ||
                            jsonObject.optString("billTotal").toUpperCase().contains(editText.getText().toString().trim().toUpperCase())) {

                        tempArray.put(jsonObject);
                        break;
                    }
            } else if (jsonObject.has("depositNo")) {

                for (int l = 0; l < jsonObject.optString("depositNo").length(); l++)
                    if (jsonObject.optString("depositNo").equalsIgnoreCase(editText.getText().toString()) ||
                            jsonObject.optString("depositName").toUpperCase().contains(editText.getText().toString().trim().toUpperCase()) ||
                            jsonObject.optString("depositAmount").toUpperCase().contains(editText.getText().toString().trim().toUpperCase())) {
                        tempArray.put(jsonObject);
                        break;
                    }
            } else {

                for (int l = 0; l < jsonObject.optString("customerName").length(); l++)
                    if (jsonObject.optString("customerName").toLowerCase().contains(editText.getText().toString().toLowerCase()) ||
                            jsonObject.optString("grandTotal").toUpperCase().contains(editText.getText().toString().trim().toUpperCase())) {
                        tempArray.put(jsonObject);
                        break;
                    }
            }
        }
        return tempArray;
    }

    public static void TimePicker(final Activity activity, final String date, final EditText setTime) {

        String hour = "1";
        String minute = "1";

        try {
            hour = new SimpleDateFormat("HH").format(new SimpleDateFormat("hh:mm aa").parse(setTime.getText().toString()));
            minute = new SimpleDateFormat("mm").format(new SimpleDateFormat("hh:mm aa").parse(setTime.getText().toString()));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        TimePicker picker = new TimePicker(activity, TimePicker.HOUR, Integer.valueOf(hour), Integer.valueOf(minute));
        picker.setLabel("", "");
        picker.setTitleText("Select Time");
        picker.setTitleTextColor(activity.getResources().getColor(R.color.white));
        picker.setTopBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
        picker.setTopLineColor(activity.getResources().getColor(R.color.colorPrimary));
        picker.setCancelTextColor(activity.getResources().getColor(R.color.white));
        picker.setSubmitTextColor(activity.getResources().getColor(R.color.white));
        picker.setTopLineVisible(false);
        picker.setOnTimePickListener(new TimePicker.OnTimePickListener() {
            @Override
            public void onTimePicked(String selectedHour, String selectedMinute, String amPm) {

                String time = selectedHour + ":" + selectedMinute + " " + amPm;

                if (dateEqualCompare(date, dateFormat(System.currentTimeMillis())))
                    if (timeCompare(time, timeFormat(System.currentTimeMillis())))
                        setTime.setText(time);
                    else
                        Toast.makeText(activity, "Time has to be less than Current Time", Toast.LENGTH_SHORT).show();

                else
                    setTime.setText(time);
            }
        });
        picker.show();
    }

    public static void picker(final Activity activity, final ArrayList<String> options, String setTitle, final EditText editText, final boolean arrayWithId, final String packageName) {

        final OptionPicker picker;
        final ArrayList<String> changeArrayString = new ArrayList<>();

        if (arrayWithId) {
            for (int i = 0; i < options.size(); i++) {
                String arrayName = options.get(i).substring(options.get(i).lastIndexOf("#"));
                changeArrayString.add(options.get(i).replace(arrayName, ""));
            }
            picker = new OptionPicker(activity, changeArrayString);

        } else
            picker = new OptionPicker(activity, options);

        picker.setTextSize(15);
        picker.setTitleText(setTitle);
        picker.setTitleTextColor(activity.getResources().getColor(R.color.white));
        picker.setTopBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
        picker.setTopLineColor(activity.getResources().getColor(R.color.colorPrimary));
        picker.setCancelTextColor(activity.getResources().getColor(R.color.white));
        picker.setSubmitTextColor(activity.getResources().getColor(R.color.white));

        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(String option) {

                if (arrayWithId)
                    for (int i = 0; i < options.size(); i++) {
                        String arrayId = options.get(i).substring(options.get(i).lastIndexOf("#"));

                        if (options.get(i).equalsIgnoreCase(option + arrayId)) {
                            editText.setTag(arrayId.replace("#", "") + "");
                        }
                    }

                if (packageName.contains("AddProductToInvoice"))
                    AddProductToInvoice.checkProductSelect = false;
                else if (packageName.contains("Invoice"))
                    Invoice.checkCustomerSelected = false;
                else if (packageName.contains(activity.getResources().getString(R.string.deposit)))
                    Payment.checkCustomerSelected = false;
                else if (packageName.contains("AddRemainder"))
                    AddRemainder.checkCustomerSelected = false;

                editText.setText(option);

                if (activity.getLocalClassName().equalsIgnoreCase(packageName))
                    if (packageName.contains("AddProductToInvoice"))
                        AddProductToInvoice.setDataByProductName();
                    else if (packageName.contains(activity.getResources().getString(R.string.deposit)))
                        Payment.customerSelected = editText.getTag() + "";
                    else if (packageName.contains("AddRemainder"))
                        AddRemainder.productSelected = editText.getTag() + "";
            }
        });
        picker.show();
    }

    public static long higherDateLong(String fromWhichData, String etDate, boolean highDate) {
        long sendLong = 0;

        try {
            JSONArray dailyArray = new JSONArray("[" + SharedPreferenceUtil.getString(fromWhichData, "") + "]");
            long nowDate = convertStringToDate(etDate).getTime();

            if (dailyArray.length() > 0) {
                JSONObject dailyObject = dailyArray.optJSONObject(0);
                long oldDate;

                if (highDate) {
                    oldDate = dailyObject.optLong("dateLongHigh");
                    if (oldDate >= nowDate)
                        sendLong = oldDate;
                    else
                        sendLong = nowDate;
                } else {
                    oldDate = dailyObject.optLong("dateLongLow");
                    if (oldDate <= nowDate)
                        sendLong = oldDate;
                    else
                        sendLong = nowDate;
                }

            } else
                sendLong = nowDate;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sendLong;
    }

    private static JSONArray assignNewArray(long endLong, JSONArray mainArray, Date startLong) {
        JSONArray byDateArray = new JSONArray();
        long loop = calculateDifferenceOfDate(startLong.getTime(), endLong);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(convertStringToDate(dateFormat(endLong)));

        for (int i = 0; i <= loop; i++) {

            for (int j = 0; j < mainArray.length(); j++) {
                JSONObject jsonObject = mainArray.optJSONObject(j);

                if (jsonObject.has("billDate")) {
                    if (dateFormat(calendar1.getTimeInMillis()).equalsIgnoreCase(jsonObject.optString("billDate")))
                        byDateArray.put(jsonObject);
                } else if (jsonObject.has("depositDate")) {
                    if (dateFormat(calendar1.getTimeInMillis()).equalsIgnoreCase(jsonObject.optString("depositDate")))
                        byDateArray.put(jsonObject);
                } else if (jsonObject.has("dailyScheduleDate")) {
                    if (dateFormat(calendar1.getTimeInMillis()).equalsIgnoreCase(jsonObject.optString("dailyScheduleDate")))
                        byDateArray.put(jsonObject);
                }
            }
            calendar1.add(Calendar.DAY_OF_MONTH, -1);
        }
        return byDateArray;
    }

    private static long calculateDifferenceOfDate(long startDate, long endDate) {

        long fromDate = startDate / 86400000;
        long toDate = endDate / 86400000;

        return toDate - fromDate;
    }

    public static Date convertStringToDate(String date) {

        try {
            return new SimpleDateFormat("dd-MM-yyyy").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static Date convertStringToDateTime(String date) {

        try {
            return new SimpleDateFormat("dd-MM-yyyy hh:mm aa").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static String removeSymbols(Activity activity, String amount) {
        return amount.replace(activity.getResources().getString(R.string.currency_india), "")
                .replace("+", "")
                .replace("-", "")
                .replace("=", "").trim();
    }

    public static String getDecimal(Double x) {
        NumberFormat formatter = new DecimalFormat("#0.00");
        return formatter.format(x);
    }

    public static String indexId(String constantsString) {

        if (!TextUtils.isEmpty(SharedPreferenceUtil.getString(constantsString, "")))
            SharedPreferenceUtil.putValue(constantsString, (Integer.valueOf(SharedPreferenceUtil.getString(constantsString, "")) + 1) + "");
        else
            SharedPreferenceUtil.putValue(constantsString, "1");

        SharedPreferenceUtil.save();
        return SharedPreferenceUtil.getString(constantsString, "");

    }

    public static boolean checkCustomerExist(Activity activity, String customerName, boolean showToast, boolean editData) {

        boolean userId = false;

        if (!SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, "").equalsIgnoreCase(""))
            try {

                JSONArray shopNameArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, "") + "]");

                for (int i = shopNameArray.length() - 1; i >= 0; i--) {
                    JSONObject jsonObject = shopNameArray.optJSONObject(i);

                    if (editData && customerName.equalsIgnoreCase(jsonObject.optString("customerName"))) {
                        if (showToast)
                            Toast.makeText(activity, "Name already exist", Toast.LENGTH_SHORT).show();
                        break;
                    } else {
                        if (i == 0)
                            userId = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        else
            userId = true;
        return userId;
    }

    public static JSONObject addCustomerDetails(String customerName) {
        JSONObject objectUserData = new JSONObject();

        try {
            String customerId = CommonUtils.indexId(Constants.CUSTOMER_ITEM_ID);
            objectUserData.put("customerPhoto", "");

            objectUserData.put("shopName", "");
            objectUserData.put("customerName", customerName);
            objectUserData.put("customerId", customerId);
            objectUserData.put("shopNumber", "");
            objectUserData.put("customerEmail", "");
            objectUserData.put("customerTIN", "");
            objectUserData.put("shopAdd", "");
            objectUserData.put("customerCity", "");
            objectUserData.put("customerState", "");
            objectUserData.put("customerCityPinCode", "");
            objectUserData.put("customerCountry", "");
            objectUserData.put("otherAddressEntered", false);
            objectUserData.put("shopOtherAdd", "");
            objectUserData.put("customerOtherCity", "");
            objectUserData.put("customerOtherState", "");
            objectUserData.put("customerOtherCityPinCode", "");
            objectUserData.put("customerOtherCountry", "");
            objectUserData.put("customerGender", "Male");
            objectUserData.put("grandTotal", "0");
            objectUserData.put("shopNameThere", false);
            objectUserData.put("shopNumberThere", false);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return objectUserData;
    }

    public static String addCustomer(JSONObject objectUserData) {
        String index = objectUserData.optString("customerId");

        try {
            JSONObject addToMainData = new JSONObject();
            addToMainData.put("profile", new JSONArray("[" + objectUserData + "]"));
            addToMainData.put("data", new JSONArray());
            addToMainData.put("grandTotal", "0");

            SharedPreferenceUtil.putValue(Constants.ALL_DETAILS_BY_ID + index, addToMainData.toString());
            SharedPreferenceUtil.putValue(Constants.CUSTOMER_DATA_SAVE, addDataNewThenOld(Constants.CUSTOMER_DATA_SAVE, objectUserData.toString()));
            SharedPreferenceUtil.save();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return index;
    }

    public static void addDeposit(String activityName, String depositDate, String depositName, String customerId, String depositAmount, String invoiceNo, String depositTimeAmountStatus, String depositTimeAmount, String depositTimeAmountSymbol) {

        try {
            int index = Integer.valueOf(customerId);
            CommonUtils.dateWiseManagement(depositDate, index + "", "Payment");

            JSONArray invoiceNoArray = new JSONArray();
            invoiceNoArray.put(invoiceNo);

            JSONObject addDepositObject = new JSONObject();
            addDepositObject.put("depositDate", depositDate);
            addDepositObject.put("depositAmountInvoiceNo", invoiceNoArray);
            addDepositObject.put("customerId", customerId);
            addDepositObject.put("depositName", depositName);
            addDepositObject.put("depositTimeAmountStatus", depositTimeAmountStatus);
            addDepositObject.put("depositTimeAmount", depositTimeAmount);
            addDepositObject.put("depositTimeAmountSymbol", depositTimeAmountSymbol);
            addDepositObject.put("depositAmount", getDecimal(Double.valueOf(depositAmount)));
            addDepositObject.put("depositNo", indexId(Constants.DEPOSIT_ITEM_ID));
            addDepositObject.put("dateLongHigh", higherDateLong(Constants.DEPOSIT_DATA_SAVE, depositDate, true));
            addDepositObject.put("dateLongLow", higherDateLong(Constants.DEPOSIT_DATA_SAVE, depositDate, false));
            addDepositObject.put("dateWise", true);

            JSONObject editObject = new JSONObject(SharedPreferenceUtil.getString(Constants.ALL_DETAILS_BY_ID + index, ""));

            JSONObject addToMainData = new JSONObject();
            JSONArray allDataById = editObject.optJSONArray("data");

            JSONArray billArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.BILL_DATA_SAVE, "") + "]");
            JSONArray allTransactionArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.ALL_TRANSACTION, "") + "]");

            allDataById.put(addDepositObject);

            double grandTotal = editObject.optDouble("grandTotal") - Double.valueOf(depositAmount);

            JSONArray customerJsonArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, "") + "]");

            for (int i = 0; i < customerJsonArray.length(); i++) {
                JSONObject customerJsonObject = customerJsonArray.optJSONObject(i);

                if (customerJsonObject.optString("customerId").equalsIgnoreCase(customerId)) {
                    customerJsonObject.put("grandTotal", getDecimal(grandTotal));
                    editObject.optJSONArray("profile").optJSONObject(0).put("grandTotal", getDecimal(grandTotal));
                }
            }

            addToMainData.put("profile", editObject.optJSONArray("profile"));
            addToMainData.put("data", allDataById);
            addToMainData.put("grandTotal", getDecimal(grandTotal));

            double newDeposit = Double.valueOf(depositAmount);
            double total;

            if (SharedPreferenceUtil.getString(Constants.SAVE_DEPOSIT_BY_DATE + depositDate, "").equalsIgnoreCase(""))
                total = newDeposit;
            else
                total = newDeposit + Double.valueOf(SharedPreferenceUtil.getString(Constants.SAVE_DEPOSIT_BY_DATE + depositDate, ""));

            SharedPreferenceUtil.putValue(Constants.ALL_TRANSACTION, allTransactionArray.toString().substring(1, allTransactionArray.toString().length() - 1));
            SharedPreferenceUtil.save();

            SharedPreferenceUtil.putValue(Constants.ALL_TRANSACTION, addDataNewThenOld(Constants.ALL_TRANSACTION, addDepositObject.toString()));
            SharedPreferenceUtil.putValue(Constants.BILL_DATA_SAVE, billArray.toString().substring(1, billArray.toString().length() - 1));
            SharedPreferenceUtil.putValue(Constants.SAVE_DEPOSIT_BY_DATE + depositDate, total + "");
            SharedPreferenceUtil.putValue(Constants.CUSTOMER_DATA_SAVE, customerJsonArray.toString().substring(1, customerJsonArray.toString().length() - 1));
            SharedPreferenceUtil.putValue(Constants.ALL_DETAILS_BY_ID + index, addToMainData.toString());
            SharedPreferenceUtil.putValue(Constants.DEPOSIT_DATA_SAVE, addDataNewThenOld(Constants.DEPOSIT_DATA_SAVE, addDepositObject.toString()));
            SharedPreferenceUtil.save();

        } catch (JSONException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static void editDeposit(JSONObject jsonObject, String depositAmount) {

        try {
            Double oldAmount = jsonObject.optDouble("depositAmount");
            jsonObject.put("depositAmount", getDecimal(Double.valueOf(depositAmount)));

            String customerId = jsonObject.optString("customerId");

            JSONArray billArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.BILL_DATA_SAVE, "") + "]");
            JSONArray customerArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, "") + "]");
            JSONArray allTransactionArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.ALL_TRANSACTION, "") + "]");
            JSONObject allTransactionByIdObject = new JSONObject(SharedPreferenceUtil.getString(Constants.ALL_DETAILS_BY_ID + customerId, ""));
            JSONArray depositArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.DEPOSIT_DATA_SAVE, "") + "]");

            for (int i = 0; i < billArray.length(); i++) {
                JSONObject billObject = billArray.optJSONObject(i);

                for (int j = 0; j < billObject.optJSONArray("billAmountPaymentNo").length(); j++) {

                    if (billObject.optJSONArray("billAmountPaymentNo").optString(j).equalsIgnoreCase(jsonObject.optString("depositNo"))) {

                        billObject.put("billPaid", true);
                        billObject.put("billAddPayment", depositAmount + "");

                        if (billObject.optDouble("billAddPayment") == 0)
                            billObject.put("billPaidType", "Amount not Paid");
                        else if (billObject.optDouble("billTotal") == billObject.optDouble("billAddPayment"))
                            billObject.put("billPaidType", "Amount Paid");
                        else if (billObject.optDouble("billTotal") < billObject.optDouble("billAddPayment"))
                            billObject.put("billPaidType", "More Amount Paid");
                        else
                            billObject.put("billPaidType", "Partially Amount Paid");

                    }
                }
            }

            for (int i = 0; i < customerArray.length(); i++) {
                JSONObject customerObject = customerArray.optJSONObject(i);

                if (customerObject.optString("customerId").equalsIgnoreCase(customerId)) {
                    Double grandTotal = customerObject.optDouble("grandTotal");
                    customerObject.put("grandTotal", getDecimal(grandTotal + oldAmount - Double.valueOf(depositAmount)));
                    break;
                }
            }

            for (int i = 0; i < allTransactionArray.length(); i++) {
                JSONObject allTransactionObject = allTransactionArray.optJSONObject(i);

                if (allTransactionObject.has("depositNo")) {
                    if (allTransactionObject.optString("depositNo").equalsIgnoreCase(jsonObject.optString("depositNo"))) {
                        Double oldAmountInAllTransaction = allTransactionObject.optDouble("depositAmount");
                        allTransactionObject.put("depositAmount", getDecimal(oldAmountInAllTransaction + oldAmount - Double.valueOf(depositAmount)));
                    }
                } else {

                    for (int j = 0; j < allTransactionObject.optJSONArray("billAmountPaymentNo").length(); j++)

                        if (allTransactionObject.optJSONArray("billAmountPaymentNo").optString(j).equalsIgnoreCase(jsonObject.optString("depositNo"))) {

                            allTransactionObject.put("billPaid", true);
                            allTransactionObject.put("billAddPayment", depositAmount + "");

                            if (allTransactionObject.optDouble("billAddPayment") == 0)
                                allTransactionObject.put("billPaidType", "Amount not Paid");
                            else if (allTransactionObject.optDouble("billTotal") == allTransactionObject.optDouble("billAddPayment"))
                                allTransactionObject.put("billPaidType", "Amount Paid");
                            else if (allTransactionObject.optDouble("billTotal") < allTransactionObject.optDouble("billAddPayment"))
                                allTransactionObject.put("billPaidType", "More Amount Paid");
                            else
                                allTransactionObject.put("billPaidType", "Partially Amount Paid");
                        }
                }
            }

            JSONArray allTransactionByIdProfileArray = allTransactionByIdObject.getJSONArray("profile");
            JSONArray allTransactionByIdDataArray = allTransactionByIdObject.getJSONArray("data");

            JSONObject allTransactionByIdProfileObject = allTransactionByIdProfileArray.optJSONObject(0);
            Double grandTotal = allTransactionByIdProfileObject.optDouble("grandTotal");
            allTransactionByIdProfileObject.put("grandTotal", getDecimal(grandTotal + oldAmount - Double.valueOf(depositAmount)));

            allTransactionByIdObject.put("grandTotal", getDecimal(grandTotal + oldAmount - Double.valueOf(depositAmount)));

            for (int i = 0; i < allTransactionByIdDataArray.length(); i++) {
                JSONObject allTransactionByIdDataObject = allTransactionByIdDataArray.optJSONObject(i);

                if (allTransactionByIdDataObject.has("depositNo")) {
                    if (allTransactionByIdDataObject.optString("depositNo").equalsIgnoreCase(jsonObject.optString("depositNo")))
                        allTransactionByIdDataObject.put("depositAmount", getDecimal(Double.valueOf(depositAmount)));

                } else
                    for (int j = 0; j < allTransactionByIdDataObject.optJSONArray("billAmountPaymentNo").length(); j++)

                        if (allTransactionByIdDataObject.optJSONArray("billAmountPaymentNo").optString(j).equalsIgnoreCase(jsonObject.optString("depositNo"))) {

                            allTransactionByIdDataObject.put("billPaid", true);
                            allTransactionByIdDataObject.put("billAddPayment", depositAmount + "");

                            if (allTransactionByIdDataObject.optDouble("billAddPayment") == 0)
                                allTransactionByIdDataObject.put("billPaidType", "Amount not Paid");
                            else if (allTransactionByIdDataObject.optDouble("billTotal") == allTransactionByIdDataObject.optDouble("billAddPayment"))
                                allTransactionByIdDataObject.put("billPaidType", "Amount Paid");
                            else if (allTransactionByIdDataObject.optDouble("billTotal") < allTransactionByIdDataObject.optDouble("billAddPayment"))
                                allTransactionByIdDataObject.put("billPaidType", "More Amount Paid");
                            else
                                allTransactionByIdDataObject.put("billPaidType", "Partially Amount Paid");

                        }
            }

            for (int i = 0; i < depositArray.length(); i++) {
                JSONObject depositObject = depositArray.optJSONObject(i);

                if (depositObject.optString("depositNo").equalsIgnoreCase(jsonObject.optString("depositNo"))) {
                    Double oldAmountInPayment = depositObject.optDouble("depositAmount");
                    depositObject.put("depositAmount", oldAmountInPayment - (oldAmount - Double.valueOf(depositAmount)));
                }
            }

            double newDeposit = Double.valueOf(depositAmount);
            double total;

            if (SharedPreferenceUtil.getString(Constants.SAVE_DEPOSIT_BY_DATE + jsonObject.optString("depositDate"), "").equalsIgnoreCase(""))
                total = newDeposit;
            else
                total = newDeposit - oldAmount + Double.valueOf(SharedPreferenceUtil.getString(Constants.SAVE_DEPOSIT_BY_DATE + jsonObject.optString("depositDate"), "").trim());

            SharedPreferenceUtil.putValue(Constants.SAVE_DEPOSIT_BY_DATE + jsonObject.optString("depositDate"), total + "");
            SharedPreferenceUtil.putValue(Constants.BILL_DATA_SAVE, billArray.toString().substring(1, billArray.toString().length() - 1));
            SharedPreferenceUtil.putValue(Constants.CUSTOMER_DATA_SAVE, customerArray.toString().substring(1, customerArray.toString().length() - 1));
            SharedPreferenceUtil.putValue(Constants.ALL_TRANSACTION, allTransactionArray.toString().substring(1, allTransactionArray.toString().length() - 1));
            SharedPreferenceUtil.putValue(Constants.ALL_DETAILS_BY_ID + customerId, allTransactionByIdObject.toString());
            SharedPreferenceUtil.putValue(Constants.DEPOSIT_DATA_SAVE, depositArray.toString().substring(1, depositArray.toString().length() - 1));

            SharedPreferenceUtil.save();

        } catch (JSONException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static void dateWiseManagement(String date, String index, String fromWhere) {
        try {
            if (fromWhere.equalsIgnoreCase("Invoice")) {

                if (!SharedPreferenceUtil.getString(Constants.BILL_DATA_SAVE, "").equalsIgnoreCase("")) {
                    JSONArray arrayDeposit = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.BILL_DATA_SAVE, "") + "]");
                    String checkDate = "";

                    if (arrayDeposit.length() > 0) {

                        for (int i = 0; i < arrayDeposit.length(); i++) {
                            JSONObject objectDeposit = arrayDeposit.optJSONObject(i);

                            if (checkDate.equalsIgnoreCase(objectDeposit.optString("billDate")))
                                objectDeposit.put("dateWise", false);

                            checkDate = objectDeposit.optString("depositDate");
                        }

                        if (arrayDeposit.length() >= 1) {
                            JSONObject objectDepositLast = arrayDeposit.optJSONObject(0);

                            if (date.equalsIgnoreCase(objectDepositLast.optString("billDate"))) {
                                objectDepositLast.put("dateWise", false);
                            }
                        }

                        SharedPreferenceUtil.putValue(Constants.BILL_DATA_SAVE, arrayDeposit.toString().substring(1, arrayDeposit.toString().length() - 1));
                        SharedPreferenceUtil.save();
                    }
                }
            } else {
                if (!SharedPreferenceUtil.getString(Constants.DEPOSIT_DATA_SAVE, "").equalsIgnoreCase("")) {
                    JSONArray arrayDeposit = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.DEPOSIT_DATA_SAVE, "") + "]");

                    if (arrayDeposit.length() > 0) {

                        if (date.equalsIgnoreCase(arrayDeposit.optJSONObject(0).optString("depositDate")))
                            arrayDeposit.optJSONObject(0).put("dateWise", false);

                        SharedPreferenceUtil.putValue(Constants.DEPOSIT_DATA_SAVE, arrayDeposit.toString().substring(1, arrayDeposit.toString().length() - 1));
                        SharedPreferenceUtil.save();
                    }
                }
            }

            if (!SharedPreferenceUtil.getString(Constants.ALL_TRANSACTION, "").equalsIgnoreCase("")) {
                JSONArray arrayTransaction = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.ALL_TRANSACTION, "") + "]");

                if (arrayTransaction.length() > 0) {

                    if (date.equalsIgnoreCase
                            (arrayTransaction.optJSONObject(0).has("billDate") ?
                                    arrayTransaction.optJSONObject(0).optString("billDate") :
                                    arrayTransaction.optJSONObject(0).optString("depositDate"))) {

                        arrayTransaction.optJSONObject(0).put("dateWise", false);
                    }
                }

                SharedPreferenceUtil.putValue(Constants.ALL_TRANSACTION, arrayTransaction.toString().substring(1, arrayTransaction.toString().length() - 1));
                SharedPreferenceUtil.save();
            }

            if (!SharedPreferenceUtil.getString(Constants.ALL_DETAILS_BY_ID + index, "").equalsIgnoreCase("")) {

                JSONObject objectDetailsById = new JSONObject(SharedPreferenceUtil.getString(Constants.ALL_DETAILS_BY_ID + index, ""));
                JSONArray arrayDetailsByIdData = objectDetailsById.optJSONArray("data");

                if (arrayDetailsByIdData.length() > 0) {

                    if (date.equalsIgnoreCase
                            (arrayDetailsByIdData.optJSONObject(arrayDetailsByIdData.length() - 1).has("billDate") ?
                                    arrayDetailsByIdData.optJSONObject(arrayDetailsByIdData.length() - 1).optString("billDate") :
                                    arrayDetailsByIdData.optJSONObject(arrayDetailsByIdData.length() - 1).optString("depositDate"))) {

                        arrayDetailsByIdData.optJSONObject(arrayDetailsByIdData.length() - 1).put("dateWise", false);
                    }
                }

                SharedPreferenceUtil.putValue(Constants.ALL_DETAILS_BY_ID + index, objectDetailsById.toString());
                SharedPreferenceUtil.save();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String addDataNewThenOld(String saveName, String addData) {
        String oldData = SharedPreferenceUtil.getString(saveName, "");
        String newData;

        if (!oldData.equalsIgnoreCase(""))
            newData = addData + "," + oldData;
        else
            newData = addData;

        return newData;
    }

    public static String addDataOldThenNew(String saveName, String addData) {
        String oldData = SharedPreferenceUtil.getString(saveName, "");
        String newData;

        if (!oldData.equalsIgnoreCase(""))
            newData = oldData + "," + addData;
        else
            newData = addData;

        return newData;
    }

    public static String encryptData(String dataToEncrypt) {

        byte[] byte_arr = dataToEncrypt.getBytes();
        String image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);

        return image_str;

    }

    public static String decodeData(String dataToDecrypt) {

        byte[] image_str1 = Base64.decode(dataToDecrypt, 0);
        String convertToString = new String(image_str1);

        return convertToString;
    }

    private static String bilInfo(Activity activity, boolean onlyInvoice, String billNo, String billDate, String billTotal, JSONArray billInfo, String billPaid, String billDue) {
        StringBuilder builderData = new StringBuilder();

        if (onlyInvoice) {
            for (int i = 0; i < billInfo.length(); i++) {
                JSONObject jsonInfoObject = billInfo.optJSONObject(i);
                String itemTax = "0.00";
                String itemDiscount = "0.00";

                if (!jsonInfoObject.optString("itemTax").equalsIgnoreCase(""))
                    itemTax = jsonInfoObject.optString("itemTax");

                if (!jsonInfoObject.optString("itemDiscount").equalsIgnoreCase(""))
                    itemDiscount = jsonInfoObject.optString("itemDiscount");

                String data = jsonInfoObject.optString("itemName") + ":- " +
                        jsonInfoObject.optString("itemQuantity") + " * " +
                        jsonInfoObject.optString("itemPrice") + " = " +
                        activity.getResources().getString(R.string.currency_india) + " " +
                        jsonInfoObject.optString("itemTotal") + "\n" + "Product GST :- " +
                        itemTax + "%" + "\n" + "Product Discount :- " + itemDiscount + "%" + "\n";

                builderData.append(data).append("\n");
//            sb.append(data);
            }

            return billNo + "  " + billDate + "\n" + builderData.toString() + billTotal + "\n" + billPaid + "\n" + billDue;
        } else
            return billNo + "  " + billDate + "\n" + billTotal /*+ "\n" + billPaid + "\n" + billDue*/;
    }

    public static void shareData(Activity activity, JSONObject mainObject, String billNo) {
        Intent waIntent = new Intent(Intent.ACTION_SEND);
        waIntent.setType("text/plain");
//        waIntent.setPackage(packageName);
        waIntent.putExtra(Intent.EXTRA_TEXT, activity.getResources().getString(R.string.app_name) + "\n\n" + sendText(activity, mainObject, billNo));
        activity.startActivity(Intent.createChooser(waIntent, "Share with"));
    }

    private static String sendText(Activity activity, JSONObject mainObject, String billNo) {
        String billDate, billTotal, billNoMain, billDue = "", billAddPayment = "";
        JSONArray billArray = mainObject.optJSONArray("data");
        String sendText = "";

        for (int i = billArray.length() - 1; i >= 0; i--) {
            JSONObject billObject = billArray.optJSONObject(i);

            if (!billNo.equalsIgnoreCase("")) {

                if (billObject.has("billNo") && billObject.optString("billNo").equalsIgnoreCase(billNo)) {
                    billNoMain = activity.getResources().getString(R.string.billing) + " No. " + billObject.optString("billNo");
                    billDate = "Date:- " + billObject.optString("billDate");
                    billTotal = activity.getResources().getString(R.string.billing) + " Total:- " + activity.getResources().getString(R.string.currency_india) + " " + billObject.optDouble("billTotal");
                    billAddPayment = "Amount Paid:- " + activity.getResources().getString(R.string.currency_india) + " " + billObject.optDouble("billAddPayment");
                    billDue = activity.getResources().getString(R.string.billing) + " Due:- " + activity.getResources().getString(R.string.currency_india) + " " + getDecimal(billObject.optDouble("billLeft"));

                    sendText = bilInfo(activity, true, billNoMain, billDate, billTotal, billObject.optJSONArray("info"), billAddPayment, billDue);
                    break;
                }
            } else {

                if (billObject.has("billNo")) {
                    billNoMain = activity.getResources().getString(R.string.billing) + " No. " + billObject.optString("billNo");
                    billDate = "Date:- " + billObject.optString("billDate");
                    billTotal = activity.getResources().getString(R.string.billing) + " Total:- " + activity.getResources().getString(R.string.currency_india) + " " + billObject.optString("billTotal");
                    billAddPayment = "Amount Paid:- " + activity.getResources().getString(R.string.currency_india) + " " + billObject.optDouble("billAddPayment");
                    billDue = activity.getResources().getString(R.string.billing) + " Due:- " + activity.getResources().getString(R.string.currency_india) + " " + getDecimal(billObject.optDouble("billLeft"));

                    if (sendText.equalsIgnoreCase(""))
                        sendText = bilInfo(activity, false, billNoMain, billDate, billTotal, billObject.optJSONArray("info"), billAddPayment, billDue);
                    else
                        sendText = sendText + "\n\n" + bilInfo(activity, false, billNoMain, billDate, billTotal, billObject.optJSONArray("info"), billAddPayment, billDue);

                } else {
                    billNoMain = activity.getResources().getString(R.string.deposit) + " No. " + billObject.optString("depositNo");
                    billDate = "Date:- " + billObject.optString("depositDate");
                    billTotal = activity.getResources().getString(R.string.deposit) + " Total:- " + activity.getResources().getString(R.string.currency_india) + " " + billObject.optString("depositAmount");

                    if (sendText.equalsIgnoreCase(""))
                        sendText = bilInfo(activity, false, billNoMain, billDate, billTotal, new JSONArray(), billAddPayment, billDue);
                    else
                        sendText = sendText + "\n\n" + bilInfo(activity, false, billNoMain, billDate, billTotal, new JSONArray(), billAddPayment, billDue);

                }
                if (i == 0) {
                    if (mainObject.optInt("grandTotal") > 0)
                        billTotal = "Grand Total:- " + activity.getResources().getString(R.string.currency_india) + " " + mainObject.optString("grandTotal") + " (Credit)";
                    else if (mainObject.optInt("grandTotal") > 0)
                        billTotal = "Grand Total:- " + activity.getResources().getString(R.string.currency_india) + " " + mainObject.optString("grandTotal").replace("-", "") + " (Deposit)";
                    else
                        billTotal = "Grand Total:- " + activity.getResources().getString(R.string.currency_india) + " " + mainObject.optString("grandTotal");

                    sendText = sendText + "\n\n" + billTotal;
                }
            }
        }

        return sendText;
    }

    public static SpannableString textSpannable(int startFromBack, int endFromBack, String text) {

        SpannableString sss1 = new SpannableString(text);
        sss1.setSpan(new RelativeSizeSpan(0.7f), text.length() - startFromBack, text.length() - endFromBack, 0);
//                sss1.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 5, 0);
        return sss1;
    }

    private static void doPhotoPrint(Activity activity, Bitmap finalBitmap) {
        PrintHelper photoPrinter = new PrintHelper(activity);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        photoPrinter.printBitmap(activity.getResources().getString(R.string.app_name), finalBitmap);
    }

    public static Bitmap ScreenShot(Activity activity, int requestCode, boolean shareImage) {

        Bitmap bitmap = screenShot(activity.getWindow().getDecorView());

        if (shareImage) {
//            Bitmap finalBitmap = mergeBitmap(bitmap, textAsBitmap(activity, activity.getResources().getString(R.string.app_name), 50f), false);
            Bitmap finalBitmap = bitmap;
            Intent intent = new Intent(activity, ShareResultActivity.class);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            float aspectRatio = finalBitmap.getWidth() / (float) finalBitmap.getHeight();
            int width = 1200;
            int height = Math.round(width / aspectRatio) - 50;

            finalBitmap = Bitmap.createScaledBitmap(finalBitmap, width, height, false);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] imageUri = stream.toByteArray();

            intent.putExtra("text", activity.getResources().getString(R.string.app_name));
            intent.putExtra("picture", imageUri);
            activity.startActivityForResult(intent, requestCode);

            return finalBitmap;
        } else {
            doPhotoPrint(activity, bitmap);
            return bitmap;
        }
    }

    private static Bitmap screenShot(View view) {
        int width = view.getWidth();
        int height = view.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void doPrint(Activity activity, JSONObject mainObject, boolean allData) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
            if (allData)
                printManager.print(activity.getResources().getString(R.string.app_name), new AdapterPrintAllData(activity, mainObject), null);
            else
                printManager.print(activity.getResources().getString(R.string.app_name), new AdapterPrintInvoice(activity, mainObject), null);
        } else {
            Toast.makeText(activity, "Print module not supported", Toast.LENGTH_SHORT).show();
        }
    }


    public static String getRealPathFromURI(Uri uri, Activity a) {
        Cursor cursor = a.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri, Boolean bBoolean) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column;
        String sel;
        Cursor cursor;
        if (bBoolean) {
            column = new String[]{MediaStore.Images.Media.DATA};
            sel = MediaStore.Images.Media._ID + "=?";
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{id}, null);
        } else {
            column = new String[]{MediaStore.Video.Media.DATA};
            sel = MediaStore.Video.Media._ID + "=?";
            cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{id}, null);
        }

        // where id is equal to

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    public static String roundOff(Double x) {
        NumberFormat formatter = new DecimalFormat("#0");
        return formatter.format(x);
    }

    public static void showKeyboard(Context activityContext, final EditText editText) {

        final InputMethodManager imm = (InputMethodManager)
                activityContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (!editText.hasFocus()) {
            editText.requestFocus();
        }

        editText.post(new Runnable() {
            @Override
            public void run() {
                imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
            }
        });
    }

    public static boolean dateGreaterCompare(String greaterDate, String lessDate) {

        long greaterDateLong = convertStringToDate(greaterDate).getTime();
        long lessDateLong = convertStringToDate(lessDate).getTime();

        return greaterDateLong > lessDateLong;
    }

    public static boolean dateLessCompare(String greaterDate, String lessDate) {

        long greaterDateLong = convertStringToDate(greaterDate).getTime();
        long lessDateLong = convertStringToDate(lessDate).getTime();

        return greaterDateLong < lessDateLong;
    }

    public static boolean isLegalPassword(String pass) {

        Pattern p = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", Pattern.DOTALL);
        Matcher m = p.matcher(pass);

        return m.find();
    }

    public static boolean isSpecialChar(String pass) {

        Pattern p = Pattern.compile("[&@!#+]", Pattern.DOTALL);
        Matcher m = p.matcher(pass);

        return m.find();
    }

    public static Bitmap mergeBitmap(Bitmap screenShotImage, Bitmap textImage, boolean below) {

        int screenShotImageHeight = screenShotImage.getHeight();
        int textImageHeight = textImage.getHeight();
        int totalWidth = screenShotImage.getWidth();
        int totalHeight = screenShotImageHeight + textImageHeight;

        Bitmap comboBitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas comboImage = new Canvas(comboBitmap);

        if (below) {
            // Text Below

            comboImage.drawBitmap(screenShotImage, 0f, 0f, null);
            textImage = Bitmap.createScaledBitmap(textImage, totalWidth, textImage.getHeight(), false);
            comboImage.drawBitmap(textImage, 0f, screenShotImage.getHeight(), null);
        } else {
            // Text Above

            comboImage.drawBitmap(textImage, 0f, 0f, null);
            screenShotImage = Bitmap.createScaledBitmap(screenShotImage, totalWidth, screenShotImageHeight, false);
            comboImage.drawBitmap(screenShotImage, 0f, textImageHeight, null);
        }
        return comboBitmap;

    }

    public static Bitmap textAsBitmap(Activity a, String text, float textSize) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setColor(a.getResources().getColor(R.color.black));

        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    public static JSONArray addDepositToInvoice(JSONArray newDataFill, double addDepositToInvoice, String customerId, boolean checkCustomerId, JSONObject depositObject) {
        try {

            for (int i = 0; i < newDataFill.length(); i++) {
                JSONObject newObjectFill;

                if (!checkCustomerId)
                    newObjectFill = newDataFill.optJSONObject(i);
                else
                    newObjectFill = newDataFill.optJSONObject(newDataFill.length() - (i + 1));

                if (newObjectFill.has("billNo")
                        && addDepositToInvoice != 0
                        && checkCustomerId ? newObjectFill.optString("customerId").equalsIgnoreCase(customerId) : 1 == 1)
                    if (newObjectFill.optDouble("billLeft") > 0) {

                        double addLeft = 0;
                        double addPaid = newObjectFill.optDouble("billTotal");
                        double alreadyPaid = newObjectFill.optDouble("billAddPayment");
                        double alreadyLeft = newObjectFill.optDouble("billLeft");

                        if (addDepositToInvoice < alreadyLeft) {
                            addPaid = alreadyPaid + addDepositToInvoice;
                            addLeft = alreadyLeft - addDepositToInvoice;
                        }

                        newObjectFill.put("billAddPayment", getDecimal(addPaid));
                        newObjectFill.put("billPaid", true);
                        newObjectFill.put("billLeft", getDecimal(addLeft));

                        if (newObjectFill.optDouble("billLeft") == newObjectFill.optDouble("billTotal"))
                            newObjectFill.put("billPaidType", "Amount not Paid");
                        else if (newObjectFill.optDouble("billLeft") == 0)
                            newObjectFill.put("billPaidType", "Amount Paid");
                        else if (newObjectFill.optDouble("billLeft") < 0)
                            newObjectFill.put("billPaidType", "More Amount Paid");
                        else
                            newObjectFill.put("billPaidType", "Partially Amount Paid");

                        JSONArray invoiceNoArray = depositObject.optJSONArray("depositAmountInvoiceNo");
                        JSONArray billAmountPaymentNoArray = newObjectFill.optJSONArray("billAmountPaymentNo");

                        for (int j = 0; j < invoiceNoArray.length(); j++) {

                            if (invoiceNoArray.optString(j).equalsIgnoreCase(newObjectFill.optString("billNo"))) {
                                break;
                            } else {
                                if (j == invoiceNoArray.length() - 1) {/*☺☻♥♦♣♠•◘○*/
                                    invoiceNoArray.put(newObjectFill.optString("billNo"));
                                    depositObject.put("depositAmountInvoiceNo", invoiceNoArray);
                                }
                            }
                        }

                        for (int j = 0; j < billAmountPaymentNoArray.length(); j++) {

                            if (billAmountPaymentNoArray.optString(j).equalsIgnoreCase(depositObject.optString("depositNo"))) {
                                break;
                            } else {
                                if (j == billAmountPaymentNoArray.length() - 1) {
                                    billAmountPaymentNoArray.put(depositObject.optString("depositNo"));
                                    newObjectFill.put("billAmountPaymentNo", billAmountPaymentNoArray);
                                }
                            }
                        }

                        if (addDepositToInvoice == alreadyLeft || addDepositToInvoice < alreadyLeft)
                            break;
                        else {
                            addDepositToInvoice = addDepositToInvoice - (addPaid - alreadyPaid);

                            if (addDepositToInvoice == 0)
                                break;
                        }
                    }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newDataFill;
    }

    public static String valueAfterAnySymbol(String stringName, String symbol) {
        String firstSubString = stringName.substring(stringName.lastIndexOf(symbol));
        return symbol.replace(firstSubString, "");
    }

    public static String valueBeforeAnySymbol(String stringName, String symbol) {
        String firstSubString = stringName.substring(stringName.indexOf(symbol));
        return symbol.replace(firstSubString, "");
    }

    private void showSystemUI(View mDecorView) {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void hideSystemUI(View mDecorView) {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }
}