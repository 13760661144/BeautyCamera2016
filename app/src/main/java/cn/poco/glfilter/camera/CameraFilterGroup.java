package cn.poco.glfilter.camera;

import android.content.Context;
import android.support.annotation.FloatRange;

import java.util.Map;

import cn.poco.glfilter.base.AbsFilterGroup;
import cn.poco.glfilter.base.DefaultFilter;

/**
 * Created by zwq on 2016/12/08 15:31.<br/><br/>
 * 镜头预览滤镜管理
 */
public class CameraFilterGroup extends AbsFilterGroup {

    private static final String TAG = CameraFilterGroup.class.getName();
    private int mCameraWidth;
    private int mCameraHeight;
    private boolean mHasPreviewData;
    private float mViewRatio;
    private float mYuvDataRatio;

    public CameraFilterGroup(Context context) {
        super(context);
    }

    @Override
    public void setViewSize(int width, int height) {
        super.setViewSize(width, height);
        mViewRatio = (width > 0 && height > 0) ? height * 1.0f / width : 0.0f;
    }

    @Override
    protected boolean isValidId(int filterId) {
        return filterId >= 0 && filterId <= 2;
    }

    protected DefaultFilter initFilterById(int filterId) {
        DefaultFilter newFilter = null;
        switch (filterId) {
            case 0:
                newFilter = new CameraFilter(mContext);
                break;
            case 1://正常拍照
                newFilter = new CameraFilterV2(mContext);
                if (newFilter != null) {
                    ((CameraFilterV2) newFilter).setCameraSize(mCameraWidth, mCameraHeight);
                }
                break;
            case 2://动态贴纸
                newFilter = new CameraYUVFilter(mContext);
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
                    if (filter != null && filter instanceof CameraFilterV2) {
                        ((CameraFilterV2) filter).setCameraSize(mCameraWidth, mCameraHeight);
                    }
                }
            }
        }
    }

    /**
     * 是否有美颜功能
     *
     * @return
     */
    public boolean hasBeauty() {
        return mCurrentFilterId == 1;
    }

    public void setBeautyEnable(boolean enable) {
        if (mCurrentFilter != null && mCurrentFilter instanceof CameraFilterV2) {
            ((CameraFilterV2) mCurrentFilter).setBeautyEnable(enable);
        }
    }

    public void setBeautyParams(@FloatRange(from = 0.0f, to = 1.0f) float percent) {
        if (mCurrentFilter != null && mCurrentFilter instanceof CameraFilterV2) {
            ((CameraFilterV2) mCurrentFilter).setBeautyParams(percent);
        }
    }

    public boolean isYuvFilter() {
        return mCurrentFilterId == 2;
    }

    public boolean canSwitchToYuvFilter() {
        if (mNewFilterId == 2) {
            if (!mHasPreviewData || mYuvDataRatio == 0.0f || mYuvDataRatio != mViewRatio){
                return false;
            }
        }
        return true;
    }

    public void updatePreviewFrame(byte[] previewData, int width, int height) {
        mHasPreviewData = (previewData != null && width > 0 && height > 0);
        mYuvDataRatio = (width > 0 && height > 0) ? width * 1.0f / height : 0.0f;
        setCameraSize(width, height);
        if (mCurrentFilter != null && mCurrentFilter instanceof CameraYUVFilter) {
            ((CameraYUVFilter) mCurrentFilter).updatePreviewFrame(previewData, width, height);
        } else if (mNewFilter != null && mNewFilter instanceof CameraYUVFilter) {
            ((CameraYUVFilter) mNewFilter).updatePreviewFrame(previewData, width, height);
        }
    }

}
