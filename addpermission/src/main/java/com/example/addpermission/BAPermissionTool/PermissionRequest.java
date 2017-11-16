package com.example.addpermission.BAPermissionTool;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by BA on 2017/11/14 0014.
 *
 * @Function : 先判断安卓版本是否大于6.0，然后判断有没有对应的权限，然后去请求，然后确认哪些没有被请求
 * 成功，然后判断用户是不是勾选了不再提示，是的话就提示用户去设置打开权限
 */

class PermissionRequest {
    private static final String TAG = "PermissionRequest";
    private List<String> nomoalList;
    private List<String> noShowList;
    private Object mContext;
    private int requestCode;
    private AlertDialog.Builder dialogBuilder; //可以自定义提示框
    private final int TO_SETTING = 202;

    private static PermissionRequest permissionRequest;

    static PermissionRequest getInstance() {
        if (permissionRequest == null) {
            synchronized (PermissionRequest.class) {
                if (permissionRequest == null)
                    permissionRequest = new PermissionRequest();
            }
        }
        return permissionRequest;
    }

    private PermissionRequest() {
        nomoalList = new ArrayList<>();
        noShowList = new ArrayList<>();
    }

    /**
     * @return false代表已经有权限，true代表去获取权限
     * @throws
     * @fuction 获取对应的权限
     * @parm Object context：由于Activity和fragment不一样，后者不是context，所以就用Object，然后判断类型后强转
     * @parm requestCode:这个就不说了
     */
    boolean getPermission(@NonNull Object context, int requestCode, @NonNull String... permissions) {
        if (checkAndroidVersion()) {
            Log.d(TAG, "getPermission: 大于6.0");
            mContext = context;
            this.requestCode = requestCode;
            if (isActivity(context) &&
                    checkPermissions((AppCompatActivity) context, permissions)) {
                Log.d(TAG, "getPermission: activity开始获取权限");
                ActivityCompat.requestPermissions((AppCompatActivity) context,
                        nomoalList.toArray(new String[nomoalList.size()]), requestCode);
                return true;
            } else if (isFragment(context) &&
                    checkPermissions(((Fragment) context).getActivity(), permissions)) {
                Log.d(TAG, "getPermission: Fragment开始获取权限");
                ((Fragment) context).requestPermissions(nomoalList.toArray(new String[]{}), requestCode);
                return true;
            }
        }
        return false;
    }

    /**
     * @return 要是权限没有然后去申请了，就返回true，反之false
     * @throws
     * @fuction 检查权限是否存在，不在就申请
     * @parm 没什么好说的
     */
    private boolean checkPermissions(@NonNull Context context, @NonNull String... permissions) {
        Log.d(TAG, "checkPermissions: 检查权限");
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(context, permission);
            if (result != PackageManager.PERMISSION_GRANTED)
                nomoalList.add(permission);
        }

