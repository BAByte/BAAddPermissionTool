package com.example.addpermission.BAPermissionTool;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import java.util.List;

/**
 * Created by BA on 2017/11/19 0019.
 *
 * @Function :
 */

 interface BARequest {
    void getPermisson(@NonNull Object mContext, int requestCode, @NonNull List<String> normoalList);
    void checkPermissionIsNoShow(List<String> noShowList,String permission);
    void toSetting();
    AlertDialog.Builder getDialogBuilder();
}
