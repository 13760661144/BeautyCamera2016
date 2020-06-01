package cn.poco.camera3.ui.bgm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import cn.poco.camera3.mgr.BgmResWrapper;
import cn.poco.camera3.info.PreviewBgmInfo;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.dynamicSticker.DownloadState;
import cn.poco.dynamicSticker.newSticker.MyHolder;
import cn.poco.resource.AbsDownloadMgr;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.IDownload;
import cn.poco.resource.PreviewBgmRes;
import cn.poco.resource.PreviewBgmResMgr2;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.utils.FileUtil;
import cn.poco.video.AudioStore;
import cn.poco.video.NativeUtils;
import my.beautyCamera.R;

public class BgmAdapter extends RecyclerView.Adapter
{
    private BgmUI.OnBgmUIControlCallback mItemClickListener;

    public void SetOnItemClickListener(BgmUI.OnBgmUIControlCallback callback)
    {
        mItemClickListener = callback;
    }

    // 数据管理
    private BgmResWrapper mBgmResWrapper;

    private Context mContext;

    private boolean mItemClickable = true;

    public BgmAdapter(Context context)
    {
        mContext = context;
        mBgmResWrapper = BgmResWrapper.getInstance();
        mBgmResWrapper.setShowLocal(true);
    }

    public void setItemClickable(boolean able)
    {
        mItemClickable = able;
    }

