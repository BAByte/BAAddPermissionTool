package com.example.addpermission.BAPermissionTool;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by BA on 2017/11/24 0024.
 *
 * @Function : 为请求权限做准备工作的类
 */

class Request implements PermissionCallBack{
    Method success;
    Method fail;
    Object target;
    int requestCode;
    String[] permissions;
    ArrayList<String> list;

    Request(@NonNull Object obj, int requestCode, @NonNull String[] permissions) {
        this.target=obj;
        this.requestCode=requestCode;
        this.permissions=permissions;
        getAnnotation();
        start();
    }

    private void getAnnotation(){
        success=MyReflect.getSuccess(target);
        fail=MyReflect.getFail(target);
    }

    private void start() {
        list=checkPermissions(getActivity(), permissions);
        //判断版本号
        if (checkAndroidVersion()&&list.size()>0) {
            goToActivity();
        } else {
            onSuccess();
        }
    }

    /**
     * @return 要是权限没有然后去申请了，就返回true，反之false
     * @throws
     * @fuction 检查权限是否存在，不在就申请
     * @parm 没什么好说的
     */
    static ArrayList<String> checkPermissions(@NonNull Context context, @NonNull String... permissions) {
        ArrayList<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(context, permission);
            if (result != PackageManager.PERMISSION_GRANTED)
                permissionList.add(permission);
        }
        return permissionList;
    }

    private void goToActivity() {
        AppCompatActivity appCompatActivity = getActivity();
        Intent intent = new Intent(appCompatActivity, PermissionActivity.class);
        //intent.putExtra("request", this);
        appCompatActivity.startActivity(intent);
    }

    @Override
    public void onSuccess() {
        try {
            success.invoke(target,requestCode);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFail() {
        try {
            fail.invoke(target,requestCode);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    //判断安卓版本号
    boolean checkAndroidVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    //判断是Activity还是Fragment
    boolean isActivity() {
        return target instanceof AppCompatActivity;
    }

    boolean isFragment() {
        return target instanceof Fragment;
    }

    AppCompatActivity getActivity() {
        if (isActivity())
            return (AppCompatActivity) target;
        else
            return (AppCompatActivity) ((Fragment) target).getActivity();
    }
}
