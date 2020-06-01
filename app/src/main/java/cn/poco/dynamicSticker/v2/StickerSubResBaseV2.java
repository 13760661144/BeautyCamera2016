package cn.poco.dynamicSticker.v2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by zwq on 2017/04/05 10:46.<br/><br/>
 * 卡顿时跳帧处理
 */
public class StickerSubResBaseV2 {

    private static final String TAG = "bbb";
    private Context mContext;
    private int mLayer;//内部层级

    public ArrayList<StickerMeta> mStickerMetas;
    public ArrayList<StickerSpriteFrame> mFrames;
    public int mAllFrameCount;//总帧数
    public boolean mIsActionRes;
    public boolean mHasAction;
    public boolean mCanLoadBitmap = true;

    private int mSpriteIndex = -1;
    private int mLastSpriteIndex = -1;
    private boolean mAnimStart;
    private int mStickerMetaIndex;
    private int mNextStickerMetaIndex;
    private boolean mNeedLoadBmp;
    private StickerMeta mTempNextStickerMeta;
    private StickerMeta mStickerMeta;
    private StickerSpriteFrame mStickerSpriteFrame;

    private float mMinInterval = 0.05f;
    private float mInterval = -1;//单位：秒/s
    private long mFirstTime = -1;
    private int mCutInterval;
    private int mTimeCount = 0;

    private long mDrawStartTime;
    private long mDrawEndTime;

    private int mMaxJumpFrame = 5;//最多允许跳过5帧
    private int mResShowType;
    private int mAnimTriggerFrameIndex;
    private boolean mNeedFace;
    private boolean mNeedResetWhenLostFace;//人脸丢失后重置为第一帧数据
    private boolean mIsDebug;

    public void setContext(Context context) {
        mContext = context;
    }

    public void setLayer(int layer) {
        mLayer = layer;
    }

    public int getLayer() {
        return mLayer;
    }

    public int getSpriteIndex() {
        return mSpriteIndex;
    }

    public int getStickerMetaIndex() {
        return mStickerMetaIndex;
    }

    public void setMaxJumpFrame(int maxJumpFrame) {
        if (maxJumpFrame < 1) {
            maxJumpFrame = 1;
        }
        mMaxJumpFrame = maxJumpFrame;
    }

    /**
     * @param resShowType 0:正常不受动作（动画）控制，
     *                    1：有动作时显示/触发事件，
     *                    2：有动作时隐藏/取消事件，
     *                    3：有动画时显示/触发事件，
     *                    4：有动画时隐藏/取消事件
     *                    5：动画开始（第一帧）时触发事件
     */
    public void setResShowType(int resShowType) {
        mResShowType = resShowType;
    }

    public int getResShowType() {
        return mResShowType;
    }

    public void setAnimTriggerFrameIndex(int animTriggerFrameIndex) {
        mAnimTriggerFrameIndex = animTriggerFrameIndex;
    }

    public int getAnimTriggerFrameIndex() {
        return mAnimTriggerFrameIndex;
    }

    public void setNeedFace(boolean needFace) {
        mNeedFace = needFace;
    }

    public boolean isNeedFace() {
        return mNeedFace;
    }

    public boolean isNeedResetWhenLostFace() {
        return mNeedResetWhenLostFace;
    }

    public void setNeedResetWhenLostFace(boolean needResetWhenLostFace) {
        mNeedResetWhenLostFace = needResetWhenLostFace;
    }

    public void resetAll() {
        if (mStickerMetas != null) {
            for (StickerMeta meta : mStickerMetas) {
                if (meta != null) {
                    meta.mHasDecodeImg = false;
                }
            }
        }
        mCanLoadBitmap = true;
        mSpriteIndex = -1;
        mLastSpriteIndex = -1;
        mStickerMetaIndex = 0;
        mNextStickerMetaIndex = 0;

        mNeedLoadBmp = false;
        mTempNextStickerMeta = null;
        mStickerMeta = null;
        mStickerSpriteFrame = null;

        mInterval = -1;
        mFirstTime = -1;
        mCutInterval = 0;
        mHasAction = false;
        mAnimStart = false;

        mDrawStartTime = -1;
        mDrawEndTime = -1;
    }

    public void resetAll2() {
        mSpriteIndex = -1;
        mLastSpriteIndex = -1;

        if (mStickerMetaIndex > 0) {
            mStickerMetaIndex = 0;
            mNextStickerMetaIndex = 0;

            mNeedLoadBmp = false;
            mTempNextStickerMeta = null;
            mStickerMeta = null;
            mStickerSpriteFrame = null;
        }

        mInterval = -1;
        mFirstTime = -1;
        mCutInterval = 0;
        mHasAction = false;
        mAnimStart = false;

        mDrawStartTime = -1;
        mDrawEndTime = -1;
    }

