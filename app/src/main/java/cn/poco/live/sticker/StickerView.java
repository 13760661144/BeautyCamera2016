package cn.poco.live.sticker;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.poco.camera3.config.StickerImageViewConfig;
import cn.poco.camera3.info.sticker.LabelInfo;
import cn.poco.camera3.info.sticker.StickerInfo;
import cn.poco.camera3.ui.drawable.StickerBKDrawable;
import cn.poco.camera3.ui.sticker.StickerZipParseHelper;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.live.LivePageListener;
import cn.poco.live.dui.DUIConfig;
import cn.poco.resource.LiveVideoStickerGroupResRedDotMrg2;
import cn.poco.resource.VideoStickerRes;
import my.beautyCamera.R;

import static android.os.Looper.myQueue;

/**
 * 直播 贴纸素材
 * Created by Gxx on 2018/1/15.
 */

public class StickerView extends LinearLayout implements StickerMgr.DataListener, View.OnClickListener
{
    private FrameLayout mStickerSoundFr;
    private ImageView mStickerFoldView;
    private FrameLayout mNonView;
    private RecyclerView mTabView;
    private ViewPager mViewPager;
    private LivePageListener mPageListener;

    private int MSG_PARSE;
    private HandlerThread mHandlerThread;
    private StickerZipParseHelper.ParseHandler mStickerHandler;
    private Handler mUiHandler;
    private ViewPager.OnPageChangeListener mPagerChangeListener;

    private boolean mHasLoadData;// 是否初始化了贴纸数据

    public StickerView(@NonNull Context context)
    {
        super(context);
        setOrientation(VERTICAL);
        StickerImageViewConfig.init(context);
        StickerMgr.getInstance().init(context);
        StickerMgr.getInstance().setDataListener(this);
        initHandler(context);
        initView(context);
    }

