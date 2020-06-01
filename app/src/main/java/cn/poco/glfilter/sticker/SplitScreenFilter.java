package cn.poco.glfilter.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.text.TextUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import cn.poco.dynamicSticker.v2.StickerSplitScreen;
import cn.poco.glfilter.base.AbsFilterGroup;
import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.glfilter.base.GlUtil;
import cn.poco.glfilter.composite.CompositeData;
import cn.poco.glfilter.composite.CompositeFilter;
import cn.poco.glfilter.composite.CompositeFilterGroup;

/**
 * Created by zwq on 2018/01/12 11:00.<br/><br/>
 */

public class SplitScreenFilter extends DefaultFilter {

    private static final String vertexShaderCode =
            "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "    gl_Position = aPosition;\n" +
                    "    vTextureCoord = aTextureCoord.xy;\n" +
                    "}";

    private static final String fragmentShaderCode =
            "precision mediump float;\n" +
                    "uniform sampler2D uTexture;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "    gl_FragColor = texture2D(uTexture, vTextureCoord);\n" +
                    "}";

    private int mViewWidth, mViewHeight;
    private float mWidthHeightRatio;
    private float mUpCut;
    private float[] mVertexPoint;
    private float[] mTexturePoint;

    private int mType;//0:normal, 1:(3.0f/4 - 9.0f/16), 2:(9.0f/16 - ?)
    private float mXOffset;
    private float mVertexXOffset;

    private int mRows = 1;
    private int mColumns = 1;

    private float[] mSpiltVertexPoint;
    private float[] mSpiltTexPoint;
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTexBuffer;

    private float[] mOriMaskTexPoint;
    private float[] mMaskTexPoint;
    private FloatBuffer mMaskTexBuffer;
    private float[] mMaskVertexPoint;
    private FloatBuffer mMaskVertexBuffer;

    private CompositeData mCompositeData;
    private CompositeFilterGroup mCompositeFilterGroup;
    private CompositeFilter mCompositeFilter;

    private int mStickerId = -1;
    private StickerSplitScreen.SplitData mSplitData;
    private ArrayList<Integer> mTempMaskTextureId;

    public SplitScreenFilter(Context context) {
        super(context);
    }

    @Override
    protected int createProgram(Context context) {
        //return GlUtil.createProgram(context, R.raw.vert_spilt_screen, R.raw.frag_spilt_screen);
        return GlUtil.createProgram(vertexShaderCode, fragmentShaderCode);
    }

    @Override
    public void setViewSize(int width, int height) {
        super.setViewSize(width, height);
        if (width <= 0 || height <= 0) return;

        mViewWidth = Math.round(width / mRenderScale);
        mViewHeight = Math.round(height / mRenderScale);

        if (mWidthHeightRatio == 0) {
            setRatio(width * 1.0f / height, 0);//default
        } else {
            setRatio(mWidthHeightRatio, mUpCut);
        }
    }

