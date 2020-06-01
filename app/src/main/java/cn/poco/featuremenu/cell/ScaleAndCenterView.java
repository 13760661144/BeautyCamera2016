package cn.poco.featuremenu.cell;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import static cn.poco.filterPendant.PendantViewEx.mType;

/**
 * Created by Shine on 2017/9/7.
 */

public class ScaleAndCenterView extends ImageView{

    public ScaleAndCenterView(Context context) {
        super(context);
    }

    public void properScaleImage(Drawable drawable, int dstWidth, int dstHeight) {
        if (drawable == null) {
            return;
        }

        Matrix transformMatrix = new Matrix();
        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();

        float widthScaleRate = (dstWidth * 1.0f) / drawableWidth;
        float heightScaleRate = (dstHeight * 1.0f) / drawableHeight;

        float scaleRate = Math.max(widthScaleRate, heightScaleRate);
        transformMatrix.postScale(scaleRate, scaleRate, 0, 0);

        int scaleHeight = (int)(drawableHeight * scaleRate);
        int heigtDiff = scaleHeight - dstHeight;
        int translationY = (int)((heigtDiff * 1.0f) / 2);

        int scaleWidth = (int)(drawableWidth * scaleRate);
        int widthDiff = scaleWidth - dstWidth;
        int translationX = (int)((widthDiff * 1.0f) / 2);
        transformMatrix.postTranslate(-translationX, -translationY);
        this.setImageMatrix(transformMatrix);
    }
}
