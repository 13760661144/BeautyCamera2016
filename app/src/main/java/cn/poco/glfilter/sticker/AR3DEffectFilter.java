package cn.poco.glfilter.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.text.TextUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.poco.dynamicSticker.v2.StickerSubRes;
import cn.poco.dynamicSticker.v2.StickerSubResARGroupItemImg;
import cn.poco.dynamicSticker.v2.StickerSubResDesc;
import cn.poco.gl3DFilter.AREffectDataFilter;
import cn.poco.gl3DFilter.Poco3DGLNative;
import cn.poco.glfilter.base.AbstractFilter;
import cn.poco.glfilter.base.GLFramebuffer;
import cn.poco.glfilter.base.GlUtil;
import cn.poco.image.PocoFace;
import cn.poco.system.SysConfig;
import cn.poco.utils.FileUtil;

/**
 * Created by liujx on 2017/9/30.
 */
public class AR3DEffectFilter extends AbstractFilter {

    private long mRenderId;
    private int mBufferId;
    private int mFocalLength = 2747;

    private boolean mBindOwnBuffer;
    private int mDepthFrameBufferId = -1;
    private GLFramebuffer mDepthFrameBuffer;

    private int[] mAttachmentParams;
    private int[] mDepthRenderBuffer;

    private int mResId = -1;
    private boolean mCanDraw;
    private boolean mResIsChange;
    private int mAnimTriggerType;//动画触发标识

    private int mModelType;
    private AREffectDataFilter.PORSARModelDescriptor mModelDesc;
    private ArrayList<AREffectDataFilter.PORSARAnimationElementProgressDescriptor> mTempAnimElementProgressDesc;
    private ArrayList<AREffectDataFilter.PORSARAnimationElementProgressDescriptor> mAnimElementProgressDesc;
    private ArrayList<StickerSubResARGroupItemImg> mARGroupItemImgs;

    private int mGroupSize;
    private int mInitGroupCount;
    private int mCurrentGroupIndex;
    private boolean mCanChangeGroupRes;

    private HashMap<String, Integer> mTextureIdsMap;
    private HashMap<String, Integer> mTextureGroupIdsMap;
    private String[] mTextureNames;
    private int[] mTextureIds;

    private PocoFace mPocoFace;
    private int mDealWidth, mDealHeight;

    public AR3DEffectFilter(Context context) {
        super(context);
    }

    @Override
    protected int createProgram(Context context) {
        mRenderId = Poco3DGLNative.createRender3DId();
        Poco3DGLNative.Render3DInit(mRenderId);

        //配置模型文件
        float[] color = new float[4];
        Poco3DGLNative.configureObjDefaultColor(mRenderId, color);

        return -1;
    }

    @Override
    protected void checkProgram() {

    }

    @Override
    public void setViewSize(int width, int height) {
        super.setViewSize(width, height);

        if (width > 0 && height > 0) {
            mDealWidth = width;
            mDealHeight = height;
            if (height * 1.0f / width > 16.0f / 9) {
                mDealHeight = Math.round(width * 16.0f / 9);
            }
        }
        //光源位置
        //39 100 11 100 60
        // r = (p / 100.0f - 0.5f) * 20;
        set3DParams(0.3f, 0.2f, -4.5f, 0.88f, 2747);
    }

    public void set3DParams(float x, float y, float z, float alpha, int focalLength) {
        //Log.i("vvv", "x:" + x + ", y:" + y + ", z:" + z + ", alpha:" + alpha + ", focalLength:" + focalLength);
        float[] position = new float[3];
        position[0] = x;
        position[1] = y;
        position[2] = z;
        if (mRenderId != 0) {
            Poco3DGLNative.configureEnvironLightPosition(mRenderId, position, alpha);
        }
        mFocalLength = focalLength;
    }

    public void set3DParamsTest(float[] params) {
        if (params == null || params.length != 5 || !mCanDraw) return;
        float x = (params[0] - 0.5f) * 20;
        float y = (params[1] - 0.5f) * 20;
        float z = (params[2] - 0.5f) * 20;
        float alpha = params[3];

        int focalLength = (int) (params[4] * 10000);
        if (focalLength < 100) {
            focalLength = 100;
        }

        set3DParams(x, y, z, alpha, focalLength);
    }

