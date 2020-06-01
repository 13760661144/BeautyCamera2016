package cn.poco.camera3.beauty.data;

import android.support.annotation.Size;

import java.io.Serializable;

/**
 * @author lmx
 *         Created by lmx on 2017-12-13.
 */

public class ShapeInfo extends BaseInfo implements Serializable
{
    //更新最新时间戳（单位：秒）
    public long update_time;

    //是否需要同步（true 需同步，false不需同步或同步已完成）
    public boolean isNeedSynchronize = false;

    //是否已修改，缓存到数据表下
    private boolean isModify = false;

    //是否是默认预设数据
    public boolean isDefaultData = true;

    public ShapeData data = new ShapeData();

    public ShapeData getData()
    {
        return data;
    }

    public void setData(ShapeData data)
    {
        this.data = data;
    }

    public long getUpdate_time()
    {
        return update_time;
    }

    /**
     * 更新时间（单位：秒）
     *
     * @param update_time
     */
    public void setUpdate_time(long update_time)
    {
        this.update_time = update_time;
    }

    public void setUpdate_time()
    {
        setUpdate_time(System.currentTimeMillis() / 1000L);
    }

    public boolean isNeedSynchronize()
    {
        return isNeedSynchronize;
    }

    public void setNeedSynchronize(boolean needSynchronize)
    {
        isNeedSynchronize = needSynchronize;
    }

    public boolean isModify()
    {
        return isModify;
    }

    public void setModify(boolean modify)
    {
        isModify = modify;
    }

    public boolean isDefaultData()
    {
        return isDefaultData;
    }

    public void setDefaultData(boolean defaultData)
    {
        isDefaultData = defaultData;
    }

    public void setParamsData(@Size(SuperShapeData.SHAPE_DATA_LENGTH) float[] defData)
    {
        if (this.data == null) this.data = new ShapeData();
        if (defData != null && defData.length == SuperShapeData.SHAPE_DATA_LENGTH)
        {
            this.data.setThinFace_radius(defData[SuperShapeData.Type.THINFACE_RADIUS]);
            this.data.setThinFace(defData[SuperShapeData.Type.THINFACE_STRENGTH]);
            this.data.setLittleFace_radius(defData[SuperShapeData.Type.LITTLEFACE_RADIUS]);
            this.data.setLittleFace(defData[SuperShapeData.Type.LITTLEFACE_STRENGTH]);
            this.data.setShavedFace_radius(defData[SuperShapeData.Type.SHAVEDFACE_RADIUS]);
            this.data.setShavedFace(defData[SuperShapeData.Type.SHAVEDFACE_STRENGTH]);
            this.data.setBigEye_radius(defData[SuperShapeData.Type.BIGEYE_RADIUS]);
            this.data.setBigEye(defData[SuperShapeData.Type.BIGEYE_STRENGTH]);
            this.data.setShrinkNose_radius(defData[SuperShapeData.Type.SHRINKNOSE_RADIUS]);
            this.data.setShrinkNose(defData[SuperShapeData.Type.SHRINKNOSE_STRENGTH]);
            this.data.setChin_radius(defData[SuperShapeData.Type.CHIN_RADIUS]);
            this.data.setChin(defData[SuperShapeData.Type.CHIN_STRENGTH]);
            this.data.setMouth_radius(defData[SuperShapeData.Type.MOUTH_RADIUS]);
            this.data.setMouth(defData[SuperShapeData.Type.MOUTH_STRENGTH]);
            this.data.setForehead_radius(defData[SuperShapeData.Type.FOREHEAD_RADIUS]);
            this.data.setForehead(defData[SuperShapeData.Type.FOREHEAD_STRENGTH]);
            this.data.setCheekbones_radius(defData[SuperShapeData.Type.CHEEKBONES_RADIUS]);
            this.data.setCheekbones(defData[SuperShapeData.Type.CHEEKBONES_STRENGTH]);
            this.data.setCanthus_radius(defData[SuperShapeData.Type.CANTHUS_RADIUS]);
            this.data.setCanthus(defData[SuperShapeData.Type.CANTHUS_STRENGTH]);
            this.data.setEyeSpan_radius(defData[SuperShapeData.Type.EYESPAN_RADIUS]);
            this.data.setEyeSpan(defData[SuperShapeData.Type.EYESPAN_STRENGTH]);
            this.data.setNosewing_radius(defData[SuperShapeData.Type.NOSEWING_RADIUS]);
            this.data.setNosewing(defData[SuperShapeData.Type.NOSEWING_STRENGTH]);
            this.data.setNoseHeight_radius(defData[SuperShapeData.Type.NOSEHEIGHT_RADIUS]);
            this.data.setNoseHeight(defData[SuperShapeData.Type.NOSEHEIGHT_STRENGTH]);
            this.data.setOverallHeight_radius(defData[SuperShapeData.Type.OVERALLHEIGHT_RADIUS]);
            this.data.setOverallHeight(defData[SuperShapeData.Type.OVERALLHEIGHT_STRENGTH]);
            this.data.setSmile_radius(defData[SuperShapeData.Type.SMILE_RADIUS]);
            this.data.setSmile(defData[SuperShapeData.Type.SMILE_STRENGTH]);
        }
    }

    public void setDefData()
    {
        setParamsData(SuperShapeData.GetDefData(this.id));
    }
}
