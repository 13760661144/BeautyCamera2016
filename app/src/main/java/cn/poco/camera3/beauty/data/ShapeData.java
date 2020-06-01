package cn.poco.camera3.beauty.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 脸型 参数值
 * 取值范围[0f, 100f]
 *
 * @author lmx
 *         Created by lmx on 2017-12-14.
 */

public class ShapeData implements Cloneable
{
    @IntDef({EYE_TYPE.CIRCLE_EYES, EYE_TYPE.OVAL_EYES})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EYE_TYPE
    {
        public int CIRCLE_EYES = 0; //圆眼
        public int OVAL_EYES = 1;   //椭眼
    }

    //是否是无脸型数据
    public boolean isNone = false;


    //大眼区分圆眼（0）、椭眼（1）
    public int eyes_type = EYE_TYPE.OVAL_EYES;

    public float thinFace_radius;
    public float thinFace;          //瘦脸
    public float littleFace_radius;
    public float littleFace;        //小脸
    public float shavedFace_radius;
    public float shavedFace;        //削脸
    public float bigEye_radius;
    public float bigEye;            //大眼
    public float shrinkNose_radius;
    public float shrinkNose;        //瘦鼻
    public float chin_radius;
    public float chin;              //下巴
    public float mouth_radius;
    public float mouth;             //嘴巴
    public float forehead_radius;
    public float forehead;          //额头
    public float cheekbones_radius;
    public float cheekbones;        //颧骨
    public float canthus_radius;
    public float canthus;           //眼角
    public float eyeSpan_radius;
    public float eyeSpan;           //眼距
    public float nosewing_radius;
    public float nosewing;          //鼻翼
    public float noseHeight_radius;
    public float noseHeight;        //鼻高
    public float overallHeight_radius;
    public float overallHeight;     //整体高度
    public float smile_radius;
    public float smile;             //微笑

    public float getThinFace_radius()
    {
        return thinFace_radius;
    }

    public void setThinFace_radius(float thinFace_radius)
    {
        this.thinFace_radius = thinFace_radius;
    }

    public float getLittleFace_radius()
    {
        return littleFace_radius;
    }

    public void setLittleFace_radius(float littleFace_radius)
    {
        this.littleFace_radius = littleFace_radius;
    }

    public float getShavedFace_radius()
    {
        return shavedFace_radius;
    }

    public void setShavedFace_radius(float shavedFace_radius)
    {
        this.shavedFace_radius = shavedFace_radius;
    }

    public float getBigEye_radius()
    {
        return bigEye_radius;
    }

    public void setBigEye_radius(float bigEye_radius)
    {
        this.bigEye_radius = bigEye_radius;
    }

    public float getShrinkNose_radius()
    {
        return shrinkNose_radius;
    }

    public void setShrinkNose_radius(float shrinkNose_radius)
    {
        this.shrinkNose_radius = shrinkNose_radius;
    }

    public float getChin_radius()
    {
        return chin_radius;
    }

    public void setChin_radius(float chin_radius)
    {
        this.chin_radius = chin_radius;
    }

    public float getMouth_radius()
    {
        return mouth_radius;
    }

    public void setMouth_radius(float mouth_radius)
    {
        this.mouth_radius = mouth_radius;
    }

    public float getThinFace()
    {
        return thinFace;
    }

    public void setThinFace(float thinFace)
    {
        this.thinFace = thinFace;
    }

    public float getLittleFace()
    {
        return littleFace;
    }

    public void setLittleFace(float littleFace)
    {
        this.littleFace = littleFace;
    }

    public float getShavedFace()
    {
        return shavedFace;
    }

    public void setShavedFace(float shavedFace)
    {
        this.shavedFace = shavedFace;
    }

    public float getBigEye()
    {
        return bigEye;
    }

    public void setBigEye(float bigEye)
    {
        this.bigEye = bigEye;
    }

    public float getShrinkNose()
    {
        return shrinkNose;
    }

    public void setShrinkNose(float shrinkNose)
    {
        this.shrinkNose = shrinkNose;
    }

    public float getChin()
    {
        return chin;
    }

    public void setChin(float chin)
    {
        this.chin = chin;
    }

    public float getMouth()
    {
        return mouth;
    }

    public void setMouth(float mouth)
    {
        this.mouth = mouth;
    }

    public float getForehead_radius()
    {
        return forehead_radius;
    }

    public void setForehead_radius(float forehead_radius)
    {
        this.forehead_radius = forehead_radius;
    }

    public float getForehead()
    {
        return forehead;
    }

    public void setForehead(float forehead)
    {
        this.forehead = forehead;
    }

    public float getCheekbones_radius()
    {
        return cheekbones_radius;
    }

    public void setCheekbones_radius(float cheekbones_radius)
    {
        this.cheekbones_radius = cheekbones_radius;
    }

    public float getCheekbones()
    {
        return cheekbones;
    }

    public void setCheekbones(float cheekbones)
    {
        this.cheekbones = cheekbones;
    }

    public float getCanthus_radius()
    {
        return canthus_radius;
    }

    public void setCanthus_radius(float canthus_radius)
    {
        this.canthus_radius = canthus_radius;
    }

    public float getCanthus()
    {
        return canthus;
    }

    public void setCanthus(float canthus)
    {
        this.canthus = canthus;
    }

    public float getEyeSpan_radius()
    {
        return eyeSpan_radius;
    }

    public void setEyeSpan_radius(float eyeSpan_radius)
    {
        this.eyeSpan_radius = eyeSpan_radius;
    }

    public float getEyeSpan()
    {
        return eyeSpan;
    }

    public void setEyeSpan(float eyeSpan)
    {
        this.eyeSpan = eyeSpan;
    }

    public float getNosewing_radius()
    {
        return nosewing_radius;
    }

    public void setNosewing_radius(float nosewing_radius)
    {
        this.nosewing_radius = nosewing_radius;
    }

    public float getNosewing()
    {
        return nosewing;
    }

    public void setNosewing(float nosewing)
    {
        this.nosewing = nosewing;
    }

    public float getNoseHeight_radius()
    {
        return noseHeight_radius;
    }

    public void setNoseHeight_radius(float noseHeight_radius)
    {
        this.noseHeight_radius = noseHeight_radius;
    }

    public float getNoseHeight()
    {
        return noseHeight;
    }

    public void setNoseHeight(float noseHeight)
    {
        this.noseHeight = noseHeight;
    }

    public float getOverallHeight_radius()
    {
        return overallHeight_radius;
    }

    public void setOverallHeight_radius(float overallHeight_radius)
    {
        this.overallHeight_radius = overallHeight_radius;
    }

    public float getOverallHeight()
    {
        return overallHeight;
    }

    public void setOverallHeight(float overallHeight)
    {
        this.overallHeight = overallHeight;
    }

    public float getSmile_radius()
    {
        return smile_radius;
    }

    public void setSmile_radius(float smile_radius)
    {
        this.smile_radius = smile_radius;
    }

    public float getSmile()
    {
        return smile;
    }

    public void setSmile(float smile)
    {
        this.smile = smile;
    }

    public boolean isNone()
    {
        return isNone;
    }

    public void setNone(boolean none)
    {
        isNone = none;
    }

    public int getEyes_type()
    {
        return eyes_type;
    }

    public void setEyes_type(@EYE_TYPE int eyes_type)
    {
        this.eyes_type = eyes_type;
    }

    public Object clone()
    {
        ShapeData data = null;
        try
        {
            data = (ShapeData) super.clone();
        }
        catch (Throwable tr)
        {
            tr.printStackTrace();
        }
        return data;
    }

}
