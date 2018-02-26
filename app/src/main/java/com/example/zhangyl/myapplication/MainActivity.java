package com.example.zhangyl.myapplication;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity implements InitStepOneFragment.OnStepOneFragmentCallBack, StepTwoFragment.OnStepTwoFragmentCallBack, MeetingModeCfgFragment.OnModeCfgFragmentCallBack {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;
    private static final String TAG = "MainActivity";
    // todo 界面框架尝试换成 QMUI http://qmuiteam.com/android/page/index.html
    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    //private View mControlsView;

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
//            ActionBar actionBar = getSupportActionBar();
//            if (actionBar != null) {
//                actionBar.show();
//            }

            //mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVisible = true;
        Fragment initStepOneFragment = InitStepOneFragment.newInstance(null, null);
        //getFragmentManager().beginTransaction().add(R.id.init_fragments_container, initStepOneFragment, InitStepOneFragment.TAG).commit();
        //getFragmentManager().beginTransaction().add(R.id.init_fragments_container, StepTwoFragment.newInstance(null, null), StepTwoFragment.TAG).commit();
        //getSupportFragmentManager().beginTransaction()
        //getFragmentManager().beginTransaction().add(R.id.init_fragments_container, MeetingModeCfgFragment.newInstance(null, null), MeetingModeCfgFragment.TAG).commit();
        getFragmentManager().beginTransaction().add(R.id.init_fragments_container, BeforeMeetingFramgent.newInstance(null, null), BeforeMeetingFramgent.TAG).commit();

        //mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.BTN_NEXT).setOnTouchListener(mDelayHideTouchListener);
        //initViews();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getActionBar(); //getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        //mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onStepOneOk() {
        getFragmentManager().beginTransaction().replace(R.id.init_fragments_container, StepTwoFragment.newInstance(null, null), StepTwoFragment.TAG).commit();
    }

    @Override
    public void onStepTwoOk() {
        getFragmentManager().beginTransaction().replace(R.id.init_fragments_container, MeetingModeCfgFragment.newInstance(null, null), MeetingModeCfgFragment.TAG).commit();
    }

    @Override
    public void onStepTwoCancel() {
        getFragmentManager().beginTransaction().replace(R.id.init_fragments_container, InitStepOneFragment.newInstance(null, null), InitStepOneFragment.TAG).commit();
    }

    @Override
    public void onBackToStepTwo() {
        getFragmentManager().beginTransaction().replace(R.id.init_fragments_container, StepTwoFragment.newInstance(null, null), StepTwoFragment.TAG).commit();
    }

    @Override
    public void onRegisterEnd() {
        Log.i(TAG, "onRegisterEnd: register end. try to switch to before meetting view");
        // todo 调用会议流程管理模块，它调用会议管理模块，联合房间管理模块，共同拼接出会前、会中、会后待机时会议列表四大界面。
        getFragmentManager().beginTransaction().replace(R.id.init_fragments_container, BeforeMeetingFramgent.newInstance(null, null), BeforeMeetingFramgent.TAG).commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMeetingMgrEvent(MeetingListMgr.MeetingMgrEvent param) {
        switch (param.event) {
            case MeetingListMgr.MeetingMgrEvent.EVENT_MEETING_START:
                onMeetingStart();
                break;
        }
    }

    private void onMeetingStart() {
        Log.i(TAG, "onMeetingStart");
        // todo getFragmentManager().beginTransaction().replace(R.id.init_fragments_container, InnerMeetingFramgent.newInstance(null, null), InnerMeetingFramgent.TAG).commit();
    }
}
