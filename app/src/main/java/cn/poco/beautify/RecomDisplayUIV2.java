package cn.poco.beautify;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.net.URLDecoder;

import cn.poco.MaterialMgr2.MgrUtils;
import cn.poco.advanced.ImageUtils;
import cn.poco.credits.Credit;
import cn.poco.resource.ResType;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.DrawableUtils;
import cn.poco.utils.WaitAnimDialog;
import my.beautyCamera.R;

import static cn.poco.tianutils.ShareData.PxToDpi_xhdpi;

/**
 * 只实现了界面逻辑<BR/>
 * 1.RecomDisplayUI obj = new RecomDisplayUI(...);<BR/>
 * 2.obj.CreateUI();<BR/>
 * 3.obj.SetBk(..); / obj.SetImg(...); / obj.SetContent(...);<BR/>
 * 4.obj.SetImgState(...); / obj.SetBtnState(...);<BR/>
 * 5.obj.Show(...);<BR/>
 * 6.obj.OnCancel();<BR/>
 */
public class RecomDisplayUIV2 {
    public static final int BTN_STATE_UNLOCK = 0x1;
    public static final int BTN_STATE_LOADING = 0x2;
    public static final int BTN_STATE_DOWNLOAD = 0x4;
    public static final int BTN_STATE_LIMIT_DOWNLOAD = 0x10;

    public static final int IMG_STATE_COMPLETE = 0x1;
    public static final int IMG_STATE_LOADING = 0x2;

    protected boolean m_uiEnabled;
    protected Context mContext;
    protected Callback mCallback;

    protected int mContainerWidth;
    protected int mContainerHeight;
    protected int m_frBottomMargin;
    protected int m_fr2W;
    protected int m_fr2H;
    protected int mImgW;
    protected int mImgH;
    protected int m_fr3W;
    protected int m_fr3H;
    protected int m_itemH;

    protected FrameLayout mParentLayout;
    protected FrameLayout mRootLayout;
    protected ImageView mBgView;
    protected Bitmap mBgBmp;

    protected FrameLayout mContainerLayout;
    protected FrameLayout mResDetailLayout;
    protected FrameLayout mUnlockTypeLayout;
    protected TextView m_weixinTip;
    protected boolean m_animFinish = true;

    protected RelativeLayout mLimitLayout;
    protected ImageView mLimitDetail;

    protected FrameLayout mImgLayout;
    protected WaitAnimDialog.WaitAnimView mImgLoading;
    protected ImageView mImageView;
    protected Bitmap m_imgBmp;
    protected int m_imgState;
    protected TextView mTitleView;
    protected TextView mContentView;

    protected LinearLayout confirmBtnLayout;
    protected ProgressBar mLoadingBtn;
    protected ImageView mBtn; //解锁/下载/loading/
    protected TextView mBtnText;
    protected int m_btnState;
    protected FrameLayout m_weixinUnlock;
    protected FrameLayout mCreditUnlock;
    private TextView mCreditTip;
    private ImageView mCreditWarn;
    protected TextView mCancelBtn;

    protected String m_credit = "";

    private ShowImgGlideTransformation mShowImgGlideTransformation;

    private int shapeRadius;

    public interface Callback {
        /**
         * 完成关闭动画后回调
         */
        public void OnClose();

        /**
         * 点击关闭或空白地方
         */
        public void OnCloseBtn();

        /**
         * @param state
         * @param unlock 是否解锁
         */
        public void OnBtn(int state, boolean unlock);

        public void OnCredit(String credit);
    }

    public RecomDisplayUIV2(Activity ac, Callback cb) {
        mContext = ac;
        mCallback = cb;
    }

    public void CreateUI() {
        CreateUI(0);
    }

    public void CreateUI(FrameLayout parent, int uiType) {
        CreateUI(uiType);
        mParentLayout = parent;
        if (mParentLayout != null && mRootLayout != null)
        {
            mParentLayout.removeView(mRootLayout);
            FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            mRootLayout.setLayoutParams(fl);
            mParentLayout.addView(mRootLayout);
            mRootLayout.setVisibility(View.GONE);
        }
    }

