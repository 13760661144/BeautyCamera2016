package cn.poco.glfilter.sticker;

import android.graphics.Bitmap;

import cn.poco.glfilter.base.AbsTask;
import cn.poco.glfilter.base.AbstractFilter;

/**
 * Created by zwq on 2018/01/18 16:38.<br/><br/>
 * 分屏色块
 */
public class SplitScreenTextureTask extends AbsTask {

    public interface TaskCallback {
        void onTaskCallback(int group, int index, Bitmap bitmap);
    }

    private AbstractFilter mFilter;
    private int mGroup;
    private int mIndex;
    private String mImgPath;
    private TaskCallback mTaskCallback;

    private Bitmap mBitmap;

    public SplitScreenTextureTask(AbstractFilter filter, int group, int index, String imgPath, TaskCallback taskCallback) {
        mFilter = filter;
        mGroup = group;
        mIndex = index;
        mImgPath = imgPath;
        mTaskCallback = taskCallback;
    }

    @Override
    public void runOnThread() {
        if (mFilter != null && mImgPath != null) {
            try {
                mBitmap = mFilter.getBitmap(mImgPath, 1);
            } catch (Throwable t) {
                t.printStackTrace();
                System.gc();
            }
        }
        mFilter = null;
        mImgPath = null;
    }

    @Override
    public void executeTaskCallback() {
        if (mTaskCallback != null) {
            mTaskCallback.onTaskCallback(mGroup, mIndex, mBitmap);
        }
    }

    public void clearAll() {
        super.clearAll();
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
        mFilter = null;
        mImgPath = null;
        mTaskCallback = null;
    }
}
