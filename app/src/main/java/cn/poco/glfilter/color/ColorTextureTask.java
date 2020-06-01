package cn.poco.glfilter.color;

import android.graphics.Bitmap;

import cn.poco.glfilter.base.AbsTask;
import cn.poco.glfilter.base.AbstractFilter;

/**
 * Created by zwq on 2017/05/16 10:45.<br/><br/>
 * 用于 decode 滤镜相关的图片
 */
public class ColorTextureTask extends AbsTask {

    public interface TaskCallback {
        public void onTaskCallback(int position, Bitmap bitmap);
    }

    private int mBmpPos;
    private Object mImg;
    private int mSampleSize = 1;
    private AbstractFilter mFilter;
    private TaskCallback mTaskCallback;

    private Bitmap mBitmap;

    public ColorTextureTask(AbstractFilter filter, int pos, Object img, int sampleSize, TaskCallback taskCallback) {
        mFilter = filter;
        mBmpPos = pos;
        mImg = img;
        mSampleSize = sampleSize;
        mTaskCallback = taskCallback;
    }

    @Override
    public void runOnThread() {
        if (mImg != null && mFilter != null) {
            try {
                mBitmap = mFilter.getBitmap(mImg, mSampleSize);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        mImg = null;
        mFilter = null;
    }

    @Override
    public void executeTaskCallback() {
        if (mTaskCallback != null) {
            mTaskCallback.onTaskCallback(mBmpPos, mBitmap);
        }
    }

    @Override
    public void clearAll() {
        super.clearAll();
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
        mImg = null;
        mFilter = null;
        mTaskCallback = null;
    }
}
