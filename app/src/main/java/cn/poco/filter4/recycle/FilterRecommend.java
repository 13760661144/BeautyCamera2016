package cn.poco.filter4.recycle;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import cn.poco.MaterialMgr2.MgrUtils;
import cn.poco.advanced.ImageUtils;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.BaseItem;
import cn.poco.resource.LockRes;
import cn.poco.resource.RecommendRes;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by lgd on 2017/5/9.
 */

public class FilterRecommend extends BaseItem
{
	private ImageView mImageView;
	private TextView mTextView;
	private ImageView mFlag;

	public FilterRecommend(@NonNull Context context)
	{
		super(context);
		init();
	}

	@Override
	public void SetData(AbsAdapter.ItemInfo info, int index)
	{
		if(info instanceof FilterAdapter.RecommendItemInfo)
		{
			FilterAdapter.ItemInfo itemInfo = (FilterAdapter.RecommendItemInfo)info;
			Glide.with(getContext()).load(itemInfo.m_logos[0]).into(mImageView);
			mTextView.setText(itemInfo.m_names[0]);
			mTextView.setBackgroundColor(itemInfo.m_bkColor);
			mTextView.getBackground().setAlpha(240);   //94

			if (((FilterAdapter.RecommendItemInfo) info).m_ex != null && ((FilterAdapter.RecommendItemInfo) info).m_ex instanceof ArrayList)
			{
				ArrayList res = (ArrayList) ((FilterAdapter.RecommendItemInfo) info).m_ex;
				if (res != null && res.size() > 0)
                {
                    Object o = res.get(0);
                    if (o != null && o instanceof RecommendRes)
                    {
                        RecommendRes recommendRes = (RecommendRes) o;
                        LockRes lockRes = MgrUtils.unLockTheme(recommendRes.m_id);
                        if (lockRes != null && lockRes.m_shareType != LockRes.SHARE_TYPE_NONE)
                        {
                        	if (TagMgr.CheckTag(getContext(), Tags.ADV_RECO_FILTER_FLAG + recommendRes.m_id)) {
								mFlag.setImageResource(R.drawable.sticker_recom);
							} else {
								mFlag.setImageResource(R.drawable.sticker_lock);
							}
							return;
                        }
                    }
                    mFlag.setImageResource(R.drawable.sticker_recom);
                }
			}
		}
	}

	private void init()
	{
//		setBackgroundColor(def_bk_out_color);
		LayoutParams params;
		mImageView = new ImageView(getContext());
//		mImageView.setId(R.id.recycle_item_down_more);
		mImageView.setBackgroundColor(ImageUtils.GetSkinColor(0x66ffffff));
		mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mImageView, params);

		mTextView = new TextView(getContext());
		mTextView.setGravity(Gravity.CENTER);
		mTextView.setTextColor(Color.WHITE);
		mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9f);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(42));
		params.gravity = Gravity.BOTTOM;
		addView(mTextView, params);

		mFlag = new ImageView(getContext());
		mFlag.setImageResource(R.drawable.sticker_recom);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.TOP | Gravity.RIGHT;
		addView(mFlag, params);
	}

	@Override
	public void onSelected()
	{

	}

	@Override
	public void onUnSelected()
	{

	}

	@Override
	public void onClick()
	{

	}
}
