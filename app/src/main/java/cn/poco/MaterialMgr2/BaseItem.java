package cn.poco.MaterialMgr2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.poco.advanced.ImageUtils;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * 素材中心除了主题和管理之外的ListItem
 */
public class BaseItem extends LinearLayout
{
	protected BaseItemInfo m_data;

	private UnScrollGridView m_list;
	private GridAdapter m_adapter;

	private FrameLayout m_topFr;
	private TextView m_itemName;
	private LinearLayout m_downloadBtn;
	private TextView m_downloadText;
	private RoundProgressBar m_progressBar;
	private ImageView m_checkBox;
	private ImageView m_dragBtn;
	private ImageView m_unlockIcon;

	private int m_leftMargin;
	private int m_itemSize;
	private int m_itemHSpace;
	private int m_itemVSpace;
	private int m_listColumnCount = 4;
	private int m_parentWidth = 0;
	private int m_topHeight = 0;
	private int m_listBottomPadding;
	private boolean m_canDrag = false;
	private boolean m_showLock;
	private boolean m_canItemClick = false;
	private boolean m_canClickDownload = true;
	private boolean m_showCheckBox = false;
	private AlertDialog m_downloadDlg;

	protected OnBaseItemCallback m_cb;
	public BaseItem(Context context, int parentWidth, int m_topHeight)
	{
		super(context);

		ShareData.InitData(getContext());
		m_leftMargin = (int)(28 / 720f * ShareData.m_screenWidth);
		m_itemSize = (int)(140 / 720f * ShareData.m_screenWidth);
		m_itemHSpace = (int)(20 / 720f * ShareData.m_screenWidth);
		m_itemVSpace = ShareData.PxToDpi_xhdpi(20);
		m_parentWidth = parentWidth;
		this.m_topHeight = m_topHeight;
		m_listColumnCount = (m_parentWidth + m_itemHSpace - m_leftMargin * 2) / (m_itemSize + m_itemHSpace);
		m_listColumnCount = Math.max(4, m_listColumnCount);
		m_listBottomPadding = ShareData.PxToDpi_xhdpi(10);

		InitUI();
	}

	public void setOnBaseItemCallback(OnBaseItemCallback cb)
	{
		m_cb = cb;
	}

	public void SetData(BaseItemInfo info)
	{
		if(null == info)
			return;
		m_data = info;
		m_itemName.setText(info.m_name);
		setChecked(m_data.m_isChecked);
		setDownloadBtnState(info.m_state, info.m_progress);
		if(info.m_state == BaseItemInfo.LOADING)
		{
			if(m_cb != null)
			{
				m_cb.OnDownload(BaseItem.this, m_data, false);
			}
		}
		//加解锁icon
		if(info.m_lock && m_showLock)
		{
			m_unlockIcon.setVisibility(View.VISIBLE);
		}
		else
		{
			m_unlockIcon.setVisibility(View.GONE);
		}
		setGrigViewDatas(info.isAllShow, false);
	}

	public void showCheckBox(boolean show)
	{
		m_showCheckBox = show;
		if(show)
		{
			m_downloadBtn.setVisibility(View.GONE);
			m_checkBox.setVisibility(View.VISIBLE);
		}
		else
		{
			m_downloadBtn.setVisibility(View.VISIBLE);
			m_checkBox.setVisibility(View.GONE);
		}
	}

	public void canDrag(boolean can)
	{
		m_canDrag = can;
	}

	public void showLock(boolean show)
	{
		m_showLock = show;
	}

	public void canClickItem(boolean show)
	{
		m_canItemClick = show;
	}

	public void canClickDownload(boolean can)
	{
		m_canClickDownload = can;
	}

	public void SetLoader(MyImageLoader loader)
	{
		m_adapter = new GridAdapter(getContext(), loader);
		m_adapter.setThumbSize(m_itemSize);
		m_list.setAdapter(m_adapter);
	}

