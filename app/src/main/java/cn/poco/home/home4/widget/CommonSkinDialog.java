package cn.poco.home.home4.widget;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.adnonstop.admasterlibs.data.AbsAdRes;
import com.adnonstop.admasterlibs.data.ActAnimationInfo;
import com.adnonstop.admasterlibs.data.IAdSkin;

import java.util.ArrayList;

import cn.poco.home.home4.introAnimation.Config;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.FullScreenDlg;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

import static cn.poco.home.home4.utils.PercentUtil.TransFormIosRadiusPx;

/**
 * Created by lgd on 2017/9/6.
 */
//通用皮肤商业
public class CommonSkinDialog extends FullScreenDlg
{

    protected String mSkinCover;//换皮肤后替换的icon
    protected String url_skinCover;
    protected String mDlgImg;//对话框图片
    protected String url_dlgImg;
    protected float[] mBtnPos;//cancel和ok按钮的坐标大小.cancel:x,y,w,h ok:x,y,w,h
    protected String mBg;//皮肤底纹图
    protected String url_bg;
    private int curIndex = 0;
    private ImageView mCircleView;
    private ArrayList<ActAnimationInfo.ActAnimationFrame> animLists = new ArrayList<>();
    private View mNoBtn;
    private View mYesBtn;
    private int mDlgImgW;
    private int mDlgImgH;

    public CommonSkinDialog(Activity activity, AbsAdRes adRes)
    {
        super(activity, R.style.homeDialog);
        //动画
        if (adRes instanceof IAdSkin)
        {
            IAdSkin res = (IAdSkin) adRes;
            mSkinCover = res.getSkinCover();
            url_skinCover = res.getSkinCoverUrl();
            mDlgImg = res.getDlgImg();
            url_dlgImg = res.getDlgImgUrl();
            mBtnPos = res.getBtnPos();
            mBg = res.getBg();
            url_bg = res.getBgUrl();
            animLists = res.getAnim();
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mDlgImg, options);
        mDlgImgW = TransFormIosRadiusPx(options.outWidth);
        mDlgImgH = TransFormIosRadiusPx(options.outHeight);
        float temp[] = mBtnPos;
        mBtnPos = new float[temp.length];
        for (int i = 0; i < mBtnPos.length; i++)
        {
//            mBtnPos[i] = TransformIosHeightPx((int) temp[i]);
            mBtnPos[i] = TransFormIosRadiusPx((int) temp[i]);
        }
        initUi();
    }

    private void initUi()
    {
        int bottomMargin = Config.AD_CENTER_BOTTOM_MARGIN;

        mCircleView = new ImageView(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mCircleView.setImageBitmap(decodeBitmap(mDlgImg));
        params.gravity = Gravity.CENTER;
        params.bottomMargin = bottomMargin;
        m_fr.addView(mCircleView, params);

        if (mBtnPos.length >= 8)
        {
            //cancel和ok按钮的坐标大小.cancel:x,y,w,h ok:x,y,w,h
            mNoBtn = new View(getContext());
            if (SysConfig.IsDebug())
            {
                mNoBtn.setBackgroundColor(0x2f00ff00);
            }
            mNoBtn.setOnClickListener(mClickListener);
            params = new FrameLayout.LayoutParams(((int) mBtnPos[2]), ((int) mBtnPos[3]));
            params.gravity = Gravity.CENTER_VERTICAL;
            params.leftMargin = ShareData.m_screenWidth / 2 - mDlgImgW / 2 + (int) mBtnPos[0];
//            params.topMargin = ShareData.m_screenHeight / 2 - bottomMargin - mDlgImgH / 2 + (int) mBtnPos[1];
            params.topMargin = (int) (- bottomMargin - mDlgImgH / 2 + (int) mBtnPos[1] +mBtnPos[3]/2);
            m_fr.addView(mNoBtn, params);

//            params.gravity = Gravity.CENTER;
//            params.bottomMargin = bottomMargin;
//            mNoBtn.setTranslationX(-mDlgImgW / 2+mBtnPos[2]/2+(int) mBtnPos[0]);
//            mNoBtn.setTranslationY(-mDlgImgH /2 +mBtnPos[3]/2+ mBtnPos[1]);

            mYesBtn = new View(getContext());
            if (SysConfig.IsDebug())
            {
                mYesBtn.setBackgroundColor(0x2fff0000);
            }
            mYesBtn.setOnClickListener(mClickListener);
            params = new FrameLayout.LayoutParams(((int) mBtnPos[6]), ((int) mBtnPos[7]));
            params.gravity = Gravity.CENTER_VERTICAL;
            params.leftMargin = ShareData.m_screenWidth / 2 - mDlgImgW / 2 + (int) mBtnPos[4];
//            params.topMargin = ShareData.m_screenHeight / 2 - bottomMargin - mDlgImgH / 2 + (int) mBtnPos[5];
            params.topMargin = (int) (- bottomMargin - mDlgImgH / 2 + (int) mBtnPos[5]+mBtnPos[7]/2);
            m_fr.addView(mYesBtn, params);
//            params.gravity = Gravity.CENTER;
//            params.bottomMargin = bottomMargin;
//            mYesBtn.setTranslationX(-mDlgImgW / 2+mBtnPos[6]/2+(int) mBtnPos[4]);
//            mYesBtn.setTranslationY(-mDlgImgH /2 +mBtnPos[7]/2+ mBtnPos[5]);
        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (v == mNoBtn)
            {
                if (callBack != null)
                {
                    callBack.onNo();
                }
            } else if (v == mYesBtn)
            {
                if (callBack != null)
                {
                    mNoBtn.setVisibility(View.GONE);
                    mYesBtn.setVisibility(View.GONE);
                    callBack.onYes();
                    startAmnAnimation();
                }
            }
        }
    };

    private void startAmnAnimation()
    {
        setCancelable(false);
        curIndex = 0;
        if (curIndex < animLists.size())
        {
            mCircleView.setImageBitmap(decodeBitmap((String) animLists.get(curIndex).img));
            mCircleView.postDelayed(mRunnable, animLists.get(curIndex).time);
        } else
        {
            if (callBack != null)
            {
                callBack.onAnimationEnd(null, null);
            }
        }
    }

    private Runnable mRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            curIndex++;
            if (curIndex < animLists.size())
            {
                if (mCircleView != null)
                {
                    mCircleView.setImageBitmap(decodeBitmap((String) animLists.get(curIndex).img));
                }
                mCircleView.postDelayed(mRunnable, animLists.get(curIndex).time);
            } else
            {
                setCancelable(true);
                if (callBack != null)
                {
                    callBack.onAnimationEnd(mBg, mSkinCover);
                }
            }
        }
    };

    protected Bitmap decodeBitmap(String path)
    {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int reqWidth = TransFormIosRadiusPx(options.outWidth);
        int reqHeight = TransFormIosRadiusPx(options.outHeight);
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(path, options);
        if(bitmap != null)
        {
            bitmap = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, false);
        }
        return bitmap;
    }

    protected CallBack callBack;

    public void setCallBack(CallBack callBack)
    {
        this.callBack = callBack;
    }

    public interface CallBack
    {
        void onNo();

        void onYes();

        void onAnimationEnd(String skinBg, String skinCover);
    }
}
