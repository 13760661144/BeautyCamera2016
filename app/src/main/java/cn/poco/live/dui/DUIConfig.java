package cn.poco.live.dui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;

import java.util.ArrayList;

import cn.poco.camera3.beauty.STag;
import cn.poco.camera3.beauty.data.BeautyShapeDataUtils;
import cn.poco.camera3.beauty.data.ShapeDataType;
import cn.poco.camera3.beauty.data.SuperShapeData;
import cn.poco.camera3.beauty.recycler.ShapeExAdapter;
import cn.poco.camera3.info.sticker.StickerInfo;
import cn.poco.camera3.mgr.StatusMgr;
import cn.poco.live.PCStatusBeautyListener;
import cn.poco.live.PCStatusFilterListener;
import cn.poco.live.PCStatusListener;
import cn.poco.live.PCStatusShapeListener;
import cn.poco.live.server.BMTServer;
import cn.poco.live.server.BMTUi;
import cn.poco.resource.FilterRes;
import my.beautyCamera.R;

/**
 * Created by Administrator on 2018/1/9.
 */

public class DUIConfig {
    private static DUIConfig sInstance;
    private PCStatusListener mStatusListener;

    public static DUIConfig getInstance() {
        if (sInstance == null) {
            sInstance = new DUIConfig();
        }
        return sInstance;
    }

    private DUICtrl mRoot = new DUICtrl("op_area");
    private DUIPageList mPageDecor = new DUIPageList();
    private DUIPageList mPageFilter = new DUIPageList();
    private DUIPageList mPageMeiXing = new DUIPageList();
    private DUIPageList mPageMeiYan = new DUIPageList();
    private DUIPageAdjust mPageMeiXingAdjust = new DUIPageAdjust();
    private DUITabHolder mTabHolder = new DUITabHolder();
    private DUIPageHolder mPageHolder = new DUIPageHolder();
    private TabItem mTabMeiYan = new TabItem();
    private TabItem mTabMeiXing = new TabItem();
    private TabItem mTabFilter = new TabItem();
    private TabItem mTabDecor = new TabItem();
    private DUISlider mMeiFu = new DUISlider();
    private DUISlider mMeiYa = new DUISlider();
    private DUISlider mMoPi = new DUISlider();


    private DUISlider mShouLianAdj = new DUISlider();
    private DUISlider mXiaoLianAdj = new DUISlider();
    private DUISlider mXueLianAdj = new DUISlider();
    private DUISlider mEtouAdj = new DUISlider();
    private DUISlider mQuanGuAdj = new DUISlider();
    private DUISlider mDaYanAdj = new DUISlider();
    private DUISlider mYanJiaoAdj = new DUISlider();
    private DUISlider mYanJuAdj = new DUISlider();
    private DUISlider mShouBiAdj = new DUISlider();
    private DUISlider mBiYiAdj = new DUISlider();
    private DUISlider mBiGaoAdj = new DUISlider();
    private DUISlider mXiaBaAdj = new DUISlider();
    private DUISlider mZuiXingAdj = new DUISlider();
    private DUISlider mZuiGaoAdj = new DUISlider();

