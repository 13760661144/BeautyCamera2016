package cn.poco.album.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.util.List;

import cn.poco.album.utils.RoundCornerTransformation;
import cn.poco.album.view.SelectedItemView;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/12/16
 */
public class SelectedAdapter extends RecyclerView.Adapter<SelectedAdapter.SelectedHolder>  {

	private Context mContext;
	private List<String> mSeletedPhotos;

	private OnDeleteClickListener mListener;

	private BitmapPool mBitmapPool;

	public SelectedAdapter(Context context, List<String> seletedPhotos) {
		mContext = context;
		mSeletedPhotos = seletedPhotos;

		mBitmapPool = Glide.get(context).getBitmapPool();
	}

	@Override
	public SelectedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = new SelectedItemView(mContext);
		RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ShareData.PxToDpi_xhdpi(190),
																		 ShareData.PxToDpi_xhdpi(190));
		itemView.setLayoutParams(params);

		return new SelectedHolder(itemView);
	}

	@Override
	public void onBindViewHolder(final SelectedHolder holder, int position) {
		SelectedItemView itemView = (SelectedItemView) holder.itemView;
		itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onDelete(v, holder.getLayoutPosition());
				}
			}
		});
		String path = mSeletedPhotos.get(position);
		Glide.with(mContext)
				.load(path)
				.asBitmap()
				.placeholder(R.drawable.cloudalbum_default_placeholder)
				.transform(new CenterCrop(mContext),
						   new RoundCornerTransformation(mContext, ShareData.PxToDpi_xhdpi(4), 0))
				.into(itemView.image);
	}

	@Override
	public int getItemCount() {
		return mSeletedPhotos.size();
	}

	public void setOnDeleteClickListener(OnDeleteClickListener listener) {
		mListener = listener;
	}

	static class SelectedHolder extends RecyclerView.ViewHolder {

		SelectedHolder(View itemView) {
			super(itemView);
		}
	}

	public interface OnDeleteClickListener {
		void onDelete(View view, int position);
	}
}
