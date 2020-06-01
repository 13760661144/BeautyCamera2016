package cn.poco.Theme;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.Theme.site.ThemePageSite;
import cn.poco.advanced.ImageUtils;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.home.home4.utils.PercentUtil;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.SysConfig;
import my.beautyCamera.R;

/**
 * Created by lgd on 2016/12/9.
 */

public class ThemePage extends IPage
{
	private static final String TAG = "ThemePage";
	private RecyclerView mRecyclerView;
	private ThemeAdapter mThemeAdapter;
	private ImageView mBtnBack;
	private TextView mTitle;
	//	private ImageView mSave;
	private int mCurSelectedIndex = 0;
	private int mLastSelectedIndex = 0;
	private ThemePageSite mSite;
	private ArrayList<ThemeInfo> mThemeInfos;
	private int mTopHeight;

	public ThemePage(Context context, BaseSite site)
	{
		super(context, site);
		mSite = (ThemePageSite)site;
		TongJiUtils.onPageStart(getContext(),R.string.主题换肤);
		MyBeautyStat.onPageStartByRes(R.string.个人中心_主题换肤_主页面);

		init();
	}

	private void init()
	{
		setBackgroundColor(Color.WHITE);
		LayoutParams params;
		mTopHeight = PercentUtil.HeightPxToPercent(90);
		String[] titles = getResources().getStringArray(R.array.theme_color_title);
		String[] topColors = getResources().getStringArray(R.array.theme_color_top);
		String[] bottomColors = getResources().getStringArray(R.array.theme_color_bottom);
		String[] iconColors = getResources().getStringArray(R.array.theme_color_icon);
		int[] types = getResources().getIntArray(R.array.theme_color_type);

		int length = titles.length;
		if(length > topColors.length)
		{
			length = topColors.length;
		}
		if(length > bottomColors.length)
		{
			length = bottomColors.length;
		}
		if(length > iconColors.length)
		{
			length = iconColors.length;
		}
		if(length > types.length)
		{
			length = types.length;
		}

		mThemeInfos = new ArrayList<>();
		for(int i = 0; i < length; i++)
		{
			ThemeInfo thumbInfo = new ThemeInfo(new int[]{Color.parseColor(topColors[i]), Color.parseColor(bottomColors[i])}, titles[i], Color.parseColor(iconColors[i]), ThemeInfo.Type.valueOf(types[i]));
			mThemeInfos.add(thumbInfo);
		}

		mCurSelectedIndex = SysConfig.s_skinColorIndex;
		mLastSelectedIndex = mCurSelectedIndex;
		mRecyclerView = new RecyclerView(getContext());
		mRecyclerView.setPadding(PercentUtil.WidthPxToPercent(17), mTopHeight, PercentUtil.WidthPxToPercent(17), 0);
		mRecyclerView.setClipToPadding(false);
		mThemeAdapter = new ThemeAdapter(mThemeInfos, mCurSelectedIndex);
		mRecyclerView.setAdapter(mThemeAdapter);
		mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
		mThemeAdapter.setOnItemClickListener(new ThemeAdapter.onItemClickListener()
		{
			@Override
			public void onClick(View view, int index)
			{
				mCurSelectedIndex = index;
			}
		});
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mRecyclerView, params);

		FrameLayout toolBar = new FrameLayout(getContext());
		toolBar.setClickable(true);
		toolBar.setBackgroundColor(0xf5ffffff);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mTopHeight);
		addView(toolBar, params);
		{
			mBtnBack = new ImageView(getContext());
			mBtnBack.setImageResource(R.drawable.album_back);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
			params.leftMargin = PercentUtil.WidthPxToPercent(15);
			toolBar.addView(mBtnBack, params);
			mBtnBack.setOnClickListener(mOnClickListener);
			ImageUtils.AddSkin(getContext(), mBtnBack);

			mTitle = new TextView(getContext());
			mTitle.setText(getResources().getString(R.string.theme_sub_title));
			mTitle.setTextColor(Color.BLACK);
			mTitle.setAlpha(0.9f);
			mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;
			toolBar.addView(mTitle, params);

//			mSave = new ImageView(getContext());
//			mSave.setImageResource(R.drawable.ok);
//			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//			params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
//			params.rightMargin = ShareData.PxToDpi_xhdpi(20);
//			toolBar.addView(mSave, params);
//			mSave.setOnClickListener(mOnClickListener);
//			ImageUtils.AddSkin(getContext(), mSave);
		}
	}

	@Override
	public void SetData(HashMap<String, Object> params)
	{

	}

	@Override
	public void onBack()
	{
		mBtnBack.performClick();
	}

	private OnClickListener mOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if(v == mBtnBack){
				TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_返回);
				if(mLastSelectedIndex != mCurSelectedIndex)
				{
					MyBeautyStat.onUseTheme(mThemeInfos.get(mCurSelectedIndex).getTitle());

					int[] colors = mThemeInfos.get(mCurSelectedIndex).getColors();
					int skinColor = mThemeInfos.get(mCurSelectedIndex).getSkinColor();
					ThemeInfo.Type type = mThemeInfos.get(mCurSelectedIndex).getType();

                    addTongjiIdByTitle(mThemeInfos.get(mCurSelectedIndex).getTitle());//添加统计

					if(colors != null && colors.length >= 2 && skinColor != 0)
					{
						SysConfig.SetSkinGradientType(type.ordinal());
						SysConfig.SetSkinColor(skinColor);
						SysConfig.SetSkinGradientIndex(mCurSelectedIndex);
						SysConfig.SetSkinGradientColor(colors[0], colors[1]);
					}
					HashMap<String, Object> datas = new HashMap<>();
					datas.put("hasChangedSkin", true);
					mSite.OnSave(getContext(),datas);
				}else{
					mSite.OnBack(getContext());
				}
			}