    /**
     * 设置比例
     *
     * @param width_height_ratio 正常时的宽高比(9:16 3:4 1:1等)
     * @param upCut              1:1时顶部裁剪高度
     */
    public void setRatio(float width_height_ratio, float upCut) {
        if (width_height_ratio <= 0.1f) {
            return;
        }
        mWidthHeightRatio = width_height_ratio;
        mUpCut = upCut;
        if (mViewWidth < 1 || mViewHeight < 1) return;

        float ratio1 = 0.0f;//相对于顶部的比例
        float ratio2 = 0.0f;//相对于顶部的比例
        if (mUpCut > 0) {// 0.5-0.75 浮动处理 黑边问题
            ratio1 = (mUpCut - 0.75f) / mViewHeight;
            ratio2 = (mUpCut + 0.75f + mViewWidth / width_height_ratio) / mViewHeight;
        } else {
            ratio2 = (mViewWidth / width_height_ratio) / mViewHeight;
        }
//        Log.i(TAG, "setRatioAndOrientation: upCut:" + upCut + ", " + ratio1 + ", " + ratio2);
        if (mVertexPoint == null) {//图像顶点坐标
            mVertexPoint = new float[8];
        }
        mVertexPoint[0] = -1.0f;
        mVertexPoint[1] = -((ratio1 * 2) - 1.0f);
        mVertexPoint[2] = 1.0f;
        mVertexPoint[3] = mVertexPoint[1];

        mVertexPoint[4] = mVertexPoint[0];
        mVertexPoint[5] = -((ratio2 * 2) - 1.0f);//0.005
        mVertexPoint[6] = mVertexPoint[2];
        mVertexPoint[7] = mVertexPoint[5];

        if (mTexturePoint == null) {//图像纹理坐标
            mTexturePoint = new float[8];
        }
        mTexturePoint[0] = 0.0f;
        mTexturePoint[1] = 1.0f - ratio1;
        mTexturePoint[2] = 1.0f;
        mTexturePoint[3] = mTexturePoint[1];

        mTexturePoint[4] = mTexturePoint[0];
        mTexturePoint[5] = 1.0f - ratio2;
        mTexturePoint[6] = mTexturePoint[2];
        mTexturePoint[7] = mTexturePoint[5];

        mType = 0;
        mXOffset = 0.0f;
        mVertexXOffset = 0.0f;
        if (mWidthHeightRatio < 3.0f / 4 && mWidthHeightRatio > 9.0f / 16) {//15:9
            mType = 1;
            mXOffset = (mWidth - (mWidth / (3.0f / 4) * mWidthHeightRatio)) / mWidth / 2.0f - 0.001f;
            mVertexXOffset = (mVertexPoint[2] - mVertexPoint[0]) * mXOffset;

        } else if (mWidthHeightRatio < 9.0f / 16) {//18:9
            mType = 2;
            mXOffset = (mWidth - (mWidth / (9.0f / 16) * mWidthHeightRatio)) / mWidth / 2.0f - 0.001f;//0.001误差
            mVertexXOffset = (mVertexPoint[2] - mVertexPoint[0]) * mXOffset;
        }
        if (mUpCut > 0) {
            ratio2 = 1.0f - ratio2;
        } else {
            ratio1 = 0.0f;
            ratio2 = 0.0f;
        }
        if (mOriMaskTexPoint == null) {//镜头分块纹理坐标
            mOriMaskTexPoint = new float[8];
        }
        mOriMaskTexPoint[0] = mXOffset;
        mOriMaskTexPoint[1] = 1.0f - ratio1;
        mOriMaskTexPoint[2] = 1.0f - mXOffset;
        mOriMaskTexPoint[3] = mOriMaskTexPoint[1];

        mOriMaskTexPoint[4] = mOriMaskTexPoint[0];
        mOriMaskTexPoint[5] = 0.0f + ratio2;
        mOriMaskTexPoint[6] = mOriMaskTexPoint[2];
        mOriMaskTexPoint[7] = mOriMaskTexPoint[5];

        if (mMaskTexPoint == null) {//色块纹理坐标
            mMaskTexPoint = new float[8];
        }
        mMaskTexPoint[0] = 0.0f;
        mMaskTexPoint[1] = 0.0f;
        mMaskTexPoint[2] = 1.0f;
        mMaskTexPoint[3] = 0.0f;

        mMaskTexPoint[4] = 0.0f;
        mMaskTexPoint[5] = 1.0f;
        mMaskTexPoint[6] = 1.0f;
        mMaskTexPoint[7] = 1.0f;

        if (mMaskVertexPoint == null) {
            mMaskVertexPoint = new float[8];
        }
        mMaskVertexPoint[0] = mVertexPoint[0] + mVertexXOffset;
        mMaskVertexPoint[1] = mVertexPoint[1];
        mMaskVertexPoint[2] = mVertexPoint[2] - mVertexXOffset;
        mMaskVertexPoint[3] = mMaskVertexPoint[1];

        mMaskVertexPoint[4] = mMaskVertexPoint[0];
        mMaskVertexPoint[5] = mVertexPoint[5];
        mMaskVertexPoint[6] = mMaskVertexPoint[2];
        mMaskVertexPoint[7] = mMaskVertexPoint[5];
    }

