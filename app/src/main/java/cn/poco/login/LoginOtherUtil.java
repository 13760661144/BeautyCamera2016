package cn.poco.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

import com.adnonstop.beautymall.constant.BeautyUser;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.beauty.data.ShapeSyncResMgr;
import cn.poco.credits.Credit;
import cn.poco.exception.MyApplication;
import cn.poco.framework.BaseSite;
import cn.poco.framework.EventCenter;
import cn.poco.framework.EventID;
import cn.poco.framework.MyFramework2App;
import cn.poco.image.filter;
import cn.poco.login.site.RegisterLoginInfoPageSite;
import cn.poco.login.site.ResetPswPageSite;
import cn.poco.loginlibs.info.BaseInfo;
import cn.poco.loginlibs.info.LoginInfo;
import cn.poco.loginlibs.info.UserInfo;
import cn.poco.pocointerfacelibs.AbsBaseInfo;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

public class LoginOtherUtil
{
	public static Bitmap createBitmapByColor(int color, int w, int h)
	{
		if(color == 0) return null;
		Bitmap out = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(out);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		Paint pt = new Paint();
		pt.setAntiAlias(true);
		pt.setFilterBitmap(true);
		pt.setColor(color);
		pt.setStyle(Paint.Style.FILL);
		canvas.drawRect(0, 0, w, h, pt);
		return out;
	}

	public static Bitmap MakeDiffCornerRoundBmp(Bitmap bmp, float leftTopPx, float rightTopPx, float leftBottomPx, float rightBottomPx)
	{
		Bitmap out = null;
		int w = bmp.getWidth();
		int h = bmp.getHeight();

		if(bmp != null && bmp.getWidth() > 0 && bmp.getHeight() > 0 && w > 0 && h > 0)
		{
			out = Bitmap.createBitmap(w, h, Config.ARGB_8888);
			Canvas canvas = new Canvas(out);
			Paint pt = new Paint();
			pt.setColor(0xFFFFFFFF);
			pt.setAntiAlias(true);
			pt.setFilterBitmap(true);
			pt.setStyle(Paint.Style.FILL);
			Path path = RoundedRect(new RectF(0, 0, w, h), leftTopPx, rightTopPx, leftBottomPx, rightBottomPx);
			canvas.drawPath(path, pt);

			pt.reset();
			pt.setAntiAlias(true);
			pt.setFilterBitmap(true);
			pt.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			Matrix m = new Matrix();
			float s;
			{
				float s1 = (float)w / (float)bmp.getWidth();
				float s2 = (float)h / (float)bmp.getHeight();
				s = s1 > s2 ? s1 : s2;
			}
			m.postTranslate((w - bmp.getWidth()) / 2f, (h - bmp.getHeight()) / 2f);
			m.postScale(s, s, w / 2f, h / 2f);
			canvas.drawBitmap(bmp, m, pt);
		}

		return out;
	}

	static public Path RoundedRect(RectF rect, float leftTopPx, float rightTopPx, float leftBottomPx, float rightBottomPx)
	{
		float left = rect.left;
		float top = rect.top;
		float right = rect.right;
		float bottom = rect.bottom;
		Path path = new Path();
		if(leftTopPx < 0) leftTopPx = 0;
		if(rightTopPx < 0) rightTopPx = 0;
		if(leftBottomPx < 0) leftBottomPx = 0;
		if(rightBottomPx < 0) rightBottomPx = 0;
		float width = right - left;
		float height = bottom - top;
		float widthMinusCorners = (width - rightTopPx - leftTopPx);
		float heightMinusCorners = (height - leftTopPx - leftBottomPx);

		path.moveTo(right, top + rightTopPx);
		path.rQuadTo(0, -rightTopPx, -rightTopPx, -rightTopPx);//top-right corner
		path.rLineTo(-widthMinusCorners, 0);
		path.rQuadTo(-leftTopPx, 0, -leftTopPx, leftTopPx); //top-left corner
		path.rLineTo(0, heightMinusCorners);

		widthMinusCorners = (width - rightBottomPx - leftBottomPx);
		heightMinusCorners = (height - rightBottomPx - rightTopPx);
		path.rQuadTo(0, leftBottomPx, leftBottomPx, leftBottomPx);//bottom-left corner
		path.rLineTo(widthMinusCorners, 0);
		path.rQuadTo(rightBottomPx, 0, rightBottomPx, -rightBottomPx); //bottom-right corner
		path.rLineTo(0, -heightMinusCorners);

		path.close();//Given close, last lineto can be removed.

		return path;
	}