    /**
     * @param uiType 0:分享解锁UI 1:限量UI
     */
    public void CreateUI(int uiType) {
        if (mRootLayout == null) {
            ShareData.InitData(mContext);
            mContainerWidth = ShareData.m_screenRealWidth;//PxToDpi_xhdpi(640);
            mContainerHeight = PxToDpi_xhdpi(900);
            m_frBottomMargin = PxToDpi_xhdpi(100);
            m_fr2W = PxToDpi_xhdpi(570);
            m_fr2H = PxToDpi_xhdpi(844 + 30);
            m_fr3W = PxToDpi_xhdpi(611);
            m_itemH = PxToDpi_xhdpi(236);
            m_fr3H = m_itemH * 2 + PxToDpi_xhdpi(30);
            mImgW = PxToDpi_xhdpi(570);
            mImgH = PxToDpi_xhdpi(570);
            shapeRadius = ShareData.getRealPixel_720P(30);

            //大屏幕居中
            if (ShareData.m_screenRealHeight - (mContainerHeight + m_frBottomMargin) > (ShareData.m_screenRealHeight - m_fr2H) / 2) {
                mContainerHeight = ShareData.m_screenRealHeight - (ShareData.m_screenRealHeight - m_fr2H) / 2 - m_frBottomMargin;
            }

            mRootLayout = new FrameLayout(mContext);
            {
                FrameLayout.LayoutParams fParams;

                mBgView = new ImageView(mContext);
                if(mBgBmp != null)
                {
                    mBgView.setBackgroundDrawable(new BitmapDrawable(mBgBmp));
                }
                else
                {
                    mBgView.setBackgroundResource(R.drawable.login_tips_all_bk);
                }
                mBgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCallback != null) {
                            mCallback.OnCloseBtn();
                        }
                    }
                });
                fParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                fParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                mRootLayout.addView(mBgView, fParams);

                /*ImageView mask = new ImageView(mContext);
                mask.setBackgroundColor(0x33000000);
                fParams = new FrameLayout.LayoutParams(ShareData.m_screenWidth, ShareData.m_screenHeight);
                fParams.gravity = Gravity.LEFT | Gravity.TOP;
                mRootLayout.addView(mask, fParams);*/

                mContainerLayout = new FrameLayout(mContext);
                fParams = new FrameLayout.LayoutParams(mContainerWidth, mContainerHeight);
                fParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
                fParams.bottomMargin = m_frBottomMargin;
                mRootLayout.addView(mContainerLayout, fParams);
                {
                    mResDetailLayout = new FrameLayout(mContext);
                    mResDetailLayout.setVisibility(View.GONE);
                    mResDetailLayout.setBackgroundDrawable(DrawableUtils.shapeDrawable(0xdbffffff, shapeRadius));
                    mResDetailLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
                    fParams = new FrameLayout.LayoutParams(m_fr2W, m_fr2H);
                    fParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                    mContainerLayout.addView(mResDetailLayout, fParams);
                    {
                        mImgLayout = new FrameLayout(mContext);
                        fParams = new FrameLayout.LayoutParams(mImgW, mImgH);
                        fParams.gravity = Gravity.LEFT | Gravity.TOP;
                        mResDetailLayout.addView(mImgLayout, fParams);
                        {
                            mImgLoading = new WaitAnimDialog.WaitAnimView(mContext);
//                            mImgLoading = new ProgressBar(mContext);
//                            mImgLoading.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.unlock_progress));
                            mImgLoading.setVisibility(View.GONE);
                            fParams = new FrameLayout.LayoutParams(PxToDpi_xhdpi(46), PxToDpi_xhdpi(46));
                            fParams.gravity = Gravity.CENTER;
                            mImgLayout.addView(mImgLoading, fParams);

                            mImageView = new ImageView(mContext);
//                            mImageView.setBackgroundColor(Color.DKGRAY);
                            mImageView.setScaleType(ScaleType.CENTER_CROP);//CENTER_CROP
                            fParams = new FrameLayout.LayoutParams(mImgW, mImgH);
                            fParams.gravity = Gravity.CENTER;
                            mImgLayout.addView(mImageView, fParams);
                        }

                        LinearLayout bottomLayout = new LinearLayout(mContext);
                        bottomLayout.setOrientation(LinearLayout.VERTICAL);
                        bottomLayout.setBackgroundResource(R.drawable.display_bottom_bg);
                        fParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        fParams.gravity = Gravity.LEFT | Gravity.TOP;
                        fParams.topMargin = PxToDpi_xhdpi(530);
                        mResDetailLayout.addView(bottomLayout, fParams);
                        {
                            mTitleView = new TextView(mContext);
                            mTitleView.getPaint().setFakeBoldText(true);
                            mTitleView.setSingleLine();
                            mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                            mTitleView.setTextColor(0xff333333);//0xff000000
                            mTitleView.setEllipsize(TextUtils.TruncateAt.END);
                            mTitleView.setGravity(Gravity.CENTER_HORIZONTAL);
                            LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, PxToDpi_xhdpi(40));
                            lParams.gravity = Gravity.LEFT | Gravity.TOP;
                            lParams.leftMargin = PxToDpi_xhdpi(50);
                            lParams.rightMargin = PxToDpi_xhdpi(50);
                            lParams.topMargin = PxToDpi_xhdpi(8);
                            bottomLayout.addView(mTitleView, lParams);

                            mContentView = new TextView(mContext);
                            mContentView.setMaxLines(2);
                            mContentView.setEllipsize(TextUtils.TruncateAt.END);
                            mContentView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                            mContentView.setTextColor(0xff333333);//0xcc000000
                            mContentView.setGravity(Gravity.CENTER_HORIZONTAL);
                            lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, PxToDpi_xhdpi(80));
                            lParams.gravity = Gravity.LEFT | Gravity.TOP;
                            lParams.leftMargin = PxToDpi_xhdpi(50);
                            lParams.rightMargin = PxToDpi_xhdpi(50);
                            lParams.topMargin = PxToDpi_xhdpi(3);
                            lParams.bottomMargin = PxToDpi_xhdpi(2);
                            bottomLayout.addView(mContentView, lParams);
                        }

                        LinearLayout btnLayout = new LinearLayout(mContext);
                        btnLayout.setOrientation(LinearLayout.VERTICAL);
                        fParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                        fParams.leftMargin = PxToDpi_xhdpi(60);
                        fParams.rightMargin = PxToDpi_xhdpi(60);
                        fParams.topMargin = PxToDpi_xhdpi(24);
                        fParams.gravity = Gravity.BOTTOM;
                        mResDetailLayout.addView(btnLayout, fParams);
                        {
                            LinearLayout.LayoutParams lParams;

                            confirmBtnLayout = new LinearLayout(mContext);
                            Bitmap bgBmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.unlock_download_bg);
                            confirmBtnLayout.setBackgroundDrawable(DrawableUtils.pressedSelector(mContext, ImageUtils.AddSkin(mContext, bgBmp), 0.86f));
                            confirmBtnLayout.setGravity(Gravity.CENTER);
                            confirmBtnLayout.setOnClickListener(m_btnLst);
                            lParams = new LinearLayout.LayoutParams(PxToDpi_xhdpi(450), ShareData.getRealPixel_720P(80));
                            btnLayout.addView(confirmBtnLayout, lParams);
                            {
                                LinearLayout confirmLayout = new LinearLayout(mContext);
                                confirmLayout.setGravity(Gravity.CENTER_VERTICAL);
                                lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                confirmBtnLayout.addView(confirmLayout, lParams);
                                {
                                    mLoadingBtn = new ProgressBar(mContext);
                                    mLoadingBtn.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.unlock_progress));
                                    mLoadingBtn.setVisibility(View.GONE);
                                    lParams = new LinearLayout.LayoutParams(PxToDpi_xhdpi(24), PxToDpi_xhdpi(24));
                                    lParams.gravity = Gravity.CENTER_VERTICAL;
                                    lParams.rightMargin = PxToDpi_xhdpi(10);
                                    confirmLayout.addView(mLoadingBtn, lParams);

                                    mBtn = new ImageView(mContext);
