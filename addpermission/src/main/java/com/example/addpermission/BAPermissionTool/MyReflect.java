package com.example.addpermission.BAPermissionTool;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Created by BA on 2017/11/24 0024.
 *
 * @Function : 注解映射类
 */

class MyReflect implements Serializable {

    /**
     * @return
     * @throws
     * @fuction 获取权限请求成功的回调方法
     * @parm 被获取的实例
     */
   static Method getSuccess(Object object) {
        Class clazz = object.getClass();

        Method[] methods = clazz.getMethods();

        for (Method m : methods) {
            if (m.isAnnotationPresent(Success.class)) {
                return m;
            }
        }
        return null;
    }

    /**
     * @return
     * @throws
     * @fuction 获取权限请求失败的回调方法
     * @parm 被获取的实例
     */
    static Method getFail(Object object) {
        Class clazz = object.getClass();

        Method[] methods = clazz.getMethods();

        for (Method m : methods) {
            if (m.isAnnotationPresent(Fail.class)) {
                return m;
            }
        }
        return null;
    }
}
