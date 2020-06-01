package cn.poco.camera3.ui.sticker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.poco.camera3.cb.sticker.StickerInnerListener;
import cn.poco.camera3.config.StickerImageViewConfig;
import cn.poco.camera3.info.sticker.StickerInfo;
import cn.poco.camera3.mgr.StatusMgr;
import cn.poco.camera3.mgr.StickerResMgr;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.dynamicSticker.newSticker.MyHolder;
import cn.poco.resource.LockRes;
import cn.poco.resource.VideoStickerRes;
import cn.poco.resource.VideoStickerResRedDotMgr2;
import cn.poco.utils.Utils;

/**
 * 贴纸
 * Created by Gxx on 2017/10/12.
 */

class StickerItemAdapter extends RecyclerView.Adapter implements View.OnClickListener
{
    private ArrayList<StickerInfo> mData;
    private StickerInnerListener mHelper;

    StickerItemAdapter()
    {
        mData = new ArrayList<>();
    }

    public void ClearAll()
    {
        mHelper = null;
        mData = null;
    }

    void setStickerDataHelper(StickerInnerListener helper)
    {
        mHelper = helper;
    }

    @Override
    public int getItemCount()
    {
        return mData != null ? mData.size() : 0;
    }

    public void setData(ArrayList<StickerInfo> data)
    {
        if (mData != null && data != null)
        {
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
        }
    }

    private StickerInfo getData(int index)
    {
        if (mData != null)
        {
            int size = mData.size();
            if (size > 0 && index >= 0 && index < size)
            {
                return mData.get(index);
            }
        }
        return null;
    }

    private boolean isMakeupSticker(int position)
    {
        StickerInfo data = getData(position);
        return data != null && data.mIsMakeup;
    }

    @Override
    public int getItemViewType(int position)
    {
        return isMakeupSticker(position) ? StickerImageViewConfig.ItemType.MAKEUP_STICKER : StickerImageViewConfig.ItemType.NORMAL_STICKER;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        StickerItemView itemView = new StickerItemView(parent.getContext(), viewType);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(CameraPercentUtil.WidthPxToPercent(108), CameraPercentUtil.WidthPxToPercent(108)));
        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (holder != null && holder instanceof MyHolder)
        {
            MyHolder mh = (MyHolder) holder;
            StickerItemView itemView = mh.getItemView();
            if (itemView != null)
            {
                itemView.setTag(position);
                itemView.setOnClickListener(this);
                itemView.initImageConfig();
                StickerInfo info = getData(position);
                if (info != null)
                {
                    if (info.mAutoSelected && info.mStatus == StatusMgr.Type.LOCAL)
                    {
                        info.mIsSelected = true;
                        itemView.performClick();
                    }

                    // 是否被选中
                    itemView.setIsSelected(info.mIsSelected);

                    // 状态
                    itemView.setStickerStatus(info);

                    // 底图
                    itemView.setThumb(info.mThumb, info.mStatus == StatusMgr.Type.LOCAL);
                }
            }
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder)
    {
        if (holder instanceof MyHolder)
        {
            StickerItemView itemView = (StickerItemView) ((MyHolder) holder).mItemView;
            itemView.ClearAll();
        }
    }

