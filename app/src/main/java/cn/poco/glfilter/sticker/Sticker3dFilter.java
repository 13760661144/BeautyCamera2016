package cn.poco.glfilter.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.poco.dynamicSticker.FaceAction;
import cn.poco.dynamicSticker.StickerType;
import cn.poco.dynamicSticker.v2.StickerSpriteFrame;
import cn.poco.dynamicSticker.v2.StickerSubRes;
import cn.poco.gldraw2.FaceDataHelper;
import cn.poco.glfilter.base.AbsFilterGroup;
import cn.poco.glfilter.base.AbstractFilter;
import cn.poco.glfilter.base.GLFramebuffer;
import cn.poco.glfilter.base.GlUtil;
import cn.poco.glfilter.base.VertexArray;
import cn.poco.glfilter.composite.CompositeData;
import cn.poco.glfilter.composite.CompositeFilter;
import cn.poco.glfilter.composite.CompositeFilterGroup;
import cn.poco.glfilter.makeup.MakeUpBaseFilter;
import cn.poco.glfilter.makeup.MakeUpFilterGroup;
import cn.poco.glfilter.shape.FaceShapeFilter;
import cn.poco.glfilter.shape.ShapeFilterGroup;
import cn.poco.image.PocoFace;
import cn.poco.pgles.PGLNativeIpl;
import cn.poco.resource.RealTimeMakeUpSubRes;

/**
 * Created by zwq on 2016/7/20.
 * 多人脸循环绘制
 */
public class Sticker3dFilter extends AbstractFilter {

    private final String TAG = "bbb";
    private int muMVPMatrixLoc;
    private int muTexMatrixLoc;
    private int muTextureLoc;

    private FloatBuffer mFaceDataBuffer, mFaceDataBuffer1;
    private HashMap<String, Integer> mTextureIdsMap;

    private StickerDrawHelper mStickerDrawHelper;
    private ArrayList<Map.Entry<String, StickerSubRes>> mStickerList;
    private boolean mHasStickerData;
    private VertexArray vertexArray;
    private VertexArray vertexArray3d;
    private ByteBuffer mSticker3dDrawIndex;

    private String mStickerType;
    private StickerSubRes mStickerEntity;
    private int currentIndex;
    private int[] mCurrentTextureIds;
    private int mCurrentTextureIdsNumber;

    private boolean mIsRecord;
    private boolean mCanDrawStickers;
    private boolean mDrawCameraData = true;
    private int mFaceSize;
    private PocoFace mPocoFace;

    private int mHasTextureIdCount;//用于判断各部位帧是否同步
    private int mDrawStickerCount;
    private boolean mBindTempFramebufferSuccess;
    private boolean mIsLastFace;

    private CompositeFilterGroup mCompositeFilterGroup;
    private CompositeFilter mCompositeFilter;
    private CompositeData mCompositeData;
    private ShapeFilterGroup mShapeFilterGroup;
    private FaceShapeFilter mFaceShapeFilter;
    private MakeUpFilterGroup mMakeUpFilterGroup;
    private float[] mTextureMatrix;
    private int mIndexLength = 0;

    private boolean mIsGifMode;
    private boolean mIsDrawActionRes;
    private String mActionKey;

    private boolean mAnimState;
    private boolean mTriggerSuccess;

    private int mMakeUpFrameIndex = -1;
    private RealTimeMakeUpSubRes mRealTimeMakeUpSubRes;

    private OnDrawStickerResListener mOnDrawStickerResListener;
    private long mAnimStartTime;
    private boolean mIsPlayingAnimMusic;

    private Sticker3dModelData mSticker3dModelData;

    private AR3DEffectFilter mAR3DEffectFilter;

    private boolean mCanInvokeSplitScreen;
    private SplitScreenFilter mSplitScreenFilter;

    private boolean mIsDebug;

    public Sticker3dFilter(Context context) {
        super(context);

        try {
            mAR3DEffectFilter = new AR3DEffectFilter(context);
        } catch (Throwable e) {
            e.printStackTrace();
            mAR3DEffectFilter = null;
        }

        initStickerManager();
        if (mStickerDrawHelper != null) {
            mStickerDrawHelper.resetFilterData();
        }
        initCurrentTextureIdsArr();
    }

    public void setOnDrawStickerResListener(OnDrawStickerResListener listener) {
        mOnDrawStickerResListener = listener;
    }