	/**
	 * 正则表达式是否含是在该字符集中
	 *
	 * @param strCheck 检验的字符串
	 * @param regEx    正则表达式
	 * @throws PatternSyntaxException
	 */
	public static boolean isInGather(String strCheck, String regEx) throws PatternSyntaxException
	{
		//生成Pattern对象并且编译一个正则表达式regEx
		Pattern p = Pattern.compile(regEx);
		//用Pattern类的matcher()方法生成一个Matcher对象
		return p.matcher(strCheck).matches();
	}


	public static StateListDrawable makeDrawableForSkin(int disableRes,int normalRes,int pressRes,Context context)
	{
		Bitmap bmp_disable = BitmapFactory.decodeResource(context.getResources(),disableRes);
//		bmp_disable = ImageUtils.AddSkin(PocoCamera.main,bmp_disable);

		Bitmap bmp_normal = BitmapFactory.decodeResource(context.getResources(),normalRes);
		bmp_normal = ImageUtils.AddSkin(context,bmp_normal);

		Bitmap bmp_press = BitmapFactory.decodeResource(context.getResources(),pressRes);
		bmp_press = ImageUtils.AddSkin(context,bmp_press);

		StateListDrawable drawable = new StateListDrawable();
		drawable.addState(new int[]{-android.R.attr.state_enabled},
				new BitmapDrawable(context.getResources(),bmp_disable));
		drawable.addState(new int[]{-android.R.attr.state_pressed},
				new BitmapDrawable(context.getResources(),bmp_normal));
		drawable.addState(new int[]{android.R.attr.state_pressed},new BitmapDrawable(context.getResources(),bmp_press));

		return drawable;
	}


	public static void showToastVeritical(Context context, String msg)
	{
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CLIP_VERTICAL, 0, 0);
		toast.show();
	}

	public static ProgressDialog showProgressDialog(ProgressDialog mProgressDialog, String message, Context context)
	{
		if(context == null) return null;
		if(mProgressDialog != null)
		{
			mProgressDialog.dismiss();
		}
		if(((Activity)context).isFinishing()) return null;
		mProgressDialog = ProgressDialog.show(context, "", message);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.show();
		return mProgressDialog;
	}

	public static void dismissProgressDialog(ProgressDialog mProgressDialog)
	{
		if(mProgressDialog != null)
		{
			mProgressDialog.dismiss();
		}
	}


	/**
	 * 检查网络状态
	 *
	 * @param context
	 * @return
	 */
	public static boolean isNetConnected(Context context)
	{
		ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if(info != null && info.isAvailable())
		{
			return info.isAvailable();
		}
		return false;
	}

//	public static boolean checkMoblie(String phoneNum)
//	{
//		String regex = "(\\+\\d+)?1[3458]\\d{9}$";  
//		return Pattern.matches(regex,phoneNum);  
//	}

	public static void setSettingInfo(LoginInfo info)
	{
		if(info == null)
		{
			return;
		}
//		final Context mContext = PocoCamera.main.getApplicationContext();
		SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(MyApplication.getInstance());
		settingInfo.SetPoco2Id(info.mUserId);
		settingInfo.SetPoco2ExpiresIn(info.mExpireTime + "");
		settingInfo.SetPoco2Token(info.mAccessToken);
		settingInfo.SetPoco2RefreshToken(info.mRefreshToken);
		SettingInfoMgr.Save(MyApplication.getInstance());

		BeautyUser.userId = info.mUserId;//福利社
		MyBeautyStat.checkLogin(MyApplication.getInstance());//统计

		//登录成功同步脸型数据
		ShapeSyncResMgr.getInstance().post2UpdateSyncData(MyFramework2App.getInstance().getApplicationContext());
	}

	public static void showToast(String content)
	{
		if(content != null && content.length() > 0)
			Toast.makeText(MyApplication.getInstance(), content, Toast.LENGTH_SHORT).show();
	}