    public void setDepthFrameBuffer(GLFramebuffer frameBuffer) {
        if (mDepthFrameBuffer != null) {
            mDepthFrameBuffer.destroy();
            mDepthFrameBuffer = null;
        }
        if (frameBuffer == null) {
            mDepthFrameBuffer = new GLFramebuffer(1, mWidth, mHeight, false, true, false);
        } else {
            mDepthFrameBuffer = frameBuffer;
        }
    }

    public void setAR3DModelData(Context context, int resId, StickerSubRes res) {
        if (res == null || res.get3DModelData() == null || res.get3DModelData().isEmpty()
                || ((res.getImgNames() == null || res.getImgNames().isEmpty())
                && (res.getStickerSubResARGroupItemImgs() == null || res.getStickerSubResARGroupItemImgs().isEmpty()))) {
            return;
        }

        mModelType = res.getSubType();
        if (res.mIsActionRes) {
            mAnimTriggerType = res.mHasAction ? (mAnimTriggerType == 3 ? 3 : 2) : 1;
        } else {
            if (mModelType == AREffectDataFilter.kPORSARModelMutualType.kPORSARModelMutualTypeAnimation) {
                if (resId != mResId || mAnimTriggerType < 4) {
                    mAnimTriggerType = 4;
                }
            } else {
                mAnimTriggerType = 0;
            }
        }

        if (resId == mResId) {
            return;
        }
        mCanDraw = false;
        mResId = resId;
        //Log.i("vvv", "setAR3DModelData: " + mResId+", type:"+mModelType);

        mModelDesc = new AREffectDataFilter.PORSARModelDescriptor();
        mModelDesc.mName = "" + mResId;
        mModelDesc.mModelRenderType = mModelType;

        for (String model : res.get3DModelData()) {
            if (TextUtils.isEmpty(model)) continue;

            if (model.endsWith("aligned_full_head.obj")) {
                mModelDesc.standardRenderObjPath = model;

            } else if (model.endsWith(".fbx") || model.endsWith(".obj")) {
                mModelDesc.arRenderObjPath = model;
            }
        }
        if (mModelDesc.arRenderObjPath == null) {
            mResId = -1;
            return;
        }

        if (mModelDesc.standardRenderObjPath == null && mModelDesc.arRenderObjPath != null) {
            //从assets拷贝到sd卡
            boolean result = false;
            int pos = mModelDesc.arRenderObjPath.lastIndexOf("/");
            if (context != null && pos != -1) {
                //String path = mModelDesc.arRenderObjPath.substring(0, pos) + "/aligned_full_head.obj";
                String path = SysConfig.GetAppPath() + "/appdata/resource/video_face/00000model/aligned_full_head.obj";

                result = FileUtil.assets2SD(context, "stickers/aligned_full_head.obj", path, false);
                if (result) {
                    mModelDesc.standardRenderObjPath = path;
                }
            }
            if (!result) {
                mResId = -1;
                return;
            }
        }
        mResId = resId;

        AREffectDataFilter.PORSARAnimationElementDescriptor animElementDesc = null;
        mModelDesc.mAminationList = new ArrayList<>();

        ArrayList<StickerSubResDesc> resDescs = res.getStickerSubResDescs();
        if (mModelDesc.mAminationList != null && resDescs != null) {
            for (StickerSubResDesc desc : resDescs) {
                String type = desc.getType();
                if (TextUtils.isEmpty(type)) continue;

                animElementDesc = new AREffectDataFilter.PORSARAnimationElementDescriptor();
                animElementDesc.mName = type;
                animElementDesc.startFrameIndex = desc.getA();
                animElementDesc.endFrameIndex = desc.getB();
                mModelDesc.mAminationList.add(animElementDesc);
            }
        }

        if (mModelType == AREffectDataFilter.kPORSARModelMutualType.kPORSARModelMutualTypeAnimation) {

        } else if (mModelType == AREffectDataFilter.kPORSARModelMutualType.kPORSARModelMutualTypeDynamicFace) {
            //-------表情款------
            if (mTempAnimElementProgressDesc == null) {
                mTempAnimElementProgressDesc = new ArrayList<>();
            } else {
                mTempAnimElementProgressDesc.clear();
            }
            AREffectDataFilter.PORSARAnimationElementProgressDescriptor animElementProgressDesc = null;

            if (mModelDesc.mAminationList != null) {
                for (AREffectDataFilter.PORSARAnimationElementDescriptor desc : mModelDesc.mAminationList) {
                    animElementProgressDesc = new AREffectDataFilter.PORSARAnimationElementProgressDescriptor();
                    animElementProgressDesc.mName = desc.mName;//0
                    animElementProgressDesc.progress = 0.0f;
                    mTempAnimElementProgressDesc.add(animElementProgressDesc);
                }
            }
            //-------表情款------
        }

        //清空task
        clearTask();
        releaseTextureId();

        ArrayList<String> imgs = res.getImgNames();
        if (imgs != null && !imgs.isEmpty()) {
            if (mTextureIdsMap == null) {
                mTextureIdsMap = new HashMap<>();
            }
            initTask(0, 0, imgs, false);
        }

        mARGroupItemImgs = res.getStickerSubResARGroupItemImgs();
        if (mARGroupItemImgs != null) {
            if (mTextureGroupIdsMap == null) {
                mTextureGroupIdsMap = new HashMap<>();
            }
            mGroupSize = mARGroupItemImgs.size();
            mInitGroupCount = 0;
            addGroupItemTask(mARGroupItemImgs, 0, false);
        } else {
            mGroupSize = 0;
            mInitGroupCount = 0;
        }
        runTask();

        mCanDraw = true;
        mResIsChange = true;
    }

