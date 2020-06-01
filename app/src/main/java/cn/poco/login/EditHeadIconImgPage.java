package cn.poco.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera.RotationImg2;
import cn.poco.face.FaceDataV2;
import cn.poco.filterBeautify.BeautyHandler;
import cn.poco.filterBeautify.FilterBeautyParams;
import cn.poco.framework.BaseSite;
import cn.poco.framework.DataKey;
import cn.poco.framework.IPage;
import cn.poco.login.site.EditHeadIconImgPageSite;
import cn.poco.loginlibs.info.BaseInfo;
import cn.poco.loginlibs.info.LoginInfo;
import cn.poco.loginlibs.info.UserInfo;
import cn.poco.resource.FilterRes;
import cn.poco.resource.FilterResMgr2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.Utils;
import cn.poco.utils.WaitAnimDialog;
import my.beautyCamera.R;

/**
 * 上传头像页面
 */
public class EditHeadIconImgPage extends IPage
{
	//private static final String TAG = "编辑头像";

	public static final String BGPATH = "bgimg";

	public static final int REGISTER = 1;
	public static final int OTHER = 2;

	private Context mContext;
	private String imgPath;
	private Bitmap bgBitmap;
	private RelativeLayout m_MainLayout;

	private ProgressDialog mProgress;
	private Handler mHandler;
	private int mode;
	private String phoneNum;
	private String zoneNum;
	private String verityCode;
	private String userId;
	private String m_accessTocken;
	private boolean isUploading;
	private LoginInfo loginInfo;
	private EditHeadIconImgPageSite mSite;
	private Handler m_uiHandler;
	private BeautyHandler m_filterHanlder;
	private HandlerThread m_filterThread;
	private Bitmap mShowBmp;
	private Bitmap bmp;
	private int filterValue = 0;
	private FilterBeautyParams mFilterBeautyParams;

	public EditHeadIconImgPage(Context context, BaseSite site)
	{
		super(context, site);
		mContext = context;
		mSite = (EditHeadIconImgPageSite)site;
		initView();
		initData();

		TongJiUtils.onPageStart(getContext(), R.string.上传头像);
	}


	/**
	 * @param params imgs[]:图片路径
	 *               mode:注册设置头像和编辑资料修改头像 REGISTER和OTHER
	 *               info: LoginPageInfo
	 *               LoginInfo:用到id和token
	 *               filterValue:拍照进入用到
	 *               userId和tocken:从修改信息页进入上传图片用到的参数
	 *               bgimg:背景图片路径
	 */
	@Override
	public void SetData(HashMap<String, Object> params)
	{
		if(params != null)
		{
			RotationImg2[] img = (RotationImg2[])params.get("imgPath");
			int mode = (int)params.get("mode");
			if(img != null)
			{
				this.imgPath = (String)img[0].m_img;
				if(imgPath != null && imgPath.length() > 0) setImg(imgPath);
			}
			if(mode != 0)
			{
				this.mode = mode;
			}

			if(params.get("info") != null)
			{
				LoginPageInfo info = (LoginPageInfo)params.get("info");
				setNumAndVerityNum(info.m_phoneNum, info.m_verityCode, info.m_areaCodeNum);
			}
			if(params.get("loginInfo1") != null)
			{
				LoginInfo loginInfo1 = (LoginInfo)params.get("loginInfo1");
				setUserId(loginInfo1.mUserId, loginInfo1.mAccessToken);
			}

			if((String)params.get("userId") != null) this.userId = (String)params.get("userId");
			if((String)params.get("tocken") != null)
				this.m_accessTocken = (String)params.get("tocken");

			if(params.get("filterValue") != null)
			{
				this.filterValue = (int)params.get("filterValue");
				setFilterValue(filterValue);
			}

			if (params.containsKey(DataKey.CAMERA_TAILOR_MADE_PARAMS))
			{
				Object o = params.get(DataKey.CAMERA_TAILOR_MADE_PARAMS);
				if (o != null && o instanceof FilterBeautyParams) {
					mFilterBeautyParams = (FilterBeautyParams) o;
				}
			}

			if(params.get(BGPATH) != null)
			{
				String bgPath = (String) params.get(BGPATH);
				if(bgPath != null && bgPath.length() > 0)
				{
					Bitmap bmp = cn.poco.imagecore.Utils.DecodeFile(bgPath, null);
//					bmp = LoginOtherUtil.MakeBkBmp2(bmp, ShareData.m_screenWidth, ShareData.m_screenHeight, 0x7fffffff);
					if(bmp != null)
					{
						this.setBackgroundDrawable(new BitmapDrawable(LoginOtherUtil.MakeBkBmp2(bmp, ShareData.m_screenWidth, ShareData.m_screenHeight, 0xb2ffffff)));
					}
				}
				else
				{
					this.setBackgroundColor(Color.WHITE);
				}
			}
			else
			{
				this.setBackgroundColor(Color.WHITE);
			}
		}
	}

