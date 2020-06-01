package cn.poco.glfilter.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.support.annotation.RawRes;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public abstract class AbstractFilter implements IFilter {

    protected Context mContext;
    protected float mRenderScale = 1.0f;
    protected int mWidth, mHeight;
    protected boolean mIsRecord;
    protected boolean mUseFramebuffer;
    protected GLFramebuffer mGLFramebuffer;
    protected boolean mHasInitFramebuffer;

    protected boolean mFilterEnable = true;

    protected int mProgramHandle;
    protected int maPositionLoc;
    protected int maTextureCoordLoc;
    protected int mDefaultTextureId;

    protected ArrayList<AbsTask> mTaskList;

    public AbstractFilter(Context context) {
        mContext = context;
        mProgramHandle = createProgram(context);
        checkProgram();
    }

    public AbstractFilter(Context context, int programHandle) {
        mContext = context;
        mProgramHandle = programHandle;
        checkProgram();
    }

    public AbstractFilter(Context context, @RawRes int vertexSourceRawId, @RawRes int fragmentSourceRawId) {
        mContext = context;
        mProgramHandle = GlUtil.createProgram(context, vertexSourceRawId, fragmentSourceRawId);
        checkProgram();
    }

    public AbstractFilter(Context context, String vertexSource, String fragmentSource) {
        mContext = context;
        mProgramHandle = GlUtil.createProgram(vertexSource, fragmentSource);
        checkProgram();
    }

    protected void checkProgram() {
        if (mProgramHandle == 0) {
            throw new RuntimeException("Unable to create program");
        }
        getGLSLValues();
    }

    protected abstract int createProgram(Context context);

    public int getBitmapTextureId(Object res) {
        return getBitmapTextureId(res, 1);
    }

    public int getBitmapTextureId(Object res, int sampleSize) {
        Bitmap maskTexture = getBitmap(res, sampleSize);
        if (maskTexture != null && !maskTexture.isRecycled()) {
            int textureId = GlUtil.createTexture(GLES20.GL_TEXTURE_2D, maskTexture);
            maskTexture.recycle();
            return textureId;
        }
        return 0;
    }

    public Bitmap getBitmap(Object res, int sampleSize) {
        if (res == null) return null;
        if (sampleSize <= 1) {
            sampleSize = 1;
        } else if (sampleSize <= 2) {
            sampleSize = 2;
        } else if (sampleSize <= 4) {
            sampleSize = 4;
        } else if (sampleSize <= 8) {
            sampleSize = 8;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        Bitmap maskTexture = null;
        try {
            if (res instanceof Integer) {
                maskTexture = BitmapFactory.decodeResource(mContext.getResources(), (Integer) res, options);
            } else if (res instanceof String) {
                maskTexture = BitmapFactory.decodeFile((String) res, options);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        options = null;
        return maskTexture;
    }

    public void addTaskToQueue(AbsTask task) {
        if (task == null) return;
        if (mTaskList == null) {
            mTaskList = new ArrayList<>();
        }
        synchronized (mTaskList) {
            if (mTaskList.isEmpty()) {
                task.start();
            }
            mTaskList.add(task);
        }
    }

    public void clearTask() {
        if (mTaskList != null) {
            for (int i = 0; i < mTaskList.size(); i++) {
                AbsTask task = mTaskList.get(i);
                if (task != null) {
                    task.clearAll();
                }
                task = null;
            }
            mTaskList.clear();
        }
    }

    public int getTaskSize() {
        if (mTaskList == null || mTaskList.isEmpty()) {
            return 0;
        }
        return mTaskList.size();
    }

    public void runTask() {
        if (mTaskList == null || mTaskList.isEmpty()) {
            return;
        }
        synchronized (mTaskList) {
            AbsTask task = mTaskList.get(0);
            if (task == null) {
                return;
            }
            task.start();
            if (task.isFinish()) {
                task.executeTaskCallback();
                task.clearAll();
                mTaskList.remove(task);

                if (mTaskList.isEmpty()) {
                    return;
                }
                task = mTaskList.get(0);
                if (task != null) {
                    task.start();
                }
            }
        }
    }

    protected void getGLSLValues() {
        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "aPosition");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "aTextureCoord");
    }

    protected void useProgram() {
        GLES20.glUseProgram(mProgramHandle);
    }

    @Override
    public void setViewSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    @Override
    public void setRenderScale(float renderScale) {
        mRenderScale = renderScale;
    }

    @Override
    public void setDrawType(boolean isRecord) {
        mIsRecord = isRecord;
    }

    @Override
    public void resetFilterData() {

    }

    public void setFilterEnable(boolean enable) {
        mFilterEnable = enable;
    }

    public boolean isFilterEnable() {
        return mFilterEnable;
    }

    /**
     * 是否使用绑定的Framebuffer
     *
     * @param use
     */
    public void setUseFramebuffer(boolean use) {
        mUseFramebuffer = use;
    }

    public void setFramebuffer(GLFramebuffer framebuffer) {
        mGLFramebuffer = framebuffer;
        if (mGLFramebuffer != null) {
            mHasInitFramebuffer = true;
        }
    }

    public GLFramebuffer getFramebuffer() {
        return mGLFramebuffer;
    }

    public boolean hasInitFramebuffer() {
        if (mGLFramebuffer == null && mWidth > 0 && mHeight > 0) {
            mGLFramebuffer = new GLFramebuffer(mWidth, mHeight);
            mHasInitFramebuffer = true;
        }
        return mHasInitFramebuffer;
    }

    public boolean bindFramebuffer(boolean clear) {
        if (mGLFramebuffer != null) {
            return mGLFramebuffer.bind(clear);
        }
        return false;
    }

    public void setFilterGroups(AbsFilterGroup... filterGroups) {

    }

    public boolean isNeedBlend() {
        return false;
    }

    public boolean isNeedFlipTexture() {
        return false;
    }

    public int getFramebufferTextureId(int defaultTextureId) {
        if (mGLFramebuffer != null && mGLFramebuffer.hasBindFramebuffer()) {
            return mGLFramebuffer.getTextureId();
        }
        return defaultTextureId;
    }

    protected abstract void bindTexture(int textureId);

    protected abstract void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride,
                                           float[] texMatrix, FloatBuffer texBuffer, int texStride);

    protected void onPreDraw() {
//        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }

    protected abstract void drawArrays(int firstVertex, int vertexCount);

    protected void onAfterDraw() {
//        GLES20.glFlush();
    }

    protected void unbindGLSLValues() {
        GLES20.glDisableVertexAttribArray(maPositionLoc);
        GLES20.glDisableVertexAttribArray(maTextureCoordLoc);
    }

    protected abstract void unbindTexture();

    protected void disuseProgram() {
        GLES20.glUseProgram(0);
    }

    @Override
    public void releaseProgram() {
        if (mGLFramebuffer != null) {
            mGLFramebuffer.destroy();
            mGLFramebuffer = null;
        }
        mHasInitFramebuffer = false;
        mContext = null;
        if (mTaskList != null) {
            for (AbsTask task : mTaskList) {
                if (task != null) {
                    task.clearAll();
                }
            }
            mTaskList.clear();
            mTaskList = null;
        }
    }
}
