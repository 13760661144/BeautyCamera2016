package cn.poco.gldraw;

/**
 * Created by zwq on 2017/02/22 12:05.<br/><br/>
 * 绘制顺序/层级
 */
public class FilterDrawOrder {

    public static final int FILTER_INVALID = -1;
    public static final int FILTER_CAMERA = 0;                 //镜头原始画面(或用YUV数据渲染的画面)

    //美形定制
    public static final int FILTER_BEAUTY = 1;                 //美颜buffing
    public static final int FILTER_BUSINESS_CUSTOM = 2;        //商业定制美白
    public static final int FILTER_THIN_FACE_BIG_EYE = 3;      //瘦脸、大眼
    public static final int FILTER_SHRINK_NOSE = 4;            //瘦鼻
    public static final int FILTER_WHITEN_TEETH = 5;           //美牙
    public static final int FILTER_BEAUTIFY_SHAPE = 6;         //脸型

    public static final int FILTER_MAKEUP = 7;                 //彩妆
    public static final int FILTER_STICKER = 8;                //贴纸
    public static final int FILTER_SHAPE = 9;                  //变形
    public static final int FILTER_COLOR = 10;                 //滤镜
    public static final int FILTER_VIDEO_TEXTURE = 11;         //视频纹理
    public static final int FILTER_SPLIT_SCREEN = 12;          //分屏
    public static final int FILTER_SHOW_FACE_POINTS = 13;      //显示脸部点数据，用于调试


    public static final int FILTER_DISPLAY = 14;               //显示最终图像、人脸框、水印
    public static final int FILTER_MAX_LAYER = 15;             //[0,12] ->size:13

//-------------------------------------------------
    /*//美形定制
    public static final int FILTER_BEAUTY = 1;                 //美颜buffing
    public static final int FILTER_BUSINESS_CUSTOM = 1;        //商业定制美白
    public static final int FILTER_THIN_FACE_BIG_EYE = 2;      //瘦脸、大眼
    public static final int FILTER_SHRINK_NOSE = 3;            //瘦鼻
    public static final int FILTER_WHITEN_TEETH = 4;           //美牙
    public static final int FILTER_BEAUTIFY_SHAPE = 5;         //脸型

    public static final int FILTER_MAKEUP = 6;                 //彩妆
    public static final int FILTER_STICKER = 7;                //贴纸
    public static final int FILTER_SHAPE = 8;                  //变形
    public static final int FILTER_COLOR = 9;                  //滤镜
    public static final int FILTER_VIDEO_TEXTURE = 10;         //视频纹理
    public static final int FILTER_SHOW_FACE_POINTS = 11;      //显示脸部点数据，用于调试


    public static final int FILTER_DISPLAY = 12;               //显示最终图像、人脸框、水印
    public static final int FILTER_MAX_LAYER = 13;             //[0,12] ->size:13*/

}
