package cn.poco.glfilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

import cn.poco.glfilter.base.AbstractFilter;
import cn.poco.glfilter.base.GlUtil;
import cn.poco.glfilter.base.IFilter;
import cn.poco.image.filter;

public class EndingFilter extends AbstractFilter implements IFilter {

    private static final String vertexShaderCode =
            "attribute vec4 aPosition;\n" +
                    "attribute vec2 aTextureCoord;\n" +
                    "attribute float aTextureId;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "varying float vTextureId;\n" +
                    "void main() {\n" +
                    "    gl_Position = aPosition;\n" +
                    "    vTextureCoord = aTextureCoord;\n" +
                    "    vTextureId = aTextureId;\n" +
                    "}";

    private static final String fragmentShaderCode =
            "precision mediump float;\n" +
                    "uniform sampler2D uTexture0;\n" +
                    "uniform sampler2D uTexture1;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "varying float vTextureId;\n" +
                    "void main() {\n" +
                    "   if (vTextureId < 1.0) {\n" +
                    "       gl_FragColor = texture2D(uTexture0, vTextureCoord);\n" +
                    "   } else if (vTextureId < 2.0) {\n" +
                    "       gl_FragColor = texture2D(uTexture1, vTextureCoord);\n" +
                    "   }\n" +
                    "}";

    private int[] mLogos = new int[]{
//            R.drawable.logo0001,
//            R.drawable.logo0002,
//            R.drawable.logo0003,
//            R.drawable.logo0004,
//            R.drawable.logo0005,
//            R.drawable.logo0006,
//            R.drawable.logo0007,
//            R.drawable.logo0008,
//            R.drawable.logo0009
    };
    private int maTextureUnitsId;
    private int uTexture_BgId, uTexture_LogoId;
    private int mBgTextureId, mLogoTextureId;
    private boolean mIsRecord;

    private Matrix mMatrix = new Matrix();
    private Bitmap mLogoBmp;

    private int step = 1;
    private int mEndingFrameCount = 10;
    private Bitmap mOriLastFrame;
    private Bitmap mLastFrame;
    private int mFrameCount;
    private int mStartIndex = -1;
    private int mIndex = mLogos.length - 1;
    private int mCurrentIndex = -1;

    public EndingFilter(Context context) {
        super(context);
        mMatrix.preScale(1, -1);
    }

    @Override
    public int getTextureTarget() {
        return GLES20.GL_TEXTURE_2D;
    }

    @Override
    public void setViewSize(int width, int height) {
        super.setViewSize(width, height);
    }

    @Override
    public void setDrawType(boolean isRecord) {
        mIsRecord = isRecord;
    }

    @Override
    public void resetFilterData() {

    }

    @Override
    protected int createProgram(Context context) {
        return GlUtil.createProgram(vertexShaderCode, fragmentShaderCode);
    }

    @Override
    protected void getGLSLValues() {
        super.getGLSLValues();
        maTextureUnitsId = GLES20.glGetAttribLocation(mProgramHandle, "aTextureId");
        uTexture_BgId = GLES20.glGetUniformLocation(mProgramHandle, "uTexture0");
        uTexture_LogoId = GLES20.glGetUniformLocation(mProgramHandle, "uTexture1");
    }

    public void setEndingFrameCount(int endingFrameCount, Bitmap lastFrame) {
        mEndingFrameCount = endingFrameCount;
        if (mLastFrame == null || mLastFrame.isRecycled()) {
            if (lastFrame != null && !lastFrame.isRecycled()) {
                mOriLastFrame = Bitmap.createBitmap(lastFrame, 0, 0, lastFrame.getWidth(), lastFrame.getHeight(), mMatrix, true);
                mLastFrame = mOriLastFrame.copy(Bitmap.Config.ARGB_8888, true);
            }
            filter.fakeGlassBeauty(mLastFrame, 0x19000000);
        }

        if (mStartIndex == -1) {
            mIndex = mLogos.length - 1;
            if (mEndingFrameCount <= mLogos.length) {
                mStartIndex = mLogos.length - mEndingFrameCount;
            } else {
                step = mEndingFrameCount / mLogos.length;
                if (step < 1) {
                    step = 1;
                }
                mStartIndex = 0;
            }
            mIndex = mStartIndex;
        }
        int mod = mFrameCount % step;
        if (mFrameCount == 0) {
            mIndex += -1;
        }
        mIndex += (mod == 0 ? 1 : 0);
        if (mIndex < 0) {
            mIndex = 0;
        } else if (mIndex > mLogos.length - 1) {
            mIndex = mLogos.length - 1;
        }
        mFrameCount++;
        if (mFrameCount == mEndingFrameCount) {
            mStartIndex = -1;
            mFrameCount = 0;
        }
    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex,
                       int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        useProgram();
        loadTexture();
        bindTexture(textureId);
        bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);

