package cn.poco.home.home4.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import cn.poco.tianutils.CommonUtils;
import cn.poco.utils.ImageUtil;

/**
 * Created by lgd on 2017/11/30.
 */
//http://blog.csdn.net/u012975705/article/details/48717391
public class CircleGiftView extends ImageView
{
    /**
     * gift动态效果总时长，在未设置时长时默认为1秒
     */
    private static final int DEFAULT_MOVIE_DURATION = 1000;

    /**
     * Movie实例，用来显示gift图片
     */
    private Movie mMovie;
    /**
     * 显示gift图片的动态效果的开始时间
     */
    private long mMovieStart;
    /**
     * 动态图当前显示第几帧
     */
    private int mCurrentAnimationTime = 0;
    /**
     * 是否显示动画,为true表示显示，false表示不显示
     */
    private boolean mVisible = true;
    /**
     * 动画效果是否被暂停
     */
    private volatile boolean mPaused = false;
    private Bitmap mMovieCanvasBitmap;
    private Paint mPaint;
    public CircleGiftView(Context context)
    {
        super(context);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    public boolean setImagePath(String path)
    {
        boolean isSuccess = false;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        if (opts.outMimeType != null && opts.outMimeType.length() > 0)
        {
            if (opts.outMimeType.equals("image/gif"))
            {
                CommonUtils.CancelViewGPU(this);
                try
                {
                    byte[] array = CommonUtils.ReadFile( path);
                    mMovie = Movie.decodeByteArray(array, 0, array.length);
                    setImageDrawable(null);
                    setScaleType(ScaleType.CENTER_CROP);
                    isSuccess = true;
                } catch (Throwable e)
                {
                    e.printStackTrace();
                    isSuccess = false;
                }
            } else
            {
                CommonUtils.LaunchViewGPU(this);
                opts.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
                if(bitmap != null && bitmap.getWidth() != 0 && bitmap.getHeight() != 0)
                {
                    bitmap = ImageUtil.makeCircleBmp(bitmap, 0, 0);
                    setImageBitmap(bitmap);
                    isSuccess = true;
                }
            }
        }
        return isSuccess;
    }

    @Override
    public void setImageBitmap(Bitmap bm)
    {
        mMovie = null;
        super.setImageBitmap(bm);
    }

    @Override
    public void setImageResource(int resId)
    {
        mMovie = null;
        super.setImageResource(resId);
    }

    @Override
    public void setImageURI(@Nullable Uri uri)
    {
        mMovie = null;
        super.setImageURI(uri);
    }

    /**
     * 设置暂停
     *
     * @param paused
     */
    public void setPaused(boolean paused)
    {
        this.mPaused = paused;
        if (!paused)
        {
            mMovieStart = android.os.SystemClock.uptimeMillis()
                    - mCurrentAnimationTime;
        }
        invalidate();
    }

    /**
     * 判断gif图是否停止了
     *
     * @return
     */
    public boolean isPaused()
    {
        return this.mPaused;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int min = Math.min(width, height);
        setMeasuredDimension(min, min);
    }

//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh)
//    {
//        super.onSizeChanged(w, h, oldw, oldh);
//        if (mMovie != null && w != 0 && h != 0)
//        {
//            if (mMovieCanvasBitmap == null)
//            {
//                mMovieCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//            } else
//            {
//                if (mMovieCanvasBitmap.getWidth() != w || mMovieCanvasBitmap.getHeight() != h)
//                {
//                    mMovieCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//                }
//            }
//        }
//    }

    private PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    private Canvas mGifCanvas;
    @Override
    protected void onDraw(Canvas canvas)
    {
//        int width = getWidth();
//        int height = getHeight();
        if ( getWidth() == 0 || getHeight() == 0)
        {
            return;
        }
        super.onDraw(canvas);
//        int width = getImageViewWidth();
//        int height = getImageViewHeight();
        int width = getWidth();
        int height = getHeight();
        if (mMovie != null)
        {
            int minVal = Math.min(width, height);
            if(mMovieCanvasBitmap == null){
                //防误差，bitmap设置大一点
                mMovieCanvasBitmap = Bitmap.createBitmap(minVal,minVal, Bitmap.Config.ARGB_8888);
            }
            if(mGifCanvas == null){
                mGifCanvas = new Canvas(mMovieCanvasBitmap);
            }
            mGifCanvas.save();
            long now = SystemClock.uptimeMillis();
            if (mMovieStart == 0)
            {
                // first time
                mMovieStart = (int) now;
            }
            int dur = mMovie.duration();
            if (dur == 0)
            {
                dur = DEFAULT_MOVIE_DURATION;
            }
            int relTime = (int) ((now - mMovieStart) % dur);
            mMovie.setTime(relTime);
            float scale1 = minVal / (float) mMovie.width();
            float scale2 = minVal / (float) mMovie.height();
            float scale = scale1 > scale2 ? scale1 : scale2;
            mGifCanvas.scale(scale, scale, width / 2f, height / 2f);
            mMovie.draw(mGifCanvas, (width - mMovie.width()) / 2, (height - mMovie.height()) / 2);
            mGifCanvas.restore();
            canvas.drawCircle(width/2,height/2,minVal/2,mPaint);
            mPaint.setXfermode(porterDuffXfermode);
            canvas.drawBitmap(mMovieCanvasBitmap,0,0, mPaint);
            mPaint.setXfermode(null);
//            int count = 0;
//            if(getBackground() == null)
//            {
//                canvas.save();
//            }else
//            {
//                count = canvas.saveLayer(0, 0, getWidth(), getHeight(), null);
////            }
//            canvas.translate(getPaddingLeft(),getPaddingTop());
//            canvas.drawCircle(mMovieCanvasBitmap.getWidth()/2,mMovieCanvasBitmap.getHeight()/2,Math.min(mMovieCanvasBitmap.getWidth()/2,mMovieCanvasBitmap.getHeight()/2),mPaint);
//            mPaint.setXfermode(porterDuffXfermode);
//            canvas.drawBitmap(mMovieCanvasBitmap,0,0, mPaint);
//            mPaint.setXfermode(null);
//            if(getBackground() == null)
//            {
//                canvas.restore();
//            }else
//            {
//                canvas.restoreToCount(count);
//            }
            if (!mPaused)
            {
                updateAnimationTime();
                invalidate();
            }
        }
    }

    private int getImageViewWidth()
    {
        return getWidth() -getPaddingLeft() - getPaddingRight();
    }

    private int getImageViewHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    /**
     * 更新当前显示进度
     */
    private void updateAnimationTime()
    {
        long now = android.os.SystemClock.uptimeMillis();
        // 如果第一帧，记录起始时间
        if (mMovieStart == 0)
        {
            mMovieStart = now;
        }
        // 取出动画的时长
        int dur = mMovie.duration();
        if (dur == 0)
        {
            dur = DEFAULT_MOVIE_DURATION;
        }
        // 算出需要显示第几帧
        mCurrentAnimationTime = (int) ((now - mMovieStart) % dur);
    }

}