    private void addGroupItemTask(ArrayList<StickerSubResARGroupItemImg> groups, int groupIndex, boolean needRunTask) {
        if (groups == null || groups.isEmpty()) {
            return;
        }
        StickerSubResARGroupItemImg item = groups.get(groupIndex);
        if (item == null || item.getImgPaths() == null || item.getImgPaths().isEmpty()) {
            return;
        }

        initTask(1, item.getItemIndex(), item.getImgPaths(), needRunTask);
    }

    /**
     *
     * @param type 0:普通的图片，1:分组的图片
     * @param itemIndex
     * @param imgPaths
     * @param needRunTask
     */
    private void initTask(int type, int itemIndex, ArrayList<String> imgPaths, boolean needRunTask) {
        String imgName = null;
        for (String imgPath : imgPaths) {
            imgName = getImgName(type, imgPath);
            if (imgName == null) {
                continue;
            }

            addTaskToQueue(new StickerARTextureTask(this, type, itemIndex, imgPath, imgName, new StickerARTextureTask.TaskCallback() {
                @Override
                public void onTaskCallback(int type, int itemIndex, String imgName, Bitmap bitmap) {
                    int textureId = -1;
                    if (bitmap != null && !bitmap.isRecycled()) {
                        textureId = GlUtil.createTexture(GLES20.GL_TEXTURE_2D, bitmap);
                        bitmap.recycle();
                        bitmap = null;
                    }

                    if (imgName != null && textureId > 0) {
                        if (type == 0 && mTextureIdsMap != null) {
                            mTextureIdsMap.put(imgName, textureId);//aa.jpg

                        } else if (type == 1 && mTextureGroupIdsMap != null) {
                            mTextureGroupIdsMap.put(itemIndex + "_" + imgName, textureId);//0_bb.jpg  1_bb.jpg
                        }
                    }
                }
            }));
        }
        if (needRunTask) {
            runTask();
        }
    }

    private String getImgName(int type, String imgPath) {
        if (TextUtils.isEmpty(imgPath)) {
            return null;
        }
        int pos = imgPath.lastIndexOf("/");
        String name = imgPath.substring(pos + 1);
        if (type == 1 && name.contains("_")) {
            try {
                String[] temp = name.split("_");
                if (temp != null) {
                    int groupId = Integer.parseInt(temp[0]);
                    if (groupId >= 0) {
                        name = temp[1];
                    }
                }
            } catch (Throwable t) {
                //t.printStackTrace();
            }
        }
        return name;
    }

    public boolean canDraw(boolean loadTexture) {
        int size = getTaskSize();
        if (size > 0) {
            if (loadTexture) {
                runTask();
            }
            if (mGroupSize <= 1 || mInitGroupCount < 1) {
                return false;
            }

        } else {
            if (loadTexture && mGroupSize > 1 && mInitGroupCount < mGroupSize) {
                mInitGroupCount++;
                if (mInitGroupCount < mGroupSize) {
                    addGroupItemTask(mARGroupItemImgs, mInitGroupCount, true);
                } else {
                    mARGroupItemImgs = null;
                }
            }
        }
        return mCanDraw;
    }