//                                    mBtn.setScaleType(ScaleType.CENTER);
                                    mBtn.setOnClickListener(m_btnLst);
                                    lParams = new LinearLayout.LayoutParams(PxToDpi_xhdpi(20), PxToDpi_xhdpi(22));
                                    lParams.gravity = Gravity.CENTER_VERTICAL;
                                    lParams.rightMargin = PxToDpi_xhdpi(10);
                                    confirmLayout.addView(mBtn, lParams);

                                    mBtnText = new TextView(mContext);
                                    mBtnText.setText(R.string.unlock_download);
                                    mBtnText.setClickable(true);
                                    mBtnText.setOnClickListener(m_btnLst);
                                    mBtnText.getPaint().setFakeBoldText(true);
                                    mBtnText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                                    mBtnText.setTextColor(DrawableUtils.colorPressedDrawable2(0xffffffff, 0x99ffffff));
                                    lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    lParams.gravity = Gravity.CENTER_VERTICAL;
                                    confirmLayout.addView(mBtnText, lParams);
                                }
                            }

                            mCancelBtn = new TextView(mContext);
                            mCancelBtn.setText(R.string.cancel);
                            //mCancelBtn.getPaint().setFakeBoldText(true);
                            mCancelBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
                            mCancelBtn.setTextColor(DrawableUtils.colorPressedDrawable2(0xffa0a0a0, 0x99a0a0a0));
                            mCancelBtn.setGravity(Gravity.CENTER);
                            mCancelBtn.setOnClickListener(m_btnLst);
                            fParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, PxToDpi_xhdpi(90));
                            btnLayout.addView(mCancelBtn, fParams);
                        }
                    }
					    /*限量素材添加tip*/
                    if (uiType == 1) {
                        mLimitLayout = new RelativeLayout(mContext);
                        mLimitLayout.setOnClickListener(m_btnLst);
                        FrameLayout.LayoutParams lParams = new FrameLayout.LayoutParams(PxToDpi_xhdpi(200), PxToDpi_xhdpi(200));
                        lParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                        lParams.topMargin = PxToDpi_xhdpi(610);
                        lParams.bottomMargin = PxToDpi_xhdpi(235);
                        mContainerLayout.addView(mLimitLayout, lParams);

                        mLimitDetail = new ImageView(mContext);
                        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//                        rParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        mLimitLayout.addView(mLimitDetail, rParams);
                    }

                    mUnlockTypeLayout = new FrameLayout(mContext);
                    mUnlockTypeLayout.setVisibility(View.VISIBLE);
                    fParams = new FrameLayout.LayoutParams(m_fr3W, m_fr3H);
                    fParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                    fParams.topMargin = (m_fr2H - m_fr3H) / 2;
                    mContainerLayout.addView(mUnlockTypeLayout, fParams);
                    {
                        ImageView bg = new ImageView(mContext);
                        bg.setBackgroundResource(R.drawable.display_up_shadow);
                        fParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, m_itemH);
                        fParams.gravity = Gravity.TOP;
                        mUnlockTypeLayout.addView(bg, fParams);

                        int itemH = PxToDpi_xhdpi(220);//220+16
                        int itemW = PxToDpi_xhdpi(606);
                        int iconW = PxToDpi_xhdpi(110);
                        m_weixinUnlock = new FrameLayout(mContext);
                        m_weixinUnlock.setOnClickListener(m_btnLst);
                        m_weixinUnlock.setBackgroundResource(R.drawable.display_up_bg);
                        fParams = new FrameLayout.LayoutParams(itemW, itemH);
                        fParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                        fParams.topMargin = (m_itemH - itemH) / 2;
                        mUnlockTypeLayout.addView(m_weixinUnlock, fParams);
                        {
                            ImageView icon = new ImageView(mContext);
                            icon.setScaleType(ScaleType.CENTER);
                            icon.setImageResource(R.drawable.display_share_icon);
                            fParams = new FrameLayout.LayoutParams(iconW, itemH);
                            fParams.gravity = Gravity.LEFT;
                            fParams.leftMargin = PxToDpi_xhdpi(10);
                            m_weixinUnlock.addView(icon, fParams);
                            ImageUtils.AddSkin(mContext, icon);

                            m_weixinTip = new TextView(mContext);
                            m_weixinTip.setTextColor(ImageUtils.GetSkinColor(0xffe75887));
                            m_weixinTip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            m_weixinTip.setText(R.string.unlock_share_to_weixin);
                            fParams = new FrameLayout.LayoutParams(PxToDpi_xhdpi(380), FrameLayout.LayoutParams.WRAP_CONTENT);
                            fParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                            fParams.leftMargin = iconW + PxToDpi_xhdpi(32 + 10);
                            m_weixinUnlock.addView(m_weixinTip, fParams);

                            ImageView next = new ImageView(mContext);
                            next.setImageResource(R.drawable.display_choose_btn);
                            fParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                            fParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                            fParams.rightMargin = PxToDpi_xhdpi(34);
                            m_weixinUnlock.addView(next, fParams);
                            ImageUtils.AddSkin(mContext, next);
                        }

                        TextView mid = new TextView(mContext);
                        mid.setGravity(Gravity.CENTER);
                        mid.setBackgroundResource(R.drawable.display_middle_icon);
                        mid.setTextColor(ImageUtils.GetSkinColor(0xffe75887));
                        mid.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                        mid.setText("OR");
                        fParams = new FrameLayout.LayoutParams(PxToDpi_xhdpi(40), PxToDpi_xhdpi(40));
                        fParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                        fParams.topMargin = itemH - PxToDpi_xhdpi(4);