    private void initStickerVerticesBuffer() {
        if (vertexArray == null) {
            vertexArray = new VertexArray();
        }
        if (vertexArray3d == null) {
            float[] mVerticesData = new float[121 * 2];
            float[] mTextureVerticesData = {0.2130682f, 0.4813008f, 0.2149621f, 0.5065041f, 0.219697f, 0.5308943f, 0.2253788f, 0.5569106f, 0.2310606f, 0.5829268f, 0.2405303f, 0.6138211f, 0.25f, 0.6430894f, 0.2613636f, 0.6731707f, 0.2727273f, 0.702439f, 0.2888258f, 0.7317073f, 0.3106061f, 0.7601626f, 0.3323864f, 0.7853659f, 0.3589015f, 0.8073171f, 0.3873106f, 0.8284553f, 0.4232955f, 0.8479675f, 0.4621212f, 0.8593496f, 0.5f, 0.8609756f, 0.5378788f, 0.8609756f, 0.5757576f, 0.8487805f, 0.6126894f, 0.8276423f, 0.6401515f, 0.8081301f, 0.6657197f, 0.7853659f, 0.6893939f, 0.7601626f, 0.7102273f, 0.7333333f, 0.7263258f, 0.704065f, 0.7395833f, 0.6747967f, 0.75f, 0.6447154f, 0.7604167f, 0.6130081f, 0.7698864f, 0.5829268f, 0.7755682f, 0.5569106f, 0.7793561f, 0.5317073f, 0.7850379f, 0.5065041f, 0.7878788f, 0.4804878f, 0.2547348f, 0.4333333f, 0.2926136f, 0.4081301f, 0.3494318f, 0.4065041f, 0.4015152f, 0.4105691f, 0.4441288f, 0.4252033f, 0.5568182f, 0.4252033f, 0.5984848f, 0.4113821f, 0.6515152f, 0.404878f, 0.7064394f, 0.4081301f, 0.7462121f, 0.4333333f, 0.500947f, 0.4723577f, 0.5f, 0.5276423f, 0.500947f, 0.5821138f, 0.5f, 0.6227642f, 0.4498106f, 0.6487805f, 0.4725379f, 0.6430894f, 0.499053f, 0.6552846f, 0.5255682f, 0.6414634f, 0.5501894f, 0.6479675f, 0.3011364f, 0.4902439f, 0.342803f, 0.4691057f, 0.3948864f, 0.4699187f, 0.4308712f, 0.501626f, 0.3929924f, 0.5081301f, 0.3418561f, 0.5081301f, 0.5681818f, 0.501626f, 0.6070076f, 0.4691057f, 0.6590909f, 0.4691057f, 0.6988636f, 0.4902439f, 0.6581439f, 0.5089431f, 0.6070076f, 0.5081301f, 0.2897727f, 0.4284553f, 0.34375f, 0.4300813f, 0.3996212f, 0.4349593f, 0.4479167f, 0.4422764f, 0.5530303f, 0.4422764f, 0.6003788f, 0.4349593f, 0.65625f, 0.4292683f, 0.7102273f, 0.4284553f, 0.3674242f, 0.4650407f, 0.3674242f, 0.5130081f, 0.3664773f, 0.4878049f, 0.6325758f, 0.4650407f, 0.6325758f, 0.5113821f, 0.6325758f, 0.4886179f, 0.4602273f, 0.4813008f, 0.5369318f, 0.4821138f, 0.4545455f, 0.598374f, 0.5464015f, 0.598374f, 0.4327652f, 0.6382114f, 0.5681818f, 0.6373984f, 0.3892045f, 0.7178862f, 0.4308712f, 0.7089431f, 0.46875f, 0.701626f, 0.5f, 0.7105691f, 0.532197f, 0.701626f, 0.5681818f, 0.7089431f, 0.6098485f, 0.7186992f, 0.5691288f, 0.7439024f, 0.5416667f, 0.7593496f, 0.5f, 0.7634146f, 0.4602273f, 0.7585366f, 0.4318182f, 0.7447154f, 0.4043561f, 0.7195122f, 0.4583333f, 0.7219512f, 0.5f, 0.7252033f, 0.5416667f, 0.7219512f, 0.5956439f, 0.7203252f, 0.5293561f, 0.7284553f, 0.5f, 0.7292683f, 0.469697f, 0.7276423f, 0.3721591f, 0.4886179f, 0.6268939f, 0.4886179f, 0.3030303f, 0.6536585f, 0.6988636f, 0.6544715f, 0.2992424f, 0.3495935f, 0.3712121f, 0.3430894f, 0.5f, 0.3430894f, 0.6297348f, 0.3447154f, 0.6979167f, 0.3504065f, 0.f, 0.001626f, 0.5f, 0.000813f, 0.9952652f, 0.000813f, 0.0018939f, 0.5821138f, 0.9943182f, 0.6138211f, 0.f, 0.996748f, 0.5f, 0.997561f, 0.9952652f, 0.996748f};
            vertexArray3d = new VertexArray(mVerticesData, mTextureVerticesData);
        }

        if (mSticker3dDrawIndex == null) {
//            if (PocoFaceTracker.mDetectMethod == PocoDetector.DetectMethod.READFACE_DETECTOR) {
//                short[] index = {33, 34, 64, 34, 35, 64, 35, 64, 65, 35, 36, 65, 36, 65, 66, 36, 37, 66, 37, 67, 66, 38, 39, 68, 39, 69, 68, 39, 40, 69, 40, 70, 69, 40, 41, 70, 41, 71, 70, 41, 42, 71, 52, 53, 74, 53, 72, 74, 72, 54, 74, 54, 55, 74, 55, 56, 74, 56, 73, 74, 73, 57, 74, 57, 52, 74, 58, 59, 77, 59, 75, 77, 75, 60, 77, 60, 61, 77, 61, 62, 77, 62, 76, 77, 76, 63, 77, 63, 58, 77, 82, 47, 46, 47, 48, 46, 48, 49, 46, 49, 50, 46, 50, 51, 46, 51, 83, 46, 82, 80, 46, 80, 45, 46, 45, 81, 46, 81, 83, 46, 80, 44, 45, 78, 44, 80, 44, 45, 81, 79, 44, 81, 78, 43, 44, 43, 79, 44, 84, 85, 96, 85, 86, 96, 86, 97, 96, 86, 87, 97, 87, 98, 97, 87, 88, 98, 88, 99, 98, 88, 89, 99, 89, 100, 99, 89, 90, 100, 84, 103, 95, 103, 94, 95, 103, 102, 94, 102, 93, 94, 102, 101, 93, 101, 92, 93, 101, 91, 92, 101, 90, 91, 108, 0, 116, 0, 1, 116, 1, 2, 116, 2, 3, 116, 3, 4, 116, 4, 5, 116, 5, 6, 116, 6, 7, 116, 116, 7, 118, 7, 8, 118, 8, 9, 118, 9, 10, 118, 10, 11, 118, 11, 12, 118, 118, 12, 119, 12, 13, 119, 13, 14, 119, 14, 15, 119, 15, 16, 119, 16, 17, 119, 17, 18, 119, 18, 19, 119, 19, 20, 119, 119, 20, 120, 20, 21, 120, 21, 22, 120, 22, 23, 120, 23, 24, 120, 24, 25, 120, 120, 25, 117, 25, 26, 117, 26, 27, 117, 27, 28, 117, 28, 29, 117, 29, 30, 117, 30, 31, 117, 31, 32, 117, 112, 32, 117, 117, 112, 115, 111, 112, 115, 115, 111, 114, 110, 111, 114, 110, 109, 114, 114, 109, 113, 109, 108, 113, 113, 108, 116, 0, 33, 108, 33, 34, 108, 108, 34, 109, 34, 35, 109, 35, 36, 109, 109, 110, 36, 36, 37, 110, 37, 38, 110, 38, 39, 110, 110, 39, 111, 39, 40, 111, 40, 41, 111, 111, 41, 112, 41, 42, 112, 42, 32, 112, 0, 33, 52, 0, 1, 52, 1, 2, 52, 52, 106, 2, 2, 3, 106, 3, 4, 106, 52, 57, 106, 4, 5, 106, 5, 6, 106, 6, 7, 106, 7, 8, 84, 8, 9, 84, 84, 95, 9, 9, 10, 95, 10, 11, 95, 11, 12, 95, 12, 13, 95, 95, 94, 13, 13, 14, 94, 94, 93, 14, 93, 15, 14, 93, 16, 15, 93, 16, 17, 93, 92, 17, 92, 91, 17, 91, 18, 17, 91, 19, 18, 91, 20, 19, 91, 90, 20, 90, 21, 20, 90, 22, 21, 90, 23, 22, 90, 24, 23, 90, 25, 24, 25, 107, 26, 107, 27, 26, 107, 28, 27, 107, 29, 28, 107, 30, 29, 61, 107, 30, 61, 31, 30, 32, 31, 61, 32, 42, 61, 33, 64, 52, 64, 53, 52, 64, 65, 53, 65, 66, 53, 66, 72, 53, 66, 54, 72, 66, 67, 54, 67, 55, 54, 37, 38, 67, 67, 38, 68, 67, 78, 55, 67, 43, 78, 67, 68, 43, 43, 79, 68, 79, 58, 81, 79, 58, 68, 58, 59, 68, 68, 69, 59, 59, 75, 69, 69, 70, 75, 75, 60, 70, 70, 71, 60, 71, 42, 61, 71, 61, 60, 61, 62, 107, 76, 62, 107, 76, 63, 83, 58, 63, 81, 63, 81, 83, 57, 73, 106, 55, 78, 80, 56, 55, 80, 56, 80, 82, 56, 73, 82, 73, 106, 82, 76, 83, 107, 106, 7, 84, 106, 82, 84, 84, 82, 85, 82, 47, 85, 107, 25, 90, 107, 90, 83, 47, 48, 85, 48, 85, 86, 48, 49, 86, 49, 86, 87, 49, 87, 88, 49, 50, 88, 50, 51, 88, 51, 89, 88, 51, 83, 89, 83, 89, 90};
//                mIndexLength = index.length;
//                mSticker3dDrawIndex = ByteBuffer.allocateDirect(mIndexLength * 2)//1320 = 660*2
//                        .order(ByteOrder.nativeOrder());
//                mSticker3dDrawIndex.asShortBuffer().put(index);
//                mSticker3dDrawIndex.position(0);
//            } else if (PocoFaceTracker.mDetectMethod == PocoDetector.DetectMethod.ULS_DETECTOR) {
                short[] index = {33, 34, 64, 34, 35, 64, 35, 64, 65, 35, 36, 65, 36, 65, 66, 36, 37, 66, 37, 37, 66, 38, 39, 38, 39, 69,
                        38, 39, 40, 69, 40, 70, 69, 40, 41, 70, 41, 71, 70, 41, 42, 71, 52, 53, 74, 53, 72, 74, 72, 54, 74, 54, 55, 74, 55, 56, 74, 56, 73, 74,
                        73, 57, 74, 57, 52, 74, 58, 59, 77, 59, 75, 77, 75, 60, 77, 60, 61, 77, 61, 62, 77, 62, 76, 77, 76, 63, 77, 63, 58, 77, 82, 47, 46, 47, 48,
                        46, 48, 49, 46, 49, 50, 46, 50, 51, 46, 51, 83, 46, 82, 80, 46, 80, 45, 46, 45, 81, 46, 81, 83, 46, 80, 44, 45, 78, 44, 80, 44, 45, 81, 79,
                        44, 81, 78, 43, 44, 43, 79, 44,
                        84, 85, 97, 85, 86, 97,  //86,97,96,//
                        86, 87, 97, 87, 98, 97, 87, 88, 98, 88, 99, 98, 88, 89, 99,
                        89, 90, 99,
                        //89,90,100,//
                        84, 103, 95,
                        103, 94, 95,
                        103, 102, 94, 102, 93, 94, 102, 101, 93, 101, 92, 93, 101, 91, 92, 101, 90, 91, 108, 0, 116, 0, 1,
                        116, 1, 2, 116, 2, 3, 116, 3, 4, 116, 4, 5, 116, 5, 6, 116, 6, 7, 116, 116, 7, 118, 7, 8, 118, 8, 9, 118, 9, 10, 118, 10, 11, 118, 11, 12, 118,
                        118, 12, 119, 12, 13, 119, 13, 14, 119, 14, 15, 119, 15, 16, 119, 16, 17, 119, 17, 18, 119, 18, 19, 119, 19, 20, 119, 119, 20, 120, 20, 21,
                        120, 21, 22, 120, 22, 23, 120, 23, 24, 120, 24, 25, 120, 120, 25, 117, 25, 26, 117, 26, 27, 117, 27, 28, 117, 28, 29, 117, 29, 30, 117, 30,
                        31, 117, 31, 32, 117, 112, 32, 117, 117, 112, 115, 111, 112, 115, 115, 111, 114, 110, 111, 114, 110, 109, 114, 114, 109, 113, 109, 108,
                        113, 113, 108, 116, 0, 33, 108, 33, 34, 108, 108, 34, 109, 34, 35, 109, 35, 36, 109, 109, 110, 36, 36, 37, 110, 37, 38, 110, 38, 39, 110, 110,
                        39, 111, 39, 40, 111, 40, 41, 111, 111, 41, 112, 41, 42, 112, 42, 32, 112, 0, 33, 52, 0, 1, 52, 1, 2, 52, 52, 106, 2, 2, 3, 106, 3, 4, 106, 52, 57,
                        106, 4, 5, 106, 5, 6, 106, 6, 7, 106, 7, 8, 84, 8, 9, 84, 84, 95, 9, 9, 10, 95, 10, 11, 95, 11, 12, 95, 12, 13, 95, 95, 94, 13, 13, 14, 94, 94, 93, 14,
                        93, 15, 14, 93, 16, 15, 93, 16, 17, 93, 92, 17, 92, 91, 17, 91, 18, 17, 91, 19, 18, 91, 20, 19, 91, 90, 20, 90, 21, 20, 90, 22, 21, 90, 23, 22, 90,
                        24, 23, 90, 25, 24, 25, 107, 26, 107, 27, 26, 107, 28, 27, 107, 29, 28, 107, 30, 29, 61, 107, 30, 61, 31, 30, 32, 31, 61, 32, 42, 61, 33, 64, 52,
                        64, 53, 52, 64, 65, 53, 65, 66, 53, 66, 72, 53, 66, 54, 72, 66, 37, 54, 37, 55, 54, 37, 38, 37, 37, 38, 38, 37, 78, 55, 37, 43, 78, 37, 38, 43, 43,
                        79, 38, 79, 58, 81, 79, 58, 38, 58, 59, 38, 38, 69, 59, 59, 75, 69, 69, 70, 75, 75, 60, 70, 70, 71, 60, 71, 42, 61, 71, 61, 60, 61, 62, 107, 76, 62,
                        107, 76, 63, 83, 58, 63, 81, 63, 81, 83, 57, 73, 106, 55, 78, 80, 56, 55, 80, 56, 80, 82, 56, 73, 82, 73, 106, 82, 76, 83, 107, 106, 7, 84, 106, 82,
                        84, 84, 82, 85, 82, 47, 85, 107, 25, 90, 107, 90, 83, 47, 48, 85, 48, 85, 86, 48, 49, 86,
                        49, 86, 87, 49, 87, 88, 49, 50, 88, 50, 51, 88, 51, 89, 88, 51, 83, 89, 83, 89, 90};

                mIndexLength = index.length;
                mSticker3dDrawIndex = ByteBuffer.allocateDirect(mIndexLength * 2)//1308 = 654*2
                        .order(ByteOrder.nativeOrder());
                mSticker3dDrawIndex.asShortBuffer().put(index);
                mSticker3dDrawIndex.position(0);
//            }
        }
    }

