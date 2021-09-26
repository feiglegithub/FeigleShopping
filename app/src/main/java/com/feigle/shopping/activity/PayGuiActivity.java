package com.feigle.shopping.activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.feigle.shopping.R;
import com.feigle.shopping.utils.PermissionUtils;
import com.feigle.shopping.utils.Utils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.lxj.xpopup.interfaces.OnConfirmListener;

import java.security.Permission;

public class PayGuiActivity extends AppCompatActivity {

    private XPopup.Builder mXPopup;
    private LoadingPopupView mLoadingPopupView;
    private ImageView alipayImageView;
    private ImageView wechatImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_gui);

        initView();
    }

    private void initView() {
        mXPopup = new XPopup.Builder(this);
        mLoadingPopupView = mXPopup.asLoading(getString(R.string.saving));

        alipayImageView = findViewById(R.id.alipayImageView);
        wechatImageView = findViewById(R.id.wechatImageView);
        Glide.with(this).load(R.drawable.alipay_anim).into(alipayImageView);
        Glide.with(this).load(R.drawable.wechat_anim).into(wechatImageView);
    }

    private void save() {
        mLoadingPopupView.show();
        Bitmap alipay = BitmapFactory.decodeResource(getResources(), R.drawable.alipay_huabei);
        Utils.saveImageToGallery(getApplicationContext(), alipay, "alipay.jpg");

        Bitmap wechart = BitmapFactory.decodeResource(getResources(), R.drawable.wechat_code);
        Utils.saveImageToGallery(getApplicationContext(), wechart, "wechat.jpg");
        mLoadingPopupView.dismiss();
        Toast.makeText(getApplicationContext(), R.string.tip_save_success, Toast.LENGTH_LONG).show();
//                mXPopup.asConfirm(getString(R.string.tip), getString(R.string.tip_save_success), new OnConfirmListener() {
//                    @Override
//                    public void onConfirm() {
//
//                    }
//                }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtils.MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    save();
                } else {
                    Toast.makeText(this, R.string.fail_save_code, Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveButton:
                if (PermissionUtils.checkDangerousPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
                    save();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionUtils.MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }

                break;
            default:
                break;
        }
    }
}
