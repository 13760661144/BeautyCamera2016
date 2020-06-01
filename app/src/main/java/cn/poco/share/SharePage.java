package cn.poco.share;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adnonstop.admasterlibs.AbsUploadFile;
import com.adnonstop.admasterlibs.data.AbsChannelAdRes;
import com.adnonstop.admasterlibs.data.UploadData;
import com.circle.ctrls.SharedTipsView;
import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.GetMessageFromWX;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.poco.adMaster.UploadFile;
import cn.poco.blogcore.BlogConfig;
import cn.poco.blogcore.FacebookBlog;
import cn.poco.blogcore.InstagramBlog;
import cn.poco.blogcore.PocoBlog;
import cn.poco.blogcore.QzoneBlog2;
import cn.poco.blogcore.SinaBlog;
import cn.poco.blogcore.Tools;
import cn.poco.blogcore.TwitterBlog;
import cn.poco.blogcore.WeiXinBlog;
import cn.poco.blogcore.WeiboInfo;
import cn.poco.business.ChannelValue;
import cn.poco.camera.BrightnessUtils;
import cn.poco.camera.RotationImg2;
import cn.poco.framework.BaseSite;
import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.holder.ObjHandlerHolder;
import cn.poco.home.site.HomePageSite;
import cn.poco.image.filter;
import cn.poco.imagecore.ImageUtils;
import cn.poco.login.UserMgr;
import cn.poco.loginlibs.info.UserInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.setting.SettingPage;
import cn.poco.share.site.SharePageSite;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.AppInterface;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.NetCore2;
import cn.poco.tianutils.NetState;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.FileUtil;
import cn.poco.utils.Utils;
import my.beautyCamera.R;
import my.beautyCamera.wxapi.SendWXAPI;

public class SharePage extends IPage
{
    private int TAG = R.string.分享;

    protected SharePageSite m_site;
    private ShareFrame mShareFrame;
    private FrameLayout mMainFrame;

    public boolean ActivityRun = false;
    private ProgressDialog mProgressDialog;
    private String mContent;
    private String mPicPath;
    private Context mContext;
    private ShareSendBlog mSendBlog;
    //	private HelpPager m_UIHelp;
    protected AbsChannelAdRes mActChannelAdRes;
    protected AbsChannelAdRes.SharePageData mActConfigure;
    protected JSONObject mActPostString;
    protected Bitmap mBindPocoDialogBG;

    private ArrayList<Integer> sdkSendList;        //SDK发送队列

    public static final int BLOG_NUMBER = 10;    //微博数量（有新微博添加时修改此值）
    public static final int POCO_ACT = 0;        //广告活动
    public static final int POCO = 1;
    public static final int SINA = 2;
    public static final int QQ = 3;
    public static final int RENREN = 4;
    //	public static final int QZONE = 5;
    public static final int FACEBOOK = 6;
    public static final int TWITTER = 7;
    public static final int TUMBLR = 8;
    public static final int DOUBAN = 9;
    public static final int WEIXIN = 10000;        //微信
    public static final int WXFRIENDS = 10001;    //微信好友圈
    public static final int YIXIN = 10002;        //易信
    public static final int YXFRIENDS = 10003;    //易信朋友圈
    public static final int QZONE = 10004;        //QQ空间
    public static final int INSTAGRAM = 10005;    //instagram
    public static final int CIRCLE = 10006;       //Circle

    public static final int STATUS_UNBINDED = 1;
    public static final int STATUS_ON = 2;
    public static final int STATUS_OFF = 3;
    public static final int STATUS_WX_NORMAL = 4;    //微信专用状态，发送状态为微信好友
    public static final int STATUS_WX_FRIENDS = 5;    //微信专用状态，发送状态为微信朋友圈

    private ArrayList<String> mPocoContents = new ArrayList<String>();
    private ArrayList<String> mSinaContents = new ArrayList<String>();
    private ArrayList<String> mQQContents = new ArrayList<String>();
    private ArrayList<String> mRenRenContents = new ArrayList<String>();
    private ArrayList<String> mQzoneContents = new ArrayList<String>();
    private ArrayList<String> mFaceBookContents = new ArrayList<String>();
    private ArrayList<String> mTwitterContents = new ArrayList<String>();
    private ArrayList<String> mTumblrContents = new ArrayList<String>();
    private ArrayList<String> mPocoActContents = new ArrayList<String>();
    private ArrayList<String> mDoubanContents = new ArrayList<String>();

    //facebook
    private ProgressDialog mFacebookProgressDialog;

    private int mEffect = -1;
    private int mEffectValue = -1;

    private PocoBlog mPoco;
    private SinaBlog mSina;
    private QzoneBlog2 mQzone;
    private WeiXinBlog mWeiXin;
    private FacebookBlog mFacebook;
    private TwitterBlog mTwitter;
    private InstagramBlog mInstagram;
    private ShareTools mShare;

    public static String city = null;
    public static double lat = 0;
    public static double lon = 0;

    private String mShareUrl = null;
    public boolean mHideBanner = false;

    private boolean mIsFromCamera;

    private ObjHandlerHolder<AbsUploadFile.Callback> m_uploadCallback;

    //社区


    //发送微博对话框点击回调接口
    public interface DialogListener
    {
        public void onClick(int view);
    }

    public interface BindCompleteListener
    {
        public void success();

        public void fail();
    }

    public SharePage(Context context, BaseSite site)
    {
        super(context, site);
        ActivityRun = true;
        mContext = getContext();
        m_site = (SharePageSite)site;
        ShareData.InitData((Activity)getContext());

        sdkSendList = new ArrayList<Integer>();

        mMainFrame = new FrameLayout(mContext);
//		Bitmap bg = MakeBmp.CreateFixBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.share_test), ShareData.m_screenWidth, ShareData.m_screenHeight, MakeBmp.POS_START, 0, Config.ARGB_8888);
//		BitmapDrawable bd = new BitmapDrawable(getResources(), bg);
//		bd.setTileModeXY(TileMode.REPEAT , TileMode.REPEAT );
//		bd.setDither(true);
//		mRFlayout.setBackgroundDrawable(bd);
//		mRFlayout.setBackgroundColor(0xffedede9);
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.addView(mMainFrame, fl);

        mShareFrame = new ShareFrame(mContext, this);
        fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        fl.gravity = Gravity.LEFT | Gravity.TOP;
        mMainFrame.addView(mShareFrame, fl);

        /**---------------第一次进入页面提示---------------*/
//        if(Configure.queryHelpFlag("shareframe_help"))
//        {
//    		LayoutParams ffparams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
//    		m_UIHelp = new HelpPager(mContext);
//			m_UIHelp.p_hasDot = false;
//			m_UIHelp.InitData(new HelpCallback()
//			{
//				@Override
//				public void OnHelpFinish()
//				{
//					closeHelp();
//				}
//			});
//
//			ImageView img = new ImageView(mContext);
//			img.setScaleType(ScaleType.FIT_XY);
//			img.setImageResource(R.drawable.share_layout_share_help);
//			m_UIHelp.AddPage(img);
//
//			img = new ImageView(mContext);
//			img.setScaleType(ScaleType.FIT_XY);
//			img.setImageResource(R.drawable.share_layout_share_help2);
//			m_UIHelp.AddPage(img);
//			m_UIHelp.setLayoutParams(ffparams);
//			mRFlayout.addView(m_UIHelp);
//        }

        //传入微博工具参数
        initBlogConfig(context);

        TongJi2.AddOnlineSaveCount(null, null, getResources().getInteger(R.integer.行为事件_保存作品) + "");
        TongJiUtils.onPageStart(getContext(), TAG);
        BrightnessUtils instance = BrightnessUtils.getInstance();
        if (instance != null)
        {
            instance.setContext(getContext()).unregisterBrightnessObserver();
            instance.resetToDefault();
            instance.clearAll();
        }
    }

