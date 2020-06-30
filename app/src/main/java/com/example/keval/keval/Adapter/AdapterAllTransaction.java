package com.example.keval.keval.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.keval.keval.Activity.Invoice;
import com.example.keval.keval.Activity.Payment;
import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CircleImageView;
import com.example.keval.keval.Utils.CommonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

public class AdapterAllTransaction extends BaseAdapter {

    private LayoutInflater inflater = null;
    JSONArray data;
    Activity activity;

    public AdapterAllTransaction(Activity activity, JSONArray data) {
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
            view = inflater.inflate(R.layout.element_all_transaction, null);

        CardView cvElementAllTransactionClick = (CardView) view.findViewById(R.id.cvElementAllTransactionClick);
        LinearLayout llElementAllTransactionNameFirstLetter = (LinearLayout) view.findViewById(R.id.llElementAllTransactionNameFirstLetter);
        TextView tvElementAllTransactionDate = (TextView) view.findViewById(R.id.tvElementAllTransactionDate);
        TextView tvElementAllTransactionName = (TextView) view.findViewById(R.id.tvElementAllTransactionName);
        TextView tvElementAllTransactionNo = (TextView) view.findViewById(R.id.tvElementAllTransactionNo);
        TextView tvElementAllTransactionTotalProduct = (TextView) view.findViewById(R.id.tvElementAllTransactionTotalProduct);
        TextView tvElementAllTransactionAmount = (TextView) view.findViewById(R.id.tvElementAllTransactionAmount);
        TextView tvElementAllTransactionNameFirstLetter = (TextView) view.findViewById(R.id.tvElementAllTransactionNameFirstLetter);
        CircleImageView cvElementAllTransactionPaid = (CircleImageView) view.findViewById(R.id.cvElementAllTransactionPaid);

        if (jsonObject.has("billNo")) {

            tvElementAllTransactionName.setText(jsonObject.optString("customerName"));
            tvElementAllTransactionAmount.setText(activity.getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(jsonObject.optDouble("billTotal")));
            tvElementAllTransactionDate.setText(jsonObject.optString("billDate"));
            tvElementAllTransactionNo.setText(activity.getResources().getString(R.string.billing) + " No. " + jsonObject.optString("billNo"));
            tvElementAllTransactionTotalProduct.setText("Total Products (" + jsonObject.optJSONArray("info").length() + ")");
            tvElementAllTransactionNo.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
            tvElementAllTransactionNameFirstLetter.setText(String.valueOf(jsonObject.optString("customerName").charAt(0)));

            GradientDrawable bgShape = (GradientDrawable) llElementAllTransactionNameFirstLetter.getBackground();
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            bgShape.setColor(color);

            llElementAllTransactionNameFirstLetter.setBackgroundResource(R.drawable.circular_view);

            if (jsonObject.optBoolean("dateWise"))
                tvElementAllTransactionDate.setVisibility(View.VISIBLE);
            else
                tvElementAllTransactionDate.setVisibility(View.GONE);

            if (jsonObject.optString("billPaidType").equalsIgnoreCase("More Amount Paid") || jsonObject.optString("billPaidType").equalsIgnoreCase("Amount Paid"))
                cvElementAllTransactionPaid.setColorFilter(ContextCompat.getColor(activity, R.color.green));
            else if (jsonObject.optString("billPaidType").equalsIgnoreCase("Amount not Paid"))
                cvElementAllTransactionPaid.setColorFilter(ContextCompat.getColor(activity, R.color.red1));
            else
                cvElementAllTransactionPaid.setColorFilter(ContextCompat.getColor(activity, R.color.yellow));


            cvElementAllTransactionClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, Invoice.class)
                            .putExtra("onlyShow", jsonObject.toString())
                            .putExtra("AllTransaction", "AllTransaction"));
                }
            });

        } else {

            tvElementAllTransactionName.setText(jsonObject.optString("depositName"));
            tvElementAllTransactionDate.setText(jsonObject.optString("depositDate"));
            tvElementAllTransactionNo.setText(activity.getResources().getString(R.string.deposit) + " No. " + jsonObject.optString("depositNo"));
            tvElementAllTransactionAmount.setText(activity.getResources().getString(R.string.currency_india) + " " + jsonObject.optString("depositAmount"));
            tvElementAllTransactionNo.setTextColor(ContextCompat.getColor(activity, R.color.list_background_pressed));
            tvElementAllTransactionTotalProduct.setText("");
            cvElementAllTransactionPaid.setColorFilter(ContextCompat.getColor(activity, R.color.white));
            tvElementAllTransactionNameFirstLetter.setText(String.valueOf(jsonObject.optString("depositName").charAt(0)));

            GradientDrawable bgShape = (GradientDrawable) llElementAllTransactionNameFirstLetter.getBackground();
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            bgShape.setColor(color);

            llElementAllTransactionNameFirstLetter.setBackgroundResource(R.drawable.circular_view);

            if (jsonObject.optBoolean("dateWise"))
                tvElementAllTransactionDate.setVisibility(View.VISIBLE);
            else
                tvElementAllTransactionDate.setVisibility(View.GONE);

            cvElementAllTransactionClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, Payment.class)
                            .putExtra("onlyShow", jsonObject.toString())
                            .putExtra("AllTransaction", "AllTransaction"));
                }
            });
        }

        return view;
    }
}
