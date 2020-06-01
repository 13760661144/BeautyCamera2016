package cn.poco.glfilter.sticker;

import android.graphics.PointF;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Map;

import cn.poco.dynamicSticker.StickerSound;
import cn.poco.dynamicSticker.StickerType;
import cn.poco.dynamicSticker.v2.StickerSplitScreen;
import cn.poco.dynamicSticker.v2.StickerSpriteFrame;
import cn.poco.dynamicSticker.v2.StickerSubRes;
import cn.poco.image.PocoFace;
import cn.poco.image.Sslandmarks;
import cn.poco.resource.RealTimeMakeUpRes;
import cn.poco.resource.RealTimeMakeUpResMgr;
import cn.poco.resource.RealTimeMakeUpSubRes;
import cn.poco.resource.VideoStickerRes;

public class StickerDrawHelper {

    private static final String TAG = "bbb";
    private volatile static StickerDrawHelper sInstance;
    private final float Ratio16_9 = 16.0f / 9;
    private final float Ratio4_3 = 4.0f / 3;
    private final float Ratio1_1 = 1.0f;
    private final float Ratio9_16 = 9.0f / 16;

    private int mScreenOrientation;
    /**
     * height/width
     */
    private float mViewRatio = 4.0f / 3;
    private int mViewWidth, mViewHeight;
    public int materialID = -1;
    public boolean mSingleFace;
    private float mPreviewRatio;

    //    private int mMaterialCount;//素材个数
    public int mTypeCount = 9;//每个素材的类型数
    public int mEachTypeOfTextureCount = 120;//每种类型的纹理数量
    private int[][] mMaterialsTextureIds; //纹理id

    private ArrayList<Map.Entry<String, StickerSubRes>> mOrderContents2;
    public String mActionName;
    public int mShapeTypeId;
    private int mBSWidth, mBSHeight;

    public boolean mIsGifMode;
    private int mFrameTopPadding;
    private int mFrameTopPaddingOnRatio1_1;

    private PocoFace mCurrentPocoFace;
    public float mFacePitch;//x轴
    public float mFaceYaw;//y轴
    public float mFaceRoll;//z轴  左正右负

    /**
     * 0,1:leftTop, 2,3:rightTop, 4,5:leftBottom, 5,6:rightBottom
     */
    private float[] mPoints;
    private float[] mTexturePoints;

    public boolean mIsRecordDraw = false;
    public boolean isResetData;

    public String mActionMusic;//action名称
    public int mAnimMusicDelayTime = -1;//毫秒
    public int mAnimMaxDuration;
    public StickerSplitScreen mStickerSplitScreen;

    public boolean mResIsChange;

    public static StickerDrawHelper getInstance() {
        if (sInstance == null) {
            synchronized (StickerDrawHelper.class) {
                if (sInstance == null) {
                    sInstance = new StickerDrawHelper();
                }
            }
        }
        return sInstance;
    }

    private StickerDrawHelper() {
        /*String model = Build.MODEL.toUpperCase(Locale.CHINA);
        if ("MI 5".equals(model)) {
        }*/
    }

    /**
     * @param orientation 0：正常，1：向左旋转90度， 2：向左(右)旋转180度， 3：向右旋转90度
     */
    public void setScreenOrientation(int orientation) {
        mScreenOrientation = orientation;
    }

    public void setViewSize(int width, int height) {
        mViewWidth = width;
        mViewHeight = height;
        if (mViewWidth > 0 && mViewHeight > 0) {
            mViewRatio = mViewHeight * 1.0f / mViewWidth;
            if (mViewRatio > Ratio16_9) {
                mViewRatio = Ratio16_9;
                mViewHeight = Math.round(mViewWidth * mViewRatio);
            }
        }
//        Log.i(TAG, "setViewSize: " + mViewWidth + ", " + mViewHeight + ", " + mViewRatio);
    }

    public int getStickerId() {
        return materialID;
    }

    public float[] getStickerPointsByType(StickerSubRes stickerSubRes, StickerSpriteFrame stickerSpriteFrame, String type, int faceSize, PocoFace face, boolean calculateDegree) {
        float[] mCurrentVertexPoints = null;
        mPoints = null;
        mTexturePoints = null;
        if (type.equals(StickerType.WaterMark) || !mIsRecordDraw || faceSize > 1) {
            mCurrentPocoFace = face;
            calculatePointsByType(stickerSubRes, stickerSpriteFrame, type, calculateDegree);
            mCurrentVertexPoints = mPoints;
            mCurrentPocoFace = null;
        }
        if (mCurrentVertexPoints == null) {
            mCurrentVertexPoints = new float[8];//18
        }
        return mCurrentVertexPoints;
    }