    public DUIConfig() {
        mTabHolder = new DUITabHolder();
        mRoot.addChild(mTabHolder);
        mTabMeiYan.setName("美颜");
        mTabMeiYan.setOnCtrlEventListener(mTabClickListener);
        mTabHolder.setSelectedId(mTabMeiYan.getId());
        mTabHolder.addChild(mTabMeiYan);
        mTabMeiXing.setName("脸型");
        mTabMeiXing.setOnCtrlEventListener(mTabClickListener);
        mTabHolder.addChild(mTabMeiXing);
        mTabFilter.setName("滤镜");
        mTabFilter.setOnCtrlEventListener(mTabClickListener);
        mTabHolder.addChild(mTabFilter);
        mTabDecor.setName("贴纸");
        mTabDecor.setOnCtrlEventListener(mTabClickListener);
        mTabHolder.addChild(mTabDecor);

        mRoot.addChild(mPageHolder);

        mPageMeiYan.setName("请调整你的美颜");
        mPageMeiYan.setColNumber(1);
        mPageMeiYan.setItemSize(315, 22);
        mPageMeiYan.setSpaceY(24);
        mPageMeiYan.setListPaddingTop(134);
        mPageMeiYan.setListPaddingLeft(22);
        mPageHolder.addChild(mPageMeiYan);

        mMeiFu.setName("美肤");
        mMeiFu.setSlideType(STag.BeautyTag.SKINBEAUTY);
        mMeiFu.setMax(100);
        mMeiFu.setMin(0);
        mMeiFu.setPos(0);
        mMeiFu.setOnCtrlEventListener(mSliderListener);
        mPageMeiYan.addChild(mMeiFu);

        mMeiYa.setName("美牙");
        mMeiYa.setSlideType(STag.BeautyTag.WHITENTEETH);
        mMeiYa.setOnCtrlEventListener(mSliderListener);
        mMeiYa.setMin(0);
        mMeiYa.setMax(100);
        mMeiYa.setPos(0);
        mPageMeiYan.addChild(mMeiYa);

        mMoPi.setName("肤色");
        mMoPi.setSlideType(STag.BeautyTag.SKINTYPE);
        mMoPi.setMax(100);
        mMoPi.setMin(0);
        mMoPi.setPos(0);
        mMoPi.setOnCtrlEventListener(mSliderListener);
        mPageMeiYan.addChild(mMoPi);

        mPageMeiXing.setName("请选择你喜爱的脸型");
        mPageMeiXing.setColNumber(3);
        mPageMeiXing.setItemSize(68, 90);
        mPageMeiXing.setSpaceY(24);
        mPageMeiXing.setSpaceX(34);
        mPageMeiXing.setListPaddingLeft(40);
        mPageHolder.addChild(mPageMeiXing);

        mPageFilter.setName("请选择你喜爱的滤镜");
        mPageFilter.setColNumber(3);
        mPageFilter.setItemSize(68, 90);
        mPageFilter.setSpaceY(20);
        mPageFilter.setSpaceX(34);
        mPageFilter.setListPaddingLeft(38);
        mPageHolder.addChild(mPageFilter);

        mPageDecor.setName("请选择你喜爱的贴纸");
        mPageHolder.addChild(mPageDecor);
        DUIDecorItem btn = new DUIDecorItem();
        btn.setDecorID(-1);
        btn.setImageResource(R.drawable.decor_cancel_icon);
        btn.setOnCtrlEventListener(mDecorClickListener);
        mPageDecor.addChild(btn);

        mPageMeiXingAdjust.setName("高级美形");
        mPageMeiXingAdjust.setStyle(DUIPageAdjust.STYLE_BIGTITLE);
        mPageMeiXingAdjust.setOnCtrlEventListener(mMeiXingAdjustPageEventListener);
        mPageHolder.addChild(mPageMeiXingAdjust);

        mShouLianAdj.setName("瘦脸");
        mShouLianAdj.setMax(100);
        mShouLianAdj.setMin(0);
        mShouLianAdj.setSlideType(ShapeDataType.THINFACE);
        mShouLianAdj.setOnCtrlEventListener(mMeiXingAdjustPageEventListener);
        mPageMeiXingAdjust.addChild(mShouLianAdj);

        mXiaoLianAdj.setName("小脸");
        mXiaoLianAdj.setMax(100);
        mXiaoLianAdj.setMin(0);
        mXiaoLianAdj.setSlideType(ShapeDataType.LITTLEFACE);
        mXiaoLianAdj.setOnCtrlEventListener(mMeiXingAdjustPageEventListener);
        mPageMeiXingAdjust.addChild(mXiaoLianAdj);

        mXueLianAdj.setName("削脸");
        mXueLianAdj.setMax(100);
        mXueLianAdj.setMin(0);
        mXueLianAdj.setSlideType(ShapeDataType.SHAVEDFACE);
        mXueLianAdj.setOnCtrlEventListener(mMeiXingAdjustPageEventListener);
        mPageMeiXingAdjust.addChild(mXueLianAdj);

        mEtouAdj.setName("额头");
        mEtouAdj.setMax(100);
        mEtouAdj.setMin(-100);
        mEtouAdj.setSlideType(ShapeDataType.FOREHEAD);
        mEtouAdj.setOnCtrlEventListener(mMeiXingAdjustPageEventListener);
        mPageMeiXingAdjust.addChild(mEtouAdj);

        mQuanGuAdj.setName("颧骨");
        mQuanGuAdj.setMax(100);
        mQuanGuAdj.setMin(0);
        mQuanGuAdj.setSlideType(ShapeDataType.CHEEKBONES);
        mQuanGuAdj.setOnCtrlEventListener(mMeiXingAdjustPageEventListener);
        mPageMeiXingAdjust.addChild(mQuanGuAdj);

        mDaYanAdj.setName("大眼");
        mDaYanAdj.setMax(100);
        mDaYanAdj.setMin(0);
        mDaYanAdj.setSlideType(ShapeDataType.BIGEYE);
        mDaYanAdj.setOnCtrlEventListener(mMeiXingAdjustPageEventListener);
        mPageMeiXingAdjust.addChild(mDaYanAdj);


        mYanJiaoAdj.setName("眼角");
        mYanJiaoAdj.setMax(100);
        mYanJiaoAdj.setMin(-100);
        mYanJiaoAdj.setSlideType(ShapeDataType.CANTHUS);
        mYanJiaoAdj.setOnCtrlEventListener(mMeiXingAdjustPageEventListener);
        mPageMeiXingAdjust.addChild(mYanJiaoAdj);

        mYanJuAdj.setName("眼距");
        mYanJuAdj.setMax(100);
        mYanJuAdj.setMin(-100);
        mYanJuAdj.setSlideType(ShapeDataType.EYESPAN);
        mYanJuAdj.setOnCtrlEventListener(mMeiXingAdjustPageEventListener);
        mPageMeiXingAdjust.addChild(mYanJuAdj);

        mShouBiAdj.setName("瘦鼻");
        mShouBiAdj.setMax(100);
        mShouBiAdj.setMin(0);
        mShouBiAdj.setSlideType(ShapeDataType.SHRINKNOSE);
        mShouBiAdj.setOnCtrlEventListener(mMeiXingAdjustPageEventListener);
        mPageMeiXingAdjust.addChild(mShouBiAdj);

        mBiYiAdj.setName("鼻翼");
        mBiYiAdj.setMax(100);
        mBiYiAdj.setMin(-100);
        mBiYiAdj.setSlideType(ShapeDataType.NOSEWING);
        mBiYiAdj.setOnCtrlEventListener(mMeiXingAdjustPageEventListener);
        mPageMeiXingAdjust.addChild(mBiYiAdj);

        //鼻高
        mBiGaoAdj.setName("鼻高");
        mBiGaoAdj.setMax(100);
        mBiGaoAdj.setMin(-100);
        mBiGaoAdj.setSlideType(ShapeDataType.NOSEHEIGHT);
        mBiGaoAdj.setOnCtrlEventListener(mMeiXingAdjustPageEventListener);
        mPageMeiXingAdjust.addChild(mBiGaoAdj);

        mXiaBaAdj.setName("下巴");
        mXiaBaAdj.setMax(100);
        mXiaBaAdj.setMin(-100);
        mXiaBaAdj.setSlideType(ShapeDataType.CHIN);
        mXiaBaAdj.setOnCtrlEventListener(mMeiXingAdjustPageEventListener);
        mPageMeiXingAdjust.addChild(mXiaBaAdj);

        mZuiXingAdj.setName("嘴型");
        mZuiXingAdj.setMax(100);
        mZuiXingAdj.setMin(-100);
        mZuiXingAdj.setSlideType(ShapeDataType.MOUTH);
        mZuiXingAdj.setOnCtrlEventListener(mMeiXingAdjustPageEventListener);
        mPageMeiXingAdjust.addChild(mZuiXingAdj);

        //嘴巴整体高度
        mZuiGaoAdj.setName("嘴高");
        mZuiGaoAdj.setMax(100);
        mZuiGaoAdj.setMin(-100);
        mZuiGaoAdj.setSlideType(ShapeDataType.OVERALLHEIGHT);
        mZuiGaoAdj.setOnCtrlEventListener(mMeiXingAdjustPageEventListener);
        mPageMeiXingAdjust.addChild(mZuiGaoAdj);

       // mPageFilterAdjust.setName("请调整滤镜浓度");
       // mPageFilterAdjust.setStyle(DUIPageAdjust.STYLE_SMALLTITLE);
       // mPageFilterAdjust.setOnCtrlEventListener(mFilterAdjustPageEventListener);
       // mPageHolder.addChild(mPageFilterAdjust);
//
       // mFilterAdj.setName("浓度");
       // mPageFilterAdjust.addChild(mFilterAdj);

        mPageHolder.setCurrentPage(mPageMeiYan.getId());

        BMTUi.getInstance().setRoot(mRoot);
        BMTUi.getInstance().updateUIHeader();
        BMTServer.getInstance().addOnConnectListener(mOnConnectListener);
    }

