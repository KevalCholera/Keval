package com.example.keval.keval.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keval.keval.DataImage.ShareBill;
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

public class Invoice extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    JSONArray arrayProductList = new JSONArray();
    JSONArray customerNameArray = new JSONArray();
    JSONArray productNameArray = new JSONArray();
    JSONObject mainObject = new JSONObject();

    ArrayList<String> optionsProductName = new ArrayList<>();
    ArrayList<String> customerNameToACT = new ArrayList<>();
    ArrayList<String> customerNameToPicker = new ArrayList<>();
    ArrayList<Integer> positionArray = new ArrayList<>();

    final int reqToAdd = 101;
    final int reqToEdit = 102;
    int positionToEdit = 0;
    double transactionStatusAmount = 0;
    double grandTotal = 0;
    String discountText = "";
    String selectedCustomerId = "";
    public static boolean checkCustomerSelected = true;

    AutoCompleteTextView atvInvoiceCustomerName;
    ListView lvProductList;
    View vInvoiceCustomerName;
    CheckBox cbInvoiceAddPayment, cbInvoiceDiscount, cbInvoiceShipping;
    LinearLayout llInvoiceDiscount, llInvoiceShipping, llInvoicePayment, llInvoiceAmountStatus;
    TextView tvInvoiceTotalToCalc, tvInvoiceDiscount, tvInvoiceSubTotal, tvInvoiceShippingCalc,
            tvInvoiceGrandTotal, tvInvoiceAmountPaidCalc, tvInvoiceDue, tvInvoiceDate, tvInvoiceTotal,
            tvInvoiceAddProduct, tvInvoiceAmountPaid, tvInvoiceAmountDiscount, tvInvoiceShipping,
            tvInvoicePaymentStatus, tvInvoiceAmountStatus, tvInvoiceAmountStatusSign, tvInvoiceDueSign,
            tvInvoiceTotalSign, tvInvoiceFinalTotal, tvInvoiceFinalTotalSign;
    EditText etInvoiceDiscount, etInvoiceShipping, etInvoicePayment;
    ImageView ivInvoiceCustomerName;

    MenuItem createInvoice, menuShareImage, menuPrintBill, menuShareData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        SharedPreferenceUtil.init(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvInvoiceDate = (TextView) findViewById(R.id.tvInvoiceDate);
        tvInvoicePaymentStatus = (TextView) findViewById(R.id.tvInvoicePaymentStatus);
        tvInvoiceAmountStatus = (TextView) findViewById(R.id.tvInvoiceAmountStatus);
        tvInvoiceAmountStatusSign = (TextView) findViewById(R.id.tvInvoiceAmountStatusSign);
        tvInvoiceTotal = (TextView) findViewById(R.id.tvInvoiceTotal);
        tvInvoiceFinalTotal = (TextView) findViewById(R.id.tvInvoiceFinalTotal);
        tvInvoiceFinalTotalSign = (TextView) findViewById(R.id.tvInvoiceFinalTotalSign);
        atvInvoiceCustomerName = (AutoCompleteTextView) findViewById(R.id.atvInvoiceCustomerName);
        tvInvoiceAddProduct = (TextView) findViewById(R.id.tvInvoiceAddProduct);
        tvInvoiceAmountPaid = (TextView) findViewById(R.id.tvInvoiceAmountPaid);
        tvInvoiceAmountDiscount = (TextView) findViewById(R.id.tvInvoiceAmountDiscount);
        tvInvoiceShipping = (TextView) findViewById(R.id.tvInvoiceShipping);
        tvInvoiceTotalToCalc = (TextView) findViewById(R.id.tvInvoiceTotalToCalc);
        tvInvoiceDiscount = (TextView) findViewById(R.id.tvInvoiceDiscount);
        tvInvoiceSubTotal = (TextView) findViewById(R.id.tvInvoiceSubTotal);
        tvInvoiceShippingCalc = (TextView) findViewById(R.id.tvInvoiceShippingCalc);
        tvInvoiceGrandTotal = (TextView) findViewById(R.id.tvInvoiceGrandTotal);
        tvInvoiceAmountPaidCalc = (TextView) findViewById(R.id.tvInvoiceAmountPaidCalc);
        tvInvoiceDue = (TextView) findViewById(R.id.tvInvoiceDue);
        tvInvoiceDueSign = (TextView) findViewById(R.id.tvInvoiceDueSign);
        tvInvoiceTotalSign = (TextView) findViewById(R.id.tvInvoiceTotalSign);

        lvProductList = (ListView) findViewById(R.id.lvInvoice);
        vInvoiceCustomerName = (View) findViewById(R.id.vInvoiceCustomerName);
        ivInvoiceCustomerName = (ImageView) findViewById(R.id.ivInvoiceCustomerName);

        llInvoicePayment = (LinearLayout) findViewById(R.id.llInvoicePayment);
        llInvoiceDiscount = (LinearLayout) findViewById(R.id.llInvoiceDiscount);
        llInvoiceAmountStatus = (LinearLayout) findViewById(R.id.llInvoiceAmountStatus);
        llInvoiceShipping = (LinearLayout) findViewById(R.id.llInvoiceShipping);

        etInvoicePayment = (EditText) findViewById(R.id.etInvoicePayment);
        etInvoiceDiscount = (EditText) findViewById(R.id.etInvoiceDiscount);
        etInvoiceShipping = (EditText) findViewById(R.id.etInvoiceShipping);

        cbInvoiceAddPayment = (CheckBox) findViewById(R.id.cbInvoiceAddPayment);
        cbInvoiceDiscount = (CheckBox) findViewById(R.id.cbInvoiceDiscount);
        cbInvoiceShipping = (CheckBox) findViewById(R.id.cbInvoiceShipping);

        tvInvoiceAddProduct.setOnClickListener(this);
        cbInvoiceAddPayment.setOnClickListener(this);
        cbInvoiceDiscount.setOnClickListener(this);
        cbInvoiceShipping.setOnClickListener(this);
        ivInvoiceCustomerName.setOnClickListener(this);
        atvInvoiceCustomerName.setOnItemClickListener(this);

        tvInvoiceDate.setText(CommonUtils.dateFormat(System.currentTimeMillis()));

        atvInvoiceCustomerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                addTextToName();
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!getIntent().hasExtra("onlyShow") && !getIntent().hasExtra("editData"))
                    etInvoicePayment.setText(tvInvoiceFinalTotal.getText().toString().trim());

            }
        });

        etInvoiceDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addTextToDiscount();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!getIntent().hasExtra("onlyShow") && !getIntent().hasExtra("editData"))
                    etInvoicePayment.setText(tvInvoiceFinalTotal.getText().toString().trim());
            }
        });

        etInvoiceShipping.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateProductTotal(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!getIntent().hasExtra("onlyShow") && !getIntent().hasExtra("editData"))
                    etInvoicePayment.setText(tvInvoiceFinalTotal.getText().toString().trim());
            }
        });

        etInvoicePayment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateProductTotal(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fillAdapter();
        fillCustomerName();
        fillProductName();
        fillCustomer();

        try {
            if (getIntent().hasExtra("onlyShow") || getIntent().hasExtra("editData")) {

                if (!getIntent().hasExtra("editData")) {
                    mainObject = new JSONObject(getIntent().getStringExtra("onlyShow"));

                    tvInvoiceAddProduct.setVisibility(View.GONE);
                    cbInvoiceDiscount.setVisibility(View.GONE);
                    cbInvoiceShipping.setVisibility(View.GONE);
                    cbInvoiceAddPayment.setVisibility(View.GONE);
                    atvInvoiceCustomerName.setFocusable(false);
                    atvInvoiceCustomerName.setClickable(true);

                    if (!getIntent().hasExtra("customerAccount"))
                        atvInvoiceCustomerName.setOnClickListener(this);

                } else
                    mainObject = new JSONObject(getIntent().getStringExtra("editData"));

                atvInvoiceCustomerName.setFocusable(false);
                vInvoiceCustomerName.setVisibility(View.GONE);
                ivInvoiceCustomerName.setVisibility(View.GONE);
                ivInvoiceCustomerName.setEnabled(false);
                atvInvoiceCustomerName.setText(mainObject.optString("customerName"));
                atvInvoiceCustomerName.setTag(mainObject.optString("customerId"));
                tvInvoiceDate.setText(mainObject.optString("billDate"));
                tvInvoiceTotal.setText(getResources().getString(R.string.currency_india) + " " + mainObject.optString("billTotal"));
                getSupportActionBar().setTitle(mainObject.optString("customerName"));

                if (!mainObject.optString("billAddPayment").equalsIgnoreCase("") && mainObject.optDouble("billAddPayment") >= 0) {
                    llInvoicePayment.setVisibility(View.VISIBLE);
                    cbInvoiceAddPayment.setVisibility(View.GONE);

                    if (getIntent().hasExtra("onlyShow")) {
                        tvInvoiceAmountPaid.setText("Amount Paid");
                        llInvoicePayment.setEnabled(false);
                        etInvoicePayment.setEnabled(false);
                    }
                    etInvoicePayment.setText(mainObject.optString("billAddPayment"));
                }
                if (!mainObject.optString("billDiscount").equalsIgnoreCase("") && mainObject.optDouble("billDiscount") > 0) {
                    llInvoiceDiscount.setVisibility(View.VISIBLE);
                    cbInvoiceDiscount.setChecked(true);

                    if (getIntent().hasExtra("onlyShow")) {
                        tvInvoiceAmountDiscount.setText("Invoice Discount");
                        llInvoiceDiscount.setEnabled(false);
                        etInvoiceDiscount.setEnabled(false);
                    }
                    etInvoiceDiscount.setText(mainObject.optString("billDiscount"));
                }
                if (!mainObject.optString("billShipping").equalsIgnoreCase("") && mainObject.optDouble("billShipping") > 0) {
                    llInvoiceShipping.setVisibility(View.VISIBLE);
                    cbInvoiceShipping.setChecked(true);

                    if (getIntent().hasExtra("onlyShow")) {
                        tvInvoiceShipping.setText("Shipping Charge");
                        llInvoiceShipping.setEnabled(false);
                        etInvoiceShipping.setEnabled(false);
                    }
                    etInvoiceShipping.setText(mainObject.optString("billShipping"));
                }

                tvInvoicePaymentStatus.setVisibility(View.VISIBLE);
                llInvoiceAmountStatus.setVisibility(View.VISIBLE);

                tvInvoiceAmountStatus.setText(CommonUtils.getDecimal(Double.valueOf(CommonUtils.removeSymbols(Invoice.this, mainObject.optString("billTransactionAtInvoice")))));
                tvInvoicePaymentStatus.setText(mainObject.optString("billTransactionStatusAtInvoice"));
                tvInvoiceAmountStatusSign.setText(mainObject.optString("billTransactionAtInvoiceSign"));

                arrayProductList = mainObject.optJSONArray("info");
                fillAdapter();

            } else if (getIntent().hasExtra("invoiceName")) {

                atvInvoiceCustomerName.setEnabled(false);
                atvInvoiceCustomerName.setText(getIntent().getStringExtra("invoiceName"));
                atvInvoiceCustomerName.setTag(getIntent().getStringExtra("invoiceId"));
                ivInvoiceCustomerName.setVisibility(View.GONE);
                vInvoiceCustomerName.setVisibility(View.GONE);
                tvInvoiceDate.setText(CommonUtils.dateFormat(System.currentTimeMillis()));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        calculateProductTotal(true);
    }

    public void addTextToDiscount() {
        if (!TextUtils.isEmpty(etInvoiceDiscount.getText().toString().trim())) {

            if (Double.valueOf(etInvoiceDiscount.getText().toString().trim()) <= 100) {

                calculateProductTotal(true);
                discountText = etInvoiceDiscount.getText().toString().trim();
            } else {

                etInvoiceDiscount.setText(discountText);
                Toast.makeText(Invoice.this, "Discount can't be greater than 100", Toast.LENGTH_SHORT).show();
            }

        } else
            calculateProductTotal(true);
    }

    public void addTextToName() {
        if (getIntent().hasExtra("invoiceName"))
            atvInvoiceCustomerName.setAdapter(null);
        else {
            if (checkCustomerSelected && !getIntent().hasExtra("onlyShow") && !getIntent().hasExtra("editData") && customerNameArray.length() > 0) {
                selectedCustomerName();
            } else
                atvInvoiceCustomerName.setAdapter(new ArrayAdapter<>(Invoice.this, android.R.layout.simple_list_item_1, new ArrayList<String>()));

            checkCustomerSelected = true;
        }

        for (int i = 0; i < customerNameArray.length(); i++) {
            JSONObject objectNameArray = customerNameArray.optJSONObject(i);

            if (atvInvoiceCustomerName.getText().toString().trim().equalsIgnoreCase(objectNameArray.optString("customerName"))) {

                tvInvoicePaymentStatus.setVisibility(View.VISIBLE);
                llInvoiceAmountStatus.setVisibility(View.VISIBLE);
                transactionStatusAmount = objectNameArray.optDouble("grandTotal");

                if (getIntent().hasExtra("onlyShow") || getIntent().hasExtra("editData")) {
                    try {
                        JSONObject objectOnlyShow;

                        if (getIntent().hasExtra("onlyShow"))
                            objectOnlyShow = new JSONObject(getIntent().getStringExtra("onlyShow"));
                        else
                            objectOnlyShow = new JSONObject(getIntent().getStringExtra("editData"));

                        if (objectOnlyShow.optString("billTransactionAtInvoiceSign").equalsIgnoreCase("-"))
                            transactionStatusAmount = 0 - objectOnlyShow.optDouble("billTransactionAtInvoice");
                        else
                            transactionStatusAmount = objectOnlyShow.optDouble("billTransactionAtInvoice");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                transactionStatus();

                break;

            } else {
                tvInvoicePaymentStatus.setVisibility(View.GONE);
                llInvoiceAmountStatus.setVisibility(View.GONE);
                cbInvoiceAddPayment.setVisibility(View.VISIBLE);
                etInvoicePayment.setText("0");
                etInvoicePayment.setVisibility(View.VISIBLE);
            }
        }

        calculateProductTotal(true);
    }

    public void transactionStatus() {

        if (transactionStatusAmount <= 0)
            if (Double.valueOf(tvInvoiceGrandTotal.getText().toString().trim()) == 0) {
                tvInvoiceAmountStatus.setText("0.00");
            } else if (Double.valueOf(tvInvoiceGrandTotal.getText().toString().trim()) < Double.valueOf(CommonUtils.removeSymbols(Invoice.this, transactionStatusAmount + ""))) {
                tvInvoiceAmountStatus.setText(tvInvoiceGrandTotal.getText().toString().trim());
            } else {
                tvInvoiceAmountStatus.setText(CommonUtils.getDecimal(Double.valueOf(CommonUtils.removeSymbols(Invoice.this, transactionStatusAmount + ""))));
            }
        else
            tvInvoiceAmountStatus.setText(CommonUtils.getDecimal(Double.valueOf(CommonUtils.removeSymbols(Invoice.this, transactionStatusAmount + ""))));

        if (transactionStatusAmount == 0) {
            tvInvoicePaymentStatus.setText("Completed Transaction");
            tvInvoiceAmountStatusSign.setText("-");
        } else if (transactionStatusAmount > 0) {
            tvInvoicePaymentStatus.setText("Amount Pending");
            tvInvoiceAmountStatusSign.setText("+");
        } else {
            tvInvoicePaymentStatus.setText("Amount Debit");
            tvInvoiceAmountStatusSign.setText("-");
        }
    }

    public void fillAdapter() {
        grandTotal = 0;
        positionArray = new ArrayList<>();
        tvInvoiceTotal.setText("0");

        if (!getIntent().hasExtra("onlyShow") && !getIntent().hasExtra("editData"))
            etInvoicePayment.setText("0");

        CommonUtils.showLog(this, arrayProductList);
        lvProductList.setAdapter(new AdapterInvoice(arrayProductList));
        CommonUtils.setListViewHeightBasedOnChildren(lvProductList);
    }

    public void fillCustomerName() {

        if (!SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, "").equalsIgnoreCase(""))
            try {
                customerNameArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, "") + "]");
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public void fillProductName() {

        optionsProductName = new ArrayList<>();

        if (!SharedPreferenceUtil.getString(Constants.STOCK_DATA_SAVE, "").equalsIgnoreCase(""))
            try {
                productNameArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.STOCK_DATA_SAVE, "") + "]");
                for (int i = productNameArray.length() - 1; i >= 0; i--) {
                    JSONObject itemNameObject = productNameArray.optJSONObject(i);
                    optionsProductName.add(itemNameObject.optString("itemName") + "#" + itemNameObject.optString("itemId"));

                    SharedPreferenceUtil.putValue(itemNameObject.optString("itemName") + itemNameObject.optString("itemId"), itemNameObject.optString("itemQuantity"));
                    SharedPreferenceUtil.save();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public void fillCustomer() {

        customerNameToPicker = new ArrayList<>();

        for (int i = 0; i < customerNameArray.length(); i++) {
            JSONObject newJsonObject = customerNameArray.optJSONObject(i);
            customerNameToPicker.add(newJsonObject.optString("customerName") + "#" + newJsonObject.optString("customerId"));

        }
    }

    public void addCustomer() {
        try {
            JSONArray getIdArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, "") + "]");

            JSONObject objectUserData = CommonUtils.addCustomerDetails(atvInvoiceCustomerName.getText().toString().trim());

            if (getIdArray.length() > 0)
                for (int i = 0; i < getIdArray.length(); i++) {
                    JSONObject getIdObject = getIdArray.optJSONObject(i);

                    if (atvInvoiceCustomerName.getText().toString().trim().equalsIgnoreCase(getIdObject.optString("customerName"))) {
                        selectedCustomerId = getIdObject.optString("customerId");
                        break;
                    } else {
                        if (i == getIdArray.length() - 1)
                            selectedCustomerId = CommonUtils.addCustomer(objectUserData);
                    }
                }
            else
                selectedCustomerId = CommonUtils.addCustomer(objectUserData);
            addInvoice(selectedCustomerId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectedCustomerName() {

        customerNameToACT = new ArrayList<>();
        selectedCustomerId = "";

        for (int i = 0; i < customerNameArray.length(); i++) {
            JSONObject newJsonObject = customerNameArray.optJSONObject(i);
            if (newJsonObject.optString("customerName").trim().toUpperCase().contains(atvInvoiceCustomerName.getText().toString().toUpperCase().trim())) {
                customerNameToACT.add(newJsonObject.optString("customerName"));
            }
        }

        if (customerNameToACT.size() > 0)
            atvInvoiceCustomerName.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, customerNameToACT));
    }

    public void editDepositByPayment() {
        try {
            boolean breakLoop = false;
            JSONArray depositArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.DEPOSIT_DATA_SAVE, "") + "]");

            for (int i = 0; i < depositArray.length(); i++) {
                JSONObject depositObject = depositArray.optJSONObject(i);

                for (int j = 0; j < mainObject.optJSONArray("billAmountPaymentNo").length(); j++) {

                    if (depositObject.optString("depositNo").equalsIgnoreCase(mainObject.optJSONArray("billAmountPaymentNo").optString(j))) {
                        CommonUtils.editDeposit(depositObject, etInvoicePayment.getText().toString().trim());
                        breakLoop = true;
                        break;
                    }
                }

                if (breakLoop)
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addInvoice(String customerId) {

        try {

            CommonUtils.dateWiseManagement(tvInvoiceDate.getText().toString().trim(), customerId, "Invoice");
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("billNo", CommonUtils.indexId(Constants.BILLING_ITEM_ID));
            jsonObject.put("billDate", tvInvoiceDate.getText().toString().trim());
            jsonObject.put("dateWise", true);

            JSONArray depositNoArray = new JSONArray();

            if (llInvoicePayment.getVisibility() == View.VISIBLE && !TextUtils.isEmpty(etInvoicePayment.getText().toString().trim()) && Double.valueOf(etInvoicePayment.getText().toString().trim()) >= 0) {

                if (!TextUtils.isEmpty(SharedPreferenceUtil.getString(Constants.DEPOSIT_ITEM_ID, ""))) {
                    depositNoArray.put((Integer.valueOf(SharedPreferenceUtil.getString(Constants.DEPOSIT_ITEM_ID, "")) + 1) + "");
                    jsonObject.put("billAmountPaymentNo", depositNoArray);
                } else {
                    depositNoArray.put("1");
                    jsonObject.put("billAmountPaymentNo", depositNoArray);
                }

                jsonObject.put("billAddPayment", etInvoicePayment.getText().toString().trim());
                jsonObject.put("billPaid", true);

            } else {
                depositNoArray.put("");
                jsonObject.put("billAddPayment", "0.00");
                jsonObject.put("billAmountPaymentNo", depositNoArray);
                jsonObject.put("billPaid", false);
            }

            jsonObject.put("customerId", customerId);
            jsonObject.put("customerName", atvInvoiceCustomerName.getText().toString().trim());
            jsonObject.put("billTotal", CommonUtils.getDecimal(Double.valueOf(tvInvoiceTotal.getText().toString().trim())));
            jsonObject.put("billAmount", CommonUtils.getDecimal(Double.valueOf(tvInvoiceTotalToCalc.getText().toString().trim())));

            if (TextUtils.isEmpty(etInvoiceDiscount.getText().toString().trim()))
                jsonObject.put("billDiscount", "0");
            else
                jsonObject.put("billDiscount", etInvoiceDiscount.getText().toString().trim());

            if (TextUtils.isEmpty(etInvoiceShipping.getText().toString().trim()))
                jsonObject.put("billShipping", "0");
            else
                jsonObject.put("billShipping", etInvoiceShipping.getText().toString().trim());

            if (tvInvoicePaymentStatus.getVisibility() == View.VISIBLE || !tvInvoicePaymentStatus.getText().toString().trim().equalsIgnoreCase(""))
                jsonObject.put("billTransactionStatusAtInvoice", tvInvoicePaymentStatus.getText().toString().trim());
            else
                jsonObject.put("billTransactionStatusAtInvoice", "Complete Transaction");

            if (tvInvoiceAmountStatus.getVisibility() == View.VISIBLE || !tvInvoiceAmountStatus.getText().toString().trim().equalsIgnoreCase(""))
                jsonObject.put("billTransactionAtInvoice", tvInvoiceAmountStatus.getText().toString().trim());
            else
                jsonObject.put("billTransactionAtInvoice", "0");

            if (tvInvoiceAmountStatusSign.getText().toString().trim().equalsIgnoreCase(""))
                jsonObject.put("billTransactionAtInvoiceSign", "+");
            else if (tvInvoiceAmountStatusSign.getText().toString().trim().equalsIgnoreCase("+"))
                jsonObject.put("billTransactionAtInvoiceSign", "+");
            else
                jsonObject.put("billTransactionAtInvoiceSign", "-");

            jsonObject.put("billDiscountInAmount", CommonUtils.removeSymbols(this, tvInvoiceDiscount.getText().toString().trim()));

            if (Double.valueOf(tvInvoiceDue.getText().toString().trim()) == 0)
                jsonObject.put("billPaidType", "Amount Paid");
            else if (tvInvoiceDueSign.getText().toString().trim().equalsIgnoreCase("-")) {

                if (Double.valueOf(tvInvoiceDue.getText().toString().trim()) > Double.valueOf(tvInvoiceFinalTotal.getText().toString().trim()))
                    jsonObject.put("billPaidType", "More Amount Paid");
            } else if (tvInvoiceFinalTotal.getText().toString().trim().equalsIgnoreCase(tvInvoiceDue.getText().toString().trim()))
                jsonObject.put("billPaidType", "Amount not paid");
            else
                jsonObject.put("billPaidType", "Partially Amount paid");

            jsonObject.put("billLeft", tvInvoiceDue.getText().toString().trim());
            jsonObject.put("info", arrayProductList);
            jsonObject.put("dateLongHigh", CommonUtils.higherDateLong(Constants.BILL_DATA_SAVE, tvInvoiceDate.getText().toString(), true));
            jsonObject.put("dateLongLow", CommonUtils.higherDateLong(Constants.BILL_DATA_SAVE, tvInvoiceDate.getText().toString(), false));

            double newDeposit = Double.valueOf(tvInvoiceGrandTotal.getText().toString().trim());
            double total;

            if (SharedPreferenceUtil.getString(Constants.SAVE_INVOICE_BY_DATE + tvInvoiceDate.getText().toString().trim(), "").equalsIgnoreCase(""))
                total = newDeposit;
            else
                total = newDeposit + Double.valueOf(SharedPreferenceUtil.getString(Constants.SAVE_INVOICE_BY_DATE + tvInvoiceDate.getText().toString().trim(), ""));

            for (int i = 0; i < productNameArray.length(); i++) {
                JSONObject productNameObject = productNameArray.optJSONObject(i);
                productNameObject.put("itemQuantity", SharedPreferenceUtil.getString(productNameObject.optString("itemName") + productNameObject.optString("itemId"), ""));
            }

            JSONObject allDetailById = new JSONObject(SharedPreferenceUtil.getString(Constants.ALL_DETAILS_BY_ID + customerId, ""));

            JSONObject addToMainObject = new JSONObject();
            JSONArray newDataFill = allDetailById.optJSONArray("data");
            newDataFill.put(jsonObject);

            double grandTotal = Float.valueOf(allDetailById.optString("grandTotal")) + Float.valueOf(tvInvoiceTotal.getText().toString().trim());
            JSONArray customerJsonArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, "") + "]");

            for (int i = 0; i < customerJsonArray.length(); i++) {
                JSONObject customerJsonObject = customerJsonArray.optJSONObject(i);

                if (customerJsonObject.optString("customerId").equalsIgnoreCase(customerId)) {
                    customerJsonObject.put("grandTotal", grandTotal + "");
                    allDetailById.optJSONArray("profile").optJSONObject(0).put("grandTotal", grandTotal + "");
                    break;
                }
            }

            addToMainObject.put("profile", allDetailById.optJSONArray("profile"));
            addToMainObject.put("data", newDataFill);
            addToMainObject.put("grandTotal", grandTotal + "");

            SharedPreferenceUtil.putValue(Constants.STOCK_DATA_SAVE, productNameArray.toString().substring(1, productNameArray.toString().length() - 1));
            SharedPreferenceUtil.putValue(Constants.BILL_DATA_SAVE, CommonUtils.addDataNewThenOld(Constants.BILL_DATA_SAVE, jsonObject.toString()));
            SharedPreferenceUtil.putValue(Constants.SAVE_INVOICE_BY_DATE + tvInvoiceDate.getText().toString().trim(), total + "");
            SharedPreferenceUtil.putValue(Constants.ALL_TRANSACTION, CommonUtils.addDataNewThenOld(Constants.ALL_TRANSACTION, jsonObject.toString()));
            SharedPreferenceUtil.putValue(Constants.CUSTOMER_DATA_SAVE, customerJsonArray.toString().substring(1, customerJsonArray.toString().length() - 1));
            SharedPreferenceUtil.putValue(Constants.ALL_DETAILS_BY_ID + customerId, addToMainObject.toString());
            SharedPreferenceUtil.save();

            if (llInvoicePayment.getVisibility() == View.VISIBLE
                    && !TextUtils.isEmpty(etInvoicePayment.getText().toString().trim())) {

                if (grandTotal < 0)
                    CommonUtils.addDeposit("Invoice", CommonUtils.dateFormat(System.currentTimeMillis()),
                            atvInvoiceCustomerName.getText().toString(), customerId, etInvoicePayment.getText().toString(),
                            jsonObject.optString("billNo"), "Payment Deposit", CommonUtils.removeSymbols(this, addToMainObject.optString("grandTotal")), "-");
                else if (grandTotal == 0)
                    CommonUtils.addDeposit("Invoice", CommonUtils.dateFormat(System.currentTimeMillis()),
                            atvInvoiceCustomerName.getText().toString(), customerId, etInvoicePayment.getText().toString(),
                            jsonObject.optString("billNo"), "Payment Completed", CommonUtils.removeSymbols(this, addToMainObject.optString("grandTotal")), "");
                else
                    CommonUtils.addDeposit("Invoice", CommonUtils.dateFormat(System.currentTimeMillis()),
                            atvInvoiceCustomerName.getText().toString(), customerId, etInvoicePayment.getText().toString(),
                            jsonObject.optString("billNo"), "Payment Credit", CommonUtils.removeSymbols(this, addToMainObject.optString("grandTotal")), "");
            }

            if (!getIntent().hasExtra("invoiceName")) {
                FragmentInvoice.onCreateDataSet(this);
                FragmentPayment.onCreateDataSet(this);
            } else {
                FragmentCustomer.onCreateSetData(this);
                setResult(Activity.RESULT_OK);
            }

            finish();
            Toast.makeText(this, "Invoice Created Successfully", Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void editInvoice(boolean editInvoiceByPayment) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("billNo", mainObject.optString("billNo"));
            jsonObject.put("billDate", mainObject.optString("billDate"));
            jsonObject.put("dateWise", mainObject.optBoolean("dateWise"));

            if (llInvoicePayment.getVisibility() == View.VISIBLE) {
                jsonObject.put("billPaid", true);

                if (!mainObject.optBoolean("billPaid")) {
                    jsonObject.put("billAddPayment", tvInvoiceAmountPaidCalc.getText().toString().trim());
                    JSONArray depositArrayName = new JSONArray();

                    if (!TextUtils.isEmpty(SharedPreferenceUtil.getString(Constants.DEPOSIT_ITEM_ID, ""))) {
                        depositArrayName.put((Integer.valueOf(SharedPreferenceUtil.getString(Constants.DEPOSIT_ITEM_ID, "")) + 1) + "");
                        jsonObject.put("billAmountPaymentNo", depositArrayName);
                    } else {
                        depositArrayName.put("1");
                        jsonObject.put("billAmountPaymentNo", depositArrayName);
                    }
                } else {
                    jsonObject.put("billAddPayment", tvInvoiceAmountPaidCalc.getText().toString().trim());
                    jsonObject.put("billAmountPaymentNo", mainObject.optJSONArray("billAmountPaymentNo"));
                }
            } else {
                jsonObject.put("billPaid", false);
                jsonObject.put("billAddPayment", mainObject.optString("billAddPayment"));
                jsonObject.put("billAmountPaymentNo", mainObject.optJSONArray("billAmountPaymentNo"));
            }

            jsonObject.put("customerId", mainObject.optString("customerId"));
            jsonObject.put("customerName", mainObject.optString("customerName"));
            jsonObject.put("billTotal", CommonUtils.getDecimal(Double.valueOf(tvInvoiceTotal.getText().toString().trim())));
            jsonObject.put("billAmount", CommonUtils.getDecimal(Double.valueOf(tvInvoiceTotalToCalc.getText().toString().trim())));

            if (TextUtils.isEmpty(etInvoiceDiscount.getText().toString().trim()))
                jsonObject.put("billDiscount", "0");
            else
                jsonObject.put("billDiscount", etInvoiceDiscount.getText().toString().trim());

            if (TextUtils.isEmpty(etInvoiceShipping.getText().toString().trim()))
                jsonObject.put("billShipping", "0");
            else
                jsonObject.put("billShipping", etInvoiceShipping.getText().toString().trim());

            jsonObject.put("billDiscountInAmount", tvInvoiceDiscount.getText().toString().trim());

            jsonObject.put("billTransactionAtInvoice", mainObject.optString("billTransactionAtInvoice"));
            jsonObject.put("billTransactionAtInvoiceSign", mainObject.optString("billTransactionAtInvoiceSign"));
            jsonObject.put("billTransactionStatusAtInvoice", mainObject.optString("billTransactionStatusAtInvoice"));

            if (Double.valueOf(tvInvoiceDue.getText().toString().trim()) == 0)
                jsonObject.put("billPaidType", "Amount Paid");
            else if (tvInvoiceDueSign.getText().toString().trim().equalsIgnoreCase("-")) {

                if (Double.valueOf(tvInvoiceDue.getText().toString().trim()) > Double.valueOf(tvInvoiceFinalTotal.getText().toString().trim()))
                    jsonObject.put("billPaidType", "More Amount Paid");
            } else if (tvInvoiceFinalTotal.getText().toString().trim().equalsIgnoreCase(tvInvoiceDue.getText().toString().trim()))
                jsonObject.put("billPaidType", "Amount not paid");
            else
                jsonObject.put("billPaidType", "Partially Amount paid");

            jsonObject.put("info", arrayProductList);
            jsonObject.put("billLeft", tvInvoiceDue.getText().toString().trim());
            jsonObject.put("dateLongHigh", mainObject.optString("dateLongHigh"));
            jsonObject.put("dateLongLow", mainObject.optString("dateLongLow"));

            double newDeposit = Double.valueOf(tvInvoiceGrandTotal.getText().toString().trim());
            double total;

            if (SharedPreferenceUtil.getString(Constants.SAVE_INVOICE_BY_DATE + tvInvoiceDate.getText().toString().trim(), "").equalsIgnoreCase(""))
                total = newDeposit;
            else
                total = newDeposit + Double.valueOf(SharedPreferenceUtil.getString(Constants.SAVE_INVOICE_BY_DATE + tvInvoiceDate.getText().toString().trim(), ""));

            JSONArray billArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.BILL_DATA_SAVE, "") + "]");
            JSONObject allDetailById = new JSONObject(SharedPreferenceUtil.getString(Constants.ALL_DETAILS_BY_ID + mainObject.optString("customerId"), ""));
            JSONArray allTransactionArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.ALL_TRANSACTION, "") + "]");

            for (int i = 0; i < allTransactionArray.length(); i++) {
                JSONObject allTransactionObject = allTransactionArray.optJSONObject(i);

                if (allTransactionObject.has("billNo") && allTransactionObject.optString("billNo").equalsIgnoreCase(jsonObject.optString("billNo"))) {
                    allTransactionArray.put(i, jsonObject);
                }
            }
            for (int i = 0; i < billArray.length(); i++) {
                JSONObject billObject = billArray.optJSONObject(i);

                if (billObject.optString("billNo").equalsIgnoreCase(jsonObject.optString("billNo"))) {
                    billArray.put(i, jsonObject);
                    break;
                }
            }

            for (int i = 0; i < productNameArray.length(); i++) {
                JSONObject productNameObject = productNameArray.optJSONObject(i);

                productNameObject.put("itemQuantity", SharedPreferenceUtil.getString(productNameObject.optString("itemName") + productNameObject.optString("itemId"), ""));

            }

            double grandTotal = Float.valueOf(allDetailById.optString("grandTotal")) + Float.valueOf(tvInvoiceTotal.getText().toString().trim()) - Double.valueOf(mainObject.optString("billTotal"));
            JSONArray customerArrayTotal = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, "") + "]");

            for (int i = 0; i < customerArrayTotal.length(); i++) {
                JSONObject customerObjectTotal = customerArrayTotal.optJSONObject(i);

                if (customerObjectTotal.optString("customerId").equalsIgnoreCase(mainObject.optString("customerId"))) {
                    customerObjectTotal.put("grandTotal", grandTotal + "");
                    allDetailById.optJSONArray("profile").optJSONObject(0).put("grandTotal", grandTotal + "");
                    break;
                }
            }
            JSONArray newDataFill = allDetailById.optJSONArray("data");

            for (int i = 0; i < newDataFill.length(); i++) {
                JSONObject newDataObject = newDataFill.optJSONObject(i);

                if (newDataObject.optString("billNo").equalsIgnoreCase(jsonObject.optString("billNo"))) {
                    newDataFill.put(i, jsonObject);
                    break;
                }
            }

            JSONObject addToMainObject = new JSONObject();
            addToMainObject.put("profile", allDetailById.optJSONArray("profile"));
            addToMainObject.put("data", newDataFill);
            addToMainObject.put("grandTotal", grandTotal + "");

            SharedPreferenceUtil.putValue(Constants.STOCK_DATA_SAVE, productNameArray.toString().substring(1, productNameArray.toString().length() - 1));
            SharedPreferenceUtil.putValue(Constants.SAVE_INVOICE_BY_DATE + tvInvoiceDate.getText().toString().trim(), total + "");
            SharedPreferenceUtil.putValue(Constants.BILL_DATA_SAVE, billArray.toString().substring(1, billArray.toString().length() - 1));
            SharedPreferenceUtil.putValue(Constants.ALL_TRANSACTION, allTransactionArray.toString().substring(1, allTransactionArray.toString().length() - 1));
            SharedPreferenceUtil.putValue(Constants.CUSTOMER_DATA_SAVE, customerArrayTotal.toString().substring(1, customerArrayTotal.toString().length() - 1));
            SharedPreferenceUtil.putValue(Constants.ALL_DETAILS_BY_ID + mainObject.optString("customerId"), addToMainObject.toString());
            SharedPreferenceUtil.save();

            if (llInvoicePayment.getVisibility() == View.VISIBLE && editInvoiceByPayment && mainObject.optDouble("billAddPayment") != Double.valueOf(etInvoicePayment.getText().toString().trim()))
                editDepositByPayment();

            else {
                if (grandTotal < 0)
                    CommonUtils.addDeposit("Invoice", CommonUtils.dateFormat(System.currentTimeMillis()),
                            atvInvoiceCustomerName.getText().toString(),
                            mainObject.optString("customerId"),
                            etInvoicePayment.getText().toString(),
                            jsonObject.optString("billNo"), "Payment Deposit", CommonUtils.removeSymbols(this, addToMainObject.optString("grandTotal")), "-");
                else if (grandTotal == 0)
                    CommonUtils.addDeposit("Invoice", CommonUtils.dateFormat(System.currentTimeMillis()),
                            atvInvoiceCustomerName.getText().toString(),
                            mainObject.optString("customerId"),
                            etInvoicePayment.getText().toString(),
                            jsonObject.optString("billNo"), "Payment Complete", CommonUtils.removeSymbols(this, addToMainObject.optString("grandTotal")), "");
                else
                    CommonUtils.addDeposit("Invoice", CommonUtils.dateFormat(System.currentTimeMillis()),
                            atvInvoiceCustomerName.getText().toString(),
                            mainObject.optString("customerId"),
                            etInvoicePayment.getText().toString(),
                            jsonObject.optString("billNo"), "Payment Credit", CommonUtils.removeSymbols(this, addToMainObject.optString("grandTotal")), "");

            }

            FragmentInvoice.onCreateDataSet(this);
            FragmentPayment.onCreateDataSet(this);

            finish();
            Toast.makeText(this, "Invoice Updated Successfully", Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void checkSubmitCondition() {
        CommonUtils.closeKeyboard(this);

        if (getIntent().hasExtra("editData")) {
            if (TextUtils.isEmpty(atvInvoiceCustomerName.getText().toString().trim()))
                Toast.makeText(this, "Select Customer Name", Toast.LENGTH_SHORT).show();
            else if (!TextUtils.isEmpty(etInvoiceDiscount.getText().toString().trim()) && Double.valueOf(etInvoiceDiscount.getText().toString().trim()) > 100)
                Toast.makeText(this, "Invoice Discount Isn't Correct", Toast.LENGTH_SHORT).show();
            else {
                if (arrayProductList.length() > 0) {
                    if (llInvoicePayment.getVisibility() == View.VISIBLE)
                        if (etInvoicePayment.getText().toString().trim().equalsIgnoreCase(""))
                            Toast.makeText(this, "Payment can't be null at Update", Toast.LENGTH_SHORT).show();
                        else if (!mainObject.optString("billAddPayment").equalsIgnoreCase("") && mainObject.optDouble("billAddPayment") >= 0) {
                            CommonUtils.showProgressDialog(this, "Updating Invoice...");

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    editInvoice(true);
                                }
                            }, 1000);
                        } else {
                            CommonUtils.showProgressDialog(this, "Updating Invoice...");

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    editInvoice(false);
                                }
                            }, 1000);
                        }
                    else {
                        CommonUtils.showProgressDialog(this, "Updating Invoice...");

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                editInvoice(false);
                            }
                        }, 1000);
                    }
                } else
                    Toast.makeText(this, "No product Collected", Toast.LENGTH_SHORT).show();
            }

        } else {
            if (TextUtils.isEmpty(atvInvoiceCustomerName.getText().toString().trim()))
                Toast.makeText(this, "Select Customer Name", Toast.LENGTH_SHORT).show();

            else if (arrayProductList.length() > 0) {

                CommonUtils.showProgressDialog(this, "Creating Invoice...");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (selectedCustomerId.equalsIgnoreCase(""))
                            addCustomer();
                        else
                            addInvoice((String) atvInvoiceCustomerName.getTag());
                    }
                }, 1000);

            } else
                Toast.makeText(this, "No product Collected", Toast.LENGTH_SHORT).show();
        }
    }

    public void calculateProductTotal(boolean checkPaymentFocus) {

        double invoiceTotal = 0.00;
        double invoiceDiscount = 0.00;
        double invoiceSubTotal = 0.00;
        double calcDiscount = 0.00;
        double invoiceShippingPrice = 0.00;
        double invoiceGrandTotal = 0.00;
        double invoicePaid = 0.00;
        double invoiceDue = 0.00;
        double amountStatus = 0.00;
        double invoiceFinalTotal = 0.00;

        if (!TextUtils.isEmpty(tvInvoiceTotal.getText().toString().trim()))
            invoiceTotal = grandTotal;

        if (llInvoiceDiscount.getVisibility() == View.VISIBLE && !TextUtils.isEmpty(etInvoiceDiscount.getText().toString().trim()))
            invoiceDiscount = Double.valueOf(etInvoiceDiscount.getText().toString().trim());

        if (llInvoiceShipping.getVisibility() == View.VISIBLE && !TextUtils.isEmpty(etInvoiceShipping.getText().toString().trim()))
            invoiceShippingPrice = Double.valueOf(etInvoiceShipping.getText().toString().trim());

        if (llInvoicePayment.getVisibility() == View.VISIBLE && !TextUtils.isEmpty(etInvoicePayment.getText().toString().trim()))
            invoicePaid = Double.valueOf(etInvoicePayment.getText().toString().trim());

        calcDiscount = (invoiceTotal * invoiceDiscount) / 100;
        invoiceSubTotal = invoiceTotal - calcDiscount;
        invoiceGrandTotal = invoiceSubTotal + invoiceShippingPrice;

        transactionStatus();

        if (tvInvoicePaymentStatus.getVisibility() != View.VISIBLE)
            amountStatus = 0;
        else if (!tvInvoiceAmountStatus.getText().toString().trim().equalsIgnoreCase("")) {

            if (transactionStatusAmount == 0)
                amountStatus = 0;
            else if (transactionStatusAmount < 0)
                amountStatus = 0 - Double.valueOf(tvInvoiceAmountStatus.getText().toString().trim());
            else
                amountStatus = Double.valueOf(tvInvoiceAmountStatus.getText().toString().trim());
        }

        invoiceFinalTotal = invoiceGrandTotal + amountStatus;
        invoiceDue = invoiceFinalTotal - invoicePaid;

        tvInvoiceTotalToCalc.setText(CommonUtils.getDecimal(invoiceTotal));
        tvInvoiceDiscount.setText(CommonUtils.getDecimal(calcDiscount));
        tvInvoiceSubTotal.setText(CommonUtils.getDecimal(invoiceSubTotal));
        tvInvoiceShippingCalc.setText(CommonUtils.getDecimal(invoiceShippingPrice));
        tvInvoiceGrandTotal.setText(CommonUtils.getDecimal(invoiceGrandTotal));
        tvInvoiceAmountPaidCalc.setText(CommonUtils.getDecimal(invoicePaid));
        tvInvoiceDue.setText(CommonUtils.removeSymbols(this, CommonUtils.getDecimal(invoiceDue)));
        tvInvoiceTotal.setText(CommonUtils.getDecimal(invoiceGrandTotal));
        tvInvoiceFinalTotal.setText(CommonUtils.removeSymbols(this, CommonUtils.getDecimal(invoiceFinalTotal)));

        if (invoiceDue < 0)
            tvInvoiceDueSign.setText("-");
        else
            tvInvoiceDueSign.setText("");

        if (invoiceFinalTotal < 0)
            tvInvoiceFinalTotalSign.setText("-");
        else
            tvInvoiceFinalTotalSign.setText("");

        if (!getIntent().hasExtra("editData") && !getIntent().hasExtra("onlyShow") && checkPaymentFocus)
            etInvoicePayment.setText(tvInvoiceFinalTotal.getText().toString().trim());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvInvoiceAddProduct:
                try {
                    JSONArray customerNameArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.STOCK_DATA_SAVE, "") + "]");

                    if (customerNameArray.length() > 0)
                        startActivityForResult(new Intent(this, AddProductToInvoice.class), reqToAdd);
                    else
                        Toast.makeText(this, "No Product Available", Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ivInvoiceCustomerName:
                if (customerNameToPicker.size() > 0)
                    CommonUtils.picker(this, customerNameToPicker, "Customer Name", atvInvoiceCustomerName, true, this.getLocalClassName());
                else
                    Toast.makeText(this, "No Customer Available", Toast.LENGTH_SHORT).show();
                break;
            case R.id.cbInvoiceDiscount:
                CommonUtils.closeKeyboard(this);
                if (llInvoiceDiscount.getVisibility() == View.VISIBLE) {
                    llInvoiceDiscount.setVisibility(View.GONE);
                    etInvoiceDiscount.setText("");

                } else {
                    llInvoiceDiscount.setVisibility(View.VISIBLE);
                    etInvoiceDiscount.setText("");
                }
                calculateProductTotal(true);
                break;
            case R.id.cbInvoiceShipping:
                CommonUtils.closeKeyboard(this);
                if (llInvoiceShipping.getVisibility() == View.VISIBLE) {
                    llInvoiceShipping.setVisibility(View.GONE);
                    etInvoiceShipping.setText("");
                    etInvoiceDiscount.setImeOptions(EditorInfo.IME_ACTION_DONE);
                } else {
                    llInvoiceShipping.setVisibility(View.VISIBLE);
                    etInvoiceShipping.setText("");
                    etInvoiceDiscount.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                }
                calculateProductTotal(true);
                break;
            case R.id.cbInvoiceAddPayment:
                CommonUtils.closeKeyboard(this);
                if (llInvoicePayment.getVisibility() == View.VISIBLE) {
                    llInvoicePayment.setVisibility(View.GONE);
                    etInvoicePayment.setText("");

                    if (llInvoiceShipping.getVisibility() == View.VISIBLE)
                        etInvoiceDiscount.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    else
                        etInvoiceDiscount.setImeOptions(EditorInfo.IME_ACTION_DONE);

                    etInvoiceShipping.setImeOptions(EditorInfo.IME_ACTION_DONE);

                } else {
                    llInvoicePayment.setVisibility(View.VISIBLE);

                    if (!getIntent().hasExtra("onlyShow") && !getIntent().hasExtra("editData"))
                        etInvoicePayment.setText(tvInvoiceDue.getText().toString());

                    if (getIntent().hasExtra("editData") && !mainObject.optBoolean("billPaid"))
                        etInvoicePayment.setText(tvInvoiceDue.getText().toString());

                    etInvoiceDiscount.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    etInvoiceShipping.setImeOptions(EditorInfo.IME_ACTION_NEXT);

                }
                break;
            case R.id.atvInvoiceCustomerName:
                startActivity(new Intent(this, CustomerAccount.class)
                        .putExtra("customerPosition", atvInvoiceCustomerName.getTag() + "")
                        .putExtra("invoice", "invoice"));
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (view.getId()) {
            case R.id.atvInvoiceCustomerName:

                for (int i = 0; i < customerNameArray.length(); i++) {
                    JSONObject newJsonObject = customerNameArray.optJSONObject(i);

                    if (newJsonObject.optString("customerName").trim().toUpperCase().equalsIgnoreCase(atvInvoiceCustomerName.getText().toString().toUpperCase().trim())) {
                        selectedCustomerId = newJsonObject.optString("customerId");

                        break;
                    }
                }
                atvInvoiceCustomerName.setTag(selectedCustomerId);

                break;
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            switch (reqCode) {
                case reqToAdd:
                    try {
                        arrayProductList.put(new JSONObject(data.getStringExtra("jsonObject")));
                        fillAdapter();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case reqToEdit:
                    try {
                        arrayProductList.put(positionToEdit, new JSONObject(data.getStringExtra("jsonObject")));
                        fillAdapter();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

        super.onActivityResult(reqCode, resultCode, data);
    }

    public void setVisibility(boolean checkVisible) {
        menuShareImage.setVisible(checkVisible);
        menuPrintBill.setVisible(checkVisible);
        menuShareData.setVisible(checkVisible);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bill_share, menu);

        createInvoice = menu.findItem(R.id.createInvoice);
        menuShareImage = menu.findItem(R.id.shareImage);
        menuPrintBill = menu.findItem(R.id.printBill);
        menuShareData = menu.findItem(R.id.shareData);

        if (getIntent().hasExtra("onlyShow")) {
            setVisibility(true);
            createInvoice.setVisible(false);
        } else if (getIntent().hasExtra("editData")) {
            setVisibility(false);
            createInvoice.setTitle("Update Invoice");
        } else {
            setVisibility(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                CommonUtils.closeKeyboard(this);
                finish();
                break;
            case R.id.shareImage:
                startActivity(new Intent(this, ShareBill.class).putExtra("data", mainObject.toString()).putExtra("shareImage", true));
                break;
            case R.id.printBill:
                CommonUtils.doPrint(this, mainObject, false);
                break;
            case R.id.shareData:
                try {
                    CommonUtils.shareData(this, new JSONObject(SharedPreferenceUtil.getString(Constants.ALL_DETAILS_BY_ID + atvInvoiceCustomerName.getTag(), "")), mainObject.optString("billNo"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.createInvoice:
                checkSubmitCondition();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class AdapterInvoice extends BaseAdapter {

        private LayoutInflater inflater = null;
        JSONArray data;

        AdapterInvoice(JSONArray data) {
            this.data = data;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return data.length();
        }

        public Object getItem(int position) {
            return data.optJSONObject(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            final JSONObject jsonObject = data.optJSONObject((data.length() - 1) - position);

            if (convertView == null)
                vi = inflater.inflate(R.layout.element_add_product_to_invoice, null);

            ImageView ivElementBillingCross = (ImageView) vi.findViewById(R.id.ivElementBillingCross);
            ImageView ivElementRemainderEdit = (ImageView) vi.findViewById(R.id.ivElementRemainderEdit);
            TextView etElementBillingName = (TextView) vi.findViewById(R.id.etElementBillingName);
            TextView etElementBillingQuantity = (TextView) vi.findViewById(R.id.etElementBillingQuantity);
            TextView tvElementBillingQuantityPrice = (TextView) vi.findViewById(R.id.tvElementBillingQuantityPrice);
            TextView tvElementBillingTotal = (TextView) vi.findViewById(R.id.tvElementBillingTotal);
            TextView tvElementBillingTax = (TextView) vi.findViewById(R.id.tvElementBillingTax);
            TextView tvElementBillingDiscount = (TextView) vi.findViewById(R.id.tvElementBillingDiscount);

            if (getIntent().hasExtra("onlyShow")) {
                ivElementBillingCross.setVisibility(View.GONE);
                ivElementRemainderEdit.setVisibility(View.GONE);
            }

            etElementBillingName.setText(jsonObject.optString("itemName"));
            etElementBillingName.setTag(jsonObject.optString("itemId"));
            etElementBillingQuantity.setText("Qnty. " + jsonObject.optString("itemQuantity"));
            tvElementBillingTotal.setText("Q.T. " + getResources().getString(R.string.currency_india) + " " + jsonObject.optString("itemTotal"));
            tvElementBillingQuantityPrice.setText("Q.P. " + getResources().getString(R.string.currency_india) + " " + jsonObject.optString("itemPrice"));
            tvElementBillingTax.setText("GST " + CommonUtils.getDecimal(jsonObject.optDouble("itemTax")) + " %");
            tvElementBillingDiscount.setText("Discount " + CommonUtils.getDecimal(jsonObject.optDouble("itemDiscount")) + " %");

            if (!positionArray.contains(position)) {
                positionArray.add(position);
                grandTotal = grandTotal + jsonObject.optDouble("itemTotal");

                if (position == data.length() - 1) {
                    tvInvoiceTotal.setText(CommonUtils.getDecimal(grandTotal));

                    if (!getIntent().hasExtra("onlyShow") && !getIntent().hasExtra("editData"))
                        etInvoicePayment.setText(CommonUtils.getDecimal(grandTotal));

                    calculateProductTotal(true);
                }
            }

            ivElementBillingCross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    double oldStock = Double.valueOf(SharedPreferenceUtil.getString(jsonObject.optString("itemName") + jsonObject.optString("itemId"), ""));
                    double subStock = Double.valueOf(jsonObject.optString("itemQuantity"));

                    SharedPreferenceUtil.putValue(jsonObject.optString("itemName") + jsonObject.optString("itemId"), (oldStock + subStock) + "");
                    SharedPreferenceUtil.save();

                    arrayProductList = CommonUtils.remove((arrayProductList.length() - 1) - position, arrayProductList);
                    fillAdapter();

                }
            });

            ivElementRemainderEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    positionToEdit = (data.length() - 1) - position;
                    startActivityForResult(new Intent(Invoice.this, AddProductToInvoice.class).putExtra("editData", jsonObject.toString()), reqToEdit);
                }
            });

            vi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Invoice.this, AddProductToInvoice.class).putExtra("onlyShow", jsonObject.toString()));
                }
            });

            return vi;

        }

    }
}