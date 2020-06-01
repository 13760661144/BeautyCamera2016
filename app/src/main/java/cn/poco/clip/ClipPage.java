package cn.poco.clip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.beautify4.Beautify4Page;
import cn.poco.camera.RotationImg2;
import cn.poco.clip.site.ClipPageSite;
import cn.poco.cloudalbumlibs.utils.CloudAlbumDialog;
import cn.poco.display.ClipView;
import cn.poco.filterPendant.MyStatusButton;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.widget.PressedButton;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2016/12/1.
 */

public class ClipPage extends IPage
{

    //private static final String TAG_CLIP = "裁剪";
    private ClipPageSite mSite;
    private Context mContext;
    private int DEF_IMG_SIZE;

    private boolean mUiEnabled = false;
    private boolean mCanbeReset = false; //是否可重置

    private Bitmap mOrgBmp; //原始
    private Object mOrgInfo;

    private MyClipView mView;
    private LinearLayout mBottomFr;
    private HorizontalScrollView mBtnBarFr;
    private FrameLayout mBottomBar;
    private MyStatusButton mCenterBtn;


    private PressedButton mCancelBtn;
    private PressedButton mOkBtn;

    private ClipItemButton mClipReset;
    private ClipItemButton mClipRotate;
    private ClipItemButton mClipMirror;
    private ClipItemButton mClipFree;
    private ClipItemButton mClip1_1;
    private ClipItemButton mClip4_3;
    private ClipItemButton mClip3_4;
    private ClipItemButton mClip16_9;
    private ClipItemButton mClip9_16;

    private int mFhW;
    private int mFhH;
    private int mBottomLayoutHeight;
    private int mBottomFrHeight;
    private int mBottomBarHeight;

    private boolean isFold = false;

    private boolean isEdited = false;

    //ui anim
    private float m_currImgH = 0f;
    private int m_imgH = 0;
    private int m_viewH = 0;
    private int m_viewTopMargin;
    private static final int SHOW_VIEW_ANIM = 300;
    private static final int SHOW_CLOSE_VIEW_ANIM = 200;

    public ClipPage(Context context, BaseSite site)
    {
        super(context, site);
        this.mContext = context;
        this.mSite = (ClipPageSite) site;

        InitData();

        InitUI();

        MyBeautyStat.onPageStartByRes(R.string.美颜美图_编辑页面_主页面);
        TongJiUtils.onPageStart(getContext(), R.string.编辑);
    }

    /**
     * @param params imgs : RotationImg[] / Bitmap
     */
    @Override
    public void SetData(HashMap<String, Object> params)
    {
        resetData();
        if (params != null)
        {
            Object o;
            o = params.get(Beautify4Page.PAGE_ANIM_IMG_H);
            if (o != null && o instanceof Integer)
            {
                m_imgH = (int) o;
            }

            o = params.get(Beautify4Page.PAGE_ANIM_VIEW_H);
            if (o != null && o instanceof Integer)
            {
                m_viewH = (int) o;
            }

            o = params.get(Beautify4Page.PAGE_ANIM_VIEW_TOP_MARGIN);
            if (o != null && o instanceof Integer)
            {
                m_viewTopMargin = (int) o;
            }

            o = params.get("imgs");
            if (o != null)
            {
                SetImg(o);
            }
        }
    }

    @Override
    public void onBack()
    {
        if (mOnAnimationClickListener != null)
        {
            mOnAnimationClickListener.onAnimationClick(mCancelBtn);
        }
    }

    @Override
    public void onClose()
    {
        mUiEnabled = false;

        clearExitDialog();

        if (mView != null)
        {
            removeView(mView);
            mView.ClearAll();
            mView = null;
        }
        if (mOrgBmp != null)
        {
            mOrgBmp = null;
        }
        if (mOrgInfo != null)
        {
            mOrgInfo = null;
        }

        MyBeautyStat.onPageEndByRes(R.string.美颜美图_编辑页面_主页面);
        TongJiUtils.onPageEnd(getContext(), R.string.编辑);
    }

    @Override
    public void onPause()
    {
        TongJiUtils.onPagePause(getContext(), R.string.编辑);
        super.onPause();
    }

    @Override
    public void onResume()
    {
        TongJiUtils.onPageResume(getContext(), R.string.编辑);
        super.onResume();
    }

