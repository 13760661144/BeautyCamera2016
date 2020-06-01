package cn.poco.system;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;

import com.adnonstop.admasterlibs.AdUtils;
import com.adnonstop.admasterlibs.IAd;

import java.util.HashMap;

import cn.poco.blogcore.Tools;
import cn.poco.camera3.beauty.data.IBeautyShape;
import cn.poco.campaignCenter.api.ICampaign;
import cn.poco.campaignCenter.statistic.CampaignCenterTongJi;
import cn.poco.cloudalbumlibs.ITongJi;
import cn.poco.cloudalbumlibs.api.IAlbum;
import cn.poco.featuremenu.api.IFeatureMenu;
import cn.poco.loginlibs.ILogin;
import cn.poco.loginlibs.LoginUtils;
import cn.poco.loginlibs.info.UploadToken;
import cn.poco.scorelibs.ICredit;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.statisticlibs.IStat;
import cn.poco.statistics.TongJi2;
import cn.poco.storagesystemlibs.IStorage;
import cn.poco.storagesystemlibs.StorageStruct;
import cn.poco.storagesystemlibs.StorageUtils;
import cn.poco.storagesystemlibs.UpdateInfo;
import cn.poco.storagesystemlibs.UploadInfo;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.NetState;
import cn.poco.web.IWeb;
import my.beautyCamera.R;

public class AppInterface implements ICredit, IStorage, ILogin, IAlbum, ITongJi, IWeb, ICampaign, CampaignCenterTongJi, IStat, IAd, IFeatureMenu, IBeautyShape
{
	//private static final String DEV = "http://open.adnonstop.com/beauty_camera/biz/dev/api/public/index.php";
	private static final String BETA = "http://tw.adnonstop.com/beauty/app/api/beauty_camera/biz/beta/api/public/index.php";
	private static final String PROD = "http://open.adnonstop.com/beauty_camera/biz/prod/api/public/index.php";

	protected static AppInterface sInstance;
	protected static String sVer;
	protected static String sVer2;//带渠道号
	protected static String sIMEI;

	private String mBaseUrl;

	private AppInterface()
	{
		if(SysConfig.IsDebug())
		{
			mBaseUrl = BETA;
		}
		else
		{
			mBaseUrl = PROD;
		}
	}

	public synchronized static AppInterface GetInstance(Context context)
	{
		if(sInstance == null)
		{
			sInstance = new AppInterface();
		}
		//不把context保存到静态变量的目的是避免多activity造成泄漏
		if(context != null)
		{
			sVer = CommonUtils.GetAppVer(context);
			sVer2 = SysConfig.GetAppVer(context);
			sIMEI = CommonUtils.GetIMEI(context);
		}
		return sInstance;
	}

	@Override
	public String GetFolderImgListUrl()
	{
		return mBaseUrl + "?r=CloudPhotos/GetFolderImgList";
	}

	@Override
	public String GetUpdateFolderUrl()
	{
		return mBaseUrl + "?r=CloudPhotos/UpdateFolder";
	}

	@Override
	public String GetCloudAlbumStorage()
	{
		return mBaseUrl + "?r=CloudPhotos/GetVolume";
	}

	@Override
	public String CreateAlbumFolder()
	{
		return mBaseUrl + "?r=CloudPhotos/CreateFolder";
	}

	@Override
	public String updateAlbumFolder()
	{
		return mBaseUrl + "?r=CloudPhotos/UpdateFolder";
	}

	@Override
	public String GetAlbumFolderList()
	{
		return mBaseUrl + "?r=CloudPhotos/GetFolderList";
	}

	@Override
	public String GetMovePhotoUrl()
	{
		return mBaseUrl + "?r=CloudPhotos/MovePhoto";
	}

	@Override
	public String GetDeletePhotoUrl()
	{
		return mBaseUrl + "?r=CloudPhotos/DelPhoto";
	}

	@Override
	public String GetDeleteFolderUrl()
	{
		return mBaseUrl + "?r=CloudPhotos/DeleteFolder";
	}

	@Override
	public String GetCreditIncomeUrl()
	{
		return mBaseUrl + "?r=Credit/CreditIncome";
	}

	@Override
	public String GetCreditConsumerUrl()
	{
		return mBaseUrl + "?r=Credit/CreditConsumer";
	}

