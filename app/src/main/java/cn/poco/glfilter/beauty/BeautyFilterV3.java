package cn.poco.glfilter.beauty;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.glfilter.base.GLFramebuffer;
import cn.poco.pgles.PGLNativeIpl;
import cn.poco.utils.FileUtil;

/**
 * Created by liujx on 2017/12/5.
 */

public class BeautyFilterV3 extends DefaultFilter {

    private int beautyLevelProgram;
    private int beautyLevelInputTextureUniform;
    private int beautyLevelGrayTableTexUniform;
    private int beautyLevelLevelTableTexUniform;
    private int beautyLevelRangeInvUniform;
    private int beautyLevelBlackUniform;
    private int beautyLevelAlphaUniform;

    private int beautyMeanProgram;
    private int beautyMeanInputTextureUniform;
    private int beautyMeanBufferWidthUniform;
    private int beautyMeanBufferHeightUniform;
    private int beautyMeanPositionLoc;
    private int beautyMeanTextureCoordLoc;

    private int beautyVarProgram;
    private int beautyVarInputTextureUniform;
    private int beautyVarMeanTextureUniform;
    private int beautyVarPositionLoc;
    private int beautyVarTextureCoordLoc;

    private int beautySmoothProgram;
    private int beautySmoothInputTextureUniform;
    private int beautySmoothMeanTextureUniform;
    private int beautySmoothVarTextureUniform;
    private int beautySmoothBlurUniform;
    private int beautySmoothThetaUniform;
    private int beautySmoothPositionLoc;
    private int beautySmoothTextureCoordLoc;

    private int scaleWidth, scaleHeight;
    private float scaleParam;

    private int grayTexId, levelTexId;

    private float white_opacity = 0.85f * 0.5f;       //美白
    private float smooth_opacity = 1.0f;      //平滑

    private float LevelRange = 1.0282258f;    //明
    private float LevelBlack = 0.0215686f;    //暗

    private GLFramebuffer mLevelBuffer;
    private int mLevelTexture;

    private GLFramebuffer mBeautyBuffer;
    private int mTexture1;
    private int mTexture2;
    private int mTexture3;
    private int mTexture4;
    private int mTexture5;

    public BeautyFilterV3(Context context) {
        super(context);
    }

    @Override
    protected int createProgram(Context context) {
        int count = 0;
        beautyLevelProgram = PGLNativeIpl.loadColorProgram();
        if (beautyLevelProgram > 0) {
            count++;
            beautyLevelInputTextureUniform = GLES20.glGetUniformLocation(beautyLevelProgram, "inputImageTexture");
            beautyLevelGrayTableTexUniform = GLES20.glGetUniformLocation(beautyLevelProgram, "grayTable");
            beautyLevelLevelTableTexUniform = GLES20.glGetUniformLocation(beautyLevelProgram, "levelTable");
            beautyLevelRangeInvUniform = GLES20.glGetUniformLocation(beautyLevelProgram, "levelScale");
            beautyLevelBlackUniform = GLES20.glGetUniformLocation(beautyLevelProgram, "levelLoss");
            beautyLevelAlphaUniform = GLES20.glGetUniformLocation(beautyLevelProgram, "alpha");

            maPositionLoc = GLES20.glGetAttribLocation(beautyLevelProgram, "position");
            maTextureCoordLoc = GLES20.glGetAttribLocation(beautyLevelProgram, "inputTextureCoordinate");
        }

        beautyMeanProgram = PGLNativeIpl.loadBlurProgram();
        if (beautyMeanProgram > 0) {
            count++;
            beautyMeanInputTextureUniform = GLES20.glGetUniformLocation(beautyMeanProgram, "inputImageTexture");
            beautyMeanBufferWidthUniform = GLES20.glGetUniformLocation(beautyMeanProgram, "texelWidthOffset");
            beautyMeanBufferHeightUniform = GLES20.glGetUniformLocation(beautyMeanProgram, "texelHeightOffset");

            beautyMeanPositionLoc = GLES20.glGetAttribLocation(beautyMeanProgram, "position");
            beautyMeanTextureCoordLoc = GLES20.glGetAttribLocation(beautyMeanProgram, "inputTextureCoordinate");
        }

        beautyVarProgram = PGLNativeIpl.loadVarianceProgram();
        if (beautyVarProgram > 0) {
            count++;
            beautyVarInputTextureUniform = GLES20.glGetUniformLocation(beautyVarProgram, "inputImageTexture");
            beautyVarMeanTextureUniform = GLES20.glGetUniformLocation(beautyVarProgram, "inputImageTexture2");

            beautyVarPositionLoc = GLES20.glGetAttribLocation(beautyVarProgram, "position");
            beautyVarTextureCoordLoc = GLES20.glGetAttribLocation(beautyVarProgram, "inputTextureCoordinate");
        }

        beautySmoothProgram = PGLNativeIpl.loadSmoothProgram();
        if (beautySmoothProgram > 0) {
            count++;
            beautySmoothInputTextureUniform = GLES20.glGetUniformLocation(beautySmoothProgram, "inputImageTexture");
            beautySmoothMeanTextureUniform = GLES20.glGetUniformLocation(beautySmoothProgram, "inputImageTexture2");
            beautySmoothVarTextureUniform = GLES20.glGetUniformLocation(beautySmoothProgram, "inputImageTexture3");
            beautySmoothBlurUniform = GLES20.glGetUniformLocation(beautySmoothProgram, "opacity");
            beautySmoothThetaUniform = GLES20.glGetUniformLocation(beautySmoothProgram, "theta");

            beautySmoothPositionLoc = GLES20.glGetAttribLocation(beautySmoothProgram, "position");
            beautySmoothTextureCoordLoc = GLES20.glGetAttribLocation(beautySmoothProgram, "inputTextureCoordinate");
        }

        return count == 4 ? 1 : 0;
    }

