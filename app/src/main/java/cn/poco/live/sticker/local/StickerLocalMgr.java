package cn.poco.live.sticker.local;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseIntArray;

import java.util.ArrayList;
import java.util.ListIterator;

import cn.poco.camera3.info.sticker.LabelInfo;
import cn.poco.camera3.info.sticker.LabelLocalInfo;
import cn.poco.camera3.info.sticker.StickerInfo;
import cn.poco.camera3.mgr.StatusMgr;
import cn.poco.camera3.ui.drawable.StickerMgrNonDrawable;
import cn.poco.live.dui.DUIConfig;
import cn.poco.live.sticker.StickerMgr;
import cn.poco.resource.LiveVideoStickerResMgr2;

/**
 * 直播 贴纸素材管理页 的管理类
 * Created by Gxx on 2018/1/16.
 */

public class StickerLocalMgr
{
    public interface DataListener
    {
        void onSelectedLabel(int index);

        void onLabelScrollToSelected(int index);

        void onChangeSelectedIconStatus(int status);

        void onChangeDeleteIconAlpha(int status);
    }

    public interface StickerStatusListener
    {
        int getIndex();

        void OnStatusChange(int id);
        
        void OnDeleted();
    }
    
    private static StickerLocalMgr sInstance;

    private ArrayList<LabelLocalInfo> mLabelArr;

    private SparseIntArray mSelectedInfoArr;
    // 记录每个被选中的 sticker 对应的 标签 index, 只是用来通知 数据刷新
    private SparseIntArray mLabelIndexes;

    // 记录每个 sticker local pager view 的回调
    private SparseArray<StickerStatusListener> mPagerViewHelperArr;

    private StickerMgrNonDrawable mStickerMgrNonDrawable;
    private boolean mHasDeleted;

    private StickerLocalMgr()
    {
    }

    public synchronized static StickerLocalMgr getInstance()
    {
        if (sInstance == null)
        {
            sInstance = new StickerLocalMgr();
        }
        return sInstance;
    }

    public void unregisterStatusListener(StickerStatusListener helper)
    {
        if (mPagerViewHelperArr == null || helper == null) return;
        mPagerViewHelperArr.delete(helper.getIndex());
    }

    public void registerStatusListener(StickerStatusListener helper)
    {
        if (mPagerViewHelperArr == null || helper == null) return;
        mPagerViewHelperArr.put(helper.getIndex(), helper);
    }

    public boolean hasDeleted()
    {
        return mHasDeleted;
    }

    public void init(Context context)
    {
        mStickerMgrNonDrawable = new StickerMgrNonDrawable(context);

        mPagerViewHelperArr = new SparseArray<>();

        mLabelIndexes = new SparseIntArray();

        mSelectedInfoArr = new SparseIntArray();
        mSelectedInfoArr.put(0, 0);

        initData(context);
    }

    private void initData(Context context)
    {
        mLabelArr = new ArrayList<>();

        ArrayList<LabelInfo> temp_list = StickerMgr.getInstance().getLabelInfoArr(context, false);

        if (temp_list != null)
        {
            int size = temp_list.size();
            for (int index = 0; index < size; index++)
            {
                LabelInfo label_info = temp_list.get(index);
                if (label_info != null)
                {
                    LabelLocalInfo label_local_info = new LabelLocalInfo();

                    if (index == getSelectedLabelIndex())
                    {
                        label_local_info.isSelected = true;
                    }

                    label_local_info.ID = label_info.ID;
                    label_local_info.mStickerArr = new ArrayList<>();
                    label_local_info.mIndex = index;
                    label_local_info.mLabelName = label_info.mLabelName;
                    label_local_info.mType = label_info.mType;

                    ArrayList<StickerInfo> preview_sticker_list = label_info.mStickerArr;
                    if (preview_sticker_list != null)
                    {
                        for (StickerInfo info : preview_sticker_list)
                        {
                            if (info != null && isLocalRes(info))
                            {
                                label_local_info.mStickerArr.add(info);
                            }
                        }
                    }

                    mLabelArr.add(label_local_info);
                }
            }
        }
    }

    private boolean isLocalRes(StickerInfo info)
    {
        return info.mStatus == StatusMgr.Type.LOCAL || info.mStatus == StatusMgr.Type.BUILT_IN;
    }

