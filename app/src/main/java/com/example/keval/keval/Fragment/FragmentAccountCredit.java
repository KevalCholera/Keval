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

import com.example.keval.keval.Adapter.AdapterAccountCredit;
import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;
import com.example.keval.keval.Utils.Constants;
import com.mpt.storage.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;

public class FragmentAccountCredit extends Fragment implements View.OnClickListener {

    ListView lvFragmentAccountCredit;
    ImageView cvFragmentAccountCreditSearch, cvFragmentAccountCreditSearchClose;
    LinearLayout llSearch, llElementNoDataLayout;
    EditText etSearch;
    boolean checkVisibility = false;
    ImageView ivCancel;
    JSONArray arrayMain = new JSONArray();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_account_credit, container, false);

        lvFragmentAccountCredit = (ListView) rootView.findViewById(R.id.lvFragmentAccountCredit);
        cvFragmentAccountCreditSearch = (ImageView) rootView.findViewById(R.id.cvFragmentAccountCreditSearch);
        cvFragmentAccountCreditSearchClose = (ImageView) rootView.findViewById(R.id.cvFragmentAccountCreditSearchClose);
        etSearch = (EditText) rootView.findViewById(R.id.etSearch);
        ivCancel = (ImageView) rootView.findViewById(R.id.ivCancel);
        llSearch = (LinearLayout) rootView.findViewById(R.id.llSearch);
        llElementNoDataLayout = (LinearLayout) rootView.findViewById(R.id.llElementNoDataLayout);

        etSearch.setHint("Account of Credit");

        try {
            arrayMain = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, "") + "]");

            if (arrayMain.length() > 0) {
                llSearch.setVisibility(View.GONE);
                etSearch.setText("");
                llElementNoDataLayout.setVisibility(View.GONE);
                lvFragmentAccountCredit.setVisibility(View.VISIBLE);
                cvFragmentAccountCreditSearch.setVisibility(View.VISIBLE);
                cvFragmentAccountCreditSearchClose.setVisibility(View.GONE);
            } else {
                llElementNoDataLayout.setVisibility(View.VISIBLE);
                etSearch.setText("");
                lvFragmentAccountCredit.setVisibility(View.GONE);
                llSearch.setVisibility(View.GONE);
                cvFragmentAccountCreditSearch.setVisibility(View.GONE);
                cvFragmentAccountCreditSearchClose.setVisibility(View.GONE);
            }

            cvFragmentAccountCreditSearch.setOnClickListener(this);
            cvFragmentAccountCreditSearchClose.setOnClickListener(this);
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
            lvFragmentAccountCredit.setVisibility(View.VISIBLE);
            lvFragmentAccountCredit.setAdapter(new AdapterAccountCredit(getActivity(), fillJson));
            CommonUtils.showLog(getActivity(), arrayMain);
        } else {
            llElementNoDataLayout.setVisibility(View.VISIBLE);
            lvFragmentAccountCredit.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkVisibility && llSearch.getVisibility() == View.GONE)
            etSearch.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvFragmentAccountCreditSearch:
                llSearch.setVisibility(View.VISIBLE);
                cvFragmentAccountCreditSearch.setVisibility(View.GONE);
                cvFragmentAccountCreditSearchClose.setVisibility(View.VISIBLE);
                checkVisibility = true;
                break;
            case R.id.cvFragmentAccountCreditSearchClose:
                CommonUtils.closeKeyboard(getActivity());
                listCustomer(arrayMain);
                llSearch.setVisibility(View.GONE);
                etSearch.setText("");
                cvFragmentAccountCreditSearchClose.setVisibility(View.GONE);
                cvFragmentAccountCreditSearch.setVisibility(View.VISIBLE);
                llSearch.setVisibility(View.GONE);
                break;
            case R.id.ivCancel:
                etSearch.setText("");
                break;
        }
    }
}