	protected void InitUI()
	{
		LinearLayout.LayoutParams ll;
		FrameLayout.LayoutParams fl;
		this.setOrientation(LinearLayout.VERTICAL);
		this.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(m_canDrag)
				{
					onDrag();
				}
			}
		});

		m_topFr = new FrameLayout(getContext());
		ll = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, m_topHeight);
		m_topFr.setLayoutParams(ll);
		this.addView(m_topFr);
		{
			m_itemName = new TextView(getContext());
			m_itemName.setTextColor(0xd8000000);
			m_itemName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
			fl.leftMargin = m_leftMargin;
			m_itemName.setLayoutParams(fl);
			m_topFr.addView(m_itemName);

			m_downloadBtn = new LinearLayout(getContext());
			m_downloadBtn.setGravity(Gravity.CENTER);
			SetThemeResStyle(R.drawable.new_material4_need_download, m_downloadBtn);
//			m_downloadBtn.setPadding(ShareData.PxToDpi_xhdpi(18), ShareData.PxToDpi_xhdpi(6), ShareData.PxToDpi_xhdpi(18), ShareData.PxToDpi_xhdpi(6));
			fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(110), ShareData.PxToDpi_xhdpi(46));
			fl.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
			fl.rightMargin = m_leftMargin / 2 + ShareData.PxToDpi_xhdpi(14);
			m_downloadBtn.setLayoutParams(fl);
			m_topFr.addView(m_downloadBtn);
			m_downloadBtn.setOnTouchListener(m_clickListener);

			m_unlockIcon = new ImageView(getContext());
			m_unlockIcon.setVisibility(View.GONE);
			m_unlockIcon.setImageResource(R.drawable.download_more_lock_icon);
			ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.CENTER;
			ll.rightMargin = ShareData.PxToDpi_xhdpi(6);
			m_unlockIcon.setLayoutParams(ll);
			m_downloadBtn.addView(m_unlockIcon);

			m_downloadText = new TextView(getContext());
//			m_downloadText.setPadding(ShareData.PxToDpi_xhdpi(15), ShareData.PxToDpi_xhdpi(6), ShareData.PxToDpi_xhdpi(15), ShareData.PxToDpi_xhdpi(6));
			m_downloadText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
			m_downloadText.setTextColor(0xffffffff);
			m_downloadText.setGravity(Gravity.CENTER);
			TextPaint tp = m_downloadText.getPaint();
			tp.setFakeBoldText(true);
			ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.CENTER;
			m_downloadText.setLayoutParams(ll);
			m_downloadBtn.addView(m_downloadText);

			m_progressBar = new RoundProgressBar(getContext(), ShareData.PxToDpi_xhdpi(26), ShareData.PxToDpi_xhdpi(26));
			m_progressBar.setVisibility(View.GONE);
			m_progressBar.SetProgressBgColor(0x66FFFFFF);
			m_progressBar.setMax(100);
			ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(26), ShareData.PxToDpi_xhdpi(26));
			ll.gravity = Gravity.CENTER;
			m_progressBar.setLayoutParams(ll);
			m_downloadBtn.addView(m_progressBar);

			m_checkBox = new ImageView(getContext());
			m_checkBox.setScaleType(ImageView.ScaleType.CENTER);
			m_checkBox.setPadding(m_leftMargin, 0, 0, 0);
			m_checkBox.setImageResource(R.drawable.new_material4_checkbox_out);
			m_checkBox.setVisibility(View.GONE);
			fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
			m_checkBox.setLayoutParams(fl);
			m_topFr.addView(m_checkBox);
			m_checkBox.setOnTouchListener(m_clickListener);
		}

		m_list = new UnScrollGridView(getContext());
