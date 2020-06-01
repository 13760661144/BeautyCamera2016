package cn.poco.glfilter.shape;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import cn.poco.camera3.beauty.data.ShapeData;
import cn.poco.gldraw2.FaceDataHelper;
import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.glfilter.base.GlUtil;
import cn.poco.glfilter.shape.V2.CrazyCheekbonesShapeFilter;
import cn.poco.glfilter.shape.V2.CrazyChinMaskShapeFilterV2;
import cn.poco.glfilter.shape.V2.CrazyChinShapeFilterV2;
import cn.poco.glfilter.shape.V2.CrazyEyeShapeFilter;
import cn.poco.glfilter.shape.V2.CrazyEyeSmileFilter;
import cn.poco.glfilter.shape.V2.CrazyMouthShapeFilterV2;
import cn.poco.glfilter.shape.V2.CrazyNoseRoundShapeFilterV2;
import cn.poco.glfilter.shape.V2.CrazyNoseShapeFilterV2;
import cn.poco.glfilter.shape.V2.SuperCrazyShapeFilterV2;

/**
 * Created by zwq on 2017/09/11 16:47.<br/><br/>
 * 美形定制
 * <p>
 * 处理顺序:
 * 眼睛、微笑、瘦鼻、嘴型、嘴提升、颧骨、眼角度(眼神)、眼距、鼻翼、鼻提升、削脸、瘦脸、小脸、额头、下巴
 * </p>
 * <p>
 * 眼睛、微笑、CrazyEyeSmileFilter
 * 瘦鼻、CrazyNoseShapeFilterV2
 * 嘴型、嘴提升、CrazyMouthShapeFilterV2
 * //## 已合并到Chin ##//额头、CrazyHeadShapeFilterV2
 * 颧骨 CrazyCheekbonesShapeFilter
 * 眼角度、眼距、CrazyEyeShapeFilter
 * 鼻翼、鼻提升、CrazyNoseRoundShapeFilterV2
 * 削脸、瘦脸、小脸、SuperCrazyShapeFilterV2
 * 额头、下巴、CrazyChinMaskShapeFilterV2 CrazyChinShapeFilterV2
 * </p>
 */
public class BeautifyFilter extends FaceShapeFilter {

    private static final String vertexShaderCode =
            "attribute vec4 aPosition;\n" +
                    "attribute vec2 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "    gl_Position = aPosition;\n" +
                    "    vTextureCoord = aTextureCoord;\n" +
                    "}";

    private static final String fragmentShaderCode =
            "precision mediump float;\n" +
                    "uniform sampler2D uTexture;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "       gl_FragColor = texture2D(uTexture, vTextureCoord);\n" +
                    "}";

    private final int EyeSmile = 0;
    private final int Nose = 1;
    private final int Mouth = 2;
    private final int CheekBone = 3;
    private final int EyeAngleDistance = 4;
    private final int NoseRound = 5;
    private final int Face = 6;

    private final int Head = 7;
    private final int ChinMask = 8;//额头、下巴变形蒙版
    private final int Chin = 9;


    protected HashMap<Object, DefaultFilter> mFilterCache;
    private int mFilterId;
    private ShapeInfoData mShapeInfoData;

    public BeautifyFilter(Context context) {
        super(context);
    }

    @Override
    protected int createProgram(Context context) {
        if (mFilterCache == null) {
            mFilterCache = new HashMap<>();
        }
        initAllFilter(context);

        return GlUtil.createProgram(vertexShaderCode, fragmentShaderCode);
    }