    @Override
    public boolean isFilterEnable() {
        return super.isFilterEnable();
    }

    @Override
    public void setFilterGroups(AbsFilterGroup... filterGroups) {
        if (filterGroups != null && filterGroups.length > 0) {
            AbsFilterGroup group = filterGroups[0];
            if (group != null && group instanceof CompositeFilterGroup) {
                mCompositeFilterGroup = (CompositeFilterGroup) group;
            }
        }
    }

    public void setStickerId(int stickerId) {
        if (mStickerId != -1 && stickerId != mStickerId) {
            clearData();
        }
        mStickerId = stickerId;
    }

    public int getStickerId() {
        return mStickerId;
    }

    public void clearData() {
        if (mSplitData != null) {
            clearTask();
            releaseMaskTextureId();
            mSplitData = null;
        }
    }

    public void checkFilterEnable() {
        if (mStickerId != -1 && mSplitData != null && !isFilterEnable()) {
            setFilterEnable(true);
        }
    }

    public void setSpiltScreenData(StickerSplitScreen.SplitData splitData) {
        if (splitData == null) {
            mRows = 1;
            mColumns = 1;
            mSplitData = null;
            return;
        }
        if (splitData == mSplitData) return;
        clearTask();
        mSplitData = splitData;

        boolean needRunTask = true;
        if (mSplitData.maskData != null) {
            StickerSplitScreen.MaskData maskData = null;
            for (int i = 0; i < mSplitData.maskData.length; i++) {
                maskData = mSplitData.maskData[i];
                if (maskData == null || TextUtils.isEmpty(maskData.pic)) {
                    continue;
                }
                if (maskData.picTextureId <= 0) {
                    initTask(0, i, maskData.pic, needRunTask);
                    needRunTask = false;
                }
            }
        }

        //限制最多6x6
        if (mSplitData.rows < 1) {
            mRows = 1;
        } else if (mSplitData.rows > 6) {
            mRows = 6;
        } else {
            mRows = mSplitData.rows;
        }
        if (mSplitData.columns < 1) {
            mColumns = 1;
        } else if (mSplitData.columns > 6) {
            mColumns = 6;
        } else {
            mColumns = mSplitData.columns;
        }
    }

    private void releaseMaskTextureId() {
        if (mTempMaskTextureId != null && !mTempMaskTextureId.isEmpty()) {
            int[] textureArr = null;
            synchronized (mTempMaskTextureId) {
                textureArr = new int[mTempMaskTextureId.size()];
                for (int i = 0; i < mTempMaskTextureId.size(); i++) {
                    textureArr[i] = mTempMaskTextureId.get(i);
                }
                mTempMaskTextureId.clear();
            }
            if (textureArr != null) {
                GLES20.glDeleteTextures(textureArr.length, textureArr, 0);
            }
        }
    }

