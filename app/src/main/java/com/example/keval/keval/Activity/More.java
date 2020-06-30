package com.example.keval.keval.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;
import com.example.keval.keval.Utils.Constants;
import com.mpt.storage.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class More extends AppCompatActivity implements View.OnClickListener {

    TextView tvMoreAccount, tvMoreAllTransaction, tvMoreBackUp, tvMoreRestore, tvMoreDailySchedule;
    File filePath;
    View vMoreRestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvMoreAccount = (TextView) findViewById(R.id.tvMoreAccount);
        tvMoreAllTransaction = (TextView) findViewById(R.id.tvMoreAllTransaction);
        tvMoreBackUp = (TextView) findViewById(R.id.tvMoreBackUp);
        tvMoreRestore = (TextView) findViewById(R.id.tvMoreRestore);
        tvMoreDailySchedule = (TextView) findViewById(R.id.tvMoreDailySchedule);
        vMoreRestore = findViewById(R.id.vMoreRestore);

        storageRequest();

        tvMoreAccount.setOnClickListener(this);
        tvMoreAllTransaction.setOnClickListener(this);
        tvMoreDailySchedule.setOnClickListener(this);
        tvMoreBackUp.setOnClickListener(this);
        tvMoreRestore.setOnClickListener(this);

    }

    public void storageRequest() {
        if (Build.VERSION.SDK_INT >= 23)
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                filePath = CommonUtils.getAppPath(this);
//                filePath = new File(CommonUtils.getAppPath(this), "DataBase");

                if (filePath.exists() && filePath.list().length > 0) {
                    tvMoreRestore.setVisibility(View.VISIBLE);
                    vMoreRestore.setVisibility(View.VISIBLE);
                } else {
                    tvMoreRestore.setVisibility(View.GONE);
                    vMoreRestore.setVisibility(View.GONE);
                }
            } else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    public void backUp() {

        if (!filePath.exists()) {
            filePath.mkdir();
            filePath.setReadable(false);
            filePath.setWritable(false);
            filePath.setReadOnly();
            Log.i("filePath2", filePath.getAbsolutePath());
        } else {

            String[] getName = Arrays.toString(filePath.list()).split(",");

            for (String aGetName : getName) {
                String fileName = aGetName.replace("[", "").replace("]", "").trim();
                File deleteFile = new File(filePath, fileName);
                deleteFile.delete();
            }
        }

        try {
            JSONArray depositArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.DEPOSIT_DATA_SAVE, "") + "]");
            JSONArray customerArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, "") + "]");
            JSONArray invoiceArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.BILL_DATA_SAVE, "") + "]");
            String dateContainsInvoice = "";
            String dateContainsDeposit = "";

            for (int i = 0; i < depositArray.length(); i++) {
                JSONObject depositObject = depositArray.optJSONObject(i);

                if (!dateContainsDeposit.equalsIgnoreCase(depositObject.optString("depositDate"))) {
                    createTextFile(filePath, Constants.SAVE_DEPOSIT_BY_DATE + depositObject.optString("depositDate"));
                    dateContainsDeposit = depositObject.optString("depositDate");
                }
            }

            for (int i = 0; i < invoiceArray.length(); i++) {
                JSONObject invoiceObject = invoiceArray.optJSONObject(i);

                if (!dateContainsInvoice.equalsIgnoreCase(invoiceObject.optString("billDate"))) {
                    createTextFile(filePath, Constants.SAVE_INVOICE_BY_DATE + invoiceObject.optString("billDate"));
                    dateContainsInvoice = invoiceObject.optString("billDate");
                }
            }

            for (int j = 0; j < customerArray.length(); j++)
                createTextFile(filePath, Constants.ALL_DETAILS_BY_ID + (j + 1));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        createTextFile(filePath, Constants.CUSTOMER_DATA_SAVE);
        createTextFile(filePath, Constants.BILL_DATA_SAVE);
        createTextFile(filePath, Constants.STOCK_DATA_SAVE);
        createTextFile(filePath, Constants.DEPOSIT_DATA_SAVE);
        createTextFile(filePath, Constants.ALL_TRANSACTION);
        createTextFile(filePath, Constants.STOCK_ITEM_ID);
        createTextFile(filePath, Constants.CUSTOMER_ITEM_ID);
        createTextFile(filePath, Constants.BILLING_ITEM_ID);
        createTextFile(filePath, Constants.DEPOSIT_ITEM_ID);
        createTextFile(filePath, Constants.DAILY_SCHEDULE);
        createTextFile(filePath, Constants.DAILY_SCHEDULE_ID);
    }

    public void createTextFile(File filePath, String fileName) {

        try {
            Log.i("filePath3", filePath.getAbsolutePath());
            OutputStream fileOutputStream = new FileOutputStream(filePath + "/" + fileName + ".bkup");
            String string = SharedPreferenceUtil.getString(fileName, "");

            fileOutputStream.write(CommonUtils.encryptData(string).getBytes());
            fileOutputStream.close();
            fileOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void restore() {

        String[] getName = Arrays.toString(filePath.list()).split(",");
        SharedPreferenceUtil.clear();

        for (String aGetName : getName) {
            String fileName = aGetName.replace(".bkup", "").replace("[", "").replace("]", "").trim();
            SharedPreferenceUtil.putValue(fileName, CommonUtils.decodeData(read_file(filePath + "/" + fileName + ".bkup")));
        }

        SharedPreferenceUtil.save();
    }

    public String read_file(String filename) {
        try {
            FileInputStream fis = new FileInputStream(new File(filename));
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
//                sb.append(line).append("\n");
                sb.append(line);
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        } catch (IOException e) {
            return "";
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                    finish();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tvMoreAccount:
                startActivity(new Intent(this, Account.class));
                break;
            case R.id.tvMoreAllTransaction:
                startActivity(new Intent(this, AllTransaction.class));
                break;
            case R.id.tvMoreBackUp:
                CommonUtils.showProgressDialog(More.this, "Taking Back up...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backUp();
                        finish();
                        Toast.makeText(More.this, "Backup has been taken", Toast.LENGTH_SHORT).show();
                    }
                }, 1000);
                break;
            case R.id.tvMoreRestore:
                restoreAlertBox();
                break;
            case R.id.tvMoreDailySchedule:
                startActivity(new Intent(this, Remainder.class));
                break;
        }
    }

    public void restoreAlertBox() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Restore!");
        builder.setMessage("Whole data should be override.\n Do you wan't to Restore?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                CommonUtils.showProgressDialog(More.this, "Restoring...");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        restore();
                        startActivity(new Intent(More.this, MyShop.class));
                        Toast.makeText(More.this, "Backup data Restored", Toast.LENGTH_SHORT).show();
                    }
                }, 1000);

                dialog.dismiss();
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
}