	private void initData()
	{
		m_filterThread = new HandlerThread("clipBmp");
		m_filterThread.start();

//		m_filterHanlder = new Handler(m_filterThread.getLooper())
//		{
//			@Override
//			public void handleMessage(Message msg)
//			{
//				// TODO Auto-generated method stub
//				mShowBmp = ImageProcessor.ConversionImgFilter(EditHeadIconImgPage.this.getContext(), bmp.copy(Config.ARGB_8888, true), filterValue);
//				m_uiHandler.sendEmptyMessage(0);
//			}
//
//		};

		m_uiHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{

                if(msg.what == BeautyHandler.MSG_SAVE)
                {
                	//清除人脸识别数据
                	FaceDataV2.ResetData();
                    if (msg.obj instanceof Bitmap) {
                        mShowBmp = (Bitmap) msg.obj;
                    } else if (msg.obj instanceof String) {
                        try {
                            mShowBmp = BitmapFactory.decodeFile((String) msg.obj);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

					msg = null;
                    if(mShowBmp != null && !mShowBmp.isRecycled())
                    {
                        cqriv.setImage(mShowBmp);
                        bmp = null;
                    }
                    else
                    {
                        cqriv.setImage(bmp);
                    }
                }
				SetWaitUI(false,"");
			}
		};

        m_filterHanlder = new BeautyHandler(m_filterThread.getLooper(), mContext, m_uiHandler);
	}

	private ImageView backBtn;
	private ImageView submitBtn;

	private ClipView cqriv;
	private int ww = ShareData.PxToDpi_xhdpi(510);
	private int wh = ShareData.PxToDpi_xhdpi(510);
	private int mTopMargin = -1;
	protected WaitAnimDialog m_waitDlg;

	private void initView()
	{
		ww = ShareData.PxToDpi_xhdpi(560);
		wh = ShareData.PxToDpi_xhdpi(560);
		mTopMargin = ShareData.PxToDpi_xhdpi(295);
		mHandler = new Handler();

		m_MainLayout = new RelativeLayout(getContext());
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		m_MainLayout.setLayoutParams(fl);
		this.addView(m_MainLayout);

		RelativeLayout.LayoutParams rParams;

		//71 23    91  45
		rParams = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(100), ShareData.PxToDpi_xhdpi(65));
		rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rParams.topMargin = ShareData.PxToDpi_xhdpi(35);
		backBtn = new ImageView(mContext);
		backBtn.setImageResource(R.drawable.beauty_login_clip_backlogo);
		backBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		backBtn.setOnClickListener(mOnClickListener);
		m_MainLayout.addView(backBtn, rParams);
		ImageUtils.AddSkin(getContext(),backBtn);
//		backBtn.setBackgroundColor(Color.GREEN);

		rParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		RelativeLayout rLayoutCenter = new RelativeLayout(getContext());
		m_MainLayout.addView(rLayoutCenter, rParams);
		rParams = new RelativeLayout.LayoutParams(ww + ShareData.PxToDpi_xhdpi(10), wh + ShareData.PxToDpi_xhdpi(10));
		rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rParams.topMargin = mTopMargin;
		RelativeLayout midLayout = new RelativeLayout(mContext);
		rLayoutCenter.addView(midLayout, rParams);

		rParams = new RelativeLayout.LayoutParams(ww, wh);
		rParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		cqriv = new ClipView(mContext);
		midLayout.addView(cqriv, rParams);


		rParams = new RelativeLayout.LayoutParams(ww,wh);
		rParams.addRule(RelativeLayout.CENTER_IN_PARENT);

		ImageView border = new ImageView(getContext());
		border.setImageResource(R.drawable.beauty_login_head_edit_border);
		midLayout.addView(border,rParams);

		rParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		rParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rParams.bottomMargin = ShareData.PxToDpi_xhdpi(92);
		submitBtn = new ImageView(mContext);
		submitBtn.setImageResource(R.drawable.beauty_login_ok_btn_normal);
		submitBtn.setOnClickListener(mOnClickListener);
		m_MainLayout.addView(submitBtn, rParams);
		ImageUtils.AddSkin(getContext(),submitBtn);

		m_waitDlg = new WaitAnimDialog((Activity)getContext());

		if(imgPath != null && !imgPath.trim().equals(""))
		{
			setImg(imgPath);
		}
	}