    private void initTask(int group, int index, String imgPath, boolean needRunTask) {
        addTaskToQueue(new SplitScreenTextureTask(this, group, index, imgPath, new SplitScreenTextureTask.TaskCallback() {
            @Override
            public void onTaskCallback(int group, int index, Bitmap bitmap) {
                int textureId = -1;
                float ratio = 1.0f;
                if (bitmap != null && !bitmap.isRecycled()) {
                    textureId = GlUtil.createTexture(GLES20.GL_TEXTURE_2D, bitmap, GLES20.GL_NEAREST, GLES20.GL_NEAREST,
                            GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);
                    ratio = bitmap.getWidth() * 1.0f / bitmap.getHeight();
                    bitmap.recycle();
                    bitmap = null;
                }

                if (group >= 0 && index >= 0 && textureId > 0) {
                    if (mSplitData != null && mSplitData.maskData != null && index < mSplitData.maskData.length) {
                        mSplitData.maskData[index].picTextureId = textureId;
                        mSplitData.maskData[index].picRatio = ratio;
                        if (mTempMaskTextureId == null) {
                            mTempMaskTextureId = new ArrayList<>();
                        }
                        mTempMaskTextureId.add(textureId);
                    }
                }
            }
        }));
        if (needRunTask) {
            runTask();
        }
    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        //super.onDraw(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);

        runTask();

        int compositeMode = 0;
        float opaqueness = 1.0f;
        int maskTextureId = 0;
        boolean needBindBuffer = false;
        if (mFilterEnable && mCompositeFilterGroup != null && mSplitData != null && mSplitData.maskData != null) {
            int index = 0;
            if (index >= 0 && index < mSplitData.maskData.length) {
                compositeMode = mSplitData.maskData[index].compositeMode;
                opaqueness = mSplitData.maskData[index].opaqueness;
                maskTextureId = mSplitData.maskData[index].picTextureId;

                if (compositeMode > 0 && maskTextureId > 0) {
                    needBindBuffer = true;
                }
            }
        }
        float itemRatio = (mWidth * 1.0f / mColumns) / (mWidth / mWidthHeightRatio / mRows);

        useProgram();
        bindTexture(textureId);

        if (mRows > 1 || mColumns > 1) {
            if (mSpiltTexPoint == null) {
                mSpiltTexPoint = new float[8];
            }
            if (mRows > 1 && mColumns == 1) {
                float stepTx = (mTexturePoint[2] - mTexturePoint[0] - mXOffset * 2.0f) / mColumns;
                float stepTy = (mTexturePoint[5] - mTexturePoint[1]) / mRows;

                mSpiltTexPoint[0] = mTexturePoint[0];
                mSpiltTexPoint[1] = ((mTexturePoint[5] + mTexturePoint[1]) - stepTy) / 2.0f;//取y中间部分
                mSpiltTexPoint[2] = mSpiltTexPoint[0] + stepTx;
                mSpiltTexPoint[3] = mSpiltTexPoint[1];

                mSpiltTexPoint[4] = mSpiltTexPoint[0];
                mSpiltTexPoint[5] = mSpiltTexPoint[1] + stepTy;
                mSpiltTexPoint[6] = mSpiltTexPoint[2];
                mSpiltTexPoint[7] = mSpiltTexPoint[5];

            } else if (mRows == 1 && mColumns > 1) {
                float stepTx = (mTexturePoint[2] - mTexturePoint[0] - mXOffset * 2.0f) / mColumns;
                float stepTy = (mTexturePoint[5] - mTexturePoint[1]) / mRows;

                mSpiltTexPoint[0] = ((mTexturePoint[2] + mTexturePoint[0]) - stepTx) / 2.0f;//取x中间部分
                mSpiltTexPoint[1] = mTexturePoint[1];
                mSpiltTexPoint[2] = mSpiltTexPoint[0] + stepTx;
                mSpiltTexPoint[3] = mSpiltTexPoint[1];

                mSpiltTexPoint[4] = mSpiltTexPoint[0];
                mSpiltTexPoint[5] = mSpiltTexPoint[1] + stepTy;
                mSpiltTexPoint[6] = mSpiltTexPoint[2];
                mSpiltTexPoint[7] = mSpiltTexPoint[5];

            } else {
                //float stepRatio = (mWidth * 1.0f / mColumns) / (mWidth / mWidthHeightRatio / mRows);
                float dx = 0.0f;
                float dy = 0.0f;
                if (itemRatio > mWidthHeightRatio) { //裁减高度 h
                    float w = Math.abs(mTexturePoint[2] - mTexturePoint[0]);
                    float dataRatio = mWidth * 1.0f / mHeight;//镜头数据宽高比
                    float realRatio = mWidthHeightRatio;

                    if (mType == 1) {
                        realRatio = 3.0f / 4;
                        dataRatio = realRatio;
                    } else if (mType == 2) {
                        realRatio = 9.0f / 16;
                        dataRatio = realRatio;
                    }
                    dy = (1.0f / itemRatio - 1.0f / realRatio) * w * dataRatio / 2.0f;

                } else { //w
                    float h = Math.abs(mTexturePoint[5] - mTexturePoint[1]);
                    float dataRatio = mHeight * 1.0f / mWidth;
                    float realRatio = mWidthHeightRatio;

                    if (mType == 1) {
                        realRatio = 3.0f / 4;
                        dataRatio = 1.0f / realRatio;
                    } else if (mType == 2) {
                        realRatio = 9.0f / 16;
                        dataRatio = 1.0f / realRatio;
                    }
                    dx = (realRatio - itemRatio) * h * dataRatio / 2.0f;
                }

                mSpiltTexPoint[0] = mTexturePoint[0] + dx;
                mSpiltTexPoint[1] = mTexturePoint[1] + dy;
                mSpiltTexPoint[2] = mTexturePoint[2] - dx;
                mSpiltTexPoint[3] = mSpiltTexPoint[1];

                mSpiltTexPoint[4] = mSpiltTexPoint[0];
                mSpiltTexPoint[5] = mTexturePoint[5] - dy;
                mSpiltTexPoint[6] = mSpiltTexPoint[2];
                mSpiltTexPoint[7] = mSpiltTexPoint[5];
            }
            if (mTexBuffer == null || mTexBuffer.capacity() != mSpiltTexPoint.length * 4) {
                mTexBuffer = GlUtil.createFloatBuffer(mSpiltTexPoint);
            } else {
                mTexBuffer.clear();
                mTexBuffer.put(mSpiltTexPoint);
                mTexBuffer.position(0);
            }

            float stepVx = (mVertexPoint[2] - mVertexPoint[0] - mVertexXOffset * 2.0f) / mColumns;
            float stepVy = (mVertexPoint[5] - mVertexPoint[1]) / mRows;

            for (int i = 0; i < mRows; i++) {
                for (int j = 0; j < mColumns; j++) {
                    if (mSpiltVertexPoint == null) {
                        mSpiltVertexPoint = new float[8];
                    }
                    mSpiltVertexPoint[0] = mVertexXOffset + (stepVx * j) + mVertexPoint[0];
                    mSpiltVertexPoint[1] = (stepVy * i) + mVertexPoint[1];
                    mSpiltVertexPoint[2] = mSpiltVertexPoint[0] + stepVx;
                    mSpiltVertexPoint[3] = mSpiltVertexPoint[1];

                    mSpiltVertexPoint[4] = mSpiltVertexPoint[0];
                    mSpiltVertexPoint[5] = mSpiltVertexPoint[1] + stepVy;
                    mSpiltVertexPoint[6] = mSpiltVertexPoint[2];
                    mSpiltVertexPoint[7] = mSpiltVertexPoint[5];

                    if (mVertexBuffer == null || mVertexBuffer.capacity() != mSpiltVertexPoint.length * 4) {
                        mVertexBuffer = GlUtil.createFloatBuffer(mSpiltVertexPoint);
                    } else {
                        mVertexBuffer.clear();
                        mVertexBuffer.put(mSpiltVertexPoint);
                        mVertexBuffer.position(0);
                    }

                    bindGLSLValues(mvpMatrix, mVertexBuffer, coordsPerVertex, vertexStride, texMatrix, mTexBuffer, texStride);
                    drawArrays(firstVertex, vertexCount);
                }
            }
        } else {
            bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);
            drawArrays(firstVertex, vertexCount);
        }
        unbindGLSLValues();
        unbindTexture();
        disuseProgram();

