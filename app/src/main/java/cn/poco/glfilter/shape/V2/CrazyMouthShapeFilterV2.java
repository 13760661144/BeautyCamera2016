package cn.poco.glfilter.shape.V2;

import android.content.Context;
import android.graphics.PointF;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import cn.poco.glfilter.shape.ShapeInfoData;
import cn.poco.image.Sslandmarks;

/**
 * 嘴巴变形V2版
 * 实现嘴部大小以及上下移动效果
 */
public class CrazyMouthShapeFilterV2 extends CrazyTriShapeFilter {

    private float mMouthStrength = 0f;  //放大缩小
    private float mMouthHighStrength = 0f;   //高低

    public CrazyMouthShapeFilterV2(Context context) {
        super(context);
    }

    @Override
    public void setShapeData(ShapeInfoData shapeData) {
        if (shapeData == null) return;

        this.mMouthStrength = shapeData.mouthStrength;
        this.mMouthHighStrength = shapeData.mouthHighStrength;  //for test

        mMouthStrength = mMouthStrength < 0 ? 0 : (mMouthStrength > 100 ? 100 : mMouthStrength);
        mMouthStrength = ((mMouthStrength - 50) * 0.25f) / 100.0f;

        mMouthHighStrength = mMouthHighStrength < 0 ? 0 : (mMouthHighStrength > 100 ? 100 : mMouthHighStrength);
        mMouthHighStrength = (50f - mMouthHighStrength) / 50;
    }

    @Override
    public boolean setIndexBuffer() {
        short[] index =
                {
                        0, 38, 8, 0, 1, 8, 1, 8, 9, 1, 2, 9, 2, 3, 9, 3, 9, 10, 3, 10, 11, 3, 11, 4, 4, 11, 12, 4, 12, 5, 5, 12, 6, 6, 12, 39,
                        12, 14, 13, 11, 14, 12, 11, 15, 14, 11, 16, 15, 10, 16, 11, 9, 16, 10, 9, 17, 16, 9, 18, 17, 8, 18, 9, 7, 18, 8,
                        38, 7, 8, 38, 19, 7, 19, 20, 7, 20, 21, 7, 21, 22, 7, 22, 23, 7, 23, 24, 7, 24, 18, 7, 24, 25, 18, 25, 26, 18, 26, 27, 18,
                        18, 27, 17, 17, 27, 16, 27, 28, 16, 28, 29, 16, 29, 30, 16, 16, 30, 15, 15, 30, 31, 15, 31, 14,
                        31, 32, 14, 32, 33, 14, 33, 34, 14, 34, 35, 14, 35, 13, 14, 35, 36, 13, 36, 37, 13, 37, 39, 13, 39, 12, 13
                };

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
        int idx[] = {82, 47, 48, 49, 50, 51, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25};
        float ret[] = new float[(idx.length + 2) * 2];

        for (int i = 0; i < idx.length; i++) {
            ret[2 * i] = points[idx[i]].x;
            ret[2 * i + 1] = points[idx[i]].y;
        }

        ret[idx.length * 2] = (points[82].x + points[7].x) / 2;
        ret[idx.length * 2 + 1] = (points[82].y + points[7].y) / 2;
        ret[idx.length * 2 + 2] = (points[83].x + points[25].x) / 2;
        ret[idx.length * 2 + 3] = (points[83].y + points[25].y) / 2;

        return ret;
    }

    @Override
    protected float[] getOffsetPoint(PointF[] points) {
        float ret[] = getSrcPoint(points);

        float []ori = new float[ret.length];
        for(int i = 0; i < ret.length; i++)
        {
            ori[i] = ret[i];
        }

        PointF realPoint[] = new PointF[ret.length / 2];
        for (int i = 0; i < realPoint.length; i++) {
            realPoint[i] = new PointF();
            realPoint[i].x = (int) (ret[2 * i] * mWidth);
            realPoint[i].y = (int) (ret[2 * i + 1] * mHeight);
        }

        PointF mouthCenter = new PointF(mWidth * (points[Sslandmarks.BotOfTopLip].x + points[Sslandmarks.TopOfBotLip].x) / 2,
                mHeight * (points[Sslandmarks.BotOfTopLip].y + points[Sslandmarks.TopOfBotLip].y) / 2);

        float scale = 1.25f * mMouthStrength;  //限制缩小区域, scale the zone.

        for (int i = 7; i <= 18; i++) {
            realPoint[i].x = (realPoint[i].x * (1 - scale) + mouthCenter.x * scale);
            realPoint[i].y = (realPoint[i].y * (1 - scale) + mouthCenter.y * scale);
            //计算角度
            double destDegree = Math.atan2((points[Sslandmarks.rMouthCorner].y - points[Sslandmarks.lMouthCorner].y), (points[Sslandmarks.rMouthCorner].x - points[Sslandmarks.lMouthCorner].x));

            //往上最大偏移量
            double up_limit = 0.3 * distanceOfPoint(realPoint[2], realPoint[10]);
            //往下最大偏移量
            double down_limit = 0.1 * distanceOfPoint(realPoint[16], realPoint[28]);

            //根据往上还是往下，取相应的偏移   movestep范围-1.0 - 1.0
            double offset = mMouthHighStrength > 0 ? up_limit : down_limit;

            //计算嘴部坐标偏移
            realPoint[i].x += (float) (offset * mMouthHighStrength * Math.cos(destDegree - Math.PI / 2));
            realPoint[i].y += (float) (offset * mMouthHighStrength * Math.sin(destDegree - Math.PI / 2));
        }

        for (int i = 0; i < realPoint.length; i++) {
            ret[2 * i] = realPoint[i].x / mWidth;
            ret[2 * i + 1] = realPoint[i].y / mHeight;
        }

        float rectify_strength = calculateRectifyMouthStrength(ret, mWidth, mHeight);

        if(rectify_strength <= 0.0f)
        {
            for(int i = 0; i < ret.length; i++)
            {
                ret[i] = ori[i];
            }
        }

        return ret;
    }

