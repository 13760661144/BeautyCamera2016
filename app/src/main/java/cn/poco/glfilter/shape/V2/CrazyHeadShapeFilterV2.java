package cn.poco.glfilter.shape.V2;

import android.content.Context;
import android.graphics.PointF;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import cn.poco.glfilter.shape.ShapeInfoData;

/**
 * Created by liujx on 2018/1/24.
 * 额头大小
 */
public class CrazyHeadShapeFilterV2 extends CrazyTriShapeFilter {

    private float mHeadStrength = 0f;  //放大缩小

    public CrazyHeadShapeFilterV2(Context context) {
        super(context);
    }

    @Override
    public void setShapeData(ShapeInfoData shapeData) {
        if (shapeData == null) return;

        float faceScale = shapeData.headStrength / 100f;   //for test

        mHeadStrength = (faceScale - 0.5f) * 0.16f;

        if (mHeadStrength < 0.0f) {
            mHeadStrength *= 1.2f;
        }
    }

    @Override
    public boolean setIndexBuffer() {
        short[] index =
                {
                        0, 7, 3,
                        7, 10, 3,
                        10, 4, 3,
                        10, 9, 4,
                        9, 2, 4,
                        9, 6, 2,
                        9, 11, 6,
                        11, 5, 6,
                        11, 8, 5,
                        8, 1, 5,

                        12, 7, 0,
                        12, 13, 7,
                        13, 14, 7,
                        14, 10, 7,
                        14, 9, 10,
                        14, 11, 9,
                        14, 8, 11,
                        14, 15, 8,
                        15, 16, 8,
                        16, 1, 8
                }; //三角形各点标号索引.

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
        int idx[] = {46, 16, 0, 32, 52, 55, 58, 61, 33, 34, 35, 36, 39, 40, 41, 42, 37, 38};

        PointF idxPoint[] = new PointF[idx.length];
        for (int i = 0; i < idx.length; i++) {
            idxPoint[i] = new PointF();
            idxPoint[i].x = points[idx[i]].x * mWidth;
            idxPoint[i].y = points[idx[i]].y * mHeight;
        }

        PointF noseT = idxPoint[0];      //鼻子
        PointF chin = idxPoint[1];      //下巴
        PointF Lface = idxPoint[2];       //左边点
        PointF Rface = idxPoint[3];     //右边点

        PointF Leye_L = idxPoint[4];    //左眼左眼角
        PointF Leye_R = idxPoint[5];    //左眼右眼角
        PointF Leye_M = new PointF((Leye_L.x + Leye_R.x) / 2, (Leye_L.y + Leye_R.y) / 2);    //左眼中心

        PointF Reye_L = idxPoint[6];    //右眼左眼角
        PointF Reye_R = idxPoint[7];    //右眼右眼角
        PointF Reye_M = new PointF((Reye_L.x + Reye_R.x) / 2, (Reye_L.y + Reye_R.y) / 2);    //右眼中心

        PointF LbrowL = idxPoint[8];    //眉毛
        PointF RbrowR = idxPoint[15];    //眉毛

        float ret[] = new float[17 * 2];

        PointF MidEye = new PointF(points[43].x * mWidth, points[43].y * mHeight);
        PointF BrowCenter = new PointF((points[37].x + points[38].x) / 2 * mWidth, (points[37].y + points[38].y) / 2 * mHeight);

        float eyeDist = (float) Math.sqrt((Leye_M.x - Reye_M.x) * (Leye_M.x - Reye_M.x) + (Leye_M.y - Reye_M.y) * (Leye_M.y - Reye_M.y));
        float browEye = (float) Math.sqrt((BrowCenter.x - MidEye.x) * (BrowCenter.x - MidEye.x) + (BrowCenter.y - MidEye.y) * (BrowCenter.y - MidEye.y));

        ret[0] = LbrowL.x + (LbrowL.x - MidEye.x) * 0.2f;
        ret[1] = LbrowL.y + (LbrowL.y - MidEye.y) * 0.2f;

        ret[2] = RbrowR.x + (RbrowR.x - MidEye.x) * 0.2f;
        ret[3] = RbrowR.y + (RbrowR.y - MidEye.y) * 0.2f;

        ret[4] = BrowCenter.x + (BrowCenter.x - MidEye.x) / browEye * eyeDist * 0.15f;
        ret[5] = BrowCenter.y + (BrowCenter.y - MidEye.y) / browEye * eyeDist * 0.15f;

        PointF left1 = new PointF(ret[4] / 3.0f + ret[0] * 2.0f / 3.0f, ret[5] / 3.0f + ret[1] * 2.0f / 3.0f);
        PointF left2 = new PointF(ret[0] / 3.0f + ret[4] * 2.0f / 3.0f, ret[1] / 3.0f + ret[5] * 2.0f / 3.0f);
        ret[6] = left1.x + (left1.x - MidEye.x) * 0.15f;
        ret[7] = left1.y + (left1.y - MidEye.y) * 0.15f;

        ret[8] = left2.x + (left2.x - MidEye.x) * 0.15f;
        ret[9] = left2.y + (left2.y - MidEye.y) * 0.15f;

        PointF right1 = new PointF(ret[4] / 3.0f + ret[2] * 2.0f / 3.0f, ret[5] / 3.0f + ret[3] * 2.0f / 3.0f);
        PointF right2 = new PointF(ret[2] / 3.0f + ret[4] * 2.0f / 3.0f, ret[3] / 3.0f + ret[5] * 2.0f / 3.0f);

        ret[10] = right1.x + (right1.x - MidEye.x) * 0.15f;
        ret[11] = right1.y + (right1.y - MidEye.y) * 0.15f;

        ret[12] = right2.x + (right2.x - MidEye.x) * 0.15f;
        ret[13] = right2.y + (right2.y - MidEye.y) * 0.15f;


//        ret[14] = 1.5f * ret[6] - 0.5f * chin.x;
//        ret[15] = 1.5f * ret[7] - 0.5f * chin.y;
//        //点9
//        ret[16] = 1.5f * ret[10] - 0.5f * chin.x;
//        ret[17] = 1.5f * ret[11] - 0.5f * chin.y;
//        //点10
//        ret[18] = 1.5f * ret[4] - 0.5f * chin.x;
//        ret[19] = 1.5f * ret[5] - 0.5f * chin.y;

        float point3Dist = distanceOfPoint(new PointF(ret[6], ret[7]), chin);

        ret[14] = ret[6] + 0.85f * eyeDist * (ret[6] - chin.x) / point3Dist;
        ret[15] = ret[7] + 0.85f * eyeDist * (ret[7] - chin.y) / point3Dist;

        float point5Dist = distanceOfPoint(new PointF(ret[10], ret[11]), chin);
        //点8
        ret[16] = ret[10] + 0.85f * eyeDist * (ret[10] - chin.x) / point5Dist;
        ret[17] = ret[11] + 0.85f * eyeDist * (ret[11] - chin.y) / point5Dist;

        float point2Dist = distanceOfPoint(new PointF(ret[4], ret[5]), chin);
        //点9
        ret[18] = ret[4] + 1.2f * eyeDist * (ret[4] - chin.x) / point2Dist;
        ret[19] = ret[5] + 1.2f * eyeDist * (ret[5] - chin.y) / point2Dist;

        //点10
        ret[20] = (ret[18] + ret[14]) / 2.0f;
        ret[21] = (ret[19] + ret[15]) / 2.0f;

        //点11
        ret[22] = (ret[18] + ret[16]) / 2.0f;
        ret[23] = (ret[19] + ret[17]) / 2.0f;

        float faceDist = distanceOfPoint(Lface, Rface) * 1.5f;

        PointF brow_center = new PointF((idxPoint[16].x + idxPoint[17].x) / 2, (idxPoint[16].y + idxPoint[17].y) / 2);

        PointF p_0_7 = new PointF((ret[0] + ret[14]) / 2, (ret[1] + ret[15]) / 2);
        float p_12_offset = distanceOfPoint(p_0_7, brow_center) * 1.3f;
        PointF p_12 = new PointF((p_0_7.x - brow_center.x) / p_12_offset * faceDist + p_0_7.x,
                (p_0_7.y - brow_center.y) / p_12_offset * faceDist + p_0_7.y);

        PointF p_7_10 = new PointF((ret[14] + ret[20]) / 2, (ret[15] + ret[21]) / 2);
        float p_13_offset = distanceOfPoint(p_7_10, brow_center);
        PointF p_13 = new PointF((p_7_10.x - brow_center.x) / p_13_offset * faceDist + p_7_10.x,
                (p_7_10.y - brow_center.y) / p_13_offset * faceDist + p_7_10.y);

        float p_14_offset = distanceOfPoint(new PointF(ret[18], ret[19]), brow_center) * 1.45f;
        PointF p_14 = new PointF((ret[18] - brow_center.x) / p_14_offset * faceDist + ret[18],
                (ret[19] - brow_center.y) / p_14_offset * faceDist + ret[19]);

        PointF p_8_11 = new PointF((ret[16] + ret[22]) / 2, (ret[17] + ret[23]) / 2);
        float p_15_offset = distanceOfPoint(p_8_11, brow_center);
        PointF p_15 = new PointF((p_8_11.x - brow_center.x) / p_15_offset * faceDist + p_8_11.x,
                (p_8_11.y - brow_center.y) / p_15_offset * faceDist + p_8_11.y);

        PointF p_1_8 = new PointF((ret[2] + ret[16]) / 2, (ret[3] + ret[17]) / 2);
        float p_16_offset = distanceOfPoint(p_1_8, brow_center) * 1.3f;
        PointF p_16 = new PointF((p_1_8.x - brow_center.x) / p_16_offset * faceDist + p_1_8.x,
                (p_1_8.y - brow_center.y) / p_16_offset * faceDist + p_1_8.y);

        ret[24] = p_12.x;
        ret[25] = p_12.y;

        ret[26] = p_13.x;
        ret[27] = p_13.y;

        ret[28] = p_14.x;
        ret[29] = p_14.y;

        ret[30] = p_15.x;
        ret[31] = p_15.y;

        ret[32] = p_16.x;
        ret[33] = p_16.y;

        for (int i = 0; i < ret.length / 2; i++) {
            ret[i * 2] = ret[i * 2] / mWidth;
            ret[i * 2 + 1] = ret[i * 2 + 1] / mHeight;
        }

        return ret;
    }

