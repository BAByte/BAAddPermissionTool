package com.example.addpermission.BAPermissionTool;

import java.io.Serializable;

/**
 * Created by BA on 2017/11/14 0014.
 *
 * @Function : 请求权限的回调接口
 */

public interface PermissionCallBack  {

    //申请成功后的回调接口
    public void onSuccess();

    //申请失败的回调接口
    public void onFail();
}
