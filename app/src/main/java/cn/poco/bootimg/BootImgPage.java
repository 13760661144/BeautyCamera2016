package cn.poco.bootimg;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.adnonstop.admasterlibs.data.AbsBootAdRes;

import java.io.IOException;
import java.util.HashMap;

import cn.poco.adMaster.ShareAdBanner;
import cn.poco.advanced.ImageUtils;
import cn.poco.banner.BannerCore3;
import cn.poco.bootimg.site.BootImgPageSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.home.home4.utils.PercentUtil;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.ConfigIni;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

public class BootImgPage extends IPage
{
    protected BootImgPageSite m_site;

    protected boolean m_uiEnabled;
    protected AbsBootAdRes m_res;

    protected FullScreenView mFullScreenView;
    protected boolean m_openMyWeb = false;
    protected boolean m_openSysWeb = false;
    //	protected VideoView mVideo;
    private AutoFitSurfaceView surfaceView;
    private MediaPlayer mVideo;
    private SurfaceHolder holder;

    protected ImageView mBack;

    protected boolean mAutoClose;
    protected boolean mOpenAnim;
    protected boolean mHasMarketLogo;

    protected FrameLayout mBottomFr;
    protected SkipBtn mSkipBtn;
    protected ImageView mMarketLogo;
    protected ImageView mMyLogo;

    private boolean isVideoFinished = false;

    public BootImgPage(Context context, BaseSite site)
    {
        super(context, site);

        m_site = (BootImgPageSite) site;
        InitData();
        InitUI();

        TongJiUtils.onPageStart(getContext(), R.string.开机广告);
    }

    protected void InitData()
    {
        ShareData.InitData((Activity) getContext());

        this.setWillNotDraw(false);

        m_uiEnabled = true;
    }

