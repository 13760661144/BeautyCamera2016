package cn.poco.glfilter.composite;

import android.content.Context;

import cn.poco.glfilter.base.AbsFilterGroup;
import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.pgles.PGLNativeIpl;

/**
 * Created by zwq on 2017/04/25 13:59.<br/><br/>
 */
public class CompositeFilterGroup extends AbsFilterGroup {

    public CompositeFilterGroup(Context context) {
        super(context);
    }

    @Override
    protected boolean isValidId(int filterId) {
        switch (filterId) {
            //滤镜混合
            case 1:         //图层模式:无
            case 8:         //颜色加深
            case 9:         //颜色减淡
            case 20:        //变暗
            case 26:        //差值

            case 29:        //排除
            case 30:        //强光
            case 33:        //变亮
            case 34:        //线性光
            case 38:        //正片叠底

            case 41:        //叠加
            case 45:        //滤色
            case 46:        //柔光
            case 59:        //亮光
            case 61:        //线性减淡

                //以下为贴纸的混合
            case 101:         //图层模式:无
            case 108:         //颜色加深
            case 109:         //颜色减淡
            case 120:        //变暗
            case 126:        //差值

            case 129:        //排除
            case 130:        //强光
            case 133:        //变亮
            case 134:        //线性光
            case 138:        //正片叠底

            case 141:        //叠加
            case 145:        //滤色
            case 146:        //柔光
            case 159:        //亮光
            case 161:        //线性减淡
                return true;
            default:
                return false;
        }
    }