    /**
     * @param params {@link HomePageSite#BUSINESS_KEY}:BusinessRes 商业参数,判断是否为null<br/>
     *               {@link HomePageSite#POST_STR_KEY}:String 商业postStr<br/>
     *               img:RotationImg2/String<br/>
     *               当img:RotationImg2时,用户什么都没操作不需要保存图片<br/>
     *               当img:String时,路径保存的是FastBmp格式的图片,需要调用<br/>
     *               {@link Utils#MakeSavePhotoPath(Context, float)}获取保存路径,然后重新保存为JPG格式<br/>
     *               to_adv:Boolean UI版式，false为普通美化进来，true为高级美化进来
     *               from_camera:Boolean 是否从拍照进来
     *               not_save:Boolean 是否从美颜美化进来、没有经过任何处理不需要保存的图片
     *               not_send_act:Boolean 商业通道进来，分享时是否不需要发送商业信息到网站服务器
     *               business：BusinessRes 商业各种数据
     *               business_post_str:JSONObject 分享时商业递交信息用到的postString
     *               hide_button:Boolean 隐藏分享界面中部按钮呈现商业模式相同的界面
     *               show_business_banner:String 显示内置素材商业banner，传入对应的商业channel value,只有在非商业模式下才会显示
     */
    @Override
    public void SetData(HashMap<String, Object> params)
    {
        if(params == null || mShareFrame == null) return;
        mActChannelAdRes = (AbsChannelAdRes)params.get(HomePageSite.BUSINESS_KEY);
        if(mActChannelAdRes != null) mActConfigure = (AbsChannelAdRes.SharePageData)mActChannelAdRes.GetPageData(AbsChannelAdRes.SharePageData.class);
        mActPostString = (JSONObject)params.get(HomePageSite.POST_STR_KEY);
        Boolean not_send_act = (Boolean)params.get("not_send_act");
        if(not_send_act == null) not_send_act = false;
        Boolean not_save = (Boolean)params.get("not_save");
        if(not_save == null) not_save = false;
        Boolean hide_button = (Boolean)params.get("hide_button");
        if(hide_button != null && hide_button)
        {
            mActConfigure = new AbsChannelAdRes.SharePageData();
            not_send_act = true;
        }
        mShareFrame.mBannerChannelValue = (String)params.get("show_business_banner");
        mShareFrame.notSaveMode = not_save;
        mShareFrame.notSendActivities(not_send_act);
        Object img = params.get("img");
        int format = 0;
        Boolean camera = (Boolean)params.get("from_camera");
        if(camera == null || !camera)
        {
            mIsFromCamera = false;
            mShareFrame.setIsFromCamera(false);
            Boolean adv = (Boolean)params.get("to_adv");
            if(adv != null && adv) format = 2;
            else format = 1;
            MyBeautyStat.onPageStartByRes(R.string.美颜美图_保存页_主页面);
        }
        else
        {
            mIsFromCamera = true;
            mShareFrame.setIsFromCamera(true);
            MyBeautyStat.onPageStartByRes(R.string.拍照_拍照保存页_主页面);
        }
        mShareFrame.mFormat = format;
        if(img == null) return;
        if(img instanceof String)
        {
            Bitmap bmp = cn.poco.imagecore.Utils.DecodeFile((String)img, null);
            if(bmp != null && !bmp.isRecycled())
            {
//                if (SettingInfoMgr.GetSettingInfo(getContext()).GetAddDateState())
//                    Utils.attachDate(bmp);
                String path = Utils.SaveImg(mContext, bmp, Utils.MakeSavePhotoPath(mContext, (float)bmp.getWidth() / bmp.getHeight()), 100, true);
                if(path == null || path.length() <= 0) return;
                mShareFrame.initialize(mContext);
                RotationImg2 rotationImg2 = Utils.Path2ImgObj(path);
                mShareFrame.setImage(rotationImg2);
                bmp.recycle();
            }
        }
        else if(img instanceof RotationImg2)
        {
//          if (SettingInfoMgr.GetSettingInfo(getContext()).GetAddDateState()) {
//              Utils.attachDate(bmp);
//          }
            mShareFrame.initialize(mContext);
            mShareFrame.setImage((RotationImg2)img);
        }
    }

    /**
     * 关闭引导页.
     */
//	public void closeHelp()
//	{
//		if(m_UIHelp != null && Configure.queryHelpFlag("shareframe_help"))
//		{
//			Configure.clearHelpFlag("shareframe_help");
//			mRFlayout.removeView(m_UIHelp);
//			m_UIHelp.ClearAll();
//			m_UIHelp = null;
////			PocoCamera.main.showTopBar();
//		}
//	}
    public void setContentAndPic(String content, String pic)
    {
        this.mContent = content;
        this.mPicPath = pic;
    }

    public void addSendRecord(int object, String content, String pic)
    {
        if(content != null && content.length() > 0 && pic != null && pic.length() > 0)
        {
            String str = content + pic;
            switch(object)
            {
                case POCO:
                    mPocoContents.add(str);
                    break;
                case SINA:
                    mSinaContents.add(str);
                    break;
                case QQ:
                    mQQContents.add(str);
                    break;
            }
        }
    }

    /**
     * 检测发送内容是否重复
     *
     * @param object  发送的微博
     * @param content 文字内容
     * @param pic     图片路径
     * @return
     */
    public boolean queryContentRepeat(int object, String content, String pic)
    {
        ArrayList<String> list = null;
        switch(object)
        {
            case POCO:
                list = mPocoContents;
                break;
            case SINA:
                list = mSinaContents;
                break;
            case QQ:
                list = mQQContents;
                break;
            case RENREN:
                list = mRenRenContents;
                break;
            case QZONE:
                list = mQzoneContents;
                break;
            case FACEBOOK:
                list = mFaceBookContents;
                break;
            case TWITTER:
                list = mTwitterContents;
                break;
            case TUMBLR:
                list = mTumblrContents;
                break;
            case POCO_ACT:
                list = mPocoActContents;
                break;
            case DOUBAN:
                list = mDoubanContents;
                break;
            default:
                break;
        }
        if(list != null)
        {
            int len = list.size();
            String str = content + pic;
            for(int i = 0; i < len; i++)
            {
                String c = list.get(i);
                if(c != null && c.equals(str)) return true;
            }
        }
        return false;
    }