    public void setDebug(boolean debug) {
        mIsDebug = debug;
    }

    public void setDrawStartTime(long drawStartTime) {
        mDrawStartTime = drawStartTime;
    }

    public long getDrawStartTime() {
        if (mDrawStartTime <= 0) {
            mDrawStartTime = System.currentTimeMillis();
        }
        return mDrawStartTime;
    }

    public void setDrawEndTime(long drawEndTime) {
        mDrawEndTime = drawEndTime;
    }

    public long getDrawEndTime() {
        if (mDrawEndTime <= 0) {
            mDrawEndTime = getDrawStartTime();
        }
        return mDrawEndTime;
    }

    public int checkIsNeedLoadNext(boolean loadNext) {
        return checkIsNeedLoadNext(loadNext, true);
    }

    /**
     * 计算是否要跳帧
     *
     * @param index
     * @param overTime
     * @return
     */
    private int[] calculateIndex(int count, int oriIndex, int index, int overTime, int cutTime) {
        if (count >= mMaxJumpFrame) {
            overTime = 0;
        }
        if (overTime > 0) {
            int nextIndex = (index + 1) % mAllFrameCount;
            StickerSpriteFrame mNextStickerSpriteFrame = mFrames.get(nextIndex);
            float duration = mInterval;
            if (mNextStickerSpriteFrame != null) {
                duration = mNextStickerSpriteFrame.getDuration();
            }
            int newOverTime = overTime - (int) (duration * 1000);
            if (nextIndex > ((oriIndex + mMaxJumpFrame) % mAllFrameCount)) {//最多跳过mMaxJumpFrame帧
                newOverTime = 0;
            }
            return calculateIndex(count + 1, oriIndex, nextIndex, newOverTime, overTime);
        } else if (overTime == 0) {
            index = (index + 1) % mAllFrameCount;
            if (count >= mMaxJumpFrame && index == oriIndex && mAllFrameCount > 1) {
                index = (oriIndex + 1) % mAllFrameCount;
            }
            cutTime = 0;
        }

        //不跳帧，继续绘制
        return new int[]{index, cutTime};
    }

