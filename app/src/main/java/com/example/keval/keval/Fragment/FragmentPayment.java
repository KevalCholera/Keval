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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.keval.keval.Activity.Payment;
import com.example.keval.keval.Adapter.AdapterPayment;
import com.example.keval.keval.Floating.FloatingActionMenu;
import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;
import com.example.keval.keval.Utils.Constants;
import com.mpt.storage.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;

public class FragmentPayment extends Fragment implements View.OnClickListener, TextWatcher {

    ImageView cvFragmentDeposit;
    static ImageView ivCancel;
    static ImageView cvFragmentDepositSearch;
    static ImageView cvFragmentDepositSearchClose;
    static ImageView cvFragmentDepositAdd;
    static EditText etSearch;
    static JSONArray jsonArray;
    static LinearLayout llSearch;
    static LinearLayout llElementNoDataLayout;
    static FloatingActionMenu ivFragmentDepositMenu;
    Calendar mCalendar = null;
    boolean checkVisibility = false;
    static LinearLayout llDate;
    static EditText etFromDate;
    static EditText etToDate;
    final int requestSend = 101;
    static ListView lvListOfPayment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_payment, container, false);

        cvFragmentDeposit = (ImageView) rootView.findViewById(R.id.cvFragmentDeposit);
        cvFragmentDepositSearch = (ImageView) rootView.findViewById(R.id.cvFragmentDepositSearch);
        cvFragmentDepositAdd = (ImageView) rootView.findViewById(R.id.cvFragmentDepositAdd);
        cvFragmentDepositSearchClose = (ImageView) rootView.findViewById(R.id.cvFragmentDepositSearchClose);
        ivCancel = (ImageView) rootView.findViewById(R.id.ivCancel);
        etSearch = (EditText) rootView.findViewById(R.id.etSearch);
        llSearch = (LinearLayout) rootView.findViewById(R.id.llSearch);
        ivFragmentDepositMenu = (FloatingActionMenu) rootView.findViewById(R.id.ivFragmentDepositMenu);
        llDate = (LinearLayout) rootView.findViewById(R.id.llDate);
        llElementNoDataLayout = (LinearLayout) rootView.findViewById(R.id.llElementNoDataLayout);
        etFromDate = (EditText) rootView.findViewById(R.id.etFromDate);
        etToDate = (EditText) rootView.findViewById(R.id.etToDate);
        lvListOfPayment = (ListView) rootView.findViewById(R.id.lvListOfPayment);

        etSearch.setHint(getResources().getString(R.string.deposit));
        mCalendar = Calendar.getInstance();
        jsonArray = new JSONArray();
        onCreateDataSet(getActivity());

        ivFragmentDepositMenu.setClosedOnTouchOutside(true);
        cvFragmentDepositSearch.setOnClickListener(this);
        cvFragmentDepositAdd.setOnClickListener(this);
        cvFragmentDepositSearchClose.setOnClickListener(this);
        cvFragmentDeposit.setOnClickListener(this);
        etFromDate.setOnClickListener(this);
        etToDate.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
        etSearch.addTextChangedListener(this);

        return rootView;
    }

    public static void onCreateDataSet(Activity activity) {
        try {
            if (!SharedPreferenceUtil.getString(Constants.DEPOSIT_DATA_SAVE, "").equalsIgnoreCase(""))
                jsonArray = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.DEPOSIT_DATA_SAVE, "") + "]");

            if (jsonArray.length() == 0) {
                llSearch.setVisibility(View.GONE);
                llDate.setVisibility(View.GONE);
                etSearch.setText("");
                etFromDate.setText("");
                etToDate.setText("");
                ivFragmentDepositMenu.setVisibility(View.GONE);
                cvFragmentDepositAdd.setVisibility(View.VISIBLE);
                cvFragmentDepositSearchClose.setVisibility(View.GONE);
                lvListOfPayment.setVisibility(View.GONE);
                llElementNoDataLayout.setVisibility(View.VISIBLE);
            } else {
                llSearch.setVisibility(View.GONE);
                llDate.setVisibility(View.GONE);
                etSearch.setText("");
                etFromDate.setText("");
                etToDate.setText("");
                cvFragmentDepositSearchClose.setVisibility(View.GONE);
                ivFragmentDepositMenu.setVisibility(View.VISIBLE);
                cvFragmentDepositAdd.setVisibility(View.GONE);
                lvListOfPayment.setVisibility(View.VISIBLE);
                llElementNoDataLayout.setVisibility(View.GONE);

                CommonUtils.showLog(activity, jsonArray);
                fillData(activity, jsonArray);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void fillData(Activity activity, JSONArray fillAJsonArray) {

        if (fillAJsonArray.length() > 0) {
            lvListOfPayment.setVisibility(View.VISIBLE);
            llElementNoDataLayout.setVisibility(View.GONE);
            lvListOfPayment.setAdapter(new AdapterPayment(activity, fillAJsonArray));
        } else {
            lvListOfPayment.setVisibility(View.GONE);
            llElementNoDataLayout.setVisibility(View.VISIBLE);
        }
    }

    public void optionSearch() {
        final CharSequence[] options;
        options = new CharSequence[]{"Search by Date", "Search by " + getResources().getString(R.string.deposit)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose source option!");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Search by Date")) {

                    llDate.setVisibility(View.VISIBLE);
                    ivFragmentDepositMenu.setVisibility(View.GONE);
                    cvFragmentDepositSearchClose.setVisibility(View.VISIBLE);
                    dialog.dismiss();

                } else if (options[item].equals("Search by " + getResources().getString(R.string.deposit))) {

                    llSearch.setVisibility(View.VISIBLE);
                    ivFragmentDepositMenu.setVisibility(View.GONE);
                    cvFragmentDepositSearchClose.setVisibility(View.VISIBLE);
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
            if (llSearch.getVisibility() == View.GONE) {
                etSearch.setText("");
            }
            if (llDate.getVisibility() == View.GONE) {
                etFromDate.setText("");
                etToDate.setText("");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvFragmentDeposit:
                ivFragmentDepositMenu.close(true);
                startActivityForResult(new Intent(getActivity(), Payment.class), requestSend);
                break;
            case R.id.cvFragmentDepositSearchClose:
                CommonUtils.closeKeyboard(getActivity());
                etSearch.setText("");
                etFromDate.setText("");
                etToDate.setText("");
                llSearch.setVisibility(View.GONE);
                llDate.setVisibility(View.GONE);
                ivFragmentDepositMenu.setVisibility(View.VISIBLE);
                cvFragmentDepositSearchClose.setVisibility(View.GONE);
                fillData(getActivity(), jsonArray);
                break;
            case R.id.cvFragmentDepositAdd:
                startActivityForResult(new Intent(getActivity(), Payment.class), requestSend);
                break;
            case R.id.cvFragmentDepositSearch:
                checkVisibility = true;
                ivFragmentDepositMenu.close(true);

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
        if (s != null && s.length() > 0 && jsonArray.length() > 0) {

            ivCancel.setVisibility(View.VISIBLE);
            fillData(getActivity(), CommonUtils.searchArray(jsonArray, etSearch));

        } else {
            ivCancel.setVisibility(View.GONE);
            fillData(getActivity(), jsonArray);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}