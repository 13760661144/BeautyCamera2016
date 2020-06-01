package cn.poco.advanced;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.statistics.TongJi2;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.NetCore2;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.MyNetCore;
import my.beautyCamera.R;

public class BlessEditLayout extends RelativeLayout
{
	private static final int ID_BTN_CLOSE = R.id.bless_edit_btn_close;
	private static final int ID_EDT_INPUT = R.id.bless_edit_btn_input;
	private static final int ID_BTN_BLESS = R.id.bless_edit_btn_bless;
	private static final int ID_BTN_OK = R.id.bless_edit_btn_ok;
	private static final int ID_BTN_PEOPLE = R.id.bless_edit_btn_people;
	private static final int ID_LAYOUT_TOP_BAR = R.id.bless_edit_layout_top_bar;
	private static final int ID_LAYOUT_BOOTOM_BAR = R.id.bless_edit_layout_bottom_bar;
	private static final int ID_IMG_SPLIT_TOP = R.id.bless_edit_img_split_top;
	private static final int ID_EDITTEXT_VIEW = R.id.bless_edit_text_view;
	private static final int ID_IMG_SPLIT_BOTTOM = R.id.bless_edit_img_split_bottom;
	private ImageView mBtnOk;//确定
	private ImageView mBtnClose;//关闭
	private ImageView mBtnBless;//热门祝福语
	private ImageView mBtnPeople;//头像;
	private EditText mEdtInput;//输入文本
	private EditText mEdtInputNick;//输入昵称
	public RelativeLayout mMidMainRLayout;//主布局
	public BlessEditDialog mDialog = null;//dialog对象本身
	public ProgressDialog mProgressDialog = null;
	public static int MAX_TEXT_LENGTH = 140;//设置最大的字数;
	private Context mContext;