    @Override
    protected DefaultFilter initFilterById(int filterId) {
        DefaultFilter filter = null;
        int programHandle = 0;
        switch (filterId) {
            case 1:         //图层模式:无
                programHandle = PGLNativeIpl.loadNormalProgram();
                break;
            case 8:         //颜色加深
                programHandle = PGLNativeIpl.loadColorBurnProgram();
                break;
            case 9:         //颜色减淡
                programHandle = PGLNativeIpl.loadColorDodgeProgram();
                break;
            case 20:        //变暗
                programHandle = PGLNativeIpl.loadDarkenProgram();
                break;
            case 26:        //差值
                programHandle = PGLNativeIpl.loadDifferenceProgram();
                break;
            case 29:        //排除
                programHandle = PGLNativeIpl.loadExclusionProgram();
                break;
            case 30:        //强光
                programHandle = PGLNativeIpl.loadHardlightProgram();
                break;
            case 33:        //变亮
                programHandle = PGLNativeIpl.loadLightenProgram();
                break;
            case 34:        //线性光
                programHandle = PGLNativeIpl.loadLinearLightProgram();
                break;
            case 38:        //正片叠底
                programHandle = PGLNativeIpl.loadMultiplyProgram();
                break;
            case 41:        //叠加
                programHandle = PGLNativeIpl.loadOverlayProgram();
                break;
            case 45:        //滤色
                programHandle = PGLNativeIpl.loadScreenProgram();
                break;
            case 46:        //柔光
                programHandle = PGLNativeIpl.loadSoftLightProgram();
                break;
            case 59:        //亮光
                programHandle = PGLNativeIpl.loadVividLightProgram();
                break;
            case 61:        //线性减淡
                programHandle = PGLNativeIpl.loadLinearDodgeProgram();
                break;

            //以下为贴纸的混合
            case 101:         //图层模式:无
                programHandle = PGLNativeIpl.loadNormalProgramV2();
                break;
            case 108:         //颜色加深
                programHandle = PGLNativeIpl.loadColorBurnProgramV2();
                break;
            case 109:         //颜色减淡
                programHandle = PGLNativeIpl.loadColorDodgeProgramV2();
                break;
            case 120:        //变暗
                programHandle = PGLNativeIpl.loadDarkenProgramV2();
                break;
            case 126:        //差值
                programHandle = PGLNativeIpl.loadDifferenceProgramV2();
                break;
            case 129:        //排除
                programHandle = PGLNativeIpl.loadExclusionProgramV2();
                break;
            case 130:        //强光
                programHandle = PGLNativeIpl.loadHardlightProgramV2();
                break;
            case 133:        //变亮
                programHandle = PGLNativeIpl.loadLightenProgramV2();
                break;
            case 134:        //线性光
                programHandle = PGLNativeIpl.loadLinearLightProgramV2();
                break;
            case 138:        //正片叠底
                programHandle = PGLNativeIpl.loadMultiplyProgramV2();
                break;
            case 141:        //叠加
                programHandle = PGLNativeIpl.loadOverlayProgramV2();
                break;
            case 145:        //滤色
                programHandle = PGLNativeIpl.loadScreenProgramV2();
                break;
            case 146:        //柔光
                programHandle = PGLNativeIpl.loadSoftLightProgramV2();
                break;
            case 159:        //亮光
                programHandle = PGLNativeIpl.loadVividLightProgramV2();
                break;
            case 161:        //线性减淡
                programHandle = PGLNativeIpl.loadLinearDodgeProgramV2();
                break;

//            //以下为贴纸的混合
//            case 101:         //图层模式:无
//                programHandle = GlUtil.createProgram(mContext, R.raw.vertex_blend, R.raw.fragment_blend_normal);
//                break;
//            case 108:         //颜色加深
//                programHandle = GlUtil.createProgram(mContext, R.raw.vertex_blend, R.raw.fragment_blend_colorburn);
//                break;
//            case 109:         //颜色减淡
//                programHandle = GlUtil.createProgram(mContext, R.raw.vertex_blend, R.raw.fragment_blend_colordodge);
//                break;
//            case 120:        //变暗
//                programHandle = GlUtil.createProgram(mContext, R.raw.vertex_blend, R.raw.fragment_blend_darken);
//                break;
//            case 126:        //差值
//                programHandle = GlUtil.createProgram(mContext, R.raw.vertex_blend, R.raw.fragment_blend_difference);
//                break;
//            case 129:        //排除
//                programHandle = GlUtil.createProgram(mContext, R.raw.vertex_blend, R.raw.fragment_blend_exclusion);
//                break;
//            case 130:        //强光
//                programHandle = GlUtil.createProgram(mContext, R.raw.vertex_blend, R.raw.fragment_blend_hardlight);
//                break;
//            case 133:        //变亮
//                programHandle = GlUtil.createProgram(mContext, R.raw.vertex_blend, R.raw.fragment_blend_lighten);
//                break;
//            case 134:        //线性光
//                programHandle = GlUtil.createProgram(mContext, R.raw.vertex_blend, R.raw.fragment_blend_linearlight);
//                break;
//            case 138:        //正片叠底
//                programHandle = GlUtil.createProgram(mContext, R.raw.vertex_blend, R.raw.fragment_blend_multiply);
//                break;
//            case 141:        //叠加
//                programHandle = GlUtil.createProgram(mContext, R.raw.vertex_blend, R.raw.fragment_blend_overlay);
//                break;
//            case 145:        //滤色
//                programHandle = GlUtil.createProgram(mContext, R.raw.vertex_blend, R.raw.fragment_blend_screen);
//                break;
//            case 146:        //柔光
//                programHandle = GlUtil.createProgram(mContext, R.raw.vertex_blend, R.raw.fragment_blend_softlight);
//                break;
//            case 159:        //亮光
//                programHandle = GlUtil.createProgram(mContext, R.raw.vertex_blend, R.raw.fragment_blend_vividlight);
//                break;
//            case 161:        //线性减淡
//                programHandle = GlUtil.createProgram(mContext, R.raw.vertex_blend, R.raw.fragment_blend_lineardodge);
//                break;
            default:
                break;
        }
        if (programHandle > 0) {
            filter = new CompositeFilter(mContext, programHandle);
        }
        return filter;
    }

    @Override
    protected boolean isValidName(String filterName) {
        return false;
    }

    @Override
    protected DefaultFilter initFilterByName(String filterName) {
        return null;
    }

    public CompositeFilter setCompositeFilterData(CompositeData compositeData) {
        if (compositeData == null || compositeData.mAlpha == 0.0f || compositeData.mMaskTextureId <= 0) {
            return null;
        }
        changeFilterById(compositeData.mCompositeMode);
        DefaultFilter filter = getFilter();
        if (filter != null && filter instanceof CompositeFilter) {
            ((CompositeFilter) filter).setCompositeData(compositeData);
            return (CompositeFilter) filter;
        }
        return null;
    }

    /**
     * 贴纸混合有透视效果
     *
     * @param compositeData
     * @return
     */
    public CompositeFilter setCompositeFilterData2(CompositeData compositeData) {
        if (compositeData == null || compositeData.mAlpha == 0.0f || compositeData.mMaskTextureId <= 0) {
            return null;
        }
        changeFilterById(100 + compositeData.mCompositeMode);
        DefaultFilter filter = getFilter();
        if (filter != null && filter instanceof CompositeFilter) {
            ((CompositeFilter) filter).setCompositeData(compositeData);
            ((CompositeFilter) filter).setStickerMode(true);
            return (CompositeFilter) filter;
        }
        return null;
    }

}
