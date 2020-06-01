package cn.poco.glfilter.color;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.pgles.PGLNativeIpl;
import my.beautyCamera.R;

/**
 * Created by liujx on 2017/10/12.
 */
public class AmaniFilter extends DefaultFilter {

    public static class ArmaniMaterialRenderStyle {
        public static final int kPORSSArmaniMaterialRenderStyleNormal = 0;              //无特殊效果
        public static final int kPORSSArmaniMaterialRenderStyleColor = 1;               //全屏上色
        public static final int kPORSSArmaniMaterialRenderStyleVerticalDuplicate = 2;   //上下分屏，上屏上色
        public static final int kPORSSArmaniMaterialRenderStyleAlignDuplicate = 3;      //对角分屏，右上、左下上色
        public static final int kPORSSArmaniMaterialRenderStyleMaterialDuplicate = 4;   //对角分屏, 左上，右下上色，右上、左下素材覆盖
    }

    private FloatBuffer clipBuffer, noRotationClipBuffer;
    private float imageVertices[] = new float[8];
    private float noRotationTextureCoordinates[] = new float[8];
    private ByteBuffer bb, bb2;

    private float[] mColorRed = {218, 3, 19, 76.5f};    //红色
    private float[] mColorBlack = {0, 0, 0, 76.5f};    //黑色
    private float[] mColorWhite = {232, 210, 190, 76.5f};    //米白色

    private int mMaterialId;
    private int mColorUniform;
    private int mColorInputTextureUniform;
    private int RenderStyle = 0;

    private long mShowTime;

    public AmaniFilter(Context context) {
        super(context);
    }

    @Override
    protected int createProgram(Context context) {

        mMaterialId = getBitmapTextureId(R.drawable.chanpin);

        return PGLNativeIpl.loadArmaniProgram();
    }

