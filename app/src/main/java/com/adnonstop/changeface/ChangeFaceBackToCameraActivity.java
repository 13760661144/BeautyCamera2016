package com.adnonstop.changeface;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import cn.poco.camera.site.activity.CameraActivitySite;
import cn.poco.framework2.BaseActivitySite;

/**
 * Created by zwq on 2018/02/07 17:23.<br/><br/>
 */

public class ChangeFaceBackToCameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setBackgroundResource(com.poco.changeface_v.R.drawable.pic_face_bg);
        frameLayout.setLayoutParams(fl);
        setContentView(frameLayout);

        Intent intent1 = getIntent();
        int openType = intent1.getIntExtra("openType", 0);
        int cameraId = intent1.getIntExtra("cameraId", 0);

        final Intent intent = new Intent();
        intent.putExtra("openType", openType);
        intent.putExtra("cameraId", cameraId);
        BaseActivitySite.setClass(intent, this, CameraActivitySite.class);

        if (cameraId == -1 || System.currentTimeMillis() - Helper.sTime > 1000L) {
            ChangeFaceBackToCameraActivity.this.startActivity(intent);
            ChangeFaceBackToCameraActivity.this.overridePendingTransition(0, 0);
            ChangeFaceBackToCameraActivity.this.finish();
            return;
        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                ChangeFaceBackToCameraActivity.this.startActivity(intent);
                ChangeFaceBackToCameraActivity.this.overridePendingTransition(0, 0);
                ChangeFaceBackToCameraActivity.this.finish();
            }
        }, 80);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