    private void dismissProgressDialog()
    {
        SharePage.this.post(new Runnable()
        {
            @Override
            public void run()
            {
                if(mProgressDialog != null)
                {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        });
    }

    /**
     * 获取活动要发送的字符串;
     */
    public JSONObject getPostActivitiesStr(String postText, int send_type)
    {
        JSONObject json = null;

        try
        {
            if(mActPostString == null) json = new JSONObject();
            else json = new JSONObject(mActPostString.toString());
            json.put("content", postText);
//            if(SettingInfoMgr.GetSettingInfo(getContext()).GetPocoId() != null && SettingInfoMgr.GetSettingInfo(getContext()).GetPocoId().length() > 0)
//            {
//                json.put("poco_id", SettingInfoMgr.GetSettingInfo(getContext()).GetPocoId());
//            }
            if(SettingInfoMgr.GetSettingInfo(getContext()).GetSinaUid() != null && SettingInfoMgr.GetSettingInfo(getContext()).GetSinaUid().length() > 0 && SettingInfoMgr.GetSettingInfo(getContext()).GetSinaUserNick() != null && SettingInfoMgr.GetSettingInfo(getContext()).GetSinaUserNick().length() > 0)
            {
                json.put("sina", SettingInfoMgr.GetSettingInfo(getContext()).GetSinaUid());
                json.put("sina_nickname", SettingInfoMgr.GetSettingInfo(getContext()).GetSinaUserNick());
            }
            if(SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneOpenid() != null && SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneOpenid().length() > 0 && SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneUserName() != null && SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneUserName().length() > 0)
            {
                json.put("q_zone", SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneOpenid());
                json.put("q_zone_nickname", SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneOpenid());
            }
            if(city != null && city.length() > 0)
            {
                json.put("city", city);
                json.put("latlng", lat + "," + lon);
            }
            switch(send_type)
            {
                case SINA:
                    json.put("sharetype", "sina");
                    break;

                case QZONE:
                    json.put("sharetype", "qzone");
                    break;

                case WEIXIN:
                    json.put("sharetype", "weixin");
                    break;

                case WXFRIENDS:
                    json.put("sharetype", "friend");
                    break;
            }
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }

        return json;
    }

    /**
     * 获取活动要发送的字符串;
     */
    public static JSONObject getPostActivitiesStr2(Context context, JSONObject postText)
    {
        JSONObject json = null;
        try
        {
            if(postText == null) json = new JSONObject();
            else json = new JSONObject(postText.toString());
//            if(SettingInfoMgr.GetSettingInfo(context).GetPocoId() != null && SettingInfoMgr.GetSettingInfo(context).GetPocoId().length() > 0)
//            {
//                json.put("poco_id", SettingInfoMgr.GetSettingInfo(context).GetPocoId());
//            }
            if(SettingInfoMgr.GetSettingInfo(context).GetSinaUid() != null && SettingInfoMgr.GetSettingInfo(context).GetSinaUid().length() > 0 && SettingInfoMgr.GetSettingInfo(context).GetSinaUserNick() != null && SettingInfoMgr.GetSettingInfo(context).GetSinaUserNick().length() > 0)
            {
                json.put("sina", SettingInfoMgr.GetSettingInfo(context).GetSinaUid());
                json.put("sina_nickname", SettingInfoMgr.GetSettingInfo(context).GetSinaUserNick());
            }
            if(SettingInfoMgr.GetSettingInfo(context).GetQzoneOpenid() != null && SettingInfoMgr.GetSettingInfo(context).GetQzoneOpenid().length() > 0 && SettingInfoMgr.GetSettingInfo(context).GetQzoneUserName() != null && SettingInfoMgr.GetSettingInfo(context).GetQzoneUserName().length() > 0)
            {
                json.put("q_zone", SettingInfoMgr.GetSettingInfo(context).GetQzoneOpenid());
                json.put("q_zone_nickname", SettingInfoMgr.GetSettingInfo(context).GetQzoneOpenid());
            }
            if(city != null && city.length() > 0)
            {
                json.put("city", city);
                json.put("latlng", lat + "," + lon);
            }
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 发送到广告活动
     *
     * @param content
     * @param file
     */
    public void sendToPocoActivities(String content, String file, int send_type)
    {
        if(mActConfigure == null) return;
        UploadData data = new UploadData();
        data.mPostApi = mActConfigure.mPostApi;
        data.mChannelValue = mActChannelAdRes.mAdId;
        data.mImgPath = file;
        data.mPostParams = getPostActivitiesStr(content, send_type).toString();
        new UploadFile(getContext(), data, AppInterface.GetInstance(getContext()), null);
    }

    /**
     * 发送到Sina微博;
     */
    public void sendToSina()
    {
        TongJi2.AddCountByRes(getContext(), R.integer.分享_新浪微博);
        if(SettingPage.checkSinaBindingStatus(getContext()))
        {
            if(mSina == null) mSina = new SinaBlog(mContext);
            if(!mSina.checkSinaClientInstall())
            {
                msgBox(mContext, getResources().getString(R.string.share_sina_error_clinet_no_install));
                return;
            }
            mSina.SetAccessToken(SettingInfoMgr.GetSettingInfo(mContext).GetSinaAccessToken());
            mSina.setSendSinaResponse(new SinaBlog.SendSinaResponse()
            {
                @Override
                public void response(boolean send_success, int response_code)
                {
                    if(send_success)
                    {
                        switch(response_code)
                        {
                            case WBConstants.ErrorCode.ERR_OK:
                                showDialogOnUIThread(mContext, null, getResources().getString(R.string.share_send_success), null);
                                addSendRecord(SINA, mContent, mPicPath);
                                ShareTools.addIntegral(getContext());
                                if(mIsFromCamera) MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.微博, R.string.拍照_拍照保存页_主页面);
                                else MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.微博, R.string.美颜美图_保存页_主页面);
                                break;

                            case WBConstants.ErrorCode.ERR_CANCEL:
                                showToastOnUIThread(mContext, getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG);
                                break;

                            case WBConstants.ErrorCode.ERR_FAIL:
                            case SinaBlog.NO_RESPONSE:
                                showToastOnUIThread(mContext, getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG);
                                break;

                            case WeiboInfo.BLOG_INFO_IMAGE_SIZE_TOO_LARGE:
                                showToastOnUIThread(mContext, getResources().getString(R.string.share_error_image_too_large), Toast.LENGTH_LONG);
                                break;
                        }
                    }
                    else
                    {
                        showToastOnUIThread(mContext, getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG);
                    }
                }
            });

            Intent intent = new Intent(mContext, SinaRequestActivity.class);
//			String sendFile = makeCachePic(mPicPath);
//			if(sendFile == null || sendFile.length() <= 0) sendFile = mPicPath;
            intent.putExtra("pic", mPicPath);
            intent.putExtra("content", mContent);
            ((Activity)mContext).startActivityForResult(intent, SinaBlog.SINA_REQUEST_CODE);
        }
        else
        {
            Toast.makeText(mContext.getApplicationContext(), "未绑定新浪微博", Toast.LENGTH_SHORT).show();
        }
    }

    public void bindQzone(final BindCompleteListener listener)
    {
        if(mQzone == null)
        {
            mQzone = new QzoneBlog2(getContext());
        }

        mQzone.bindQzoneWithSDK(new QzoneBlog2.BindQzoneCallback()
        {
            @Override
            public void success(String accessToken, String expiresIn, String openId, String nickName)
            {
                SettingInfoMgr.GetSettingInfo(getContext()).SetQzoneAccessToken(accessToken);
                SettingInfoMgr.GetSettingInfo(getContext()).SetQzoneOpenid(openId);
                SettingInfoMgr.GetSettingInfo(getContext()).SetQzoneExpiresIn(expiresIn);
                SettingInfoMgr.GetSettingInfo(getContext()).SetQzoneSaveTime(String.valueOf(System.currentTimeMillis() / 1000));
                SettingInfoMgr.GetSettingInfo(getContext()).SetQzoneUserName(nickName);

                if(listener != null) listener.success();
            }

            @Override
            public void fail()
            {
                switch(mQzone.LAST_ERROR)
                {
                    case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
                        AlertDialog dlg = new AlertDialog.Builder(mContext).create();
                        dlg.setTitle(mContext.getResources().getString(R.string.tips));
                        dlg.setMessage(getResources().getString(R.string.share_qq_error_clinet_no_install));
                        dlg.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getResources().getString(R.string.ensure), (DialogInterface.OnClickListener)null);
                        dlg.show();
                        break;

                    default:
                        msgBox(mContext, getResources().getString(R.string.share_qq_bind_fail));
                        break;
                }
                if(listener != null) listener.fail();
            }
        });
    }

    public void bindSina(final BindCompleteListener listener)
    {
        if(mSina == null)
        {
            mSina = new SinaBlog(getContext());
        }

        mSina.bindSinaWithSSO(new SinaBlog.BindSinaCallback()
        {
            @Override
            public void success(final String accessToken, String expiresIn, String uid, String userName, String nickName)
            {
                SettingInfoMgr.GetSettingInfo(getContext()).SetSinaAccessToken(accessToken);
                SettingInfoMgr.GetSettingInfo(getContext()).SetSinaUid(uid);
                SettingInfoMgr.GetSettingInfo(getContext()).SetSinaExpiresIn(expiresIn);
                SettingInfoMgr.GetSettingInfo(getContext()).SetSinaSaveTime(String.valueOf(System.currentTimeMillis() / 1000));
                SettingInfoMgr.GetSettingInfo(getContext()).SetSinaUserName(userName);
                SettingInfoMgr.GetSettingInfo(getContext()).SetSinaUserNick(nickName);

                if(listener != null) listener.success();

                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mSina.flowerCameraSinaWeibo(Constant.sinaUserId, accessToken);
                    }
                }).start();
            }

            @Override
            public void fail()
            {
                switch(mSina.LAST_ERROR)
                {
                    case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
                        msgBox(mContext, getResources().getString(R.string.share_sina_error_clinet_no_install));
                        break;

                    default:
                        msgBox(mContext, getResources().getString(R.string.share_sina_bind_fail));
                        break;
                }
                if(listener != null) listener.fail();
            }
        });
    }