    @Override
    protected float[] getOffsetPoint(PointF[] points) {

        float srcPoints[] = getSrcPoint(points);
        float ret[] = new float[srcPoints.length];
        for (int i = 0; i < srcPoints.length / 2; i++) {
            ret[2 * i] = srcPoints[2 * i] * mWidth;
            ret[2 * i + 1] = srcPoints[2 * i + 1] * mHeight;
        }

        PointF offsetSrc = new PointF(ret[4], ret[5]);
        PointF offsetDst = new PointF(ret[18], ret[19]);
        float offsetDistance = distanceOfPoint(offsetSrc, offsetDst);
        float offset_x = (offsetDst.x - offsetSrc.x);
        float offset_y = (offsetDst.y - offsetSrc.y);

        if (mHeadStrength < 0.0) {
            float offset_leftx = (ret[14] - ret[8]);
            float offset_lefty = (ret[15] - ret[9]);

            float offset_rightx = (ret[16] - ret[12]);
            float offset_righty = (ret[17] - ret[13]);

            ret[14] += offset_leftx * mHeadStrength;
            ret[15] += offset_lefty * mHeadStrength;

            ret[16] += offset_rightx * mHeadStrength;
            ret[17] += offset_righty * mHeadStrength;

            offset_leftx = (ret[20] - ret[4]);
            offset_lefty = (ret[21] - ret[5]);

            offset_rightx = (ret[22] - ret[4]);
            offset_righty = (ret[23] - ret[5]);

            ret[20] += offset_leftx * mHeadStrength * 0.98;
            ret[21] += offset_lefty * mHeadStrength * 0.98;

            ret[22] += offset_rightx * mHeadStrength * 0.98;
            ret[23] += offset_righty * mHeadStrength * 0.98;

            ret[18] += offset_x * mHeadStrength * 0.9;
            ret[19] += offset_y * mHeadStrength * 0.9;

        } else {
            for (int i = 7; i < 12; i++) {
                ret[2 * i] += offset_x * mHeadStrength;
                ret[2 * i + 1] += offset_y * mHeadStrength;
            }
        }


        for (int i = 0; i < ret.length / 2; i++) {
            ret[i * 2] = ret[i * 2] / mWidth;
            ret[i * 2 + 1] = ret[i * 2 + 1] / mHeight;
        }

        return ret;
    }

}
