package cn.poco.glfilter.beauty;

import android.content.Context;
import android.opengl.GLES20;
import android.support.annotation.FloatRange;

import java.nio.FloatBuffer;

import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.glfilter.base.GlUtil;
import cn.poco.pgles.PGLNativeIpl;

/**
 * Created by Jdlin on 2016/7/27.
 * 正常拍照美颜滤镜
 * 乐视lex620加入此滤镜 镜头预览会卡顿
 * 着色器中查表会卡
 */
public class BeautyFilterV2 extends DefaultFilter {

    private int mTableTextureLoc;
    private int muTexelWidthLoc;
    private int muTexelHeightLoc;
    private int muDeltaLoc;
    private int muPercentLoc;
    private int muQflagLoc;
    private int muStep1Loc;
    private int muStep2Loc;
    private int muStep3Loc;

    private int mTableTextureId;
    private int mBeautyLoc;
    private float mTexelWidth;
    private float mTexelHeight;
    private float mDelta = 0.0014f;
    private float mPercent = 0.75f;
    private float mStep1 = 12.5f;
    private float mStep2 = 6.5f;
    private float mStep3 = 0.5f;
    private float mQualCommFlag = 0.0f;
    private boolean mIsBeauty = true;

    public BeautyFilterV2(Context context) {
        super(context);
    }

    @Override
    protected int createProgram(Context context) {
        mTableTextureId = GlUtil.createTexture(GLES20.GL_TEXTURE_2D);

        return PGLNativeIpl.loadBeautyProgramByTexture();
    }

    @Override
    protected void getGLSLValues() {
        super.getGLSLValues();
        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "a_position");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "a_textureCoord0");

        mTableTextureLoc = GLES20.glGetUniformLocation(mProgramHandle, "tableTexture");
        mBeautyLoc = GLES20.glGetUniformLocation(mProgramHandle, "isBeauty");

        muTexelWidthLoc = GLES20.glGetUniformLocation(mProgramHandle, "texelWidth");
        muTexelHeightLoc = GLES20.glGetUniformLocation(mProgramHandle, "texelHeight");
        muDeltaLoc = GLES20.glGetUniformLocation(mProgramHandle, "delta");
        muPercentLoc = GLES20.glGetUniformLocation(mProgramHandle, "percent");
        muStep1Loc = GLES20.glGetUniformLocation(mProgramHandle, "step1");
        muStep2Loc = GLES20.glGetUniformLocation(mProgramHandle, "step2");
        muStep3Loc = GLES20.glGetUniformLocation(mProgramHandle, "step3");

        muQflagLoc = GLES20.glGetUniformLocation(mProgramHandle, "qflag");
    }

    @Override
    public boolean isNeedFlipTexture() {
        return false;
    }

    public void setCameraSize(int width, int height) {
        mTexelWidth = 1.f / (float) width;
        mTexelHeight = 1.f / (float) height;
        int minVal = Math.min(width, height);
        if (minVal <= 720) {
            mDelta = 0.0010f;
            mStep1 = 10.5f;
            mStep2 = 5.5f;
            mStep3 = 0.5f;
        } else if (minVal <= 1080) {
            mDelta = 0.0014f;
            mStep1 = 12.5f;
            mStep2 = 6.5f;
            mStep3 = 0.5f;
        } else {
            mDelta = 0.0018f;
            mStep1 = 16.5f;
            mStep2 = 8.5f;
            mStep3 = 0.5f;
        }
    }

    /**
     * @param percent 0-1.0
     */
    public void setBeautyParams(@FloatRange(from = 0.0f, to = 1.0f) float percent) {
        if (percent < 0) {
            percent = 0;
        } else if (percent > 1.0f) {
            percent = 1.0f;
        }
        mPercent = percent;
    }

    @Override
    protected void bindTexture(int textureId) {
        super.bindTexture(textureId);

        PGLNativeIpl.nativeBindBeautyTexture(mTableTextureLoc, mTableTextureId);
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        super.bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);

        GLES20.glUniform1f(muTexelWidthLoc, mTexelWidth);
        GLES20.glUniform1f(muTexelHeightLoc, mTexelHeight);
        GLES20.glUniform1f(muPercentLoc, mPercent);

        GLES20.glUniform1f(muDeltaLoc, mDelta);
        GLES20.glUniform1f(muStep1Loc, mStep1);
        GLES20.glUniform1f(muStep2Loc, mStep2);
        GLES20.glUniform1f(muStep3Loc, mStep3);
        GLES20.glUniform1f(muQflagLoc, mQualCommFlag);
        GLES20.glUniform1f(mBeautyLoc, mIsBeauty ? 1.0f : 0.0f);
    }
}
