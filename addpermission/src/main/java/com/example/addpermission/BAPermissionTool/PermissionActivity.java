package com.example.addpermission.BAPermissionTool;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by BA on 2017/11/23 0023.
 *
 * @Function : 用来请求权限的Activity，没有界面
 */

public class PermissionActivity extends Activity {
    private Request request;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        request = AddPermission.getInstance().request;
        if (request != null)
            ActivityCompat.requestPermissions
                    (this, request.list.toArray(new String[request.list.size()]), request.requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == request.requestCode && grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    request.list.remove(permissions[i]);
            }

            //失败后确认是不是用户勾线了不再提示
            if (request.list.size() > 0) {
                ArrayList<String> noShowList = new ArrayList<>();
                for (String permission : request.list) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
                        noShowList.add(permission);
                }

                //只要有一个权限被勾选了不再提示，就提示用户去设置里面自己打开权限
                if (noShowList.size() > 0) {
                    showDialog();
                } else {
                    //如果没有一个被勾选不再提示，意味着用户拒绝了权限，就回调请求权限失败的接口
                    request.onFail();
                    finish();
                }
            } else {
                request.onSuccess();
                finish();
            }
        }
    }

    /**
     * @return
     * @throws
     * @fuction 展示警告框
     * @parm 用来在用户点击取消后回调请求权限错误
     */
    private void showDialog() {
        new AlertDialog.Builder(this).setTitle("某个权限无法打开")
                .setMessage("即将去设置界面，请将该软件的权限全部打开，否则部分功能将无法使用")
                .setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.onFail();
                        finish();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toSetting();
                        Log.d("ssss", "onClick: 确定");
                    }
                })
                .create()
                .show();
    }


    public void toSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        String packageName = this.getPackageName();
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        this.startActivityForResult(intent, 1012);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (request != null) {
            if (requestCode == 1012) {
                ArrayList<String> arrayList = Request.checkPermissions(this, request.list.toArray(new String[request.list.size()]));
                if (arrayList.size() == 0)
                    request.onSuccess();
                else
                    request.onFail();
            }
        } else {
            Toast.makeText(getApplicationContext(), "请不要在设置界面重复调整权限开关！！", Toast.LENGTH_LONG).show();
        }
        finish();
    }
}
