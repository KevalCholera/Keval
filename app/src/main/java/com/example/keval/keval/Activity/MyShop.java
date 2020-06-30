package com.example.keval.keval.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.keval.keval.Fragment.FragmentCustomer;
import com.example.keval.keval.Fragment.FragmentInvoice;
import com.example.keval.keval.Fragment.FragmentPayment;
import com.example.keval.keval.Fragment.FragmentProduct;
import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;
import com.mpt.storage.SharedPreferenceUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class MyShop extends AppCompatActivity {

    ViewPager vpCategoryItems;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    LinearLayout llMyShop;
    private boolean exit = false;
    private TabLayout tbMyShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_shop);
        getSupportActionBar().setTitle(getResources().getString(R.string.billing));
        SharedPreferenceUtil.init(this);

        vpCategoryItems = (ViewPager) findViewById(R.id.vpCategoryItems);
        llMyShop = (LinearLayout) findViewById(R.id.llMyShop);
        tbMyShop = (TabLayout) findViewById(R.id.tbMyShop);

        calcHeightForTabLayout();

        final ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(getResources().getString(R.string.billing));
        arrayList.add(getResources().getString(R.string.payment));
        arrayList.add(getResources().getString(R.string.stock));
        arrayList.add(getResources().getString(R.string.customers));

        setupViewPager(vpCategoryItems);
        tbMyShop.setupWithViewPager(vpCategoryItems);

        vpCategoryItems.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                getSupportActionBar().setTitle(arrayList.get(position));
                CommonUtils.closeKeyboard(MyShop.this);
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

    public void calcHeightForTabLayout() {

        llMyShop.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    //noinspection deprecation
                    llMyShop.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    llMyShop.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                LinearLayout.LayoutParams layoutTotal;
                layoutTotal = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, llMyShop.getMeasuredHeight() / 12);
                layoutTotal.setMargins(0, 0, 0, 0);

                tbMyShop.setLayoutParams(layoutTotal);
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentInvoice(), getResources().getString(R.string.billing));
        adapter.addFragment(new FragmentPayment(), getResources().getString(R.string.payment));
        adapter.addFragment(new FragmentProduct(), getResources().getString(R.string.stock));
        adapter.addFragment(new FragmentCustomer(), getResources().getString(R.string.customers));
        viewPager.setAdapter(adapter);
    }

    public void storageRequest() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                startActivity(new Intent(this, More.class));
            else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else
            startActivity(new Intent(this, More.class));
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void deleteCache() {
        try {
            File dir = getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
        }
    }

    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                    startActivity(new Intent(this, More.class));
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_myshop, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuMore:
                storageRequest();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
//            deleteCache();
        } else {
            Toast.makeText(this, "Press Back again to Exit.", Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 5000);
        }
    }
}