        return nomoalList.size() > 0;
    }

    //判断安卓版本号
    private boolean checkAndroidVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * @return
     * @throws
     * @fuction 在申请的Activity或者Fragment的onRequestPermissionsResult()方法调用，用来确认请求结果
     * @parm 最后一个就是回调的接口
     */
    void callBack(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, PermissionCallBack callBack) {
        Log.d(TAG, "callBack: 回调接口");
        //判断哪些权限申请失败
        Log.d(TAG, "callBack: 判断请求码" + requestCode);

        //当Activity复写了onRequestPermissionsResult方法后，系统将不会回调Fragment的onRequestPermissionsResultb
        //方法，所以要进行多一层的验证判断
        if (requestCode != this.requestCode && isFragment(mContext) && checkListIsSame(permissions)) {
            //因为在Activity中的RequestCode已经变了，所以要纠正
            ((Fragment)mContext).onRequestPermissionsResult(this.requestCode,permissions,grantResults);
            return; //如果确实出现了上面说的问题就直接调用Fragment的onRequestPermissionsResult方法来处理
        } else if (requestCode==this.requestCode&&grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    nomoalList.remove(permissions[i]);
            }

            //失败后确认是不是用户勾线了不再提示
            if (nomoalList.size() > 0) {
                for (String permission : nomoalList)
                    checkPermissionIsNoShow(permission);

                //只要有一个权限被勾选了不再提示，就提示用户去设置里面自己打开权限
                if (noShowList.size() > 0) {
                    showDialog(callBack);
                } else {
                    //如果没有一个被勾选不再提示，意味着用户拒绝了权限，就回调请求权限失败的接口
                    callBack.onFail(nomoalList.toArray(new String[nomoalList.size()]));
                }
            } else {
                callBack.onSuccess();
            }
        }

        //初始化，为了保险啦，可以不初始化的
        nomoalList.clear();
        noShowList.clear();
        this.requestCode = 0;
    }

    /**
     * @return
     * @throws
     * @fuction 判断某个权限是否被勾选不再提示
     * @parm 被判断的权限
     */
    private void checkPermissionIsNoShow(String permission) {
        if (isActivity(mContext)
                && !ActivityCompat.shouldShowRequestPermissionRationale(
                (AppCompatActivity) mContext, permission)) {
            noShowList.add(permission);
        } else if (isFragment(mContext)
                && !((Fragment) mContext).shouldShowRequestPermissionRationale(permission)) {
            noShowList.add(permission);
        }
    }

    //判断是Activity还是Fragment
    private boolean isActivity(Object context) {
        return context instanceof AppCompatActivity;
    }

    private boolean isFragment(Object context) {
        return context instanceof Fragment;
    }

    /**
     * @return
     * @throws
     * @fuction 展示警告框
     * @parm 用来在用户点击取消后回调请求权限错误
     */
    private void showDialog(final PermissionCallBack permissionCallBack) {
        if (dialogBuilder == null) {
            if (isActivity(mContext))
                dialogBuilder = new AlertDialog.Builder((AppCompatActivity) mContext);
            else if (isFragment(mContext))
                dialogBuilder = new AlertDialog.Builder(((Fragment) mContext).getActivity());
            else {
                permissionCallBack.onFail(noShowList.toArray(new String[nomoalList.size()]));
                return;
            }

            dialogBuilder.setTitle("无法打开权限请求")
                    .setMessage("即将去设置界面，请将该软件的权限全部打开，否则部分功能无法使用")
                    .setCancelable(false)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            permissionCallBack.onFail(noShowList.toArray(new String[noShowList.size()]));
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
            dialogBuilder = null;
        } else {
            dialogBuilder.setCancelable(false)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            permissionCallBack.onFail(noShowList.toArray(new String[noShowList.size()]));
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
            dialogBuilder = null;
        }
    }

    /**
     * @return
     * @throws
     * @fuction 跳转到设置界面
     * @parm
     */
    private void toSetting() {
        Log.d("ssss", "toSetting: " + mContext);
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        //做一个Activity和Fragment的区分
        String packageName;
        if (isActivity(mContext)) {
            Log.d("ssss", "toSetting: Activity");
            packageName = ((AppCompatActivity) mContext).getPackageName();
            Uri uri = Uri.fromParts("package", packageName, null);
            intent.setData(uri);
            ((AppCompatActivity) mContext).startActivityForResult(intent, TO_SETTING);
            mContext = null;
        } else if (isFragment(mContext)) {
            Log.d("ssss", "toSetting: Activity");
            packageName = ((Fragment) mContext).getActivity().getPackageName();
            Uri uri = Uri.fromParts("package", packageName, null);
            intent.setData(uri);
            ((Fragment) mContext).startActivityForResult(intent, TO_SETTING);
            mContext = null;
        }
    }

    //允许用户自定义提示框
    void setDialog(AlertDialog.Builder builder) {
        dialogBuilder = builder;
    }

    //在Fragment中请求权限后，如果在Activity中复写了onRequestPermissionsResult()方法，那系统是不会回调
    //碎片中onRequestPermissionsResult方法的，但是会回调Activiy中的，所以加一次判断
    private boolean checkListIsSame(String[] permissions) {
        return Arrays.asList(permissions).containsAll(nomoalList);
    }
}
