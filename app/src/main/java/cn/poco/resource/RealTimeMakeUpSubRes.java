package cn.poco.resource;

import java.util.Arrays;

/**
 * Created by zwq on 2017/06/08 17:27.<br/><br/>
 */

public class RealTimeMakeUpSubRes {

    public int mId;
    public String mName;
    public boolean mNeedReset;

    //眼睛
    public int mEyeLash;//睫毛素材
    public int mEyeLine;//眼线素材
    public int mEyeShadow;//眼影素材
    public int mEyeLashColor;//睫毛颜色 rgba
    public int mEyeLineColor;//眼线颜色 rgba
    public int mEyeShadowBlendType;//眼影混合模式
    public float mEyeShadowOpaqueness = 1.0f;//眼影不透明度 0.0 - 1.0

    //腮红
    public int mBlushLeft;//腮红左素材
    public int mBlushRight;//腮红右素材
    public int mBlushColor;//腮红颜色 rgba
    public float mBlushOpaqueness = 1.0f;//腮红不透明度 0.0 - 1.0

    //嘴唇
    public int mLip;//嘴唇素材
    public int mLipColor;//嘴唇颜色 rgba
    public float mLipOpaqueness = 1.0f;//嘴唇不透明度 0.0 - 1.0
    public int mLipCValue = 255;//C值0 - 255
    public int mLipAValue = 255;//A值0 - 255
    public int mLipBlendType;

    //注意图片的尺寸
    //嘴唇高光
    public int mLipHighLight;//嘴唇高光素材
    public float mLipHighLightOpaqueness = 1.0f;//嘴唇高光(强度)不透明度 0.0 - 1.0
    public int mLipHighLightBlendType = 1;

    public int mLipHighLightUp;//上嘴唇高光素材
    public float mLipHighLightUpOpaqueness = 1.0f;//嘴唇高光(强度)不透明度 0.0 - 1.0
    public int mLipHighLightUpBlendType = 1;

    public int mResIndex;
    private int[] mTextureIds;

    public void setTextureId(int index, int textureId) {
        if (mTextureIds == null) {
            mTextureIds = new int[8];
        }
        if (index >= 0 && index < mTextureIds.length) {
            mTextureIds[index] = textureId;
        }
    }

    public int getTextureId(int index) {
        if (mTextureIds != null && index >= 0 && index < mTextureIds.length) {
            return mTextureIds[index];
        }
        return 0;
    }

    public void resetTextureIds() {
        if (mTextureIds != null) {
            Arrays.fill(mTextureIds, 0);
        }
    }
}