//	public static UserInfo transUserInfo(cn.poco.login2.entity.UserInfo user) {
//		UserInfo userInfo = new UserInfo();
//		if(user != null)
//		{
//			userInfo.m_id = user.mUserId;
//			userInfo.m_birthdayDay = Integer.valueOf(user.mBirthdayDay);
//			userInfo.m_birthdayMonth = Integer.valueOf(user.mBirthdayMonth);
//			userInfo.m_birthdayYear = Integer.valueOf(user.mBirthdayYear);
//			userInfo.m_intro = user.mIntroduce;
//			userInfo.m_points = Integer.valueOf(user.mCredit);
//			userInfo.m_locationId = Long.valueOf(user.mLocationId);
//			userInfo.m_mobile = user.mPhone;
//			userInfo.m_sex = user.mSex;
//			userInfo.m_nickName = user.mNickName;
//			userInfo.m_thumbUrl = user.mAvatarUrl;
//			userInfo.m_zoneNum = user.mZoneNum;
//			userInfo.m_msg = user.mMsg;
//			userInfo.m_notice = user.mNotice;
//		}
//		return userInfo;
//	}
//
//	public static cn.poco.login2.entity.UserInfo transUserInfoOld(UserInfo user) {
//		cn.poco.login2.entity.UserInfo userInfo = new cn.poco.login2.entity.UserInfo();
//		if(user != null)
//		{
//			userInfo.mUserId = user.m_id;
//			userInfo.mAvatarUrl = user.m_thumbUrl;
//			userInfo.mBirthdayDay = String.valueOf(user.m_birthdayDay);
//			userInfo.mBirthdayMonth = String.valueOf(user.m_birthdayMonth);
//			userInfo.mBirthdayYear = String.valueOf(user.m_birthdayYear);
//			userInfo.mIntroduce = user.m_intro;
//			userInfo.mLocationId = String.valueOf(user.m_locationId);
//			userInfo.mPhone = user.m_mobile;
//			userInfo.mSex = user.m_sex;
//			userInfo.mZoneNum = user.m_zoneNum;
//			userInfo.mMsg = user.m_msg;
//			userInfo.mNotice = user.m_notice;
//			userInfo.mNickName = user.m_nickName;
//			userInfo.mCredit = String.valueOf(user.m_points);
//		}
//		return userInfo;
//	}

	public static class ResetPswAction extends LoginOtherActionBase
	{

		public static class ResetPswBaseInfo extends LoginOtherActionBaseInfo
		{
//			zoneNum, phoneNum, verityCode, passwd,
			public String m_zoneNum;
			public String m_phoneNum;
			public String m_verityCode;
			public String m_passwd;
		}

		private ProgressDialog mProgressDialog = null;
		private Context m_context;
		private ResetPswPageSite m_site;
		private LoginInfo m_loginInfo;
		private ResetPswBaseInfo m_baseInfo;
		public ResetPswAction(Context context, LoginOtherActionBaseInfo info) {
			super(context, info);

			m_context = context;
			m_site = (ResetPswPageSite) info.m_site;
			m_baseInfo = (ResetPswBaseInfo) info;
			m_loginInfo = info.loginInfo;

		}

		@Override
		public void start() {
			mProgressDialog = new ProgressDialog(m_context);
			mProgressDialog.setMessage(m_context.getResources().getString(R.string.loginutil_dailogtips));
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}

		@Override
		public void finish() {
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
		}

		@Override
		public void actionSuccess() {
			finish();
			LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginutil_modifypasswordsuccess));
			m_site.resetPswSuccess(m_context);
			new Thread(new Runnable() {
				@Override
				public void run() {
					Credit.syncCreditIncome(m_context, m_context.getResources().getInteger(R.integer.积分_每天使用) + "");
				}
			}).start();
		}

		@Override
		public void showErrorInformation(int code, BaseInfo info)
		{
			if(info != null && info.mMsg != null && info.mMsg.length() > 0) LoginOtherUtil.showToast(info.mMsg);
			else LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginutil_loginutil_modifypasswordfail));
		}

		@Override
		public void showErrorInformation(int code, LoginInfo info) {
//			if (code == 10001 || code == 10004) {
//				LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginutil_parametererror));
//			} else if (code == 10005) {
//				LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginutil_passwordformaterror));
//			} else if (code == 10006) {
//				LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginutil_notfound));
//			} else if (code == 10007) {
//				LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginutil_operafail));
//			} else if (code == 10008) {
//				LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginutil_unknowerror));
//			} else {
//				if(info != null && info.mMsg != null)
//				{
//					LoginOtherUtil.showToast(info.mMsg);
//				}
//				else
//				{
//					LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginutil_loginutil_modifypasswordfail));
//				}
//			}
			if(info != null && info.mMsg != null && info.mMsg.length() > 0) LoginOtherUtil.showToast(info.mMsg);
			else LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginutil_loginutil_modifypasswordfail));
		}

		@Override
		public void actionReal(HttpResponseCallback callback)
		{
			LoginUtils2.forgetPassWord(m_baseInfo.m_zoneNum, m_baseInfo.m_phoneNum, m_baseInfo.m_verityCode, m_baseInfo.m_passwd, callback);
		}