    protected void InitUI()
    {
        ShareData.InitData((Activity) getContext());

        this.setBackgroundColor(0xFFFFFFFF);

        this.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    if (m_uiEnabled && m_res != null && m_res.mClick != null && m_res.mClick.length() > 0)
                    {
                        TongJi2.AddCountByRes(getContext(), R.integer.首页_开机画面);

                        BannerCore3.OpenUrl(getContext(), m_res.mClick, new BannerCore3.OpenUrlCallback()
                        {
                            @Override
                            public void OpenMyWeb(Context context, String url)
                            {
                                m_openMyWeb = true;
                                m_site.OnMyWeb(getContext(), url);
                            }

                            @Override
                            public void OpenSystemWeb(Context context, String url)
                            {
                                m_openSysWeb = true;
                                m_site.OnSystemWeb(context, url);
                                if (mAutoClose)
                                {
                                    m_site.OnHome(getContext(), true, m_res);
                                }
                            }
                        });
                    }
                } catch (Throwable e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * img:BootImgRes 商业资源<br/>
     * auto_close:Boolean 是否自动关闭<br/>
     * open_anim:Boolean 是否有打开动画<br/>
     * has_market_logo:Boolean 是否有市场logo<br/>
     */
    @Override
    public void SetData(HashMap<String, Object> params)
    {
        Object obj;
        obj = params.get("auto_close");
        if (obj instanceof Boolean)
        {
            mAutoClose = (Boolean) obj;
        }
        obj = params.get("open_anim");
        if (obj instanceof Boolean)
        {
            mOpenAnim = (Boolean) obj;
        }
        obj = params.get("has_market_logo");
        if (obj instanceof Boolean)
        {
            mHasMarketLogo = (Boolean) params.get("has_market_logo");
        }

        FrameLayout.LayoutParams fl;
        mBottomFr = new FrameLayout(getContext());
        mBottomFr.setBackgroundColor(0xffffffff);
        fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(232));
        fl.gravity = Gravity.LEFT | Gravity.BOTTOM;
        this.addView(mBottomFr, fl);
        mBottomFr.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });

        int res = 0;
        String ver = SysConfig.GetAppVer(getContext());
        if (mHasMarketLogo && ver != null && ConfigIni.showChannelLogo)
        {
            if (ver.endsWith("_r3"))
            {
                res = R.drawable.main_welcome_logo_r3;
            } else if (ver.endsWith("_r10"))
            {
                res = R.drawable.main_welcome_logo_r10;
            } else if (ver.endsWith("_r12"))
            {
                res = R.drawable.main_welcome_logo_r12;
            } else if (ver.endsWith("_r18"))
            {
                res = R.drawable.main_welcome_logo_r18;
            } else if (ver.endsWith("_r19"))
            {
                res = R.drawable.main_welcome_logo_r19;
            } else if (ver.endsWith("_r20"))
            {
                res = R.drawable.main_welcome_logo_r20;
            } else if (ver.endsWith("_r31"))
            {
                res = R.drawable.main_welcome_logo_r31;
            } else if (ver.endsWith("_r33"))
            {
                //res = R.drawable.main_welcome_logo_r33;
                res = R.drawable.main_welcome_logo_r33_2;
            } else if (ver.endsWith("_r34"))
            {
                res = R.drawable.main_welcome_logo_r34;
            } else if (ver.endsWith("_r35"))
            {
                res = R.drawable.main_welcome_logo_r35;
            } else if (ver.endsWith("_r39"))
            {
                res = R.drawable.main_welcome_logo_r39;
            } else if (ver.endsWith("_r40"))
            {
                res = R.drawable.main_welcome_logo_r40;
            }
        }

        int showTime = 800;
        if (params != null && (m_res = (AbsBootAdRes) params.get("boot_img")) != null && !ConfigIni.hideBusiness)
        {
            ShareAdBanner.SendTj(getContext(), m_res.mShowTjs);
            if (m_res.mAdm != null && m_res.mAdm.length > 0)
            {
                boolean isVideo = false;
                if (m_res.mAdm[0] instanceof String)
                {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(m_res.mAdm[0], options);
                    if (options.outMimeType == null)
                    {
                        isVideo = true;
                    }
                }

                if (isVideo)
                {
                    surfaceView = new AutoFitSurfaceView(getContext());
                    int width = 0, height = 0;
                    try
                    {
                        String widthStr, heightStr;
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(m_res.mAdm[0]);
                        widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                        heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);

                        width = Integer.valueOf(widthStr);
                        height = Integer.valueOf(heightStr);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                   surfaceView.setAspectRatio(width, height);

                    //填满显示区域
                    if(width > 0 && height > 0)
                    {
                        float scale1 = (float)ShareData.getScreenW() / (float)width;
                        float scale2 = (float)(ShareData.getScreenH() - ShareData.PxToDpi_xhdpi(232)) / (float)height;
                        float scale = scale1 > scale2 ? scale1 : scale2;
                        int w = (int)Math.ceil(width * scale);
                        int h = (int)Math.ceil(height * scale);
                        int x = (ShareData.getScreenW() - w) / 2;
                        int y = (ShareData.getScreenH() - ShareData.PxToDpi_xhdpi(232) - h) / 2;
                        fl = new LayoutParams(w, h);
                        fl.gravity = Gravity.LEFT | Gravity.TOP;
                        fl.leftMargin = x;
                        fl.topMargin = y;
                    }
                    else
                    {
                        fl = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                    }
                    if (mAutoClose)
                    {
                        this.setVisibility(View.INVISIBLE);
                    }
                    addView(surfaceView, 0, fl);
                    mVideo = new MediaPlayer();
                    mVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
                    {
                        @Override
                        public void onPrepared(MediaPlayer mp)
                        {
                            mp.setOnInfoListener(new MediaPlayer.OnInfoListener()
                            {
                                @Override
                                public boolean onInfo(MediaPlayer mp, int what, int extra)
                                {
                                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START)
                                    {
                                        if (mAutoClose)
                                        {
                                            BootImgPage.this.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    return true;
                                }
                            });
                            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                            {
                                @Override
                                public void onCompletion(MediaPlayer mp)
                                {
                                    isVideoFinished = true;
                                }
                            });
                            if(!isVideoFinished)
                            {
                                mVideo.seekTo(mVideoPos);
                            }
                            mVideo.start();
                        }
                    });
                    holder = surfaceView.getHolder();
                    holder.addCallback(new SurfaceHolder.Callback()
                    {
                        @Override
                        public void surfaceCreated(SurfaceHolder holder)
                        {
                            mVideo.setDisplay(holder);
                            try
                            {
                                mVideo.reset();
                                mVideo.setDataSource(m_res.mAdm[0]);
                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                            mVideo.prepareAsync();
                        }

                        @Override
                        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
                        {

                        }

                        @Override
                        public void surfaceDestroyed(SurfaceHolder holder)
                        {

                        }
                    });
                } else
                {
                    mFullScreenView = new FullScreenView(getContext());
                    fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                    fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                    fl.bottomMargin = ShareData.PxToDpi_xhdpi(232);
                    this.addView(mFullScreenView, 0, fl);
                    mFullScreenView.SetData(m_res.mAdm[0]);
                }
            }
            showTime = m_res.mShowTime;

            //下面的logo
            mMyLogo = new ImageView(getContext());
            mMyLogo.setImageResource(R.drawable.bootimgpage_my_logo);
            mMyLogo.setScaleType(ImageView.ScaleType.CENTER);
            ImageUtils.AddSkin(getContext(), mMyLogo);
            fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.CENTER;
            mBottomFr.addView(mMyLogo, fl);

            ///右上角广告提示
            ImageView mAdTip = new ImageView(getContext());
            mAdTip.setImageResource(R.drawable.bootimgpage_ad_tip);
            fl = new FrameLayout.LayoutParams(PercentUtil.WidthPxxToPercent(48), PercentUtil.HeightPxxToPercent(27));
            fl.gravity = Gravity.RIGHT | Gravity.TOP;
            fl.rightMargin = PercentUtil.WidthPxToPercent(6);
            fl.topMargin = PercentUtil.HeightPxToPercent(6);
            addView(mAdTip, fl);


            if (mAutoClose)
            {
                //跳过按钮
                mSkipBtn = new SkipBtn(getContext());
                fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                //fl.gravity = Gravity.RIGHT | Gravity.BOTTOM;
                fl.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                fl.rightMargin = ShareData.PxToDpi_xhdpi(24);
                //fl.bottomMargin = (ShareData.m_screenHeight - ShareData.PxToDpi_xxhdpi(1575) - ShareData.PxToDpi_xhdpi(32)) / 2;
                //this.addView(mSkipBtn, fl);
                mBottomFr.addView(mSkipBtn, fl);
                mSkipBtn.setOnTouchListener(new OnAnimationClickListener()
                {
                    @Override
                    public void onAnimationClick(View v)
                    {
                        OnHome();
                    }

                    @Override
                    public void onTouch(View v)
                    {
                    }

                    @Override
                    public void onRelease(View v)
                    {
                    }
                });
                mSkipBtn.SetSkipTime(showTime);
            }

            //市场logo
            if (res != 0)
            {
                mMarketLogo = new ImageView(getContext());
                mMarketLogo.setImageResource(res);
                mMarketLogo.setScaleType(ImageView.ScaleType.FIT_CENTER);
                fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xxhdpi(342), ShareData.PxToDpi_xxhdpi(134));
                fl.gravity = Gravity.LEFT | Gravity.BOTTOM;
                mBottomFr.addView(mMarketLogo, fl);
            }
        } else
        {
            //4.0 百分比布局
            mMyLogo = new ImageView(getContext());
            mMyLogo.setImageResource(R.drawable.bootimgpage_start_logo);
            mMyLogo.setScaleType(ImageView.ScaleType.FIT_CENTER);
            fl = new FrameLayout.LayoutParams(PercentUtil.WidthPxxToPercent(641), PercentUtil.HeightPxxToPercent(321));
            fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
            fl.topMargin = PercentUtil.HeightPxxToPercent(298);
            this.addView(mMyLogo, fl);

            ImageView centerText = new ImageView(getContext());
            centerText.setImageResource(R.drawable.bootimgpage_start_text);
            centerText.setScaleType(ImageView.ScaleType.FIT_CENTER);
            fl = new FrameLayout.LayoutParams(PercentUtil.WidthPxxToPercent(57), PercentUtil.HeightPxxToPercent(670));
            fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
            fl.topMargin = PercentUtil.HeightPxxToPercent(298 + 321 + 176);
            this.addView(centerText, fl);

            ImageView copyright = new ImageView(getContext());
            copyright.setImageResource(R.drawable.bootimgpage_start_copyright);
            copyright.setScaleType(ImageView.ScaleType.FIT_CENTER);
            fl = new FrameLayout.LayoutParams(PercentUtil.WidthPxxToPercent(184), PercentUtil.HeightPxxToPercent(19));
            fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            fl.bottomMargin = PercentUtil.HeightPxxToPercent(70);
            this.addView(copyright, fl);

            if (mOpenAnim && android.os.Build.VERSION.SDK_INT >= 17)
            {
                AlphaAnimation aa = new AlphaAnimation(0, 1);
                aa.setDuration(600);
                mMyLogo.startAnimation(aa);

                centerText.startAnimation(aa);
                copyright.startAnimation(aa);

            }

            //市场logo
            if (res != 0)
            {
                mMarketLogo = new ImageView(getContext());
                mMarketLogo.setImageResource(res);
                mMarketLogo.setScaleType(ImageView.ScaleType.FIT_CENTER);
                fl = new FrameLayout.LayoutParams(PercentUtil.WidthPxxToPercent(420), PercentUtil.HeightPxxToPercent(165));
                fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
                fl.bottomMargin = PercentUtil.HeightPxxToPercent(90);
                mBottomFr.addView(mMarketLogo, fl);
            }
        }

        if (mAutoClose)
        {
            this.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    if (!m_openMyWeb && !m_openSysWeb)
                    {
                        OnHome();
                    }
                }
            }, showTime);
        } else
        {
            //显示后退按钮
            mBack = new ImageView(getContext());
            mBack.setImageResource(R.drawable.business_btn_back);
            fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.LEFT | Gravity.TOP;
            fl.leftMargin = ShareData.PxToDpi_xhdpi(30);
            fl.topMargin = ShareData.PxToDpi_xhdpi(30);
            this.addView(mBack, fl);
            mBack.setOnTouchListener(new OnAnimationClickListener()
            {
                @Override
                public void onAnimationClick(View v)
                {
                    if (m_uiEnabled)
                    {
                        onBack();
                        //m_uiEnabled = false;
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
            });
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        if (top > 0)
        {
            top = 0;
        }
        if (bottom > ShareData.m_screenHeight)
        {
            bottom = ShareData.m_screenHeight;
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    protected void OnHome()
    {
        if (m_uiEnabled)
        {
            m_site.OnHome(getContext(), true, m_res);
            m_uiEnabled = false;
        }
    }

    @Override
    public void onBack()
    {
        if (m_uiEnabled)
        {
            m_site.OnBack(getContext());
        }
    }

    @Override
    public void onClose()
    {
        if (mFullScreenView != null)
        {
            mFullScreenView.ClearAll();
            mFullScreenView = null;
        }

        if (mVideo != null)
        {
//			mVideo.stopPlayback();
            mVideo.stop();
            mVideo.release();
            mVideo = null;
        }

        TongJiUtils.onPageEnd(getContext(), R.string.开机广告);
        super.onClose();
    }

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params)
    {
        super.onPageResult(siteID, params);

        if (m_openMyWeb)
        {
            m_site.OnHome(getContext(), false, m_res);
        }
    }

    public void changeThemeSkin()
    {
        ImageUtils.AddSkin(getContext(), mMyLogo);
    }

    protected int mVideoPos;

    @Override
    public void onResume()
    {
        TongJiUtils.onPageResume(getContext(), R.string.开机广告);
    }

    @Override
    public void onPause()
    {
        TongJiUtils.onPagePause(getContext(), R.string.开机广告);
        if (mVideo != null)
        {
            mVideoPos = mVideo.getCurrentPosition();
        }
    }
}