	public BlessEditLayout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		mContext = context;
		initialize(context);
	}

	public BlessEditLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
		initialize(context);
	}

	public BlessEditLayout(Context context)
	{
		super(context);
		mContext = context;
		initialize(context);

	}

	/**
	 * 构造函数时,初始化布局.
	 *
	 * @param context
	 */
	protected void initialize(Context context)
	{
		TongJi2.AddCountByRes(getContext(), R.integer.美化_卡片_写贺卡文字界面);
		//设置背景mImgBlessBk
		LayoutParams relativeParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		ImageView mImgBlessBk = new ImageView(context);
		mImgBlessBk.setBackgroundResource(R.drawable.dialog_blessedit_bg);
		this.addView(mImgBlessBk, relativeParams);

		//昵称,祝福语;
		relativeParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		relativeParams.topMargin = ShareData.PxToDpi_hdpi(20);
		RelativeLayout topBtnLayout = new RelativeLayout(context);
		topBtnLayout.setId(ID_LAYOUT_TOP_BAR);
		topBtnLayout.setGravity(Gravity.CENTER_VERTICAL);
		this.addView(topBtnLayout, relativeParams);

		//祝福语;
		relativeParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		relativeParams.rightMargin = ShareData.PxToDpi_hdpi(20);
		mBtnBless = new ImageView(getContext());
		mBtnBless.setImageDrawable(CommonUtils.CreateXHDpiBtnSelector((Activity)getContext(), R.drawable.dialog_blessedit_btn_bless, R.drawable.dialog_blessedit_btn_bless_over));
		mBtnBless.setId(ID_BTN_BLESS);
		mBtnBless.setScaleType(ScaleType.CENTER_INSIDE);
		mBtnBless.setOnClickListener(mOnClickListener);
		topBtnLayout.addView(mBtnBless, relativeParams);

		//昵称;
		relativeParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		relativeParams.addRule(RelativeLayout.ALIGN_TOP, ID_BTN_BLESS);
		relativeParams.addRule(RelativeLayout.LEFT_OF, ID_BTN_BLESS);
		relativeParams.rightMargin = ShareData.PxToDpi_hdpi(18);
		relativeParams.leftMargin = ShareData.PxToDpi_hdpi(32);
		RelativeLayout nickLayout = new RelativeLayout(context);
		nickLayout.setPadding(0, 0, ShareData.PxToDpi_hdpi(8), 0);
		topBtnLayout.addView(nickLayout, relativeParams);

		relativeParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		relativeParams.topMargin = ShareData.PxToDpi_hdpi(10);
		mBtnPeople = new ImageView(getContext());
		mBtnPeople.setImageDrawable(CommonUtils.CreateXHDpiBtnSelector((Activity)getContext(), R.drawable.dialog_blessedit_btn_people, R.drawable.dialog_blessedit_btn_people));
		mBtnPeople.setId(ID_BTN_PEOPLE);
		nickLayout.addView(mBtnPeople, relativeParams);

		//输入昵称;
		final float inputSize = 14.0f;
		mEdtInputNick = new EditText(context);
		mEdtInputNick.setBackgroundDrawable(null);
		mEdtInputNick.setClickable(true);
		mEdtInputNick.setTextSize(inputSize - 1);
		mEdtInputNick.setGravity(Gravity.TOP | Gravity.LEFT);
		mEdtInputNick.setSingleLine(true);
		mEdtInputNick.setTextColor(0xff444649);
		mEdtInputNick.setHintTextColor(0xff444649);
		mEdtInputNick.setText(TagMgr.GetTagValue(getContext(), Tags.ADV_BLESS_CARD_SENDER));
		if(mEdtInputNick.getText().toString().length() == 0)
		{
			SettingInfo info = SettingInfoMgr.GetSettingInfo(getContext());
			String pocoNick = info.GetPocoNick();
			String sinaNick = info.GetSinaUserNick();
			String qqNick = info.GetQzoneUserName();
			if(sinaNick != null && sinaNick.trim().length() > 0)
			{
				mEdtInputNick.setText(sinaNick);
			}
			else if(qqNick != null && qqNick.trim().length() > 0)
			{
				mEdtInputNick.setText(qqNick);
			}
			else if(pocoNick != null && pocoNick.trim().length() > 0)
			{
				mEdtInputNick.setText(pocoNick);
			}
			else
			{
				mEdtInputNick.setHint(mDefaultText);
			}
		}
		mEdtInputNick.setClickable(true);
		mEdtInputNick.setOnFocusChangeListener(mFocusChangeListener);
		relativeParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		relativeParams.addRule(RelativeLayout.RIGHT_OF, ID_BTN_PEOPLE);
		nickLayout.addView(mEdtInputNick, relativeParams);
		mEdtInputNick.addTextChangedListener(mTextWatcherNick);

		relativeParams = new LayoutParams(LayoutParams.FILL_PARENT, 1);
		relativeParams.addRule(BELOW, ID_LAYOUT_TOP_BAR);
		relativeParams.leftMargin = ShareData.PxToDpi_hdpi(20);
		relativeParams.rightMargin = ShareData.PxToDpi_hdpi(20);
		relativeParams.topMargin = ShareData.PxToDpi_hdpi(5);
		ImageView line1 = new ImageView(context);
		line1.setId(ID_IMG_SPLIT_TOP);
		line1.setScaleType(ScaleType.FIT_XY);
		line1.setBackgroundColor(0xffDDDDDD);
		this.addView(line1, relativeParams);

		//编辑文字,mEdtInput
		relativeParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		relativeParams.addRule(RelativeLayout.BELOW, ID_IMG_SPLIT_TOP);
		relativeParams.addRule(RelativeLayout.ABOVE, ID_IMG_SPLIT_BOTTOM);
		relativeParams.leftMargin = ShareData.PxToDpi_hdpi(18);
		relativeParams.rightMargin = ShareData.PxToDpi_hdpi(18);
		mEdtInput = new EditText(context);
		mEdtInput.setId(ID_EDITTEXT_VIEW);
		mEdtInput.setBackgroundDrawable(null);
		mEdtInput.setTextColor(0xff444649);
		mEdtInput.setHintTextColor(0xffCCCCCC);
		mEdtInput.setTextSize(14.0f);
		mEdtInput.setHint("写下你的文字...");
		mEdtInput.setId(ID_EDT_INPUT);
		mEdtInput.setGravity(Gravity.TOP | Gravity.LEFT);
		this.addView(mEdtInput, relativeParams);
		mEdtInput.addTextChangedListener(mTextWatcherText);

		relativeParams = new LayoutParams(LayoutParams.FILL_PARENT, 1);
		relativeParams.addRule(ABOVE, ID_LAYOUT_BOOTOM_BAR);
		relativeParams.leftMargin = ShareData.PxToDpi_hdpi(20);
		relativeParams.rightMargin = ShareData.PxToDpi_hdpi(20);
		ImageView line2 = new ImageView(context);
		line2.setId(ID_IMG_SPLIT_BOTTOM);
		line2.setScaleType(ScaleType.FIT_XY);
		line2.setBackgroundColor(0xffDDDDDD);
		this.addView(line2, relativeParams);

		//底部控制按钮,确定,取消;
		relativeParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		relativeParams.leftMargin = ShareData.PxToDpi_hdpi(20);
		relativeParams.rightMargin = ShareData.PxToDpi_hdpi(20);
		relativeParams.topMargin = ShareData.PxToDpi_hdpi(20);
		relativeParams.bottomMargin = ShareData.PxToDpi_hdpi(20);
		LinearLayout btnLayout = new LinearLayout(context);
		btnLayout.setId(ID_LAYOUT_BOOTOM_BAR);
		this.addView(btnLayout, relativeParams);

		LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		linearParams.weight = 1;
		mBtnOk = new ImageView(getContext());
		mBtnOk.setImageDrawable(CommonUtils.CreateXHDpiBtnSelector((Activity)getContext(), R.drawable.dialog_blessedit_btn_ok, R.drawable.dialog_blessedit_btn_ok_over));
		mBtnOk.setScaleType(ScaleType.CENTER_INSIDE);
		mBtnOk.setId(ID_BTN_OK);
		mBtnOk.setOnClickListener(mOnClickListener);
		btnLayout.addView(mBtnOk, linearParams);

		linearParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		linearParams.weight = 0;
		linearParams.leftMargin = ShareData.PxToDpi_hdpi(20);
		mBtnClose = new ImageView(getContext());
		mBtnClose.setImageDrawable(CommonUtils.CreateXHDpiBtnSelector((Activity)getContext(), R.drawable.dialog_blessedit_btn_cacel, R.drawable.dialog_blessedit_btn_cacel_over));
		mBtnClose.setScaleType(ScaleType.CENTER_INSIDE);
		mBtnClose.setId(ID_BTN_CLOSE);
		mBtnClose.setOnClickListener(mOnClickListener);
		btnLayout.addView(mBtnClose, linearParams);


	}

	/**
	 * 昵称编辑框监听
	 */
	private TextWatcher mTextWatcherNick = new TextWatcher()
	{

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s)
		{
			// TODO Auto-generated method stub
			int strLen = getStrLenth(s.toString(), 16);
			if(strLen > 16)
			{
				String strMax = s.toString().substring(0, indexMax);
				mEdtInputNick.setText(strMax);
				mEdtInputNick.setSelection(mEdtInputNick.getText().length());
				Toast.makeText(mContext.getApplicationContext(), "昵称最大长度不得超过16个字符.", Toast.LENGTH_SHORT).show();
				return;
			}
		}
	};
	/**
	 * 输入编辑框监听
	 */
	private TextWatcher mTextWatcherText = new TextWatcher()
	{

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s)
		{
			// TODO Auto-generated method stub
			int strLen = getStrLenth(s.toString(), MAX_TEXT_LENGTH);
			if(strLen > MAX_TEXT_LENGTH)
			{
				String strMax = s.toString().substring(0, indexMax);
				mEdtInput.setText(strMax);
				mEdtInput.setSelection(mEdtInput.getText().length());
				return;
			}
//			mTxtTextCount.setText(String.valueOf((50 - strLen)/2));
		}
	};
	/**
	 * 所有View.OnClickListener事件
	 */
	private OnClickListener mOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			int viewId = v.getId();
			switch(viewId)
			{
				case ID_BTN_CLOSE://关闭
					if(mDialog != null)
					{
						mDialog.onClose();
					}
					break;
				case ID_BTN_OK://确定
					String text = mEdtInput.getText().toString().trim();
					if(text != null && text.trim().length() > 0)
					{
						String textNick = mEdtInputNick.getText().toString().trim();
						if(textNick.equals(mDefaultText) == true)
						{
							textNick = "";
						}
						if(textNick != null && textNick.trim().length() > 0)
						{
							TagMgr.SetTagValue(getContext(), Tags.ADV_BLESS_CARD_SENDER, textNick);
						}
						if(mDialog != null)
						{
							mDialog.onEditTextOk(text, textNick);
						}
					}
					else
					{
						AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
						dialog.setTitle("还没有贺卡文字");
						dialog.setMessage("请输入,或者看看\"热门祝福语\"里面有没有合适的?");
						dialog.setNegativeButton("取消", new DialogInterface.OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								// TODO Auto-generated method stub
								//打开软键盘
								showSoftKeyboard(mEdtInput);
								return;
							}
						});
						dialog.setPositiveButton("确定", new DialogInterface.OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								// TODO Auto-generated method stub
								showBlessTopics();
							}
						});
						dialog.show();
					}
					break;
				case ID_BTN_BLESS://祝福语
					TongJi2.AddCountByRes(getContext(), R.integer.美化_卡片_热门祝福语);
					showBlessTopics();
					break;

				default:
					break;
			}

		}
	};


	/**
	 * 显示祝福语弹出框
	 * isRun 记录当前是否有异步加载任务
	 */
	private boolean isRun = false;

	@SuppressWarnings("unchecked")
	private void showBlessTopics()
	{
		if(!isRun)
		{
			blessArray = sOldArray;
			if(blessArray == null || blessArray.size() == 0)
			{
				isRun = true;
				mProgressDialog = new ProgressDialog(mContext, R.style.dialog);
				mProgressDialog.setMessage("正在加载祝福语列表");
				mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
				{

					@Override
					public void onDismiss(DialogInterface dialog)
					{
						// TODO Auto-generated method stub
						isRun = false;
					}
				});
				mProgressDialog.show();
				asyncDataLoader();
			}
			else
			{
				showBlessDialog2(blessArray);
			}
		}
	}

	private static ArrayList<String> sOldArray = null;
	/**
	 * 异步更新祝福语列表
	 * blessArray 保存祝福语列表到内存
	 */
	private ArrayList<String> blessArray;
	Handler mHandler = null;

	private void asyncDataLoader()
	{
		mHandler = new Handler();
		try
		{
			new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					blessArray = new ArrayList<String>();
					blessArray = getAllTopics();
					mHandler.post(new Runnable()
					{

						@Override
						public void run()
						{
							// TODO Auto-generated method stub
							if(mProgressDialog != null)
							{
								mProgressDialog.dismiss();
								mProgressDialog = null;
							}
							if(blessArray.size() > 0)
							{
								sOldArray = blessArray;
								showBlessDialog2(blessArray);
							}
							else
							{
								if(mContext != null)
								{
									Toast.makeText(mContext.getApplicationContext(), "加载祝福语列表失败.", Toast.LENGTH_SHORT).show();
								}
							}
							isRun = false;
						}
					});
				}
			}).start();
		}
		catch(Exception e)
		{

		}
	}

	/**
	 * 弹出祝福语对话框,重写ArrayAdapter适配器
	 *
	 * @param blessArray
	 */
	protected void showBlessDialog(ArrayList<String> blessArray)
	{
		// TODO Auto-generated method stub
		final String[] itemName = new String[blessArray.size()];
		for(int i = 0; i < blessArray.size(); i++)
		{
			itemName[i] = blessArray.get(i).trim();
		}
		if(mContext == null) return;
		ArrayAdapter<String> atAdapter = new ArrayAdapter<String>(mContext, R.layout.textview, R.id.textview_tv, itemName);
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
		alertDialog.setTitle("热门祝福语");
		alertDialog.setAdapter(atAdapter, new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub
				mEdtInput.setText("");
				int index = mEdtInput.getSelectionEnd();
				String text = itemName[which];
				mEdtInput.getText().insert(index, text);
				TongJi2.AddCountByRes(getContext(), R.integer.美化_卡片_热门祝福语_某一条祝福语);
//			    //打开软键盘
//	            showSoftKeyboard(mEdtInput);
			}
		});
		alertDialog.setPositiveButton("取消", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub

			}
		});
		alertDialog.show();

	}


	/**
	 * 重写弹出框;
	 */


	/**
	 * 弹出祝福语对话框,重写ArrayAdapter适配器
	 *
	 * @param blessArray
	 */
	Dialog dialog = null;

	protected void showBlessDialog2(ArrayList<String> blessArray)
	{
		// TODO Auto-generated method stub
		if(mContext == null) return;
		BlessView view = new BlessView(getContext());
		view.initLayout(blessArray);

//	    FrameLayout outLayout=new FrameLayout(getContext());
//	    outLayout.setBackgroundColor(Color.YELLOW);
//	    FrameLayout.LayoutParams frameParams=new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
//	    FrameLayout innerLayout=new FrameLayout(getContext());
//	    outLayout.addView(innerLayout, frameParams);
//	    
//	    frameParams=new FrameLayout.LayoutParams(Utils.sScreenW,Utils.sScreenH);
//	    innerLayout.addView(view, frameParams);

		dialog = new Dialog(getContext(), R.style.waitDialog);

		FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		FrameLayout mContentPanel = new FrameLayout(getContext());
		mContentPanel.setLayoutParams(frameParams);

		frameParams = new FrameLayout.LayoutParams(ShareData.m_screenWidth, ShareData.m_screenHeight);
		view.setLayoutParams(frameParams);
		mContentPanel.addView(view);

		dialog.setContentView(mContentPanel);
		view.setOnBlessDialogClickListener(new OnBlessDialogClick()
		{

			@Override
			public void onClickCancel()
			{
				// TODO Auto-generated method stub
				if(dialog != null)
				{
					dialog.dismiss();
				}
			}

			@Override
			public void onClickBlessText(String blessStr)
			{
				// TODO Auto-generated method stub
				if(dialog != null)
				{
					dialog.dismiss();
				}
				mEdtInput.setText("");
				int index = mEdtInput.getSelectionEnd();
				String text = blessStr;
				mEdtInput.getText().insert(index, text);
				TongJi2.AddCountByRes(getContext(), R.integer.美化_卡片_热门祝福语_某一条祝福语);
			}
		});
//	    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//	    lp.copyFrom(dialog.getWindow().getAttributes());
//	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
//	    lp.height = WindowManager.LayoutParams.FILL_PARENT;
		dialog.show();
//	    dialog.getWindow().setAttributes(lp);

	}

	class BlessView extends RelativeLayout
	{
		private static final int ID_LAYOUT_TOPBAR = R.id.bless_edit_layout_top_bar2;
		private static final int ID_BTN_RETURN = R.id.bless_edit_btn_return;
		private ImageView mBtnReturn;
		private ListView mListViewTopic;
		private BaseAdapter mAdapter;
		private OnBlessDialogClick listener;

		public BlessView(Context context)
		{
			super(context);
		}

		public void setOnBlessDialogClickListener(OnBlessDialogClick listener)
		{
			this.listener = listener;
		}

		private void initLayout(final List<String> infos)
		{

			//布局背景;
			/*BitmapDrawable bmDrawable;
			bmDrawable = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.topoiclayout_page_bg));
			bmDrawable.setTileModeX(TileMode.REPEAT);
			this.setBackgroundDrawable(bmDrawable);
			this.setBackgroundResource(R.drawable.topoiclayout_page_bg);*/

			//topBarLayout;
			LayoutParams relativeParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			FrameLayout topBarlayout = new FrameLayout(getContext());
			/*bmDrawable = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.frame_topbar_bk));
			bmDrawable.setTileModeX(TileMode.REPEAT);
			topBarlayout.setBackgroundDrawable(bmDrawable);*/
			topBarlayout.setBackgroundColor(0xf4ffffff);
			this.addView(topBarlayout, relativeParams);
			topBarlayout.setId(ID_LAYOUT_TOPBAR);


			//topBarLayout:话题文字;
			FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			frameParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
			TextView mTxtInfo = new TextView(getContext());
			mTxtInfo.setTextColor(0xff8c8c8c);
			mTxtInfo.setTextSize(22.0f);
			mTxtInfo.setText("热门祝福语");
			topBarlayout.addView(mTxtInfo, frameParams);

			//topBarLayout:返回按钮;
			frameParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			frameParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
			mBtnReturn = new ImageView(getContext());
			mBtnReturn.setBackgroundResource(R.drawable.framework_back_btn);
			topBarlayout.addView(mBtnReturn, frameParams);
			mBtnReturn.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					if(listener != null)
					{
						listener.onClickCancel();
					}
				}
			});
			mBtnReturn.setId(ID_BTN_RETURN);

			relativeParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			relativeParams.addRule(RelativeLayout.BELOW, ID_LAYOUT_TOPBAR);
			mListViewTopic = new ListView(getContext());
			mListViewTopic.setCacheColorHint(0);
			mListViewTopic.setDivider(new ColorDrawable(0xfff4f4f0));
			mListViewTopic.setDividerHeight(1);
			mAdapter = new BlessAdapter(getContext(), infos);
			mListViewTopic.setAdapter(mAdapter);
			mListViewTopic.setOnItemClickListener(new AdapterView.OnItemClickListener()
			{

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{
					// TODO Auto-generated method stub
					if(listener != null)
					{
						listener.onClickBlessText(infos.get(position));
					}
				}
			});
			this.addView(mListViewTopic, relativeParams);
		}
	}

	//定义一个接口用来回调;
	public interface OnBlessDialogClick
	{
		void onClickCancel();

		void onClickBlessText(String blessStr);
	}

	public class BlessAdapter extends BaseAdapter
	{
		private List<String> mApps;
		private LayoutInflater inflater;

		public BlessAdapter(Context context, List<String> infos)
		{
			this.mApps = infos;
			inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return mApps.size();
		}

		@Override
		public Object getItem(int position)
		{
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position)
		{
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			// TODO Auto-generated method stub
			ViewHolder holder;
			if(convertView == null)
			{
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.textview, null);
				holder.name = (TextView)convertView.findViewById(R.id.textview_tv);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder)convertView.getTag();
			}
			holder.name.setText(mApps.get(position));
			holder.name.setTextColor(0xff5a5a5a);
			return convertView;
		}

		class ViewHolder
		{
			TextView name;
		}
	}


	/**
	 * 选择之后,打开软键盘
	 *
	 * @param editText
	 */
	protected void showSoftKeyboard(final EditText editText)
	{
		InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		if(imm != null)
		{
			imm.showSoftInput(editText, 0);
		}
	}


	/**
	 * 从网络下载祝福语列表
	 *
	 * @return
	 */
	private ArrayList<String> getAllTopics()
	{
		HttpURLConnection urlConnection = null;
		ArrayList<String> blessArray = new ArrayList<String>();
		String urlAddr = MyNetCore.GetPocoUrl(getContext(), "http://img-m.poco.cn/mypoco/mtmpfile/MobileAPI/Recommend/get_wish_list.php?ctype=beautycamera&out=xml");
		NetCore2 net = new NetCore2();
		NetCore2.NetMsg msg = net.HttpGet(urlAddr);
		if(msg != null && msg.m_stateCode == HttpURLConnection.HTTP_OK && msg.m_data != null)
		{
			try
			{
				XmlPullParser pullParser = Xml.newPullParser();
				ByteArrayInputStream inputStream = new ByteArrayInputStream(msg.m_data);
				pullParser.setInput(inputStream, "UTF-8");
				int evenType = pullParser.getEventType();
				String nodeName = null;
				while(evenType != XmlPullParser.END_DOCUMENT)
				{
					switch(evenType)
					{
						case XmlPullParser.START_TAG:
							nodeName = pullParser.getName();
							if(("item").equals(nodeName))
							{
								String topName = pullParser.nextText();
								blessArray.add(topName);
							}
							break;
						default:
							break;
					}
					evenType = pullParser.next();
				}
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
		return blessArray;
	}

	/**
	 * 设置输入文本.
	 *
	 * @param strEdtText
	 */
	public void setInputText(String strEdtText)
	{
		if(mEdtInput != null)
		{
			mEdtInput.setText(strEdtText);
			mEdtInput.setSelection(mEdtInput.getText().length());
		}
	}

	/**
	 * 计算字符串的长度,以字节计算.
	 *
	 * @param str 字符串.
	 * @return
	 */
	public int indexMax = 0;

	public int getStrLenth(String str, int maxNum)
	{
		int strLen = 0;
		indexMax = 0;
		if(str == null || str.trim().length() == 0) return strLen;
		int length = str.length();
		int hanz = 0;
		int zimu = 0;
		for(int loci = 0; loci < length; loci++)
		{
			if(str.substring(loci, loci + 1).matches("[\u4e00-\u9fa5]"))
			{
				hanz++;
			}
			else
			{
				zimu++;
			}
			strLen = (hanz * 2 + zimu);
			if(strLen > maxNum)
			{
				indexMax = loci;
				break;
			}

		}
		return strLen;
	}

	/**
	 * 设置昵称的FocusChange事件
	 */
	boolean fristTouch = false;
	boolean fristUnFocus = false;
	private String mDefaultText = "我的名字?";
	private OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener()
	{
		@Override
		public void onFocusChange(View v, boolean hasFocus)
		{
			String strText = mEdtInputNick.getText().toString();
			if(hasFocus == false)
			{
				if(TextUtils.isEmpty(strText) && fristUnFocus)
				{
					mEdtInputNick.setText(mDefaultText);
					fristUnFocus = true;
				}
			}
			else
			{
				Log.e("debug", "strText.equals(mDefaultText):" + strText.equals(mDefaultText));
				Log.e("debug", "fristTouch:" + fristTouch);
				if(strText.equals(mDefaultText) == true && !fristTouch)
				{
					mEdtInputNick.setText("");
					fristTouch = false;
				}
			}
		}
	};

	/**
	 * 非静态版本,置空所有监听事件,移除所有View.
	 */
	public void removeAllListener()
	{

		if(dialog != null)
		{
			dialog.dismiss();
		}
		mBtnBless.setOnClickListener(null);
		mBtnClose.setOnClickListener(null);
		mBtnOk.setOnClickListener(null);

		mEdtInput.clearFocus();
		mEdtInput.removeTextChangedListener(mTextWatcherText);
		mEdtInput.addTextChangedListener(null);
		mEdtInput.setOnClickListener(null);
		mEdtInput.setOnFocusChangeListener(null);

		mEdtInputNick.clearFocus();
		mEdtInputNick.removeTextChangedListener(mTextWatcherNick);
		mEdtInputNick.addTextChangedListener(null);
		mEdtInputNick.setOnClickListener(null);
		mEdtInputNick.setOnFocusChangeListener(null);
		mProgressDialog = null;
		this.removeAllViews();
		mContext = null;
	}
}