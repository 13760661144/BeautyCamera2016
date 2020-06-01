package cn.poco.login;

import android.text.TextUtils;

import com.adnonstop.beautyaccount.CallbackListener;
import com.adnonstop.beautyaccount.HttpRequest;
import com.adnonstop.beautyaccount.LoginConstant;
import com.adnonstop.beautyaccount.RequestParam;

import java.io.File;
import java.util.HashMap;

import cn.poco.loginlibs.LoginUtils;
import cn.poco.loginlibs.info.BaseInfo;
import cn.poco.loginlibs.info.CheckVerifyInfo;
import cn.poco.loginlibs.info.LoginInfo;
import cn.poco.loginlibs.info.RegisterLoginInfo;
import cn.poco.loginlibs.info.TPLoginInfo;
import cn.poco.loginlibs.info.UserInfo;
import cn.poco.loginlibs.info.VerifyInfo;
import okhttp3.MultipartBody;

public class LoginUtils2
{
	/**
	 * 手机登录
	 *
	 * @param zoneNum
	 * @param phone
	 * @param password
	 * @param callback 回调，类型转换为LoginInfo
	 */
	public static void userLogin(String zoneNum, String phone, String password, final HttpResponseCallback callback)
	{
		if(zoneNum == null || zoneNum.length() <= 0) zoneNum = "86";
		String loginParam = RequestParam.loginParam(zoneNum, phone, password);
		HttpRequest.getInstance().postRequest(LoginConstant.LOGIN_URL, loginParam, new CallbackListener()
		{
			@Override
			public void success(com.alibaba.fastjson.JSONObject jsonObject, String s)
			{
				LoginInfo out = new LoginInfo();
				if(jsonObject != null && out.DecodeJsonData(jsonObject.toString()))
				{
					out.mCode = 0;
					out.mProtocolCode = 200;
				}
				else
				{
					out.mCode = -1;
					out.mMsg = "登录失败";
				}
				if(callback != null) callback.response(out);
			}

			@Override
			public void failure(int code, String msg, String s1)
			{
				LoginInfo out = new LoginInfo();
				out.mCode = code;
				if(code < 0) out.mMsg = "网络连接失败";
				else out.mMsg = msg;
				if(callback != null) callback.response(out);
			}
		}, null);
	}

	/**获取验证码
	 * @param zoneNum 国际区号
	 * @param phone   手机号码
	 * @param type    验证码类型
	 * @param callback 回调，类型转换为VerifyInfo
	 */
	public static void getVerifyCode(String zoneNum, String phone, LoginUtils.VerifyCodeType type, final HttpResponseCallback callback)
	{
		if(zoneNum == null || zoneNum.length() <= 0) zoneNum = "86";
		String verifyCodeType = null;
		switch(type)
		{
			case register:
				verifyCodeType = LoginConstant.FLAG_REGISTER;
				break;

			case find:
				verifyCodeType = LoginConstant.FLAG_FIND;
				break;

			case bind_mobile:
				verifyCodeType = LoginConstant.FLAG_BIND;
				break;
		}
		String sendSmsParam = RequestParam.sendSmsParam(zoneNum, phone, verifyCodeType);
		HttpRequest.getInstance().postRequest(LoginConstant.SEND_SMS_VERIFYCODE, sendSmsParam, new CallbackListener()
		{
			@Override
			public void success(com.alibaba.fastjson.JSONObject jsonObject, String s)
			{
				VerifyInfo out = new VerifyInfo();
				if(jsonObject != null && out.DecodeJsonData(jsonObject.toString()))
				{
					out.mCode = 0;
					out.mProtocolCode = 200;
				}
				else
				{
					out.mCode = -1;
					out.mMsg = "获取验证码失败";
				}
				if(callback != null) callback.response(out);
			}

			@Override
			public void failure(int code, String msg, String s1)
			{
				VerifyInfo out = new VerifyInfo();
				out.mCode = code;
				if(code < 0) out.mMsg = "网络连接失败";
				else out.mMsg = msg;
				if(callback != null) callback.response(out);
			}
		}, null);
	}

