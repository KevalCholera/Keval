package com.example.keval.keval.DataImage;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShareBill extends AppCompatActivity {

    LinearLayout llShareBillInfo,llShareBill;
    TextView tvShareBillDate, tvShareBillCustomerName, tvShareBillInvoiceNo, tvShareBillGrandTotal, tvShareBillAmountPaid, tvShareBillAmountDue;
    TextView tvShareBillItemName, tvShareBillItemQnty, tvShareBillItemRate, tvShareBillItemTotal, tvShareBillItemId;
    boolean finishActivity = false;
    int requestCode = 104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CommonUtils.fullActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_bill);

        llShareBillInfo = (LinearLayout) findViewById(R.id.llShareBillInfo);
        llShareBill = (LinearLayout) findViewById(R.id.llShareBill);
        tvShareBillDate = (TextView) findViewById(R.id.tvShareBillDate);
        tvShareBillCustomerName = (TextView) findViewById(R.id.tvShareBillCustomerName);
        tvShareBillInvoiceNo = (TextView) findViewById(R.id.tvShareBillInvoiceNo);
        tvShareBillGrandTotal = (TextView) findViewById(R.id.tvShareBillGrandTotal);
        tvShareBillItemId = (TextView) findViewById(R.id.tvShareBillItemId);
        tvShareBillAmountDue = (TextView) findViewById(R.id.tvShareBillAmountDue);
        tvShareBillAmountPaid = (TextView) findViewById(R.id.tvShareBillAmountPaid);

        tvShareBillItemName = (TextView) findViewById(R.id.tvShareBillItemName);
        tvShareBillItemQnty = (TextView) findViewById(R.id.tvShareBillItemQnty);
        tvShareBillItemRate = (TextView) findViewById(R.id.tvShareBillItemRate);
        tvShareBillItemTotal = (TextView) findViewById(R.id.tvShareBillItemTotal);

        try {
            JSONObject data = new JSONObject(getIntent().getStringExtra("data"));
            tvShareBillDate.setText("Date:- " + data.optString("billDate"));
            tvShareBillCustomerName.setText("Customer Name:- " + data.optString("customerName"));
            tvShareBillCustomerName.setTag(data.optString("customerId"));
            tvShareBillInvoiceNo.setText(getResources().getString(R.string.billing) + " No:- " + data.optString("billNo"));
            tvShareBillGrandTotal.setText(getResources().getString(R.string.billing) + " Total         "+getResources().getString(R.string.currency_india) + data.optString("billTotal"));
            tvShareBillAmountPaid.setText(getResources().getString(R.string.billing) + " Paid         "+getResources().getString(R.string.currency_india) + CommonUtils.getDecimal(data.optDouble("billAddPayment")));
            tvShareBillAmountDue.setText(getResources().getString(R.string.billing) + " Due         "+getResources().getString(R.string.currency_india) + CommonUtils.getDecimal((data.optDouble("billLeft"))));

            JSONArray infoArray = data.optJSONArray("info");
            for (int i = 0; i < infoArray.length(); i++)
                calcWidthHeight(i, infoArray.optJSONObject(i));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    CommonUtils.ScreenShot(ShareBill.this, requestCode, getIntent().hasExtra("shareImage"));

                }
            }, 100);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void calcWidthHeight(final int position, final JSONObject jsonObject) {

        llShareBillInfo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    tvShareBillItemName.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    tvShareBillItemId.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    tvShareBillItemQnty.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    tvShareBillItemRate.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    tvShareBillItemTotal.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    tvShareBillItemName.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    tvShareBillItemId.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    tvShareBillItemQnty.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    tvShareBillItemRate.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    tvShareBillItemTotal.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                LinearLayout.LayoutParams layoutId = new LinearLayout.LayoutParams(tvShareBillItemId.getMeasuredWidth(), tvShareBillItemId.getMeasuredHeight());
                LinearLayout.LayoutParams layoutName = new LinearLayout.LayoutParams(tvShareBillItemName.getMeasuredWidth(), tvShareBillItemName.getMeasuredHeight());
                LinearLayout.LayoutParams layoutQnty = new LinearLayout.LayoutParams(tvShareBillItemQnty.getMeasuredWidth(), tvShareBillItemQnty.getMeasuredHeight());
                LinearLayout.LayoutParams layoutRate = new LinearLayout.LayoutParams(tvShareBillItemRate.getMeasuredWidth(), tvShareBillItemRate.getMeasuredHeight());
                LinearLayout.LayoutParams layoutTotal = new LinearLayout.LayoutParams(tvShareBillItemTotal.getMeasuredWidth(), tvShareBillItemTotal.getMeasuredHeight());

                linearBillShare(position, jsonObject, layoutId, layoutName, layoutQnty, layoutRate, layoutTotal);
            }
        });
    }

    public void linearBillShare(int position, JSONObject jsonObject, LinearLayout.LayoutParams layoutId, LinearLayout.LayoutParams layoutName, LinearLayout.LayoutParams layoutQnty, LinearLayout.LayoutParams layoutRate, LinearLayout.LayoutParams layoutTotal) {

        LinearLayout llHorizontalLayout = new LinearLayout(this);
        llHorizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView tvItemId = new TextView(this);
        tvItemId.setLayoutParams(layoutId);
        tvItemId.setText((position + 1) + ".");
        tvItemId.setTextSize(getResources().getDimension(R.dimen.textSize7));
        tvItemId.setTextColor(getResources().getColor(R.color.black));

        TextView tvItemName = new TextView(this);
        tvItemName.setLayoutParams(layoutName);
        tvItemName.setText(jsonObject.optString("itemName"));
        tvItemName.setTextSize(getResources().getDimension(R.dimen.textSize7));
        tvItemName.setTextColor(getResources().getColor(R.color.black));

        TextView tvItemQnty = new TextView(this);
        tvItemQnty.setLayoutParams(layoutQnty);
        tvItemQnty.setText(jsonObject.optString("itemQuantity"));
        tvItemQnty.setTextSize(getResources().getDimension(R.dimen.textSize7));
        tvItemQnty.setGravity(Gravity.CENTER);
        tvItemQnty.setTextColor(getResources().getColor(R.color.black));

        TextView tvItemRate = new TextView(this);
        tvItemRate.setLayoutParams(layoutRate);
        tvItemRate.setText(jsonObject.optString("itemPrice"));
        tvItemRate.setTextSize(getResources().getDimension(R.dimen.textSize7));
        tvItemRate.setGravity(Gravity.CENTER);
        tvItemRate.setPadding(20, 0, 0, 0);
        tvItemRate.setTextColor(getResources().getColor(R.color.black));

        TextView tvItemTotal = new TextView(this);
        tvItemTotal.setLayoutParams(layoutTotal);
        tvItemTotal.setText(getResources().getString(R.string.currency_india) + " "  + jsonObject.optString("itemTotal"));
        tvItemTotal.setTextSize(getResources().getDimension(R.dimen.textSize7));
        tvItemTotal.setGravity(Gravity.END);
        tvItemTotal.setTextColor(getResources().getColor(R.color.black));

        llHorizontalLayout.addView(tvItemId);
        llHorizontalLayout.addView(tvItemName);
        llHorizontalLayout.addView(tvItemQnty);
        llHorizontalLayout.addView(tvItemRate);
        llHorizontalLayout.addView(tvItemTotal);
        llShareBillInfo.addView(llHorizontalLayout);
    }

    @Override
    public void onActivityResult(int requestData, int resultData, Intent data) {
        super.onActivityResult(requestData, resultData, data);

        if (requestData == requestCode)
            finishActivity = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (finishActivity)
            finish();
    }
}