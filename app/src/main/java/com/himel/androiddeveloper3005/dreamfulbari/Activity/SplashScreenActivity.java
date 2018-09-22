package com.himel.androiddeveloper3005.dreamfulbari.Activity;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.himel.androiddeveloper3005.dreamfulbari.Util.ActivityUtils;
import com.himel.androiddeveloper3005.dreamfulbari.Util.AppUtils;
import com.himel.androiddeveloper3005.dreamfulbari.Util.ToolBarAndStatusBar;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;
import com.wang.avi.AVLoadingIndicatorView;
import com.himel.androiddeveloper3005.dreamfulbari.R;
import java.util.ArrayList;

public class SplashScreenActivity extends AppCompatActivity {

    private Context mContext;
    private Activity mActivity;
    private static final int SPLASH_DURATION = 2500;
    private boolean mPermissionDenied = false;
    private RelativeLayout rlLayout;
    private ImageView logo;
    private TextView welcomeNote;
    private int coverArea = 0;
    private SplashScreenActivity mainActivity;
    private AVLoadingIndicatorView avi;
    private Animation fromBottom,fromTop;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initVariables();
        initView();
    }



   private void initVariables() {
        mActivity = SplashScreenActivity.this;
        mContext = mActivity.getApplicationContext();
        fromBottom = AnimationUtils.loadAnimation(mContext,R.anim.frombottom);
        fromTop = AnimationUtils.loadAnimation(mContext,R.anim.fromtop);

    }
    private void initView() {
        setContentView(R.layout.activity_splash_screen);
        logo = findViewById(R.id.image);
        logo.setAnimation(fromTop);
        welcomeNote = findViewById(R.id.ivLogo);
        welcomeNote.setAnimation(fromBottom);





    }
    @Override
    protected void onResume() {
        super.onResume();
        initFunctionality();
    }

    private void initFunctionality() {
        if (AppUtils.isNetworkAvailable(mContext)) {
            welcomeNote.postDelayed(new Runnable() {
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