    private void updateModelData() {
        if (mResIsChange) {
            Poco3DGLNative.release3DmodelData(mRenderId, 0);
            if (mModelDesc != null) {
                Poco3DGLNative.configureRenderModel(mRenderId, mModelDesc, 1);
                //mModelDesc = null;
            }

            if (mTempAnimElementProgressDesc != null && !mTempAnimElementProgressDesc.isEmpty()) {
                if (mAnimElementProgressDesc == null) {
                    mAnimElementProgressDesc = new ArrayList<>();
                } else {
                    mAnimElementProgressDesc.clear();
                }
                mAnimElementProgressDesc.addAll(mTempAnimElementProgressDesc);

                mTempAnimElementProgressDesc.clear();

                Poco3DGLNative.updateARAnimationProgress(mRenderId, mAnimElementProgressDesc);
            }

            //Poco3DGLNative.executeAnimationPlay(mRenderId, 1);
            //Poco3DGLNative.executeAnimationCirclePlay(mRenderId, 1);

            mCurrentGroupIndex = 0;
            mTextureNames = Poco3DGLNative.getBitmapString(mRenderId);
            bindTextureIdByName(mCurrentGroupIndex);

            Poco3DGLNative.executedesiredRenderObj(mRenderId, 1);  // 是否执行渲染3D
            mResIsChange = false;
        }
    }

    private boolean bindTextureIdByName(int groupIndex) {
        if (mTextureNames != null && mTextureNames.length > 0
                && ((mTextureIdsMap != null && !mTextureIdsMap.isEmpty())
                || (mTextureGroupIdsMap != null && !mTextureGroupIdsMap.isEmpty()))) {
            if (mTextureIds == null || mTextureIds.length != mTextureNames.length) {
                mTextureIds = new int[mTextureNames.length];
            }

            Integer temp = null;
            for (int i = 0; i < mTextureNames.length; i++) {
                if (TextUtils.isEmpty(mTextureNames[i])) continue;

                if (mTextureIdsMap != null && (temp = mTextureIdsMap.get(mTextureNames[i])) != null) {
                    mTextureIds[i] = temp.intValue();

                } else if (mTextureGroupIdsMap != null && (temp = mTextureGroupIdsMap.get(groupIndex + "_" + mTextureNames[i])) != null) {
                    mTextureIds[i] = temp.intValue();
                }
            }
            temp = null;

            Poco3DGLNative.configureBitmapId(mRenderId, mTextureIds, mTextureNames);
            return true;
        }
        return false;
    }

    public void bindOwnFrameBuffer(boolean ownBuffer) {
        mBindOwnBuffer = ownBuffer;
    }

    public void setFaceData(PocoFace pocoFace) {
        mPocoFace = pocoFace;
    }

