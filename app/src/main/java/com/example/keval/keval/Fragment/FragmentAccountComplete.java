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

import com.example.keval.keval.Adapter.AdapterAccountComplete;
import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;
import com.example.keval.keval.Utils.Constants;
import com.mpt.storage.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;

public class FragmentAccountComplete extends Fragment implements View.OnClickListener {

    ListView lvFragmentAccountComplete;
    ImageView cvFragmentAccountCompleteSearch, cvFragmentAccountCompleteSearchClose;
    LinearLayout llSearch, llElementNoDataLayout;
    EditText etSearch;
    ImageView ivCancel;
    JSONArray arrayMain = new JSONArray();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_account_complete, container, false);

        lvFragmentAccountComplete = (ListView) rootView.findViewById(R.id.lvFragmentAccountComplete);
        cvFragmentAccountCompleteSearch = (ImageView) rootView.findViewById(R.id.cvFragmentAccountCompleteSearch);
        cvFragmentAccountCompleteSearchClose = (ImageView) rootView.findViewById(R.id.cvFragmentAccountCompleteSearchClose);
        etSearch = (EditText) rootView.findViewById(R.id.etSearch);
        ivCancel = (ImageView) rootView.findViewById(R.id.ivCancel);
        llSearch = (LinearLayout) rootView.findViewById(R.id.llSearch);
        llElementNoDataLayout = (LinearLayout) rootView.findViewById(R.id.llElementNoDataLayout);

        etSearch.setHint("Account of Complete");

        try {

            arrayMain = new JSONArray("[" + SharedPreferenceUtil.getString(Constants.CUSTOMER_DATA_SAVE, "") + "]");

            if (arrayMain.length() > 0) {
                llSearch.setVisibility(View.GONE);
                cvFragmentAccountCompleteSearch.setVisibility(View.VISIBLE);
                cvFragmentAccountCompleteSearchClose.setVisibility(View.GONE);
                llElementNoDataLayout.setVisibility(View.GONE);
                lvFragmentAccountComplete.setVisibility(View.VISIBLE);
            } else {
                llElementNoDataLayout.setVisibility(View.VISIBLE);
                lvFragmentAccountComplete.setVisibility(View.GONE);
                llSearch.setVisibility(View.GONE);
                cvFragmentAccountCompleteSearch.setVisibility(View.GONE);
                cvFragmentAccountCompleteSearchClose.setVisibility(View.GONE);
            }

            cvFragmentAccountCompleteSearch.setOnClickListener(this);
            cvFragmentAccountCompleteSearchClose.setOnClickListener(this);
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
            lvFragmentAccountComplete.setVisibility(View.VISIBLE);
            lvFragmentAccountComplete.setAdapter(new AdapterAccountComplete(getActivity(), fillJson));
            CommonUtils.showLog(getActivity(), arrayMain);
        } else {
            llElementNoDataLayout.setVisibility(View.VISIBLE);
            lvFragmentAccountComplete.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.cvFragmentAccountCompleteSearch:
                llSearch.setVisibility(View.VISIBLE);
                cvFragmentAccountCompleteSearch.setVisibility(View.GONE);
                cvFragmentAccountCompleteSearchClose.setVisibility(View.VISIBLE);
                break;
            case R.id.cvFragmentAccountCompleteSearchClose:
                CommonUtils.closeKeyboard(getActivity());
                listCustomer(arrayMain);
                llSearch.setVisibility(View.GONE);
                etSearch.setText("");
                cvFragmentAccountCompleteSearchClose.setVisibility(View.GONE);
                cvFragmentAccountCompleteSearch.setVisibility(View.VISIBLE);
                llSearch.setVisibility(View.GONE);
                break;
            case R.id.ivCancel:
                etSearch.setText("");
                break;

        }
    }
}