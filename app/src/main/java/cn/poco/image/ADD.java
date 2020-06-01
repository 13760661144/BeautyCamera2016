package cn.poco.image;

import android.content.Context;
import android.graphics.Bitmap;

import cn.poco.addpost.detect;

/**
 * Created by lee on 2016/12/23.
 */

public class ADD
{
    private static float[] tempFacePointsMulti = new float[90];
    private static boolean FacePPDetectFlag = false;
    private static boolean FaceDetectLocal = false;

    private static void resetTempVal(){
        for (int i = 0; i < 90; i++){
            tempFacePointsMulti[i] = 0;
        }
        FaceDetectLocal = false;
        FacePPDetectFlag = false;
    }

    /**
     * 阿玛尼商业专供
     * @param context
     * @param bmp
     * @return
     */
    public static float[][] AmniDetectForShape(Context context, Bitmap bmp)
    {
        if (bmp == null){
            return null;
        }

        resetTempVal();

        int minSize = 640;
        Float[] tempResult = detect.detect(bmp, minSize, context);

        if (tempResult != null) {

            if (170 != tempResult.length)
                return null;

            int[] table = {5, 0, 15,
                    29, 35, 33, 31,
                    79, 81, 75, 77,
                    21, 26, 25, 19,
                    71, 72, 67, 65,
                    64, 62, 58, 63,
                    37, 49, 48, 54, 51, 52, 46, 43, 44, 38, 41, 40, 50, 47, 53, 42, 45, 39,
                    20, 66};

            for (int i = 0; i < PocoLandmarks.LengthOfLandmarks / 2; i++) {
                tempFacePointsMulti[2 * i] = tempResult[2 * table[i]];
                tempFacePointsMulti[2 * i + 1] = tempResult[2 * table[i] + 1];
            }

            float[][] result = new float[1][PocoLandmarks.LengthOfFaceDetect];

            result[0][PocoLandmarks.FaceRectX] = tempFacePointsMulti[PocoLandmarks.LengthOfLandmarks + PocoLandmarks.FaceRectX] = tempResult[166];
            result[0][PocoLandmarks.FaceRectY] = tempFacePointsMulti[PocoLandmarks.LengthOfLandmarks + PocoLandmarks.FaceRectY] = tempResult[167];
            result[0][PocoLandmarks.FaceRectWidth] = tempFacePointsMulti[PocoLandmarks.LengthOfLandmarks + PocoLandmarks.FaceRectWidth] = tempResult[168];
            result[0][PocoLandmarks.FaceRectHeight] = tempFacePointsMulti[PocoLandmarks.LengthOfLandmarks + PocoLandmarks.FaceRectHeight] = tempResult[169];
            result[0][PocoLandmarks.lEyeCenterX] = tempFacePointsMulti[PocoLandmarks.lEyeX];
            result[0][PocoLandmarks.lEyeCenterY] = tempFacePointsMulti[PocoLandmarks.lEyeY];
            result[0][PocoLandmarks.rEyeCenterX] = tempFacePointsMulti[PocoLandmarks.rEyeX];
            result[0][PocoLandmarks.rEyeCenterY] = tempFacePointsMulti[PocoLandmarks.rEyeY];
            result[0][PocoLandmarks.MouthCenterX] = (tempFacePointsMulti[PocoLandmarks.BotOfTopLipX]
                    + tempFacePointsMulti[PocoLandmarks.TopOfBotLipX]) / 2;
            result[0][PocoLandmarks.MouthCenterY] = (tempFacePointsMulti[PocoLandmarks.BotOfTopLipY]
                    + tempFacePointsMulti[PocoLandmarks.TopOfBotLipY]) / 2;
            FacePPDetectFlag = true;
            FaceDetectLocal = false;
            return result;
        }else{
            float[][] result = filter.detectForshapeMulti(context, bmp);
            if (result != null){
                FacePPDetectFlag = false;
                FaceDetectLocal = true;
                return result;
            }
            return null;
        }
    }

    /**
     * 阿玛尼商业专供
     * @return
     */
    public static float[][] AmniGetFeatureCosmetic(){
        if (FacePPDetectFlag) {
            float[][] reval = new float[1][PocoLandmarks.LengthOfLandmarks];
            for (int j = 0; j < PocoLandmarks.LengthOfLandmarks; j++) {
                reval[0][j] = tempFacePointsMulti[j];
            }
            return reval;
        }else if (FaceDetectLocal){
            return filter.getFeatureCosmeticMulti();
        }
        return null;
    }