    public void initDecorToPC(ArrayList<StickerInfo> arr)
    {
        if (arr == null) return;

        ArrayList<DUICtrl> ctrls = new ArrayList<>();
        for (StickerInfo info : arr)
        {
            if (info != null)
            {
                DUIDecorItem btn = new DUIDecorItem();
                btn.setDecorID(info.id);
                if (info.mStatus == StatusMgr.Type.BUILT_IN)
                {
                    btn.setImageResource((Integer) info.mThumb);
                }
                else
                {
                    btn.setImageFile((String) info.mThumb);
                }
                btn.setOnCtrlEventListener(mDecorClickListener);
                mPageDecor.addChild(btn);
                ctrls.add(btn);
            }
        }

        BMTUi.getInstance().postInsertCommand(mPageDecor.getId(), ctrls, 1);
    }

    public void insertDecorToPC(StickerInfo info)
    {
        if (info == null) return;

        DUIDecorItem btn = new DUIDecorItem();
        btn.setDecorID(info.id);
        if (info.mStatus == StatusMgr.Type.BUILT_IN)
        {
            btn.setImageResource((Integer) info.mThumb);
        }
        else
        {
            btn.setImageFile((String) info.mThumb);
        }
        btn.setOnCtrlEventListener(mDecorClickListener);
        mPageDecor.addChild(btn);

        BMTUi.getInstance().postInsertCommand(btn, 1);
    }

    public void updateDecorStatusToPC(int res_id)
    {
        int size = mPageDecor.getChildCount();
        for (int i = 0; i < size; i++)
        {
            DUIDecorItem item = (DUIDecorItem) mPageDecor.getChildAt(i);
            if (item != null && item.getDecorID() == res_id)
            {
                mPageDecor.setSelectedId(item.getId());
                BMTUi.getInstance().postModifyCommand(item);
            }
        }
    }

    public void deletedDecor(ArrayList<Integer> arr)
    {
        if (arr == null) return;

        ArrayList<DUICtrl> out = new ArrayList<>();

        for (int id : arr)
        {
            int size = mPageDecor.getChildCount();
            for (int i = 0; i < size; i++)
            {
                DUIDecorItem item = (DUIDecorItem) mPageDecor.getChildAt(i);
                if (item != null && item.getDecorID() == id)
                {
                    out.add(item);
                    break;
                }
            }
        }

        for (DUICtrl ctrl : out)
        {
            mPageDecor.removeChild(ctrl);
        }

        BMTUi.getInstance().postDeleteCommand(mPageDecor.getId(), out);
    }

