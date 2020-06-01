package cn.poco.camera3.beauty.data;

import cn.poco.camera3.beauty.STag;

/**
 * @author lmx
 *         Created by lmx on 2017-12-15.
 */

public class BeautyShapeDataUtils
{
    /**
     * 获取对应{@link ShapeDataType}的浮点值
     *
     * @param shapeInfo
     * @param type
     * @return
     */
    public static float GetData(ShapeInfo shapeInfo, @ShapeDataType int type)
    {
        float out = 0.0f;
        if (shapeInfo != null && shapeInfo.data != null)
        {
            switch (type)
            {
                case ShapeDataType.THINFACE:
                    out = shapeInfo.data.getThinFace();
                    break;
                case ShapeDataType.LITTLEFACE:
                    out = shapeInfo.data.getLittleFace();
                    break;
                case ShapeDataType.SHAVEDFACE:
                    out = shapeInfo.data.getShavedFace();
                    break;
                case ShapeDataType.BIGEYE:
                    out = shapeInfo.data.getBigEye();
                    break;
                case ShapeDataType.SHRINKNOSE:
                    out = shapeInfo.data.getShrinkNose();
                    break;
                case ShapeDataType.CHIN:
                    out = shapeInfo.data.getChin();
                    break;
                case ShapeDataType.MOUTH:
                    out = shapeInfo.data.getMouth();
                    break;
                case ShapeDataType.FOREHEAD:
                    out = shapeInfo.data.getForehead();
                    break;
                case ShapeDataType.CHEEKBONES:
                    out = shapeInfo.data.getCheekbones();
                    break;
                case ShapeDataType.CANTHUS:
                    out = shapeInfo.data.getCanthus();
                    break;
                case ShapeDataType.EYESPAN:
                    out = shapeInfo.data.getEyeSpan();
                    break;
                case ShapeDataType.NOSEWING:
                    out = shapeInfo.data.getNosewing();
                    break;
                case ShapeDataType.NOSEHEIGHT:
                    out = shapeInfo.data.getNoseHeight();
                    break;
                case ShapeDataType.OVERALLHEIGHT:
                    out = shapeInfo.data.getOverallHeight();
                    break;
                case ShapeDataType.SMILE:
                    out = shapeInfo.data.getSmile();
                    break;
                case ShapeDataType.UNSET:
                default:
                    break;
            }
        }
        return out;
    }

    /**
     * 更新对应{@link ShapeDataType}浮点值
     *
     * @param shapeInfo
     * @param type
     * @param data
     */
    public static void UpdateData(ShapeInfo shapeInfo, @ShapeDataType int type, float data)
    {
        if (shapeInfo != null && shapeInfo.data != null)
        {
            switch (type)
            {
                case ShapeDataType.THINFACE:
                    shapeInfo.data.setThinFace(data);
                    break;
                case ShapeDataType.LITTLEFACE:
                    shapeInfo.data.setLittleFace(data);
                    break;
                case ShapeDataType.SHAVEDFACE:
                    shapeInfo.data.setShavedFace(data);
                    break;
                case ShapeDataType.BIGEYE:
                    shapeInfo.data.setBigEye(data);
                    break;
                case ShapeDataType.SHRINKNOSE:
                    shapeInfo.data.setShrinkNose(data);
                    break;
                case ShapeDataType.CHIN:
                    shapeInfo.data.setChin(data);
                    break;
                case ShapeDataType.MOUTH:
                    shapeInfo.data.setMouth(data);
                    break;
                case ShapeDataType.FOREHEAD:
                    shapeInfo.data.setForehead(data);
                    break;
                case ShapeDataType.CHEEKBONES:
                    shapeInfo.data.setCheekbones(data);
                    break;
                case ShapeDataType.CANTHUS:
                    shapeInfo.data.setCanthus(data);
                    break;
                case ShapeDataType.EYESPAN:
                    shapeInfo.data.setEyeSpan(data);
                    break;
                case ShapeDataType.NOSEWING:
                    shapeInfo.data.setNosewing(data);
                    break;
                case ShapeDataType.NOSEHEIGHT:
                    shapeInfo.data.setNoseHeight(data);
                    break;
                case ShapeDataType.OVERALLHEIGHT:
                    shapeInfo.data.setOverallHeight(data);
                    break;
                case ShapeDataType.SMILE:
                    shapeInfo.data.setSmile(data);
                    break;
                case ShapeDataType.UNSET:
                default:
                    break;
            }
        }
    }

    /**
     * 根据{@link ShapeDataType} 判断对应的seek类型{@link STag.SeekBarType}
     *
     * @param type
     * @return
     */
    @STag.SeekBarType
    public static int GetSeekbarType(@ShapeDataType int type)
    {
        switch (type)
        {
            //瘦脸、小脸、削脸、颧骨、大眼、瘦鼻 单向seek
            //额头、眼角、眼距、鼻高、下巴、嘴型、鼻翼、嘴巴整体高度 双向seek
            case ShapeDataType.THINFACE:
            case ShapeDataType.LITTLEFACE:
            case ShapeDataType.SHAVEDFACE:
            case ShapeDataType.CHEEKBONES:
            case ShapeDataType.BIGEYE:
            case ShapeDataType.SHRINKNOSE:
                return STag.SeekBarType.SEEK_TAG_UNIDIRECTIONAL;
            case ShapeDataType.CHIN:
            case ShapeDataType.MOUTH:
            case ShapeDataType.FOREHEAD:
            case ShapeDataType.CANTHUS:
            case ShapeDataType.EYESPAN:
            case ShapeDataType.NOSEHEIGHT:
            case ShapeDataType.OVERALLHEIGHT:
            case ShapeDataType.NOSEWING:
                return STag.SeekBarType.SEEK_TAG_BIDIRECTIONAL;
            case ShapeDataType.UNSET:
            default:
                return STag.SeekBarType.SEEK_TAG_UNIDIRECTIONAL;
        }
    }

    /**
     * 获取双向seekbar对应[0,100]的真实值
     *
     * @param uiProgress seekbar返回的ui progress
     * @return
     */
    public static int GetBidirectionalRealSize(int uiProgress)
    {
        if (uiProgress >= 0 && uiProgress <= 100)
        {
            return (int) (50 + (0.5f * uiProgress));
        }
        else if (uiProgress < 0 && uiProgress >= -100)
        {
            return (int) (50 - (-0.5f * uiProgress));
        }
        return 0;
    }

    /**
     * 获取双向seekbar对应[-100,100]的UI值
     *
     * @param realProgress
     * @return
     */
    public static int GetBidirectionalUISize(int realProgress)
    {
        if (realProgress >= 50 && realProgress <= 100)
        {
            return (int) ((realProgress - 50) * 1f / 0.5f);
        }
        else if (realProgress < 50 && realProgress >= 0)
        {
            return (int) ((50 - realProgress) * 1f / -0.5f);
        }
        return 0;
    }


    /**
     * 获取底层真实的美肤（肤质、磨皮）值
     *
     * @param skinBeautyUiSize
     * @return
     */
    public static float GetRealSkinBeautySize(int skinBeautyUiSize)
    {
        return 10 + skinBeautyUiSize * 0.9f;
    }

    /**
     * 获取UI显示的美肤（肤质、磨皮）值
     * @param skinBeautyRealSize
     * @return
     */
    public static int GetUISkinBeautySize(float  skinBeautyRealSize)
    {
        return (int) ((skinBeautyRealSize - 10) / 0.9f);
    }

}
