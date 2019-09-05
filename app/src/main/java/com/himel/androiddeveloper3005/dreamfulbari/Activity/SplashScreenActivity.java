package com.himel.androiddeveloper3005.dreamfulbari.Activity;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.himel.androiddeveloper3005.dreamfulbari.Util.ActivityUtils;
import com.himel.androiddeveloper3005.dreamfulbari.Util.AppUtils;
import com.tt.whorlviewlibrary.WhorlView;
import com.himel.androiddeveloper3005.dreamfulbari.R;

public class SplashScreenActivity extends AppCompatActivity {
    private Context mContext;
    private Activity mActivity;
    private static final int SPLASH_DURATION = 2500;
    private ProgressBar mProgressBar;





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
        mProgressBar =  this.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);

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
                    mProgressBar.setVisibility(View.INVISIBLE);
                    ActivityUtils.getInstance().invokeActivity(mActivity, HomePageActivity.class, true);
                }
            }, SPLASH_DURATION);

        } else {
            AppUtils.noInternetWarning(findViewById(R.id.ivLogo), mContext);
        }

    }
}