    /**
     * 更新美颜拉杆数据
     *
     * @param type
     * @param uiProgress
     */
    public void updateBeautySlideToPC(@STag.BeautyTag int type, int uiProgress)
    {
        if (mPageMeiYan != null)
        {
            int childCount = mPageMeiYan.getChildCount();
            for (int i = 0; i < childCount; i++)
            {
                DUISlider childAt = (DUISlider) mPageMeiYan.getChildAt(i);
                if (childAt != null && childAt.getSlideType() == type)
                {
                    childAt.setPos(uiProgress);
                }
            }
        }
    }

    /**
     * 添加滤镜item到pc
     *
     * @param filterRes
     * @param maskColor
     */
    public void insertFilterToPC(FilterRes filterRes, int maskColor)
    {
        if (filterRes == null)
        {
            return;
        }

        DUIFilterItem filterItem = new DUIFilterItem();
        filterItem.setFilterId(filterRes.m_id);
        if (maskColor != 0) {
            filterItem.setMaskColor(maskColor);
        }
        filterItem.setOnCtrlEventListener(mFilterItemClickListener);
        filterItem.setIsCancel(true);
        if (filterRes.m_id == 0) {
            filterItem.setName("原图");
            filterItem.setImageResource(R.drawable.filter_res_non_icon);
        } else {
            filterItem.setName(filterRes.m_name);
            if (filterRes.m_thumb instanceof Integer) {
                filterItem.setImageResource((Integer) filterRes.m_thumb);
            } else if (filterRes.m_thumb instanceof String){
                filterItem.setImageFile((String) filterRes.m_thumb);
            }
        }
        mPageFilter.addChild(filterItem);
        if (filterItem.getFilterId() == 0) {
            BMTUi.getInstance().postInsertCommand(filterItem, 0);
        } else {
            BMTUi.getInstance().postInsertCommand(filterItem, 1);
        }
    }

    public void deleteFilterToPC(ArrayList<Integer> filter_id)
    {
        if (filter_id != null && filter_id.size() > 0)
        {
            ArrayList<DUICtrl> out = new ArrayList<>();

            for (int id : filter_id)
            {
                int size = mPageFilter.getChildCount();
                for (int i = 0; i < size; i++)
                {
                    DUIFilterItem item = (DUIFilterItem) mPageFilter.getChildAt(i);
                    if (item != null && item.getFilterId() == id)
                    {
                        out.add(item);
                        break;
                    }
                }
            }

            for (DUICtrl ctrl : out)
            {
                mPageFilter.removeChild(ctrl);
            }

            BMTUi.getInstance().postDeleteCommand(mPageFilter.getId(), out);
        }
    }

    /**
     * 更新滤镜选中状态
     *
     * @param filterId
     */
    public void updateFilterStatusToPC(int filterId)
    {
        int childCount = mPageFilter.getChildCount();
        for (int i = 0; i < childCount; i++)
        {
            DUIFilterItem filterItem = (DUIFilterItem) mPageFilter.getChildAt(i);
            if (filterItem != null && filterItem.getFilterId() == filterId)
            {
                mPageFilter.setSelectedId(filterItem.getId());
                BMTUi.getInstance().postModifyCommand(filterItem);
            }
        }
    }

    /**
     * 添加脸型item
     *
     * @param itemInfos
     */
    public void insertShapeToPC(ArrayList<ShapeExAdapter.ShapeExItemInfo> itemInfos)
    {
        if (itemInfos != null && itemInfos.size() > 0)
        {
            ArrayList<DUICtrl> out = new ArrayList<>();
            for (ShapeExAdapter.ShapeExItemInfo itemInfo : itemInfos)
            {
                if (itemInfo != null)
                {
                    DUICircleItem btn = new DUICircleItem();
                    if (itemInfo.m_uri == SuperShapeData.ID_NON_SHAPE) {
                        btn.setIsCancel(true);
                    }
                    if (itemInfo.m_logo instanceof Integer) {
                        btn.setImageResource((Integer) itemInfo.m_logo);
                    } else if (itemInfo.m_logo instanceof String) {
                        btn.setImageFile((String) itemInfo.m_logo);
                    }
                    btn.setShapeId(itemInfo.m_uri);
                    btn.setName(itemInfo.m_name);
                    btn.setOnCtrlEventListener(mMeiXingItemClickListener);
                    mPageMeiXing.addChild(btn);
                    out.add(btn);
                }
            }
            BMTUi.getInstance().postInsertCommand(mPageMeiXing.getId(), out, 0);
        }
    }

    /**
     * 更新脸型item选中状态
     *
     * @param shape_id
     */
    public void  updateShapeStatusToPC(int shape_id)
    {
        int childCount = mPageMeiXing.getChildCount();
        for (int i = 0; i < childCount; i++)
        {
            DUICircleItem duiCircleItem = (DUICircleItem) mPageMeiXing.getChildAt(i);
            if (duiCircleItem != null && duiCircleItem.getShapeId() == shape_id)
            {
                mPageMeiXing.setSelectedId(duiCircleItem.getId());
                BMTUi.getInstance().postModifyCommand(duiCircleItem);
            }
        }
    }