    /**
     * 唇下部有修正
     * @param context
     * @param bmp
     * @return
     */
    public static float[][] BenefitDetectForShape(Context context, Bitmap bmp)
    {
        if (bmp == null){
            return null;
        }

        resetTempVal();

        int minSize = 640;
        Float[] tempResult = detect.detect(bmp, minSize, context);

        if (tempResult != null) {
            if (170 != tempResult.length)
                return null;

            int[] table = {5, 0, 15,
                    29, 35, 33, 31,
                    79, 81, 75, 77,
                    21, 26, 25, 19,
                    71, 72, 67, 65,
                    64, 62, 58, 63,
                    37, 49, 48, 54, 51, 52, 46, 43, 44, 38, 41, 40, 50, 47, 53, 42, 45, 39,
                    24, 70};


            for (int i = 0; i < PocoLandmarks.LengthOfLandmarks / 2; i++) {
                tempFacePointsMulti[2 * i] = tempResult[2 * table[i]];
                tempFacePointsMulti[2 * i + 1] = tempResult[2 * table[i] + 1];

                if(i>=41)
                    System.out.println("tabX:"+tempFacePointsMulti[2*i]+", tabY:"+tempFacePointsMulti[2*i+1]);
            }
            System.out.println("tabX20:"+tempResult[2*20]+", tabY20:"+tempResult[2*20+1]);
            System.out.println("tabX66:"+tempResult[2*66]+", tabY66:"+tempResult[2*66+1]);

            tempFacePointsMulti[PocoLandmarks.lMouthBot1X] -= (float) 5.f / bmp.getWidth();
            tempFacePointsMulti[PocoLandmarks.rMouthBot1X] += (float) 7.f / bmp.getWidth();

            float[][] result = new float[1][PocoLandmarks.LengthOfFaceDetect];

            result[0][PocoLandmarks.FaceRectX] = tempFacePointsMulti[PocoLandmarks.LengthOfLandmarks + PocoLandmarks.FaceRectX] = tempResult[166];
            result[0][PocoLandmarks.FaceRectY] = tempFacePointsMulti[PocoLandmarks.LengthOfLandmarks + PocoLandmarks.FaceRectY] = tempResult[167];
            result[0][PocoLandmarks.FaceRectWidth] = tempFacePointsMulti[PocoLandmarks.LengthOfLandmarks + PocoLandmarks.FaceRectWidth] = tempResult[168];
            result[0][PocoLandmarks.FaceRectHeight] = tempFacePointsMulti[PocoLandmarks.LengthOfLandmarks + PocoLandmarks.FaceRectHeight] = tempResult[169];
            result[0][PocoLandmarks.lEyeCenterX] = tempFacePointsMulti[PocoLandmarks.lEyeX];
            result[0][PocoLandmarks.lEyeCenterY] = tempFacePointsMulti[PocoLandmarks.lEyeY];
            result[0][PocoLandmarks.rEyeCenterX] = tempFacePointsMulti[PocoLandmarks.rEyeX];
            result[0][PocoLandmarks.rEyeCenterY] = tempFacePointsMulti[PocoLandmarks.rEyeY];
            result[0][PocoLandmarks.MouthCenterX] = (tempFacePointsMulti[PocoLandmarks.BotOfTopLipX]
                    + tempFacePointsMulti[PocoLandmarks.TopOfBotLipX]) / 2;
            result[0][PocoLandmarks.MouthCenterY] = (tempFacePointsMulti[PocoLandmarks.BotOfTopLipY]
                    + tempFacePointsMulti[PocoLandmarks.TopOfBotLipY]) / 2;
            FacePPDetectFlag = true;
            FaceDetectLocal = false;
            return result;
        }else{
            float[][] result = filter.detectForshapeMulti(context, bmp);
            if (result != null){
                FacePPDetectFlag = false;
                FaceDetectLocal = true;
                return result;
            }
            return null;
        }
    }

    /**
     * Benefit商业
     * @return
     */
    public static float[][] BenefitGetFeatureCosmetic(){
        if (FacePPDetectFlag) {
            float[][] reval = new float[1][PocoLandmarks.LengthOfLandmarks];
            for (int j = 0; j < PocoLandmarks.LengthOfLandmarks; j++) {
                reval[0][j] = tempFacePointsMulti[j];
            }
            return reval;
        }else if (FaceDetectLocal){
            return filter.getFeatureCosmeticMulti();
        }
        return null;
    }

    /**
     * Benefit商业专供检测接口
     * @return
     */
    public synchronized static PocoFaceInfo [] BenefitFaceinfoMuti(Context context, Bitmap destBmp)
    {
        if (destBmp == null){
            return null;
        }

        resetTempVal();

        float [][]facedata = BenefitDetectForShape(context, destBmp);

        if(facedata != null)
        {
            if(facedata.length > 0)
            {
                float [][]feature = BenefitGetFeatureCosmetic();
                PocoFaceInfo info[] = new PocoFaceInfo[facedata.length];

                for(int i=0; i<facedata.length; i++) {
                    info[i] = new PocoFaceInfo();
                    info[i].initFaceinfo();

                    info[i].setFaceRect(facedata[i]);
                    info[i].setMakeUpFeatures(feature[i]);
                }

                return  info;
            }
        }

        return null;
    }