    private void initHandler(Context context)
    {
        mPagerChangeListener = new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position)
            {
                if (mTabView != null)
                {
                    RecyclerView.Adapter adapter = mTabView.getAdapter();
                    if (adapter != null)
                    {
                        int labelSize = adapter.getItemCount();
                        if (labelSize > 0 && position >= 0 && position < labelSize)
                        {
                            LabelInfo info = StickerMgr.getInstance().getLabelInfoByIndex(position);
                            if (info != null)
                            {
                                info.isSelected = true;
                                if (info.isShowRedPoint)
                                {
                                    info.isShowRedPoint = false;
                                    LiveVideoStickerGroupResRedDotMrg2.getInstance().markResFlag(getContext(), info.ID);
                                }
                                int id = info.ID;
                                int old_sel_id = StickerMgr.getInstance().getSelectedInfo(StickerMgr.SelectedInfoKey.LABEL);
                                if (old_sel_id != id)
                                {
                                    StickerMgr.getInstance().updateSelectedInfo(StickerMgr.SelectedInfoKey.LABEL, id);
                                    info = StickerMgr.getInstance().getLabelInfoByID(old_sel_id);
                                    if (info != null)
                                    {
                                        info.isSelected = false;
                                        adapter.notifyItemChanged(info.mIndex);
                                    }
                                }
                                adapter.notifyItemChanged(position);
                                scrollLabelToCenter(position);
                            }
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        };

        mUiHandler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what == MSG_PARSE)
                {
                    StickerZipParseHelper.ParseHandler.ParseObj obj = (StickerZipParseHelper.ParseHandler.ParseObj)msg.obj;
                    VideoStickerRes res = obj.res;
                    // 先回调再缓存
                    if(mPageListener != null)
                    {
                        mPageListener.OnSelectedSticker(res);
                    }
                    StickerZipParseHelper.addCache(res);
                }
            }
        };
        MSG_PARSE = StickerZipParseHelper.ParseHandler.MSG_PARSE;
        mHandlerThread = new HandlerThread("sticker_handler_thread");
        mHandlerThread.start();
        mStickerHandler = new StickerZipParseHelper.ParseHandler(mHandlerThread.getLooper(), context, mUiHandler);
    }

    public void ClearAll()
    {
        mPageListener = null;

        removeAllViews();

        StickerZipParseHelper.clearAll();
    }

    public boolean hasLoadData()
    {
        return mHasLoadData;
    }

    private void initView(Context context)
    {
        mStickerSoundFr = new FrameLayout(context);
        mStickerSoundFr.setOnClickListener(this);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        addView(mStickerSoundFr, llp);
        {
            //收缩素材icon
            mStickerFoldView = new ImageView(getContext());
            mStickerFoldView.setImageResource(R.drawable.sticker_list_fold_gray);
            mStickerFoldView.setScaleType(ImageView.ScaleType.CENTER);
            mStickerFoldView.setOnClickListener(this);
            FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            fp.gravity = Gravity.END | Gravity.BOTTOM;
            fp.rightMargin = CameraPercentUtil.WidthPxToPercent(14);
            fp.bottomMargin = CameraPercentUtil.WidthPxToPercent(18);
            mStickerSoundFr.addView(mStickerFoldView, fp);
        }

        LinearLayout content = new LinearLayout(context);
        content.setBackgroundDrawable(new StickerBKDrawable());
        content.setOrientation(VERTICAL);
        llp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        addView(content, llp);
        {
            LinearLayout top = new LinearLayout(context);
            top.setOrientation(LinearLayout.HORIZONTAL);
            llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.WidthPxToPercent(80));
            content.addView(top, llp);
            {
                mNonView = new FrameLayout(context);
                mNonView.setOnClickListener(this);
                llp = new LinearLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(132), FrameLayout.LayoutParams.MATCH_PARENT);
                top.addView(mNonView, llp);
                {
                    // 空标签
                    ImageView icon = new ImageView(context);
                    icon.setImageResource(R.drawable.sticker_non_white);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.CENTER_VERTICAL;
                    params.leftMargin = CameraPercentUtil.WidthPxToPercent(32);
                    mNonView.addView(icon, params);

                    TextView text = new TextView(context);
                    text.setTextColor(Color.WHITE);
                    text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
                    text.setText("无");
                    params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.CENTER_VERTICAL;
                    params.leftMargin = CameraPercentUtil.WidthPxToPercent(32 + 30 + 12);
                    mNonView.addView(text, params);
                }

                mTabView = new RecyclerView(context);
                mTabView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                mTabView.setOverScrollMode(OVER_SCROLL_NEVER);
                ((SimpleItemAnimator) mTabView.getItemAnimator()).setSupportsChangeAnimations(false);
                mTabView.getItemAnimator().setChangeDuration(0);
                llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                top.addView(mTabView, llp);
            }

            mViewPager = new ViewPager(context);
            mViewPager.addOnPageChangeListener(mPagerChangeListener);
            llp = new LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, CameraPercentUtil.WidthPxToPercent(380));
            content.addView(mViewPager, llp);
        }
    }

    @Override
    public void onStartLoadData()
    {
        ArrayList<LabelInfo> infoArr = StickerMgr.getInstance().getLabelInfoArr(getContext());
        if (infoArr != null)
        {
            LabelAdapter adapter = new LabelAdapter();
            adapter.setDataListener(this);
            adapter.setData(infoArr);
            mTabView.setAdapter(adapter);

            StickerPagerAdapter pagerAdapter = new StickerPagerAdapter();
            pagerAdapter.setStickerDataListener(this);
            pagerAdapter.setSize(infoArr.size() - 1);
            mViewPager.setAdapter(pagerAdapter);
        }

        if (StickerMgr.getInstance().isLoadBuildIn())
        {
            onLoadDataSucceed();
        }
    }

    @Override
    public void onLoadDataSucceed()
    {
        DUIConfig.getInstance().initDecorToPC(StickerMgr.getInstance().getLocalStickerArr());

        if (mTabView != null)
        {
            LabelAdapter adapter = (LabelAdapter) mTabView.getAdapter();
            if (adapter != null)
            {
                ArrayList<LabelInfo> infoArr = StickerMgr.getInstance().getLabelInfoArr(getContext());
                adapter.setData(infoArr);
            }
        }

        notifyViewPagerChildUpdateData();

        onSelectedSticker(null);

        mHasLoadData = true;
    }

    private void notifyViewPagerChildUpdateData()
    {
        if (mViewPager != null)
        {
            int size = mViewPager.getChildCount();
            for (int index = 0; index < size; index++)
            {
                View child = mViewPager.getChildAt(index);
                if (child != null && child instanceof StickerPagerView)
                {
                    ((StickerPagerView) child).updateData();
                }
            }
        }
    }

    @Override
    public void onSelectedLabel(int index)
    {
        if (mViewPager != null)
        {
            PagerAdapter adapter = mViewPager.getAdapter();
            if (adapter != null)
            {
                int pagerSize = adapter.getCount();
                if (pagerSize > 0)
                {
                    if (index >= 0 && index < pagerSize)
                    {
                        mViewPager.setCurrentItem(index);
                    }
                    else if (index == pagerSize)
                    {
                        if (mPageListener != null)
                        {
                            mPageListener.onOpenStickerMgrPage();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onSelectedSticker(Object info)
    {
        if(info != null && info instanceof StickerInfo)
        {
            VideoStickerRes res = (VideoStickerRes)((StickerInfo)info).mRes;
            // 需要解压
            if(res.mStickerRes == null)
            {
                StickerZipParseHelper.ParseHandler.ParseObj obj = new StickerZipParseHelper.ParseHandler.ParseObj();
                obj.res = res;
                sendStickerParseMsg(obj);
            }
            else
            {
                if(mPageListener != null)
                {
                    mPageListener.OnSelectedSticker(res);
                }
            }
        }
        else // non
        {
            if(mPageListener != null)
            {
                mPageListener.OnSelectedSticker(null);
            }
        }
    }

    private void sendStickerParseMsg(StickerZipParseHelper.ParseHandler.ParseObj obj)
    {
        if(obj == null) return;

        mStickerHandler.removeMessages(MSG_PARSE);
        mStickerHandler.setNewParse(mStickerHandler.isParse());

        Message msg = mStickerHandler.obtainMessage();
        msg.what = MSG_PARSE;
        msg.obj = obj;
        mStickerHandler.sendMessage(msg);
    }

    @Override
    public void onRefreshAllData()
    {
        ArrayList<LabelInfo> infoArr = StickerMgr.getInstance().getLabelInfoArr(getContext());
        if (infoArr != null)
        {
            RecyclerView.Adapter adapter = mTabView.getAdapter();
            if (adapter != null && adapter instanceof LabelAdapter)
            {
                ((LabelAdapter) adapter).setData(infoArr);
            }
        }

        notifyViewPagerChildUpdateData();

        if (mViewPager != null)
        {
            int selected_sticker_id = StickerMgr.getInstance().getSelectedInfo(StickerMgr.SelectedInfoKey.STICKER);
            if(selected_sticker_id == -1)
            {
                onSelectedSticker(null);
                mViewPager.setCurrentItem(0);
            }
            else
            {
                int label_id = StickerMgr.getInstance().getSelectedInfo(StickerMgr.SelectedInfoKey.LABEL);
                LabelInfo info = StickerMgr.getInstance().getLabelInfoByID(label_id);
                mViewPager.setCurrentItem(info != null ? info.mIndex : 0);
            }
        }
    }

    public void onPCSelectedSticker(int id)
    {
        StickerInfo info = StickerMgr.getInstance().getStickerInfoByID(id);

        int label_index = StickerMgr.getInstance().modifyPreviousSelected(info);

        onSelectedLabel(label_index);

        onSelectedSticker(info);
    }

    public void setPageListener(LivePageListener listener)
    {
        mPageListener = listener;
    }

    private void scrollLabelToCenter(final int position)
    {
        if (mTabView != null)
        {
            RecyclerView.LayoutManager manager = mTabView.getLayoutManager();
            if(manager != null)
            {
                View view = manager.findViewByPosition(position);
                float center = mTabView.getWidth() / 2f;
                if(view != null)
                {
                    float viewCenter = view.getX() + view.getWidth() / 2f;
                    mTabView.smoothScrollBy((int)(viewCenter - center), 0);
                }
                else
                {
                    mTabView.smoothScrollToPosition(position);
                    myQueue().addIdleHandler(new MessageQueue.IdleHandler()
                    {
                        @Override
                        public boolean queueIdle()
                        {
                            myQueue().removeIdleHandler(this);
                            scrollLabelToCenter(position);
                            return false;
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v == mNonView)
        {
            StickerMgr.getInstance().clearAllSelectedInfo();
            onSelectedSticker(null);
        }
        else if (v == mStickerSoundFr || v == mStickerFoldView)
        {
            if (mPageListener != null)
            {
                mPageListener.onCloseStickerList();
            }
        }
    }
}