    @Override
    public int getItemCount()
    {
        return mBgmResWrapper.GetDataSize(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        BgmCircleView itemView = new BgmCircleView(parent.getContext());
        itemView.setId(R.id.bgm_item_view);
        RecyclerView.LayoutParams rlp = new RecyclerView.LayoutParams(CameraPercentUtil.WidthPxToPercent(128), RecyclerView.LayoutParams.MATCH_PARENT);

        itemView.setLayoutParams(rlp);

        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (holder instanceof MyHolder)
        {
            MyHolder mh = (MyHolder) holder;

            BgmCircleView itemView = mh.getItemView();

            itemView.setTag(position);

            itemView.setOnClickListener(mClickListener);

            PreviewBgmInfo info = mBgmResWrapper.GetInfo(mContext, position);

            // 底图
            itemView.setThumb(info.getThumb());

            itemView.setText(info.getName());

            // 是否被选中
            itemView.setIsSelected(info.isIsSel());

            // 选中时缩略图旋转角度
            itemView.setThumbDegree(info.getThumbDegree());

            // 选中时缩略图是否自转
            itemView.setThumbAutoUpdate(info.isIsSel() &&
                    info.getId() != 0 /*&&
                    info.getId() != PreviewBgmResMgr.BGM_INFO_LOCAL_ID &&
                    info.getId() != PreviewBgmResMgr.BMG_INFO_LOCAL_SELECT_ID*/);

            itemView.setDownloadProgress(info.getProgress(), false);

            itemView.setInfoState(info.getState());

            // 是否编辑过音乐
            itemView.setIsClip(info.isClip());
        }
    }

    /**
     * 点击
     */
    private View.OnClickListener mClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (!mItemClickable) return;

            if (v instanceof FrameLayout)
            {
                FrameLayout itemView = (FrameLayout) v;
                int position = (int) itemView.getTag();

                PreviewBgmInfo info = mBgmResWrapper.GetInfo(mContext, position);

                if (info.getId() == PreviewBgmResMgr2.BGM_INFO_NON_ID)
                {
                    MyBeautyStat.onClickByRes(R.string.拍照_视频预览页_主页面_音乐_无);
                    TongJi2.AddCountByRes(v.getContext(), R.integer.拍照_动态贴纸_录像_预览_音乐_无);
                }

                switch (info.getState())
                {
                    case DownloadState.DOWNLOAD_FAILED:
                    {
                        info.setState(DownloadState.NEED_DOWNLOAD);
                        info.setProgress(0);
                    }

                    case DownloadState.NEED_DOWNLOAD:// 需要下载
                    {
                        Object res = info.getEx();
                        if (res != null && res instanceof PreviewBgmRes)
                        {
                            info.setState(DownloadState.WAITTING_FOR_DOWNLOAD);
                            notifyItemChanged(mBgmResWrapper.GetIndexById(mContext, info.getId()));
                            int uid = DownloadRes((IDownload) res, mDownloadCB);
                            info.setDownloadID(uid);
                        }
                        break;
                    }

                    case DownloadState.DOWNLOAD_SUCCESS:// 下载成功
                    {
                        info.setState(DownloadState.HAVE_DOWNLOADED);
                    }

                    case DownloadState.HAVE_DOWNLOADED:// 已下载
                    {
                        if (info.getId() == PreviewBgmResMgr2.BGM_INFO_LOCAL_ID) //本地音乐
                        {
                            mItemClickable = false;
                            if (mItemClickListener != null)
                            {
                                mItemClickListener.onBgmItemClick(info);
                            }
                            return;
                        }

                        if (mBgmResWrapper != null)
                        {
                            int index = mBgmResWrapper.UpdateSelIndex(mContext, position); //已下载的bgm音乐 &　无音乐
                            PreviewBgmInfo previewBgmInfo = mBgmResWrapper.GetInfoById(mContext, PreviewBgmResMgr2.BGM_INFO_LOCAL_ID);
                            if (previewBgmInfo != null && previewBgmInfo.getId() == PreviewBgmResMgr2.BGM_INFO_LOCAL_ID)
                            {
                                previewBgmInfo.setThumb(R.drawable.bgm_local);
                            }

                            if (index != position)
                            {
                                if (info.getId() != PreviewBgmResMgr2.BGM_INFO_NON_ID)
                                {
                                    long duration = (long) (NativeUtils.getDurationFromFile((String) info.getRes()) * 1000L);
                                    info.setDuration(duration);
                                    info.setIsClip(true);
                                }

                                notifyItemChanged(index);
                                notifyItemChanged(position);
                                if (mItemClickListener != null)
                                {
                                    // res == null, 选中 non
                                    mItemClickListener.onBgmItemClick(info);
                                }
                            }
                            else
                            {
                                if (info.getId() == PreviewBgmResMgr2.BGM_INFO_NON_ID)
                                {
                                    return;
                                }

                                if (mItemClickListener != null)
                                {
                                    mItemClickListener.onOpenClipView(info);
                                }
                            }
                        }
                    }
                }
            }
        }
    };

    public void updateAdapterInfo(AudioStore.AudioInfo audioInfo, int info_id)
    {
        if (mBgmResWrapper != null && audioInfo != null)
        {
            int position = mBgmResWrapper.GetIndexById(mContext, info_id);

            //更新专辑封面图片
            Object coverObj = audioInfo.getCoverPath();
            if (TextUtils.isEmpty(audioInfo.getCoverPath()))
            {
                coverObj = R.drawable.music_cover_default;
            }
            // 顺序不能变
            int index = mBgmResWrapper.UpdateSelIndex(mContext, position);
            PreviewBgmInfo previewBgmInfo = mBgmResWrapper.GetInfo(mContext, position);
            if (previewBgmInfo != null)
            {
                previewBgmInfo.setDuration(audioInfo.getDuration());
                previewBgmInfo.setThumb(coverObj);
                previewBgmInfo.setName(audioInfo.getTitle());
                previewBgmInfo.setState(DownloadState.HAVE_DOWNLOADED);
                previewBgmInfo.setResName(audioInfo.getTitle());
                previewBgmInfo.setRes(audioInfo.getPath());
                previewBgmInfo.setIsClip(true);
            }

            if (index != position)
            {
                notifyItemChanged(index);
            }
            notifyItemChanged(position);
        }
    }


    public void insertBgmLocalInfo(AudioStore.AudioInfo audioInfo)
    {
        if (audioInfo != null && mBgmResWrapper != null)
        {
            PreviewBgmInfo info = new PreviewBgmInfo();
            info.setId(PreviewBgmResMgr2.BMG_INFO_LOCAL_SELECT_ID);
            info.setName(audioInfo.getTitle());
            String coverPath = audioInfo.getCoverPath();
            boolean exists = FileUtil.isFileExists(coverPath);
            info.setThumb(exists ? coverPath : R.drawable.music_cover_default);
            info.setRes(audioInfo.getPath());
            info.setState(DownloadState.HAVE_DOWNLOADED);
            info.setResName(audioInfo.getTitle());
            info.setDuration(audioInfo.getDuration());
            info.setIsClip(true);
            info.setIsSel(true);

            int oldSelectIndex = mBgmResWrapper.InsertBgmLocalInfo(mContext, info, 2, true);

            notifyDataSetChanged();
        }
    }

    public void removeBgmLocalInfo()
    {
        if (mBgmResWrapper != null)
        {
            mBgmResWrapper.RemoveBgmLocalInfo(mContext, 2);
        }

        notifyItemRemoved(2);
    }


    /**
     * 下载回调
     */
    private AbsDownloadMgr.Callback mDownloadCB = new AbsDownloadMgr.Callback()
    {
        @Override
        public void OnProgress(int downloadId, IDownload res, int progress)
        {
            if (res instanceof PreviewBgmRes && mContext != null)
            {
                int id = ((PreviewBgmRes) res).m_id;

                int index = mBgmResWrapper.SetInfoDownloadState(mContext, id, progress, DownloadState.DOWNLOADING);
                if (index >= 0)
                {
                    notifyItemChanged(index);
                }
            }
        }

        @Override
        public void OnComplete(int downloadId, IDownload res)
        {
            if (res instanceof PreviewBgmRes && mContext != null)
            {
                int id = ((PreviewBgmRes) res).m_id;

                int index = mBgmResWrapper.SetInfoDownloadState(mContext, id, 0, DownloadState.DOWNLOAD_SUCCESS);
                if (index >= 0)
                {
                    PreviewBgmInfo info = mBgmResWrapper.GetInfo(mContext, index);
//                    info.setThumb(((PreviewBgmRes) res).m_thumb);
                    info.setRes(((PreviewBgmRes) res).m_res);
                    info.setDownloadID(-1);
                    notifyItemChanged(index);
                }
            }
        }

        @Override
        public void OnFail(int downloadId, IDownload res)
        {
            if (res instanceof PreviewBgmRes && mContext != null)
            {
                int id = ((PreviewBgmRes) res).m_id;

                int index = mBgmResWrapper.SetInfoDownloadState(mContext, id, 0, DownloadState.DOWNLOAD_FAILED);
                mBgmResWrapper.GetInfo(mContext, index).setDownloadID(-1);
                if (index >= 0)
                {
                    notifyItemChanged(index);
                }

                if (mItemClickListener != null)
                {
                    mItemClickListener.onDownloadFailed();
                }
            }
        }
    };

    private int DownloadRes(IDownload res, AbsDownloadMgr.Callback downloadCB)
    {
        if (DownloadMgr.getInstance() != null)
        {
            return DownloadMgr.getInstance().DownloadRes(res, downloadCB);
        }
        return -1;
    }

    public void ClearMemory()
    {
        mDownloadCB = null;

        mClickListener = null;

        if (DownloadMgr.getInstance() != null)
        {
            int size = mBgmResWrapper.GetDataSize(mContext);
            for (int i = 0; i < size; i++)
            {
                PreviewBgmInfo info = mBgmResWrapper.GetInfo(mContext, i);
                int uid = info.getDownloadID();
                if (uid == -1) continue;
                DownloadMgr.getInstance().CancelDownload(uid);
            }
        }


        mBgmResWrapper.ClearMemory();

        mContext = null;
        mBgmResWrapper = null;
    }
}