    //越界判断
    private float calculateRectifyMouthStrength(float []ioffset_coordinate, int width, int height)
    {
        float area_outside_strength = 1.0f;//用于保护越界的情况下，图像断层问题

        ArrayList<PointF> test_array = new ArrayList<PointF>();

        PointF i_ps[] = new PointF[40];
        for(int i = 0; i < 40; i++){
            i_ps[i] = new PointF(ioffset_coordinate[i*2]*width,
                    ioffset_coordinate[i*2+1]*height);
        }

        test_array.add(i_ps[38]);
        test_array.add(i_ps[8]);
        test_array.add(i_ps[18]);
        test_array.add(i_ps[24]);
        test_array.add(i_ps[23]);
        test_array.add(i_ps[22]);
        test_array.add(i_ps[21]);
        test_array.add(i_ps[20]);
        test_array.add(i_ps[19]);

        PointF []tArray = new PointF[test_array.size()];

        for(int i = 0; i < tArray.length; i++)
        {
            tArray[i] = new PointF(test_array.get(i).x, test_array.get(i).y);
        }

        boolean p_inside = isPointInsidePolygon(tArray , i_ps[7]);
        if(!p_inside)
        {
            area_outside_strength = 0.0f;
            return area_outside_strength;
        }

        test_array.clear();

        test_array.add(i_ps[7]);
        test_array.add(i_ps[8]);
        test_array.add(i_ps[9]);
        test_array.add(i_ps[17]);
        test_array.add(i_ps[27]);
        test_array.add(i_ps[26]);
        test_array.add(i_ps[25]);
        test_array.add(i_ps[24]);

        tArray = null;
        tArray = new PointF[test_array.size()];

        for(int i = 0; i < tArray.length; i++)
        {
            tArray[i] = new PointF(test_array.get(i).x, test_array.get(i).y);
        }

        p_inside = isPointInsidePolygon(tArray, i_ps[18]);
        if(!p_inside)
        {
            area_outside_strength = 0.0f;
            return area_outside_strength;
        }

        test_array.clear();

        test_array.add(i_ps[18]);
        test_array.add(i_ps[9]);
        test_array.add(i_ps[16]);
        test_array.add(i_ps[27]);

        tArray = null;
        tArray = new PointF[test_array.size()];

        for(int i = 0; i < tArray.length; i++)
        {
            tArray[i] = new PointF(test_array.get(i).x, test_array.get(i).y);
        }

        p_inside = isPointInsidePolygon(tArray, i_ps[17]);
        if(!p_inside)
        {
            area_outside_strength = 0.0f;
            return area_outside_strength;
        }

        test_array.clear();
        test_array.add(i_ps[17]);
        test_array.add(i_ps[9]);
        test_array.add(i_ps[10]);
        test_array.add(i_ps[11]);
        test_array.add(i_ps[15]);
        test_array.add(i_ps[30]);
        test_array.add(i_ps[29]);
        test_array.add(i_ps[28]);
        test_array.add(i_ps[27]);
        tArray = null;
        tArray = new PointF[test_array.size()];

        for(int i = 0; i < tArray.length; i++)
        {
            tArray[i] = new PointF(test_array.get(i).x, test_array.get(i).y);
        }
        p_inside = isPointInsidePolygon(tArray, i_ps[16]);
        if(!p_inside)
        {
            area_outside_strength = 0.0f;
            return area_outside_strength;
        }

        test_array.clear();
        test_array.add(i_ps[16]);
        test_array.add(i_ps[11]);
        test_array.add(i_ps[14]);
        test_array.add(i_ps[31]);
        test_array.add(i_ps[30]);

        tArray = null;
        tArray = new PointF[test_array.size()];

        for(int i = 0; i < tArray.length; i++)
        {
            tArray[i] = new PointF(test_array.get(i).x, test_array.get(i).y);
        }
        p_inside = isPointInsidePolygon(tArray , i_ps[15]);
        if(!p_inside)
        {
            area_outside_strength = 0.0f;
            return area_outside_strength;
        }

        test_array.clear();
        test_array.add(i_ps[15]);
        test_array.add(i_ps[11]);
        test_array.add(i_ps[12]);
        test_array.add(i_ps[13]);
        test_array.add(i_ps[35]);
        test_array.add(i_ps[34]);
        test_array.add(i_ps[33]);
        test_array.add(i_ps[32]);
        test_array.add(i_ps[31]);

        tArray = null;
        tArray = new PointF[test_array.size()];

        for(int i = 0; i < tArray.length; i++)
        {
            tArray[i] = new PointF(test_array.get(i).x, test_array.get(i).y);
        }
        p_inside = isPointInsidePolygon(tArray, i_ps[14]);
        if(!p_inside)
        {
            area_outside_strength = 0.0f;
            return area_outside_strength;
        }

        test_array.clear();
        test_array.add(i_ps[14]);
        test_array.add(i_ps[12]);
        test_array.add(i_ps[39]);
        test_array.add(i_ps[37]);
        test_array.add(i_ps[36]);
        test_array.add(i_ps[35]);

        tArray = null;
        tArray = new PointF[test_array.size()];

        for(int i = 0; i < tArray.length; i++)
        {
            tArray[i] = new PointF(test_array.get(i).x, test_array.get(i).y);
        }

        p_inside = isPointInsidePolygon(tArray, i_ps[13]);
        if(!p_inside)
        {
            area_outside_strength = 0.0f;
            return area_outside_strength;
        }

        return area_outside_strength;
    }
}
