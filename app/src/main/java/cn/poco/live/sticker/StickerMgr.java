package cn.poco.live.sticker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.animation.DecelerateInterpolator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

import cn.poco.camera3.info.InfoBuilder;
import cn.poco.camera3.info.StickerDownloadAnim;
import cn.poco.camera3.info.sticker.LabelInfo;
import cn.poco.camera3.info.sticker.StickerInfo;
import cn.poco.camera3.mgr.StatusMgr;
import cn.poco.camera3.mgr.TypeMgr;
import cn.poco.framework.MyFramework2App;
import cn.poco.live.dui.DUIConfig;
import cn.poco.resource.AbsDownloadMgr;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.IDownload;
import cn.poco.resource.LiveVideoStickerGroupRes;
import cn.poco.resource.LiveVideoStickerGroupResMgr2;
import cn.poco.resource.LiveVideoStickerRes;
import cn.poco.resource.LiveVideoStickerResMgr2;
import cn.poco.resource.VideoStickerRes;
import my.beautyCamera.R;

/**
 * 直播 贴纸 管理
 * Created by Gxx on 2018/1/15.
 */

public class StickerMgr
{
    private boolean mIsLoadBuildIn;

    @Retention(RetentionPolicy.SOURCE)
    public @interface SelectedInfoKey
    {
        int STICKER = 1 << 2;
        int LABEL = 1 << 4;
    }

    public interface DataListener
    {
        void onStartLoadData();

        void onLoadDataSucceed();

        void onSelectedLabel(int index);

        void onSelectedSticker(Object info);

        void onRefreshAllData();
    }

    public interface StickerStatusListener
    {
        int getIndex();

        void OnStatusChange(int id);

        void OnProgress(int id);

        void OnComplete(int id);

        void OnFail(int id);
    }

    @Retention(RetentionPolicy.SOURCE)
    @interface Msg
    {
        int START_LOAD_DATA = 1 << 5;
        int LOAD_DATA_SUCCEED = 1 << 6;
    }

    private static StickerMgr sInstance;

    // key --> value: "sticker" --> id; "shape" --> id; "label" --> id
    private volatile SparseIntArray mSelectedArr;

    private ArrayList<LabelInfo> mAllLabelsArr; // 全部标签数据
    private ArrayList<StickerInfo> mAllStickersArr; // 全部贴纸数据
    private ArrayList<StickerInfo> mAllBuildInStickersArr; //全部内置贴纸数据

    private ArrayList<StickerInfo> mAllLocalStickerArr; // 全部已下载、内置贴纸数据

    private SparseIntArray mDownloadIDArr;
    private ArrayList<StickerInfo> mDownloadInfoArr;

    private volatile boolean mCancelDownload; // 取消准备下载的贴纸素材
    private volatile boolean mLoadDataSucceed;
    private volatile boolean mCancelLoadData; // 取消load 贴纸素材

    // listener
    private SparseArray<StickerStatusListener> mStatusListenerArr;
    private AbsDownloadMgr.Callback m_download_cb;
    private DataListener mDataListener;
    private Handler mMainHandler;
    private HandlerThread mThread;
    private Handler mThreadHandler;

    public synchronized static StickerMgr getInstance()
    {
        if (sInstance == null)
        {
            sInstance = new StickerMgr();
        }
        return sInstance;
    }

    public static boolean sInstanceIsNull()
    {
        return sInstance == null;
    }

    public boolean isLoadBuildIn()
    {
        return mIsLoadBuildIn;
    }

    public void unregisterStickerStatusListener(StickerStatusListener listener)
    {
        if (mStatusListenerArr == null || listener == null) return;
        mStatusListenerArr.delete(listener.getIndex());
    }

    public void registerStickerStatusListener(StickerStatusListener listener)
    {
        if (mStatusListenerArr == null || listener == null) return;
        mStatusListenerArr.put(listener.getIndex(), listener);
    }

