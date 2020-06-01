package cn.poco.arWish;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.circle.utils.Utils;

import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.arWish.site.ARHideWishPrePageSite;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.camera3.widget.ExitTipsDialog;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.FileUtil;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.widget.PressedButton;
import my.beautyCamera.R;

/**
 * ar 藏祝福 前置页
 * Created by Gxx on 2018/1/26.
 */

public class ARHideWishPrePage extends IPage implements View.OnClickListener
{
    public static final String KEY_IMAGE_PATH = "image_path";
    public static final String KEY_VIDEO_PATH = "video_path";
    public static final String KEY_IMAGE_THUMB = "image_thumb";

    private ARHideWishPrePageSite mSite;
    private PressedButton mBackBtn;
    private ImageView mStep1Btn;
    private ImageView mStep2Btn;
    private ImageView mSaveBtn;
    private FrameLayout mDialogLayout;
    private LinearLayout mDialogView;
    private TextView mAlbumBtn;
    private TextView mRecordBtn;
    private TextView mCancelBtn;
    private OnAnimationClickListener mAnimClickListener;

    private String mImagePath;
    private String mVideoPath;
    private Bitmap mImageThumb;
    private ImageView mImgResetIcon;
    private ImageView mVideoResetIcon;

    public ARHideWishPrePage(Context context, ARHideWishPrePageSite site)
    {
        super(context, site);
        MyBeautyStat.onPageStartByRes(R.string.ar祝福_送祝福_主页面);
        mSite = site;
        initCB();
        initView(context);
    }

    private void initCB()
    {
        mAnimClickListener = new OnAnimationClickListener()
        {
            @Override
            public void onAnimationClick(View v)
            {
                if (mSite != null)
                {
                    if (v == mBackBtn)
                    {
                        onBack();
                    }
                    else if (v == mStep1Btn)
                    {
                        MyBeautyStat.onClickByRes(R.string.AR祝福_送祝福_主页面_点击拍照);
                        MyBeautyStat.onPageStartByRes(R.string.ar祝福_送祝福_打开镜头拍照);
                        mSite.onStep1ToTakePicture(getContext());
                    }
                    else if (v == mStep2Btn)
                    {
                        MyBeautyStat.onClickByRes(R.string.AR祝福_送祝福_主页面_点击加入视频);
                        showDStep2Dialog(true);
                    }
                    else if (v == mCancelBtn)
                    {
                        MyBeautyStat.onClickByRes(R.string.AR祝福_送祝福_弹出视频选择_本地录制_取消);
                        showDStep2Dialog(false);
                    }
                    else if (v == mAlbumBtn)
                    {
                        MyBeautyStat.onClickByRes(R.string.AR祝福_送祝福_弹出视频选择_本地录制_从相册选);
                        mSite.onStep2ToOpenAlbumPage(getContext());
                    }
                    else if (v == mRecordBtn)
                    {
                        MyBeautyStat.onClickByRes(R.string.AR祝福_送祝福_弹出视频选择_本地录制_录制);
                        MyBeautyStat.onPageStartByRes(R.string.ar祝福_送祝福_打开镜头录像);
                        mSite.onStep2ToRecordVideo(getContext());
                    }
                    else if (v == mSaveBtn)
                    {
                        if (TextUtils.isEmpty(mVideoPath) || TextUtils.isEmpty(mImagePath))
                        {
                            return;
                        }

                        MyBeautyStat.onClickByRes(R.string.AR祝福_送祝福_主页面_完成);

                        HashMap<String, Object> params = new HashMap<>();

                        ViewGroup parent = (ViewGroup) mBackBtn.getParent();
                        if (parent != null)
                        {
                            parent.setDrawingCacheEnabled(true);
                            params.put("screenshots", Bitmap.createBitmap(parent.getDrawingCache()));
                            parent.setDrawingCacheEnabled(false);
                        }

                        params.put("image", mImagePath);
                        params.put("video", mVideoPath);

                        mSite.onSave(getContext(), params);
                    }
                }
            }
        };
    }