//                        fParams.leftMargin = PxToDpi_xhdpi(2);
                        mUnlockTypeLayout.addView(mid, fParams);

                        bg = new ImageView(mContext);
//                        bg.setBackgroundResource(R.drawable.display_down_shadow);
                        fParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, m_itemH);
                        fParams.gravity = Gravity.TOP;
                        fParams.topMargin = itemH + PxToDpi_xhdpi(20 - 5);//30
                        mUnlockTypeLayout.addView(bg, fParams);

                        mCreditUnlock = new FrameLayout(mContext);
                        mCreditUnlock.setOnClickListener(m_btnLst);
                        mCreditUnlock.setBackgroundResource(R.drawable.display_down_bg);
                        fParams = new FrameLayout.LayoutParams(itemW, itemH);
                        fParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                        fParams.topMargin = itemH + PxToDpi_xhdpi(19) + (m_itemH - itemH) / 2 - PxToDpi_xhdpi(3);
                        mUnlockTypeLayout.addView(mCreditUnlock, fParams);
                        {
                            ImageView icon = new ImageView(mContext);
                            icon.setScaleType(ScaleType.CENTER);
                            icon.setImageResource(R.drawable.display_credit_icon);
                            fParams = new FrameLayout.LayoutParams(iconW, itemH);
                            fParams.gravity = Gravity.LEFT;
                            fParams.leftMargin = PxToDpi_xhdpi(10);
                            mCreditUnlock.addView(icon, fParams);
                            ImageUtils.AddSkin(mContext, icon);

                            LinearLayout mTextLinearLayout = new LinearLayout(mContext);
                            mTextLinearLayout.setOrientation(LinearLayout.VERTICAL);
                            mTextLinearLayout.setGravity(Gravity.CENTER_VERTICAL);
                            fParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, itemH);
                            fParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                            fParams.leftMargin = iconW;
                            mCreditUnlock.addView(mTextLinearLayout, fParams);
                            {
                                TextView text = new TextView(mContext);
                                text.setTextColor(ImageUtils.GetSkinColor(0xffe75887));
                                text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                                text.setText(R.string.unlock_use_credit);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(PxToDpi_xhdpi(400), LinearLayout.LayoutParams.WRAP_CONTENT);
                                layoutParams.gravity = Gravity.CENTER_VERTICAL;
                                layoutParams.leftMargin = PxToDpi_xhdpi(32 + 10);
                                mTextLinearLayout.addView(text, layoutParams);

                                LinearLayout mTextLinearLayout2 = new LinearLayout(mContext);
                                mTextLinearLayout2.setOrientation(LinearLayout.HORIZONTAL);
                                layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layoutParams.gravity = Gravity.CENTER_VERTICAL;
                                layoutParams.leftMargin = PxToDpi_xhdpi(32 + 10);
                                mTextLinearLayout.addView(mTextLinearLayout2, layoutParams);

                                mCreditWarn = new ImageView(mContext);
                                mCreditWarn.setImageResource(R.drawable.display_prompt_icon);
                                layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layoutParams.gravity = Gravity.CENTER_VERTICAL;
                                mTextLinearLayout2.addView(mCreditWarn, layoutParams);

                                mCreditTip = new TextView(mContext);
                                mCreditTip.setTextColor(ImageUtils.GetSkinColor(0xffe75887));
                                mCreditTip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                                mCreditTip.setText(R.string.unlock_credit_not_enough);
                                layoutParams = new LinearLayout.LayoutParams(PxToDpi_xhdpi(400), LinearLayout.LayoutParams.WRAP_CONTENT);
                                layoutParams.gravity = Gravity.CENTER_VERTICAL;
                                mTextLinearLayout2.addView(mCreditTip, layoutParams);
                            }

                            ImageView next = new ImageView(mContext);
                            next.setImageResource(R.drawable.display_choose_btn);
                            fParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                            fParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                            fParams.rightMargin = PxToDpi_xhdpi(34);
                            mCreditUnlock.addView(next, fParams);
                            ImageUtils.AddSkin(mContext, next);

                            //TextView text = new TextView(mContext);
                            //text.setTextColor(ImageUtils.GetSkinColor(0xffe75887));
                            //text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            //text.setText(R.string.unlock_use_credit);
                            //fParams = new FrameLayout.LayoutParams(PxToDpi_xhdpi(400), FrameLayout.LayoutParams.WRAP_CONTENT);
                            //fParams.gravity = Gravity.LEFT;
                            //fParams.topMargin = PxToDpi_xhdpi(70);
                            //fParams.leftMargin = iconW + PxToDpi_xhdpi(32 + 10);
                            //mCreditUnlock.addView(text, fParams);

                            //mCreditTip = new TextView(mContext);
                            //mCreditTip.setTextColor(ImageUtils.GetSkinColor(0xffe75887));
                            //mCreditTip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                            //mCreditTip.setText(R.string.unlock_credit_not_enough);
                            //fParams = new FrameLayout.LayoutParams(PxToDpi_xhdpi(400), FrameLayout.LayoutParams.WRAP_CONTENT);
                            //fParams.gravity = Gravity.LEFT | Gravity.TOP;
                            //fParams.leftMargin = iconW + PxToDpi_xhdpi(60 + 10);
                            //fParams.topMargin = PxToDpi_xhdpi(120);
                            //mCreditUnlock.addView(mCreditTip, fParams);

                            //mCreditWarn = new ImageView(mContext);
                            //mCreditWarn.setImageResource(R.drawable.display_prompt_icon);
                            //fParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                            //fParams.gravity = Gravity.LEFT | Gravity.TOP;
                            //fParams.leftMargin = iconW + PxToDpi_xhdpi(32 + 10 + 2);
                            //fParams.topMargin = PxToDpi_xhdpi(122);
                            //mCreditUnlock.addView(mCreditWarn, fParams);

                            //ImageView next = new ImageView(mContext);
                            //next.setImageResource(R.drawable.display_choose_btn);
                            //fParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                            //fParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                            //fParams.rightMargin = PxToDpi_xhdpi(34);
                            //mCreditUnlock.addView(next, fParams);
                            //ImageUtils.AddSkin(mContext, next);
                        }
                    }

