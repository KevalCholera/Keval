package com.example.keval.keval.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.keval.keval.Activity.Product;
import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;
import com.example.keval.keval.Utils.Constants;
import com.mpt.storage.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.example.keval.keval.Fragment.FragmentProduct.onCreateDataSet;

/**
 * Created by keval on 04-Nov-17.
 */

public class AdapterProduct extends BaseAdapter {

    private LayoutInflater inflater = null;
    private JSONArray jsonArray, jsonData;
    private Activity activity;

    public AdapterProduct(Activity activity, JSONArray jsonArray, JSONArray jsonData) {
        this.jsonArray = jsonArray;
        this.jsonData = jsonData;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return jsonArray.length();
    }

    public Object getItem(int position) {
        return jsonArray.optJSONObject(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {
        View vi = convertView;

        final JSONObject jsonObject;
        jsonObject = jsonArray.optJSONObject(jsonArray.length() - (position + 1));

        if (convertView == null)
            vi = inflater.inflate(R.layout.element_fragment_product, null);

        TextView tvElementFragmentStockProductName = (TextView) vi.findViewById(R.id.tvElementFragmentStockProductName);
        TextView tvElementFragmentStockProductPrice = (TextView) vi.findViewById(R.id.tvElementFragmentStockProductPrice);
        TextView tvElementFragmentStockProductQuantity = (TextView) vi.findViewById(R.id.tvElementFragmentStockProductQuantity);
        TextView tvElementFragmentStockProductTax = (TextView) vi.findViewById(R.id.tvElementFragmentStockProductTax);
        TextView ivElementFragmentStockProductEdit = (TextView) vi.findViewById(R.id.ivElementFragmentStockProductEdit);
        TextView ivElementFragmentStockProductDelete = (TextView) vi.findViewById(R.id.ivElementFragmentStockProductDelete);
        LinearLayout llElementFragmentProductClick = (LinearLayout) vi.findViewById(R.id.llElementFragmentProductClick);

        tvElementFragmentStockProductName.setText(jsonObject.optString("itemName"));
        tvElementFragmentStockProductName.setTag(jsonObject.optString("itemId"));
        tvElementFragmentStockProductTax.setText("GST --> " + jsonObject.optString("itemTax") + " %");

        if (jsonObject.optString("itemQuantity").equalsIgnoreCase(""))
            tvElementFragmentStockProductQuantity.setText("Qnty. --> 0.00");
        else
            tvElementFragmentStockProductQuantity.setText("Qnty. --> " + CommonUtils.getDecimal(jsonObject.optDouble("itemQuantity")));

        if (jsonObject.optString("itemPrice").equalsIgnoreCase(""))
            tvElementFragmentStockProductPrice.setText("Price --> " + activity.getResources().getString(R.string.currency_india) + " " + "0.00");
        else
            tvElementFragmentStockProductPrice.setText("Price --> " + activity.getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(jsonObject.optDouble("itemPrice")));

        llElementFragmentProductClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, Product.class).putExtra("position", position).putExtra("onlyShow", true));
            }
        });

        ivElementFragmentStockProductEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, Product.class).putExtra("position", position));
            }
        });

        ivElementFragmentStockProductDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < jsonData.length(); i++) {
                    JSONObject jsonObject1 = jsonData.optJSONObject(i);

                    if (jsonObject1.optString("itemId").equalsIgnoreCase(jsonObject.optString("itemId"))) {
                        alertBox(i, jsonObject.optString("itemName"));
                        break;
                    }
                }
            }
        });

        return vi;
    }

    public void alertBox(final int position, String productName) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle("Delete!");
        builder.setMessage("Are you sure to delete \"" + productName + "\" product ?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {

                CommonUtils.showProgressDialog(activity, "Deleting Product...");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        jsonData = CommonUtils.remove(position, jsonData);

                        SharedPreferenceUtil.putValue(Constants.STOCK_DATA_SAVE, jsonData.toString().substring(1, jsonData.toString().length() - 1));
                        SharedPreferenceUtil.save();
                        onCreateDataSet(activity);

                        dialog.dismiss();
                        CommonUtils.cancelProgressDialog();

                    }
                }, 1000);

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
}