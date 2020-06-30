package com.example.keval.keval.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keval.keval.Fragment.FragmentCustomer;
import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;
import com.example.keval.keval.Utils.Constants;
import com.mpt.storage.SharedPreferenceUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;

public class Customer extends AppCompatActivity implements View.OnClickListener {

    JSONArray oldUserData = new JSONArray();
    File mediaStorageDir;
    Spinner spCustomerGender;
    AlertDialog.Builder alertDialogs;
    View dialog;
    private AlertDialog alert;

    final private int reqCodeForCaptureImage = 1;
    final private int reqCodeForImage = 2;
    String realPath = " ";
    String editIndex = "";
    String customerName = "";

    EditText etProfileCustomerPinCode, etProfileCustomerTin;
    ImageView ivCustomerCity, ivCustomerState, ivCustomerCountry, cvProfilePhoto;
    EditText etProfileCustomerShopName, etProfileCustomerEmail, etProfileCustomerName, etProfileCustomerNumber,
            etProfileCustomerShopAddress, etProfileCustomerOtherShopAddress, etProfileCustomerOtherPinCode;
    AutoCompleteTextView atvCustomerOtherCity, atvCustomerOtherState, atvCustomerOtherCountry, atvCustomerCity,
            atvCustomerState, atvCustomerCountry;
    ImageView ivCustomerOtherCity, ivCustomerOtherState, ivCustomerOtherCountry;
    CheckBox cbCustomerShipAnotherAddress;
    LinearLayout llCustomerOtherCity, llCustomerOtherState, llCustomerOtherCountry;
    Button btCustomerChangePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferenceUtil.init(this);

        cvProfilePhoto = (ImageView) findViewById(R.id.cvProfilePhoto);
        ivCustomerCity = (ImageView) findViewById(R.id.ivCustomerCity);
        ivCustomerState = (ImageView) findViewById(R.id.ivCustomerState);
        ivCustomerCountry = (ImageView) findViewById(R.id.ivCustomerCountry);
        ivCustomerOtherState = (ImageView) findViewById(R.id.ivCustomerOtherState);
        ivCustomerOtherCountry = (ImageView) findViewById(R.id.ivCustomerOtherCountry);
        ivCustomerOtherCity = (ImageView) findViewById(R.id.ivCustomerOtherCity);

        etProfileCustomerShopName = (EditText) findViewById(R.id.etProfileCustomerShopName);
        etProfileCustomerName = (EditText) findViewById(R.id.etProfileCustomerName);
        etProfileCustomerNumber = (EditText) findViewById(R.id.etProfileCustomerNumber);
        etProfileCustomerShopAddress = (EditText) findViewById(R.id.etProfileCustomerShopAddress);
        etProfileCustomerEmail = (EditText) findViewById(R.id.etProfileCustomerEmail);
        etProfileCustomerPinCode = (EditText) findViewById(R.id.etProfileCustomerPinCode);
        etProfileCustomerTin = (EditText) findViewById(R.id.etProfileCustomerTin);
        etProfileCustomerOtherPinCode = (EditText) findViewById(R.id.etProfileCustomerOtherPinCode);
        etProfileCustomerOtherShopAddress = (EditText) findViewById(R.id.etProfileCustomerOtherShopAddress);

        spCustomerGender = (Spinner) findViewById(R.id.spCustomerGender);
        cbCustomerShipAnotherAddress = (CheckBox) findViewById(R.id.cbCustomerShipAnotherAddress);
        btCustomerChangePic = (Button) findViewById(R.id.btCustomerChangePic);

        atvCustomerCity = (AutoCompleteTextView) findViewById(R.id.atvCustomerCity);
        atvCustomerState = (AutoCompleteTextView) findViewById(R.id.atvCustomerState);
        atvCustomerCountry = (AutoCompleteTextView) findViewById(R.id.atvCustomerCountry);
        atvCustomerOtherCity = (AutoCompleteTextView) findViewById(R.id.atvCustomerOtherCity);
        atvCustomerOtherState = (AutoCompleteTextView) findViewById(R.id.atvCustomerOtherState);
        atvCustomerOtherCountry = (AutoCompleteTextView) findViewById(R.id.atvCustomerOtherCountry);