	@Override
	public String GetTaskCenterUrl()
	{
		return mBaseUrl + "?r=WapDuty/List";
	}

	@Override
	public String GetVerifyUrl()
	{
		return mBaseUrl + "?r=MessageVerify/SendSmsVerifyCode";
	}

	@Override
	public String GetCheckVerifyUrl()
	{
		return mBaseUrl + "?r=MessageVerify/CheckSmsVerifyCode";
	}

	@Override
	public String GetMobileRegisterUrl()
	{
		return mBaseUrl + "?r=OAuth/Register";
	}

	@Override
	public String GetFillRegisterInfoUrl()
	{
		return mBaseUrl + "?r=OAuth/RegisterUserInfo";
	}

	@Override
	public String GetUserLoginUrl()
	{
		return mBaseUrl + "?r=OAuth/Login";
	}

	@Override
	public String GetForgetPassWordUrl()
	{
		return mBaseUrl + "?r=OAuth/Forget";
	}

	@Override
	public String GetChangePasswordUrl()
	{
		return mBaseUrl + "?r=OAuth/ChangePassword";
	}

	@Override
	public String GetBindMobileUrl()
	{
		return mBaseUrl + "?r=OAuth/BindMobile";
	}

	@Override
	public String GetRefreshTokenUrl()
	{
		return mBaseUrl + "?r=OAuth/RefreshToken";
	}

	@Override
	public String GetUserInfoUrl()
	{
		return mBaseUrl + "?r=User/GetUserInfo";
	}

	@Override
	public String GetUpdateUserInfoUrl()
	{
		return mBaseUrl + "?r=User/UpdateUserInfo";
	}

	@Override
	public String GetTPLoginUrl()
	{
		return mBaseUrl + "?r=TPOAuth/Auth";
	}

	@Override
	public String GetUploadHeadThumbUrl(Context context)
	{
		if(ConnectivityManager.TYPE_WIFI == NetState.GetConnectNet(context))
		{
			return "http://os-upload-wifi.poco.cn/poco/upload";
		}
		else
		{
			return "http://os-upload.poco.cn/poco/upload";
		}
	}

	@Override
	public String GetUploadHeadThumbTokenUrl()
	{
		return mBaseUrl + "?r=Common/AliyunOSSToken";
	}

	@Override
	public String GetTokenUrl()
	{
		return mBaseUrl + "?r=Common/AliyunOSSToken";
	}

	@Override
	public String GetUpdateMyWebUrl()
	{
		return mBaseUrl + "?r=CloudPhotos/SavePhoto";
	}

	@Override
	public String MakeUpdateMyWebData(UpdateInfo info)
	{
		return StorageUtils.EncodeUpdateData(info, this);
	}

	@Override
	public UploadInfo GetUploadInfo(StorageStruct str, int num)
	{
		return StorageUtils.GetTokenInfo(str.mUserId, str.mAccessToken, num, str.mIsAlbum, this);
	}

	@Override
	public String GetAppName()
	{
		return "beauty_camera_android";
	}

	@Override
	public String GetAppVer()
	{
		return sVer;
	}

	@Override
	public String GetMKey()
	{
		return sIMEI;
	}