    public int checkIsNeedLoadNext(boolean loadNext, boolean byTime) {
        if (mStickerMetas == null || mStickerMetas.isEmpty() || mFrames == null || mFrames.isEmpty())
            return mStickerMetaIndex;
        if (mAllFrameCount == 0) {
            mAllFrameCount = mFrames.size();
        }
        if (mAllFrameCount == 0) {
            return 0;
        }
        if (!loadNext && mSpriteIndex == -1 && mLastSpriteIndex == -1) {
            loadNext = true;
            byTime = false;
        }
        if (loadNext) {
            if (mInterval <= 0) {
                if (byTime && mSpriteIndex != -1 && mLastSpriteIndex == -1) {
                    mSpriteIndex = -1;
                }
                mSpriteIndex = (mSpriteIndex + 1) % mAllFrameCount;
            } else {
                if (mInterval <= 0) {
                    mInterval = mMinInterval;
                }
                int overTime = 0;
                if (mFirstTime > 0) {
                    overTime = (int) (getDrawEndTime() - mFirstTime) - (int) (mInterval * 1000);
                    if (overTime >= 500) {
                        overTime = 0;
                    }
                }
                if (mIsDebug) {
//                    Log.i(TAG, "checkIsNeedLoadNext: "+mSpriteIndex+", duration:"+mInterval+", overTime:"+overTime+", "+mFirstTime);
                }
                int[] fMsg = calculateIndex(0, mSpriteIndex, mSpriteIndex, overTime, overTime);
                if (mSpriteIndex != fMsg[0]) {
                    mCutInterval = fMsg[1];
                }
                mSpriteIndex = fMsg[0];
            }

            if (mIsActionRes) {
                if (mSpriteIndex < 2 && mHasAction && !mAnimStart) {//mSpriteIndex == 0
                    mHasAction = false;
                    mAnimStart = true;
                }
                if (!mAnimStart) {
                    mSpriteIndex = 0;
                    mStickerMetaIndex = 0;
                    mNextStickerMetaIndex = 0;
                    mInterval = 0;
                }
                if (mSpriteIndex == mAllFrameCount - 1 && !mHasAction && mAnimStart) {
                    mAnimStart = false;
                }
                if (mIsDebug) {
//                    Log.i(TAG, "checkIsNeedLoadNext: "+mSpriteIndex+", "+mHasAction+", "+mAnimStart+", "+mAllFrameCount);
                }
            }

            if (mSpriteIndex > -1 && mSpriteIndex < mAllFrameCount) {//有多张图片的情况时
                if (mSpriteIndex == 0 && mStickerMetaIndex == 0) {
                    mStickerMeta = mStickerMetas.get(mStickerMetaIndex);
                    if (mStickerMeta != null && mStickerMeta.mHasDecodeImg) {
                        mNeedLoadBmp = false;
                    } else {
                        mNeedLoadBmp = true;
                        mNextStickerMetaIndex = 0;
                    }
                }
                if (mIsDebug) {
//                    Log.i(TAG, "checkIsNeedLoadNext: " + mLastSpriteIndex + ", " + mSpriteIndex + ", " + mStickerMetaIndex + ", " + mNeedLoadBmp);
                }
                if (!mNeedLoadBmp && mStickerMeta != null && (mSpriteIndex == mStickerMeta.mStartIndex + mStickerMeta.mFrameCount || mSpriteIndex < mStickerMeta.mStartIndex)) {
                    mNextStickerMetaIndex = (mStickerMetaIndex + 1) % mStickerMetas.size();
                    StickerMeta nextStickerMeta = mStickerMetas.get(mNextStickerMetaIndex);
                    if (mIsDebug) {
//                        Log.i(TAG, "checkIsNeedLoadNext: <-11-> " + nextStickerMeta.mHasDecodeImg + ", " + mSpriteIndex + ", " + mStickerMetaIndex + ", " + mNextStickerMetaIndex);
                    }
                    if (nextStickerMeta != null && nextStickerMeta.mHasDecodeImg) {
                        mStickerMetaIndex = mNextStickerMetaIndex;
                        mStickerMeta = nextStickerMeta;
                        nextStickerMeta = null;
                        mNeedLoadBmp = false;
                    } else {
                        mNeedLoadBmp = true;
                    }
                }
                if (mIsActionRes && !mAnimStart && mSpriteIndex == 0 && mNeedLoadBmp && mSpriteIndex != mLastSpriteIndex) {
                    mNeedLoadBmp = false;
                }
                if (!mNeedLoadBmp && mSpriteIndex != mLastSpriteIndex) {
                    mStickerSpriteFrame = mFrames.get(mSpriteIndex);
                    mLastSpriteIndex = mSpriteIndex;
                    if (mIsDebug) {
//                        Log.i(TAG, "checkIsNeedLoadNext: <-22-> mNeedLoadBmp:" + mNeedLoadBmp + ", " + mSpriteIndex + ", " + mStickerMetaIndex + ", " + mLastSpriteIndex);
                    }
                    if (byTime) {
                        if (mStickerSpriteFrame == null) {
                            mInterval = mMinInterval;
                        } else {
                            mInterval = mStickerSpriteFrame.getDuration();
                        }
                        if (mCutInterval > 0) {
                            if (mIsDebug) {
//                                Log.i(TAG, "checkIsNeedLoadNext: "+mSpriteIndex+", duration:"+mInterval+", mCutInterval:"+mCutInterval);
                            }
                            mInterval -= (mCutInterval / 1000.0f);
                            mCutInterval = 0;
                        }
                        if (mInterval <= 0) {
                            mInterval = mMinInterval;
                        }
//                        mFirstTime = System.currentTimeMillis();
//                        mFirstTime = getDrawStartTime();
                        mFirstTime = getDrawEndTime();
                    }
                }
                if (mNeedLoadBmp) {
                    mSpriteIndex = mLastSpriteIndex;
                    if (mIsDebug) {
//                        Log.i(TAG, "checkIsNeedLoadNext: mNextStickerMeta not null " + mSpriteIndex + ", " + mStickerMetaIndex + ", " + mCanLoadBitmap + ", " + mLastSpriteIndex);
                    }
                }
            } else {
                mStickerMetaIndex = 0;
                mSpriteIndex = -1;
            }
        }
        if (byTime && mSpriteIndex > -1) {
            mTimeCount++;
        }
        if (mIsDebug) {
//            Log.i(TAG, "checkIsNeedLoadNext: "+mSpriteIndex+", "+mLastSpriteIndex+", "+mStickerMetaIndex+", "+mInterval);
        }
        if (mNextStickerMetaIndex != mStickerMetaIndex) {
            return mNextStickerMetaIndex;
        }
        return mStickerMetaIndex;
    }