        llCustomerOtherCity = (LinearLayout) findViewById(R.id.llCustomerOtherCity);
        llCustomerOtherState = (LinearLayout) findViewById(R.id.llCustomerOtherState);
        llCustomerOtherCountry = (LinearLayout) findViewById(R.id.llCustomerOtherCountry);

        if (getIntent().hasExtra("customerPosition") && !TextUtils.isEmpty(getIntent().getStringExtra("customerPosition"))) {
            editIndex = getIntent().getStringExtra("customerPosition");

            try {
                JSONObject mainObject = new JSONObject(SharedPreferenceUtil.getString(Constants.ALL_DETAILS_BY_ID + editIndex, ""));
                JSONObject objectCustomerData = mainObject.optJSONArray("profile").optJSONObject(0);

                realPath = objectCustomerData.optString("customerPhoto");
                if (!TextUtils.isEmpty(realPath)) {

                    Picasso.with(Customer.this)
                            .load(Uri.fromFile(new File(realPath)))
                            .centerCrop()
                            .placeholder(R.mipmap.icon_user)
                            .fit()
                            .error(R.mipmap.icon_user)
                            .into(cvProfilePhoto);
                }

                customerName = objectCustomerData.optString("customerName");

                etProfileCustomerShopName.setText(objectCustomerData.optString("shopName"));
                etProfileCustomerShopName.setTag(objectCustomerData.optString("grandTotal"));
                etProfileCustomerName.setText(objectCustomerData.optString("customerName"));
                etProfileCustomerName.setTag(objectCustomerData.optString("customerId"));
                etProfileCustomerNumber.setText(objectCustomerData.optString("shopNumber"));
                etProfileCustomerShopAddress.setText(objectCustomerData.optString("shopAdd"));
                etProfileCustomerEmail.setText(objectCustomerData.optString("customerEmail"));

                etProfileCustomerTin.setText(objectCustomerData.optString("customerTIN"));
                atvCustomerCity.setText(objectCustomerData.optString("customerCity"));
                atvCustomerState.setText(objectCustomerData.optString("customerState"));
                etProfileCustomerPinCode.setText(objectCustomerData.optString("customerCityPinCode"));
                atvCustomerCountry.setText(objectCustomerData.optString("customerCountry"));

                cbCustomerShipAnotherAddress.setChecked(objectCustomerData.optBoolean("otherAddressEntered"));

                etProfileCustomerOtherShopAddress.setText(objectCustomerData.optString("shopOtherAdd"));
                atvCustomerOtherCity.setText(objectCustomerData.optString("customerOtherCity"));
                atvCustomerOtherState.setText(objectCustomerData.optString("customerOtherState"));
                etProfileCustomerOtherPinCode.setText(objectCustomerData.optString("customerOtherCityPinCode"));
                atvCustomerOtherCountry.setText(objectCustomerData.optString("customerOtherCountry"));

                if (objectCustomerData.optString("customerGender").equalsIgnoreCase("Male"))
                    spCustomerGender.setSelection(0);
                else
                    spCustomerGender.setSelection(1);

                getSupportActionBar().setTitle(objectCustomerData.optString("customerName"));

                if (cbCustomerShipAnotherAddress.isChecked())
                    visibleOtherAddress(View.VISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cvProfilePhoto.setOnClickListener(this);
        btCustomerChangePic.setOnClickListener(this);
        ivCustomerState.setOnClickListener(this);
        cbCustomerShipAnotherAddress.setOnClickListener(this);
    }

    public void visibleOtherAddress(int visibility) {

        if (visibility == View.VISIBLE)
            atvCustomerCountry.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        else
            atvCustomerCountry.setImeOptions(EditorInfo.IME_ACTION_DONE);

        etProfileCustomerOtherShopAddress.setVisibility(visibility);
        llCustomerOtherCity.setVisibility(visibility);
        llCustomerOtherState.setVisibility(visibility);
        etProfileCustomerOtherPinCode.setVisibility(visibility);
        llCustomerOtherCountry.setVisibility(visibility);
    }

    public void checkCondition() {
        if (TextUtils.isEmpty(etProfileCustomerName.getText().toString().trim()))
            Toast.makeText(Customer.this, "Enter Customer Name", Toast.LENGTH_SHORT).show();
        else if (!TextUtils.isEmpty(etProfileCustomerEmail.getText().toString().trim()) && !CommonUtils.isValidEmail(etProfileCustomerEmail.getText().toString().trim()))
            Toast.makeText(Customer.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
        else
            afterCondition();
    }

    public void afterCondition() {
        if (!getIntent().hasExtra("customerPosition")) {
            if (CommonUtils.checkCustomerExist(Customer.this, etProfileCustomerName.getText().toString().trim(), true, true)) {
                CommonUtils.showProgressDialog(Customer.this, "Creating Customer Details...");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addCustomer();
                    }
                }, 1000);
            }
        } else if (CommonUtils.checkCustomerExist(Customer.this, etProfileCustomerName.getText().toString().trim(), true, !customerName.equalsIgnoreCase(etProfileCustomerName.getText().toString().trim()))) {
            CommonUtils.showProgressDialog(Customer.this, "Updating Customer Details...");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    editCustomer();
                }
            }, 1000);
        }
    }

    public void conditionToCapturePhoto(File mediaFile1) {
        if (!mediaFile1.exists()) {

            if (!mediaFile1.mkdirs()) {

                Toast.makeText(this, "Failed to create directory MyCameraVideo.",
                        Toast.LENGTH_LONG).show();

                Log.d("MyCameraVideo", "Failed to create directory MyCameraVideo.");
            }
        }
    }

    public void takePhoto() {
        java.util.Date date = new java.util.Date();
        String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(date.getTime());
        File mediaFile = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)));

        Uri fileUri;
        File image = new File(mediaFile + "/image");
        conditionToCapturePhoto(image);

        mediaStorageDir = new File(image.getPath() + File.separator + "IMG_" + timeStamp + ".png");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = Uri.fromFile(mediaStorageDir);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, reqCodeForCaptureImage);
    }

    public void imageUploadDialog() {
        final CharSequence[] options;
        options = new CharSequence[]{"Take Photo", "Choose from Gallery", "Cancel"};

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Choose Image!");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    takePhoto();
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intImage.setType("image/*");
                    startActivityForResult(intImage, reqCodeForImage);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void addCustomer() {
        JSONObject objectUserData = new JSONObject();

        try {
            String index = CommonUtils.indexId(Constants.CUSTOMER_ITEM_ID);

            objectUserData.put("customerPhoto", realPath.trim());
            objectUserData.put("shopName", etProfileCustomerShopName.getText().toString().trim());
            objectUserData.put("customerName", etProfileCustomerName.getText().toString().trim());
            objectUserData.put("customerId", index);
            objectUserData.put("shopNumber", etProfileCustomerNumber.getText().toString().trim());
            objectUserData.put("customerEmail", etProfileCustomerEmail.getText().toString().trim());
            objectUserData.put("customerTIN", etProfileCustomerTin.getText().toString().trim());
            objectUserData.put("shopAdd", etProfileCustomerShopAddress.getText().toString().trim());
            objectUserData.put("customerCity", atvCustomerCity.getText().toString().trim());
            objectUserData.put("customerState", atvCustomerState.getText().toString().trim());
            objectUserData.put("customerCityPinCode", etProfileCustomerPinCode.getText().toString().trim());
            objectUserData.put("customerCountry", atvCustomerCountry.getText().toString().trim());

            if (etProfileCustomerOtherShopAddress.getText().toString().trim().equalsIgnoreCase("")
                    && atvCustomerOtherCity.getText().toString().trim().equalsIgnoreCase("")
                    && atvCustomerOtherState.getText().toString().trim().equalsIgnoreCase("")
                    && etProfileCustomerOtherPinCode.getText().toString().trim().equalsIgnoreCase("")
                    && atvCustomerOtherCountry.getText().toString().trim().equalsIgnoreCase(""))

                cbCustomerShipAnotherAddress.setChecked(false);

            objectUserData.put("otherAddressEntered", cbCustomerShipAnotherAddress.isChecked());

            if (cbCustomerShipAnotherAddress.isChecked()) {
                objectUserData.put("shopOtherAdd", etProfileCustomerOtherShopAddress.getText().toString().trim());
                objectUserData.put("customerOtherCity", atvCustomerOtherCity.getText().toString().trim());
                objectUserData.put("customerOtherState", atvCustomerOtherState.getText().toString().trim());
                objectUserData.put("customerOtherCityPinCode", etProfileCustomerOtherPinCode.getText().toString().trim());
                objectUserData.put("customerOtherCountry", atvCustomerOtherCountry.getText().toString().trim());

            } else {
                objectUserData.put("shopOtherAdd", "");
                objectUserData.put("customerOtherCity", "");
                objectUserData.put("customerOtherState", "");
                objectUserData.put("customerOtherCityPinCode", "");
                objectUserData.put("customerOtherCountry", "");
            }

            if (!etProfileCustomerShopName.getText().toString().trim().equalsIgnoreCase(""))
                objectUserData.put("shopNameThere", true);
            else
                objectUserData.put("shopNameThere", false);

            if (!etProfileCustomerNumber.getText().toString().trim().equalsIgnoreCase(""))
                objectUserData.put("shopNumberThere", true);
            else
                objectUserData.put("shopNumberThere", false);

            objectUserData.put("customerGender", spCustomerGender.getSelectedItem().toString().trim());
            objectUserData.put("grandTotal", "0");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        CommonUtils.addCustomer(objectUserData);

        FragmentCustomer.onCreateSetData(this);
        finish();
        Toast.makeText(this, "Customer Added Successfully", Toast.LENGTH_SHORT).show();
    }

    public void editCustomer() {
        try {

            if (SharedPreferenceUtil.contains(Constants.CUSTOMER_DATA_SAVE) && !TextUtils.isEmpty(SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, "")))
                oldUserData = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, "") + "]");

            JSONObject objectUserData = new JSONObject();

            objectUserData.put("customerPhoto", realPath.trim());
            objectUserData.put("shopName", etProfileCustomerShopName.getText().toString().trim());
            objectUserData.put("customerName", etProfileCustomerName.getText().toString().trim());
            objectUserData.put("customerId", etProfileCustomerName.getTag() + "");
            objectUserData.put("shopNumber", etProfileCustomerNumber.getText().toString().trim());
            objectUserData.put("customerEmail", etProfileCustomerEmail.getText().toString().trim());
            objectUserData.put("customerTIN", etProfileCustomerTin.getText().toString().trim());
            objectUserData.put("shopAdd", etProfileCustomerShopAddress.getText().toString().trim());
            objectUserData.put("customerCity", atvCustomerCity.getText().toString().trim());
            objectUserData.put("customerState", atvCustomerState.getText().toString().trim());
            objectUserData.put("customerCityPinCode", etProfileCustomerPinCode.getText().toString().trim());
            objectUserData.put("customerCountry", atvCustomerCountry.getText().toString().trim());

            if (etProfileCustomerOtherShopAddress.getText().toString().trim().equalsIgnoreCase("")
                    && atvCustomerOtherCity.getText().toString().trim().equalsIgnoreCase("")
                    && atvCustomerOtherState.getText().toString().trim().equalsIgnoreCase("")
                    && etProfileCustomerOtherPinCode.getText().toString().trim().equalsIgnoreCase("")
                    && atvCustomerOtherCountry.getText().toString().trim().equalsIgnoreCase(""))

                cbCustomerShipAnotherAddress.setChecked(false);

            objectUserData.put("otherAddressEntered", cbCustomerShipAnotherAddress.isChecked());

            if (cbCustomerShipAnotherAddress.isChecked()) {
                objectUserData.put("shopOtherAdd", etProfileCustomerOtherShopAddress.getText().toString().trim());
                objectUserData.put("customerOtherCity", atvCustomerOtherCity.getText().toString().trim());
                objectUserData.put("customerOtherState", atvCustomerOtherState.getText().toString().trim());
                objectUserData.put("customerOtherCityPinCode", etProfileCustomerOtherPinCode.getText().toString().trim());
                objectUserData.put("customerOtherCountry", atvCustomerOtherCountry.getText().toString().trim());

            } else {
                objectUserData.put("shopOtherAdd", "");
                objectUserData.put("customerOtherCity", "");
                objectUserData.put("customerOtherState", "");
                objectUserData.put("customerOtherCityPinCode", "");
                objectUserData.put("customerOtherCountry", "");
            }

            if (!etProfileCustomerShopName.getText().toString().trim().equalsIgnoreCase(""))
                objectUserData.put("shopNameThere", true);
            else
                objectUserData.put("shopNameThere", false);

            if (!etProfileCustomerNumber.getText().toString().trim().equalsIgnoreCase(""))
                objectUserData.put("shopNumberThere", true);
            else
                objectUserData.put("shopNumberThere", false);

            objectUserData.put("customerGender", spCustomerGender.getSelectedItem().toString().trim());
            objectUserData.put("grandTotal", etProfileCustomerShopName.getTag() + "");

            boolean checkLoopOver = false;

            for (int i = 0; i < oldUserData.length(); i++) {
                JSONObject oldUserObject = oldUserData.optJSONObject(i);

                if (oldUserObject.optString("customerId").equalsIgnoreCase((String) etProfileCustomerName.getTag())) {
                    oldUserData.put(i, objectUserData);
                    checkLoopOver = true;
                }

                if (checkLoopOver)
                    break;
            }

            JSONArray billingJsonArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.BILL_DATA_SAVE, "") + "]");
            JSONArray depositJsonArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.DEPOSIT_DATA_SAVE, "") + "]");
            JSONArray dailyScheduleJsonArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.DAILY_SCHEDULE, "") + "]");
            JSONArray allTransactionArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.ALL_TRANSACTION, "") + "]");
            JSONObject objectDetailById = new JSONObject(SharedPreferenceUtil.getString(Constants.ALL_DETAILS_BY_ID + editIndex, ""));
            JSONArray allDataByIdArray = objectDetailById.optJSONArray("data");

            for (int i = 0; i < allTransactionArray.length(); i++) {
                JSONObject allTransactionObject = allTransactionArray.optJSONObject(i);

                if (allTransactionObject.optString("customerId").equalsIgnoreCase((String) etProfileCustomerName.getTag())) {
                    if (allTransactionObject.has("depositNo"))
                        allTransactionObject.put("depositName", etProfileCustomerName.getText().toString().trim());
                    else
                        allTransactionObject.put("customerName", etProfileCustomerName.getText().toString().trim());
                }
            }

            for (int i = 0; i < allDataByIdArray.length(); i++) {
                JSONObject allDataByIdObject = allDataByIdArray.optJSONObject(i);

                if (allDataByIdObject.has("customerName"))
                    allDataByIdObject.put("customerName", etProfileCustomerName.getText().toString().trim());
                else
                    allDataByIdObject.put("depositName", etProfileCustomerName.getText().toString().trim());
            }

            JSONObject editToMainDataObject = new JSONObject();
            editToMainDataObject.put("profile", new JSONArray("[" + objectUserData.toString() + "]"));
            editToMainDataObject.put("data", allDataByIdArray);
            editToMainDataObject.put("grandTotal", objectDetailById.optString("grandTotal"));

            for (int i = 0; i < billingJsonArray.length(); i++) {
                JSONObject billingJsonObject = billingJsonArray.optJSONObject(i);

                if (billingJsonObject.optString("customerId").equalsIgnoreCase((String) etProfileCustomerName.getTag())) {
                    billingJsonObject.put("customerName", etProfileCustomerName.getText().toString().trim());
                }
            }

            for (int j = 0; j < depositJsonArray.length(); j++) {
                JSONObject depositJsonObject = depositJsonArray.optJSONObject(j);

                if (depositJsonObject.optString("customerId").equalsIgnoreCase((String) etProfileCustomerName.getTag())) {
                    depositJsonObject.put("depositName", etProfileCustomerName.getText().toString().trim());
                }
            }

            for (int i = 0; i < dailyScheduleJsonArray.length(); i++) {
                JSONObject dailyScheduleJsonObject = dailyScheduleJsonArray.optJSONObject(i);

                if (dailyScheduleJsonObject.optString("customerId").equalsIgnoreCase((String) etProfileCustomerName.getTag())) {
                    dailyScheduleJsonObject.put("customerName", etProfileCustomerName.getText().toString().trim());
                }
            }

            SharedPreferenceUtil.putValue(Constants.ALL_TRANSACTION, allTransactionArray.toString().substring(1, allTransactionArray.toString().length() - 1));
            SharedPreferenceUtil.putValue(Constants.BILL_DATA_SAVE, billingJsonArray.toString().substring(1, billingJsonArray.toString().length() - 1));
            SharedPreferenceUtil.putValue(Constants.DAILY_SCHEDULE, dailyScheduleJsonArray.toString().substring(1, dailyScheduleJsonArray.toString().length() - 1));
            SharedPreferenceUtil.putValue(Constants.DEPOSIT_DATA_SAVE, depositJsonArray.toString().substring(1, depositJsonArray.toString().length() - 1));
            SharedPreferenceUtil.putValue(Constants.ALL_DETAILS_BY_ID + editIndex, editToMainDataObject.toString());
            SharedPreferenceUtil.putValue(Constants.CUSTOMER_DATA_SAVE, oldUserData.toString().substring(1, oldUserData.toString().length() - 1));
            SharedPreferenceUtil.save();

            FragmentCustomer.onCreateSetData(Customer.this);
            setResult(Activity.RESULT_OK, new Intent().putExtra("customerName", etProfileCustomerName.getText().toString()));
            finish();
            Toast.makeText(Customer.this, "Customer Updated Successfully", Toast.LENGTH_SHORT).show();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void openOccasionsPopupOptions() {
        try {
            alertDialogs = new AlertDialog.Builder(this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            dialog = inflater.inflate(R.layout.element_dialog_all, null);

            final ListView lvElementDialogAll = (ListView) dialog.findViewById(R.id.lvElementDialogAll);
            EditText etElementDialogAllSearch = (EditText) dialog.findViewById(R.id.etElementDialogAllSearch);

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
//                dialog.setBackground(new ColorDrawable(Color.TRANSPARENT));
//            else
//                dialog.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            final JSONArray arrayStatesName = new JSONArray();
            final JSONArray[] arrayNewStatesName = {new JSONArray()};

            arrayStatesName.put("Andaman and Nicobar Islands");
            arrayStatesName.put("Andhra Pradesh");
            arrayStatesName.put("Arunachal Pradesh");
            arrayStatesName.put("Assam");
            arrayStatesName.put("Bihar");
            arrayStatesName.put("Chandigarh");
            arrayStatesName.put("Chhattisgarh");
            arrayStatesName.put("Dadra and Nagar Haveli");
            arrayStatesName.put("Daman and Diu");
            arrayStatesName.put("Delhi");
            arrayStatesName.put("Goa");
            arrayStatesName.put("Gujarat");
            arrayStatesName.put("Haryana");
            arrayStatesName.put("Himachal Pradesh");
            arrayStatesName.put("Jammu and Kashmir");
            arrayStatesName.put("Jharkhand");
            arrayStatesName.put("Karnataka");
            arrayStatesName.put("Kerala");
            arrayStatesName.put("Lakshadweep");
            arrayStatesName.put("Madhya Pradesh");
            arrayStatesName.put("Maharashtra");
            arrayStatesName.put("Manipur");
            arrayStatesName.put("Meghalaya");
            arrayStatesName.put("Mizoram");
            arrayStatesName.put("Nagaland");
            arrayStatesName.put("Odisha");
            arrayStatesName.put("Puducherry");
            arrayStatesName.put("Punjab");
            arrayStatesName.put("Rajasthan");
            arrayStatesName.put("Sikkim");
            arrayStatesName.put("Tamil Nadu");
            arrayStatesName.put("Telangana");
            arrayStatesName.put("Tripura");
            arrayStatesName.put("Uttar Pradesh");
            arrayStatesName.put("Uttarakhand");
            arrayStatesName.put("West Bengal");

            lvElementDialogAll.setAdapter(new AdapterCustomerFields(arrayStatesName));

            etElementDialogAllSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    arrayNewStatesName[0] = new JSONArray();

                    if (s != null && !s.equals("") && s.length() > 0) {
                        for (int i = 0; i < arrayStatesName.length(); i++)
                            if (arrayStatesName.optString(i).contains(s))
                                arrayNewStatesName[0].put(arrayStatesName.optString(i));

                        lvElementDialogAll.setAdapter(new AdapterCustomerFields(arrayNewStatesName[0]));

                    } else
                        lvElementDialogAll.setAdapter(new AdapterCustomerFields(arrayStatesName));
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            alertDialogs.setView(dialog);
            alert = alertDialogs.create();
            alert.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            alert.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void storageRequest() {
        if (Build.VERSION.SDK_INT >= 23)
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                filePath = new File(CommonUtils.getAppPath(this), "DataBase");
                CommonUtils.closeKeyboard(this);
                imageUploadDialog();
            } else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {

                case reqCodeForCaptureImage:
                    realPath = mediaStorageDir.getPath();

                    CommonUtils.showProgressDialog(this, "Updating Profile Pic...");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Picasso.with(Customer.this)
                                    .load(mediaStorageDir)
                                    .centerCrop()
                                    .placeholder(R.mipmap.icon_user)
                                    .error(R.mipmap.icon_user)
                                    .fit()
                                    .into(cvProfilePhoto);

                            CommonUtils.cancelProgressDialog();
                        }
                    }, 1000);

                    break;

                case reqCodeForImage:

                    if (Build.VERSION.SDK_INT < 11)
                        realPath = CommonUtils.getRealPathFromURI_BelowAPI11(this, data.getData(), false);
                    else if (Build.VERSION.SDK_INT < 19)
                        realPath = CommonUtils.getRealPathFromURI_API11to18(this, data.getData(), false);
                    else
                        realPath = CommonUtils.getRealPathFromURI_API11to18(this, data.getData(), false);

                    final File filePhoto = new File(realPath);

                    if (realPath != null && !realPath.equalsIgnoreCase("")) {
                        CommonUtils.showProgressDialog(this, "Updating Profile Pic...");

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Picasso.with(Customer.this)
                                        .load(filePhoto)
                                        .placeholder(R.mipmap.icon_user)
                                        .error(R.mipmap.icon_user)
                                        .into(cvProfilePhoto);

                                CommonUtils.cancelProgressDialog();
                            }
                        }, 1000);
                    } else
                        Toast.makeText(this, "Sorry, SomeThing went wrong", Toast.LENGTH_SHORT).show();

                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer, menu);

        if (getIntent().hasExtra("customerPosition"))
            menu.findItem(R.id.menuCustomer).setTitle("Update Customer");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                CommonUtils.closeKeyboard(this);
                finish();
                break;
            case R.id.menuCustomer:
                CommonUtils.closeKeyboard(this);
                checkCondition();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvProfilePhoto:
                CommonUtils.closeKeyboard(this);
                startActivity(new Intent(this, CustomerProfilePic.class).putExtra("viewPhoto", realPath).putExtra("customerName", etProfileCustomerName.getText().toString().trim()));
                break;
            case R.id.btCustomerChangePic:
                storageRequest();
                break;
            case R.id.cbCustomerShipAnotherAddress:
                if (cbCustomerShipAnotherAddress.isChecked())
                    visibleOtherAddress(View.VISIBLE);
                else
                    visibleOtherAddress(View.GONE);
                break;
            case R.id.ivCustomerState:
                openOccasionsPopupOptions();
                break;
        }
    }

    public class AdapterCustomerFields extends BaseAdapter {

        private LayoutInflater inflater = null;
        JSONArray data;

        AdapterCustomerFields(JSONArray data) {
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

            if (convertView == null)
                vi = inflater.inflate(R.layout.element_customer_fields, null);

            TextView tvElementCustomerFields = (TextView) vi.findViewById(R.id.tvElementCustomerFields);
            tvElementCustomerFields.setText(data.optString(position));

            vi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    atvCustomerState.setText(data.optString(position));
                    alert.dismiss();
                }
            });

            return vi;

        }
    }
}