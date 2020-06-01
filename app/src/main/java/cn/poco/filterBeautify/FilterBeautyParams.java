package cn.poco.filterBeautify;

import android.content.Context;

import java.util.ArrayList;

import cn.poco.camera3.beauty.ShapeSPConfig;
import cn.poco.camera3.beauty.data.BaseShapeResMgr;
import cn.poco.camera3.beauty.data.BeautyData;
import cn.poco.camera3.beauty.data.BeautyInfo;
import cn.poco.camera3.beauty.data.BeautyResMgr;
import cn.poco.camera3.beauty.data.ShapeData;
import cn.poco.camera3.beauty.data.ShapeInfo;
import cn.poco.camera3.beauty.data.ShapeResMgr;
import cn.poco.camera3.beauty.data.ShapeSyncResMgr;
import cn.poco.camera3.beauty.data.SuperShapeData;
import cn.poco.image.CrazyShapeFilter;

/**
 * 静态美颜脸型处理所需要参数
 *
 * @author lmx
 *         Created by lmx on 2018-01-29.
 */

public class FilterBeautyParams
{
    public float skinBeautySize;      //美肤、肤质、磨皮
    public float skinTypeSize;        //肤色
    public float whitenTeethSize;     //美牙


    public int eye_type = ShapeData.EYE_TYPE.OVAL_EYES;

    public float circleEye_radius;
    public float circleEye;         //圆眼
    public float ovalEye_radius;
    public float ovalEye;           //椭圆眼
    public float thinFace_radius;
    public float thinFace;          //瘦脸
    public float littleFace_radius;
    public float littleFace;        //小脸
    public float shavedFace_radius;
    public float shavedFace;        //削脸
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

    //静态处理参数对象
    public CrazyShapeFilter.ShapeData shapeData = new CrazyShapeFilter.ShapeData();

    public FilterBeautyParams()
    {
        setDefault();
    }

    public int getEye_type()
    {
        return eye_type;
    }

    public void setEye_type(int eye_type)
    {
        this.eye_type = eye_type;
    }

    public float getSkinTypeSize()
    {
        return skinTypeSize;
    }

    public void setSkinTypeSize(float skinTypeSize)
    {
        this.skinTypeSize = skinTypeSize;
    }

    public float getWhitenTeethSize()
    {
        return whitenTeethSize;
    }

    public void setWhitenTeethSize(float whitenTeethSize)
    {
        this.whitenTeethSize = whitenTeethSize;
    }

    public float getSkinBeautySize()
    {
        return skinBeautySize;
    }

    public void setSkinBeautySize(float skinBeautySize)
    {
        this.skinBeautySize = skinBeautySize;
    }

    public float getCircleEye_radius()
    {
        return circleEye_radius;
    }

    public void setCircleEye_radius(float circleEye_radius)
    {
        this.circleEye_radius = circleEye_radius;
        this.shapeData.circleEyeRadius = circleEye_radius;
    }

    public float getCircleEye()
    {
        return circleEye;
    }

    public void setCircleEye(float circleEye)
    {
        this.circleEye = circleEye;
        this.shapeData.circleEyeStrength = circleEye;
    }

    public float getOvalEye_radius()
    {
        return ovalEye_radius;
    }

    public void setOvalEye_radius(float ovalEye_radius)
    {
        this.ovalEye_radius = overallHeight_radius;
        this.shapeData.ovalEyeRadius = ovalEye_radius;
    }

    public float getOvalEye()
    {
        return this.ovalEye;
    }

    public void setOvalEye(float ovalEye)
    {
        this.ovalEye = ovalEye;
        this.shapeData.ovalEyeStrength = ovalEye;
    }

    public float getThinFace_radius()
    {
        return this.thinFace_radius;
    }

    public void setThinFace_radius(float thinFace_radius)
    {
        this.thinFace_radius = thinFace_radius;
        this.shapeData.ThinFaceRadius = thinFace_radius;
    }

    public float getThinFace()
    {
        return this.thinFace;
    }

