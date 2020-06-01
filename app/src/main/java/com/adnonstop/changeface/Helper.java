package com.adnonstop.changeface;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.adnonstop.hzbeautycommonlib.ShareValueHZCommon;
import com.poco.changeface_mp.frame.listener.Interpolator;
import com.poco.changeface_v.FaceConfig;
import com.poco.changeface_v.FaceFunctionIntercept;
import com.poco.changeface_v.FaceManager;
import com.poco.changeface_v.FaceShareManager;
import com.poco.changeface_v.FaceTJManager;
import com.poco.changeface_v.photo.manager.PhotoManager;

import org.json.JSONObject;

import cn.poco.camera.site.activity.CameraActivitySite;
import cn.poco.framework.MyFramework2App;
import cn.poco.framework2.BaseActivitySite;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.system.SysConfig;
import cn.poco.taskCenter.SendBlogActivity;
import cn.poco.tianutils.CommonUtils;

/**
 * Created by Raining on 2018/1/29.
 * 初始化换脸数据
 */

public class Helper
{
	public static void init(Context context){
		try {
			//换脸轻应用SDK
			FaceManager.getInstance().init(context, SysConfig.GetAppPath(), FaceConfig.APP_BEAUTY_CAMERA, CommonUtils.GetAppVer(context), "beautyCamera", SysConfig.IsDebug());

			//分享
			FaceShareManager.getInstance().setOnShareListener(new FaceShareManager.OnShareListener() {
				/**
				 * @param context 分享界面提供的上下文
				 * @param shareType {@link FaceShareManager SHARE_COMMUNITY / SHARE_CIRCLE / SHARE_WECHAT / SHARE_WEBO / SHARE_QQ_ZONE / SHARE_QQ}
				 * @param title 标题
				 * @param content 分享的内容
				 * @param picPath 图片地址
				 * @param jumpUrl 链接地址
				 */
				@Override
				public void onShare(final Context context, int shareType, String title, String content, String picPath, String jumpUrl) {
					Activity activity = (Activity)context;
					Intent intent = new Intent(activity, SendBlogActivity.class);
					intent.putExtra("shareTitle", title);
					intent.putExtra("shareContent", content);
					intent.putExtra("shareImgUrl", picPath);
					intent.putExtra("shareLinkUrl", jumpUrl);
					switch(shareType)
					{
						case FaceShareManager.SHARE_WECHAT:
							intent.putExtra("type", ShareValueHZCommon.SocialNetwork.WECHAT);
							break;

						case FaceShareManager.SHARE_WECHAT_CIRCLE:
							intent.putExtra("type", ShareValueHZCommon.SocialNetwork.WECHAT_MOMENT);
							break;

						case FaceShareManager.SHARE_WEBO:
							intent.putExtra("type", ShareValueHZCommon.SocialNetwork.WEIBO);
							break;

						case FaceShareManager.SHARE_QQ:
							intent.putExtra("type", ShareValueHZCommon.SocialNetwork.QQ);
							break;

						case FaceShareManager.SHARE_QQ_ZONE:
							intent.putExtra("type", ShareValueHZCommon.SocialNetwork.QZONE);
							break;
					}
					activity.startActivityForResult(intent, SendBlogActivity.RESULT_CODE);
				}
			});
		} catch(Throwable e) {
			e.printStackTrace();
		}

		//注册拦截器
		registerInterpolator();
	}

	public static long sTime;
	public static int sCameraId = -1;
	private static void registerInterpolator() {
		//拦截镜头
		FaceFunctionIntercept.getInstance().registerInterpolator(FaceFunctionIntercept.PHOTO_INTERCEPT_TYPE, new Interpolator() {
			@Override
			public boolean interpolator() {
				Activity activity = MyFramework2App.getInstance().getActivity();
				if(activity != null) {
					/*Intent intent = new Intent();
					intent.putExtra("openType", 0);
					intent.putExtra("cameraId", sCameraId);
					BaseActivitySite.setClass(intent, activity, CameraActivitySite.class);
					activity.startActivity(intent);
					activity.overridePendingTransition(0, 0);*/

                    Intent intent = new Intent(activity, ChangeFaceBackToCameraActivity.class);
                    intent.putExtra("openType", 0);
                    intent.putExtra("cameraId", sCameraId);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(0, 0);
				}
				return true;
			}
		});
		//拦截相册
		FaceFunctionIntercept.getInstance().registerInterpolator(FaceFunctionIntercept.ALBUM_INTERCEPT_TYPE, new Interpolator() {
			@Override
			public boolean interpolator() {
				Activity activity = MyFramework2App.getInstance().getActivity();
				if(activity != null) {
					Intent intent = new Intent();
					intent.putExtra("openType", 1);
                    intent.putExtra("cameraId", 0);
					BaseActivitySite.setClass(intent, activity, CameraActivitySite.class);
					activity.startActivity(intent);
					activity.overridePendingTransition(0, 0);
				}
				return true;
			}
		});

        //统计
        FaceTJManager.getInstance().setSensorTjListener(new FaceTJManager.OnSensorTjListener() {
            @Override
            public void onSensorListener(String eventName, JSONObject properties) {
                if (eventName != null && properties != null) {
                    MyBeautyStat.onClick(eventName, properties);
                }
            }
        });
	}

    public static void openIntroPage(Activity activity, boolean third) {
        FaceManager.getInstance().openChangeFace(activity, third);
    }

    private static Dialog mDialog;
	public static void showTip(Activity activity) {
        mDialog = PhotoManager.getInstance().showTipDialog(activity, true);
        if (mDialog != null) {
            mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mDialog = null;
                }
            });
        }
    }

    public static void clearAll() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
}