    /**
     * 更新脸型类型数据
     *
     * @param type
     * @param realProgress
     */
    public void updateShapeAdjustStatusToPC(@ShapeDataType int type, int realProgress)
    {
        int childCount = mPageMeiXingAdjust.getChildCount();
        for (int i = 0; i < childCount; i++)
        {
            DUISlider childAt = (DUISlider) mPageMeiXingAdjust.getChildAt(i);
            if (childAt != null && childAt.getSlideType() == type)
            {
                int seekbarType = BeautyShapeDataUtils.GetSeekbarType(type);
                if (seekbarType == STag.SeekBarType.SEEK_TAG_BIDIRECTIONAL) {
                    //双向seekbar
                    realProgress = BeautyShapeDataUtils.GetBidirectionalUISize(realProgress);
                }
                childAt.setPos(realProgress);
            }
        }
    }

    public void setShapeAdjustPageStatus(int shapeId, boolean isOpen)
    {
        int childCount = mPageMeiXing.getChildCount();
        for (int i = 0; i < childCount; i++)
        {
            DUICircleItem duiCircleItem = (DUICircleItem) mPageMeiXing.getChildAt(i);
            if (mPageMeiXing.getSelectedId() == duiCircleItem.getId() && shapeId == duiCircleItem.getShapeId()) {
                if (isOpen) {
                    mPageHolder.setCurrentPage(mPageMeiXingAdjust.getId());
                } else {
                    mPageHolder.backToLastPage();
                }
                break;
            }
        }
    }

    private BMTServer.OnConnectListener mOnConnectListener = new BMTServer.OnConnectListener() {
        @Override
        public void onConnected() {
            BMTUi.getInstance().updateUIHeader();

            if (mStatusListener != null)
            {
                mStatusListener.onPCConnected();
            }
        }

        @Override
        public void onDisconnected() {
            if (mStatusListener != null)
            {
                mStatusListener.onPCDisconnected();
            }
        }
    };

    /**
     * 美肤、美牙、肤质的拉杆
     */
    private DUICtrl.OnCtrlEventListener mSliderListener = new DUICtrl.OnCtrlEventListener() {
        @Override
        public void onEvent(DUICtrl ctrl, String event, Bundle attr) {
            if (!TextUtils.isEmpty(event) && event.contains(DUICtrl.EVENT_SLIDER))
            {
                int progress = 0;
                if (attr.containsKey("pos"))
                {
                    progress = Integer.parseInt(attr.getString("pos"));
                }
                DUISlider slider = (DUISlider) ctrl;
                if (slider != null && mStatusListener != null && mStatusListener instanceof PCStatusBeautyListener)
                {
                    int type = STag.BeautyTag.UNSET;
                    switch (slider.getSlideType())
                    {
                        case STag.BeautyTag.SKINBEAUTY:
                        {
                            type = STag.BeautyTag.SKINBEAUTY;
                            break;
                        }
                        case STag.BeautyTag.WHITENTEETH:
                        {
                            type = STag.BeautyTag.WHITENTEETH;
                            break;
                        }
                        case STag.BeautyTag.SKINTYPE:
                        {
                            type = STag.BeautyTag.SKINTYPE;
                            break;
                        }
                    }
                    ((PCStatusBeautyListener) mStatusListener).onPCSliderBeauty(type, progress);
                }
            }
            //Log.d("hwq", event);
        }
    };

    private DUICtrl.OnCtrlEventListener mTabClickListener = new DUICtrl.OnCtrlEventListener() {
        @Override
        public void onEvent(DUICtrl ctrl, String event, Bundle attr) {
            int id = ctrl.getId();
            if (id == mTabMeiYan.getId()) {
                mPageHolder.setCurrentPage(mPageMeiYan.getId());
                if (mStatusListener != null && mStatusListener instanceof PCStatusBeautyListener)
                {
                    ((PCStatusBeautyListener) mStatusListener).onPCClickBeautyTab();
                }
            } else if (id == mTabMeiXing.getId()) {
                mPageHolder.setCurrentPage(mPageMeiXing.getId());
                if (mStatusListener != null && mStatusListener instanceof PCStatusShapeListener)
                {
                    ((PCStatusShapeListener) mStatusListener).onPCClickShapeTab();
                }
            } else if (id == mTabFilter.getId()) {
                mPageHolder.setCurrentPage(mPageFilter.getId());
                if (mStatusListener != null && mStatusListener instanceof PCStatusFilterListener)
                {
                    ((PCStatusFilterListener) mStatusListener).onPCClickFilterTab();
                }
            } else if (id == mTabDecor.getId()) {
                mPageHolder.setCurrentPage(mPageDecor.getId());
                if (mStatusListener != null)
                {
                    mStatusListener.onPCClickDecorTab();
                }
            }
            mTabHolder.setSelectedId(id);
        }
    };