    public void setThinFace(float thinFace)
    {
        this.thinFace = thinFace;
        this.shapeData.ThinFaceStrength = thinFace;
    }

    public float getLittleFace_radius()
    {
        return littleFace_radius;
    }

    public void setLittleFace_radius(float littleFace_radius)
    {
        this.littleFace_radius = littleFace_radius;
        this.shapeData.LittleFaceRadius = littleFace_radius;
    }

    public float getLittleFace()
    {
        return littleFace;
    }

    public void setLittleFace(float littleFace)
    {
        this.littleFace = littleFace;
        this.shapeData.LittleFaceStrength = littleFace;
    }

    public float getShavedFace_radius()
    {
        return this.shavedFace_radius;
    }

    public void setShavedFace_radius(float shavedFace_radius)
    {
        this.shavedFace_radius = shavedFace_radius;
        this.shapeData.ShaveFaceRadius = shavedFace_radius;
    }

    public float getShavedFace()
    {
        return shavedFace;
    }

    public void setShavedFace(float shavedFace)
    {
        this.shavedFace = shavedFace;
        this.shapeData.ShaveFaceStrength = shavedFace;
    }

    public float getShrinkNose()
    {
        return this.shrinkNose;
    }

    public void setShrinkNose(float shrinkNose)
    {
        this.shrinkNose = shrinkNose;
        this.shapeData.noseShrinkStrength = shrinkNose;
    }

    public float getChin_radius()
    {
        return this.chin_radius;
    }

    public void setChin_radius(float chin_radius)
    {
        this.chin_radius = chin_radius;
        this.shapeData.chinRadius = chin_radius;
    }

    public float getChin()
    {
        return this.chin;
    }

    public void setChin(float chin)
    {
        this.chin = chin;
        this.shapeData.chinStrength = chin;
    }

    public float getMouth()
    {
        return mouth;
    }

    public void setMouth(float mouth)
    {
        this.mouth = mouth;
        this.shapeData.mouthStrength = mouth;
    }

    public float getForehead()
    {
        return this.forehead;
    }

    public void setForehead(float forehead)
    {
        this.forehead = forehead;
        this.shapeData.headStrength = forehead;
    }

    public float getCheekbones_radius()
    {
        return this.cheekbones_radius;
    }

    public void setCheekbones_radius(float cheekbones_radius)
    {
        this.cheekbones_radius = cheekbones_radius;
        this.shapeData.cheekBoneRadius = cheekbones_radius;
    }

    public float getCheekbones()
    {
        return this.cheekbones;
    }

    public void setCheekbones(float cheekbones)
    {
        this.cheekbones = cheekbones;
        this.shapeData.cheekBoneStrength = cheekbones;
    }

    public float getCanthus()
    {
        return canthus;
    }

    public void setCanthus(float canthus)
    {
        this.canthus = canthus;
        this.shapeData.eyeAngleSrength = canthus;
    }


    public float getEyeSpan()
    {
        return eyeSpan;
    }

    public void setEyeSpan(float eyeSpan)
    {
        this.eyeSpan = eyeSpan;
        this.shapeData.eyeDistSrength = eyeSpan;
    }


    public float getNosewing()
    {
        return this.nosewing;
    }

    public void setNosewing(float nosewing)
    {
        this.nosewing = nosewing;
        this.shapeData.noseWingStrength = nosewing;
    }


    public float getNoseHeight()
    {
        return this.noseHeight;
        // return this.shapeData.NoseLengthStrength;
    }

    public void setNoseHeight(float noseHeight)
    {
        this.noseHeight = noseHeight;
        this.shapeData.NoseLengthStrength = noseHeight;
    }


    public float getOverallHeight()
    {
        return this.overallHeight;
        // return this.shapeData.mouthLengthStrength;
    }

    public void setOverallHeight(float overallHeight)
    {
        this.overallHeight = overallHeight;
        this.shapeData.mouthLengthStrength = overallHeight;
    }

