package cn.poco.arWish;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.arWish.site.ArIntroCreateSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by admin on 2018/1/19.
 */

public class ArCreateIntroPage extends IPage {

    private ArIntroCreateSite mBaseSite;
    private Button mVideoBtn;
    private Button mAlbumBtn;
    private ImageView mBackBtn;

    public ArCreateIntroPage(Context context, BaseSite site) {
        super(context, site);
        mBaseSite = (ArIntroCreateSite) site;
        initUI();
    }

    private void initUI() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        ImageView bgImage = new ImageView(getContext());
        bgImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        bgImage.setImageResource(R.drawable.__fil__21092017041711300295095560);
        addView(bgImage, params);

        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.topMargin = ShareData.PxToDpi_xhdpi(28);
        params.leftMargin = ShareData.PxToDpi_xhdpi(28);
        mBackBtn = new ImageView(getContext());
        mBackBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mBackBtn.setImageResource(R.drawable.ar_top_bar_back_btn);
        addView(mBackBtn, params);
//        ImageUtils.AddSkin(getContext(), mBackBtn);
        mBackBtn.setOnTouchListener(mOnAnimationClickListener);

        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        LinearLayout msgLayout = new LinearLayout(getContext());
        msgLayout.setOrientation(LinearLayout.VERTICAL);
        msgLayout.setGravity(Gravity.CENTER);
        addView(msgLayout, params);

//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        TextView createTips = new TextView(getContext());
//        createTips.setText("第一步：录制视频\n第二步：瞄准现实物品，将祝福物品藏起来，完成!\n最后喊上亲朋好友一起找祝福吧");
//        msgLayout.addView(createTips, layoutParams);

        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setColor(ImageUtils.GetSkinColor());
        btnBg.setCornerRadius(ShareData.PxToDpi_xhdpi(55));


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(358), ShareData.PxToDpi_xhdpi(110));
        mAlbumBtn = new Button(getContext());
        mAlbumBtn.setText(getResources().getString(R.string.ar_create_intro_album_btn));
        mAlbumBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
        mAlbumBtn.setTextColor(Color.WHITE);
        mAlbumBtn.setOnTouchListener(mOnAnimationClickListener);
        mAlbumBtn.setBackground(btnBg);
        msgLayout.addView(mAlbumBtn, layoutParams);

        layoutParams = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(358), ShareData.PxToDpi_xhdpi(110));
        layoutParams.topMargin = ShareData.PxToDpi_xhdpi(60);
        layoutParams.bottomMargin = ShareData.PxToDpi_xhdpi(90);
        mVideoBtn = new Button(getContext());
        mVideoBtn.setText(getResources().getString(R.string.ar_create_intro_video_btn));
        mVideoBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
        mVideoBtn.setTextColor(Color.WHITE);
        mVideoBtn.setOnTouchListener(mOnAnimationClickListener);
        mVideoBtn.setBackground(btnBg);
        msgLayout.addView(mVideoBtn, layoutParams);
    }

    @Override
    public void SetData(HashMap<String, Object> params) {

    }

    @Override
    public void onBack() {
        mBaseSite.onBack();
    }

    private OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener() {
        @Override
        public void onAnimationClick(View v) {
            if (v == mVideoBtn) {
                mBaseSite.goToRecordVideo();
            }else if(v == mAlbumBtn){
                mBaseSite.goToChooseImage();
            }else if(v == mBackBtn){
                mBaseSite.onBack();
            }
        }
    };
}