//		@Override
//		public AbsBaseInfo actionReal() {
//			LoginInfo out = null;
//			if(m_baseInfo != null)
//			{
//				out = LoginUtils.forgetPassWord(m_baseInfo.m_zoneNum, m_baseInfo.m_phoneNum, m_baseInfo.m_verityCode, m_baseInfo.m_passwd, AppInterface.GetInstance(m_context));
//			}
//			return out;
//		}

		@Override
		public void actionCredit(String userId, String tocken) {

		}
	}


	public static class BindPhoneAction extends LoginOtherActionBase
	{
		public static class BindPhoneActionBaseInfo extends LoginOtherActionBaseInfo
		{
			public String m_zoneNum;
			public String m_userId;
			public String m_phoneNum;
			public String m_verityCode;
			public String m_psw;
		}

		private ProgressDialog m_dialog = null;

		private Context m_context;

		private ResetPswPageSite m_site;

		private BindPhoneActionBaseInfo m_baseInfo;
		public BindPhoneAction(Context context, LoginOtherActionBaseInfo info) {
			super(context, info);

			m_context = context;
			m_baseInfo = (BindPhoneActionBaseInfo) info;
			m_site = (ResetPswPageSite) m_baseInfo.m_site;
		}

		@Override
		public void start() {
			m_dialog = new ProgressDialog(m_context);
			m_dialog.setMessage(m_context.getResources().getString(R.string.loginutil_bindphoneing));
			m_dialog.setCancelable(false);
			m_dialog.show();
		}

		@Override
		public void finish() {
			if (m_dialog != null) {
				m_dialog.dismiss();
			}
		}

		@Override
		public void actionSuccess() {
			if(m_baseInfo.loginInfo != null)
			{
				LoginOtherUtil.setSettingInfo(m_baseInfo.loginInfo);
			}
			Credit.CreditIncome(m_context, m_context.getResources().getInteger(R.integer.积分_关联手机)+"");
			HashMap<String,Object> datas = new HashMap<String, Object>();
			datas.put("id", m_baseInfo.m_userId);
			m_site.successBind(datas,m_context);
			finish();
		}

		@Override
		public void showErrorInformation(int code, BaseInfo info) {
			if (code == 10002) {
				LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginutil_writepassword));
			} else {
				if(info != null && info.mMsg != null && info.mMsg.length() > 0) LoginOtherUtil.showToast(info.mMsg);
				else LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginutil_bindphonefail));
			}
			finish();
		}

		@Override
		public void showErrorInformation(int code, LoginInfo info) {

		}

		@Override
		public void actionReal(HttpResponseCallback callback)
		{
			LoginUtils2.bindMobile(m_baseInfo.m_zoneNum, m_baseInfo.m_userId, m_baseInfo.m_phoneNum, m_baseInfo.m_verityCode, m_baseInfo.m_psw, callback);
		}

