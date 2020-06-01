package cn.poco.glfilter.color;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import cn.poco.gldraw2.FaceDataHelper;
import cn.poco.glfilter.base.AbsFilterGroup;
import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.glfilter.base.GLFramebuffer;
import cn.poco.glfilter.base.GlUtil;
import cn.poco.glfilter.composite.CompositeData;
import cn.poco.glfilter.composite.CompositeFilter;
import cn.poco.glfilter.composite.CompositeFilterGroup;
import cn.poco.image.PocoFace;
import cn.poco.image.Sslandmarks;
import cn.poco.pgles.PGLNativeIpl;
import cn.poco.resource.FilterRes;
import my.beautyCamera.R;

/**
 * Created by zwq on 2017/03/16 13:45.<br/><br/>
 */
public class DynamicColorFilter extends DefaultFilter {

    private final String TAG = "DynamicColorFilter";

    private int muTexture1Loc;
    private int muTexture2Loc;
    private int muProcessTypeLoc;
    private int muAlphaLoc;

    private int mViewSizeLoc;
    private int mFaceCountLoc;
    private int mPositionLoc;
    private int mIsHollowLoc;
    private int mAspectLoc;
    private int mAngleLoc;

    private int mTexture1Id;
    private int mTexture2Id;
    private int mProcessType = 1;//查表  //0:不处理, 1:查表，2:整体不透明度和skip face

    //方向、比例
    private int mViewWidth, mViewHeight;
    private GLFramebuffer mBlendFrameBuffer;
    private boolean mRatioOrRotateIsChange = true;
    private float mWidthHeightRatio;
    private int mFlip;
    private float mUpCut;
    private float[] mVertexPoint;//顶点坐标
    private float[] mTexturePoint;//原始图像纹理坐标
    private float[] mTexturePoint1;//光效纹理坐标
    private FloatBuffer mVertexPointBuffer;
    private FloatBuffer mTexturePointBuffer;
    private FloatBuffer mTexturePointBuffer1;

    //表
    private boolean mIsInitData;
    private boolean mFilterDataIsChange;
    private int mFilterResId;
    private Object mTableImg;
    private int mTableTextureId;//颜色表id
    private float mAlpha = 0.8f;                  //滤镜整体不透明度

    //光效
    private Object[] mImgArr = new Object[3];
    private int[] mTempTextureId;
    private int[] mTempCompositeOpArr;
    private float[] mTempAlphaArr;
    private int[] mDeleteIds;

    private int[] mTextureIds = new int[3];       //光效id
    private int[] mCompositeOpArr = new int[3];   //混合模式
    private float[] mAlphaArr = new float[3];     //不透明度
    private IntBuffer mCompositeOpBuffer;
    private FloatBuffer mAlphaBuffer;

    //暗角
    private int mDarkTextureId;//暗角id
    private int mDarkComp = 38;
    private float mDarkAlpha = 0.0f;

    //skip face
    private boolean mSkipFace;//避开人脸
    private int mFaceCount = 0;                   //人脸个数
    private float mPoints[] = new float[5 * 3];  //最多5张人脸
    private FloatBuffer mFacePointsBuffer;
    private FloatBuffer mAspectBuffer;
    private float[] mCosSinArr = new float[5 * 2];  //每张人脸的角度

    private CompositeFilterGroup mCompositeFilterGroup;
    private CompositeFilter mCompositeFilter;
    private CompositeData mCompositeData;
    private CompositeData mDarkCompositeData;

    private boolean mCanDraw;

    public DynamicColorFilter(Context context) {
        super(context);
        initArr(true);
        mIsInitData = false;
        mCanDraw = true;
    }

    @Override
    protected int createProgram(Context context) {
//        return GlUtil.createProgram(context, R.raw.vertex_color_filter, R.raw.fragment_color_filter);
        return PGLNativeIpl.loadFilterDownloadProgramV2();
    }