//*************************微信***************************

    /**
     * 注册微信
     */
    public boolean registerWeiXin(int type)
    {
        if(mWeiXin == null) mWeiXin = new WeiXinBlog(getContext());
        if(mWeiXin.registerWeiXin()) return true;

        boolean WXSceneSession = type != STATUS_WX_FRIENDS ? true : false;
        showWeiXinErrorMessage(getContext(), mWeiXin.LAST_ERROR, WXSceneSession);
        return false;
    }

    public static void showToastOnUIThread(final Context context, final String text, final int time)
    {
        if(context != null && text != null)
        {
            ((Activity)context).runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(context, text, time).show();
                }
            });
        }
    }

    public static void showDialogOnUIThread(final Context context, final String title, final String text, final String click_url)
    {
        if(context != null && text != null)
        {
            ((Activity)context).runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    final AlertDialog dlg = new AlertDialog.Builder(context).create();
                    if(title != null && title.length() > 0) dlg.setTitle(title);
                    else dlg.setTitle(context.getResources().getString(R.string.tips));
                    if(click_url != null && click_url.length() > 0)
                    {
                        FrameLayout frame = new FrameLayout(context);
                        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                        fl.gravity = Gravity.LEFT | Gravity.TOP;
                        frame.setLayoutParams(fl);
                        {
                            TextView textview = new TextView(context);
                            textview.setText(text);
                            textview.setTextColor(Color.BLUE);
                            textview.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
                            textview.getPaint().setAntiAlias(true);//抗锯齿
                            textview.setPadding(ShareData.PxToDpi_xhdpi(20), ShareData.PxToDpi_xhdpi(20), ShareData.PxToDpi_xhdpi(20), ShareData.PxToDpi_xhdpi(20));
                            fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                            fl.gravity = Gravity.CENTER;
                            frame.addView(textview, fl);
                            textview.setOnClickListener(new OnClickListener()
                            {
                                @Override
                                public void onClick(View arg0)
                                {
                                    CommonUtils.OpenBrowser(context, click_url);
                                    dlg.dismiss();
                                }
                            });
                        }
                        dlg.setView(frame);
                    }
                    else
                    {
                        dlg.setMessage(text);
                    }
                    dlg.setButton(AlertDialog.BUTTON_POSITIVE, context.getResources().getString(R.string.ensure), (DialogInterface.OnClickListener)null);
                    dlg.show();
                }
            });
        }
    }

    public static void showDialogOnUIThread(final Context context, final boolean result, final String text, final Bitmap bg)
    {
        if(context != null && text != null)
        {
            ((Activity)context).runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    final Dialog dlg = new Dialog(context, R.style.notitledialog);
                    DialogView view = new DialogView(context, bg);
                    view.setInfo(result, text, new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            dlg.dismiss();
                        }
                    });
                    dlg.setContentView(view);
                    dlg.setCanceledOnTouchOutside(false);
                    dlg.show();
                }
            });
        }
    }

    private static String getTransaction(Bundle bundle)
    {
        final GetMessageFromWX.Req req = new GetMessageFromWX.Req(bundle);
        return req.transaction;
    }

    //制作发微博用的缓存图片
    private String makeCachePic(String file)
    {
        int network_type = NetState.GetConnectNet(getContext());
        int longest = SysConfig.GetPhotoSize(getContext());
        int quality = 100;
        switch(network_type)
        {
            case ConnectivityManager.TYPE_MOBILE:
                longest = 1024;
                quality = 90;
                break;

            case ConnectivityManager.TYPE_WIFI:
                quality = 90;
                break;

            case NetState.NET_NONE:
                return file;
        }

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, opts);
        opts.inSampleSize = 1;
        boolean bigPic = false;
        int bigest = opts.outWidth > opts.outHeight ? opts.outWidth : opts.outHeight;
        if(bigest > longest)
        {
            opts.inSampleSize = bigest / longest;
            bigPic = true;
        }

        opts.inJustDecodeBounds = false;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bmp = cn.poco.imagecore.Utils.DecodeFile(file, opts, true);
        if(bmp == null)
        {
            return null;
        }
        if(bigPic)
        {
            Matrix matrix = new Matrix();
            bigest = bmp.getWidth() > bmp.getHeight() ? bmp.getWidth() : bmp.getHeight();
            float scale = (float)longest / bigest;
            int outW = (int)(bmp.getWidth() * scale);
            int outH = (int)(bmp.getHeight() * scale);
            Bitmap outBmp = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(outBmp);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix.postScale(scale, scale);
            canvas.drawBitmap(bmp, matrix, null);
            bmp.recycle();
            bmp = outBmp;
            System.gc();
        }
        String FileName = FileCacheMgr.GetPagePath(this);
        CommonUtils.AddJpgExifInfo(getContext(), FileName);
        String path = null;
        try
        {
            path = Utils.SaveImg(getContext(), bmp, FileName, quality, false);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 开始发送SDK微博
     */
    public void startSendSdkClient(int send_blog)
    {
        if(sdkSendList == null) return;
        sdkSendList.clear();
//		if(mShareFrame.mLightedWeiXin == STATUS_WX_NORMAL)
//		{
//			sdkSendList.add(WEIXIN);
//		}
//		else if(mShareFrame.mLightedWeiXin == STATUS_WX_FRIENDS)
//		{
//			sdkSendList.add(WXFRIENDS);
//		}
//
//		if(mShareFrame.mLightedYiXin == STATUS_WX_NORMAL)
//		{
//			sdkSendList.add(YIXIN);
//		}
//		else if(mShareFrame.mLightedYiXin == STATUS_WX_FRIENDS)
//		{
//			sdkSendList.add(YXFRIENDS);
//		}
//
//		if(mShareFrame.mLightedQzone == STATUS_ON)
//		{
//			sdkSendList.add(QZONE);
//		}

        sdkSendList.add(send_blog);
        sendSdkClient();
    }

    private synchronized boolean sendSdkClient()
    {
        if(sdkSendList != null && sdkSendList.size() > 0)
        {
//			String send_path = null;
            Bitmap thumb = null;
            switch(sdkSendList.get(0))
            {
                case WEIXIN:
                    sdkSendList.remove(0);
//					send_path = makeWeiXinCachePic(mPicPath);
//					if(send_path == null) send_path = mPicPath;
                    if(mWeiXin == null) mWeiXin = new WeiXinBlog(getContext());
                    thumb = MakeBmp.CreateBitmap(cn.poco.imagecore.Utils.DecodeImage(getContext(), mPicPath, 0, -1, 150, 150), 150, 150, -1, 0, Bitmap.Config.ARGB_8888);
                    if(mWeiXin.sendToWeiXin(mPicPath, thumb, true))
                    {
                        SendWXAPI.WXCallListener listener = new SendWXAPI.WXCallListener()
                        {
                            @Override
                            public void onCallFinish(int result)
                            {
                                if(!ActivityRun) return;
                                switch(result)
                                {
                                    case BaseResp.ErrCode.ERR_OK:
                                        ShareTools.addIntegral(getContext());
                                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
                                        if(mIsFromCamera) MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.微信好友, R.string.拍照_拍照保存页_主页面);
                                        else MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.微信好友, R.string.美颜美图_保存页_主页面);
                                        break;
                                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
                                        break;
                                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
                                        break;
                                    default:
                                        break;
                                }
                                SendWXAPI.removeListener(this);
                            }
                        };
                        SendWXAPI.addListener(listener);
                    }
                    else
                    {
                        showWeiXinErrorMessage(getContext(), mWeiXin.LAST_ERROR, true);
                        sendSdkClient();
                    }
                    return true;

                case WXFRIENDS:
                    sdkSendList.remove(0);
//					send_path = makeWeiXinCachePic(mPicPath);
//					if(send_path == null) send_path = mPicPath;
                    if(mWeiXin == null) mWeiXin = new WeiXinBlog(getContext());
                    thumb = MakeBmp.CreateBitmap(cn.poco.imagecore.Utils.DecodeImage(getContext(), mPicPath, 0, -1, 150, 150), 150, 150, -1, 0, Bitmap.Config.ARGB_8888);
                    if(mWeiXin.sendToWeiXin(mPicPath, thumb, false))
                    {
                        SendWXAPI.WXCallListener listener = new SendWXAPI.WXCallListener()
                        {
                            @Override
                            public void onCallFinish(int result)
                            {
                                if(!ActivityRun) return;
                                switch(result)
                                {
                                    case BaseResp.ErrCode.ERR_OK:
                                        ShareTools.addIntegral(getContext());
                                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
                                        if(mIsFromCamera) MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.朋友圈, R.string.拍照_拍照保存页_主页面);
                                        else MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.朋友圈, R.string.美颜美图_保存页_主页面);
                                        break;
                                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
                                        break;
                                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
                                        break;
                                    default:
                                        break;
                                }
                                SendWXAPI.removeListener(this);
                            }
                        };
                        SendWXAPI.addListener(listener);
                    }
                    else
                    {
                        SharePage.showWeiXinErrorMessage(getContext(), mWeiXin.LAST_ERROR, false);
                        sendSdkClient();
                    }
                    return true;

                case QZONE:
                    sdkSendList.remove(0);
                    if(!Tools.checkApkExist(getContext(), QzoneBlog2.QQ_PACKAGE_NAME))
                    {
                        AlertDialog dlg = new AlertDialog.Builder(getContext()).create();
                        dlg.setTitle(mContext.getResources().getString(R.string.tips));
                        dlg.setMessage(getContext().getResources().getString(R.string.share_qq_error_clinet_no_install));
                        dlg.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getResources().getString(R.string.ensure), (DialogInterface.OnClickListener)null);
                        dlg.show();
                        return true;
                    }

                    if(mQzone == null) mQzone = new QzoneBlog2(getContext());
                    mQzone.SetAccessToken(SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneAccessToken());
                    mQzone.setOpenId(SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneOpenid());
