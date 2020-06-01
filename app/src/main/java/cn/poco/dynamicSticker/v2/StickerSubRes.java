package cn.poco.dynamicSticker.v2;

import java.util.ArrayList;

import cn.poco.dynamicSticker.FaceAction;

/**
 * Created by zwq on 2017/04/05 10:46.<br/><br/>
 */
public class StickerSubRes extends StickerSubResBaseV2 {

    private String mTypeName;//类型名称
    private ArrayList<String> mJsonNames;//json文件名
    private ArrayList<String> mImgNames;//图片文件名
    private int mTier;//网络控制层级
    private float mScale = 1.0f;//缩放比例
    private float mOffsetX;//左负右正(正方向向右)
    private float mOffsetY;//上负下正(正方向向下)
    private String mGX;//left center right fill 当屏幕分辨率与素材分辨率不符时的布局方式(仅适用于场景素材)  scale>1.0是为裁剪方式，scale<1.0时为对齐方式
    private String mGY;//top center bottom fill (仅适用于场景素材)
    private int mLayerCompositeMode = 0;//混合模式
    private float mLayerOpaqueness = 1.0f;//混合模式不透明度
    private String mAction;//是否是脸部动作触发
    private boolean mGifEnable = false;//是否用在gif
    private float[] mGifOffset;//gif偏移量，适用于gif素材

    private boolean mSFullEnable = true;//是否支持全面屏
    private boolean mS43Enable = false;//是否支持4:3比例
    private float[] mS43Offset;//4:3比例偏移量

    private boolean mS916Enable = false;//9:16
    private float[] mS916Offset;

    private int mSubType = 1;//只针对3d素材   1:静态渲染，2:播放动画，3:动态表情
    private ArrayList<String> m3DModelData;//xx.fbx xx.mtl xx.obj
    private ArrayList<StickerSubResARGroupItemImg> mStickerSubResARGroupItemImgs;
    private ArrayList<StickerSubResDesc> mStickerSubResDescs;

    public StickerSubRes() {
    }

    public int getSubType() {
        return mSubType;
    }

    public void setSubType(int subType) {
        mSubType = subType;
    }

    public ArrayList<String> get3DModelData() {
        return m3DModelData;
    }

    public void set3DModelData(ArrayList<String> modelData) {
        this.m3DModelData = modelData;
    }

    public ArrayList<StickerSubResARGroupItemImg> getStickerSubResARGroupItemImgs() {
        return mStickerSubResARGroupItemImgs;
    }

    public void setStickerSubResARGroupItemImgs(ArrayList<StickerSubResARGroupItemImg> stickerSubResARGroupItemImgs) {
        mStickerSubResARGroupItemImgs = stickerSubResARGroupItemImgs;
    }

    public ArrayList<StickerSubResDesc> getStickerSubResDescs() {
        return mStickerSubResDescs;
    }

    public void setStickerSubResDescs(ArrayList<StickerSubResDesc> stickerSubResDescs) {
        mStickerSubResDescs = stickerSubResDescs;
    }

    public boolean isSFullEnable() {
        return mSFullEnable;
    }

    public void setSFullEnable(boolean sFullEnable) {
        mSFullEnable = sFullEnable;
    }

    public boolean isS43Enable() {
        return mS43Enable;
    }

    public void setS43Enable(boolean s43Enable) {
        this.mS43Enable = s43Enable;
    }

    public float[] getS43Offset() {
        return mS43Offset;
    }

    public void setS43Offset(float[] s43Offset) {
        this.mS43Offset = s43Offset;
    }

    public void setS916Enable(boolean s916Enable) {
        mS916Enable = s916Enable;
    }

    public boolean isS916Enable() {
        return mS916Enable;
    }

    public void setS916Offset(float[] s916Offset) {
        mS916Offset = s916Offset;
    }

    public float[] getS916Offset() {
        return mS916Offset;
    }

