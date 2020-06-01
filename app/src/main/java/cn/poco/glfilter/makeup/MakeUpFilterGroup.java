package cn.poco.glfilter.makeup;

import android.content.Context;

import java.util.Map;

import cn.poco.glfilter.base.AbsFilterGroup;
import cn.poco.glfilter.base.DefaultFilter;

/**
 * Created by zwq on 2017/04/25 13:59.<br/><br/>
 */
public class MakeUpFilterGroup extends AbsFilterGroup {

    private int mCameraWidth;
    private int mCameraHeight;

    public MakeUpFilterGroup(Context context) {
        super(context);
    }

    @Override
    protected boolean isValidId(int filterId) {
        return filterId > 0 && filterId < 5;
    }

    @Override
    protected DefaultFilter initFilterById(int filterId) {
        DefaultFilter filter = null;
        switch (filterId) {
            case 1://眼妆
                filter = new MakeUpEyeFilter(mContext);
                break;
            case 2://腮红
                filter = new MakeUpBlushFilter(mContext);
                break;
            case 3://唇妆
                filter = new MakeUpLipFilter(mContext);
                break;
            case 4://唇妆高光
                filter = new MakeUpLipHighLightFilter(mContext);
                break;
            default:
                break;
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

    public MakeUpBaseFilter getFilter(int filterType) {
        changeFilterById(filterType);
        DefaultFilter filter = getFilter();
        if (filter != null && filter instanceof MakeUpBaseFilter) {
            ((MakeUpBaseFilter) filter).setCameraSize(mCameraWidth, mCameraHeight);
            return (MakeUpBaseFilter) filter;
        }
        return null;
    }

    public void setCameraSize(int width, int height) {
        if (width != mCameraWidth || height != mCameraHeight) {
            mCameraWidth = width;
            mCameraHeight = height;
            if (mFilterCache != null) {
                for (Map.Entry<Object, DefaultFilter> entry : mFilterCache.entrySet()) {
                    DefaultFilter filter = entry.getValue();
                    if (filter != null) {
                        if (filter instanceof MakeUpBaseFilter) {
                            ((MakeUpBaseFilter) filter).setCameraSize(mCameraWidth, mCameraHeight);
                        }
                    }
                }
            }
        }
    }

}