        drawArrays(firstVertex, vertexCount);

        unbindGLSLValues();
        unbindTexture();
        disuseProgram();
    }

    private void loadTexture() {
        Bitmap bitmap = null;
        if (mIsRecord && mLastFrame != null && !mLastFrame.isRecycled()) {
            mBgTextureId = GlUtil.createTexture(GLES20.GL_TEXTURE_2D, mLastFrame);

        } else if (mOriLastFrame != null && !mOriLastFrame.isRecycled()) {
            mBgTextureId = GlUtil.createTexture(GLES20.GL_TEXTURE_2D, mOriLastFrame);

        } else {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), mLogos[0]);
            if (bitmap != null && !bitmap.isRecycled()) {
                mBgTextureId = GlUtil.createTexture(GLES20.GL_TEXTURE_2D, bitmap);
                bitmap.recycle();
            } else {
//                Log.i(TAG, "mBgBmp is null");
            }
        }

        if (mIsRecord) {
            if (mLogoBmp == null || mLogoBmp.isRecycled()) {
                mCurrentIndex = -1;
            }
            if (mCurrentIndex != mIndex) {
                mCurrentIndex = mIndex;
                if (mLogoBmp != null && !mLogoBmp.isRecycled()) {
                    mLogoBmp.recycle();
                    mLogoBmp = null;
                }
                bitmap = BitmapFactory.decodeResource(mContext.getResources(), mLogos[mIndex]);
                if (bitmap != null && !bitmap.isRecycled()) {
                    mLogoBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mMatrix, true);
                }
            }
            if (mLogoBmp != null && !mLogoBmp.isRecycled()) {
                mLogoTextureId = GlUtil.createTexture(GLES20.GL_TEXTURE_2D, mLogoBmp);
            } else {
//                Log.i(TAG, "mLogoBmp is null");
            }
        }
    }

    @Override
    protected void bindTexture(int textureId) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mBgTextureId);
        GLES20.glUniform1i(uTexture_BgId, 0);

        if (mIsRecord) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mLogoTextureId);
            GLES20.glUniform1i(uTexture_LogoId, 1);
        }
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
        GLES20.glVertexAttrib1f(maTextureUnitsId, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        if (mIsRecord) {
            GLES20.glEnableVertexAttribArray(maPositionLoc);
            GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
            GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
            GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
            GLES20.glVertexAttrib1f(maTextureUnitsId, 1);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        }
    }

    @Override
    protected void drawArrays(int firstVertex, int vertexCount) {

    }

    @Override
    protected void unbindGLSLValues() {
        super.unbindGLSLValues();
    }

    @Override
    protected void unbindTexture() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    @Override
    public void loadNextTexture(boolean load) {

    }

    @Override
    public void releaseProgram() {
        GLES20.glDeleteProgram(mProgramHandle);
        mProgramHandle = -1;

        if (mOriLastFrame != null && !mOriLastFrame.isRecycled()) {
            mOriLastFrame.recycle();
            mOriLastFrame = null;
        }
        if (mLastFrame != null && !mLastFrame.isRecycled()) {
            mLastFrame.recycle();
            mLastFrame = null;
        }
        if (mLogoBmp != null && !mLogoBmp.isRecycled()) {
            mLogoBmp.recycle();
            mLogoBmp = null;
        }
    }
}
