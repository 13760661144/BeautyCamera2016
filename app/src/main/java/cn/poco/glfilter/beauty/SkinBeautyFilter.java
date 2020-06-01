package cn.poco.glfilter.beauty;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.pgles.PGLNativeIpl;
import my.beautyCamera.R;

/**
 * 商业定制美白
 */
public class SkinBeautyFilter extends DefaultFilter {

    private int filterBGCosmeticTextureUniform, LookupTextureWhiteUniform;

    private int StrengthUniform, processUniform;

    private int mTableId;
    private float SkinColorStrength = 0.5f;  //调节力度 0~1之间

    public SkinBeautyFilter(Context context) {
        super(context);
    }

    @Override
    protected int createProgram(Context context) {
        return PGLNativeIpl.loadLookUp64Program();
//        return GlUtil.createProgram(context, R.raw.vert_lookup, R.raw.frag_lookup);
    }

    @Override
    public int getTextureTarget() {
        return GLES20.GL_TEXTURE_2D;
    }

    @Override
    protected void getGLSLValues() {
        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "position");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "inputTextureCoordinate");

        filterBGCosmeticTextureUniform = GLES20.glGetUniformLocation(mProgramHandle, "inputImageTexture");
        LookupTextureWhiteUniform = GLES20.glGetUniformLocation(mProgramHandle, "lookupTableImage");

        processUniform = GLES20.glGetUniformLocation(mProgramHandle, "lookuptableImageAvailable");
        StrengthUniform = GLES20.glGetUniformLocation(mProgramHandle, "strength");

    }

    //    //美白范围0~1
//    public void setStengthScale(float Scale) {
//        if (Scale < 0.0f) {
//            Scale = 0.0f;
//        } else if (Scale > 1.0f) {
//            Scale = 1.0f;
//        }
//        SkinColorStrength = Scale;
//    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
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
        if (mTableId <= 0) {
            //mTableId = getBitmapTextureId(R.drawable.meibai_jifanxi);
            mTableId = getBitmapTextureId(R.drawable.meibai_yuxi);
        }
    }

    @Override
    protected void bindTexture(int textureId) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(getTextureTarget(), textureId);
        GLES20.glUniform1i(filterBGCosmeticTextureUniform, 0);

        if (mTableId > 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTableId);
            GLES20.glUniform1i(LookupTextureWhiteUniform, 1);
        }
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        //镜头画面
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
        GLES20.glUniform1i(processUniform, 1);
        GLES20.glUniform1f(StrengthUniform, SkinColorStrength);
    }

    @Override
    protected void drawArrays(int firstVertex, int vertexCount) {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, firstVertex, vertexCount);
    }

    @Override
    protected void unbindTexture() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    @Override
    public void releaseProgram() {
        GLES20.glDeleteProgram(mProgramHandle);
        mProgramHandle = -1;

        GLES20.glDeleteTextures(1, new int[]{mTableId}, 0);
    }
}