	protected void setImg(String imgPath)
	{
		RotationImg2 img = Utils.Path2ImgObj(imgPath);
		Bitmap temp = cn.poco.imagecore.Utils.DecodeImage(getContext(), img.m_img, img.m_degree, -1, -1, -1);
		bmp = MakeBmpV2.CreateBitmapV2(temp, img.m_degree, img.m_flip, -1, ShareData.m_screenWidth, ShareData.m_screenHeight, Config.ARGB_8888);
		if(temp != null)
		{
			temp.recycle();
			temp = null;
		}
		if(bmp != null)
		{
			cqriv.setImage(bmp);
		}
	}

	public void setFilterValue(int value)
	{
		this.filterValue = value;
//		if(filterValue != 0)
//		{
//			m_filterHanlder.sendEmptyMessage(0);
//		}

        if(m_filterHanlder != null)
        {
			SetWaitUI(true,"");
            Message msg = m_filterHanlder.obtainMessage();
            msg.what = BeautyHandler.MSG_SAVE;
            msg.obj = getOutMsg();
            m_filterHanlder.sendMessage(msg);
        }
	}

	private void SetWaitUI(boolean flag, String title)
	{
		if(flag)
		{
			if(m_waitDlg != null)
			{
				m_waitDlg.show();
			}
		}
		else
		{
			if(m_waitDlg != null)
			{
				m_waitDlg.hide();
			}
		}
	}

    public BeautyHandler.SaveMsg getOutMsg()
    {
		FilterRes res = null;
		if(filterValue != 0)
		{
			res = FilterResMgr2.getInstance().GetRes(filterValue);
		}

		if (mFilterBeautyParams == null)
		{
			// 构造默认
			mFilterBeautyParams = new FilterBeautyParams();
			mFilterBeautyParams.getCamera(getContext());
		}

		BeautyHandler.SaveMsg saveMsg = new BeautyHandler.SaveMsg();
		saveMsg.mImgs = bmp != null && !bmp.isRecycled() ? bmp.copy(Config.ARGB_8888, true) : null;
		saveMsg.mFilterUri = filterValue;
		saveMsg.mFilterAlpha = res != null ? res.m_filterAlpha : 100;
        saveMsg.isBlur = false;
        saveMsg.isDark = false;
		saveMsg.hasWaterMark = false;
		saveMsg.hasDateMark = false;
		saveMsg.mFilterRes = res;
		saveMsg.needDetectFace = true;//重新检测人脸

		saveMsg.saveToFile = false;
		saveMsg.isShare = false;
		saveMsg.mFilterBeautyParams = mFilterBeautyParams;
		saveMsg.mAdjustSize = (int) mFilterBeautyParams.getSkinBeautySize();
        return saveMsg;
    }

