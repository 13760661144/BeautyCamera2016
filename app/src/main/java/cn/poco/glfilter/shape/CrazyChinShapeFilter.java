package cn.poco.glfilter.shape;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.pgles.PGLNativeIpl;

public class CrazyChinShapeFilter extends DefaultFilter {

    private int muMaskTextureLoc;
    private int mMaskTextureId;

    public CrazyChinShapeFilter(Context context) {
        super(context);
    }

    @Override
    protected int createProgram(Context context) {
//        return GlUtil.createProgram(context, R.raw.vert_chin1, R.raw.frag_chin1);
        return PGLNativeIpl.loadChinShapeProgram();
    }

    @Override
    protected void getGLSLValues() {
        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "aPosition");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "aTextureCoord");
        muTextureLoc = GLES20.glGetUniformLocation(mProgramHandle, "uTexture");
        muMaskTextureLoc = GLES20.glGetUniformLocation(mProgramHandle, "uMaskTexture");
    }

    public void setMaskTextureId(int textureId) {
        mMaskTextureId = textureId;
    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        useProgram();

        bindTexture(textureId);
        bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);
        drawArrays(firstVertex, vertexCount);

        unbindGLSLValues();
        unbindTexture();
        disuseProgram();
    }

    @Override
    protected void bindTexture(int textureId) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(getTextureTarget(), textureId);
        GLES20.glUniform1i(muTextureLoc, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(getTextureTarget(), mMaskTextureId);
        GLES20.glUniform1i(muMaskTextureLoc, 1);
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
    }

}
