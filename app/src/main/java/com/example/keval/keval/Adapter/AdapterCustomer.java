package com.example.keval.keval.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.keval.keval.Activity.CustomerAccount;
import com.example.keval.keval.Activity.CustomerProfilePic;
import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class AdapterCustomer extends BaseAdapter {

    private LayoutInflater inflater = null;
    private JSONArray data;
    Activity activity;

    public AdapterCustomer(Activity activity, JSONArray data) {
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
        View vi = convertView;
        final JSONObject objectCustomerList = data.optJSONObject(position);

        if (convertView == null)
            vi = inflater.inflate(R.layout.element_fragment_customer, null);

        ImageView cvProfilePhoto;
        TextView tvElementListOfCustomerShopName, tvElementListOfCustomerName, tvElementListOfCustomerNumber,
                tvElementCustomerTotal, tvElementCustomerTotalStatus;
        LinearLayout llElementCustomerCardClick, llProfilePhoto;

        cvProfilePhoto = (ImageView) vi.findViewById(R.id.cvProfilePhoto);
        llElementCustomerCardClick = (LinearLayout) vi.findViewById(R.id.llElementCustomerCardClick);
        llProfilePhoto = (LinearLayout) vi.findViewById(R.id.llProfilePhoto);
        tvElementListOfCustomerShopName = (TextView) vi.findViewById(R.id.tvElementListOfCustomerShopName);
        tvElementListOfCustomerName = (TextView) vi.findViewById(R.id.tvElementListOfCustomerName);
        tvElementListOfCustomerNumber = (TextView) vi.findViewById(R.id.tvElementListOfCustomerNumber);
        tvElementCustomerTotal = (TextView) vi.findViewById(R.id.tvElementCustomerTotal);
        tvElementCustomerTotalStatus = (TextView) vi.findViewById(R.id.tvElementCustomerTotalStatus);

        Picasso.with(activity)
                .load(Uri.fromFile(new File(objectCustomerList.optString("customerPhoto"))))
                .placeholder(R.mipmap.icon_user)
                .fit()
                .centerCrop()
                .error(R.mipmap.icon_user)
                .into(cvProfilePhoto);

        tvElementListOfCustomerShopName.setText(objectCustomerList.optString("shopName"));
        tvElementListOfCustomerNumber.setText(objectCustomerList.optString("shopNumber"));


        tvElementListOfCustomerName.setText(objectCustomerList.optString("customerName"));
        tvElementCustomerTotal.setText(CommonUtils.removeSymbols(activity, CommonUtils.getDecimal(objectCustomerList.optDouble("grandTotal"))));

        if (objectCustomerList.optDouble("grandTotal") < 0)
            tvElementCustomerTotalStatus.setText(" Dr.");
        else
            tvElementCustomerTotalStatus.setText(" Cr.");


        llElementCustomerCardClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, CustomerAccount.class)
                        .putExtra("customerPosition", objectCustomerList.optString("customerId")));
            }
        });

        if (!objectCustomerList.optString("customerPhoto").equalsIgnoreCase(""))
            llProfilePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, CustomerProfilePic.class)
                            .putExtra("customerName", objectCustomerList.optString("customerName"))
                            .putExtra("viewPhoto", objectCustomerList.optString("customerPhoto")));
                }
            });
        else
            llProfilePhoto.setOnClickListener(null);

        return vi;
    }
}