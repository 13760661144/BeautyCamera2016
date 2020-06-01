package cn.poco.mosaic;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.MaterialMgr2.site.DownloadMorePageSite;
import cn.poco.acne.view.CirclePanel;
import cn.poco.acne.view.UndoPanel;
import cn.poco.advanced.ImageUtils;
import cn.poco.advanced.RecommendItemConfig2;
import cn.poco.beautify.RecomDisplayMgr;
import cn.poco.beautify.SonWindow;
import cn.poco.beautify4.Beautify4Page;
import cn.poco.camera.RotationImg2;
import cn.poco.cloudalbumlibs.utils.CloudAlbumDialog;
import cn.poco.filterPendant.MyStatusButton;
import cn.poco.framework.BaseSite;
import cn.poco.framework.DataKey;
import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.image.PocoOilMask;
import cn.poco.image.filter;
import cn.poco.makeup.MySeekBar;
import cn.poco.mosaic.site.MosaicPageSite;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.AbsDragAdapter;
import cn.poco.recycleview.DragRecycleView;
import cn.poco.resource.BaseRes;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.IDownload;
import cn.poco.resource.LockRes;
import cn.poco.resource.LockResMgr2;
import cn.poco.resource.MosaicRes;
import cn.poco.resource.MosaicResMgr2;
import cn.poco.resource.RecommendRes;
import cn.poco.resource.ResType;
import cn.poco.resource.ResourceUtils;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.ShareData;
import cn.poco.tianutils.UndoRedoDataMgr;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.Utils;
import cn.poco.view.material.MosaicViewEx;
import cn.poco.widget.recycle.RecommendAdapter;
import cn.poco.widget.recycle.RecommendDragContainer2;
import my.beautyCamera.R;

/**
 * 马赛克主界面
 *
 * @author lmx
 *         created by lmx on 2016/12/02.
 * @since v4.0
 */
public class MosaicPage extends IPage
{

    //private static final String TAG = "马赛克";
    protected MosaicPageSite m_site;

    public boolean mUiEnabled;
    private MosaicViewEx mView;
    private SonWindow mSonWin;

    private int m_viewWidth;
    private int m_viewHeight;

    private Bitmap mOrgBmp;
    private Bitmap mShowBmp;

    private final int MOSAIC_BASE = 1066816;//马赛克涂鸦


    //撤销
    public final static int MSG_SAVE_CHCHE = 0x100;
    private HandlerThread mImageThread;
    private Handler mImageHandler;
    private Handler mUIHandler;
    private UndoPanel mUndoCtrl;
    private UndoRedoDataMgr mUndoData;

    private FrameLayout mBottomFr;
    private int mSeekBarMax = 100;
    private CirclePanel mCirclePanel;
    private int mSeekBarHeight;

    private FrameLayout mBottomBar;
    private ImageView mCancelBtn;
    private ImageView mOkBtn;
    private LinearLayout mCenterBtn;
    private MyStatusButton mPaintTypeBtn;
    private MyStatusButton mDoodleTypeBtn;

    private boolean isPaintType;
    private FrameLayout mEditScrollSeekFr;
    private FrameLayout mPaintSizeSeekBarFr;
    private FrameLayout mRubberSizeSeekBarFr;
    private FrameLayout mDoodlePaintFr;

    private ImageView mRubberBtn;
    private ImageView mPaintBtn;
    private boolean isRubberSeek = false; //默认马赛克画笔选择器

    private boolean isRubberMode = false;//橡皮擦模式
    private boolean isPaintMode = false;//画笔模式

    private int mRubberSizeProgress;
    private int mPaintSizeProgress;

    private float mMinRadius;
    private float mMaxRadius;


    private FrameLayout mBottomLayout;
    private MosaicConfig mConfig;
    private RecommendDragContainer2 mDragContainer;
    private DragRecycleView mPaintList;
    private DragRecycleView mDoodleList;
    private RecommendAdapter mPaintAdapter;
    private RecommendAdapter mDoodleAdapter;

    private int mPaintTypeUri = RecommendAdapter.ItemInfo.URI_NONE;
    private int mDoodleTypeUri = RecommendAdapter.ItemInfo.URI_NONE;
    private ArrayList<RecommendAdapter.ItemInfo> mPaintListInfos;
    private ArrayList<RecommendAdapter.ItemInfo> mDoodleListInfos;

    protected RecomDisplayMgr m_recomView;

    private RecommendItemConfig2 mDoodleConfig;

    private int mBottomLayoutHeight;
    private int mBottomBarHeight;

    private boolean isFold = false;
    private boolean isSelUri = false;//判断是否为setdata值
    private boolean mChange = false;

    //ui anim
    private float m_currImgH = 0f;
    private int m_imgH = 0;
    private int m_viewH = 0;
    private int m_viewTopMargin;
    private static final int SHOW_VIEW_ANIM = 300;
    private LinearLayout mEditLayout;
    private int mEditWidth;


    public MosaicPage(Context context, BaseSite site)
    {
        super(context, site);

        m_site = (MosaicPageSite) site;

        initData(context);
        initUI(context);

        MyBeautyStat.onPageStartByRes(R.string.美颜美图_马赛克页面_主页面);
        TongJiUtils.onPageStart(getContext(), R.string.马赛克);
    }