    private void initAllFilter(Context context) {
        DefaultFilter filter = null;
        try {
            filter = new CrazyEyeSmileFilter(context);
            mFilterCache.put(Integer.valueOf(EyeSmile), filter);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            filter = new CrazyNoseShapeFilterV2(context);
            mFilterCache.put(Integer.valueOf(Nose), filter);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            filter = new CrazyMouthShapeFilterV2(context);
            mFilterCache.put(Integer.valueOf(Mouth), filter);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            filter = new CrazyCheekbonesShapeFilter(context);
            mFilterCache.put(Integer.valueOf(CheekBone), filter);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            filter = new CrazyEyeShapeFilter(context);
            mFilterCache.put(Integer.valueOf(EyeAngleDistance), filter);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            filter = new CrazyNoseRoundShapeFilterV2(context);
            mFilterCache.put(Integer.valueOf(NoseRound), filter);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            filter = new SuperCrazyShapeFilterV2(context);
            mFilterCache.put(Integer.valueOf(Face), filter);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        //#################################################
        /*try {
            filter = new CrazyHeadShapeFilterV2(context);
            mFilterCache.put(Integer.valueOf(Head), filter);
        } catch (Throwable e) {
            e.printStackTrace();
        }*/
        try {
            filter = new CrazyChinMaskShapeFilterV2(context);
            mFilterCache.put(Integer.valueOf(ChinMask), filter);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            filter = new CrazyChinShapeFilterV2(context);
            mFilterCache.put(Integer.valueOf(Chin), filter);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setRenderScale(float renderScale) {
        super.setRenderScale(renderScale);
        if (mFilterCache != null) {
            for (Map.Entry<Object, DefaultFilter> entry : mFilterCache.entrySet()) {
                if (entry.getValue() != null) {
                    entry.getValue().setRenderScale(renderScale);
                }
            }
        }
    }

    @Override
    public void setViewSize(int width, int height) {
        super.setViewSize(width, height);
        if (mFilterCache != null) {
            for (Map.Entry<Object, DefaultFilter> entry : mFilterCache.entrySet()) {
                if (entry.getValue() != null) {
                    entry.getValue().setViewSize(width, height);
                }
            }
        }
    }

    @Override
    public boolean isNeedFlipTexture() {
        return false;
    }

    public void setShapeFilterId(int filterId) {
        if (filterId != mFilterId || mShapeInfoData == null) {
            mFilterId = filterId;
            mShapeInfoData = ShapeInfoData.getShapeInfoById(mFilterId);

//            //++++++++++++++++++++++++++++++++++++++++
//            int shapeId = SuperShapeData.ID_NON_SHAPE;
//            switch (mFilterId) {
//                case 17:
//                    shapeId = SuperShapeData.ID_ZIRANXIUSHI;
//                case 18:
//                    shapeId = SuperShapeData.ID_MODENGNVWANG;
//                    break;
//                case 19:
//                    shapeId = SuperShapeData.ID_JINGZHIWANGHONG;
//                    break;
//                case 20:
//                    shapeId = SuperShapeData.ID_BABIGONGZHU;
//                    break;
//                case 21:
//                    shapeId = SuperShapeData.ID_DUDUTONGYAN;
//                    break;
//                case 22:
//                    shapeId = SuperShapeData.ID_JIMENGSHAONV;
//                    break;
//                case 23:
//                    shapeId = SuperShapeData.ID_DAIMENGTIANXIN;
//                    break;
//                case 24:
//                    shapeId = SuperShapeData.ID_XIAOLIANNVSHEN;
//                    break;
//            }
//            ShapeData shapeData = null;
//            float[] data = SuperShapeData.GetDefData(shapeId);
//            if (data != null) {
//                ShapeInfo info = new ShapeInfo();
//                info.setParamsData(data);
//                shapeData = info.getData();
//            }
//            setShapeData(shapeData);
        }
    }

    public void setShapeData(ShapeData shapeData) {
        if (shapeData == null) return;
        if (mShapeInfoData == null) {
            mShapeInfoData = new ShapeInfoData();
        }
        mShapeInfoData.isOvalEye = shapeData.eyes_type == ShapeData.EYE_TYPE.OVAL_EYES;
        mShapeInfoData.eyeRadius = shapeData.bigEye_radius;//眼睛
        mShapeInfoData.eyeStrength = shapeData.bigEye;

        mShapeInfoData.smileRadius = shapeData.smile_radius;//微笑
        mShapeInfoData.smileStrength = shapeData.smile;

        mShapeInfoData.noseRadius = shapeData.shrinkNose_radius;//瘦鼻
        mShapeInfoData.noseStrength = shapeData.shrinkNose;

        mShapeInfoData.mouthRadius = shapeData.mouth_radius;//嘴型
        mShapeInfoData.mouthStrength = shapeData.mouth;

        mShapeInfoData.mouthHighStrength = shapeData.overallHeight;//嘴高低

        mShapeInfoData.headStrength = shapeData.forehead;//额头

        mShapeInfoData.eyeAngleStrength = shapeData.canthus;//眼角

        mShapeInfoData.eyeDistStrength = shapeData.eyeSpan;//眼距

        mShapeInfoData.noseWingStrength = shapeData.nosewing;// / 2.0f + 50f;//鼻翼

        mShapeInfoData.noseLengthStrength = shapeData.noseHeight;//鼻高

        mShapeInfoData.faceRadius2 = shapeData.shavedFace_radius;//削脸
        mShapeInfoData.faceStrength2 = shapeData.shavedFace;

        mShapeInfoData.faceRadius1 = shapeData.thinFace_radius;//瘦脸
        mShapeInfoData.faceStrength1 = shapeData.thinFace;

        mShapeInfoData.faceRadius3 = shapeData.littleFace_radius;//小脸
        mShapeInfoData.faceStrength3 = shapeData.littleFace;

        mShapeInfoData.chinRadius = shapeData.chin_radius;//下巴
        mShapeInfoData.chinStrength = shapeData.chin;

        mShapeInfoData.cheekBoneRadius = shapeData.cheekbones_radius;
        mShapeInfoData.cheekBoneStrength = shapeData.cheekbones;//颧骨
    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        if (mFilterCache == null || mFilterCache.isEmpty() || mGLFramebuffer == null) {
            return;
        }

        int faceSize = FaceDataHelper.getInstance().getFaceSize();
        if (faceSize >= 1) {
            for (int i = 0; i < faceSize; i++) {
                mPocoFace = FaceDataHelper.getInstance().changeFace(i).getFace();
                if (mPocoFace != null && mShapeInfoData != null) {
                    drawSubShape(i, mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);
                } else {
                    if (i > 0) {
                        mGLFramebuffer.bindNext(true);
                        textureId = mGLFramebuffer.getPreviousTextureId();
                    }
                    drawNormal(0, mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);
                }
            }

            mPocoFace = FaceDataHelper.getInstance().changeFace(0).getFace();
            if (mPocoFace != null) {
                //额头和下巴合并到同一个filter处理
                int frameBufferId = textureId;
                int chinMaskId = frameBufferId;
                DefaultFilter filter = mFilterCache.get(Integer.valueOf(ChinMask));
                if (filter instanceof CrazyChinMaskShapeFilterV2) {
                    mGLFramebuffer.bindNext(true);
                    frameBufferId = mGLFramebuffer.getPreviousTextureId();

                    ((CrazyChinMaskShapeFilterV2) filter).setFaceData(mPocoFace);
                    ((CrazyChinMaskShapeFilterV2) filter).setShapeData(mShapeInfoData);
                    filter.onDraw(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, frameBufferId, texStride);

                    chinMaskId = ((CrazyChinMaskShapeFilterV2) filter).getMaskTextureId();
                }

                filter = mFilterCache.get(Integer.valueOf(Chin));
                if (filter instanceof CrazyChinShapeFilterV2) {
                    mGLFramebuffer.rebind(true);

                    ((CrazyChinShapeFilterV2) filter).setMaskTextureId(chinMaskId);
                    filter.onDraw(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, frameBufferId, texStride);
                }
            }

        } else {
            drawNormal(0, mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);
        }

        mUseOtherFaceData = false;
        mPocoFace = null;
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
    }

    public int drawNormal(int faceIndex, float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        useProgram();
        bindTexture(textureId);
        bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);
        drawArrays(firstVertex, vertexCount);
        unbindGLSLValues();
        unbindTexture();
        disuseProgram();
        return 0;
    }

    public int drawSubShape(int faceIndex, float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        DefaultFilter filter = null;
        int subIndex = 0;
        int frameBufferId = textureId;

        for (int i = 0; i < 7; i++) {
            if (subIndex > 0 && mPocoFace == null) {
                continue;
            }
            filter = mFilterCache.get(Integer.valueOf(i));
            if (filter == null) {
                continue;
            }
            if ((faceIndex == 0 && subIndex > 0) || faceIndex > 0) {
                mGLFramebuffer.bindNext(true);
                frameBufferId = mGLFramebuffer.getPreviousTextureId();
            }
            subIndex++;

            if (filter instanceof CrazyEyeSmileFilter) {
                ((CrazyEyeSmileFilter) filter).setFaceData(mPocoFace);
                ((CrazyEyeSmileFilter) filter).setShapeData(mShapeInfoData);

            } else if (filter instanceof CrazyNoseShapeFilterV2) {
                ((CrazyNoseShapeFilterV2) filter).setFaceData(mPocoFace);
                ((CrazyNoseShapeFilterV2) filter).setShapeData(mShapeInfoData);

            } else if (filter instanceof CrazyMouthShapeFilterV2) {
                ((CrazyMouthShapeFilterV2) filter).setFaceData(mPocoFace);
                ((CrazyMouthShapeFilterV2) filter).setShapeData(mShapeInfoData);

            } else if (filter instanceof CrazyEyeShapeFilter) {
                ((CrazyEyeShapeFilter) filter).setFaceData(mPocoFace);
                ((CrazyEyeShapeFilter) filter).setShapeData(mShapeInfoData);

            } else if (filter instanceof CrazyNoseRoundShapeFilterV2) {
                ((CrazyNoseRoundShapeFilterV2) filter).setFaceData(mPocoFace);
                ((CrazyNoseRoundShapeFilterV2) filter).setShapeData(mShapeInfoData);

            } else if (filter instanceof SuperCrazyShapeFilterV2) {
                filter.setFramebuffer(mGLFramebuffer);
                ((SuperCrazyShapeFilterV2) filter).setFaceData(mPocoFace);
                ((SuperCrazyShapeFilterV2) filter).setShapeData(mShapeInfoData);

            } else if (filter instanceof CrazyCheekbonesShapeFilter) {
                ((CrazyCheekbonesShapeFilter) filter).setFaceData(mPocoFace);
                ((CrazyCheekbonesShapeFilter) filter).setShapeData(mShapeInfoData);
            }

            filter.onDraw(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, frameBufferId, texStride);

        }
        return subIndex;
    }

    @Override
    public void releaseProgram() {
        super.releaseProgram();
        if (mFilterCache != null) {
            for (Map.Entry<Object, DefaultFilter> entry : mFilterCache.entrySet()) {
                if (entry.getValue() != null) {
                    entry.getValue().releaseProgram();
                }
            }
            mFilterCache.clear();
            mFilterCache = null;
        }
    }
}
