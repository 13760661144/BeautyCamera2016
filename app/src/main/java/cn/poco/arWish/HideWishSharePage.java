package cn.poco.arWish;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adnonstop.admasterlibs.AbsUploadFile;
import com.adnonstop.admasterlibs.data.UploadData;
import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.mm.sdk.modelbase.BaseResp;

import java.io.File;
import java.util.HashMap;

import cn.poco.arWish.site.HideWishSharePageSite;
import cn.poco.blogcore.QzoneBlog2;
import cn.poco.blogcore.SinaBlog;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.holder.ObjHandlerHolder;
import cn.poco.image.filter;
import cn.poco.share.ShareFrame;
import cn.poco.share.ShareTools;
import cn.poco.share.SimpleSharePage;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.AppInterface;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.NetState;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.Utils;
import my.beautyCamera.R;

/**
 * Created by pocouser on 2018/1/22.
 */

public class HideWishSharePage extends IPage
{
	private static final int TAG = R.string.ar祝福_送祝福_完成送祝福;

	private HideWishSharePageSite m_site;

	private ImageView m_backBtn;
	private ImageView m_imageView;
	private ImageView m_finish;
	private UploadRoundProgressView m_progress;
	private SimpleSharePage mSharePage;
	private LinearLayout mIconQQ;
	private LinearLayout mIconSina;
	private LinearLayout mIconQzone;
	private LinearLayout mIconWeiXin;
	private LinearLayout mIconWXFriends;

	private String m_videoPath;
	private String m_imagePath;
	private Bitmap m_image;
//	private String m_locationAddress;
	private Bitmap m_lastPageScreenshots;
//	private Bitmap mScreenshots;
	private ShareTools m_shareTools;
	private String m_savePath;
	private boolean m_uploading = false;
//	private String m_latitude;
//	private String m_longitude;
	private String m_wishWord;
	private ObjHandlerHolder<AbsUploadFile.Callback> m_uploadCallback;

	public HideWishSharePage(Context context, BaseSite site)
	{
		super(context, site);
		if(site != null) m_site = (HideWishSharePageSite)site;
		TongJiUtils.onPageStart(getContext(), TAG);
	}

	@Override
	public void SetData(HashMap<String, Object> params)
	{
		if(params.containsKey("screenshots"))
		{
			Bitmap lastPageScreenshots = (Bitmap)params.get("screenshots");
			if(lastPageScreenshots != null && !lastPageScreenshots.isRecycled()) m_lastPageScreenshots = filter.fakeGlassBeauty(lastPageScreenshots, 0x19000000);
		}
		if(params.containsKey("image"))
		{
			m_imagePath = (String) params.get("image");
			if(m_imagePath == null || m_imagePath.length() <= 0 || !new File(m_imagePath).exists())
			{
				Toast.makeText(getContext(), getResources().getString(R.string.arwish_hidewish_share_no_image), Toast.LENGTH_LONG).show();
				if(m_site != null) m_site.onBack(getContext());
				return;
			}
		}
		if(params.containsKey("video"))
		{
			m_videoPath = (String)params.get("video");
			if(m_videoPath == null || m_videoPath.length() <= 0 || !new File(m_videoPath).exists())
			{
				Toast.makeText(getContext(), getResources().getString(R.string.arwish_hidewish_share_no_video), Toast.LENGTH_LONG).show();
				if(m_site != null) m_site.onBack(getContext());
				return;
			}
		}
//		if(params.containsKey("latitude"))
//		{
//			m_latitude = (String)params.get("latitude");
//		}
//		if(params.containsKey("longitude"))
//		{
//			m_longitude = (String)params.get("longitude");
//		}
//		if(params.containsKey("address"))
//		{
//			m_locationAddress = (String) params.get("address");
//		}
		if(!NetState.IsConnectNet(getContext()))
		{
			Toast.makeText(getContext(), getResources().getString(R.string.arwish_hidewish_share_upload_fail), Toast.LENGTH_LONG).show();
			if(m_site != null) m_site.onBack(getContext());
			return;
		}
		init();
		showProgressPage();
		m_uploading = true;
		post(new Runnable()
		{
			@Override
			public void run()
			{
				getShareUrl();
				//测试UI
//				m_wishWord = "ABC123";
//				saveFile(MakeBmpV2.DecodeImage(getContext(), m_imagePath, 0, -1, -1, -1, Bitmap.Config.ARGB_8888));
			}
		});
	}

