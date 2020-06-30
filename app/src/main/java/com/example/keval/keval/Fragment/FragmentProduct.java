package com.example.keval.keval.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.keval.keval.Activity.Product;
import com.example.keval.keval.Adapter.AdapterProduct;
import com.example.keval.keval.Floating.FloatingActionButton;
import com.example.keval.keval.Floating.FloatingActionMenu;
import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;
import com.example.keval.keval.Utils.Constants;
import com.mpt.storage.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FragmentProduct extends Fragment implements View.OnClickListener, TextWatcher {

    static ListView lvFragmentStock;
    static FloatingActionButton ivFragmentStockAddStock;
    static JSONArray jsonData;
    static EditText etSearch;
    static LinearLayout llSearch;
    ImageView ivCancel;
    static FloatingActionMenu ivFragmentStockMenu;
    boolean checkVisibility = false;
    ImageView cvFragmentStockSearch;
    ImageView cvFragmentStock;
    static ImageView cvFragmentStockSearchClose;
    static LinearLayout llElementNoDataLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_product, container, false);
        jsonData = new JSONArray();

        lvFragmentStock = (ListView) view.findViewById(R.id.lvFragmentStock);
        ivFragmentStockAddStock = (FloatingActionButton) view.findViewById(R.id.ivFragmentStockAddStock);
        etSearch = (EditText) view.findViewById(R.id.etSearch);
        llSearch = (LinearLayout) view.findViewById(R.id.llSearch);
        llElementNoDataLayout = (LinearLayout) view.findViewById(R.id.llElementNoDataLayout);
        ivCancel = (ImageView) view.findViewById(R.id.ivCancel);
        cvFragmentStockSearchClose = (ImageView) view.findViewById(R.id.cvFragmentStockSearchClose);
        cvFragmentStock = (ImageView) view.findViewById(R.id.cvFragmentStock);
        cvFragmentStockSearch = (ImageView) view.findViewById(R.id.cvFragmentStockSearch);
        ivFragmentStockMenu = (FloatingActionMenu) view.findViewById(R.id.ivFragmentStockMenu);

        etSearch.setHint("Product Name");
        onCreateDataSet(getActivity());

        etSearch.addTextChangedListener(this);

        ivFragmentStockAddStock.setOnClickListener(this);
        cvFragmentStock.setOnClickListener(this);
        cvFragmentStockSearch.setOnClickListener(this);
        ivFragmentStockMenu.setClosedOnTouchOutside(true);
        cvFragmentStockSearchClose.setOnClickListener(this);
        ivCancel.setOnClickListener(this);

        return view;
    }

    public static void onCreateDataSet(Activity activity) {
        try {
            jsonData = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.STOCK_DATA_SAVE, "") + "]");

            if (jsonData.length() > 0) {
                ivFragmentStockAddStock.setVisibility(View.GONE);
                llSearch.setVisibility(View.GONE);
                ivFragmentStockMenu.setVisibility(View.VISIBLE);
                etSearch.setText("");
                cvFragmentStockSearchClose.setVisibility(View.GONE);
                lvFragmentStock.setVisibility(View.VISIBLE);
                llElementNoDataLayout.setVisibility(View.GONE);
                fillData(activity, jsonData);
            } else {
                etSearch.setText("");
                lvFragmentStock.setVisibility(View.GONE);
                llElementNoDataLayout.setVisibility(View.VISIBLE);
                ivFragmentStockAddStock.setVisibility(View.VISIBLE);
                cvFragmentStockSearchClose.setVisibility(View.GONE);
                ivFragmentStockMenu.setVisibility(View.GONE);
                llSearch.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void fillData(Activity activity, JSONArray jsonArray) {
        CommonUtils.showLog(activity, jsonArray);

        if (jsonData.length() > 0) {
            lvFragmentStock.setVisibility(View.VISIBLE);
            lvFragmentStock.setAdapter(new AdapterProduct(activity, jsonArray, jsonData));
            CommonUtils.setListViewHeightBasedOnChildren(lvFragmentStock);
        } else {
            lvFragmentStock.setVisibility(View.GONE);
            llElementNoDataLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ivFragmentStockAddStock:
                ivFragmentStockMenu.close(true);
                startActivity(new Intent(getActivity(), Product.class));
                break;
            case R.id.cvFragmentStock:
                ivFragmentStockMenu.close(true);
                startActivity(new Intent(getActivity(), Product.class));
                break;
            case R.id.cvFragmentStockSearchClose:
                CommonUtils.closeKeyboard(getActivity());
                ivFragmentStockMenu.close(true);
                fillData(getActivity(), jsonData);
                cvFragmentStockSearchClose.setVisibility(View.GONE);
                ivFragmentStockMenu.setVisibility(View.VISIBLE);
                llSearch.setVisibility(View.GONE);
                break;
            case R.id.cvFragmentStockSearch:
                ivFragmentStockMenu.close(true);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkVisibility = true;
                        llSearch.setVisibility(View.VISIBLE);
                        etSearch.setText("");
                        ivFragmentStockMenu.setVisibility(View.GONE);
                        cvFragmentStockSearchClose.setVisibility(View.VISIBLE);
                    }
                }, 500);
                break;
            case R.id.ivCancel:
                etSearch.setText("");
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkVisibility && llSearch.getVisibility() == View.GONE)
            etSearch.setText("");
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (s != null && s.length() > 0 && jsonData.length() > 0) {

            ivCancel.setVisibility(View.VISIBLE);
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < jsonData.length(); i++) {
                JSONObject jsonObject = jsonData.optJSONObject(i);

                if (jsonObject.optString("itemName").toUpperCase().contains(etSearch.getText().toString().toUpperCase()) ||
                        jsonObject.optString("itemQuantity").toUpperCase().contains(etSearch.getText().toString().toUpperCase()) ||
                        jsonObject.optString("itemPrice").toUpperCase().contains(etSearch.getText().toString().toUpperCase()) ||
                        jsonObject.optString("itemTax").toUpperCase().contains(etSearch.getText().toString().toUpperCase()))
                    jsonArray.put(jsonObject);

            }
            fillData(getActivity(), jsonArray);

        } else {
            ivCancel.setVisibility(View.GONE);
            fillData(getActivity(), jsonData);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}