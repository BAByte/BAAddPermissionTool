##功能
学习时为了实践，写的小东西，不推荐使用！！！！
在使用运行时权限时处理多权限太复杂，就写个工具包，需要注意的是，你请求的权限，
必须在AndroidManifest.xml中声明
jar包下载地址：https://pan.baidu.com/s/1eRFrbR8

##使用前提

    //在对应这两个包中的Activiy或者Fragment才能用
    android.support.v7.app.AppCompatActivity;
    android.support.v4.app.Fragment;

##对外提供的接口
~~~java
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
~~~
---

##使用方法

- Activity和碎片使用方法一样
~~~java
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
 ~~~

---

##设置自定义的Dialog

直接调用函数即可，但是每一次的都要设置，无法全局设置，因为需要和Activity绑定，全局也不是不可以，但是我觉得没有必要