    private DUICtrl.OnCtrlEventListener mDecorClickListener = new DUICtrl.OnCtrlEventListener() {
        @Override
        public void onEvent(DUICtrl ctrl, String event, Bundle attr) {
            mPageDecor.setSelectedId(ctrl.getId());

            if (mStatusListener != null)
            {
                mStatusListener.onPCSelectedDecor(((DUIDecorItem)ctrl).getDecorID());
            }
        }
    };

    private DUICtrl.OnCtrlEventListener mMeiXingItemClickListener = new DUICtrl.OnCtrlEventListener() {
        @Override
        public void onEvent(DUICtrl ctrl, String event, Bundle attr) {
            boolean isOpenSub = false;
            if (event.equals(DUICtrl.EVENT_DOUBLECLICK) || mPageMeiXing.getSelectedId() == ctrl.getId()) {
                //二级菜单编辑
                if (((DUICircleItem) ctrl).getShapeId() == SuperShapeData.ID_NON_SHAPE)
                {
                } else {
                    isOpenSub = true;
                    mPageHolder.setCurrentPage(mPageMeiXingAdjust.getId());
                    if (mStatusListener != null && mStatusListener instanceof PCStatusShapeListener) {
                        ((PCStatusShapeListener) mStatusListener).onPCShapeSubLayoutOpen(((DUICircleItem) ctrl).getShapeId(), true);
                    }
                }
            }
            mPageMeiXing.setSelectedId(ctrl.getId());
            if (!isOpenSub && mStatusListener != null && mStatusListener instanceof PCStatusShapeListener && ctrl instanceof DUICircleItem) {
                ((PCStatusShapeListener) mStatusListener).onPCSelectedShape(((DUICircleItem) ctrl).getShapeId());
            }
        }
    };

    private DUICtrl.OnCtrlEventListener mFilterItemClickListener = new DUICtrl.OnCtrlEventListener() {
        @Override
        public void onEvent(DUICtrl ctrl, String event, Bundle attr) {
            /*if (event.equals(DUICtrl.EVENT_DOUBLECLICK) || mPageFilter.getSelectedId() == ctrl.getId()) {
                mPageHolder.setCurrentPage(mPageFilterAdjust.getId());
            }*/
            mPageFilter.setSelectedId(ctrl.getId());
            if (mStatusListener != null && mStatusListener instanceof PCStatusFilterListener && ctrl instanceof DUIFilterItem)
            {
                ((PCStatusFilterListener) mStatusListener).onPCSelectedFilter(((DUIFilterItem) ctrl).getFilterId());
            }
        }
    };

    private DUICtrl.OnCtrlEventListener mFilterAdjustPageEventListener = new DUICtrl.OnCtrlEventListener() {
        @Override
        public void onEvent(DUICtrl ctrl, String event, Bundle attr) {
            if (event.equals(DUICtrl.EVENT_CUSTOM)) {
                String e = attr.getString("event");
                if ("back".equals(e)) {
                    mPageHolder.backToLastPage();
                    if (mStatusListener != null && mStatusListener instanceof PCStatusFilterListener) {
                        ((PCStatusFilterListener) mStatusListener).onPCFilterSubLayoutOpen(false);
                    }
                } else if ("reset".equals(e)) {
                    if (mStatusListener != null && mStatusListener instanceof PCStatusFilterListener) {
                        for (int i = 0, childCount = mPageFilter.getChildCount(); i < childCount; i++) {
                            DUIFilterItem filterItem = (DUIFilterItem) mPageFilter.getChildAt(i);
                            if (filterItem.getId() == mPageFilter.getSelectedId()) {
                                ((PCStatusFilterListener) mStatusListener).onPCResetFilterData(filterItem.getFilterId());
                                break;
                            }
                        }
                    }
                }
            } else if (event.contains(DUICtrl.EVENT_SLIDER)) {
                //滑动
            }
        }
    };

    private DUICtrl.OnCtrlEventListener mMeiXingAdjustPageEventListener = new DUICtrl.OnCtrlEventListener() {
        @Override
        public void onEvent(DUICtrl ctrl, String event, Bundle attr) {
            if (event.equals(DUICtrl.EVENT_CUSTOM)) {
                String e = attr.getString("event");
                if ("back".equals(e)) {
                    mPageHolder.backToLastPage();
                    if (mStatusListener != null && mStatusListener instanceof PCStatusShapeListener) {
                        for (int i = 0, childCount = mPageMeiXing.getChildCount(); i < childCount; i++) {
                            DUICircleItem childAt = (DUICircleItem) mPageMeiXing.getChildAt(i);
                            if (childAt.getId() == mPageMeiXing.getSelectedId()) {
                                ((PCStatusShapeListener) mStatusListener).onPCShapeSubLayoutOpen(childAt.getShapeId(), false);
                                break;
                            }
                        }
                    }
                } else if ("reset".equals(e)) {
                    if (mStatusListener != null && mStatusListener instanceof PCStatusShapeListener) {
                        for (int i = 0, childCount = mPageMeiXing.getChildCount(); i < childCount; i++) {
                            DUICircleItem childAt = (DUICircleItem) mPageMeiXing.getChildAt(i);
                            if (childAt.getId() == mPageMeiXing.getSelectedId()) {
                                ((PCStatusShapeListener) mStatusListener).onPCResetShapeData(childAt.getShapeId());
                                break;
                            }
                        }
                    }
                }
            } else if (event.contains(DUICtrl.EVENT_SLIDER)) {
                if (ctrl != null && ctrl instanceof DUISlider) {
                    if (mStatusListener != null && mStatusListener instanceof PCStatusShapeListener) {
                        int slideType = ((DUISlider) ctrl).getSlideType();
                        int progress = attr.containsKey("pos") ? Integer.parseInt(attr.getString("pos")) : 0;

                        for (int i = 0, childCount = mPageMeiXing.getChildCount(); i < childCount; i++) {
                            DUICircleItem childAt = (DUICircleItem) mPageMeiXing.getChildAt(i);
                            if (childAt.getId() == mPageMeiXing.getSelectedId()) {
                                ((PCStatusShapeListener) mStatusListener).onPCSlideShapeAdjust(slideType, childAt.getShapeId(), progress);
                                break;
                            }
                        }
                    }
                }
            }
        }
    };