    public void setDataListener(DataListener listener)
    {
        mDataListener = listener;
    }

    public void init(final Context context)
    {
        mCancelLoadData = false;
        mCancelDownload = false;

        initArr();

        initBuildInRes(context);

        mMainHandler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case Msg.LOAD_DATA_SUCCEED:
                    {
                        if (msg.obj != null)
                        {
                            SparseArray params = (SparseArray) msg.obj;
                            ArrayList<StickerInfo> sticker = (ArrayList<StickerInfo>) params.get(0);
                            ArrayList<LabelInfo> label = (ArrayList<LabelInfo>) params.get(1);
                            ArrayList<StickerInfo> localInfoArr = (ArrayList<StickerInfo>) params.get(2);

                            if (sInstance != null)
                            {
                                mAllStickersArr = sticker;
                                mAllLabelsArr = label;
                                mAllLocalStickerArr = localInfoArr;
                                mLoadDataSucceed = true;
                                notifyLoadDataSucceed();
                            }
                        }
                        if (mThread != null)
                        {
                            mThread.quit();
                            mThread = null;
                        }
                        if (mThreadHandler != null)
                        {
                            mThreadHandler = null;
                        }
                        break;
                    }
                }
            }
        };

        mThread = new HandlerThread("load_data_thread");
        mThread.start();

        mThreadHandler = new Handler(mThread.getLooper(), new Handler.Callback()
        {
            @Override
            public boolean handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case Msg.START_LOAD_DATA:
                    {
                        if (msg.obj != null && msg.obj instanceof HashMap)
                        {
                            HashMap<String, Object> params = (HashMap<String, Object>) msg.obj;
                            if (params.containsKey("label"))
                            {
                                ArrayList<LabelInfo> label_list = (ArrayList<LabelInfo>) params.get("label");
                                initAllSticker(context, label_list);
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });

        m_download_cb = new AbsDownloadMgr.Callback()
        {
            @Override
            public void OnProgress(int downloadId, IDownload res, int progress)
            {
                if (res != null && res instanceof VideoStickerRes)
                {
                    VideoStickerRes stickerRes = (VideoStickerRes) res;
                    StickerInfo info = getStickerInfoByID(stickerRes.m_id);
                    if (info != null && info.mDownloadAnimStatus == StatusMgr.DownloadAnimStatus.IN_IDLE)
                    {
                        info.mDownloadStatus = StatusMgr.DownloadStatus.ING;
                        info.mDownloadAnimStatus = StatusMgr.DownloadAnimStatus.START;
                        startDownloadAnim(info, 0, 96, 15000);
                    }
                }
            }

            @Override
            public void OnComplete(int downloadId, IDownload res)
            {
                if (mDownloadIDArr != null)
                {
                    mDownloadIDArr.delete(downloadId);
                }

                if (res != null && res instanceof VideoStickerRes)
                {
                    VideoStickerRes stickerRes = (VideoStickerRes) res;
                    StickerInfo info = getStickerInfoByID(stickerRes.m_id);
                    if (info != null)
                    {
                        if (mDownloadInfoArr != null)
                        {
                            mDownloadInfoArr.remove(info);
                        }
                        info.mDownloadStatus = StatusMgr.DownloadStatus.SUCCEED;
                        if (info.mDownloadAnimStatus == StatusMgr.DownloadAnimStatus.END || info.mDownloadAnimStatus == StatusMgr.DownloadAnimStatus.IN_IDLE)
                        {
                            info.mProgress = -1;
                            info.mStatus = StatusMgr.Type.LOCAL;
                            info.mDownloadStatus = StatusMgr.DownloadStatus.IN_IDLE;
                            info.mDownloadAnimStatus = StatusMgr.DownloadAnimStatus.IN_IDLE;
                            for (int index : info.mLabelIndexList)
                            {
                                StickerStatusListener listener = getStatusListener(index);
                                if (listener != null)
                                {
                                    listener.OnComplete(info.id);
                                }
                            }
                            DUIConfig.getInstance().insertDecorToPC(info);
                        }
                    }
                }
            }

            @Override
            public void OnFail(int downloadId, IDownload res)
            {
                if (mDownloadIDArr != null)
                {
                    mDownloadIDArr.delete(downloadId);
                }

                if (res != null && res instanceof VideoStickerRes)
                {
                    VideoStickerRes stickerRes = (VideoStickerRes) res;
                    StickerInfo info = getStickerInfoByID(stickerRes.m_id);
                    if (info != null)
                    {
                        if (mDownloadInfoArr != null)
                        {
                            mDownloadInfoArr.remove(info);
                        }
                        info.mDownloadStatus = StatusMgr.DownloadStatus.FAILED;
                        if (info.mDownloadAnimStatus == StatusMgr.DownloadAnimStatus.END || info.mDownloadAnimStatus == StatusMgr.DownloadAnimStatus.IN_IDLE)
                        {
                            info.mProgress = -1;
                            info.mStatus = StatusMgr.Type.NEED_DOWN_LOAD;
                            info.mDownloadStatus = StatusMgr.DownloadStatus.IN_IDLE;
                            info.mDownloadAnimStatus = StatusMgr.DownloadAnimStatus.IN_IDLE;
                            for (int index : info.mLabelIndexList)
                            {
                                StickerStatusListener listener = getStatusListener(index);
                                if (listener != null)
                                {
                                    listener.OnFail(info.id);
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    private void sendMsgLoadSucceed(SparseArray<Object> params)
    {
        if (mMainHandler != null)
        {
            Message msg = new Message();
            msg.what = Msg.LOAD_DATA_SUCCEED;
            msg.obj = params;
            mMainHandler.sendMessage(msg);
        }
    }

    private void sendMsgToLoadData()
    {
        if (mThreadHandler != null)
        {
            if (mAllLabelsArr != null)
            {
                ArrayList<LabelInfo> arr = new ArrayList<>();
                for (LabelInfo info : mAllLabelsArr)
                {
                    if (info != null)
                    {
                        LabelInfo newInfo = new LabelInfo();
                        newInfo.set(info);
                        arr.add(newInfo);
                    }
                }
                HashMap<String, Object> params = new HashMap<>();
                params.put("label", arr);
                Message msg = mThreadHandler.obtainMessage();
                msg.what = Msg.START_LOAD_DATA;
                msg.obj = params;
                mThreadHandler.sendMessage(msg);
            }
        }
    }

    private void initBuildInRes(Context context)
    {
        if (mAllBuildInStickersArr == null)
        {
            mAllBuildInStickersArr = new ArrayList<>();
        }

        ArrayList<LiveVideoStickerRes> local_list = LiveVideoStickerResMgr2.getInstance().sync_GetLocalRes(MyFramework2App.getInstance().getApplicationContext(), null);

        for (LiveVideoStickerRes res : local_list)
        {
            StickerInfo local_info = InfoBuilder.BuildLiveStickerInfo(context, res, true);
            mAllBuildInStickersArr.add(local_info);
        }
    }

    private ValueAnimator.AnimatorUpdateListener mDownLoadAnimUpdateListener = new ValueAnimator.AnimatorUpdateListener()
    {
        @Override
        public void onAnimationUpdate(ValueAnimator animation)
        {
            if (animation instanceof StickerDownloadAnim)
            {
                StickerInfo info = (StickerInfo) ((StickerDownloadAnim) animation).getRes();

                if (mCancelDownload && info != null)
                {
                    info.mAnim.setRes(null);
                    info.mAnim.removeAllListeners();
                    info.mAnim.removeAllUpdateListeners();
                    info.mAnim.cancel();
                    info.mAnim = null;
                    return;
                }

                if (info != null)
                {
                    float value = (float) animation.getAnimatedValue();

                    switch (info.mDownloadStatus)
                    {
                        case StatusMgr.DownloadStatus.SUCCEED:
                        {
                            info.mDownloadStatus = StatusMgr.DownloadStatus.IN_IDLE;
                            info.mAnim.setRes(null);
                            info.mAnim.removeAllListeners();
                            info.mAnim.removeAllUpdateListeners();
                            info.mAnim.cancel();
                            info.mAnim = null;
                            startDownloadAnim(info, value, 100, (long) ((100 - value) * (1500f / 100)));
                            return;
                        }

                        case StatusMgr.DownloadStatus.FAILED:
                        {
                            info.mProgress = -1;
                            info.mStatus = StatusMgr.Type.NEED_DOWN_LOAD;
                            info.mDownloadStatus = StatusMgr.DownloadStatus.IN_IDLE;
                            info.mDownloadAnimStatus = StatusMgr.DownloadAnimStatus.IN_IDLE;

                            info.mAnim.setRes(null);
                            info.mAnim.removeAllListeners();
                            info.mAnim.removeAllUpdateListeners();
                            animation.cancel();
                            info.mAnim = null;
                            break;
                        }

                        default:
                        {
                            info.mProgress = value;
                            break;
                        }
                    }

                    for (int index : info.mLabelIndexList)
                    {
                        StickerStatusListener listener = getStatusListener(index);
                        if (listener != null)
                        {
                            listener.OnProgress(info.id);
                        }
                    }
                }
            }
        }
    };

    private AnimatorListenerAdapter mDownLoadAnimListener = new AnimatorListenerAdapter()
    {
        @Override
        public void onAnimationEnd(Animator animation)
        {
            if (animation != null && animation instanceof StickerDownloadAnim)
            {
                StickerInfo info = (StickerInfo) ((StickerDownloadAnim) animation).getRes();

                if (mCancelDownload && info != null)
                {
                    info.mAnim.setRes(null);
                    info.mAnim.removeAllUpdateListeners();
                    info.mAnim.removeAllListeners();
                    info.mAnim.cancel();
                    info.mAnim = null;
                    return;
                }

                if (info != null)
                {
                    info.mDownloadAnimStatus = StatusMgr.DownloadAnimStatus.END;
                    info.mAnim.removeAllUpdateListeners();
                    info.mAnim.removeAllListeners();
                    info.mAnim.setRes(null);
                    info.mAnim = null;

                    switch (info.mDownloadStatus)
                    {
                        case StatusMgr.DownloadStatus.IN_IDLE:
                        case StatusMgr.DownloadStatus.SUCCEED:
                        {
                            info.mProgress = -1;
                            info.mStatus = StatusMgr.Type.LOCAL;
                            info.mDownloadStatus = StatusMgr.DownloadStatus.IN_IDLE;
                            info.mDownloadAnimStatus = StatusMgr.DownloadAnimStatus.IN_IDLE;
                            for (int index : info.mLabelIndexList)
                            {
                                StickerStatusListener listener = getStatusListener(index);
                                if (listener != null)
                                {
                                    listener.OnComplete(info.id);
                                }
                            }
                            DUIConfig.getInstance().insertDecorToPC(info);
                            break;
                        }

                        case StatusMgr.DownloadStatus.FAILED:
                        {
                            info.mProgress = -1;
                            info.mStatus = StatusMgr.Type.NEED_DOWN_LOAD;
                            info.mDownloadStatus = StatusMgr.DownloadStatus.IN_IDLE;
                            info.mDownloadAnimStatus = StatusMgr.DownloadAnimStatus.IN_IDLE;
                            for (int index : info.mLabelIndexList)
                            {
                                StickerStatusListener listener = getStatusListener(index);
                                if (listener != null)
                                {
                                    listener.OnFail(info.id);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
    };

    private void startDownloadAnim(StickerInfo info, float start, float end, long duration)
    {
        if (info != null)
        {
            info.mAnim = new StickerDownloadAnim();
            info.mAnim.setRes(info);
            info.mAnim.setDuration(duration);
            info.mAnim.setFloatValues(start, end);
            info.mAnim.setInterpolator(new DecelerateInterpolator());

            info.mAnim.addListener(mDownLoadAnimListener);
            info.mAnim.addUpdateListener(mDownLoadAnimUpdateListener);
            info.mAnim.start();
        }
    }

    public void DownloadRes(StickerInfo info)
    {
        if (info != null)
        {
            int download_id = DownloadMgr.getInstance().DownloadRes((IDownload) info.mRes, m_download_cb);
            if (mDownloadIDArr != null)
            {
                mDownloadIDArr.put(download_id, download_id);
            }
            if (mDownloadInfoArr != null)
            {
                mDownloadInfoArr.add(info);
            }
        }
    }

    public void CancelDownload()
    {
        if(mDownloadInfoArr != null)
        {
            Iterator<StickerInfo> iterator = mDownloadInfoArr.iterator();
            while (iterator.hasNext())
            {
                StickerInfo info = iterator.next();
                if (info != null)
                {
                    info.mStatus = StatusMgr.Type.NEED_DOWN_LOAD;
                    info.mProgress = -1;
                    notifyPagerViewDataChange(info);
                    iterator.remove();
                }
            }
        }

        if (mDownloadIDArr != null)
        {
            int size = mDownloadIDArr.size();
            for (int index = 0; index < size; index++)
            {
                DownloadMgr.getInstance().CancelDownload(mDownloadIDArr.valueAt(index));
            }
        }
    }

    private void initArr()
    {
        if (mAllLocalStickerArr == null)
        {
            mAllLocalStickerArr = new ArrayList<>();
        }

        if (mDownloadInfoArr == null)
        {
            mDownloadInfoArr = new ArrayList<>();
        }

        if (mSelectedArr == null)
        {
            mSelectedArr = new SparseIntArray();
        }

        if (mStatusListenerArr == null)
        {
            mStatusListenerArr = new SparseArray<>();
        }

        if (mAllLabelsArr == null)
        {
            mAllLabelsArr = new ArrayList<>();
        }

        if (mAllStickersArr == null)
        {
            mAllStickersArr = new ArrayList<>();
        }

        if (mDownloadIDArr == null)
        {
            mDownloadIDArr = new SparseIntArray();
        }
    }

    public ArrayList<LabelInfo> getLabelInfoArr(Context context)
    {
        return getLabelInfoArr(context, true);
    }

    /**
     * @return 贴纸素材标签
     */
    public ArrayList<LabelInfo> getLabelInfoArr(final Context context, boolean hasMgrLogo)
    {
        if (mAllLabelsArr != null && mAllLabelsArr.size() > 0)
        {
            ArrayList<LabelInfo> out = new ArrayList<>(mAllLabelsArr);
            if (hasMgrLogo)
            {
                out.add(buildMgrTabInfo(context));
            }

            return out;
        }

        // 每次进直播页，默认选中无
        updateSelectedInfo(SelectedInfoKey.STICKER, -1);

        ArrayList<LiveVideoStickerGroupRes> source = LiveVideoStickerGroupResMgr2.getInstance().getCloudDownloadRes(context, false, null);

        if (source != null && source.size() > 0)
        {
            initAllLabelInfo(context, source);

            // 初始化贴纸数据
            sendMsgToLoadData();
        }
        else
        {
            // 内置
            initLocalLabel(context);
        }

        ArrayList<LabelInfo> out = null;

        if (mAllLabelsArr != null)
        {
            out = new ArrayList<>(mAllLabelsArr);
            if (hasMgrLogo)
            {
                out.add(buildMgrTabInfo(context));
            }

            if (mAllBuildInStickersArr != null)
            {
                mAllBuildInStickersArr.clear();
                mAllBuildInStickersArr = null;
            }
        }

        return out;
    }

    private boolean checkQuitThread()
    {
        return mCancelLoadData;
    }

    private void initAllSticker(Context context, ArrayList<LabelInfo> labelArr)
    {
        if (checkQuitThread() || labelArr == null || labelArr.size() < 1) return;

        ArrayList<StickerInfo> out = new ArrayList<>();

        ArrayList<StickerInfo> localInfoArr = new ArrayList<>();

        int label_size = labelArr.size();

        ArrayList<LiveVideoStickerRes> resList = LiveVideoStickerResMgr2.getInstance().GetResArr(context);

        if (resList != null)
        {
            for (LiveVideoStickerRes res : resList)
            {
                if (checkQuitThread()) return;

                StickerInfo info = InfoBuilder.BuildLiveStickerInfo(context, res, false);

                if (info != null)
                {
                    out.add(info);
                }
            }
        }

        for (int index = 0; index < label_size; index++)// 遍历标签
        {
            if (checkQuitThread()) return; // 检查是否不需要继续

            LabelInfo labelInfo = labelArr.get(index);

            if (labelInfo != null)
            {
                if (checkQuitThread()) return;

                int[] ids = labelInfo.mStickerIDArr;

                if (ids != null)
                {
                    if (checkQuitThread()) return;

                    for (int id : ids)// 遍历每个标签的 id 数组
                    {
                        if (checkQuitThread()) return;// 检查是否不需要继续

                        for (StickerInfo info : out) // 遍历全部贴纸素材
                        {
                            if (checkQuitThread()) return;

                            if (info != null && info.id == id && labelInfo.mStickerArr != null)// 根据贴纸 id 进行匹配
                            {
                                labelInfo.mStickerArr.add(info);
                                info.mLabelIndexList.add(index);

                                if (info.mStatus == StatusMgr.Type.BUILT_IN || info.mStatus == StatusMgr.Type.LOCAL)
                                {
                                    if (!localInfoArr.contains(info))
                                    {
                                        localInfoArr.add(info);
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }

        SparseArray<Object> params = new SparseArray<>();
        params.put(0, out);// 全部stickerArr
        params.put(1, labelArr); // 更新后的labelArr
        params.put(2, localInfoArr);

        sendMsgLoadSucceed(params);
    }

    private void initAllLabelInfo(Context context, ArrayList<LiveVideoStickerGroupRes> source)
    {
        int size = source.size();

        for (int index = 0; index < size; index++)
        {
            LiveVideoStickerGroupRes res = source.get(index);
            if (res != null)
            {
                LabelInfo label_info = InfoBuilder.BuildLiveLabelInfo(context, res);
                label_info.mIndex = index;
                label_info.mStickerArr = new ArrayList<>();
                if (mAllLabelsArr != null)
                {
                    mAllLabelsArr.add(label_info);
                }
            }
        }

        // 默认选中第一个 lab
        if (mAllLabelsArr != null && mAllLabelsArr.size() > 0)
        {
            LabelInfo info = mAllLabelsArr.get(0);
            if (info != null)
            {
                info.isSelected = true;
                updateSelectedInfo(SelectedInfoKey.LABEL, info.ID);
            }
        }
    }

    private void initLocalLabel(Context context)
    {
        mIsLoadBuildIn = true;

        LabelInfo label_info = new LabelInfo();
        label_info.mType = TypeMgr.StickerLabelType.HOT;
        label_info.ID = LabelInfo.BUILT_IN_LABEL_HOT_ID;
        label_info.isSelected = true;
        label_info.mLabelName = "HOT";
        label_info.mIndex = 0;
        label_info.mStickerArr = new ArrayList<>();

        if (mSelectedArr != null)
        {
            mSelectedArr.put(SelectedInfoKey.LABEL, LabelInfo.BUILT_IN_LABEL_HOT_ID);
        }

        ArrayList<LiveVideoStickerRes> list = LiveVideoStickerResMgr2.getInstance().GetAllLocalRes();

        for (LiveVideoStickerRes res : list)
        {
            if (res != null)
            {
                StickerInfo build_in_info = InfoBuilder.BuildLiveStickerInfo(context, res, true);
                build_in_info.mLabelIndexList.add(label_info.mIndex);
                mAllStickersArr.add(build_in_info);
                mAllLocalStickerArr.add(build_in_info);

                if (label_info.mStickerArr != null)
                {
                    label_info.mStickerArr.add(build_in_info);
                }
            }
        }

        if (mAllLabelsArr != null)
        {
            mAllLabelsArr.add(label_info);
        }
        mLoadDataSucceed = true;
    }

    /**
     * 修改之前 素材 的选中状态
     *
     * @return 被选中贴纸的第一个 标签 下标
     */
    public int modifyPreviousSelected(StickerInfo info)
    {
        int index = 0;

        int last_sticker_selected_id = getSelectedInfo(SelectedInfoKey.STICKER);

        StickerInfo last_sticker_selected_info = getStickerInfoByID(last_sticker_selected_id);

        if(last_sticker_selected_info != null)
        {
            last_sticker_selected_info.mIsSelected = false;
            notifyPagerViewDataChange(last_sticker_selected_info);
        }

        if (info != null)
        {
            updateSelectedInfo(SelectedInfoKey.STICKER, info.id);// 更新选中的素材 id
            if (info.mLabelIndexList != null && info.mLabelIndexList.size() > 0)
            {
                index = info.mLabelIndexList.get(0);
            }
            info.mIsSelected = true;
            notifyPagerViewDataChange(info);
        }
        else
        {
            updateSelectedInfo(SelectedInfoKey.STICKER, -1);// 更新选中的素材 id
        }

        return index;
    }

    /**
     * 更新被选中的信息 <br/>
     * key --> value: "sticker" --> id; "shape" --> id; "label" --> id
     */
    public void updateSelectedInfo(@SelectedInfoKey int key, int value)
    {
        if (mSelectedArr != null)
        {
            mSelectedArr.put(key, value);
        }
    }

    @NonNull
    private LabelInfo buildMgrTabInfo(Context context)
    {
        LabelInfo info = new LabelInfo();
        info.mIndex = mAllLabelsArr.size();
        info.mType = TypeMgr.StickerLabelType.MANAGER;//管理标签
        info.mLabelName = context.getString(R.string.sticker_pager_manager);
        return info;
    }

    private void cancelLoadData()
    {
        mCancelLoadData = true;
        mCancelDownload = true;

        if (mThread != null)
        {
            mThread.quit();
            mThread = null;
        }
        if (mThreadHandler != null)
        {
            mThreadHandler.removeMessages(Msg.START_LOAD_DATA);
            mThreadHandler = null;
        }
    }

    public void notifyReflashAllData()
    {
        if (mDataListener != null)
        {
            mDataListener.onRefreshAllData();
        }
    }

    private void notifyLoadDataSucceed()
    {
        if (mDataListener != null)
        {
            mDataListener.onLoadDataSucceed();
        }
    }

    /**
     * 修改数据之后，一定要调用通知邻近的 StickerPagerView 刷新 ui
     */
    public void notifyPagerViewDataChange(StickerInfo... info_list)
    {
        if (info_list != null)
        {
            for (StickerInfo info : info_list)
            {
                if (info != null)
                {
                    for (int index : info.mLabelIndexList)
                    {
                        StickerStatusListener listener = getStatusListener(index);
                        if (listener != null)
                        {
                            listener.OnStatusChange(info.id);
                        }
                    }
                }
            }
        }
    }

    public int getSelectedInfo(int key)
    {
        if (mSelectedArr != null)
        {
            return mSelectedArr.get(key);
        }
        return -1;
    }

    public LabelInfo getLabelInfoByID(int label_id)
    {
        if (mAllLabelsArr != null)
        {
            for (LabelInfo info : mAllLabelsArr)
            {
                if (info != null && info.ID == label_id)
                {
                    return info;
                }
            }
        }
        return null;
    }

    public LabelInfo getLabelInfoByIndex(int label_index)
    {
        if (mAllLabelsArr != null && mAllLabelsArr.size() > 0 && label_index >= 0 && label_index < mAllLabelsArr.size())
        {
            return mAllLabelsArr.get(label_index);
        }
        return null;
    }

    public ArrayList<StickerInfo> getLocalStickerArr()
    {
        return mAllLocalStickerArr != null ? new ArrayList<>(mAllLocalStickerArr) : new ArrayList<StickerInfo>();
    }

    public ArrayList<StickerInfo> getStickerInfoArr(int label_index)
    {
        if (mLoadDataSucceed)
        {
            LabelInfo label_info = getLabelInfoByIndex(label_index);

            if (label_info != null)
            {
                return label_info.mStickerArr;
            }
        }

        LabelInfo info = getLabelInfoByIndex(label_index);
        return InfoBuilder.BuildEmptyStickerInfoList(info);
    }

    public StickerInfo getStickerInfoByID(int sticker_id)
    {
        if (mAllStickersArr != null)
        {
            for (StickerInfo info : mAllStickersArr)
            {
                if (info.id == sticker_id)
                {
                    return info;
                }
            }
        }
        return null;
    }

    public int getStickerIndexInView(int sticker_ID, int label_index)
    {
        LabelInfo label_info = getLabelInfoByIndex(label_index);
        if (label_info != null)
        {
            return getStickerIndexInView(sticker_ID, label_info.mStickerArr);
        }
        return -1;
    }

    private int getStickerIndexInView(int sticker_ID, ArrayList<StickerInfo> list)
    {
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

    private StickerStatusListener getStatusListener(int label_index)
    {
        if (mStatusListenerArr != null)
        {
            return mStatusListenerArr.get(label_index);
        }
        return null;
    }

    public void clearAllSelectedInfo()
    {
        int sticker_id = mSelectedArr.get(SelectedInfoKey.STICKER);

        if (sticker_id == -1)
        {
            return;
        }

        StickerInfo sticker_info = getStickerInfoByID(sticker_id);

        if (sticker_info != null)
        {
            sticker_info.mIsSelected = false;
        }

        updateSelectedInfo(SelectedInfoKey.STICKER, -1);

        notifyPagerViewDataChange(sticker_info);
    }

    private void clearAllData()
    {
        // selected
        if (mSelectedArr != null)
        {
            mSelectedArr.clear();
            mSelectedArr = null;
        }

        // label
        if (mAllLabelsArr != null)
        {
            ListIterator<LabelInfo> iterator = mAllLabelsArr.listIterator();
            while (iterator.hasNext())
            {
                LabelInfo info = iterator.next();

                if (info != null)
                {
                    if (info.mStickerArr != null)
                    {
                        info.mStickerArr.clear();
                        info.mStickerArr = null;
                    }
                }
                iterator.remove();
            }
            mAllLabelsArr = null;
        }

        // sticker
        if (mAllStickersArr != null)
        {
            mAllStickersArr.clear();
            mAllStickersArr = null;
        }

        if (mDownloadInfoArr != null)
        {
            mDownloadInfoArr.clear();
            mDownloadInfoArr = null;
        }

        if (mDownloadIDArr != null)
        {
            mDownloadIDArr.clear();
            mDownloadIDArr = null;
        }
    }

    public void ClearAll()
    {
        m_download_cb = null;

        mDataListener = null;

        if (mStatusListenerArr != null)
        {
            mStatusListenerArr.clear();
            mStatusListenerArr = null;
        }

        cancelLoadData();
        CancelDownload();
        clearAllData();

        if (mMainHandler != null)
        {
            mMainHandler.removeMessages(Msg.LOAD_DATA_SUCCEED);
            mMainHandler = null;
        }

        mLoadDataSucceed = false;

        mIsLoadBuildIn = false;

        sInstance = null;
    }
}
