package com.example.keval.keval.Fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.keval.keval.Activity.Invoice;
import com.example.keval.keval.Adapter.AdapterInvoice;
import com.example.keval.keval.Floating.FloatingActionMenu;
import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;
import com.example.keval.keval.Utils.Constants;
import com.mpt.storage.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;

public class FragmentInvoice extends Fragment implements View.OnClickListener, TextWatcher {

    ImageView cvFragmentBilling;
    static ImageView ivCancel;
    static ImageView cvFragmentBillingSearchClose;
    static ImageView cvFragmentBillingSearch;
    static ImageView cvFragmentBillingAdd;
    static JSONArray jsonArray;
    static EditText etSearch;
    static FloatingActionMenu ivFragmentBillingMenu;
    private static LinearLayout llSearch;
    private static LinearLayout llElementNoDataLayout;
    boolean checkVisibility = false;
    static LinearLayout llDate;
    static EditText etFromDate;
    static EditText etToDate;
    Calendar mCalendar = Calendar.getInstance();
    static ListView lvListOfInvoice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_invoice, container, false);

        cvFragmentBilling = (ImageView) rootView.findViewById(R.id.cvFragmentBilling);
        cvFragmentBillingSearchClose = (ImageView) rootView.findViewById(R.id.cvFragmentBillingSearchClose);
        cvFragmentBillingSearch = (ImageView) rootView.findViewById(R.id.cvFragmentBillingSearch);
        cvFragmentBillingAdd = (ImageView) rootView.findViewById(R.id.cvFragmentBillingAdd);
        ivCancel = (ImageView) rootView.findViewById(R.id.ivCancel);
        etSearch = (EditText) rootView.findViewById(R.id.etSearch);
        llSearch = (LinearLayout) rootView.findViewById(R.id.llSearch);
        ivFragmentBillingMenu = (FloatingActionMenu) rootView.findViewById(R.id.ivFragmentBillingMenu);
        llDate = (LinearLayout) rootView.findViewById(R.id.llDate);
        etFromDate = (EditText) rootView.findViewById(R.id.etFromDate);
        etToDate = (EditText) rootView.findViewById(R.id.etToDate);
        llElementNoDataLayout = (LinearLayout) rootView.findViewById(R.id.llElementNoDataLayout);
        lvListOfInvoice = (ListView) rootView.findViewById(R.id.lvListOfInvoice);

        etSearch.setHint(getResources().getString(R.string.billing));
        mCalendar = Calendar.getInstance();

        jsonArray = new JSONArray();
        onCreateDataSet(getActivity());

        ivFragmentBillingMenu.setClosedOnTouchOutside(true);
        cvFragmentBillingSearch.setOnClickListener(this);
        cvFragmentBillingSearchClose.setOnClickListener(this);
        cvFragmentBillingAdd.setOnClickListener(this);
        cvFragmentBilling.setOnClickListener(this);
        etFromDate.setOnClickListener(this);
        etToDate.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
        etSearch.addTextChangedListener(this);

        return rootView;
    }

    public static void onCreateDataSet(Activity activity) {
        try {

            if (!SharedPreferenceUtil.getString(Constants.BILL_DATA_SAVE, "").equalsIgnoreCase(""))
                jsonArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.BILL_DATA_SAVE, "") + "]");

            if (jsonArray.length() == 0) {
                llSearch.setVisibility(View.GONE);
                llDate.setVisibility(View.GONE);
                etSearch.setText("");
                etFromDate.setText("");
                etToDate.setText("");
                cvFragmentBillingAdd.setVisibility(View.VISIBLE);
                cvFragmentBillingSearchClose.setVisibility(View.GONE);
                ivFragmentBillingMenu.setVisibility(View.GONE);
                lvListOfInvoice.setVisibility(View.GONE);
                llElementNoDataLayout.setVisibility(View.VISIBLE);
            } else {
                lvListOfInvoice.setVisibility(View.VISIBLE);
                llElementNoDataLayout.setVisibility(View.GONE);
                llSearch.setVisibility(View.GONE);
                etSearch.setText("");
                etFromDate.setText("");
                etToDate.setText("");
                llDate.setVisibility(View.GONE);
                cvFragmentBillingSearchClose.setVisibility(View.GONE);
                cvFragmentBillingAdd.setVisibility(View.GONE);
                ivFragmentBillingMenu.setVisibility(View.VISIBLE);
                CommonUtils.showLog(activity, jsonArray);

                fillData(activity, jsonArray);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void fillData(Activity activity, JSONArray jsonArray) {

        if (jsonArray.length() > 0) {
            lvListOfInvoice.setVisibility(View.VISIBLE);
            lvListOfInvoice.setAdapter(new AdapterInvoice(activity, jsonArray));
            llElementNoDataLayout.setVisibility(View.GONE);
        } else {
            lvListOfInvoice.setVisibility(View.GONE);
            llElementNoDataLayout.setVisibility(View.VISIBLE);
        }
    }

    public void optionSearch() {
        final CharSequence[] options;
        options = new CharSequence[]{"Search by Date", "Search by " + getResources().getString(R.string.billing)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose source option!");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Search by Date")) {

                    llDate.setVisibility(View.VISIBLE);
                    ivFragmentBillingMenu.setVisibility(View.GONE);
                    cvFragmentBillingSearchClose.setVisibility(View.VISIBLE);
                    dialog.dismiss();

                } else if (options[item].equals("Search by " + getResources().getString(R.string.billing))) {

                    llSearch.setVisibility(View.VISIBLE);
                    ivFragmentBillingMenu.setVisibility(View.GONE);
                    cvFragmentBillingSearchClose.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
            }
        });
        builder.show();
        builder.setCancelable(true);
    }

    public void datePicker(final EditText etDate, final boolean checkFromDate) {

        DatePickerDialog DatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker DatePicker, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                etDate.setText(CommonUtils.dateFormat(mCalendar.getTimeInMillis()));

                if (!etToDate.getText().toString().equalsIgnoreCase("")) {

                    Calendar newCalender = Calendar.getInstance();
                    newCalender.setTime(CommonUtils.convertStringToDate(etToDate.getText().toString()));

                    if (checkFromDate && mCalendar.getTimeInMillis() > newCalender.getTimeInMillis())
                        etToDate.setText(etFromDate.getText().toString());
                }

                if (CommonUtils.convertStringToDate(etFromDate.getText().toString()).getTime() <= jsonArray.optJSONObject(0).optLong("dateLongHigh"))
                    fillData(getActivity(), CommonUtils.searchByDate(getActivity(), etToDate, etFromDate, jsonArray));
                else if (CommonUtils.convertStringToDate(etFromDate.getText().toString()).getTime() > jsonArray.optJSONObject(0).optLong("dateLongHigh"))
                    fillData(getActivity(), new JSONArray());

            }

        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));

        DatePicker.show();
        DatePicker.setCancelable(false);
        DatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());

        if (!checkFromDate)
            DatePicker.getDatePicker().setMinDate(CommonUtils.convertStringToDate(etFromDate.getText().toString()).getTime());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkVisibility) {
            if (llSearch.getVisibility() == View.GONE)
                etSearch.setText("");
            if (llDate.getVisibility() == View.GONE) {
                etFromDate.setText("");
                etToDate.setText("");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvFragmentBillingSearchClose:
                CommonUtils.closeKeyboard(getActivity());
                etSearch.setText("");
                etFromDate.setText("");
                etToDate.setText("");
                llSearch.setVisibility(View.GONE);
                llDate.setVisibility(View.GONE);
                ivFragmentBillingMenu.setVisibility(View.VISIBLE);
                cvFragmentBillingSearchClose.setVisibility(View.GONE);
                fillData(getActivity(), jsonArray);
                break;
            case R.id.cvFragmentBilling:
                ivFragmentBillingMenu.close(true);
                startActivity(new Intent(getActivity(), Invoice.class));
                break;
            case R.id.cvFragmentBillingAdd:
                startActivity(new Intent(getActivity(), Invoice.class));
                break;
            case R.id.cvFragmentBillingSearch:
                checkVisibility = true;
                ivFragmentBillingMenu.close(true);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        optionSearch();
                    }
                }, 500);
                break;
            case R.id.etFromDate:
                datePicker(etFromDate, true);
                break;
            case R.id.etToDate:
                if (TextUtils.isEmpty(etFromDate.getText().toString()))
                    Toast.makeText(getActivity(), "Select From Date First", Toast.LENGTH_SHORT).show();
                else
                    datePicker(etToDate, false);
                break;
            case R.id.ivCancel:
                etSearch.setText("");
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s != null && s.length() > 0) {

            if (jsonArray.length() > 0) {
                ivCancel.setVisibility(View.VISIBLE);
                fillData(getActivity(), CommonUtils.searchArray(jsonArray, etSearch));
            }

        } else {
            Log.i("jsonArray", jsonArray.toString());
            ivCancel.setVisibility(View.GONE);
            fillData(getActivity(), jsonArray);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}