    //商业检测
    public static float[][] BussinessDetectForShape(Context context, Bitmap bmp)
    {
        if (bmp == null){
            return null;
        }

        resetTempVal();

        int minSize = 640;
        Float[] tempResult = detect.detect(bmp, minSize, context);

        if (tempResult != null) {
            if (170 != tempResult.length)
                return null;

            int[] table = {5, 0, 15,
                    29, 35, 33, 31,
                    79, 81, 75, 77,
                    21, 26, 25, 19,
                    71, 72, 67, 65,
                    64, 62, 58, 63,
                    37, 49, 48, 54, 51, 52, 46, 43, 44, 38, 41, 40, 50, 47, 53, 42, 45, 39,
                    24, 70};


            for (int i = 0; i < PocoLandmarks.LengthOfLandmarks / 2; i++) {
                tempFacePointsMulti[2 * i] = tempResult[2 * table[i]];
                tempFacePointsMulti[2 * i + 1] = tempResult[2 * table[i] + 1];

            }

            float[][] result = new float[1][PocoLandmarks.LengthOfFaceDetect];

            result[0][PocoLandmarks.FaceRectX] = tempFacePointsMulti[PocoLandmarks.LengthOfLandmarks + PocoLandmarks.FaceRectX] = tempResult[166];
            result[0][PocoLandmarks.FaceRectY] = tempFacePointsMulti[PocoLandmarks.LengthOfLandmarks + PocoLandmarks.FaceRectY] = tempResult[167];
            result[0][PocoLandmarks.FaceRectWidth] = tempFacePointsMulti[PocoLandmarks.LengthOfLandmarks + PocoLandmarks.FaceRectWidth] = tempResult[168];
            result[0][PocoLandmarks.FaceRectHeight] = tempFacePointsMulti[PocoLandmarks.LengthOfLandmarks + PocoLandmarks.FaceRectHeight] = tempResult[169];
            result[0][PocoLandmarks.lEyeCenterX] = tempFacePointsMulti[PocoLandmarks.lEyeX];
            result[0][PocoLandmarks.lEyeCenterY] = tempFacePointsMulti[PocoLandmarks.lEyeY];
            result[0][PocoLandmarks.rEyeCenterX] = tempFacePointsMulti[PocoLandmarks.rEyeX];
            result[0][PocoLandmarks.rEyeCenterY] = tempFacePointsMulti[PocoLandmarks.rEyeY];
            result[0][PocoLandmarks.MouthCenterX] = (tempFacePointsMulti[PocoLandmarks.BotOfTopLipX]
                    + tempFacePointsMulti[PocoLandmarks.TopOfBotLipX]) / 2;
            result[0][PocoLandmarks.MouthCenterY] = (tempFacePointsMulti[PocoLandmarks.BotOfTopLipY]
                    + tempFacePointsMulti[PocoLandmarks.TopOfBotLipY]) / 2;
            FacePPDetectFlag = true;
            FaceDetectLocal = false;
            return result;
        }else{
            float[][] result = filter.detectForshapeMulti(context, bmp);
            if (result != null){
                FacePPDetectFlag = false;
                FaceDetectLocal = true;
                return result;
            }
            return null;
        }
    }

    public static float[][] BussinessGetFeatureCosmetic(){
        if (FacePPDetectFlag) {
            float[][] reval = new float[1][PocoLandmarks.LengthOfLandmarks];
            for (int j = 0; j < PocoLandmarks.LengthOfLandmarks; j++) {
                reval[0][j] = tempFacePointsMulti[j];
            }
            return reval;
        }else if (FaceDetectLocal){
            return filter.getFeatureCosmeticMulti();
        }
        return null;
    }

    /**
     * face++检测接口  商业用途
     * @return
     */
    public synchronized static PocoFaceInfo [] BussinessFaceinfoMuti(Context context, Bitmap destBmp)
    {
        if (destBmp == null){
            return null;
        }

        resetTempVal();

        float [][]facedata = BussinessDetectForShape(context, destBmp);

        if(facedata != null)
        {
            if(facedata.length > 0)
            {
                float [][]feature = BussinessGetFeatureCosmetic();
                PocoFaceInfo info[] = new PocoFaceInfo[facedata.length];

                for(int i=0; i<facedata.length; i++) {
                    info[i] = new PocoFaceInfo();
                    info[i].initFaceinfo();

                    info[i].setFaceRect(facedata[i]);
                    info[i].setMakeUpFeatures(feature[i]);
                }

                return  info;
            }
        }

        return null;
    }
}
