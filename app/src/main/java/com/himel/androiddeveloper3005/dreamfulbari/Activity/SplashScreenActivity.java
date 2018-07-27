package com.himel.androiddeveloper3005.dreamfulbari.Activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.himel.androiddeveloper3005.dreamfulbari.Util.ActivityUtils;
import com.himel.androiddeveloper3005.dreamfulbari.Util.AppUtils;
import com.himel.androiddeveloper3005.dreamfulbari.Util.ToolBarAndStatusBar;
import com.wang.avi.AVLoadingIndicatorView;
import com.himel.androiddeveloper3005.dreamfulbari.R;
import java.util.ArrayList;

public class SplashScreenActivity extends ToolBarAndStatusBar {

    private Context mContext;
    private Activity mActivity;
    private static final int SPLASH_DURATION = 2500;
    private boolean mPermissionDenied = false;
    private RelativeLayout rlLayout;
    private ImageView img_arrow;
    private int coverArea = 0;
    private SplashScreenActivity mainActivity;
    private AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initVariables();
        initView();


    }





    private void initVariables() {
        mActivity = SplashScreenActivity.this;
        mContext = mActivity.getApplicationContext();

    }

    private void initView() {
        setContentView(R.layout.activity_splash_screen);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initFunctionality();
    }

    private void initFunctionality() {
        if (AppUtils.isNetworkAvailable(mContext)) {
            findViewById(R.id.ivLogo).postDelayed(new Runnable() {
                @Override
                public void run() {
                    ActivityUtils.getInstance().invokeActivity(mActivity, LoginActivity.class, true);
                }
            }, SPLASH_DURATION);

        } else {
            AppUtils.noInternetWarning(findViewById(R.id.ivLogo), mContext);
        }

    }
}
