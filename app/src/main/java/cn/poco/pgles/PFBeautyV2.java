package cn.poco.pgles;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.LinkedList;

/**
 * Created by admin on 2016/9/18.
 */
public class PFBeautyV2 {

    private String TAG = "PFBeautyV2";

    private final LinkedList<Runnable> mRunOnDraw;
    protected int mGLProgId;
    protected static int mOutputWidth;
    protected static int mOutputHeight;
    protected boolean mIsInitialized;
    private Context mContext;

    private int muTexMatrixLoc;
    private int muMVPMatrixLoc;

    private int mStep1Loc;
    private int mStep2Loc;
    private int mStep3Loc;
    protected int mTexWidthLoc;
    protected int mTexHeightLoc;
    protected int mDeltaLoc;
    protected int mPercentLoc;
    protected int mBeautyLoc;
    protected int mTableTextureLoc;
    private int mTableTextureId;

    private int ProcessWidth = 1080;
    private int ProcessHeight = 1440;

    private float mPrecentVal = 0.75f;
    private float mDeltaVal = 0.0014f;
    private float mStep1Val = 12.5f;
    private float mStep2Val = 6.5f;
    private float mStep3Val = 0.5f;

    public PFBeautyV2(Context context){
        mContext = context;
        mRunOnDraw = new LinkedList<Runnable>();
        mTableTextureId = PFUtil.createTexture(GLES20.GL_TEXTURE_2D);
    }

    public void onInit(){
        mGLProgId = PGLNativeIpl.loadBeautyProgram();
        mIsInitialized = true;
        getGLSLValues();
    }

    public final void init(){
        if (mIsInitialized){return;}

        onInit();
        mIsInitialized = true;
    }

    public final void destroy(){
        if (!mIsInitialized) { return;}
        mIsInitialized = false;
        if (mGLProgId > 0){
            GLES20.glDeleteProgram(mGLProgId);
        }
    }

    protected void getGLSLValues() {
        muTexMatrixLoc = GLES20.glGetUniformLocation(mGLProgId, "uTexMatrix");
        muMVPMatrixLoc = GLES20.glGetUniformLocation(mGLProgId, "uMVPMatrix");

        mStep1Loc = GLES20.glGetUniformLocation(mGLProgId, "step1");
        mStep2Loc = GLES20.glGetUniformLocation(mGLProgId, "step2");
        mStep3Loc = GLES20.glGetUniformLocation(mGLProgId, "step3");
        mTexWidthLoc = GLES20.glGetUniformLocation(mGLProgId, "texelWidth");
        mTexHeightLoc = GLES20.glGetUniformLocation(mGLProgId, "texelHeight");
        mDeltaLoc = GLES20.glGetUniformLocation(mGLProgId, "delta");
        mPercentLoc = GLES20.glGetUniformLocation(mGLProgId, "percent");
        mTableTextureLoc = GLES20.glGetUniformLocation(mGLProgId, "tableTexture");
        mBeautyLoc = GLES20.glGetUniformLocation(mGLProgId, "isBeauty");
    }


    protected void bindGLSLValues(boolean isBeauty){
        GLES20.glUniform1f(mTexWidthLoc, 1f/ProcessHeight);
        GLES20.glUniform1f(mTexHeightLoc, 1f/ProcessWidth);
        GLES20.glUniform1f(mPercentLoc, mPrecentVal);

        //此处要根据PREVIEW SIZE来设置这些参数
        GLES20.glUniform1f(mDeltaLoc, mDeltaVal);
        GLES20.glUniform1f(mStep1Loc, mStep1Val);
        GLES20.glUniform1f(mStep2Loc, mStep2Val);
        GLES20.glUniform1f(mStep3Loc, mStep3Val);
        GLES20.glUniform1f(mBeautyLoc, isBeauty ? 1.0f : 0.0f);
    }