	@Override
	public void createNewAlbum(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_相册创建页_创建);
	}

	@Override
	public void goBackFromCreateAlbum(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_相册创建页_返回);
	}

	@Override
	public void createAlbum(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_首页_新建相册);
	}

	@Override
	public void createNameByShortCut(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_相册创建页_快速创建标题);
	}

	@Override
	public void goToCloudSettingFrame(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_首页_设置);
	}

	@Override
	public void clickTransportListInSettingFrame(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_设置_传输列表);
	}

	@Override
	public void onClickWiFiTransportButton(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_设置_WIFI传输开关);
	}

	@Override
	public void uploadPhoto(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_内页_上传照片);
	}

	@Override
	public void longPressToChoose(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_内页_长按下弹出多选);
	}

	@Override
	public void choose(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_内页_多选);
	}

	@Override
	public void chooseToMove(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_内页_多选下移动);
	}

	@Override
	public void chooseToDelete(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_内页_多选下删除);
	}

	@Override
	public void chooseToSave(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_内页_多选下保存至本地);
	}

	@Override
	public void deleteAlbum(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_内页_删除相册);
	}

	@Override
	public void renameAlbum(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_内页_相册重命名);
	}

	@Override
	public void deleteOnBig(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_照片详情页_删除);
	}

	@Override
	public void saveOnBig(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_照片详情页_保存到本地);
	}

	@Override
	public void moveOnBig(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_照片详情页_移动);
	}

	@Override
	public void firstCancelToDeleteAlbum(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_相册删除_第一次弹框_取消);
	}

	@Override
	public void confirmDeleteToDeleteAlbum(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_相册删除_提示框_删除相册);
	}

	@Override
	public void confirmDeleteAlbumReally(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_相册删除_提示框_确认删除);
	}

	@Override
	public void secondCancelToDeleteAlbum(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_相册删除_二次弹框_取消);
	}

	@Override
	public void transportWaitClick(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_传输列表_等待传输列点击);
	}

	@Override
	public void cancelCellularToTransport(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_设置_询问弹框_蜂窝网络上传_取消);
	}

	@Override
	public void confirmCellularToTransport(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_设置_询问弹框_蜂窝网络上传_确认);
	}

	@Override
	public void uploadingBar(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_正在上传bar);
	}

	@Override
	public void transportWaitBar(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_等待传输bar);
	}

	@Override
	public void transportErrorBar(Context context)
	{
		TongJi2.AddCountByRes(context, R.integer.云相册_传输失败bar);
	}

	@Override
	public String GetAppUpdateInfoUrl()
	{
		return mBaseUrl + "?r=Init/UpdateApp";
	}

	@Override
	public String getCampaignInfo()
	{
		return mBaseUrl + "?r=Operate/GetList";
	}

	public String getQAUrl()
	{
		String out = null;

		if(SysConfig.IsDebug())
		{
			out = "http://tw.adnonstop.com/beauty/app/wap/beauty_camera/beta/public/index.php?r=Feedback/list";
		}
		else
		{
			out = "http://wap.adnonstop.com/beauty_camera/prod/public/index.php?r=Feedback/list";
		}

		return out;
	}

	@Override
	public void onClickBanner(String id)
	{
		TongJi2.AddCountById(id);
	}

	@Override
	public void onClickShare(String id)
	{
		TongJi2.AddCountById(id);
	}

	@Override
	public void onClickTryRightNow(String id)
	{
		TongJi2.AddCountById(id);
	}

	@Override
	public String GetStatOldLiveUrl()
	{
		return "http://phtj.poco.cn/phone_tj.php";
	}

	@Override
	public String GetStatOldOfflineUrl()
	{
		return "http://phtjp.poco.cn/phone_tj_post.php";
	}

	@Override
	public String GetStatOnlineUrl()
	{
		String out = null;

		if(SysConfig.IsDebug())
		{
			out = "http://optimus.adnonstop.com/collect-beta";
		}
		else
		{
			out = "http://optimus.adnonstop.com/collect";
		}
		return out;
	}

	@Override
	public String GetStatOfflineUrl()
	{
		String out = null;

		if(SysConfig.IsDebug())
		{
			out = "http://optimus.adnonstop.com/collect-multi-beta";
		}
		else
		{
			out = "http://optimus.adnonstop.com/collect-multi";
		}
		return out;
	}

	@Override
	public String GetStatIMEI()
	{
		return sIMEI;
	}

	@Override
	public String GetStatTJVer()
	{
		return "3";
	}

	@Override
	public String GetStatAppVer()
	{
		return sVer2;
	}

	@Override
	public String GetStatAppId()
	{
		return "106_3";
	}

	@Override
	public String GetStatUserId(Context context)
	{
		return SettingInfo.GetPoco2Id(SettingInfoMgr.GetSettingSP4Process(context), true);
	}

	@Override
	public String GetStatUserToken(Context context)
	{
		return SettingInfo.GetPoco2Token(SettingInfoMgr.GetSettingSP4Process(context), true);
	}

	@Override
	public synchronized UploadToken GetStatNearToken(Context context)
	{
		UploadToken out = null;

		String userId = GetStatUserId(context);
		String userToken = GetStatUserToken(context);

		if(userId != null && userId.length() > 0 && userToken != null && userToken.length() > 0)
		{
			HashMap<String, String> data = new HashMap<>();
			CommonUtils.SP_ReadSP(context, LoginUtils.GPS_CONFIG_SP_NAME, data);

			String identify = data.get(LoginUtils.GPS_TOKEN_IDENTIFY);
			String expire = data.get(LoginUtils.GPS_TOKEN_EXPIRE);
			String accessKey = data.get(LoginUtils.GPS_TOKEN_ACCESS_KEY);
			String accessToken = data.get(LoginUtils.GPS_TOKEN_ACCESS_TOKEN);

			if(identify == null || identify.length() <= 0 || expire == null || expire.length() <= 0 || accessKey == null || accessKey.length() <= 0 || accessToken == null || accessToken.length() <= 0 || Tools.isBindExpired(expire, System.currentTimeMillis() / 1000, 3600))
			{
				out = LoginUtils.getUploadHeadThumbToken(userId, userToken, "jpg", this);
				if(out != null)
				{
					data.put(LoginUtils.GPS_TOKEN_IDENTIFY, out.mIdentify);
					data.put(LoginUtils.GPS_TOKEN_EXPIRE, out.mExpireTime);
					data.put(LoginUtils.GPS_TOKEN_ACCESS_KEY, out.mAccessKey);
					data.put(LoginUtils.GPS_TOKEN_ACCESS_TOKEN, out.mAccessToken);
					CommonUtils.SP_SaveMap(context, LoginUtils.GPS_CONFIG_SP_NAME, data);
				}
			}
		}

		if(out == null)
		{
			out = new UploadToken();
			out.mAccessKey = "ed3a70f144ca4d9e88af1ba4da26f8de5aea9d0d";
			out.mAccessToken = "f0c80d5ed6533fa51ae9442e5c440a00b6bd42e8";
			out.mExpireTime = "2114352000";
			out.mIdentify = "anonymous";
		}

		return out;
	}

	@Override
	public String GetStatUploadGpsUrl(Context context)
	{
//		if(ConnectivityManager.TYPE_WIFI == NetState.GetConnectNet(context))
//		{
//			return "http://near-api-wifi.adnonstop.com/location/record";
//		}
//		else
//		{
		return "http://near-api.adnonstop.com/location/record";
//		}
	}

	@Override
	public long GetMemoryMB(Context context)
	{
		return Runtime.getRuntime().maxMemory() / 1048576;
	}

	@Override
	public String GetPhoneName(Context context)
	{
		return Build.MODEL;
	}

	@Override
	public boolean IsDebug(Context context)
	{
		return SysConfig.IsDebug();
	}

	@Override
	public String GetAdUrl(Context context)
	{
		if(SysConfig.IsDebug())
		{
			return "http://tw.adnonstop.com/zt/web/index.php?r=api/tpad/data/list";
		}
		else
		{
			return "http://union.adnonstop.com/?r=api/tpad/data/list";
		}
	}

	@Override
	public String GetAdUserId(Context context)
	{
		return SettingInfo.GetPoco2Id(SettingInfoMgr.GetSettingSP4Process(context), true);
	}

	@Override
	public String GetAdAppChannel(Context context)
	{
		return ConfigIni.getMiniVer().replace("_", "");
	}

	@Override
	public String GetUserAgentString(Context context)
	{
		return AdUtils.USER_AGENT;
	}

	@Override
	public String GetAdDefPostApi(Context context)
	{
		if(SysConfig.IsDebug())
		{
			return "http://tw.adnonstop.com/zt/web/index.php?r=api/v1/appdata/add";
		}
		else
		{
			return "http://zt.adnonstop.com/index.php?r=api/v1/appdata/add";
		}
	}

	@Override
	public String GetFeatureMenuData() {
		return mBaseUrl + "?r=switch/init/getdata";
	}

	@Override
	public String GetBeautyShapeApi()
	{
		return mBaseUrl + "?r=ucd/index/get";
	}

	@Override
	public String SaveBeautyShapeApi()
	{
		return mBaseUrl + "?r=ucd/index/save";
	}

	@Override
	public String GetAdAppName(Context context)
	{
		return "beauty_business";
	}

	@Override
	public String GetAdAppVer()
	{
		if(SysConfig.IsDebug())
		{
			return "88.8.8";
		}
		else
		{
			return GetAppVer();
		}
	}
}
