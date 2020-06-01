package cn.poco.glfilter.shape.V2;

import android.content.Context;
import android.graphics.PointF;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import cn.poco.glfilter.shape.ShapeInfoData;
import cn.poco.image.Sslandmarks;

/**
 * Created by liujx on 2018/1/24.
 * 此类实现眼距 和 眼角效果
 */
public class CrazyEyeShapeFilter extends CrazyTriShapeFilter {

    private float mEyeAngleStrength = 0;
    private float mEyeDistStrength = 0;

    private PointF offsetLeft = new PointF();
    private PointF offsetRight = new PointF();

    public CrazyEyeShapeFilter(Context context) {
        super(context);
    }

    @Override
    public void setShapeData(ShapeInfoData shapeData) {
        if (shapeData == null) return;

        float faceScale = (100 - shapeData.eyeAngleStrength) / 100f;
        mEyeAngleStrength = (faceScale - 0.5f) * 2.0f;

        faceScale = shapeData.eyeDistStrength / 100f;
        mEyeDistStrength = (faceScale - 0.5f) * 2.0f;
    }

    @Override
    public boolean setIndexBuffer() {
        short[] idx1 = {
                8, 9, 0, 9, 10, 0, 10, 0, 1, 10, 11, 1, 11, 12, 1, 12, 2, 1, 12, 13, 2, 13, 3, 2, 13, 14, 3, 14, 4, 3, 14, 15, 4, 15, 16, 4, 16, 17, 4,
                17, 18, 4, 18, 4, 5, 18, 19, 5, 19, 5, 6, 19, 20, 6, 20, 21, 6, 21, 6, 7, 21, 22, 7, 22, 0, 7, 22, 23, 0, 23, 8, 0,
                0, 1, 7, 1, 6, 7, 1, 2, 6, 2, 5, 6, 2, 3, 5, 3, 4, 5
        };

        short[] index = new short[idx1.length * 2];  //左右眼

        for (int i = 0; i < index.length; i++) {
            if (i < idx1.length)
                index[i] = idx1[i];
            else
                index[i] = (short) (idx1[i - idx1.length] + 24);
        }

        if (drawIndex == null) {

            mIndexLength = index.length;  //
            drawIndex = ByteBuffer.allocateDirect(index.length * 2)
                    .order(ByteOrder.nativeOrder());
            drawIndex.asShortBuffer().put(index);
        }

        drawIndex.position(0);
        return true;
    }