	private void init()
	{
		Bitmap bg = MakeBmp.CreateFixBitmap(MakeBmpV2.DecodeImage(getContext(), R.drawable.ar_bg, 0, -1, ShareData.m_screenWidth, ShareData.m_screenHeight, Bitmap.Config.ARGB_8888), ShareData.m_screenWidth, ShareData.m_screenHeight, MakeBmp.POS_START, 0, Bitmap.Config.ARGB_8888);
		if(bg != null && !bg.isRecycled()) setBackgroundDrawable(new BitmapDrawable(getResources(), bg));
		else setBackgroundResource(R.drawable.ar_bg);

		FrameLayout.LayoutParams fl;
		LinearLayout.LayoutParams ll;

		m_backBtn = new ImageView(getContext());
		m_backBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		m_backBtn.setImageResource(R.drawable.ar_top_bar_back_btn);
		fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		fl.topMargin = ShareData.PxToDpi_xhdpi(30);
		fl.leftMargin = ShareData.PxToDpi_xhdpi(30);
		addView(m_backBtn, fl);
		m_backBtn.setOnTouchListener(mBtnListener);

		ImageView hide_text = new ImageView(getContext());
		hide_text.setImageResource(R.drawable.arwish_share_title);
		fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
		fl.topMargin = ShareData.PxToDpi_xxhdpi(192);
		addView(hide_text, fl);

		FrameLayout shadowFrame = new FrameLayout(getContext());
		shadowFrame.setBackgroundResource(R.drawable.arwish_share_bg_shadow);
		fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		fl.topMargin = ShareData.PxToDpi_xxhdpi(380);
		addView(shadowFrame, fl);
		{
			m_imageView = new ImageView(getContext());
			m_imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xxhdpi(448), ShareData.PxToDpi_xxhdpi(796));
			fl.gravity = Gravity.CENTER;
//			fl.topMargin = ShareData.PxToDpi_xxhdpi(420);
			shadowFrame.addView(m_imageView, fl);
			m_imageView.setOnClickListener(mClickListener);
		}

		LinearLayout m_weiboFrame = new LinearLayout(getContext());
		m_weiboFrame.setOrientation(LinearLayout.HORIZONTAL);
		fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		fl.topMargin = ShareData.PxToDpi_xhdpi(914);
		addView(m_weiboFrame, fl);
		{
			int margin = (int)(((float)ShareData.m_screenWidth - ShareData.PxToDpi_xhdpi(460)) / 6);

			mIconWXFriends = new LinearLayout(getContext());
			mIconWXFriends.setOrientation(LinearLayout.VERTICAL);
			ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.LEFT | Gravity.TOP;
			m_weiboFrame.addView(mIconWXFriends, ll);
			mIconWXFriends.setOnTouchListener(mBtnListener);
			{
				ImageView icon = new ImageView(getContext());
				icon.setImageResource(R.drawable.arwish_hide_share_wechat_circle);
				ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
				mIconWXFriends.addView(icon, ll);

				TextView name = new TextView(getContext());
				name.setTextColor(Color.WHITE);
				name.setText(getResources().getString(R.string.friends_circle));
				name.setAlpha(0.86f);
				name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
				ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
				ll.topMargin = ShareData.PxToDpi_xhdpi(10);
				mIconWXFriends.addView(name, ll);
			}

			mIconWeiXin = new LinearLayout(getContext());
			mIconWeiXin.setOrientation(LinearLayout.VERTICAL);
			ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.LEFT | Gravity.TOP;
			ll.leftMargin = margin;
			m_weiboFrame.addView(mIconWeiXin, ll);
			mIconWeiXin.setOnTouchListener(mBtnListener);
			{
				ImageView icon = new ImageView(getContext());
				icon.setImageResource(R.drawable.arwish_hide_share_wechat);
				ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
				mIconWeiXin.addView(icon, ll);

				TextView name = new TextView(getContext());
				name.setTextColor(Color.WHITE);
				name.setText(getResources().getString(R.string.wechat_friends));
				name.setAlpha(0.86f);
				name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
				ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
				ll.topMargin = ShareData.PxToDpi_xhdpi(10);
				mIconWeiXin.addView(name, ll);
			}

			mIconSina = new LinearLayout(getContext());
			mIconSina.setOrientation(LinearLayout.VERTICAL);
			ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.LEFT | Gravity.TOP;
			ll.leftMargin = margin;
			m_weiboFrame.addView(mIconSina, ll);
			mIconSina.setOnTouchListener(mBtnListener);
			{
				ImageView icon = new ImageView(getContext());
				icon.setImageResource(R.drawable.arwish_hide_share_sina);
				ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
				mIconSina.addView(icon, ll);

				TextView name = new TextView(getContext());
				name.setTextColor(Color.WHITE);
				name.setText(getResources().getString(R.string.sina_weibo));
				name.setAlpha(0.86f);
				name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
				ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
				ll.topMargin = ShareData.PxToDpi_xhdpi(10);
				mIconSina.addView(name, ll);
			}

			mIconQzone = new LinearLayout(getContext());
			mIconQzone.setOrientation(LinearLayout.VERTICAL);
			ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.LEFT | Gravity.TOP;
			ll.leftMargin = margin;
			m_weiboFrame.addView(mIconQzone, ll);
			mIconQzone.setOnTouchListener(mBtnListener);
			{
				ImageView icon = new ImageView(getContext());
				icon.setImageResource(R.drawable.arwish_hide_share_qzone);
				ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
				mIconQzone.addView(icon, ll);

				TextView name = new TextView(getContext());
				name.setTextColor(Color.WHITE);
				name.setText(getResources().getString(R.string.QQZone));
				name.setAlpha(0.86f);
				name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
				ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
				ll.topMargin = ShareData.PxToDpi_xhdpi(10);
				mIconQzone.addView(name, ll);
			}

			mIconQQ = new LinearLayout(getContext());
			mIconQQ.setOrientation(LinearLayout.VERTICAL);
			ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.LEFT | Gravity.TOP;
			ll.leftMargin = margin;
			m_weiboFrame.addView(mIconQQ, ll);
			mIconQQ.setOnTouchListener(mBtnListener);
			{
				ImageView icon = new ImageView(getContext());
				icon.setImageResource(R.drawable.arwish_hide_share_qq);
				ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
				mIconQQ.addView(icon, ll);

				TextView name = new TextView(getContext());
				name.setTextColor(Color.WHITE);
				name.setText(getResources().getString(R.string.QQ));
				name.setAlpha(0.86f);
				name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
				ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
				ll.topMargin = ShareData.PxToDpi_xhdpi(10);
				mIconQQ.addView(name, ll);
			}
		}

