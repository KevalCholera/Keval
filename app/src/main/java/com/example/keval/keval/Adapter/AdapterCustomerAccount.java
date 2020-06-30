package com.example.keval.keval.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.keval.keval.Activity.Invoice;
import com.example.keval.keval.Activity.Payment;
import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CircleImageView;
import com.example.keval.keval.Utils.CommonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class AdapterCustomerAccount extends BaseAdapter {

    private LayoutInflater inflater = null;
    JSONArray data;
    Activity activity;

    public AdapterCustomerAccount(Activity activity, JSONArray data) {
        this.data = data;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        View view = convertView;
        final JSONObject jsonObject = data.optJSONObject(position);

        if (convertView == null)
            view = inflater.inflate(R.layout.element_customer_account, null);

        CardView cvElementCustomerAccountClick = (CardView) view.findViewById(R.id.cvElementCustomerAccountClick);
        TextView tvElementCustomerAccountDate = (TextView) view.findViewById(R.id.tvElementCustomerAccountDate);
        TextView tvElementCustomerAccountNo = (TextView) view.findViewById(R.id.tvElementCustomerAccountNo);
        TextView tvElementCustomerAccountTotalProduct = (TextView) view.findViewById(R.id.tvElementCustomerAccountTotalProduct);
        TextView tvElementCustomerAccountAmount = (TextView) view.findViewById(R.id.tvElementCustomerAccountAmount);
        CircleImageView cvElementCustomerAccountPaid = (CircleImageView) view.findViewById(R.id.cvElementCustomerAccountPaid);

        if (jsonObject.has("billNo")) {

            tvElementCustomerAccountAmount.setText(activity.getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(jsonObject.optDouble("billTotal")));
            tvElementCustomerAccountDate.setText(jsonObject.optString("billDate"));
            tvElementCustomerAccountNo.setText(activity.getResources().getString(R.string.billing) + " No. " + jsonObject.optString("billNo"));
            tvElementCustomerAccountTotalProduct.setText("Total Products (" + jsonObject.optJSONArray("info").length() + ")");
            tvElementCustomerAccountNo.setTextColor(activity.getResources().getColor(R.color.colorPrimary));

            if (jsonObject.optBoolean("dateWise"))
                tvElementCustomerAccountDate.setVisibility(View.VISIBLE);
            else
                tvElementCustomerAccountDate.setVisibility(View.GONE);

            if (jsonObject.optString("billPaidType").equalsIgnoreCase("More Amount Paid") || jsonObject.optString("billPaidType").equalsIgnoreCase("Amount Paid"))
                cvElementCustomerAccountPaid.setColorFilter(ContextCompat.getColor(activity, R.color.green));
            else if (jsonObject.optString("billPaidType").equalsIgnoreCase("Amount not Paid"))
                cvElementCustomerAccountPaid.setColorFilter(ContextCompat.getColor(activity, R.color.red1));
            else
                cvElementCustomerAccountPaid.setColorFilter(ContextCompat.getColor(activity, R.color.yellow));

            cvElementCustomerAccountClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, Invoice.class)
                            .putExtra("onlyShow", jsonObject.toString())
                            .putExtra("customerAccount", "customerAccount"));
                }
            });

        } else {

            tvElementCustomerAccountDate.setText(jsonObject.optString("depositDate"));
            tvElementCustomerAccountNo.setText(activity.getResources().getString(R.string.payment) + " No. " + jsonObject.optString("depositNo"));
            tvElementCustomerAccountNo.setTextColor(activity.getResources().getColor(R.color.list_background_pressed));
            tvElementCustomerAccountAmount.setText("");
            cvElementCustomerAccountPaid.setColorFilter(ContextCompat.getColor(activity, R.color.white));
            tvElementCustomerAccountTotalProduct.setText(activity.getResources().getString(R.string.currency_india) + " " + jsonObject.optString("depositAmount"));

            if (jsonObject.optBoolean("dateWise"))
                tvElementCustomerAccountDate.setVisibility(View.VISIBLE);
            else
                tvElementCustomerAccountDate.setVisibility(View.GONE);

            cvElementCustomerAccountClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, Payment.class)
                            .putExtra("onlyShow", jsonObject.toString())
                            .putExtra("customerAccount", "customerAccount"));
                }
            });
        }

        return view;
    }
}