    @Override
    protected float[] getSrcPoint(PointF[] points) {
        PointF realPoint[] = new PointF[points.length];

        for (int i = 0; i < points.length; i++) {
            realPoint[i] = new PointF();
            realPoint[i].x = (int) (points[i].x * mWidth);
            realPoint[i].y = (int) (points[i].y * mHeight);
        }

        double angle = Math.atan2(realPoint[55].y - realPoint[58].y, realPoint[55].x - realPoint[58].x);

        double diseyel = Math.sqrt((realPoint[55].y - realPoint[52].y) * (realPoint[55].y - realPoint[52].y) +
                (realPoint[55].x - realPoint[52].x) * (realPoint[55].x - realPoint[52].x));

        double distleft = Math.sqrt((realPoint[55].x - realPoint[78].x) * (realPoint[55].x - realPoint[78].x) +
                (realPoint[55].y - realPoint[78].y) * (realPoint[55].y - realPoint[78].y));  //计算左右偏移距离

        double distright = Math.sqrt((realPoint[58].x - realPoint[79].x) * (realPoint[58].x - realPoint[79].x) +
                (realPoint[58].y - realPoint[79].y) * (realPoint[58].y - realPoint[79].y));  //计算左右偏移距离

        double distlu = diseyel / 3.f;  //计算下偏移距离

        //扩大区域   计算中心位置，由中心点向外扩张
        PointF leyeCenter = new PointF((realPoint[52].x + realPoint[72].x + realPoint[55].x + realPoint[73].x) / 4,
                (realPoint[52].y + realPoint[72].y + realPoint[55].y + realPoint[73].y) / 4);

        realPoint[52].x = realPoint[52].x - (leyeCenter.x - realPoint[52].x) * 0.7f;
        realPoint[52].y = realPoint[52].y - (leyeCenter.y - realPoint[52].y) * 0.7f;

        realPoint[55].x = realPoint[55].x - (leyeCenter.x - realPoint[55].x) * 0.3f;
        realPoint[55].y = realPoint[55].y - (leyeCenter.y - realPoint[55].y) * 0.3f;

        realPoint[53].x = realPoint[53].x - (leyeCenter.x - realPoint[53].x) * 0.7f;
        realPoint[53].y = realPoint[53].y - (leyeCenter.y - realPoint[53].y) * 0.7f;

        realPoint[72].x = realPoint[72].x - (leyeCenter.x - realPoint[72].x) * 0.7f;
        realPoint[72].y = realPoint[72].y - (leyeCenter.y - realPoint[72].y) * 0.7f;

        realPoint[54].x = realPoint[54].x - (leyeCenter.x - realPoint[54].x) * 0.7f;
        realPoint[54].y = realPoint[54].y - (leyeCenter.y - realPoint[54].y) * 0.7f;

        realPoint[57].x = realPoint[57].x - (leyeCenter.x - realPoint[57].x) * 0.7f;
        realPoint[57].y = realPoint[57].y - (leyeCenter.y - realPoint[57].y) * 0.7f;

        realPoint[73].x = realPoint[73].x - (leyeCenter.x - realPoint[73].x) * 0.7f;
        realPoint[73].y = realPoint[73].y - (leyeCenter.y - realPoint[73].y) * 0.7f;

        realPoint[56].x = realPoint[56].x - (leyeCenter.x - realPoint[56].x) * 0.7f;
        realPoint[56].y = realPoint[56].y - (leyeCenter.y - realPoint[56].y) * 0.7f;

        PointF reyeCenter = new PointF((realPoint[58].x + realPoint[75].x + realPoint[61].x + realPoint[76].x) / 4,
                (realPoint[58].y + realPoint[75].y + realPoint[61].y + realPoint[76].y) / 4);

        realPoint[58].x = realPoint[58].x - (reyeCenter.x - realPoint[58].x) * 0.3f;
        realPoint[58].y = realPoint[58].y - (reyeCenter.y - realPoint[58].y) * 0.3f;

        realPoint[59].x = realPoint[59].x - (reyeCenter.x - realPoint[59].x) * 0.7f;
        realPoint[59].y = realPoint[59].y - (reyeCenter.y - realPoint[59].y) * 0.7f;

        realPoint[75].x = realPoint[75].x - (reyeCenter.x - realPoint[75].x) * 0.7f;
        realPoint[75].y = realPoint[75].y - (reyeCenter.y - realPoint[75].y) * 0.7f;

        realPoint[60].x = realPoint[60].x - (reyeCenter.x - realPoint[60].x) * 0.7f;
        realPoint[60].y = realPoint[60].y - (reyeCenter.y - realPoint[60].y) * 0.7f;

        realPoint[61].x = realPoint[61].x - (reyeCenter.x - realPoint[61].x) * 0.7f;
        realPoint[61].y = realPoint[61].y - (reyeCenter.y - realPoint[61].y) * 0.7f;

        realPoint[63].x = realPoint[63].x - (reyeCenter.x - realPoint[63].x) * 0.7f;
        realPoint[63].y = realPoint[63].y - (reyeCenter.y - realPoint[63].y) * 0.7f;

        realPoint[76].x = realPoint[76].x - (reyeCenter.x - realPoint[76].x) * 0.7f;
        realPoint[76].y = realPoint[76].y - (reyeCenter.y - realPoint[76].y) * 0.7f;

        realPoint[62].x = realPoint[62].x - (reyeCenter.x - realPoint[62].x) * 0.7f;
        realPoint[62].y = realPoint[62].y - (reyeCenter.y - realPoint[62].y) * 0.7f;

//        distleft *= 1.1;
//        distright *= 1.1;

        //==========左眼================
        int idx[] = {52, 53, 72, 54, 55, 56, 73, 57};
        float ret[] = new float[24 * 2 * 2];
        for (int i = 0; i < idx.length; i++) {
            ret[2 * i] = realPoint[idx[i]].x;
            ret[2 * i + 1] = realPoint[idx[i]].y;
        }
        ret[2 * 8] = realPoint[52].x + (float) (distleft * (realPoint[52].x - realPoint[53].x) / Math.sqrt((realPoint[52].x - realPoint[53].x) * (realPoint[52].x - realPoint[53].x) +
                (realPoint[52].y - realPoint[53].y) * (realPoint[52].y - realPoint[53].y)));
        ret[2 * 8 + 1] = realPoint[52].y + (float) (distleft * (realPoint[52].y - realPoint[53].y) / Math.sqrt((realPoint[52].x - realPoint[53].x) * (realPoint[52].x - realPoint[53].x) +
                (realPoint[52].y - realPoint[53].y) * (realPoint[52].y - realPoint[53].y)));

        ret[2 * 9] = realPoint[52].x + (float) (distleft * (realPoint[52].x - realPoint[57].x) / Math.sqrt((realPoint[52].x - realPoint[57].x) * (realPoint[52].x - realPoint[57].x) +
                (realPoint[52].y - realPoint[57].y) * (realPoint[52].y - realPoint[57].y)));
        ret[2 * 9 + 1] = realPoint[52].y + (float) (distleft * (realPoint[52].y - realPoint[57].y) / Math.sqrt((realPoint[52].x - realPoint[57].x) * (realPoint[52].x - realPoint[57].x) +
                (realPoint[52].y - realPoint[57].y) * (realPoint[52].y - realPoint[57].y)));

        ret[2 * 10] = (realPoint[52].x + realPoint[64].x) * 0.5f;
        ret[2 * 10 + 1] = (realPoint[52].y * 0.35f + realPoint[64].y * 0.65f);

        ret[2 * 9 + 1] = (ret[2 * 9 + 1] + ret[2 * 10 + 1]) / 2;

        ret[2 * 11] = (realPoint[53].x + realPoint[64].x) * 0.5f;
        ret[2 * 11 + 1] = (realPoint[53].y * 0.35f + realPoint[64].y * 0.65f);

        ret[2 * 12] = (realPoint[53].x * 0.2f + realPoint[65].x * 0.8f);
        ret[2 * 12 + 1] = (realPoint[53].y * 0.2f + realPoint[65].y * 0.8f);

        ret[2 * 13] = (realPoint[72].x * 0.2f + realPoint[66].x * 0.8f);
        ret[2 * 13 + 1] = (realPoint[72].y * 0.2f + realPoint[66].y * 0.8f);

        ret[2 * 14] = (realPoint[54].x * 0.4f + realPoint[67].x * 0.6f);
        ret[2 * 14 + 1] = (realPoint[54].y * 0.4f + realPoint[67].y * 0.6f);

        ret[2 * 15] = (realPoint[55].x * 0.35f + realPoint[67].x * 0.65f);
        ret[2 * 15 + 1] = (realPoint[55].y * 0.35f + realPoint[67].y * 0.65f);

        ret[2 * 16] = realPoint[55].x + (float) (distleft * (realPoint[55].x - realPoint[56].x) / Math.sqrt((realPoint[55].x - realPoint[56].x) * (realPoint[55].x - realPoint[56].x) +
                (realPoint[55].y - realPoint[56].y) * (realPoint[55].y - realPoint[56].y)));
        ret[2 * 16 + 1] = realPoint[55].y + (float) (distleft * (realPoint[55].y - realPoint[56].y) / Math.sqrt((realPoint[55].x - realPoint[56].x) * (realPoint[55].x - realPoint[56].x) +
                (realPoint[55].y - realPoint[56].y) * (realPoint[55].y - realPoint[56].y)));

        ret[2 * 16 + 1] = (ret[2 * 15 + 1] + ret[2 * 16 + 1]) / 2;

        ret[2 * 17] = realPoint[55].x + (float) (distleft * (realPoint[55].x - realPoint[54].x) / Math.sqrt((realPoint[55].x - realPoint[54].x) * (realPoint[55].x - realPoint[54].x) +
                (realPoint[55].y - realPoint[54].y) * (realPoint[55].y - realPoint[54].y)));
        ret[2 * 17 + 1] = realPoint[55].y + (float) (distleft * (realPoint[55].y - realPoint[54].y) / Math.sqrt((realPoint[55].x - realPoint[54].x) * (realPoint[55].x - realPoint[54].x) +
                (realPoint[55].y - realPoint[54].y) * (realPoint[55].y - realPoint[54].y)));

        PointF p5556 = new PointF((realPoint[55].x + realPoint[56].x) / 2, (realPoint[55].y + realPoint[56].y) / 2);

        ret[2 * 18] = p5556.x + (float) (distleft * (p5556.x - realPoint[72].x) / Math.sqrt((p5556.x - realPoint[72].x) * (p5556.x - realPoint[72].x) +
                (p5556.y - realPoint[72].y) * (p5556.y - realPoint[72].y)));
        ret[2 * 18 + 1] = p5556.y + (float) (distleft * (p5556.y - realPoint[72].y) / Math.sqrt((p5556.x - realPoint[72].x) * (p5556.x - realPoint[72].x) +
                (p5556.y - realPoint[72].y) * (p5556.y - realPoint[72].y)));

        ret[2 * 19] = realPoint[56].x + (float) (distleft * (realPoint[56].x - realPoint[72].x) / Math.sqrt((realPoint[56].x - realPoint[72].x) * (realPoint[56].x - realPoint[72].x) +
                (realPoint[56].y - realPoint[72].y) * (realPoint[56].y - realPoint[72].y)));
        ret[2 * 19 + 1] = realPoint[56].y + (float) (distleft * (realPoint[56].y - realPoint[72].y) / Math.sqrt((realPoint[56].x - realPoint[72].x) * (realPoint[56].x - realPoint[72].x) +
                (realPoint[56].y - realPoint[72].y) * (realPoint[56].y - realPoint[72].y)));


        PointF p5673 = new PointF((realPoint[73].x + realPoint[56].x) / 2, (realPoint[73].y + realPoint[56].y) / 2);
        ret[2 * 20] = p5673.x + (float) (distleft * (p5673.x - realPoint[72].x) / Math.sqrt((p5673.x - realPoint[72].x) * (p5673.x - realPoint[72].x) +
                (p5673.y - realPoint[72].y) * (p5673.y - realPoint[72].y)));
        ret[2 * 20 + 1] = p5673.y + (float) (distleft * (p5673.y - realPoint[72].y) / Math.sqrt((p5673.x - realPoint[72].x) * (p5673.x - realPoint[72].x) +
                (p5673.y - realPoint[72].y) * (p5673.y - realPoint[72].y)));

        PointF p5773 = new PointF((realPoint[73].x + realPoint[57].x) / 2, (realPoint[73].y + realPoint[57].y) / 2);
        ret[2 * 21] = p5773.x + (float) (distleft * (p5773.x - realPoint[72].x) / Math.sqrt((p5773.x - realPoint[72].x) * (p5773.x - realPoint[72].x) +
                (p5773.y - realPoint[72].y) * (p5773.y - realPoint[72].y)));
        ret[2 * 21 + 1] = p5773.y + (float) (distleft * (p5773.y - realPoint[72].y) / Math.sqrt((p5773.x - realPoint[72].x) * (p5773.x - realPoint[72].x) +
                (p5773.y - realPoint[72].y) * (p5773.y - realPoint[72].y)));

        ret[2 * 22] = realPoint[57].x + (float) (distleft * (realPoint[57].x - realPoint[72].x) / Math.sqrt((realPoint[57].x - realPoint[72].x) * (realPoint[57].x - realPoint[72].x) +
                (realPoint[57].y - realPoint[72].y) * (realPoint[57].y - realPoint[72].y)));
        ret[2 * 22 + 1] = realPoint[57].y + (float) (distleft * (realPoint[57].y - realPoint[72].y) / Math.sqrt((realPoint[57].x - realPoint[72].x) * (realPoint[57].x - realPoint[72].x) +
                (realPoint[57].y - realPoint[72].y) * (realPoint[57].y - realPoint[72].y)));

        PointF p5257 = new PointF((realPoint[52].x + realPoint[57].x) / 2, (realPoint[52].y + realPoint[57].y) / 2);

        ret[2 * 23] = p5257.x + (float) (distleft * (p5257.x - realPoint[72].x) / Math.sqrt((p5257.x - realPoint[72].x) * (p5257.x - realPoint[72].x) +
                (p5257.y - realPoint[72].y) * (p5257.y - realPoint[72].y)));
        ret[2 * 23 + 1] = p5257.y + (float) (distleft * (p5257.y - realPoint[72].y) / Math.sqrt((p5257.x - realPoint[72].x) * (p5257.x - realPoint[72].x) +
                (p5257.y - realPoint[72].y) * (p5257.y - realPoint[72].y)));

        //=================右眼====
        int idx_right[] = {58, 59, 75, 60, 61, 62, 76, 63};
        for (int i = 0; i < idx.length; i++) {
            ret[2 * (24 + i)] = realPoint[idx_right[i]].x;
            ret[2 * (24 + i) + 1] = realPoint[idx_right[i]].y;
        }

        ret[2 * 32] = realPoint[58].x + (float) (distright * (realPoint[58].x - realPoint[59].x) / Math.sqrt((realPoint[58].x - realPoint[59].x) * (realPoint[58].x - realPoint[59].x) +
                (realPoint[58].y - realPoint[59].y) * (realPoint[58].y - realPoint[59].y)));
        ret[2 * 32 + 1] = realPoint[58].y + (float) (distright * (realPoint[58].y - realPoint[59].y) / Math.sqrt((realPoint[58].x - realPoint[59].x) * (realPoint[58].x - realPoint[59].x) +
                (realPoint[58].y - realPoint[59].y) * (realPoint[58].y - realPoint[59].y)));

        ret[2 * 33] = realPoint[58].x + (float) (distright * (realPoint[58].x - realPoint[63].x) / Math.sqrt((realPoint[58].x - realPoint[63].x) * (realPoint[58].x - realPoint[63].x) +
                (realPoint[58].y - realPoint[63].y) * (realPoint[58].y - realPoint[63].y)));
        ret[2 * 33 + 1] = realPoint[58].y + (float) (distright * (realPoint[58].y - realPoint[63].y) / Math.sqrt((realPoint[58].x - realPoint[63].x) * (realPoint[58].x - realPoint[63].x) +
                (realPoint[58].y - realPoint[63].y) * (realPoint[58].y - realPoint[63].y)));

        ret[2 * 34] = (realPoint[58].x * 0.35f + realPoint[68].x * 0.65f);
        ret[2 * 34 + 1] = (realPoint[58].y * 0.35f + realPoint[68].y * 0.65f);

        ret[2 * 33 + 1] = (ret[2 * 33 + 1] + ret[2 * 34 + 1]) / 2;

        ret[2 * 35] = (realPoint[59].x + realPoint[68].x) * 0.5f;
        ret[2 * 35 + 1] = (realPoint[59].y + realPoint[68].y) * 0.5f;

        ret[2 * 36] = (realPoint[59].x * 0.2f + realPoint[69].x * 0.8f);
        ret[2 * 36 + 1] = (realPoint[59].y * 0.2f + realPoint[69].y * 0.8f);

        ret[2 * 37] = (realPoint[75].x * 0.2f + realPoint[70].x * 0.8f);
        ret[2 * 37 + 1] = (realPoint[75].y * 0.2f + realPoint[70].y * 0.8f);

        ret[2 * 38] = (realPoint[60].x * 0.35f + realPoint[71].x * 0.65f);
        ret[2 * 38 + 1] = (realPoint[60].y * 0.35f + realPoint[71].y * 0.65f);

        ret[2 * 39] = (realPoint[61].x * 0.35f + realPoint[71].x * 0.65f);
        ret[2 * 39 + 1] = (realPoint[61].y * 0.35f + realPoint[71].y * 0.65f);

        ret[2 * 40] = realPoint[61].x + (float) (distright * (realPoint[61].x - realPoint[62].x) / Math.sqrt((realPoint[61].x - realPoint[62].x) * (realPoint[61].x - realPoint[62].x) +
                (realPoint[61].y - realPoint[62].y) * (realPoint[61].y - realPoint[62].y)));
        ret[2 * 40 + 1] = realPoint[61].y + (float) (distright * (realPoint[61].y - realPoint[62].y) / Math.sqrt((realPoint[61].x - realPoint[62].x) * (realPoint[61].x - realPoint[62].x) +
                (realPoint[61].y - realPoint[62].y) * (realPoint[61].y - realPoint[62].y)));

        ret[2 * 40 + 1] = (ret[2 * 40 + 1] + ret[2 * 39 + 1]) / 2;

        ret[2 * 41] = realPoint[61].x + (float) (distright * (realPoint[61].x - realPoint[60].x) / Math.sqrt((realPoint[61].x - realPoint[60].x) * (realPoint[61].x - realPoint[60].x) +
                (realPoint[61].y - realPoint[60].y) * (realPoint[61].y - realPoint[60].y)));
        ret[2 * 41 + 1] = realPoint[61].y + (float) (distright * (realPoint[61].y - realPoint[60].y) / Math.sqrt((realPoint[61].x - realPoint[60].x) * (realPoint[61].x - realPoint[60].x) +
                (realPoint[61].y - realPoint[60].y) * (realPoint[61].y - realPoint[60].y)));

        PointF p6162 = new PointF((realPoint[61].x + realPoint[62].x) / 2, (realPoint[61].y + realPoint[62].y) / 2);

        ret[2 * 42] = p6162.x + (float) (distright * (p6162.x - realPoint[75].x) / Math.sqrt((p6162.x - realPoint[75].x) * (p6162.x - realPoint[75].x) +
                (p6162.y - realPoint[75].y) * (p6162.y - realPoint[75].y)));
        ret[2 * 42 + 1] = p6162.y + (float) (distright * (p6162.y - realPoint[75].y) / Math.sqrt((p6162.x - realPoint[75].x) * (p6162.x - realPoint[75].x) +
                (p6162.y - realPoint[75].y) * (p6162.y - realPoint[75].y)));

        ret[2 * 43] = realPoint[62].x + (float) (distright * (realPoint[62].x - realPoint[75].x) / Math.sqrt((realPoint[62].x - realPoint[75].x) * (realPoint[62].x - realPoint[75].x) +
                (realPoint[62].y - realPoint[75].y) * (realPoint[62].y - realPoint[75].y)));
        ret[2 * 43 + 1] = realPoint[62].y + (float) (distright * (realPoint[62].y - realPoint[75].y) / Math.sqrt((realPoint[62].x - realPoint[75].x) * (realPoint[62].x - realPoint[75].x) +
                (realPoint[62].y - realPoint[75].y) * (realPoint[62].y - realPoint[75].y)));


        PointF p6276 = new PointF((realPoint[76].x + realPoint[62].x) / 2, (realPoint[76].y + realPoint[62].y) / 2);
        ret[2 * 44] = p6276.x + (float) (distright * (p6276.x - realPoint[75].x) / Math.sqrt((p6276.x - realPoint[75].x) * (p6276.x - realPoint[75].x) +
                (p6276.y - realPoint[75].y) * (p6276.y - realPoint[75].y)));
        ret[2 * 44 + 1] = p6276.y + (float) (distright * (p6276.y - realPoint[75].y) / Math.sqrt((p6276.x - realPoint[75].x) * (p6276.x - realPoint[75].x) +
                (p6276.y - realPoint[75].y) * (p6276.y - realPoint[75].y)));

        PointF p6376 = new PointF((realPoint[76].x + realPoint[63].x) / 2, (realPoint[76].y + realPoint[63].y) / 2);
        ret[2 * 45] = p6376.x + (float) (distright * (p6376.x - realPoint[75].x) / Math.sqrt((p6376.x - realPoint[75].x) * (p6376.x - realPoint[75].x) +
                (p6376.y - realPoint[75].y) * (p6376.y - realPoint[75].y)));
        ret[2 * 45 + 1] = p6376.y + (float) (distright * (p6376.y - realPoint[75].y) / Math.sqrt((p6376.x - realPoint[75].x) * (p6376.x - realPoint[75].x) +
                (p6376.y - realPoint[75].y) * (p6376.y - realPoint[75].y)));

        ret[2 * 46] = realPoint[63].x + (float) (distright * (realPoint[63].x - realPoint[75].x) / Math.sqrt((realPoint[63].x - realPoint[75].x) * (realPoint[63].x - realPoint[75].x) +
                (realPoint[63].y - realPoint[75].y) * (realPoint[63].y - realPoint[75].y)));
        ret[2 * 46 + 1] = realPoint[63].y + (float) (distright * (realPoint[63].y - realPoint[75].y) / Math.sqrt((realPoint[63].x - realPoint[75].x) * (realPoint[63].x - realPoint[75].x) +
                (realPoint[63].y - realPoint[75].y) * (realPoint[63].y - realPoint[75].y)));

        PointF p5863 = new PointF((realPoint[58].x + realPoint[63].x) / 2, (realPoint[58].y + realPoint[63].y) / 2);

        ret[2 * 47] = p5863.x + (float) (distright * (p5863.x - realPoint[75].x) / Math.sqrt((p5863.x - realPoint[75].x) * (p5863.x - realPoint[75].x) +
                (p5863.y - realPoint[75].y) * (p5863.y - realPoint[75].y)));
        ret[2 * 47 + 1] = p5863.y + (float) (distright * (p5863.y - realPoint[75].y) / Math.sqrt((p5863.x - realPoint[75].x) * (p5863.x - realPoint[75].x) +
                (p5863.y - realPoint[75].y) * (p5863.y - realPoint[75].y)));

        for (int i = 0; i < ret.length / 2; i++) {
            ret[2 * i] /= mWidth;
            ret[2 * i + 1] /= mHeight;
        }

        offsetLeft.x = (float) (0.75 * distleft / Math.sqrt(mWidth * mWidth + mHeight * mHeight) * Math.cos(angle));
        offsetLeft.y = (float) (0.75 * distleft / Math.sqrt(mWidth * mWidth + mHeight * mHeight) * Math.sin(angle));

        offsetRight.x = (float) (0.75 * distright / Math.sqrt(mWidth * mWidth + mHeight * mHeight) * Math.cos(angle));
        offsetRight.y = (float) (0.75 * distright / Math.sqrt(mWidth * mWidth + mHeight * mHeight) * Math.sin(angle));

        return ret;
    }

