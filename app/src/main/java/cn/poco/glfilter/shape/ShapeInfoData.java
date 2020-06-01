package cn.poco.glfilter.shape;

/**
 * Created by zwq on 2017/09/11 16:57.<br/><br/>
 * 脸型参数
 */
public class ShapeInfoData {

    public boolean isOvalEye;//是否是椭圆眼
    public float eyeRadius;//眼睛
    public float eyeStrength;

    public float noseRadius;//鼻子
    public float noseStrength;

    public float mouthRadius;//嘴巴
    public float mouthStrength;

    public float smileRadius;//微笑
    public float smileStrength;

    public float chinRadius;//下巴
    public float chinStrength;

    public float faceRadius1;//瘦脸
    public float faceStrength1;

    public float faceRadius2;//削脸
    public float faceStrength2;

    public float faceRadius3;//小脸
    public float faceStrength3;

    public float mouthHighStrength;//嘴高低

    public float headStrength;//额头

    public float eyeAngleStrength;//眼角

    public float eyeDistStrength;//眼距

    public float noseWingStrength;//鼻翼

    public float noseLengthStrength;//鼻高

    public float cheekBoneRadius;
    public float cheekBoneStrength;//颧骨

    public static ShapeInfoData getShapeInfoById(int id) {
        switch (id) {
            case 17:
                return getZiRanMei();
            case 18:
                return getQingChangNvWang();
            case 19:
                return getWangHong();
            case 20:
                return getKaTongMengShen();
            case 21:
                return getDuDuLian();
            case 22:
                return getJiMengShaoNv();
            case 23:
                return getDaiMengTianXin();
            case 24:
                return getQiZhiNvShen();
        }
        return null;
    }

    public static ShapeInfoData getZiRanMei() {
        ShapeInfoData shapeInfoData = new ShapeInfoData();
        shapeInfoData.eyeRadius = 17.7f;
        shapeInfoData.eyeStrength = 14.4f;
        shapeInfoData.noseRadius = 0f;
        shapeInfoData.noseStrength = 50f;
        shapeInfoData.mouthRadius = 0f;
        shapeInfoData.mouthStrength = 67.7f;
        shapeInfoData.smileRadius = 34.7f;
        shapeInfoData.smileStrength = 42.6f;

        shapeInfoData.chinRadius = 82.6f;
        shapeInfoData.chinStrength = 76f;
        shapeInfoData.faceRadius1 = 10.5f;
        shapeInfoData.faceStrength1 = 9.1f;
        shapeInfoData.faceRadius2 = 19.1f;
        shapeInfoData.faceStrength2 = 24.9f;
        shapeInfoData.faceRadius3 = 0f;
        shapeInfoData.faceStrength3 = 16.7f;
        return shapeInfoData;
    }

    public static ShapeInfoData getQingChangNvWang() {
        ShapeInfoData shapeInfoData = new ShapeInfoData();
        shapeInfoData.eyeRadius = 29.5f;
        shapeInfoData.eyeStrength = 18.8f;
        shapeInfoData.noseRadius = 0f;
        shapeInfoData.noseStrength = 54.9f;
        shapeInfoData.mouthRadius = 0f;
        shapeInfoData.mouthStrength = 66.3f;
        shapeInfoData.smileRadius = 24f;
        shapeInfoData.smileStrength = 34.2f;

        shapeInfoData.chinRadius = 32.8f;
        shapeInfoData.chinStrength = 56f;
        shapeInfoData.faceRadius1 = 14.2f;
        shapeInfoData.faceStrength1 = 16.7f;
        shapeInfoData.faceRadius2 = 14.7f;
        shapeInfoData.faceStrength2 = 49.1f;
        shapeInfoData.faceRadius3 = 12.6f;
        shapeInfoData.faceStrength3 = 58.4f;
        return shapeInfoData;
    }

    public static ShapeInfoData getWangHong() {
        ShapeInfoData shapeInfoData = new ShapeInfoData();
        shapeInfoData.eyeRadius = 48.4f;
        shapeInfoData.eyeStrength = 20f;
        shapeInfoData.noseRadius = 0f;
        shapeInfoData.noseStrength = 43.7f;
        shapeInfoData.mouthRadius = 0f;
        shapeInfoData.mouthStrength = 51.9f;
        shapeInfoData.smileRadius = 17f;
        shapeInfoData.smileStrength = 37.2f;

        shapeInfoData.chinRadius = 37.9f;
        shapeInfoData.chinStrength = 55.6f;
        shapeInfoData.faceRadius1 = 48.4f;
        shapeInfoData.faceStrength1 = 28.4f;
        shapeInfoData.faceRadius2 = 41.9f;
        shapeInfoData.faceStrength2 = 36.5f;
        shapeInfoData.faceRadius3 = 7.4f;
        shapeInfoData.faceStrength3 = 68.8f;
        return shapeInfoData;
    }