    private void calculatePointsByType(StickerSubRes stickerSubRes, StickerSpriteFrame stickerSpriteFrame, String type, boolean calculateDegree) {
        if (stickerSubRes == null || stickerSpriteFrame == null) {
//            Log.i(TAG, "calculatePointsByType mStickerEntity is null:"+type);
            return;
        }
        int bmpWidth = stickerSpriteFrame.getFrameWidth();
        int bmpHeight = stickerSpriteFrame.getFrameHeight();
//        float bmpHWRatio = bmpHeight * 1.0f / bmpWidth;
        float bmpScale = stickerSubRes.getScale();
        float bmpTransX = stickerSubRes.getOffsetX();
        float bmpTransY = stickerSubRes.getOffsetY();

        if (mCurrentPocoFace != null) {
            float baseWidth = 0;
            float centerX = 0;
            float centerY = 0;
            if (calculateDegree) {
                mFaceRoll = (float) (Math.PI / 2.0 - mCurrentPocoFace.roll);
                //mFaceRoll = PointsUtils.getRadians(mCurrentPocoFace.mGLPoints[Sslandmarks.rEyeOuter], mCurrentPocoFace.mGLPoints[Sslandmarks.lEyeOuter]);
            } else {
                mFaceRoll = 0.0f;
            }
            int rotateType = mScreenOrientation;//旋转方向
            float dxRatio = 1.0f;
            float dyRatio = 1.0f;
            if (rotateType == 1 || rotateType == 3) {
                dxRatio = 1.0f / mViewRatio;
            } else {
                dyRatio = mViewRatio;
            }
            if (type.equals(StickerType.Head)) {
                baseWidth = PointsUtils.getDistance(mCurrentPocoFace.mGLPoints[Sslandmarks.rForeHead], mCurrentPocoFace.mGLPoints[Sslandmarks.lForeHead], dxRatio, dyRatio);
                PointF cp = PointsUtils.getCenterPoint(mCurrentPocoFace.mGLPoints[Sslandmarks.rForeHead], mCurrentPocoFace.mGLPoints[Sslandmarks.lForeHead]);
                centerX = cp.x;
                centerY = cp.y;
                if (rotateType == 1 || rotateType == 3) {
                    centerY = mCurrentPocoFace.mGLPoints[Sslandmarks.mForeHead].y;
                } else {
                    centerX = mCurrentPocoFace.mGLPoints[Sslandmarks.mForeHead].x;
                }
                calculatePoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, baseWidth, centerX, centerY, 0.5f, 0.5f, true);

            } else if (type.equals(StickerType.Ear)) {
                baseWidth = PointsUtils.getDistance(mCurrentPocoFace.mGLPoints[Sslandmarks.EarlobeRight], mCurrentPocoFace.mGLPoints[Sslandmarks.EarlobeLeft], dxRatio, dyRatio);
                PointF cp = PointsUtils.getCenterPoint(mCurrentPocoFace.mGLPoints[Sslandmarks.EarlobeRight], mCurrentPocoFace.mGLPoints[Sslandmarks.EarlobeLeft]);
                centerX = cp.x;
                centerY = cp.y;
                /*if (rotateType == 1 || rotateType == 3) {
                    centerY = mCurrentPocoFace.mGLPoints[Sslandmarks.EarlobeLeft].y;
                } else {
                    centerX = mCurrentPocoFace.mGLPoints[Sslandmarks.EarlobeLeft].x;
                }*/
                calculatePoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, baseWidth, centerX, centerY, 0.5f, 0.5f, true);

            } else if (type.equals(StickerType.Eye)) {
                baseWidth = PointsUtils.getDistance(mCurrentPocoFace.mGLPoints[Sslandmarks.rEyeOuter], mCurrentPocoFace.mGLPoints[Sslandmarks.lEyeOuter], dxRatio, dyRatio);
                PointF cp = PointsUtils.getCenterPoint(mCurrentPocoFace.mGLPoints[Sslandmarks.rEyeOuter], mCurrentPocoFace.mGLPoints[Sslandmarks.lEyeOuter]);
                centerX = cp.x;
                centerY = cp.y;
                if (rotateType == 1 || rotateType == 3) {
                    centerY = mCurrentPocoFace.mGLPoints[Sslandmarks.lrEyeCenter].y;
                } else {
                    centerX = mCurrentPocoFace.mGLPoints[Sslandmarks.lrEyeCenter].x;
                }
                calculatePoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, baseWidth, centerX, centerY, 0.5f, 0.5f, true);

            } else if (type.equals(StickerType.Nose)) {
                baseWidth = PointsUtils.getDistance(mCurrentPocoFace.mGLPoints[Sslandmarks.NoseRight], mCurrentPocoFace.mGLPoints[Sslandmarks.NoseLeft], dxRatio, dyRatio);
                PointF cp = PointsUtils.getCenterPoint(mCurrentPocoFace.mGLPoints[Sslandmarks.NoseRight], mCurrentPocoFace.mGLPoints[Sslandmarks.NoseLeft]);
                centerX = cp.x;
                centerY = cp.y;
                if (rotateType == 1 || rotateType == 3) {
                    centerY = mCurrentPocoFace.mGLPoints[Sslandmarks.NoseTop].y;
                } else {
                    centerX = mCurrentPocoFace.mGLPoints[Sslandmarks.NoseTop].x;
                }
                calculatePoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, baseWidth, centerX, centerY, 0.5f, 0.5f, true);

            } else if (type.equals(StickerType.Mouth)) {
                baseWidth = PointsUtils.getDistance(mCurrentPocoFace.mGLPoints[Sslandmarks.rMouthCorner], mCurrentPocoFace.mGLPoints[Sslandmarks.lMouthCorner], dxRatio, dyRatio);
                PointF cp = PointsUtils.getCenterPoint(mCurrentPocoFace.mGLPoints[Sslandmarks.rMouthCorner], mCurrentPocoFace.mGLPoints[Sslandmarks.lMouthCorner]);
                centerX = cp.x;
                centerY = cp.y;
                calculatePoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, baseWidth, centerX, centerY, 0.5f, 0.5f, true);

            } else if (type.equals(StickerType.Shoulder)) {
                PointF feature[] = new PointF[mCurrentPocoFace.mGLPoints.length];
                for (int i = 0; i < feature.length; i++) {
                    feature[i] = new PointF();
                    feature[i].x = (mCurrentPocoFace.mGLPoints[i].x + 1.f) * mViewWidth / 2;
                    feature[i].y = (mCurrentPocoFace.mGLPoints[i].y + 1.f) * mViewHeight / 2;
                }
                float face_roll = mFaceRoll;
                if (face_roll < 0.0f) {
                    face_roll += (float) (Math.PI * 2.0f);
                }

                //两眼的中心点
                PointF leyeCenter = new PointF(feature[Sslandmarks.lEyeCenter].x, feature[Sslandmarks.lEyeCenter].y);
                PointF reyeCenter = new PointF(feature[Sslandmarks.rEyeCenter].x, feature[Sslandmarks.rEyeCenter].y);
                PointF face_coordinate_center = new PointF((leyeCenter.x + reyeCenter.x) / 2, (leyeCenter.y + reyeCenter.y) / 2);

                //旋转
                for (int i = 0; i < feature.length; i++) {
                    PointF t_p = feature[i];
                    feature[i] = rotatePoint(t_p, face_coordinate_center, -face_roll);
                }

                float[] ret = calculateShoulderParam(feature);
                PointF world_coor_result_p = new PointF(ret[0], ret[1]);
                PointF r_result_p = rotatePoint(world_coor_result_p, face_coordinate_center, face_roll);

                baseWidth = PointsUtils.getDistance(mCurrentPocoFace.mGLPoints[Sslandmarks.rForeHead], mCurrentPocoFace.mGLPoints[Sslandmarks.lForeHead], dxRatio, dyRatio) * 3.8f;
                centerX = r_result_p.x / mViewWidth * 2 - 1.f;
                centerY = r_result_p.y / mViewHeight * 2 - 1.f;

                calculatePoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, baseWidth, centerX, centerY, 0.5f, 0.5f, true);
            }
            if (!calculateDegree) {
                //mFaceRoll = mCurrentPocoFace.roll;
                mFaceRoll = (float) (Math.PI / 2 - mCurrentPocoFace.roll);
            }
            if (mFacePoints != null) {
                return;
            }
        }
        float xOffset = 0.0f;
        float yOffset = 0.0f;
        if (mPreviewRatio > 0.0f && (type.equals(StickerType.Foreground) || type.equals(StickerType.Frame))) {
            if (mPreviewRatio < Ratio16_9) {
                if (type.equals(StickerType.Foreground)) {
                    yOffset = -(mViewWidth * Ratio16_9 - mFrameTopPadding - mViewWidth * mPreviewRatio) / mViewHeight * 2.0f;
                } else if (type.equals(StickerType.Frame)) {
                    yOffset = mFrameTopPadding * 1.0f / mViewHeight * 2.0f;
                }

                float[] offsetArr = null;
                if (mIsGifMode) {
                    offsetArr = stickerSubRes.getGifOffset();
                } else if (mPreviewRatio == Ratio4_3) {
                    offsetArr = stickerSubRes.getS43Offset();
                } else if (mPreviewRatio == Ratio1_1) {
                    offsetArr = stickerSubRes.getGifOffset();
                } else if (mPreviewRatio == Ratio9_16) {
                    offsetArr = stickerSubRes.getS916Offset();
                }
                if (offsetArr != null && offsetArr.length == 2) {
                    if (type.equals(StickerType.Foreground)/* && mPreviewRatio != Ratio9_16*/) {
                        if (offsetArr[0] == 0.0f && offsetArr[1] == 0.0f) {
                            offsetArr = null;
                        } else {
                            yOffset = 0.0f;
                        }
                    } else if (type.equals(StickerType.Frame) && mPreviewRatio == Ratio1_1) {
                        yOffset = 0.0f;
                    }
                }
                if (offsetArr != null && offsetArr.length == 2) {
                    bmpTransX = offsetArr[0];
                    bmpTransY = offsetArr[1];
                }
            } else if (mPreviewRatio < 10.0f && mPreviewRatio > Ratio16_9) {
                float r = Ratio16_9 / mPreviewRatio;
                bmpScale *= r;
                xOffset = (1.0f - r) / 2.0f * 2.0f;
                if (type.equals(StickerType.Foreground)) {
                    //yOffset += yOffset(mViewWidth * (mPreviewRatio - Ratio16_9 * r) / mViewHeight * 2.0f);
                    yOffset += (mViewWidth * (mPreviewRatio - Ratio16_9) / (mViewWidth * mPreviewRatio) * 2.0f) * (2.0f - r);
                }
            }
        }
        if (type.equals(StickerType.Foreground)) {
            calculatePoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, 2, 2, -1, 1, 0, 1, false, stickerSubRes.getXAlignType(0), stickerSubRes.getYAlignType(0), 1, 1, 0, 0, xOffset, yOffset);

        } else if (type.equals(StickerType.Frame)) {
            calculatePoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, 2, 2, -1, 1, 0, 1, false, stickerSubRes.getXAlignType(1), stickerSubRes.getYAlignType(1), 1, 1, 0, 0, xOffset, yOffset);

        } else if (type.equals(StickerType.Full)) {
            calculatePoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, 2, 2, -1, 1, 0, 1, false, stickerSubRes.getXAlignType(1), stickerSubRes.getYAlignType(1), 1, 1, 0, 0, xOffset, yOffset);

        } else if (type.equals(StickerType.WaterMark)) {
            calculatePoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, 1, -1, -1, 0, 0, false);//2.0f / 3
        }
    }

    private float[] calculateShoulderParam(PointF[] features) {
        PointF noseLeft = new PointF(features[Sslandmarks.NoseLeft].x, features[Sslandmarks.NoseLeft].y);
        PointF noseRight = new PointF(features[Sslandmarks.NoseRight].x, features[Sslandmarks.NoseRight].y);
        PointF chin = new PointF(features[Sslandmarks.chin].x, features[Sslandmarks.chin].y);

        PointF chinLeft = new PointF(features[Sslandmarks.chinLeft].x, features[Sslandmarks.chinLeft].y);
        PointF chinRight = new PointF(features[Sslandmarks.chinRight].x, features[Sslandmarks.chinRight].y);

        PointF p = new PointF();
        p.x = (noseLeft.x + noseRight.x) / 2;
        p.y = (noseLeft.y + noseRight.y) / 2;

        float noseChinDist = PointsUtils.getDistance(p, chin);

        float[] ret = new float[3];
        ret[0] = (chinLeft.x + chinRight.x) / 2;
        ret[1] = (noseLeft.y + noseRight.y) / 2 - noseChinDist * 1.7f;
        ret[2] = PointsUtils.getDistance(features[Sslandmarks.rForeHead], features[Sslandmarks.lForeHead]) * 3.8f;
        return ret;
    }

    private PointF rotatePoint(PointF srcPoint, PointF centerPoint, float rotation) {
        PointF originVector = new PointF(srcPoint.x - centerPoint.x, srcPoint.y - centerPoint.y);
        PointF rotateVector = rotateVector(originVector, rotation);
        return new PointF(rotateVector.x + centerPoint.x, rotateVector.y + centerPoint.y);
    }

    private PointF rotateVector(PointF srcVector, float rotation) {
        float cos_v = (float) Math.cos(rotation);
        float sin_v = (float) Math.sin(rotation);

        float rotateX = cos_v * srcVector.x - sin_v * srcVector.y;
        float rotateY = cos_v * srcVector.y + sin_v * srcVector.x;
        return new PointF(rotateX, rotateY);
    }

    private void calculatePoints(String type, float bmpWidth, float bmpHeight, float bmpScale, float bmpTransX, float bmpTransY,
                                 float baseWidth, float centerX, float centerY, float ratioX, float ratioY, boolean canRotate) {
        calculatePoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, baseWidth, 2, centerX, centerY, ratioX, ratioY, canRotate, -1, -1, 1, 1, 0, 0);
    }

    private void calculatePoints(String type, float bmpWidth, float bmpHeight, float bmpScale, float bmpTransX, float bmpTransY,
                                 float baseWidth, float centerX, float centerY, float ratioX, float ratioY, boolean canRotate,
                                 int xAlignType, int yAlignType, float xPadding, float yPadding) {
        calculatePoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, baseWidth, 2, centerX, centerY, ratioX, ratioY, canRotate, xAlignType, yAlignType, 1, 1, xPadding, yPadding);
    }

    private void calculatePoints(String type, float bmpWidth, float bmpHeight, float bmpScale, float bmpTransX, float bmpTransY,
                                 float baseWidth, float baseHeight, float centerX, float centerY, float ratioX, float ratioY, boolean canRotate,
                                 int xAlignType, int yAlignType, float xMax, float yMax, float xPadding, float yPadding) {
        calculatePoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, baseWidth, baseHeight, centerX, centerY, ratioX, ratioY, canRotate, xAlignType, yAlignType, xMax, yMax, xPadding, yPadding, 0, 0);
    }

    /**
     * @param type       类型
     * @param bmpWidth   图片宽
     * @param bmpHeight  图片高
     * @param bmpScale   缩放比例
     * @param bmpTransX  x偏移，左负右正(正方向向右)
     * @param bmpTransY  y偏移，上负下正(正方向向下)
     * @param baseWidth  标准宽度
     * @param baseHeight 标准高度
     * @param centerX    中心点 x
     * @param centerY    中心点 y
     * @param ratioX     图片宽比例，用于辅助计算图片在OpenGL坐标系的x位置
     * @param ratioY     图片高比例，用于辅助计算图片在OpenGL坐标系的y位置
     * @param canRotate  是否跟随人脸旋转
     * @param xAlignType x方向对齐方式，0:左对齐，1:居中，2:右对齐（只对 Foreground 和 Frame 有效）
     * @param yAlignType y方向对齐方式，0:上对齐，1:居中，2:下对齐（只对 Foreground 和 Frame 有效）
     */
    private void calculatePoints(String type, float bmpWidth, float bmpHeight, float bmpScale, float bmpTransX, float bmpTransY,
                                 float baseWidth, float baseHeight, float centerX, float centerY, float ratioX, float ratioY, boolean canRotate,
                                 int xAlignType, int yAlignType, float xMax, float yMax, float xPadding, float yPadding, float xOffset, float yOffset) {
        float ratio = bmpHeight * 1.0f / bmpWidth;
        float realW = baseWidth * bmpScale;
        float realH = realW * ratio / mViewRatio;
        float temp;

        int rotateType = mScreenOrientation;//旋转方向
        if (canRotate) {
            if (rotateType == 1) {//向左旋转90度
                mFaceRoll += Math.PI / 2;
            } else if (rotateType == 2) {//向左(右)旋转180度
                mFaceRoll += Math.PI;
            } else if (rotateType == 3) {//向右旋转90度
                mFaceRoll -= Math.PI / 2;
            }
            mFaceRoll = -mFaceRoll;

            if (rotateType == 1) {
//                if (StickerType.Head.equals(type))
//                    Log.i(TAG, "calculatePoints: "+realW);
                realH = realW * ratio / (1.0f / mViewRatio);
                temp = centerX;
                centerX = centerY;
                centerY = temp;
                ratioX = 1.0f - ratioX;
                bmpTransX = -bmpTransX;

            } else if (rotateType == 2) {
                if (type.equals(StickerType.Shoulder)) {
                    centerY = Math.abs(centerY);
                }
                centerX = -centerX;
                centerY = -centerY;

            } else if (rotateType == 3) {
                if (type.equals(StickerType.Shoulder)) {
                    centerX = Math.abs(centerX);
                }
                realH = realW * ratio / (1.0f / mViewRatio);
                temp = centerX;
                centerX = centerY;
                centerY = temp;
                ratioY = 1.0f - ratioY;
                bmpTransY = -bmpTransY;
            }
        }

        float offsetX = realW * bmpTransX;
        float x1 = centerX - realW * ratioX;
        float x2 = x1 + realW;
        if (xAlignType > -1 && realW != baseWidth) {//Foreground Frame
            if (xAlignType == 0) {//left
                x1 = (xMax - baseWidth);//-1.0f;
                x2 = x1 + realW;
            } else if (xAlignType == 1) {//center
                x1 = (xMax - baseWidth) + (baseWidth - realW) / baseWidth;
                x2 = x1 + realW;
            } else if (xAlignType == 2) {//right
                x1 = xMax - realW;
                x2 = xMax;
            } else if (xAlignType == 3) {//fill
                x2 = x1 + realW;
            } else {//default left
                x1 = (xMax - baseWidth);
                x2 = x1 + realW;
            }
        }

        x1 = x1 + offsetX + xPadding + xOffset;
        x2 = x2 + offsetX + xPadding + xOffset;
        float x3 = x1;
        float x4 = x2;

        float offsetY = realH * bmpTransY;
        float y1 = centerY - realH * ratioY;
        float y3 = y1 + realH;
        if (yAlignType > -1 && realH != baseHeight) {//Foreground Frame
            if (yAlignType == 0) {//top
                y1 = yMax - realH;//1.0f
                y3 = yMax;
            } else if (yAlignType == 1) {//center
                y1 = (yMax - baseHeight) + (baseHeight - realH) / baseHeight;
                y3 = y1 + realH;
            } else if (yAlignType == 2) {//bottom
                y1 = (yMax - baseHeight);
                y3 = y1 + realH;
            } else if (yAlignType == 3) {//fill
                y3 = y1 + realH;
            } else {//default top
                y1 = yMax - realH;
                y3 = yMax;
            }
        }
        y1 = y1 - offsetY - yPadding - yOffset;
        y3 = y3 - offsetY - yPadding - yOffset;
        float y2 = y1;
        float y4 = y3;

        if (canRotate) {
            if (rotateType == 1) {
                temp = x1;
                x1 = -x2;
                x2 = -temp;
                temp = x3;
                x3 = -x4;
                x4 = -temp;

                float[] result = new float[]{x3, y3, x4, y4, x1, y1, x2, y2};
                mPoints = PointsUtils.rotate(result, -centerX, centerY, mFaceRoll, mViewRatio, 1, 1, 1);

            } else if (rotateType == 2) {
                float[] result = new float[]{x3, y3, x4, y4, x1, y1, x2, y2};
                mPoints = PointsUtils.rotate(result, centerX, centerY, mFaceRoll * 2, 1.0f / mViewRatio, 2, 1, 1);

            } else if (rotateType == 3) {
                temp = y1;
                y1 = -y3;
                y3 = -temp;
                temp = y2;
                y2 = -y4;
                y4 = -temp;

                float[] result = new float[]{x3, y3, x4, y4, x1, y1, x2, y2};
                mPoints = PointsUtils.rotate(result, centerX, -centerY, mFaceRoll, mViewRatio, 3, 1, 1);

            } else {
                float[] result = new float[]{x3, y3, x4, y4, x1, y1, x2, y2};
                mPoints = PointsUtils.rotate(result, centerX, centerY, mFaceRoll * 2, 1.0f / mViewRatio, 0, 1, 1);
            }
//            if (type.equals(StickerType.Shoulder)) {
//                Log.i(TAG, "centerX:" + centerX + ", centerY:" + centerY + ", 1:(" + x1 + ", " + y1 + "), 2:(" + x2 + ", " + y2 + "), 3:(" + x3 + ", " + y3 + "), 4:(" + x4 + ", " + y4 + ")");
//            }
        } else {
            mPoints = new float[]{x3, y3, x4, y4, x1, y1, x2, y2};
        }
//        if (type.equals(StickerType.Foreground)) {
//            Log.i(TAG, "calculatePoints: "+x3+"f, "+y3+"f, "+x4+"f, "+y4+"f, "+x1+"f, "+y1+"f, "+x2+"f, "+y2+"f");
//        }
    }

    public float[] getTexturePoints() {
        if (mTexturePoints == null) {
            mTexturePoints = new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
        }
        return mTexturePoints;
    }

    public float[] getSpriteTextureVertex(float width, float height, Rect rect, float offset) {
        float[] textureVertex = new float[8];
        if (width == 0 || height == 0 || rect == null) {
            return textureVertex;
        }
        textureVertex[0] = rect.left / width + rect.right * offset / width;
        textureVertex[1] = rect.top / height + rect.bottom * offset / height;
        textureVertex[2] = (rect.left + rect.right) / width - rect.right * offset / width;
        textureVertex[3] = textureVertex[1];

        textureVertex[4] = textureVertex[0];
        textureVertex[5] = (rect.top + rect.bottom) / height - rect.bottom * offset / height;
        textureVertex[6] = textureVertex[2];
        textureVertex[7] = textureVertex[5];

        if (mTexturePoints != null && mTexturePoints.length == textureVertex.length) {
            for (int i = 0; i < mTexturePoints.length / 2; i++) {
                textureVertex[i * 2] += mTexturePoints[i * 2] * rect.right / width;
                textureVertex[i * 2 + 1] += mTexturePoints[i * 2 + 1] * rect.bottom / height;
            }
        }
        mTexturePoints = null;
//        Log.i(TAG, "calculatePoints: "+textureVertex[0]+", "+textureVertex[1]+", "+textureVertex[2]+", "+textureVertex[3]+", "+textureVertex[4]+", "+textureVertex[5]+", "+textureVertex[6]+", "+textureVertex[7]
//                +", "+textureVertex[8]+", "+textureVertex[9]+", "+textureVertex[10]+", "+textureVertex[11]+", "+textureVertex[12]+", "+textureVertex[13]+", "+textureVertex[14]+", "+textureVertex[15]);
        return textureVertex;
    }

    public int[] getTextureIdsByType(String type) {
        if (mMaterialsTextureIds == null) {
            mMaterialsTextureIds = new int[StickerType.STICKER_TYPE_NUM][mEachTypeOfTextureCount];
        }
        int[] textureIds = mMaterialsTextureIds[StickerType.getIndexByType(type)];
        if (textureIds == null) {
            textureIds = new int[mEachTypeOfTextureCount];
            mMaterialsTextureIds[StickerType.getIndexByType(type)] = textureIds;
        }
        return textureIds;
    }

    public void resetFilterData() {
        mMaterialsTextureIds = new int[StickerType.STICKER_TYPE_NUM][mEachTypeOfTextureCount];
        isResetData = true;
    }

    public ArrayList<Map.Entry<String, StickerSubRes>> getOrderContents2() {
        return mOrderContents2;
    }

    public void setMode(boolean isGifMode) {
        mIsGifMode = isGifMode;
    }

    public void setPreviewRatio(float previewRatio) {
        mPreviewRatio = previewRatio;
    }

    public int getRatioType() {
        if (mPreviewRatio == Ratio9_16) {
            return 1;
        } else if (mPreviewRatio == Ratio1_1) {
            return 2;
        } else if (mPreviewRatio == Ratio4_3) {
            return 3;
        } else if (mPreviewRatio == Ratio16_9) {
            return 4;
        } else if (mPreviewRatio < 10.0f && mPreviewRatio > Ratio16_9) {
            return 5;
        }
        return 0;
    }

    public void setFrameTopPaddingOnRatio1_1(int padding) {
        mFrameTopPaddingOnRatio1_1 = padding;
    }

    public void setFrameTopPadding(int frameTopPadding) {
        mFrameTopPadding = frameTopPadding;
    }

    public boolean setStickerRes(int id, VideoStickerRes videoStickerRes) {
        mActionName = null;
        if (materialID == id && mOrderContents2 != null) {
            return false;
        }
        mSingleFace = false;
        mOrderContents2 = null;
        mShapeTypeId = 0;
        mActionMusic = null;
        mAnimMusicDelayTime = -1;
        mAnimMaxDuration = 0;
        mStickerSplitScreen = null;

        if (videoStickerRes != null && videoStickerRes.mStickerRes != null) {
            mOrderContents2 = videoStickerRes.mStickerRes.mOrderStickerRes;
            mBSWidth = videoStickerRes.mStickerRes.mSWidth;
            mBSHeight = videoStickerRes.mStickerRes.mSHeight;
            mActionName = videoStickerRes.mStickerRes.mAction;
            mAnimMaxDuration = videoStickerRes.mStickerRes.mMaxFrameDurations;
            mStickerSplitScreen = videoStickerRes.mStickerRes.mStickerSplitScreen;
            if (mStickerSplitScreen != null) {
                mStickerSplitScreen.reset();
            }
            if (videoStickerRes.mStickerRes.mIs3DRes) {
                mSingleFace = true;//3D只支持1个人脸
            }

            if (videoStickerRes.m_has_music && videoStickerRes.mStickerRes.mStickerSoundRes != null && videoStickerRes.mStickerRes.mStickerSoundRes.mStickerSounds != null) {
                for (StickerSound stickerSound : videoStickerRes.mStickerRes.mStickerSoundRes.mStickerSounds) {
                    if (stickerSound != null) {
                        if ("se1".equals(stickerSound.mType)) {
                            mAnimMusicDelayTime = (int) stickerSound.mDelayDuration;
                        } else if ("se2".equals(stickerSound.mType)) {
                            mActionMusic = stickerSound.mAction;
                        }
                    }
                }
            }
        }
        materialID = id;
        mResIsChange = true;

        //--商业定制 start--
        if (mOrderContents2 != null && !mOrderContents2.isEmpty()) {
            //initInnisFreeResData(id);
            //initGivenchyResData(id);
            //initAmaniResData(id);
            initYuXiResData(id);
        }
        //initMakeUpRes();
        //--商业定制 end--

        return true;
    }

    public void setShapeTypeId(int shapeTypeId) {
        mShapeTypeId = shapeTypeId;
    }

    public void onResume() {
        if (mRealTimeMakeUpRes != null) {
            mRealTimeMakeUpRes.resetAll();
        }
        if (mStickerSplitScreen != null) {
            mStickerSplitScreen.reset();
        }
    }

    public void clearAll() {
        mMaterialsTextureIds = null;
        mOrderContents2 = null;
        mStickerSplitScreen = null;
        mActionMusic = null;

        //RealTimeMakeUpResMgr.clearAll();

        mCurrentPocoFace = null;

        sInstance = null;
    }

    private boolean mUseMakeUpRes;
    private RealTimeMakeUpRes mRealTimeMakeUpRes;

    public RealTimeMakeUpSubRes getMakeUpSubRes(int frameIndex) {
        if (mUseMakeUpRes && mRealTimeMakeUpRes != null) {
            return mRealTimeMakeUpRes.getSubRes(frameIndex);
        }
        return null;
    }

    public int getMakeUpSubResIndex() {
        if (mUseMakeUpRes && mRealTimeMakeUpRes != null) {
            return mRealTimeMakeUpRes.getSubResIndex();
        }
        return -1;
    }

    private void initMakeUpRes() {
        mUseMakeUpRes = false;
        mRealTimeMakeUpRes = null;
        int resIndex = -1;
//        if (materialID == 2364) {//植村秀实时彩妆 20170704
//            resIndex = 0;
//        } else if (materialID == 2529) {
//            resIndex = 1;
//        } else if (materialID == 2530) {
//            resIndex = 2;
//        }
        if (materialID == 39167) {//YSL  2017.10.20-2017.10.31
            resIndex = 0;
        }
        if (resIndex > -1) {
//            long time = System.currentTimeMillis();
//            if (time > 1499097600000L && time < 1500480000000L) {//201707040000-201707200000  test
//                mUseMakeUpRes = true;
//            }

            mUseMakeUpRes = true;
            if (mUseMakeUpRes) {
                RealTimeMakeUpResMgr.initLocalRes();
                if (RealTimeMakeUpResMgr.mMakeUpResList != null && !RealTimeMakeUpResMgr.mMakeUpResList.isEmpty()) {
                    mRealTimeMakeUpRes = RealTimeMakeUpResMgr.mMakeUpResList.get(resIndex);
                }
                if (mRealTimeMakeUpRes != null) {
                    mRealTimeMakeUpRes.resetAll();
                }
            }
        }
    }

    private void initInnisFreeResData(int resId) {//innisfree商业定制
        if (resId == 2534 || resId == 2535 || resId == 2536) {
            for (Map.Entry<String, StickerSubRes> entry : mOrderContents2) {
                if (entry != null && StickerType.Foreground.equals(entry.getKey())) {
                    entry.getValue().setResShowType(2);
                    break;
                }
            }
        }
    }

    private void initGivenchyResData(int resId) {//纪梵希商业定制
        if (resId == 38559) {
            for (Map.Entry<String, StickerSubRes> entry : mOrderContents2) {
                if (entry != null) {
                    if (StickerType.Foreground.equals(entry.getKey())) {
                        entry.getValue().setResShowType(3);
                        entry.getValue().setAnimTriggerFrameIndex(30);//开始触发美颜效果
                        entry.getValue().setNeedFace(true);
                        entry.getValue().setOffsetY(2.35f);
                    }
                    if (StickerType.Frame.equals(entry.getKey())) {
                        entry.getValue().setNeedFace(true);
                    }
                }
            }
        }
    }

    private void initAmaniResData(int resId) {//阿玛尼商业定制
        if (resId == 39165) {
            mSingleFace = true;
            for (Map.Entry<String, StickerSubRes> entry : mOrderContents2) {
                if (entry != null) {
                    if (StickerType.Eye.equals(entry.getKey())) {
                        entry.getValue().setMaxJumpFrame(3);
                        entry.getValue().setResShowType(5);
                    }
                }
            }
        }
    }

    private void initYuXiResData(int resId) {//羽西鲜活精华商业定制
        if (resId == 41744 || resId == 41743) {//20180104
            for (Map.Entry<String, StickerSubRes> entry : mOrderContents2) {
                if (entry != null) {
                    if (StickerType.Foreground.equals(entry.getKey())) {
                        entry.getValue().setResShowType(3);
                        entry.getValue().setAnimTriggerFrameIndex(33);//开始触发美白效果
                        entry.getValue().setNeedFace(true);
                    }
                    entry.getValue().setNeedResetWhenLostFace(true);
                }
            }
        }
    }

    //=================================================================
    private float[] mFacePoints;
    private float[] mFaceTexturePoints;

    // 贴纸的中心点
    public float mCenterX;
    public float mCenterY;

    /**
     * 获取Texture顶点
     *
     * @param width
     * @param height
     * @param rect
     * @param offset
     * @return
     */
    public float[] get3DTextureVertex(float width, float height, Rect rect, float offset) {
        float[] textureVertex = new float[8];
        if (width == 0 || height == 0 || rect == null) {
            return textureVertex;
        }
        textureVertex[0] = rect.left / width + rect.right * offset / width;
        textureVertex[1] = rect.top / height + rect.bottom * offset / height;
        textureVertex[2] = (rect.left + rect.right) / width - rect.right * offset / width;
        textureVertex[3] = textureVertex[1];

        textureVertex[4] = textureVertex[0];
        textureVertex[5] = (rect.top + rect.bottom) / height - rect.bottom * offset / height;
        textureVertex[6] = textureVertex[2];
        textureVertex[7] = textureVertex[5];

        if (mFaceTexturePoints != null && mFaceTexturePoints.length == textureVertex.length) {
            for (int i = 0; i < mFaceTexturePoints.length / 2; i++) {
                textureVertex[i * 2] += mFaceTexturePoints[i * 2] * rect.right / width;
                textureVertex[i * 2 + 1] += mFaceTexturePoints[i * 2 + 1] * rect.bottom / height;
            }
        }
        mFaceTexturePoints = null;
        return textureVertex;
    }

    /**
     * 获取Vertex顶点
     *
     * @param stickerSubRes
     * @param stickerSpriteFrame
     * @param type
     * @return
     */
    public float[] getSticker3DPoints(StickerSubRes stickerSubRes, StickerSpriteFrame stickerSpriteFrame, String type, int faceSize, PocoFace face) {
        float[] vertexPoints = null;
        mFacePoints = null;
        mFaceTexturePoints = null;
        if (type.equals(StickerType.WaterMark) || !mIsRecordDraw || faceSize > 1) {
            mCurrentPocoFace = face;
            calculateCenterPoints(stickerSubRes, stickerSpriteFrame, type);
            vertexPoints = mFacePoints;
            mCurrentPocoFace = null;
        }
        if (vertexPoints == null) {
            vertexPoints = new float[8];
        }
        return vertexPoints;
    }

    private void calculateCenterPoints(StickerSubRes stickerSubRes,
                                       StickerSpriteFrame stickerSpriteFrame, String type) {
        if (stickerSubRes == null || stickerSpriteFrame == null) {
            return;
        }
        int bmpWidth = stickerSpriteFrame.getFrameWidth();
        int bmpHeight = stickerSpriteFrame.getFrameHeight();
        float bmpScale = stickerSubRes.getScale();
        float bmpTransX = stickerSubRes.getOffsetX();
        float bmpTransY = stickerSubRes.getOffsetY();

        mFacePitch = 0.0f;
        mFaceYaw = 0.0f;
        mFaceRoll = 0.0f;
        if (mCurrentPocoFace != null) {
            float baseWidth = 0;
            float centerX = 0;
            float centerY = 0;

            // 计算姿态角
            mFacePitch = mCurrentPocoFace.pitch; // x轴姿态角
            mFaceYaw = mCurrentPocoFace.yaw;  // y轴姿态角
            mFaceRoll = mCurrentPocoFace.roll; // z轴姿态角

            int rotateType = mScreenOrientation;//旋转方向
            float dxRatio = 1.0f;
            float dyRatio = 1.0f;
            if (rotateType == 1 || rotateType == 3) {
                dxRatio = 1.0f / mViewRatio;
            } else {
                dyRatio = mViewRatio;
            }

//            // 调整角度
//            if (mFaceYaw > Math.PI / 6 || mFaceYaw < -Math.PI / 6) {
//                mFaceYaw = (float) (mFaceYaw / Math.abs(mFaceYaw) * Math.PI / 6);
//            }
            // 根据Y轴的偏转角度，反推贴纸处于正脸的宽度
            // Y轴姿态角与正脸平面夹角不大于30度，否则推算出来的结果偏差过大
            float offset = (float) Math.abs(Math.cos(mFaceYaw));
            if (offset < 0.50f) {
                offset = 0.50f;
            }
            if (type.equals(StickerType.Head)) { // 头顶
//                baseWidth = PointsUtils.getDistance(mCurrentPocoFace.mGLPoints[8], mCurrentPocoFace.mGLPoints[24], dxRatio, dyRatio) * 0.78f;
                baseWidth = PointsUtils.getDistance(mCurrentPocoFace.mGLPoints[Sslandmarks.rForeHead], mCurrentPocoFace.mGLPoints[Sslandmarks.lForeHead], dxRatio, dyRatio) / offset;
                PointF cp = PointsUtils.getCenterPoint(mCurrentPocoFace.mGLPoints[Sslandmarks.rForeHead], mCurrentPocoFace.mGLPoints[Sslandmarks.lForeHead]);
                centerX = cp.x;
                centerY = cp.y;
                calculate3DPoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, baseWidth, centerX, centerY, 0.5f, 0.5f, true);

            } else if (type.equals(StickerType.Ear)) { // 耳朵
                // 侧脸的时候，由于关键点的误差，这里需要单独做限制处理，因为耳垂被挡住了，位置偏差过大，原来的姿态角会导致偏差过大
                if (mFaceYaw > Math.PI / 6 || mFaceYaw < -Math.PI / 6) {
                    mFaceYaw = (float) (mFaceYaw / Math.abs(mFaceYaw) * Math.PI / 6);
                }
                offset = (float) Math.abs(Math.cos(mFaceYaw));
                if (offset < 0.58f) {
                    offset = 0.58f;
                }
                baseWidth = PointsUtils.getDistance(mCurrentPocoFace.mGLPoints[Sslandmarks.EarlobeRight], mCurrentPocoFace.mGLPoints[Sslandmarks.EarlobeLeft], dxRatio, dyRatio) / offset;
                PointF cp = PointsUtils.getCenterPoint(mCurrentPocoFace.mGLPoints[Sslandmarks.EarlobeRight], mCurrentPocoFace.mGLPoints[Sslandmarks.EarlobeLeft]);
                // 备注：脸颊的中心点与耳垂中心位置平均，消除SDK所带来的偏差，导致透视矩阵换算完，耳垂位置对不上，因为侧脸时，平面上的人脸中心并不在关键点中心。
                PointF cp1 = PointsUtils.getCenterPoint(mCurrentPocoFace.mGLPoints[Sslandmarks.lCheekCenter], mCurrentPocoFace.mGLPoints[Sslandmarks.rCheekCenter]);
                centerX = (cp.x + cp1.x) / 2.0f;
                centerY = (cp.y + cp1.y) / 2.0f;
                calculate3DPoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, baseWidth, centerX, centerY, 0.5f, 0.5f, true);
                mFacePitch = 0.0f; // 耳朵抬头和低头均不用X轴的姿态角，耳环是在抬头和低头时都是竖直向下的
            } else if (type.equals(StickerType.Eye)) { // 眼睛
                baseWidth = PointsUtils.getDistance(mCurrentPocoFace.mGLPoints[Sslandmarks.rEyeOuter], mCurrentPocoFace.mGLPoints[Sslandmarks.lEyeOuter], dxRatio, dyRatio) / offset;
                // 中心点位置的调整，x轴与y轴的位置需要采取不同的计算方式，需要消除屏幕旋转的情况
                PointF cp = PointsUtils.getCenterPoint(mCurrentPocoFace.mGLPoints[Sslandmarks.rEyeOuter], mCurrentPocoFace.mGLPoints[Sslandmarks.lEyeOuter]);
                if (mScreenOrientation % 2 == 0) {
                    centerX = mCurrentPocoFace.mGLPoints[Sslandmarks.lrEyeCenter].x;
                    centerY = cp.y;
                } else {
                    centerX = cp.x;
                    centerY = mCurrentPocoFace.mGLPoints[Sslandmarks.lrEyeCenter].y;
                }
                calculate3DPoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, baseWidth, centerX, centerY, 0.5f, 0.5f, true);

            } else if (type.equals(StickerType.Nose)) { // 鼻子
                baseWidth = PointsUtils.getDistance(mCurrentPocoFace.mGLPoints[Sslandmarks.NoseRight], mCurrentPocoFace.mGLPoints[Sslandmarks.NoseLeft], dxRatio, dyRatio) / offset;
                // 鼻子中心需要调整，需要消除屏幕旋转的情况
                if (mScreenOrientation % 2 == 0) {
                    centerX = mCurrentPocoFace.mGLPoints[Sslandmarks.NoseTop].x;
                    centerY = (mCurrentPocoFace.mGLPoints[Sslandmarks.NoseTop].y + mCurrentPocoFace.mGLPoints[Sslandmarks.NoseBottom].y) / 2.0f;
                } else {
                    centerX = (mCurrentPocoFace.mGLPoints[Sslandmarks.NoseTop].x + mCurrentPocoFace.mGLPoints[Sslandmarks.NoseBottom].x) / 2.0f;
                    centerY = mCurrentPocoFace.mGLPoints[Sslandmarks.NoseTop].y;
                }
                calculate3DPoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, baseWidth, centerX, centerY, 0.5f, 0.5f, true);

            } else if (type.equals(StickerType.Mouth)) { // 嘴巴
                baseWidth = PointsUtils.getDistance(mCurrentPocoFace.mGLPoints[Sslandmarks.rMouthCorner], mCurrentPocoFace.mGLPoints[Sslandmarks.lMouthCorner], dxRatio, dyRatio) / offset;
                PointF cp = PointsUtils.getCenterPoint(mCurrentPocoFace.mGLPoints[Sslandmarks.rMouthCorner], mCurrentPocoFace.mGLPoints[Sslandmarks.lMouthCorner]);
                centerX = cp.x;
                centerY = cp.y;
                calculate3DPoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, baseWidth, centerX, centerY, 0.5f, 0.5f, true);

            } else if (type.equals(StickerType.Chin)) { // 下巴
                baseWidth = PointsUtils.getDistance(mCurrentPocoFace.mGLPoints[Sslandmarks.chinNearRight], mCurrentPocoFace.mGLPoints[Sslandmarks.chinNearLeft], dxRatio, dyRatio) / offset * 2.8f;
                centerX = mCurrentPocoFace.mGLPoints[Sslandmarks.chin].x;
                centerY = mCurrentPocoFace.mGLPoints[Sslandmarks.chin].y;
                calculate3DPoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, baseWidth, centerX, centerY, 0.5f, 0.5f, true);
                mFacePitch = 0.0f; // 抬头低头的姿态角不要，会拉伸素材
            } else if (type.equals(StickerType.Shoulder)) { // 肩膀
                baseWidth = PointsUtils.getDistance(mCurrentPocoFace.mGLPoints[8], mCurrentPocoFace.mGLPoints[24], dxRatio, dyRatio) * 2.8f;
                // 脸颊与颈部的交接点位置(下颌骨隅的关键点)在歪头的情况，偏差最小，最容易计算。
                // 然后就是肩膀是下颌骨隅的位
                PointF cp = PointsUtils.getCenterPoint(mCurrentPocoFace.mGLPoints[8], mCurrentPocoFace.mGLPoints[24]);
                // 歪头的角度跟屏幕的角度不一致，导致位置不对（这个影响似乎无法消除）
                // 归一化后，需要消除长宽比对计算肩膀中心点的影响
                if (mScreenOrientation == 0) {
                    float temp = PointsUtils.getDistance(mCurrentPocoFace.mGLPoints[8], mCurrentPocoFace.mGLPoints[24], dxRatio, dyRatio);
                    centerX = cp.x - (float) (temp * Math.sin(-mFaceRoll + Math.PI / 2)) * 0.8f;
                    float temp2 = PointsUtils.getDistance(mCurrentPocoFace.mGLPoints[8], mCurrentPocoFace.mGLPoints[24], dxRatio, dyRatio) / mViewRatio;
                    centerY = cp.y - (float) (temp2 * Math.cos(-mFaceRoll + Math.PI / 2)) * 0.8f;

                } else if (mScreenOrientation == 1) {
                    float temp = PointsUtils.getDistance(mCurrentPocoFace.mGLPoints[8], mCurrentPocoFace.mGLPoints[24], dxRatio, dyRatio) * mViewRatio;
                    centerX = cp.x - (float) (temp * Math.cos(-mFaceRoll)) * 0.8f;
                    float temp2 = PointsUtils.getDistance(mCurrentPocoFace.mGLPoints[8], mCurrentPocoFace.mGLPoints[24], dxRatio, dyRatio);
                    centerY = cp.y + (float) (temp2 * Math.sin(-mFaceRoll)) * 0.8f;

                } else if (mScreenOrientation == 2) {
                    float temp = PointsUtils.getDistance(mCurrentPocoFace.mGLPoints[8], mCurrentPocoFace.mGLPoints[24], dxRatio, dyRatio);
                    centerX = cp.x + (float) (temp * Math.sin(-mFaceRoll - (float) Math.PI / 2)) * 0.8f;
                    float temp2 = PointsUtils.getDistance(mCurrentPocoFace.mGLPoints[8], mCurrentPocoFace.mGLPoints[24], dxRatio, dyRatio) / mViewRatio;
                    centerY = cp.y + (float) (temp2 * Math.cos(-mFaceRoll - (float) Math.PI / 2)) * 0.8f;

                } else {
                    float temp = PointsUtils.getDistance(mCurrentPocoFace.mGLPoints[8], mCurrentPocoFace.mGLPoints[24], dxRatio, dyRatio) * mViewRatio;
                    centerX = cp.x + (float) (temp * Math.cos(-mFaceRoll + Math.PI)) * 0.8f;
                    float temp2 = PointsUtils.getDistance(mCurrentPocoFace.mGLPoints[8], mCurrentPocoFace.mGLPoints[24], dxRatio, dyRatio);
                    centerY = cp.y - (float) (temp2 * Math.sin(-mFaceRoll + Math.PI)) * 0.8f;
                }

                calculate3DPoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, baseWidth, centerX, centerY, 0.5f, 0.5f, true);
                mFacePitch = 0.0f; // 禁用抬头低头的姿态角，角度不一致
                mFaceYaw = 0.0f; // 禁用肩膀的Y轴旋转
            }
            if (mFacePoints != null) {
                return;
            }
        }

        float xOffset = 0.0f;
        float yOffset = 0.0f;
        if (mPreviewRatio > 0.0f && (type.equals(StickerType.Foreground) || type.equals(StickerType.Frame))) {
            if (mPreviewRatio < Ratio16_9) {
                if (type.equals(StickerType.Foreground)) {
                    yOffset = -(mViewWidth * Ratio16_9 - mFrameTopPadding - mViewWidth * mPreviewRatio) / mViewHeight * 2.0f;
                } else if (type.equals(StickerType.Frame)) {
                    yOffset = mFrameTopPadding * 1.0f / mViewHeight * 2.0f;
                }

                float[] offsetArr = null;
                if (mIsGifMode) {
                    offsetArr = stickerSubRes.getGifOffset();
                } else if (mPreviewRatio == Ratio4_3) {
                    offsetArr = stickerSubRes.getS43Offset();
                } else if (mPreviewRatio == Ratio1_1) {
                    offsetArr = stickerSubRes.getGifOffset();
                } else if (mPreviewRatio == Ratio9_16) {
                    offsetArr = stickerSubRes.getS916Offset();
                }
                if (offsetArr != null && offsetArr.length == 2) {
                    if (type.equals(StickerType.Foreground)/* && mPreviewRatio != Ratio9_16*/) {
                        if (offsetArr[0] == 0.0f && offsetArr[1] == 0.0f) {
                            offsetArr = null;
                        } else {
                            yOffset = 0.0f;
                        }
                    } else if (type.equals(StickerType.Frame) && mPreviewRatio == Ratio1_1) {
                        yOffset = 0.0f;
                    }
                }
                if (offsetArr != null && offsetArr.length == 2) {
                    bmpTransX = offsetArr[0];
                    bmpTransY = offsetArr[1];
                }
            } else if (mPreviewRatio < 10.0f && mPreviewRatio > Ratio16_9) {
                float r = Ratio16_9 / mPreviewRatio;
                bmpScale *= r;
                xOffset = (1.0f - r) / 2.0f * 2.0f;
                if (type.equals(StickerType.Foreground)) {
                    //yOffset += (mViewWidth * (mPreviewRatio - Ratio16_9 * r) / mViewHeight * 2.0f);
                    //yOffset += (mViewWidth * (mPreviewRatio - Ratio16_9) / (mViewWidth * mPreviewRatio) * 2.0f) * r;
                    yOffset += (mViewWidth * (mPreviewRatio - Ratio16_9) / (mViewWidth * mPreviewRatio) * 2.0f) * (2.0f - r);// y + y * (1.0 - r)
                }
            }
        }
        if (type.equals(StickerType.Foreground)) {
            calculate3DPoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, 2, 2, -1, 1, 0, 1, false, stickerSubRes.getXAlignType(0), stickerSubRes.getYAlignType(0), 1, 1, 0, 0, xOffset, yOffset);

        } else if (type.equals(StickerType.Frame)) {
            calculate3DPoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, 2, 2, -1, 1, 0, 1, false, stickerSubRes.getXAlignType(1), stickerSubRes.getYAlignType(1), 1, 1, 0, 0, xOffset, yOffset);

        } else if (type.equals(StickerType.Full)) {
            if (stickerSubRes.getGX() == null) {//test 商业
                stickerSubRes.setGX("center");
            }
            if (stickerSubRes.getGY() == null) {
                stickerSubRes.setGY("center");
            }
            calculate3DPoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, 2, 2, -1, 1, 0, 1, false, stickerSubRes.getXAlignType(1), stickerSubRes.getYAlignType(1), 1, 1, 0, 0, xOffset, yOffset);

        } else if (type.equals(StickerType.WaterMark)) {
            calculate3DPoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, 1, -1, -1, 0, 0, false);
        }
    }

    private void calculate3DPoints(String type, float bmpWidth, float bmpHeight, float bmpScale,
                                   float bmpTransX, float bmpTransY, float baseWidth, float centerX,
                                   float centerY, float ratioX, float ratioY, boolean canRotate) {
        calculate3DPoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, baseWidth, 2, centerX, centerY, ratioX, ratioY, canRotate, -1, -1, 1, 1, 0, 0);
    }

    private void calculate3DPoints(String type, float bmpWidth, float bmpHeight, float bmpScale,
                                   float bmpTransX, float bmpTransY, float baseWidth, float baseHeight,
                                   float centerX, float centerY, float ratioX, float ratioY,
                                   boolean canRotate, int xAlignType, int yAlignType,
                                   float xMax, float yMax, float xPadding, float yPadding) {
        calculate3DPoints(type, bmpWidth, bmpHeight, bmpScale, bmpTransX, bmpTransY, baseWidth, baseHeight,
                centerX, centerY, ratioX, ratioY, canRotate, xAlignType, yAlignType, xMax, yMax, xPadding, yPadding, 0.0f, 0.0f);
    }

    /**
     * @param type       类型
     * @param bmpWidth   图片宽
     * @param bmpHeight  图片高
     * @param bmpScale   缩放比例
     * @param bmpTransX  x偏移，左负右正(正方向向右)
     * @param bmpTransY  y偏移，上负下正(正方向向下)
     * @param baseWidth  标准宽度
     * @param baseHeight 标准高度
     * @param centerX    中心点 x
     * @param centerY    中心点 y
     * @param ratioX     图片宽比例，用于辅助计算图片在OpenGL坐标系的x位置
     * @param ratioY     图片高比例，用于辅助计算图片在OpenGL坐标系的y位置
     * @param canRotate  是否跟随人脸旋转
     * @param xAlignType x方向对齐方式，0:左对齐，1:居中，2:右对齐（只对 Foreground 和 Frame 有效）
     * @param yAlignType y方向对齐方式，0:上对齐，1:居中，2:下对齐（只对 Foreground 和 Frame 有效）
     */
    private void calculate3DPoints(String type, float bmpWidth, float bmpHeight, float bmpScale,
                                   float bmpTransX, float bmpTransY, float baseWidth, float baseHeight,
                                   float centerX, float centerY, float ratioX, float ratioY,
                                   boolean canRotate, int xAlignType, int yAlignType,
                                   float xMax, float yMax, float xPadding, float yPadding, float xOffset, float yOffset) {
        float ratio = bmpHeight * 1.0f / bmpWidth;
        if (type.equals(StickerType.Full) && ratio < 1.0f) {
            bmpScale = baseHeight * mViewRatio / ratio / baseWidth;
        }
        float realW = baseWidth * bmpScale;
        float realH = realW * ratio / mViewRatio;

        // 根据屏幕旋转方向调整宽度和高度
        int rotateType = mScreenOrientation; // 旋转方向
        // 根据旋转角度重新计算高度，90度或者270度时，长宽比需要倒过来)(Foreground 和 Frame除外)
        if (rotateType == 1 || rotateType == 3) {
            if (!type.equals(StickerType.Foreground) && !type.equals(StickerType.Frame) && !type.equals(StickerType.Full)) {
                realH = realW * ratio / (1.0f / mViewRatio);
            }
        }

        // 计算基于屏幕中心的左右位置
        float offsetX = realW * bmpTransX;
        float left = -realW * ratioX;
        float right = left + realW;
        // 前景和帧的图像需要另外计算偏移
        if (type.equals(StickerType.Foreground) || type.equals(StickerType.Frame) || type.equals(StickerType.Full)) {
            left += centerX;
            right += centerX;

            centerX = 0;
        }
        if (xAlignType > -1 && realW != baseWidth) {//Foreground Frame
            if (xAlignType == 0) {// 左边
                left = (xMax - baseWidth);//-1.0f;
                right = left + realW;
            } else if (xAlignType == 1) {// 中心
                left = (xMax - baseWidth) + (baseWidth - realW) / baseWidth;
                right = left + realW;
            } else if (xAlignType == 2) {// 右边
                left = xMax - realW;
                right = xMax;
            } else if (xAlignType == 3) {// 填充
                right = left + realW;
            } else { //默认左边
                left = (xMax - baseWidth);
                right = left + realW;
            }
        }
        left = left + offsetX + xPadding + xOffset;
        right = right + offsetX + xPadding + xOffset;

        // 计算基于屏幕中心的上下位置
        float offsetY = realH * bmpTransY;
        float top = -realH * ratioY;
        float bottom = top + realH;
        // 前景和帧需要另外计算偏移
        if (type.equals(StickerType.Foreground) || type.equals(StickerType.Frame) || type.equals(StickerType.Full)) {
            top += centerY;
            bottom += centerY;

            centerY = 0;
        }
        if (yAlignType > -1 && realH != baseHeight) {//Foreground Frame
            if (yAlignType == 0) {// 顶部
                top = yMax - realH;//1.0f
                bottom = yMax;
            } else if (yAlignType == 1) {// 中间
                top = (yMax - baseHeight) + (baseHeight - realH) / baseHeight;
                bottom = top + realH;
            } else if (yAlignType == 2) {// 底部
                top = (yMax - baseHeight);
                bottom = top + realH;
            } else if (yAlignType == 3) {// 填充
                bottom = top + realH;
            } else {// 默认是顶部
                top = yMax - realH;
                bottom = yMax;
            }
        }
        top = top - offsetY - yPadding - yOffset;
        bottom = bottom - offsetY - yPadding - yOffset;

        // 计算中心点
        mCenterX = centerX;
        mCenterY = centerY;

        mFacePoints = new float[]{
                left, bottom,
                right, bottom,
                left, top,
                right, top
        };

        // 在屏幕中心算好绕Z轴旋转后的顶点，这么做主要是为了解决手机旋转时贴纸变形的问题
        if (canRotate) {
            float[] reference = new float[]{
                    left, bottom,
                    right, bottom,
                    left, top,
                    right, top
            };
            if (rotateType == 0) {
                mFacePoints = PointsUtils.rotate(reference, 0, 0,
                        -mFaceRoll + (float) Math.PI / 2, 1.0f / mViewRatio, 0, 1, 1);
            } else if (rotateType == 1) {
                mFacePoints = PointsUtils.rotate(reference, 0, 0,
                        -mFaceRoll, mViewRatio, 1, 1, 1);
            } else if (rotateType == 2) {
                mFacePoints = PointsUtils.rotate(reference, 0, 0,
                        -mFaceRoll - (float) Math.PI / 2, 1.0f / mViewRatio, 2, 1, 1);
            } else if (rotateType == 3) {
                mFacePoints = PointsUtils.rotate(reference, 0, 0,
                        -mFaceRoll + (float) Math.PI, mViewRatio, 3, 1, 1);
            }
        }
    }
}
