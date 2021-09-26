package com.feigle.shopping.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.feigle.shopping.R;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;

public class PermissionUtils {
    public final static int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 1;

    /**
     * 检查是否已被授权危险权限
     *
     * @param permissions
     * @return
     */
    public static boolean checkDangerousPermissions(final Activity ac, final String[] permissions) {
        XPopup.Builder builder = new XPopup.Builder(ac);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(ac, permission) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.shouldShowRequestPermissionRationale(ac, permission)) {
                builder.asConfirm(ac.getString(R.string.confirm), ac.getString(R.string.confirm_permission_storage), ac.getString(R.string.cancel), ac.getString(R.string.allow), new OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        ActivityCompat.requestPermissions(ac, permissions, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                    }
                }, new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                }, false);
                return false;
            }
        }
        return true;
    }
}
