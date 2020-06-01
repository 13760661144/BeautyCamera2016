package cn.poco.campaignCenter.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;

/**
 * Created by Shine on 2016/12/30.
 */

public class CampaignBgView extends AppCompatImageView
{
    public static final int HEAD = 0;
    public static final int NORMAL = 1;

    public int mType;
    public float mRate;
    public int mPosition;
    private int mTranslationY;

    public CampaignBgView(Context context) {
        super(context);
    }

    public void setType(int type) {
        this.mType = type;
        if (mType == HEAD) {
            mRate = 1;
        } else if (mType == NORMAL) {
            mRate = 0;
        }
    }

    public void setUpImage(Drawable drawable) {
        if (drawable != null) {
            Matrix transformMatrix = new Matrix();
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();

            float scaleRateWidth = (float)((this.getWidth() * 1.0) / drawableWidth);
            float scaleRateHeight = (float)((this.getHeight() * 1.0) / drawableHeight);
            float scaleRate = Math.max(scaleRateWidth, scaleRateHeight);

            // fill bitmap to the view
            transformMatrix.postScale(scaleRate, scaleRate, 0, 0);

           // center bitmap
            if (mType != HEAD) {
                int scaleHeight = (int)(drawableHeight * scaleRate);
                int heightDiff = scaleHeight - this.getHeight();
                mTranslationY = (int)((heightDiff * 1.0) / 2);
            }
            int scaleWidth = (int)(drawableWidth * scaleRate);
            int widthDiff = scaleWidth - this.getWidth();
            int translationX = (int)((widthDiff * 1.0) / 2);

            transformMatrix.postTranslate(-translationX, -mTranslationY);
            this.setImageMatrix(transformMatrix);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        float canvasOffset = (mRate * mTranslationY);
        canvas.translate(0, canvasOffset);
        super.onDraw(canvas);
        canvas.restore();
    }




}
