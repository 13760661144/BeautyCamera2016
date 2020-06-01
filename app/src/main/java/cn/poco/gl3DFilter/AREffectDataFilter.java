package cn.poco.gl3DFilter;

import java.util.ArrayList;

/**
 * Created by liujx on 2017/9/30.
 */

public class AREffectDataFilter {

    public static class kPORSARModelMutualType{
        public static final int kPORSARModelMutualTypeUnknow = 0;
        public static final int kPORSARModelMutualTypeStill = 1;            //静态渲染
        public static final int kPORSARModelMutualTypeAnimation = 2;        //播放动画
        public static final int kPORSARModelMutualTypeDynamicFace = 3;      //动态表情
    }

    /**
     将一个模型所包含的连续的动作分割成几个单独的动作来表达
     */
    public static class PORSARAnimationElementDescriptor
    {
        public String mName;
        public int startFrameIndex;             //动作在整个模型动画序列中的起始帧
        public int endFrameIndex;              //动作在整个模型动画序列中的起结束帧
    }

    /**
     对渲染模型的描述
     */
    public static class PORSARModelDescriptor
    {
        public String mName;
        public String standardRenderObjPath;        //标准参考模型，如果是人脸贴纸，使用默认的标准人头
        public String arRenderObjPath;             //效果模型

        public int mModelRenderType;              //标注该模型的渲染类型  kPORSARModelMutualType
        public ArrayList<PORSARAnimationElementDescriptor> mAminationList;  //描述模型所包含的动画

    }

    /**
     描述当前动作的执行进度
     */
    public static class PORSARAnimationElementProgressDescriptor {
        public String mName;  //索引名字，应该与PORSARAnimationElementDescriptor相对应
        public float progress;          //帧数
    }

}