//		@Override
//		public AbsBaseInfo actionReal() {
//			BaseInfo out = null;
//			if(m_baseInfo != null)
//			{
//				out = LoginUtils.bindMobile(m_baseInfo.m_zoneNum, m_baseInfo.m_userId, m_baseInfo.m_phoneNum, m_baseInfo.m_verityCode, m_baseInfo.m_psw, AppInterface.GetInstance(m_context));
//			}
//			return out;
//		}

		@Override
		public void actionCredit(String userId, String tocken) {

		}
	}


	public static Bitmap getTempBK(Context context)
	{
		Bitmap out = null;
		out = Bitmap.createBitmap((int)(ShareData.m_screenWidth/2f),(int)(ShareData.m_screenHeight/2f),Config.ARGB_8888);
		Canvas canvas = new Canvas(out);

		int topColor = ImageUtils.GetSkinColor1(0xffbf699f);
		int bottomColor = ImageUtils.GetSkinColor2(0xffbeb2a3);
		int centerX = ShareData.m_screenWidth / 4;
		int centerY = ShareData.m_screenHeight / 4;
		int radius = (int)Math.sqrt(centerX * centerX + centerY * centerY);
		Paint mPaint = new Paint();
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(Color.WHITE);
		mPaint.setAntiAlias(true);
		LinearGradient shader = new LinearGradient(0, 0, ShareData.m_screenWidth , ShareData.m_screenHeight, new int[]{topColor, bottomColor}, null, Shader.TileMode.CLAMP);
		mPaint.setShader(shader);
		canvas.drawCircle(centerX, centerY, radius, mPaint);
		return out;
	}


		public static Bitmap MakeBkBmp2(Bitmap bmp, int outW, int outH, int fillColor)
		{
			Bitmap out;

			out = MakeBmp.CreateBitmap(bmp, outW / 2, outH / 2, (float)outW / (float)outH, 0, Config.ARGB_8888);
			//out = MakeBmp.CreateFixBitmap(bmp, outW, outH, MakeBmp.POS_CENTER, 0, Config.ARGB_8888);
			//filter.largeRblurOpacity(out, 100, 0);
			filter.fakeGlassBeauty(out, fillColor);
			Canvas canvas = new Canvas(out);
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
			canvas.drawColor(fillColor);

			return out;
		}


	public static class GlideCircleTransform extends BitmapTransformation
	{
		private int broderSize;
		public GlideCircleTransform(Context context, int broderSize) {
			super(context);
			this.broderSize = broderSize;
		}

		@Override
		protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
//			ImageUtils.MakeHeadBmp(bmp, ShareData.PxToDpi_xhdpi(166), 4, 0xffffffff);
//			return makeCircleBmp(pool, toTransform, outWidth, outHeight, broderSize, 0xffffffff);
			return ImageUtils.MakeHeadBmp(toTransform, ShareData.PxToDpi_xhdpi(166), 4, 0xffffffff);
		}

		@Override public String getId() {
			return getClass().getName();
		}
	}

	public static Bitmap makeCircleBmp(BitmapPool pool, Bitmap source, int outWidth, int outHeight, int broderSize, int broderColor)
	{
		if (source == null) return null;
		outWidth = ShareData.PxToDpi_hdpi(166);
		outHeight = ShareData.PxToDpi_xhdpi(166);
		int size = Math.min(outWidth, outHeight);
		Bitmap squared = Bitmap.createScaledBitmap(source, outWidth, outHeight, true);
		Bitmap result = pool.get(outWidth, outHeight, Bitmap.Config.ARGB_8888);
		if (result == null) {
			result = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
		}
		Canvas canvas = new Canvas(result);
		Paint paint = new Paint();
		paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
		paint.setAntiAlias(true);
		float r = size / 2f;
		canvas.drawCircle(r, r, r - broderSize, paint);

		if(broderSize > 0)
		{
			paint.reset();
			paint.setColor(broderColor);
			paint.setStrokeWidth(broderSize);
			paint.setStyle(Paint.Style.STROKE);
			paint.setAntiAlias(true);
			paint.setFlags(Paint.ANTI_ALIAS_FLAG);
			canvas.drawCircle(size / 2f, size / 2f, size / 2f - broderSize, paint);
		}

		return result;
	}


	//高斯模糊图片
	public static Bitmap getScreenBmpPath(Bitmap bmp)
	{
		bmp = LoginOtherUtil.MakeBkBmp2(bmp,ShareData.m_screenWidth,ShareData.m_screenHeight,0x19000000);
		return bmp;
	}



	//注册
	public static class RegisterAction extends LoginOtherActionBase
	{

		public static class RegisterBaseInfo extends LoginOtherActionBaseInfo
		{
			public String m_userId;
			public String m_token;
			public String m_iconUrl;
			public String m_nickName;
			public String m_pwd;
		}

		private ProgressDialog mProgress;

		private RegisterBaseInfo m_actionInfo;

		private RegisterLoginInfoPageSite m_site;

		private Context m_context;

		public RegisterAction(Context context, LoginOtherActionBaseInfo info) {
			super(context, info);
			m_actionInfo = (RegisterBaseInfo) info;
			m_site = (RegisterLoginInfoPageSite) info.m_site;
			m_context = context;
		}

		@Override
		public void start() {
			mProgress = new ProgressDialog(m_context);
			mProgress.setMessage(m_context.getResources().getString(R.string.loginutil_registering));
			mProgress.setCancelable(false);
			mProgress.show();
		}

		@Override
		public void finish() {
			if(mProgress != null)
			{
				mProgress.dismiss();
				mProgress = null;
			}
		}

		@Override
		public void actionSuccess() {
			if(m_loginInfo != null)
			{
				LoginOtherUtil.setSettingInfo(m_loginInfo);
			}
			if (m_site != null) {
				m_site.registerSuccess(m_context);
			}

			new Thread(new Runnable() {
				@Override
				public void run() {
					Credit.syncCreditIncome(m_context, m_context.getResources().getInteger(R.integer.积分_每天使用) + "");
				}
			}).start();
			Handler handler = new Handler();
			handler.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					TongJi2.AddOnlineClickCount(null, m_context.getResources().getInteger(R.integer.行为事件_美人账号注册成功) + "", m_context.getResources().getString(R.string.注册));
				}
			},200);
		}

		@Override
		public void showErrorInformation(int code, BaseInfo info) {
//			if(code == 10005)
//			{
//				LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginutil_registered));
//			}
//			else
//			{
				if(info != null && info.mMsg != null && info.mMsg.length() > 0) LoginOtherUtil.showToast(info.mMsg);
				else LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginutil_registerfail));
