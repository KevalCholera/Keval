package com.example.keval.keval.Fragment;

import android.os.Bundle;
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

import com.example.keval.keval.Adapter.AdapterAccountDeposit;
import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;
import com.example.keval.keval.Utils.Constants;
import com.mpt.storage.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;

public class FragmentAccountDeposit extends Fragment implements View.OnClickListener {

    ListView lvFragmentAccountDeposit;
    ImageView cvFragmentAccountDepositSearch, cvFragmentAccountDepositSearchClose;
    LinearLayout llSearch, llElementNoDataLayout;
    EditText etSearch;
    ImageView ivCancel;
    JSONArray arrayMain = new JSONArray();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_account_deposit, container, false);

        lvFragmentAccountDeposit = (ListView) rootView.findViewById(R.id.lvFragmentAccountDeposit);
        cvFragmentAccountDepositSearch = (ImageView) rootView.findViewById(R.id.cvFragmentAccountDepositSearch);
        cvFragmentAccountDepositSearchClose = (ImageView) rootView.findViewById(R.id.cvFragmentAccountDepositSearchClose);
        etSearch = (EditText) rootView.findViewById(R.id.etSearch);
        ivCancel = (ImageView) rootView.findViewById(R.id.ivCancel);
        llSearch = (LinearLayout) rootView.findViewById(R.id.llSearch);
        llElementNoDataLayout = (LinearLayout) rootView.findViewById(R.id.llElementNoDataLayout);

        etSearch.setHint("Account of Deposit");

        try {

            arrayMain = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, "") + "]");

            if (arrayMain.length() > 0) {
                llSearch.setVisibility(View.GONE);
                cvFragmentAccountDepositSearch.setVisibility(View.VISIBLE);
                cvFragmentAccountDepositSearchClose.setVisibility(View.GONE);
                llElementNoDataLayout.setVisibility(View.GONE);
                lvFragmentAccountDeposit.setVisibility(View.VISIBLE);
            } else {
                llElementNoDataLayout.setVisibility(View.VISIBLE);
                lvFragmentAccountDeposit.setVisibility(View.GONE);
                llSearch.setVisibility(View.GONE);
                cvFragmentAccountDepositSearch.setVisibility(View.GONE);
                cvFragmentAccountDepositSearchClose.setVisibility(View.GONE);
            }

            cvFragmentAccountDepositSearch.setOnClickListener(this);
            cvFragmentAccountDepositSearchClose.setOnClickListener(this);
            ivCancel.setOnClickListener(this);
            listCustomer(arrayMain);

            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (s != null && s.length() > 0 && arrayMain.length() > 0) {
                        ivCancel.setVisibility(View.VISIBLE);
                        listCustomer(CommonUtils.searchArray(arrayMain, etSearch));
                    } else {
                        ivCancel.setVisibility(View.GONE);
                        listCustomer(arrayMain);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    public void listCustomer(JSONArray fillJson) {
        if (fillJson.length() > 0) {
            llElementNoDataLayout.setVisibility(View.GONE);
            lvFragmentAccountDeposit.setVisibility(View.VISIBLE);
            lvFragmentAccountDeposit.setAdapter(new AdapterAccountDeposit(getActivity(), fillJson));
            CommonUtils.showLog(getActivity(), arrayMain);
        } else {
            llElementNoDataLayout.setVisibility(View.VISIBLE);
            lvFragmentAccountDeposit.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.cvFragmentAccountDepositSearch:
                llSearch.setVisibility(View.VISIBLE);
                cvFragmentAccountDepositSearch.setVisibility(View.GONE);
                cvFragmentAccountDepositSearchClose.setVisibility(View.VISIBLE);
                break;
            case R.id.cvFragmentAccountDepositSearchClose:
                CommonUtils.closeKeyboard(getActivity());
                listCustomer(arrayMain);
                llSearch.setVisibility(View.GONE);
                etSearch.setText("");
                cvFragmentAccountDepositSearchClose.setVisibility(View.GONE);
                cvFragmentAccountDepositSearch.setVisibility(View.VISIBLE);
                llSearch.setVisibility(View.GONE);
                break;
            case R.id.ivCancel:
                etSearch.setText("");
                break;

        }
    }
}