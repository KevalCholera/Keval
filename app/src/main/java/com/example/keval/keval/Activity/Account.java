package com.example.keval.keval.Activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.example.keval.keval.Fragment.FragmentAccountComplete;
import com.example.keval.keval.Fragment.FragmentAccountCredit;
import com.example.keval.keval.Fragment.FragmentAccountDeposit;
import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class Account extends AppCompatActivity {

    ViewPager vpAccount;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    TabLayout tbAccount;
    LinearLayout llAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        getSupportActionBar().setTitle("Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        vpAccount = (ViewPager) findViewById(R.id.vpAccount);
        tbAccount = (TabLayout) findViewById(R.id.tbAccount);
        llAccount = (LinearLayout) findViewById(R.id.llAccount);

        setupViewPager(vpAccount);
        tbAccount.setupWithViewPager(vpAccount);

        vpAccount.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                CommonUtils.closeKeyboard(Account.this);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrollStateChanged(int pos) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentAccountCredit(), getResources().getString(R.string.credit));
        adapter.addFragment(new FragmentAccountDeposit(), getResources().getString(R.string.deposit));
        adapter.addFragment(new FragmentAccountComplete(), getResources().getString(R.string.complete));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                CommonUtils.closeKeyboard(this);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
