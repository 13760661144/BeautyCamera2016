package cn.poco.glfilter.beauty;

import android.content.Context;

import java.util.Map;

import cn.poco.glfilter.base.AbsFilterGroup;
import cn.poco.glfilter.base.DefaultFilter;

/**
 * Created by zwq on 2016/12/08 15:31.<br/><br/>
 * 美颜滤镜管理
 */
public class BeautyFilterGroup extends AbsFilterGroup {

    private int mCameraWidth;
    private int mCameraHeight;

    public BeautyFilterGroup(Context context) {
        super(context);
    }

    @Override
    protected boolean isValidId(int filterId) {
        return filterId >= 0 && filterId <= 4;
    }

    protected DefaultFilter initFilterById(int filterId) {
        DefaultFilter newFilter = null;
        switch (filterId) {
            case 0:
                newFilter = new BeautyFilter(mContext);
                break;
            case 1:
                newFilter = new BeautyFilterV2(mContext);
                if (newFilter != null) {
                    ((BeautyFilterV2) newFilter).setCameraSize(mCameraWidth, mCameraHeight);
                }
                break;
            case 2:
                newFilter = new BeautyStickerFilter(mContext);
                if (newFilter != null) {
                    ((BeautyStickerFilter) newFilter).setCameraSize(mCameraWidth, mCameraHeight);
                }
                break;
            case 3:
                newFilter = new SkinBeautyFilter(mContext);
                break;
            case 4:
                newFilter = new BeautyFilterV3(mContext);
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

    public void setCameraSize(int width, int height) {
        if (width != mCameraWidth || height != mCameraHeight) {
            mCameraWidth = width;
            mCameraHeight = height;
            if (mFilterCache != null) {
                for (Map.Entry<Object, DefaultFilter> entry : mFilterCache.entrySet()) {
                    DefaultFilter filter = entry.getValue();
                    if (filter != null) {
                        if (filter instanceof BeautyFilterV2) {
                            ((BeautyFilterV2) filter).setCameraSize(mCameraWidth, mCameraHeight);
                        } else if (filter instanceof BeautyStickerFilter) {
                            ((BeautyStickerFilter) filter).setCameraSize(mCameraWidth, mCameraHeight);
                        }
                    }
                }
            }
        }
    }

    public void setBeautyParams(float percent) {
        if (mCurrentFilter != null) {
            if (mCurrentFilter instanceof BeautyFilterV2) {
                ((BeautyFilterV2) mCurrentFilter).setBeautyParams(percent / 100.f);
            } else if (mCurrentFilter instanceof BeautyStickerFilter) {
                ((BeautyStickerFilter) mCurrentFilter).setBeautyParams(percent / 100.f);
            }
        }
    }

    public void setBeautyParams(float smooth, float white) {
        if (mCurrentFilter != null) {
           if (mCurrentFilter instanceof BeautyFilterV3) {
                ((BeautyFilterV3) mCurrentFilter).setBeautyParams((10 + smooth * 0.9f) / 100.0f, white / 100.f);
            }
        }
    }
}
