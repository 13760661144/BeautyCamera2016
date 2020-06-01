package cn.poco.cloudAlbum.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.poco.advanced.ImageUtils;
import cn.poco.cloudAlbum.TransportImgs;
import cn.poco.cloudalbumlibs.ITongJi;
import cn.poco.cloudalbumlibs.adapter.AbsTransportAdapter;
import cn.poco.cloudalbumlibs.model.PhotoInfo;
import cn.poco.cloudalbumlibs.model.TransportImgInfo;
import cn.poco.cloudalbumlibs.model.TransportItem;
import cn.poco.cloudalbumlibs.utils.CloudAlbumDialog;
import cn.poco.cloudalbumlibs.utils.ImageLoader;
import cn.poco.cloudalbumlibs.view.ProgressView;
import cn.poco.storage.StorageService;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/8/29
 */
public class DownloadAdapter extends AbsTransportAdapter {

	private ITongJi mITongJi;

	public DownloadAdapter(Context context, List<TransportItem> items, ITongJi iTongJi) {
		super(context, items);

		mITongJi = iTongJi;
	}

	@Override
	protected void showTitle(final ViewHolder viewHolder, String title, int status) {
		viewHolder.message.setVisibility(View.GONE);
		switch(status) {
			case TransportItem.UPLOADING:
				viewHolder.icon.setImageResource(cn.poco.cloudalbumlibs.R.drawable.cloudalbum_upload2);
				break;
			case TransportItem.DOWNLOADING:
				viewHolder.icon.setImageResource(cn.poco.cloudalbumlibs.R.drawable.cloudalbum_download);
				break;
			case TransportItem.ERROR:
				viewHolder.icon.setImageResource(cn.poco.cloudalbumlibs.R.drawable.cloudalbum_error);
				break;
			case TransportItem.WAITTING:
				viewHolder.icon.setImageResource(cn.poco.cloudalbumlibs.R.drawable.cloudalbum_waitting_upload);
				viewHolder.message.setVisibility(View.VISIBLE);
				break;
		}
		viewHolder.tip.setText(title);
	}

	@Override
	protected void showItem(final ViewHolder viewHolder, final TransportImgInfo info, int type) {
		viewHolder.name.setText(info.getImgName());
		String albumName = info.getToAlbumName();
		String album = mContext.getResources().getString(R.string.album);
		if (!info.getToAlbumName().endsWith(album)) {
			String gap = album.equals("相册") ? "" : " ";
			albumName = albumName + gap + album;
		}

		String s = mContext.getResources().getString(R.string.download_from_album, albumName);
		viewHolder.path.setText(s);
		ImageLoader.displayImage(mContext, info.getImgPath(), viewHolder.image);
		viewHolder.size.setText(getSizeString(info.getImgVolume()));
		viewHolder.size2.setText(getSizeString(info.getImgVolume()));
		viewHolder.progress.setTag(info);

		if (mMode == EDIT) {
			if (canEdit(info, type)) {
				viewHolder.size.setVisibility(View.INVISIBLE);
				viewHolder.size2.setVisibility(View.VISIBLE);
				info.setStatus(type);
				info.setCanEdit(true);

				if (info.isSelect()) {
					viewHolder.selected.setVisibility(View.VISIBLE);
					viewHolder.unSelect.setVisibility(View.INVISIBLE);
				} else {
					viewHolder.selected.setVisibility(View.INVISIBLE);
					viewHolder.unSelect.setVisibility(View.VISIBLE);
				}
			} else {
				viewHolder.size.setVisibility(View.VISIBLE);
				viewHolder.size2.setVisibility(View.INVISIBLE);
				viewHolder.selected.setVisibility(View.INVISIBLE);
				viewHolder.unSelect.setVisibility(View.INVISIBLE);
				info.setStatus(type);
				info.setCanEdit(false);

				if (info.isSelect()) {
					info.setSelect(false);
					if (mSelectItems.remove(info)) {
						mListener.onChange(mSelectItems.size());
					}
				}
			}
		} else {
			viewHolder.size.setVisibility(View.VISIBLE);
			viewHolder.size2.setVisibility(View.INVISIBLE);
			viewHolder.selected.setVisibility(View.INVISIBLE);
			viewHolder.unSelect.setVisibility(View.INVISIBLE);
		}

		if (type == TransportItem.DOWNLOADING) {

			info.setProgressListener(new TransportImgInfo.onProgressListener() {
				@Override
				public void updateProgress(float progress) {
					if (viewHolder.progress.getTag() == info) {
						viewHolder.progress.setProgress(progress / 100.f);

						if (mMode == EDIT && progress > 0 &&
								(viewHolder.unSelect.getVisibility() == View.VISIBLE ||
								viewHolder.selected.getVisibility() == View.VISIBLE)) {
							notifyDataSetChanged();
						} else if (mMode == EDIT && progress <= 0 &&
								(viewHolder.unSelect.getVisibility() != View.VISIBLE &&
								viewHolder.selected.getVisibility() != View.VISIBLE)) {
							info.setCanEdit(true);
							notifyDataSetChanged();
						}
					}
				}
			});

			info.updateProgress();
		} else {
			viewHolder.progress.setProgress(0);
		}
	}

