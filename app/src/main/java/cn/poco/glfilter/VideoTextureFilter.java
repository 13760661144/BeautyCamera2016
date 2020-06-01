package cn.poco.glfilter;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

import cn.poco.glfilter.base.GlUtil;
import cn.poco.glfilter.camera.CameraFilter;

/**
 * Created by zwq on 2016/09/01 11:07.<br/><br/>
 */
public class VideoTextureFilter extends CameraFilter {

    private static final String TAG = "VideoTextureFilter";

    private static final String vertexShaderCode =
            "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "uniform mat4 uMVPMatrix;\n" +
                    "uniform mat4 uTexMatrix;\n" +
                    "varying vec2 textureCoord;\n" +
                    "void main() {\n" +
                    "    gl_Position = uMVPMatrix * aPosition;\n" +
                    "    textureCoord = (uTexMatrix * aTextureCoord).xy;\n" +
                    "}";

    private static final String fragmentShaderCode =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "uniform sampler2D uTexture;\n" +
                    "uniform samplerExternalOES uVideoTexture;\n" +
                    "uniform float ratioY;\n" +
                    "varying vec2 textureCoord;\n" +
                    "vec2 textureCoordFlip;\n" +
                    "void main() {\n" +
                    "    textureCoordFlip = vec2(textureCoord.x, 1.0 - textureCoord.y);\n" +
                    "    vec4 cameraTexture = texture2D(uTexture, textureCoordFlip);\n" +
                    "    vec4 videoTexture;\n" +
                    "    float y_coord = textureCoord.y * ratioY;\n" +
                    "    if (y_coord > 1.0) {\n" +
                    "       videoTexture = vec4(0.0);\n" +
                    "    } else {\n" +
                    "       videoTexture = texture2D(uVideoTexture, vec2(textureCoord.x, y_coord));\n" +
                    "    }\n" +
                    "    gl_FragColor = vec4(cameraTexture + videoTexture - cameraTexture * videoTexture);\n" +
                    "}";

    private static final String fragmentShaderCode2 =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "uniform sampler2D uTexture;\n" +
                    "uniform samplerExternalOES uVideoTexture;\n" +
                    "uniform float ratioY;\n" +
                    "uniform float coordRectF[4];\n " +
                    "varying vec2 textureCoord;\n" +
                    "vec2 textureCoordFlip;\n" +
                    "void main() {\n" +
                    "    textureCoordFlip = textureCoord;\n" +
                    "    if(textureCoordFlip.x >= coordRectF[0] && textureCoordFlip.x <= coordRectF[2]) {\n" +
                    "       textureCoordFlip.x = (textureCoordFlip.x - coordRectF[0]) / (coordRectF[2] - coordRectF[0]);\n" +
                    "    } else {\n" +
                    "       textureCoordFlip.x = -1.0;\n" +
                    "    }\n" +
                    "    if(textureCoordFlip.y >= coordRectF[1] && textureCoordFlip.y <= coordRectF[3]) {\n" +
                    "       textureCoordFlip.y = (textureCoordFlip.y - coordRectF[1]) / (coordRectF[3] - coordRectF[1]);\n" +
                    "    } else {\n" +
                    "       textureCoordFlip.y = 2.0;\n" +
                    "    }\n" +
                    "    textureCoordFlip.y = 1.0 - textureCoordFlip.y;\n" +
                    "    vec4 cameraTexture;\n" +
                    "    if(textureCoordFlip.x == -1.0 || textureCoordFlip.y == -1.0) {\n" +
                    "       cameraTexture = vec4(0.0);\n" +
                    "    } else {\n" +
                    "       cameraTexture = texture2D(uTexture, textureCoordFlip);\n" +
                    "    }\n" +
                    "    vec4 videoTexture;\n" +
                    "    float y_coord = textureCoord.y * ratioY;\n" +
                    "    if (y_coord > 1.0) {\n" +
                    "       videoTexture = vec4(0.0);\n" +
                    "    } else {\n" +
                    "       videoTexture = texture2D(uVideoTexture, vec2(textureCoord.x, y_coord));\n" +
                    "    }\n" +
                    "    gl_FragColor = vec4(cameraTexture + videoTexture - cameraTexture * videoTexture);\n" +
                    "}";

    private int mVideoTextureId = -1;
    private float[] mVideoTextureMatrix;
    private int muVideoTextureLoc;
    private int muRatioYLoc;
    private int muCoordRectF;

    private float ratioY = 1.0f;
    private float[] mCoordRect;
    private int mVideoTextureWidth;
    private int mVideoTextureHeight;

    public VideoTextureFilter(Context context) {
        super(context);
        mCoordRect = new float[]{0.65f, 0.05f / (4.0f / 3), 0.95f, 0.45f / (4.0f / 3)};
    }

    @Override
    protected int createProgram(Context applicationContext) {
        return GlUtil.createProgram(vertexShaderCode, fragmentShaderCode);
    }

    @Override
    protected void getGLSLValues() {
        super.getGLSLValues();
        muVideoTextureLoc = GLES20.glGetUniformLocation(mProgramHandle, "uVideoTexture");
        muRatioYLoc = GLES20.glGetUniformLocation(mProgramHandle, "ratioY");
        muCoordRectF = GLES20.glGetUniformLocation(mProgramHandle, "coordRectF");
    }

    public void setVideoTextureSize(int width, int height) {
        if (width != 0 && height != 0 && mWidth != 0 && mHeight != 0) {
            if (width != mVideoTextureWidth || height != mVideoTextureHeight) {
                mVideoTextureWidth = width;
                mVideoTextureHeight = height;
                ratioY = (mHeight * 1.0f / mWidth) * mVideoTextureWidth / mVideoTextureHeight;
//                Log.i("bbb", "width:"+width+", height:"+height+", ratioY:"+ratioY);
            }
        }
    }

    public void setVideoTextureData(int textureId, float[] texMatrix) {
        if (textureId != mVideoTextureId) {
            mVideoTextureId = textureId;
//            Log.i("bbb", "mVideoTextureId:"+mVideoTextureId);
        }
        mVideoTextureMatrix = texMatrix;
    }

    public void setVideoTextureId(int videoTextureId) {
        if (videoTextureId != mVideoTextureId) {
            mVideoTextureId = videoTextureId;
//            Log.i("bbb", "mVideoTextureId:"+mVideoTextureId);
        }
    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        if (mVideoTextureId > 0 && mVideoTextureMatrix != null) {
            texMatrix = mVideoTextureMatrix;
        }
        super.onDraw(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);
    }

    @Override
    protected void bindTexture(int textureId) {
        mDefaultTextureId = textureId;
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(muTextureLoc, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(getTextureTarget(), mVideoTextureId);
        GLES20.glUniform1i(muVideoTextureLoc, 1);
//        GlUtil.checkGlError("bindTexture");
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex,
                                  int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, texMatrix, 0);
        GLES20.glUniform1f(muRatioYLoc, ratioY);
        GLES20.glUniform1fv(muCoordRectF, mCoordRect.length, mCoordRect, 0);

        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
    }

    @Override
    protected void unbindTexture() {
        GLES20.glBindTexture(getTextureTarget(), 0);
    }
}
