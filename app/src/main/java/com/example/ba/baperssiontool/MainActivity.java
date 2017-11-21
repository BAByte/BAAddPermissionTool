package com.example.ba.baperssiontool;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.addpermission.BAPermissionTool.AddPermission;
import com.example.addpermission.BAPermissionTool.PermissionCallBack;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AddPermission.with(this)
                .code(110)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.INTERNET)
                .go();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("sssssss", "onRequestPermissionsResult: Activity的回调");
        AddPermission.callBack(requestCode, permissions, grantResults, new PermissionCallBack() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "请求权限成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(String[] permissions) {
                Toast.makeText(MainActivity.this, "请求权限失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //当用户勾选了不再提示后会提示用户去设置界面开启，然后你可以在这里再判断有没有获取到权限
        //requestCode是202
    }
}
