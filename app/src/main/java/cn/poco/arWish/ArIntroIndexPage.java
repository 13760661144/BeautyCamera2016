package cn.poco.arWish;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.HashMap;

import cn.poco.arWish.site.ArIntroIndexSite;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by admin on 2018/1/19.
 */

public class ArIntroIndexPage extends IPage {

    private ArIntroIndexSite mBaseSite;
    private ImageView mCreateArIntroBtn;
    private ImageView mFindArBtn;
    private ImageView mBackBtn;
    private ImageView mVideoGif;
//    public static String INTRO_THUMB_PATH = "http://btcam-oss.adnonstop.com/api/20180129/06/39355a6ef6827b398.jpg_m640-hd";
    public static String INTRO_VIDEO_PATH = "http://btcam-oss.adnonstop.com/api/20180129/06/71525a6ef3b8105b3.mp4";//播放地址

    public ArIntroIndexPage(Context context, BaseSite site) {
        super(context, site);
        mBaseSite = (ArIntroIndexSite) site;
        initUI();

        MyBeautyStat.onPageStartByRes(R.string.ar祝福_活动首页_主页面);
    }

    private void initUI() {

        setBackgroundResource(R.drawable.ar_intro_index_bg);

        FrameLayout.LayoutParams fParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        ScrollView scrollView = new ScrollView(getContext());
        scrollView.setVerticalFadingEdgeEnabled(false);
        scrollView.setFadingEdgeLength(0);
        scrollView.setVerticalScrollBarEnabled(false);
        addView(scrollView, fParams);

        //content
        fParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        RelativeLayout contentLayout = new RelativeLayout(getContext());
        contentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        scrollView.addView(contentLayout, fParams);

        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(CameraPercentUtil.WidthPxxToPercent(321), CameraPercentUtil.HeightPxxToPercent(418));
        rParams.leftMargin = CameraPercentUtil.WidthPxxToPercent(151);
        rParams.topMargin = CameraPercentUtil.HeightPxxToPercent(967);
        mVideoGif = new ImageView(getContext());
        mVideoGif.setOnTouchListener(mOnAnimationClickListener);
        contentLayout.addView(mVideoGif, rParams);

        rParams = new RelativeLayout.LayoutParams(CameraPercentUtil.WidthPxxToPercent(1080), CameraPercentUtil.HeightPxxToPercent(1632));
        rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        ImageView infoImage = new ImageView(getContext());
        infoImage.setImageResource(R.drawable.ar_intro_index_info);
        contentLayout.addView(infoImage, rParams);

        rParams = new  RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rParams.topMargin = ShareData.PxToDpi_xhdpi(30);
        rParams.leftMargin = ShareData.PxToDpi_xhdpi(30);
        mBackBtn = new ImageView(getContext());
        mBackBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mBackBtn.setImageResource(R.drawable.ar_top_bar_back_btn);
        contentLayout.addView(mBackBtn, rParams);
//        ImageUtils.AddSkin(getContext(), mBackBtn);
        mBackBtn.setOnTouchListener(mOnAnimationClickListener);

        rParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rParams.topMargin = CameraPercentUtil.HeightPxxToPercent(1659);
        rParams.bottomMargin = CameraPercentUtil.HeightPxxToPercent(90);
        LinearLayout btnLayout = new LinearLayout(getContext());
        btnLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        contentLayout.addView(btnLayout, rParams);

        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mCreateArIntroBtn = new ImageView(getContext());
        mCreateArIntroBtn.setImageResource(R.drawable.ar_intro_index_create_btn);
        mCreateArIntroBtn.setOnTouchListener(mOnAnimationClickListener);
        btnLayout.addView(mCreateArIntroBtn, lParams);

        lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lParams.leftMargin = CameraPercentUtil.WidthPxxToPercent(22);
        mFindArBtn = new ImageView(getContext());
        mFindArBtn.setImageResource(R.drawable.ar_intro_index_search_btn);
        mFindArBtn.setOnTouchListener(mOnAnimationClickListener);
        btnLayout.addView(mFindArBtn, lParams);
    }

    @Override
    public void SetData(HashMap<String, Object> params) {
        Glide.with(getContext())
                .load(R.drawable.ar_intro_index_video_gif_view)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mVideoGif);
    }

    @Override
    public void onBack() {
        mBaseSite.onBack(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClose() {
        super.onClose();
        MyBeautyStat.onPageEndByRes(R.string.ar祝福_活动首页_主页面);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener() {
        @Override
        public void onAnimationClick(View v) {
            if (v == mCreateArIntroBtn) {
                MyBeautyStat.onClickByRes(R.string.AR祝福_活动首页_主页面_送祝福按钮);
                mBaseSite.goToCreateAr(getContext());
            } else if (v == mFindArBtn) {
                MyBeautyStat.onClickByRes(R.string.AR祝福_活动首页_主页面_找祝福按钮);
                mBaseSite.goToFindAr(getContext());
            } else if (v == mBackBtn) {
                MyBeautyStat.onClickByRes(R.string.AR祝福_活动首页_主页面_返回按钮);
                mBaseSite.onBack(getContext());
            } else if (v == mVideoGif) {
                mBaseSite.goToPlayIntro(getContext());
            }
        }
    };
}