	protected void setNumAndVerityNum(String phoneNum, String verityNum, String zoneNum)
	{
		this.phoneNum = phoneNum;
		this.verityCode = verityNum;
		this.zoneNum = zoneNum;
	}

	protected void setUserId(String id, String token)
	{
		this.userId = id;
		this.m_accessTocken = token;
	}

	private void recycleBitmap(Bitmap b)
	{
		if(b != null && !b.isRecycled())
		{
			b.recycle();
		}
	}

	private OnClickListener mOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{

			if(v == backBtn)
			{
				if(mSite != null)
				{
					mSite.onBackToLastPage(getContext());
				}
			}
			else if(v == submitBtn)
			{
				if(isUploading)
				{
					Toast.makeText(mContext, getContext().getResources().getString(R.string.editheadiconpage_uploadtips), Toast.LENGTH_SHORT).show();
				}
				else
				{
					if(LoginOtherUtil.isNetConnected(mContext))
					{
						Bitmap temp = cqriv.getClipBmp();
						File file = new File(UserMgr.HEAD_TEMP_PATH);
						if(file.exists())
						{
							file.delete();
						}
						FileOutputStream out = null;
						try
						{
							out = new FileOutputStream(file);
							Bitmap m_thumb = MakeBmp.CreateBitmap(temp, 2048, 2048, -1, 0, Config.ARGB_8888);
							Bitmap.CompressFormat format = CompressFormat.PNG;
							if(imgPath != null && cn.poco.imagecore.ImageUtils.CheckIfJpg(imgPath) != 0) format = CompressFormat.JPEG;
							m_thumb.compress(format, 100, out);
						}
						catch(FileNotFoundException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						finally
						{
							if(out != null)
							{
								try
								{
									out.close();
								}
								catch(IOException e)
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						uploadHeadBmp(mode);
					}
					else
					{
						Toast.makeText(mContext, getContext().getResources().getString(R.string.editheadiconpage_nonetwork), Toast.LENGTH_SHORT).show();
					}

				}
			}

		}
	};

	@Override
	public void onBack()
	{
		mSite.onBack(getContext());
	}

	protected void uploadHeadBmp(final int style)
	{
		mProgress = new ProgressDialog(getContext());
		mProgress.setMessage(getContext().getResources().getString(R.string.editheadiconpage_uploading));
		mProgress.setCancelable(false);
		mProgress.show();
		isUploading = true;
		if(style == REGISTER && userId == null && m_accessTocken == null)
		{
//			new Thread(new Runnable()
//			{
//				@Override
//				public void run()
//				{
//					final LoginInfo info = LoginUtils.register(zoneNum, phoneNum, verityCode, AppInterface.GetInstance(getContext()));
//					mHandler.post(new Runnable()
//					{
//						@Override
//						public void run()
//						{
//							if(info != null)
//							{
//								if(info.mCode == 0)
//								{
//									userId = info.mUserId;
//									m_accessTocken = info.mAccessToken;
//									loginInfo = info;
//									uploadBmpReal();
//								}
//								else
//								{
//									if(mProgress != null)
//									{
//										mProgress.dismiss();
//										mProgress = null;
//									}
//									LoginOtherUtil.showToastVeritical(getContext(),getContext().getResources().getString(R.string.editheadiconpage_uploadfail));
//									isUploading = false;
//								}
//							}
//							else
//							{
//								if(mProgress != null)
//								{
//									mProgress.dismiss();
//									mProgress = null;
//								}
//								LoginOtherUtil.showToastVeritical(getContext(), getContext().getResources().getString(R.string.editheadiconpage_uploadfail2));
//								isUploading = false;
//							}
//						}
//					});
//				}
//			}).start();

			LoginUtils2.register(zoneNum, phoneNum, verityCode, new HttpResponseCallback()
			{
				@Override
				public void response(Object object)
				{
					if(object == null)
					{
						if(mProgress != null)
						{
							mProgress.dismiss();
							mProgress = null;
						}
						LoginOtherUtil.showToastVeritical(getContext(), getContext().getResources().getString(R.string.editheadiconpage_uploadfail2));
						isUploading = false;
						return;
					}
					LoginInfo info = (LoginInfo)object;
					if(info.mCode == 0)
					{
						userId = info.mUserId;
						m_accessTocken = info.mAccessToken;
						loginInfo = info;
						uploadBmpReal();
					}
					else
					{
						if(mProgress != null)
						{
							mProgress.dismiss();
							mProgress = null;
						}
						LoginOtherUtil.showToastVeritical(getContext(),getContext().getResources().getString(R.string.editheadiconpage_uploadfail));
						isUploading = false;
					}
				}
			});
		}
		else
		{
			uploadBmpReal();
		}
	}


	public void uploadBmpReal()
	{
		if(userId != null && m_accessTocken != null)
		{
//			new Thread(new Runnable()
//			{
//				@Override
//				public void run()
//				{
//					AliyunHeadUpload upload = new AliyunHeadUpload();
//					final String m_headUrl = upload.uploadHeadThumb(getContext(), userId, m_accessTocken, UserMgr.HEAD_TEMP_PATH, AppInterface.GetInstance(getContext()));
//					if(m_headUrl != null && m_headUrl.length() > 0)
//					{
//						final UserInfo userInfo = LoginUtils.getUserInfo(userId, m_accessTocken, AppInterface.GetInstance(mContext));
//						if(userInfo != null)
//						{
//							userInfo.mUserIcon = m_headUrl;
//						}
//						final BaseInfo resultInfo = LoginUtils.updateUserInfo(userId, m_accessTocken, userInfo, AppInterface.GetInstance(getContext()));
//						if(resultInfo != null)
//						{
//							mHandler.post(new Runnable()
//							{
//								@Override
//								public void run()
//								{
//									if(mProgress != null)
//									{
//										mProgress.dismiss();
//										mProgress = null;
//									}
//									isUploading = false;
//									if(resultInfo.mCode == 0)
//									{
//										LoginOtherUtil.showToastVeritical(getContext(), getContext().getResources().getString(R.string.editheadiconpage_uploadsuccess));
//										UserMgr.MoveFile(UserMgr.HEAD_TEMP_PATH, UserMgr.HEAD_PATH);
//										isUploading = false;
//										HashMap<String, Object> mParams = new HashMap<>();
//										mParams.put("id", userId);
//										mParams.put("headUrl", m_headUrl);
//										mParams.put("info", loginInfo);
//										mSite.upLoadSuccess(mParams,getContext());
//									}
//									else
//									{
//										LoginOtherUtil.showToastVeritical(getContext(), getContext().getResources().getString(R.string.editheadiconpage_uploadfailed));
//									}
//								}
//							});
//						}
//						else
//						{
//							mHandler.post(new Runnable()
//							{
//								@Override
//								public void run()
//								{
//									if(mProgress != null)
//									{
//										mProgress.dismiss();
//										mProgress = null;
//									}
//									LoginOtherUtil.showToastVeritical(getContext(), getContext().getResources().getString(R.string.editheadiconpage_uploadfailed));
//									isUploading = false;
//								}
//							});
//						}
//					}
//					else
//					{
//						mHandler.post(new Runnable()
//						{
//							@Override
//							public void run()
//							{
//								if(mProgress != null)
//								{
//									mProgress.dismiss();
//									mProgress = null;
//								}
//								LoginOtherUtil.showToastVeritical(getContext(),getContext().getResources().getString(R.string.editheadiconpage_uploadfailed));
//								isUploading = false;
//							}
//						});
//					}
//				}
//			}).start();
			LoginUtils2.uploadHeadThumb(userId, m_accessTocken, UserMgr.HEAD_TEMP_PATH, new HttpResponseCallback()
			{
				@Override
				public void response(Object object)
				{
					if(object == null)
					{
						if(mProgress != null)
						{
							mProgress.dismiss();
							mProgress = null;
						}
						LoginOtherUtil.showToastVeritical(getContext(), getContext().getResources().getString(R.string.editheadiconpage_uploadfailed));
						isUploading = false;
						return;
					}
					final String m_headUrl = (String)object;
					LoginUtils2.getUserInfo(userId, m_accessTocken, new HttpResponseCallback()
					{
						@Override
						public void response(Object object)
						{
							if(object == null)
							{
								if(mProgress != null)
								{
									mProgress.dismiss();
									mProgress = null;
								}
								LoginOtherUtil.showToastVeritical(getContext(), getContext().getResources().getString(R.string.editheadiconpage_uploadfailed));
								isUploading = false;
								return;
							}
							UserInfo userInfo = (UserInfo)object;
							userInfo.mUserIcon = m_headUrl;
							LoginUtils2.updateUserInfo(userId, m_accessTocken, userInfo, new HttpResponseCallback()
							{
								@Override
								public void response(Object object)
								{
									if(object == null)
									{
										if(mProgress != null)
										{
											mProgress.dismiss();
											mProgress = null;
										}
										LoginOtherUtil.showToastVeritical(getContext(), getContext().getResources().getString(R.string.editheadiconpage_uploadfailed));
										isUploading = false;
										return;
									}
									BaseInfo resultInfo = (BaseInfo)object;
									if(mProgress != null)
									{
										mProgress.dismiss();
										mProgress = null;
									}
									isUploading = false;
									if(resultInfo.mCode == 0)
									{
										LoginOtherUtil.showToastVeritical(getContext(), getContext().getResources().getString(R.string.editheadiconpage_uploadsuccess));
										UserMgr.MoveFile(UserMgr.HEAD_TEMP_PATH, UserMgr.HEAD_PATH);
										isUploading = false;
										HashMap<String, Object> mParams = new HashMap<>();
										mParams.put("id", userId);
										mParams.put("headUrl", m_headUrl);
										mParams.put("info", loginInfo);
										mSite.upLoadSuccess(mParams,getContext());
									}
									else
									{
										if(resultInfo.mMsg != null && resultInfo.mMsg.length() > 0) LoginOtherUtil.showToastVeritical(getContext(), resultInfo.mMsg);
										else LoginOtherUtil.showToastVeritical(getContext(), getContext().getResources().getString(R.string.editheadiconpage_uploadfailed));
									}
								}
							});
						}
					});
				}
			});
		}
		else
		{
			if(mProgress != null)
			{
				mProgress.dismiss();
				mProgress = null;
			}
			isUploading = false;
		}
	}


	@Override
	public void onClose()
	{
		this.setBackgroundDrawable(null);
		if(mProgress != null)
		{
			mProgress.dismiss();
			mProgress = null;
		}
		recycleBitmap(mShowBmp);
//		recycleBitmap(bmp);
		recycleBitmap(bgBitmap);
		if(m_waitDlg != null)
		{
			m_waitDlg.dismiss();
			m_waitDlg = null;
		}

		TongJiUtils.onPageEnd(getContext(), R.string.上传头像);
	}

	@Override
	public void onPause() {
		super.onPause();
		TongJiUtils.onPagePause(getContext(), R.string.上传头像);
	}

	@Override
	public void onResume() {
		super.onResume();
		TongJiUtils.onPageResume(getContext(), R.string.上传头像);
	}

	/*interface RegisterLisener
	{
		public void register(LoginInfo info);
	}*/
}
