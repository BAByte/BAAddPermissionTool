##功能

在使用运行时权限时处理多权限太复杂，就写个工具包，需要注意的是，你请求的权限，
必须在AndroidManifest.xml中声明
jar包下载地址：https://pan.baidu.com/s/1eStmojK

##使用前提

    //在对应这两个包中的Activiy或者Fragment才能用
    android.support.v7.app.AppCompatActivity;
    android.support.v4.app.Fragment;

##对外提供的接口


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

---

##使用方法

- Activity和碎片使用方法一样
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


---

##设置自定义的Dialog

直接调用函数即可，但是每一次的都要设置，无法全局设置，因为需要和Activity绑定，全局也不是不可以，但是我觉得没有必要