    private void initView(Context context)
    {
        ArHidePreBgView bgView = new ArHidePreBgView(context);
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(bgView, params);

        mBackBtn = new PressedButton(context);
        mBackBtn.setOnTouchListener(mAnimClickListener);
        mBackBtn.setButtonImage(R.drawable.ar_top_bar_back_btn, R.drawable.ar_top_bar_back_btn);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = ShareData.PxToDpi_xhdpi(30);
        params.leftMargin = ShareData.PxToDpi_xhdpi(30);
        addView(mBackBtn, params);

        ImageView top_bar = new ImageView(context);
        top_bar.setImageResource(R.drawable.ar_hide_pre_top_bar);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = CameraPercentUtil.WidthPxxToPercent(58);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        addView(top_bar, params);

        RelativeLayout content_view = new RelativeLayout(context);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.topMargin = CameraPercentUtil.WidthPxxToPercent(752);
        addView(content_view, params);
        {
            TextView step_one_text = new TextView(context);
            step_one_text.setId(Utils.generateViewId());
            step_one_text.setText(R.string.ar_hide_pre_step_one);
            step_one_text.setTextColor(0xffffefc0);
            step_one_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            RelativeLayout.LayoutParams rlparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rlparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            content_view.addView(step_one_text, rlparams);

            mStep1Btn = new ImageView(context);
            mStep1Btn.setId(Utils.generateViewId());
            mStep1Btn.setBackgroundResource(R.drawable.ar_wish_hide_plus_logo);
            mStep1Btn.setOnTouchListener(mAnimClickListener);
            rlparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rlparams.addRule(RelativeLayout.BELOW, step_one_text.getId());
            rlparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            rlparams.topMargin = CameraPercentUtil.WidthPxxToPercent(50);
            content_view.addView(mStep1Btn, rlparams);

            mImgResetIcon = new ImageView(context);
            mImgResetIcon.setId(Utils.generateViewId());
            mImgResetIcon.setVisibility(GONE);
            mImgResetIcon.setImageResource(R.drawable.ar_hide_pre_again_logo);
            rlparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rlparams.addRule(RelativeLayout.ALIGN_END, mStep1Btn.getId());
            rlparams.addRule(RelativeLayout.ALIGN_BOTTOM, mStep1Btn.getId());
            rlparams.bottomMargin = -CameraPercentUtil.WidthPxxToPercent(6);
            rlparams.rightMargin = CameraPercentUtil.WidthPxxToPercent(21);
            content_view.addView(mImgResetIcon, rlparams);

            TextView step_two_text = new TextView(context);
            step_two_text.setId(Utils.generateViewId());
            step_two_text.setText(R.string.ar_hide_pre_step_two);
            step_two_text.setTextColor(0xffffefc0);
            step_two_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            rlparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rlparams.addRule(RelativeLayout.BELOW, mStep1Btn.getId());
            rlparams.topMargin = CameraPercentUtil.WidthPxxToPercent(80);
            rlparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            content_view.addView(step_two_text, rlparams);

            mStep2Btn = new ImageView(context);
            mStep2Btn.setId(Utils.generateViewId());
            mStep2Btn.setBackgroundResource(R.drawable.ar_wish_hide_plus_logo);
            mStep2Btn.setOnTouchListener(mAnimClickListener);
            rlparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rlparams.addRule(RelativeLayout.BELOW, step_two_text.getId());
            rlparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            rlparams.topMargin = CameraPercentUtil.WidthPxxToPercent(50);
            content_view.addView(mStep2Btn, rlparams);

            mVideoResetIcon = new ImageView(context);
            mVideoResetIcon.setId(Utils.generateViewId());
            mVideoResetIcon.setVisibility(GONE);
            mVideoResetIcon.setImageResource(R.drawable.ar_hide_pre_again_logo);
            rlparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rlparams.addRule(RelativeLayout.ALIGN_RIGHT, mStep2Btn.getId());
            rlparams.addRule(RelativeLayout.ALIGN_BOTTOM, mStep2Btn.getId());
            rlparams.bottomMargin = -CameraPercentUtil.WidthPxxToPercent(6);
            rlparams.rightMargin = CameraPercentUtil.WidthPxxToPercent(21);
            content_view.addView(mVideoResetIcon, rlparams);

            mSaveBtn = new ImageView(context);
            mSaveBtn.setImageResource(R.drawable.ar_hide_save_logo);
            mSaveBtn.setImageAlpha((int) (255 * 0.6f));
            mSaveBtn.setOnTouchListener(mAnimClickListener);
            rlparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rlparams.addRule(RelativeLayout.BELOW, mStep2Btn.getId());
            rlparams.topMargin = CameraPercentUtil.WidthPxxToPercent(123);
            rlparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            content_view.addView(mSaveBtn, rlparams);
        }

        mDialogLayout = new FrameLayout(context);
        mDialogLayout.setVisibility(GONE);
        mDialogLayout.setOnClickListener(this);
        mDialogLayout.setClickable(true);
        mDialogLayout.setLongClickable(true);
        mDialogLayout.setBackgroundColor(ColorUtils.setAlphaComponent(Color.BLACK, 0));
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mDialogLayout, params);
        {
            mDialogView = new LinearLayout(context);
            mDialogView.setOrientation(LinearLayout.VERTICAL);
            mDialogView.setBackgroundColor(Color.WHITE);
            mDialogView.setTranslationY(CameraPercentUtil.WidthPxToPercent(340));
            params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM;
            mDialogLayout.addView(mDialogView, params);
            {
                mAlbumBtn = new TextView(context);
                mAlbumBtn.setBackgroundColor(Color.WHITE);
                mAlbumBtn.setText(R.string.ar_hide_from_album);
                mAlbumBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                mAlbumBtn.setTextColor(ImageUtils.GetSkinColor());
                mAlbumBtn.setGravity(Gravity.CENTER);
                mAlbumBtn.setOnTouchListener(mAnimClickListener);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.WidthPxToPercent(110));
                mDialogView.addView(mAlbumBtn, lp);

                View line = new View(context);
                line.setBackgroundColor(0xffececec);
                lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.WidthPxToPercent(1));
                mDialogView.addView(line, lp);