//					mQzone.sendToPublicQzone(1, makeCachePic(mPicPath));
                    mQzone.setSendQQorQzoneCompletelistener(new QzoneBlog2.SendQQorQzoneCompletelistener()
                    {
                        @Override
                        public void sendComplete(int result)
                        {
                            if(!ActivityRun) return;
                            if(result == QzoneBlog2.SEND_SUCCESS)
                            {
                                ShareTools.addIntegral(getContext());
                                Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
                                if(mIsFromCamera) MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ空间, R.string.拍照_拍照保存页_主页面);
                                else MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ空间, R.string.美颜美图_保存页_主页面);
                            }
                            else if(result == QzoneBlog2.SEND_CANCEL)
                            {
                                Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    mQzone.sendToPublicQzone(1, mPicPath);
                    return true;

                case QQ:
                    sdkSendList.remove(0);
                    if(!Tools.checkApkExist(getContext(), QzoneBlog2.QQ_PACKAGE_NAME))
                    {
                        AlertDialog dlg = new AlertDialog.Builder(getContext()).create();
                        dlg.setTitle(mContext.getResources().getString(R.string.tips));
                        dlg.setMessage(getContext().getResources().getString(R.string.share_qq_error_clinet_no_install));
                        dlg.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getResources().getString(R.string.ensure), (DialogInterface.OnClickListener)null);
                        dlg.show();
                        return true;
                    }

                    if(mQzone == null) mQzone = new QzoneBlog2(getContext());
                    mQzone.SetAccessToken(SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneAccessToken());
                    mQzone.setOpenId(SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneOpenid());
//					mQzone.sendToQQ(makeCachePic(mPicPath));
                    mQzone.setSendQQorQzoneCompletelistener(new QzoneBlog2.SendQQorQzoneCompletelistener()
                    {
                        @Override
                        public void sendComplete(int result)
                        {
                            if(!ActivityRun) return;
                            if(result == QzoneBlog2.SEND_SUCCESS)
                            {
                                ShareTools.addIntegral(getContext());
                                Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
                                if(mIsFromCamera) MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ好友, R.string.拍照_拍照保存页_主页面);
                                else MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ好友, R.string.美颜美图_保存页_主页面);
                            }
                            else if(result == QzoneBlog2.SEND_CANCEL)
                            {
                                Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    mQzone.sendToQQ(mPicPath);
                    return true;

                case FACEBOOK:
                    sdkSendList.remove(0);
                    if(mFacebook == null) mFacebook = new FacebookBlog(mContext);
                    Bitmap bmp = cn.poco.imagecore.Utils.DecodeImage(mContext, mPicPath, 0, -1, -1, -1);
                    mFacebookProgressDialog = ProgressDialog.show(mContext, "", mContext.getResources().getString(R.string.share_facebook_client_call));
                    mFacebookProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mFacebookProgressDialog.setCancelable(true);
                    boolean result = mFacebook.sendPhotoToFacebookBySDK(bmp, new FacebookBlog.FaceBookSendCompleteCallback()
                    {
                        @Override
                        public void sendComplete(int result, String error_info)
                        {
                            if(mFacebookProgressDialog != null)
                            {
                                if(mFacebookProgressDialog.isShowing()) mFacebookProgressDialog.dismiss();
                                else return;
                            }
                            if(result == FacebookBlog.RESULT_FAIL)
                            {
                                AlertDialog dlg = new AlertDialog.Builder(mContext).create();
                                dlg.setTitle(mContext.getResources().getString(R.string.error));
                                dlg.setMessage(error_info);
                                dlg.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getResources().getString(R.string.ensure), (DialogInterface.OnClickListener)null);
                                dlg.show();
                                return;
                            }
                            ShareTools.addIntegral(getContext());
                            if(mIsFromCamera) MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.Facebook, R.string.拍照_拍照保存页_主页面);
                            else MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.Facebook, R.string.美颜美图_保存页_主页面);
                        }
                    });
                    if(!result)
                    {
                        if(mFacebookProgressDialog != null) mFacebookProgressDialog.dismiss();
                        String message = null;
                        switch(mFacebook.LAST_ERROR)
                        {
                            case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
                                message = getContext().getResources().getString(R.string.share_facebook_client_no_install);
                                break;

                            case WeiboInfo.BLOG_INFO_IMAGE_IS_NULL:
                                message = getContext().getResources().getString(R.string.share_error_image_is_null);
                                break;

                            default:
                                message = getContext().getResources().getString(R.string.share_facebook_client_start_fail);
                                break;
                        }
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                    return true;

                case TWITTER:
                    sdkSendList.remove(0);
                    if(mTwitter == null) mTwitter = new TwitterBlog(mContext);
                    if(!mTwitter.sendToTwitter(mPicPath, null))
                    {
                        String message = null;
                        switch(mTwitter.LAST_ERROR)
                        {
                            case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
                                message = getContext().getResources().getString(R.string.share_twitter_client_no_install);
                                break;

                            case WeiboInfo.BLOG_INFO_CONTEXT_IS_NULL:
                                message = getContext().getResources().getString(R.string.share_error_image_is_null);
                                break;
                        }
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        ShareTools.addIntegral(getContext());
                        if(mIsFromCamera) MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.Twitter, R.string.拍照_拍照保存页_主页面);
                        else MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.Twitter, R.string.美颜美图_保存页_主页面);
                    }
                    return true;

                case INSTAGRAM:
                    sdkSendList.remove(0);
                    if(mInstagram == null) mInstagram = new InstagramBlog(mContext);
                    if(!mInstagram.sendToInstagram(mPicPath))
                    {
                        String message = null;
                        switch(mInstagram.LAST_ERROR)
                        {
                            case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
                                message = getContext().getResources().getString(R.string.share_instagram_client_no_install);
                                break;

                            case WeiboInfo.BLOG_INFO_IMAGE_IS_NULL:
                                message = getContext().getResources().getString(R.string.share_error_image_is_null);
                                break;
                        }
                        AlertDialog dlg = new AlertDialog.Builder(mContext).create();
                        dlg.setTitle(mContext.getResources().getString(R.string.tips));
                        dlg.setMessage(message);
                        dlg.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getResources().getString(R.string.ensure), (DialogInterface.OnClickListener)null);
                        dlg.show();
                    }
                    else ShareTools.addIntegral(getContext());
                    return true;

                case CIRCLE:
                    sdkSendList.remove(0);
//                    if(mShare == null) mShare = new ShareTools(getContext());
//                    mShare.sendToCircle(mPicPath, mContent, new ShareTools.SendCompletedListener()
//                    {
//                        @Override
//                        public void getResult(Object result)
//                        {
//                            SharePage.showCircleCodeMessage(getContext(), (int)result);
//                        }
//                    });
                    //发布到社区
                    if (!FileUtil.isFileExists(mPicPath)) {
                        Toast.makeText(getContext(), R.string.share_error_image_is_null, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    shareToCommunity(mContent);
                    return true;
            }
        }
        return false;
    }

    private void shareToCommunity(String content)
    {
        if(!UserMgr.IsLogin(getContext(), null)){
            //未登录去登录
            m_site.OnLogin(getContext());
            return ;
        }
        UserInfo userInfo = UserMgr.ReadCache(getContext());

        if(userInfo !=null && TextUtils.isEmpty(userInfo.mMobile)){
            //未完善资料去完善资料
            m_site.onBindPhone(getContext());
            return ;
        }
        if(content == null) content = "";
        m_site.onCommunity(getContext(),mPicPath,content,1);
    }

    private void showSuccessDialog(){
        SharedTipsView view=new SharedTipsView(getContext());

        final Dialog dialog = new Dialog(getContext(), R.style.fullDialog1);
//        dialog.getWindow().setDimAmount(0.3f);//设置昏暗度为0
        view.setJump2AppClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                m_site.onHome(getContext());
                dialog.dismiss();
            }
        });
        view.setStayClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
//        Window window = dialog.getWindow();
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = com.circle.utils.Utils.getScreenW();
//        window.setAttributes(lp);
//        window.setGravity(Gravity.CENTER);
        dialog.show();
        dialog.setContentView(view);
    }

    /**
     * 不能放在主线程调用
     *
     * @param url 图片网络链接
     * @return
     */
    public static Bitmap downloadImage(String url)
    {
        if(url == null || url.length() <= 0) return null;
        NetCore2 net = new NetCore2();
        NetCore2.NetMsg msg = net.HttpGet(url);
        if(msg != null && msg.m_stateCode == HttpURLConnection.HTTP_OK && msg.m_data != null)
        {
            return BitmapFactory.decodeByteArray(msg.m_data, 0, msg.m_data.length);
        }
        return null;
    }