        if (needBindBuffer && mGLFramebuffer != null) {
            mGLFramebuffer.bindNext(true);
            textureId = mGLFramebuffer.getPreviousTextureId();
            if (mMaskTexPoint == null) {
                mMaskTexPoint = new float[8];

                mMaskTexPoint[0] = 0.0f;
                mMaskTexPoint[1] = 0.0f;
                mMaskTexPoint[2] = 1.0f;
                mMaskTexPoint[3] = 0.0f;

                mMaskTexPoint[4] = 0.0f;
                mMaskTexPoint[5] = 1.0f;
                mMaskTexPoint[6] = 1.0f;
                mMaskTexPoint[7] = 1.0f;
            }
            if (mMaskTexBuffer == null || mMaskTexBuffer.capacity() != mMaskTexPoint.length * 4) {
                mMaskTexBuffer = GlUtil.createFloatBuffer(mMaskTexPoint);
            } else {
                mMaskTexBuffer.clear();
                mMaskTexBuffer.put(mMaskTexPoint);
                mMaskTexBuffer.position(0);
            }

            if (mMaskVertexPoint == null) {
                mMaskVertexPoint = new float[8];

                mMaskVertexPoint[0] = -1.0f;
                mMaskVertexPoint[1] = 1.0f;
                mMaskVertexPoint[2] = 1.0f;
                mMaskVertexPoint[3] = mMaskVertexPoint[1];

                mMaskVertexPoint[4] = mMaskVertexPoint[0];
                mMaskVertexPoint[5] = 1.0f;
                mMaskVertexPoint[6] = mMaskVertexPoint[2];
                mMaskVertexPoint[7] = mMaskVertexPoint[5];
            }
            if (mMaskVertexBuffer == null || mMaskVertexBuffer.capacity() != mMaskVertexPoint.length * 4) {
                mMaskVertexBuffer = GlUtil.createFloatBuffer(mMaskVertexPoint);
            } else {
                mMaskVertexBuffer.clear();
                mMaskVertexBuffer.put(mMaskVertexPoint);
                mMaskVertexBuffer.position(0);
            }

            if (mTexBuffer == null || mTexBuffer.capacity() != mOriMaskTexPoint.length * 4) {
                mTexBuffer = GlUtil.createFloatBuffer(mOriMaskTexPoint);
            } else {
                mTexBuffer.clear();
                mTexBuffer.put(mOriMaskTexPoint);
                mTexBuffer.position(0);
            }

            drawComposite(maskTextureId, compositeMode, opaqueness, mMaskTexBuffer,
                    mvpMatrix, mMaskVertexBuffer, firstVertex, vertexCount, coordsPerVertex,
                    vertexStride, texMatrix, mTexBuffer, textureId, texStride);
        }
    }

    private void drawComposite(int maskTextureId, int compositeMode, float alpha, FloatBuffer maskTexBuffer,
                               float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex,
                               int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        if (mCompositeFilterGroup != null) {
            if (mCompositeData == null) {
                mCompositeData = new CompositeData();
            }
            if (mCompositeData != null) {
                mCompositeData.setData(maskTextureId, compositeMode, alpha, maskTexBuffer, -1, null);
            }
            mCompositeFilter = mCompositeFilterGroup.setCompositeFilterData(mCompositeData);
            if (mCompositeFilter != null) {
                mCompositeFilter.onDraw(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);
                mCompositeFilter = null;
            }
        }
    }

    @Override
    public void releaseProgram() {
        super.releaseProgram();

        releaseMaskTextureId();
        mTempMaskTextureId = null;

        mSpiltVertexPoint = null;
        mSpiltTexPoint = null;
        mVertexBuffer = null;
        mTexBuffer = null;

        mMaskTexPoint = null;
        mMaskTexBuffer = null;

        mCompositeData = null;
        mCompositeFilterGroup = null;
        mCompositeFilter = null;
        mSplitData = null;
    }
}
