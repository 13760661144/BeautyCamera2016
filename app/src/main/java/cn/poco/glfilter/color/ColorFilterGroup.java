package cn.poco.glfilter.color;

import android.content.Context;

import cn.poco.glfilter.base.AbsFilterGroup;
import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.resource.FilterRes;

/**
 * Created by zwq on 2016/12/08 15:31.<br/><br/>
 * 颜色滤镜管理
 */
public class ColorFilterGroup extends AbsFilterGroup {

    private float mWidthHeightRatio;
    private int mFlip;
    private int mUpCut;

    private final int mAmaniId = 2000000;//阿玛尼定制

    public ColorFilterGroup(Context context) {
        super(context);
    }

    @Override
    protected boolean isValidId(int filterId) {
        return (filterId >= 0 && filterId <= 11) || filterId == mAmaniId;
    }

    protected DefaultFilter initFilterById(int filterId) {
        DefaultFilter newFilter = null;
        switch (filterId) {
            //   0         1          2         3         4            5          6        7           8          9       10
            // "None", "Jasmine", "Camellia", "Rosa", "Lavender", "Sunflower", "Clover", "Peach", "Dandelion", "Lilac", "Tulip"
            case 0:
                break;
            case 1:
                newFilter = new JasmineFilter(mContext);
                break;
            case 2:
                newFilter = new CamelliaFilter(mContext);
                break;
            case 3:
                newFilter = new RosaFilter(mContext);
                break;
            case 4:
                newFilter = new LavenderFilter(mContext);
                break;
            case 5:
                newFilter = new SunflowerFilter(mContext);
                break;
            case 6:
                newFilter = new CloverFilter(mContext);
                break;
            case 7:
                newFilter = new PeachFilter(mContext);
                break;
            case 8:
                newFilter = new DandelionFilter(mContext);
                break;
            case 9:
                newFilter = new LilacFilter(mContext);
                break;
            case 10:
                newFilter = new TulipFilter(mContext);
                break;
            case 11:
                newFilter = new DynamicColorFilter(mContext);
                break;

            case mAmaniId:
                newFilter = new AmaniFilter(mContext);
                break;
            default:
                break;
        }
        return newFilter;
    }

    @Override
    protected boolean isValidName(String filterName) {
        return false;
    }

    @Override
    protected DefaultFilter initFilterByName(String filterName) {
        return null;
    }

    @Override
    public DefaultFilter getFilter() {
        if (mWidthHeightRatio > 0) {
            setRatioAndOrientation(mWidthHeightRatio, mFlip, mUpCut);
        }
        return super.getFilter();
    }

    private DefaultFilter getDynamicFilter() {
        DefaultFilter filter = null;
        if (mCurrentFilterId == 11 && mCurrentFilter != null) {
            filter = mCurrentFilter;
        } else if (mNewFilterId == 11 && mNewFilter != null) {
            filter = mNewFilter;
        }
        return filter;
    }

    public void setFilterData(FilterRes filterRes) {
        DefaultFilter filter = getDynamicFilter();
        if (filter != null) {
            ((DynamicColorFilter)filter).setFilterData(filterRes);
        }
    }

    public void setRatioAndOrientation(float width_height_ratio, int flip, int upCut) {
        mWidthHeightRatio = width_height_ratio;
        mFlip = flip;
        mUpCut = upCut;

        DefaultFilter filter = getDynamicFilter();
        if (filter != null) {
            ((DynamicColorFilter)filter).setRatioAndOrientation(width_height_ratio, flip, upCut);
        }
    }

    public void changeColorFilterRenderStyle(boolean reset) {
        DefaultFilter filter = null;
        if (mCurrentFilterId == mAmaniId && mCurrentFilter != null) {
            filter = mCurrentFilter;
        } else if (mNewFilterId == mAmaniId && mNewFilter != null) {
            filter = mNewFilter;
        }
        if (filter != null && filter instanceof AmaniFilter) {
            ((AmaniFilter)filter).changeRenderStyle(reset);
        }
    }

}
