package cn.poco.dynamicSticker.newSticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.FloatRange;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

/**
 * Glide bitmap to round transformation
 *
 * @author lmx
 *         Created by lmx on 2017/6/27.
 */

public class CropCircleTransformation implements Transformation<Bitmap>
{
    private static final String TAG = "CropCircleTransformation";
    private BitmapPool mBitmapPool;

    private float mScale = 1.0f;

    public CropCircleTransformation(Context context)
    {
        this(Glide.get(context).getBitmapPool());
    }

    public CropCircleTransformation(BitmapPool pool)
    {
        this.mBitmapPool = pool;
    }

    public void setScale(@FloatRange(from = 0.0f, to = 1.0f) float scale)
    {
        this.mScale = scale;
    }

    @Override
    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight)
    {
        if (resource != null)
        {
            Bitmap source = resource.get();
            int size = Math.min(source.getWidth(), source.getHeight());

            int width = (source.getWidth() - size) / 2;
            int height = (source.getHeight() - size) / 2;

            Bitmap bitmap = mBitmapPool.get(size, size, Bitmap.Config.ARGB_8888);
            if (bitmap == null)
            {
                bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            if (width != 0 || height != 0)
            {
                // source isn't square, move viewport to center
                Matrix matrix = new Matrix();
                matrix.setTranslate(-width, -height);
                if (mScale != 1.0f)
                {
                    matrix.postScale(mScale, mScale);
                }
                shader.setLocalMatrix(matrix);
            }
            paint.setShader(shader);
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            return BitmapResource.obtain(bitmap, mBitmapPool);
        }
        return null;
    }

    @Override
    public String getId()
    {
        return TAG;
    }
}
