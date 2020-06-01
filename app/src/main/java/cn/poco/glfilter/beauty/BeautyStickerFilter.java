package cn.poco.glfilter.beauty;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.glfilter.base.GlUtil;
import cn.poco.pgles.PGLNativeIpl;

/**
 *
 * 动态贴纸美颜磨皮接口
 */
public class BeautyStickerFilter extends DefaultFilter {

    private int mTableTextureLoc;
    private int muTexelWidthLoc;
    private int muTexelHeightLoc;
    private int muDeltaLoc;
    private int muPercentLoc;
    private int muQflagLoc;
    private int muStep1Loc;
    private int muStep2Loc;
    private int muStep3Loc;

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

    protected byte[] mTable = null;
    protected int mIndexTexture0;

    public BeautyStickerFilter(Context context) {
        super(context);
    }

    @Override
    protected int createProgram(Context context) {
        mTable = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 4, 5, 7, 8, 9, 11, 12, 13, 15, 16, 17, 19, 20, 21, 23, 24, 25, 27, 28, 29, 31, 32, 33, 34, 36, 37, 38, 40, 41, 42, 44, 45, 46, 48, 49, 50, 51, 53, 54, 55, 57, 58, 59, 61, 62, 63, 64, 66, 67, 68, 70, 71, 72, 73, 75, 76, 77, 78, 80, 81, 82, 83, 85, 86, 87, 88, 90, 91, 92, 93, 95, 96, 97, 98, 99, 101, 102,
                103, 104, 105, 107, 108, 109, 110, 111, 113, 114, 115, 116, 117, 118, 120, 121, 122, 123, 124, 125, 126, -128, -127, -126, -125, -124, -123, -122, -121, -120, -119, -117, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -106, -105, -104, -103, -102, -101, -100, -99, -98, -97, -96, -95, -94, -93, -92, -91, -90, -89, -88, -87, -86, -85, -84, -83, -82, -81, -80, -79, -78, -77, -77, -76, -75, -74, -73, -72, -71, -70, -69, -68, -68, -67,
                -66, -65, -64, -63, -62, -61, -61, -60, -59, -58, -57, -56, -55, -55, -54, -53, -52, -51, -50, -50, -49, -48, -47, -46, -45, -45, -44, -43, -42, -41, -41, -40, -39, -38, -37, -37, -36, -35, -34, -33, -33, -32, -31, -30, -29, -29, -28, -27, -26, -26, -25, -24, -23, -22, -22, -21, -20, -19, -19, -18, -17, -16, -15, -15, -14, -13, -12, -12, -11, -10, -9, -9, -8, -7,
                -6, -6, -5, -4, -3, -3, -2, -1};

        mIndexTexture0 = GlUtil.createIndexTexture(mTable);
//        return GlUtil.createProgram(context, R.raw.vertex_beauty, R.raw.fragment_beauty);
        return PGLNativeIpl.loadStikerBeautyProgramV3();
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
        return true;
    }

    @Override
    public void setViewSize(int width, int height) {
        super.setViewSize(width, height);
    }

    public void setCameraSize(int width, int height) {
        mTexelWidth = 1.f / (float) width;
        mTexelHeight = 1.f / (float) height;
        int minVal = Math.min(width, height);
        if (minVal <= 720){
            mDelta = 0.0010f;
            mStep1 = 10.5f;
            mStep2 = 5.5f;
            mStep3 = 0.5f;
        }else if (minVal <= 1080){
            mDelta = 0.0014f;
            mStep1 = 12.5f;
            mStep2 = 6.5f;
            mStep3 = 0.5f;
        }else{
            mDelta = 0.0018f;
            mStep1 = 16.5f;
            mStep2 = 8.5f;
            mStep3 = 0.5f;
        }
    }

    public void setBeautyEnable(boolean enable) {
        mIsBeauty = enable;
    }

    /**
     * @param percent 0-1.0
     */
    public void setBeautyParams(float percent) {
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

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mIndexTexture0);
        GLES20.glUniform1i(mTableTextureLoc, 1);
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
        if (muQflagLoc > 0) {
            GLES20.glUniform1f(muQflagLoc, mQualCommFlag);
        }
        GLES20.glUniform1f(mBeautyLoc, mIsBeauty ? 1.0f : 0.0f);
    }

    @Override
    protected void unbindTexture() {
        super.unbindTexture();
    }

    @Override
    public void releaseProgram() {
        super.releaseProgram();
        GLES20.glDeleteTextures(1, new int[]{mIndexTexture0}, 0);
    }
}
