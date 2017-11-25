package com.example.ba.baperssiontool;

import android.Manifest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.addpermission.BAPermissionTool.AddPermission;
import com.example.addpermission.BAPermissionTool.Fail;
import com.example.addpermission.BAPermissionTool.Success;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //请求权限
                AddPermission.with(MainActivity.this)
                        .code(110)
                        .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA)
                        .go();
            }
        });
    }

    /**
     *@fuction 这个方法可以随便写名字，但是要声明注解： @Success
     * 请求权限成功后会自动回调该方法，小于安卓m的也是自动回调该方法
     *@parm 请求码
     *@return
     *@exception
     */
    @Success
    public void requestSuccess(int code) {
        if (code == 110)
            Toast.makeText(this, "请求权限成功", Toast.LENGTH_SHORT).show();
    }

    /**
     *@fuction 失败的时候回调的方法
     * 这个方法可以随便写名字，但是要声明注解：   @Fail
     *@parm
     *@return
     *@exception
     */
    @Fail
    public void requestFail(int code) {
        if (code == 110)
            Toast.makeText(this, "请求权限失败", Toast.LENGTH_SHORT).show();
    }
}
