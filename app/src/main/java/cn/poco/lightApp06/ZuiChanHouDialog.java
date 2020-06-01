//package cn.poco.lightApp06;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.os.Bundle;
//import android.text.InputFilter;
//import android.text.InputType;
//import android.util.TypedValue;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.Toast;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//
//import cn.poco.business.ActSignUpDialog;
//import cn.poco.business.ActSignUpView;
//import cn.poco.tianutils.ShareData;
//import my.beautyCamera.R;
//
///**
// * Created by pocouser on 2017/5/8.
// */
//
//public class ZuiChanHouDialog extends Dialog
//{
//	private EditText m_name;
//	private EditText m_phone;
//	private View m_submit;
//	private View m_ignore;
//	private View m_exit;
//	private ActSignUpDialog.OkListener m_listener;
//	private String m_channel_value;
//
//	public ZuiChanHouDialog(Context context, int themeResId)
//	{
//		super(context, themeResId);
//	}
//
//	public void setOkListener(ActSignUpDialog.OkListener listener)
//	{
//		m_listener = listener;
//	}
//
//	public void setChannelValue(String channelValue)
//	{
//		m_channel_value = channelValue;
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState)
//	{
//		super.onCreate(savedInstanceState);
//
//		FrameLayout mainFrame = new FrameLayout(getContext());
//		mainFrame.setBackgroundResource(R.drawable.light_app06_ad71_dialog_bg);
//		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(750), ShareData.PxToDpi_xhdpi(800));
//		fl.gravity = Gravity.CENTER;
//		setContentView(mainFrame, fl);
//		{
//			m_exit = new View(getContext());
////			m_exit.setBackgroundColor(0x55ff0000);
//			fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(64), ShareData.PxToDpi_xhdpi(64));
//			fl.gravity = Gravity.LEFT | Gravity.TOP;
//			fl.leftMargin = ShareData.PxToDpi_xhdpi(609);
//			fl.topMargin = ShareData.PxToDpi_xhdpi(44);
//			mainFrame.addView(m_exit, fl);
//			m_exit.setOnClickListener(new View.OnClickListener()
//			{
//				@Override
//				public void onClick(View v)
//				{
//					dismiss();
//				}
//			});
//
//			m_name = new EditText(getContext());
//			m_name.setBackgroundDrawable(null);
////			m_name.setBackgroundColor(0x55ff0000);
//			m_name.setSingleLine();
//			m_name.setTextColor(0xff000000);
//			m_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
//			m_name.setGravity(Gravity.CENTER_VERTICAL);
//			m_name.setPadding(ShareData.PxToDpi_xhdpi(12), 0, ShareData.PxToDpi_xhdpi(12), 0);
//			fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(334), ShareData.PxToDpi_xhdpi(64));
//			fl.gravity = Gravity.LEFT | Gravity.TOP;
//			fl.leftMargin = ShareData.PxToDpi_xhdpi(242);
//			fl.topMargin = ShareData.PxToDpi_xhdpi(347);
//			mainFrame.addView(m_name, fl);
//
//			m_phone = new EditText(getContext());
//			m_phone.setBackgroundDrawable(null);
////			m_phone.setBackgroundColor(0x55ff0000);
//			m_phone.setSingleLine();
//			m_phone.setTextColor(0xff000000);
//			m_phone.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
//			m_phone.setGravity(Gravity.CENTER_VERTICAL);
//			m_phone.setPadding(ShareData.PxToDpi_xhdpi(12), 0, ShareData.PxToDpi_xhdpi(12), 0);
//			m_phone.setInputType(InputType.TYPE_CLASS_NUMBER);
//			m_phone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
//			fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(334), ShareData.PxToDpi_xhdpi(64));
//			fl.gravity = Gravity.LEFT | Gravity.TOP;
//			fl.leftMargin = ShareData.PxToDpi_xhdpi(242);
//			fl.topMargin = ShareData.PxToDpi_xhdpi(439);
//			mainFrame.addView(m_phone, fl);
//
//			m_submit = new View(getContext());
////			m_submit.setBackgroundColor(0x55ff0000);
//			fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(204), ShareData.PxToDpi_xhdpi(46));
//			fl.gravity = Gravity.LEFT | Gravity.TOP;
//			fl.leftMargin = ShareData.PxToDpi_xhdpi(162);
//			fl.topMargin = ShareData.PxToDpi_xhdpi(638);
//			mainFrame.addView(m_submit, fl);
//			m_submit.setOnClickListener(new View.OnClickListener()
//			{
//				@Override
//				public void onClick(View v)
//				{
//					if(m_listener != null)
//					{
//						String name_value = m_name.getText().toString();
//						if(name_value == null || name_value.trim().length() <= 0)
//						{
//							Toast.makeText(getContext(), "姓名未填写", Toast.LENGTH_LONG).show();
//							return;
//						}
//						String phone_value = m_phone.getText().toString();
//						if(phone_value == null || phone_value.trim().length() <= 0)
//						{
//							Toast.makeText(getContext(), "手机未填写", Toast.LENGTH_LONG).show();
//							return;
//						}
//						if(phone_value.trim().length() < 11)
//						{
//							Toast.makeText(getContext(), "手机号不合法", Toast.LENGTH_LONG).show();
//							return;
//						}
//						String postVal = "";
//						try
//						{
//							name_value = URLEncoder.encode(name_value, "UTF-8");
//							phone_value = URLEncoder.encode(phone_value, "UTF-8");
//						}
//						catch(UnsupportedEncodingException e){}
//						postVal += "|name:" + name_value;
//						postVal += "|tel:" + phone_value;
//						ActSignUpView.postInfo(m_channel_value, postVal);
//						m_listener.onOk(postVal);
//					}
//					dismiss();
//				}
//			});
//
//			m_ignore = new View(getContext());
////			m_ignore.setBackgroundColor(0x55ff0000);
//			fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(204), ShareData.PxToDpi_xhdpi(46));
//			fl.gravity = Gravity.LEFT | Gravity.TOP;
//			fl.leftMargin = ShareData.PxToDpi_xhdpi(387);
//			fl.topMargin = ShareData.PxToDpi_xhdpi(638);
//			mainFrame.addView(m_ignore, fl);
//			m_ignore.setOnClickListener(new View.OnClickListener()
//			{
//				@Override
//				public void onClick(View v)
//				{
//					m_listener.onOk(null);
//					dismiss();
//				}
//			});
//		}
//	}
//
//	@Override
//	public void dismiss()
//	{
//		m_listener = null;
//		super.dismiss();
//	}
//}