    public boolean isHadLocalRes(ArrayList<StickerInfo> list)
    {
        if (list != null)
        {
            for (StickerInfo info : list)
            {
                if (info != null && info.mStatus == StatusMgr.Type.LOCAL)
                {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isHadLocalRes(int label_index)
    {
        return isHadLocalRes(getStickerInfoArr(label_index));
    }

    public ArrayList<LabelLocalInfo> getLabelInfoArr()
    {
        return mLabelArr;
    }

    public int getLabelArrValidSize()
    {
        return mLabelArr != null ? mLabelArr.size() : 0;
    }

    public ArrayList<StickerInfo> getStickerInfoArr(int label_index)
    {
        LabelLocalInfo label_local_info = getLabelInfoByIndex(label_index);
        if (label_local_info != null)
        {
            return label_local_info.mStickerArr;
        }
        return new ArrayList<>();
    }

    public int getStickerInfoArrSize(int label_index)
    {
        ArrayList<StickerInfo> list = getStickerInfoArr(label_index);
        return list != null ? list.size() : 0;
    }

    public void deleteSelectedSticker(Context context)
    {
        mHasDeleted = true;
        int selected_index = getSelectedLabelIndex();

        ArrayList<StickerInfo> list = getStickerInfoArr(selected_index);
        if (list != null)
        {
            ArrayList<Integer> id_arr = new ArrayList<>();

            ListIterator<StickerInfo> iterator = list.listIterator();
            while (iterator.hasNext())
            {
                StickerInfo info = iterator.next();

                if (info != null && info.mIsInMgrSelected)
                {
                    if (info.mLabelIndexList != null)
                    {
                        for (int index : info.mLabelIndexList)
                        {
                            if (index != selected_index)
                            {
                                ArrayList<StickerInfo> temp_list = getStickerInfoArr(index);
                                if (temp_list != null)
                                {
                                    temp_list.remove(info);
                                    notifyAllDataChange(index);
                                }
                            }
                        }
                    }

                    // 重置预览时 素材状态
                    if (info.id == StickerMgr.getInstance().getSelectedInfo(StickerMgr.SelectedInfoKey.STICKER))
                    {
                        info.mIsSelected = false;
                        StickerMgr.getInstance().updateSelectedInfo(StickerMgr.SelectedInfoKey.STICKER, -1);
                        DUIConfig.getInstance().updateDecorStatusToPC(-1);

                        int old_label_id = StickerMgr.getInstance().getSelectedInfo(StickerMgr.SelectedInfoKey.LABEL);
                        LabelInfo old_label_info = StickerMgr.getInstance().getLabelInfoByID(old_label_id);
                        LabelInfo new_label_info = StickerMgr.getInstance().getLabelInfoByIndex(0);
                        if (old_label_info != null && new_label_info != null && new_label_info.ID != old_label_info.ID)
                        {
                            old_label_info.isSelected = false;
                            new_label_info.isSelected = true;
                            StickerMgr.getInstance().updateSelectedInfo(StickerMgr.SelectedInfoKey.LABEL, new_label_info.ID);
                        }
                    }

                    info.mIsInMgrSelected = false;
                    info.mStatus = StatusMgr.Type.NEED_DOWN_LOAD;
                    info.mProgress = -1;
                    id_arr.add(info.id); // 将需要被删除的 贴纸素材id 收集起来

                    iterator.remove();
                }
            }

            DUIConfig.getInstance().deletedDecor(id_arr);

            // 删掉 贴纸素材 id 组的文件
            int size = id_arr.size();
            int[] ids = new int[size];
            for (int i = 0; i < size; i++)
            {
                ids[i] = id_arr.get(i);
            }

            LiveVideoStickerResMgr2.getInstance().DeleteRes(context, ids);

            notifyAllDataChange(selected_index);

            StickerMgr.getInstance().notifyReflashAllData();
        }
    }

    public void notifyItemDataChanged(StickerInfo info)
    {
        if (info != null && info.mLabelIndexList != null)
        {
            for (int index : info.mLabelIndexList)
            {
                StickerStatusListener helper = getStatusListener(index);
                if (helper != null)
                {
                    helper.OnStatusChange(info.id);
                }
            }
        }
    }

    private void notifyAllDataChange(int label_index)
    {
        StickerStatusListener helper = getStatusListener(label_index);
        if (helper != null)
        {
            helper.OnDeleted();
        }
    }

    public void selectedAllSticker(boolean selected_all)
    {
        ArrayList<StickerInfo> list = getStickerInfoArr(getSelectedLabelIndex());
        if (list != null)
        {
            for (StickerInfo info : list)
            {
                if (info != null && info.mStatus != StatusMgr.Type.BUILT_IN)
                {
                    info.mIsInMgrSelected = selected_all;

                    if (info.mLabelIndexList != null)
                    {
                        for (int index : info.mLabelIndexList)
                        {
                            mLabelIndexes.put(index, index);
                        }
                    }
                }
            }

            notifyPagerViewAllDataChange();
        }
    }

    private void notifyPagerViewAllDataChange()
    {
        if (mLabelIndexes != null)
        {
            int size = mLabelIndexes.size();
            for (int index = 0; index < size; index++)
            {
                notifyAllDataChange(mLabelIndexes.valueAt(index));
            }

            mLabelIndexes.clear();
        }
    }

    private StickerStatusListener getStatusListener(int label_index)
    {
        if (mPagerViewHelperArr != null)
        {
            return mPagerViewHelperArr.get(label_index);
        }
        return null;
    }

    public int getSelectedLabelIndex()
    {
        return mSelectedInfoArr != null ? mSelectedInfoArr.get(0) : 0;
    }

    public void updateSelectedLabelIndex(int index)
    {
        if (mSelectedInfoArr != null)
        {
            mSelectedInfoArr.put(0, index);
        }
    }

    public boolean isAllStickerSelected(int label_index)
    {
        return isAllStickerSelected(getStickerInfoArr(label_index));
    }

    public boolean isAllStickerSelected(ArrayList<StickerInfo> list)
    {
        if (list == null || list.size() == 0) return false;

        for (StickerInfo info : list)
        {
            if (info != null && info.mStatus != StatusMgr.Type.BUILT_IN && !info.mIsInMgrSelected)
            {
                return false;
            }
        }
        return true;
    }

    public boolean isSelectedNone(int label_index)
    {
        return isSelectedNone(getStickerInfoArr(label_index));
    }

    public boolean isSelectedNone(ArrayList<StickerInfo> list)
    {
        if (list == null || list.size() == 0) return false;

        for (StickerInfo info : list)
        {
            if (info != null && info.mIsInMgrSelected)
            {
                return false;
            }
        }

        return true;
    }

    public LabelLocalInfo getLabelInfoByIndex(int label_index)
    {
        if (mLabelArr != null && mLabelArr.size() > 0 && label_index >= 0 && label_index < mLabelArr.size())
        {
            return mLabelArr.get(label_index);
        }
        return null;
    }

    public int getStickerInfoIndexInPagerView(int sticker_ID, int label_index)
    {
        ArrayList<StickerInfo> list = getStickerInfoArr(label_index);
        if (list != null)
        {
            int size = list.size();
            for (int index = 0; index < size; index++)
            {
                StickerInfo info = list.get(index);
                if (info != null && info.id == sticker_ID)
                {
                    return index;
                }
            }
        }
        return -1;
    }

    public void ClearAll()
    {
        mHasDeleted = false;

        mStickerMgrNonDrawable = null;

        if (mPagerViewHelperArr != null)
        {
            mPagerViewHelperArr.clear();
            mPagerViewHelperArr = null;
        }

        if (mLabelIndexes != null)
        {
            mLabelIndexes.clear();
            mLabelIndexes = null;
        }

        if (mSelectedInfoArr != null)
        {
            mSelectedInfoArr.clear();
            mSelectedInfoArr = null;
        }
        resetAllResMgrStatus();
        sInstance = null;
    }

    private void resetAllResMgrStatus()
    {
        if (mLabelArr != null)
        {
            for (LabelLocalInfo info : mLabelArr)
            {
                if (info != null && info.mStickerArr != null)
                {
                    for (StickerInfo sticker : info.mStickerArr)
                    {
                        if (sticker != null)
                        {
                            sticker.mIsInMgrSelected = false;
                        }
                    }
                }
            }
            mLabelArr.clear();
            mLabelArr = null;
        }
    }

    public StickerMgrNonDrawable getStickerMgrNonDrawable()
    {
        return mStickerMgrNonDrawable;
    }
}
