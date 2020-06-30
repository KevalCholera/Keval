package com.example.keval.keval.DataImage;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;

import java.io.ByteArrayOutputStream;

public class ShareResultActivity extends AppCompatActivity {

    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CommonUtils.fullActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_result);

        final String sharingText = getIntent().getStringExtra("text");
        byte[] food = getIntent().getByteArrayExtra("picture");
        mBitmap = BitmapFactory.decodeByteArray(food, 0, food.length);
        ImageView ivScreenShot = (ImageView) findViewById(R.id.ivScreenShot);
        //ImageView image = (ImageView) findViewById(R.id.header_logo);
        ivScreenShot.setImageBitmap(mBitmap);

        Button btnShare = (Button) findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getPackageManager();
                try {
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                   /* mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                    float aspectRatio = mBitmap.getWidth() /
                            (float) mBitmap.getHeight();
                    int width = 480;
                    int height = Math.round(width / aspectRatio);

                    mBitmap = Bitmap.createScaledBitmap(
                            mBitmap, width, height, false);*/


                    String path = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "Title", null);
                    Uri imageUri = Uri.parse(path);

                    //PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);

                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("image/*");
//                    waIntent.setPackage("com.whatsapp");
                    waIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    waIntent.putExtra(Intent.EXTRA_TEXT, sharingText);

                    waIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(waIntent, "Share with"));

//                    Uri imageUri1 = Uri.parse(path);
//                    ArrayList<Uri> imageUris = new ArrayList<>();
//                    imageUris.add(imageUri);
//                    imageUris.add(imageUri1);
//
//                    Intent shareIntent = new Intent();
//                    shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
//                    shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
//                    shareIntent.setType("image/*");
//                    startActivity(Intent.createChooser(shareIntent, "Share with"));

                } catch (Exception e) {
                    Log.e("Error on sharing", e + " ");
                    // Toast.makeText(context, "App not Installed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                finish();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}