    public void onOutputSizeChanged(final int width, final int height){
        mOutputHeight = height;
        mOutputWidth = width;
    }

    public void draw(final int textureid, float[] transformMatrix, float[] viewProjectionMatrix, FloatBuffer cubeBuffer, FloatBuffer textureBuffer, boolean isBeauty){
        GLES20.glUseProgram(mGLProgId);
        runPendingOnDrawTasks();
        if (!mIsInitialized) {
            return;
        }

        GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, transformMatrix, 0);
        GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, viewProjectionMatrix, 0);

        activeAttribute("a_position", 3, GLES20.GL_FLOAT, 0, cubeBuffer);
        activeAttribute("a_textureCoord0", 2, GLES20.GL_FLOAT, 0, textureBuffer);

        if (textureid > 0) {
            bindSampler("uTexture", textureid, GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        }

        bindGLSLValues(isBeauty);

        GLES20.glClearColor(0f, 0f, 0f, 1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        disableAtrribute("a_position");
        disableAtrribute("a_textureCoord0");

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        GLES20.glUseProgram(0);
    }

    protected void runPendingOnDrawTasks() {
        while (!mRunOnDraw.isEmpty()) {
            mRunOnDraw.removeFirst().run();
        }
    }

    protected void bindSampler(String name, int textureid, int textureTarget) {
        int location = GLES20.glGetUniformLocation(mGLProgId, name);
        if (location >= 0)
        {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(textureTarget, textureid);

            //very important instruction
            GLES20.glUniform1i(location, 0);
        }

        PGLNativeIpl.nativeBindBeautyTexture(mTableTextureLoc, mTableTextureId);
    }

    protected void activeAttribute(String name, int size, int type, int stride, final Buffer buffer) {
        int location = GLES20.glGetAttribLocation(mGLProgId, name);
        if (location >= 0)
        {
            buffer.position(0);
            GLES20.glVertexAttribPointer(location, size, type, false, stride, buffer);
            GLES20.glEnableVertexAttribArray(location);
        }
    }

    protected void disableAtrribute(String name) {
        int location = GLES20.glGetAttribLocation(mGLProgId, name);
        if (location >= 0)
        {
            GLES20.glDisableVertexAttribArray(location);
        }
    }

    //在bindGLSLValues方法之前一定要set这些参数
    public void setFBOSize(int FBOWidth, int FBOHeight){
//        Log.e(TAG, "set FBOSIZE");
        ProcessWidth = FBOWidth;
        ProcessHeight = FBOHeight;
        int MINVAL = Math.min(FBOWidth, FBOHeight);
        if (MINVAL <= 720){
            mDeltaVal = 0.0010f;  mStep1Val = 10.5f;
            mStep2Val = 5.5f;   mStep3Val = 0.5f;
        }else if (MINVAL <= 1080){
            mDeltaVal = 0.0014f;  mStep1Val = 12.5f;
            mStep2Val = 6.5f;   mStep3Val = 0.5f;
        }else{
            mDeltaVal = 0.0018f;  mStep1Val = 16.5f;
            mStep2Val = 8.5f;   mStep3Val = 0.5f;
        }
    }

    public void setSize(int width, int height) {
//        texlwidth = 1.0f / (float) width;
//        texlheight = 1.0f / (float) height;
//        int minval = Math.min(width, height);
//        if (minval <= 640) {
//            step1 = 3.5f;
//            step2 = 1.5f;
//            delta = 0.0005f;
//        } else if (minval <= 1000) {
//            step1 = 5.5f;
//            step2 = 2.5f;
//            delta = 0.0005f;
//        } else if (minval <= 1100) {
//            step1 = 11.5f;
//            step2 = 5.5f;
//            delta = 0.0007f;
//        } else {
//            step1 = 18.5f;
//            step2 = 8.5f;
//            delta = 0.0009f;
//        }
//		System.out.println("width:"+width+", height:"+height);
    }

}
