package com.example.keval.keval.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keval.keval.Alarm.AlarmUtil;
import com.example.keval.keval.Floating.FloatingActionMenu;
import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;
import com.example.keval.keval.Utils.Constants;
import com.mpt.storage.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class Remainder extends AppCompatActivity implements View.OnClickListener {

    LinearLayout llDailySchedule;
    ImageView cvDailyScheduleAddSchedule, cvDailyScheduleSearchClose, cvDailyScheduleAddScheduleMenu, cvDailyScheduleSearch, ivCancel;
    EditText etSearch;
    FloatingActionMenu ivDailyScheduleMenu;
    final int requestData = 101;
    JSONArray dailyScheduleArray;
    LinearLayout llSearch;
    LinearLayout llDate;
    EditText etFromDate, etToDate;
    Calendar mCalendar = Calendar.getInstance();
    ScrollView svDailySchedule;
    LinearLayout llElementNoDataLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_schedule);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        llDailySchedule = (LinearLayout) findViewById(R.id.llDailySchedule);
        ivDailyScheduleMenu = (FloatingActionMenu) findViewById(R.id.ivDailyScheduleMenu);
        cvDailyScheduleAddSchedule = (ImageView) findViewById(R.id.cvDailyScheduleAddSchedule);
        cvDailyScheduleSearchClose = (ImageView) findViewById(R.id.cvDailyScheduleSearchClose);
        cvDailyScheduleAddScheduleMenu = (ImageView) findViewById(R.id.cvDailyScheduleAddScheduleMenu);
        cvDailyScheduleSearch = (ImageView) findViewById(R.id.cvDailyScheduleSearch);
        ivCancel = (ImageView) findViewById(R.id.ivCancel);
        llSearch = (LinearLayout) findViewById(R.id.llSearch);
        llElementNoDataLayout = (LinearLayout) findViewById(R.id.llElementNoDataLayout);
        etSearch = (EditText) findViewById(R.id.etSearch);
        llDate = (LinearLayout) findViewById(R.id.llDate);
        etFromDate = (EditText) findViewById(R.id.etFromDate);
        etToDate = (EditText) findViewById(R.id.etToDate);
        svDailySchedule = (ScrollView) findViewById(R.id.svDailySchedule);

        etSearch.setHint("Daily Schedule");
        mCalendar = Calendar.getInstance();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (etSearch.getText().toString().equalsIgnoreCase("")) {
                    ivCancel.setVisibility(View.GONE);
                    fillLinearByData(dailyScheduleArray, false);
                } else {
                    ivCancel.setVisibility(View.VISIBLE);

                    JSONArray jsonArraySearch = new JSONArray();

                    for (int i = 0; i < dailyScheduleArray.length(); i++) {
                        JSONObject jsonObject = dailyScheduleArray.optJSONObject(i);

                        if (jsonObject.optString("customerName").toUpperCase().contains(etSearch.getText().toString().toUpperCase())
                                || jsonObject.optString("dailyScheduleAmount").toUpperCase().contains(etSearch.getText().toString().trim().toUpperCase())
                                || jsonObject.optString("dailyScheduleAmountType").toUpperCase().contains(etSearch.getText().toString().trim().toUpperCase()))
                            jsonArraySearch.put(jsonObject);
                    }

                    fillLinearByData(jsonArraySearch, false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fillArray();
        fillLinearByData(dailyScheduleArray, true);

        ivDailyScheduleMenu.setClosedOnTouchOutside(true);
        cvDailyScheduleSearch.setOnClickListener(this);
        cvDailyScheduleAddScheduleMenu.setOnClickListener(this);
        cvDailyScheduleSearchClose.setOnClickListener(this);
        cvDailyScheduleAddSchedule.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
        etFromDate.setOnClickListener(this);
        etToDate.setOnClickListener(this);

    }

    public void fillArray() {
        try {
            dailyScheduleArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.DAILY_SCHEDULE, "") + "]");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteScheduleAlert(final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Delete!");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int id) {
                CommonUtils.showProgressDialog(Remainder.this, "Deleting Remainder...");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dailyScheduleArray = CommonUtils.remove(position, dailyScheduleArray);
                        SharedPreferenceUtil.putValue(Constants.DAILY_SCHEDULE, dailyScheduleArray.toString().substring(1, dailyScheduleArray.toString().length() - 1));
                        SharedPreferenceUtil.save();
                        fillLinearByData(dailyScheduleArray, true);

                        Toast.makeText(Remainder.this, "Schedule Deleted Successfully", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                        CommonUtils.cancelProgressDialog();
                    }
                }, 1000);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create();
        builder.show();
    }

    public void addToLinearLayout(final JSONObject jsonObject) {

        LayoutInflater inf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inf.inflate(R.layout.element_daily_schedule, null);

        final LinearLayout llElementDailySchedule = (LinearLayout) view.findViewById(R.id.llElementDailySchedule);
        TextView tvElementDailyScheduleName = (TextView) view.findViewById(R.id.tvElementDailyScheduleName);
        TextView tvElementRemainderDate = (TextView) view.findViewById(R.id.tvElementRemainderDate);
        TextView tvElementDailyScheduleTime = (TextView) view.findViewById(R.id.tvElementDailyScheduleTime);
        TextView tvElementDailyScheduleAmount = (TextView) view.findViewById(R.id.tvElementDailyScheduleAmount);
        ImageView ivElementRemainderEdit = (ImageView) view.findViewById(R.id.ivElementRemainderEdit);
        ImageView ivElementRemainderDelete = (ImageView) view.findViewById(R.id.ivElementRemainderDelete);
        final SwitchCompat swElementRemainderOnOff = (SwitchCompat) view.findViewById(R.id.swElementRemainderOnOff);

        tvElementDailyScheduleName.setText(jsonObject.optString("customerName"));
        tvElementDailyScheduleName.setTag(jsonObject.optString("dailyScheduleId"));
        tvElementRemainderDate.setText(jsonObject.optString("dailyScheduleDate"));
        tvElementDailyScheduleTime.setText(jsonObject.optString("dailyScheduleTime"));
        llElementDailySchedule.setTag(llDailySchedule.getChildCount() + "");

        if (TextUtils.isEmpty(jsonObject.optString("dailyScheduleAmount")))
            tvElementDailyScheduleAmount.setText(jsonObject.optString("dailyScheduleAddInfo"));
        else
            tvElementDailyScheduleAmount.setText("Amount to " + jsonObject.optString("dailyScheduleAmountType") + "     " + getResources().getString(R.string.currency_india) + " " + jsonObject.optString("dailyScheduleAmount"));

        swElementRemainderOnOff.setChecked(jsonObject.optBoolean("dailyScheduleIsActive"));

        final String setDateTime = jsonObject.optString("dailyScheduleDate") + " " + jsonObject.optString("dailyScheduleTime");
        if (CommonUtils.convertStringToDateTime(CommonUtils.dateFormat(System.currentTimeMillis()) + " " + CommonUtils.timeFormat(System.currentTimeMillis())).getTime() >= CommonUtils.convertStringToDateTime(setDateTime).getTime()) {

            try {
                for (int i = 0; i < dailyScheduleArray.length(); i++) {
                    JSONObject dailyScheduleObject = dailyScheduleArray.optJSONObject(i);

                    if (jsonObject.optString("dailyScheduleId").equalsIgnoreCase(dailyScheduleObject.optString("dailyScheduleId"))) {

                        jsonObject.put("dailyScheduleIsActive", false);

                        AlarmUtil.cancelAlarm(getApplicationContext(), CommonUtils.alarmIntent(Remainder.this), jsonObject.optInt("dailyScheduleId"));

                        SharedPreferenceUtil.putValue(Constants.DAILY_SCHEDULE, dailyScheduleArray.toString().substring(1, dailyScheduleArray.toString().length() - 1));
                        SharedPreferenceUtil.save();

                        swElementRemainderOnOff.setChecked(false);

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        llElementDailySchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Remainder.this, AddRemainder.class).putExtra("onlyShow", jsonObject.toString()));
            }
        });

        ivElementRemainderEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Remainder.this, AddRemainder.class)
                        .putExtra("editData", jsonObject.toString())
                        .putExtra("position", Integer.valueOf((String) llElementDailySchedule.getTag())), requestData);
            }
        });

        ivElementRemainderDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteScheduleAlert(Integer.valueOf((String) llElementDailySchedule.getTag()));
            }
        });

        swElementRemainderOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {

                    if (CommonUtils.convertStringToDateTime(CommonUtils.dateFormat(System.currentTimeMillis()) + " " + CommonUtils.timeFormat(System.currentTimeMillis())).getTime() < CommonUtils.convertStringToDateTime(setDateTime).getTime()) {

                        for (int i = 0; i < dailyScheduleArray.length(); i++) {
                            JSONObject dailyScheduleObject = dailyScheduleArray.optJSONObject(i);

                            if (jsonObject.optString("dailyScheduleId").equalsIgnoreCase(dailyScheduleObject.optString("dailyScheduleId")))

                                if (jsonObject.optBoolean("dailyScheduleIsActive")) {
                                    jsonObject.put("dailyScheduleIsActive", false);

                                    AlarmUtil.cancelAlarm(getApplicationContext(), CommonUtils.alarmIntent(Remainder.this), jsonObject.optInt("dailyScheduleId"));
                                    Toast.makeText(Remainder.this, "Schedule Off!", Toast.LENGTH_SHORT).show();
                                } else {
                                    jsonObject.put("dailyScheduleIsActive", true);

                                    CommonUtils.setAlarm(Remainder.this, jsonObject);
                                    Toast.makeText(Remainder.this, "Schedule On!", Toast.LENGTH_SHORT).show();
                                }
                        }

                        SharedPreferenceUtil.putValue(Constants.DAILY_SCHEDULE, dailyScheduleArray.toString().substring(1, dailyScheduleArray.toString().length() - 1));
                        SharedPreferenceUtil.save();

                    } else {
                        Toast.makeText(Remainder.this, "Date or Time has went!", Toast.LENGTH_SHORT).show();
                        swElementRemainderOnOff.setChecked(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        llDailySchedule.addView(view);
    }

    public void fillLinearByData(JSONArray jsonArray, boolean onCreate) {
        llDailySchedule.removeAllViews();
        CommonUtils.showLog(this, jsonArray);

        if (onCreate)
            if (jsonArray.length() > 0) {
                llElementNoDataLayout.setVisibility(View.GONE);
                svDailySchedule.setVisibility(View.VISIBLE);
                ivDailyScheduleMenu.setVisibility(View.VISIBLE);
                llDate.setVisibility(View.GONE);
                cvDailyScheduleAddSchedule.setVisibility(View.GONE);
            } else {
                llElementNoDataLayout.setVisibility(View.VISIBLE);
                svDailySchedule.setVisibility(View.GONE);
                ivDailyScheduleMenu.setVisibility(View.GONE);
                llDate.setVisibility(View.GONE);
                cvDailyScheduleSearchClose.setVisibility(View.GONE);
                cvDailyScheduleAddSchedule.setVisibility(View.VISIBLE);
            }

        if (jsonArray.length() > 0) {
            llElementNoDataLayout.setVisibility(View.GONE);
            svDailySchedule.setVisibility(View.VISIBLE);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject dailyScheduleObject = jsonArray.optJSONObject(i);
                addToLinearLayout(dailyScheduleObject);
            }
        } else {
            llElementNoDataLayout.setVisibility(View.VISIBLE);
            svDailySchedule.setVisibility(View.GONE);
        }
    }

    public void ReminderStatus(boolean checkStatus) {

        JSONArray arrayReminder = new JSONArray();

        if (checkStatus) {

            for (int i = 0; i < dailyScheduleArray.length(); i++) {
                JSONObject dailyScheduleObject = dailyScheduleArray.optJSONObject(i);

                if (dailyScheduleObject.optBoolean("dailyScheduleIsActive"))
                    arrayReminder.put(dailyScheduleObject);
            }
        } else {

            for (int i = 0; i < dailyScheduleArray.length(); i++) {
                JSONObject dailyScheduleObject = dailyScheduleArray.optJSONObject(i);

                if (!dailyScheduleObject.optBoolean("dailyScheduleIsActive"))
                    arrayReminder.put(dailyScheduleObject);
            }
        }
        fillLinearByData(arrayReminder, false);
    }

    public void datePicker(final EditText etDate, final boolean checkFromDate) {

        DatePickerDialog DatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker DatePicker, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                etDate.setText(CommonUtils.dateFormat(mCalendar.getTimeInMillis()));

                if (!etToDate.getText().toString().equalsIgnoreCase("")) {

                    Calendar newCalender = Calendar.getInstance();
                    newCalender.setTime(CommonUtils.convertStringToDate(etToDate.getText().toString()));

                    if (checkFromDate && mCalendar.getTimeInMillis() > newCalender.getTimeInMillis())
                        etToDate.setText(etFromDate.getText().toString());
                }

                if (CommonUtils.convertStringToDate(etFromDate.getText().toString()).getTime() <= dailyScheduleArray.optJSONObject(0).optLong("dateLongHigh"))
                    fillLinearByData(CommonUtils.searchByDate(Remainder.this, etToDate, etFromDate, dailyScheduleArray), false);

                else if (CommonUtils.convertStringToDate(etFromDate.getText().toString()).getTime() > dailyScheduleArray.optJSONObject(0).optLong("dateLongHigh"))
                    fillLinearByData(new JSONArray(), false);

            }

        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));

        DatePicker.show();
        DatePicker.setCancelable(false);

        if (!checkFromDate)
            DatePicker.getDatePicker().setMinDate(CommonUtils.convertStringToDate(etFromDate.getText().toString()).getTime());
    }

    public void optionSearch() {
        final CharSequence[] options;
        options = new CharSequence[]{"Search by Date", "Search by " + getResources().getString(R.string.customers) + " Name",
                "Search by Reminder Off", "Search by Reminder On"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose source option!");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Search by Date")) {
                    llDate.setVisibility(View.VISIBLE);
                    cvDailyScheduleSearchClose.setVisibility(View.VISIBLE);
                    ivDailyScheduleMenu.setVisibility(View.GONE);
                    dialog.dismiss();
                } else if (options[item].equals("Search by " + getResources().getString(R.string.customers) + " Name")) {
                    cvDailyScheduleSearchClose.setVisibility(View.VISIBLE);
                    ivDailyScheduleMenu.setVisibility(View.GONE);
                    llSearch.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                } else if (options[item].equals("Search by Reminder Off")) {
                    cvDailyScheduleSearchClose.setVisibility(View.VISIBLE);
                    ivDailyScheduleMenu.setVisibility(View.GONE);
                    ReminderStatus(false);
                    dialog.dismiss();
                } else if (options[item].equals("Search by Reminder On")) {
                    cvDailyScheduleSearchClose.setVisibility(View.VISIBLE);
                    ivDailyScheduleMenu.setVisibility(View.GONE);
                    ReminderStatus(true);
                    dialog.dismiss();
                }
            }
        });
        builder.show();
        builder.setCancelable(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case requestData:
                    cvDailyScheduleSearchClose.performClick();
                    fillArray();
                    fillLinearByData(dailyScheduleArray, true);
                    break;
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvDailyScheduleAddSchedule:
                ivDailyScheduleMenu.close(true);
                startActivityForResult(new Intent(this, AddRemainder.class), requestData);
                break;
            case R.id.cvDailyScheduleSearchClose:
                ivDailyScheduleMenu.close(true);
                cvDailyScheduleSearchClose.setVisibility(View.GONE);
                llDate.setVisibility(View.GONE);
                ivDailyScheduleMenu.setVisibility(View.VISIBLE);
                llSearch.setVisibility(View.GONE);
                fillArray();
                fillLinearByData(dailyScheduleArray, true);
                etToDate.setText("");
                etFromDate.setText("");
                break;
            case R.id.cvDailyScheduleAddScheduleMenu:
                ivDailyScheduleMenu.close(true);
                startActivityForResult(new Intent(this, AddRemainder.class), requestData);
                break;
            case R.id.cvDailyScheduleSearch:
                ivDailyScheduleMenu.close(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        optionSearch();
                    }
                }, 500);

                break;
            case R.id.ivCancel:
                etSearch.setText("");
                break;
            case R.id.etFromDate:
                datePicker(etFromDate, true);
                break;
            case R.id.etToDate:
                if (TextUtils.isEmpty(etFromDate.getText().toString()))
                    Toast.makeText(this, "Select From Date First", Toast.LENGTH_SHORT).show();
                else
                    datePicker(etToDate, false);
                break;
        }
    }
}