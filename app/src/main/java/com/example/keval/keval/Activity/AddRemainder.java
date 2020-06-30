package com.example.keval.keval.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keval.keval.Alarm.AlarmUtil;
import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;
import com.example.keval.keval.Utils.Constants;
import com.mpt.storage.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class AddRemainder extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    Calendar mCalendar = null;
    ArrayList<String> options = new ArrayList<>();
    Spinner spAddDailyScheduleAccount;
    private JSONObject onlyShowObject;
    private JSONArray customerNameArray = new JSONArray();

    EditText etAddDailyScheduleDate, etAddDailyScheduleAmount, etAddDailyScheduleAddInfo, etAddDailyScheduleTime;
    TextView tvAddDailyScheduleSubmit, tvAddDailyScheduleAccount;
    ImageView ivAddDailyScheduleCustomerName;
    AutoCompleteTextView atvAddDailyScheduleCustomerName;
    View vAddDailyScheduleCustomerName;

    public static String productSelected = "";
    public static boolean checkCustomerSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_daily_schedule);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etAddDailyScheduleDate = (EditText) findViewById(R.id.etAddDailyScheduleDate);
        etAddDailyScheduleTime = (EditText) findViewById(R.id.etAddDailyScheduleTime);
        ivAddDailyScheduleCustomerName = (ImageView) findViewById(R.id.ivAddDailyScheduleCustomerName);
        etAddDailyScheduleAmount = (EditText) findViewById(R.id.etAddDailyScheduleAmount);
        etAddDailyScheduleAddInfo = (EditText) findViewById(R.id.etAddDailyScheduleAddInfo);
        tvAddDailyScheduleSubmit = (TextView) findViewById(R.id.tvAddDailyScheduleSubmit);
        tvAddDailyScheduleAccount = (TextView) findViewById(R.id.tvAddDailyScheduleAccount);
        spAddDailyScheduleAccount = (Spinner) findViewById(R.id.spAddDailyScheduleAccount);
        vAddDailyScheduleCustomerName = findViewById(R.id.vAddDailyScheduleCustomerName);
        atvAddDailyScheduleCustomerName = (AutoCompleteTextView) findViewById(R.id.atvAddDailyScheduleCustomerName);

        if (getIntent().hasExtra("onlyShow") || getIntent().hasExtra("editData")) {
            try {

                if (getIntent().hasExtra("onlyShow")) {
                    onlyShowObject = new JSONObject(getIntent().getStringExtra("onlyShow"));
                    etAddDailyScheduleDate.setEnabled(false);
                    etAddDailyScheduleTime.setEnabled(false);
                    etAddDailyScheduleAmount.setEnabled(false);
                    etAddDailyScheduleAddInfo.setEnabled(false);
                    tvAddDailyScheduleAccount.setVisibility(View.VISIBLE);
                    spAddDailyScheduleAccount.setVisibility(View.GONE);
                    tvAddDailyScheduleAccount.setText(onlyShowObject.optString("dailyScheduleAmountType"));
                    tvAddDailyScheduleSubmit.setVisibility(View.GONE);

                } else {
                    onlyShowObject = new JSONObject(getIntent().getStringExtra("editData"));
                    tvAddDailyScheduleSubmit.setText("Update Remainder");

                    if (onlyShowObject.optString("dailyScheduleAmountType").equalsIgnoreCase("Collect"))
                        spAddDailyScheduleAccount.setSelection(0);
                    else
                        spAddDailyScheduleAccount.setSelection(1);
                }

                ivAddDailyScheduleCustomerName.setVisibility(View.GONE);
                vAddDailyScheduleCustomerName.setVisibility(View.GONE);
                atvAddDailyScheduleCustomerName.setEnabled(false);

                getSupportActionBar().setTitle(onlyShowObject.optString("customerName"));
                etAddDailyScheduleDate.setText(onlyShowObject.optString("dailyScheduleDate"));
                etAddDailyScheduleTime.setText(onlyShowObject.optString("dailyScheduleTime"));
                atvAddDailyScheduleCustomerName.setText(onlyShowObject.optString("customerName"));
                atvAddDailyScheduleCustomerName.setTag(onlyShowObject.optString("customerId"));
                etAddDailyScheduleAmount.setText(onlyShowObject.optString("dailyScheduleAmount"));
                etAddDailyScheduleAmount.setTag(onlyShowObject.optString("dailyScheduleId"));
                etAddDailyScheduleAddInfo.setText(onlyShowObject.optString("dailyScheduleAddInfo"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            options = new ArrayList<>();
            checkCustomerName();

            etAddDailyScheduleDate.setText(CommonUtils.dateFormat(System.currentTimeMillis()));
            etAddDailyScheduleTime.setText(CommonUtils.timeFormat(System.currentTimeMillis()));
        }

        mCalendar = Calendar.getInstance();
        etAddDailyScheduleDate.setOnClickListener(this);
        etAddDailyScheduleTime.setOnClickListener(this);
        tvAddDailyScheduleSubmit.setOnClickListener(this);
        ivAddDailyScheduleCustomerName.setOnClickListener(this);
        atvAddDailyScheduleCustomerName.addTextChangedListener(this);

        atvAddDailyScheduleCustomerName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                setDataByProductName();
            }
        });
    }

    public void setDataByProductName() {

        for (int i = 0; i < customerNameArray.length(); i++) {
            JSONObject newJsonObject = customerNameArray.optJSONObject(i);

            if (newJsonObject.optString("customerName").trim().toUpperCase().equalsIgnoreCase(atvAddDailyScheduleCustomerName.getText().toString().toUpperCase().trim())) {
                atvAddDailyScheduleCustomerName.setTag(newJsonObject.optString("customerId"));
                productSelected = newJsonObject.optString("customerId");
                break;
            }
        }
    }

    public boolean checkItemIsExist() {

        boolean check = true;

        for (int i = 0; i < customerNameArray.length(); i++) {
            JSONObject customerNameObject = customerNameArray.optJSONObject(i);

            if (customerNameObject.optString("customerName").trim().equalsIgnoreCase(atvAddDailyScheduleCustomerName.getText().toString().trim())) {
                check = true;
                atvAddDailyScheduleCustomerName.setTag(customerNameObject.optString("customerId"));
                break;
            } else {
                check = false;
            }
        }
        return check;
    }

    public void selectedCustomerName() {

        ArrayList<String> filteredCustomerName = new ArrayList<>();
        productSelected = "";

        for (int i = 0; i < customerNameArray.length(); i++) {
            JSONObject newJsonObject = customerNameArray.optJSONObject(i);
            if (newJsonObject.optString("customerName").trim().toUpperCase().contains(atvAddDailyScheduleCustomerName.getText().toString().toUpperCase().trim()))
                filteredCustomerName.add(newJsonObject.optString("customerName"));
        }

        if (filteredCustomerName.size() > 0)
            atvAddDailyScheduleCustomerName.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredCustomerName));

    }

    public void checkCustomerName() {
        if (!SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, "").equalsIgnoreCase(""))
            try {

                customerNameArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, "") + "]");
                for (int i = 0; i < customerNameArray.length(); i++) {
                    JSONObject newJsonObject = customerNameArray.optJSONObject(i);
                    options.add(newJsonObject.optString("customerName") + "#" + newJsonObject.optString("customerId"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public void datePicker() {

        DatePickerDialog DatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker DatePicker, int year, int month, int dayOfMonth) {
                mCalendar = Calendar.getInstance();
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                etAddDailyScheduleDate.setText(CommonUtils.dateFormat(mCalendar.getTime().getTime()));
                etAddDailyScheduleTime.setText(CommonUtils.timeFormat(System.currentTimeMillis()));

            }

        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));

        DatePicker.show();
        DatePicker.setCancelable(false);
        DatePicker.getDatePicker().setMinDate(System.currentTimeMillis());
    }

    public void conditionSchedule() {
        if (TextUtils.isEmpty(atvAddDailyScheduleCustomerName.getText().toString()))
            Toast.makeText(this, "Select Customer Name", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(etAddDailyScheduleAmount.getText().toString().trim()) && TextUtils.isEmpty(etAddDailyScheduleAddInfo.getText().toString().trim()))
            Toast.makeText(this, "Enter Amount or Addition info.", Toast.LENGTH_SHORT).show();
        else if (productSelected.equalsIgnoreCase("") && !checkItemIsExist())
            Toast.makeText(this, "Customer name isn't Correct", Toast.LENGTH_SHORT).show();
        else {
            CommonUtils.closeKeyboard(this);
            if (!getIntent().hasExtra("editData") && !getIntent().hasExtra("onlyShow"))
                CommonUtils.showProgressDialog(this, "Creating Remainder...");
            else
                CommonUtils.showProgressDialog(this, "Updating Remainder...");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    createUpdateSchedule();

                }
            }, 1000);

        }
    }

    public void createUpdateSchedule() {
        try {
            JSONObject jsonObject = new JSONObject();

            if (!getIntent().hasExtra("editData") && !getIntent().hasExtra("onlyShow"))
                jsonObject.put("dailyScheduleId", CommonUtils.indexId(Constants.DAILY_SCHEDULE_ID));
            else
                jsonObject.put("dailyScheduleId", etAddDailyScheduleAmount.getTag() + "");

            jsonObject.put("dailyScheduleIsActive", true);
            jsonObject.put("dailyScheduleDate", etAddDailyScheduleDate.getText().toString().trim());
            jsonObject.put("dailyScheduleTime", etAddDailyScheduleTime.getText().toString().trim());
            jsonObject.put("customerId", atvAddDailyScheduleCustomerName.getTag() + "");
            jsonObject.put("customerName", atvAddDailyScheduleCustomerName.getText().toString().trim());
            jsonObject.put("dailyScheduleAmount", etAddDailyScheduleAmount.getText().toString().trim());
            jsonObject.put("dailyScheduleAddInfo", etAddDailyScheduleAddInfo.getText().toString().trim());
            jsonObject.put("dailyScheduleAmountType", spAddDailyScheduleAccount.getSelectedItem().toString().trim());
            jsonObject.put("dateLongHigh", CommonUtils.higherDateLong(Constants.DAILY_SCHEDULE, etAddDailyScheduleDate.getText().toString(), true));
            jsonObject.put("dateLongLow", CommonUtils.higherDateLong(Constants.DAILY_SCHEDULE, etAddDailyScheduleDate.getText().toString(), false));

            if (!getIntent().hasExtra("editData") && !getIntent().hasExtra("onlyShow")) {
                SharedPreferenceUtil.putValue(Constants.DAILY_SCHEDULE, CommonUtils.addDataNewThenOld(Constants.DAILY_SCHEDULE, jsonObject.toString()));
                SharedPreferenceUtil.save();
                Toast.makeText(this, "Remainder Created Successfully", Toast.LENGTH_SHORT).show();
            } else {
                AlarmUtil.cancelAlarm(this, CommonUtils.alarmIntent(this), onlyShowObject.optInt("dailyScheduleId"));

                JSONArray jsonArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.DAILY_SCHEDULE, "") + "]");
                jsonArray.put(getIntent().getIntExtra("position", -1), jsonObject);

                SharedPreferenceUtil.putValue(Constants.DAILY_SCHEDULE, jsonArray.toString().substring(1, jsonArray.toString().length() - 1));
                SharedPreferenceUtil.save();

                Toast.makeText(this, "Remainder Updated Successfully", Toast.LENGTH_SHORT).show();
            }

            CommonUtils.setAlarm(AddRemainder.this, jsonObject);

            setResult(Activity.RESULT_OK);
            finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (!getIntent().hasExtra("position") && checkCustomerSelected && customerNameArray.length() > 0)
            selectedCustomerName();
        checkCustomerSelected = true;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_daily_schedule, menu);

        if (getIntent().hasExtra("onlyShow"))
            menu.findItem(R.id.menuAddDailySchedule).setVisible(false);
        else if (getIntent().hasExtra("editData"))
            menu.findItem(R.id.menuAddDailySchedule).setTitle("Update Remainder");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menuAddDailySchedule:
                conditionSchedule();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.etAddDailyScheduleDate:
                datePicker();
                break;
            case R.id.ivAddDailyScheduleCustomerName:
                if (options.size() > 0)
                    CommonUtils.picker(this, options, "Customer Name", atvAddDailyScheduleCustomerName, true, this.getLocalClassName());
                else
                    Toast.makeText(this, "No Customer Available", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tvAddDailyScheduleSubmit:
                conditionSchedule();
                break;
            case R.id.etAddDailyScheduleTime:
                CommonUtils.TimePicker(this, etAddDailyScheduleDate.getText().toString(), etAddDailyScheduleTime);
                break;
        }
    }
}