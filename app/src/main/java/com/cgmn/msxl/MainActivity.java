package com.cgmn.msxl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.os.Bundle;
import com.cgmn.msxl.ac.AppMainActivity;
import com.cgmn.msxl.ac.loginActivity;
import com.cgmn.msxl.application.GlobalTreadPools;
import com.cgmn.msxl.comp.CustmerToast;
import com.cgmn.msxl.comp.LoginBaseActivity;
import com.cgmn.msxl.data.User;
import com.cgmn.msxl.db.AppSqlHelper;
import com.cgmn.msxl.server_interface.BaseData;
import com.cgmn.msxl.utils.CommonUtil;
import com.cgmn.msxl.utils.FixStringBuffer;
import com.cgmn.msxl.utils.MessageUtil;
import com.cgmn.msxl.utils.NetworkUtil;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends LoginBaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button btn;
    private Context mContext;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        btn = findViewById(R.id.register_btn);
        btn.setOnClickListener(this);

        if(!NetworkUtil.isNetworkConnected(mContext)){
            CustmerToast.makeText(mContext, R.string.no_network);
        }else{
            autoLogin();
        }
    }

    private void autoLogin() {
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == MessageUtil.LOAD_USER_INFOR) {
                    Map<String, Object> map = (Map<String, Object>) msg.obj;
                    if (!CommonUtil.isEmpty(map)) {
                        final Map<String, String> p = new HashMap<>();
                        p.put("email", (String) map.get("phone"));
                        p.put("pws", (String) map.get("password"));
                        p.put("GENERAL_LOGIN", "1");
                        CustmerToast.makeText(mContext, R.string.logining).show();
                        GlobalTreadPools.getInstance(mContext).execute(new Runnable() {
                            @Override
                            public void run() {
                                onLoginRequest(p, mContext, mHandler);
                                Log.e(TAG,"NAME="+Thread.currentThread().getName());
                            }
                        });
                    }
                }if(msg.what == MessageUtil.REQUEST_SUCCESS){
                    BaseData data = (BaseData) msg.obj;
                    User user = data.getUser();
                    //跳转
                    Intent intent = new Intent(mContext, AppMainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("userName", user.getUserName());
                    intent.putExtra("userDate", bundle);
                    startActivity(intent);
                    finish();
                }else if(msg.what == MessageUtil.EXCUTE_EXCEPTION){
                    Exception exception = (Exception) msg.obj;
                    FixStringBuffer mes = new FixStringBuffer();
                    mes.append("登陆失败: %s", exception.getMessage());
                    //TODO: 异常处理
                    CustmerToast.makeText(mContext, mes.toString()).show();
                }
                return false;
            }
        });

        //加载用户信息
        GlobalTreadPools.getInstance(mContext).execute(new Runnable() {
            @Override
            public void run() {
                AppSqlHelper sqlHeper = new AppSqlHelper(mContext);
                Map<String, Object> map = sqlHeper.getActiveUser();
                Message message = Message.obtain();
                message.what = MessageUtil.LOAD_USER_INFOR;
                message.obj = map;
                mHandler.sendMessage(message);
            }
        });
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this, loginActivity.class));
        this.finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.main_layout);
        InputStream is ;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inSampleSize = 2;
        is= getResources().openRawResource(R.drawable.main);
        Bitmap bm = BitmapFactory.decodeStream(is, null, opt);
        BitmapDrawable bd = new BitmapDrawable(getResources(), bm);
        layout.setBackgroundDrawable(bd);
    }
}