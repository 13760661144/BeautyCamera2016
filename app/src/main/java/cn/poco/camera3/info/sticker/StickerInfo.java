package cn.poco.camera3.info.sticker;

import java.util.ArrayList;

import cn.poco.camera3.info.StickerDownloadAnim;
import cn.poco.camera3.mgr.StatusMgr;
import cn.poco.resource.LimitRes;
import cn.poco.resource.LockRes;

/**
 * 贴纸
 * Created by Gxx on 2017/5/22.
 */

public class StickerInfo
{
    public int id;
    public String mName;
    public int mStatus;
    public Object mThumb = "";
    public Object mRes;
    public LockRes mLockRes;
    public LimitRes mLimitRes;
    public String mShowType;
    public ArrayList<Integer> mLabelIndexList; // 保存 某个标签 的 index（包含该 贴纸素材）
    public boolean mIsMakeup;

    public float mProgress = -1;//下载进度
    public boolean mIsFace;//是否是脸型
    public boolean mIsSelected;//是否选中（非管理页）
    public boolean mIsInMgrSelected;//是否选中（管理页）
    public boolean mHasMusic;//是否有音效
    public boolean mHasLock; //是否有锁
    public boolean mAutoSelected; // 自动下载后，自动选中

    public int mDownloadAnimStatus = StatusMgr.DownloadAnimStatus.IN_IDLE;
    public int mDownloadStatus = StatusMgr.DownloadStatus.IN_IDLE;
    public StickerDownloadAnim mAnim;

    public StickerInfo()
    {
        mLabelIndexList = new ArrayList<>();
    }
}