                mRecordBtn = new TextView(context);
                mRecordBtn.setBackgroundColor(Color.WHITE);
                mRecordBtn.setText(R.string.ar_hide_record);
                mRecordBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                mRecordBtn.setTextColor(ImageUtils.GetSkinColor());
                mRecordBtn.setGravity(Gravity.CENTER);
                mRecordBtn.setOnTouchListener(mAnimClickListener);
                lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.WidthPxToPercent(110));
                mDialogView.addView(mRecordBtn, lp);

                line = new View(context);
                line.setBackgroundColor(0xffececec);
                lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.WidthPxToPercent(6));
                mDialogView.addView(line, lp);

                mCancelBtn = new TextView(context);
                mCancelBtn.setBackgroundColor(Color.WHITE);
                mCancelBtn.setText(R.string.ar_hide_cancel);
                mCancelBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                mCancelBtn.setTextColor(0xff808080);
                mCancelBtn.setGravity(Gravity.CENTER);
                mCancelBtn.setOnTouchListener(mAnimClickListener);
                lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.WidthPxToPercent(110));
                mDialogView.addView(mCancelBtn, lp);
            }
        }
    }

    private void showDStep2Dialog(final boolean show)
    {
        if (mDialogLayout != null && mDialogView != null)
        {
            final float start_alpha = show ? 0 : 0.5f;
            final float end_alpha = show ? 0.5f : 0;

            final int start_y = show ? CameraPercentUtil.WidthPxToPercent(340) : 0;
            final int end_y = show ? 0 : CameraPercentUtil.WidthPxToPercent(340);

            if (show)
            {
                MyBeautyStat.onPageStartByRes(R.string.ar祝福_送祝福_弹出视频选择_本地或录制);
                mDialogLayout.setVisibility(VISIBLE);
            }
            else
            {
                MyBeautyStat.onPageEndByRes(R.string.ar祝福_送祝福_弹出视频选择_本地或录制);
            }

            ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override
                public void onAnimationUpdate(ValueAnimator animation)
                {
                    float value = (float) animation.getAnimatedValue();
                    float alpha = start_alpha + (end_alpha - start_alpha) * value;
                    float y = start_y + (end_y - start_y) * value;
                    mDialogLayout.setBackgroundColor(ColorUtils.setAlphaComponent(Color.BLACK, (int) (255 * alpha)));
                    mDialogView.setTranslationY(y);
                }
            });

            anim.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    if (!show)
                    {
                        mDialogLayout.setVisibility(GONE);
                    }
                }
            });
            anim.setDuration(300);
            anim.start();
        }
    }

    private void closeStep2Dialog()
    {
        if (mDialogLayout != null && mDialogView != null)
        {
            mDialogView.setTranslationY(CameraPercentUtil.WidthPxToPercent(340));
            mDialogLayout.setBackgroundColor(ColorUtils.setAlphaComponent(Color.BLACK, (int) (255 * 0.5f)));
            mDialogLayout.setVisibility(GONE);
        }
    }

    @Override
    public void SetData(HashMap<String, Object> params)
    {

    }

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params)
    {
        super.onPageResult(siteID, params);

        if (siteID == SiteID.AR_WISHES_CAMERA)
        {
            MyBeautyStat.onPageEndByRes(R.string.ar祝福_送祝福_打开镜头拍照);
        }
        else if (siteID == SiteID.CAMERA || siteID == SiteID.DYNAMIC_STICKER_VIDEO_PREVIEW)
        {
            MyBeautyStat.onPageEndByRes(R.string.ar祝福_送祝福_打开镜头录像);
        }
    }

    @Override
    public void onBackResult(int siteID, HashMap<String, Object> params)
    {
        closeStep2Dialog();

        if (params != null)
        {
            if (params.containsKey(KEY_VIDEO_PATH))
            {
                Object obj = params.get(KEY_VIDEO_PATH);
                if (obj != null)
                {
                    mVideoPath = (String) obj;

                    if (!TextUtils.isEmpty(mVideoPath))
                    {
                        Bitmap video_thumb = FileUtil.getLocalVideoThumbnail(mVideoPath);
                        int size = CameraPercentUtil.WidthPxxToPercent(240);
                        Bitmap bmp = MakeBmpV2.CreateFixBitmapV2(video_thumb, 0, 0, MakeBmpV2.POS_H_CENTER | MakeBmpV2.POS_V_CENTER, size, size, Bitmap.Config.ARGB_8888);
                        int px = CameraPercentUtil.WidthPxxToPercent(120);
                        if (bmp != null && !bmp.isRecycled())
                        {
                            Bitmap thumb = cn.poco.tianutils.ImageUtils.MakeRoundBmp(bmp, size, size, px);
                            if (thumb != null && !thumb.isRecycled())
                            {
                                mStep2Btn.setImageBitmap(thumb);
                                mVideoResetIcon.setVisibility(VISIBLE);
                            }
                        }
                    }
                }
            }

            // 图片路径
            if (params.containsKey(KEY_IMAGE_PATH))
            {
                Object obj = params.get(KEY_IMAGE_PATH);
                if (obj != null)
                {
                    mImagePath = (String) obj;
                }
            }

            // 藏祝福 礼物缩略图
            if (params.containsKey(KEY_IMAGE_THUMB))
            {
                Object obj = params.get(KEY_IMAGE_THUMB);
                if (obj != null)
                {
                    Bitmap bmp = (Bitmap) obj;

                    if (!bmp.isRecycled())
                    {
                        int size = CameraPercentUtil.WidthPxxToPercent(240);
                        int px = CameraPercentUtil.WidthPxxToPercent(120);
                        mImageThumb = cn.poco.tianutils.ImageUtils.MakeRoundBmp(bmp, size, size, px);
                        if (mImageThumb != null && !mImageThumb.isRecycled())
                        {
                            mStep1Btn.setImageBitmap(mImageThumb);
                            mImgResetIcon.setVisibility(VISIBLE);
                        }
                    }
                }
            }
        }

        if (mSaveBtn != null)
        {
            if (!TextUtils.isEmpty(mVideoPath) && !TextUtils.isEmpty(mImagePath))
            {
                mSaveBtn.setImageAlpha(255);
            }
            else
            {
                mSaveBtn.setImageAlpha((int) (255 * 0.6f));
            }
        }
    }

    @Override
    public void onClose()
    {
        if (mImageThumb != null && !mImageThumb.isRecycled())
        {
            mImageThumb.recycle();
            mImageThumb = null;
        }

        MyBeautyStat.onPageEndByRes(R.string.ar祝福_送祝福_主页面);
    }

    @Override
    public void onBack()
    {
        if (!TextUtils.isEmpty(mVideoPath) || !TextUtils.isEmpty(mImagePath))
        {
            ExitTipsDialog dialog = new ExitTipsDialog(getContext());
            dialog.setOnClickListener(new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    switch (which)
                    {
                        case ExitTipsDialog.EXIT_CONFIRM:
                        {
                            dialog.dismiss();
                            mVideoPath = null;
                            mImagePath = null;
                            onBack();
                            break;
                        }

                        default:
                        {
                            dialog.cancel();
                            break;
                        }
                    }
                }
            });
            dialog.show();
            return;
        }

        MyBeautyStat.onClickByRes(R.string.AR祝福_送祝福_主页面_返回);

        if (mSite != null)
        {
            mSite.onBack(getContext());
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v == mDialogLayout)
        {
            showDStep2Dialog(false);
        }
    }
}
