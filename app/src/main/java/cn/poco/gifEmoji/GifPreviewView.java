package cn.poco.gifEmoji;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.ui.preview.PhotoSaveUIConfig;
import cn.poco.camera3.ui.preview.SaveAnimView;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * @author Gxx
 */

public class GifPreviewView extends FrameLayout implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, OnEditControlListener
{
    public VideoView mVideo;
    public ContourTextView mText;
    public ImageView mWatermark;
    public FrameLayout mBackBtn;
    public SaveAnimView mSaveView;
    public EditPage mEditPage;
    public long mAnimDuration = 300; // 动画统一持续时间
    public boolean mIsShowEditPage = false;

    public GifPreviewView(@NonNull Context context)
    {
        super(context);
        ShareData.InitData(context);
        setBackgroundColor(0xe6ffffff);
        initView();
    }

    private void initView()
    {

        mVideo = new VideoView(getContext());
        mVideo.setOnPreparedListener(this);
        mVideo.setOnCompletionListener(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) (ShareData.getScreenW() * (540 * 1f / 720)), (int) (ShareData.getScreenW() * (540 * 1f / 720)));
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.topMargin = CameraPercentUtil.HeightPxToPercent(268);
        this.addView(mVideo, params);

        mText = new ContourTextView(getContext());
        mText.setOnClickListener(mClickListener);
        mText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 27);
        mText.setColor(0xb3000000, 0xffffffff);
        mText.setTextColor(Color.WHITE);
        mText.setText(R.string.gif_edit_add_text_tip);
        mText.setSingleLine(true);
        mText.setGravity(Gravity.CENTER);
        mText.setShadowLayer(ShareData.PxToDpi_xhdpi(5), ShareData.PxToDpi_xhdpi(1), ShareData.PxToDpi_xhdpi(1), 0x40000000);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.topMargin = CameraPercentUtil.HeightPxToPercent(706);
        this.addView(mText, params);

        mWatermark = new ImageView(getContext());
        mWatermark.setImageResource(R.drawable.gif_watermark);
        mWatermark.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        params = new LayoutParams((int) (ShareData.getScreenW() * (170 * 1f / 720)), (int) (ShareData.m_screenRealHeight * (70 * 1f / 1280)));
        params.gravity = Gravity.END;
        params.rightMargin = (int) (ShareData.getScreenW() * (90 * 1f / 720));
        params.topMargin = CameraPercentUtil.HeightPxToPercent(268);
        this.addView(mWatermark, params);

        mBackBtn = new FrameLayout(getContext());
        mBackBtn.setOnTouchListener(mOnAnimationClickListener);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        params.leftMargin = CameraPercentUtil.WidthPxToPercent(106);
        params.bottomMargin = CameraPercentUtil.HeightPxToPercent(145);
        this.addView(mBackBtn, params);
        {
            ImageView back_logo = new ImageView(getContext());
            back_logo.setImageResource(R.drawable.camera_pre_back_gray);
            params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(70), CameraPercentUtil.WidthPxToPercent(70));
            mBackBtn.addView(back_logo, params);

            TextView back_text = new TextView(getContext());
            back_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
            back_text.setTypeface(Typeface.DEFAULT_BOLD);
            back_text.setTextColor(0xff999999);
            back_text.setText(R.string.gif_pre_back_text);
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = CameraPercentUtil.WidthPxToPercent(72);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            mBackBtn.addView(back_text, params);
        }

        mSaveView = new SaveAnimView(getContext());
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.camera_photo_pre_bg);
        bmp = ImageUtils.AddSkin(getContext(), bmp);
        mSaveView.setBackground(new BitmapDrawable(bmp));
        mSaveView.setImageResource(R.drawable.camera_photo_pre_save_logo);
        mSaveView.setOnTouchListener(mOnAnimationClickListener);
        params = new FrameLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(170), CameraPercentUtil.WidthPxToPercent(170));
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        params.bottomMargin = CameraPercentUtil.HeightPxToPercent(106);
        this.addView(mSaveView, params);

        mEditPage = new EditPage(getContext());
        mEditPage.setClickable(true);
        mEditPage.setVisibility(GONE);
        mEditPage.SetOnEditControlListener(this);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mEditPage, params);
    }

    public void playVideo(String videoPath)
    {
        File file = new File(videoPath);
        if (file.exists())
        {
            if (mVideo != null)
            {
                mVideo.setVideoPath(file.getPath());
            }
        }
    }

    protected OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener()
    {
        @Override
        public void onAnimationClick(View v)
        {
            if (v == mBackBtn)
            {
                if (mPreviewListener != null)
                {
                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_表情包_预览_x_返回);
                    MyBeautyStat.onClickByRes(R.string.拍照_表情包预览页_主页面_返回);
                    mPreviewListener.onPreviewBack();
                }
            }
            else if (v == mSaveView)
            {
                TongJi2.AddCountByRes(getContext(), R.integer.拍照_表情包_预览_分享);
                MyBeautyStat.onClickByRes(R.string.拍照_表情包预览页_主页面_保存);
                if (mPreviewListener != null)
                {
                    String text = mText.getText().toString();
                    if (text.equals(getResources().getString(R.string.gif_edit_add_text_tip)))
                    {
                        text = "";
                    }
                    mPreviewListener.onPreviewSave(text);
                }
            }
        }
    };

    protected OnClickListener mClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (v == mText)
            {
                TongJi2.AddCountByRes(getContext(), R.integer.拍照_表情包_预览_点击添加文字);
                MyBeautyStat.onClickByRes(R.string.拍照_表情包预览页_主页面_点击添加文字);
                ShowEditPageAnim();
            }
        }
    };

    protected OnPreviewControlListener mPreviewListener;

    public void SetOnPreviewControlListener(OnPreviewControlListener listener)
    {
        mPreviewListener = listener;
    }

    /**
     * VideoView 播完回调
     */
    @Override
    public void onCompletion(MediaPlayer mp)
    {
        if (mVideo != null)
        {
            mVideo.start();
        }
    }

    /**
     * VideoView 准备回调
     */
    @Override
    public void onPrepared(MediaPlayer mp)
    {
        if (mVideo != null)
        {
            mVideo.start();
        }
    }

    public void ClearMemory()
    {
        mClickListener = null;
        mPreviewListener = null;

        if (mVideo != null)
        {
            this.removeView(mVideo);
            mVideo.suspend();
            mVideo.setOnCompletionListener(null);
            mVideo.setOnPreparedListener(null);
            mVideo = null;
        }

        if (mSaveView != null)
        {
            mSaveView.setOnTouchListener(null);
//            mSaveView.setConfig(null);
        }

        mBackBtn.setOnTouchListener(null);
        mText.setOnClickListener(null);

        mEditPage.SetOnEditControlListener(null);
        mEditPage.ClearMemory();

        removeView(mVideo);
        removeView(mEditPage);
        removeView(mWatermark);
        removeView(mSaveView);
        removeView(mText);
        removeView(mBackBtn);
        mVideo = null;
        mText = null;
        mEditPage = null;
    }

    public Animator InitTransYAnimator(View item, int dy, long duration)
    {
        ObjectAnimator obj = ObjectAnimator.ofFloat(item, "translationY", dy);
        obj.setDuration(duration);
        return obj;
    }

    public Animator InitTransYAnimator(View item, int startY, int endY, long duration)
    {
        ObjectAnimator obj = ObjectAnimator.ofFloat(item, "translationY", startY, endY);
        obj.setDuration(duration);
        return obj;
    }

    protected void ShowEditPageAnim()
    {
        if(mEditPage == null) return;

        int dy = ShareData.m_screenHeight;
        mEditPage.setVisibility(VISIBLE);
        mEditPage.setText(mText.getText().toString());
        Animator animator = InitTransYAnimator(mEditPage, dy, 0, mAnimDuration);
        animator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mIsShowEditPage = true;
                if (mVideo != null)
                {
                    mVideo.pause();
                }
                TongJiUtils.onPageStart(getContext(), R.string.表情包_文字编辑);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                animation.removeAllListeners();
                if (mEditPage != null) mEditPage.ShowInput();
            }
        });
        animator.start();
    }

    public void CloseEditPageAnim()
    {
        int dy = ShareData.m_screenHeight;
        Animator animator = InitTransYAnimator(mEditPage, dy, mAnimDuration);
        animator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                TongJiUtils.onPageEnd(getContext(), R.string.表情包_文字编辑);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                animation.removeAllListeners();
                if (mEditPage != null) mEditPage.setVisibility(GONE);
                mIsShowEditPage = false;
                if (mVideo != null)
                {
                    mVideo.resume();
                }
            }
        });
        animator.start();
    }

    /**
     * 编辑页保存回调
     */
    @Override
    public void onEditSave(String text)
    {
        if (text != null && !text.equals(""))
        {
            if (mText != null)
            {
                while (compareTextWidth(text))
                {
                    text = text.substring(0, text.length() - 1);
                }
                mText.setText(text);
            }
        }
        else
        {
            mText.setText(R.string.gif_edit_add_text_tip);
        }
        CloseEditPageAnim();
    }

    private boolean compareTextWidth(String text)
    {
        Paint paint = mText.getPaint();
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.width() >= CameraPercentUtil.WidthPxToPercent(540);
    }

    /**
     * 编辑页返回回调
     */
    @Override
    public void onEditBack()
    {
        CloseEditPageAnim();
    }
}