    private void InitData()
    {
        ShareData.InitData(mContext);
        DEF_IMG_SIZE = SysConfig.GetPhotoSize(getContext());

        mBottomBarHeight = ShareData.PxToDpi_xhdpi(88);
        mBottomLayoutHeight = ShareData.PxToDpi_xhdpi(232);
        mBottomFrHeight = mBottomBarHeight + mBottomLayoutHeight;//88 + 232

        mFhW = ShareData.m_screenWidth;
        mFhH = ShareData.m_screenHeight - mBottomFrHeight;

        mFhW -= mFhW % 2;
        mFhH -= mFhH % 2;
    }

    private void InitUI()
    {
        LayoutParams fp;
        LinearLayout.LayoutParams lp;

        mView = new MyClipView((Activity) getContext(), mFhW, mFhH, mCallback);
        fp = new LayoutParams(LayoutParams.MATCH_PARENT, mFhH);
        fp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        this.addView(mView, 0, fp);

        mBottomFr = new LinearLayout(mContext);
        mBottomFr.setOrientation(LinearLayout.VERTICAL);
        mBottomFr.setGravity(Gravity.CENTER_HORIZONTAL);
        fp = new LayoutParams(LayoutParams.MATCH_PARENT, mBottomFrHeight);
        fp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        this.addView(mBottomFr, fp);
        {
            mBottomBar = new FrameLayout(mContext);
            mBottomBar.setBackgroundColor(0xe6ffffff);
            mBottomBar.setClickable(true);
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(88));
            lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
            mBottomFr.addView(mBottomBar, lp);
            {
                mCancelBtn = new PressedButton(mContext, R.drawable.beautify_cancel, R.drawable.beautify_cancel);
                mCancelBtn.setPadding(ShareData.PxToDpi_xhdpi(22), 0, ShareData.PxToDpi_xhdpi(22), 0);
                mCancelBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mCancelBtn.setOnTouchListener(mOnAnimationClickListener);
                fp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                fp.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                mBottomBar.addView(mCancelBtn, fp);

                mOkBtn = new PressedButton(mContext, R.drawable.beautify_ok, R.drawable.beautify_ok);
                mOkBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mOkBtn.setOnTouchListener(mOnAnimationClickListener);
                mOkBtn.setPadding(ShareData.PxToDpi_xhdpi(22), 0, ShareData.PxToDpi_xhdpi(22), 0);
                ImageUtils.AddSkin(mContext, mOkBtn);
                fp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                fp.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
                mBottomBar.addView(mOkBtn, fp);

                mCenterBtn = new MyStatusButton(mContext);
                mCenterBtn.setData(ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getResources(), R.drawable.clip_color_icon)), getContext().getString(R.string.clippage_clip));
                mCenterBtn.setBtnStatus(true, false);
                mCenterBtn.setOnClickListener(mBtnOnClickListener);
                fp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                fp.gravity = Gravity.CENTER;
                mCenterBtn.setLayoutParams(fp);
                mBottomBar.addView(mCenterBtn);
            }

            //素材区域
            mBtnBarFr = new HorizontalScrollView(mContext);
            mBtnBarFr.setBackgroundColor(Color.TRANSPARENT);
            mBtnBarFr.setHorizontalScrollBarEnabled(false);
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mBottomLayoutHeight);
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            mBottomFr.addView(mBtnBarFr, lp);
            {
                LinearLayout btnLayout = new LinearLayout(getContext());
                btnLayout.setOrientation(LinearLayout.HORIZONTAL);
                btnLayout.setGravity(Gravity.CENTER_VERTICAL);
                fp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                fp.topMargin = ShareData.PxToDpi_xhdpi(56);
                btnLayout.setLayoutParams(fp);
                mBtnBarFr.addView(btnLayout, fp);
                {
                    int mw = ShareData.PxToDpi_xhdpi(80);
                    int mh = LinearLayout.LayoutParams.WRAP_CONTENT;

                    // left margin:32   right margin:18   last right margin:32
                    int leftMargin = ShareData.PxToDpi_xhdpi(32);
                    int rightMargin = ShareData.PxToDpi_xhdpi(18);

                    mClipReset = new ClipItemButton(mContext);
                    mClipReset.InitData(R.drawable.clip_page_clip_reset2, getContext().getString(R.string.clippage_clip_reset), mBtnOnClickListener);
                    mClipReset.SetTextColor(0x26000000, false);
                    lp = new LinearLayout.LayoutParams(mw, mh);
                    lp.leftMargin = leftMargin;
                    lp.rightMargin = rightMargin;
                    mClipReset.setLayoutParams(lp);
                    btnLayout.addView(mClipReset);

                    mClipRotate = new ClipItemButton(mContext);
                    mClipRotate.InitData(R.drawable.clip_page_clip_rotate, getContext().getString(R.string.clippage_clip_rotate), mBtnOnClickListener);
                    lp = new LinearLayout.LayoutParams(mw, mh);
                    lp.leftMargin = leftMargin;
                    lp.rightMargin = rightMargin;
                    mClipRotate.setLayoutParams(lp);
                    btnLayout.addView(mClipRotate);

                    mClipMirror = new ClipItemButton(mContext);
                    mClipMirror.InitData(R.drawable.clip_page_clip_mirror, getContext().getString(R.string.clippage_clip_mirror), mBtnOnClickListener);
                    lp = new LinearLayout.LayoutParams(mw, mh);
                    lp.leftMargin = leftMargin;
                    lp.rightMargin = rightMargin;
                    mClipMirror.setLayoutParams(lp);
                    btnLayout.addView(mClipMirror);

                    mClipFree = new ClipItemButton(mContext);
                    mClipFree.InitData(R.drawable.clip_page_clip_free, getContext().getString(R.string.clippage_clip_free), mBtnOnClickListener);
                    lp = new LinearLayout.LayoutParams(mw, mh);
                    lp.leftMargin = leftMargin;
                    lp.rightMargin = rightMargin;
                    mClipFree.setLayoutParams(lp);
                    btnLayout.addView(mClipFree);

                    mClip1_1 = new ClipItemButton(mContext);
                    mClip1_1.InitData(R.drawable.clip_page_clip_1_1, getContext().getString(R.string.clippage_clip_1_1), mBtnOnClickListener);
                    lp = new LinearLayout.LayoutParams(mw, mh);
                    lp.leftMargin = leftMargin;
                    lp.rightMargin = rightMargin;
                    mClip1_1.setLayoutParams(lp);
                    btnLayout.addView(mClip1_1);

                    mClip4_3 = new ClipItemButton(mContext);
                    mClip4_3.InitData(R.drawable.clip_page_clip_4_3, getContext().getString(R.string.clippage_clip_4_3), mBtnOnClickListener);
                    lp = new LinearLayout.LayoutParams(mw, mh);
                    lp.leftMargin = leftMargin;
                    lp.rightMargin = rightMargin;
                    mClip4_3.setLayoutParams(lp);
                    btnLayout.addView(mClip4_3);

                    mClip3_4 = new ClipItemButton(mContext);
                    mClip3_4.InitData(R.drawable.clip_page_clip_3_4, getContext().getString(R.string.clippage_clip_3_4), mBtnOnClickListener);
                    lp = new LinearLayout.LayoutParams(mw, mh);
                    lp.leftMargin = leftMargin;
                    lp.rightMargin = rightMargin;
                    mClip3_4.setLayoutParams(lp);
                    btnLayout.addView(mClip3_4);

                    mClip16_9 = new ClipItemButton(mContext);
                    mClip16_9.InitData(R.drawable.clip_page_clip_16_9, getContext().getString(R.string.clippage_clip_16_9), mBtnOnClickListener);
                    lp = new LinearLayout.LayoutParams(mw, mh);
                    lp.leftMargin = leftMargin;
                    lp.rightMargin = rightMargin;
                    mClip16_9.setLayoutParams(lp);
                    btnLayout.addView(mClip16_9);

                    mClip9_16 = new ClipItemButton(mContext);
                    mClip9_16.InitData(R.drawable.clip_page_clip_9_16, getContext().getString(R.string.clippage_clip_9_16), mBtnOnClickListener);
                    lp = new LinearLayout.LayoutParams(mw, mh);
                    lp.leftMargin = leftMargin;
                    lp.rightMargin = leftMargin;
                    mClip9_16.setLayoutParams(lp);
                    btnLayout.addView(mClip9_16);
                }
            }
        }

        mUiEnabled = true;
    }

    private void resetData()
    {
    }

    private void SetImg(Object params)
    {
        if (params != null)
        {
            if (params instanceof RotationImg2[])
            {
                mOrgInfo = params;
                mOrgBmp = cn.poco.imagecore.Utils.DecodeFinalImage(getContext(), ((RotationImg2[]) params)[0].m_img, ((RotationImg2[]) params)[0].m_degree, -1, ((RotationImg2[]) params)[0].m_flip, -1, -1);
//                mOrgBmp = Utils.DecodeShowImage((Activity) getContext(), ((RotationImg2[]) params)[0].m_img, ((RotationImg2[]) params)[0].m_degree, -1, ((RotationImg2[]) params)[0].m_flip);
            }
            else if (params instanceof Bitmap)
            {
                mOrgBmp = (Bitmap) params;
            }

            if (mView != null && mOrgBmp != null && !mOrgBmp.isRecycled())
            {
                mView.SetImg(mOrgInfo, mOrgBmp, true);
            }

            ShowStarAnim();
        }
    }

    private void ShowStarAnim()
    {
        if (m_viewH > 0 && m_imgH > 0 && mOrgBmp != null)
        {
            int tempStartY = (int) (ShareData.PxToDpi_xhdpi(90) + (m_viewH - mFhH) / 2f);
            float scaleX = (float) mFhW / (float) mOrgBmp.getWidth();
            float scaleY = (float) mFhH / (float) mOrgBmp.getHeight();
            m_currImgH = mOrgBmp.getHeight() * Math.min(scaleX, scaleY);
            float scaleH = m_imgH / m_currImgH;
            ShowViewAnim(mView, tempStartY, 0, scaleH, 1f, SHOW_VIEW_ANIM);
        }
    }

    private void ShowViewAnim(final View view, int startY, int endY, float startScale, float endScale, int duration)
    {
        if (view != null)
        {

            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator object1 = ObjectAnimator.ofFloat(view, "scaleX", startScale, endScale);
            ObjectAnimator object2 = ObjectAnimator.ofFloat(view, "scaleY", startScale, endScale);
            ObjectAnimator object3 = ObjectAnimator.ofFloat(view, "translationY", startY, endY);
            ObjectAnimator object4 = ObjectAnimator.ofFloat(mBottomFr, "translationY", mBottomBarHeight + mBottomLayoutHeight, 0);
            animatorSet.setDuration(duration);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.playTogether(object1, object2, object3, object4);
            animatorSet.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mUiEnabled = true;
                    if (view != null)
                    {
                        view.clearAnimation();
                    }
                    if (mBottomFr != null)
                    {
                        mBottomFr.clearAnimation();
                    }
                }
            });
            animatorSet.start();
        }
    }

    private OnClickListener mBtnOnClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (v == mClipReset)
            {
                if (mCanbeReset)
                {
                    MyBeautyStat.onClickByRes(R.string.美颜美图_编辑页面_主页面_重置);
                    TongJi2.AddCountByRes(getContext(), R.integer.修图_编辑_重置);
                    setEdit(ClipType.RESET);
                }
            }
            else if (v == mClipRotate)
            {
                MyBeautyStat.onClickByRes(R.string.美颜美图_编辑页面_主页面_旋转);
                TongJi2.AddCountByRes(getContext(), R.integer.修图_编辑_旋转);
                setEdit(ClipType.ROTATE);
            }
            else if (v == mClipMirror)
            {
                MyBeautyStat.onClickByRes(R.string.美颜美图_编辑页面_主页面_镜像);
                TongJi2.AddCountByRes(getContext(), R.integer.修图_编辑_镜像);
                setEdit(ClipType.MIRROR);
            }
            else if (v == mClipFree)
            {
                MyBeautyStat.onClickByRes(R.string.美颜美图_编辑页面_主页面_FREE);
                TongJi2.AddCountByRes(getContext(), R.integer.修图_编辑_FREE);
                setEdit(ClipType.FREE);
            }
            else if (v == mClip1_1)
            {
                MyBeautyStat.onClickByRes(R.string.美颜美图_编辑页面_主页面_1_1);
                TongJi2.AddCountByRes(getContext(), R.integer.修图_编辑_1_1);
                setEdit(ClipType.C1_1);
            }
            else if (v == mClip4_3)
            {
                MyBeautyStat.onClickByRes(R.string.美颜美图_编辑页面_主页面_4_3);
                TongJi2.AddCountByRes(getContext(), R.integer.修图_编辑_4_3);
                setEdit(ClipType.C4_3);
            }
            else if (v == mClip3_4)
            {
                MyBeautyStat.onClickByRes(R.string.美颜美图_编辑页面_主页面_3_4);
                TongJi2.AddCountByRes(getContext(), R.integer.修图_编辑_3_4);
                setEdit(ClipType.C3_4);
            }
            else if (v == mClip16_9)
            {
                MyBeautyStat.onClickByRes(R.string.美颜美图_编辑页面_主页面_16_9);
                TongJi2.AddCountByRes(getContext(), R.integer.修图_编辑_16_9);
                setEdit(ClipType.C16_9);
            }
            else if (v == mClip9_16)
            {
                MyBeautyStat.onClickByRes(R.string.美颜美图_编辑页面_主页面_9_16);
                TongJi2.AddCountByRes(getContext(), R.integer.修图_编辑_9_16);
                setEdit(ClipType.C9_16);
            }
            else if (v == mCenterBtn)
            {
                isFold = !isFold;
                SetViewState(isFold);
                SetBottomFrState(isFold);
                mCenterBtn.setBtnStatus(true, isFold);
                MyBeautyStat.onClickByRes(isFold ? R.string.美颜美图_编辑页面_主页面_收回bar : R.string.美颜美图_编辑页面_主页面_展开bar);
                TongJi2.AddCountByRes(getContext(), isFold ? R.integer.修图_编辑_收回bar : R.integer.修图_编辑_展开bar);
            }
        }
    };

    private OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener()
    {
        @Override
        public void onAnimationClick(View v)
        {
            if (v == mCancelBtn)
            {
                MyBeautyStat.onClickByRes(R.string.美颜美图_编辑页面_主页面_取消);
                TongJi2.AddCountByRes(getContext(), R.integer.修图_编辑_取消);
                if (isEdited)
                {
                    showExitDialog();
                }
                else
                {
                    onExit();
                }
            }
            else if (v == mOkBtn)
            {
                MyBeautyStat.onClickByRes(R.string.美颜美图_编辑页面_主页面_确认);
                TongJi2.AddCountByRes(getContext(), R.integer.修图_编辑_确认);
                if (mSite != null && mView != null)
                {
                    Bitmap bitmap = mView.GetClipBmp();
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("img", bitmap);
                    params.putAll(getBackAnimParam());
                    mSite.OnSave(getContext(), params);
                }
            }
        }

        @Override
        public void onTouch(View v)
        {

        }

        @Override
        public void onRelease(View v)
        {

        }
    };

    private HashMap<String, Object> getBackAnimParam()
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put(Beautify4Page.PAGE_BACK_ANIM_VIEW_TOP_MARGIN, mView != null ? mView.getTranslationY() : 0);
        params.put(Beautify4Page.PAGE_BACK_ANIM_IMG_H, (float) m_imgH);
        return params;
    }

    private void removeShowView()
    {
        if (mView != null)
        {
            removeView(mView);
            mView = null;
        }
    }


    private void setEdit(@ClipType int type)
    {
        if (mView != null)
        {
            boolean reset = true;
            isEdited = true;
            switch (type)
            {
                case ClipType.RESET:
                {
                    if (mOrgBmp != null && !mOrgBmp.isRecycled())
                    {
                        mView.SetImg(mOrgInfo, mOrgBmp, true);
                    }
                    reset = false;
                    isEdited = false;
                    break;
                }
                case ClipType.ROTATE:
                {
                    mView.AnimRotate(90);
                    mView.invalidate();
                    break;
                }
                case ClipType.MIRROR:
                {
                    mView.AnimFlipH();
                    mView.invalidate();
                    break;
                }
                case ClipType.FREE:
                {
                    mView.SetClipWHScale(-1, true);
                    mView.invalidate();
                    break;
                }
                case ClipType.C1_1:
                {
                    mView.SetClipWHScale(1);
                    mView.invalidate();
                    break;
                }
                case ClipType.C4_3:
                {
                    float scale = 4f / 3f;
                    mView.SetClipWHScale(scale);
                    mView.invalidate();
                    break;
                }
                case ClipType.C3_4:
                {
                    float scale = 3f / 4f;
                    mView.SetClipWHScale(scale);
                    mView.invalidate();
                    break;
                }
                case ClipType.C16_9:
                {
                    float scale = 16f / 9f;
                    mView.SetClipWHScale(scale);
                    mView.invalidate();
                    break;
                }
                case ClipType.C9_16:
                {
                    float scale = 9f / 16f;
                    mView.SetClipWHScale(scale);
                    mView.invalidate();
                    break;
                }
            }
            setResetBtnState(reset);
        }
    }

    private void setResetBtnState(boolean reset)
    {
        if (this.mCanbeReset != reset)
        {
            this.mCanbeReset = reset;
            if (mClipReset != null)
            {
                mClipReset.SetImgRes(reset ? R.drawable.clip_page_clip_reset1 : R.drawable.clip_page_clip_reset2, reset);
                mClipReset.SetTextColor(reset ? 0xb3000000 : 0x26000000, reset);
            }
        }
    }

    private void SetBottomFrState(boolean isFold)
    {
        if (mBottomFr != null)
        {
            mBottomFr.clearAnimation();
            int start;
            int end;
            start = isFold ? 0 : mBottomLayoutHeight;
            end = isFold ? mBottomLayoutHeight : 0;
            ObjectAnimator object = ObjectAnimator.ofFloat(mBottomFr, "translationY", start, end);
            object.setDuration(300);
            object.setInterpolator(new AccelerateDecelerateInterpolator());
            object.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    mUiEnabled = false;
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mUiEnabled = true;
                }
            });
            object.start();
        }
    }

    private void SetViewState(boolean isFold)
    {
        if (mView != null)
        {
            mView.clearAnimation();
            float start;
            float end;
            if (isFold)
            {
                start = 0;
                end = mBottomLayoutHeight / 2;
            }
            else
            {
                start = mBottomLayoutHeight / 2;
                end = 0;
            }
            mUiEnabled = false;
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mView, "translationY", start, end);
            objectAnimator.setDuration(300);
            objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimator.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mUiEnabled = true;
                }
            });
            objectAnimator.start();
        }
    }

    private MyClipView.Callback mCallback = new ClipView.Callback()
    {
        @Override
        public Bitmap MakeShowImg(Object info, int frW, int frH)
        {
            return null;
        }

        @Override
        public Bitmap MakeOutputImg(Object info, int outW, int outH)
        {
            return null;
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ClipType.RESET, ClipType.ROTATE, ClipType.MIRROR, ClipType.FREE, ClipType.C1_1, ClipType.C4_3, ClipType.C3_4, ClipType.C16_9, ClipType.C9_16})
    public @interface ClipType
    {
        int RESET = 0;
        int ROTATE = 1;
        int MIRROR = 2;
        int FREE = 3;
        int C1_1 = 4;
        int C4_3 = 5;
        int C3_4 = 6;
        int C16_9 = 7;
        int C9_16 = 8;
    }

    private CloudAlbumDialog mExitDialog;

    private void showExitDialog()
    {
        if (mExitDialog == null)
        {
            mExitDialog = new CloudAlbumDialog(getContext(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ImageUtils.AddSkin(getContext(), mExitDialog.getOkButtonBg());
            mExitDialog.setCancelable(true).setCancelButtonText(R.string.cancel).setOkButtonText(R.string.ensure).setMessage(R.string.confirm_back).setListener(new CloudAlbumDialog.OnButtonClickListener()
            {
                @Override
                public void onOkButtonClick()
                {
                    if (mExitDialog != null)
                    {
                        mExitDialog.dismiss();
                    }
                    onExit();
                }

                @Override
                public void onCancelButtonClick()
                {
                    if (mExitDialog != null)
                    {
                        mExitDialog.dismiss();
                    }
                }
            });
        }
        mExitDialog.show();
    }

    private void clearExitDialog()
    {
        if (mExitDialog != null)
        {
            mExitDialog.dismiss();
            mExitDialog.setListener(null);
            mExitDialog = null;
        }
    }

    private void onExit()
    {
        if (mSite != null)
        {
            HashMap<String, Object> params = new HashMap<>();
            params.put("img", mOrgBmp);
            params.putAll(getBackAnimParam());
            removeShowView();
            mOrgBmp = null; //置空不能recycle
            mSite.OnBack(getContext(), params);
        }
    }
}