    @Override
    protected void getGLSLValues() {
        super.getGLSLValues();
        muTexture1Loc = GLES20.glGetUniformLocation(mProgramHandle, "uTexture1");
        muTexture2Loc = GLES20.glGetUniformLocation(mProgramHandle, "uTexture2");
        muProcessTypeLoc = GLES20.glGetUniformLocation(mProgramHandle, "processType");
        muAlphaLoc = GLES20.glGetUniformLocation(mProgramHandle, "alpha");

        //skip face
        mViewSizeLoc = GLES20.glGetUniformLocation(mProgramHandle, "viewSize");
        mPositionLoc = GLES20.glGetUniformLocation(mProgramHandle, "faceCenterArray");
        mFaceCountLoc = GLES20.glGetUniformLocation(mProgramHandle, "facesHollowCount");
        mIsHollowLoc = GLES20.glGetUniformLocation(mProgramHandle, "enableHollow");
        mAspectLoc = GLES20.glGetUniformLocation(mProgramHandle, "aspect");
        mAngleLoc = GLES20.glGetUniformLocation(mProgramHandle, "cosSin");
    }

    @Override
    public void setViewSize(int width, int height) {
        super.setViewSize(width, height);
        mViewWidth = Math.round(width / mRenderScale);
        mViewHeight = Math.round(height / mRenderScale);

        if (mBlendFrameBuffer != null) {
            mBlendFrameBuffer.destroy();
            mBlendFrameBuffer = null;
        }
        if (mBlendFrameBuffer == null) {
            mBlendFrameBuffer = new GLFramebuffer(2, width, height);
        }

        if (width > 1 && height > 1) {
            if (mWidthHeightRatio == 0) {
                setRatioAndOrientation(width * 1.0f / height, 0, 0);//default
            } else {
                setRatioAndOrientation(mWidthHeightRatio, mFlip, mUpCut);
            }
            if (mRatioOrRotateIsChange) {
                mRatioOrRotateIsChange = false;
                initBufferData();
            }
        }
    }

    @Override
    public boolean isFilterEnable() {
        return mIsInitData && super.isFilterEnable();
        /*boolean enable = mIsInitData && super.isFilterEnable();
        if (enable) {
            int size = getTaskSize();
            if (size > 0) {
                runTask();
                return false;
            }
        }
        return enable;*/
    }

    @Override
    public boolean isNeedFlipTexture() {
        return false;
    }

    private void initArr(boolean reset) {
        if (mImgArr == null) {
            mImgArr = new Object[3];
        }
        if (mTempTextureId == null) {
            mTempTextureId = new int[5];
        }
        if (mTempCompositeOpArr == null) {
            mTempCompositeOpArr = new int[3];
        }
        if (mTempAlphaArr == null) {
            mTempAlphaArr = new float[3];
        }

        if (mTextureIds == null) {
            mTextureIds = new int[3];
        }
        if (mCompositeOpArr == null) {
            mCompositeOpArr = new int[3];
        }
        if (mAlphaArr == null) {
            mAlphaArr = new float[3];
        }
        if (reset) {
            Arrays.fill(mImgArr, null);
            Arrays.fill(mTempTextureId, 0);
            Arrays.fill(mTempCompositeOpArr, 0);
            Arrays.fill(mTempAlphaArr, 0.0f);

//            Arrays.fill(mTextureIds, 0);
//            Arrays.fill(mCompositeOpArr, 1);
//            Arrays.fill(mAlphaArr, 0.0f);
        }
        if (mCompositeOpBuffer == null) {
            mCompositeOpBuffer = IntBuffer.wrap(mCompositeOpArr);
        }
        if (mAlphaBuffer == null) {
            mAlphaBuffer = FloatBuffer.wrap(mAlphaArr);
        }
    }

