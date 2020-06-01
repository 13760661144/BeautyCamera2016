package cn.poco.camera3.beauty.data;

import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 1、官方默认脸型数据值，float取值范围[0f, 100f]<br/>
 * 2、以下预设数据由设计录入文档数据写入<br/>
 * 3、脸型数据变更，请更新{@link ShapeResMgr#NEW_JSON_VERSION} 和 {@link ShapeSyncResMgr#NEW_JSON_VERSION}
 * @author lmx
 *         Created by lmx on 2017-12-13.
 */

public class SuperShapeData
{
    @IntDef({
            Type.THINFACE_RADIUS,
            Type.THINFACE_STRENGTH,
            Type.LITTLEFACE_RADIUS,
            Type.LITTLEFACE_STRENGTH,
            Type.SHAVEDFACE_RADIUS,
            Type.SHAVEDFACE_STRENGTH,
            Type.BIGEYE_RADIUS,
            Type.BIGEYE_STRENGTH,
            Type.SHRINKNOSE_RADIUS,
            Type.SHRINKNOSE_STRENGTH,
            Type.CHIN_RADIUS,
            Type.CHIN_STRENGTH,
            Type.MOUTH_RADIUS,
            Type.MOUTH_STRENGTH,
            Type.FOREHEAD_RADIUS,
            Type.FOREHEAD_STRENGTH,
            Type.CHEEKBONES_RADIUS,
            Type.CHEEKBONES_STRENGTH,
            Type.CANTHUS_RADIUS,
            Type.CANTHUS_STRENGTH,
            Type.EYESPAN_RADIUS,
            Type.EYESPAN_STRENGTH,
            Type.NOSEWING_RADIUS,
            Type.NOSEWING_STRENGTH,
            Type.NOSEHEIGHT_RADIUS,
            Type.NOSEHEIGHT_STRENGTH,
            Type.OVERALLHEIGHT_RADIUS,
            Type.OVERALLHEIGHT_STRENGTH,
            Type.SMILE_RADIUS,
            Type.SMILE_STRENGTH
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type
    {
        //瘦脸
        public static final int THINFACE_RADIUS = 0;
        public static final int THINFACE_STRENGTH = 1;

        //小脸
        public static final int LITTLEFACE_RADIUS = 2;
        public static final int LITTLEFACE_STRENGTH = 3;

        //削脸
        public static final int SHAVEDFACE_RADIUS = 4;
        public static final int SHAVEDFACE_STRENGTH = 5;

        //大眼
        public static final int BIGEYE_RADIUS = 6;
        public static final int BIGEYE_STRENGTH = 7;

        //瘦鼻
        public static final int SHRINKNOSE_RADIUS = 8;
        public static final int SHRINKNOSE_STRENGTH = 9;

        //下巴
        public static final int CHIN_RADIUS = 10;
        public static final int CHIN_STRENGTH = 11;

        //嘴巴
        public static final int MOUTH_RADIUS = 12;
        public static final int MOUTH_STRENGTH = 13;

        //额头
        public static final int FOREHEAD_RADIUS = 14;
        public static final int FOREHEAD_STRENGTH = 15;

        //颧骨
        public static final int CHEEKBONES_RADIUS = 16;
        public static final int CHEEKBONES_STRENGTH = 17;

        //眼角
        public static final int CANTHUS_RADIUS = 18;
        public static final int CANTHUS_STRENGTH = 19;

        //眼距
        public static final int EYESPAN_RADIUS = 20;
        public static final int EYESPAN_STRENGTH = 21;

        //鼻翼
        public static final int NOSEWING_RADIUS = 22;
        public static final int NOSEWING_STRENGTH = 23;

        //鼻高
        public static final int NOSEHEIGHT_RADIUS = 24;
        public static final int NOSEHEIGHT_STRENGTH = 25;

        //嘴巴整体高度
        public static final int OVERALLHEIGHT_RADIUS = 26;
        public static final int OVERALLHEIGHT_STRENGTH = 27;

        //微笑
        public static final int SMILE_RADIUS = 28;
        public static final int SMILE_STRENGTH = 29;
    }

    public static final int SHAPE_DATA_LENGTH = 30;


    //TODO 内置脸型自定义ID
    public static final int ID_ZIRANXIUSHI        = 0x00000001;   //自然修饰
    public static final int ID_BABIGONGZHU        = 0x00000002;   //芭比公主
    public static final int ID_JINGZHIWANGHONG    = 0x00000003;   //精致网红
    public static final int ID_JIMENGSHAONV       = 0x00000004;   //激萌少女
    public static final int ID_MODENGNVWANG       = 0x00000005;   //摩登女王
    public static final int ID_DAIMENGTIANXIN     = 0x00000006;   //呆萌甜心
    public static final int ID_DUDUTONGYAN        = 0x00000007;   //嘟嘟童颜
    public static final int ID_XIAOLIANNVSHEN     = 0x00000008;   //小脸女神

    public static final int ID_NON_SHAPE          = 0x00000010;   //无

    //用户设定（app同步）
    public static final int ID_MINE_SYNC          = 0x00000120;   //我的（app同步）


    /**
     * 获取鼻翼底层所需真实数据
     *
     * @param in 设计录入数据
     * @return
     */
    public static float GetRealNoseWingStrength(float in)
    {
        return (in - 50.0f) * 2f;
    }


    /**
     * 自然修饰（椭眼款）
     * @return
     */
    public static final float[] GetZIranxiushi()
    {
        float[] params = new float[SHAPE_DATA_LENGTH];
        //大眼（椭眼）
        params[Type.BIGEYE_RADIUS]           = 4.8f;
        params[Type.BIGEYE_STRENGTH]         = 5.0f;

        //颧骨
        params[Type.CHEEKBONES_RADIUS]       = 0f;
        params[Type.CHEEKBONES_STRENGTH]     = 17.2f;

        //额头
        params[Type.FOREHEAD_RADIUS]         = 0f;
        params[Type.FOREHEAD_STRENGTH]       = 50.0f;

        //鼻子（瘦鼻）
        params[Type.SHRINKNOSE_RADIUS]       = 0f;
        params[Type.SHRINKNOSE_STRENGTH]     = 10.7f;

        //嘴巴
        params[Type.MOUTH_RADIUS]            = 0f;
        params[Type.MOUTH_STRENGTH]          = 60.9f;

        //微笑
        params[Type.SMILE_RADIUS]            = 50.0f;
        params[Type.SMILE_STRENGTH]          = 0f;

        //下巴
        params[Type.CHIN_RADIUS]             = 50.0f;
        params[Type.CHIN_STRENGTH]           = 50.0f;

        //削脸
        params[Type.SHAVEDFACE_RADIUS]       = 5.0f;
        params[Type.SHAVEDFACE_STRENGTH]     = 13.0f;

        //瘦脸
        params[Type.THINFACE_RADIUS]         = 6.1f;
        params[Type.THINFACE_STRENGTH]       = 23.0f;

        //小脸
        params[Type.LITTLEFACE_RADIUS]       = 6.9f;
        params[Type.LITTLEFACE_STRENGTH]     = 11.1f;

        //眼神（眼角）
        params[Type.CANTHUS_RADIUS]          = 0f;
        params[Type.CANTHUS_STRENGTH]        = 50.0f;

        //眼距
        params[Type.EYESPAN_RADIUS]          = 0f;
        params[Type.EYESPAN_STRENGTH]        = 50.0f;

        //嘴高
        params[Type.OVERALLHEIGHT_RADIUS]    = 0f;
        params[Type.OVERALLHEIGHT_STRENGTH]  = 50.0f;

        //鼻翼
        params[Type.NOSEWING_RADIUS]         = 0f;
        params[Type.NOSEWING_STRENGTH]       = 50.0f;

        //鼻高
        params[Type.NOSEHEIGHT_RADIUS]       = 0f;
        params[Type.NOSEHEIGHT_STRENGTH]     = 50.0f;

        return params;
    }

    /**
     * 芭比公主（椭眼款）
     * @return
     */
    public static final float[] GetBabigongzhu()
    {
        float[] params = new float[SHAPE_DATA_LENGTH];
        //大眼（椭眼）
        params[Type.BIGEYE_RADIUS]            = 0f;
        params[Type.BIGEYE_STRENGTH]          = 15.4f;

        //颧骨
        params[Type.CHEEKBONES_RADIUS]        = 0f;
        params[Type.CHEEKBONES_STRENGTH]      = 75.7f;

        //额头
        params[Type.FOREHEAD_RADIUS]          = 0f;
        params[Type.FOREHEAD_STRENGTH]        = 13.5f;

        //鼻子（瘦鼻）
        params[Type.SHRINKNOSE_RADIUS]        = 0f;
        params[Type.SHRINKNOSE_STRENGTH]      = 30.4f;

        //嘴巴
        params[Type.MOUTH_RADIUS]             = 0f;
        params[Type.MOUTH_STRENGTH]           = 60.7f;

        //微笑
        params[Type.SMILE_RADIUS]             = 0f;
        params[Type.SMILE_STRENGTH]           = 0f;

        //下巴
        params[Type.CHIN_RADIUS]              = 9.4f;
        params[Type.CHIN_STRENGTH]            = 79.8f;

        //削脸
        params[Type.SHAVEDFACE_RADIUS]        = 32.4f;
        params[Type.SHAVEDFACE_STRENGTH]      = 34.6f;

        //瘦脸
        params[Type.THINFACE_RADIUS]          = 33.0f;
        params[Type.THINFACE_STRENGTH]        = 27.6f;

        //小脸
        params[Type.LITTLEFACE_RADIUS]        = 7.4f;
        params[Type.LITTLEFACE_STRENGTH]      = 87.8f;

        //眼神（眼角）
        params[Type.CANTHUS_RADIUS]           = 0f;
        params[Type.CANTHUS_STRENGTH]         = 54.8f;

        //眼距
        params[Type.EYESPAN_RADIUS]           = 0f;
        params[Type.EYESPAN_STRENGTH]         = 36.1f;

        //嘴高
        params[Type.OVERALLHEIGHT_RADIUS]     = 0f;
        params[Type.OVERALLHEIGHT_STRENGTH]   = 56.7f;

        //鼻翼
        params[Type.NOSEWING_RADIUS]          = 0f;
        params[Type.NOSEWING_STRENGTH]        = 49.3f;

        //鼻高
        params[Type.NOSEHEIGHT_RADIUS]        = 0f;
        params[Type.NOSEHEIGHT_STRENGTH]      = 46.7f;
        return params;
    }

    /**
     * 精致网红（椭眼款）
     * @return
     */
    public static final float[] GetJingzhiWanghong()
    {
        float[] params = new float[SHAPE_DATA_LENGTH];
        //大眼（椭眼）
        params[Type.BIGEYE_RADIUS]            = 15.0f;
        params[Type.BIGEYE_STRENGTH]          = 29.8f;

        //颧骨
        params[Type.CHEEKBONES_RADIUS]        = 0f;
        params[Type.CHEEKBONES_STRENGTH]      = 57.2f;

        //额头
        params[Type.FOREHEAD_RADIUS]          = 0f;
        params[Type.FOREHEAD_STRENGTH]        = 33.5f;

        //鼻子（瘦鼻）
        params[Type.SHRINKNOSE_RADIUS]        = 0f;
        params[Type.SHRINKNOSE_STRENGTH]      = 31.5f;

        //嘴巴
        params[Type.MOUTH_RADIUS]             = 0f;
        params[Type.MOUTH_STRENGTH]           = 46.7f;

        //微笑
        params[Type.SMILE_RADIUS]             = 8.3f;
        params[Type.SMILE_STRENGTH]           = 8.9f;

        //下巴
        params[Type.CHIN_RADIUS]              = 3.5f;
        params[Type.CHIN_STRENGTH]            = 50.4f;

        //削脸
        params[Type.SHAVEDFACE_RADIUS]        = 58.3f;
        params[Type.SHAVEDFACE_STRENGTH]      = 29.6f;

        //瘦脸
        params[Type.THINFACE_RADIUS]          = 35.6f;
        params[Type.THINFACE_STRENGTH]        = 71.1f;

        //小脸
        params[Type.LITTLEFACE_RADIUS]        = 7.4f;
        params[Type.LITTLEFACE_STRENGTH]      = 36.7f;

        //眼神（眼角）
        params[Type.CANTHUS_RADIUS]           = 0f;
        params[Type.CANTHUS_STRENGTH]         = 58.5f;

        //眼距
        params[Type.EYESPAN_RADIUS]           = 0f;
        params[Type.EYESPAN_STRENGTH]         = 40.9f;

        //嘴高
        params[Type.OVERALLHEIGHT_RADIUS]     = 0f;
        params[Type.OVERALLHEIGHT_STRENGTH]   = 60.2f;

        //鼻翼
        params[Type.NOSEWING_RADIUS]          = 0f;
        params[Type.NOSEWING_STRENGTH]        = 64.1f;

        //鼻高
        params[Type.NOSEHEIGHT_RADIUS]        = 0f;
        params[Type.NOSEHEIGHT_STRENGTH]      = 50.2f;
        return params;
    }

    /**
     * 激萌少女（椭眼款式）
     * @return
     */
    public static final float[] GetJimengshaonv()
    {
        float[] params = new float[SHAPE_DATA_LENGTH];
        //大眼（椭眼）
        params[Type.BIGEYE_RADIUS]             = 0f;
        params[Type.BIGEYE_STRENGTH]           = 24.8f;

        //颧骨
        params[Type.CHEEKBONES_RADIUS]         = 0f;
        params[Type.CHEEKBONES_STRENGTH]       = 37f;

        //额头
        params[Type.FOREHEAD_RADIUS]           = 0f;
        params[Type.FOREHEAD_STRENGTH]         = 43.0f;

        //鼻子（瘦鼻）
        params[Type.SHRINKNOSE_RADIUS]         = 0f;
        params[Type.SHRINKNOSE_STRENGTH]       = 20.7f;

        //嘴巴
        params[Type.MOUTH_RADIUS]              = 0f;
        params[Type.MOUTH_STRENGTH]            = 55.9f;

        //微笑
        params[Type.SMILE_RADIUS]              = 50.0f;
        params[Type.SMILE_STRENGTH]            = 0f;

        //下巴
        params[Type.CHIN_RADIUS]               = 50.0f;
        params[Type.CHIN_STRENGTH]             = 55.0f;

        //削脸
        params[Type.SHAVEDFACE_RADIUS]         = 49.4f;
        params[Type.SHAVEDFACE_STRENGTH]       = 24.4f;

        //瘦脸
        params[Type.THINFACE_RADIUS]           = 35.7f;
        params[Type.THINFACE_STRENGTH]         = 25.4f;

        //小脸
        params[Type.LITTLEFACE_RADIUS]         = 37.6f;
        params[Type.LITTLEFACE_STRENGTH]       = 15.0f;

        //眼神（眼角）
        params[Type.CANTHUS_RADIUS]            = 0f;
        params[Type.CANTHUS_STRENGTH]          = 36.1f;

        //眼距
        params[Type.EYESPAN_RADIUS]            = 0f;
        params[Type.EYESPAN_STRENGTH]          = 50.0f;

        //嘴高
        params[Type.OVERALLHEIGHT_RADIUS]      = 0f;
        params[Type.OVERALLHEIGHT_STRENGTH]    = 50.0f;

        //鼻翼
        params[Type.NOSEWING_RADIUS]           = 0f;
        params[Type.NOSEWING_STRENGTH]         = 50.0f;

        //鼻高
        params[Type.NOSEHEIGHT_RADIUS]         = 0f;
        params[Type.NOSEHEIGHT_STRENGTH]       = 50.0f;
        return params;
    }

    /**
     * 摩登女王（椭眼款）
     * @return
     */
    public static final float[] GetModengnvwang()
    {
        float[] params = new float[SHAPE_DATA_LENGTH];
        //大眼（椭眼）
        params[Type.BIGEYE_RADIUS]              = 0f;
        params[Type.BIGEYE_STRENGTH]            = 11.3f;

        //颧骨
        params[Type.CHEEKBONES_RADIUS]          = 0f;
        params[Type.CHEEKBONES_STRENGTH]        = 37.0f;

        //额头
        params[Type.FOREHEAD_RADIUS]            = 0f;
        params[Type.FOREHEAD_STRENGTH]          = 43.0f;

        //鼻子（瘦鼻）
        params[Type.SHRINKNOSE_RADIUS]          = 0f;
        params[Type.SHRINKNOSE_STRENGTH]        = 25.9f;

        //嘴巴
        params[Type.MOUTH_RADIUS]               = 0f;
        params[Type.MOUTH_STRENGTH]             = 50.0f;

        //微笑
        params[Type.SMILE_RADIUS]               = 50f;
        params[Type.SMILE_STRENGTH]             = 0f;

        //下巴
        params[Type.CHIN_RADIUS]                = 50f;
        params[Type.CHIN_STRENGTH]              = 60.4f;

        //削脸
        params[Type.SHAVEDFACE_RADIUS]          = 0f;
        params[Type.SHAVEDFACE_STRENGTH]        = 35.2f;

        //瘦脸
        params[Type.THINFACE_RADIUS]            = 18.5f;
        params[Type.THINFACE_STRENGTH]          = 19.8f;

        //小脸
        params[Type.LITTLEFACE_RADIUS]          = 0f;
        params[Type.LITTLEFACE_STRENGTH]        = 7.0f;

        //眼神（眼角）
        params[Type.CANTHUS_RADIUS]             = 0f;
        params[Type.CANTHUS_STRENGTH]           = 65.6f;

        //眼距
        params[Type.EYESPAN_RADIUS]             = 0f;
        params[Type.EYESPAN_STRENGTH]           = 50.2f;

        //嘴高
        params[Type.OVERALLHEIGHT_RADIUS]       = 0f;
        params[Type.OVERALLHEIGHT_STRENGTH]     = 50.0f;

        //鼻翼
        params[Type.NOSEWING_RADIUS]            = 0f;
        params[Type.NOSEWING_STRENGTH]          = 50.0f;

        //鼻高
        params[Type.NOSEHEIGHT_RADIUS]          = 0f;
        params[Type.NOSEHEIGHT_STRENGTH]        = 50.0f;
        return params;
    }

    /**
     * 呆萌甜心（椭眼款）
     * @return
     */
    public static final float[] GetDamengtianxin()
    {
        float[] params = new float[SHAPE_DATA_LENGTH];
        //大眼（椭眼）
        params[Type.BIGEYE_RADIUS]              = 0f;
        params[Type.BIGEYE_STRENGTH]            = 20.6f;

        //颧骨
        params[Type.CHEEKBONES_RADIUS]          = 0f;
        params[Type.CHEEKBONES_STRENGTH]        = 33.1f;

        //额头
        params[Type.FOREHEAD_RADIUS]            = 0f;
        params[Type.FOREHEAD_STRENGTH]          = 43.0f;

        //鼻子（瘦鼻）
        params[Type.SHRINKNOSE_RADIUS]          = 0f;
        params[Type.SHRINKNOSE_STRENGTH]        = 20.9f;

        //嘴巴
        params[Type.MOUTH_RADIUS]               = 0f;
        params[Type.MOUTH_STRENGTH]             = 55.7f;

        //微笑
        params[Type.SMILE_RADIUS]               = 50f;
        params[Type.SMILE_STRENGTH]             = 0f;

        //下巴
        params[Type.CHIN_RADIUS]                = 50f;
        params[Type.CHIN_STRENGTH]              = 64.4f;

        //削脸
        params[Type.SHAVEDFACE_RADIUS]          = 49.4f;
        params[Type.SHAVEDFACE_STRENGTH]        = 10.7f;

        //瘦脸
        params[Type.THINFACE_RADIUS]            = 35.7f;
        params[Type.THINFACE_STRENGTH]          = 10.2f;

        //小脸
        params[Type.LITTLEFACE_RADIUS]          = 37.6f;
        params[Type.LITTLEFACE_STRENGTH]        = 10.2f;

        //眼神（眼角）
        params[Type.CANTHUS_RADIUS]             = 0f;
        params[Type.CANTHUS_STRENGTH]           = 50.0f;

        //眼距
        params[Type.EYESPAN_RADIUS]             = 0f;
        params[Type.EYESPAN_STRENGTH]           = 50.0f;

        //嘴高
        params[Type.OVERALLHEIGHT_RADIUS]       = 0f;
        params[Type.OVERALLHEIGHT_STRENGTH]     = 50.0f;

        //鼻翼
        params[Type.NOSEWING_RADIUS]            = 0f;
        params[Type.NOSEWING_STRENGTH]          = 50.0f;

        //鼻高
        params[Type.NOSEHEIGHT_RADIUS]          = 0f;
        params[Type.NOSEHEIGHT_STRENGTH]        = 50.0f;
        return params;
    }
    /**
     * 嘟嘟童颜（圆眼）
     */
    public static final float[] GetDudulianton()
    {
        float[] params = new float[SHAPE_DATA_LENGTH];
        //大眼（圆眼）
        params[Type.BIGEYE_RADIUS]               = 39.3f;
        params[Type.BIGEYE_STRENGTH]             = 32.2f;

        //颧骨
        params[Type.CHEEKBONES_RADIUS]           = 5.4f;
        params[Type.CHEEKBONES_STRENGTH]         = 38.5f;

        //额头
        params[Type.FOREHEAD_RADIUS]             = 0f;
        params[Type.FOREHEAD_STRENGTH]           = 20.6f;

        //鼻子（瘦鼻）
        params[Type.SHRINKNOSE_RADIUS]           = 0f;
        params[Type.SHRINKNOSE_STRENGTH]         = 56.7f;

        //嘴巴
        params[Type.MOUTH_RADIUS]                = 0f;
        params[Type.MOUTH_STRENGTH]              = 76.5f;

        //微笑
        params[Type.SMILE_RADIUS]                = 7.6f;
        params[Type.SMILE_STRENGTH]              = 10.7f;

        //下巴
        params[Type.CHIN_RADIUS]                 = 32.8f;
        params[Type.CHIN_STRENGTH]               = 34.8f;

        //削脸
        params[Type.SHAVEDFACE_RADIUS]           = 0f;
        params[Type.SHAVEDFACE_STRENGTH]         = 23.0f;

        //瘦脸
        params[Type.THINFACE_RADIUS]             = 0f;
        params[Type.THINFACE_STRENGTH]           = 0f;

        //小脸
        params[Type.LITTLEFACE_RADIUS]           = 100f;
        params[Type.LITTLEFACE_STRENGTH]         = 100f;

        //眼神（眼角）
        params[Type.CANTHUS_RADIUS]              = 0f;
        params[Type.CANTHUS_STRENGTH]            = 38.0f;

        //眼距
        params[Type.EYESPAN_RADIUS]              = 0f;
        params[Type.EYESPAN_STRENGTH]            = 39.3f;

        //嘴高
        params[Type.OVERALLHEIGHT_RADIUS]        = 0f;
        params[Type.OVERALLHEIGHT_STRENGTH]      = 90.6f;

        //鼻翼
        params[Type.NOSEWING_RADIUS]             = 0f;
        params[Type.NOSEWING_STRENGTH]           = 50.0f;

        //鼻高
        params[Type.NOSEHEIGHT_RADIUS]           = 0f;
        params[Type.NOSEHEIGHT_STRENGTH]         = 65.2f;
        return params;
    }

    /**
     * 小脸女神（椭眼款）
     * @return
     */
    public static final float[] GetXiaoliannvshen()
    {
        float[] params = new float[SHAPE_DATA_LENGTH];
        //大眼（椭眼）
        params[Type.BIGEYE_RADIUS]                = 0f;
        params[Type.BIGEYE_STRENGTH]              = 36.5f;

        //颧骨
        params[Type.CHEEKBONES_RADIUS]            = 100f;
        params[Type.CHEEKBONES_STRENGTH]          = 69.8f;

        //额头
        params[Type.FOREHEAD_RADIUS]              = 0f;
        params[Type.FOREHEAD_STRENGTH]            = 13.0f;

        //鼻子（瘦鼻）
        params[Type.SHRINKNOSE_RADIUS]            = 0f;
        params[Type.SHRINKNOSE_STRENGTH]          = 49.3f;

        //嘴巴
        params[Type.MOUTH_RADIUS]                 = 0f;
        params[Type.MOUTH_STRENGTH]               = 61.3f;

        //微笑
        params[Type.SMILE_RADIUS]                 = 9.6f;
        params[Type.SMILE_STRENGTH]               = 8.3f;

        //下巴
        params[Type.CHIN_RADIUS]                  = 9.1f;
        params[Type.CHIN_STRENGTH]                = 78.9f;

        //削脸
        params[Type.SHAVEDFACE_RADIUS]            = 18.1f;
        params[Type.SHAVEDFACE_STRENGTH]          = 55.6f;

        //瘦脸
        params[Type.THINFACE_RADIUS]              = 51.1f;
        params[Type.THINFACE_STRENGTH]            = 20.7f;

        //小脸
        params[Type.LITTLEFACE_RADIUS]            = 0f;
        params[Type.LITTLEFACE_STRENGTH]          = 100f;

        //眼神（眼角）
        params[Type.CANTHUS_RADIUS]               = 0f;
        params[Type.CANTHUS_STRENGTH]             = 50.0f;

        //眼距
        params[Type.EYESPAN_RADIUS]               = 0f;
        params[Type.EYESPAN_STRENGTH]             = 50.0f;

        //嘴高
        params[Type.OVERALLHEIGHT_RADIUS]         = 0f;
        params[Type.OVERALLHEIGHT_STRENGTH]       = 50.0f;

        //鼻翼
        params[Type.NOSEWING_RADIUS]              = 0f;
        params[Type.NOSEWING_STRENGTH]            = 50.0f;

        //鼻高
        params[Type.NOSEHEIGHT_RADIUS]            = 0f;
        params[Type.NOSEHEIGHT_STRENGTH]          = 50.0f;
        return params;
    }

    /**
     * 无脸型，参数均为0
     *
     * @return
     */
    @FloatRange(from = 0.0f, to = 100.0f)
    public static float[] GetNonShape()
    {
        float[] params = new float[SHAPE_DATA_LENGTH];
        //大眼
        params[Type.BIGEYE_RADIUS]                = 0f;
        params[Type.BIGEYE_STRENGTH]              = 0f;

        //颧骨
        params[Type.CHEEKBONES_RADIUS]            = 0f;
        params[Type.CHEEKBONES_STRENGTH]          = 0f;

        //额头
        params[Type.FOREHEAD_RADIUS]              = 0f;
        params[Type.FOREHEAD_STRENGTH]            = 50f;

        //鼻子（瘦鼻）
        params[Type.SHRINKNOSE_RADIUS]            = 0f;
        params[Type.SHRINKNOSE_STRENGTH]          = 0f;

        //嘴巴
        params[Type.MOUTH_RADIUS]                 = 0f;
        params[Type.MOUTH_STRENGTH]               = 50f;

        //微笑
        params[Type.SMILE_RADIUS]                 = 0f;
        params[Type.SMILE_STRENGTH]               = 0f;

        //下巴
        params[Type.CHIN_RADIUS]                  = 0f;
        params[Type.CHIN_STRENGTH]                = 50f;

        //削脸
        params[Type.SHAVEDFACE_RADIUS]            = 0f;
        params[Type.SHAVEDFACE_STRENGTH]          = 0f;

        //瘦脸
        params[Type.THINFACE_RADIUS]              = 0f;
        params[Type.THINFACE_STRENGTH]            = 0f;

        //小脸
        params[Type.LITTLEFACE_RADIUS]            = 0f;
        params[Type.LITTLEFACE_STRENGTH]          = 0f;

        //眼神（眼角）
        params[Type.CANTHUS_RADIUS]               = 0f;
        params[Type.CANTHUS_STRENGTH]             = 50f;

        //眼距
        params[Type.EYESPAN_RADIUS]               = 0f;
        params[Type.EYESPAN_STRENGTH]             = 50f;

        //嘴高
        params[Type.OVERALLHEIGHT_RADIUS]         = 0f;
        params[Type.OVERALLHEIGHT_STRENGTH]       = 50f;

        //鼻翼
        params[Type.NOSEWING_RADIUS]              = 0f;
        params[Type.NOSEWING_STRENGTH]            = 50.0f;

        //鼻高
        params[Type.NOSEHEIGHT_RADIUS]            = 0f;
        params[Type.NOSEHEIGHT_STRENGTH]          = 50f;
        return params;
    }

    /**
     * 我的脸型预设参数，参数均为0
     *
     * @return
     */
    @FloatRange(from = 0.0f, to = 100.0f)
    public static float[] GetDefultMineShape()
    {
        float[] params = new float[SHAPE_DATA_LENGTH];
        //大眼
        params[Type.BIGEYE_RADIUS]                = 0f;
        params[Type.BIGEYE_STRENGTH]              = 9.4f;

        //颧骨
        params[Type.CHEEKBONES_RADIUS]            = 0f;
        params[Type.CHEEKBONES_STRENGTH]          = 0f;

        //额头
        params[Type.FOREHEAD_RADIUS]              = 0f;
        params[Type.FOREHEAD_STRENGTH]            = 50.0f;

        //鼻子（瘦鼻）
        params[Type.SHRINKNOSE_RADIUS]            = 0f;
        params[Type.SHRINKNOSE_STRENGTH]          = 0f;

        //嘴巴
        params[Type.MOUTH_RADIUS]                 = 0f;
        params[Type.MOUTH_STRENGTH]               = 50.0f;

        //微笑
        params[Type.SMILE_RADIUS]                 = 50.0f;
        params[Type.SMILE_STRENGTH]               = 0f;

        //下巴
        params[Type.CHIN_RADIUS]                  = 50.0f;
        params[Type.CHIN_STRENGTH]                = 50.0f;

        //削脸
        params[Type.SHAVEDFACE_RADIUS]            = 50.0f;
        params[Type.SHAVEDFACE_STRENGTH]          = 5.7f;

        //瘦脸
        params[Type.THINFACE_RADIUS]              = 13.3f;
        params[Type.THINFACE_STRENGTH]            = 15.6f;

        //小脸
        params[Type.LITTLEFACE_RADIUS]            = 0f;
        params[Type.LITTLEFACE_STRENGTH]          = 0f;

        //眼神（眼角）
        params[Type.CANTHUS_RADIUS]               = 0f;
        params[Type.CANTHUS_STRENGTH]             = 50.0f;

        //眼距
        params[Type.EYESPAN_RADIUS]               = 0f;
        params[Type.EYESPAN_STRENGTH]             = 50.0f;

        //嘴高
        params[Type.OVERALLHEIGHT_RADIUS]         = 0f;
        params[Type.OVERALLHEIGHT_STRENGTH]       = 50.0f;

        //鼻翼
        params[Type.NOSEWING_RADIUS]              = 0f;
        params[Type.NOSEWING_STRENGTH]            = 50.0f;

        //鼻高
        params[Type.NOSEHEIGHT_RADIUS]            = 0f;
        params[Type.NOSEHEIGHT_STRENGTH]          = 50.0f;
        return params;
    }

    /**
     * 默认美颜 美牙 肤质参数
     *
     * @return
     */
    public static BeautyData GetDefBeautyData()
    {
        BeautyData data = new BeautyData();
        data.setSkinBeauty(55f);      //肤质、美肤、磨皮 底层60，UI55
        data.setWhitenTeeth(0);       //美牙 0
        data.setSkinType(75f);        //肤色75
        return data;
    }

    public static float[] GetDefData(int shapeId)
    {
        switch (shapeId)
        {
            case ID_ZIRANXIUSHI:
                return GetZIranxiushi();
            case ID_BABIGONGZHU:
                return GetBabigongzhu();
            case ID_JINGZHIWANGHONG:
                return GetJingzhiWanghong();
            case ID_JIMENGSHAONV:
                return GetJimengshaonv();
            case ID_MODENGNVWANG:
                return GetModengnvwang();
            case ID_DAIMENGTIANXIN:
                return GetDamengtianxin();
            case ID_DUDUTONGYAN:
                return GetDudulianton();
            case ID_XIAOLIANNVSHEN:
                return GetXiaoliannvshen();
            case ID_MINE_SYNC:
                return GetDefultMineShape();
            case ID_NON_SHAPE:
            default:
                return GetNonShape();
        }
    }
}