	/**第三方登录绑定手机获取验证码
	 * @param zoneNum 国际区号
	 * @param phone   手机号码
	 * @param userId   用户id
	 * @param callback 回调，类型转换为VerifyInfo
	 */
	public static void getVerifyCode(String zoneNum, String phone, String userId, final HttpResponseCallback callback)
	{
		long uid;
		try
		{
			uid = Long.valueOf(userId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			BaseInfo out = new BaseInfo();
			out.mCode = -1;
			out.mMsg = "获取验证码失败，用户id错误";
			if(callback != null) callback.response(out);
			return;
		}
		if(zoneNum == null || zoneNum.length() <= 0) zoneNum = "86";

		String sendSmsParam = RequestParam.bindPhoneSendSmsParam(zoneNum, phone, LoginConstant.FLAG_BIND, uid);
		HttpRequest.getInstance().postRequest(LoginConstant.SEND_SMS_VERIFYCODE_FOR_BIND_PHONE, sendSmsParam, new CallbackListener()
		{
			@Override
			public void success(com.alibaba.fastjson.JSONObject jsonObject, String s)
			{
				VerifyInfo out = new VerifyInfo();
				if(jsonObject != null && out.DecodeJsonData(jsonObject.toString()))
				{
					out.mCode = 0;
					out.mProtocolCode = 200;
				}
				else
				{
					out.mCode = -1;
					out.mMsg = "获取验证码失败";
				}
				if(callback != null) callback.response(out);
			}

			@Override
			public void failure(int code, String msg, String s1)
			{
				VerifyInfo out = new VerifyInfo();
				out.mCode = code;
				if(code < 0) out.mMsg = "网络连接失败";
				else out.mMsg = msg;
				if(callback != null) callback.response(out);
			}
		}, null);
	}

	/** 校对验证码
	 * @param zoneNum    国际区号
	 * @param phone      手机号码
	 * @param type       验证码类型
	 * @param verifyCode 验证码
	 * @param callback 	 回调，类型转换为CheckVerifyInfo
	 */

	public static void checkVerifyCode(String zoneNum, String phone, String verifyCode, LoginUtils.VerifyCodeType type, final HttpResponseCallback callback)
	{
		if(zoneNum == null || zoneNum.length() <= 0) zoneNum = "86";
		String verifyCodeType = null;
		switch(type)
		{
			case register:
				verifyCodeType = LoginConstant.FLAG_REGISTER;
				break;

			case find:
				verifyCodeType = LoginConstant.FLAG_FIND;
				break;

			case bind_mobile:
				verifyCodeType = LoginConstant.FLAG_BIND;
				break;
		}
		String checkSmsParam = RequestParam.checkSmsParam(zoneNum, phone, verifyCode, verifyCodeType);
		HttpRequest.getInstance().postRequest(LoginConstant.CHECK_SMS_VERIFYCODE, checkSmsParam, new CallbackListener()
		{
			@Override
			public void success(com.alibaba.fastjson.JSONObject jsonObject, String s)
			{
				CheckVerifyInfo out = new CheckVerifyInfo();
				if(jsonObject != null && out.DecodeJsonData(jsonObject.toString()))
				{
					out.mCode = 0;
					out.mProtocolCode = 200;
				}
				else
				{
					out.mCode = -1;
					out.mMsg = "验证码不正确";
				}
				if(callback != null) callback.response(out);
			}

			@Override
			public void failure(int code, String msg, String s1)
			{
				CheckVerifyInfo out = new CheckVerifyInfo();
				out.mCode = code;
				if(code < 0) out.mMsg = "网络连接失败";
				else out.mMsg = msg;
				if(callback != null) callback.response(out);
			}
		}, null);
	}


	/** 注册
	 * @param zoneNum    国际区号
	 * @param phone      手机号码
	 * @param verifyCode 验证码
	 * @param callback   回调，类型转换为RegisterLoginInfo
	 */
	public static void register(String zoneNum, String phone, String verifyCode, final HttpResponseCallback callback)
	{
		if(zoneNum == null || zoneNum.length() <= 0) zoneNum = "86";
		String registerParam = RequestParam.registerParam(zoneNum, phone,null, verifyCode);
		HttpRequest.getInstance().postRequest(LoginConstant.REGISTER_URL, registerParam, new CallbackListener()
		{
			@Override
			public void success(com.alibaba.fastjson.JSONObject jsonObject, String s)
			{
				RegisterLoginInfo out = new RegisterLoginInfo();
				if(jsonObject != null && out.DecodeJsonData(jsonObject.toString()))
				{
					out.mCode = 0;
					out.mProtocolCode = 200;
				}
				else
				{
					out.mCode = -1;
					out.mMsg = "注册失败";
				}
				if(callback != null) callback.response(out);
			}

			@Override
			public void failure(int code, String msg, String s1)
			{
				RegisterLoginInfo out = new RegisterLoginInfo();
				out.mCode = code;
				if(code < 0) out.mMsg = "网络连接失败";
				else out.mMsg = msg;
				if(callback != null) callback.response(out);
			}
		}, null);
	}

	/**
	 * 填写注册信息
	 *
	 * @param userId
	 * @param accessToken
	 * @param userIcon
	 * @param nickname
	 * @param password
	 * @param callback 回调，类型转换为BaseInfo
	 */
	public static void fillUserRegisterInfo(String userId, String accessToken, String userIcon, String nickname, String password, final HttpResponseCallback callback)
	{
		long uid;
		try
		{
			uid = Long.valueOf(userId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			BaseInfo out = new BaseInfo();
			out.mCode = -1;
			out.mMsg = "递交信息失败，用户id错误";
			if(callback != null) callback.response(out);
			return;
		}
		String userInfoParam = RequestParam.userInfoParam(uid, accessToken, userIcon, nickname, password);
		HttpRequest.getInstance().postRequest(LoginConstant.REGISTER_USERINFO_URL, userInfoParam, new CallbackListener()
		{
			@Override
			public void success(com.alibaba.fastjson.JSONObject jsonObject, String s)
			{
				BaseInfo out = new BaseInfo();
				out.mCode = 0;
				out.mProtocolCode = 200;
				if(callback != null) callback.response(out);
			}

			@Override
			public void failure(int code, String msg, String s1)
			{
				BaseInfo out = new BaseInfo();
				out.mCode = code;
				if(code < 0) out.mMsg = "网络连接失败";
				else out.mMsg = msg;
				if(callback != null) callback.response(out);
			}
		}, null);
	}

	/**
	 * 忘记密码
	 *
	 * @param zoneNum
	 * @param phone
	 * @param verifyCode
	 * @param password
	 * @param callback 回调，类型转换为LoginInfo
	 */
	public static void forgetPassWord(String zoneNum, String phone, String verifyCode, String password, final HttpResponseCallback callback)
	{
		if(zoneNum == null || zoneNum.length() <= 0) zoneNum = "86";
		String resetPwdParam = RequestParam.resetPwdParam(zoneNum, phone, verifyCode, password);
		HttpRequest.getInstance().postRequest(LoginConstant.FORGET_PWD_URL, resetPwdParam, new CallbackListener()
		{
			@Override
			public void success(com.alibaba.fastjson.JSONObject jsonObject, String s)
			{
				LoginInfo out = new LoginInfo();
				if(jsonObject != null && out.DecodeJsonData(jsonObject.toString()))
				{
					out.mCode = 0;
					out.mProtocolCode = 200;
				}
				else
				{
					out.mCode = -1;
					out.mMsg = "重置密码失败";
				}
				if(callback != null) callback.response(out);
			}

			@Override
			public void failure(int code, String msg, String s1)
			{
				LoginInfo out = new LoginInfo();
				out.mCode = code;
				if(code < 0) out.mMsg = "网络连接失败";
				else out.mMsg = msg;
				if(callback != null) callback.response(out);
			}
		}, null);
	}

	/**
	 * 修改密码
	 *
	 * @param userId
	 * @param accessToken
	 * @param oldPwd
	 * @param newPwd
	 * @param callback 回调，类型转换为BaseInfo
	 */
	public static void changePassWord(String userId, String accessToken, String oldPwd, String newPwd, final HttpResponseCallback callback)
	{
		long uid;
		try
		{
			uid = Long.valueOf(userId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			BaseInfo out = new BaseInfo();
			out.mCode = -1;
			out.mMsg = "修改密码失败，用户id错误";
			if(callback != null) callback.response(out);
			return;
		}
		String changePassword = RequestParam.changePassword(uid, accessToken, oldPwd, newPwd);
		HttpRequest.getInstance().postRequest(LoginConstant.CHANGE_PASSWORD_URL, changePassword, new CallbackListener()
		{
			@Override
			public void success(com.alibaba.fastjson.JSONObject jsonObject, String s)
			{
				BaseInfo out = new BaseInfo();
				out.mCode = 0;
				out.mProtocolCode = 200;
				if(callback != null) callback.response(out);
			}

			@Override
			public void failure(int code, String msg, String s1)
			{
				BaseInfo out = new BaseInfo();
				out.mCode = code;
				if(code < 0) out.mMsg = "网络连接失败";
				else out.mMsg = msg;
				if(callback != null) callback.response(out);
			}
		}, "changepwd");
	}

	/**
	 * 绑定手机
	 *
	 * @param zoneNum
	 * @param userId
	 * @param phone
	 * @param verifyCode
	 * @param password
	 * @param callback 回调，类型转换为BaseInfo
	 */
	public static void bindMobile(String zoneNum, String userId, String phone, String verifyCode, String password, final HttpResponseCallback callback)
	{
		if(zoneNum == null || zoneNum.length() <= 0) zoneNum = "86";
		String bindPhoneParam = RequestParam.bindPhoneParam(zoneNum, phone, verifyCode, userId, password);
		HttpRequest.getInstance().postRequest(LoginConstant.BIND_PHONE_URL, bindPhoneParam, new CallbackListener()
		{
			@Override
			public void success(com.alibaba.fastjson.JSONObject jsonObject, String s)
			{
				BaseInfo out = new BaseInfo();
				out.mCode = 0;
				out.mProtocolCode = 200;
				if(callback != null) callback.response(out);
			}

			@Override
			public void failure(int code, String msg, String s1)
			{
				BaseInfo out = new BaseInfo();
				out.mCode = code;
				if(code < 0) out.mMsg = "网络连接失败";
				else out.mMsg = msg;
				if(callback != null) callback.response(out);
			}
		}, null);
	}

	/**
	 * 刷新token
	 *
	 * @param userId
	 * @param refreshToken
	 * @param callback 回调，类型转换为LoginInfo
	 * @return
	 */
	public static void refreshToken(String userId, String refreshToken, final HttpResponseCallback callback)
	{
		long uid;
		try
		{
			uid = Long.valueOf(userId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			BaseInfo out = new BaseInfo();
			out.mCode = -1;
			out.mMsg = "刷新Token失败，用户id错误";
			if(callback != null) callback.response(out);
			return;
		}
		String refreshTokenParam = RequestParam.refreshTokenParam(uid, refreshToken);
		HttpRequest.getInstance().postRequest(LoginConstant.REFRESH_TOKEN_URL, refreshTokenParam, new CallbackListener()
		{
			@Override
			public void success(com.alibaba.fastjson.JSONObject jsonObject, String s)
			{
				LoginInfo out = new LoginInfo();
				if(jsonObject != null && out.DecodeJsonData(jsonObject.toString()))
				{
					out.mCode = 0;
					out.mProtocolCode = 200;
				}
				else
				{
					out.mCode = -1;
					out.mMsg = "刷新Token失败";
				}
				if(callback != null) callback.response(out);
			}

			@Override
			public void failure(int code, String msg, String s1)
			{
				LoginInfo out = new LoginInfo();
				out.mCode = code;
				if(code < 0) out.mMsg = "网络连接失败";
				else out.mMsg = msg;
				if(callback != null) callback.response(out);
			}
		}, null);
	}

	/**
	 * 微博第三方登录
	 *
	 * @param openid        第三方平台的用户ID
	 * @param accessToken   第三方平台的授权Token
	 * @param expiresIn     过期时间
	 * @param callback		回调，类型转换为TPLoginInfo
	 */
	public static void TPLogin(String openid, String accessToken, long expiresIn, final HttpResponseCallback callback)
	{
		String thirdLoginParam = RequestParam.thirdLoginParam(openid, accessToken, "", expiresIn, "", "sina");
		HttpRequest.getInstance().postRequest(LoginConstant.THIRD_LOGIN_URL, thirdLoginParam, new CallbackListener()
		{
			@Override
			public void success(com.alibaba.fastjson.JSONObject jsonObject, String s)
			{
				TPLoginInfo out = new TPLoginInfo();
				if(jsonObject != null && out.DecodeJsonData(jsonObject.toString()))
				{
					out.mCode = 0;
					out.mProtocolCode = 200;
				}
				else
				{
					out.mCode = -1;
					out.mMsg = "新浪登录失败";
				}
				if(callback != null) callback.response(out);
			}

			@Override
			public void failure(int code, String msg, String s1)
			{
				TPLoginInfo out = new TPLoginInfo();
				out.mCode = code;
				if(code < 0) out.mMsg = "网络连接失败";
				else out.mMsg = msg;
				if(callback != null) callback.response(out);
			}
		}, null);
	}

	/**
	 * 微信登陆
	 * @param unionId 微信独有
	 * @param callback 回调，类型转换为TPLoginInfo
	 */
	public static void weChatLogin(String openId, String accessToken, String refreshToken, long expiresIn, String unionId, final HttpResponseCallback callback)
	{
		String thirdLoginParam = RequestParam.thirdLoginParam(openId, accessToken, refreshToken, expiresIn, unionId, "weixin_open");
		HttpRequest.getInstance().postRequest(LoginConstant.THIRD_LOGIN_URL, thirdLoginParam, new CallbackListener()
		{
			@Override
			public void success(com.alibaba.fastjson.JSONObject jsonObject, String s)
			{
				TPLoginInfo out = new TPLoginInfo();
				if(jsonObject != null && out.DecodeJsonData(jsonObject.toString()))
				{
					out.mCode = 0;
					out.mProtocolCode = 200;
				}
				else
				{
					out.mCode = -1;
					out.mMsg = "微信登录失败";
				}
				if(callback != null) callback.response(out);
			}

			@Override
			public void failure(int code, String msg, String s1)
			{
				TPLoginInfo out = new TPLoginInfo();
				out.mCode = code;
				if(code < 0) out.mMsg = "网络连接失败";
				else out.mMsg = msg;
				if(callback != null) callback.response(out);
			}
		}, null);
	}

	/**
	 * 获取用户信息
	 *
	 * @param userId
	 * @param accessToken
	 * @param callback 回调，类型转换为UserInfo
	 */
	public static void getUserInfo(String userId, String accessToken, final HttpResponseCallback callback)
	{
		long uid;
		try
		{
			uid = Long.valueOf(userId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			BaseInfo out = new BaseInfo();
			out.mCode = -1;
			out.mMsg = "获取用户信息失败，用户id错误";
			if(callback != null) callback.response(out);
			return;
		}
		String userInfoParam = RequestParam.getUserInfoParam(uid, accessToken);
		HttpRequest.getInstance().postRequest(LoginConstant.GET_USER_INFO_URL, userInfoParam, new CallbackListener()
		{
			@Override
			public void success(com.alibaba.fastjson.JSONObject jsonObject, String s)
			{
				UserInfo out = new UserInfo();
				if(jsonObject != null && out.DecodeJsonData(jsonObject.toString()))
				{
					out.mCode = 0;
					out.mProtocolCode = 200;
				}
				else
				{
					out.mCode = -1;
					out.mMsg = "获取用户信息失败";
				}
				if(callback != null) callback.response(out);
			}

			@Override
			public void failure(int code, String msg, String s1)
			{
				UserInfo out = new UserInfo();
				out.mCode = code;
				if(code < 0) out.mMsg = "网络连接失败";
				else out.mMsg = msg;
				if(callback != null) callback.response(out);
			}
		}, null);
	}

	/**
	 * 更新用户信息
	 *
	 * @param userId
	 * @param accessToken
	 * @param info
	 * @param callback 回调，类型转换为BaseInfo
	 */
	public static void updateUserInfo(String userId, String accessToken, UserInfo info, final HttpResponseCallback callback)
	{
		HashMap<String, Object> map = new HashMap<>();
		map.put("accessToken", accessToken);
		map.put("userId", userId);
		if(!TextUtils.isEmpty(info.mNickname))
		{
			map.put("nickname", info.mNickname);
		}
		if(!TextUtils.isEmpty(info.mUserIcon))
		{
			map.put("userIcon", info.mUserIcon);
		}
		if(!TextUtils.isEmpty(info.mSex))
		{
			map.put("sex", info.mSex);
		}
		if(!TextUtils.isEmpty(info.mMobile))
		{
			map.put("mobile", info.mMobile);
		}
		if(!TextUtils.isEmpty(info.mZoneNum))
		{
			map.put("zoneNum", info.mZoneNum);
		}
		if(!TextUtils.isEmpty(info.mSignature))
		{
			map.put("signature", info.mSignature);
		}
		if(!TextUtils.isEmpty(info.mBirthdayYear))
		{
			map.put("birthdayYear", info.mBirthdayYear);
		}
		if(!TextUtils.isEmpty(info.mBirthdayMonth))
		{
			map.put("birthdayMonth", info.mBirthdayMonth);
		}
		if(!TextUtils.isEmpty(info.mBirthdayDay))
		{
			map.put("birthdayDay", info.mBirthdayDay);
		}
		if(!TextUtils.isEmpty(info.mLocationId))
		{
			map.put("locationId", info.mLocationId);
		}
		if(!TextUtils.isEmpty(info.mUserSpace))
		{
			map.put("userSpace", info.mUserSpace);
		}
		String updateUserInfoParam = RequestParam.updateUserInfoParam(map);
		HttpRequest.getInstance().postRequest(LoginConstant.UPDATE_USER_INFO_URL, updateUserInfoParam, new CallbackListener()
		{
			@Override
			public void success(com.alibaba.fastjson.JSONObject jsonObject, String s)
			{
				BaseInfo out = new BaseInfo();
				out.mCode = 0;
				out.mProtocolCode = 200;
				if(callback != null) callback.response(out);
			}

			@Override
			public void failure(int code, String msg, String s1)
			{
				BaseInfo out = new BaseInfo();
				out.mCode = code;
				if(code < 0) out.mMsg = "网络连接失败";
				else out.mMsg = msg;
				if(callback != null) callback.response(out);
			}
		}, null);
	}

	/**
	 * 上传头像缩略图
	 *
	 * @param callback 回调，类型转换为String
	 */
	public static void uploadHeadThumb(String userId, String accessToken, String path, final HttpResponseCallback callback)
	{
		if(userId == null || accessToken == null || path == null)
		{
			if(callback != null) callback.response(null);
			return;
		}
		long uid;
		try
		{
			uid = Long.valueOf(userId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if(callback != null) callback.response(null);
			return;
		}
		File file = new File(path);
		if(!file.exists())
		{
			if(callback != null) callback.response(null);
			return;
		}

		MultipartBody multipartBody = RequestParam.uploadPicParam(uid, accessToken, file);
		HttpRequest.getInstance().uploadPic(LoginConstant.UPLOAD_PIC_LIMIT_ONE, multipartBody, new CallbackListener()
		{
			@Override
			public void success(com.alibaba.fastjson.JSONObject jsonObject, String s)
			{
				if(jsonObject != null)
				{
					String head_url = jsonObject.getString("picUrl");
					if(callback != null) callback.response(head_url);
				}
			}

			@Override
			public void failure(int i, String s, String s1)
			{
				if(callback != null) callback.response(null);
			}
		}, null);
	}
}
