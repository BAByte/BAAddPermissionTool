package com.example.ba.baperssiontool.BAPermissionTool;

/**
 * Created by BA on 2017/11/14 0014.
 *
 * @Function : 请求权限的回调接口
 */

public interface PermissionCallBack {

    //申请成功后的回调接口
    public void onSuccess();

    //申请失败的回调接口，参数是请求失败的权限的数组
    public void onFail(String [] permissions);
}
