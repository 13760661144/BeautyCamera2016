package cn.poco.glfilter.sticker;

import android.graphics.Bitmap;

import cn.poco.glfilter.base.AbsTask;
import cn.poco.glfilter.base.AbstractFilter;

/**
 * Created by zwq on 2017/10/30 16:38.<br/><br/>
 * 用于 decode 贴纸素材图片
 */
public class StickerARTextureTask extends AbsTask {

    public interface TaskCallback {
        void onTaskCallback(int type, int itemIndex, String imgName, Bitmap bitmap);
    }

    private AbstractFilter mFilter;
    private int mType;
    private int mItemIndex;
    private String mImgPath;
    private String mImgName;
    private TaskCallback mTaskCallback;

    private Bitmap mBitmap;

    /**
     * @param filter
     * @param type         0:普通的， 1:分组的
     * @param itemIndex
     * @param imgPath
     * @param imgName
     * @param taskCallback
     */
    public StickerARTextureTask(AbstractFilter filter, int type, int itemIndex, String imgPath, String imgName, TaskCallback taskCallback) {
        mFilter = filter;
        mType = type;
        mItemIndex = itemIndex;
        mImgPath = imgPath;
        mImgName = imgName;
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
            mTaskCallback.onTaskCallback(mType, mItemIndex, mImgName, mBitmap);
        }
    }

    public void clearAll() {
        super.clearAll();
        mImgName = null;
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
        mFilter = null;
        mImgPath = null;
        mTaskCallback = null;
    }

}
