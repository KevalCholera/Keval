package com.example.keval.keval.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keval.keval.Fragment.FragmentCustomer;
import com.example.keval.keval.Fragment.FragmentInvoice;
import com.example.keval.keval.Fragment.FragmentPayment;
import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;
import com.example.keval.keval.Utils.Constants;
import com.mpt.storage.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Payment extends AppCompatActivity implements View.OnClickListener, TextWatcher, AdapterView.OnItemClickListener {

    public static String customerSelected = "";
    public static boolean checkCustomerSelected = true;
    ArrayList<String> options;
    TextView tvElementDepositDate, tvElementPaymentStatus, tvElementPaymentAmount, tvElementPaymentRupeeSymbol, tvElementDepositInvoiceNo;
    AutoCompleteTextView atvDepositCustomerName;
    EditText etElementDepositAmount;
    LinearLayout llElementPaymentStatus;
    ImageView ivDepositCustomerName;
    View vDepositCustomerName;
    private JSONObject jsonObject;
    private JSONArray customerNameArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        options = new ArrayList<>();
        checkShopName();

        llElementPaymentStatus = (LinearLayout) findViewById(R.id.llElementPaymentStatus);
        tvElementDepositDate = (TextView) findViewById(R.id.tvElementDepositDate);
        tvElementDepositInvoiceNo = (TextView) findViewById(R.id.tvElementDepositInvoiceNo);
        tvElementPaymentAmount = (TextView) findViewById(R.id.tvElementPaymentAmount);
        tvElementPaymentStatus = (TextView) findViewById(R.id.tvElementPaymentStatus);
        tvElementPaymentRupeeSymbol = (TextView) findViewById(R.id.tvElementPaymentRupeeSymbol);
        atvDepositCustomerName = (AutoCompleteTextView) findViewById(R.id.atvDepositCustomerName);
        etElementDepositAmount = (EditText) findViewById(R.id.etElementDepositAmount);
        ivDepositCustomerName = (ImageView) findViewById(R.id.ivDepositCustomerName);
        vDepositCustomerName = findViewById(R.id.vDepositCustomerName);

        jsonObject = new JSONObject();
        customerNameArray = new JSONArray();

        tvElementDepositDate.setText(CommonUtils.dateFormat(System.currentTimeMillis()));
        ivDepositCustomerName.setOnClickListener(this);

        atvDepositCustomerName.setTag("");

        try {
            customerNameArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, "") + "]");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        atvDepositCustomerName.setOnItemClickListener(this);
        llElementPaymentStatus.setVisibility(View.GONE);

        if (getIntent().hasExtra("onlyShow") || getIntent().hasExtra("editData")) {

            try {
                if (getIntent().hasExtra("onlyShow")) {
                    jsonObject = new JSONObject(getIntent().getStringExtra("onlyShow"));
                    etElementDepositAmount.setEnabled(false);
                    atvDepositCustomerName.setFocusable(false);
                    atvDepositCustomerName.setClickable(true);

                    if (!getIntent().hasExtra("customerAccount"))
                        atvDepositCustomerName.setOnClickListener(this);

                } else {
                    jsonObject = new JSONObject(getIntent().getStringExtra("editData"));
                    etElementDepositAmount.setEnabled(true);
                }

                JSONArray arrayInvoiceNo = jsonObject.optJSONArray("depositAmountInvoiceNo");

                if (!arrayInvoiceNo.get(0).toString().equalsIgnoreCase("") && arrayInvoiceNo.get(0).toString() != null) {
                    tvElementDepositInvoiceNo.setVisibility(View.VISIBLE);
                    tvElementDepositInvoiceNo.setText("Invoice No:- " + arrayInvoiceNo.get(0).toString().trim());
                }

                atvDepositCustomerName.setFocusable(false);
                vDepositCustomerName.setVisibility(View.GONE);
                ivDepositCustomerName.setVisibility(View.GONE);
                llElementPaymentStatus.setVisibility(View.VISIBLE);
                ivDepositCustomerName.setEnabled(false);
                tvElementDepositDate.setText(jsonObject.optString("depositDate"));
                atvDepositCustomerName.setText(jsonObject.optString("depositName"));
                atvDepositCustomerName.setTag(jsonObject.optString("customerId"));
                etElementDepositAmount.setText(jsonObject.optString("depositAmount"));
                tvElementPaymentAmount.setText(jsonObject.optString("depositTimeAmount"));
                tvElementPaymentStatus.setText(jsonObject.optString("depositTimeAmountStatus"));
                tvElementPaymentRupeeSymbol.setText(jsonObject.optString("depositTimeAmountSymbol"));

                getSupportActionBar().setTitle(jsonObject.optString("depositName"));

            } catch (JSONException | NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (getIntent().hasExtra("depositName")) {
            atvDepositCustomerName.setClickable(true);
            atvDepositCustomerName.setFocusable(false);
            atvDepositCustomerName.setText(getIntent().getStringExtra("depositName"));
            atvDepositCustomerName.setTag(getIntent().getStringExtra("depositId"));
            ivDepositCustomerName.setVisibility(View.GONE);
            vDepositCustomerName.setVisibility(View.GONE);
            tvElementDepositDate.setText(CommonUtils.dateFormat(System.currentTimeMillis()));
            setPaymentStatus();
        } else
            atvDepositCustomerName.addTextChangedListener(this);
    }

    public void setPaymentStatus() {

        try {
            llElementPaymentStatus.setVisibility(View.VISIBLE);
            JSONArray arrayCustomerDetail = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.ALL_DETAILS_BY_ID + atvDepositCustomerName.getTag().toString(), "") + "]");
            JSONObject objectCustomerDetail = arrayCustomerDetail.optJSONObject(0);

            if (objectCustomerDetail.optDouble("grandTotal") == 0)
                tvElementPaymentStatus.setText("Payment Completed");
            else if (objectCustomerDetail.optDouble("grandTotal") < 0)
                tvElementPaymentStatus.setText("Payment Deposit");
            else
                tvElementPaymentStatus.setText("Payment Credit");

            tvElementPaymentAmount.setText((CommonUtils.getDecimal(objectCustomerDetail.optDouble("grandTotal")).replace("-", "")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setDataByCustomerName() {

        for (int i = 0; i < customerNameArray.length(); i++) {
            JSONObject newJsonObject = customerNameArray.optJSONObject(i);

            if (newJsonObject.optString("customerName").trim().toUpperCase().equalsIgnoreCase(atvDepositCustomerName.getText().toString().toUpperCase().trim())) {
                atvDepositCustomerName.setTag(newJsonObject.optString("customerId"));
                customerSelected = newJsonObject.optString("customerId");
                setPaymentStatus();
                break;
            } else {
                llElementPaymentStatus.setVisibility(View.GONE);
            }
        }
    }

    public void addEditDeposit(final String customerId) {

        if (!getIntent().hasExtra("editData")) {
            CommonUtils.addDeposit("Payment",
                    CommonUtils.dateFormat(System.currentTimeMillis()),
                    atvDepositCustomerName.getText().toString().trim(),
                    customerId,
                    etElementDepositAmount.getText().toString().trim(), "",
                    tvElementPaymentStatus.getText().toString().trim(),
                    tvElementPaymentAmount.getText().toString().trim(),
                    tvElementPaymentRupeeSymbol.getText().toString().trim());

        } else if (jsonObject.optDouble("depositAmount") != Double.valueOf(etElementDepositAmount.getText().toString().trim()))
            CommonUtils.editDeposit(jsonObject, etElementDepositAmount.getText().toString().trim());

        if (getIntent().hasExtra("depositName")) {
            FragmentCustomer.onCreateSetData(this);
            setResult(Activity.RESULT_OK);
        } else {
            FragmentPayment.onCreateDataSet(Payment.this);
            FragmentInvoice.onCreateDataSet(Payment.this);
        }
        finish();

        if (!getIntent().hasExtra("editData"))
            Toast.makeText(this, "Payment Added Successfully", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Payment Updated Successfully", Toast.LENGTH_SHORT).show();
    }

    public boolean checkCustomerIsExist() {

        boolean check = false;

        for (int i = 0; i < customerNameArray.length(); i++) {
            JSONObject customerNameObject = customerNameArray.optJSONObject(i);

            if (customerNameObject.optString("customerName").trim().equalsIgnoreCase(atvDepositCustomerName.getText().toString().trim())) {
                check = true;
                atvDepositCustomerName.setTag(customerNameObject.optString("customerId"));
                break;
            } else {
                check = false;
            }
        }
        return check;
    }

    public void checkShopName() {
        if (!SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, "").equalsIgnoreCase(""))
            try {

                JSONArray newJsonArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, "") + "]");
                for (int i = 0; i < newJsonArray.length(); i++) {
                    JSONObject newJsonObject = newJsonArray.optJSONObject(i);
                    options.add(newJsonObject.optString("customerName") + "#" + newJsonObject.optString("customerId"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public void selectedCustomerName() {

        ArrayList<String> filteredCustomerName = new ArrayList<>();
        customerSelected = "";

        for (int i = 0; i < customerNameArray.length(); i++) {
            JSONObject newJsonObject = customerNameArray.optJSONObject(i);

            if (newJsonObject.optString("customerName").trim().toUpperCase().contains(atvDepositCustomerName.getText().toString().toUpperCase().trim()))
                filteredCustomerName.add(newJsonObject.optString("customerName"));
        }

        if (filteredCustomerName.size() > 0)
            atvDepositCustomerName.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredCustomerName));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_deposit, menu);

        if (getIntent().hasExtra("onlyShow"))
            menu.findItem(R.id.menuDeposit).setVisible(false);
        else if (getIntent().hasExtra("editData"))
            menu.findItem(R.id.menuDeposit).setTitle("Update Payment");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                CommonUtils.closeKeyboard(this);
                finish();
                break;
            case R.id.menuDeposit:
                CommonUtils.closeKeyboard(Payment.this);

                if (atvDepositCustomerName.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(Payment.this, "Select Customer Name", Toast.LENGTH_SHORT).show();
                else if (etElementDepositAmount.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(Payment.this, "Enter Amount to Deposit", Toast.LENGTH_SHORT).show();
                else {

                    if (!getIntent().hasExtra("editData"))
                        CommonUtils.showProgressDialog(this, "Adding Payment...");
                    else
                        CommonUtils.showProgressDialog(this, "Updating Payment...");

                    if (customerSelected.trim().equalsIgnoreCase("") && !checkCustomerIsExist()) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                JSONObject objectCustomerDetails = CommonUtils.addCustomerDetails(atvDepositCustomerName.getText().toString().trim());
                                String customerId = CommonUtils.addCustomer(objectCustomerDetails);
                                addEditDeposit(customerId);

                            }
                        }, 1000);
                    } else
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                addEditDeposit((String) atvDepositCustomerName.getTag());

                            }
                        }, 1000);
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivDepositCustomerName:
                if (options.size() > 0)
                    CommonUtils.picker(Payment.this, options, "Customer Name", atvDepositCustomerName, true, Payment.this.getLocalClassName());
                else
                    Toast.makeText(Payment.this, "No Customer Available", Toast.LENGTH_SHORT).show();
                break;
            case R.id.atvDepositCustomerName:
                startActivity(new Intent(this, CustomerAccount.class)
                        .putExtra("customerPosition", atvDepositCustomerName.getTag() + "")
                        .putExtra("deposit", "deposit"));
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (checkCustomerSelected && customerNameArray.length() > 0)
            selectedCustomerName();

        if (!atvDepositCustomerName.getText().toString().equalsIgnoreCase(""))
            setDataByCustomerName();

        checkCustomerSelected = true;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (view.getId()) {

            case R.id.atvDepositCustomerName:
                setDataByCustomerName();
                break;
        }
    }
}