package cn.poco.Theme;

import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import my.beautyCamera.R;

/**
 * Created by lgd on 2016/12/9.
 */

public class ThemeAdapter extends RecyclerView.Adapter
{
	private static final String TAG = "ThemeAdapter";
	private ArrayList<ThemeInfo> themeInfos;
	private int curSelectedIndex;
	private View lastSelectView;
	public ThemeAdapter(ArrayList<ThemeInfo> thumbInfos)
	{
		this(thumbInfos, 0);
	}
	public ThemeAdapter(ArrayList<ThemeInfo> thumbInfos,int curSelectedIndex)
	{
		super();
		this.themeInfos = thumbInfos;
		this.curSelectedIndex = curSelectedIndex;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		ThemeItem thumbItem = new ThemeItem(parent.getContext());
		ViewHolder viewHolder = new ViewHolder(thumbItem);
		return viewHolder;
	}

	/**
	 * 刷新方法，点击刷新，   curView显示，lastView 隐藏
	 *
	 * @param holder
	 * @param position
	 */
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
	{
		ViewHolder viewHolder = (ViewHolder)holder;
		final FrameLayout parent = viewHolder.parent;
		final ImageView hook = viewHolder.hook;
		final TextView textView = viewHolder.textView;
		textView.setText(themeInfos.get(position).getTitle());
		ThemeInfo.Type type = themeInfos.get(position).getType();
		GradientDrawable gradientDrawable;
		if(type == ThemeInfo.Type.LEFT_RIGHT)
		{
			 gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, themeInfos.get(position).getColors());
		}else if(type == ThemeInfo.Type.RIGHT_LEFT){
			gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, themeInfos.get(position).getColors());
		}else{
			gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, themeInfos.get(position).getColors());
		}
		gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		gradientDrawable.setShape(GradientDrawable.OVAL);
		parent.setBackgroundDrawable(gradientDrawable);
		parent.setTag(new Integer(position));
		if(curSelectedIndex == position){
//			hook.setAlpha(1.0f);
			lastSelectView = parent;
			hook.setVisibility(View.VISIBLE);
		}else{
//			hook.setAlpha(0f);
			hook.setVisibility(View.GONE);
		}

		if(onItemClickListener != null)
		{
			parent.setOnTouchListener(animationClickListener);
		}
	}

	// 1  点击未完成，直接滚动到底部        解决
	// 2 点击完成，动画过程滚动到底部             回收的view  半透明

	private int clickPosition = 0;
	private OnThemeAnimationClickListener animationClickListener = new OnThemeAnimationClickListener()
	{
		@Override
		public void onAnimationClickStart(View v)
		{
			if(curSelectedIndex != clickPosition)
			{
				v.findViewById(R.id.theme_color_hook).setVisibility(View.VISIBLE);
				if(lastSelectView != null && lastSelectView.getTag() != null &&  ((int)lastSelectView.getTag()) == curSelectedIndex){
					lastSelectView.findViewById(R.id.theme_color_hook).setVisibility(View.GONE);
				}
				lastSelectView = v;
				curSelectedIndex = clickPosition;
				onItemClickListener.onClick(v, curSelectedIndex);
			}
		}

		@Override
		public void onAnimationClick(View v)
		{
		}

		@Override
		public void onTouch(View v)
		{
			if(v.getTag() != null)
			{
				clickPosition = (int)v.getTag();
			}
		}

		@Override
		public void onRelease(View v)
		{

		}
	};

	@Override
	public int getItemCount()
	{
		return themeInfos.size();
	}

	private onItemClickListener onItemClickListener;

	public void setOnItemClickListener(onItemClickListener onItemClickListener)
	{
		this.onItemClickListener = onItemClickListener;
	}

	interface onItemClickListener
	{
		void onClick(View view, int index);
	}


	class ViewHolder extends RecyclerView.ViewHolder
	{
		FrameLayout parent;
		TextView textView;
		ImageView hook;

		public ViewHolder(View itemView)
		{
			super(itemView);
			parent = (FrameLayout)itemView.findViewById(R.id.theme_color_parent);
			textView = (TextView)itemView.findViewById(R.id.theme_color_text);
			hook = (ImageView)itemView.findViewById(R.id.theme_color_hook);

		}
	}
}
