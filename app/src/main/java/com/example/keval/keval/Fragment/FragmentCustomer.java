package com.example.keval.keval.Fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.keval.keval.Activity.Customer;
import com.example.keval.keval.Adapter.AdapterCustomer;
import com.example.keval.keval.Floating.FloatingActionMenu;
import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CircleImageView;
import com.example.keval.keval.Utils.CommonUtils;
import com.example.keval.keval.Utils.Constants;
import com.mpt.storage.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class FragmentCustomer extends Fragment implements View.OnClickListener, TextWatcher {

    static ListView lvListOfCustomer;
    ImageView cvListOfCustomerAddCustomer;
    ImageView ivCancel;
    ImageView cvFragmentCustomerSearch;
    static ImageView cvFragmentCustomerSearchClose;
    static ImageView cvFragmentCustomerAdd;
    ImageView cvFragmentCustomerSort;
    static EditText etSearch;
    static JSONArray jsonArray;
    static FloatingActionMenu ivFragmentBillingMenu;
    static ArrayList<String> ascendingArray = new ArrayList<>();
    ArrayList<String> descendingArray = new ArrayList<>();
    static LinearLayout llSearch;
    boolean checkVisibility = false;
    static LinearLayout llElementNoDataLayout;
    LinearLayout llElementCustomerCardClick;
    CircleImageView cvProfilePhoto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customers, container, false);
        ascendingArray = new ArrayList<>();
        descendingArray = new ArrayList<>();

        lvListOfCustomer = (ListView) rootView.findViewById(R.id.lvListOfCustomer);
        cvListOfCustomerAddCustomer = (ImageView) rootView.findViewById(R.id.cvListOfCustomerAddCustomer);
        cvFragmentCustomerSearch = (ImageView) rootView.findViewById(R.id.cvFragmentCustomerSearch);
        cvFragmentCustomerAdd = (ImageView) rootView.findViewById(R.id.cvFragmentCustomerAdd);
        cvFragmentCustomerSort = (ImageView) rootView.findViewById(R.id.cvFragmentCustomerSort);
        cvFragmentCustomerSearchClose = (ImageView) rootView.findViewById(R.id.cvFragmentCustomerSearchClose);
        etSearch = (EditText) rootView.findViewById(R.id.etSearch);
        ivCancel = (ImageView) rootView.findViewById(R.id.ivCancel);
        llSearch = (LinearLayout) rootView.findViewById(R.id.llSearch);
        llElementNoDataLayout = (LinearLayout) rootView.findViewById(R.id.llElementNoDataLayout);
        ivFragmentBillingMenu = (FloatingActionMenu) rootView.findViewById(R.id.ivFragmentBillingMenu);
        llElementCustomerCardClick = (LinearLayout) rootView.findViewById(R.id.llElementCustomerCardClick);
        cvProfilePhoto = (CircleImageView) rootView.findViewById(R.id.cvProfilePhoto);

        etSearch.setHint(getResources().getString(R.string.customers));
        ivFragmentBillingMenu.setClosedOnTouchOutside(true);

        onCreateSetData(getActivity());

        cvFragmentCustomerSearch.setOnClickListener(this);
        cvFragmentCustomerSort.setOnClickListener(this);
        cvFragmentCustomerAdd.setOnClickListener(this);
        cvFragmentCustomerSearchClose.setOnClickListener(this);
        cvListOfCustomerAddCustomer.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
        etSearch.addTextChangedListener(this);

        return rootView;
    }

    public static void onCreateSetData(Activity activity) {
        try {
            String dataCustomer = "[" + SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, "") + "]";
            jsonArray = new JSONArray(dataCustomer);

            if (jsonArray.length() == 0) {
                llSearch.setVisibility(View.GONE);
                ivFragmentBillingMenu.setVisibility(View.GONE);
                cvFragmentCustomerAdd.setVisibility(View.VISIBLE);
                llElementNoDataLayout.setVisibility(View.VISIBLE);
                lvListOfCustomer.setVisibility(View.GONE);
                cvFragmentCustomerSearchClose.setVisibility(View.GONE);
                etSearch.setText("");
            } else {
                llElementNoDataLayout.setVisibility(View.GONE);
                lvListOfCustomer.setVisibility(View.VISIBLE);
                cvFragmentCustomerSearchClose.setVisibility(View.GONE);
                llSearch.setVisibility(View.GONE);
                etSearch.setText("");
                ivFragmentBillingMenu.setVisibility(View.VISIBLE);
                cvFragmentCustomerAdd.setVisibility(View.GONE);

                CommonUtils.showLog(activity, jsonArray);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    ascendingArray.add(jsonObject.optString("customerName").toUpperCase());
                }

                Collections.sort(ascendingArray);

                lvListOfCustomer.setAdapter(new AdapterCustomer(activity, jsonArray));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void listCustomer(JSONArray fillJson) {
        if (fillJson.length() > 0) {
            llElementNoDataLayout.setVisibility(View.GONE);
            lvListOfCustomer.setVisibility(View.VISIBLE);
            lvListOfCustomer.setAdapter(new AdapterCustomer(getActivity(), fillJson));
        } else {
            llElementNoDataLayout.setVisibility(View.VISIBLE);
            lvListOfCustomer.setVisibility(View.GONE);
        }
    }

    public void deleteCustomer(int position) {
        if (SharedPreferenceUtil.contains(Constants.CUSTOMER_DATA_SAVE) && !TextUtils.isEmpty(SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, ""))) {
            String oldUserData = SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, "");
            try {
                JSONArray deletedJson = new JSONArray("[" + oldUserData + "]");

                deletedJson = CommonUtils.remove(position, deletedJson);

                SharedPreferenceUtil.putValue(Constants.CUSTOMER_DATA_SAVE, deletedJson.toString().substring(1, deletedJson.toString().length() - 1));
                SharedPreferenceUtil.save();

                listCustomer(deletedJson);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void alertBox(final int position) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage("Are you sure you want to delete?");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteCustomer(position);
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvFragmentCustomerSearch:
                ivFragmentBillingMenu.close(true);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkVisibility = true;
                        llSearch.setVisibility(View.VISIBLE);
                        ivFragmentBillingMenu.setVisibility(View.GONE);
                        cvFragmentCustomerSearchClose.setVisibility(View.VISIBLE);
                    }
                }, 500);
                break;
            case R.id.cvFragmentCustomerSort:
                ivFragmentBillingMenu.close(true);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        optionSort();
                    }
                }, 500);
                break;
            case R.id.cvFragmentCustomerAdd:
                ivFragmentBillingMenu.close(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getActivity(), Customer.class));
                    }
                }, 500);

                break;
            case R.id.cvFragmentCustomerSearchClose:
                CommonUtils.closeKeyboard(getActivity());
                ivFragmentBillingMenu.close(true);
                listCustomer(jsonArray);
                cvFragmentCustomerSearchClose.setVisibility(View.GONE);
                ivFragmentBillingMenu.setVisibility(View.VISIBLE);
                llSearch.setVisibility(View.GONE);
                break;
            case R.id.cvListOfCustomerAddCustomer:
                ivFragmentBillingMenu.close(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getActivity(), Customer.class));
                    }
                }, 500);

                break;
            case R.id.ivCancel:
                etSearch.setText("");
                break;
        }
    }

    public void optionSort() {
        final CharSequence[] options;
        options = new CharSequence[]{"Sort Ascending", "Sort Descending"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose source option!");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Sort Ascending")) {

                    checkVisibility = false;
                    ivFragmentBillingMenu.setVisibility(View.GONE);
                    cvFragmentCustomerSearchClose.setVisibility(View.VISIBLE);
                    listCustomer(CommonUtils.sorting(ascendingArray, jsonArray, "customerName"));

                    dialog.dismiss();

                } else if (options[item].equals("Sort Descending")) {

                    for (int i = ascendingArray.size() - 1; i >= 0; i--)
                        descendingArray.add(ascendingArray.get(i));

                    checkVisibility = false;
                    ivFragmentBillingMenu.setVisibility(View.GONE);
                    cvFragmentCustomerSearchClose.setVisibility(View.VISIBLE);
                    listCustomer(CommonUtils.sorting(descendingArray, jsonArray, "customerName"));

                    dialog.dismiss();

                }
            }
        });
        builder.show();
        builder.setCancelable(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkVisibility && llSearch.getVisibility() == View.GONE)
            etSearch.setText("");
        super.onPause();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (s != null && s.length() > 0 && jsonArray.length() > 0) {

            ivCancel.setVisibility(View.VISIBLE);
            listCustomer(CommonUtils.searchArray(jsonArray, etSearch));

        } else {
            ivCancel.setVisibility(View.GONE);
            listCustomer(jsonArray);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