    public float getSmile_radius()
    {
        return this.smile_radius;
        // return this.shapeData.smileAngle;
    }

    public void setSmile_radius(float smile_radius)
    {
        this.smile_radius = smile_radius;
        this.shapeData.smileAngle = smile_radius;
    }

    public float getSmile()
    {
        return this.smile;
        // return this.shapeData.smileStrength;
    }

    public void setSmile(float smile)
    {
        this.smile = smile;
        this.shapeData.smileStrength = smile;
    }

    public float getShrinkNose_radius()
    {
        return this.shrinkNose_radius;
    }

    public void setShrinkNose_radius(float shrinkNose_radius)
    {
        this.shrinkNose_radius = shrinkNose_radius;
    }

    public float getMouth_radius()
    {
        return this.mouth_radius;
    }

    public void setMouth_radius(float mouth_radius)
    {
        this.mouth_radius = mouth_radius;
    }

    public float getForehead_radius()
    {
        return this.forehead_radius;
    }

    public void setForehead_radius(float forehead_radius)
    {
        this.forehead_radius = forehead_radius;
    }

    public float getCanthus_radius()
    {
        return this.canthus_radius;
    }

    public void setCanthus_radius(float canthus_radius)
    {
        this.canthus_radius = canthus_radius;
    }

    public float getEyeSpan_radius()
    {
        return eyeSpan_radius;
    }

    public void setEyeSpan_radius(float eyeSpan_radius)
    {
        this.eyeSpan_radius = eyeSpan_radius;
    }

    public float getNosewing_radius()
    {
        return nosewing_radius;
    }

    public void setNosewing_radius(float nosewing_radius)
    {
        this.nosewing_radius = nosewing_radius;
    }

    public float getNoseHeight_radius()
    {
        return noseHeight_radius;
    }

    public void setNoseHeight_radius(float noseHeight_radius)
    {
        this.noseHeight_radius = noseHeight_radius;
    }

    public float getOverallHeight_radius()
    {
        return overallHeight_radius;
    }

    public void setOverallHeight_radius(float overallHeight_radius)
    {
        this.overallHeight_radius = overallHeight_radius;
    }

    public CrazyShapeFilter.ShapeData getShapeData()
    {
        return shapeData;
    }

    public void setShapeData(CrazyShapeFilter.ShapeData shapeData)
    {
        this.shapeData = shapeData;
    }

    public void setDefault()
    {
        setSkinBeautySize(55.0f);
        setWhitenTeethSize(0.0f);
        setSkinTypeSize(75.0f);
    }

    public void getCamera(Context context)
    {
        ArrayList<BeautyInfo> beautyInfos = BeautyResMgr.getInstance().GetResArrByInfoFilter(context, null);
        if (beautyInfos != null && beautyInfos.size() > 0 && beautyInfos.get(0) != null) {
            BeautyData beautyData = beautyInfos.get(0).getData();
            SetBeautyData2Params(this, beautyData);
        }else {
            setDefault();
        }

        int shapeId = ShapeSPConfig.getInstance(context).getShapeId();
        ShapeSPConfig.getInstance(context).clearAll();
        ShapeInfo shapeInfo;
        if (shapeId == SuperShapeData.ID_MINE_SYNC) {
            shapeInfo = BaseShapeResMgr.HasItem(ShapeSyncResMgr.getInstance().SyncGetSdcardArr(context), shapeId);
        } else {
            shapeInfo = BaseShapeResMgr.HasItem(ShapeResMgr.getInstance().SyncGetSdcardArr(context), shapeId);
        }
        if (shapeInfo != null && shapeInfo.getData() != null) {
            SetShapeData2Params(this, shapeInfo.getData());
        }
    }

    public static void SetBeautyData2Params(FilterBeautyParams filterBeautyParams, BeautyData beautyData) {
        if (filterBeautyParams != null && beautyData != null)
        {
            //肤色
            filterBeautyParams.setSkinTypeSize(beautyData.getSkinType());
            //美牙
            filterBeautyParams.setWhitenTeethSize(beautyData.getWhitenTeeth());
            //美肤（磨皮）
            filterBeautyParams.setSkinBeautySize(beautyData.getSkinBeauty());
        }
    }

