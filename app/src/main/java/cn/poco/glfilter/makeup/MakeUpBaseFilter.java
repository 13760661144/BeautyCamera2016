package cn.poco.glfilter.makeup;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import java.util.Arrays;

import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.glfilter.base.GlUtil;
import cn.poco.image.PocoFace;
import cn.poco.resource.RealTimeMakeUpSubRes;

/**
 * Created by zwq on 2017/06/08 19:37.<br/><br/>
 */

public abstract class MakeUpBaseFilter extends DefaultFilter {

    protected boolean mResIsChange;
    protected RealTimeMakeUpSubRes mRealTimeMakeUpSubRes;
    protected int mTextureIdCount;
    protected int[] mTempTextureId;

    protected int mResWidth;
    protected int mResHeight;

    protected boolean mUseOtherFaceData;
    protected PocoFace mPocoFace;

    protected int mCameraWidth;
    protected int mCameraHeight;

    public MakeUpBaseFilter(Context context) {
        super(context);
    }

    @Override
    public int getTextureTarget() {
        return GLES20.GL_TEXTURE_2D;
    }

    @Override
    public void loadNextTexture(boolean load) {

    }

    public void setUseOtherFaceData(boolean useOtherFaceData) {
        mUseOtherFaceData = useOtherFaceData;
    }

    public void setFaceData(PocoFace pocoFace) {
        mPocoFace = pocoFace;
    }

    public boolean setMakeUpRes(RealTimeMakeUpSubRes realTimeMakeUpSubRes) {
        if (realTimeMakeUpSubRes == mRealTimeMakeUpSubRes) return false;

        mRealTimeMakeUpSubRes = realTimeMakeUpSubRes;
        if (mRealTimeMakeUpSubRes != null) {
            mResIsChange = true;
            if (mTextureIdCount < 1) {
                mTextureIdCount = 1;
            }
            if (mTempTextureId == null) {
                mTempTextureId = new int[mTextureIdCount];
            } else {
                Arrays.fill(mTempTextureId, 0);
            }
        }
        return true;
    }

    protected void initTask(int position, Object img) {
        if (img == null) return;
        MakeUpTextureTask task = new MakeUpTextureTask(this, position, img, 1, new MakeUpTextureTask.TaskCallback() {
            @Override
            public void onTaskCallback(int position, Bitmap bitmap) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    int textureId = GlUtil.createTexture(GLES20.GL_TEXTURE_2D, bitmap);
                    mResWidth = bitmap.getWidth();
                    mResHeight = bitmap.getHeight();
                    bitmap.recycle();
                    if (mTempTextureId == null) {
                        mTempTextureId = new int[mTextureIdCount];
                    }
                    mTempTextureId[position] = textureId;
                }
            }
        });
        addTaskToQueue(task);
        if (position == 0) {
            runTask();
        }
    }

    abstract public void loadTexture();

    public void setCameraSize(int cameraWidth, int cameraHeight) {
        mCameraWidth = cameraWidth;
        mCameraHeight = cameraHeight;
    }
}
