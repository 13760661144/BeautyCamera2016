package cn.poco.camera3.util;

import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

/**
 * @author Gxx
 *         Created by Gxx on 2017/9/5.
 */

public class BKUtils
{
    /**
     * 圆角边框
     *
     * @param tl    左上
     * @param tr    右上
     * @param br    右下
     * @param bl    左下
     * @param color 颜色
     */
    public static ShapeDrawable getShapeDrawable(boolean tl, boolean tr, boolean br, boolean bl, float radius, int color)
    {
        float[] outerRadii = new float[8];
        if (tl)
        {
            outerRadii[0] = radius;
            outerRadii[1] = radius;
        }
        if (tr)
        {
            outerRadii[2] = radius;
            outerRadii[3] = radius;
        }
        if (br)
        {
            outerRadii[4] = radius;
            outerRadii[5] = radius;
        }
        if (bl)
        {
            outerRadii[6] = radius;
            outerRadii[7] = radius;
        }
        RoundRectShape round = new RoundRectShape(outerRadii, null, null);
        ShapeDrawable shape = new ShapeDrawable(round);
        shape.getPaint().setColor(color);
        shape.getPaint().setAntiAlias(true);
        shape.getPaint().setDither(true);
        shape.getPaint().setStyle(Paint.Style.FILL);
        return shape;
    }

    /**
     * 四个角都是圆角
     */
    public static ShapeDrawable getShapeDrawable(float radius, int color)
    {
        return getShapeDrawable(true, true, true, true, radius, color);
    }
}
