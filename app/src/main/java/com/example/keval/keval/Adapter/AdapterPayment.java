package com.example.keval.keval.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.keval.keval.Activity.Payment;
import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;
import com.example.keval.keval.Utils.Constants;
import com.mpt.storage.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

public class AdapterPayment extends BaseAdapter {

    private LayoutInflater inflater = null;
    private JSONArray data;
    private Activity activity;


    public AdapterPayment(Activity activity, JSONArray data) {
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
            view = inflater.inflate(R.layout.element_fragment_payment, null);

        CardView cvElementFragmentPaymentClick = (CardView) view.findViewById(R.id.cvElementFragmentPaymentClick);
        LinearLayout llElementFragmentPaymentWholeDayAmount = (LinearLayout) view.findViewById(R.id.llElementFragmentPaymentWholeDayAmount);
        LinearLayout llElementFragmentDepositNameFirstLetter = (LinearLayout) view.findViewById(R.id.llElementFragmentDepositNameFirstLetter);
        TextView tvElementFragmentDepositDate = (TextView) view.findViewById(R.id.tvElementFragmentDepositDate);
        TextView tvElementFragmentDepositWholeDayAmount = (TextView) view.findViewById(R.id.tvElementFragmentDepositWholeDayAmount);
        TextView tvElementFragmentDepositNo = (TextView) view.findViewById(R.id.tvElementFragmentDepositNo);
        TextView tvElementFragmentDepositName = (TextView) view.findViewById(R.id.tvElementFragmentDepositName);
        TextView tvElementFragmentDepositAmount = (TextView) view.findViewById(R.id.tvElementFragmentDepositAmount);
        TextView tvElementFragmentDepositNameFirstLetter = (TextView) view.findViewById(R.id.tvElementFragmentDepositNameFirstLetter);

        tvElementFragmentDepositDate.setText(jsonObject.optString("depositDate"));
        tvElementFragmentDepositWholeDayAmount.setText(activity.getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(Double.valueOf(SharedPreferenceUtil.getString(Constants.SAVE_DEPOSIT_BY_DATE + jsonObject.optString("depositDate"), ""))));
        tvElementFragmentDepositNo.setText(activity.getResources().getString(R.string.payment) + " No. " + jsonObject.optString("depositNo"));
        tvElementFragmentDepositName.setText(jsonObject.optString("depositName"));
        tvElementFragmentDepositAmount.setText(activity.getResources().getString(R.string.currency_india) + " " + jsonObject.optString("depositAmount"));
        tvElementFragmentDepositNameFirstLetter.setText(String.valueOf(jsonObject.optString("depositName").charAt(0)));

        GradientDrawable bgShape = (GradientDrawable) llElementFragmentDepositNameFirstLetter.getBackground();
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        bgShape.setColor(color);

        llElementFragmentDepositNameFirstLetter.setBackgroundResource(R.drawable.circular_view);

        if (jsonObject.optBoolean("dateWise")) {
            tvElementFragmentDepositDate.setVisibility(View.VISIBLE);
            llElementFragmentPaymentWholeDayAmount.setVisibility(View.VISIBLE);
        } else {
            tvElementFragmentDepositDate.setVisibility(View.GONE);
            llElementFragmentPaymentWholeDayAmount.setVisibility(View.GONE);
        }

        cvElementFragmentPaymentClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, Payment.class).putExtra("onlyShow", jsonObject.toString()));
            }
        });

        return view;
    }
}