    public void initData(Context context)
    {
        ShareData.InitData(context);

        mBottomLayoutHeight = ShareData.PxToDpi_xhdpi(232);
        mBottomBarHeight = ShareData.PxToDpi_xhdpi(88);

        m_viewWidth = ShareData.m_screenWidth;
        m_viewHeight = ShareData.m_screenHeight - mBottomLayoutHeight - mBottomBarHeight;

        mMinRadius = ShareData.PxToDpi_xhdpi(12);
        mMaxRadius = ShareData.PxToDpi_xhdpi(55);
        mSeekBarHeight = ShareData.PxToDpi_xhdpi(80);
        mEditWidth = ShareData.PxToDpi_xhdpi(104);

        mUndoData = new UndoRedoDataMgr(10, true, new UndoRedoDataMgr.Callback()
        {
            @Override
            public void DeleteData(Object data)
            {
                if (data != null)
                {
                    File file = new File((String) data);
                    if (file != null && file.exists())
                    {
                        file.delete();
                    }
                }
            }
        });

        mDoodleConfig = new RecommendItemConfig2(getContext(), true);

        mUiEnabled = true;

        mPaintListInfos = MosaicResMgr.getPaint2Res(getContext());
        mDoodleListInfos = MosaicResMgr.getMosaicRes(context);
        mConfig = new MosaicConfig();

        isPaintType = false;

        mDoodleTypeUri = MosaicResMgr.getResFirstUri(mDoodleListInfos);

        mPaintTypeUri = MosaicResMgr.getResFirstUri(mPaintListInfos);

        if (TagMgr.CheckTag(getContext(), Tags.MOSAIC_PAINT_PROGRESS_SIZE))
        {
            mPaintSizeProgress = mSeekBarMax / 2;
        }
        else
        {
            mPaintSizeProgress = TagMgr.GetTagIntValue(getContext(), Tags.MOSAIC_PAINT_PROGRESS_SIZE, mSeekBarMax / 2);
        }

        if (TagMgr.CheckTag(getContext(), Tags.MOSAIC_RUBBER_PROGRESS_SIZE))
        {
            mRubberSizeProgress = mSeekBarMax / 2;
        }
        else
        {
            mRubberSizeProgress = TagMgr.GetTagIntValue(getContext(), Tags.MOSAIC_RUBBER_PROGRESS_SIZE, mSeekBarMax / 2);
        }

        if (DownloadMgr.getInstance() != null)
        {
            DownloadMgr.getInstance().AddDownloadListener(m_downloadLst);
        }

        mImageThread = new HandlerThread("mosaic_page_thread");
        mImageThread.start();
        mImageHandler = new Handler(mImageThread.getLooper())
        {
            @Override
            public void dispatchMessage(Message msg)
            {
                switch (msg.what)
                {
                    case MSG_SAVE_CHCHE:
                    {

                        if (msg.obj != null && msg.obj instanceof Bitmap)
                        {
                            String path = FileCacheMgr.GetLinePath();
                            if (Utils.SaveTempImg((Bitmap) msg.obj, path))
                            {
                                mUndoData.AddData(path);
                            }
                        }

                        if (mUIHandler != null)
                        {
                            Message uiMsg = mUIHandler.obtainMessage();
                            uiMsg.what = MSG_SAVE_CHCHE;
                            mUIHandler.sendMessage(uiMsg);
                        }

                        break;
                    }
                    default:
                        break;
                }
            }
        };

        mUIHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if (msg.what == MSG_SAVE_CHCHE)
                {
                    mUiEnabled = true;
                    if (mView != null)
                    {
                        mView.setUiEnabled(true);

                        updateUndoRedoBtnState();
                        if (isPaintType)
                        {
                            mTongjiArr.put(mPaintTypeUri, KEY_PAINT_TJ);
                        }
                        else
                        {
                            mTongjiArr.put(mDoodleTypeUri, KEY_DOODLE_TJ);
                        }
                    }
                }
            }
        };
    }

    public DownloadMgr.DownloadListener m_downloadLst = new DownloadMgr.DownloadListener()
    {

        @Override
        public void OnDataChange(int resType, int downloadId, IDownload[] resArr)
        {
            if (resArr != null && ((BaseRes) resArr[0]).m_type == BaseRes.TYPE_LOCAL_PATH)
            {
                if (resType == ResType.MOSAIC.GetValue())
                {
                    ArrayList<RecommendAdapter.ItemInfo> dst = MosaicResMgr.getMosaicRes(getContext());
                    if (dst.size() > mDoodleListInfos.size())
                    {
                        mDoodleAdapter.notifyItemDownLoad(dst.size() - mDoodleListInfos.size());
                    }
                    mDoodleListInfos = dst;
                    mDoodleAdapter.SetData(mDoodleListInfos);
                    mDoodleAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    public void initUI(Context context)
    {
        LayoutParams fl_lp;
        LinearLayout.LayoutParams ll_lp;

        mView = new MosaicViewEx(getContext()); //+2为了去白边
        mView.setOnControlCallback(myTouchListener);
        mView.setPaintSize(getDrawPaintSize(mPaintSizeProgress));
        mView.setMosaicPaintSize(getDrawPaintSize(mPaintSizeProgress));
        fl_lp = new LayoutParams(m_viewWidth, m_viewHeight);
        fl_lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        mView.setLayoutParams(fl_lp);
        this.addView(mView, 0);

        mSonWin = new SonWindow(getContext());
        fl_lp = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.m_screenWidth / 3);
        fl_lp.gravity = Gravity.TOP | Gravity.START;
        mSonWin.setLayoutParams(fl_lp);
        this.addView(mSonWin);


        mBottomFr = new FrameLayout(context);
        fl_lp = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        fl_lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        mBottomFr.setLayoutParams(fl_lp);
        this.addView(mBottomFr);
        {
            //重做 撤销
            mUndoCtrl = new UndoPanel(getContext());
            mUndoCtrl.setCallback(mUndoCB);
            fl_lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            fl_lp.gravity = Gravity.BOTTOM | Gravity.LEFT;
            fl_lp.leftMargin = ShareData.PxToDpi_xhdpi(20);
            fl_lp.bottomMargin = ShareData.PxToDpi_xhdpi(18) + mBottomBarHeight + mBottomLayoutHeight;
            mUndoCtrl.setLayoutParams(fl_lp);
            mUndoCtrl.setVisibility(INVISIBLE);
            mBottomFr.addView(mUndoCtrl);
        }

        mBottomBar = new FrameLayout(getContext());
        fl_lp = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mBottomBarHeight);
        fl_lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        fl_lp.bottomMargin = mBottomLayoutHeight;
        mBottomBar.setLayoutParams(fl_lp);
        mBottomBar.setBackgroundColor(0xe6ffffff);
        mBottomFr.addView(mBottomBar);
        {
            mCancelBtn = new ImageView(context);
            mCancelBtn.setScaleType(ScaleType.CENTER_INSIDE);
            mCancelBtn.setImageResource(R.drawable.beautify_cancel);
            mCancelBtn.setPadding(ShareData.PxToDpi_xhdpi(22), 0, ShareData.PxToDpi_xhdpi(22), 0);
            fl_lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            fl_lp.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            mCancelBtn.setLayoutParams(fl_lp);
            mBottomBar.addView(mCancelBtn);
            mCancelBtn.setOnTouchListener(mOnAnimationClickListener);

            mOkBtn = new ImageView(context);
            mOkBtn.setScaleType(ScaleType.CENTER_INSIDE);
            mOkBtn.setImageResource(R.drawable.beautify_ok);
            mOkBtn.setPadding(ShareData.PxToDpi_xhdpi(22), 0, ShareData.PxToDpi_xhdpi(22), 0);
            ImageUtils.AddSkin(getContext(), mOkBtn);
            fl_lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            fl_lp.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
            mOkBtn.setLayoutParams(fl_lp);
            mBottomBar.addView(mOkBtn);
            mOkBtn.setOnTouchListener(mOnAnimationClickListener);

            mCenterBtn = new LinearLayout(context);
            mCenterBtn.setOrientation(LinearLayout.HORIZONTAL);
            fl_lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            fl_lp.gravity = Gravity.CENTER;
            mCenterBtn.setLayoutParams(fl_lp);
            mBottomBar.addView(mCenterBtn);
            mCenterBtn.setOnClickListener(mOnClickListener);
            {
                mDoodleTypeBtn = new MyStatusButton(context);
                mDoodleTypeBtn.setData(ImageUtils.AddSkin(context, BitmapFactory.decodeResource(getResources(), R.drawable.mosaicpage_doodle_icon)), getContext().getString(R.string.mosaicpage_doodle));
                mDoodleTypeBtn.setBtnStatus(!isPaintType, isFold);
                mDoodleTypeBtn.setOnClickListener(mOnClickListener);
                ll_lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                ll_lp.gravity = Gravity.CENTER_VERTICAL;
                ll_lp.rightMargin = ShareData.PxToDpi_xhdpi(14);
                mDoodleTypeBtn.setLayoutParams(ll_lp);
                mCenterBtn.addView(mDoodleTypeBtn);

                mPaintTypeBtn = new MyStatusButton(context);
                mPaintTypeBtn.setData(ImageUtils.AddSkin(context, BitmapFactory.decodeResource(getResources(), R.drawable.mosaicpage_paint_icon)), getContext().getString(R.string.mosaicpage_paint));
                mPaintTypeBtn.setBtnStatus(isPaintType, isFold);
                mPaintTypeBtn.setOnClickListener(mOnClickListener);
                ll_lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                ll_lp.gravity = Gravity.CENTER_VERTICAL;
                ll_lp.setMargins(ShareData.PxToDpi_xhdpi(40 + 14), 0, 0, 0);
                mPaintTypeBtn.setLayoutParams(ll_lp);
                mCenterBtn.addView(mPaintTypeBtn);
            }
        }

        mBottomLayout = new FrameLayout(context);
        fl_lp = new LayoutParams(LayoutParams.MATCH_PARENT, mBottomLayoutHeight);
        fl_lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        mBottomLayout.setLayoutParams(fl_lp);
        mBottomFr.addView(mBottomLayout);
        {
            //包裹seekbar 和 素材fr
            mEditScrollSeekFr = new FrameLayout(context);
            fl_lp = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(136 * 2 + 232));
            fl_lp.gravity = Gravity.CENTER_VERTICAL;
            fl_lp.leftMargin = mEditWidth;
            mEditScrollSeekFr.setLayoutParams(fl_lp);
            mBottomLayout.addView(mEditScrollSeekFr);
            {
                //画笔滚动条
                mPaintSizeSeekBarFr = new FrameLayout(context);
                fl_lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                fl_lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                mPaintSizeSeekBarFr.setLayoutParams(fl_lp);
                mPaintSizeSeekBarFr.setVisibility(VISIBLE);
                mEditScrollSeekFr.addView(mPaintSizeSeekBarFr);
                {
                    MySeekBar mPaintSizeSeekBar = new MySeekBar(getContext());
                    mPaintSizeSeekBar.setMax(mSeekBarMax);
                    mPaintSizeSeekBar.setProgress(mPaintSizeProgress);
                    mPaintSizeSeekBar.setBackgroundColor(0x57000000);
                    mPaintSizeSeekBar.setOnProgressChangeListener(mPaintSizeSeekBarOnChangeListener);
                    fl_lp = new LayoutParams(ShareData.PxToDpi_xhdpi(530), mSeekBarHeight);
                    fl_lp.gravity = Gravity.CENTER;
                    fl_lp.topMargin = ShareData.PxToDpi_xhdpi((136 - 80) / 2);
                    fl_lp.bottomMargin = fl_lp.topMargin;
                    mPaintSizeSeekBar.setLayoutParams(fl_lp);
                    mPaintSizeSeekBarFr.addView(mPaintSizeSeekBar);
                }

                //素材
                mDoodlePaintFr = new FrameLayout(context);
                fl_lp = new LayoutParams(LayoutParams.MATCH_PARENT, mBottomLayoutHeight);
                fl_lp.gravity = Gravity.CENTER;
                mDoodlePaintFr.setLayoutParams(fl_lp);
                mEditScrollSeekFr.addView(mDoodlePaintFr);
                {
                    mDoodleAdapter = new RecommendAdapter(mConfig);
                    mDoodleAdapter.SetData(mDoodleListInfos);
                    mDoodleAdapter.setOnDragCallBack(mDoubleDragCallBack);
                    mDoodleAdapter.setOnItemClickListener(mDoodleListCallback);
                    mDoodleList = new DragRecycleView(getContext(), mDoodleAdapter);
                    fl_lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    mDoodleList.setLayoutParams(fl_lp);
                    mDoodlePaintFr.addView(mDoodleList);

                    mPaintAdapter = new RecommendAdapter(mConfig);
                    mPaintAdapter.SetData(mPaintListInfos);
                    mPaintAdapter.setOnItemClickListener(mPaintListCallback);
                    mPaintAdapter.setOnDragCallBack(mPaintDragCallBack);
                    fl_lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    mPaintList = new DragRecycleView(getContext(), mPaintAdapter);
                    mPaintList.setLayoutParams(fl_lp);
                    mPaintList.setVisibility(View.GONE);
                    mDoodlePaintFr.addView(mPaintList);
                }

                //橡皮檫滚动条
                mRubberSizeSeekBarFr = new FrameLayout(context);
                fl_lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                fl_lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                mRubberSizeSeekBarFr.setLayoutParams(fl_lp);
                mRubberSizeSeekBarFr.setVisibility(VISIBLE);
                mEditScrollSeekFr.addView(mRubberSizeSeekBarFr);
                {
                    MySeekBar mRubberSizeSeekBar = new MySeekBar(getContext());
                    mRubberSizeSeekBar.setMax(mSeekBarMax);
                    mRubberSizeSeekBar.setProgress(mRubberSizeProgress);
                    mRubberSizeSeekBar.setBackgroundColor(0x57000000);
                    mRubberSizeSeekBar.setOnProgressChangeListener(mPaintSizeSeekBarOnChangeListener);
                    fl_lp = new LayoutParams(ShareData.PxToDpi_xhdpi(530), mSeekBarHeight);
                    fl_lp.gravity = Gravity.CENTER;
                    fl_lp.topMargin = ShareData.PxToDpi_xhdpi((136 - 80) / 2);
                    fl_lp.bottomMargin = fl_lp.topMargin;
                    mRubberSizeSeekBar.setLayoutParams(fl_lp);
                    mRubberSizeSeekBarFr.addView(mRubberSizeSeekBar);
                }
            }
            //画笔 橡皮擦 工具
            mEditLayout = new LinearLayout(context);
            mEditLayout.setMinimumWidth(mEditWidth);
            mEditLayout.setOrientation(LinearLayout.VERTICAL);
            mEditLayout.setGravity(Gravity.CENTER);
            fl_lp = new LayoutParams(LayoutParams.WRAP_CONTENT, mBottomLayoutHeight);
            fl_lp.gravity = Gravity.BOTTOM | Gravity.LEFT;
            fl_lp.leftMargin = ShareData.PxToDpi_xhdpi(19);
            fl_lp.rightMargin = fl_lp.leftMargin;
            mEditLayout.setLayoutParams(fl_lp);
            mBottomLayout.addView(mEditLayout);
            {
                mPaintBtn = new ImageView(context);
                mPaintBtn.setScaleType(ScaleType.CENTER_INSIDE);
                mPaintBtn.setImageResource(R.drawable.mosaicpage_paint_btn_normal);
                mPaintBtn.setOnTouchListener(mOnAnimationClickListener);
                ll_lp = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(80), ShareData.PxToDpi_xhdpi(80));
                ll_lp.gravity = Gravity.CENTER_HORIZONTAL;
                mPaintBtn.setLayoutParams(ll_lp);
                mEditLayout.addView(mPaintBtn);

                mRubberBtn = new ImageView(context);
                mRubberBtn.setScaleType(ScaleType.CENTER_INSIDE);
                mRubberBtn.setImageResource(R.drawable.mosaicpage_rubber_btn_normal);
                mRubberBtn.setOnTouchListener(mOnAnimationClickListener);
                ll_lp = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(80), ShareData.PxToDpi_xhdpi(80));
                ll_lp.gravity = Gravity.CENTER_HORIZONTAL;
                ll_lp.topMargin = ShareData.PxToDpi_xhdpi(20);
                mRubberBtn.setLayoutParams(ll_lp);
                mEditLayout.addView(mRubberBtn);
            }
            //进度tip
            mCirclePanel = new CirclePanel(context);
            fl_lp = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(120));
            fl_lp.gravity = Gravity.BOTTOM;
            fl_lp.bottomMargin = ShareData.PxToDpi_xhdpi(320 - 64 / 2 - 60);
            this.addView(mCirclePanel, fl_lp);
        }
        mDragContainer = new RecommendDragContainer2(getContext(), mDoodleList);
        fl_lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mDragContainer, fl_lp);

        initRecomDisplayMgr();
    }

    private RecommendAdapter.OnDragCallBack mPaintDragCallBack = new AbsDragAdapter.OnDragCallBack()
    {
        @Override
        public void onItemDelete(AbsDragAdapter.ItemInfo info, int position)
        {

        }

        @Override
        public void onItemMove(AbsDragAdapter.ItemInfo info, int fromPosition, int toPosition)
        {

        }

        @Override
        public void onDragStart(AbsDragAdapter.ItemInfo itemInfo, int position)
        {

        }

        @Override
        public void onDragEnd(AbsDragAdapter.ItemInfo itemInfo, int position)
        {

        }

        @Override
        public void onLongClick(AbsDragAdapter.ItemInfo itemInfo, int position)
        {
            if (!itemInfo.m_canDrag)
            {
                Toast.makeText(getContext(), "该素材不允许拖拽和删除", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private RecommendAdapter.OnDragCallBack mDoubleDragCallBack = new AbsDragAdapter.OnDragCallBack()
    {
        @Override
        public void onItemDelete(AbsDragAdapter.ItemInfo info, int position)
        {

        }

        @Override
        public void onItemMove(AbsDragAdapter.ItemInfo info, int fromPosition, int toPosition)
        {
            AbsAdapter.ItemInfo itemInfo = mDoodleListInfos.get(fromPosition);
            int fromPos = fromPosition;
            int toPos = toPosition;
            if (itemInfo != null)
            {
                fromPos = ResourceUtils.HasId(MosaicResMgr2.getInstance().GetOrderArr(), itemInfo.m_uri);
            }
            itemInfo = mDoodleListInfos.get(toPosition);
            if (itemInfo != null)
            {
                toPos = ResourceUtils.HasId(MosaicResMgr2.getInstance().GetOrderArr(), itemInfo.m_uri);
            }
            if (fromPos >= 0 && toPos >= 0)
            {
                ResourceUtils.ChangeOrderPosition(MosaicResMgr2.getInstance().GetOrderArr(), fromPos, toPos);
                MosaicResMgr2.getInstance().SaveOrderArr();
            }
        }

        @Override
        public void onDragStart(AbsDragAdapter.ItemInfo itemInfo, int position)
        {
//            int out = BaseResMgr.HasItem(ThemeResMgr.GetLocalResArr(),itemInfo.m_uri);
//            if(out >=0 ){
//                mDragContainer.setCanDelete(false);
//            }else{
//                mDragContainer.setCanDelete(true);
//            }
            mDragContainer.setCanDelete(false);
        }

        @Override
        public void onDragEnd(AbsDragAdapter.ItemInfo itemInfo, int position)
        {
        }

        @Override
        public void onLongClick(AbsDragAdapter.ItemInfo itemInfo, int position)
        {

        }
    };

    private void initRecomDisplayMgr()
    {
        m_recomView = new RecomDisplayMgr(getContext(), new RecomDisplayMgr.Callback()
        {
            @Override
            public void UnlockSuccess(BaseRes res)
            {
                if (res != null && mDoodleAdapter != null)
                {
                    int index = mDoodleAdapter.GetIndex(res.m_id);
                    if (index != -1)
                    {
                        TagMgr.SetTag(getContext(), Tags.MOSAIC_PAINT_UNLOCK_ID_FLAG + res.m_id);
                        mDoodleAdapter.Unlock(res.m_id);
                        mDoodleAdapter.SetSelectByUri(res.m_id);
                    }
                }
            }

            @Override
            public void OnCloseBtn()
            {

            }

            @Override
            public void OnBtn(int state)
            {

            }

            @Override
            public void OnClose()
            {

            }

            @Override
            public void OnLogin()
            {
                if (m_site != null)
                {
                    m_site.OnLogin(getContext());
                }
            }
        });
        m_recomView.Create(this);
    }

    private boolean isChange = false;
    private MosaicViewEx.ControlCallback myTouchListener = new MosaicViewEx.ControlCallback()
    {
        @Override
        public void updateSonWin(Bitmap bmp, int x, int y)
        {
            if (mSonWin != null)
            {
                mSonWin.SetData(bmp, x, y);
            }
        }

        @Override
        public void canvasChanged(Bitmap bmp)
        {
            if (bmp != null)
            {
                isChange = true;
                mUiEnabled = false;
                mView.setUiEnabled(false);
                mChange = true;

                if (mImageHandler != null)
                {
                    Message msg = mImageHandler.obtainMessage();
                    msg.what = MSG_SAVE_CHCHE;
                    msg.obj = bmp;
                    mImageHandler.sendMessage(msg);
                }
            }
        }

        @Override
        public void fingerDown()
        {
            if (mUndoCtrl != null)
            {
                mUndoCtrl.hide();
            }
        }

        @Override
        public void fingerUp()
        {
            if (mUndoCtrl != null && isChange)
            {
                mUndoCtrl.show();
            }
        }

    };

    private OnClickListener mOnClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (mUiEnabled)
            {
                if (v == mCenterBtn)
                {
                    if (isPaintType)
                    {
                        mOnClickListener.onClick(mPaintTypeBtn);
                    }
                    else
                    {
                        mOnClickListener.onClick(mDoodleTypeBtn);
                    }
                }
                else if (v == mPaintTypeBtn)
                {
                    if (isPaintType)
                    {
                        isFold = !isFold;
                        mPaintTypeBtn.setBtnStatus(true, !mPaintTypeBtn.isDown());
                        mDoodleTypeBtn.setBtnStatus(false, !mDoodleTypeBtn.isDown());
                        SetViewFrState(isFold);
                        SetBottomFrState(isFold);
                        MyBeautyStat.onClickByRes(isFold ? R.string.美颜美图_马赛克页面_主页面_画风tab收回 : R.string.美颜美图_马赛克页面_主页面_画风tab展开);
                    }
                    else
                    {
                        isPaintType = true;
                        boolean oldIsFold = isFold;
                        isFold = false;
                        mPaintTypeBtn.setBtnStatus(true, false);
                        mDoodleTypeBtn.setBtnStatus(false, false);
                        if (oldIsFold)
                        {
                            SetViewFrState(isFold);
                            SetBottomFrState(isFold);
                        }
                        mPaintList.setVisibility(VISIBLE);
                        mDoodleList.setVisibility(GONE);
                        readInfo2();
                        changeResFoldIndexState();
                        MyBeautyStat.onClickByRes(R.string.美颜美图_马赛克页面_主页面_画风tab);
                        MyBeautyStat.onClickByRes(R.string.美颜美图_马赛克页面_主页面_画风tab展开);
                        TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_马赛克_画风);
                    }
                }
                else if (v == mDoodleTypeBtn)
                {
                    if (!isPaintType)
                    {
                        isFold = !isFold;
                        mDoodleTypeBtn.setBtnStatus(true, !mDoodleTypeBtn.isDown());
                        mPaintTypeBtn.setBtnStatus(false, !mPaintTypeBtn.isDown());
                        SetViewFrState(isFold);
                        SetBottomFrState(isFold);
                        MyBeautyStat.onClickByRes(isFold ? R.string.美颜美图_马赛克页面_主页面_涂鸦tab收回 : R.string.美颜美图_马赛克页面_主页面_涂鸦tab展开);
                    }
                    else
                    {
                        isPaintType = false;
                        boolean oldIsFold = isFold;
                        isFold = false;

                        mDoodleTypeBtn.setBtnStatus(true, false);
                        mPaintTypeBtn.setBtnStatus(false, false);
                        if (oldIsFold)
                        {
                            SetViewFrState(isFold);
                            SetBottomFrState(isFold);
                        }
                        mPaintList.setVisibility(GONE);
                        mDoodleList.setVisibility(VISIBLE);
                        readInfo2();
                        changeResFoldIndexState();
                        MyBeautyStat.onClickByRes(R.string.美颜美图_马赛克页面_主页面_涂鸦tab);
                        MyBeautyStat.onClickByRes(R.string.美颜美图_马赛克页面_主页面_涂鸦tab展开);
                        TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_马赛克_涂鸦);
                    }
                }
            }
        }
    };

    private OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener()
    {
        @Override
        public void onAnimationClick(View v)
        {
            if (mUiEnabled)
            {
                if (v == mCancelBtn)
                {
                    if (m_recomView != null && m_recomView.IsShow())
                    {
                        m_recomView.OnCancel(true);
                    }
                    else
                    {
                        MyBeautyStat.onClickByRes(R.string.美颜美图_马赛克页面_主页面_取消);
                        TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_马赛克_取消);
                        if (mChange)
                        {
                            showExitDialog();
                        }
                        else
                        {
                            cancel();
                        }
                    }
                }
                else if (v == mOkBtn)
                {
                    MyBeautyStat.onClickByRes(R.string.美颜美图_马赛克页面_主页面_确认);
                    TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_马赛克_确认);
                    sendTonjiParams();
                    sendSensorsData();

                    if (m_site != null && mView != null)
                    {
                        HashMap<String, Object> params = new HashMap<>();
                        params.putAll(getBackAnimParam());
                        params.put("img", mView.getOutSaveBmp());
                        m_site.OnSave(getContext(), params);
                    }
                }
                else if (v == mRubberBtn)
                { //橡皮擦模式
                    //如果用橡皮擦前是画风效果，要更新画风效果的合成用的原图
                    if (isRubberMode && isPaintType)
                    {
                        RecommendAdapter.ItemInfo tempItemInfo = (RecommendAdapter.ItemInfo) mPaintAdapter.GetItemInfoByUri(mPaintTypeUri);
                        if (tempItemInfo != null && mPaintTypeUri == MOSAIC_BASE)
                        {
                            Bitmap bmp = filter.mosaicEffect(mView.getOutBmp());
                            mView.setDoodleType(bmp, (MosaicRes) tempItemInfo.m_ex);
                        }
                        else if (tempItemInfo != null)
                        {
                            mView.setMosaicType(((int[]) tempItemInfo.m_ex)[1]);
                        }
                    }
                    if (isPaintMode && !isRubberSeek)
                    {//默认画笔切换到橡皮擦
                        isPaintMode = false;
                        isRubberSeek = true;
                        isRubberMode = true;
                        SetOverSeekAndDoodlePaintViewState(true);
                        SetRubberPaintBtnState(isRubberMode, isPaintMode);
                        setDrawPaintSize(mRubberSizeProgress);
                        mView.setRubberMode(true);
                        MyBeautyStat.onClickByRes(R.string.美颜美图_马赛克页面_主页面_切换橡皮擦);
                        TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_马赛克_切换橡皮擦);
                    }
                    else
                    {
                        isRubberSeek = true;
                        isRubberMode = !isRubberMode;
                        mView.setRubberMode(isRubberMode);
                        if (isRubberMode) isPaintMode = false;
                        SetSwitchSeekAndDoodlePaintViewState(isRubberSeek, isRubberMode);
                        SetRubberPaintBtnState(isRubberMode, isPaintMode);
                        if (!isRubberMode) isRubberSeek = false;
                        setDrawPaintSize(!isRubberMode ? mPaintSizeProgress : mRubberSizeProgress);
                        if (isRubberMode)
                        {
                        }
                        else
                        {
                            TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_马赛克_切换橡皮擦);
                        }
                        MyBeautyStat.onClickByRes(R.string.美颜美图_马赛克页面_主页面_切换橡皮擦);
                    }
                }
                else if (v == mPaintBtn)
                {
                    //如果用橡皮擦前是画风效果，要更新画风效果的合成用的原图
                    if (isRubberMode && isPaintType)
                    {
                        RecommendAdapter.ItemInfo tempItemInfo = (RecommendAdapter.ItemInfo) mPaintAdapter.GetItemInfoByUri(mPaintTypeUri);
                        if (tempItemInfo != null && mPaintTypeUri == MOSAIC_BASE)
                        {
                            Bitmap bmp = filter.mosaicEffect(mView.getOutBmp());
                            mView.setDoodleType(bmp, (MosaicRes) tempItemInfo.m_ex);
                        }
                        else if (tempItemInfo != null)
                        {
                            mView.setMosaicType(((int[]) tempItemInfo.m_ex)[1]);
                        }
                    }
                    if (isRubberSeek && !isPaintMode)
                    { //默认橡皮擦切换到画笔
                        isPaintMode = true;
                        isRubberSeek = false;
                        isRubberMode = false;
                        SetOverSeekAndDoodlePaintViewState(false);
                        SetRubberPaintBtnState(false, isPaintMode);
                        setDrawPaintSize(mPaintSizeProgress);
                        mView.setRubberMode(false);
                        MyBeautyStat.onClickByRes(R.string.美颜美图_马赛克页面_主页面_切换画笔);
                        TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_马赛克_切换画笔);
                    }
                    else
                    {
                        isRubberSeek = false;
                        isPaintMode = !isPaintMode;
                        mView.setRubberMode(false);
                        if (isPaintMode) isRubberMode = false;
                        SetSwitchSeekAndDoodlePaintViewState(isRubberSeek, isPaintMode);
                        SetRubberPaintBtnState(isRubberMode, isPaintMode);
                        setDrawPaintSize(mPaintSizeProgress);
                        mView.changingPaintSize(false);
                        if (!isPaintMode)
                        {
                            MyBeautyStat.onClickByRes(R.string.美颜美图_马赛克页面_主页面_切换画笔);
                        }
                        MyBeautyStat.onClickByRes(R.string.美颜美图_马赛克页面_主页面_切换画笔);
                    }
                }
            }
        }

        @Override
        public void onTouch(View v)
        {

        }

        @Override
        public void onRelease(View v)
        {

        }
    };

    /**
     * 统计
     */
    private void sendTonjiParams()
    {
        if (mTongjiArr != null)
        {
            int len = mTongjiArr.size();
            for (int i = 0; i < len; i++)
            {
                if (mTongjiArr.valueAt(i) == 1)
                {
                    TongJi2.AddCountById(Integer.toString(mTongjiArr.keyAt(i)));
                }
                else
                {
                    MosaicRes res = cn.poco.resource.MosaicResMgr2.getInstance().GetRes(mTongjiArr.keyAt(i));
                    if (res != null)
                    {
                        TongJi2.AddCountById(Integer.toString(res.m_tjId));
                    }
                }
            }
        }
    }

    /**
     * 神策素材使用统计
     */
    private void sendSensorsData()
    {
        if (mTongjiArr != null && mTongjiArr.size() > 0)
        {
            String[] tongjiIds = new String[mTongjiArr.size()];
            int len = mTongjiArr.size();
            for (int i = 0; i < len; i++)
            {
                int value = mTongjiArr.valueAt(i);
                if (value == KEY_PAINT_TJ)
                {
                    tongjiIds[i] = String.valueOf(mTongjiArr.keyAt(i));
                }
                else if (value == KEY_DOODLE_TJ)
                {
                    tongjiIds[i] = String.valueOf(mTongjiArr.keyAt(i));
                }
                else
                {
                    MosaicRes res = cn.poco.resource.MosaicResMgr2.getInstance().GetRes(mTongjiArr.keyAt(i));
                    if (res != null)
                    {
                        tongjiIds[i] = String.valueOf(res.m_tjId);
                    }
                }
            }
            MyBeautyStat.onUseMosaic(tongjiIds);
        }
    }

    private UndoPanel.Callback mUndoCB = new UndoPanel.Callback()
    {
        @Override
        public void onUndo()
        {
            if (mUiEnabled && mUndoCtrl.isCanUndo())
            {
                MyBeautyStat.onClickByRes(R.string.美颜美图_马赛克页面_主页面_撤销);
                Object obj = mUndoData.Undo();
                if (obj != null && mView != null)
                {
                    mView.setUiEnabled(false);

                    mView.updateImageBitmap(cn.poco.imagecore.Utils.DecodeImage(getContext(), obj, 0, -1, -1, -1));
                    mView.invalidate();
                    updateUndoRedoBtnState();
                    if (isPaintType)
                    {
                        RecommendAdapter.ItemInfo tempItemInfo = (RecommendAdapter.ItemInfo) mPaintAdapter.GetItemInfoByUri(mPaintTypeUri);
                        if (tempItemInfo != null && mPaintTypeUri == MOSAIC_BASE)
                        {
                            Bitmap bmp = filter.mosaicEffect(mView.getOutBmp());
                            mView.setDoodleType(bmp, (MosaicRes) tempItemInfo.m_ex);
                        }
                        else if (tempItemInfo != null)
                        {
                            mView.updateMosaicOrgBmp();
                        }
                    }
                    mView.setUiEnabled(true);
                }
            }
        }

        @Override
        public void onRedo()
        {
            if (mUiEnabled && mUndoCtrl.isCanRedo())
            {
                MyBeautyStat.onClickByRes(R.string.美颜美图_马赛克页面_主页面_重做);
                Object obj = mUndoData.Redo();
                if (obj != null && mView != null)
                {
                    mView.setUiEnabled(false);

                    mView.updateImageBitmap(cn.poco.imagecore.Utils.DecodeImage(getContext(), obj, 0, -1, -1, -1));
                    mView.invalidate();
                    updateUndoRedoBtnState();
                    if (isPaintType)
                    {
                        RecommendAdapter.ItemInfo tempItemInfo = (RecommendAdapter.ItemInfo) mPaintAdapter.GetItemInfoByUri(mPaintTypeUri);
                        if (tempItemInfo != null && mPaintTypeUri == MOSAIC_BASE)
                        {
                            Bitmap bmp = filter.mosaicEffect(mView.getOutBmp());
                            mView.setDoodleType(bmp, (MosaicRes) tempItemInfo.m_ex);
                        }
                        else if (tempItemInfo != null)
                        {
                            mView.updateMosaicOrgBmp();
                        }
                    }
                    mView.setUiEnabled(true);
                }
            }
        }
    };

    public void readInfo2()
    {
        if (isPaintType)
        {
            RecommendAdapter.ItemInfo tempInfo = (RecommendAdapter.ItemInfo) mPaintAdapter.GetItemInfoByUri(mPaintTypeUri);
            if (tempInfo != null)
            {
                mDoodleTypeBtn.setCircleIcon(null);
                if (mPaintTypeUri == RecommendAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI)
                {
                    mPaintTypeBtn.setCircleIcon(null);
                    return;
                }
                if (mPaintTypeUri == RecommendAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI)
                {
                    return;
                }
                mPaintAdapter.SetSelectByUri(mPaintTypeUri);
                //如果是"马赛克基本", 就用涂鸦的技术实现
                if (mPaintTypeUri == MOSAIC_BASE)
                {
                    Bitmap tempBmp = cn.poco.imagecore.Utils.DecodeImage(getContext(), ((MosaicRes) tempInfo.m_ex).m_icon, 0, -1, -1, -1);
                    if (tempBmp != null)
                    {
                        mPaintTypeBtn.setCircleIcon(MakeBmp.CreateFixBitmap(tempBmp, ShareData.PxToDpi_xhdpi(34), ShareData.PxToDpi_xhdpi(34), MakeBmp.POS_CENTER, 0, Config.ARGB_8888));
                        recycleBitmap(tempBmp, true);
                    }
                    Bitmap bmp = filter.mosaicEffect(mView.getOutBmp());
                    mView.setDoodleType(bmp, (MosaicRes) tempInfo.m_ex);
                }
                else
                {
                    Bitmap tempBmp = cn.poco.imagecore.Utils.DecodeImage(getContext(), ((int[]) tempInfo.m_ex)[0], 0, -1, -1, -1);
                    if (tempBmp != null)
                    {
                        mPaintTypeBtn.setCircleIcon(MakeBmp.CreateFixBitmap(tempBmp, ShareData.PxToDpi_xhdpi(34), ShareData.PxToDpi_xhdpi(34), MakeBmp.POS_CENTER, 0, Config.ARGB_8888));
                        recycleBitmap(tempBmp, true);
                    }
                    mView.setMosaicType(((int[]) tempInfo.m_ex)[1]);
                }
            }
        }
        else
        {

            RecommendAdapter.ItemInfo tempInfo = (RecommendAdapter.ItemInfo) mDoodleAdapter.GetItemInfoByUri(mDoodleTypeUri);
            if (tempInfo != null && tempInfo.m_ex != null)
            {
                mPaintTypeBtn.setCircleIcon(null);
                if (mDoodleTypeUri == RecommendAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI)
                {
                    mDoodleTypeBtn.setCircleIcon(null);
                    return;
                }
                if (mDoodleTypeUri == RecommendAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI)
                {
                    return;
                }
                mDoodleAdapter.SetSelectByUri(mDoodleTypeUri);
                Bitmap tempBmp = cn.poco.imagecore.Utils.DecodeImage(getContext(), ((MosaicRes) tempInfo.m_ex).m_icon, 0, -1, -1, -1);
                if (tempBmp != null)
                {
                    mDoodleTypeBtn.setCircleIcon(MakeBmp.CreateFixBitmap(tempBmp, ShareData.PxToDpi_xhdpi(34), ShareData.PxToDpi_xhdpi(34), MakeBmp.POS_CENTER, 0, Config.ARGB_8888));
                    recycleBitmap(tempBmp, true);
                }
                Bitmap bmp = cn.poco.imagecore.Utils.DecodeImage(getContext(), ((MosaicRes) tempInfo.m_ex).m_img, 0, -1, -1, -1);
                mView.setDoodleType(bmp, (MosaicRes) tempInfo.m_ex);
            }
        }
    }

    private void recycleBitmap(Bitmap bmp, boolean recycle)
    {
        if (bmp != null && !bmp.isRecycled() && recycle)
        {
            bmp.recycle();
            bmp = null;
        }
    }

    /**
     * 如果橡皮擦或者画笔seekbar展开，切换type更新index
     */
    private void changeResFoldIndexState()
    {
        if (isRubberMode)
        {
            if (!isPaintType)
            {
                mDoodleAdapter.CancelSelect();
            }
            else
            {
                mPaintAdapter.CancelSelect();
            }
        }
        else
        {
            if (!isPaintType)
            {
                mDoodleAdapter.SetSelectByUri(mDoodleTypeUri, false, false);
            }
            else
            {
                mPaintAdapter.SetSelectByUri(mPaintTypeUri, false, false);
            }
        }
    }

    private void updateUndoRedoBtnState()
    {
        mUndoCtrl.setCanUndo(mUndoData.CanUndo());
        mUndoCtrl.setCanRedo(mUndoData.CanRedo());
    }

    /*点击切换动画操作*/
    private void SetSwitchSeekAndDoodlePaintViewState(boolean rubberSeek, final boolean isMode)
    {
        mEditScrollSeekFr.clearAnimation();
        mPaintSizeSeekBarFr.clearAnimation();
        mRubberSizeSeekBarFr.clearAnimation();

        mUiEnabled = false;
        int seekStart, seekEnd;
        if (isMode)
        {
            if (rubberSeek)
            {//往上滑动
                mRubberSizeSeekBarFr.setVisibility(VISIBLE);
                //seekbar
                seekStart = 0;
                seekEnd = -ShareData.PxToDpi_xhdpi(136); // 206 - 70
            }
            else
            {
                mPaintSizeSeekBarFr.setVisibility(VISIBLE);
                seekStart = 0;
                seekEnd = ShareData.PxToDpi_xhdpi(136);
            }
        }
        else
        {
            if (rubberSeek)
            {//往下滑动
                seekStart = -ShareData.PxToDpi_xhdpi(136);//206 - 70
                seekEnd = 0;
            }
            else
            {
                seekStart = ShareData.PxToDpi_xhdpi(136);
                seekEnd = 0;
            }
        }

        ObjectAnimator object = ObjectAnimator.ofFloat(mEditScrollSeekFr, "translationY", seekStart, seekEnd);
        object.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                mUiEnabled = true;
            }
        });
        object.setInterpolator(new AccelerateDecelerateInterpolator());
        object.setDuration(300);
        object.start();
    }

    /*点击过度动画操作*/
    private void SetOverSeekAndDoodlePaintViewState(final boolean rubberSeek)
    {
        if (!mUiEnabled) return;
        mPaintSizeSeekBarFr.clearAnimation();
        mRubberSizeSeekBarFr.clearAnimation();
        mDoodlePaintFr.clearAnimation();
        float doodlePaintStart, doodlePaintEnd;
        if (rubberSeek)
        {
            //默认画笔跳转到橡皮擦
            mRubberSizeSeekBarFr.setVisibility(VISIBLE);
            doodlePaintStart = ShareData.PxToDpi_xhdpi(136);
            doodlePaintEnd = -ShareData.PxToDpi_xhdpi(136);

        }
        else
        {
            //默认橡皮擦跳转到画笔
            mPaintSizeSeekBarFr.setVisibility(VISIBLE);
            doodlePaintStart = -ShareData.PxToDpi_xhdpi(136);
            doodlePaintEnd = ShareData.PxToDpi_xhdpi(136);
        }

        mUiEnabled = false;
        ObjectAnimator object1 = ObjectAnimator.ofFloat(mEditScrollSeekFr, "translationY", doodlePaintStart, doodlePaintEnd);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(object1);
        animatorSet.setDuration(300);
        animatorSet.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
//                if (rubberSeek) {
//                    mPaintSizeSeekBarFr.setVisibility(INVISIBLE);
//                } else {
//                    mRubberSizeSeekBarFr.setVisibility(INVISIBLE);
//                }
                mUiEnabled = true;
            }
        });
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    private Bitmap mBtnBgBmp;
    private BitmapDrawable mBtnBgBmpDrawable;

    private BitmapDrawable getmBtnBgBmpDrawable()
    {
        if (mBtnBgBmp == null)
        {
            mBtnBgBmp = ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getResources(), R.drawable.advanced_color_palette_bg));
        }
        if (mBtnBgBmpDrawable == null)
        {
            mBtnBgBmpDrawable = new BitmapDrawable(getResources(), mBtnBgBmp);
        }
        return mBtnBgBmpDrawable;
    }

    private void SetRubberPaintBtnState(boolean rubberMode, boolean paintMode)
    {
        this.isRubberMode = rubberMode;
        this.isPaintMode = paintMode;
        if (rubberMode)
        {
            mRubberBtn.setBackgroundDrawable(getmBtnBgBmpDrawable());
            mRubberBtn.setImageResource(R.drawable.mosaicpage_rubber_btn_selected);
        }
        else
        {
            mRubberBtn.setBackgroundColor(Color.TRANSPARENT);
            mRubberBtn.setImageResource(R.drawable.mosaicpage_rubber_btn_normal);
        }
        if (paintMode)
        {
            mPaintBtn.setBackgroundDrawable(getmBtnBgBmpDrawable());
            mPaintBtn.setImageResource(R.drawable.mosaicpage_paint_btn_selected);
        }
        else
        {
            mPaintBtn.setBackgroundColor(Color.TRANSPARENT);
            mPaintBtn.setImageResource(R.drawable.mosaicpage_paint_btn_normal);
        }
    }

    private MySeekBar.OnProgressChangeListener mPaintSizeSeekBarOnChangeListener = new MySeekBar.OnProgressChangeListener()
    {

        @Override
        public void onStopTrackingTouch(MySeekBar seekBar)
        {
            mView.changingPaintSize(false);
            if (mCirclePanel != null)
            {
                mCirclePanel.hide();
            }
        }

        @Override
        public void onStartTrackingTouch(MySeekBar seekBar)
        {
        }

        @Override
        public void onProgressChanged(MySeekBar seekBar, int progress)
        {
            mView.setMosaicPaintSize(progress);
            mView.changingPaintSize(true);
            if (isRubberMode)
            {
                mRubberSizeProgress = progress;
            }
            else
            {
                mPaintSizeProgress = progress;
            }

            setDrawPaintSize(progress);

            showCircle(seekBar, progress);
        }

        private void showCircle(MySeekBar seekBar, int progress)
        {
//            float radius = progress / 95f * (mMaxRadius - mMinRadius) + mMinRadius;
            float circleX = ShareData.PxToDpi_xhdpi(104 + 55 / 2) + seekBar.getLeft() + seekBar.getMaxDistans() * progress / 100f;
            float circleY = mCirclePanel.getHeight() / 2f - ShareData.PxToDpi_xhdpi(3);
            mCirclePanel.change(circleX, circleY, mMaxRadius);
            mCirclePanel.setText(String.valueOf((int) ((100 - 5) / 100f * progress + 5)));//5 - 100 调节范围
            mCirclePanel.show();
        }
    };

    private void setDrawPaintSize(int progress)
    {
        mView.setPaintSize(getDrawPaintSize(progress));
    }

    private int getDrawPaintSize(int progress)
    {
        int size = (int) (progress / 95f * (mMaxRadius - mMinRadius) + mMinRadius);
        return (int) (size * 1.2);
    }

    private RecommendAdapter.OnItemClickListener mPaintListCallback = new AbsAdapter.OnItemClickListener()
    {
        @Override
        public void OnItemDown(AbsAdapter.ItemInfo info, int index)
        {

        }

        @Override
        public void OnItemUp(AbsAdapter.ItemInfo info, int index)
        {

        }

        @Override
        public void OnItemClick(AbsAdapter.ItemInfo info, int index)
        {
            boolean foldSeekBar = true;
            RecommendAdapter.ItemInfo itemInfo = (RecommendAdapter.ItemInfo) info;
            if (isRubberMode)
            {
                if (mPaintTypeUri == info.m_uri)
                {
//                    if (!isPaintType) {
//                        ((RecommendItemList) mDoodleList.m_view).SetSelectByIndex(index);
//                        mDoodleAdapter.SetSelectByUri(mDoodleTypeUri,false,false);
//                    } else {
//                        mPaintList.m_view.SetSelectByIndex(index);
//                    }
                    foldSeekBar = true;
                }
                //如果用橡皮擦前是画风效果，要更新画风效果的合成用的原图
                if (mPaintTypeUri == itemInfo.m_uri && itemInfo.m_uri == MOSAIC_BASE)
                {
                    Bitmap bmp = filter.mosaicEffect(mView.getOutBmp());
                    mView.setDoodleType(bmp, (MosaicRes) itemInfo.m_ex);
                }
                else if (mPaintTypeUri == itemInfo.m_uri)
                {
                    mView.setMosaicType(((int[]) itemInfo.m_ex)[1]);
                    mView.updateMosaicOrgBmp();
                }
            }
            else
            {
                if (isPaintMode)
                {
                    if (mPaintTypeUri == itemInfo.m_uri)
                    {
//                        if (!isPaintType) {
//                            ((RecommendItemList) mDoodleList.m_view).SetSelectByIndex(index);
//                        } else {
//                            mPaintList.m_view.SetSelectByIndex(index);
//                            mDoodleAdapter.SetSelectByUri(mDoodleTypeUri,false,false);
//                        }
                    }
                    foldSeekBar = true;
                }
                else
                {
                    foldSeekBar = false;
                }
            }

            if (mUiEnabled && mPaintTypeUri != itemInfo.m_uri)
            {
                mDoodleAdapter.CancelSelect();
//                ((RecommendItemList) mDoodleList.m_view).SetSelectByIndex(-1);
                mDoodleTypeBtn.setCircleIcon(null);
                mPaintTypeUri = itemInfo.m_uri;
                //如果是"马赛克基本", 就用涂鸦的技术实现
                if (mPaintTypeUri == MOSAIC_BASE)
                {
                    Bitmap tempBmp = cn.poco.imagecore.Utils.DecodeImage(getContext(), ((MosaicRes) itemInfo.m_ex).m_icon, 0, -1, -1, -1);
                    if (tempBmp != null)
                    {
                        mPaintTypeBtn.setCircleIcon(MakeBmp.CreateFixBitmap(tempBmp, ShareData.PxToDpi_xhdpi(34), ShareData.PxToDpi_xhdpi(34), MakeBmp.POS_CENTER, 0, Config.ARGB_8888));
                        recycleBitmap(tempBmp, true);
                    }
                    Bitmap bmp = filter.mosaicEffect(mView.getOutBmp());
                    mView.setDoodleType(bmp, (MosaicRes) itemInfo.m_ex);
                }
                else
                {
                    Bitmap tempBmp = cn.poco.imagecore.Utils.DecodeImage(getContext(), ((int[]) itemInfo.m_ex)[0], 0, -1, -1, -1);
                    if (tempBmp != null)
                    {
                        mPaintTypeBtn.setCircleIcon(MakeBmp.CreateFixBitmap(tempBmp, ShareData.PxToDpi_xhdpi(34), ShareData.PxToDpi_xhdpi(34), MakeBmp.POS_CENTER, 0, Config.ARGB_8888));
                        recycleBitmap(tempBmp, true);
                    }
                    mView.setMosaicType(((int[]) itemInfo.m_ex)[1]);
                }
            }

            if (foldSeekBar)
            {
                isRubberMode = false;
                SetRubberPaintBtnState(false, false);
                SetSwitchSeekAndDoodlePaintViewState(isRubberSeek, false);
                mView.setRubberMode(isRubberMode);
                setDrawPaintSize(mPaintSizeProgress);
                mView.changingPaintSize(false);
                mUiEnabled = true;
                isRubberSeek = false;
            }
        }
    };
    private int mPaintDownloadingId = -1;
    private RecommendAdapter.OnItemClickListener mDoodleListCallback = new AbsAdapter.OnItemClickListener()
    {
        @Override
        public void OnItemDown(AbsAdapter.ItemInfo info, int index)
        {

        }

        @Override
        public void OnItemUp(AbsAdapter.ItemInfo info, int index)
        {

        }

        @Override
        public void OnItemClick(AbsAdapter.ItemInfo info, int index)
        {
            boolean foldSeekBar = true;
            if (isRubberMode)
            {
//                reIndexSelectedByDoodle(info, index);
                foldSeekBar = true; //关闭seekbar
            }
            else
            {
                if (isPaintMode)
                {
//                    reIndexSelectedByDoodle(info, index);
                    foldSeekBar = true;
                }
                else
                {
                    foldSeekBar = isPaintType;
                }
            }
            if (mUiEnabled && info.m_uri == RecommendAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI)
            {
                //下载更多
                TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_马赛克_下载更多);
                openDownloadMorePage(ResType.MOSAIC);
                return;
            }
            if (mUiEnabled && info.m_uri == RecommendAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI)
            {
                //推荐位
                ArrayList<RecommendRes> ress = (ArrayList<RecommendRes>) (((RecommendAdapter.ItemInfo) info).m_ex);
                RecommendRes recommendRes = null;
                if (ress != null && ress.size() > 0)
                {
                    recommendRes = ress.get(0);
                }
                if (recommendRes != null && m_recomView != null)
                {
                    showRecommendView(m_recomView, recommendRes, ResType.MOSAIC.GetValue());
                }
                return;
            }
            if (mUiEnabled && mDoodleTypeUri != info.m_uri)
            {
                switch (((RecommendAdapter.ItemInfo) info).m_style)
                {
                    case NEW:
                    case NORMAL:
                    {
                        mPaintTypeBtn.setCircleIcon(null);
                        mDoodleTypeUri = info.m_uri;
                        mPaintDownloadingId = mDoodleTypeUri;
                        Bitmap tempBmp = cn.poco.imagecore.Utils.DecodeImage(getContext(), ((MosaicRes) ((RecommendAdapter.ItemInfo) info).m_ex).m_icon, 0, -1, -1, -1);
                        if (tempBmp != null)
                        {
                            mDoodleTypeBtn.setCircleIcon(MakeBmp.CreateFixBitmap(tempBmp, ShareData.PxToDpi_xhdpi(34), ShareData.PxToDpi_xhdpi(34), MakeBmp.POS_CENTER, 0, Config.ARGB_8888));
                            recycleBitmap(tempBmp, true);
                        }
                        Bitmap bmp = cn.poco.imagecore.Utils.DecodeImage(getContext(), ((MosaicRes) ((RecommendAdapter.ItemInfo) info).m_ex).m_img, 0, -1, -1, -1);
                        mView.setDoodleType(bmp, (MosaicRes) ((RecommendAdapter.ItemInfo) info).m_ex);
                        break;
                    }

                    case NEED_DOWNLOAD:
                    {
                        RecommendAdapter.ItemInfo itemInfo = (RecommendAdapter.ItemInfo) info;
                        if (itemInfo.m_isLock && TagMgr.CheckTag(getContext(), Tags.MOSAIC_PAINT_UNLOCK_ID_FLAG + itemInfo.m_uri))
                        {
                            ArrayList<LockRes> lockArr = LockResMgr2.getInstance().getMosaicLockArr();
                            if (lockArr != null)
                            {
                                for (LockRes lockRes : lockArr)
                                {
                                    if (lockRes.m_id == itemInfo.m_uri && lockRes.m_shareType != LockRes.SHARE_TYPE_NONE && TagMgr.CheckTag(getContext(), Tags.MOSAIC_PAINT_UNLOCK_ID_FLAG + lockRes.m_id))
                                    {
                                        if (m_recomView != null)
                                        {
                                            showRecommendView(m_recomView, lockRes, 0);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        else
                        {
                            mPaintDownloadingId = itemInfo.m_uri;
                            DownloadMgr.getInstance().DownloadRes((MosaicRes) itemInfo.m_ex, new DownloadMgr.Callback()
                            {

                                @Override
                                public void OnProgress(int downloadId, IDownload res, int progress)
                                {
                                }

                                @Override
                                public void OnFail(int downloadId, IDownload res)
                                {
                                    Toast.makeText(getContext(), getResources().getString(R.string.material_download_failed), Toast.LENGTH_SHORT).show();
                                    if (mDoodleAdapter != null)
                                    {
                                        mDoodleAdapter.SetItemStyleByUri(((BaseRes) res).m_id, RecommendAdapter.ItemInfo.Style.NEED_DOWNLOAD);
                                    }
                                }

                                @Override
                                public void OnComplete(int downloadId, IDownload res)
                                {
                                    cn.poco.resource.MosaicResMgr2.getInstance().AddNewFlag(getContext(), ((BaseRes) res).m_id);
                                    if (mDoodleAdapter != null)
                                    {
                                        mDoodleAdapter.SetItemStyleByUri(((BaseRes) res).m_id, RecommendAdapter.ItemInfo.Style.NEW);
                                        if (mPaintDownloadingId == ((BaseRes) res).m_id)
                                        {
                                            mDoodleAdapter.SetSelectByUri(((BaseRes) res).m_id);
                                        }
                                    }
                                }
                            });
                            mDoodleAdapter.SetItemStyleByUri(info.m_uri, RecommendAdapter.ItemInfo.Style.LOADING);
                        }
                        break;
                    }
                    default:
                        break;
                }
            }

            if (foldSeekBar)
            {
                isRubberMode = false;
                SetRubberPaintBtnState(false, false);
                SetSwitchSeekAndDoodlePaintViewState(isRubberSeek, false);
                mView.setRubberMode(isRubberMode);
                setDrawPaintSize(mPaintSizeProgress);
                mView.changingPaintSize(false);
                mUiEnabled = true;
                isRubberSeek = false;
            }
        }
    };

    private void SetViewFrState(boolean isFold)
    {
        mView.clearAnimation();
        int start, end;
        if (isFold)
        {
            start = m_viewHeight;
            end = start + mBottomLayoutHeight;
        }
        else
        {
            start = ShareData.m_screenHeight - mBottomBarHeight;
            end = m_viewHeight;
        }
        mUiEnabled = false;

        ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                if (mView != null)
                {
                    LayoutParams fp = (LayoutParams) mView.getLayoutParams();
                    fp.height = (int) animation.getAnimatedValue();
                    mView.setLayoutParams(fp);
                }
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                mUiEnabled = true;
            }
        });
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.start();
    }

    public void SetBottomFrState(boolean isFold)
    {
        mBottomFr.clearAnimation();
        int start;
        int end;
        start = isFold ? 0 : mBottomLayoutHeight;
        end = isFold ? mBottomLayoutHeight : 0;
        ObjectAnimator object = ObjectAnimator.ofFloat(mBottomFr, "translationY", start, end);
        object.setDuration(300);
        object.setInterpolator(new AccelerateDecelerateInterpolator());
        object.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mUiEnabled = false;
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                mUiEnabled = true;
            }
        });
        object.start();
    }

    private final int KEY_PAINT_TJ = 1;
    private final int KEY_DOODLE_TJ = 0;

    protected SparseIntArray mTongjiArr = new SparseIntArray();

    public void SetImg(Object params)
    {
        if (params != null)
        {
            if (params instanceof RotationImg2[])
            {
                Bitmap temp = cn.poco.imagecore.Utils.DecodeImage(getContext(), ((RotationImg2[]) params)[0].m_img, ((RotationImg2[]) params)[0].m_degree, -1, m_viewWidth, m_viewHeight);
                mOrgBmp = MakeBmpV2.CreateBitmapV2(temp, ((RotationImg2[]) params)[0].m_degree, ((RotationImg2[]) params)[0].m_flip, -1, m_viewWidth, m_viewHeight, Config.ARGB_8888);
                if (temp != null && !temp.isRecycled())
                {
                    temp.recycle();
                    temp = null;
                }
            }
            else if (params instanceof Bitmap)
            {
                this.mOrgBmp = (Bitmap) params;
            }

            if (mOrgBmp != null)
            {
                mShowBmp = cutOrgBmp(mOrgBmp);
                setImageBmp(mShowBmp);
            }

            if (mDoodleTypeUri > 0)
            {
                int tempUri = mDoodleTypeUri;
                mDoodleTypeUri = 0;
                if (mDoodleAdapter != null)
                {
                    mDoodleAdapter.SetSelectByUri(tempUri, false, true);
                }
            }
        }
    }

    private Bitmap cutOrgBmp(Bitmap src)
    {
        if (src == null) return null;
        int screenMax = Math.max(ShareData.m_screenWidth, ShareData.m_screenHeight);
        int srcMax = Math.max(src.getWidth(), src.getHeight());

        if (screenMax >= 1920)
        {
            if (srcMax > 1600)
            {
                srcMax = 1600;
            }
            else
            {
                return src;
            }
        }
        else if (screenMax >= 1280)
        {
            if (srcMax > 1024)
            {
                srcMax = 1024;
            }
            else
            {
                return src;
            }
        }
        else
        {
            if (srcMax > 1024)
            {
                srcMax = 1024;
            }
            else
            {
                return src;
            }
        }

//        int outW = 0, outH = 0;
//        if (srcMax == 1024) {
//            if (src.getWidth() > src.getHeight()) {
//                outW = 1024;
//                outH = (int) ((float) outW / (float) src.getWidth() * (float) src.getHeight());
//            } else {
//                outH = 1024;
//                outW = (int) ((float) outH / (float) src.getHeight() * (float) src.getWidth());
//            }
//        } else if (srcMax == 1600) {
//            if (src.getWidth() > src.getHeight()) {
//                outW = 1600;
//                outH = (int) ((float) outW / (float) src.getWidth() * (float) src.getHeight());
//            } else {
//                outH = 1600;
//                outW = (int) ((float) outH / (float) src.getHeight() * (float) src.getWidth());
//            }
//        } else {
//            outW = src.getWidth();
//            outH = src.getHeight();
//        }
//        Log.d("bbb", "cutOrgBmp: srcMax:" + srcMax + ", screenMax:" + screenMax + ", outW:" + outW + ", outH:" + outH);
        Bitmap temp = MakeBmpV2.CreateBitmapV2(src, 0, MakeBmpV2.FLIP_NONE, -1, srcMax, srcMax, Config.ARGB_8888);
        return temp;
    }

    public void setImageBmp(final Bitmap orgBmp)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String path = FileCacheMgr.GetLinePath();
                if (Utils.SaveTempImg(orgBmp, path))
                {
                    mUndoData.AddData(path);
                }
            }
        }).start();

        mView.setImageViewBitmap(orgBmp);
        mView.setMosaicType(PocoOilMask.Vangogh);

        ShowStartAnim();

        updateUndoRedoBtnState();
    }

    private void ShowStartAnim()
    {
        if (m_viewH > 0 && m_imgH > 0)
        {
            int tempStartY = (int) (ShareData.PxToDpi_xhdpi(90) + (m_viewH - m_viewHeight) / 2f);
            float scaleX = (float) m_viewWidth / (float) mShowBmp.getWidth();
            float scaleY = (float) m_viewHeight / (float) mShowBmp.getHeight();
            m_currImgH = mShowBmp.getHeight() * Math.min(scaleX, scaleY);
            float scaleH = m_imgH / m_currImgH;
            ShowViewAnim(mView, tempStartY, 0, scaleH, 1f, SHOW_VIEW_ANIM);
        }
    }

    private void ShowViewAnim(final View view, int startY, int endY, float startScale, float endScale, int duration)
    {
        if (view != null)
        {
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator object1 = ObjectAnimator.ofFloat(view, "scaleX", startScale, endScale);
            ObjectAnimator object2 = ObjectAnimator.ofFloat(view, "scaleY", startScale, endScale);
            ObjectAnimator object3 = ObjectAnimator.ofFloat(view, "translationY", startY, endY);
            ObjectAnimator object4 = ObjectAnimator.ofFloat(mBottomFr, "translationY", mBottomBarHeight + mBottomLayoutHeight, 0);
            animatorSet.setDuration(duration);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.playTogether(object1, object2, object3, object4);
            animatorSet.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mUiEnabled = true;
                    view.clearAnimation();
                    mBottomFr.clearAnimation();
                }
            });
            animatorSet.start();
        }
    }

    public void releaseMem()
    {
        if (mImageThread != null)
        {
            mImageThread.quit();
            mImageThread = null;
        }

        if (mImageHandler != null)
        {
            mImageHandler.removeMessages(MSG_SAVE_CHCHE);
            mImageHandler = null;
        }

        if (mUIHandler != null)
        {
            mUIHandler.removeMessages(MSG_SAVE_CHCHE);
            mUIHandler = null;
        }

        if (mView != null)
        {
            removeView(mView);
            mView.ReleaseMem();
            mView = null;
        }

        if (mPaintTypeBtn != null)
        {
            mPaintTypeBtn.releaseMem();
        }
        if (mDoodleTypeBtn != null)
        {
            mDoodleTypeBtn.releaseMem();
        }
        if (DownloadMgr.getInstance() != null)
        {
            DownloadMgr.getInstance().RemoveDownloadListener(m_downloadLst);
        }

        if (mBtnBgBmpDrawable != null)
        {
            mBtnBgBmpDrawable = null;
        }

        if (mBtnBgBmp != null && !mBtnBgBmp.isRecycled())
        {
            mBtnBgBmp.recycle();
            mBtnBgBmp = null;
        }
    }

    private void removeShowView()
    {
        if (mView != null)
        {
            removeView(mView);
            mView.ReleaseMem();
            mView = null;
        }
    }

    @Override
    public void onBack()
    {
        if (mOnAnimationClickListener != null)
        {
            mOnAnimationClickListener.onAnimationClick(mCancelBtn);
        }
    }

    /**
     * @param params imgs : RotationImg[] / Bitmap
     */
    @Override
    public void SetData(HashMap<String, Object> params)
    {
        if (params != null)
        {
            Object o;
            o = params.get(DataKey.BEAUTIFY_DEF_SEL_URI);
            if (o != null && o instanceof Integer)
            {
                isSelUri = true;
                int temp = (int) params.get(DataKey.BEAUTIFY_DEF_SEL_URI);
                if (temp != 0)
                {
                    mDoodleTypeUri = temp;
                }
            }

            o = params.get(Beautify4Page.PAGE_ANIM_IMG_H);
            if (o != null && o instanceof Integer)
            {
                m_imgH = (int) o;
            }

            o = params.get(Beautify4Page.PAGE_ANIM_VIEW_H);
            if (o != null && o instanceof Integer)
            {
                m_viewH = (int) o;
            }

            o = params.get(Beautify4Page.PAGE_ANIM_VIEW_TOP_MARGIN);
            if (o != null && o instanceof Integer)
            {
                m_viewTopMargin = (int) o;
            }

            if (params.get("imgs") != null)
            {
                SetImg(params.get("imgs"));
            }
        }
    }

    @Override
    public void onClose()
    {
        onSaveParams();
        clearExitDialog();

        if (m_recomView != null)
        {
            m_recomView.ClearAllaa();
            m_recomView = null;
        }
        if (mDoodleAdapter != null)
        {
            mDoodleAdapter.ClearAll();
            mDoodleAdapter = null;
        }
        if (mPaintAdapter != null)
        {
            mPaintAdapter.ClearAll();
            mPaintAdapter = null;
        }
        releaseMem();
        this.removeAllViews();

        MyBeautyStat.onPageEndByRes(R.string.美颜美图_马赛克页面_主页面);
        TongJiUtils.onPageEnd(getContext(), R.string.马赛克);
    }

    @Override
    public void onPause()
    {
        TongJiUtils.onPagePause(getContext(), R.string.马赛克);
        super.onPause();
    }

    @Override
    public void onResume()
    {
        TongJiUtils.onPageResume(getContext(), R.string.马赛克);
        super.onResume();
    }

    public void onSaveParams()
    {
        TagMgr.SetTagValue(getContext(), Tags.MOSAIC_PAINT_PROGRESS_SIZE, Integer.toString(mPaintSizeProgress));
        TagMgr.SetTagValue(getContext(), Tags.MOSAIC_RUBBER_PROGRESS_SIZE, Integer.toString(mRubberSizeProgress));
        TagMgr.Save(getContext());
    }

  /*  public class SaveCacheMessageInfo {
        private boolean finished = false;
        private String path;
        private Bitmap bmp;
    }*/

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params)
    {
        if (siteID == SiteID.DOWNLOAD_MORE)
        {
            if (params != null)
            {
                ResType type = (ResType) params.get(DownloadMorePageSite.DOWNLOAD_MORE_TYPE);
                boolean del = false;
                {
                    Object obj = params.get(DownloadMorePageSite.DOWNLOAD_MORE_DEL);
                    if (obj instanceof Boolean)
                    {
                        del = (Boolean) obj;
                    }
                }
                switch (type)
                {
                    case MOSAIC:
                        if (del)
                        {
                            mDoodleListInfos = MosaicResMgr.getMosaicRes(getContext());
                            mDoodleAdapter.SetData(mDoodleListInfos);
                            mDoodleAdapter.notifyDataSetChanged();
                        }
                        break;
                    default:
                        break;
                }

                if (params.get(DataKey.BEAUTIFY_DEF_SEL_URI) != null)
                {
                    int uri = (int) params.get(DataKey.BEAUTIFY_DEF_SEL_URI);
                    if (del && uri == 0)
                    {
                        //删除素材并且没有指定素材后，沿用原素材id
                        if (!isPaintType)
                        {
                            uri = mDoodleTypeUri;
                        }
                    }

                    if (!isPaintType)
                    {
                        boolean isScroll2Center = false;
                        if (uri == 0)
                        {
                            uri = MosaicResMgr.getResFirstUri(mDoodleListInfos);
                        }
                        else
                        {
                            isScroll2Center = true;
                        }
                        mDoodleAdapter.SetSelectByUri(uri, isScroll2Center, true);
                    }
                }
            }
        }
        if (siteID == SiteID.LOGIN || siteID == SiteID.REGISTER_DETAIL)
        {
            if (m_recomView != null)
            {
                m_recomView.UpdateCredit();
            }
        }

        super.onPageResult(siteID, params);
    }

    private void cancel()
    {
        if (m_site != null)
        {
            HashMap<String, Object> params = new HashMap<>();
            params.put("img", mOrgBmp);
            params.putAll(getBackAnimParam());
            removeShowView();
            m_site.OnBack(getContext(), params);
        }
    }

    private HashMap<String, Object> getBackAnimParam()
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put(Beautify4Page.PAGE_BACK_ANIM_VIEW_TOP_MARGIN, (mView.getHeight() - m_viewHeight) / 2f);
        params.put(Beautify4Page.PAGE_BACK_ANIM_IMG_H, mView.getImgHeight());
        return params;
    }

    private void openDownloadMorePage(ResType resType)
    {
        MyBeautyStat.onClickByRes(R.string.美颜美图_马赛克页面_主页面_下载更多);
        if (m_site != null) m_site.OpenDownloadMore(getContext(), resType);
    }

    private void showRecommendView(RecomDisplayMgr recomDisplayMgr, BaseRes baseRes, int type)
    {
        //推荐位统计
        MyBeautyStat.onClickByRes(R.string.美颜美图_马赛克页面_主页面_马赛克推荐位);
        TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_马赛克_推荐位);
        if (recomDisplayMgr != null && baseRes != null)
        {
            recomDisplayMgr.SetBk(CommonUtils.GetScreenBmp((Activity) getContext(), ShareData.m_screenWidth / 8, ShareData.m_screenHeight / 8), true);
            recomDisplayMgr.SetDatas(baseRes, type);
            recomDisplayMgr.Show();
        }
    }

    private CloudAlbumDialog mExitDialog;

    private void showExitDialog()
    {
        if (mExitDialog == null)
        {
            mExitDialog = new CloudAlbumDialog(getContext(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ImageUtils.AddSkin(getContext(), mExitDialog.getOkButtonBg());
            mExitDialog.setCancelable(true).setCancelButtonText(R.string.cancel).setOkButtonText(R.string.ensure).setMessage(R.string.confirm_back).setListener(new CloudAlbumDialog.OnButtonClickListener()
            {
                @Override
                public void onOkButtonClick()
                {
                    if (mExitDialog != null)
                    {
                        mExitDialog.dismiss();
                    }
                    cancel();
                }

                @Override
                public void onCancelButtonClick()
                {
                    if (mExitDialog != null)
                    {
                        mExitDialog.dismiss();
                    }
                }
            });
        }
        mExitDialog.show();
    }

    private void clearExitDialog()
    {
        if (mExitDialog != null)
        {
            mExitDialog.dismiss();
            mExitDialog.setListener(null);
            mExitDialog = null;
        }
    }
}