    private void initStickerManager() {
        if (mStickerDrawHelper == null) {
            mStickerDrawHelper = StickerDrawHelper.getInstance();
        }
        mStickerDrawHelper.resetFilterData();
    }

    private void initCurrentTextureIdsArr() {
        mCurrentTextureIds = new int[StickerType.STICKER_TYPE_NUM * mStickerDrawHelper.mEachTypeOfTextureCount];
        mCurrentTextureIdsNumber = 0;
    }

    @Override
    public int getTextureTarget() {
        return GLES20.GL_TEXTURE_2D;
    }

    @Override
    public void setViewSize(int width, int height) {
        super.setViewSize(width, height);
        if (mAR3DEffectFilter != null) {
            mAR3DEffectFilter.setViewSize(width, height);
        }
        if (mStickerDrawHelper != null) {
            int viewWidth = Math.round(width / mRenderScale);
            int viewHeight = Math.round(height / mRenderScale);
//            Log.i(TAG, "sticker setViewSize: "+viewWidth+", "+viewHeight+", "+mRenderScale);
            mStickerDrawHelper.setViewSize(viewWidth, viewHeight);
        }
    }

    public void setDepthFrameBuffer(GLFramebuffer frameBuffer) {
        if (mAR3DEffectFilter != null) {
            mAR3DEffectFilter.setDepthFrameBuffer(frameBuffer);
        }
    }

    public void setSplitScreenFilter(SplitScreenFilter splitScreenFilter) {
        mSplitScreenFilter = splitScreenFilter;
    }

    @Override
    public void setDrawType(boolean isRecord) {
        mIsRecord = isRecord;
        if (mStickerDrawHelper != null) {
            mStickerDrawHelper.mIsRecordDraw = isRecord;
        }
    }

    @Override
    protected int createProgram(Context context) {
//        return GlUtil.createProgram(context, R.raw.vertex_face3d, R.raw.fragment_face3d);
//        return GlUtil.createProgram(context, R.raw.vertex_sticker, R.raw.fragment_sticker_no_blend);
//        return PGLNativeIpl.loadStiker3dProgram();
        return PGLNativeIpl.loadStikerCompositeProgramV2();
    }

    private void initTextureUnitsDataArr() {
        initStickerManager();
        if (mTextureIdsMap == null) {
            mTextureIdsMap = new HashMap<String, Integer>();
        }
    }

    @Override
    protected void getGLSLValues() {
        super.getGLSLValues();
        muMVPMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uMVPMatrix");
        muTexMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uTexMatrix");
        muTextureLoc = GLES20.glGetUniformLocation(mProgramHandle, "uTexture");

        initTextureUnitsDataArr();
    }

    @Override
    public boolean isNeedBlend() {
        return true;
    }

