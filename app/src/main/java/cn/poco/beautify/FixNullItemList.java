package cn.poco.beautify;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import cn.poco.tsv.FastItemList;

public class FixNullItemList extends FastItemList
{
	public FixNullItemList(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}

	public FixNullItemList(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public FixNullItemList(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if(m_drawable)
		{
			int itemW = def_item_left + def_item_width + def_item_right;

			//控制画图矩形
			canvas.save();
			canvas.clipRect(m_showOffsetX + itemW, 0, m_showOffsetX + m_showW, getHeight());

			int infoListSize = m_infoList.size();
			if(itemW > 0 && m_infoList != null && infoListSize > 0)
			{
				int showLen = GetShowNum(m_showW, itemW);
				int fristIndex = m_showOffsetX / itemW;
				if(fristIndex < 0)
				{
					fristIndex = 0;
				}
				else if(fristIndex >= infoListSize)
				{
					fristIndex = infoListSize - 1;
				}
				int endIndex = fristIndex + showLen - 1;
				if(endIndex >= infoListSize)
				{
					endIndex = infoListSize - 1;
				}
				//显示的真实个数
				int len = endIndex - fristIndex + 1;
				if(len > 0)
				{
					//显示
					int x = fristIndex * itemW;
					for(int i = fristIndex; i <= endIndex; i++)
					{
						if(i != 0)
						{
							canvas.save();
							canvas.translate(x + def_item_left, 0);
							DrawItem(canvas, i, m_infoList.get(i));
							canvas.restore();
						}
						x += itemW;
					}
				}
			}

			canvas.restore();

			//画null item
			if(m_infoList != null && m_infoList.size() > 0)
			{
				canvas.save();
				canvas.translate(m_showOffsetX + def_item_left, 0);
				DrawItem(canvas, 0, m_infoList.get(0));
				canvas.restore();
			}
		}
	}

	@Override
	protected int GetTouchIndex(int x)
	{
		//第一个必为null
		if(x - m_showOffsetX < def_item_left + def_item_width + def_item_right)
		{
			return 0;
		}

		return super.GetTouchIndex(x);
	}
}
