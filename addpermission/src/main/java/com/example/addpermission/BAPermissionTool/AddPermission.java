package com.example.addpermission.BAPermissionTool;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;


/**
 * Created by BA on 2017/11/24 0024.
 *
 * @Function : 建造者
 */

public class AddPermission {
    private static AddPermission permissionBuilder; //单例
    private static Object target;
    private int requestCode;
    private String[] mPermissions;
    Request request;


    private AddPermission() {
    }

     static AddPermission getInstance() {
        if (permissionBuilder == null) {
            synchronized (AddPermission.class) {
                if (permissionBuilder == null)
                    permissionBuilder = new AddPermission();
            }
        }
        return permissionBuilder;
    }

    /**
     * @return AddPermission
     * @throws
     * @fuction 绑定context
     * @parm Activity或者Fragment
     */
    public static AddPermission with(@NonNull Object context) {
        target = context;
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
        return permissionBuilder;
    }

    /**
     * @return AddPermission
     * @throws
     * @fuction 设置请求的权限
     * @parm 权限
     */
    public AddPermission permissions(@NonNull String... permissions) {
       mPermissions=permissions;
        return permissionBuilder;
    }

    /**
     * @return
     * @throws
     * @fuction 开始请求权限
     * @parm
     */
    public void go() {
       request= new Request(target,requestCode,mPermissions);
    }
}