//		m_list.setBackgroundColor(Color.LTGRAY);
		m_list.setFocusable(true);
		m_list.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		m_list.setPadding(0, 0, 0, m_listBottomPadding);
		m_list.setNumColumns(m_listColumnCount);
		m_list.setColumnWidth(m_itemSize);
		m_list.setVerticalSpacing(m_itemVSpace);
		m_list.setHorizontalSpacing(m_itemHSpace);
		m_list.setCacheColorHint(0x00000000);
		m_list.setSelector(new ColorDrawable(Color.TRANSPARENT)); //将选中时的背景颜色设为透明
		m_list.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		ll = new LinearLayout.LayoutParams(m_parentWidth - m_leftMargin * 2, ViewGroup.LayoutParams.WRAP_CONTENT);
		ll.gravity = Gravity.CENTER;
		m_list.setLayoutParams(ll);
		this.addView(m_list);

		m_list.setOnAnimCompleteListener(new UnScrollGridView.OnAnimCompleteListener()
		{

			@Override
			public void onComplete()
			{
				if(m_canDrag && m_data != null && m_adapter != null)
				{
					int len = m_data.m_ress.size();
					if(len > m_listColumnCount)
					{
						m_dragBtn.setVisibility(View.VISIBLE);
						m_dragBtn.setImageResource(R.drawable.new_material4_arrow_below_btn);
					}
					else
					{
						m_dragBtn.setVisibility(View.GONE);
						m_adapter.SetDatas(m_data.m_ress);
					}
					m_adapter.SetIsAllShow(false);
					m_adapter.notifyDataSetChanged();
				}
			}
		});
		m_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				if(m_canDrag)
				{
					m_clickListener.onAnimationClick(m_dragBtn);
				}
				if(m_canItemClick)
				{
					if(m_data.m_state == BaseItemInfo.COMPLETE)
					{
						if(m_cb != null)
						{
							m_cb.OnUse(m_data, position);
						}
					}
					else if(m_data.m_state == BaseItemInfo.LOADING)
					{
						if(m_canClickDownload)
							Toast.makeText(getContext(), R.string.material_downloading_tip, Toast.LENGTH_LONG).show();
					}
					else if(m_canClickDownload)
					{
						if(m_data != null && m_data.m_themeRes != null)
						{
							if(m_data.m_lock && TagMgr.CheckTag(getContext(), Tags.THEME_UNLOCK + m_data.m_themeRes.m_id))
							{
								m_clickListener.onAnimationClick(m_downloadBtn);
								return;
							}
						}
						if(m_downloadDlg != null)
						{
							m_downloadDlg.show();
						}
					}
				}
			}
		});

		m_dragBtn = new ImageView(getContext());
		m_dragBtn.setVisibility(View.GONE);
		m_dragBtn.setScaleType(ImageView.ScaleType.CENTER);
		m_dragBtn.setImageResource(R.drawable.new_material4_arrow_below_btn);
		ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		ll.gravity = Gravity.CENTER_HORIZONTAL;
		m_dragBtn.setLayoutParams(ll);
		this.addView(m_dragBtn);
		m_dragBtn.setOnTouchListener(m_clickListener);

		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		TextView text = new TextView(getContext());
		text.setText(R.string.material_download_this);
		text.setTextColor(0xff000000);
		text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		text.setGravity(Gravity.CENTER_HORIZONTAL);
		text.setPadding(0, ShareData.PxToDpi_xhdpi(20), 0, ShareData.PxToDpi_xhdpi(20));
		builder.setView(text);
		builder.setPositiveButton(R.string.material_download, new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if(m_downloadDlg != null)
				{
					m_downloadDlg.dismiss();
				}
				m_clickListener.onAnimationClick(m_downloadBtn);
			}
		});
		builder.setNegativeButton(R.string.material_cancel, new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if(m_downloadDlg != null)
				{
					m_downloadDlg.dismiss();
				}
			}
		});
		m_downloadDlg = builder.create();
	}

	protected OnAnimationClickListener m_clickListener = new OnAnimationClickListener()
	{
		@Override
		public void onAnimationClick(View v)
		{
			if(m_canDrag)
			{
				if(v == m_dragBtn || v == BaseItem.this)
				{
					onDrag();
				}
			}
			if(v == m_downloadBtn)
			{
				if(m_data != null && m_data.m_state != BaseItemInfo.LOADING)
				{
					if(m_data.m_state == BaseItemInfo.COMPLETE && m_cb != null)
					{
						m_cb.OnUse(m_data, 0);
					}
					else if(m_cb != null)
					{
						m_cb.OnDownload(BaseItem.this, m_data, true);
					}
				}
			}
			if(v == m_checkBox)
			{
				if(m_data != null)
				{
					m_data.m_isChecked = !m_data.m_isChecked;
					setChecked(m_data.m_isChecked);
					if(m_cb != null)
					{
						m_cb.OnCheck(m_data);
					}
				}
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
	};

	private void setChecked(boolean checked)
	{
		if(!checked)
		{
			m_checkBox.setImageResource(R.drawable.new_material4_checkbox_out);
		}
		else
		{
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.new_material4_checkbox_over_bg);
			bmp = ImageUtils.AddSkin(getContext(), bmp);
			int width = bmp.getWidth();
			int height = bmp.getHeight();
			Bitmap out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			Bitmap bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.new_material4_checkbox_over);
			int width1 = bmp1.getWidth();
			int height1 = bmp1.getHeight();
			Canvas canvas = new Canvas(out);
			canvas.drawBitmap(bmp, new Matrix(), null);
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
			Matrix matrix = new Matrix();
			matrix.postTranslate((width - width1) / 2f, (height - height1) / 2f);
			canvas.drawBitmap(bmp1, matrix, null);
			m_checkBox.setImageBitmap(out);
		}
	}

	private void onDrag()
	{
		if(m_data == null)
			return;
		if(m_data.isAllShow)
		{
			m_data.isAllShow = false;
			setGrigViewDatas(m_data.isAllShow, true);
		}
		else
		{
			m_data.isAllShow = true;
			setGrigViewDatas(m_data.isAllShow, true);
		}
	}

	public void setDownloadBtnState(int state, int progress)
	{
		if(m_showCheckBox == true)
		{
			m_checkBox.setVisibility(View.VISIBLE);
			return;
		}
		if(state == BaseItemInfo.PREPARE)
		{
			m_progressBar.setVisibility(View.GONE);
			m_downloadText.setVisibility(VISIBLE);
			SetThemeResStyle(R.drawable.new_material4_need_download, m_downloadBtn);
			m_downloadText.setText(R.string.material_download);
		}
		else if(state == BaseItemInfo.LOADING)
		{
			m_progressBar.setVisibility(View.VISIBLE);
			m_downloadText.setVisibility(GONE);
			m_downloadBtn.setBackgroundResource(R.drawable.new_material4_downloading_complete);
			m_progressBar.setProgress(progress);
		}
		else if(state == BaseItemInfo.COMPLETE)
		{
			m_progressBar.setVisibility(View.GONE);
			m_downloadText.setVisibility(VISIBLE);
			m_downloadBtn.setBackgroundResource(R.drawable.new_material4_downloading_complete);
			m_downloadText.setText(R.string.material_use);
		}
		else if(state == BaseItemInfo.CONTINUE)
		{
			m_progressBar.setVisibility(View.GONE);
			m_downloadText.setVisibility(VISIBLE);
			SetThemeResStyle(R.drawable.new_material4_need_download, m_downloadBtn);
			m_downloadText.setText(R.string.material_download_continue);
		}
	}

	protected void SetThemeResStyle(int res, View view)
	{
		if(view != null && res > 0)
		{
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), res);
			bmp = ImageUtils.AddSkin(getContext(), bmp);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				view.setBackground(new BitmapDrawable(getResources(), bmp));
			} else {
				//noinspection deprecation
				view.setBackgroundDrawable(new BitmapDrawable(getResources(), bmp));
			}
		}
	}

	/**
	 * 设置GrigView显示的数据以及高度
	 * @param hasAnim	展开收起的时候是否做动画
	 * @param isAllShow	true 展开   false 收起
	 */
	private void setGrigViewDatas(boolean isAllShow, boolean hasAnim)
	{
		if(m_data != null && m_data.m_ress != null)
		{
			m_adapter.SetDatas(m_data.m_ress);
			m_adapter.SetIsAllShow(isAllShow);
			if(isAllShow)
			{
				if(m_canDrag)
				{
					int len = m_data.m_ress.size();
					if(len > m_listColumnCount)
					{
						m_dragBtn.setVisibility(View.VISIBLE);
					}
					else
					{
						m_dragBtn.setVisibility(View.GONE);
					}
				}
				else
				{
					m_dragBtn.setVisibility(View.GONE);
				}
				m_dragBtn.setImageResource(R.drawable.new_material4_arrow_up_btn);
				m_list.setRefreshable(false);
			}
			else
			{
				if(hasAnim)
				{
					m_adapter.SetIsAllShow(true);
				}
				m_list.setRefreshable(true);
			}
			setGridViewHeight(isAllShow, hasAnim);
			m_adapter.notifyDataSetChanged();
		}
	}

	public void setGridViewHeight(boolean isShow, boolean hasAnim)
	{
		if(m_data != null && m_data.m_ids != null && m_data.m_ids.length > 0)
		{
			if(isShow)
			{
				int len = m_data.m_ids.length;
				int colum;
				if(len % m_listColumnCount == 0)
				{
					colum = len / m_listColumnCount;
				}
				else
				{
					colum = len / m_listColumnCount + 1;
				}
				int gridHeight = m_itemSize * colum + m_itemVSpace * (colum - 1) + m_listBottomPadding;
				m_list.setHeight(gridHeight, hasAnim);
			}
			else
			{
				int gridHeight = m_itemSize + m_listBottomPadding;
				m_list.setHeight(gridHeight, hasAnim);
			}
			m_adapter.notifyDataSetChanged();
		}
	}

	public void releaseMem()
	{
		if(m_list != null)
		{
			m_list.clear();
			m_list.setAdapter(null);
			m_list = null;
		}
		if(m_adapter != null)
		{
			m_adapter.ClearAll();
			m_adapter = null;
		}
		if(m_downloadDlg != null)
		{
			m_downloadDlg.dismiss();
			m_downloadDlg = null;
		}
		m_cb = null;
	}

	public static interface OnBaseItemCallback
	{
		public void OnDownload(View view, BaseItemInfo info, boolean clickDownloadBtn);

		public void OnUse(BaseItemInfo info, int index);

		public void OnCheck(BaseItemInfo info);
	}
}