		m_finish = new ImageView(getContext());
		m_finish.setImageResource(R.drawable.ar_hide_save_logo);
		fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		fl.topMargin = ShareData.PxToDpi_xhdpi(1112);
		addView(m_finish, fl);
		m_finish.setOnTouchListener(mBtnListener);
	}

	protected OnAnimationClickListener mBtnListener = new OnAnimationClickListener()
	{
		@Override
		public void onAnimationClick(View v)
		{
			if(m_uploading) return;
			if(m_shareTools == null) m_shareTools = new ShareTools(getContext());
			if(v == m_backBtn || v == m_finish)
			{
				MyBeautyStat.onClickByRes(R.string.AR祝福_送祝福_完成送祝福_返回);
				if(m_site != null) m_site.backToActivityPage(getContext());
			}
			else if(v == mIconWXFriends)
			{
				m_shareTools.sendToWeiXin(m_savePath, false, new ShareTools.SendCompletedListener()
				{
					@Override
					public void getResult(Object result)
					{
						switch((int)result)
						{
							case BaseResp.ErrCode.ERR_OK:
								MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.朋友圈, TAG);
								Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
								break;

							case BaseResp.ErrCode.ERR_USER_CANCEL:
								Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
								break;

							case BaseResp.ErrCode.ERR_AUTH_DENIED:
								Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
								break;
						}
					}
				});
			}
			else if(v == mIconWeiXin)
			{
				m_shareTools.sendToWeiXin(m_savePath, true, new ShareTools.SendCompletedListener()
				{
					@Override
					public void getResult(Object result)
					{
						switch((int)result)
						{
							case BaseResp.ErrCode.ERR_OK:
								MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.微信好友, TAG);
								Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
								break;

							case BaseResp.ErrCode.ERR_USER_CANCEL:
								Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
								break;

							case BaseResp.ErrCode.ERR_AUTH_DENIED:
								Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
								break;
						}
					}
				});
			}
			else if(v == mIconSina)
			{
				m_shareTools.sendToSinaBySDK("#美人相机#https://www.adnonstop.com/beauty_camera/wap/index.php", m_savePath, new ShareTools.SendCompletedListener()
				{
					@Override
					public void getResult(Object result)
					{
						switch((int)result)
						{
							case WBConstants.ErrorCode.ERR_OK:
								MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.微博, TAG);
								Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
								break;

							case WBConstants.ErrorCode.ERR_CANCEL:
								Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
								break;

							case WBConstants.ErrorCode.ERR_FAIL:
							case SinaBlog.NO_RESPONSE:
								Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
								break;
						}
					}
				});
			}
			else if(v == mIconQQ)
			{
				m_shareTools.sendToQQ(m_savePath, new ShareTools.SendCompletedListener()
				{
					@Override
					public void getResult(Object result)
					{
						switch((int)result)
						{
							case QzoneBlog2.SEND_SUCCESS:
								MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ好友, TAG);
								Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
								break;

							case QzoneBlog2.SEND_CANCEL:
								Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
								break;

							case QzoneBlog2.SEND_FAIL:
								Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
								break;
						}
					}
				});
			}
			else if(v == mIconQzone)
			{
				m_shareTools.sendToQzone(m_savePath, new ShareTools.SendCompletedListener()
				{
					@Override
					public void getResult(Object result)
					{
						switch((int)result)
						{
							case QzoneBlog2.SEND_SUCCESS:
								MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ空间, TAG);
								Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
								break;

							case QzoneBlog2.SEND_CANCEL:
								Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
								break;

							case QzoneBlog2.SEND_FAIL:
								Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
								break;
						}
					}
				});
			}
		}
	};

	private View.OnClickListener mClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			if(m_uploading) return;
			if(view == m_imageView)
			{
				if(m_site != null && m_savePath != null) m_site.OnPreview(getContext(), m_savePath);
			}
		}
	};

	/**
	 * 制作缩略图;
	 *
	 * @param org 原始图片
	 */
	private Bitmap createThumb(Bitmap org)
	{
		if(org == null || org.isRecycled()) return null;

		int thumb_w = 264;
		int thumb_h = 264;
		Bitmap thumb = MakeBmp.CreateFixBitmap(org, thumb_w, thumb_h, MakeBmp.POS_CENTER, 0, Bitmap.Config.ARGB_8888);
		if(thumb == null || thumb.isRecycled()) return null;
		return ShareFrame.makeCircle(thumb);
	}

