package cn.poco.glfilter.sticker;

import android.graphics.Bitmap;

import cn.poco.dynamicSticker.v2.StickerSubRes;
import cn.poco.glfilter.base.AbsTask;

/**
 * Created by zwq on 2017/04/26 16:38.<br/><br/>
 * 用于 decode 贴纸素材图片
 */
public class StickerTextureTask extends AbsTask {

    public interface TaskCallback {
        public void onTaskCallback(String type, StickerSubRes stickerSubRes, int position, Bitmap bitmap);
    }

    private int mBmpPos;
    private StickerSubRes mStickerSubRes;
    private TaskCallback mTaskCallback;

    private String mKey;
    private Bitmap mBitmap;

    public StickerTextureTask(StickerSubRes stickerSubRes, int position, TaskCallback taskCallback) {
        mStickerSubRes = stickerSubRes;
        mBmpPos = position;
        mTaskCallback = taskCallback;
    }

    @Override
    public void runOnThread() {
        if (mStickerSubRes != null) {
            mKey = mStickerSubRes.getTypeName();
            if (mKey != null) {
                try {
                    mBitmap = mStickerSubRes.loadBitmap();
                } catch (Throwable t) {
                    t.printStackTrace();
                    System.gc();
                }
            }
        }
    }

    @Override
    public void executeTaskCallback() {
        if (mTaskCallback != null) {
            mTaskCallback.onTaskCallback(mKey, mStickerSubRes, mBmpPos, mBitmap);
        }
        mStickerSubRes = null;
    }

    public void clearAll() {
        super.clearAll();
        mKey = null;
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
        mStickerSubRes = null;
        mTaskCallback = null;
    }

}
