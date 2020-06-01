package cn.poco.business;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;

import cn.poco.tianutils.SimpleHorizontalListView;

/**
 * Created by lgd on 2017/11/8.
 * 裁剪最下方
 * 2018年3月5日新规范居中裁剪
 */

public class ADImageView extends View
{
    protected Bitmap bitmap;
    protected Matrix matrix = new Matrix();
    protected Paint paint = new Paint();
    public ADImageView(Context context)
    {
        super(context);
    }

//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
//    {
//        super.onLayout(changed, left, top, right, bottom);
//    }

    private void updateMatrix()
    {
        if(bitmap != null && getWidth() != 0 && getHeight() != 0){
            final float viewWidth = getWidth();
            final float viewHeight = getHeight();
            final int drawableWidth = bitmap.getWidth();
            final int drawableHeight = bitmap.getHeight();
            matrix.reset();
            final float widthScale = viewWidth / drawableWidth;
            final float heightScale = viewHeight / drawableHeight;

            float scale = Math.max(widthScale, heightScale);
            matrix.postScale(scale, scale);
//            matrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
//                    0);  //以最上方为始点，不裁剪上方区域
            matrix.postTranslate((viewWidth - drawableWidth * scale) / 2f, (viewHeight - drawableHeight * scale) / 2f);//居中裁剪

            paint.reset();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        updateMatrix();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        widthMeasureSpec = SimpleHorizontalListView.GetMyMeasureSpec(0, widthMeasureSpec);
        heightMeasureSpec = SimpleHorizontalListView.GetMyMeasureSpec(0, heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if(bitmap != null && !bitmap.isRecycled()){
            canvas.drawBitmap(bitmap,matrix,paint);
        }
    }

    public void setBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
        updateMatrix();
    }
}