    @Override
    protected void getGLSLValues() {
        mProgramHandle = 0;
    }

    @Override
    public int getTextureTarget() {
        return GLES20.GL_TEXTURE_2D;
    }

    @Override
    public void setViewSize(int width, int height) {
        super.setViewSize(width, height);
        if (width == 0 || height == 0) {
            return;
        }

        if (mLevelBuffer != null) {
            mLevelBuffer.destroy();
            mLevelBuffer = null;
        }
        mLevelBuffer = new GLFramebuffer(1, mWidth, mHeight, GLES20.GL_RGB);

        scaleWidth = 360;
        scaleHeight = Math.round(scaleWidth * (mHeight * 1.0f / mWidth));
        if (mBeautyBuffer != null) {
            mBeautyBuffer.destroy();
            mBeautyBuffer = null;
        }
        mBeautyBuffer = new GLFramebuffer(3, scaleWidth, scaleHeight, GLES20.GL_RGB);
    }

    /**
     * 范围均0.0 - 1.0
     *
     * @param smoothParam 磨皮
     * @param whiteParam  肤色
     */
    public void setBeautyParams(float smoothParam, float whiteParam) {
        smooth_opacity = smoothParam < 0.0f ? 0.0f : (smoothParam > 1.0f ? 1.0f : smoothParam);
        white_opacity = whiteParam < 0.0f ? 0.0f : (whiteParam > 1.0f ? 1.0f : whiteParam);
        white_opacity *= 0.5f;
    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex,
                       int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {

        drawLevel(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);

        drawBeautyEffect(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, mLevelTexture, texStride);

        unbindGLSLValues();
        unbindTexture();
        disuseProgram();
    }

    private void drawLevel(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex,
                           int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        mLevelBuffer.bind(true);
        mLevelTexture = mLevelBuffer.getTextureId();

        GLES20.glUseProgram(beautyLevelProgram);

        if (grayTexId <= 0) {
            //grayTexId = getBitmapTextureId(R.drawable.gray_my);
            grayTexId = PGLNativeIpl.loadBeautyGrayId();
        }
        if (levelTexId <= 0) {
//            levelTexId = getBitmapTextureId(R.drawable.level_table);
            byte[] data = FileUtil.getAssetsByte(mContext, "skinStyle/level_table_1");
            levelTexId = PGLNativeIpl.loadBeautyColorByteId(data);
        }

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(getTextureTarget(), textureId);
        GLES20.glUniform1i(beautyLevelInputTextureUniform, 1);

        if (grayTexId > 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
            GLES20.glBindTexture(getTextureTarget(), grayTexId);
            GLES20.glUniform1i(beautyLevelGrayTableTexUniform, 2);
        }

        if (levelTexId > 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
            GLES20.glBindTexture(getTextureTarget(), levelTexId);
            GLES20.glUniform1i(beautyLevelLevelTableTexUniform, 3);
        }

        GLES20.glUniform1f(beautyLevelAlphaUniform, white_opacity);
        GLES20.glUniform1f(beautyLevelRangeInvUniform, LevelRange);  //248
        GLES20.glUniform1f(beautyLevelBlackUniform, LevelBlack);

        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    private void drawBeautyEffect(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex,
                                  int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        mBeautyBuffer.bindByIndex(0, true);
        mTexture1 = mBeautyBuffer.getTextureIdByIndex(0);

        GLES20.glViewport(0, 0, scaleWidth, scaleHeight);
        float var_width_offset = (1.0f / scaleWidth);
        float var_height_offset = (1.0f / scaleHeight);

        GLES20.glUseProgram(beautyMeanProgram);

        //纵轴
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(getTextureTarget(), textureId);
        GLES20.glUniform1i(beautyMeanInputTextureUniform, 1);

        GLES20.glUniform1f(beautyMeanBufferWidthUniform, 0.0f);
        GLES20.glUniform1f(beautyMeanBufferHeightUniform, var_height_offset * 1.5f);

        GLES20.glEnableVertexAttribArray(beautyMeanPositionLoc);
        GLES20.glVertexAttribPointer(beautyMeanPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(beautyMeanTextureCoordLoc);
        GLES20.glVertexAttribPointer(beautyMeanTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);


        mBeautyBuffer.bindByIndex(1, true);
        mTexture2 = mBeautyBuffer.getTextureIdByIndex(1);
        //横轴
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);

        GLES20.glBindTexture(getTextureTarget(), mTexture1);
        GLES20.glUniform1i(beautyMeanInputTextureUniform, 2);
        GLES20.glUniform1f(beautyMeanBufferWidthUniform, var_width_offset * 1.5f);
        GLES20.glUniform1f(beautyMeanBufferHeightUniform, 0.0f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);


        mBeautyBuffer.bindByIndex(2, true);
        mTexture3 = mBeautyBuffer.getTextureIdByIndex(2);

        //calculate var params
        GLES20.glUseProgram(beautyVarProgram);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(getTextureTarget(), textureId);
        GLES20.glUniform1i(beautyVarInputTextureUniform, 1);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        GLES20.glBindTexture(getTextureTarget(), mTexture2);
        GLES20.glUniform1i(beautyVarMeanTextureUniform, 2);

        GLES20.glEnableVertexAttribArray(beautyVarPositionLoc);
        GLES20.glVertexAttribPointer(beautyVarPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(beautyVarTextureCoordLoc);
        GLES20.glVertexAttribPointer(beautyVarTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        mBeautyBuffer.bindByIndex(0, true);
        mTexture4 = mBeautyBuffer.getTextureIdByIndex(0);

        //second mean
        GLES20.glUseProgram(beautyMeanProgram);

        //纵轴
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(getTextureTarget(), mTexture3);
        GLES20.glUniform1i(beautyMeanInputTextureUniform, 1);

        GLES20.glUniform1f(beautyMeanBufferWidthUniform, 0.0f);
        GLES20.glUniform1f(beautyMeanBufferHeightUniform, var_height_offset * 1.5f);

        GLES20.glEnableVertexAttribArray(beautyMeanPositionLoc);
        GLES20.glVertexAttribPointer(beautyMeanPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(beautyMeanTextureCoordLoc);
        GLES20.glVertexAttribPointer(beautyMeanTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        mBeautyBuffer.bindByIndex(2, true);
        mTexture5 = mBeautyBuffer.getTextureIdByIndex(2);
        //横轴
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);

        GLES20.glBindTexture(getTextureTarget(), mTexture4);
        GLES20.glUniform1i(beautyMeanInputTextureUniform, 2);
        GLES20.glUniform1f(beautyMeanBufferWidthUniform, var_width_offset * 1.5f);
        GLES20.glUniform1f(beautyMeanBufferHeightUniform, 0.0f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

//----------------------------------------------
        if (mGLFramebuffer != null) {
            mGLFramebuffer.rebind(true);
        } else {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        }
        GLES20.glViewport(0, 0, mWidth, mHeight);

        GLES20.glUseProgram(beautySmoothProgram);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(getTextureTarget(), textureId);
        GLES20.glUniform1i(beautySmoothInputTextureUniform, 1);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        GLES20.glBindTexture(getTextureTarget(), mTexture2);
        GLES20.glUniform1i(beautySmoothMeanTextureUniform, 2);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        GLES20.glBindTexture(getTextureTarget(), mTexture5);
        GLES20.glUniform1i(beautySmoothVarTextureUniform, 3);

        GLES20.glUniform1f(beautySmoothBlurUniform, smooth_opacity);
        GLES20.glUniform1f(beautySmoothThetaUniform, 0.07f);

        GLES20.glEnableVertexAttribArray(beautySmoothPositionLoc);
        GLES20.glVertexAttribPointer(beautySmoothPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(beautySmoothTextureCoordLoc);
        GLES20.glVertexAttribPointer(beautySmoothTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    @Override
    protected void bindTexture(int textureId) {

    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {

    }

    @Override
    protected void drawArrays(int firstVertex, int vertexCount) {

    }

    @Override
    protected void unbindGLSLValues() {

    }

    @Override
    protected void unbindTexture() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    @Override
    protected void disuseProgram() {

    }

    @Override
    public void loadNextTexture(boolean load) {

    }

    @Override
    public void releaseProgram() {
        GLES20.glDeleteProgram(mProgramHandle);
        mProgramHandle = -1;

        GLES20.glDeleteProgram(beautyLevelProgram);
        beautyLevelProgram = -1;

        GLES20.glDeleteProgram(beautyMeanProgram);
        beautyMeanProgram = -1;

        GLES20.glDeleteProgram(beautySmoothProgram);
        beautySmoothProgram = -1;

        GLES20.glDeleteTextures(2, new int[]{grayTexId, levelTexId}, 0);

        if (mLevelBuffer != null) {
            mLevelBuffer.destroy();
            mLevelBuffer = null;
        }
        if (mBeautyBuffer != null) {
            mBeautyBuffer.destroy();
            mBeautyBuffer = null;
        }
    }

}