    public static void SetShapeData2Params(FilterBeautyParams filterBeautyParams, ShapeData shapeData)
    {
        if (filterBeautyParams != null && shapeData != null)
        {
            if (shapeData.getEyes_type() == ShapeData.EYE_TYPE.OVAL_EYES)
            {
                //椭圆眼
                filterBeautyParams.setEye_type(ShapeData.EYE_TYPE.OVAL_EYES);
                filterBeautyParams.setOvalEye(shapeData.getBigEye());
                filterBeautyParams.setOvalEye_radius(shapeData.getBigEye_radius());

                //清除圆眼数据
                filterBeautyParams.setCircleEye(0.0f);
                filterBeautyParams.setCircleEye_radius(0.0f);
            }
            else if (shapeData.getEyes_type() == ShapeData.EYE_TYPE.CIRCLE_EYES)
            {
                //圆眼
                filterBeautyParams.setEye_type(ShapeData.EYE_TYPE.CIRCLE_EYES);
                filterBeautyParams.setCircleEye(shapeData.getBigEye());
                filterBeautyParams.setCircleEye_radius(shapeData.getBigEye_radius());

                //清除椭眼数据
                filterBeautyParams.setOvalEye(0.0f);
                filterBeautyParams.setOvalEye_radius(0.0f);
            }
            //瘦脸
            filterBeautyParams.setThinFace(shapeData.getThinFace());
            filterBeautyParams.setThinFace_radius(shapeData.getThinFace_radius());

            //小脸
            filterBeautyParams.setLittleFace(shapeData.getLittleFace());
            filterBeautyParams.setLittleFace_radius(shapeData.getLittleFace_radius());

            //削脸
            filterBeautyParams.setShavedFace(shapeData.getShavedFace());
            filterBeautyParams.setShavedFace_radius(shapeData.getShavedFace_radius());

            //瘦鼻
            filterBeautyParams.setShrinkNose(shapeData.getShrinkNose());
            filterBeautyParams.setShrinkNose_radius(shapeData.getShrinkNose_radius());

            //下巴
            filterBeautyParams.setChin(shapeData.getChin());
            filterBeautyParams.setChin_radius(shapeData.getChin_radius());

            //嘴巴
            filterBeautyParams.setMouth(shapeData.getMouth());
            filterBeautyParams.setMouth_radius(shapeData.getMouth_radius());

            //额头
            filterBeautyParams.setForehead(shapeData.getForehead());
            filterBeautyParams.setForehead_radius(shapeData.getForehead_radius());

            //颧骨
            filterBeautyParams.setCheekbones(shapeData.getCheekbones());
            filterBeautyParams.setCheekbones_radius(shapeData.getCheekbones_radius());

            //眼角
            filterBeautyParams.setCanthus(shapeData.getCanthus());
            filterBeautyParams.setCanthus_radius(shapeData.getCanthus_radius());

            //眼距
            filterBeautyParams.setEyeSpan(shapeData.getEyeSpan());
            filterBeautyParams.setEyeSpan_radius(shapeData.getEyeSpan_radius());

            //鼻翼
            filterBeautyParams.setNosewing(shapeData.getNosewing());
            filterBeautyParams.setNosewing_radius(shapeData.getNosewing_radius());

            //鼻高
            filterBeautyParams.setNoseHeight(shapeData.getNoseHeight());
            filterBeautyParams.setNoseHeight_radius(shapeData.getNoseHeight_radius());

            //嘴巴整体高度
            filterBeautyParams.setOverallHeight(shapeData.getOverallHeight());
            filterBeautyParams.setOverallHeight_radius(shapeData.getOverallHeight_radius());

            //微笑
            filterBeautyParams.setSmile(shapeData.getSmile());
            filterBeautyParams.setSmile_radius(shapeData.getSmile_radius());
        }
    }
}
