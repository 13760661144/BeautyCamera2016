package cn.poco.camera;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.CommonUtils;

/**
 * 磨皮（美颜）{@link TailorMadeType#MOPI}其数值为UI显示值（0-100），换算底层真实数值为（10-100），即10 + ui_size * 0.9 = 底层数值
 * {@link #GetRealMopiSize}
 *
 * @author lmx
 *         Created by lmx on 2017/7/5.
 * @deprecated
 */
public class TailorMadeConfig
{
    public int[] mParams;
    public int[] mDefParams;//默认参数

    public static int PARAM_SIZE = 6;
    public Context mContext;
    private @TailorMadeType
    int mCurrentType = TailorMadeType.MOPI;

    public boolean isSetChange = false;

    //约定顺序
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TailorMadeType.MOPI, TailorMadeType.SHOULIAN, TailorMadeType.DAYAN,
            TailorMadeType.SHOUBI, TailorMadeType.MEIYA, TailorMadeType.SKINBEAUTY})
    @Deprecated
    public static @interface TailorMadeType
    {
        int MOPI = 0; //磨皮
        int SHOULIAN = 1; //瘦脸
        int DAYAN = 2; //大眼
        int SHOUBI = 3;//瘦鼻
        int MEIYA = 4;//美牙
        int SKINBEAUTY = 5;//肤色
    }

    public TailorMadeConfig(@NonNull Context context)
    {
        this.mContext = context;
        CameraConfig.getInstance().initAll(context);
    }

    public void clear()
    {
        mContext = null;
        mDefParams = null;
        mParams = null;
        CameraConfig.getInstance().clearAll();
    }

    public TailorMadeConfig init()
    {
        // 如果是首次安装、后续版本升级，检查是否第一次使用，将开关设置为开
        if (isShowGuide())
        {
            saveSwitchState(true);
        }
        mParams = new int[PARAM_SIZE];
        mParams = getParams(mParams);

        mDefParams = new int[PARAM_SIZE];
        mDefParams = getDefParams(mDefParams);
        saveParams(true);
        return this;
    }

    public boolean isSwitchOn()
    {
        return CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.TailorMadeSwitchOn);
    }

    public boolean saveSwitchState(boolean on)
    {
        return CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.TailorMadeSwitchOn, on);
    }

    // 是否显示指引 相当于第一次使用
    public boolean isShowGuide()
    {
        return TagMgr.CheckTag(mContext, Tags.CAMERA_TAILOR_MADE_GUIDE_FLAG);
    }

    public boolean isShowNewLogo()
    {
        return TagMgr.CheckTag(mContext, Tags.CAMERA_TAILOR_MADE_NEW_FLAG);
    }

    public void setGuideTag()
    {
        TagMgr.SetTag(mContext, Tags.CAMERA_TAILOR_MADE_GUIDE_FLAG);
    }

    public void setNewLogoTag()
    {
        TagMgr.SetTag(mContext, Tags.CAMERA_TAILOR_MADE_NEW_FLAG);
    }

    public void saveTag()
    {
        TagMgr.Save(mContext);
    }

    public void updateTailorTag()
    {
        boolean guideFlag = isShowGuide();
        boolean newFlag = isShowNewLogo();

        if (guideFlag)
        {
            setGuideTag();
        }

        if (newFlag)
        {
            setNewLogoTag();
        }

        if (guideFlag || newFlag)
        {
            saveTag();
        }
    }

    public static float GetRealMopiSize(int mopiUiSize)
    {
        return 10 + mopiUiSize * 0.9f;
    }

    public static int GetUIMopiSize(float mopiRealSize)
    {
        return (int) ((mopiRealSize - 10) / 0.9f);
    }

    /**
     * @param params
     */
    public void set(int[] params)
    {
        this.mParams = params;
        isSetChange = true;
    }

    public void setDef(int[] defParams)
    {
        this.mDefParams = defParams;
    }

    /**
     * 获取调节杆滑动修改过参数值
     *
     * @return
     */
    public int[] get()
    {
        return mParams;
    }

    /**
     * 获取默认参数值
     *
     * @return
     */
    public int[] getDef()
    {
        return mDefParams;
    }

    /**
     * 获取当前镜头参数值
     *
     * @return
     */
    public int[] getCamera()
    {
        if (isSwitchOn())
        {
            return get();
        }
        return getDef();
    }

    /**
     * 从sp中更新
     *
     * @param params
     * @return
     */
    public int[] getParams(int[] params)
    {
        if (params == null || params.length != PARAM_SIZE)
        {
            params = new int[PARAM_SIZE];
        }

        //NOTE 版本升级4.1.3(206)，美形定制参数全部重置
        int i = TagMgr.GetTagIntValue(mContext, Tags.CAMERA_TAILOR_MADE_VERSION);
        int vc = CommonUtils.GetAppVerCode(mContext);
        boolean reset = false;
        if (i < 206)
        {
            reset = true;
        }
        TagMgr.SetTagValue(mContext, Tags.CAMERA_TAILOR_MADE_VERSION, String.valueOf(vc));

        if (reset)
        {
            params = getDefParams(null);
        }
        else
        {
            params[TailorMadeType.MOPI] = TagMgr.GetTagIntValue(mContext, Tags.CAMERA_TAILOR_MADE_BEAUTY, 55);
            params[TailorMadeType.SHOULIAN] = TagMgr.GetTagIntValue(mContext, Tags.CAMERA_TAILOR_MADE_SHOULIAN, 5);
            params[TailorMadeType.DAYAN] = TagMgr.GetTagIntValue(mContext, Tags.CAMERA_TAILOR_MADE_DAYAN, 10);
            params[TailorMadeType.SHOUBI] = TagMgr.GetTagIntValue(mContext, Tags.CAMERA_TAILOR_MADE_SHOUBI, 0);
            params[TailorMadeType.MEIYA] = TagMgr.GetTagIntValue(mContext, Tags.CAMERA_TAILOR_MADE_MEIYA, 0);
            params[TailorMadeType.SKINBEAUTY] = TagMgr.GetTagIntValue(mContext, Tags.CAMERA_TAILOR_MADE_SKINBEAUTY, 75);
        }
        return params;
    }

    private int[] getDefParams(int[] params)
    {
        if (params == null || params.length != PARAM_SIZE)
        {
            params = new int[PARAM_SIZE];
        }
        params[TailorMadeType.MOPI] = 55;
        params[TailorMadeType.SHOULIAN] = 5;
        params[TailorMadeType.DAYAN] = 10;

        params[TailorMadeType.SHOUBI] = 0;
        params[TailorMadeType.MEIYA] = 0;
        params[TailorMadeType.SKINBEAUTY] = 75;
        return params;
    }

    public void saveParams()
    {
        if (mParams != null && mParams.length == PARAM_SIZE)
        {
            if (isSetChange)
            {
                TagMgr.SetTagValue(mContext, Tags.CAMERA_TAILOR_MADE_BEAUTY, Integer.toString(mParams[TailorMadeType.MOPI]));
                TagMgr.SetTagValue(mContext, Tags.CAMERA_TAILOR_MADE_SHOULIAN, Integer.toString(mParams[TailorMadeType.SHOULIAN]));
                TagMgr.SetTagValue(mContext, Tags.CAMERA_TAILOR_MADE_DAYAN, Integer.toString(mParams[TailorMadeType.DAYAN]));
                TagMgr.SetTagValue(mContext, Tags.CAMERA_TAILOR_MADE_LIANGYAN, "0");//去除亮眼 祛眼袋 参数0
                TagMgr.SetTagValue(mContext, Tags.CAMERA_TAILOR_MADE_QUYANDAI, "0");//去除亮眼 祛眼袋 参数0
                TagMgr.SetTagValue(mContext, Tags.CAMERA_TAILOR_MADE_SHOUBI, Integer.toString(mParams[TailorMadeType.SHOUBI]));
                TagMgr.SetTagValue(mContext, Tags.CAMERA_TAILOR_MADE_MEIYA, Integer.toString(mParams[TailorMadeType.MEIYA]));
                TagMgr.SetTagValue(mContext, Tags.CAMERA_TAILOR_MADE_SKINBEAUTY, Integer.toString(mParams[TailorMadeType.SKINBEAUTY]));

                TagMgr.Save(mContext);
                isSetChange = false;
            }
        }
    }

    /**
     * @param commit true 强制保存
     */
    public void saveParams(boolean commit)
    {
        isSetChange = commit;
        saveParams();
    }


    /**
     * 取消更改参数，重置
     *
     * @return
     */
    public int[] resetParams()
    {
        return mParams = getParams(mParams);
    }

    public int getDef(int index)
    {
        if (mDefParams != null && mDefParams.length == PARAM_SIZE)
        {
            return mDefParams[index];
        }
        return 0;
    }

    /**
     * @param index {@link TailorMadeType}
     * @return
     */
    public int get(int index)
    {
        if (mParams != null && mParams.length == PARAM_SIZE)
        {
            return mParams[index];
        }
        else if (mDefParams != null && mDefParams.length == PARAM_SIZE)
        {
            return mDefParams[index];
        }
        return 0;
    }

    /**
     * @param index {@link TailorMadeType}
     * @param size
     */
    public void set(int index, int size)
    {
        if (mParams != null && mParams.length == PARAM_SIZE)
        {
            mParams[index] = size;
            isSetChange = true;
        }
    }
}