    public void checkData() {
        if (mNextStickerMetaIndex == mStickerMetaIndex) {
            return;
        }
        if (mStickerMetas == null || mStickerMetas.isEmpty() || mFrames == null || mFrames.isEmpty()) {
            return;
        }
        mStickerMeta = mStickerMetas.get(mNextStickerMetaIndex);
        if (mStickerMeta != null) {
            mSpriteIndex = mStickerMeta.mStartIndex;
        }
        mStickerSpriteFrame = mFrames.get(mSpriteIndex);
        mStickerMetaIndex = mNextStickerMetaIndex;
        if (mIsDebug) {
//            Log.i(TAG, "checkData: " + mLastSpriteIndex + ", " + mSpriteIndex + ", " + mStickerMetaIndex + ", " + mNextStickerMetaIndex);
        }
    }

    public void loadNextMeta() {
        if (mTempNextStickerMeta != null) {
            mTempNextStickerMeta.mHasDecodeImg = true;
            mTempNextStickerMeta = null;
            mNeedLoadBmp = false;
        }
    }

    public Bitmap loadBitmap() {
        if (mContext == null || mStickerMetas == null || mStickerMetas.isEmpty()) {
            return null;
        }
        if (mTempNextStickerMeta == null) {
            mTempNextStickerMeta = mStickerMetas.get(mNextStickerMetaIndex);
        }
        if (mTempNextStickerMeta == null || mTempNextStickerMeta.mImage == null) {
            return null;
        }
        mTempNextStickerMeta.mHasDecodeImg = false;
        Bitmap bmp = getBitmap(mContext, mTempNextStickerMeta.mImage);
        if (mTempNextStickerMeta != null && bmp != null) {
            mTempNextStickerMeta.mImgWidth = bmp.getWidth();
            mTempNextStickerMeta.mImgHeight = bmp.getHeight();
        }
        return bmp;
    }

    public int getImgWidth() {
        if (mStickerMeta != null) {
            return mStickerMeta.mImgWidth;
        }
        return 0;
    }

    public int getImgHeight() {
        if (mStickerMeta != null) {
            return mStickerMeta.mImgHeight;
        }
        return 0;
    }

    public boolean canDraw() {
        if (mIsDebug) {
//            Log.i(TAG, "canDraw: "+mSpriteIndex);
        }
        if (mStickerSpriteFrame == null) {
            return false;
        } else if (mIsActionRes && !mAnimStart) {
            if ((mSpriteIndex == 0/* && mHasAction*/) || mSpriteIndex == mAllFrameCount - 1) {
                return true;
            }
            return false;
        } else if (mSpriteIndex == -1) {
            return false;
        }
        return true;
    }

    public boolean isDrawActionRes() {
        if (mIsActionRes && mSpriteIndex > 0) {
            return true;
        }
        return false;
    }

    public int getResDrawState() {
        if (mSpriteIndex <= mMaxJumpFrame) {//可能会有跳帧情况
            return 0;//start
        } else if (mSpriteIndex >= (mAllFrameCount - 1) - mMaxJumpFrame) {
            return 2;//stop
        } else {
            return 1;//drawing
        }
    }

    public int getActionResDrawState() {
        if (mIsActionRes) {
            if (mSpriteIndex < 1) {
                return 0;//wait
            } else if (mSpriteIndex < 3) {
                return 1;//start
            } else if (mSpriteIndex == mAllFrameCount - 1) {
                return 3;//stop
            } else {
                return 2;//drawing
            }
        }
        return -1;
    }

    public StickerSpriteFrame getSpriteFrame() {
        if (mIsDebug) {
//            Log.i(TAG, "getSpriteFrame: " + mSpriteIndex);
        }
        return mStickerSpriteFrame;
    }

    private Bitmap getBitmap(Context context, Object res) {
        if (res instanceof Integer) {
            return BitmapFactory.decodeResource(context.getResources(), (Integer) res);
        } else if (res instanceof String) {
            if (res == null || res.toString().trim().equals("")) {
                return null;
            }
            String path = res.toString();
            if (path.startsWith("/")) {
                return BitmapFactory.decodeFile(path);
            } else {
                if (context == null) {
                    return null;
                }
                Bitmap bitmap = null;
                InputStream inputStream = null;
                try {
                    inputStream = context.getAssets().open(path);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        inputStream = null;
                    }
                }
                return bitmap;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "StickerSubResBase{" +
                ", mLayer=" + mLayer +
                ", mAllFrameCount=" + mAllFrameCount +
                ", mIsActionRes=" + mIsActionRes +
                ", mHasAction=" + mHasAction +
                ", mStickerMetaIndex=" + mStickerMetaIndex +
                ", mSpriteIndex=" + mSpriteIndex +
                '}';
    }
}