//	private Bitmap getScreenshots()
//	{
//		if(mScreenshots == null || mScreenshots.isRecycled()) mScreenshots = filter.fakeGlassBeauty(SharePage.screenCapture(getContext(), ShareData.m_screenWidth, ShareData.m_screenHeight), 0xdcf5f5f5);
//		return mScreenshots;
//	}

	private void showProgressPage()
	{
		if(m_progress != null) return;
		m_progress = new UploadRoundProgressView(getContext());
		m_progress.setBackground(m_lastPageScreenshots);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.START | Gravity.TOP;
		addView(m_progress, fl);
	}

	private void setProgress(int progress)
	{
		if(m_progress != null) m_progress.setProgress(progress);
	}

	private void closeProgressPage()
	{
		if(m_progress == null) return;
		removeView(m_progress);
		m_progress = null;
		System.gc();
	}

//	private void showSharePage()
//	{
//		closeSharePage();
//		mSharePage = new SimpleSharePage(getContext());
//		mSharePage.changeTitle(getResources().getString(R.string.arwish_hidewish_share_title));
//		mSharePage.needAnime();
//		mSharePage.setScreenshots(getScreenshots(), ShareData.PxToDpi_xhdpi(390));
//		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//		fl.gravity = Gravity.START | Gravity.TOP;
//		addView(mSharePage, fl);
//		ArrayList<SimpleSharePage.ShareType> shareTypeArrayList = new ArrayList<>();
//		shareTypeArrayList.add(SimpleSharePage.ShareType.wechat_friends_circle);
//		shareTypeArrayList.add(SimpleSharePage.ShareType.wechat);
//		shareTypeArrayList.add(SimpleSharePage.ShareType.sina);
//		shareTypeArrayList.add(SimpleSharePage.ShareType.qzone);
//		shareTypeArrayList.add(SimpleSharePage.ShareType.qq);
//		mSharePage.init(shareTypeArrayList, new SimpleSharePage.SimpleSharePageClickListener()
//		{
//			@Override
//			public void onClick(SimpleSharePage.ShareType type)
//			{
//				if(type == null)
//				{
//					closeSharePage();
//					return;
//				}
//				saveFile();
//				if(m_shareTools == null) m_shareTools = new ShareTools(getContext());
//				switch(type)
//				{
//					case wechat:
//						m_shareTools.sendToWeiXin(m_savePath, true, new ShareTools.SendCompletedListener()
//						{
//							@Override
//							public void getResult(Object result)
//							{
//								switch((int)result)
//								{
//									case BaseResp.ErrCode.ERR_OK:
//										Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
//										break;
//
//									case BaseResp.ErrCode.ERR_USER_CANCEL:
//										Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
//										break;
//
//									case BaseResp.ErrCode.ERR_AUTH_DENIED:
//										Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
//										break;
//								}
//							}
//						});
//						break;
//
//					case wechat_friends_circle:
//						m_shareTools.sendToWeiXin(m_savePath, false, new ShareTools.SendCompletedListener()
//						{
//							@Override
//							public void getResult(Object result)
//							{
//								switch((int)result)
//								{
//									case BaseResp.ErrCode.ERR_OK:
//										Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
//										break;
//
//									case BaseResp.ErrCode.ERR_USER_CANCEL:
//										Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
//										break;
//
//									case BaseResp.ErrCode.ERR_AUTH_DENIED:
//										Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
//										break;
//								}
//							}
//						});
//						break;
//
//					case sina:
//						m_shareTools.sendToSinaBySDK(null, m_savePath, new ShareTools.SendCompletedListener()
//						{
//							@Override
//							public void getResult(Object result)
//							{
//								switch((int)result)
//								{
//									case WBConstants.ErrorCode.ERR_OK:
//										Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
//										break;
//
//									case WBConstants.ErrorCode.ERR_CANCEL:
//										Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
//										break;
//
//									case WBConstants.ErrorCode.ERR_FAIL:
//									case SinaBlog.NO_RESPONSE:
//										Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
//										break;
//								}
//							}
//						});
//						break;
//
//					case qq:
//						m_shareTools.sendToQQ(m_savePath, new ShareTools.SendCompletedListener()
//						{
//							@Override
//							public void getResult(Object result)
//							{
//								switch((int)result)
//								{
//									case QzoneBlog2.SEND_SUCCESS:
//										Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
//										break;
//
//									case QzoneBlog2.SEND_CANCEL:
//										Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
//										break;
//
//									case QzoneBlog2.SEND_FAIL:
//										Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
//										break;
//								}
//							}
//						});
//						break;
//
//					case qzone:
//						m_shareTools.sendToQzone(m_savePath, new ShareTools.SendCompletedListener()
//						{
//							@Override
//							public void getResult(Object result)
//							{
//								switch((int)result)
//								{
//									case QzoneBlog2.SEND_SUCCESS:
//										Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
//										break;
//
//									case QzoneBlog2.SEND_CANCEL:
//										Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
//										break;
//
//									case QzoneBlog2.SEND_FAIL:
//										Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
//										break;
//								}
//							}
//						});
//						break;
//				}
//			}
//		});
//	}

	private void saveFile(Bitmap tips_image)
	{
		if(m_savePath != null && m_savePath.length() > 0 && new File(m_savePath).exists()) return;
		if(m_wishWord == null || m_wishWord.length() <= 0) return;
		if(tips_image == null || tips_image.isRecycled()) return;

		Paint codePaint = new Paint();
		codePaint.setAntiAlias(true);
		codePaint.setColor(0xffd52820);
		codePaint.setTextSize(56);
		Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Whitney-Bold.otf");
		if(typeface != null) codePaint.setTypeface(typeface);
		Rect rect = new Rect();
		codePaint.getTextBounds(m_wishWord,0,m_wishWord.length(),rect);
		Paint.FontMetrics fontMetrics = codePaint.getFontMetrics();
		int w = (int) Math.floor(codePaint.measureText(m_wishWord));
		int h = (int) Math.floor(fontMetrics.descent - fontMetrics.ascent);
		float y = -fontMetrics.ascent;
		Bitmap codeBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(codeBmp);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		canvas.drawText(m_wishWord, 0, y, codePaint);
		Bitmap drawble = MakeBmpV2.DecodeImage(getContext(), R.drawable.arwish_share_bg, 0, -1, -1, -1, Bitmap.Config.ARGB_8888);
		if(drawble == null || drawble.isRecycled()) return;
		m_image = drawble.copy(Bitmap.Config.ARGB_8888, true);
		canvas = new Canvas(m_image);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		canvas.drawBitmap(codeBmp, (m_image.getWidth() - codeBmp.getWidth()) / 2, 914 - (h - rect.height())/2f, null);
		codeBmp.recycle();
		Bitmap thumb = createThumb(tips_image);
		if(thumb == null || thumb.isRecycled()) return;
		Canvas thumb_canvas = new Canvas(thumb);
		thumb_canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		Paint circle_paint = new Paint();
		circle_paint.setAntiAlias(true);
		circle_paint.setColor(0xffd52820);
		circle_paint.setStyle(Paint.Style.STROKE);
		circle_paint.setStrokeWidth(2);
		int center = thumb.getWidth() / 2;
		thumb_canvas.drawCircle(center, center, center, circle_paint);
		canvas.drawBitmap(thumb, (m_image.getWidth() - thumb.getWidth()) / 2, 390, null);
		thumb.recycle();
		System.gc();
		m_savePath = Utils.SaveImg(getContext(), m_image, Utils.MakeSavePhotoPath(getContext(), (float) m_image.getWidth() / m_image.getHeight()), 100, true);
		m_imageView.setImageBitmap(m_image);
	}

