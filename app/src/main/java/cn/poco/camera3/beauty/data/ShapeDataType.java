package cn.poco.camera3.beauty.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author lmx
 *         Created by lmx on 2017-12-14.
 */
@IntDef({
        ShapeDataType.UNSET,
        ShapeDataType.THINFACE,
        ShapeDataType.LITTLEFACE,
        ShapeDataType.SHAVEDFACE,
        ShapeDataType.BIGEYE,
        ShapeDataType.SHRINKNOSE,
        ShapeDataType.CHIN,
        ShapeDataType.MOUTH,
        ShapeDataType.FOREHEAD,
        ShapeDataType.CHEEKBONES,
        ShapeDataType.CANTHUS,
        ShapeDataType.EYESPAN,
        ShapeDataType.NOSEWING,
        ShapeDataType.NOSEHEIGHT,
        ShapeDataType.OVERALLHEIGHT,
        ShapeDataType.SMILE
})
@Retention(RetentionPolicy.SOURCE)
public @interface ShapeDataType
{
    int UNSET = -1;
    int THINFACE = 0;       //瘦脸
    int LITTLEFACE = 1;     //小脸
    int SHAVEDFACE = 2;     //削脸
    int BIGEYE = 3;         //大眼
    int SHRINKNOSE = 4;     //瘦鼻
    int CHIN = 5;           //下巴
    int MOUTH = 6;          //嘴巴
    int FOREHEAD = 7;       //额头
    int CHEEKBONES = 8;     //颧骨
    int CANTHUS = 9;        //眼角
    int EYESPAN = 10;       //眼距
    int NOSEWING = 11;      //鼻翼
    int NOSEHEIGHT = 12;    //鼻高
    int OVERALLHEIGHT = 13; //嘴巴整体高度
    int SMILE = 14;         //微笑
}
