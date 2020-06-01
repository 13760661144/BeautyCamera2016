package cn.poco.glfilter.base;

import android.content.Context;
import android.opengl.GLES20;
import android.support.annotation.RawRes;

import java.nio.FloatBuffer;

/**
 * Created by Jdlin on 2016/7/8.
 */
public class DefaultFilter extends AbstractFilter implements IFilter {

    private static final String vertexShaderCode =
            "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "uniform mat4 uMVPMatrix;  // MVP 的变换矩阵（整体变形）\n" +
                    "uniform mat4 uTexMatrix;  // Texture 的变换矩阵 （只对texture变形）\n" +
                    "varying vec2 textureCoord;\n" +
                    "void main() {\n" +
                    "    gl_Position = uMVPMatrix * aPosition;\n" +
                    "    textureCoord = (uTexMatrix * aTextureCoord).xy;\n" +
                    "}";

    private static final String fragmentShaderCode =
            "precision mediump float;\n" +
                    "varying vec2 textureCoord;\n" +
                    "uniform sampler2D uTexture;\n" +
                    "void main() {\n" +
                    "    gl_FragColor = texture2D(uTexture, textureCoord);\n" +
                    "}";

    protected int muMVPMatrixLoc;
    protected int muTexMatrixLoc;
    protected int muTextureLoc;

    public DefaultFilter(Context context) {
        super(context);
    }

    public DefaultFilter(Context context, int programHandle) {
        super(context, programHandle);
    }

    public DefaultFilter(Context context, @RawRes int vertexSourceRawId, @RawRes int fragmentSourceRawId) {
        super(context, vertexSourceRawId, fragmentSourceRawId);
    }

    public DefaultFilter(Context context, String vertexSource, String fragmentSource) {
        super(context, vertexSource, fragmentSource);
    }

    @Override
    protected int createProgram(Context context) {
        return GlUtil.createProgram(vertexShaderCode, fragmentShaderCode);
    }

    @Override
    protected void getGLSLValues() {
        super.getGLSLValues();

        muTextureLoc = GLES20.glGetUniformLocation(mProgramHandle, "uTexture");
        muMVPMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uMVPMatrix");
        muTexMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uTexMatrix");
    }

    @Override
    public int getTextureTarget() {
        return GLES20.GL_TEXTURE_2D;
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
        mDefaultTextureId = textureId;
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(getTextureTarget(), textureId);
        GLES20.glUniform1i(muTextureLoc, 0);
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, texMatrix, 0);
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
    }

    @Override
    protected void drawArrays(int firstVertex, int vertexCount) {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, firstVertex, vertexCount);
    }

    @Override
    protected void unbindTexture() {
        GLES20.glBindTexture(getTextureTarget(), 0);
//        GLES20.glDeleteTextures(1, new int[]{mDefaultTextureId}, 0);
    }

    @Override
    public void loadNextTexture(boolean load) {

    }

    @Override
    public void releaseProgram() {
        super.releaseProgram();
        GLES20.glDeleteProgram(mProgramHandle);
        mProgramHandle = -1;
    }
}