    public void setFilterData(FilterRes filterRes) {
        if (filterRes == null || filterRes.m_datas == null || filterRes.m_datas.length < 1
                || filterRes.m_datas[0] == null || filterRes.m_datas[0].m_res == null) {
            mCanDraw = false;
            return;
        }
        if (filterRes.m_id == mFilterResId) {
            mCanDraw = true;
            return;
        }
        mIsInitData = false;
        mFilterResId = filterRes.m_id;

        initArr(true);//初始化数组
        for (int i = 0; i < 4 && i < filterRes.m_datas.length; i++) {
            FilterRes.FilterData filterData = filterRes.m_datas[i];
            if (filterData == null || filterData.m_res == null) {
                continue;
            }
            if (i == 0) {
                mTableImg = filterData.m_res;
                mSkipFace = filterData.m_isSkipFace;
            } else {
                mImgArr[i - 1] = filterData.m_res;//img
                if (i > 0 && filterData.m_params != null && filterData.m_params.length == 2) {
                    mTempCompositeOpArr[i - 1] = filterData.m_params[0];//comOp
                    mTempAlphaArr[i - 1] = filterData.m_params[1] / 100.0f;//alpha
                }
            }
        }
        mAlpha = filterRes.m_filterAlpha / 100.0f;
        if (filterRes.m_isHasvignette) {
            mDarkComp = 38;
            mDarkAlpha = 1.0f;
        } else {
            mDarkAlpha = 0.0f;
        }
        //test
//        mAlpha = 1.0f;
//        mSkipFace = true;
//        mDarkAlpha = 1.0f;

        int sampleSize = 1;
        if (mViewWidth < 1080) {//1080x1920
            sampleSize = 2;
        }
        //--------------
        clearTask();
        if (mTableImg != null) {
            initTask(0, mTableImg, 1);
        }
        for (int i = 0; i < 3; i++) {
            initTask(i + 1, mImgArr[i], sampleSize);
        }
        if (mDarkAlpha == 1.0f) {
            if (mDarkTextureId == 0) {
                initTask(4, R.drawable.darkcon_v2, sampleSize);
            } else {
                mTempTextureId[4] = mDarkTextureId;
            }
        }
//        Log.i(TAG, "setFilterData: " + mTextureIds[0] + ", " + mTextureIds[1] + ", " + mTextureIds[2]);
        mFilterDataIsChange = true;
        mIsInitData = true;
    }

