package cn.poco.dynamicSticker;

/**
 * Created by zwq on 2016/07/04 11:16.<br/><br/>
 * 贴纸类型
 */
public class StickerType {

    public static final int STICKER_TYPE_NUM = 10;//素材的类型数

    public static final String Face3D = "3D";

    public static final String Face = "face";// 0
    public static final String Head = "head";// 1
    public static final String Ear = "ear";// 2
    public static final String Eye = "eye";// 3
    public static final String Nose = "nose";// 4
    public static final String Mouth = "mouth";// 5
    public static final String Chin = "chin";// 6//下巴
    public static final String Shoulder = "shoulder";// 7//肩膀

    //固定位置
    public static final String Foreground = "foreground";// 10//前景
    public static final String Frame = "frame";// 11//相框
    public static final String Full = "full";// 12//全屏铺满
    public static final String WaterMark = "watermark";// 15//水印

    public static int getLayer(String type) {
        if (Face3D.equals(type)) {
            return 0;
        } else if (Face.equals(type)) {
            return 1;
        } else if (Head.equals(type)) {
            return 2;
        } else if (Ear.equals(type)) {
            return 3;
        } else if (Eye.equals(type)) {
            return 4;
        } else if (Nose.equals(type)) {
            return 5;
        } else if (Mouth.equals(type)) {
            return 6;
        } else if (Chin.equals(type)) {
            return 7;
        } else if (Shoulder.equals(type)) {
            return 8;

        } else if (Foreground.equals(type)) {
            return 10;
        } else if (Frame.equals(type)) {
           return 11;
        } else if (Full.equals(type)) {
            return 12;

        } else if (WaterMark.equals(type)) {
            return 15;
        }
        return 15;
    }

    public static int getIndexByType(String type) {
        if (type.equals(StickerType.Face)) {
            return 0;
        } else if (type.equals(StickerType.Head)) {
            return 1;
        } else if (type.equals(StickerType.Ear)) {
            return 2;
        } else if (type.equals(StickerType.Eye)) {
            return 3;
        } else if (type.equals(StickerType.Nose)) {
            return 4;
        } else if (type.equals(StickerType.Mouth)) {
            return 5;
        } else if (type.equals(StickerType.Chin)) {
            return 6;
        } else if (type.equals(StickerType.Shoulder)) {
            return 7;
        } else if (type.equals(StickerType.Foreground)) {
            return 8;
        } else if (type.equals(StickerType.Frame)) {
            return 9;
        } else if (type.equals(StickerType.Full)) {
            return 10;
        } else if (type.equals(StickerType.WaterMark)) {
            return 11;
        } else {
            return 0;
        }
    }
}