	@Override
	protected void itemClick(int position, int index) {
		if (mMode == EDIT && index != 0) {
			TransportImgInfo info = getItem(position).getItems().get(index-1);

			if (info.isCanEdit()) {
				if (info.isSelect()) {
					info.setSelect(false);
					mSelectItems.remove(info);
				}
				else {
					mSelectItems.add(info);
					info.setSelect(true);
				}
				notifyDataSetChanged();
				if (mListener != null) {
					mListener.onChange(mSelectItems.size());
				}
			}
		} else if (mMode == NONE) {
			final TransportItem item = getItem(position);
			if (item == null) {
				return;
			}
			final TransportImgInfo info = item.getItems().get(index-1);
			if (item.getStatus() == TransportItem.ERROR && index != 0) {
				StorageService.ClearTransportInfo(mContext, info.getAcid());
				TransportImgs.getInstance(mContext).mDownloadErrorList.remove(info);
				retryDownload(info);

				int size = TransportImgs.getInstance(mContext).mDownloadErrorList.size();

				if (size == 0) {
					mItems.remove(item);
				} else {
					String title = mContext.getResources().getString(R.string.download_error_title, size);
					item.setTitle(title);
				}

				notifyDataSetChanged();
			}

			if (item.getStatus() == TransportItem.WAITTING && index != 0) {

				mITongJi.transportWaitClick(mContext);

				if (TransportImgs.sAllowCellular) {
					downloadWaittingImg(item, info);
				} else {
					showDialog(item, info);
				}
			}
		}
	}

	@Override
	protected boolean canEdit(TransportImgInfo info, int type) {

		return info.isCanEdit() || type == TransportItem.ERROR || type == TransportItem.WAITTING ||
				(type == TransportItem.DOWNLOADING && info.getProgress() <= 0.0f);
	}

	private void showDialog(final TransportItem item, final TransportImgInfo info) {

		final CloudAlbumDialog dialog = new CloudAlbumDialog(mContext, ViewPager.LayoutParams.WRAP_CONTENT,
															 ViewPager.LayoutParams.WRAP_CONTENT);
		ImageUtils.AddSkin(mContext, dialog.getOkButtonBg());
		dialog.setCancelButtonText(cn.poco.cloudalbumlibs.R.string.cloud_album_cancel)
				.setOkButtonText(cn.poco.cloudalbumlibs.R.string.cloud_album_continue)
				.setMessage(cn.poco.cloudalbumlibs.R.string.cloud_album_ensure_to_download)
				.setListener(new CloudAlbumDialog.OnButtonClickListener() {
					@Override
					public void onOkButtonClick() {
						mITongJi.confirmCellularToTransport(mContext);
						dialog.dismiss();

						TransportImgs.sAllowCellular = true;
						downloadWaittingImg(item, info);
					}

					@Override
					public void onCancelButtonClick() {
						mITongJi.cancelCellularToTransport(mContext);
						dialog.dismiss();
					}
				})
				.show();
	}

	private void downloadWaittingImg(TransportItem item, TransportImgInfo info) {
		TransportImgs.getInstance(mContext).mDownloadWaittingList.remove(info);

		int size = TransportImgs.getInstance(mContext).mDownloadWaittingList.size();

		if (size == 0) {
			mItems.remove(item);
		} else {
			item.getItems().remove(info);
			String title = mContext.getResources().getString(R.string.wait_to_download_title, size);
			item.setTitle(title);
		}

		notifyDataSetChanged();

		retryDownload(info);
	}

	/**
	 * 重新下载
	 * @param info 图片信息
	 */
	private void retryDownload(TransportImgInfo info) {

		PhotoInfo photoInfo = new PhotoInfo();
		photoInfo.setUrl(info.getUrl());
		photoInfo.setVolume(info.getImgVolume());
		photoInfo.setCoverUrl(info.getImgPath());

		List<PhotoInfo> photoInfos = new ArrayList<>();
		photoInfos.add(photoInfo);

		TransportImgs.getInstance(mContext).downloadImgs(photoInfos, info.getToAlbumName());

	}

	@Override
	protected void initTransportItem(View convertView, ProgressView progressView, ImageView imageView, TextView nameView, TextView pathView, TextView sizeView, ImageView unSelect, View selected, ImageView selectedBg) {
		super.initTransportItem(convertView, progressView, imageView, nameView, pathView, sizeView, unSelect, selected, selectedBg);
		ImageUtils.AddSkin(mContext, selectedBg);
	}
}