    private void initTask(int position, Object img, int sampleSize) {
        if (img == null) return;
        ColorTextureTask task = new ColorTextureTask(this, position, img, sampleSize, new ColorTextureTask.TaskCallback() {
            @Override
            public void onTaskCallback(int position, Bitmap bitmap) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    int textureId = GlUtil.createTexture(GLES20.GL_TEXTURE_2D, bitmap);
                    bitmap.recycle();
                    if (mTempTextureId == null) {
                        mTempTextureId = new int[5];
                    }
                    mTempTextureId[position] = textureId;
                }
            }
        });
        addTaskToQueue(task);
        if (position == 0) {
            runTask();
        }
    }

    /**
     * 设置比例和旋转方向
     *
     * @param width_height_ratio 正常时的宽高比(9:16 3:4 1:1等)
     * @param flip               四个翻转方向(正常时为0 顺时针分别为1， 2， 3)
     * @param upCut              1:1时顶部裁剪高度
     */
    public void setRatioAndOrientation(float width_height_ratio, int flip, float upCut) {
        if (width_height_ratio <= 0.1f){
            return;
        }
        mWidthHeightRatio = width_height_ratio;
        mFlip = flip;
        mUpCut = upCut;
        if (mViewWidth < 1 || mViewHeight < 1) return;
        if (flip < 0) {
            flip = 0;
        } else if (flip > 3) {
            flip = 0;
        }
        float ratio1 = 0.0f;//相对于顶部的比例
        float ratio2 = 0.0f;//相对于顶部的比例
        if (mUpCut > 0) {// 0.5-0.75 浮动处理 黑边问题
            ratio1 = (mUpCut - 0.75f) / mViewHeight;
            ratio2 = (mUpCut + 0.75f + mViewWidth / width_height_ratio) / mViewHeight;
        } else {
            ratio2 = (mViewWidth / width_height_ratio) / mViewHeight;
        }
//        Log.i(TAG, "setRatioAndOrientation: upCut:" + upCut + ", " + ratio1 + ", " + ratio2);
        if (mVertexPoint == null) {//原始图像顶点坐标
            mVertexPoint = new float[8];
        }
        mVertexPoint[0] = -1.0f;
        mVertexPoint[1] = -((ratio1 * 2) - 1.0f);
        mVertexPoint[2] = 1.0f;
        mVertexPoint[3] = mVertexPoint[1];

        mVertexPoint[4] = -1.0f;
        mVertexPoint[5] = -((ratio2 * 2) - 1.0f);//0.005
        mVertexPoint[6] = 1.0f;
        mVertexPoint[7] = mVertexPoint[5];

        if (mTexturePoint == null) {//原始图像纹理坐标
            mTexturePoint = new float[8];
        }
        mTexturePoint[0] = 0.0f;
        mTexturePoint[1] = 1.0f - ratio1;
        mTexturePoint[2] = 1.0f;
        mTexturePoint[3] = mTexturePoint[1];

        mTexturePoint[4] = 0.0f;
        mTexturePoint[5] = 1.0f - ratio2;
        mTexturePoint[6] = 1.0f;
        mTexturePoint[7] = mTexturePoint[5];

        if (mTexturePoint1 == null) {//光效纹理坐标
            mTexturePoint1 = new float[8];
        }
        if (width_height_ratio <= 1.0f) {
            switch (flip) {
                case 0: {
                    mTexturePoint1[0] = (1.0f - width_height_ratio) * 0.5f;
                    mTexturePoint1[1] = 0.0f;

                    mTexturePoint1[2] = (1.0f + width_height_ratio) * 0.5f;
                    mTexturePoint1[3] = mTexturePoint1[1];

                    mTexturePoint1[4] = mTexturePoint1[0];
                    mTexturePoint1[5] = 1.0f;

                    mTexturePoint1[6] = mTexturePoint1[2];
                    mTexturePoint1[7] = mTexturePoint1[5];
                    break;
                }
                case 1: {
                    mTexturePoint1[0] = 0.0f;
                    mTexturePoint1[1] = (1.0f + width_height_ratio) * 0.5f;

                    mTexturePoint1[2] = mTexturePoint1[0];
                    mTexturePoint1[3] = (1.0f - width_height_ratio) * 0.5f;

                    mTexturePoint1[4] = 1.0f;
                    mTexturePoint1[5] = mTexturePoint1[1];

                    mTexturePoint1[6] = mTexturePoint1[4];
                    mTexturePoint1[7] = mTexturePoint1[3];
                    break;
                }
                case 2: {
                    mTexturePoint1[0] = (1.0f + width_height_ratio) * 0.5f;
                    mTexturePoint1[1] = 1.0f;

                    mTexturePoint1[2] = (1.0f - width_height_ratio) * 0.5f;
                    mTexturePoint1[3] = mTexturePoint1[1];

                    mTexturePoint1[4] = mTexturePoint1[0];
                    mTexturePoint1[5] = 0.0f;

                    mTexturePoint1[6] = mTexturePoint1[2];
                    mTexturePoint1[7] = mTexturePoint1[5];
                    break;
                }
                case 3: {
                    mTexturePoint1[0] = 1.0f;
                    mTexturePoint1[1] = (1.0f - width_height_ratio) * 0.5f;

                    mTexturePoint1[2] = mTexturePoint1[0];
                    mTexturePoint1[3] = (1.0f + width_height_ratio) * 0.5f;

                    mTexturePoint1[4] = 0.0f;
                    mTexturePoint1[5] = mTexturePoint1[1];

                    mTexturePoint1[6] = mTexturePoint1[4];
                    mTexturePoint1[7] = mTexturePoint1[3];
                    break;
                }
            }
        } else {
            switch (flip) {
                case 0: {
                    mTexturePoint1[0] = 0.0f;
                    mTexturePoint1[1] = (1.0f - 1.0f / width_height_ratio) * 0.5f;

                    mTexturePoint1[2] = 1.0f;
                    mTexturePoint1[3] = mTexturePoint1[1];

                    mTexturePoint1[4] = mTexturePoint1[0];
                    mTexturePoint1[5] = (1.0f + 1.0f / width_height_ratio) * 0.5f;

                    mTexturePoint1[6] = mTexturePoint1[2];
                    mTexturePoint1[7] = mTexturePoint1[5];
                    break;
                }
                case 1: {
                    mTexturePoint1[0] = (1.0f - 1.0f / width_height_ratio) * 0.5f;
                    mTexturePoint1[1] = 1.0f;

                    mTexturePoint1[2] = mTexturePoint1[0];
                    mTexturePoint1[3] = 0.0f;

                    mTexturePoint1[4] = (1.0f + 1.0f / width_height_ratio) * 0.5f;
                    mTexturePoint1[5] = mTexturePoint1[1];

                    mTexturePoint1[6] = mTexturePoint1[4];
                    mTexturePoint1[7] = mTexturePoint1[3];
                    break;
                }
                case 2: {
                    mTexturePoint1[0] = 1.0f;
                    mTexturePoint1[1] = (1.0f + 1.0f / width_height_ratio) * 0.5f;

                    mTexturePoint1[2] = 0.0f;
                    mTexturePoint1[3] = mTexturePoint1[1];

                    mTexturePoint1[4] = mTexturePoint1[0];
                    mTexturePoint1[5] = (1.0f - 1.0f / width_height_ratio) * 0.5f;

                    mTexturePoint1[6] = mTexturePoint1[2];
                    mTexturePoint1[7] = mTexturePoint1[5];
                    break;
                }
                case 3: {
                    mTexturePoint1[0] = (1.0f + 1.0f / width_height_ratio) * 0.5f;
                    mTexturePoint1[1] = 0.0f;

                    mTexturePoint1[2] = mTexturePoint1[0];
                    mTexturePoint1[3] = 1.0f;

                    mTexturePoint1[4] = (1.0f - 1.0f / width_height_ratio) * 0.5f;
                    mTexturePoint1[5] = mTexturePoint1[1];

                    mTexturePoint1[6] = mTexturePoint1[4];
                    mTexturePoint1[7] = mTexturePoint1[3];
                    break;
                }
            }
        }
        mRatioOrRotateIsChange = true;
    }

    private void initBufferData() {
        if (mVertexPointBuffer == null || mVertexPointBuffer.capacity() != mVertexPoint.length * 4) {
            mVertexPointBuffer = GlUtil.createFloatBuffer(mVertexPoint);
        } else {
            mVertexPointBuffer.clear();
            mVertexPointBuffer.put(mVertexPoint);
            mVertexPointBuffer.position(0);
        }

        if (mTexturePointBuffer == null || mTexturePointBuffer.capacity() != mTexturePoint.length * 4) {
            mTexturePointBuffer = GlUtil.createFloatBuffer(mTexturePoint);
        } else {
            mTexturePointBuffer.clear();
            mTexturePointBuffer.put(mTexturePoint);
            mTexturePointBuffer.position(0);
        }

        if (mTexturePointBuffer1 == null || mTexturePointBuffer1.capacity() != mTexturePoint1.length * 4) {
            mTexturePointBuffer1 = GlUtil.createFloatBuffer(mTexturePoint1);
        } else {
            mTexturePointBuffer1.clear();
            mTexturePointBuffer1.put(mTexturePoint1);
            mTexturePointBuffer1.position(0);
        }
    }

    private void checkIsNeedChange() {
        int size = getTaskSize();
        if (size > 0) {
            runTask();
        }
        if (size == 0 && mRatioOrRotateIsChange) {
            mRatioOrRotateIsChange = false;
            initBufferData();
        }
        if (size == 0 && mFilterDataIsChange) {
            mFilterDataIsChange = false;

            if (mTempTextureId != null) {
                mDeleteIds = new int[4];
                mDeleteIds[0] = mTableTextureId;
                if (mTextureIds != null) {
                    for (int i = 0; i < mTextureIds.length; i++) {
                        mDeleteIds[i + 1] = mTextureIds[i];
                    }
                }

                mTableTextureId = mTempTextureId[0];
                mTextureIds[0] = mTempTextureId[1];
                mTextureIds[1] = mTempTextureId[2];
                mTextureIds[2] = mTempTextureId[3];
                mDarkTextureId = mTempTextureId[4];
            }
            for (int i = 0; i < 3; i++) {
                if (mTempCompositeOpArr != null) {
                    mCompositeOpArr[i] = mTempCompositeOpArr[i];
                }
                if (mTempAlphaArr != null) {
                    mAlphaArr[i] = mTempAlphaArr[i];
                }
            }

            mCompositeOpBuffer.clear();
            mCompositeOpBuffer.put(mCompositeOpArr);
            mCompositeOpBuffer.position(0);
            mAlphaBuffer.clear();
            mAlphaBuffer.put(mAlphaArr);
            mAlphaBuffer.position(0);

            mCanDraw = true;
        }
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

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        if (mCanDraw) {
            mProcessType = 1;
        } else {
            mProcessType = 0;
        }
        mTexture1Id = mTableTextureId;
        mTexture2Id = 0;
        super.onDraw(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);

        if (mCompositeFilterGroup != null && mGLFramebuffer != null && mProcessType == 1) {
            int count = 0;
            int frameBufferId = 0;
            int tableDataId = -1;
//-----------------------------------------

            if (mBlendFrameBuffer != null) {
                if (mCompositeData == null) {
                    mCompositeData = new CompositeData();
                }

                boolean onlyBlendLightEffect = true;
                int validCount = 0;
                if (mAlpha < 1.0f || mSkipFace || (mDarkAlpha > 0.0f && mDarkTextureId > 0)) {
                    onlyBlendLightEffect = false;
                } else {
                    for (int i = 0; i < mTextureIds.length; i++) {
                        if (mTextureIds[i] <= 0) continue;
                        validCount++;
                    }
                }

                frameBufferId = mGLFramebuffer.getCurrentTextureId();
                int originFrameBufferId = frameBufferId;
                for (int i = 0; i < mTextureIds.length; i++) {
                    if (mTextureIds[i] <= 0) continue;

                    if (mCompositeData != null) {
                        mCompositeData.setData(mTextureIds[i], mCompositeOpArr[i], mAlphaArr[i], mTexturePointBuffer1, -1, null);
                    }
                    mCompositeFilter = mCompositeFilterGroup.setCompositeFilterData(mCompositeData);
                    if (mCompositeFilter != null) {
                        if (onlyBlendLightEffect && count == validCount - 1) {
                            mGLFramebuffer.bindNext(true);
                            if (count > 0) {
                                frameBufferId = mBlendFrameBuffer.getCurrentTextureId();
                            }
                        } else {
                            mBlendFrameBuffer.bindNext(true);
                            if (frameBufferId <= 0) {
                                frameBufferId = mBlendFrameBuffer.getPreviousTextureId();
                            }
                        }
                        if (frameBufferId <= 0) {
                            frameBufferId = originFrameBufferId;
                        }
                        mCompositeFilter.onDraw(mvpMatrix, mVertexPointBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, mTexturePointBuffer, frameBufferId, texStride);
                        count++;
                        frameBufferId = 0;
                    }
                    mCompositeFilter = null;
                }
                frameBufferId = 0;
                if (count > 0 && !onlyBlendLightEffect) {
                    frameBufferId = mBlendFrameBuffer.getCurrentTextureId();
                    count = 0;
                }
            }

//-----------------------------------------
            //整体不透明度、避开人脸
            if (mAlpha < 1.0f || mSkipFace) {
                mProcessType = 2;
                int mOriginId = mDefaultTextureId;
                mTexture1Id = mOriginId;

                mGLFramebuffer.bindNext(true);
                if (frameBufferId <= 0) {
                    frameBufferId = mGLFramebuffer.getPreviousTextureId();
                    if (tableDataId == -1) {
                        tableDataId = frameBufferId;
                    }
                } else {
                    if (tableDataId == -1) {
                        tableDataId = mGLFramebuffer.getPreviousTextureId();
                    }
                }
                mTexture2Id = tableDataId;

                super.onDraw(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, frameBufferId, texStride);
                count++;
                frameBufferId = 0;
                mDefaultTextureId = mOriginId;
            }

            //暗角
            if (mDarkAlpha > 0.0f && mDarkTextureId > 0) {
                if (mDarkCompositeData == null) {
                    mDarkCompositeData = new CompositeData();
                    mDarkCompositeData.setData(mDarkTextureId, mDarkComp, mDarkAlpha, texBuffer, -1, null);
                }
                mCompositeFilter = mCompositeFilterGroup.setCompositeFilterData(mDarkCompositeData);
                if (mCompositeFilter != null) {
                    mGLFramebuffer.bindNext(true);
                    if (frameBufferId <= 0) {
                        frameBufferId = mGLFramebuffer.getPreviousTextureId();
                    }
                    mCompositeFilter.onDraw(mvpMatrix, mVertexPointBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, mTexturePointBuffer, frameBufferId, texStride);
                    count++;
                    frameBufferId = 0;
                }
                mCompositeFilter = null;
            }
            if (count > 0) {
                mGLFramebuffer.setHasBind(false);
            }
            mCompositeFilterGroup = null;
        }
    }

    @Override
    protected void bindTexture(int textureId) {
        checkIsNeedChange();

        super.bindTexture(textureId);

        if (mTexture1Id > 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(getTextureTarget(), mTexture1Id);
            GLES20.glUniform1i(muTexture1Loc, 1);
        } else {
            mProcessType = 0;
        }
        if (mTexture2Id > 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
            GLES20.glBindTexture(getTextureTarget(), mTexture2Id);
            GLES20.glUniform1i(muTexture2Loc, 2);
        }
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        GLES20.glUniform1i(muProcessTypeLoc, mProcessType);
        GLES20.glUniform1f(muAlphaLoc, mAlpha);
        super.bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);

        if (mProcessType != 2) {
            return;
        }
        //skip face
        ArrayList<PocoFace> tempFaces = FaceDataHelper.getInstance().getFaceList();
        if (tempFaces != null && !tempFaces.isEmpty()) {
            float[][] faceData = new float[tempFaces.size()][10];
            for (int i = 0; i < tempFaces.size(); i++) {
                PocoFace pocoFace = tempFaces.get(i);
                faceData[i] = new float[10];
                if (pocoFace == null || pocoFace.points_array == null) {
                    continue;
                }
                faceData[i][0] = pocoFace.points_array[Sslandmarks.lEyeCenter].x;
                faceData[i][1] = 1.0f - pocoFace.points_array[Sslandmarks.lEyeCenter].y;

                faceData[i][2] = pocoFace.points_array[Sslandmarks.rEyeCenter].x;
                faceData[i][3] = 1.0f - pocoFace.points_array[Sslandmarks.rEyeCenter].y;

                faceData[i][4] = pocoFace.points_array[Sslandmarks.chinLeft].x;
                faceData[i][5] = 1.0f - pocoFace.points_array[Sslandmarks.chinLeft].y;

                faceData[i][6] = pocoFace.points_array[Sslandmarks.chinRight].x;
                faceData[i][7] = 1.0f - pocoFace.points_array[Sslandmarks.chinRight].y;

                faceData[i][8] = (pocoFace.rect.left + pocoFace.rect.right) / 2.f;
                faceData[i][9] = (2.f - pocoFace.rect.top - pocoFace.rect.bottom) / 2.f;
            }
            calculateCenterAndRadius(faceData, mWidth, mHeight);
        } else {
            calculateCenterAndRadius(null, mWidth, mHeight);
        }
        if (mFacePointsBuffer == null) {
            mFacePointsBuffer = FloatBuffer.wrap(mPoints);
        } else {
            mFacePointsBuffer.clear();
            mFacePointsBuffer.put(mPoints);
            mFacePointsBuffer.position(0);
        }
        GLES20.glUniform3fv(mPositionLoc, mPoints.length / 3, mFacePointsBuffer);

        GLES20.glUniform1i(mFaceCountLoc, mFaceCount);
        GLES20.glUniform1i(mIsHollowLoc, mSkipFace ? 1 : 0);

        float[] aspectArr = new float[2];
        if (mFlip == 1 || mFlip == 3) {
            aspectArr[0] = 1.0f;
            aspectArr[1] = 2.25f + 0.5f;
        } else {
            aspectArr[0] = 2.25f + 0.5f;
            aspectArr[1] = 1.0f;
        }
        if (mAspectBuffer == null) {
            mAspectBuffer = FloatBuffer.wrap(aspectArr);
        } else {
            mAspectBuffer.clear();
            mAspectBuffer.put(aspectArr);
            mAspectBuffer.position(0);
        }
        GLES20.glUniform2fv(mAspectLoc, aspectArr.length / 2, mAspectBuffer);

        float[] size = {mWidth / 1000.f, mHeight / 1000.f};
        GLES20.glUniform2fv(mViewSizeLoc, size.length / 2, FloatBuffer.wrap(size));
        GLES20.glUniform2fv(mAngleLoc, mCosSinArr.length / 2, FloatBuffer.wrap(mCosSinArr));
    }

    @Override
    protected void unbindTexture() {
        super.unbindTexture();
        if (mDeleteIds != null) {
            GLES20.glDeleteTextures(mDeleteIds.length, mDeleteIds, 0);
            mDeleteIds = null;
        }
    }

    @Override
    public void releaseProgram() {
        super.releaseProgram();
        if (mTempTextureId != null) {
            GLES20.glDeleteTextures(mTempTextureId.length, mTempTextureId, 0);
        }
        if (mTextureIds != null) {
            GLES20.glDeleteTextures(mTextureIds.length, mTextureIds, 0);
        }
        if (mBlendFrameBuffer != null) {
            mBlendFrameBuffer.destroy();
            mBlendFrameBuffer = null;
        }

        mImgArr = null;
        mTempTextureId = null;
        mTempCompositeOpArr = null;
        mTempAlphaArr = null;
        mTextureIds = null;
        mCompositeOpArr = null;
        mAlphaArr = null;

        mCompositeOpBuffer = null;
        mAlphaBuffer = null;

        mVertexPoint = null;
        mTexturePoint = null;
        mTexturePoint1 = null;
        mVertexPointBuffer = null;
        mTexturePointBuffer = null;
        mTexturePointBuffer1 = null;
        mFacePointsBuffer = null;
        mAspectBuffer = null;

        mCompositeFilter = null;
        mCompositeData = null;
        mDarkCompositeData = null;
    }

    /**
     * FacePoints 人脸数据（含多人脸）
     */
    private void calculateCenterAndRadius(float[][] FacePoints, int width, int height) {
        if (FacePoints == null) {
            mFaceCount = 0;
            Arrays.fill(mPoints, 0);
            Arrays.fill(mCosSinArr, 0);
        } else {
            mFaceCount = FacePoints.length > 5 ? 5 : FacePoints.length;
            float facePts[] = new float[10 * mFaceCount];
            for (int i = 0; i < mFaceCount; i++) {
                facePts[10 * i] = (int) (FacePoints[i][0] * width);
                facePts[10 * i + 1] = (int) (FacePoints[i][1] * height);

                facePts[10 * i + 2] = (int) (FacePoints[i][2] * width);
                facePts[10 * i + 3] = (int) (FacePoints[i][3] * height);

                facePts[10 * i + 4] = (int) (FacePoints[i][4] * width);
                facePts[10 * i + 5] = (int) (FacePoints[i][5] * height);

                facePts[10 * i + 6] = (int) (FacePoints[i][6] * width);
                facePts[10 * i + 7] = (int) (FacePoints[i][7] * height);

                facePts[10 * i + 8] = (int) (FacePoints[i][8] * width);
                facePts[10 * i + 9] = (int) (FacePoints[i][9] * height);

                float wdx = facePts[8 * i + 6] - facePts[8 * i + 4];
                float wdy = facePts[8 * i + 7] - facePts[8 * i + 5];
                double shortSide = Math.sqrt(wdx * wdx + wdy * wdy);

                mPoints[3 * i] = facePts[10 * i + 8] / 1000.f;
                mPoints[3 * i + 1] = facePts[10 * i + 9] / 1000.f;   //中心点
                mPoints[3 * i + 2] = (int) (shortSide * 1.6) / 1000.f;     //半径

                double distEye = Math.sqrt((facePts[8 * i] - facePts[8 * i + 2]) * (facePts[8 * i] - facePts[8 * i + 2]) + (facePts[8 * i + 1] - facePts[8 * i + 3]) * (facePts[8 * i + 1] - facePts[8 * i + 3]));

                if (mFlip == 1 || mFlip == 3) {
                    mCosSinArr[2 * i + 1] = (float) ((facePts[8 * i] - facePts[8 * i + 2]) / distEye);
                    mCosSinArr[2 * i] = (float) ((facePts[8 * i + 1] - facePts[8 * i + 3]) / distEye);
                } else {
                    mCosSinArr[2 * i] = (float) ((facePts[8 * i] - facePts[8 * i + 2]) / distEye);
                    mCosSinArr[2 * i + 1] = (float) ((facePts[8 * i + 1] - facePts[8 * i + 3]) / distEye) * (-1);
                }
            }
        }
    }
}