//**********************商业上传图片分享链接*************************

    /**
     * 分享商业图片链接
     *
     * @param pic     图片地址
     * @param content 发送的文本内容
     * @param type    分享微博的类型，传入SharePage里的几个静态参数，例如SharePage.POCO
     */
    public void shareActivitiesUrl(final String pic, final String content, final int type)
    {
        if(mShareUrl != null && mShareUrl.length() > 0)
        {
            shareActivitiesUrl(mShareUrl, pic, content, type);
        }
        else
        {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage(getResources().getString(R.string.share_server_uploading));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//			mProgressDialog.setOnCancelListener(mCancelListener);
//			mProgressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "取消发送", mCancelClickListener);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            if(m_uploadCallback == null)
            {
                m_uploadCallback = new ObjHandlerHolder<AbsUploadFile.Callback>(new AbsUploadFile.Callback()
                {
                    @Override
                    public void onProgress(int progress)
                    {
                    }

                    @Override
                    public void onSuccess(String shareUrl)
                    {
                        if(mProgressDialog != null)
                        {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        if(shareUrl != null && shareUrl.length() > 0)
                        {
                            mShareUrl = shareUrl;
                            shareActivitiesUrl(shareUrl, pic, content, type);
                        }
                        else
                        {
                            showToastOnUIThread(mContext, getResources().getString(R.string.share_server_upload_fail), Toast.LENGTH_LONG);
                        }
                    }

                    @Override
                    public void onFailure()
                    {
                        if(mProgressDialog != null)
                        {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        showToastOnUIThread(mContext, getResources().getString(R.string.share_server_upload_fail), Toast.LENGTH_LONG);
                    }
                });
            }

            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    sendToPocoGetUrl(mContext, mActConfigure.mPostApi, mActChannelAdRes.mAdId, pic, null, mActPostString, m_uploadCallback);
                }
            }).start();
        }
    }

    private void shareActivitiesUrl(String url, String pic, String content, int type)
    {
        Bitmap thumb = null;

        switch(type)
        {
            case SINA:
                mContent = content + " " + url;
                mPicPath = pic;
                sendToSina();
                break;

            case QQ:
                if(!Tools.checkApkExist(getContext(), QzoneBlog2.QQ_PACKAGE_NAME))
                {
                    AlertDialog dlg = new AlertDialog.Builder(getContext()).create();
                    dlg.setTitle(mContext.getResources().getString(R.string.tips));
                    dlg.setMessage(mContext.getResources().getString(R.string.share_qq_error_clinet_no_install));
                    dlg.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getResources().getString(R.string.ensure), (DialogInterface.OnClickListener)null);
                    dlg.show();
                    return;
                }
                mContent = content + " " + url;
                mPicPath = pic;

                if(mQzone == null) mQzone = new QzoneBlog2(getContext());
                mQzone.SetAccessToken(SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneAccessToken());
                mQzone.setOpenId(SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneOpenid());
                mQzone.setSendQQorQzoneCompletelistener(new QzoneBlog2.SendQQorQzoneCompletelistener()
                {
                    @Override
                    public void sendComplete(int result)
                    {
                        if(!ActivityRun) return;
                        if(result == QzoneBlog2.SEND_SUCCESS)
                        {
                            ShareTools.addIntegral(getContext());
                            Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
                            if(mIsFromCamera) MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ好友, R.string.拍照_拍照保存页_主页面);
                            else MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ好友, R.string.美颜美图_保存页_主页面);
                        }
                        else if(result == QzoneBlog2.SEND_CANCEL)
                        {
                            Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                mQzone.sendUrlToQQ(mPicPath, mContext.getResources().getString(R.string.share_default_title), mContent, url);
                break;

            case QZONE:
                if(!Tools.checkApkExist(getContext(), QzoneBlog2.QQ_PACKAGE_NAME))
                {
                    AlertDialog dlg = new AlertDialog.Builder(getContext()).create();
                    dlg.setTitle(mContext.getResources().getString(R.string.tips));
                    dlg.setMessage(mContext.getResources().getString(R.string.share_qq_error_clinet_no_install));
                    dlg.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getResources().getString(R.string.ensure), (DialogInterface.OnClickListener)null);
                    dlg.show();
                    return;
                }

                mContent = content + url;
                mPicPath = pic;

                try
                {
                    if(mQzone == null) mQzone = new QzoneBlog2(getContext());
                    mQzone.SetAccessToken(SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneAccessToken());
                    mQzone.setOpenId(SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneOpenid());
                    String sendPath = makeCachePic(mPicPath);
                    mQzone.setSendQQorQzoneCompletelistener(new QzoneBlog2.SendQQorQzoneCompletelistener()
                    {
                        @Override
                        public void sendComplete(int result)
                        {
                            if(!ActivityRun) return;
                            if(result == QzoneBlog2.SEND_SUCCESS)
                            {
                                ShareTools.addIntegral(getContext());
                                Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
                                if(mIsFromCamera) MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ空间, R.string.拍照_拍照保存页_主页面);
                                else MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ空间, R.string.美颜美图_保存页_主页面);
                            }
                            else if(result == QzoneBlog2.SEND_CANCEL)
                            {
                                Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    mQzone.SendMsgWithSDK(mContent, sendPath, mContext.getResources().getString(R.string.share_default_title), mContext.getResources().getString(R.string.share_default_title), url, null);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                break;

            case WEIXIN:
                mContent = content;
                mPicPath = pic;

                if(mWeiXin == null) mWeiXin = new WeiXinBlog(getContext());
                if(mActChannelAdRes != null && mActChannelAdRes.mAdId != null && mActChannelAdRes.mAdId.equals(ChannelValue.AD82_1)) thumb = BitmapFactory.decodeResource(getResources(), R.drawable.share_lancome_thumb);
                else thumb = MakeBmp.CreateBitmap(cn.poco.imagecore.Utils.DecodeImage(getContext(), mPicPath, 0, -1, 150, 150), 150, 150, -1, 0, Bitmap.Config.ARGB_8888);
                String wx_title = null;
                String wx_content = null;
                if(mActConfigure != null)
                {
                    if(mActConfigure.mWeixinFriendTitle != null && mActConfigure.mWeixinFriendTitle.length() > 0)
                        wx_title = mActConfigure.mWeixinFriendTitle;
                    if(mActConfigure.mWeixinFriendContent != null && mActConfigure.mWeixinFriendContent.length() > 0)
                        wx_content = mActConfigure.mWeixinFriendContent;
                }
                if(wx_title == null) wx_title = content;
                if(mWeiXin.sendUrlToWeiXin(url, wx_title, wx_content, thumb, true))
                {
                    SendWXAPI.WXCallListener listener = new SendWXAPI.WXCallListener()
                    {
                        @Override
                        public void onCallFinish(int result)
                        {
                            if(!ActivityRun) return;
                            switch(result)
                            {
                                case BaseResp.ErrCode.ERR_OK:
                                    ShareTools.addIntegral(getContext());
                                    Toast.makeText(getContext(), mContext.getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
                                    if(mIsFromCamera) MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.微信好友, R.string.拍照_拍照保存页_主页面);
                                    else MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.微信好友, R.string.美颜美图_保存页_主页面);
                                    break;
                                case BaseResp.ErrCode.ERR_USER_CANCEL:
                                    Toast.makeText(getContext(), mContext.getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
                                    break;
                                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                                    Toast.makeText(getContext(), mContext.getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    break;
                            }
                            SendWXAPI.removeListener(this);
                        }
                    };
                    SendWXAPI.addListener(listener);
                }
                else
                {
                    SharePage.showWeiXinErrorMessage(getContext(), mWeiXin.LAST_ERROR, true);
                }
                break;

            case WXFRIENDS:
                mContent = content;
                mPicPath = pic;

                if(mWeiXin == null) mWeiXin = new WeiXinBlog(getContext());
                if(mActChannelAdRes != null && mActChannelAdRes.mAdId != null && mActChannelAdRes.mAdId.equals(ChannelValue.AD82_1)) thumb = BitmapFactory.decodeResource(getResources(), R.drawable.share_lancome_thumb);
                else thumb = MakeBmp.CreateBitmap(cn.poco.imagecore.Utils.DecodeImage(getContext(), mPicPath, 0, -1, 150, 150), 150, 150, -1, 0, Bitmap.Config.ARGB_8888);
                String wxf_content = null;
                if(mActConfigure != null && mActConfigure.mWeixinCircleTitle != null && mActConfigure.mWeixinCircleTitle.length() > 0)
                    wxf_content = mActConfigure.mWeixinCircleTitle;
                if(wxf_content == null) wxf_content = content;
                if(mWeiXin.sendUrlToWeiXin(url, wxf_content, null, thumb, false))
                {
                    SendWXAPI.WXCallListener listener = new SendWXAPI.WXCallListener()
                    {
                        @Override
                        public void onCallFinish(int result)
                        {
                            if(!ActivityRun) return;
                            switch(result)
                            {
                                case BaseResp.ErrCode.ERR_OK:
                                    ShareTools.addIntegral(getContext());
                                    Toast.makeText(getContext(), mContext.getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
                                    if(mIsFromCamera) MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.朋友圈, R.string.拍照_拍照保存页_主页面);
                                    else MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.朋友圈, R.string.美颜美图_保存页_主页面);
                                    break;
                                case BaseResp.ErrCode.ERR_USER_CANCEL:
                                    Toast.makeText(getContext(), mContext.getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
                                    break;
                                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                                    Toast.makeText(getContext(), mContext.getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    break;
                            }
                            SendWXAPI.removeListener(this);
                        }
                    };
                    SendWXAPI.addListener(listener);
                }
                else
                {
                    SharePage.showWeiXinErrorMessage(getContext(), mWeiXin.LAST_ERROR, false);
                }
                break;

            case FACEBOOK:
                if(mFacebook == null) mFacebook = new FacebookBlog(mContext);
                String facebook_title = null;
                String facebook_content = null;
                if(mActConfigure != null)
                {
                    if(mActConfigure.mWeixinFriendTitle != null && mActConfigure.mWeixinFriendTitle.length() > 0)
                        facebook_title = mActConfigure.mWeixinFriendTitle;
                    if(mActConfigure.mWeixinFriendContent != null && mActConfigure.mWeixinFriendContent.length() > 0)
                        facebook_content = mActConfigure.mWeixinFriendContent;
                }
                if(facebook_title == null)
                    facebook_title = mContext.getResources().getString(R.string.share_default_title);
                if(facebook_content == null) facebook_content = content;
                boolean result = mFacebook.sendUrlToFacebookBySDK(facebook_title, facebook_content, url, new FacebookBlog.FaceBookSendCompleteCallback()
                {
                    @Override
                    public void sendComplete(int result, String error_info)
                    {
                        if(result == FacebookBlog.RESULT_FAIL)
                        {
                            AlertDialog dlg = new AlertDialog.Builder(mContext).create();
                            dlg.setTitle(mContext.getResources().getString(R.string.error));
                            dlg.setMessage(error_info);
                            dlg.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getResources().getString(R.string.ensure), (DialogInterface.OnClickListener)null);
                            dlg.show();
                            return;
                        }
                        ShareTools.addIntegral(getContext());
                        if(mIsFromCamera) MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.Facebook, R.string.拍照_拍照保存页_主页面);
                        else MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.Facebook, R.string.美颜美图_保存页_主页面);
                    }
                });
                if(!result)
                {
                    String message = null;
                    switch(mFacebook.LAST_ERROR)
                    {
                        case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
                            message = mContext.getResources().getString(R.string.share_facebook_client_no_install);
                            break;

                        case WeiboInfo.BLOG_INFO_IMAGE_IS_NULL:
                            message = mContext.getResources().getString(R.string.share_error_image_is_null);
                            break;

                        default:
                            message = getContext().getResources().getString(R.string.share_facebook_client_start_fail);
                            break;
                    }
                    AlertDialog dlg = new AlertDialog.Builder(mContext).create();
                    dlg.setTitle(mContext.getResources().getString(R.string.tips));
                    dlg.setMessage(message);
                    dlg.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getResources().getString(R.string.ensure), (DialogInterface.OnClickListener)null);
                    dlg.show();
                }
                break;

            case TWITTER:
                mContent = content + " " + url;
                mPicPath = pic;

                if(mTwitter == null) mTwitter = new TwitterBlog(mContext);
                if(!mTwitter.sendToTwitter(mPicPath, mContent))
                {
                    String message = null;
                    switch(mTwitter.LAST_ERROR)
                    {
                        case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
                            message = mContext.getResources().getString(R.string.share_twitter_client_no_install);
                            break;

                        case WeiboInfo.BLOG_INFO_CONTEXT_IS_NULL:
                            message = mContext.getResources().getString(R.string.share_error_image_is_null);
                            break;
                    }
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    ShareTools.addIntegral(getContext());
                    if(mIsFromCamera) MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.Twitter, R.string.拍照_拍照保存页_主页面);
                    else MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.Twitter, R.string.美颜美图_保存页_主页面);
                }
                break;

            case INSTAGRAM:
                mContent = content;
                mPicPath = pic;

                if(mInstagram == null) mInstagram = new InstagramBlog(mContext);
                if(!mInstagram.sendToInstagram(mPicPath))
                {
                    String message = null;
                    switch(mInstagram.LAST_ERROR)
                    {
                        case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
                            message = mContext.getResources().getString(R.string.share_instagram_client_no_install);
                            break;

                        case WeiboInfo.BLOG_INFO_IMAGE_IS_NULL:
                            message = mContext.getResources().getString(R.string.share_error_image_is_null);
                            break;
                    }
                    AlertDialog dlg = new AlertDialog.Builder(mContext).create();
                    dlg.setTitle(mContext.getResources().getString(R.string.tips));
                    dlg.setMessage(message);
                    dlg.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getResources().getString(R.string.ensure), (DialogInterface.OnClickListener)null);
                    dlg.show();
                }
                else ShareTools.addIntegral(getContext());
                break;

            case CIRCLE:
                mContent = content + " " + url;
                mPicPath = pic;