//                    mCancelBtn = new ImageView(mContext);
//                    mCancelBtn.setScaleType(ScaleType.CENTER);
//                    mCancelBtn.setImageResource(R.drawable.homepage_vip_arrow_btn);
//                    mCancelBtn.setOnClickListener(m_btnLst);
//                    fParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(80));
//                    fParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
//                    mContainerLayout.addView(mCancelBtn, fParams);
                }
            }

            SetImgState(m_imgState);
            SetBtnState(m_btnState);
        }
    }

    public void SetBk(Bitmap bkBmp) {
        if (mBgView != null) {
            mBgView.setBackgroundDrawable(null);
        }
        if (mBgBmp != null) {
            mBgBmp.recycle();
            mBgBmp = null;
        }
        mBgBmp = bkBmp;
        if (mBgView != null) {
            if(mBgBmp != null && !mBgBmp.isRecycled())
            {
                mBgView.setBackgroundDrawable(new BitmapDrawable(mBgBmp));
            }
            else
            {
                mBgView.setBackgroundResource(R.drawable.login_tips_all_bk);
            }
        }
    }

    public void SetBk(int color) {
        if (mBgView != null) {
            mBgView.setBackgroundDrawable(null);
        }
        if (mBgBmp != null) {
            mBgBmp.recycle();
            mBgBmp = null;
        }
        if (mBgView != null) {
            mBgView.setBackgroundColor(color);
        }
    }

    public void setLimitRemainingImg(Object imgPath) {
        if (imgPath != null) {
            Bitmap bmp = cn.poco.imagecore.Utils.DecodeImage(mContext, imgPath, 0, -1, -1, -1);
            if (mLimitDetail != null) {
                mLimitDetail.setImageBitmap(bmp);
            }
        }
    }

    public void SetImg(Object res) {
        if (m_imgBmp != null) {
            m_imgBmp.recycle();
            m_imgBmp = null;
        }
        if (mImageView != null) {
            mImageView.setImageBitmap(null);
        }

        if (res != null) {
//            m_imgBmp = cn.poco.imagecore.Utils.DecodeImage(mContext, res, 0, -1, mImgW, mImgH);
//            if (mImageView != null) {
//                if (m_imgBmp != null && !m_imgBmp.isRecycled()) {
//                    float ratio = m_imgBmp.getHeight() * 1.0f / m_imgBmp.getWidth();
//                    float viewRatio = mImgH * 1.0f / mImgW;
//                    if (ratio != viewRatio) {
//                        int targetHeight = (int) (m_imgBmp.getWidth() * viewRatio);
//                        m_imgBmp = MakeBmp.CreateFixBitmap(m_imgBmp, m_imgBmp.getWidth(), targetHeight, MakeBmp.POS_START, 0, Config.ARGB_8888);
//                    }
//                }
//                mImageView.setImageBitmap(cn.poco.tianutils.ImageUtils.MakeRoundBmp(m_imgBmp, shapeRadius / 2));
//            }

            if (mShowImgGlideTransformation == null)
            {
                mShowImgGlideTransformation = new ShowImgGlideTransformation(mContext);
            }
            mShowImgGlideTransformation.setShapeRadius(shapeRadius);
            mShowImgGlideTransformation.setImgWH(mImgW, mImgH);
            Glide.with(mContext).load(res).asBitmap().transform(mShowImgGlideTransformation).into(mImageView);
        }
    }

    public void ResetCanCelBtnText() {
        if(mCancelBtn != null) {
            mCancelBtn.setText(R.string.cancel);
        }
    }

    public static Bitmap DrawBezier(Bitmap bmp)
    {
        Bitmap out = bmp;
        if(bmp != null)
        {
            out = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(out);
            Paint pt = new Paint();
            pt.setColor(0xFFFFFFFF);
            pt.setAntiAlias(true);
            pt.setFilterBitmap(true);
            pt.setStyle(Paint.Style.FILL);

            Path path = new Path();
            path.moveTo(0, 0);
            path.lineTo(bmp.getWidth(), 0);
            path.lineTo(bmp.getWidth(), bmp.getHeight() - PxToDpi_xhdpi(30));
            path.quadTo(bmp.getWidth() / 2, bmp.getHeight() + PxToDpi_xhdpi(30), 0, bmp.getHeight() - PxToDpi_xhdpi(30));
            path.close();
            canvas.drawPath(path, pt);

            pt.reset();
            pt.setAntiAlias(true);
            pt.setFilterBitmap(true);
            pt.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bmp, new Matrix(), pt);
        }
        return out;
    }

    public static Bitmap toRoundCorner(Bitmap bitmap, int roundPx) {
        if (bitmap == null || bitmap.isRecycled()) {
            return bitmap;
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xff424242);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        bitmap = null;

        return output;
    }

    public void SetWeixinTip(int resId)
    {
        if (m_weixinTip != null)
        {
            m_weixinTip.setText(resId);
        }
    }

    public void SetCredit(String credit) {
        FrameLayout.LayoutParams fl;
        int iconW = PxToDpi_xhdpi(110);
        m_credit = credit;
        if (!TextUtils.isEmpty(credit) && Integer.parseInt(credit) >= 60) {
            mCreditWarn.setVisibility(View.GONE);
            mCreditTip.setText(mContext.getResources().getString(R.string.unlock_user_current_credit) + credit);
            //fl = new FrameLayout.LayoutParams(PxToDpi_xhdpi(400), FrameLayout.LayoutParams.WRAP_CONTENT);
            //fl.gravity = Gravity.LEFT | Gravity.TOP;
            //fl.leftMargin = iconW + PxToDpi_xhdpi(32 + 10);
            //fl.topMargin = PxToDpi_xhdpi(120);
            //mCreditTip.setLayoutParams(fl);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(PxToDpi_xhdpi(400), LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            mCreditTip.setLayoutParams(layoutParams);
        } else {
            mCreditWarn.setVisibility(View.VISIBLE);
            mCreditTip.setText(R.string.unlock_credit_not_enough);
            //fl = new FrameLayout.LayoutParams(PxToDpi_xhdpi(400), FrameLayout.LayoutParams.WRAP_CONTENT);
            //fl.gravity = Gravity.LEFT | Gravity.TOP;
            //fl.leftMargin = iconW + PxToDpi_xhdpi(60 + 10);
            //fl.topMargin = PxToDpi_xhdpi(120);
            //mCreditTip.setLayoutParams(fl);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(PxToDpi_xhdpi(400), LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            mCreditTip.setLayoutParams(layoutParams);
        }
    }

    public void unLogin() {
        FrameLayout.LayoutParams fl;
        int iconW = PxToDpi_xhdpi(110);
        mCreditWarn.setVisibility(View.VISIBLE);
        mCreditTip.setText(R.string.unlock_user_login_tip);
        //fl = new FrameLayout.LayoutParams(PxToDpi_xhdpi(400), FrameLayout.LayoutParams.WRAP_CONTENT);
        //fl.gravity = Gravity.LEFT | Gravity.TOP;
        //fl.leftMargin = iconW + PxToDpi_xhdpi(60 + 10);
        //fl.topMargin = PxToDpi_xhdpi(120);
        //mCreditTip.setLayoutParams(fl);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(PxToDpi_xhdpi(400), LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        mCreditTip.setLayoutParams(layoutParams);
    }

    public void SetContent(String name, String content) {
        if (mTitleView != null) {
            mTitleView.setText(name);
        }
        if (mContentView != null) {
            if (content.contains("%")) {
                try {
                    content = URLDecoder.decode(content, "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mContentView.setText(content);
        }
    }

    public void SetImgState(int state) {
        switch (state) {
            case IMG_STATE_COMPLETE:
                if (mImgLoading != null) {
                    mImgLoading.setVisibility(View.GONE);
                }
                if (mImageView != null) {
                    mImageView.setVisibility(View.VISIBLE);
                }

                m_imgState = state;
                break;

            case IMG_STATE_LOADING:
                if (mImgLoading != null) {
                    mImgLoading.setVisibility(View.VISIBLE);
                }
                if (mImageView != null) {
                    mImageView.setVisibility(View.GONE);
                }

                m_imgState = state;
                break;

            default:
                break;
        }
    }

    public void SetBtnState(int state) {
        mUnlockTypeLayout.setVisibility(View.GONE);
        mResDetailLayout.setVisibility(View.VISIBLE);
        switch (state) {
            case BTN_STATE_UNLOCK: {
                if (mLoadingBtn != null) {
                    mLoadingBtn.setVisibility(View.GONE);
                }
                if (mBtn != null) {
                    mBtn.setImageResource(R.drawable.unlock_icon);
                    mBtn.setVisibility(View.VISIBLE);
                }
                if (mBtnText != null) {
                    mBtnText.setText(R.string.unlock_download_unlock);
                }

                m_btnState = state;
                break;
            }

            case BTN_STATE_LOADING: {
                if (mLoadingBtn != null) {
                    mLoadingBtn.setVisibility(View.VISIBLE);
                }
                if (mBtn != null) {
                    mBtn.setVisibility(View.GONE);
                }
                if (mBtnText != null) {
                    mBtnText.setText(R.string.unlock_downloading);
                }
                if(mCancelBtn != null) {
                    mCancelBtn.setText(R.string.ensure);
                }

                m_btnState = state;
                break;
            }

            case BTN_STATE_DOWNLOAD: {
                if (mLoadingBtn != null) {
                    mLoadingBtn.setVisibility(View.GONE);
                }
                if (mBtn != null) {
//                    mBtn.setImageResource(R.drawable.unlock_download_btn);
//                    mBtn.setVisibility(View.VISIBLE);
                    mBtn.setVisibility(View.GONE);
                }
                if (mBtnText != null) {
                    mBtnText.setText(R.string.unlock_download);
                }

                m_btnState = state;
                break;
            }
            case BTN_STATE_LIMIT_DOWNLOAD: {
                if (mLoadingBtn != null) {
                    mLoadingBtn.setVisibility(View.GONE);
                }
                if (mBtn != null) {
//                    mBtn.setImageResource(R.drawable.unlock_download_btn);
//                    mBtn.setVisibility(View.VISIBLE);
                    mBtn.setVisibility(View.GONE);
                }
                if (mBtnText != null) {
                    mBtnText.setText(R.string.unlock_download_limit);
                }
                m_btnState = state;
                break;
            }

            default:
                break;
        }
    }

    public boolean IsShow() {
        boolean out = false;

        if (mParentLayout != null && mRootLayout != null) {
            int len = mParentLayout.getChildCount();
            for (int i = 0; i < len; i++) {
                if (mParentLayout.getChildAt(i) == mRootLayout && mRootLayout.getVisibility() == View.VISIBLE) {
                    out = true;
                    break;
                }
            }
        }

        return out;
    }

    protected Toast m_toast;

    public void showToast(String msg) {
        if (m_toast == null) {
            m_toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        }
        m_toast.setText(msg);
        m_toast.show();
    }

    public void Show()
    {
        m_uiEnabled = true;
        mRootLayout.setVisibility(View.VISIBLE);
        m_animFinish = false;
        SetFr1State(true, true, new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                m_animFinish = true;
            }
        });
    }

    public void Show(FrameLayout parent) {
        if (m_animFinish && mRootLayout != null) {
            mParentLayout = parent;
            m_uiEnabled = true;

            if (mParentLayout != null && mRootLayout != null) {
                mParentLayout.removeView(mRootLayout);
                FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                mRootLayout.setLayoutParams(fl);
                mParentLayout.addView(mRootLayout);

                Show();
            }
        }
    }

    protected void SetFr1State(boolean isOpen, boolean hasAnimation, AnimationListener lst) {
        if (mContainerLayout != null) {
            mContainerLayout.clearAnimation();
            mBgView.clearAnimation();

            TranslateAnimation ta = null;
            AlphaAnimation aa = null;
            if (isOpen) {
                mContainerLayout.setVisibility(View.VISIBLE);

                if (hasAnimation) {
                    ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_PARENT, -1, Animation.RELATIVE_TO_SELF, 0);
                    aa = new AlphaAnimation(0, 1);
                }
            } else {
                mContainerLayout.setVisibility(View.GONE);

                if (hasAnimation) {
                    ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_PARENT, 1);
                    aa = new AlphaAnimation(1, 0);
                }
            }

            if (hasAnimation) {
                AnimationSet as;
                as = new AnimationSet(true);
                ta.setDuration(350);
                as.addAnimation(ta);
                as.setAnimationListener(lst);
                mContainerLayout.startAnimation(as);

                aa.setDuration(350);
                mBgView.startAnimation(aa);
            } else {
                if (lst != null) {
                    lst.onAnimationEnd(null);
                }
            }
        }
    }

    public void OnCancel(boolean hasAnim) {
        if (m_uiEnabled) {
            m_uiEnabled = false;

            SetFr1State(false, hasAnim, new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mParentLayout != null && mRootLayout != null) {
                        mRootLayout.setVisibility(View.GONE);
//                        if (mContainerLayout != null) {
//                            mContainerLayout.clearAnimation();
//                        }
//                        if (mBgView != null) {
//                            mBgView.clearAnimation();
//                        }
                    }

                    if (mCallback != null) {
                        mCallback.OnClose();
                    }
                }
            });
        }
    }

    public void AddCredit(Context context, int type, int themeID, int resId) {
        String params = "";
        if (type == ResType.FRAME.GetValue()) {
            params = Credit.APP_ID + Credit.FRAME + resId;
        } else if (type == ResType.MAKEUP_GROUP.GetValue()) {
            params = Credit.APP_ID + Credit.MAKEUP + resId;
        } else if (type == ResType.GLASS.GetValue()) {
            params = Credit.APP_ID + Credit.GLASS + resId;
        } else if (type == ResType.MOSAIC.GetValue()) {
            params = Credit.APP_ID + Credit.MOSAIC + resId;
        } else if (type == ResType.FILTER.GetValue()) {
            params = Credit.APP_ID + Credit.FILTER + resId;
        }
        Credit.CreditIncome(params, context, R.integer.积分_首次使用新素材);
        MgrUtils.AddThemeCredit(context, MgrUtils.getThemeRes(mContext, themeID));
    }

    public void ConsumeCredit(Context context, final int themeId, Credit.Callback cb) {
        String params = Credit.APP_ID + Credit.THEME + themeId;
        Credit.CreditConsume(params, context, 1049, cb);
    }

    public void ShowChooseUnlockType(boolean show) {
        if (show) {
            mResDetailLayout.setVisibility(View.GONE);
            mUnlockTypeLayout.setVisibility(View.VISIBLE);
        } else {
            mResDetailLayout.setVisibility(View.VISIBLE);
            mUnlockTypeLayout.setVisibility(View.GONE);
        }
    }

    protected View.OnClickListener m_btnLst = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == confirmBtnLayout || v == mBtn || v == mBtnText) {
                if (mCallback != null) {
                    mCallback.OnBtn(m_btnState, false);
                }
            } else if (v == mCancelBtn) {
                if (mCallback != null) {
                    mCallback.OnCloseBtn();
                }
            } else if (v == m_weixinUnlock) {
                if (mCallback != null) {
                    mCallback.OnBtn(m_btnState, true);
                }
            } else if (v == mCreditUnlock) {
                if (mCallback != null) {
                    mCallback.OnCredit(m_credit);
                }
            } else if (v == mLimitLayout) {
                if (mCallback != null) {
                    mCallback.OnCloseBtn();
                }
            }
        }
    };

    public void ClearAll() {
        SetBk(null);
        SetImg(null);
        Glide.get(mContext).clearMemory();
        if(mParentLayout != null)
        {
            mParentLayout.removeView(mRootLayout);
        }
        mShowImgGlideTransformation = null;
    }

    public static class ShowImgGlideTransformation extends BitmapTransformation
    {
        float shapeRadius;
        int mImgH; int mImgW;
        public ShowImgGlideTransformation(Context context)
        {
            super(context);
        }

        public void setShapeRadius(float shapeRadius)
        {
            this.shapeRadius = shapeRadius;
        }

        public void setImgWH(int mImgW, int mImgH)
        {
            this.mImgW = mImgW;
            this.mImgH = mImgH;
        }

        @Override
        public String getId()
        {
            return "ShowImgGlideTransformation";
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap source, int outWidth, int outHeight)
        {
            if (source == null) return null;
            Bitmap result = pool.get(source.getWidth(), source.getHeight(), Config.ARGB_8888);
            if (result == null) result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Config.ARGB_8888);

            Canvas canvas = new Canvas(result);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvas.drawBitmap(source, 0, 0, null);

            float ratio = result.getHeight() * 1.0f / result.getWidth();
            float viewRatio = mImgH * 1.0f / mImgW;
            if (ratio != viewRatio) {
                int targetHeight = (int) (result.getWidth() * viewRatio);
                result = MakeBmp.CreateFixBitmap(result, result.getWidth(), targetHeight, MakeBmp.POS_START, 0, Config.ARGB_8888);
            }

            return cn.poco.tianutils.ImageUtils.MakeRoundBmp(result, shapeRadius / 2);
        }
    }
}
