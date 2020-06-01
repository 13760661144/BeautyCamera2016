package cn.poco.glfilter.color;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.pgles.PGLNativeIpl;
import my.beautyCamera.R;

/**
 * Created by Jdlin on 2016/7/8.
 */
public class CamelliaFilter extends DefaultFilter {

    protected int maTextureCoordLoc1;
    protected int mTexture1;
    protected int muTextureLoc1;

    public CamelliaFilter(Context context) {
        super(context);
    }

    @Override
    protected int createProgram(Context context) {
        mTexture1 = getBitmapTextureId(R.drawable.crazy01_mask1);
        return PGLNativeIpl.loadStikerCamelliaProgram();
    }

    @Override
    protected void getGLSLValues() {
        super.getGLSLValues();
        maTextureCoordLoc1 = GLES20.glGetAttribLocation(mProgramHandle, "aTextureCoord1");
        muTextureLoc1 = GLES20.glGetUniformLocation(mProgramHandle, "uTexture1");
    }

    @Override
    public boolean isNeedFlipTexture() {
        return true;
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        super.bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc1);
        GLES20.glVertexAttribPointer(maTextureCoordLoc1, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
    }

    @Override
    protected void bindTexture(int textureId) {
        super.bindTexture(textureId);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 1);
        GLES20.glBindTexture(getTextureTarget(), mTexture1);
        GLES20.glUniform1i(muTextureLoc1, 1);
    }

    @Override
    protected void unbindGLSLValues() {
        super.unbindGLSLValues();
        GLES20.glDisableVertexAttribArray(maTextureCoordLoc1);
    }

    @Override
    public void releaseProgram() {
        super.releaseProgram();
        GLES20.glDeleteTextures(1, new int[]{mTexture1}, 0);
    }
}
