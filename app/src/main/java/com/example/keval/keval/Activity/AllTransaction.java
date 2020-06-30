package com.example.keval.keval.Activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.example.keval.keval.Adapter.AdapterAllTransaction;
import com.example.keval.keval.Floating.FloatingActionButton;
import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;
import com.example.keval.keval.Utils.Constants;
import com.mpt.storage.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class AllTransaction extends AppCompatActivity implements View.OnClickListener {

    private JSONArray jsonArray;
    Calendar mCalendar = Calendar.getInstance();
    FloatingActionButton cvAllTransactionSearch, cvAllTransactionSearchClose;

    LinearLayout llSearch, llElementNoDataLayout, llDate;
    EditText etFromDate, etToDate, etSearch;
    ImageView ivCancel;
    ListView lvListOfAllTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_transaction);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvListOfAllTransaction = (ListView) findViewById(R.id.lvListOfAllTransaction);
        cvAllTransactionSearch = (FloatingActionButton) findViewById(R.id.cvAllTransactionSearch);
        cvAllTransactionSearchClose = (FloatingActionButton) findViewById(R.id.cvAllTransactionSearchClose);
        llDate = (LinearLayout) findViewById(R.id.llDate);
        etFromDate = (EditText) findViewById(R.id.etFromDate);
        etToDate = (EditText) findViewById(R.id.etToDate);
        llSearch = (LinearLayout) findViewById(R.id.llSearch);
        llElementNoDataLayout = (LinearLayout) findViewById(R.id.llElementNoDataLayout);
        etSearch = (EditText) findViewById(R.id.etSearch);
        ivCancel = (ImageView) findViewById(R.id.ivCancel);

        etSearch.setHint("All Transaction");
        mCalendar = Calendar.getInstance();

        try {
            jsonArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.ALL_TRANSACTION, "") + "]");

            if (jsonArray.length() == 0) {
                llDate.setVisibility(View.GONE);
                cvAllTransactionSearch.setVisibility(View.GONE);
                cvAllTransactionSearchClose.setVisibility(View.GONE);
                lvListOfAllTransaction.setVisibility(View.GONE);
                llElementNoDataLayout.setVisibility(View.VISIBLE);
            } else {
                lvListOfAllTransaction.setVisibility(View.VISIBLE);
                llElementNoDataLayout.setVisibility(View.GONE);
                llDate.setVisibility(View.GONE);
                cvAllTransactionSearch.setVisibility(View.VISIBLE);
                cvAllTransactionSearchClose.setVisibility(View.GONE);
                lvListOfAllTransaction.setAdapter(new AdapterAllTransaction(this, jsonArray));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        cvAllTransactionSearch.setOnClickListener(this);
        cvAllTransactionSearchClose.setOnClickListener(this);
        etFromDate.setOnClickListener(this);
        etToDate.setOnClickListener(this);
        ivCancel.setOnClickListener(this);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s != null && s.length() > 0 && jsonArray.length() > 0) {
                    ivCancel.setVisibility(View.VISIBLE);
                    JSONArray newArray = new JSONArray();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);

                        if (jsonObject.has("billNo") && jsonObject.optString("billNo").equalsIgnoreCase(etSearch.getText().toString()) || jsonObject.optString("shopName").toUpperCase().contains(etSearch.getText().toString().toUpperCase()))
                            newArray.put(jsonObject);
                        else if (jsonObject.optString("depositNo").equalsIgnoreCase(etSearch.getText().toString()) || jsonObject.optString("depositName").toUpperCase().contains(etSearch.getText().toString().toUpperCase()))
                            newArray.put(jsonObject);

                    }
                    lvListOfAllTransaction.setAdapter(new AdapterAllTransaction(AllTransaction.this, newArray));

                } else {
                    ivCancel.setVisibility(View.GONE);
                    lvListOfAllTransaction.setAdapter(new AdapterAllTransaction(AllTransaction.this, jsonArray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

                if (CommonUtils.convertStringToDate(etFromDate.getText().toString()).getTime() <= jsonArray.optJSONObject(0).optLong("dateLongHigh"))
                    lvListOfAllTransaction.setAdapter(new AdapterAllTransaction(AllTransaction.this, CommonUtils.searchByDate(AllTransaction.this, etToDate, etFromDate, jsonArray)));
                else if (CommonUtils.convertStringToDate(etFromDate.getText().toString()).getTime() > jsonArray.optJSONObject(0).optLong("dateLongHigh"))
                    lvListOfAllTransaction.setAdapter(new AdapterAllTransaction(AllTransaction.this, new JSONArray()));

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
        options = new CharSequence[]{"Search by Date", "Search by All Transaction"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose source option!");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Search by Date")) {
                    llDate.setVisibility(View.VISIBLE);
                    cvAllTransactionSearch.setVisibility(View.GONE);
                    cvAllTransactionSearchClose.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                } else if (options[item].equals("Search by All Transaction")) {
                    cvAllTransactionSearch.setVisibility(View.GONE);
                    cvAllTransactionSearchClose.setVisibility(View.VISIBLE);
                    llSearch.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
            }
        });
        builder.show();
        builder.setCancelable(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                CommonUtils.closeKeyboard(this);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cvAllTransactionSearchClose:
                lvListOfAllTransaction.setAdapter(new AdapterAllTransaction(AllTransaction.this, jsonArray));
                cvAllTransactionSearch.setVisibility(View.VISIBLE);
                cvAllTransactionSearchClose.setVisibility(View.GONE);
                llDate.setVisibility(View.GONE);
                llSearch.setVisibility(View.GONE);
                etSearch.setText("");
                etFromDate.setText("");
                etToDate.setText("");
                break;
            case R.id.cvAllTransactionSearch:
                optionSearch();
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
            case R.id.ivCancel:
                etSearch.setText("");
                break;
        }
    }
}