    @Override
    protected void getGLSLValues() {
        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "position");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "inputTextureCoordinate");

        mColorInputTextureUniform = GLES20.glGetUniformLocation(mProgramHandle, "inputImageTexture");
        mColorUniform = GLES20.glGetUniformLocation(mProgramHandle, "filterColor");
    }

    @Override
    public int getTextureTarget() {
        return GLES20.GL_TEXTURE_2D;
    }

    public void changeRenderStyle(boolean reset) {
        if (reset) {
            RenderStyle = 0;
        } else {
            RenderStyle++;
            if (RenderStyle > 4) {
                RenderStyle = 1;
            }
        }
        //Log.i("vvv", "changeRenderStyle: " + RenderStyle);
    }

    private void checkRenderStyle() {
        long ct = System.currentTimeMillis();
        long rt = ct - mShowTime;

        if (rt >= 0 && rt < 1200) {
            RenderStyle = ArmaniMaterialRenderStyle.kPORSSArmaniMaterialRenderStyleColor;

        } else if (rt >= 1200 && rt < 2300) {
            RenderStyle = ArmaniMaterialRenderStyle.kPORSSArmaniMaterialRenderStyleVerticalDuplicate;

        } else if (rt >= 2300 && rt < 3400) {
            RenderStyle = ArmaniMaterialRenderStyle.kPORSSArmaniMaterialRenderStyleAlignDuplicate;

        } else if (rt >= 3400 && rt < 4500) {
            RenderStyle = ArmaniMaterialRenderStyle.kPORSSArmaniMaterialRenderStyleMaterialDuplicate;

        } else {
            mShowTime = ct;
            RenderStyle = ArmaniMaterialRenderStyle.kPORSSArmaniMaterialRenderStyleColor;
        }
    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
//        checkRenderStyle();

        useProgram();
        bindTexture(textureId);
        bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);

        unbindGLSLValues();
        unbindTexture();
        disuseProgram();
    }

    @Override
    protected void bindTexture(int textureId) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(getTextureTarget(), textureId);
        GLES20.glUniform1i(mColorInputTextureUniform, 0);
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        switch (RenderStyle) {
            case ArmaniMaterialRenderStyle.kPORSSArmaniMaterialRenderStyleNormal:
                GLES20.glEnableVertexAttribArray(maPositionLoc);
                GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
                GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
                GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
                GLES20.glUniform4f(mColorUniform, 0, 0, 0, 0);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
                break;

            case ArmaniMaterialRenderStyle.kPORSSArmaniMaterialRenderStyleColor:
                GLES20.glEnableVertexAttribArray(maPositionLoc);
                GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
                GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
                GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
                GLES20.glUniform4f(mColorUniform, mColorRed[0] / 255.f, mColorRed[1] / 255.f, mColorRed[2] / 255.f, mColorRed[3] / 255.f);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
                break;

            case ArmaniMaterialRenderStyle.kPORSSArmaniMaterialRenderStyleVerticalDuplicate:
                imageVertices[0] = -1.0f;
                imageVertices[1] = -1.0f;
                imageVertices[2] = 1.0f;
                imageVertices[3] = -1.0f;
                imageVertices[4] = -1.0f;
                imageVertices[5] = 0.0f;
                imageVertices[6] = 1.0f;
                imageVertices[7] = 0.0f;


                noRotationTextureCoordinates[0] = 0.0f;
                noRotationTextureCoordinates[1] = 0.25f;
                noRotationTextureCoordinates[2] = 1.0f;
                noRotationTextureCoordinates[3] = 0.25f;
                noRotationTextureCoordinates[4] = 0.0f;
                noRotationTextureCoordinates[5] = 0.75f;
                noRotationTextureCoordinates[6] = 1.0f;
                noRotationTextureCoordinates[7] = 0.75f;

                bb2 = ByteBuffer.allocateDirect(imageVertices.length * 4);
                bb2.order(ByteOrder.nativeOrder());
                clipBuffer = bb2.asFloatBuffer();
                clipBuffer.put(imageVertices);
                clipBuffer.position(0);

                bb = ByteBuffer.allocateDirect(noRotationTextureCoordinates.length * 4);
                bb.order(ByteOrder.nativeOrder());
                noRotationClipBuffer = bb.asFloatBuffer();
                noRotationClipBuffer.put(noRotationTextureCoordinates);
                noRotationClipBuffer.position(0);

                GLES20.glEnableVertexAttribArray(maPositionLoc);
                GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 0, clipBuffer);
                GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
                GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, 0, noRotationClipBuffer);
                GLES20.glUniform4f(mColorUniform, mColorBlack[0] / 255.f, mColorBlack[1] / 255.f, mColorBlack[2] / 255.f, mColorBlack[3] / 255.f);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);   //下

                imageVertices[0] = -1.0f;
                imageVertices[1] = 0.0f;
                imageVertices[2] = 1.0f;
                imageVertices[3] = 0.0f;
                imageVertices[4] = -1.0f;
                imageVertices[5] = 1.0f;
                imageVertices[6] = 1.0f;
                imageVertices[7] = 1.0f;

                clipBuffer.put(imageVertices);
                clipBuffer.position(0);
                GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 0, clipBuffer);
                GLES20.glUniform4f(mColorUniform, mColorRed[0] / 255.f, mColorRed[1] / 255.f, mColorRed[2] / 255.f, mColorRed[3] / 255.f);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);   //上
                break;

            case ArmaniMaterialRenderStyle.kPORSSArmaniMaterialRenderStyleAlignDuplicate:
                imageVertices[0] = -1.0f;
                imageVertices[1] = -1.0f;
                imageVertices[2] = 0.0f;
                imageVertices[3] = -1.0f;
                imageVertices[4] = -1.0f;
                imageVertices[5] = 0.0f;
                imageVertices[6] = 0.0f;
                imageVertices[7] = 0.0f;

                bb2 = ByteBuffer.allocateDirect(imageVertices.length * 4);
                bb2.order(ByteOrder.nativeOrder());
                clipBuffer = bb2.asFloatBuffer();
                clipBuffer.put(imageVertices);
                clipBuffer.position(0);

                GLES20.glEnableVertexAttribArray(maPositionLoc);
                GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 0, clipBuffer);
                GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
                GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
                GLES20.glUniform4f(mColorUniform, mColorRed[0] / 255.f, mColorRed[1] / 255.f, mColorRed[2] / 255.f, mColorRed[3] / 255.f);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

                imageVertices[0] = 0.0f;
                imageVertices[1] = 0.0f;
                imageVertices[2] = 1.0f;
                imageVertices[3] = 0.0f;
                imageVertices[4] = 0.0f;
                imageVertices[5] = 1.0f;
                imageVertices[6] = 1.0f;
                imageVertices[7] = 1.0f;

                clipBuffer.put(imageVertices);
                clipBuffer.position(0);
                GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 0, clipBuffer);
                GLES20.glUniform4f(mColorUniform, mColorRed[0] / 255.f, mColorRed[1] / 255.f, mColorRed[2] / 255.f, mColorRed[3] / 255.f);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

                imageVertices[0] = 0.0f;
                imageVertices[1] = -1.0f;
                imageVertices[2] = 1.0f;
                imageVertices[3] = -1.0f;
                imageVertices[4] = 0.0f;
                imageVertices[5] = 0.0f;
                imageVertices[6] = 1.0f;
                imageVertices[7] = 0.0f;

                clipBuffer.put(imageVertices);
                clipBuffer.position(0);
                GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 0, clipBuffer);
                GLES20.glUniform4f(mColorUniform, mColorBlack[0] / 255.f, mColorBlack[1] / 255.f, mColorBlack[2] / 255.f, mColorBlack[3] / 255.f);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);    //右下

                imageVertices[0] = -1.0f;
                imageVertices[1] = 0.0f;
                imageVertices[2] = 0.0f;
                imageVertices[3] = 0.0f;
                imageVertices[4] = -1.0f;
                imageVertices[5] = 1.0f;
                imageVertices[6] = 0.0f;
                imageVertices[7] = 1.0f;

                clipBuffer.put(imageVertices);
                clipBuffer.position(0);
                GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 0, clipBuffer);
                GLES20.glUniform4f(mColorUniform, mColorWhite[0] / 255.f, mColorWhite[1] / 255.f, mColorWhite[2] / 255.f, mColorWhite[3] / 255.f);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);  //左上
                break;

            case ArmaniMaterialRenderStyle.kPORSSArmaniMaterialRenderStyleMaterialDuplicate:
                imageVertices[0] = 0.0f;
                imageVertices[1] = -1.0f;
                imageVertices[2] = 1.0f;
                imageVertices[3] = -1.0f;
                imageVertices[4] = 0.0f;
                imageVertices[5] = 0.0f;
                imageVertices[6] = 1.0f;
                imageVertices[7] = 0.0f;

                bb2 = ByteBuffer.allocateDirect(imageVertices.length * 4);
                bb2.order(ByteOrder.nativeOrder());
                clipBuffer = bb2.asFloatBuffer();
                clipBuffer.put(imageVertices);
                clipBuffer.position(0);

                GLES20.glEnableVertexAttribArray(maPositionLoc);
                GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 0, clipBuffer);
                GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
                GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
                GLES20.glUniform4f(mColorUniform, mColorRed[0] / 255.f, mColorRed[1] / 255.f, mColorRed[2] / 255.f, mColorRed[3] / 255.f);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

                imageVertices[0] = -1.0f;
                imageVertices[1] = 0.0f;
                imageVertices[2] = 0.0f;
                imageVertices[3] = 0.0f;
                imageVertices[4] = -1.0f;
                imageVertices[5] = 1.0f;
                imageVertices[6] = 0.0f;
                imageVertices[7] = 1.0f;

                clipBuffer.put(imageVertices);
                clipBuffer.position(0);
                GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 0, clipBuffer);
                GLES20.glUniform4f(mColorUniform, mColorRed[0] / 255.f, mColorRed[1] / 255.f, mColorRed[2] / 255.f, mColorRed[3] / 255.f);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

                if (mMaterialId > 0) {
                    GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 1);
                    GLES20.glBindTexture(getTextureTarget(), mMaterialId);
                    GLES20.glUniform1i(mColorInputTextureUniform, 1);
                }

                imageVertices[0] = -1.0f;
                imageVertices[1] = -1.0f;
                imageVertices[2] = 0.0f;
                imageVertices[3] = -1.0f;
                imageVertices[4] = -1.0f;
                imageVertices[5] = 0.0f;
                imageVertices[6] = 0.0f;
                imageVertices[7] = 0.0f;

                noRotationTextureCoordinates[0] = 0.0f;
                noRotationTextureCoordinates[1] = 1.0f;
                noRotationTextureCoordinates[2] = 1.0f;
                noRotationTextureCoordinates[3] = 1.0f;
                noRotationTextureCoordinates[4] = 0.0f;
                noRotationTextureCoordinates[5] = 0.0f;
                noRotationTextureCoordinates[6] = 1.0f;
                noRotationTextureCoordinates[7] = 0.0f;

                bb = ByteBuffer.allocateDirect(noRotationTextureCoordinates.length * 4);
                bb.order(ByteOrder.nativeOrder());
                noRotationClipBuffer = bb.asFloatBuffer();
                noRotationClipBuffer.put(noRotationTextureCoordinates);
                noRotationClipBuffer.position(0);

                clipBuffer.put(imageVertices);
                clipBuffer.position(0);
                GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 0, clipBuffer);
                GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, 0, noRotationClipBuffer);
                GLES20.glUniform4f(mColorUniform, 0, 0, 0, 0);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

                imageVertices[0] = 0.0f;
                imageVertices[1] = 0.0f;
                imageVertices[2] = 1.0f;
                imageVertices[3] = 0.0f;
                imageVertices[4] = 0.0f;
                imageVertices[5] = 1.0f;
                imageVertices[6] = 1.0f;
                imageVertices[7] = 1.0f;

                clipBuffer.put(imageVertices);
                clipBuffer.position(0);
                GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 0, clipBuffer);
                GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, 0, noRotationClipBuffer);
                GLES20.glUniform4f(mColorUniform, 0, 0, 0, 0);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
                break;
        }
    }

    @Override
    protected void drawArrays(int firstVertex, int vertexCount) {

    }

    @Override
    protected void unbindTexture() {

    }


    @Override
    public void releaseProgram() {
        GLES20.glDeleteProgram(mProgramHandle);
        mProgramHandle = -1;

        GLES20.glDeleteTextures(1, new int[]{mMaterialId}, 0);
    }

}
