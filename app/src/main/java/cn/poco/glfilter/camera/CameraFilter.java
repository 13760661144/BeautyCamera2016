package cn.poco.glfilter.camera;

import android.content.Context;
import android.opengl.GLES11Ext;

import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.glfilter.base.GlUtil;

public class CameraFilter extends DefaultFilter {

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
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 textureCoord;\n" +
                    "uniform samplerExternalOES uTexture;\n" +
                    "void main() {\n" +
                    "    gl_FragColor = texture2D(uTexture, textureCoord);\n" +
                    "}";

    public CameraFilter(Context context) {
        super(context);
    }

    @Override
    public int getTextureTarget() {
        return GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
    }

    @Override
    protected int createProgram(Context applicationContext) {
        return GlUtil.createProgram(vertexShaderCode, fragmentShaderCode);
    }

}
