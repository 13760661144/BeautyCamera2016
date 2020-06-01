package cn.poco.home.home4.userInfoMenu.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.view.View;

/**
 * Created by Shine on 2017/8/8.
 */
public class EmptyUserAvatar2 extends View {
    private Bitmap mBitmap;

    public EmptyUserAvatar2(Context context) {
        this(context, -1);
    }

    private Paint mPaint;
    public EmptyUserAvatar2(Context context, int resId) {
        super(context);
        if (resId != -1) {
            mBitmap = BitmapFactory.decodeResource(context.getResources(), resId);
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null) {
            Shader shader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mPaint.setShader(shader);
            canvas.drawCircle(mBitmap.getWidth() / 2, mBitmap.getHeight() / 2, mBitmap.getWidth() / 2, mPaint);
        }
    }
}