    @Override
    public void setFilterGroups(AbsFilterGroup... filterGroups) {
        if (filterGroups != null && filterGroups.length > 0) {
            AbsFilterGroup group = filterGroups[0];
            if (group != null && group instanceof CompositeFilterGroup) {
                mCompositeFilterGroup = (CompositeFilterGroup) group;
            }
            if (filterGroups.length > 1) {
                group = filterGroups[1];
                if (group != null && group instanceof ShapeFilterGroup) {
                    mShapeFilterGroup = (ShapeFilterGroup) group;
                }
            }
            if (filterGroups.length > 2) {
                group = filterGroups[2];
                if (group != null && group instanceof MakeUpFilterGroup) {
                    mMakeUpFilterGroup = (MakeUpFilterGroup) group;
                }
            }
        }
    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex,
                       int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        mCanDrawStickers = true;
        if (mStickerDrawHelper == null || mStickerDrawHelper.materialID < 1 || mStickerDrawHelper.getOrderContents2() == null || mStickerDrawHelper.getOrderContents2().isEmpty()) {
            if (mSplitScreenFilter != null && mSplitScreenFilter.isFilterEnable() && mStickerDrawHelper.mStickerSplitScreen == null) {
                mSplitScreenFilter.setFilterEnable(false);
            }
            if (mDrawCameraData) {
                mCanDrawStickers = false;
            } else {
                return;
            }
        }
        initStickerVerticesBuffer();

        if (mStickerDrawHelper == null) {
            initStickerManager();
        }
        if (mStickerDrawHelper.mResIsChange) {
            mStickerDrawHelper.mResIsChange = false;
            resetFilterData();
            clearTask();
        }

        mStickerList = mStickerDrawHelper.getOrderContents2();//test
        mIsGifMode = mStickerDrawHelper.mIsGifMode;
        mHasStickerData = mCanDrawStickers;
//        GlUtil.checkGlError("draw start");

        mFaceSize = FaceDataHelper.getInstance().getFaceSize();
        int faceSize = mFaceSize;
        if (mStickerDrawHelper.mSingleFace || !mCanDrawStickers || faceSize < 1) {
            faceSize = 1;
        }

        mDefaultTextureId = textureId;
        useProgram();
        runTask();

        boolean drawCameraData = mDrawCameraData;
        mIsLastFace = false;
        mBindTempFramebufferSuccess = false;
        mHasTextureIdCount = 0;
        mMakeUpFrameIndex = -1;
        mIsDrawActionRes = false;
        mCanInvokeSplitScreen = true;

        long time = System.currentTimeMillis();
        for (int i = 0; i < faceSize; i++) {
            if (i == faceSize - 1) {
                mIsLastFace = true;
            }
            if (mDrawCameraData && i > 0) {
                mDrawCameraData = false;
            }
            mPocoFace = FaceDataHelper.getInstance().changeFace(i).getFace();

            loadTexture(i, time);
            drawItem(i, mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);
        }
        mPocoFace = null;
        mCompositeFilterGroup = null;
        mShapeFilterGroup = null;
        mGLFramebuffer = null;
        runTask();
//        Log.i(TAG, "-------------drawItem: time:"+(System.currentTimeMillis() - time));

        unbindGLSLValues();
        unbindTexture();
        disuseProgram();
        if (!mDrawCameraData && drawCameraData) {
            mDrawCameraData = true;
        }
    }

    private boolean hasFaceAction(String action) {
        if (mPocoFace != null) {//表情动作判断
            if (FaceAction.OpenMouth.equals(action)) {
                return mPocoFace.MouthOpen;
            } else if (FaceAction.Blink.equals(action)) {
                return mPocoFace.ZhaYan;
            } else if (FaceAction.EyeBrow.equals(action)) {
                return mPocoFace.TiaoMei;
            } else if (FaceAction.NodHead.equals(action)) {
                return mPocoFace.NodHead;
            }
        }
        return false;
    }

    protected void loadTexture(int faceIndex, long time) {
        mDrawStickerCount = mDrawCameraData ? 1 : 0;
        if (!mCanDrawStickers) {
            return;
        }
        //boolean splitScreenEnable = false;
        for (Map.Entry<String, StickerSubRes> entry : mStickerList) {
            mStickerType = entry.getKey();
            if ((mStickerEntity = entry.getValue()) == null) {
                continue;
            }
            if (mStickerEntity.mIsActionRes && mPocoFace != null) {//添加表情动作判断
                mStickerEntity.mHasAction = hasFaceAction(mStickerEntity.getAction());
            }
            if (faceIndex == 0 && StickerType.Face3D.equals(mStickerType)) {
                mHasTextureIdCount++;
                if (mAR3DEffectFilter != null) {
                    mAR3DEffectFilter.setAR3DModelData(mContext, mStickerDrawHelper.materialID, mStickerEntity);
                    if (mAR3DEffectFilter.canDraw(true) && mPocoFace != null) {
                        mDrawStickerCount++;
                    }
                }
                continue;
            }
            if (mStickerDrawHelper.isResetData) {
                mStickerEntity.resetAll();
            } else if (mStickerEntity.isNeedResetWhenLostFace() && mPocoFace == null) {
                mStickerEntity.resetAll2();
            }
            mStickerEntity.setDrawStartTime(time);
            currentIndex = mStickerEntity.checkIsNeedLoadNext(false);
            if (mStickerDrawHelper.getTextureIdsByType(mStickerType)[currentIndex] == 0) {
                mStickerEntity.setContext(mContext);
                if (mStickerEntity.mCanLoadBitmap) {
                    mStickerEntity.mCanLoadBitmap = false;
                    StickerTextureTask task = new StickerTextureTask(mStickerEntity, currentIndex, new StickerTextureTask.TaskCallback() {
                        @Override
                        public void onTaskCallback(String type, StickerSubRes stickerPart, int position, Bitmap bitmap) {
                            if (type != null) {
                                int textureId = 0;
                                if (bitmap != null && !bitmap.isRecycled()) {
                                    textureId = GlUtil.createTexture(GLES20.GL_TEXTURE_2D, bitmap);
                                }
                                if (position == 0) {
                                    setTextureIdByType(type, textureId);
                                }
                                mStickerDrawHelper.getTextureIdsByType(type)[position] = textureId;
                                if (textureId > 0) {
                                    mCurrentTextureIds[mCurrentTextureIdsNumber++] = textureId;
                                }
                            }
                            if (stickerPart != null) {
                                stickerPart.mCanLoadBitmap = true;
                                stickerPart.loadNextMeta();
                            }
                        }
                    });
                    addTaskToQueue(task);
                }
                if (currentIndex > 0) {
                    currentIndex--;
                }
                setTextureIdByType(mStickerType, mStickerDrawHelper.getTextureIdsByType(mStickerType)[currentIndex]);

                runTask();
            } else {
                if (faceIndex == 0) {
                    mHasTextureIdCount++;
                }
                mStickerEntity.checkData();
                setTextureIdByType(mStickerType, mStickerDrawHelper.getTextureIdsByType(mStickerType)[currentIndex]);
            }
            if (!mStickerEntity.canDraw()) {
                continue;
            }
            if (mStickerEntity.mIsActionRes && mStickerEntity.isDrawActionRes()) {
                mIsDrawActionRes = true;
            }
            /*if (StickerType.Shoulder.equals(mStickerType)) {//肩膀支持多人脸
                if (mDrawCameraData) {
                    mDrawStickerCount++;
                }
            } else */
            if (StickerType.Foreground.equals(mStickerType) || StickerType.Frame.equals(mStickerType)
                    || StickerType.Full.equals(mStickerType) || StickerType.WaterMark.equals(mStickerType)) {
                if (mIsLastFace) {
                    mDrawStickerCount++;
                    //splitScreenEnable = true;
                }
                if (StickerType.Foreground.equals(mStickerType)) {
                    mMakeUpFrameIndex = mStickerEntity.getSpriteIndex();
                }
            } else if (mPocoFace != null && mPocoFace.mGLPoints != null) {
                mDrawStickerCount++;
                //splitScreenEnable = true;
            }

            if (mCanInvokeSplitScreen) {//分屏
                mCanInvokeSplitScreen = false;
                splitScreen(true/*splitScreenEnable*/, mStickerEntity.mAllFrameCount, mStickerEntity.getSpriteIndex());
            }
        }
        if (faceIndex == 0 && mPocoFace == null && !mIsDrawActionRes) {
            mActionKey = null;
//            Log.i(TAG, "loadTexture: -------------null");
        }
        if (mStickerDrawHelper.isResetData) {
            mStickerDrawHelper.isResetData = false;
        }

        //音效触发判断
        if (faceIndex == 0 && mOnDrawStickerResListener != null) {
            if (mStickerDrawHelper.mAnimMusicDelayTime > 0 && mStickerDrawHelper.mAnimMaxDuration > 0 && mAnimStartTime > 0) {
                long delayTime = System.currentTimeMillis() - mAnimStartTime;
                if (delayTime >= mStickerDrawHelper.mAnimMaxDuration) {
                    mAnimStartTime = 0;
                    mIsPlayingAnimMusic = false;
                    mOnDrawStickerResListener.onPlayAnimMusic(0);
                } else if (!mIsPlayingAnimMusic && delayTime >= mStickerDrawHelper.mAnimMusicDelayTime && mOnDrawStickerResListener.getPlayState(0) != OnDrawStickerResListener.PLAYING) {
//                    Log.i(TAG, "loadTexture:AnimMusic duration:" + mStickerDrawHelper.mAnimMaxDuration + ", delayTime:" + mStickerDrawHelper.mAnimMusicDelayTime + ", time:" + delayTime);
                    mOnDrawStickerResListener.onPlayAnimMusic(1);
                    mIsPlayingAnimMusic = true;
                }
            }
            if (mPocoFace != null) {//添加表情动作判断
                boolean hasAction = hasFaceAction(mStickerDrawHelper.mActionMusic);
                if (hasAction && mOnDrawStickerResListener.getPlayState(1) != OnDrawStickerResListener.PLAYING) {
                    mOnDrawStickerResListener.onPlayActionMusic(mStickerDrawHelper.mActionMusic);//可能会触发多次
                }
            }
        }
    }

