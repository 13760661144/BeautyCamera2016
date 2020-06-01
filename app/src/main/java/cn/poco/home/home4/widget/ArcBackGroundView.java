package cn.poco.home.home4.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.view.View;

import cn.poco.advanced.ImageUtils;
import cn.poco.home.home4.utils.PercentUtil;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.ShareData;

//主页 渐变背景和 商业图片皮肤背景
public class ArcBackGroundView extends View {
    private static final String TAG = "AnimationView";
    public final static int DURATION = 600;
    public final static int ARC_TOP_MARGIN = PercentUtil.HeightPxToPercent(147);
    public final static int ARC_HEIGHT = PercentUtil.HeightPxToPercent(78);      //圆弧大小 //222-144
    public final static int ARC_MAX_HEIGHT = PercentUtil.HeightPxToPercent(80); //超过高度
    private int topColor;
    private int bottomColor;
    private int type;

    private Path mPath;
    private Paint mBackGroundPaint;
    private Paint tempPaint;
    private int arcHeight;
    private int arcTopMargin;

    private Bitmap mAdBitmap;
    private Paint mAdPaint;
    private int mAdAlpha;
    private Rect mBpRect;

    public ArcBackGroundView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        topColor = ImageUtils.GetSkinColor1(0xffbf699f);
        bottomColor = ImageUtils.GetSkinColor2(0xffbeb2a3);
        type = SysConfig.s_skinColorType;
        arcHeight = ARC_HEIGHT;
        arcTopMargin = ARC_TOP_MARGIN;

        mBackGroundPaint = new Paint();
        mBackGroundPaint.setStyle(Paint.Style.FILL);
        mBackGroundPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mBackGroundPaint.setAntiAlias(true);

        mPath = new Path();
            /*
			 * LinearGradient shader = new LinearGradient(0, 0, endX, endY, new
             * int[]{startColor, midleColor, endColor},new float[]{0 , 0.5f,
             * 1.0f}, TileMode.MIRROR);
             * 参数一为渐变起初点坐标x位置，参数二为y轴位置，参数三和四分辨对应渐变终点
             * 其中参数new int[]{startColor, midleColor,endColor}是参与渐变效果的颜色集合，
             * 其中参数new float[]{0 , 0.5f, 1.0f}是定义每个颜色处于的渐变相对位置， 这个参数可以为null，如果为null表示所有的颜色按顺序均匀的分布
             */
        // Shader.TileMode三种模式
        // REPEAT:沿着渐变方向循环重复
        // CLAMP:如果在预先定义的范围外画的话，就重复边界的颜色
        // MIRROR:与REPEAT一样都是循环重复，但这个会对称重复

        mAdAlpha = 255;
        mAdPaint = new Paint();
        mAdPaint.setAntiAlias(true);

        mBpRect = new Rect(0, 0, ShareData.m_screenWidth, ShareData.m_screenHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBackGroundPaint.setShader(initShader());
        mBpRect.left = 0;
        mBpRect.right = w;
        mBpRect.top = arcHeight - ARC_HEIGHT;
        mBpRect.bottom = h + +arcHeight - ARC_HEIGHT;
    }


    private PaintFlagsDrawFilter paintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    @Override
    protected void onDraw(Canvas canvas) {
//		canvas.setDrawFilter(paintFlagsDrawFilter);
        canvas.save();
        super.onDraw(canvas);
        drawDrag(canvas);
        canvas.restore();
    }

    private void drawDrag(Canvas canvas) {
        mPath.reset();
        mPath.moveTo(0, arcTopMargin);
        mPath.lineTo(0, getHeight());
        mPath.lineTo(getWidth(), getHeight());
        mPath.lineTo(getWidth(), arcTopMargin);
        mPath.quadTo(getWidth() / 2, arcTopMargin + arcHeight * 2, 0, arcTopMargin);
//		mPath.lineTo(0,arcTopMargin);

        canvas.setDrawFilter(null);
        canvas.drawPath(mPath, mBackGroundPaint);
        //切换主题
        if (tempPaint != null) {
            canvas.drawPath(mPath, tempPaint);
        }

        if (mAdBitmap != null) {
            mAdPaint.setAlpha(mAdAlpha);
            canvas.setDrawFilter(paintFlagsDrawFilter);
            canvas.clipPath(mPath);
//			canvas.drawBitmap(mAdBitmap, mAdMatrix, mAdPaint);
            canvas.drawBitmap(mAdBitmap, null, mBpRect, mAdPaint);
        }

    }

    public Shader initShader() {
        LinearGradient shader;
        if (type == 0) {
            shader = new LinearGradient(0, 0, getWidth(), getHeight(), new int[]{topColor, bottomColor}, null, Shader.TileMode.CLAMP);

        } else if (type == 2) {
            shader = new LinearGradient(getWidth(), 0, 0, getHeight(), new int[]{topColor, bottomColor}, null, Shader.TileMode.CLAMP);
        } else {
            shader = new LinearGradient(getWidth() / 2, 0, getWidth() / 2, getHeight(), new int[]{topColor, bottomColor}, null, Shader.TileMode.CLAMP);
        }
        return shader;
    }

    public void setSkinTheme(boolean isHasAmn, final Callback callback) {
        topColor = ImageUtils.GetSkinColor1(topColor);
        bottomColor = ImageUtils.GetSkinColor2(bottomColor);
        type = SysConfig.s_skinColorType;
        if (isHasAmn) {
            tempPaint = new Paint();
            tempPaint.setAlpha(0);
            tempPaint.setShader(initShader());
            ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 255);
            valueAnimator.setDuration(DURATION);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int alpha = (int) animation.getAnimatedValue();
                    tempPaint.setAlpha(alpha);
                    postInvalidate();
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mBackGroundPaint = tempPaint;
                    tempPaint = null;
                    if (callback != null) {
                        callback.onEnd();
                    }
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    if (callback != null) {
                        callback.onStart();
                    }
                }
            });
            valueAnimator.start();
        } else {
            mBackGroundPaint.setShader(initShader());
            invalidate();
        }
    }

    public void setSkinTheme(final Callback callback) {
        setSkinTheme(true, callback);
    }

    public void setArcHeight(int arcHeight) {
        if (arcHeight >= 0) {
            mBpRect.top = arcHeight;
            mBpRect.bottom = ShareData.m_screenHeight + arcHeight;
        } else {
            mBpRect.top = 0;
            mBpRect.bottom = ShareData.m_screenHeight;

        }
        this.arcHeight = ARC_HEIGHT + arcHeight;
        invalidate();
    }


    public int getArcHeight() {
        return arcHeight;
    }

    public interface Callback {
        void onStart();

        void onEnd();
    }

    public void setSkinBitmap(Bitmap mAmnBitmap) {
        this.mAdBitmap = mAmnBitmap;
        invalidate();
    }

    public int getAdAlpha() {
        return mAdAlpha;
    }

    public void setAdAlpha(int amnAlpha) {
        this.mAdAlpha = amnAlpha;
        invalidate();
    }
}
