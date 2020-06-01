package cn.poco.mosaic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cn.poco.advanced.ImageUtils;
import cn.poco.image.PocoOilMask;
import cn.poco.resource.MosaicRes;
import cn.poco.tianutils.ShareData;
import cn.poco.transitions.TweenLite;

/**
 * 马赛克
 *
 * @author pocouser
 */
public class MosaicView extends View {
    public static final int MODE_MOSAIC = 0;
    public static final int MODE_DOODLE = 1;
    private int MODE = MODE_MOSAIC;
    private int mMosaicType = -1;

    private int mViewWidth, mViewHeight;

    private boolean saveCache = false;
    private MyTouchListener mTouchListener;

    private ShapeForMosaicView m_origin;
    private float mLastScaleForAll;
    private float mLastOriginX, mLastOriginY;

    private ShapeForMosaicView m_mosaic;
    private Matrix mMatrix;
    private Paint mPaint;
    private Bitmap mOrgBmp;//最原始的图片，用于橡皮擦还原
    private Bitmap mMosaicOrgBmp;//马赛克原图，用于合成
    private Bitmap mActionDownBeforeBmp;//保存down事件之前的bitmap
    private int mCircleColor = ImageUtils.GetSkinColor(0xffe75988);
    private int mCircleStroke;

    /**
     * 纹理和橡皮擦的笔触大小
     */
    private int mPaintSize;
    /**
     * 马赛克的笔触大小，范围0~100;
     */
    private int mMosaicPaintSize = 50;
    private boolean isPaintSizeChanging = false;

    private float magnifyX;
    private float magnifyY;

    private float circleX, circleY;
    private float lastX;
    private float lastY;

    private float mDownX;
    private float mDownY;

    private float mMoveX;
    private float mMoveY;


    public int def_anim_time = 400; //动画持续时间
    public int def_anim_type = TweenLite.EASING_CIRC | TweenLite.EASE_OUT; //动画类型
    protected TweenLite m_tween = new TweenLite();
    protected float anim_old_scale;
    protected float anim_old_x;
    protected float anim_old_y;
    protected float anim_ds;
    protected float anim_dx;
    protected float anim_dy;

    public MosaicView(Context context) {
        super(context);
    }

    public MosaicView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MosaicView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MosaicView(Context context, int viewWidth, int viewHeight) {
        super(context);
        mViewWidth = viewWidth;
        mViewHeight = viewHeight;

        lastX = mViewWidth / 2;
        lastY = mViewHeight / 2;
        circleX = lastX;
        circleY = lastY;
        m_origin = new ShapeForMosaicView();
        m_origin.m_w = mViewWidth;
        m_origin.m_h = mViewHeight;
        m_origin.m_centerX = m_origin.m_w / 2f;
        m_origin.m_centerY = m_origin.m_h / 2f;
        m_origin.m_x = (mViewWidth - m_origin.m_w) / 2f;
        m_origin.m_y = (mViewHeight - m_origin.m_h) / 2f;
        m_origin.m_scaleX = (float) mViewWidth / m_origin.m_w;
        m_origin.m_scaleY = m_origin.m_scaleX;
        m_origin.MAX_SCALE = 5.0f;
        m_origin.MIN_SCALE = 0.5f;
        m_origin.DEF_SCALE = 1.0f;
        mLastScaleForAll = 1.0f;

        mPaintSize = 50;
        mMatrix = new Matrix();
        mPaint = new Paint();
        m_sonWinOffset = ShareData.PxToDpi_xhdpi(10);
        m_sonWinBorder = ShareData.PxToDpi_xhdpi(5);
        m_sonWinRadius = (int) (ShareData.m_screenWidth * 0.145f);
        m_sonWinSize = m_sonWinRadius * 2;
        mCircleStroke = ShareData.PxToDpi_xhdpi(3);
    }

    PaintFlagsDrawFilter temp_filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.setDrawFilter(temp_filter);
        if (m_mosaic != null && m_mosaic.m_bmp != null) {
            mMatrix.reset();
            mMatrix.postScale(m_origin.m_scaleX * m_mosaic.m_scaleX, m_origin.m_scaleY * m_mosaic.m_scaleY, m_mosaic.m_centerX, m_mosaic.m_centerY);
            mMatrix.postTranslate(m_origin.m_x + m_mosaic.m_x, m_origin.m_y + m_mosaic.m_y);

            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setFilterBitmap(true);
            canvas.drawBitmap(m_mosaic.m_bmp, mMatrix, mPaint);
        }