    @Override
    protected float[] getOffsetPoint(PointF[] points) {
        float ret[] = getSrcPoint(points);

        PointF LefteyeCenter = new PointF((faceData[Sslandmarks.lEyeTop].x + faceData[Sslandmarks.lEyeBot].x +
                faceData[Sslandmarks.lEyeOuter].x + faceData[Sslandmarks.lEyeInner].x) * mWidth / 4.f,
                (faceData[Sslandmarks.lEyeTop].y + faceData[Sslandmarks.lEyeBot].y +
                        faceData[Sslandmarks.lEyeOuter].y + faceData[Sslandmarks.lEyeInner].y) * mHeight / 4.f);

        float a = (float) (Math.PI * 0.04 * mEyeAngleStrength);
        for (int i = 0; i < 7; i++) {
            float x = ret[2 * i] * mWidth;
            float y = ret[2 * i + 1] * mHeight;

            float x0 = (float) ((x - LefteyeCenter.x) * Math.cos(a) - (y - LefteyeCenter.y) * Math.sin(a) + LefteyeCenter.x);
            float y0 = (float) ((x - LefteyeCenter.x) * Math.sin(a) + (y - LefteyeCenter.y) * Math.cos(a) + LefteyeCenter.y);

            //眼角(角度)
            ret[2 * i] = x0 / mWidth;
            ret[2 * i + 1] = y0 / mHeight;
        }


        PointF RightEyeCenter = new PointF((faceData[Sslandmarks.rEyeTop].x + faceData[Sslandmarks.rEyeBot].x +
                faceData[Sslandmarks.rEyeOuter].x + faceData[Sslandmarks.rEyeInner].x) * mWidth / 4.f,
                (faceData[Sslandmarks.rEyeTop].y + faceData[Sslandmarks.rEyeBot].y +
                        faceData[Sslandmarks.rEyeOuter].y + faceData[Sslandmarks.rEyeInner].y) * mHeight / 4.f);

        for (int i = 24; i < 32; i++) {
            float x = ret[2 * i] * mWidth;
            float y = ret[2 * i + 1] * mHeight;

            float x0 = (float) ((x - RightEyeCenter.x) * Math.cos(-a) - (y - RightEyeCenter.y) * Math.sin(-a) + RightEyeCenter.x);
            float y0 = (float) ((x - RightEyeCenter.x) * Math.sin(-a) + (y - RightEyeCenter.y) * Math.cos(-a) + RightEyeCenter.y);

            ret[2 * i] = x0 / mWidth;
            ret[2 * i + 1] = y0 / mHeight;
        }


        float x_offset = (offsetLeft.x * mEyeDistStrength * 1.2f);
        float y_offset = (offsetLeft.y * mEyeDistStrength * 1.2f);
        for (int i = 0; i <= 7; i++) {
            ret[2 * i] += x_offset * 0.5f;
            ret[2 * i + 1] += y_offset * 0.5f;
        }

        x_offset = (offsetRight.x * mEyeDistStrength * 1.2f);
        y_offset = (offsetRight.y * mEyeDistStrength * 1.2f);
        for (int i = 24; i <= 31; i++) {
            ret[2 * i] -= x_offset * 0.5f;
            ret[2 * i + 1] -= y_offset * 0.5f;
        }

        return ret;
    }

}
