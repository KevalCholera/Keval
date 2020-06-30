package com.example.keval.keval.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keval.keval.Adapter.AdapterCustomerAccount;
import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;
import com.example.keval.keval.Utils.Constants;
import com.mpt.storage.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class CustomerAccount extends AppCompatActivity implements View.OnClickListener {

    String checkDate = "";
    TextView tvCustomerAccountTotal, tvCustomerAccountTotalStatus;
    private JSONObject customerData;
    ImageView cvCustomerAccountSearch, cvCustomerAccountSearchClose;
    LinearLayout llDate, llElementNoDataLayout;
    EditText etFromDate, etToDate;
    LinearLayout llSearch;
    EditText etSearch;
    ImageView ivCancel;
    Calendar mCalendar = Calendar.getInstance();
    JSONArray mainArray = new JSONArray();
    final int requestSendForName = 101;
    final int requestSendForData = 102;
    ListView lvCustomerAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkDate = "";
        tvCustomerAccountTotal = (TextView) findViewById(R.id.tvCustomerAccountTotal);
        tvCustomerAccountTotalStatus = (TextView) findViewById(R.id.tvCustomerAccountTotalStatus);
        cvCustomerAccountSearch = (ImageView) findViewById(R.id.cvCustomerAccountSearch);
        cvCustomerAccountSearchClose = (ImageView) findViewById(R.id.cvCustomerAccountSearchClose);
        llDate = (LinearLayout) findViewById(R.id.llDate);
        llElementNoDataLayout = (LinearLayout) findViewById(R.id.llElementNoDataLayout);
        etFromDate = (EditText) findViewById(R.id.etFromDate);
        etToDate = (EditText) findViewById(R.id.etToDate);
        llSearch = (LinearLayout) findViewById(R.id.llSearch);
        etSearch = (EditText) findViewById(R.id.etSearch);
        ivCancel = (ImageView) findViewById(R.id.ivCancel);
        lvCustomerAccount = (ListView) findViewById(R.id.lvCustomerAccount);

        etSearch.setHint("Customer Account");
        mCalendar = Calendar.getInstance();

        fillData();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s != null && s.length() > 0 && mainArray.length() > 0) {
                    ivCancel.setVisibility(View.VISIBLE);
                    JSONArray newArray = new JSONArray();

                    for (int i = 0; i < mainArray.length(); i++) {
                        JSONObject jsonObject = mainArray.optJSONObject(i);

                        if (jsonObject.has("billNo") && jsonObject.optString("billNo").equalsIgnoreCase(etSearch.getText().toString()))
                            newArray.put(jsonObject);
                        else if (jsonObject.optString("depositNo").equalsIgnoreCase(etSearch.getText().toString()))
                            newArray.put(jsonObject);

                    }
                    fillToLayout(newArray);

                } else {
                    ivCancel.setVisibility(View.GONE);
                    fillToLayout(mainArray);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cvCustomerAccountSearch.setOnClickListener(this);
        cvCustomerAccountSearchClose.setOnClickListener(this);
        etFromDate.setOnClickListener(this);
        etToDate.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
    }

    public void fillData() {
        try {

            String customerPosition = getIntent().getStringExtra("customerPosition");

            customerData = new JSONObject(SharedPreferenceUtil.getString(Constants.ALL_DETAILS_BY_ID + customerPosition, ""));

            JSONArray jsonArray = customerData.optJSONArray("data");
            getSupportActionBar().setTitle(customerData.optJSONArray("profile").optJSONObject(0).optString("customerName"));

            if (jsonArray.length() == 0) {
                llDate.setVisibility(View.GONE);
                cvCustomerAccountSearch.setVisibility(View.GONE);
                cvCustomerAccountSearchClose.setVisibility(View.GONE);
                lvCustomerAccount.setVisibility(View.GONE);
                llElementNoDataLayout.setVisibility(View.VISIBLE);
            } else {
                lvCustomerAccount.setVisibility(View.VISIBLE);
                llElementNoDataLayout.setVisibility(View.GONE);
                llDate.setVisibility(View.GONE);
                cvCustomerAccountSearch.setVisibility(View.VISIBLE);
                cvCustomerAccountSearchClose.setVisibility(View.GONE);

                if (jsonArray.length() > 0) {
                    mainArray = new JSONArray();

                    for (int i = jsonArray.length() - 1; i >= 0; i--)
                        mainArray.put(jsonArray.optJSONObject(i));
                }

                fillToLayout(mainArray);
            }

            double total = customerData.optJSONArray("profile").optJSONObject(0).optDouble("grandTotal");
            tvCustomerAccountTotal.setText(CommonUtils.removeSymbols(this, CommonUtils.getDecimal(total)));

            if (total < 0)
                tvCustomerAccountTotalStatus.setText("Dr.");
            else if (total > 0)
                tvCustomerAccountTotalStatus.setText("Dr.");
            else
                tvCustomerAccountTotalStatus.setText("");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void fillToLayout(JSONArray fillData) {
        CommonUtils.showLog(this, fillData);

        if (fillData.length() > 0) {
            lvCustomerAccount.setVisibility(View.VISIBLE);
            llElementNoDataLayout.setVisibility(View.GONE);
            fillDataByArray(fillData);
        } else {
            lvCustomerAccount.setVisibility(View.GONE);
            llElementNoDataLayout.setVisibility(View.VISIBLE);
        }
    }

    public void fillDataByArray(JSONArray fillAJsonArray) {

        if (fillAJsonArray.length() > 0) {
            lvCustomerAccount.setVisibility(View.VISIBLE);
            llElementNoDataLayout.setVisibility(View.GONE);
            lvCustomerAccount.setAdapter(new AdapterCustomerAccount(this, fillAJsonArray));
        } else {
            lvCustomerAccount.setVisibility(View.GONE);
            llElementNoDataLayout.setVisibility(View.VISIBLE);
        }
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

                if (CommonUtils.convertStringToDate(etFromDate.getText().toString()).getTime() <= mainArray.optJSONObject(0).optLong("dateLongHigh"))
                    fillToLayout(CommonUtils.searchByDate(CustomerAccount.this, etToDate, etFromDate, mainArray));
                else if (CommonUtils.convertStringToDate(etFromDate.getText().toString()).getTime() > mainArray.optJSONObject(0).optLong("dateLongHigh"))
                    fillToLayout(new JSONArray());

            }

        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));

        DatePicker.show();
        DatePicker.setCancelable(false);
        DatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());

        if (!checkFromDate)
            DatePicker.getDatePicker().setMinDate(CommonUtils.convertStringToDate(etFromDate.getText().toString()).getTime());
    }

    public void optionSearch() {
        final CharSequence[] options;
        options = new CharSequence[]{"Search by Date", "Search by Customer Account"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose source option!");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Search by Date")) {
                    llDate.setVisibility(View.VISIBLE);
                    cvCustomerAccountSearch.setVisibility(View.GONE);
                    cvCustomerAccountSearchClose.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                } else if (options[item].equals("Search by Customer Account")) {
                    cvCustomerAccountSearch.setVisibility(View.GONE);
                    cvCustomerAccountSearchClose.setVisibility(View.VISIBLE);
                    llSearch.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
            }
        });
        builder.show();
        builder.setCancelable(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {

                case requestSendForName:
                    getSupportActionBar().setTitle(data.getStringExtra("customerName"));
                    break;
                case requestSendForData:
                    fillData();
                    break;

            }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        CommonUtils.closeKeyboard(this);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer_profile, menu);

        if (getIntent().hasExtra("fragment"))
            menu.findItem(R.id.editProfile).setVisible(false);

        if (getIntent().hasExtra("invoice") || getIntent().hasExtra("deposit")) {
            menu.findItem(R.id.editProfile).setVisible(false);
            menu.findItem(R.id.menuCustomerProfileInvoice).setVisible(false);
            menu.findItem(R.id.menuCustomerProfileDeposit).setVisible(false);
        }

        if (mainArray.length() == 0) {
            menu.findItem(R.id.menuCustomerProfileShareData).setVisible(false);
            menu.findItem(R.id.menuCustomerProfilePrint).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.editProfile:
                startActivityForResult(new Intent(this, Customer.class)
                        .putExtra("customerPosition", getIntent().getStringExtra("customerPosition")), requestSendForName);

                break;
            case R.id.menuCustomerProfileShareData:
                CommonUtils.shareData(this, customerData, "");
                break;
            case R.id.menuCustomerProfilePrint:
                CommonUtils.doPrint(this, customerData, true);
                break;
            case R.id.menuCustomerProfileInvoice:
                startActivityForResult(new Intent(this, Invoice.class)
                        .putExtra("invoiceName", customerData.optJSONArray("profile").optJSONObject(0).optString("customerName"))
                        .putExtra("invoiceId", customerData.optJSONArray("profile").optJSONObject(0).optString("customerId")), requestSendForData);
                break;
            case R.id.menuCustomerProfileDeposit:
                startActivityForResult(new Intent(this, Payment.class)
                        .putExtra("depositName", customerData.optJSONArray("profile").optJSONObject(0).optString("customerName"))
                        .putExtra("depositId", customerData.optJSONArray("profile").optJSONObject(0).optString("customerId")), requestSendForData);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cvCustomerAccountSearchClose:
                fillToLayout(mainArray);
                cvCustomerAccountSearch.setVisibility(View.VISIBLE);
                cvCustomerAccountSearchClose.setVisibility(View.GONE);
                llDate.setVisibility(View.GONE);
                llSearch.setVisibility(View.GONE);
                etSearch.setText("");
                etFromDate.setText("");
                etToDate.setText("");
                break;
            case R.id.cvCustomerAccountSearch:
                optionSearch();
                break;
            case R.id.etFromDate:
                datePicker(etFromDate, true);
                break;
            case R.id.etToDate:
                if (TextUtils.isEmpty(etFromDate.getText().toString()))
                    Toast.makeText(this, "Select From Date First", Toast.LENGTH_SHORT).show();
                else {
                    datePicker(etToDate, false);
                }
                break;
            case R.id.ivCancel:
                etSearch.setText("");
                break;
        }
    }
}