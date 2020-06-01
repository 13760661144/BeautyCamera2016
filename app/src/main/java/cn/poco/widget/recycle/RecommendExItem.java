package cn.poco.widget.recycle;

import android.content.Context;

import cn.poco.recycleview.AbsExConfig;
import cn.poco.recycleview.BaseExAdapter;
import cn.poco.recycleview.BaseGroup;
import cn.poco.recycleview.BaseItem;
import cn.poco.recycleview.BaseItemContainer;

/**
 * Created by lgd on 2017/5/18.
 */

class RecommendExItem extends BaseItemContainer
{

	public RecommendExItem(Context context, AbsExConfig config)
	{
		super(context, config);
	}

	@Override
	public BaseGroup initGroupView()
	{
		return new RecommendExGroup(getContext());
	}

	@Override
	public BaseItem initItemView()
	{
		return new RecommendExSubItem(getContext());
	}

	@Override
	public void setItemInfo(BaseExAdapter.ItemInfo itemInfo,int position)
	{
		super.setItemInfo(itemInfo,position);
	}

	@Override
	public void addItemViews()
	{
		super.addItemViews();
	}

	@Override
	public void removeItemViews()
	{
		super.removeItemViews();
	}
}
