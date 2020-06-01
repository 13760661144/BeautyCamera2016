package cn.poco.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

import cn.poco.dynamicSticker.StickerType;

/**
 * Created by liujx on 2018/1/23.
 */

public class filterori {

    public static float getDistance(PointF pointF1, PointF pointF2) {
        float dx = pointF1.x - pointF2.x;
        float dy = pointF1.y - pointF2.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
    public static float getRadians(PointF pointF1, PointF pointF2) {
        float dx = pointF2.x - pointF1.x;
        float dy = pointF2.y - pointF1.y;
        return (float) Math.atan2(dy, dx);//弧度
    }
    public static PointF getCenterPoint(PointF pointF1, PointF pointF2){
        PointF center = new PointF();
        center.x = (pointF1.x + pointF2.x) / 2;
        center.y = (pointF1.y + pointF2.y) / 2;
        return center;
    }

    //返回额头左中右三个点
    public static PointF[] calForeHead(PointF []points_array)
    {
        PointF []ret = new PointF[3];
        for(int i=0; i<3; i++)
        {
            ret[i] = new PointF();
            ret[i].x = 0;
            ret[i].y = 0;
        }

        double eye_angle = Math.atan2(points_array[PocoLandmarks.rEyeX/2].y - points_array[PocoLandmarks.lEyeX/2].y,
                points_array[PocoLandmarks.rEyeX / 2].x - points_array[PocoLandmarks.lEyeX / 2].x);

        float two_eye_center_x = (points_array[PocoLandmarks.lEyeX / 2].x + points_array[PocoLandmarks.rEyeX / 2].x) / 2;
        float two_eye_center_y = (points_array[PocoLandmarks.lEyeX / 2].y + points_array[PocoLandmarks.rEyeX / 2].y) / 2;

        PointF eye_center = new PointF(two_eye_center_x, two_eye_center_y);

        double distance = getDistance(eye_center, points_array[PocoLandmarks.TopOfTopLipX / 2]);

        double l_width = getDistance(points_array[PocoLandmarks.lEyeInnerX / 2], points_array[PocoLandmarks.lEyeOuterX / 2]);
        double r_width = getDistance(points_array[PocoLandmarks.rEyeInnerX / 2], points_array[PocoLandmarks.rEyeOuterX / 2]);

        double l_scale = 0.7 + (((l_width / distance) / 1.2 * 0.4) > 0.4 ? 0.4 : (((l_width / distance) / 1.2 * 0.4) < 0.0 ? 0.0 : ((l_width / distance) / 1.2 * 0.4)));
        double r_scale = 0.7 + (((r_width / distance) / 1.2 * 0.4) > 0.4 ? 0.4 : (((r_width / distance) / 1.2 * 0.4) < 0.0 ? 0.0 : ((r_width / distance) / 1.2 * 0.4)));

        ret[0].x = (float) (points_array[PocoLandmarks.lEyeOuterX / 2].x + l_scale * distance * Math.sin(eye_angle));
        ret[0].y = (float) (points_array[PocoLandmarks.lEyeOuterX / 2].y - l_scale * distance * Math.cos(eye_angle));

        ret[2].x = (float) (points_array[PocoLandmarks.rEyeOuterX / 2].x + r_scale * distance * Math.sin(eye_angle));
        ret[2].y = (float) (points_array[PocoLandmarks.rEyeOuterX / 2].y - r_scale * distance * Math.cos(eye_angle));

        ret[1].x = (float) (two_eye_center_x + 1.2 * distance * Math.sin(eye_angle));
        ret[1].y = (float) (two_eye_center_y - 1.2 * distance * Math.cos(eye_angle));

        return ret;
    }

    /*
    * width     图片的宽
    * height    图片的高
    * faceinfo  人脸数据
    * type      贴纸类型
    * 返回float数组 长度为4 依次存放中心点x，中心点y，旋转弧度，素材缩放后的宽;
    * */
    public static float []getSticterPosition(int width, int height, PocoFaceInfo faceInfo, String type)
    {
        if(faceInfo == null)
            return null;

        float faceArray[] = faceInfo.getFaceFeaturesMakeUp();
        if(faceArray == null || faceArray.length != PocoLandmarks.LengthOfLandmarks)
            return null;

        float ret[] = new float[4];
        PointF []points_array = new PointF[faceArray.length / 2];
        for(int i=0; i<faceArray.length/2; i++)
        {
            points_array[i] = new PointF();
            points_array[i].x = faceArray[2*i] * width;
            points_array[i].y = faceArray[2*i+1] * height;
        }

        ret[2] = getRadians(points_array[PocoLandmarks.lEyeOuterX / 2], points_array[PocoLandmarks.rEyeOuterX / 2]);

        PointF []ForeHead = calForeHead(points_array);
        if (type.equals(StickerType.Head)) {
            ret[3] = getDistance(ForeHead[2], ForeHead[0]);
            PointF cp = getCenterPoint(ForeHead[2], ForeHead[0]);
            ret[0] = cp.x;
            ret[1] = cp.y;
            ret[3] = getDistance(ForeHead[2], ForeHead[0]);

        } else if (type.equals(StickerType.Eye)) {

            ret[0] = (points_array[PocoLandmarks.rEyeOuterX / 2].x + points_array[PocoLandmarks.lEyeOuterX / 2].x) / 2;
            ret[1] = (points_array[PocoLandmarks.rEyeOuterX / 2].y + points_array[PocoLandmarks.lEyeOuterX / 2].y) / 2;
            ret[3] = getDistance(points_array[PocoLandmarks.rEyeOuterX / 2], points_array[PocoLandmarks.lEyeOuterX / 2]);

        } else if (type.equals(StickerType.Nose)) {
            ret[3] = getDistance(points_array[PocoLandmarks.NoseRightX / 2], points_array[PocoLandmarks.NoseLeftX / 2]);
            PointF cp = getCenterPoint(points_array[PocoLandmarks.NoseRightX / 2], points_array[PocoLandmarks.NoseLeftX / 2]);
            ret[0] = cp.x;
            ret[1] = cp.y;
        } else if (type.equals(StickerType.Mouth)) {
            PointF cp = getCenterPoint(points_array[PocoLandmarks.rMouthCornerX / 2], points_array[PocoLandmarks.lMouthCornerX / 2]);
            ret[0] = cp.x;
            ret[1] = cp.y;
            ret[3] = getDistance(points_array[PocoLandmarks.rMouthCornerX / 2], points_array[PocoLandmarks.lMouthCornerX / 2]);

        } else if (type.equals(StickerType.Shoulder)) {

            PointF p = new PointF();
            p.x = (points_array[PocoLandmarks.NoseLeftX / 2].x + points_array[PocoLandmarks.NoseRightX / 2].x) / 2;
            p.y = (points_array[PocoLandmarks.NoseLeftX / 2].y + points_array[PocoLandmarks.NoseRightX / 2].y) / 2;

            float noseChinDist = getDistance(p, points_array[PocoLandmarks.chinX / 2]);

            ret[3] = getDistance(ForeHead[2], ForeHead[0]) * 3.8f;

            ret[0] = (points_array[PocoLandmarks.chinLeftX / 2].x + points_array[PocoLandmarks.chinRightX / 2].x) / 2;
            ret[1] = (points_array[PocoLandmarks.NoseLeftX / 2].y + points_array[PocoLandmarks.NoseRightX / 2].y) / 2 + noseChinDist * 1.7f;
        }

        return ret;
    }



    public static Bitmap LookTable_RS(Bitmap dest, Bitmap mask, Context context)
    {
        try{
            RenderScript rs = RenderScript.create(context);
            Allocation allocationA = Allocation.createFromBitmap(rs, dest);
            Allocation allocationB = Allocation.createFromBitmap(rs, mask);

            ScriptC_lookupTable lookupTable = new ScriptC_lookupTable(rs);
            lookupTable.set_TableAllocation(allocationB);

            lookupTable.invoke_createRemapArray();

            lookupTable.forEach_root(allocationA, allocationA);
            allocationA.copyTo(dest);

            allocationA.destroy();
            allocationB.destroy();
            lookupTable.destroy();
            rs.destroy();
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }

        return  dest;
    }

    public static Bitmap Composite_RS(Bitmap dest, Bitmap mask, int comOp, int alpha, Context context)
    {
        try{
            RenderScript rs = RenderScript.create(context);
            Allocation allocationA = Allocation.createFromBitmap(rs, dest);
            Allocation allocationB = Allocation.createFromBitmap(rs, mask);

            ScriptC_Composite comp= new ScriptC_Composite(rs);
            comp.set_mask(allocationB);
            comp.set_alpha(alpha*255/100);
            comp.set_comOp(comOp);
            comp.forEach_root(allocationA, allocationA);
            allocationA.copyTo(dest);

            allocationA.destroy();
            allocationB.destroy();
            comp.destroy();
            rs.destroy();
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }

        return dest;
    }

    /*
    * mask的宽高为1:1
    * 将mask按照cutOp的裁剪规则 缩放到dwidrh,dheigth大小
    * */
    public static Bitmap cutAndResize(Bitmap mask, int dwidth, int dheight, int cutOp)
    {
        if ((null == mask) || (mask.getConfig() != Bitmap.Config.ARGB_8888) || mask.isRecycled())
            return mask;

        int mwidth = mask.getWidth();
        int mheight = mask.getHeight();

        int sizeMax = Math.max(dwidth, dheight);
        int sizeMin = Math.min(dwidth, dheight);

        float dmin_max = (float)sizeMin / sizeMax;

        int x=0, y=0, swidth=mwidth, sheight=mheight;
        Bitmap ret = null;
        if ((cutOp == PocoCutOperator.left) || (cutOp == PocoCutOperator.right)
                || (cutOp == PocoCutOperator.top) || (cutOp == PocoCutOperator.bottom)
                || (cutOp == PocoCutOperator.leftRight) || (cutOp == PocoCutOperator.topBottom)) {

            Matrix matrix = new Matrix();
            float scale = (float)sizeMax / Math.max(mwidth, mheight);
            matrix.setScale(scale, scale);

            int MaskMinSize = (int)(dmin_max * mwidth + 0.5f);

            switch(cutOp){
                case PocoCutOperator.left:
                    x = mwidth - MaskMinSize;
                    y = 0;
                    swidth = MaskMinSize;
                    sheight = mheight;
                    break;
                case PocoCutOperator.right:
                    x = 0;
                    y = 0;
                    swidth = MaskMinSize;
                    sheight = mheight;
                    break;
                case PocoCutOperator.top:
                    x = 0;
                    y = mheight - MaskMinSize;
                    swidth = mwidth;
                    sheight = MaskMinSize;
                    break;
                case PocoCutOperator.bottom:
                    x = 0;
                    y = 0;
                    swidth = mwidth;
                    sheight = MaskMinSize;
                    break;
                case PocoCutOperator.leftRight:
                    x = (int)((mwidth - MaskMinSize) / 2.0 + 0.5);
                    y = 0;
                    swidth = MaskMinSize;
                    sheight = mheight;
                    break;
                case PocoCutOperator.topBottom:
                    x = 0;
                    y = (int)((mheight - MaskMinSize) / 2.0 + 0.5);
                    swidth = mwidth;
                    sheight = MaskMinSize;
                    break;
            }

            ret = Bitmap.createBitmap(mask, x, y, swidth, sheight, matrix, true);
        }
        else if(PocoCutOperator.stretching == cutOp) {
            Matrix matrix = new Matrix();
            float scale = (float)sizeMax / Math.max(mwidth, mheight);
            matrix.setScale(scale, scale);

            ret = Bitmap.createBitmap(mask, 0, 0, mwidth, mheight, matrix, true);
        }
        return ret;
    }

    /**
     *  滤镜下载静态效果接口  4.0.7优化 使用rs加速
     *  @param destBmp     原图
     *  @param colorTable  滤镜颜色表 (必须)
     *  @param mask        光效图片数组  (非必须)
     *  @param Comop       光效图片对应的混合模式数组 (非必须)
     *  @param opacity     光效图片对应的不透明度模式数组 (非必须)
     *  @param faceInfo    人脸数据  无人脸传null
     *  @param isHollow    光效是否避开人脸
     * */
    public static Bitmap loadFilterV2_rs(Context txt, Bitmap destBmp, Bitmap colorTable, Bitmap []mask, int []Comop, int []opacity, PocoFaceInfo[] faceInfo, boolean isHollow) {
        if (destBmp == null || destBmp.getConfig() != Bitmap.Config.ARGB_8888 || destBmp.isRecycled())
            return destBmp;

        int width = destBmp.getWidth();
        int height = destBmp.getHeight();

//        double t = System.currentTimeMillis();
        if (colorTable != null && !colorTable.isRecycled())
            destBmp =  LookTable_RS(destBmp, colorTable, txt);

//        System.out.println("lookup table time:"+(System.currentTimeMillis()-t));
//        t = System.currentTimeMillis();

        Bitmap src = null;

        if(isHollow)
            src = destBmp.copy(Bitmap.Config.ARGB_8888, true);

        if (mask != null) {
            int maskNum = mask.length;

            for (int i = 0; i < maskNum; i++) {

                int cutop = 0;
                if (width > height) {
                    cutop = PocoCutOperator.topBottom;
                } else if (height > width) {
                    cutop = PocoCutOperator.leftRight;
                } else {
                    cutop = PocoCutOperator.stretching;
                }
                Bitmap reMask = cutAndResize(mask[i], width, height, cutop);
                if(reMask != null) {
                    destBmp = Composite_RS(destBmp, reMask, Comop[i], opacity[i], txt);
                    reMask.recycle();
                    reMask = null;
                }
            }
        }

//        System.out.println("3 lightEffect time:"+(System.currentTimeMillis()-t));

        if(isHollow) {
            if (faceInfo != null && faceInfo.length > 0) {
                int faceNum = faceInfo.length;
                int faceArray[] = new int[10 * faceNum];
                for (int i = 0; i < faceNum; i++) {

                    float[] facefeatures = faceInfo[i].getFaceFeaturesMakeUp();
                    float[] faceRect = faceInfo[i].getFaceRect();

                    if (facefeatures != null && facefeatures.length == PocoLandmarks.LengthOfLandmarks) {
                        faceArray[10 * i] = (int) (facefeatures[PocoLandmarks.lEyeX] * width);
                        faceArray[10 * i + 1] = (int) (facefeatures[PocoLandmarks.lEyeY] * height);

                        faceArray[10 * i + 2] = (int) (facefeatures[PocoLandmarks.rEyeX] * width);
                        faceArray[10 * i + 3] = (int) (facefeatures[PocoLandmarks.rEyeY] * height);

                        faceArray[10 * i + 4] = (int) (facefeatures[PocoLandmarks.chinLeftX] * width);
                        faceArray[10 * i + 5] = (int) (facefeatures[PocoLandmarks.chinLeftY] * height);

                        faceArray[10 * i + 6] = (int) (facefeatures[PocoLandmarks.chinRightX] * width);
                        faceArray[10 * i + 7] = (int) (facefeatures[PocoLandmarks.chinRightY] * height);

                        faceArray[10 * i + 8] = (int) ((faceRect[PocoLandmarks.FaceRectX] + faceRect[PocoLandmarks.FaceRectWidth] / 2) * width);
                        faceArray[10 * i + 9] = (int) ((faceRect[PocoLandmarks.FaceRectY]  + faceRect[PocoLandmarks.FaceRectHeight] / 2)* height);
                    }
                }
                PocoNativeFilter.LightEffectAvoidFace(destBmp, src, faceArray, faceNum);
            }
        }

        if(src != null)
        {
            src.recycle();
            src = null;
        }

        return destBmp;
    }

}
