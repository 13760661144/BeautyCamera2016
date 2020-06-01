package cn.poco.camera;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author lmx
 *         Created by lmx on 2017/11/14.
 */
@IntDef({PatchDialogType.STEP_1_PATCH_TIPS,
        PatchDialogType.STEP_2_PATCH_CAMERA,
        PatchDialogType.STEP_3_PATCH_PICTURE,
        PatchDialogType.STEP_4_PATCH_FINISH,})
@Retention(RetentionPolicy.SOURCE)
public @interface PatchDialogType
{
    /**
     * 开始提示
     */
    int STEP_1_PATCH_TIPS = 1;

    /**
     * 镜头校正
     */
    int STEP_2_PATCH_CAMERA = 2;
    /**
     * 照片校正
     */
    int STEP_3_PATCH_PICTURE = 3;
    /**
     * 完成
     */
    int STEP_4_PATCH_FINISH = 4;
}