        if (isPaintSizeChanging || isOddTouched) {
            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setFilterBitmap(true);
            mPaint.setColor(mCircleColor);
            mPaint.setStyle(Style.STROKE);
            mPaint.setStrokeWidth(mCircleStroke);
            canvas.drawCircle(circleX, circleY, ShareData.PxToDpi_xhdpi(mPaintSize / 2), mPaint);
        }

        //画放大镜
        if (isOddTouched) {
            drawMagnifyNew();
        }

        if (!m_tween.M1IsFinish()) {
            float s = m_tween.M1GetPos();
            m_origin.m_scaleX = anim_old_scale + anim_ds * s;
            m_origin.m_scaleY = m_origin.m_scaleX;
            m_origin.m_x = anim_old_x + anim_dx * s;
            m_origin.m_y = anim_old_y + anim_dy * s;
            this.invalidate();
        }
        canvas.restore();
    }

    public void drawMagnifyNew() {
        if (m_SonWinBmp == null) {
            m_SonWinBmp = Bitmap.createBitmap(m_sonWinSize, m_sonWinSize, Config.ARGB_8888);
        }
        Canvas m_sonWinCanvas = new Canvas(m_SonWinBmp);
        m_sonWinCanvas.setDrawFilter(temp_filter);

        //清理
        m_sonWinCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

        //draw mask
        mPaint.reset();
        mPaint.setStyle(Style.FILL);
        mPaint.setColor(0xffffffff);
        m_sonWinCanvas.drawRoundRect(new RectF(m_sonWinOffset, m_sonWinOffset, m_sonWinSize - m_sonWinOffset, m_sonWinSize - m_sonWinOffset), m_sonWinBorder << 1, m_sonWinBorder << 1, mPaint);

        if (m_mosaic.m_bmp != null) {
            mMatrix.reset();
            mMatrix.postTranslate(m_sonWinSize / 2f - GetMaskX(magnifyX), m_sonWinSize / 2f - GetMaskY(magnifyY));
            mMatrix.postScale(m_origin.m_scaleX * m_mosaic.m_scaleX * 1.1f, m_origin.m_scaleX * m_mosaic.m_scaleY * 1.1f, m_sonWinSize / 2f, m_sonWinSize / 2f);

            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setFilterBitmap(true);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            m_sonWinCanvas.drawBitmap(m_mosaic.m_bmp, mMatrix, mPaint);
        }

        //画圈
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setColor(mCircleColor);
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeWidth(mCircleStroke);
        m_sonWinCanvas.drawCircle(m_sonWinSize / 2f, m_sonWinSize / 2f, ShareData.PxToDpi_xhdpi(mPaintSize / 2) * 1.1f, mPaint);
        updateSonWin(m_SonWinBmp, m_sonWinX, m_sonWinY);
        m_sonWinCanvas.setBitmap(null);
    }

    private Bitmap m_SonWinBmp;
    private int m_sonWinRadius;
    private int m_sonWinSize;

    private int m_sonWinOffset;
    private int m_sonWinBorder;
    private int m_sonWinX = 0;
    private int m_sonWinY = 0;

    private void updateSonWin(Bitmap sonWinBmp, int sonWinX, int sonWinY) {
        if (mTouchListener != null) {
            mTouchListener.updateSonWin(sonWinBmp, sonWinX, sonWinY);
        }
    }

    protected void RefreshSonWinPos(float x, float y) {
        int size = m_sonWinRadius * 2;
        if (x < size && y < size) {
            m_sonWinX = m_origin.m_w - size;
            m_sonWinY = 0;
        } else if (x > m_origin.m_w - size && y < size) {
            m_sonWinX = 0;
            m_sonWinY = 0;
        }
    }

    boolean isOddTouched = false;
    boolean isSingleMove = false;

    float point1X = 0;
    float point1Y = 0;
    float point2X = 0;
    float point2Y = 0;
    float centerX = 0;
    float centerY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isUiEnabled) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN: {
                    singleDown(event);
                    break;
                }
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                case MotionEvent.ACTION_UP: {
                    singleUp(event);
                    releaseActionDownBmp();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (event.getPointerCount() > 1) {
                        multiMove(event);
                    } else {
                        singleMove(event);
                    }
                    break;
                }
                case MotionEvent.ACTION_POINTER_DOWN: {
                    rollBackBmp();
                    multiDown(event);
                    break;
                }
                case MotionEvent.ACTION_POINTER_UP: {
                    multiUp(event);
                    break;
                }
                default:
                    break;
            }
        }
        return true;
    }

    private boolean isUiEnabled = true;

    public void setUiEnabled(boolean enabled) {
        isUiEnabled = enabled;
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        widthMeasureSpec = SimpleHorizontalListView.GetMyMeasureSpec(0, widthMeasureSpec);
//        heightMeasureSpec = SimpleHorizontalListView.GetMyMeasureSpec(0, heightMeasureSpec);
//        int w = MeasureSpec.getSize(widthMeasureSpec);
//        int h = MeasureSpec.getSize(heightMeasureSpec);
//        setMeasuredDimension(w, h);
//
//        UpdateUI(w, h);
//    }
//
//    public void UpdateUI(int w, int h) {
//        boolean isUpdate = false;
//        if (m_origin != null && m_mosaic != null) {
//            isUpdate = true;
//            mViewWidth = w;
//            mViewHeight = h;
//            m_origin.m_w = mViewWidth;
//            m_origin.m_h = mViewHeight;
//
//            m_origin.m_centerX = m_origin.m_w / 2f;
//            m_origin.m_centerY = m_origin.m_h / 2f;
//
//            m_origin.m_x = (mViewWidth - m_origin.m_w) / 2f;
//            m_origin.m_y = (mViewHeight - m_origin.m_h) / 2f;
//
//            m_origin.m_scaleX = (float) mViewWidth / m_origin.m_w;
//            m_origin.m_scaleY = m_origin.m_scaleX;
//
//            m_mosaic.m_x = (mViewWidth - m_mosaic.m_w) / 2f;
//            m_mosaic.m_y = (mViewHeight - m_mosaic.m_h) / 2f;
//            float scale01 = 1f * mViewWidth / m_mosaic.m_w;
//            float scale02 = 1f * mViewHeight / m_mosaic.m_h;
//            m_mosaic.m_scaleX = scale01 < scale02 ? scale01 : scale02;
//            m_mosaic.m_scaleY = m_mosaic.m_scaleX;
//        }
//
//        if (isUpdate && point1X > 0 && point1Y > 0 && point2X > 0 && point2Y > 0) {
//            float scale = getScale(point1X, point1Y, point2X, point2Y);
//            m_origin.m_scaleX = mLastScaleForAll * scale;
//            if (m_origin.m_scaleX < m_origin.MIN_SCALE) {
//                m_origin.m_scaleX = m_origin.MIN_SCALE;
//                scale = m_origin.m_scaleX / mLastScaleForAll;
//            }
//            if (m_origin.m_scaleX > m_origin.MAX_SCALE) {
//                m_origin.m_scaleX = m_origin.MAX_SCALE;
//                scale = m_origin.m_scaleX / mLastScaleForAll;
//            }
//            m_origin.m_scaleY = m_origin.m_scaleX;
//            float offsetX = 0;
//            float offsetY = 0;
//            offsetX = (mLastOriginX + m_origin.m_centerX - centerX) * (scale - 1f);
//            offsetY = (mLastOriginY + m_origin.m_centerY - centerY) * (scale - 1f);
//            m_origin.m_x = mLastOriginX + (point1X + point2X) / 2f - centerX + offsetX;
//            m_origin.m_y = mLastOriginY + (point1Y + point2Y) / 2f - centerY + offsetY;
//        }
//
//        if (isUpdate) {
//            this.invalidate();
//        }
//    }

    /**
     * 单指down
     *
     * @param event
     */
    private void singleDown(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        mDownX = x;
        mDownY = y;
        circleX = x;
        circleY = y;
        magnifyX = x;
        magnifyY = y;
        lastX = GetMaskX(x);
        lastY = GetMaskY(y);
        mPath.reset();
        mPath.moveTo(lastX, lastY);
        isOddTouched = true;
        isSingleMove = false;

        if (MODE == MODE_MOSAIC) {
            PocoOilMask.initOilMaskBitmap(getContext(), mMosaicType);
        }

        if (isOddTouched) {

            releaseActionDownBmp();
            try {
                mActionDownBeforeBmp = m_mosaic.m_bmp.copy(Config.ARGB_8888, true);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            if (isRubberMode || MODE == MODE_DOODLE) {
                mPaint.reset();
                mPaint.setAntiAlias(true);
                mPaint.setFilterBitmap(true);
                mPaint.setStyle(Style.STROKE);
                mPaint.setStrokeCap(Paint.Cap.ROUND);
                mPaint.setStrokeJoin(Paint.Join.ROUND);
                mPaint.setStrokeWidth(ShareData.PxToDpi_xhdpi(mPaintSize) / (m_origin.m_scaleX * m_mosaic.m_scaleX));

                if (isRubberMode) {
                    // 擦除
                    erasePoint();
                } else {
                    // 涂鸦
                    doodlePoint();
                }
            } else {
                MosicBeautifyTools.getMosicBmp(m_mosaic.m_bmp, mMosaicOrgBmp, (int) lastX, (int) lastY, (int) (ShareData.PxToDpi_xhdpi(mMosaicPaintSize) / (m_origin.m_scaleX * m_mosaic.m_scaleX)), mMosaicType);
            }
            saveCache = true;
        }

        RefreshSonWinPos(x, y);

        if (mTouchListener != null) {
            mTouchListener.fingerDown();
        }
        this.invalidate();
    }

    /**
     * 单指up
     *
     * @param event
     */
    public void singleUp(MotionEvent event) {
        updateSonWin(null, m_sonWinX = 0, m_sonWinY = 0);
        isOddTouched = false;
        isSingleMove = false;
        circleX = mViewWidth / 2;
        circleY = mViewHeight / 2;
        this.invalidate();

        if (MODE == MODE_MOSAIC) {
            PocoOilMask.releaseOilMaskBitmap();
        }

        if (mTouchListener != null) {
            mTouchListener.fingerUp();
            if (saveCache) {
                saveCache = false;
                mTouchListener.canvasChanged(m_mosaic.m_bmp);
            }
        }
    }

    /**
     * 多指down
     *
     * @param event
     */
    public void multiDown(MotionEvent event) {
        updateSonWin(null, m_sonWinX = 0, m_sonWinY = 0);

        isOddTouched = false;
        isSingleMove = false;
        //m_tween.M1End();
        //双指初始化
        point1X = event.getX(0);
        point1Y = event.getY(0);
        point2X = event.getX(1);
        point2Y = event.getY(1);
        centerX = (point1X + point2X) / 2f;
        centerY = (point1Y + point2Y) / 2f;
        //缩放
        mLastScaleForAll = m_origin.m_scaleX;
        //移动
        mLastOriginX = m_origin.m_x;
        mLastOriginY = m_origin.m_y;
        this.invalidate();
    }

    /**
     * 多指up
     *
     * @param event
     */
    public void multiUp(MotionEvent event) {
        //actionPointerUp = true;
        isOddTouched = false;
        isSingleMove = false;
        mImgAmin();
        this.invalidate();
    }

    /**
     * 单指move
     *
     * @param event
     */
    public void singleMove(MotionEvent event) {
        if (isOddTouched) {
            float x = event.getX();
            float y = event.getY();
            float newX = GetMaskX(x);
            float newY = GetMaskY(y);
            mMoveX = x;
            mMoveY = y;

            float dx = Math.abs(newX - lastX);
            float dy = Math.abs(newY - lastY);
            if (dx >= 1 || dy >= 1) {
                isSingleMove = true;
                magnifyX = x;
                magnifyY = y;
                circleX = x;
                circleY = y;
                if (isRubberMode || MODE == MODE_DOODLE) {
                    mPath.quadTo(lastX, lastY, (newX + lastX) / 2, (newY + lastY) / 2);

                    mPaint.reset();
                    mPaint.setAntiAlias(true);
                    mPaint.setFilterBitmap(true);
                    mPaint.setStyle(Style.STROKE);
                    mPaint.setStrokeCap(Paint.Cap.ROUND);
                    mPaint.setStrokeJoin(Paint.Join.ROUND);
                    mPaint.setStrokeWidth(ShareData.PxToDpi_xhdpi(mPaintSize) / (m_origin.m_scaleX * m_mosaic.m_scaleX));
                    if (isRubberMode) {
                        // 擦除
                        erase();
                    } else {
                        // 涂鸦
                        doodle();
                    }
                } else {
                    MosicBeautifyTools.getMosicBmp(m_mosaic.m_bmp, mMosaicOrgBmp, (int) newX, (int) newY, (int) (ShareData.PxToDpi_xhdpi(mMosaicPaintSize) / (m_origin.m_scaleX * m_mosaic.m_scaleX)), mMosaicType);
                }
                lastX = newX;
                lastY = newY;
                saveCache = true;

                RefreshSonWinPos(x, y);

                this.invalidate();
            }
        }
    }

    private void releaseActionDownBmp() {
        if (mActionDownBeforeBmp != null && !mActionDownBeforeBmp.isRecycled()) {
            mActionDownBeforeBmp.recycle();
            mActionDownBeforeBmp = null;
        }
    }


    private void rollBackBmp() {
        boolean isRecycle = false;
        if (!isSingleMove) {
            isRecycle = true;
        } else {
            float spacing = cn.poco.tianutils.ImageUtils.Spacing(Math.abs((mDownX - mMoveX) / 2f), Math.abs((mDownY - mMoveY) / 2f));
            if (spacing < 50) isRecycle = true;
        }

        if (isRecycle && mActionDownBeforeBmp != null && !mActionDownBeforeBmp.isRecycled()) {
            if (m_mosaic.m_bmp != null && !m_mosaic.m_bmp.isRecycled()) {
                m_mosaic.m_bmp.recycle();
                m_mosaic.m_bmp = null;
            }
            m_mosaic.m_bmp = mActionDownBeforeBmp;
            mActionDownBeforeBmp = null;
            saveCache = false;
        }

        releaseActionDownBmp();
    }

    private void doodlePoint() {
        Canvas canvas = new Canvas(m_mosaic.m_bmp);
        canvas.setDrawFilter(temp_filter);
        mPaint.setShader(mBitmapShader);
        canvas.drawPoint(lastX, lastY, mPaint);
        canvas.setBitmap(null);
    }

    private void erasePoint() {
        Canvas canvas = new Canvas(m_mosaic.m_bmp);
        canvas.setDrawFilter(temp_filter);
        Matrix mMatrix = new Matrix();
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.drawPoint(lastX, lastY, mPaint);

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        canvas.drawBitmap(mOrgBmp, mMatrix, mPaint);

        canvas.setBitmap(null);
    }

    private void doodle() {
        Canvas canvas = new Canvas(m_mosaic.m_bmp);
        canvas.setDrawFilter(temp_filter);
        mPaint.setShader(mBitmapShader);
        canvas.drawPath(mPath, mPaint);
        canvas.setBitmap(null);
    }

    private void erase() {
        Canvas canvas = new Canvas(m_mosaic.m_bmp);
        canvas.setDrawFilter(temp_filter);
        Matrix mMatrix = new Matrix();
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.drawPath(mPath, mPaint);

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        canvas.drawBitmap(mOrgBmp, mMatrix, mPaint);
        canvas.setBitmap(null);
    }

    /**
     * 多指move
     *
     * @param event
     */
    public void multiMove(MotionEvent event) {
        isSingleMove = false;
        float scale = getScale(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
        m_origin.m_scaleX = mLastScaleForAll * scale;
        if (m_origin.m_scaleX < m_origin.MIN_SCALE) {
            m_origin.m_scaleX = m_origin.MIN_SCALE;
            scale = m_origin.m_scaleX / mLastScaleForAll;
        }
        if (m_origin.m_scaleX > m_origin.MAX_SCALE) {
            m_origin.m_scaleX = m_origin.MAX_SCALE;
            scale = m_origin.m_scaleX / mLastScaleForAll;
        }
        m_origin.m_scaleY = m_origin.m_scaleX;
        float offsetX = 0;
        float offsetY = 0;
        offsetX = (mLastOriginX + m_origin.m_centerX - centerX) * (scale - 1f);
        offsetY = (mLastOriginY + m_origin.m_centerY - centerY) * (scale - 1f);
        m_origin.m_x = mLastOriginX + (event.getX(0) + event.getX(1)) / 2f - centerX + offsetX;
        m_origin.m_y = mLastOriginY + (event.getY(0) + event.getY(1)) / 2f - centerY + offsetY;
        this.invalidate();
    }

    public void mImgAmin() {
        //回弹动画
        if (m_origin.m_scaleX < 1 || m_origin.m_scaleY < 1) {
            DoAnim(new RectF(0, 0, 1, 1), def_anim_type, def_anim_time);
        } else {
            float[] src = new float[]{m_mosaic.m_x + m_mosaic.m_centerX, m_mosaic.m_y + m_mosaic.m_centerY};
            float[] dst = new float[2];
            GetShowPos(dst, src);

            boolean doAnim = false;

            float imgW = m_origin.m_scaleX * m_mosaic.m_scaleX * m_mosaic.m_w;
            if (imgW > m_origin.m_w) {
                float min = m_origin.m_w - imgW / 2f;
                float max = imgW / 2f;
                if (dst[0] < min) {
                    dst[0] = min;

                    doAnim = true;
                } else if (dst[0] > max) {
                    dst[0] = max;

                    doAnim = true;
                }
            } else {
                dst[0] = m_origin.m_w / 2f;

                doAnim = true;
            }

            float imgH = m_origin.m_scaleY * m_mosaic.m_scaleY * m_mosaic.m_h;
            if (imgH > m_origin.m_h) {
                float min = m_origin.m_h - imgH / 2f;
                float max = imgH / 2f;
                if (dst[1] < min) {
                    dst[1] = min;

                    doAnim = true;
                } else if (dst[1] > max) {
                    dst[1] = max;

                    doAnim = true;
                }
            } else {
                dst[1] = m_origin.m_h / 2f;

                doAnim = true;
            }

            if (doAnim) {
                float l, t, r, b;
                l = dst[0] - imgW / 2f;
                if (l > 0) {
                    l = 0;
                } else {
                    l = -l / imgW;
                }
                r = dst[0] + imgW / 2f;
                if (r < m_origin.m_w) {
                    r = 1;
                } else {
                    r = (imgW - (r - m_origin.m_w)) / imgW;
                }
                t = dst[1] - imgH / 2f;
                if (t > 0) {
                    t = 0;
                } else {
                    t = -t / imgH;
                }
                b = dst[1] + imgH / 2f;
                if (b < m_origin.m_h) {
                    b = 1;
                } else {
                    b = (imgH - (b - m_origin.m_h)) / imgH;
                }

                DoAnim(new RectF(l, t, r, b), def_anim_type, def_anim_time);
            }
        }
    }

    /**
     * @param rect 图片的比例矩形
     */
    public void DoAnim(RectF rect, int animType, int animTime) {
        float l = rect.left;
        float t = rect.top;
        float r = rect.right;
        float b = rect.bottom;
        if (rect.left > rect.right) {
            l = rect.right;
            r = rect.left;
        }
        if (rect.top > rect.bottom) {
            t = rect.bottom;
            b = rect.top;
        }
        if (m_mosaic != null) {
            float rectW = (r - l) * m_mosaic.m_w * m_mosaic.m_scaleX;
            if (rectW <= 0) {
                rectW = 1;
            }
            float rectH = (b - t) * m_mosaic.m_h * m_mosaic.m_scaleY;
            if (rectH <= 0) {
                rectH = 1;
            }
            float scale = m_origin.m_w / rectW;
            {
                float scale2 = m_origin.m_h / rectH;
                if (scale2 < scale) {
                    scale = scale2;
                }
            }
            //限制最大最小缩放比例
            if (scale > m_origin.MAX_SCALE) {
                scale = m_origin.MAX_SCALE;
            } else if (scale < m_origin.MIN_SCALE) {
                scale = m_origin.MIN_SCALE;
            }

            //计算矩形框相对图片的位置
            rectW *= scale;
            rectH *= scale;
            float rectX = l * m_mosaic.m_w * m_mosaic.m_scaleX * scale;
            float rectY = t * m_mosaic.m_h * m_mosaic.m_scaleY * scale;
            //计算图片相对世界的位置
            float imgX = (m_mosaic.m_x + m_mosaic.m_centerX - m_mosaic.m_centerX * m_mosaic.m_scaleX) * scale;
            float imgY = (m_mosaic.m_y + m_mosaic.m_centerY - m_mosaic.m_centerY * m_mosaic.m_scaleY) * scale;

            float showX = (m_origin.m_w - rectW) / 2f;
            float showY = (m_origin.m_h - rectH) / 2f;

            float oX = showX - rectX - imgX;
            float oY = showY - rectY - imgY;

            anim_old_scale = m_origin.m_scaleX;
            anim_old_x = m_origin.m_x;
            anim_old_y = m_origin.m_y;

            anim_ds = scale - anim_old_scale;
            anim_dx = oX + m_origin.m_centerX * scale - m_origin.m_centerX - anim_old_x;
            anim_dy = oY + m_origin.m_centerY * scale - m_origin.m_centerY - anim_old_y;
            m_tween.Init(0f, 1f, animTime);
            m_tween.M1Start(animType);
        }
    }

    private float getScale(float x1, float y1, float x2, float y2) {
        float oldSpace = (float) Math.sqrt((point1X - point2X) * (point1X - point2X) + (point1Y - point2Y) * (point1Y - point2Y));
        float newSpace = (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        return newSpace / oldSpace;
    }

    /**
     * 逻辑坐标转换为真实的显示坐标
     *
     * @param dst 长度为2的倍数
     * @param src 长度为2的倍数
     */
    protected void GetShowPos(float[] dst, float[] src) {
        int len = src.length / 2 * 2;
        for (int i = 0; i < len; i += 2) {
            dst[i] = (src[i] - m_origin.m_centerX) * m_origin.m_scaleX + m_origin.m_x + m_origin.m_centerX;
            dst[i + 1] = (src[i + 1] - m_origin.m_centerY) * m_origin.m_scaleY + m_origin.m_y + m_origin.m_centerY;
        }
    }

    /**
     * 显示坐标转换为逻辑坐标
     *
     * @param dst
     * @param src
     */
    protected void GetLogicPos(float[] dst, float[] src) {
        int len = src.length / 2 * 2;
        for (int i = 0; i < len; i += 2) {
            dst[i] = (src[i] - m_origin.m_x - m_origin.m_centerX) / m_origin.m_scaleX + m_origin.m_centerX;
            dst[i + 1] = (src[i + 1] - m_origin.m_y - m_origin.m_centerY) / m_origin.m_scaleY + m_origin.m_centerY;
        }
    }

    protected float GetMaskX(float x) {
        return (x - m_origin.m_x - m_mosaic.m_x - m_mosaic.m_centerX) / (m_origin.m_scaleX * m_mosaic.m_scaleX) + m_mosaic.m_centerX;
    }

    protected float GetMaskY(float y) {
        return (y - m_origin.m_y - m_mosaic.m_y - m_mosaic.m_centerY) / (m_origin.m_scaleY * m_mosaic.m_scaleY) + m_mosaic.m_centerY;
    }

    public void setImageViewBitmap(Bitmap bmp) {
        if (bmp != null) {
            mOrgBmp = bmp;

            m_mosaic = new ShapeForMosaicView();
            m_mosaic.m_bmp = mOrgBmp.copy(Config.ARGB_8888, true);
            m_mosaic.m_w = m_mosaic.m_bmp.getWidth();
            m_mosaic.m_h = m_mosaic.m_bmp.getHeight();
            m_mosaic.m_centerX = m_mosaic.m_w / 2f;
            m_mosaic.m_centerY = m_mosaic.m_h / 2f;
            m_mosaic.m_x = (mViewWidth - m_mosaic.m_w) / 2f;
            m_mosaic.m_y = (mViewHeight - m_mosaic.m_h) / 2f;
            float scale01 = 1f * mViewWidth / m_mosaic.m_w;
            float scale02 = 1f * mViewHeight / m_mosaic.m_h;
            m_mosaic.m_scaleX = scale01 < scale02 ? scale01 : scale02;
            m_mosaic.m_scaleY = m_mosaic.m_scaleX;
            invalidate();

            mPath = new Path();

            mShaderMatrix = new Matrix();
            mMatrix.reset();
            mMatrix.postScale(m_origin.m_scaleX * m_mosaic.m_scaleX, m_origin.m_scaleY * m_mosaic.m_scaleY, m_mosaic.m_centerX, m_mosaic.m_centerY);
            mMatrix.postTranslate(m_origin.m_x + m_mosaic.m_x, m_origin.m_y + m_mosaic.m_y);
            mMatrix.invert(mShaderMatrix);
        }
    }

    public void updateImageBitmap(Bitmap bmp) {
        if (bmp != null) {
            if (m_mosaic.m_bmp != null) {
                m_mosaic.m_bmp.recycle();
                m_mosaic.m_bmp = null;
            }
            m_mosaic.m_bmp = bmp;
            invalidate();
        }
    }

    /**
     * 设置马赛克类型
     *
     * @param type 例如{@link PocoOilMask#Vangogh}
     */
    public void setMosaicType(int type) {
        if (MODE != MODE_MOSAIC || isRubberMode) {
            if (mMosaicOrgBmp != null) {
                mMosaicOrgBmp.recycle();
                mMosaicOrgBmp = null;
            }
            mMosaicOrgBmp = m_mosaic.m_bmp.copy(Config.ARGB_8888, true);
        }

        MODE = MODE_MOSAIC;
        mMosaicType = type;
    }

    public void updateMosaicOrgBmp() {
        if (mMosaicOrgBmp != null) {
            mMosaicOrgBmp.recycle();
            mMosaicOrgBmp = null;
            mMosaicOrgBmp = m_mosaic.m_bmp.copy(Config.ARGB_8888, true);
        }
    }

    private Bitmap mDoodleBmp;
    private Path mPath;
    private Shader mBitmapShader;
    private Matrix mShaderMatrix;

    /**
     * 设置涂鸦效果
     *
     * @param doodleBmp
     * @param doodleRes
     */
    public void setDoodleType(Bitmap doodleBmp, MosaicRes doodleRes) {
        MODE = MODE_DOODLE;
        if (mDoodleBmp != null && !mDoodleBmp.isRecycled()) {
            mDoodleBmp.recycle();
            mDoodleBmp = null;
        }
        mDoodleBmp = doodleBmp;

        mBitmapShader = new BitmapShader(doodleBmp, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        if (doodleRes.m_paintType != MosaicRes.PAINT_TYPE_TILE) {
            Matrix tempMatrix = new Matrix();
            int maskWidth = mOrgBmp.getWidth();
            int maskHeight = mOrgBmp.getHeight();
            int paintWidth = mDoodleBmp.getWidth();
            int paintHeight = mDoodleBmp.getHeight();
            boolean horizontalOrVertical = maskWidth > maskHeight;
            float scale = horizontalOrVertical ? 1f * maskWidth / paintWidth : 1f * maskHeight / paintHeight;
            int type = horizontalOrVertical ? doodleRes.horizontal_fill : doodleRes.vertical_fill;
            switch (type) {
                case MosaicRes.POS_START: {
                    if (horizontalOrVertical) {
                        tempMatrix.postTranslate((maskWidth - paintWidth) / 2, 0);
                        tempMatrix.postScale(scale, scale, maskWidth / 2, 0);
                    } else {
                        tempMatrix.postTranslate(0, (maskHeight - paintHeight) / 2);
                        tempMatrix.postScale(scale, scale, 0, maskHeight / 2);
                    }
                    break;
                }

                case MosaicRes.POS_CENTER: {
                    tempMatrix.postTranslate((maskWidth - paintWidth) / 2, (maskHeight - paintHeight) / 2);
                    tempMatrix.postScale(scale, scale, maskWidth / 2, maskHeight / 2);
                    break;
                }

                case MosaicRes.POS_END: {
                    if (horizontalOrVertical) {
                        tempMatrix.postTranslate((maskWidth - paintWidth) / 2, maskHeight - paintHeight);
                        tempMatrix.postScale(scale, scale, maskWidth / 2, maskHeight);
                    } else {
                        tempMatrix.postTranslate(maskWidth - paintWidth, (maskHeight - paintHeight) / 2);
                        tempMatrix.postScale(scale, scale, maskWidth, maskHeight / 2);
                    }
                    break;
                }
                default:
                    break;
            }
            mBitmapShader.setLocalMatrix(tempMatrix);
        } else if (mShaderMatrix != null) {
            mBitmapShader.setLocalMatrix(mShaderMatrix);
        }
    }

    /**
     * 设置纹理和橡皮擦笔触大小
     *
     * @param size
     */
    public void setPaintSize(int size) {
        mPaintSize = size;
    }

    /**
     * 设置马赛克笔触大小
     *
     * @param size
     */
    public void setMosaicPaintSize(int size) {
        mMosaicPaintSize = size;
    }

    public void changingPaintSize(boolean isChanging) {
        isPaintSizeChanging = isChanging;
        this.invalidate();
    }

    public void changingPaintSize(boolean isChanging, boolean invalidate) {
        isPaintSizeChanging = isChanging;
        if (invalidate) {
            this.invalidate();
        }
    }

    private boolean isRubberMode;

    /**
     * 设置橡皮擦模式
     *
     * @param rubberMode
     */
    public void setRubberMode(boolean rubberMode) {
        isRubberMode = rubberMode;
    }

    public void setMyTouchListener(MyTouchListener listener) {
        mTouchListener = listener;
    }

    public Bitmap getOutBmp() {
        return m_mosaic.m_bmp.copy(Config.ARGB_8888, true);
    }

    /**
     * 会清理当初显示的Bitmap,只适用于保存
     *
     * @return
     */
    public Bitmap getOutSaveBmp() {
        Bitmap out = m_mosaic.m_bmp;
        m_mosaic.m_bmp = null;
        return out;
    }

    public void releaseMem() {
        if (mOrgBmp != null && !mOrgBmp.isRecycled()) {
            mOrgBmp.recycle();
            mOrgBmp = null;
        }
        releaseMemMosaic();
    }

    public void releaseMemMosaic() {
        if (m_mosaic != null && m_mosaic.m_bmp != null && !m_mosaic.m_bmp.isRecycled()) {
            m_mosaic.m_bmp.recycle();
            m_mosaic = null;
        }
        if (mDoodleBmp != null && !mDoodleBmp.isRecycled()) {
            mDoodleBmp.recycle();
            mDoodleBmp = null;
        }
        if (mMosaicOrgBmp != null && !mMosaicOrgBmp.isRecycled()) {
            mMosaicOrgBmp.recycle();
            mMosaicOrgBmp = null;
        }

        if (m_SonWinBmp != null && !m_SonWinBmp.isRecycled()) {
            m_SonWinBmp.recycle();
            m_SonWinBmp = null;
        }

        releaseActionDownBmp();
    }

    public interface MyTouchListener {

        void updateSonWin(Bitmap bmp, int x, int y);

        void canvasChanged(Bitmap bmp);

        void fingerDown();

        void fingerUp();
    }

    public class ShapeForMosaicView {
        public float m_x = 0;
        public float m_y = 0;

        public int m_w;
        public int m_h;
        public float m_centerX;
        public float m_centerY;
        public float m_scaleX = 1f;
        public float m_scaleY = 1f;

        public float MAX_SCALE = 2f;
        public float DEF_SCALE = 1f;
        public float MIN_SCALE = 0.5f;

        public Bitmap m_bmp = null;
    }

}
