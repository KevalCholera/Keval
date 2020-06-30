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
import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CircleImageView;
import com.example.keval.keval.Utils.CommonUtils;
import com.example.keval.keval.Utils.Constants;
import com.mpt.storage.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

public class AdapterInvoice extends BaseAdapter {

    private LayoutInflater inflater = null;
    private JSONArray data;
    private Activity activity;

    public AdapterInvoice(Activity activity, JSONArray data) {
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
            view = inflater.inflate(R.layout.element_fragment_invoice, null);

        CardView cvElementFragmentInvoiceClick = (CardView) view.findViewById(R.id.cvElementFragmentInvoiceClick);
        LinearLayout llElementFragmentInvoiceWholeDayAmount = (LinearLayout) view.findViewById(R.id.llElementFragmentInvoiceWholeDayAmount);
        LinearLayout llElementFragmentInvoiceNameFirstLetter = (LinearLayout) view.findViewById(R.id.llElementFragmentInvoiceNameFirstLetter);
        TextView tvElementFragmentInvoiceDate = (TextView) view.findViewById(R.id.tvElementFragmentInvoiceDate);
        TextView tvElementFragmentInvoiceNo = (TextView) view.findViewById(R.id.tvElementFragmentInvoiceNo);
        TextView tvElementFragmentInvoiceName = (TextView) view.findViewById(R.id.tvElementFragmentInvoiceName);
        TextView tvElementFragmentInvoiceTotalProduct = (TextView) view.findViewById(R.id.tvElementFragmentInvoiceTotalProduct);
        TextView tvElementFragmentInvoiceAmount = (TextView) view.findViewById(R.id.tvElementFragmentInvoiceAmount);
        TextView tvElementFragmentInvoiceWholeDayAmount = (TextView) view.findViewById(R.id.tvElementFragmentInvoiceWholeDayAmount);
        TextView tvElementFragmentInvoiceNameFirstLetter = (TextView) view.findViewById(R.id.tvElementFragmentInvoiceNameFirstLetter);
        CircleImageView cvElementFragmentInvoicePaid = (CircleImageView) view.findViewById(R.id.cvElementFragmentInvoicePaid);

        tvElementFragmentInvoiceAmount.setText(activity.getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(jsonObject.optDouble("billTotal")));
        tvElementFragmentInvoiceDate.setText(jsonObject.optString("billDate"));
        tvElementFragmentInvoiceNo.setText(activity.getResources().getString(R.string.billing) + " No." + jsonObject.optString("billNo"));
        tvElementFragmentInvoiceName.setText(jsonObject.optString("customerName"));
        tvElementFragmentInvoiceTotalProduct.setText("Total Products ( " + jsonObject.optJSONArray("info").length() + " )");
        tvElementFragmentInvoiceWholeDayAmount.setText(activity.getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(Double.valueOf(SharedPreferenceUtil.getString(Constants.SAVE_INVOICE_BY_DATE + jsonObject.optString("billDate").trim(), ""))));
        tvElementFragmentInvoiceNameFirstLetter.setText(String.valueOf(jsonObject.optString("customerName").charAt(0)));

        GradientDrawable bgShape = (GradientDrawable) llElementFragmentInvoiceNameFirstLetter.getBackground();
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        bgShape.setColor(color);

        llElementFragmentInvoiceNameFirstLetter.setBackgroundResource(R.drawable.circular_view);

        if (jsonObject.optBoolean("dateWise")) {
            tvElementFragmentInvoiceDate.setVisibility(View.VISIBLE);
            llElementFragmentInvoiceWholeDayAmount.setVisibility(View.VISIBLE);
        } else {
            tvElementFragmentInvoiceDate.setVisibility(View.GONE);
            llElementFragmentInvoiceWholeDayAmount.setVisibility(View.GONE);
        }

        if (jsonObject.optString("billPaidType").equalsIgnoreCase("More Amount Paid") || jsonObject.optString("billPaidType").equalsIgnoreCase("Amount Paid"))
            cvElementFragmentInvoicePaid.setColorFilter(ContextCompat.getColor(activity, R.color.green));
        else if (jsonObject.optString("billPaidType").equalsIgnoreCase("Amount not Paid"))
            cvElementFragmentInvoicePaid.setColorFilter(ContextCompat.getColor(activity, R.color.red1));
        else
            cvElementFragmentInvoicePaid.setColorFilter(ContextCompat.getColor(activity, R.color.yellow));

        cvElementFragmentInvoiceClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, Invoice.class).putExtra("onlyShow", jsonObject.toString()));
            }
        });

        return view;
    }
}