    public float[] getGifOffset() {
        return mGifOffset;
    }

    public void setGifOffset(float[] gifOffset) {
        this.mGifOffset = gifOffset;
    }

    public boolean isGifEnable() {
        return mGifEnable;
    }

    public void setGifEnable(boolean gifEnable) {
        this.mGifEnable = gifEnable;
    }

    public String getTypeName() {
        return mTypeName;
    }

    public void setTypeName(String typeName) {
        mTypeName = typeName;
    }

    public ArrayList<String> getJsonNames() {
        return mJsonNames;
    }

    public void setJsonNames(ArrayList<String> jsonNames) {
        mJsonNames = jsonNames;
    }

    public ArrayList<String> getImgNames() {
        return mImgNames;
    }

    public void setImgNames(ArrayList<String> imgNames) {
        mImgNames = imgNames;

        //test
        if (m3DModelData != null && mImgNames != null) {
            mStickerSubResARGroupItemImgs = new ArrayList<>(1);
            StickerSubResARGroupItemImg arImg = new StickerSubResARGroupItemImg();
            arImg.setItemIndex(0);
            for (String path : mImgNames) {
                arImg.addImg(path);
            }
            mStickerSubResARGroupItemImgs.add(arImg);
        }
    }

    public int getTier() {
        return mTier;
    }

    public void setTier(int tier) {
        mTier = tier;
    }

    public float getScale() {
        return mScale;
    }

    public void setScale(float scale) {
        if (scale <= 0.0f) {
            scale = 1.0f;
        }
        mScale = scale;
    }

    public float getOffsetX() {
        return mOffsetX;
    }

    public void setOffsetX(float offsetX) {
        mOffsetX = offsetX;
    }

    public float getOffsetY() {
        return mOffsetY;
    }

    public void setOffsetY(float offsetY) {
        mOffsetY = offsetY;
    }

    public String getGX() {
        return mGX;
    }

    public void setGX(String GX) {
        mGX = GX;
    }

    public String getGY() {
        return mGY;
    }

    public void setGY(String GY) {
        mGY = GY;
    }

    public int getLayerCompositeMode() {
        return mLayerCompositeMode;
    }

    public void setLayerCompositeMode(int layerCompositeMode) {
        mLayerCompositeMode = layerCompositeMode;
    }

    public float getLayerOpaqueness() {
        return mLayerOpaqueness;
    }

    public void setLayerOpaqueness(float layerOpaqueness) {
        if (layerOpaqueness <= 0.0f) {
            layerOpaqueness = 100.0f;
        }
        mLayerOpaqueness = layerOpaqueness / 100.f;
    }

    public String getAction() {
        return mAction;
    }

    public void setAction(String action) {
        mAction = action;
        mIsActionRes = FaceAction.isExistAction(mAction);
    }

    /**
     * 裁剪方式 转为 对齐方式，right裁剪 -> 左对齐
     *
     * @param type 0:Foreground，1:Frame
     * @return 0:左对齐，1:居中，2:右对齐
     */
    public int getXAlignType(int type) {
        if ("right".equals(mGX)) {
            return 0;
        } else if ("center".equals(mGX)) {
            return 1;
        } else if ("left".equals(mGX)) {
            return 2;
        } else if ("fill".equals(mGX)) {
            return 3;
        } else {
            return 0;
        }
    }

    /**
     * 裁剪方式 转为 对齐方式，bottom裁剪 -> 上对齐
     *
     * @param type 0:Foreground，1:Frame
     * @return 0:上对齐，1:居中，2:下对齐
     */
    public int getYAlignType(int type) {
        if ("bottom".equals(mGY)) {
            return 0;
        } else if ("center".equals(mGY)) {
            return 1;
        } else if ("top".equals(mGY)) {
            return 2;
        } else if ("fill".equals(mGY)) {
            return 3;
        } else {
//            return type == 0 ? 2 : 0;
            return 0;
        }
    }
}
