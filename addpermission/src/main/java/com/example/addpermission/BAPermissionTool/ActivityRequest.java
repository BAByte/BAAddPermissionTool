package com.example.addpermission.BAPermissionTool;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BA on 2017/11/19 0019.
 *
 * @Function :
 */

public class ActivityRequest implements BARequest {
    private AppCompatActivity context;
    @Override
    public void getPermisson(@NonNull Object mContext, int requestCode, @NonNull List<String> normoalList) {
        this.context = (AppCompatActivity) mContext;
        ActivityCompat.requestPermissions(this.context,
                normoalList.toArray(new String[normoalList.size()]), requestCode);
    }

    @Override
    public void checkPermissionIsNoShow(List<String> noShowList,String permission) {
        if (noShowList == null)
            noShowList = new ArrayList<>();
        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        context, permission))
            noShowList.add(permission);
    }

    @Override
    public void toSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        String packageName = context.getPackageName();
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        context.startActivityForResult(intent, AddPermission.TO_SETTING);
    }


    @Override
    public AlertDialog.Builder getDialogBuilder() {
        return new AlertDialog.Builder(context);
    }
}