    public void ClearAll()
    {
        if (mPageDecor != null)
        {
            mPageDecor.clearChild();
            DUIDecorItem btn = new DUIDecorItem();
            btn.setDecorID(-1);
            btn.setImageResource(R.drawable.decor_cancel_icon);
            btn.setOnCtrlEventListener(mDecorClickListener);
            mPageDecor.addChild(btn);
        }

        if (mPageFilter != null)
        {
            mPageFilter.clearChild();
        }

        if (mPageMeiXing != null)
        {
            mPageMeiXing.clearChild();
        }

        //TODO 退出清除
    }

    public void setStatusListener(PCStatusListener listener)
    {
        this.mStatusListener = listener;
    }

    private class DUITabHolder extends DUICtrl {

        private int mCurSelId;

        public DUITabHolder() {
            super("tab_holder");
        }

        public void setSelectedId(int id) {
            mCurSelId = id;
            BMTUi.getInstance().postModifyCommand(this);
        }

        public ArrayList<Pair<String, String>> getAttributes() {
            ArrayList<Pair<String, String>> list = super.getAttributes();
            list.add(new Pair<String, String>("selected", "" + mCurSelId));
            return list;
        }
    }

    private class TabItem extends DUICtrl {
        public TabItem() {
            super("tab");
        }
    }

    private class DUIPageHolder extends DUICtrl {
        private int mCurPageId;
        private int mLastPageId;

        public DUIPageHolder() {
            super("page_switch");
        }

        public void setCurrentPage(int id) {
            mLastPageId = mCurPageId;
            mCurPageId = id;
            BMTUi.getInstance().postModifyCommand(this);
        }

        public void backToLastPage() {
            setCurrentPage(mLastPageId);
        }

        public ArrayList<Pair<String, String>> getAttributes() {
            ArrayList<Pair<String, String>> list = super.getAttributes();
            list.add(new Pair<String, String>("selected", "" + mCurPageId));
            return list;
        }
    }

    private class DUIPageAdjust extends DUICtrl {
        public static final int STYLE_SMALLTITLE = 0;
        public static final int STYLE_BIGTITLE = 1;
        public int mStyle = 0;

        public DUIPageAdjust() {
            super("page_adjust");
        }

        public void setStyle(int style) {
            mStyle = style;
        }

        public ArrayList<Pair<String, String>> getAttributes() {
            ArrayList<Pair<String, String>> list = super.getAttributes();
            list.add(new Pair<String, String>("style", "" + mStyle));
            return list;
        }
    }

    private class DUIPageList extends DUICtrl {
        private int mCurSelId;
        private int mListPaddingLeft = 38;
        private int mListPaddingTop = 86;
        private int mColNumber = 3;
        private int mItemWidth = 68;
        private int mItemHeight = 68;
        private int mSpaceX = 32;
        private int mSpaceY = 12;

        public DUIPageList() {
            super("page_list");
        }

        public void setSelectedId(int id) {
            mCurSelId = id;
            BMTUi.getInstance().postModifyCommand(this);
        }

        public int getSelectedId() {
            return mCurSelId;
        }

        public void setListPaddingLeft(int padding) {
            mListPaddingLeft = padding;
            BMTUi.getInstance().postModifyCommand(this);
        }

        public void setListPaddingTop(int padding) {
            mListPaddingTop = padding;
            BMTUi.getInstance().postModifyCommand(this);
        }

        public void setItemSize(int width, int height) {
            mItemWidth = width;
            mItemHeight = height;
            BMTUi.getInstance().postModifyCommand(this);
        }

        public void setColNumber(int col) {
            mColNumber = col;
            BMTUi.getInstance().postModifyCommand(this);
        }

        public void setSpaceX(int space) {
            mSpaceX = space;
            BMTUi.getInstance().postModifyCommand(this);
        }

        public void setSpaceY(int space) {
            mSpaceY = space;
            BMTUi.getInstance().postModifyCommand(this);
        }