    public int getDepthFrameBufferId() {
        return mDepthFrameBufferId;
    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        updateModelData();

        if (mBindOwnBuffer && mDepthFrameBuffer != null) {
            mDepthFrameBuffer.bindNext(true);
            mDepthFrameBufferId = mDepthFrameBuffer.getTextureId();
        }

        if (mPocoFace != null && mPocoFace.points_count > 0) { //有人脸
            //Poco3DGLNative.executemodelViewMAvailable(mRenderId, 1);

            if (mAnimTriggerType == 1) {
                mCanChangeGroupRes = true;

            } else if (mAnimTriggerType == 2) {
                if (mGroupSize > 1 && mCanChangeGroupRes && ((mCurrentGroupIndex + 1) % mGroupSize) < mInitGroupCount) {
                    mCurrentGroupIndex = (mCurrentGroupIndex + 1) % mGroupSize;
                    bindTextureIdByName(mCurrentGroupIndex);
                    mCanChangeGroupRes = false;
                }
                Poco3DGLNative.executeAnimationPlay(mRenderId, 1);
                mAnimTriggerType = 3;
            } else if (mAnimTriggerType == 3) {
                //anim playing
            } else if (mAnimTriggerType == 4) {
                if (mGroupSize <= 1) {
                    Poco3DGLNative.executeAnimationCirclePlay(mRenderId, 1);
                    Poco3DGLNative.executeAnimationPlay(mRenderId, 1);
                    mAnimTriggerType = 5;
                }
            } else if (mAnimTriggerType == 5) {
                //circle playing
            } else {
                mCanChangeGroupRes = true;
            }

            float[] landmask3d = new float[mPocoFace.points3d_count * 3];

            for (int i = 0; i < mPocoFace.points3d_count; i++) {
                landmask3d[3 * i] = mPocoFace.points_array3D[3 * i];
                landmask3d[3 * i + 1] = mPocoFace.points_array3D[3 * i + 1];
                landmask3d[3 * i + 2] = mPocoFace.points_array3D[3 * i + 2];
            }

            float[] landmask2d = null;
            if (mPocoFace.points66_array != null) {
                landmask2d = new float[mPocoFace.points66_array.length];

                for (int i = 0; i < landmask2d.length / 2; i++) {
                    landmask2d[2 * i] = mDealWidth * mPocoFace.points66_array[2 * i];
                    landmask2d[2 * i + 1] = mDealHeight * mPocoFace.points66_array[2 * i + 1];
                }

                float[] rotate = {mPocoFace.pitch, mPocoFace.yaw, (float) (mPocoFace.roll - Math.PI / 2)};
                int[] imgsize = {mDealWidth, mDealHeight};
                Poco3DGLNative.updateLandmarksPair(mRenderId, landmask2d, landmask3d, rotate, imgsize, mFocalLength);
            }


            PointF _p_52 = new PointF(mPocoFace.points_array[52].x * mDealWidth, mPocoFace.points_array[52].y * mDealHeight);
            PointF _p_72 = new PointF(mPocoFace.points_array[72].x * mDealWidth, mPocoFace.points_array[72].y * mDealHeight);
            PointF _p_55 = new PointF(mPocoFace.points_array[55].x * mDealWidth, mPocoFace.points_array[55].y * mDealHeight);
            PointF _p_73 = new PointF(mPocoFace.points_array[73].x * mDealWidth, mPocoFace.points_array[73].y * mDealHeight);

            PointF _p_53 = new PointF(mPocoFace.points_array[53].x * mDealWidth, mPocoFace.points_array[53].y * mDealHeight);
            PointF _p_54 = new PointF(mPocoFace.points_array[54].x * mDealWidth, mPocoFace.points_array[54].y * mDealHeight);
            PointF _p_56 = new PointF(mPocoFace.points_array[56].x * mDealWidth, mPocoFace.points_array[56].y * mDealHeight);
            PointF _p_57 = new PointF(mPocoFace.points_array[57].x * mDealWidth, mPocoFace.points_array[57].y * mDealHeight);
            PointF _p_left_eye_center = new PointF((_p_53.x + _p_54.x + _p_56.x + _p_57.x) * 0.25f, (_p_53.y + _p_54.y + _p_56.y + _p_57.y) * 0.25f);

            ArrayList<PointF> left_eye_cons = new ArrayList<PointF>();
            left_eye_cons.add(_p_52);
            left_eye_cons.add(_p_72);
            left_eye_cons.add(_p_55);
            left_eye_cons.add(_p_73);
            left_eye_cons.add(_p_left_eye_center);
            float _t_left_eye_strength = calibrateEyeOpenStrength(left_eye_cons);

            PointF _p_61 = new PointF(mPocoFace.points_array[61].x * mDealWidth, mPocoFace.points_array[61].y * mDealHeight);
            PointF _p_75 = new PointF(mPocoFace.points_array[75].x * mDealWidth, mPocoFace.points_array[75].y * mDealHeight);
            PointF _p_58 = new PointF(mPocoFace.points_array[58].x * mDealWidth, mPocoFace.points_array[58].y * mDealHeight);
            PointF _p_76 = new PointF(mPocoFace.points_array[76].x * mDealWidth, mPocoFace.points_array[76].y * mDealHeight);

            PointF _p_59 = new PointF(mPocoFace.points_array[59].x * mDealWidth, mPocoFace.points_array[59].y * mDealHeight);
            PointF _p_60 = new PointF(mPocoFace.points_array[60].x * mDealWidth, mPocoFace.points_array[60].y * mDealHeight);
            PointF _p_62 = new PointF(mPocoFace.points_array[62].x * mDealWidth, mPocoFace.points_array[62].y * mDealHeight);
            PointF _p_63 = new PointF(mPocoFace.points_array[63].x * mDealWidth, mPocoFace.points_array[63].y * mDealHeight);
            PointF _p_right_eye_center = new PointF((_p_59.x + _p_60.x + _p_62.x + _p_63.x) * 0.25f, (_p_59.y + _p_60.y + _p_62.y + _p_63.y) * 0.25f);


            ArrayList<PointF> right_eye_cons = new ArrayList<PointF>();
            right_eye_cons.add(_p_61);
            right_eye_cons.add(_p_75);
            right_eye_cons.add(_p_58);
            right_eye_cons.add(_p_76);
            right_eye_cons.add(_p_right_eye_center);
            float _t_right_eye_strength = calibrateEyeOpenStrength(right_eye_cons);

            PointF _p_84 = new PointF(mPocoFace.points_array[84].x * mDealWidth, mPocoFace.points_array[84].y * mDealHeight);
            PointF _p_87 = new PointF(mPocoFace.points_array[87].x * mDealWidth, mPocoFace.points_array[87].y * mDealHeight);
            PointF _p_90 = new PointF(mPocoFace.points_array[90].x * mDealWidth, mPocoFace.points_array[90].y * mDealHeight);
            PointF _p_93 = new PointF(mPocoFace.points_array[93].x * mDealWidth, mPocoFace.points_array[93].y * mDealHeight);
            PointF _p_98 = new PointF(mPocoFace.points_array[98].x * mDealWidth, mPocoFace.points_array[98].y * mDealHeight);
            PointF _p_102 = new PointF(mPocoFace.points_array[102].x * mDealWidth, mPocoFace.points_array[102].y * mDealHeight);

            ArrayList<PointF> mouth_cons = new ArrayList<PointF>();
            mouth_cons.add(_p_84);
            mouth_cons.add(_p_87);
            mouth_cons.add(_p_90);
            mouth_cons.add(_p_93);
            mouth_cons.add(_p_98);
            mouth_cons.add(_p_102);
            float _t_mouth_strength = calibrateMouthOpenStrength(mouth_cons);

            if (mAnimElementProgressDesc != null) {
                if (mAnimElementProgressDesc.size() > 0) {
                    mAnimElementProgressDesc.get(0).progress = _t_left_eye_strength;
                }
                if (mAnimElementProgressDesc.size() > 1) {
                    mAnimElementProgressDesc.get(1).progress = _t_right_eye_strength;
                }
                if (mAnimElementProgressDesc.size() > 2) {
                    mAnimElementProgressDesc.get(2).progress = _t_mouth_strength;
                }

                Poco3DGLNative.updateARAnimationProgress(mRenderId, mAnimElementProgressDesc);
            }
        } else {
            //Poco3DGLNative.executemodelViewMAvailable(mRenderId, 0);//没人脸
        }
        mPocoFace = null;

        if (!mBindOwnBuffer) {
            if (mAttachmentParams == null) {
                mAttachmentParams = new int[1];
            }
            GLES20.glGetFramebufferAttachmentParameteriv(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE, mAttachmentParams, 0);
            if (mAttachmentParams[0] == GLES20.GL_NONE) {
                mDepthRenderBuffer = new int[1];
                GLES20.glGenRenderbuffers(1, mDepthRenderBuffer, 0);
                GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mDepthRenderBuffer[0]);

                GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, mWidth, mHeight);
                GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, mDepthRenderBuffer[0]);
            }
        }

        Poco3DGLNative.render3D(mRenderId, textureId, mBufferId);

    }

    @Override
    public int getTextureTarget() {
        return 0;
    }

    @Override
    protected void bindTexture(int textureId) {

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
    }

    @Override
    public void loadNextTexture(boolean load) {
    }

    private void releaseTextureId() {
        int count = 0;
        if (mTextureIdsMap != null) {
            count += mTextureIdsMap.size();
        }
        if (mTextureGroupIdsMap != null) {
            count += mTextureGroupIdsMap.size();
        }

        if (count > 0) {
            mTextureIds = new int[count];
            int i = 0;
            if (mTextureIdsMap != null && !mTextureIdsMap.isEmpty()) {
                for (Map.Entry<String, Integer> entry : mTextureIdsMap.entrySet()) {
                    mTextureIds[i] = entry.getValue().intValue();
                    i++;
                }
            }
            mTextureIdsMap = null;
            if (mTextureGroupIdsMap != null && !mTextureGroupIdsMap.isEmpty()) {
                for (Map.Entry<String, Integer> entry : mTextureGroupIdsMap.entrySet()) {
                    mTextureIds[i] = entry.getValue().intValue();
                    i++;
                }
            }
            mTextureGroupIdsMap = null;
        }
        if (mTextureIds != null && mTextureIds.length > 0) {
            GLES20.glDeleteTextures(mTextureIds.length, mTextureIds, 0);
        }
        mTextureIds = null;
    }

    @Override
    public void releaseProgram() {
        super.releaseProgram();
        Poco3DGLNative.release3DmodelData(mRenderId, 1);
        mProgramHandle = -1;

        releaseTextureId();

        if (mDepthFrameBuffer != null) {
            mDepthFrameBuffer.destroy();
            mDepthFrameBuffer = null;
        }

        mModelDesc = null;
        mTempAnimElementProgressDesc = null;
        mAnimElementProgressDesc = null;
    }

    private void poco_insert_pocopoint_into_array(ArrayList<Float> arrayList, PointF p) {
        arrayList.add(p.x);
        arrayList.add(p.y);
    }

    private PointF poco_getpoint_center_p(PointF p1, PointF p2) {
        PointF __t_p = new PointF();
        __t_p.x = (p1.x + p2.x) * 0.5f;
        __t_p.y = (p1.y + p2.y) * 0.5f;
        return __t_p;
    }

    private PointF poco_scale_point(PointF p, int scalex, int scaley) {
        PointF __t_p = new PointF();
        __t_p.x = p.x * scalex;
        __t_p.y = p.y * scaley;
        return __t_p;
    }

    private float[] get2dPoints(PointF[] face_src, int imageSize_width, int imageSize_height) {
//       float []_t_output_2d_landmarks = new float[66 * 2];

        ArrayList<Float> _t_output_2d_landmarks = new ArrayList();

        PointF _t_p;
        //右脸
        _t_p = poco_scale_point(face_src[0], imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(face_src[4], imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(poco_getpoint_center_p(face_src[5], face_src[6]), imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(face_src[7], imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(poco_getpoint_center_p(face_src[8], face_src[9]), imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(poco_getpoint_center_p(face_src[10], face_src[11]), imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(poco_getpoint_center_p(face_src[12], face_src[13]), imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(face_src[14], imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);

        _t_p = poco_scale_point(face_src[16], imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        //左脸
        _t_p = poco_scale_point(face_src[18], imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(poco_getpoint_center_p(face_src[20], face_src[19]), imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(poco_getpoint_center_p(face_src[22], face_src[21]), imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(poco_getpoint_center_p(face_src[24], face_src[23]), imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(face_src[25], imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(poco_getpoint_center_p(face_src[27], face_src[26]), imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(face_src[28], imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(face_src[30], imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);

        //左眉
        _t_p = poco_scale_point(face_src[33], imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(poco_getpoint_center_p(face_src[34], face_src[64]), imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(poco_getpoint_center_p(face_src[35], face_src[65]), imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(poco_getpoint_center_p(face_src[36], face_src[66]), imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(face_src[67], imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        //右眉
        _t_p = poco_scale_point(face_src[68], imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(poco_getpoint_center_p(face_src[39], face_src[69]), imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(poco_getpoint_center_p(face_src[40], face_src[70]), imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(poco_getpoint_center_p(face_src[41], face_src[71]), imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(face_src[42], imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);

        //鼻子
        _t_p = poco_scale_point(face_src[43], imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(face_src[44], imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(face_src[45], imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(face_src[46], imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(face_src[82], imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(poco_getpoint_center_p(face_src[47], face_src[48]), imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(face_src[49], imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(poco_getpoint_center_p(face_src[50], face_src[51]), imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(face_src[83], imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);

        //左眼
        int i = 0;
        for (i = 52; i <= 57; i++) {
            _t_p = poco_scale_point(face_src[i], imageSize_width, imageSize_height);
            poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        }
        //右眼
        for (i = 58; i <= 63; i++) {
            _t_p = poco_scale_point(face_src[i], imageSize_width, imageSize_height);
            poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        }
        //外唇上
        for (i = 84; i <= 90; i++) {
            _t_p = poco_scale_point(face_src[i], imageSize_width, imageSize_height);
            poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        }
        //外唇下
        for (i = 91; i <= 95; i++) {
            _t_p = poco_scale_point(face_src[i], imageSize_width, imageSize_height);
            poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        }
        //内唇
        _t_p = poco_scale_point(face_src[96], imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(poco_getpoint_center_p(face_src[98], face_src[97]), imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(poco_getpoint_center_p(face_src[98], face_src[99]), imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(face_src[100], imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(poco_getpoint_center_p(face_src[101], face_src[102]), imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);
        _t_p = poco_scale_point(poco_getpoint_center_p(face_src[103], face_src[102]), imageSize_width, imageSize_height);
        poco_insert_pocopoint_into_array(_t_output_2d_landmarks, _t_p);


        int convert_count = (int) _t_output_2d_landmarks.size();
        float[] _t_convert_y_output_landmarks = new float[convert_count];

        for (i = 0; i < convert_count; i++) {
            _t_convert_y_output_landmarks[i] = _t_output_2d_landmarks.get(i);
        }

        return _t_convert_y_output_landmarks;
    }

    private float calculateRadian(PointF fromVector, PointF dstVector) {
        float model_f = (float) Math.sqrt(Math.pow(fromVector.x, 2.0) + Math.pow(fromVector.y, 2.0));
        float model_dst = (float) Math.sqrt(Math.pow(dstVector.x, 2.0) + Math.pow(dstVector.y, 2.0));
        float dotProduct = fromVector.x * dstVector.x + fromVector.y * dstVector.y;
        float cos_theta = dotProduct / (model_f * model_dst);

        float theta = (float) Math.acos(cos_theta);
        theta = Math.abs(theta);

        float normal_z = fromVector.x * dstVector.y - fromVector.y * dstVector.x;
        if (normal_z < 0) {                                 //右手定则
            theta = -theta;
        }

        return theta;
    }

    private float calibrateEyeOpenStrength(ArrayList<PointF> eyePoints) {
        if (0 >= eyePoints.size()) {
            return 0.0f;
        }

        PointF _t_A = eyePoints.get(0);
        PointF _t_B = eyePoints.get(1);
        PointF _t_C = eyePoints.get(2);
        PointF _t_D = eyePoints.get(3);
        PointF _t_center = eyePoints.get(4);

        PointF _t_vector_C_center = new PointF(_t_center.x - _t_C.x, _t_center.y - _t_C.y);
        PointF _t_vector_C_B = new PointF(_t_B.x - _t_C.x, _t_B.y - _t_C.y);
        float _t_radians = Math.abs(calculateRadian(_t_vector_C_center, _t_vector_C_B));

        float eye_close_min_threshold = 0.14f;
        float eye_spread_max_threshold = 0.27f;

        _t_radians = Math.max(eye_close_min_threshold, Math.min(eye_spread_max_threshold, _t_radians));
        _t_radians = (_t_radians - eye_close_min_threshold) / (eye_spread_max_threshold - eye_close_min_threshold);

        return _t_radians;
    }

    private float calibrateMouthOpenStrength(ArrayList<PointF> mouthPoints) {
        if (0 >= mouthPoints.size()) {
            return 0.f;
        }

        PointF _t_84 = mouthPoints.get(0);
        PointF _t_87 = mouthPoints.get(1);
        PointF _t_90 = mouthPoints.get(2);
        PointF _t_93 = mouthPoints.get(3);
        PointF _t_98 = mouthPoints.get(4);
        PointF _t_102 = mouthPoints.get(5);

        PointF _t_vector_horizontal = new PointF(_t_90.x - _t_84.x, _t_90.y - _t_84.y);
        float _length_vec_hor = (float) Math.sqrt(_t_vector_horizontal.x * _t_vector_horizontal.x + _t_vector_horizontal.y * _t_vector_horizontal.y);
        PointF _t_vector_vertical = new PointF(_t_102.x - _t_98.x, _t_102.y - _t_98.y);
        float _length_vec_ver = (float) Math.sqrt(_t_vector_vertical.x * _t_vector_vertical.x + _t_vector_vertical.y * _t_vector_vertical.y);

        float mouth_close_min_threshold = 0.07f;
        float mouth_spread_max_threshold = 0.6f;

        float _v_output_mouth_spread = _length_vec_ver / _length_vec_hor;
        _v_output_mouth_spread = Math.max(mouth_close_min_threshold, Math.min(mouth_spread_max_threshold, _v_output_mouth_spread));
        _v_output_mouth_spread = (_v_output_mouth_spread - mouth_close_min_threshold) / (mouth_spread_max_threshold - mouth_close_min_threshold);

        return _v_output_mouth_spread;
    }
}

