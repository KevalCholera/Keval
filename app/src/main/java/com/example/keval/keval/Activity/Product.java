package com.example.keval.keval.Activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keval.keval.Fragment.FragmentProduct;
import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;
import com.example.keval.keval.Utils.Constants;
import com.mpt.storage.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Product extends AppCompatActivity implements TextWatcher {

    EditText etElementEditStockName, etElementEditStockMainQuantity, etElementEditStockQuantityAddSub,
            etElementEditStockPrice, etElementEditStockTax, etElementEditStockQuantityForCreate;
    TextView tvElementEditStockSubmit, tvElementEditStockQuantityAddSub,
            etElementEditStockQuantityTotal, tvElementEditStockQuantityEqual;
    MenuItem reduceStock, addStock;
    boolean addMenuVisible = true;
    LinearLayout llEditStockQuantity, llEditStockPrice, llEditStockTax, llEditStockQuantityForCreate, llEditStockEdit;
    String itemName = "";
    private String taxText = "";
    private JSONArray jsonData = new JSONArray();
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etElementEditStockName = (EditText) findViewById(R.id.etElementEditStockName);
        etElementEditStockMainQuantity = (EditText) findViewById(R.id.etElementEditStockMainQuantity);
        etElementEditStockTax = (EditText) findViewById(R.id.etElementEditStockTax);
        etElementEditStockQuantityForCreate = (EditText) findViewById(R.id.etElementEditStockQuantityForCreate);
        tvElementEditStockSubmit = (TextView) findViewById(R.id.tvElementEditStockSubmit);
        tvElementEditStockQuantityAddSub = (TextView) findViewById(R.id.tvElementEditStockQuantityAddSub);
        tvElementEditStockQuantityEqual = (TextView) findViewById(R.id.tvElementEditStockQuantityEqual);
        etElementEditStockQuantityTotal = (TextView) findViewById(R.id.etElementEditStockQuantityTotal);
        etElementEditStockQuantityAddSub = (EditText) findViewById(R.id.etElementEditStockQuantityAddSub);
        etElementEditStockPrice = (EditText) findViewById(R.id.etElementEditStockPrice);
        llEditStockQuantity = (LinearLayout) findViewById(R.id.llEditStockQuantity);
        llEditStockPrice = (LinearLayout) findViewById(R.id.llEditStockPrice);
        llEditStockTax = (LinearLayout) findViewById(R.id.llEditStockTax);
        llEditStockQuantityForCreate = (LinearLayout) findViewById(R.id.llEditStockQuantityForCreate);
        llEditStockEdit = (LinearLayout) findViewById(R.id.llEditStockEdit);

        etElementEditStockMainQuantity.addTextChangedListener(this);
        etElementEditStockQuantityAddSub.addTextChangedListener(this);

        try {
            jsonData = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.STOCK_DATA_SAVE, "") + "]");
            position = jsonData.length() - (getIntent().getIntExtra("position", 0) + 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (getIntent().hasExtra("position")) {
            calcHeightWidth();
            llEditStockEdit.setVisibility(View.VISIBLE);

            JSONObject jsonObject = jsonData.optJSONObject(position);

            etElementEditStockName.setText(jsonObject.optString("itemName"));
            etElementEditStockName.setTag(jsonObject.optString("itemId"));
            etElementEditStockTax.setText(CommonUtils.getDecimal(jsonObject.optDouble("itemTax")));
            getSupportActionBar().setTitle(jsonObject.optString("itemName"));

            itemName = jsonObject.optString("itemName").trim();

            if (jsonObject.optString("itemQuantity").equalsIgnoreCase(""))
                etElementEditStockMainQuantity.setText("0");
            else
                etElementEditStockMainQuantity.setText(jsonObject.optInt("itemQuantity") + "");

            if (jsonObject.optString("itemPrice").equalsIgnoreCase(""))
                etElementEditStockPrice.setText("0");
            else
                etElementEditStockPrice.setText(CommonUtils.getDecimal(jsonObject.optDouble("itemPrice")));

            if (getIntent().hasExtra("onlyShow")) {
                calcHeightWidthForAddProduct();
                etElementEditStockName.setEnabled(false);
                llEditStockEdit.setVisibility(View.GONE);
                llEditStockQuantityForCreate.setVisibility(View.VISIBLE);
                etElementEditStockQuantityForCreate.setEnabled(false);

                if (jsonObject.optString("itemQuantity").equalsIgnoreCase(""))
                    etElementEditStockQuantityForCreate.setText("0");
                else
                    etElementEditStockQuantityForCreate.setText(jsonObject.optInt("itemQuantity") + "");

                etElementEditStockTax.setEnabled(false);
                etElementEditStockPrice.setEnabled(false);
                tvElementEditStockSubmit.setVisibility(View.GONE);
            }

        } else {
            llEditStockQuantityForCreate.setVisibility(View.VISIBLE);
            etElementEditStockMainQuantity.setEnabled(true);

            getSupportActionBar().setTitle("Add Product");
            tvElementEditStockSubmit.setText("Add Product");
            calcHeightWidthForAddProduct();
        }

        tvElementEditStockSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.closeKeyboard(Product.this);
                if (TextUtils.isEmpty(etElementEditStockName.getText().toString().trim()))
                    Toast.makeText(Product.this, "Enter Product Name", Toast.LENGTH_SHORT).show();
                else if (getIntent().hasExtra("position") && TextUtils.isEmpty(etElementEditStockMainQuantity.getText().toString().trim()))
                    Toast.makeText(Product.this, "Enter Quantity", Toast.LENGTH_SHORT).show();
                else if (!getIntent().hasExtra("position") && TextUtils.isEmpty(etElementEditStockQuantityForCreate.getText().toString().trim()))
                    Toast.makeText(Product.this, "Enter Quantity", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etElementEditStockPrice.getText().toString().trim()))
                    Toast.makeText(Product.this, "Enter Price", Toast.LENGTH_SHORT).show();
                else if (getIntent().hasExtra("position") && Double.valueOf(etElementEditStockQuantityTotal.getText().toString().trim()) < 0)
                    Toast.makeText(Product.this, "Quantity can't be else than zero", Toast.LENGTH_SHORT).show();
                else if (!TextUtils.isEmpty(etElementEditStockTax.getText().toString().trim()) && Double.valueOf(etElementEditStockTax.getText().toString().trim()) > 100)
                    Toast.makeText(Product.this, "Product GST Isn't Correct", Toast.LENGTH_SHORT).show();
                else
                    checkItemNameExist(jsonData, position);
            }
        });

        etElementEditStockTax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!TextUtils.isEmpty(etElementEditStockTax.getText().toString().trim())) {
                    if (Double.valueOf(etElementEditStockTax.getText().toString().trim()) <= 100) {
                        taxText = etElementEditStockTax.getText().toString().trim();
                    } else {
                        etElementEditStockTax.setText(taxText);

                        Toast.makeText(Product.this, "GST can't be greater than 100", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void calcHeightWidth() {

        llEditStockQuantity.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    //noinspection deprecation
                    llEditStockQuantity.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    llEditStockQuantity.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                LinearLayout.LayoutParams layoutTotal = new LinearLayout.LayoutParams(llEditStockQuantity.getMeasuredWidth(), llEditStockQuantity.getMeasuredHeight());

                layoutTotal.setMargins(0, 10, 0, 0);
                llEditStockPrice.setLayoutParams(layoutTotal);
                llEditStockTax.setLayoutParams(layoutTotal);
            }
        });
    }

    public void calcHeightWidthForAddProduct() {

        llEditStockQuantityForCreate.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    //noinspection deprecation
                    llEditStockQuantityForCreate.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    llEditStockQuantityForCreate.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                LinearLayout.LayoutParams layoutTotal;

                layoutTotal = new LinearLayout.LayoutParams(llEditStockQuantityForCreate.getMeasuredWidth(), llEditStockQuantityForCreate.getMeasuredHeight());

                layoutTotal.setMargins(0, 10, 0, 0);
                llEditStockPrice.setLayoutParams(layoutTotal);
                llEditStockTax.setLayoutParams(layoutTotal);
            }
        });
    }

    public void checkItemNameExist(final JSONArray jsonData, final int position) {

        if (getIntent().hasExtra("position"))
            if (!itemName.trim().equalsIgnoreCase(etElementEditStockName.getText().toString().trim())) {
                for (int i = 0; i < jsonData.length(); i++) {
                    JSONObject jsonObject = jsonData.optJSONObject(i);

                    if (etElementEditStockName.getText().toString().trim().equalsIgnoreCase(jsonObject.optString("itemName").trim())) {
                        Toast.makeText(this, "Product name already exist", Toast.LENGTH_SHORT).show();
                        break;
                    } else {
                        if (i == jsonData.length() - 1) {
                            CommonUtils.showProgressDialog(this, "Updating Product...");

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    editProduct(jsonData, position);
                                }
                            }, 1000);
                        }
                    }
                }
            } else {
                CommonUtils.showProgressDialog(this, "Updating Product...");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        editProduct(jsonData, position);
                    }
                }, 1000);
            }

        else {
            if (jsonData.length() > 0)
                for (int i = 0; i < jsonData.length(); i++) {
                    JSONObject jsonObject = jsonData.optJSONObject(i);

                    if (etElementEditStockName.getText().toString().trim().equalsIgnoreCase(jsonObject.optString("itemName").trim())) {
                        Toast.makeText(this, "Product name already exist", Toast.LENGTH_SHORT).show();
                        break;
                    } else {
                        if (i == jsonData.length() - 1) {
                            CommonUtils.showProgressDialog(this, "Adding Product...");

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    addProduct();
                                }
                            }, 1000);
                        }
                    }
                }
            else {
                CommonUtils.showProgressDialog(this, "Adding Product...");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addProduct();

                    }
                }, 1000);
            }
        }
    }

    public void addProduct() {
        CommonUtils.closeKeyboard(this);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("itemName", etElementEditStockName.getText().toString().trim());
            jsonObject.put("itemQuantity", CommonUtils.getDecimal(Double.valueOf(etElementEditStockQuantityForCreate.getText().toString().trim())));
            jsonObject.put("itemPrice", CommonUtils.getDecimal(Double.valueOf(etElementEditStockPrice.getText().toString().trim())));

            if (!etElementEditStockTax.getText().toString().trim().equalsIgnoreCase(""))
                jsonObject.put("itemTax", CommonUtils.getDecimal(Double.valueOf(etElementEditStockTax.getText().toString().trim())));
            else
                jsonObject.put("itemTax", CommonUtils.getDecimal(Double.valueOf("0.00")));

            jsonObject.put("itemId", CommonUtils.indexId(Constants.STOCK_ITEM_ID));

            SharedPreferenceUtil.putValue(Constants.STOCK_DATA_SAVE, CommonUtils.addDataOldThenNew(Constants.STOCK_DATA_SAVE, jsonObject.toString()));
            SharedPreferenceUtil.save();

            Toast.makeText(this, "Product Added Successfully", Toast.LENGTH_SHORT).show();

            FragmentProduct.onCreateDataSet(this);
            finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void editProduct(JSONArray jsonData, int position) {

        try {
            JSONObject jsonObject1 = new JSONObject();

            jsonObject1.put("itemName", etElementEditStockName.getText().toString().trim());
            jsonObject1.put("itemId", etElementEditStockName.getTag());
            jsonObject1.put("itemQuantity", CommonUtils.getDecimal(Double.valueOf(etElementEditStockQuantityTotal.getText().toString().trim())));
            jsonObject1.put("itemPrice", CommonUtils.getDecimal(Double.valueOf(etElementEditStockPrice.getText().toString().trim())));

            if (!etElementEditStockTax.getText().toString().trim().equalsIgnoreCase(""))
                jsonObject1.put("itemTax", CommonUtils.getDecimal(Double.valueOf(etElementEditStockTax.getText().toString().trim())));
            else
                jsonObject1.put("itemTax", CommonUtils.getDecimal(Double.valueOf("0.00")));

            jsonData.put(position, jsonObject1);

            JSONArray allTransactionArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.ALL_TRANSACTION, "") + "]");
            JSONArray billDataSaveArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.BILL_DATA_SAVE, "") + "]");
            JSONArray customerDataSaveArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, "") + "]");

            for (int i = 0; i < allTransactionArray.length(); i++) {
                JSONObject allTransactionObject = allTransactionArray.optJSONObject(i);

                if (allTransactionObject.has("billNo")) {
                    JSONArray allTransactionInfoArray = allTransactionObject.optJSONArray("info");

                    for (int j = 0; j < allTransactionInfoArray.length(); j++) {
                        JSONObject allTransactionInfoObject = allTransactionInfoArray.optJSONObject(j);

                        if (allTransactionInfoObject.optString("itemName").equalsIgnoreCase(itemName))
                            allTransactionInfoObject.put("itemName", etElementEditStockName.getText().toString().trim());
                    }
                }
            }

            for (int k = 0; k < billDataSaveArray.length(); k++) {
                JSONObject billDataSaveObject = billDataSaveArray.optJSONObject(k);
                JSONArray billDataSaveInfoArray = billDataSaveObject.optJSONArray("info");

                for (int j = 0; j < billDataSaveInfoArray.length(); j++) {
                    JSONObject billDataSaveInfoObject = billDataSaveInfoArray.optJSONObject(j);

                    if (billDataSaveInfoObject.optString("itemName").equalsIgnoreCase(itemName)) {
                        billDataSaveInfoObject.put("itemName", etElementEditStockName.getText().toString().trim());

                    }
                }
            }

            for (int i = 0; i < customerDataSaveArray.length(); i++) {
                JSONArray allDetailsByIdArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.ALL_DETAILS_BY_ID + (i + 1), "") + "]");

                for (int j = 0; j < allDetailsByIdArray.length(); j++) {
                    JSONObject allDetailsByIdObject = allDetailsByIdArray.optJSONObject(j);
                    JSONArray allDetailsByIdDataArray = allDetailsByIdObject.optJSONArray("data");

                    for (int k = 0; k < allDetailsByIdDataArray.length(); k++) {
                        JSONObject allDetailsByIdDataObject = allDetailsByIdDataArray.optJSONObject(k);

                        if (allDetailsByIdDataObject.has("billNo")) {
                            JSONArray allDetailsByIdInfoArray = allDetailsByIdDataObject.optJSONArray("info");

                            for (int l = 0; l < allDetailsByIdInfoArray.length(); l++) {
                                JSONObject allDetailsByIdInfoObject = allDetailsByIdInfoArray.optJSONObject(l);

                                if (allDetailsByIdInfoObject.optString("itemName").equalsIgnoreCase(itemName)) {
                                    allDetailsByIdInfoObject.put("itemName", etElementEditStockName.getText().toString().trim());

                                }
                            }
                        }
                    }
                }
                SharedPreferenceUtil.putValue(Constants.ALL_DETAILS_BY_ID + (i + 1), allDetailsByIdArray.toString().substring(1, allDetailsByIdArray.toString().length() - 1));
            }

            SharedPreferenceUtil.putValue(Constants.BILL_DATA_SAVE, billDataSaveArray.toString().substring(1, billDataSaveArray.toString().length() - 1));
            SharedPreferenceUtil.putValue(Constants.ALL_TRANSACTION, allTransactionArray.toString().substring(1, allTransactionArray.toString().length() - 1));
            SharedPreferenceUtil.putValue(Constants.STOCK_DATA_SAVE, jsonData.toString().substring(1, jsonData.toString().length() - 1));
            SharedPreferenceUtil.save();

            Toast.makeText(this, "Product Updated Successfully", Toast.LENGTH_SHORT).show();

            FragmentProduct.onCreateDataSet(this);
            finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void calcAdd() {

        double priceQuantity = 0;
        double priceAddQuantity = 0;
        double totalQuantity = 0;

        try {
            if (!etElementEditStockMainQuantity.getText().toString().trim().equalsIgnoreCase(""))
                priceQuantity = Double.valueOf(etElementEditStockMainQuantity.getText().toString().trim());

            if (!etElementEditStockQuantityAddSub.getText().toString().trim().equalsIgnoreCase(""))
                priceAddQuantity = Double.valueOf(etElementEditStockQuantityAddSub.getText().toString().trim());

            totalQuantity = priceQuantity + priceAddQuantity;
            etElementEditStockQuantityTotal.setText((int) totalQuantity + "");

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void calcSub() {

        double priceQuantity = 0;
        double priceSubQuantity = 0;
        double totalQuantity = 0;
        try {

            if (!etElementEditStockMainQuantity.getText().toString().trim().equalsIgnoreCase(""))
                priceQuantity = Double.valueOf(etElementEditStockMainQuantity.getText().toString().trim());

            if (!etElementEditStockQuantityAddSub.getText().toString().trim().equalsIgnoreCase(""))
                priceSubQuantity = Double.valueOf(etElementEditStockQuantityAddSub.getText().toString().trim());

            totalQuantity = priceQuantity - priceSubQuantity;
            etElementEditStockQuantityTotal.setText((int) totalQuantity + "");

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (addMenuVisible)
            calcAdd();
        else
            calcSub();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_stock, menu);

        reduceStock = menu.findItem(R.id.reduceStock);
        addStock = menu.findItem(R.id.addStock);

        if (getIntent().hasExtra("onlyShow") || !getIntent().hasExtra("position")) {
            reduceStock.setVisible(false);
            addStock.setVisible(false);
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
            case R.id.reduceStock:
                reduceStock.setVisible(false);
                addStock.setVisible(true);
                addMenuVisible = false;
                tvElementEditStockQuantityAddSub.setText("-");
                calcHeightWidth();
                calcSub();
                break;
            case R.id.addStock:
                reduceStock.setVisible(true);
                addStock.setVisible(false);
                addMenuVisible = true;
                tvElementEditStockQuantityAddSub.setText("+");
                calcHeightWidth();
                calcAdd();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}