    @Override
    public void onClick(View v)
    {
        if (mHelper != null)
        {
            final int index = (int) v.getTag();
            final StickerInfo info = getData(index);
            if (info != null)
            {
                //商业统计
                checkTrackLink(v.getContext(), (VideoStickerRes) info.mRes);

                //商业流程监控
                clickBusinessTrack(v.getContext(), info.id);

                switch (info.mStatus)
                {
                    case StatusMgr.Type.LIMIT:
                    case StatusMgr.Type.LOCK:
                    case StatusMgr.Type.NEW:
                    case StatusMgr.Type.NEED_DOWN_LOAD:
                    {
                        if (!mHelper.checkNetworkAvailable()) break;

                        if (info.mHasLock)
                        {
                            LockRes lockRes = info.mLockRes;

                            if (lockRes != null)
                            {
                                if (lockRes.m_id == info.id && lockRes.m_shareType != LockRes.SHARE_TYPE_NONE)
                                {
                                    //解锁素材弹出pop
                                    mHelper.popLockView(lockRes);
                                    return;
                                }
                            }
                        }
                        else if (info.mStatus == StatusMgr.Type.NEW)
                        {
                            VideoStickerResRedDotMgr2.getInstance().markResFlag(v.getContext(), info.id);
                        }

                        info.mStatus = StatusMgr.Type.DOWN_LOADING;
                        info.mProgress = 0;
                        StickerResMgr.getInstance().notifyPagerViewDataChange(info);
                        StickerResMgr.getInstance().DownloadRes(info);
                        break;
                    }

                    case StatusMgr.Type.BUILT_IN:
                    case StatusMgr.Type.LOCAL:
                    {
                        mHelper.onSelectedSticker(info);
                    }
                }
            }
        }
    }

    /**
     * 商业触发统计
     *
     * @param videoStickerRes
     */
    public void checkTrackLink(Context context, VideoStickerRes videoStickerRes)
    {
        if (videoStickerRes != null && videoStickerRes.m_tracking_link != null && videoStickerRes.m_tracking_link.startsWith("http"))
        {
            Utils.UrlTrigger(context, videoStickerRes.m_tracking_link);
        }
    }

    public void clickBusinessTrack(Context context, int id)
    {
        /***************** 商业 start ****************/
        if (id == 1993)
        {
            Utils.UrlTrigger(context, "http://cav.adnonstop.com/cav/fe0a01a3d9/0062002946/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
        }
        else if (id == 1992)
        {
            Utils.UrlTrigger(context, "http://cav.adnonstop.com/cav/fe0a01a3d9/0062003087/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
        }
        else if (id == 1994)
        {
            Utils.UrlTrigger(context, "http://cav.adnonstop.com/cav/fe0a01a3d9/0062003088/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
        }
        else if (id == 1995)
        {
            Utils.UrlTrigger(context, "http://cav.adnonstop.com/cav/fe0a01a3d9/0062003089/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
        }
        else if (id == 2534)//悦诗风吟201707
        {
            Utils.UrlTrigger(context, "http://cav.adnonstop.com/cav/fe0a01a3d9/0064603087/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
        }
        else if (id == 2535)//悦诗风吟201707
        {
            Utils.UrlTrigger(context, "http://cav.adnonstop.com/cav/fe0a01a3d9/0064603088/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
        }
        else if (id == 2536)//悦诗风吟201707
        {
            Utils.UrlTrigger(context, "http://cav.adnonstop.com/cav/fe0a01a3d9/0064602946/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
        }
        else if (id == 2364)//植村秀201707
        {
            Utils.UrlTrigger(context, "http://cav.adnonstop.com/cav/1bab1f05d4/0064202946/?url=http://ad.doubleclick.net/ddm/trackclk/N8897.2563106MEIRENAPP-CHINA/B20065905.201613756;dc_trk_aid=401496846;dc_trk_cid=90555961");
        }
        else if (id == 2529)//植村秀201707
        {
            Utils.UrlTrigger(context, "http://cav.adnonstop.com/cav/1bab1f05d4/0064203087/?url=http://ad.doubleclick.net/ddm/trackclk/N8897.2563106MEIRENAPP-CHINA/B20065905.201613756;dc_trk_aid=401496846;dc_trk_cid=90555961");
        }
        else if (id == 2530)//植村秀201707
        {
            Utils.UrlTrigger(context, "http://cav.adnonstop.com/cav/1bab1f05d4/0064203088/?url=http://ad.doubleclick.net/ddm/trackclk/N8897.2563106MEIRENAPP-CHINA/B20065905.201613756;dc_trk_aid=401496846;dc_trk_cid=90555961");
        }
        /***************** 商业 end ****************/
    }
}