//                if(mShare == null) mShare = new ShareTools(getContext());
//                mShare.sendToCircle(mPicPath, mContent, new ShareTools.SendCompletedListener()
//                {
//                    @Override
//                    public void getResult(Object result)
//                    {
//                        SharePage.showCircleCodeMessage(getContext(), (int)result);
//                    }
//                });
                if (!FileUtil.isFileExists(mPicPath)) {
                    Toast.makeText(getContext(), R.string.share_error_image_is_null, Toast.LENGTH_SHORT).show();
                } else {
                    shareToCommunity(mContent);
                }
                break;
        }
    }

    /**
     * 上传图片到poco获取分享链接
     *
     * @param context       context
     * @param pic           图片地址
     * @param channel_value 商业channel_value
     * @param music         上传需要的音频文件，可能为null
     * @param postString
     * @param callback      回调
     * @return
     */
    public static void sendToPocoGetUrl(Context context, String url, String channel_value, String pic, String music, JSONObject postString, ObjHandlerHolder<AbsUploadFile.Callback> callback)
    {
        UploadData data = new UploadData();
        data.mPostApi = url;
        data.mChannelValue = channel_value;
        data.mImgPath = pic;
        data.mPostParams = getPostActivitiesStr2(context, postString).toString();
        if(music != null) data.mVideoPath = music;
        new UploadFile(context, data, AppInterface.GetInstance(context), callback);
    }

    //**********************传入各种微博信息**********************
    @SuppressWarnings("deprecation")
    public static void initBlogConfig(Context context)
    {
        BlogConfig.QZONE_CALLBACK_URL = URLEncoder.encode("auth://auth.qq.com");
        BlogConfig.QZONE_CONSUMER_KEY = Constant.getQzoneAppKey(context);

        BlogConfig.SINA_CALLBACK_URL = "http://www.poco.cn";
        BlogConfig.SINA_CONSUMER_KEY = Constant.getSinaConsumerKey(context);
        BlogConfig.SINA_CONSUMER_SECRET = Constant.getSinaConsumerSecret(context);

        BlogConfig.WEIXIN_CONSUMER_KEY = Constant.getWeixinAppId(context);
        BlogConfig.WEIXIN_CONSUMER_SECRET = Constant.getWeixinAppSecret(context);
    }

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params)
    {
        if(mShareFrame != null) mShareFrame.onResume();
        switch(siteID)
        {
            case SiteID.PUBLISH_OPUS_PAGE:
                if(params != null && params.containsKey("isSuccess")){
                    boolean b = (boolean)params.get("isSuccess");
                    if(b) showSuccessDialog();
                }
                break;

            case SiteID.LOGIN:
            case SiteID.REGISTER_DETAIL:
            case SiteID.RESETPSW:
                if(!UserMgr.IsLogin(getContext(), null)) return;
                UserInfo userInfo = UserMgr.ReadCache(getContext());
                if(userInfo == null) return;
                if(TextUtils.isEmpty(userInfo.mMobile)) return ;
                if (!FileUtil.isFileExists(mPicPath)) {
                    Toast.makeText(getContext(), R.string.share_error_image_is_null, Toast.LENGTH_SHORT).show();
                } else {
                    shareToCommunity(mContent);
                }
                break;
        }
        super.onPageResult(siteID, params);
    }

    @Override
    public void onResume()
    {
        TongJiUtils.onPageResume(getContext(), TAG);
        if(mFacebookProgressDialog != null && mFacebookProgressDialog.isShowing())
            mFacebookProgressDialog.dismiss();
        if(mShareFrame != null)
        {
            mShareFrame.hideKeyboard();
            mShareFrame.onResume();
        }
        sendSdkClient();
    }

    @Override
    public void onPause()
    {
        TongJiUtils.onPagePause(getContext(), TAG);
        if(mShareFrame != null) mShareFrame.onPause();
        super.onPause();
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(mSina != null) mSina.onActivityResult(requestCode, resultCode, data, -1);
        if(mQzone != null) mQzone.onActivityResult(requestCode, resultCode, data);
        if(mFacebook != null) mFacebook.onActivityResult(requestCode, resultCode, data, -1);
        return false;
    }

    @Override
    public void onClose()
    {
        ActivityRun = false;
        if(mProgressDialog != null)
        {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        if(mFacebookProgressDialog != null)
        {
            mFacebookProgressDialog.dismiss();
            mFacebookProgressDialog = null;
        }
        if(sdkSendList != null)
        {
            sdkSendList.clear();
            sdkSendList = null;
        }
        if(mPoco != null)
        {
            mPoco = null;
        }
        if(mSina != null)
        {
            mSina.clear();
            mSina = null;
        }
        if(mQzone != null)
        {
            mQzone.clear();
            mQzone = null;
        }
        if(mFacebook != null)
        {
            mFacebook.clear();
            mFacebook = null;
        }
        if(mTwitter != null)
        {
            mTwitter.clear();
            mTwitter = null;
        }
        if(m_uploadCallback != null)
        {
            m_uploadCallback.Clear();
            m_uploadCallback = null;
        }
        mShare = null;
        mInstagram = null;
        if(mBindPocoDialogBG != null && !mBindPocoDialogBG.isRecycled())
        {
            mBindPocoDialogBG.recycle();
            mBindPocoDialogBG = null;
        }
        mWeiXin = null;
        mShareFrame.clear();
        this.clearFocus();
        System.gc();

        TongJiUtils.onPageEnd(getContext(), TAG);
        if(mIsFromCamera) MyBeautyStat.onPageEndByRes(R.string.拍照_拍照保存页_主页面);
        else MyBeautyStat.onPageEndByRes(R.string.美颜美图_保存页_主页面);
    }

    @Override
    public void onBack()
    {
        if(mShareFrame.onBack()) return;
        if(mShareFrame.sendBlogFrameStatue())
        {
            mShareFrame.closeSendBlogPage();
            return;
        }
        //if(mShareFrame.isJaneOpen()) return;
        //商业需要
        /*if(mActConfigure != null && !mOnBack && mActConfigure.m_channelValue != null &&
				mActConfigure.m_channelValue.equals("miaocuijiao_201508"))
		{
			mOnBack = true;
			PocoCamera.main.onBackPressed();
			PocoCamera.main.onBackPressed();
			return;
		}*/
        if(m_site != null)
        {
            m_site.OnBack();
            mShareFrame.outPageAnime();
        }
    }

    //图片转换为流
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle)
    {
        byte[] out = null;

        Bitmap temp = bmp;
        if(temp != null)
        {
            if(temp.getConfig() != Bitmap.Config.ARGB_8888)
            {
                temp = temp.copy(Bitmap.Config.ARGB_8888, true);
            }
            out = ImageUtils.JpgEncode(temp, 90);
            if(temp != bmp)
            {
                temp.recycle();
                temp = null;
            }
            if(needRecycle)
            {
                bmp.recycle();
            }
        }

        return out;
    }

    /**
     * 微信解锁素材
     *
     * @param context
     * @param shareText
     * @param thumb
     * @param cb
     */
    public static void unlockResourceByWeiXin(Context context, String shareText, Bitmap thumb, final SendWXAPI.WXCallListener cb)
    {
        String url = "http://a.app.qq.com/o/simple.jsp?pkgname=my.beautyCamera";
        if(SysConfig.GetAppVer(context) != null && SysConfig.GetAppVer(context).contains("r18"))
        {
            url = "http://phone.poco.cn/app/beautycamera";
        }
        unlockResourceByWeiXin(context, shareText, url, thumb, cb);
    }

    /**
     * 微信解锁素材
     *
     * @param context
     * @param shareText
     * @param url
     * @param thumb
     * @param cb
     */
    public static void unlockResourceByWeiXin(Context context, String shareText, String url, Bitmap thumb, final SendWXAPI.WXCallListener cb)
    {
        if(context == null) return;
        initBlogConfig(context);
        WeiXinBlog mWeiXin = new WeiXinBlog(context);
        if(mWeiXin.sendUrlToWeiXin(url, shareText, "", thumb, false))
        {
            SendWXAPI.WXCallListener listener = new SendWXAPI.WXCallListener()
            {
                @Override
                public void onCallFinish(int result)
                {
                    SendWXAPI.removeListener(this);
                    if(cb != null)
                    {
                        cb.onCallFinish(result);
                    }
                }
            };
            SendWXAPI.addListener(listener);
        }
        else showWeiXinErrorMessage(context, mWeiXin.LAST_ERROR, false);
    }

    public static void msgBox(final Context context, final String content)
    {
        if(Thread.currentThread().getId() == 1)
        {
            AlertDialog dlg = new AlertDialog.Builder(context).create();
            dlg.setTitle(context.getResources().getString(R.string.tips));
            dlg.setMessage(content);
            dlg.setButton(AlertDialog.BUTTON_POSITIVE, context.getResources().getString(R.string.ensure), (DialogInterface.OnClickListener)null);
            dlg.show();
        }
        else
        {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    AlertDialog dlg = new AlertDialog.Builder(context).create();
                    dlg.setTitle(context.getResources().getString(R.string.tips));
                    dlg.setMessage(content);
                    dlg.setButton(AlertDialog.BUTTON_POSITIVE, context.getResources().getString(R.string.ensure), (DialogInterface.OnClickListener)null);
                    dlg.show();
                }
            });
        }
    }

    /**
     * 显示微信错误信息（在主UI线程调用）
     *
     * @param context
     * @param error_code     错误码，对应WeiboInfo中的参数
     * @param WXSceneSession true为发送微信好友，false为发送微信朋友圈
     */
    public static void showWeiXinErrorMessage(Context context, int error_code, boolean WXSceneSession)
    {
        if(context == null) return;

        switch(error_code)
        {
            case WeiboInfo.BLOG_INFO_IMAGE_IS_NULL:
                Toast.makeText(context, context.getResources().getString(R.string.share_error_image_is_null), Toast.LENGTH_LONG).show();
                break;

            case WeiboInfo.BLOG_INFO_URL_IS_NULL:
                Toast.makeText(context, context.getResources().getString(R.string.share_error_url), Toast.LENGTH_LONG).show();
                break;

            case WeiboInfo.BLOG_INFO_CLIENT_VERSION_LOW:
                if(WXSceneSession)
                {
                    Toast.makeText(context, context.getResources().getString(R.string.share_weixin_error_client_version_low1), Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(context, context.getResources().getString(R.string.share_weixin_error_client_version_low2), Toast.LENGTH_LONG).show();
                }
                break;

            case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
                Toast.makeText(context, context.getResources().getString(R.string.share_weixin_error_client_no_install), Toast.LENGTH_LONG).show();
                break;

            case WeiboInfo.BLOG_INFO_THUMB_ERROR:
                Toast.makeText(context, context.getResources().getString(R.string.share_error_thumb), Toast.LENGTH_LONG).show();
                break;

            case WeiboInfo.BLOG_INFO_CONTEXT_IS_NULL:
                Toast.makeText(context, context.getResources().getString(R.string.share_error_context_is_null), Toast.LENGTH_LONG).show();
                break;

            case WeiboInfo.BLOG_INFO_FILE_IS_NULL:
                Toast.makeText(context, context.getResources().getString(R.string.share_error_file_is_null), Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * 根据错误码显示错误信息Toast（只能主线程调用）
     * @param context
     * @param error_code
     */
    public static void showQQErrorMessageToast(Context context, int error_code)
    {
        if(context == null) return;
        switch(error_code)
        {
            case WeiboInfo.BLOG_INFO_IMAGE_IS_NULL:
                Toast.makeText(context, context.getResources().getString(R.string.share_error_image_is_null), Toast.LENGTH_LONG).show();
                break;

            case WeiboInfo.BLOG_INFO_URL_IS_NULL:
                Toast.makeText(context, context.getResources().getString(R.string.share_error_url), Toast.LENGTH_LONG).show();
                break;

            case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
                Toast.makeText(context, context.getResources().getString(R.string.share_qq_error_clinet_no_install), Toast.LENGTH_LONG).show();
                break;

            case WeiboInfo.BLOG_INFO_THUMB_ERROR:
                Toast.makeText(context, context.getResources().getString(R.string.share_error_thumb), Toast.LENGTH_LONG).show();
                break;

            case WeiboInfo.BLOG_INFO_CONTEXT_IS_NULL:
                Toast.makeText(context, context.getResources().getString(R.string.share_error_context_is_null), Toast.LENGTH_LONG).show();
                break;

            case WeiboInfo.BLOG_INFO_VIDEO_IS_NULL:
                Toast.makeText(context, context.getResources().getString(R.string.share_error_video_is_null), Toast.LENGTH_LONG).show();
                break;
        }
    }

    public static void showCircleCodeMessage(Context context, int code)
    {
        if(context == null) return;

        switch(code)
        {
            case 10000:
                break;

            case 10001:
                Toast.makeText(context, context.getResources().getString(R.string.share_circle_client_no_install), Toast.LENGTH_LONG).show();
                break;

            case 10002:
                Toast.makeText(context, context.getResources().getString(R.string.share_circle_without_data), Toast.LENGTH_LONG).show();
                break;

            case 10004:
                Toast.makeText(context, context.getResources().getString(R.string.share_circle_unsupport_version), Toast.LENGTH_LONG).show();
                break;

            case 10005:
                Toast.makeText(context, context.getResources().getString(R.string.share_circle_without_image), Toast.LENGTH_LONG).show();
                break;

            case 10007:
                Toast.makeText(context, context.getResources().getString(R.string.share_circle_unsupport_video), Toast.LENGTH_LONG).show();
                break;

            case 10009:
                Toast.makeText(context, context.getResources().getString(R.string.share_circle_illegal_video), Toast.LENGTH_LONG).show();
                break;

            case 0:
                Toast.makeText(context, context.getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
                break;

            case 1:
                Toast.makeText(context, context.getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
                break;

            default:
                Toast.makeText(context, context.getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * 截屏(只能在Activity中调用)
     *
     * @param context
     * @param screen_w 屏幕宽
     * @param screen_h 屏幕高
     * @return 截屏图片
     */
    public static Bitmap screenCapture(Context context, int screen_w, int screen_h) {
        if (context == null || screen_w <= 0 || screen_h <= 0)
            return null;
        //获取屏幕
        View decorview = ((Activity) context).getWindow().getDecorView();
        decorview.setDrawingCacheEnabled(true);
        decorview.buildDrawingCache();
        Bitmap screen = decorview.getDrawingCache();
        if (screen != null && !screen.isRecycled()) {
            Bitmap out = MakeBmp.CreateFixBitmap(screen, screen_w, screen_h, MakeBmp.POS_CENTER, 0, Bitmap.Config.ARGB_8888);
            decorview.setDrawingCacheEnabled(false);
            return out;
        }
        return null;
    }

    /**
     * 制作磨砂玻璃背景
     *
     * @param bmp
     * @return
     */
    public static Bitmap makeGlassBackground(Bitmap bmp) {
        if (bmp == null || bmp.isRecycled())
            return null;

        return filter.fakeGlassBeauty(bmp, 0x33000000);
    }

    public Bitmap getGlassBackground()
    {
        if(mBindPocoDialogBG == null || mBindPocoDialogBG.isRecycled())
        {
            mBindPocoDialogBG = makeGlassBackground(screenCapture(getContext(), ShareData.m_screenWidth, ShareData.m_screenHeight));
        }
        return mBindPocoDialogBG;
    }
}
