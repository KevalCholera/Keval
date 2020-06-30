package com.example.keval.keval.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.example.keval.keval.R;
import com.squareup.picasso.Picasso;

import java.io.File;

public class CustomerProfilePic extends AppCompatActivity {

    ImageView ivViewPhotoOriginal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile_pic);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAll);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("customerName"));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivViewPhotoOriginal = (ImageView) findViewById(R.id.ivViewPhotoOriginal);

        Picasso.with(this)
                .load(Uri.fromFile(new File(getIntent().getStringExtra("viewPhoto"))))
                .placeholder(R.mipmap.icon_user)
                .fit()
                .error(R.mipmap.icon_user)
                .into(ivViewPhotoOriginal);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

}