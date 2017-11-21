package com.example.addpermission.BAPermissionTool;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import java.util.List;

/**
 * Created by BA on 2017/11/19 0019.
 *
 * @Function :
 */

public class FragmentRequest implements BARequest {

    private Fragment context;

    @Override
    public void getPermisson(@NonNull Object mContext, int requestCode, @NonNull List<String> normoalList) {
        this.context = (Fragment) mContext;
        context.requestPermissions(normoalList.toArray(new String[]{}), requestCode);
    }

    @Override
    public void checkPermissionIsNoShow(List<String> noShowList,String permission) {
        if (!context.shouldShowRequestPermissionRationale(permission))
            noShowList.add(permission);
    }

    @Override
    public void toSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        String packageName; packageName = context.getActivity().getPackageName();
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        context.startActivityForResult(intent, AddPermission.TO_SETTING);
    }



    @Override
    public AlertDialog.Builder getDialogBuilder() {
        return new AlertDialog.Builder(context.getActivity());
    }
}