    private void splitScreen(boolean enable, int allFrameCount, int frameIndex) {
        if (mSplitScreenFilter != null) {
            if (mStickerDrawHelper.mStickerSplitScreen == null || !enable) {
                mSplitScreenFilter.setFilterEnable(false);
            } else {
                if (mStickerDrawHelper.materialID != mSplitScreenFilter.getStickerId()) {
                    mSplitScreenFilter.setStickerId(mStickerDrawHelper.materialID);
                    mSplitScreenFilter.setFilterEnable(false);
                } else {
                    mSplitScreenFilter.checkFilterEnable();
                }
                if (mStickerDrawHelper.mStickerSplitScreen.getType() == 1 && mPocoFace != null) {//添加表情动作判断
                    mStickerDrawHelper.mStickerSplitScreen.setHasAction(hasFaceAction(mStickerDrawHelper.mStickerSplitScreen.getAction()));
                }
                int index = mStickerDrawHelper.mStickerSplitScreen.getSplitDataIndex(allFrameCount, frameIndex);
                //Log.i(TAG, "splitScreen: "+frameIndex+", "+index);
                if (index > -2) {
                    mSplitScreenFilter.setFilterEnable(true);
                    mSplitScreenFilter.setFilterGroups(mCompositeFilterGroup);
                    boolean reset = mStickerDrawHelper.mStickerSplitScreen.isHasReset();
                    if (reset) {
                        mStickerDrawHelper.mStickerSplitScreen.setHasReset(false);
                        mSplitScreenFilter.clearData();
                    }
                    mSplitScreenFilter.setSpiltScreenData(mStickerDrawHelper.mStickerSplitScreen.getSplitData(index));
                }
            }
        }
    }

    private void drawItem(int faceIndex, float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex,
                          int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
//        Log.i(TAG, "drawItem: 111");
        int frameBufferId = textureId;
        if (mDrawCameraData) {
            mBindTempFramebufferSuccess = true;
        } else {
            if (mDrawStickerCount < 1) {
                return;
            }
            frameBufferId = bindOrDrawFrameBuffer(mvpMatrix, coordsPerVertex, vertexStride, vertexBuffer, texMatrix, texStride, texBuffer);
        }
        if (mDrawCameraData) {
            /*镜头画面*/
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(getTextureTarget(), textureId);
            GLES20.glUniform1i(muTextureLoc, 0);

            GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mvpMatrix, 0);
            GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, texMatrix, 0);
            GLES20.glEnableVertexAttribArray(maPositionLoc);
            GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
            GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
            GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        }

        //彩妆------start-----------
        int stickerCount = 0;
        if (mStickerDrawHelper != null && mPocoFace != null) {
            mRealTimeMakeUpSubRes = mStickerDrawHelper.getMakeUpSubRes(mMakeUpFrameIndex);
            if (mRealTimeMakeUpSubRes != null) {
                boolean flag = drawMakeUp(faceIndex, mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, frameBufferId, texStride);
                if (flag) {
                    stickerCount++;
                }
            }
        }
        //彩妆------end-----------

        if (!mCanDrawStickers || mTextureIdsMap == null) {
//            Log.i(TAG, "drawItem: 222");
            return;
        }
