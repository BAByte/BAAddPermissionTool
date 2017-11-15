package com.example.ba.baperssiontool.BAPermissionTool;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
/**
 * Created by BA on 2017/11/14 0014.
 *
 * @Function : 请求权限功能封装类
 */

public class AddPermission {

    //请求权限
    public static boolean requestPermission(Object context, int requestCode, String...permissions){
      return PermissionRequest.getInstance().getPermission(context, requestCode, permissions);
    }

    //请求结果回调
    public static void callBack(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, PermissionCallBack callBack){
        PermissionRequest.getInstance().callBack( requestCode, permissions, grantResults, callBack);
    }

    //自定提示框的样式
    public static void setDialogBuliderStytle(AlertDialog.Builder builder){
        PermissionRequest.getInstance().setDialog(builder);
    }
}