//			else if(v == mSave){
//				if(mLastSelectedIndex != mCurSelectedIndex)
//				{
//					int[] colors = mThemeInfos.get(mCurSelectedIndex).getColors();
//					int skinColor = mThemeInfos.get(mCurSelectedIndex).getSkinColor();
//					ThemeInfo.Type type = mThemeInfos.get(mCurSelectedIndex).getType();
//					if(colors != null && colors.length >= 2 && skinColor != 0)
//					{
//						SysConfig.SetSkinGradientType(type.ordinal());
//						SysConfig.SetSkinColor(skinColor);
//						SysConfig.SetSkinGradientIndex(mCurSelectedIndex);
//						SysConfig.SetSkinGradientColor(colors[0], colors[1]);
//					}
//					HashMap<String, Object> datas = new HashMap<>();
//					datas.put("hasChangedSkin", true);
//					mSite.OnSave(datas);
//				}else{
//					mSite.OnBack();
//				}
//			}
		}

        private void addTongjiIdByTitle(String title) {
            if (TextUtils.isEmpty(title))
                return;
            if (title.equals("Begonia")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Begonia);
            } else if (title.equals("Sakura")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Sakura);
            } else if (title.equals("Fragrans")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Fragrans);
            } else if (title.equals("Datura")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Datura);
            } else if (title.equals("Minosa")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Minosa);
            } else if (title.equals("Tiffany")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Tiffany);
            } else if (title.equals("Mint")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Mint);
            } else if (title.equals("Jasmine")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Jasmine);
            } else if (title.equals("Tulip")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Tulip);
            } else if (title.equals("Daylily")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Daylily);
            } else if (title.equals("Azalea")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Azalea);
            } else if (title.equals("Peoly")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Peoly);
            } else if (title.equals("Violet")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Violet);
            } else if (title.equals("Cymbiddium")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Cymbiddium);
            } else if (title.equals("Peach")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Peach);
            } else if (title.equals("Canna")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Canna);
            } else if (title.equals("Gardenia")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Gardenia);
            } else if (title.equals("Bamboo")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Bamboo);
            } else if (title.equals("Coral")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Coral);
            } else if (title.equals("Lilac")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Lilac);
            } else if (title.equals("Pansy")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Pansy);
            } else if (title.equals("Ivy")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Ivy);
            } else if (title.equals("Sunflower")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Sunflower);
            } else if (title.equals("Calendula")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Calendula);
            } else if (title.equals("Epiphyllum")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Epiphyllum);
            } else if (title.equals("Statice")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Statice);
            } else if (title.equals("Aloe")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Aloe);
            } else if (title.equals("Daffodil")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Daffodil);
            } else if (title.equals("Iris")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Iris);
            } else if (title.equals("Hyacinth")) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤_Hyacinth);
            }
        }
	};

	@Override
	public void onClose()
	{
		TongJiUtils.onPageEnd(getContext(), R.string.主题换肤);
		MyBeautyStat.onPageEndByRes(R.string.个人中心_主题换肤_主页面);
	}

	@Override
	public void onResume()
	{
		TongJiUtils.onPageResume(getContext(),R.string.主题换肤);
	}

	@Override
	public void onPause()
	{
		TongJiUtils.onPagePause(getContext(),R.string.主题换肤);
	}
}
