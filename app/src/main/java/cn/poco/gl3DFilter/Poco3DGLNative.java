package cn.poco.gl3DFilter;

import java.util.ArrayList;
import cn.poco.gl3DFilter.AREffectDataFilter.*;
import cn.poco.image.PocoNativeFilter;

/**
 * Created by liujx on 2017/10/16.
 */

public class Poco3DGLNative {

    static
    {
        PocoNativeFilter.Init();
//        System.loadLibrary("PocoImage");
    }

    //创建模型缓存
    public static native long createRender3DId();
    /**
     更新每一帧检测到的人脸数据
     @param landmarks2d 2d特征数据
     @param landmarks3d 3d特征数据
     @param rotate 旋转[x, y, z]
     @param imageSize 图片尺寸
      @param focalLength 透视参数
     */
    public static native void updateLandmarksPair(long renderId, float []landmarks2d, float []landmarks3d, float []rotate, int []imageSize, int focalLength);

    /**
     设置动画当前的进度
     更应该认为这是专门为kPORSARModelMutualTypeDynamicFace开放的接口
     @param progressDescriptor 进度描述 [0.0- 1.0]
     */
    public static native void updateARAnimationProgress(long renderId, ArrayList<PORSARAnimationElementProgressDescriptor> progressDescriptor);

    /**
     设置模型的默认色彩

     @param Color  物体默认显示材质颜色 {0.0-1.0}  长度为4
     */
    public static native void configureObjDefaultColor(long renderId, float []Color);

    /**
     设置环境的光源

     @param lightPosition 光源使得物体更清晰  设备化大小1.0->buffersize    长度为3
     @param ligthStrength  光照强度
     */
    public static native void configureEnvironLightPosition(long renderId, float []lightPosition, float ligthStrength);


    /**
     配置渲染模型

     @param model 模型文件相关参数
     */
    public static native void configureRenderModel(long renderId, PORSARModelDescriptor model, int isEnable);

    /**
     播放动画，标识可以执行动画，如果是kPORSARModelMutualTypeAnimation类型，则播放骨骼动画,如果是kPORSARModelMutualTypeDynamicFace类型
     还需要配合updateARAnimationProgress函数

     isplay 大于0时触发
     */
    public static native void executeAnimationPlay(long renderId, int isplay);

    /*
    * 是否执行循环播放动画
    * */
    public static native void executeAnimationCirclePlay(long renderId, int isCircleplay);

    /*
    * 人脸检测有无时调用
    * isAvailable 小于等于0时渲染底图  否则渲染3D效果图
    * */
    public static native void executemodelViewMAvailable(long renderId, int isAvailable);

    /*
    * 触发渲染3D模型
    * isRenderObj 大于0时触发
    * */
    public static native void executedesiredRenderObj(long renderId, int isRenderObj);

    public static native void Render3DInit(long renderId);
    public static native void render3D(long renderId, int textureid, int bufferid);

    public static native String []getBitmapString(long renderId);
    public static native void configureBitmapId(long renderId, int bitmapid[], String name[]);

	/*
	*  释放数据
	*  切换和退出时调用
	*  切换无需释放标准人脸数据  isReleaseRefModel 设为0;
	*  退出的时候需要释放所有数据  isReleaseRefModel = 1;
	*/

    public static native void release3DmodelData(long renderId, int isReleaseRefModel);

}