//	private void closeSharePage()
//	{
//		if(mSharePage != null)
//		{
//			removeView(mSharePage);
//			mSharePage.close();
//			mSharePage = null;
//			System.gc();
//		}
//	}

	private void uploadFail()
	{
		m_uploading = false;
		closeProgressPage();
		Toast.makeText(getContext(), getResources().getString(R.string.arwish_hidewish_share_upload_fail), Toast.LENGTH_LONG).show();
		if(m_site != null) m_site.onBack(getContext());
	}

	private void getShareUrl()
	{
		final UploadData data = new UploadData();
		data.mChannelValue = "ar_201802";
		data.mImgPath = m_imagePath;
		data.mVideoPath = m_videoPath;

		m_uploadCallback = new ObjHandlerHolder<AbsUploadFile.Callback>(new AbsUploadFile.Callback()
		{
			@Override
			public void onProgress(int progress)
			{
				setProgress(progress);
			}

			@Override
			public void onSuccess(final String word)
			{
				post(new Runnable()
				{
					@Override
					public void run()
					{
						m_wishWord = word;
						if(m_wishWord == null || m_wishWord.length() <= 0)
						{
							uploadFail();
							return;
						}
						Bitmap clue_bmp = MakeBmpV2.DecodeImage(getContext(), m_imagePath, 0, -1, -1, -1, Bitmap.Config.ARGB_8888);
						Utils.SaveImg(getContext(), clue_bmp, Utils.MakeSavePhotoPath(getContext(), (float) clue_bmp.getWidth() / clue_bmp.getHeight()), 100, true);
						m_uploading = false;
						closeProgressPage();
						saveFile(clue_bmp);
						Toast.makeText(getContext(), R.string.arwish_hidewish_share_save, Toast.LENGTH_LONG).show();
					}
				});
			}

			@Override
			public void onFailure()
			{
				post(new Runnable()
				{
					@Override
					public void run()
					{
						uploadFail();
					}
				});
			}
		});

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				new UploadArWishFile(getContext(), data, AppInterface.GetInstance(getContext()), m_uploadCallback);
			}
		}).start();
	}

	@Override
	public void onBack()
	{
		if(m_uploading) return;
		if(m_site != null) m_site.backToActivityPage(getContext());
	}

	@Override
	public void onClose()
	{
//		if(mScreenshots != null && !mScreenshots.isRecycled())
//		{
//			mScreenshots.recycle();
//			mScreenshots = null;
//		}
		if(m_image != null && !m_image.isRecycled())
		{
			m_image.recycle();
			m_image = null;
		}
		if(m_lastPageScreenshots != null && !m_lastPageScreenshots.isRecycled())
		{
			m_lastPageScreenshots.recycle();
			m_lastPageScreenshots = null;
		}
		if(m_uploadCallback != null)
		{
			m_uploadCallback.Clear();
			m_uploadCallback = null;
		}
		m_shareTools = null;
		System.gc();
		TongJiUtils.onPageEnd(getContext(), TAG);
		super.onClose();
	}

	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(m_shareTools != null) m_shareTools.onActivityResult(requestCode, resultCode, data);
		return super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause()
	{
		TongJiUtils.onPagePause(getContext(), TAG);
		super.onPause();
	}

	@Override
	public void onResume()
	{
		TongJiUtils.onPageResume(getContext(), TAG);
		super.onResume();
	}
}
