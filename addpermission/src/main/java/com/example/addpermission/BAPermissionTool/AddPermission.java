package com.example.addpermission.BAPermissionTool;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
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

public class AddPermission {

    private List<String> nomoalList; //即将请求的权限集合
    private List<String> noShowList; //被勾选了不再提示的数组集合
    private String[] mPermissions;   //判断是否被允许的权限
    private static Object mContext;
    private int requestCode = 101;
    private AlertDialog.Builder dialogBuilder; //可以自定义提示框
    static final int TO_SETTING = 202;
    private BARequest request; //实现请求权限的接口
    private static AddPermission permissionRequest; //单例

    private AddPermission() {
    }

    private static AddPermission getInstance() {
        if (permissionRequest == null) {
            synchronized (AddPermission.class) {
                if (permissionRequest == null)
                    permissionRequest = new AddPermission();
            }
        }
        return permissionRequest;
    }

    /**
     * @return AddPermission
     * @throws
     * @fuction 绑定context
     * @parm Activity或者Fragment
     */
    public static AddPermission with(@NonNull Object context) {
        mContext = context;
        return getInstance();
    }

    /**
     * @return AddPermission
     * @throws
     * @fuction 设置请求码
     * @parm 请求码
     */
    public AddPermission code(int code) {
        requestCode = code;
        return permissionRequest;
    }

    /**
     * @return AddPermission
     * @throws
     * @fuction 设置请求的权限
     * @parm 权限
     */
    public AddPermission permissions(@NonNull String... permissions) {
        mPermissions = permissions;
        return permissionRequest;
    }

    /**
     * @return false代表已经有权限或者小于安卓6.0，true代表去获取权限
     * @throws
     * @fuction 开始请求权限
     * @parm
     */
    public boolean go() {
        return checkAndroidVersion() && AddPermission.getInstance().getPermission();
    }

    /**
     * @return AddPermission
     * @throws
     * @fuction 设置dialog的样式
     * @parm AlertDialog.Builder
     */
    public AddPermission setDialogStyle(AlertDialog.Builder builder) {
        permissionRequest.setDialog(builder);
        return permissionRequest;
    }

    /**
     * @return
     * @throws
     * @fuction 请求权限的回调接口
     * @parm 将该方法(onRequestPermissionsResult)的参数传入
     */
    public static void callBack(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, PermissionCallBack callBack) {
        AddPermission.getInstance().insideCallBack(requestCode, permissions, grantResults, callBack);
    }

    /**
     * @return false代表已经有权限，true代表去获取权限
     * @throws
     * @fuction 获取对应的权限
     * @parm Object context：由于Activity和fragment不一样，后者不是context，所以就用Object，然后判断类型后强转
     * @parm requestCode:这个就不说了
     */
    private boolean getPermission() {
        nomoalList = new ArrayList<>();
        if (isActivity(mContext) &&
                checkPermissions((AppCompatActivity) mContext, mPermissions)) {
            request = new ActivityRequest();
            request.getPermisson(mContext, requestCode, nomoalList);
            return true;
        } else if (isFragment(mContext) &&
                checkPermissions(((Fragment) mContext).getActivity(), mPermissions)) {
            request = new FragmentRequest();
            request.getPermisson(mContext, requestCode, nomoalList);
            return true;
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
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(context, permission);
            if (result != PackageManager.PERMISSION_GRANTED)
                nomoalList.add(permission);
        }
        return nomoalList.size() > 0;
    }

    //判断是Activity还是Fragment
    private boolean isActivity(Object context) {
        return context instanceof AppCompatActivity;
    }

    private boolean isFragment(Object context) {
        return context instanceof Fragment;
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
    private void insideCallBack(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, PermissionCallBack callBack) {
        //当Activity复写了onRequestPermissionsResult方法后，
        // 系统将不会回调Fragment的onRequestPermissionsResultb
        //方法，所以要进行多一层的验证判断
        if (requestCode != this.requestCode && isFragment(mContext)
                && checkListIsSame(permissions)) {
            //因为在Activity中的RequestCode已经变了，所以要纠正
            ((Fragment) mContext)
                    .onRequestPermissionsResult(this.requestCode, permissions, grantResults);
            return; //如果确实出现了上面说的问题就直接调用Fragment的onRequestPermissionsResult方法来处理
        } else if (requestCode == this.requestCode && grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    nomoalList.remove(permissions[i]);
            }

            //失败后确认是不是用户勾线了不再提示
            if (nomoalList.size() > 0) {
                noShowList = new ArrayList<>();
                for (String permission : nomoalList)
                    request.checkPermissionIsNoShow(noShowList, permission);

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
        if (noShowList != null)
            noShowList.clear();
        this.requestCode = 0;
    }

    //在Fragment中请求权限后，如果在Activity中复写了onRequestPermissionsResult()方法
    //那系统是不会回调碎片中onRequestPermissionsResult方法的，但是会回调Activiy中的，所以加一次判断
    private boolean checkListIsSame(String[] permissions) {
        return Arrays.asList(permissions).containsAll(nomoalList);
    }

    /**
     * @return
     * @throws
     * @fuction 展示警告框
     * @parm 用来在用户点击取消后回调请求权限错误
     */
    private void showDialog(final PermissionCallBack permissionCallBack) {
        if (dialogBuilder == null) {
            dialogBuilder = request.getDialogBuilder();
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
                            request.toSetting();
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
                            request.toSetting();
                            Log.d("ssss", "onClick: 确定");
                        }
                    })
                    .create()
                    .show();
        }
    }

    //允许用户自定义提示框
    private void setDialog(AlertDialog.Builder builder) {
        dialogBuilder = builder;
    }
}
