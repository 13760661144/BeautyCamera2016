package cn.poco.MaterialMgr2;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.ColorUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.poco.resource.GroupRes;
import cn.poco.resource.ResType;
import cn.poco.tianutils.ImageUtils;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

import static android.graphics.BitmapFactory.decodeFile;

/**
 * Created by admin on 2016/5/20.
 */
public class ManageItem extends RelativeLayout
{
	public ResType mTag;
	public ArrayList<GroupRes> mDatas;
	private ImageView mIcon;
	private Bitmap mIconBmp;
	private TextView mTxDescribe;
	protected TextView mNumber;
	protected ImageView mBtnArrow;
	public RelativeLayout mItemContainer;
    public int mTongJiId;

	public View mLine;

	public ManageItem(Context context)
	{
		super(context);
		initialize(context);
	}

	private void initialize(Context context)
	{
		int ID = 210;

		RelativeLayout.LayoutParams rl_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, ShareData.PxToDpi_xhdpi(130));
		mItemContainer = new RelativeLayout(context);
		mItemContainer.setBackgroundColor(0xffffffff);
		addView(mItemContainer, rl_lp);
		final int ID_CONTAINER = ID++;
		mItemContainer.setId(ID_CONTAINER);

		rl_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		rl_lp.addRule(RelativeLayout.CENTER_VERTICAL);
		rl_lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		rl_lp.leftMargin = ShareData.PxToDpi_xhdpi(40);
		mIcon = new ImageView(context);
		mIcon.setScaleType(ImageView.ScaleType.CENTER);
		mItemContainer.addView(mIcon, rl_lp);
		final int ID_ICON = ID++;
		mIcon.setId(ID_ICON);

		rl_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		rl_lp.addRule(RelativeLayout.CENTER_VERTICAL);
		rl_lp.leftMargin = ShareData.PxToDpi_xhdpi(23);
		rl_lp.addRule(RelativeLayout.RIGHT_OF, ID_ICON);
		mTxDescribe = new TextView(context);
		mTxDescribe.setTextColor(0xff333333);
		mTxDescribe.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		mItemContainer.addView(mTxDescribe, rl_lp);
		final int ID_DESCRIBE = ID++;
		mTxDescribe.setId(ID_DESCRIBE);

		//箭头
		rl_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		rl_lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rl_lp.addRule(RelativeLayout.CENTER_VERTICAL);
		rl_lp.rightMargin = ShareData.PxToDpi_xhdpi(28);
		mBtnArrow = new ImageView(context);
		mItemContainer.addView(mBtnArrow, rl_lp);
		mBtnArrow.setImageResource(R.drawable.new_material4_arrow_btn);
		final int ID_ARROW = ID++;
		mBtnArrow.setId(ID_ARROW);

		//数字容器
		/*rl_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		rl_lp.addRule(RelativeLayout.CENTER_VERTICAL);
		rl_lp.addRule(RelativeLayout.LEFT_OF, ID_ARROW);
		rl_lp.rightMargin = ShareData.PxToDpi_xhdpi(28);
		RelativeLayout numberHolder = new RelativeLayout(context);
//		numberHolder.setBackgroundResource(R.drawable.new_material_downloadable_num);
		mItemContainer.addView(numberHolder, rl_lp);*/
		//数字
		rl_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		rl_lp.addRule(RelativeLayout.CENTER_VERTICAL);
		rl_lp.addRule(RelativeLayout.LEFT_OF, ID_ARROW);
		rl_lp.rightMargin = ShareData.PxToDpi_xhdpi(28);
		mNumber = new TextView(context);
		mNumber.setTextColor(0xffb2b2b2);
		mNumber.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
		mNumber.setText("0");
		mNumber.setGravity(Gravity.CENTER);
		mItemContainer.addView(mNumber, rl_lp);

//		FrameLayout bottom = new FrameLayout(context);
//		bottom.setBackgroundColor(Color.WHITE);
//		rl_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 1);
//		rl_lp.addRule(RelativeLayout.BELOW, ID_CONTAINER);
//		addView(bottom, rl_lp);
//		{
//			View line = new View(context);
//			line.setBackgroundColor(0x15000000);
//			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//			bottom.addView(line, params);
//		}

		mLine = new View(getContext());
		mLine.setBackgroundColor(ColorUtils.compositeColors(0x15000000, 0xffffffff));
		rl_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 1);
		rl_lp.addRule(RelativeLayout.BELOW, ID_CONTAINER);
		addView(mLine, rl_lp);
	}

	public void setUI(ResType tag, String describe, Object res)
	{
		mTag = tag;
		mTxDescribe.setText(describe);
		setIcon(res);
	}

    public void setTongJiId(int tongJiId) {
        mTongJiId = tongJiId;
    }

	public void setIcon(Object res)
	{
		if(res != null)
		{
			if(res instanceof Integer)
			{
				mIcon.setImageResource((Integer)res);
			}
			else
			{
				Bitmap tempBitmap1 = decodeFile((String)res);
				Bitmap tempBitmap2 = MakeBmpV2.CreateFixBitmapV2(tempBitmap1, 0, 0, MakeBmpV2.POS_H_CENTER | MakeBmpV2.POS_V_CENTER, ShareData.PxToDpi_xhdpi(100), ShareData.PxToDpi_xhdpi(100), Bitmap.Config.ARGB_8888);
				if(tempBitmap1 != null)
				{
					tempBitmap1.recycle();
					tempBitmap1 = null;
				}
				mIconBmp = ImageUtils.MakeRoundBmp(tempBitmap2, ShareData.PxToDpi_xhdpi(10));
				mIcon.setImageBitmap(mIconBmp);
				if(tempBitmap2 != null)
				{
					tempBitmap2.recycle();
					tempBitmap2 = null;
				}
			}
		}
	}

	public void setData(ArrayList<GroupRes> datas)
	{
		mDatas = datas;
		if(mDatas != null)
		{
			mNumber.setText(String.valueOf(mDatas.size()));
		}
		else
		{
			mNumber.setText("0");
		}
	}

	private View.OnClickListener mOnClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{

		}
	};

	public void releaseMem()
	{
		if(mIconBmp != null)
		{
			mIconBmp.recycle();
			mIconBmp = null;
		}
	}
}
