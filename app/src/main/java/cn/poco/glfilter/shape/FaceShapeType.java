package cn.poco.glfilter.shape;

/**
 * Created by zwq on 2016/07/04 11:16.<br/><br/>
 * 脸部变形类型
 */
public class FaceShapeType {

    //-------------------------------------------变形参数 (值:名称)
    public static final String None                  = "none";                //0
    public static final String ThinFace              = "thinFace";            //1 瘦脸
    public static final String BigEye                = "bigEye";              //2 大眼
    public static final String BigMouth              = "bigMouth";            //3 大嘴
    public static final String Extrusion             = "extrusion";           //4 挤压

    public static final String UpDownStrecth         = "upDownStrecth";       //5 上下拉伸
    public static final String FatFace               = "fatFace";             //6 大脸
    public static final String Sadness               = "sadness";             //7 悲伤
    public static final String GourdFace             = "gourdFace";           //8 葫芦脸
    public static final String Mosaic                = "mosaic";              //9 马赛克

    public static final String HandFace              = "handFace";            //10 巴掌脸
    public static final String BigEyeCute            = "bigEyeCute";          //11 大眼卖萌
    public static final String BigEyeLongFace        = "bigEyeLongFace";      //12 大眼长脸
    public static final String NatureMakeUp          = "natureMakeUp";        //13 自然修饰
    public static final String CuteDog1              = "fadou1";              //14 萌犬1
    public static final String CuteDog2              = "fadou2";              //15 萌犬2
    public static final String CuteDog3              = "fadou3";              //16 萌犬3

    public static final String ZiRanMei              = "Ziranmei";            //17 自然美
    public static final String QiChang               = "Qichang";             //18 气场女王
    public static final String WangHong              = "Wanghong";            //19 网红脸
    public static final String KaTong                = "Katong";              //20 卡通萌神
    public static final String DuDu                  = "Dudu";                //21 嘟嘟脸
    public static final String JiMeng                = "Jimeng";              //22 激萌少女
    public static final String DaiMeng               = "Daimeng";             //23 呆萌甜心
    public static final String QiZhi                 = "Qizhi";               //24 气质女神

    public static int getShapeIdByName(String name) {
        if (None.equals(name)) {
            return 0;
        } else if (ThinFace.equals(name)) {
            return 1;
        } else if (BigEye.equals(name)) {
            return 2;
        } else if (BigMouth.equals(name)) {
            return 3;
        } else if (Extrusion.equals(name)) {
            return 4;

        } else if (UpDownStrecth.equals(name)) {
            return 5;
        } else if (FatFace.equals(name)) {
            return 6;
        } else if (Sadness.equals(name)) {
            return 7;
        } else if (GourdFace.equals(name)) {
            return 8;
        } else if (Mosaic.equals(name)) {
            return 9;

        } else if (HandFace.equals(name)) {
            return 10;
        } else if (BigEyeCute.equals(name)) {
            return 11;
        } else if (BigEyeLongFace.equals(name)) {
            return 12;
        } else if (NatureMakeUp.equals(name)) {
            return 13;
        } else if (CuteDog1.equals(name)) {
            return 14;
        } else if (CuteDog2.equals(name)) {
            return 15;
        } else if (CuteDog3.equals(name)) {
            return 16;

        } else if (ZiRanMei.equals(name)) {
            return 17;
        } else if (QiChang.equals(name)) {
            return 18;
        } else if (WangHong.equals(name)) {
            return 19;
        } else if (KaTong.equals(name)) {
            return 20;
        } else if (DuDu.equals(name)) {
            return 21;
        } else if (JiMeng.equals(name)) {
            return 22;
        } else if (DaiMeng.equals(name)) {
            return 23;
        } else if (QiZhi.equals(name)) {
            return 24;
        }

        return 0;
    }
}