//			}
		}

		@Override
		public void showErrorInformation(int code, LoginInfo info)
		{
			if(info != null && info.mMsg != null && info.mMsg.length() > 0) LoginOtherUtil.showToast(info.mMsg);
			else LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginutil_registerfail));
		}

		@Override
		public void actionReal(HttpResponseCallback callback)
		{
			LoginUtils2.fillUserRegisterInfo(m_actionInfo.m_userId, m_actionInfo.m_token, m_actionInfo.m_iconUrl, m_actionInfo.m_nickName, m_actionInfo.m_pwd, callback);
		}

//		@Override
//		public AbsBaseInfo actionReal() {
//			BaseInfo out = null;
//			if(m_actionInfo != null)
//			{
//				out = LoginUtils.fillUserRegisterInfo(m_actionInfo.m_userId, m_actionInfo.m_token, m_actionInfo.m_iconUrl, m_actionInfo.m_nickName, m_actionInfo.m_pwd, AppInterface.GetInstance(m_context));
//			}
//			return out;
//		}

		@Override
		public void actionCredit(String userId, String tocken) {
			Credit.CreditIncome_notThreadinMethod(m_context, m_context.getResources().getInteger(R.integer.积分_手机注册)+"",userId,tocken,mHandler);
		}
	}


	public static abstract class LoginOtherActionBase
	{
		protected Context m_context;
		protected LoginInfo m_loginInfo;
		protected Handler mHandler = new Handler();
		public LoginOtherActionBase(Context context,LoginOtherActionBaseInfo info)
		{
			m_context = context;
			m_loginInfo = info.loginInfo;
		}

		public void action()
		{
			start();
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					final AbsBaseInfo info = actionReal();
//					if(info != null)
//					{
//						if(info.mCode == 0 && info.mProtocolCode == 200)
//						{
//							if(info instanceof LoginInfo)
//							{
//								m_loginInfo = (LoginInfo) info;
//							}
//							if(m_loginInfo != null && m_loginInfo.mCode == 0 && m_loginInfo.mProtocolCode == 200)
//							{
//								actionCredit(m_loginInfo.mUserId,m_loginInfo.mAccessToken);
//							}
//							final UserInfo userInfo = LoginUtils.getUserInfo(m_loginInfo.mUserId, m_loginInfo.mAccessToken, AppInterface.GetInstance(m_context));
//							mHandler.post(new Runnable() {
//								@Override
//								public void run() {
//									if(userInfo != null)
//									{
//										if(userInfo.mCode == 0 && info.mProtocolCode == 200)
//										{
//											UserMgr.SaveCache(userInfo);
//											if(info instanceof LoginInfo)
//											{
//												LoginOtherUtil.setSettingInfo(m_loginInfo);
//											}
//											actionSuccess();
//											EventCenter.sendEvent(EventID.HOMEPAGE_UPDATE_MENU_AVATAR);
//										}
//										else
//										{
//											actionSuccess();
//										}
//									}
//									finish();
//								}
//							});
//						}
//						else
//						{
//							mHandler.post(new Runnable() {
//								@Override
//								public void run() {
//									if(info instanceof BaseInfo)
//									{
//										showErrorInformation(info.mCode, (BaseInfo) info);
//									}
//									else if(info instanceof LoginInfo)
//									{
//										showErrorInformation(info.mCode, (LoginInfo) info);
//									}
//
//									finish();
//								}
//							});
//						}
//					}
//					else
//					{
//						mHandler.post(new Runnable() {
//							@Override
//							public void run() {
//								finish();
//								LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginutil_networkerror));
//							}
//						});
//					}
//				}
//			}).start();
//		};

			actionReal(new HttpResponseCallback()
			{
				@Override
				public void response(Object object)
				{
					if(object == null)
					{
						finish();
						LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginutil_networkerror));
						return;
					}
					final AbsBaseInfo info = (AbsBaseInfo)object;
					if(info.mCode == 0 && info.mProtocolCode == 200)
					{
						if(info instanceof LoginInfo)
						{
							m_loginInfo = (LoginInfo) info;
						}
						if(m_loginInfo != null && m_loginInfo.mCode == 0 && m_loginInfo.mProtocolCode == 200)
						{
							actionCredit(m_loginInfo.mUserId, m_loginInfo.mAccessToken);
						}
						LoginUtils2.getUserInfo(m_loginInfo.mUserId, m_loginInfo.mAccessToken, new HttpResponseCallback()
						{
							@Override
							public void response(Object object)
							{
								if(object == null)
								{
									finish();
									return;
								}
								UserInfo userInfo = (UserInfo)object;
								if(userInfo.mCode == 0 && info.mProtocolCode == 200)
								{
									UserMgr.SaveCache(userInfo);
									if(info instanceof LoginInfo)
									{
										LoginOtherUtil.setSettingInfo(m_loginInfo);
									}
									actionSuccess();
									EventCenter.sendEvent(EventID.HOMEPAGE_UPDATE_MENU_AVATAR);
								}
								else
								{
									actionSuccess();
								}
								finish();
							}
						});
					}
					else
					{
						if(info instanceof BaseInfo)
						{
							showErrorInformation(info.mCode, (BaseInfo) info);
						}
						else if(info instanceof LoginInfo)
						{
							showErrorInformation(info.mCode, (LoginInfo) info);
						}
						finish();
					}
				}
			});
		}

		public abstract void start();

		public abstract void finish();

		public abstract void actionSuccess();

		public abstract void showErrorInformation(int code,BaseInfo info);

		public abstract void showErrorInformation(int code,LoginInfo info);

//		public abstract AbsBaseInfo actionReal();
		public abstract void actionReal(HttpResponseCallback callback);

		public abstract void actionCredit(String userId,String tocken);


		public static class LoginOtherActionBaseInfo
		{
			public LoginInfo loginInfo;

			public BaseSite m_site;
		}

	}
}
