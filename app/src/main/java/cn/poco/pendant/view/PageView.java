package cn.poco.pendant.view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.poco.pendant.PendantPage;
import cn.poco.resource.DecorateGroupRes;
import cn.poco.resource.DecorateRes;
import cn.poco.tianutils.ShareData;

/**
 * Created by: fwc
 * Date: 2016/11/18
 */
public class PageView extends FrameLayout {

	private Context mContext;

	private GridView mGridView;

	private LayoutParams mLayoutParams;

	private List<DecorateRes> mItems;

	private int mGroupIndex;
	private int mPageIndex;

	private OnClickRes mOnClickRes;

	private int mImageHeight;

	private boolean mIntercept = false;

	public PageView(Context context, DecorateGroupRes res, int groupIndex, int pageIndex) {
		super(context);
		mPageIndex = pageIndex;
		mGroupIndex = groupIndex;

		mImageHeight = ShareData.PxToDpi_xhdpi(90);

		mItems = new ArrayList<>();
		int start = pageIndex * PendantPage.PAGE_COUNT;
		int end = start + PendantPage.PAGE_COUNT;
		if (res.m_group.size() < end) {
			end = res.m_group.size();
		}

		mItems.addAll(res.m_group.subList(start, end));
		init();
	}

	public void setOnClickRes(OnClickRes res) {
		mOnClickRes = res;
	}

	private void init() {
		mContext = getContext();

		mGridView = new GridView(mContext);
		mGridView.setNumColumns(6);
		mGridView.setPadding(ShareData.PxToDpi_xhdpi(28), ShareData.PxToDpi_xhdpi(18), ShareData.PxToDpi_xhdpi(28), 0);
		mGridView.setHorizontalSpacing(ShareData.PxToDpi_xhdpi(30));
		mGridView.setVerticalSpacing(ShareData.PxToDpi_xhdpi(18));

		mLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mGridView, mLayoutParams);

		mGridView.setAdapter(new GridAdapter());
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mOnClickRes != null) {
					int index = mPageIndex * PendantPage.PAGE_COUNT + position;
					DecorateRes res = mItems.get(position);
					mOnClickRes.onClick(mGroupIndex, index, res);
				}
			}
		});

	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		mIntercept = l != null;
		super.setOnClickListener(l);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return mIntercept || super.onInterceptTouchEvent(ev);
	}

	private class GridAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public DecorateRes getItem(int i) {
			return mItems.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			ImageView imageView;

			if (view == null) {
				imageView = new ImageView(mContext);
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			} else {
				imageView = (ImageView) view;
			}

			Object image = mItems.get(i).m_thumb;
			if (image instanceof Integer) {
				Glide.with(mContext).load((Integer)image).into(imageView);
			} else {
				Glide.with(mContext).load(image.toString()).into(imageView);
			}
			GridView.LayoutParams params = new GridView.LayoutParams(mImageHeight, mImageHeight);
			imageView.setLayoutParams(params);
			return imageView;
		}
	}

	public interface OnClickRes {
		void onClick(int groupIndex, int position, DecorateRes res);
	}
}
