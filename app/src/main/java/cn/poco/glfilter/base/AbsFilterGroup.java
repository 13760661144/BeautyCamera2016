package cn.poco.glfilter.base;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zwq on 2016/12/08 15:15.<br/><br/>
 * 管理同一类型的Filter
 */
public abstract class AbsFilterGroup {

    protected Context mContext;
    protected int mWidth, mHeight;
    protected float mRenderScale = 1.0f;
    protected HashMap<Object, DefaultFilter> mFilterCache;//缓存

    protected boolean mFilterIsChange;
    protected final int INVALID_ID = -1;

    protected int mNewFilterId = INVALID_ID;
    protected String mNewFilterName;
    protected DefaultFilter mNewFilter;

    protected int mCurrentFilterId = INVALID_ID;
    protected String mCurrentFilterName;
    protected DefaultFilter mCurrentFilter;
    
    public AbsFilterGroup(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        mContext = context;
    }

    public void setRenderScale(float renderScale) {
        mRenderScale = renderScale;
    }

    public void setViewSize(int width, int height) {
        mWidth = width;
        mHeight = height;
        if (mFilterCache != null) {
            for (Map.Entry<Object, DefaultFilter> entry : mFilterCache.entrySet()) {
                DefaultFilter filter = entry.getValue();
                if (filter != null) {
                    filter.setRenderScale(mRenderScale);
                    filter.setViewSize(mWidth, mHeight);
                }
            }
        }
    }

    public boolean filterIsChange() {
        mFilterIsChange = false;
        if (mNewFilterId != INVALID_ID) {
            mFilterIsChange =  true;
        } else if (mNewFilterName != null) {
            mFilterIsChange = true;
        }
        return mFilterIsChange;
    }

    public DefaultFilter getFilter() {
        if (mNewFilterId != INVALID_ID) {
            mCurrentFilter = mNewFilter;
            mCurrentFilterId = mNewFilterId;

            mNewFilter = null;
            mNewFilterId = INVALID_ID;

        } else if (mNewFilterName != null) {
            mCurrentFilter = mNewFilter;
            mCurrentFilterName = mNewFilterName;

            mNewFilter = null;
            mNewFilterName = null;
        }
        return mCurrentFilter;
    }

    protected abstract boolean isValidId(int filterId);
    protected abstract DefaultFilter initFilterById(int filterId);

    private DefaultFilter initFilter(boolean isValid, int filterId) {
        if (isValid || (isValidId(filterId) && filterId != mCurrentFilterId)) {
            if (mFilterCache == null) {
                mFilterCache = new HashMap<Object, DefaultFilter>();
            }
            DefaultFilter filter = null;
            if (mFilterCache.containsKey(Integer.valueOf(filterId))) {
                filter = mFilterCache.get(Integer.valueOf(filterId));

            } else {
                try {
                    filter = initFilterById(filterId);
                } catch (Exception e) {
                    e.printStackTrace();
                    filter = null;
                }
                if (filter != null) {
                    filter.setRenderScale(mRenderScale);
                    filter.setViewSize(mWidth, mHeight);
                    mFilterCache.put(Integer.valueOf(filterId), filter);
                }
            }
            return filter;
        }
        return null;
    }

    public DefaultFilter preInitFilter(int filterId) {
        return initFilter(false, filterId);
    }

    public void changeFilterById(int filterId) {
        if (isValidId(filterId) && filterId != mCurrentFilterId) {
            mNewFilter = initFilter(true, filterId);
            if (mNewFilter == null) {
                mNewFilterId = 0;//没有时也不保留当前filter
            } else {
                mNewFilterId = filterId;
            }
        }
    }

    /*public void changeFilterById(int filterId) {
        if (isValidId(filterId) && filterId != mCurrentFilterId) {
            if (mFilterCache == null) {
                mFilterCache = new HashMap<Object, DefaultFilter>();
            }
            if (mFilterCache.containsKey(Integer.valueOf(filterId))) {
                mNewFilter = mFilterCache.get(Integer.valueOf(filterId));
                mNewFilterId = filterId;
            } else {
                try {
                    mNewFilter = initFilterById(filterId);
                } catch (Exception e) {
                    e.printStackTrace();
                    mNewFilter = null;
                }
                if (mNewFilter == null) {
                    mNewFilterId = 0;
                } else {
                    mNewFilter.setRenderScale(mRenderScale);
                    mNewFilter.setViewSize(mWidth, mHeight);
                    mNewFilterId = filterId;
                    mFilterCache.put(Integer.valueOf(filterId), mNewFilter);
//                    Log.i(TAG, "changeFilter mCurrentFilterId:" + mCurrentFilterId + ", mNewFilterId:" + mNewFilterId);
                }
            }
        }
    }*/

    protected abstract boolean isValidName(String filterName);
    protected abstract DefaultFilter initFilterByName(String filterName);

    public void changeFilterByName(String filterName) {
        if (isValidName(filterName) && filterName != mCurrentFilterName) {
            if (mFilterCache == null) {
                mFilterCache = new HashMap<Object, DefaultFilter>();
            }
            if (mFilterCache.containsKey(filterName)) {
                mNewFilter = mFilterCache.get(filterName);
                mNewFilterName = filterName;
            } else {
                try {
                    mNewFilter = initFilterByName(filterName);
                } catch (Exception e) {
                    e.printStackTrace();
                    mNewFilter = null;
                }
                if (mNewFilter == null) {
                    mNewFilterName = null;
                } else {
                    mNewFilter.setRenderScale(mRenderScale);
                    mNewFilter.setViewSize(mWidth, mHeight);
                    mNewFilterName = filterName;
                    mFilterCache.put(filterName, mNewFilter);
//                    Log.i(TAG, "changeFilter mCurrentFilterId:" + mCurrentFilterId + ", mNewFilterId:" + mNewFilterId);
                }
            }
        }
    }

    public void release(boolean doEglCleanup) {
        if (mFilterCache != null) {
            for (Map.Entry<Object, DefaultFilter> entry : mFilterCache.entrySet()) {
                DefaultFilter filter = entry.getValue();
                if (filter != null) {
                    if (doEglCleanup) {
                        filter.releaseProgram();
                    }
                    filter = null;
                }
            }
            mFilterCache.clear();
            mFilterCache = null;
        }
        mContext = null;
    }

}