    public static ShapeInfoData getQiZhiNvShen() {
        ShapeInfoData shapeInfoData = new ShapeInfoData();
        shapeInfoData.eyeRadius = 31.4f;
        shapeInfoData.eyeStrength = 22.1f;
        shapeInfoData.noseRadius = 0f;
        shapeInfoData.noseStrength = 69.1f;
        shapeInfoData.mouthRadius = 0f;
        shapeInfoData.mouthStrength = 64.7f;
        shapeInfoData.smileRadius = 44f;
        shapeInfoData.smileStrength = 24f;

        shapeInfoData.chinRadius = 7.2f;
        shapeInfoData.chinStrength = 55.3f;
        shapeInfoData.faceRadius1 = 12.3f;
        shapeInfoData.faceStrength1 = 23.3f;
        shapeInfoData.faceRadius2 = 35.6f;
        shapeInfoData.faceStrength2 = 22.8f;
        shapeInfoData.faceRadius3 = 0f;
        shapeInfoData.faceStrength3 = 43.7f;
        return shapeInfoData;
    }

    public static ShapeInfoData getKaTongMengShen() {
        ShapeInfoData shapeInfoData = new ShapeInfoData();
        shapeInfoData.eyeRadius = 51.2f;
        shapeInfoData.eyeStrength = 20.2f;
        shapeInfoData.noseRadius = 0f;
        shapeInfoData.noseStrength = 58.8f;
        shapeInfoData.mouthRadius = 0f;
        shapeInfoData.mouthStrength = 60.7f;
        shapeInfoData.smileRadius = 13.7f;
        shapeInfoData.smileStrength = 50.5f;

        shapeInfoData.chinRadius = 3.5f;
        shapeInfoData.chinStrength = 76f;
        shapeInfoData.faceRadius1 = 0f;
        shapeInfoData.faceStrength1 = 11.2f;
        shapeInfoData.faceRadius2 = 0f;
        shapeInfoData.faceStrength2 = 0f;
        shapeInfoData.faceRadius3 = 0f;
        shapeInfoData.faceStrength3 = 100f;
        return shapeInfoData;
    }

    public static ShapeInfoData getDuDuLian() {
        ShapeInfoData shapeInfoData = new ShapeInfoData();
        shapeInfoData.eyeRadius = 49.8f;
        shapeInfoData.eyeStrength = 31.4f;
        shapeInfoData.noseRadius = 0f;
        shapeInfoData.noseStrength = 68.4f;
        shapeInfoData.mouthRadius = 0f;
        shapeInfoData.mouthStrength = 70.7f;
        shapeInfoData.smileRadius = 23.3f;
        shapeInfoData.smileStrength = 16f;

        shapeInfoData.chinRadius = 0f;
        shapeInfoData.chinStrength = 51.6f;
        shapeInfoData.faceRadius1 = 0f;
        shapeInfoData.faceStrength1 = 0f;
        shapeInfoData.faceRadius2 = 0f;
        shapeInfoData.faceStrength2 = 0f;
        shapeInfoData.faceRadius3 = 0f;
        shapeInfoData.faceStrength3 = 100f;
        return shapeInfoData;
    }

    public static ShapeInfoData getJiMengShaoNv() {
        ShapeInfoData shapeInfoData = new ShapeInfoData();
        shapeInfoData.eyeRadius = 37.7f;
        shapeInfoData.eyeStrength = 23.7f;
        shapeInfoData.noseRadius = 0f;
        shapeInfoData.noseStrength = 57.9f;
        shapeInfoData.mouthRadius = 0f;
        shapeInfoData.mouthStrength = 58.1f;
        shapeInfoData.smileRadius = 25.3f;
        shapeInfoData.smileStrength = 40f;

        shapeInfoData.chinRadius = 4.7f;
        shapeInfoData.chinStrength = 100f;
        shapeInfoData.faceRadius1 = 0f;
        shapeInfoData.faceStrength1 = 0f;
        shapeInfoData.faceRadius2 = 9.8f;
        shapeInfoData.faceStrength2 = 12.8f;
        shapeInfoData.faceRadius3 = 0f;
        shapeInfoData.faceStrength3 = 80.2f;
        return shapeInfoData;
    }

    public static ShapeInfoData getDaiMengTianXin() {
        ShapeInfoData shapeInfoData = new ShapeInfoData();
        shapeInfoData.eyeRadius = 36.7f;
        shapeInfoData.eyeStrength = 20.7f;
        shapeInfoData.noseRadius = 0f;
        shapeInfoData.noseStrength = 62.6f;
        shapeInfoData.mouthRadius = 0f;
        shapeInfoData.mouthStrength = 70.2f;
        shapeInfoData.smileRadius = 0f;
        shapeInfoData.smileStrength = 35.8f;

        shapeInfoData.chinRadius = 80.2f;
        shapeInfoData.chinStrength = 50.9f;
        shapeInfoData.faceRadius1 = 3f;
        shapeInfoData.faceStrength1 = 1.6f;
        shapeInfoData.faceRadius2 = 14.7f;
        shapeInfoData.faceStrength2 = 21.4f;
        shapeInfoData.faceRadius3 = 0f;
        shapeInfoData.faceStrength3 = 17.4f;
        return shapeInfoData;
    }
}