        public ArrayList<Pair<String, String>> getAttributes() {
            ArrayList<Pair<String, String>> list = super.getAttributes();
            list.add(new Pair<String, String>("selected", "" + mCurSelId));
            list.add(new Pair<String, String>("list_padding_left", "" + mListPaddingLeft));
            list.add(new Pair<String, String>("list_padding_top", "" + mListPaddingTop));
            list.add(new Pair<String, String>("col", "" + mColNumber));
            list.add(new Pair<String, String>("item_width", "" + mItemWidth));
            list.add(new Pair<String, String>("item_height", "" + mItemHeight));
            list.add(new Pair<String, String>("spacex", "" + mSpaceX));
            list.add(new Pair<String, String>("spacey", "" + mSpaceY));
            return list;
        }
    }

    private class DUISlider extends DUICtrl {
        private int mPos = 50;
        private int mMin = 0;
        private int mMax = 100;
        private int mSlideType;

        public DUISlider() {
            super("slider");
        }

        public void setSlideType(int mSlideType)
        {
            this.mSlideType = mSlideType;
        }

        public int getSlideType()
        {
            return mSlideType;
        }

        public int getPos()
        {
            return mPos;
        }

        public void setPos(int pos) {
            mPos = pos;
            BMTUi.getInstance().postModifyCommand(this);
        }

        public void setMin(int min) {
            mMin = min;
            BMTUi.getInstance().postModifyCommand(this);
        }

        public void setMax(int max) {
            mMax = max;
            BMTUi.getInstance().postModifyCommand(this);
        }

        public ArrayList<Pair<String, String>> getAttributes() {
            ArrayList<Pair<String, String>> list = super.getAttributes();
            list.add(new Pair<String, String>("pos", "" + mPos));
            list.add(new Pair<String, String>("min", "" + mMin));
            list.add(new Pair<String, String>("max", "" + mMax));
            return list;
        }
    }

    private class DUIDecorItem extends DUICtrl {
        private Object mResImage = 0;
        private int mDecorID;
        public DUIDecorItem() {
            super("decor_item");
        }

        public void setImageResource(int resImage) {
            mResImage = resImage;
            BMTUi.getInstance().addResource(mResImage);
        }

        public void setImageFile(String resImage) {
            mResImage = resImage;
            BMTUi.getInstance().addResource(mResImage);
        }

        public ArrayList<Pair<String, String>> getAttributes() {
            ArrayList<Pair<String, String>> list = super.getAttributes();
            if (mResImage instanceof Integer) {
                list.add(new Pair<String, String>("image", "" + (Integer) mResImage));
            } else {
                list.add(new Pair<String, String>("image", "" + (String) mResImage));
            }
            return list;
        }

        public int getDecorID()
        {
            return mDecorID;
        }

        public void setDecorID(int id)
        {
            this.mDecorID = id;
        }
    }

    private class DUICircleItem extends DUICtrl {
        private Object mResImage = 0;
        private boolean mIsCancel = false;
        private int mShapeId;

        public DUICircleItem() {
            super("circle_item");
        }

        public void setImageResource(int resImage) {
            mResImage = resImage;
            BMTUi.getInstance().addResource(mResImage);
        }

        public void setImageFile(String resImage) {
            mResImage = resImage;
            BMTUi.getInstance().addResource(mResImage);
        }

        public int getShapeId()
        {
            return mShapeId;
        }

        public void setShapeId(int mShapeId)
        {
            this.mShapeId = mShapeId;
        }

        public void setIsCancel(boolean isCancel) {
            mIsCancel = isCancel;
        }

        public ArrayList<Pair<String, String>> getAttributes() {
            ArrayList<Pair<String, String>> list = super.getAttributes();
            if (mResImage instanceof Integer) {
                list.add(new Pair<String, String>("image", "" + (Integer)mResImage));
            } else {
                list.add(new Pair<String, String>("image", "" + (String) mResImage));
            }
            list.add(new Pair<String, String>("is_cancel", mIsCancel ? "1" : "0"));
            return list;
        }
    }

    private class DUIFilterItem extends DUICtrl {
        private Object mResImage = 0;
        private boolean mIsCancel = false;
        private int mMaskColor = 0xe75988;
        private int mFilterId;

        public DUIFilterItem() {
            super("filter_item");
        }

        public void setImageResource(int resImage) {
            mResImage = resImage;
            BMTUi.getInstance().addResource(mResImage);
        }

        public void setImageFile(String resImage) {
            mResImage = resImage;
            BMTUi.getInstance().addResource(mResImage);
        }

        public int getFilterId()
        {
            return mFilterId;
        }

        public void setFilterId(int mFilterId)
        {
            this.mFilterId = mFilterId;
        }

        public void setIsCancel(boolean isCancel) {
            mIsCancel = isCancel;
        }

        public void setMaskColor(int color) {
            mMaskColor = color;
        }

        public ArrayList<Pair<String, String>> getAttributes() {
            ArrayList<Pair<String, String>> list = super.getAttributes();
            if (mResImage instanceof Integer) {
                list.add(new Pair<String, String>("image", "" + (Integer)mResImage));
            } else {
                list.add(new Pair<String, String>("image", "" + (String) mResImage));
            }
            list.add(new Pair<String, String>("mask_color", "" + mMaskColor));
            list.add(new Pair<String, String>("is_cancel", mIsCancel ? "1" : "0"));
            return list;
        }
    }
}