//        mIsDebug = true;
//        int hi = 0, ei = 0, ni = 0, mi = 0, si = 0, fgi = 0, fri = 0;//测试是否同步
        int shapeId = 0;
        boolean hasDrawShape = false;
        if (mPocoFace != null) {
            shapeId = mStickerDrawHelper.mShapeTypeId;
        }
        mBindTempFramebufferSuccess = true;
        boolean bindFrameBuffer = false;
        StickerSubRes stickerSubRes = null;
        StickerSpriteFrame spriteFrame = null;
        for (Map.Entry<String, StickerSubRes> entry : mStickerList) {
            mStickerType = entry.getKey();
            mStickerEntity = entry.getValue();
//            mIsDebug = false;
//            if (StickerType.Foreground.equals(mStickerType)) {
//                mIsDebug = true;
//            }
            stickerSubRes = entry.getValue();
            if (stickerSubRes == null) {
                continue;
            }
            if (faceIndex == 0 && StickerType.Face3D.equals(mStickerType)) {
                //处理完变形之后再绘制3D
            } else {
                if (!stickerSubRes.canDraw() || (spriteFrame = stickerSubRes.getSpriteFrame()) == null) {
                    continue;
                }
            }
            if (stickerSubRes.getResShowType() == 1 && !mIsDrawActionRes) {
                continue;
            } else if (stickerSubRes.getResShowType() == 2 && mIsDrawActionRes) {
                continue;
            } else if (stickerSubRes.getResShowType() == 3) {
                if (mOnDrawStickerResListener != null) {
                    if (stickerSubRes.getResDrawState() == 0 && !mAnimState) {
                        mOnDrawStickerResListener.onAnimStateChange(1);
                        mAnimState = true;
                        mTriggerSuccess = false;
                    } else if (stickerSubRes.getResDrawState() == 1) {
                        if (stickerSubRes.getSpriteIndex() >= stickerSubRes.getAnimTriggerFrameIndex() && !mTriggerSuccess) {
                            if (stickerSubRes.isNeedFace() && mPocoFace == null) {
                                mOnDrawStickerResListener.onAnimTrigger(0);
                            } else {
                                mOnDrawStickerResListener.onAnimTrigger(1);
                            }
                            mTriggerSuccess = true;
                        }
                    } else if (mAnimState) {
                        mOnDrawStickerResListener.onAnimStateChange(0);
                        mAnimState = false;
                        mTriggerSuccess = false;
                    }
                }
            } else if (stickerSubRes.getResShowType() == 4) {
                //not implement...
            } else if (stickerSubRes.getResShowType() == 5) {
                if (mOnDrawStickerResListener != null) {
                    if (stickerSubRes.getSpriteIndex() == 0 && !mAnimState) {
                        mOnDrawStickerResListener.onAnimStateChange(1);
                        mAnimState = true;
                    } else if (stickerSubRes.getSpriteIndex() > 0 && mAnimState) {
                        mAnimState = false;
                    }
                }
            }
            /*if (StickerType.Shoulder.equals(mStickerType)) {
                if (!mDrawCameraData) {
                    continue;
                }
            } else */if (StickerType.Foreground.equals(mStickerType) || StickerType.Frame.equals(mStickerType)
                    || StickerType.Full.equals(mStickerType) || StickerType.WaterMark.equals(mStickerType)) {
                if (!mIsLastFace) {
                    continue;
                }
                if (stickerSubRes.isNeedFace() && mPocoFace == null) {
                    continue;
                }
                if (!mIsGifMode && ((!stickerSubRes.isS916Enable() && mStickerDrawHelper.getRatioType() == 1))
                        || (!stickerSubRes.isGifEnable() && mStickerDrawHelper.getRatioType() == 2)
                        || (!stickerSubRes.isS43Enable() && mStickerDrawHelper.getRatioType() == 3)
                        || (!stickerSubRes.isSFullEnable() && mStickerDrawHelper.getRatioType() == 5)) {
                    continue;
                }
            } else if (mPocoFace == null || mPocoFace.mGLPoints == null) {
                continue;
            }
//            Log.i(TAG, "drawItem: mStickerType:"+mStickerType+", faceIndex:"+faceIndex);
            if (mIsLastFace && shapeId > 0 && !hasDrawShape && (StickerType.Foreground.equals(mStickerType)
                    || (faceIndex == 0 && StickerType.Face3D.equals(mStickerType)))) {
                //处理最后一个人脸变形
                int bufferId = drawShape(shapeId, faceIndex, mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);
                hasDrawShape = true;
                if (stickerCount == 0 && bufferId > 0) {
                    bindFrameBuffer = true;
                    if (StickerType.Face3D.equals(mStickerType) && mGLFramebuffer != null) {
                        frameBufferId = mGLFramebuffer.getCurrentTextureId();
                    }
                }
            }
            if (faceIndex == 0 && StickerType.Face3D.equals(mStickerType)) {
                //绘制3D
                boolean r = drawAR3D(true, mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, frameBufferId, texStride);
                if (r) {
                    stickerCount++;
                }
                continue;
            }
            if (mIsGifMode && !stickerSubRes.isGifEnable()) {
                continue;
            }
            int stickerTextureId = getTextureIdByType(mStickerType);
            if (stickerTextureId == 0) {
                continue;
            }

            //音效触发判断
            if (mStickerEntity.mIsActionRes) {
                if (mActionKey == null) {
                    mActionKey = mStickerType;
                }
                if (mStickerType.equals(mActionKey)) {
                    if (mOnDrawStickerResListener != null) {
                        if (mStickerEntity.getActionResDrawState() == 1 && mOnDrawStickerResListener.getPlayState(2) != OnDrawStickerResListener.PLAYING) {
                            mOnDrawStickerResListener.onPlayActionAnimMusic(mStickerEntity.getAction(), 1);//可能会触发多次
                        } else if (mStickerEntity.getActionResDrawState() == 3) {
                            mOnDrawStickerResListener.onPlayActionAnimMusic(mStickerEntity.getAction(), 0);
                        }
                    }
                }
            }

            int compositeMode = stickerSubRes.getLayerCompositeMode();
            if ((compositeMode > 0 && stickerCount > 0) || bindFrameBuffer) {
                frameBufferId = bindOrDrawFrameBuffer(mvpMatrix, coordsPerVertex, vertexStride, vertexBuffer, texMatrix, texStride, texBuffer);
            }
            stickerCount++;

            if (mStickerType.equals(StickerType.Face)) {
                float[] vertexes = getPoints();
                vertexArray3d.updateBuffer(vertexes, 0, vertexes.length);

                if (compositeMode > 0) {
                    float[] faceData = new float[vertexes.length];
                    for (int i = 0; i < vertexes.length; i++) {
                        faceData[i] = (vertexes[i] + 1.0f) / 2.0f;
                    }
                    // 创建纹理坐标缓冲
                    if (mFaceDataBuffer == null || mFaceDataBuffer.capacity() != faceData.length * 4) {
                        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(faceData.length * 4);
                        byteBuffer.order(ByteOrder.nativeOrder());
                        mFaceDataBuffer = byteBuffer.asFloatBuffer();
                    } else {
                        mFaceDataBuffer.clear();
                    }
                    mFaceDataBuffer.put(faceData);
                    mFaceDataBuffer.position(0);

                    drawComposite(stickerTextureId, stickerSubRes.getLayerCompositeMode(), stickerSubRes.getLayerOpaqueness(), vertexArray3d.textureVerticesBuffer, mIndexLength, mSticker3dDrawIndex,
                            mvpMatrix, vertexArray3d.vertexBuffer, firstVertex, vertexCount, coordsPerVertex, 0, texMatrix, mFaceDataBuffer, frameBufferId, 0);

                } else {
                    drawNormal(mvpMatrix, vertexArray3d.vertexBuffer, firstVertex, vertexCount, coordsPerVertex, 0, texMatrix, vertexArray3d.textureVerticesBuffer, stickerTextureId, texStride, mIndexLength, mSticker3dDrawIndex);
                }

            } else {
                if (compositeMode > 0) {
//                    float[] points = mStickerDrawHelper.getStickerPointsByType(stickerSubRes, spriteFrame, mStickerType, true);//test
//                    float[] textureVertex = mStickerDrawHelper.getSpriteTextureVertex(stickerSubRes.getImgWidth(), stickerSubRes.getImgHeight(), spriteFrame.getFrame(), 0);
//                    vertexArray.updateBuffer(points, textureVertex);
//
//                    float[] faceData = new float[points.length];
//                    for (int i = 0; i < points.length; i++) {
//                        faceData[i] = (points[i] + 1.0f) / 2.0f;
//                    }
//                    // 创建纹理坐标缓冲
//                    if (mFaceDataBuffer1 == null || mFaceDataBuffer1.capacity() < faceData.length * 4) {
//                        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(faceData.length * 4);
//                        byteBuffer.order(ByteOrder.nativeOrder());
//                        mFaceDataBuffer1 = byteBuffer.asFloatBuffer();
//                    } else {
//                        mFaceDataBuffer1.clear();
//                    }
//                    mFaceDataBuffer1.put(faceData);
//                    mFaceDataBuffer1.position(0);
//
//                    drawComposite2(stickerTextureId, stickerSubRes.getLayerCompositeMode(), stickerSubRes.getLayerOpaqueness(), vertexArray.textureVerticesBuffer, -1, null,
//                            mvpMatrix, vertexArray.vertexBuffer, firstVertex, vertexCount, coordsPerVertex, 0, texMatrix, mFaceDataBuffer1, frameBufferId, 0, 0, 0);

//=============================================================

                    // 计算透视变换
                    float[] points = mStickerDrawHelper.getSticker3DPoints(stickerSubRes, spriteFrame, mStickerType, mFaceSize, mPocoFace);
                    float[] textureVertex = mStickerDrawHelper.get3DTextureVertex(stickerSubRes.getImgWidth(), stickerSubRes.getImgHeight(), spriteFrame.getFrame(), 0);

                    if (mSticker3dModelData == null) {
                        mSticker3dModelData = new Sticker3dModelData();
                    }
                    // 设置相机位置
                    mSticker3dModelData.setLookAt();
                    // 设置中心点
                    mSticker3dModelData.setCenterPosition(mStickerDrawHelper.mCenterX, mStickerDrawHelper.mCenterY, 0.0f);

                    if (StickerType.Shoulder.equals(mStickerType) || StickerType.Foreground.equals(mStickerType) || StickerType.Frame.equals(mStickerType)
                            || StickerType.Full.equals(mStickerType) || StickerType.WaterMark.equals(mStickerType)) {
                        // 帧、前景和水印不需要旋转
                        mSticker3dModelData.setModelAngle(0.0f, 0.0f, 0.0f);
                    } else {
                        float angleX = (float) (mStickerDrawHelper.mFacePitch * 180 / Math.PI);
                        float angleY = (float) (mStickerDrawHelper.mFaceYaw * 180 / Math.PI);
                        mSticker3dModelData.setModelAngle(angleX, angleY, 0.0f);
                    }
                    mSticker3dModelData.calculateMVPMatrix();

                    // 设置vertex顶点位置
                    mSticker3dModelData.setVertexVertices(points);
                    // 设置texture位置
                    mSticker3dModelData.setTextureVertices(textureVertex);

                    drawComposite2(stickerTextureId, stickerSubRes.getLayerCompositeMode(), stickerSubRes.getLayerOpaqueness(), mSticker3dModelData.getTextureBuffer(), -1, null,
                            mSticker3dModelData.getMVPMatrix(), mSticker3dModelData.getVertexBuffer(), firstVertex, vertexCount, coordsPerVertex, 8, texMatrix,
                            texBuffer, frameBufferId, 8, mSticker3dModelData.getCenterX(), mSticker3dModelData.getCenterY());

                } else {
                    float[] points = mStickerDrawHelper.getStickerPointsByType(stickerSubRes, spriteFrame, mStickerType, mFaceSize, mPocoFace, true);//test
                    float[] textureVertex = mStickerDrawHelper.getSpriteTextureVertex(stickerSubRes.getImgWidth(), stickerSubRes.getImgHeight(), spriteFrame.getFrame(), 0);
                    vertexArray.updateBuffer(points, textureVertex);
                    drawNormal(mvpMatrix, vertexArray.vertexBuffer, firstVertex, vertexCount, coordsPerVertex, 0, texMatrix, vertexArray.textureVerticesBuffer, stickerTextureId, texStride, -1, null);
                }
            }
        }
        if (shapeId > 0 && ((mIsLastFace && !hasDrawShape) || (!mIsLastFace))) {
            //处理人脸变形
            drawShape(shapeId, faceIndex, mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);
        }
        if (mIsDebug) {
//            Log.i(TAG, "drawItem: h:" + hi + ", e:" + ei + ", n:" + ni + ", m:" + mi + ", s:" + si + ", fg:" + fgi + ", fr:" + fri + ", " + mIsDrawActionRes + ", " + (mPocoFace == null ? false : mPocoFace.NodHead));
        }
        if (mIsLastFace && mGLFramebuffer != null) {
            mGLFramebuffer.setHasBind(false);
        }
    }

    private boolean drawAR3D(boolean useOwnBuffer, float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex,
                             int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        if (mAR3DEffectFilter != null && mAR3DEffectFilter.canDraw(false) && mPocoFace != null) {
            mAR3DEffectFilter.bindOwnFrameBuffer(useOwnBuffer);
            mAR3DEffectFilter.setFaceData(mPocoFace);
            mAR3DEffectFilter.onDraw(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);

            int frameBufferId = mAR3DEffectFilter.getDepthFrameBufferId();
            if (useOwnBuffer && frameBufferId > 0) {
                if (mGLFramebuffer != null) {
                    mBindTempFramebufferSuccess = mGLFramebuffer.bindNext(true);
                }
                useProgram();//重新绑定handle

                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(getTextureTarget(), frameBufferId);
                GLES20.glUniform1i(muTextureLoc, 0);

                GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mvpMatrix, 0);
                GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, texMatrix, 0);
                GLES20.glEnableVertexAttribArray(maPositionLoc);
                GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
                GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
                GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            }
            return true;
        }
        return false;
    }

    private void drawNormal(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex,
                            int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride, int elementsCount, ByteBuffer indexBuffer) {
        useProgram();

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(getTextureTarget(), textureId);
        GLES20.glUniform1i(muTextureLoc, 1);

        GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, texMatrix, 0);
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);

        if (elementsCount > 0 && indexBuffer != null) {
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, elementsCount, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        } else {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        }
    }

    /**
     * 混合模式有透视
     */
    private void drawComposite2(int stickerTextureId, int compositeMode, float alpha, FloatBuffer stickerTextureBuffer, int elementsCount, ByteBuffer indexBuffer,
                                float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex,
                                int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride, float centerX, float centerY) {
        if (mCompositeFilterGroup != null) {
            if (mCompositeData == null) {
                mCompositeData = new CompositeData();
            }
            if (mCompositeData != null) {
                mCompositeData.setData(stickerTextureId, compositeMode, alpha, stickerTextureBuffer, elementsCount, indexBuffer);
            }
            mCompositeFilter = mCompositeFilterGroup.setCompositeFilterData2(mCompositeData);
            if (mCompositeFilter != null) {
                mCompositeFilter.setCenterPoint(centerX, centerY);
                mCompositeFilter.onDraw(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);
                mCompositeFilter = null;
            }
        }
    }

    private void drawComposite(int stickerTextureId, int compositeMode, float alpha, FloatBuffer stickerTextureBuffer, int elementsCount, ByteBuffer indexBuffer,
                               float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex,
                               int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        if (mCompositeFilterGroup != null) {
            if (mCompositeData == null) {
                mCompositeData = new CompositeData();
            }
            if (mCompositeData != null) {
                mCompositeData.setData(stickerTextureId, compositeMode, alpha, stickerTextureBuffer, elementsCount, indexBuffer);
            }
            mCompositeFilter = mCompositeFilterGroup.setCompositeFilterData(mCompositeData);
            if (mCompositeFilter != null) {
                mCompositeFilter.onDraw(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);
                mCompositeFilter = null;
            }
        }
    }

    private boolean drawMakeUp(int faceIndex, float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex,
                               int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        boolean flag = false;
        if (mMakeUpFilterGroup != null) {
            MakeUpBaseFilter makeUpBaseFilter = null;
            int frameBufferId = textureId;
            for (int i = 1; i <= 4; i++) {
                if (i == 1 && mRealTimeMakeUpSubRes.mEyeLash == 0 && mRealTimeMakeUpSubRes.mEyeLine == 0 && mRealTimeMakeUpSubRes.mEyeShadow == 0) {
                    continue;
                } else if (i == 2 && mRealTimeMakeUpSubRes.mBlushLeft == 0 && mRealTimeMakeUpSubRes.mBlushRight == 0) {
                    continue;
                } else if (i == 3 && mRealTimeMakeUpSubRes.mLip == 0) {
                    continue;
                } else if (i == 4 && mRealTimeMakeUpSubRes.mLipHighLight == 0 && mRealTimeMakeUpSubRes.mLipHighLightUp == 0) {
                    continue;
                }

                makeUpBaseFilter = mMakeUpFilterGroup.getFilter(i);
                if (makeUpBaseFilter != null) {
                    frameBufferId = textureId;
                    if (mGLFramebuffer != null) {
                        mBindTempFramebufferSuccess = mGLFramebuffer.bindNext(true);
                        frameBufferId = mGLFramebuffer.getPreviousTextureId();
                    }
                    makeUpBaseFilter.setFramebuffer(mGLFramebuffer);
                    makeUpBaseFilter.setUseOtherFaceData(true);
                    makeUpBaseFilter.setFaceData(mPocoFace);
                    makeUpBaseFilter.setMakeUpRes(mRealTimeMakeUpSubRes);

                    makeUpBaseFilter.onDraw(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, frameBufferId, texStride);
                    flag = true;
                }
            }
        }
        return flag;
    }

    private int drawShape(int shapeId, int faceIndex, float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex,
                          int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        int frameBufferId = -1;
        if (mShapeFilterGroup != null) {
            mFaceShapeFilter = (FaceShapeFilter) mShapeFilterGroup.getFilter();
            if (mFaceShapeFilter != null) {
                if (mTextureMatrix == null) {
//                  mTextureMatrix = new float[]{0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f};//origin texMatrix
                    mTextureMatrix = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
                }
                frameBufferId = textureId;
                if (mGLFramebuffer != null) {
                    mBindTempFramebufferSuccess = mGLFramebuffer.bindNext(true);
                    frameBufferId = mGLFramebuffer.getPreviousTextureId();
                }
//                Log.i(TAG, "drawShape: shape faceIndex:"+faceIndex);
                mFaceShapeFilter.setShapeFilterId(shapeId);
                mFaceShapeFilter.setFramebuffer(mGLFramebuffer);
                mFaceShapeFilter.setUseOtherFaceData(true);
                mFaceShapeFilter.setFaceData(mPocoFace);
                mFaceShapeFilter.onDraw(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, mTextureMatrix, texBuffer, frameBufferId, texStride);
            }
            mFaceShapeFilter = null;
        }
        return frameBufferId;
    }

    private int bindOrDrawFrameBuffer(float[] mvpMatrix, int coordsPerVertex, int vertexStride, FloatBuffer vertexBuffer, float[] texMatrix, int texStride, FloatBuffer texBuffer) {
        int frameBufferId = mDefaultTextureId;
        if (mBindTempFramebufferSuccess) {
            mBindTempFramebufferSuccess = false;

            if (mGLFramebuffer != null) {
                mBindTempFramebufferSuccess = mGLFramebuffer.bindNext(true);
                frameBufferId = mGLFramebuffer.getPreviousTextureId();
            }
            useProgram();//重新绑定handle

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(getTextureTarget(), frameBufferId);
            GLES20.glUniform1i(muTextureLoc, 0);

            GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mvpMatrix, 0);
            GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, texMatrix, 0);
            GLES20.glEnableVertexAttribArray(maPositionLoc);
            GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
            GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
            GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        }
        return frameBufferId;
    }

    @Override
    protected void bindTexture(int textureId) {
        mDefaultTextureId = textureId;
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
    }

    @Override
    protected void drawArrays(int firstVertex, int vertexCount) {
    }

    @Override
    protected void unbindGLSLValues() {
        super.unbindGLSLValues();
    }

    @Override
    protected void unbindTexture() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        resetTextureUnitsId();
    }

    @Override
    public void releaseProgram() {
//        super.releaseProgram();
        GLES20.glDeleteProgram(mProgramHandle);
        if (mCurrentTextureIds != null) {
            GLES20.glDeleteTextures(mCurrentTextureIds.length, mCurrentTextureIds, 0);
        }
        mContext = null;
        mHasInitFramebuffer = false;
        mProgramHandle = -1;
        mStickerEntity = null;
        mStickerList = null;
        mStickerDrawHelper = null;
        mCurrentTextureIds = null;
        if (mTextureIdsMap != null) {
            mTextureIdsMap.clear();
            mTextureIdsMap = null;
        }
        vertexArray = null;
        vertexArray3d = null;
        mFaceDataBuffer = null;
        mFaceDataBuffer1 = null;
        mSticker3dDrawIndex = null;
        mOnDrawStickerResListener = null;
        mTaskList = null;
        mPocoFace = null;

        if (mSticker3dModelData != null) {
            mSticker3dModelData.release();
            mSticker3dModelData = null;
        }

        if (mAR3DEffectFilter != null) {
            mAR3DEffectFilter.releaseProgram();
            mAR3DEffectFilter = null;
        }
    }

    @Override
    public void loadNextTexture(boolean load) {
        if (load && mHasStickerData) {
            mHasStickerData = false;
            if (mHasTextureIdCount != mStickerList.size()) {
                return;
            }
            long time = System.currentTimeMillis();
            if (mAnimStartTime <= 0) {
                mAnimStartTime = time;
                mIsPlayingAnimMusic = false;
            } else if (time - mAnimStartTime >= mStickerDrawHelper.mAnimMaxDuration) {
                mAnimStartTime = time;
                mIsPlayingAnimMusic = false;
            }
            for (Map.Entry<String, StickerSubRes> entry : mStickerList) {
                if (StickerType.Face3D.equals(entry.getKey())) {
                    continue;
                }
                if (entry.getValue() != null) {
                    entry.getValue().setDrawEndTime(time);
                    entry.getValue().checkIsNeedLoadNext(load);
                }
            }
        }
    }

    @Override
    public void resetFilterData() {
        mAnimStartTime = 0;
        mIsPlayingAnimMusic = false;
        if (mCurrentTextureIdsNumber != 0) {
            GLES20.glDeleteTextures(mCurrentTextureIdsNumber, mCurrentTextureIds, 0);
            initCurrentTextureIdsArr();
        }
        if (mStickerDrawHelper != null) {
            mStickerDrawHelper.resetFilterData();
        }
        mActionKey = null;
    }

    private void resetTextureUnitsId() {
        if (mTextureIdsMap != null) {
            mTextureIdsMap.clear();
        }
    }

    public void setTextureIdByType(String type, Integer textureId) {
        if (mTextureIdsMap != null) {
            mTextureIdsMap.put(type, textureId);
        }
    }

    public int getTextureIdByType(String type) {
        if (mTextureIdsMap != null) {
            Integer value = mTextureIdsMap.get(type);
            if (null != value) {
                return value.intValue();
            }
        }
        return 0;
    }

    public float[] getPoints() {
        float[] vertices = new float[242];//121 * 2
        if (mPocoFace == null || mPocoFace.mGLPoints == null) {
            return vertices;
        }
        PointF[] points = mPocoFace.mGLPoints;

        for (int i = 0; i < 106; ++i) {
            vertices[i * 2] = points[i].x;
            vertices[i * 2 + 1] = points[i].y;
        }

        float x1 = points[35].x;
        float y1 = points[35].y;
        float x2 = points[40].x;
        float y2 = points[40].y;
        float x4, y4, x5, y5, x6, y6;
        float x3 = points[72].x;
        float y3 = points[72].y;
        x4 = (x1 * x1 * x3 + x2 * x2 * x3 - x3 * (y1 - y2) * (y1 - y2) -
                2 * x1 * (x2 * x3 + (y1 - y2) * (y2 - y3)) +
                2 * x2 * (y1 - y2) * (y1 - y3)) / ((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        y4 = (2 * (x1 - x2) * (-x2 * y1 + x3 * (y1 - y2) + x1 * y2) - (x1 - x2 + y1 -
                y2) * (x1 - x2 - y1 + y2) * y3) / ((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        x3 = points[75].x;
        y3 = points[75].y;
        x5 = (x1 * x1 * x3 + x2 * x2 * x3 - x3 * (y1 - y2) * (y1 - y2) -
                2 * x1 * (x2 * x3 + (y1 - y2) * (y2 - y3)) +
                2 * x2 * (y1 - y2) * (y1 - y3)) / ((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        y5 = (2 * (x1 - x2) * (-x2 * y1 + x3 * (y1 - y2) + x1 * y2) - (x1 - x2 + y1 -
                y2) * (x1 - x2 - y1 + y2) * y3) / ((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        x6 = (x4 + x5) / 2;
        y6 = (y4 + y5) / 2;
        vertices[212] = (points[4].x + points[84].x) / 2;
        vertices[213] = (points[4].y + points[84].y) / 2;
        vertices[214] = (points[29].x + points[90].x) / 2;
        vertices[215] = (points[29].y + points[90].y) / 2;
        vertices[216] = x6 + (x4 - x6) / 3 * 4;
        vertices[217] = y6 + (y4 - y6) / 3 * 4;
        vertices[218] = x6 + (x4 - x6) / 3 * 2;
        vertices[219] = y6 + (y4 - y6) / 3 * 2;
        vertices[220] = x6;
        vertices[221] = y6;
        vertices[222] = x6 + (x5 - x6) / 3 * 2;
        vertices[223] = y6 + (y5 - y6) / 3 * 2;
        vertices[224] = x6 + (x5 - x6) / 3 * 4;
        vertices[225] = y6 + (y5 - y6) / 3 * 4;
        float mid_x = (points[29].x + points[3].x) / 2;
        float mid_y = (points[29].y + points[3].y) / 2;
        float right_x = points[29].x + points[29].x - mid_x;
        float right_y = points[29].y + points[29].y - mid_y;
        float left_x = points[3].x + points[3].x - mid_x;
        float left_y = points[3].y + points[3].y - mid_y;
        float top_x = x6 * 2 - points[45].x;
        float top_y = y6 * 2 - points[45].y;
        float bottom_x = points[16].x * 2 - points[93].x;
        float bottom_y = points[16].y * 2 - points[93].y;
        vertices[226] = top_x + (left_x - mid_x);
        vertices[227] = top_y + (left_y - mid_y);
        vertices[228] = top_x;
        vertices[229] = top_y;
        vertices[230] = top_x + (right_x - mid_x);
        vertices[231] = top_y + (right_y - mid_y);
        vertices[232] = left_x;
        vertices[233] = left_y;
        vertices[234] = right_x;
        vertices[235] = right_y;
        vertices[236] = bottom_x + (left_x - mid_x);
        vertices[237] = bottom_y + (left_y - mid_y);
        vertices[238] = bottom_x;
        vertices[239] = bottom_y;
        vertices[240] = bottom_x + (right_x - mid_x);
        vertices[241] = bottom_y + (right_y - mid_y);

        return vertices;
    }
}
