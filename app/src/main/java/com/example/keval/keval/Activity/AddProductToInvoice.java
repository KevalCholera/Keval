package com.example.keval.keval.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;
import com.example.keval.keval.Utils.Constants;
import com.mpt.storage.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddProductToInvoice extends AppCompatActivity implements TextWatcher, View.OnClickListener {

    static JSONArray customerNameArray = new JSONArray();
    ArrayList<String> filteredProduct = new ArrayList<>();

    String taxText = "";
    String discountText = "";
    static String productSelected = "";
    public static boolean checkProductSelect = true;

    static AutoCompleteTextView atvAddProductToInvoiceProductName;
    ImageView ivAddProductToInvoiceProductName;
    EditText etAddProductToInvoiceProductQuantity;
    EditText etAddProductToInvoiceProductDiscount;
    static TextView etAddProductToInvoiceProductPrice, etAddProductToInvoiceProductTax;
    TextView tvAdProductToInvoiceQuantity, tvAdProductToInvoicePrice, tvAddProductToInvoiceProductTotalToCalc,
            tvAdProductToInvoiceDiscountOnProductTotal, tvAdProductToInvoiceSubTotal, tvAdProductToInvoiceTaxOnSubTotal,
            tvAdProductToInvoiceGrandTotal, tvAddProductToInvoiceProductType, tvAddProductToInvoiceProductTotal;
    View vAddProductToInvoiceCustomerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_to_invoice);
        SharedPreferenceUtil.init(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        atvAddProductToInvoiceProductName = (AutoCompleteTextView) findViewById(R.id.atvAddProductToInvoiceProductName);
        etAddProductToInvoiceProductQuantity = (EditText) findViewById(R.id.etAddProductToInvoiceProductQuantity);
        etAddProductToInvoiceProductDiscount = (EditText) findViewById(R.id.etAddProductToInvoiceProductDiscount);
        ivAddProductToInvoiceProductName = (ImageView) findViewById(R.id.ivAddProductToInvoiceProductName);
        etAddProductToInvoiceProductPrice = (TextView) findViewById(R.id.etAddProductToInvoiceProductPrice);
        etAddProductToInvoiceProductTax = (TextView) findViewById(R.id.etAddProductToInvoiceProductTax);
        tvAddProductToInvoiceProductType = (TextView) findViewById(R.id.tvAddProductToInvoiceProductType);
        tvAddProductToInvoiceProductTotal = (TextView) findViewById(R.id.tvAddProductToInvoiceProductTotal);
        tvAdProductToInvoiceQuantity = (TextView) findViewById(R.id.tvAdProductToInvoiceQuantity);
        tvAdProductToInvoicePrice = (TextView) findViewById(R.id.tvAdProductToInvoicePrice);
        tvAddProductToInvoiceProductTotalToCalc = (TextView) findViewById(R.id.tvAddProductToInvoiceProductTotalToCalc);
        tvAdProductToInvoiceDiscountOnProductTotal = (TextView) findViewById(R.id.tvAdProductToInvoiceDiscountOnProductTotal);
        tvAdProductToInvoiceSubTotal = (TextView) findViewById(R.id.tvAdProductToInvoiceSubTotal);
        tvAdProductToInvoiceTaxOnSubTotal = (TextView) findViewById(R.id.tvAdProductToInvoiceTaxOnSubTotal);
        tvAdProductToInvoiceGrandTotal = (TextView) findViewById(R.id.tvAdProductToInvoiceGrandTotal);
        vAddProductToInvoiceCustomerName = findViewById(R.id.vAddProductToInvoiceCustomerName);

        ivAddProductToInvoiceProductName.setOnClickListener(this);
        etAddProductToInvoiceProductQuantity.addTextChangedListener(this);
        etAddProductToInvoiceProductPrice.addTextChangedListener(this);
        etAddProductToInvoiceProductTax.addTextChangedListener(this);
        etAddProductToInvoiceProductDiscount.addTextChangedListener(this);
        atvAddProductToInvoiceProductName.addTextChangedListener(this);

        etAddProductToInvoiceProductPrice.setEnabled(false);
        etAddProductToInvoiceProductTax.setEnabled(false);

        fillProductData();
        fillProduct();
        calculateProductTotal();

        atvAddProductToInvoiceProductName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                setDataByProductName();
            }
        });
    }

    public static void setDataByProductName() {

        for (int i = 0; i < customerNameArray.length(); i++) {
            JSONObject newJsonObject = customerNameArray.optJSONObject(i);

            if (newJsonObject.optString("itemName").trim().toUpperCase().equalsIgnoreCase(atvAddProductToInvoiceProductName.getText().toString().toUpperCase().trim())) {
                atvAddProductToInvoiceProductName.setTag(newJsonObject.optString("itemId"));
                productSelected = newJsonObject.optString("itemId");

                etAddProductToInvoiceProductPrice.setText(newJsonObject.optString("itemPrice"));
                etAddProductToInvoiceProductTax.setText(CommonUtils.getDecimal(newJsonObject.optDouble("itemTax")));

                break;
            }
        }
    }

    public void selectedCustomerName() {

        ArrayList<String> filteredCustomerName = new ArrayList<>();
        productSelected = "";

        for (int i = 0; i < customerNameArray.length(); i++) {
            JSONObject newJsonObject = customerNameArray.optJSONObject(i);
            if (newJsonObject.optString("itemName").trim().toUpperCase().contains(atvAddProductToInvoiceProductName.getText().toString().toUpperCase().trim())) {
                filteredCustomerName.add(newJsonObject.optString("itemName"));
            }
        }

        if (filteredCustomerName.size() > 0) {
            atvAddProductToInvoiceProductName.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredCustomerName));
        }
    }

    public void fillProductData() {

        if (!SharedPreferenceUtil.getString(Constants.STOCK_DATA_SAVE, "").equalsIgnoreCase(""))
            try {
                customerNameArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.STOCK_DATA_SAVE, "") + "]");

                if (getIntent().hasExtra("editData")) {
                    JSONObject editDataObject = new JSONObject(getIntent().getStringExtra("editData"));
                    getSupportActionBar().setTitle(editDataObject.optString("itemName"));

                    atvAddProductToInvoiceProductName.setText(editDataObject.optString("itemName"));
                    atvAddProductToInvoiceProductName.setTag(editDataObject.optString("itemId"));
                    etAddProductToInvoiceProductQuantity.setText(editDataObject.optString("itemQuantity"));
                    etAddProductToInvoiceProductPrice.setText(editDataObject.optString("itemPrice"));
                    tvAddProductToInvoiceProductTotal.setText(editDataObject.optString("itemTotal"));

                    productSelected = editDataObject.optString("itemId");

                    if (!TextUtils.isEmpty(editDataObject.optString("itemTax")))
                        etAddProductToInvoiceProductTax.setText(CommonUtils.getDecimal(editDataObject.optDouble("itemTax")));
                    else
                        etAddProductToInvoiceProductTax.setText("0.00");

                    if (!TextUtils.isEmpty(editDataObject.optString("itemDiscount")))
                        etAddProductToInvoiceProductDiscount.setText(CommonUtils.getDecimal(editDataObject.optDouble("itemDiscount")));
                    else
                        etAddProductToInvoiceProductDiscount.setText("0.00");

                    double oldStock = Double.valueOf(SharedPreferenceUtil.getString(atvAddProductToInvoiceProductName.getText().toString().trim() + (String) atvAddProductToInvoiceProductName.getTag(), ""));
                    double subStock = Double.valueOf(etAddProductToInvoiceProductQuantity.getText().toString().trim());

                    SharedPreferenceUtil.putValue(atvAddProductToInvoiceProductName.getText().toString().trim() + atvAddProductToInvoiceProductName.getTag(), (oldStock + subStock) + "");
                    SharedPreferenceUtil.save();

                } else if (getIntent().hasExtra("onlyShow")) {
                    JSONObject onlyShow = new JSONObject(getIntent().getStringExtra("onlyShow"));
                    getSupportActionBar().setTitle(onlyShow.optString("itemName"));

                    vAddProductToInvoiceCustomerName.setVisibility(View.GONE);
                    ivAddProductToInvoiceProductName.setVisibility(View.GONE);

                    atvAddProductToInvoiceProductName.setText(onlyShow.optString("itemName"));
                    atvAddProductToInvoiceProductName.setTag(onlyShow.optString("itemId"));
                    etAddProductToInvoiceProductPrice.setText(onlyShow.optString("itemPrice"));
                    etAddProductToInvoiceProductQuantity.setText(onlyShow.optString("itemQuantity"));
                    tvAddProductToInvoiceProductTotal.setText(onlyShow.optString("itemTotal"));
                    ivAddProductToInvoiceProductName.setEnabled(false);

                    productSelected = onlyShow.optString("itemId");

                    if (!TextUtils.isEmpty(onlyShow.optString("itemTax")))
                        etAddProductToInvoiceProductTax.setText(CommonUtils.getDecimal(onlyShow.optDouble("itemTax")));
                    else
                        etAddProductToInvoiceProductTax.setText("0.00");

                    if (!TextUtils.isEmpty(onlyShow.optString("itemDiscount")))
                        etAddProductToInvoiceProductDiscount.setText(CommonUtils.getDecimal(onlyShow.optDouble("itemDiscount")));
                    else
                        etAddProductToInvoiceProductDiscount.setText("0.00");

                    atvAddProductToInvoiceProductName.setEnabled(false);
                    etAddProductToInvoiceProductQuantity.setEnabled(false);
                    etAddProductToInvoiceProductPrice.setEnabled(false);
                    etAddProductToInvoiceProductTax.setEnabled(false);
                    etAddProductToInvoiceProductDiscount.setEnabled(false);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public void fillProduct() {

        filteredProduct = new ArrayList<>();

        for (int i = 0; i < customerNameArray.length(); i++) {
            JSONObject newJsonObject = customerNameArray.optJSONObject(i);
            filteredProduct.add(newJsonObject.optString("itemName") + "#" + newJsonObject.optString("itemId"));

        }
    }

    public void calculateProductTotal() {
        double productQuantity = 0.00;
        double productPrice = 0.00;
        double productTax = 0.00;
        double productDiscount = 0.00;
        double productTotal = 0.00;
        double calcTax = 0.00;
        double calcDiscount = 0.00;
        double totalPayment = 0.00;
        double totalDiscountPayment = 0.00;

        if (!TextUtils.isEmpty(etAddProductToInvoiceProductQuantity.getText().toString().trim()))
            productQuantity = Double.valueOf(etAddProductToInvoiceProductQuantity.getText().toString().trim());

        if (!TextUtils.isEmpty(etAddProductToInvoiceProductPrice.getText().toString().trim()))
            productPrice = Double.valueOf(etAddProductToInvoiceProductPrice.getText().toString().trim());

        if (!TextUtils.isEmpty(etAddProductToInvoiceProductTax.getText().toString().trim()))
            productTax = Double.valueOf(etAddProductToInvoiceProductTax.getText().toString().trim());

        if (!TextUtils.isEmpty(etAddProductToInvoiceProductDiscount.getText().toString().trim()))
            productDiscount = Double.valueOf(etAddProductToInvoiceProductDiscount.getText().toString().trim());

        totalPayment = productQuantity * productPrice;
        calcDiscount = (totalPayment * productDiscount) / 100;
        totalDiscountPayment = totalPayment - calcDiscount;
        calcTax = (totalDiscountPayment * productTax) / 100;

        productTotal = totalDiscountPayment + calcTax;

        tvAdProductToInvoiceQuantity.setText(getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(productQuantity));
        tvAdProductToInvoicePrice.setText("* " + getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(productPrice));
        tvAddProductToInvoiceProductTotalToCalc.setText("= " + getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(totalPayment));
        tvAdProductToInvoiceDiscountOnProductTotal.setText("- " + getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(calcDiscount));
        tvAdProductToInvoiceSubTotal.setText("= " + getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(totalDiscountPayment));
        tvAdProductToInvoiceTaxOnSubTotal.setText("+ " + getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(calcTax));
        tvAdProductToInvoiceGrandTotal.setText("= " + getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(productTotal));

        tvAddProductToInvoiceProductTotal.setText(CommonUtils.getDecimal(productTotal));
    }

    public void checkConditionToAdd() {
        if (TextUtils.isEmpty(atvAddProductToInvoiceProductName.getText().toString().trim()))
            Toast.makeText(this, "Please Select Product Name", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(etAddProductToInvoiceProductQuantity.getText().toString().trim()))
            Toast.makeText(this, "Please Enter Product Quantity", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(etAddProductToInvoiceProductPrice.getText().toString().trim()))
            Toast.makeText(this, "Please Enter Product Price", Toast.LENGTH_SHORT).show();
        else if (!TextUtils.isEmpty(etAddProductToInvoiceProductTax.getText().toString().trim()) && Double.valueOf(etAddProductToInvoiceProductTax.getText().toString().trim()) > 100)
            Toast.makeText(this, "Product GST Isn't Correct", Toast.LENGTH_SHORT).show();
        else if (!TextUtils.isEmpty(etAddProductToInvoiceProductDiscount.getText().toString().trim()) && Double.valueOf(etAddProductToInvoiceProductDiscount.getText().toString().trim()) > 100)
            Toast.makeText(this, "Product Discount Isn't Correct", Toast.LENGTH_SHORT).show();
        else if (productSelected.equalsIgnoreCase("") && !checkItemIsExist())
            Toast.makeText(this, "Product Not Available", Toast.LENGTH_SHORT).show();
        else {
            double oldStock = Double.valueOf(SharedPreferenceUtil.getString(atvAddProductToInvoiceProductName.getText().toString().trim() + (String) atvAddProductToInvoiceProductName.getTag(), ""));
            double subStock = Double.valueOf(etAddProductToInvoiceProductQuantity.getText().toString().trim());

            if (oldStock >= subStock) {
                SharedPreferenceUtil.putValue(atvAddProductToInvoiceProductName.getText().toString().trim() + (String) atvAddProductToInvoiceProductName.getTag(), (oldStock - subStock) + "");
                SharedPreferenceUtil.save();
                addProductToInvoice();

            } else {
                Toast.makeText(this, "Insufficient Stock", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean checkItemIsExist() {

        boolean check = true;

        for (int i = 0; i < customerNameArray.length(); i++) {
            JSONObject customerNameObject = customerNameArray.optJSONObject(i);

            if (customerNameObject.optString("itemName").trim().equalsIgnoreCase(atvAddProductToInvoiceProductName.getText().toString().trim())) {
                check = true;
                atvAddProductToInvoiceProductName.setTag(customerNameObject.optString("itemId"));
                break;
            } else {
                check = false;
            }
        }
        return check;
    }

    public void addProductToInvoice() {

        try {
            CommonUtils.closeKeyboard(this);
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("itemName", atvAddProductToInvoiceProductName.getText().toString().trim());
            jsonObject.put("itemId", atvAddProductToInvoiceProductName.getTag() + "");
            jsonObject.put("itemQuantity", etAddProductToInvoiceProductQuantity.getText().toString().trim());
            jsonObject.put("itemPrice", etAddProductToInvoiceProductPrice.getText().toString().trim());

            if (TextUtils.isEmpty(etAddProductToInvoiceProductTax.getText().toString().trim())) {
                jsonObject.put("itemTax", "0.00");
                jsonObject.put("itemTaxInAmount", "0.00");
            } else {
                jsonObject.put("itemTax", etAddProductToInvoiceProductTax.getText().toString().trim());
                jsonObject.put("itemTaxInAmount", tvAdProductToInvoiceTaxOnSubTotal.getText().toString().trim().replace("+", "").trim().replace(getResources().getString(R.string.currency_india), "").trim());
            }

            if (TextUtils.isEmpty(etAddProductToInvoiceProductDiscount.getText().toString().trim())) {
                jsonObject.put("itemDiscount", "0.00");
                jsonObject.put("itemDiscountInAmount", "0.00");
            } else {
                jsonObject.put("itemDiscount", etAddProductToInvoiceProductDiscount.getText().toString().trim());
                jsonObject.put("itemDiscountInAmount", tvAdProductToInvoiceDiscountOnProductTotal.getText().toString().trim().replace("-", "").trim().replace(getResources().getString(R.string.currency_india), "").trim());
            }

            if (!TextUtils.isEmpty(tvAddProductToInvoiceProductTotal.getText().toString().trim()))
                jsonObject.put("itemTotal", tvAddProductToInvoiceProductTotal.getText().toString().trim());
            else
                jsonObject.put("itemTotal", "0.00");

            setResult(Activity.RESULT_OK, new Intent().putExtra("jsonObject", jsonObject.toString()));
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

        if (!TextUtils.isEmpty(etAddProductToInvoiceProductTax.getText().toString().trim())) {

            if (Double.valueOf(etAddProductToInvoiceProductTax.getText().toString().trim()) <= 100) {
                calculateProductTotal();
                taxText = etAddProductToInvoiceProductTax.getText().toString().trim();
            } else {
                etAddProductToInvoiceProductTax.setText(taxText);
                Toast.makeText(this, "GST can't be greater than 100", Toast.LENGTH_SHORT).show();
            }

        } else
            calculateProductTotal();

        if (!TextUtils.isEmpty(etAddProductToInvoiceProductDiscount.getText().toString().trim())) {
            if (Double.valueOf(etAddProductToInvoiceProductDiscount.getText().toString().trim()) <= 100) {
                calculateProductTotal();
                discountText = etAddProductToInvoiceProductDiscount.getText().toString().trim();
            } else {
                etAddProductToInvoiceProductDiscount.setText(discountText);
                Toast.makeText(this, "Discount can't be greater than 100", Toast.LENGTH_SHORT).show();
            }

        } else
            calculateProductTotal();

        if (atvAddProductToInvoiceProductName.hasFocus()) {
            if (checkProductSelect && customerNameArray.length() > 0)
                selectedCustomerName();
            else
                atvAddProductToInvoiceProductName.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>()));

            checkProductSelect = true;
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menuAddProductToInvoice:
                checkConditionToAdd();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivAddProductToInvoiceProductName:
                if (filteredProduct.size() > 0)
                    CommonUtils.picker(this, filteredProduct, "Product Name", atvAddProductToInvoiceProductName, true, this.getLocalClassName());
                else
                    Toast.makeText(this, "No Product Available", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_product_to_invoice, menu);

        if (getIntent().hasExtra("onlyShow"))
            menu.findItem(R.id.menuAddProductToInvoice).setVisible(false);
        else if (getIntent().hasExtra("editData"))
            menu.findItem(R.id.menuAddProductToInvoice).setTitle("Update Product");

        return true;
    }

    @Override
    public void onBackPressed() {

        if (getIntent().hasExtra("editData")) {

            double oldStock = Double.valueOf(SharedPreferenceUtil.getString(atvAddProductToInvoiceProductName.getText().toString().trim() + (String) atvAddProductToInvoiceProductName.getTag(), ""));
            double subStock = Double.valueOf(etAddProductToInvoiceProductQuantity.getText().toString().trim());

            SharedPreferenceUtil.putValue(atvAddProductToInvoiceProductName.getText().toString().trim() + atvAddProductToInvoiceProductName.getTag() + "", (oldStock - subStock) + "");
            SharedPreferenceUtil.save();
        }

        CommonUtils.closeKeyboard(this);
        finish();
        super.onBackPressed();
    }

}
