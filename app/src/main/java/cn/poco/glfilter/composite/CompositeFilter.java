package cn.poco.glfilter.composite;

import android.content.Context;
import android.opengl.GLES20;
import android.support.annotation.RawRes;

import java.nio.FloatBuffer;

import cn.poco.glfilter.base.DefaultFilter;

/**
 * Created by zwq on 2017/04/25 14:33.<br/><br/>
 */
public class CompositeFilter extends DefaultFilter {

    private int maTextureCoordLoc1;
    private int muTextureLoc1;
    private int muAlphaLoc;

    private boolean mIsStickerMode;
    private int mCenterXLoc;
    private int mCenterYLoc;

    private float mCenterX;
    private float mCenterY;

    private CompositeData mCompositeData;

    public CompositeFilter(Context context, int programHandle) {
        super(context, programHandle);
    }

    public CompositeFilter(Context context, @RawRes int vertexSourceRawId, @RawRes int fragmentSourceRawId) {
        super(context, vertexSourceRawId, fragmentSourceRawId);
    }

    @Override
    protected void getGLSLValues() {
        super.getGLSLValues();

        maTextureCoordLoc1 = GLES20.glGetAttribLocation(mProgramHandle, "aTextureCoord1");
        muTextureLoc1 = GLES20.glGetUniformLocation(mProgramHandle, "uTexture1");
        muAlphaLoc = GLES20.glGetUniformLocation(mProgramHandle, "alpha");

        mCenterXLoc = GLES20.glGetUniformLocation(mProgramHandle, "centerX");
        mCenterYLoc = GLES20.glGetUniformLocation(mProgramHandle, "centerY");
    }

    public void setCompositeData(CompositeData compositeData) {
        mCompositeData = compositeData;
    }

    public void setStickerMode(boolean stickerMode) {
        mIsStickerMode = stickerMode;
    }

    public void setCenterPoint(float cx, float cy) {
        mCenterX = cx;
        mCenterY = cy;
    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        super.onDraw(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);
    }

    @Override
    protected void useProgram() {
        super.useProgram();

        if (mIsStickerMode) {
            GLES20.glFrontFace(GLES20.GL_CCW);
            GLES20.glCullFace(GLES20.GL_BACK);
        }
    }

    @Override
    protected void bindTexture(int textureId) {
        super.bindTexture(textureId);

        if (mCompositeData == null) return;
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(getTextureTarget(), mCompositeData.mMaskTextureId);
        GLES20.glUniform1i(muTextureLoc1, 1);
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        if (mIsStickerMode) {
            GLES20.glUniform1f(mCenterXLoc, mCenterX);
            GLES20.glUniform1f(mCenterYLoc, mCenterY);
        }

        if (mCompositeData != null && mCompositeData.mTextureBuffer != null) {
            GLES20.glUniform1f(muAlphaLoc, mCompositeData.mAlpha);

            GLES20.glEnableVertexAttribArray(maTextureCoordLoc1);
            GLES20.glVertexAttribPointer(maTextureCoordLoc1, 2, GLES20.GL_FLOAT, false, texStride, mCompositeData.mTextureBuffer);
        }
        super.bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);
    }

    @Override
    protected void drawArrays(int firstVertex, int vertexCount) {
        if (mCompositeData != null && mCompositeData.mTextureBuffer != null && mCompositeData.mElementsCount > 0) {
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mCompositeData.mElementsCount, GLES20.GL_UNSIGNED_SHORT, mCompositeData.mIndexBuffer);
        } else {
            super.drawArrays(firstVertex, vertexCount);
        }
    }

    @Override
    public void releaseProgram() {
        super.releaseProgram();
        if (mCompositeData != null) {
            mCompositeData.release();
            mCompositeData = null;
        }
    }
}
