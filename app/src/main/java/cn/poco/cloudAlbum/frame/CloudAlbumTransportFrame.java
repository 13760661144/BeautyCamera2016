package cn.poco.cloudAlbum.frame;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.poco.advanced.ImageUtils;
import cn.poco.cloudAlbum.CloudAlbumPage;
import cn.poco.cloudAlbum.IAlbumPage;
import cn.poco.cloudAlbum.TransportImgs;
import cn.poco.cloudAlbum.adapter.DownloadAdapter;
import cn.poco.cloudAlbum.adapter.UploadAdapter;
import cn.poco.cloudalbumlibs.adapter.AbsTransportAdapter;
import cn.poco.cloudalbumlibs.model.TransportImgInfo;
import cn.poco.cloudalbumlibs.model.TransportItem;
import cn.poco.cloudalbumlibs.utils.CloudAlbumDialog;
import cn.poco.cloudalbumlibs.utils.SToast;
import cn.poco.statistics.TongJiUtils;
import cn.poco.storage.StorageService;
import cn.poco.system.AppInterface;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/8/29
 */
public class CloudAlbumTransportFrame extends FrameLayout implements CloudAlbumPage.IFrame {

	protected Context mContext;

	private boolean mDownload;

	private RelativeLayout mContainer;

	private RelativeLayout mAppBar;
	private ImageView mBackView;
	private TextView mEditView;

	private ListView mListView;

	private TextView[] mTabs = new TextView[2];
	private View mTabLine;

	private RelativeLayout mSelectBar;
	private TextView mSelectAll;
	private TextView mSelectCancel;

	private FrameLayout mBottomBar;
	private View mDeleteView;
	private ImageView mDeleteImage;
	private TextView mDeleteText;

	private int mPosition = 0;

	private Animator[] mTabLineAnimators = new Animator[2];

	private AbsTransportAdapter mAdapter;

	private UploadAdapter mUploadAdapter;
	private DownloadAdapter mDownloadAdapter;

	private UploadAdapter mEditUplaodAdapter;
	private DownloadAdapter mEditDownloadAdapter;
	private List<TransportImgInfo> mEditInfos = new ArrayList<>();
	private List<TransportItem> mEditItems = new ArrayList<>();

	private List<TransportItem> mUploadItems = new ArrayList<>();
	private List<TransportItem> mDownloadItems = new ArrayList<>();

	private int mTotalCount;

	private TransportImgs mTransportImgs;

	private IAlbumPage mIAlbumPage;

	private boolean mEditting = false;

	private ListView mEditListView;

	public CloudAlbumTransportFrame(IAlbumPage iAlbumPage, boolean download) {
		super(iAlbumPage.getPageContext());
		TongJiUtils.onPageStart(getContext(), R.string.云相册_设置_传输列表);
		mContext = iAlbumPage.getPageContext();
		mDownload = download;
		mIAlbumPage = iAlbumPage;

		mTransportImgs = TransportImgs.getInstance(mContext);

		initViews();
	}

	private void initViews() {
		LayoutInflater.from(mContext).inflate(cn.poco.cloudalbumlibs.R.layout.cloudalbum_transport_frame, this, true);

		mContainer = (RelativeLayout)findViewById(cn.poco.cloudalbumlibs.R.id.rl_container);
		initContainer(mContainer);

		mAppBar = (RelativeLayout)findViewById(R.id.app_bar);
		mBackView = (ImageView)findViewById(R.id.iv_back);
		mEditView = (TextView)findViewById(R.id.tv_edit);
		initAppBar(mAppBar, mBackView, mEditView);
		mBackView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				back();
			}
		});
		mEditView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mTotalCount != 0 && getMaxSelectCount() > 0) {
					edit();
				}
			}
		});

		mTabs[0] = (TextView)findViewById(cn.poco.cloudalbumlibs.R.id.tv_upload);
		mTabs[1] = (TextView)findViewById(cn.poco.cloudalbumlibs.R.id.tv_download);
		mTabLine = findViewById(cn.poco.cloudalbumlibs.R.id.tab_line);
		initTab(mTabs[0], mTabs[1], mTabLine);
		mTabs[0].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				changeTab(0);
			}
		});
		mTabs[1].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				changeTab(1);
			}
		});

		mListView = (ListView)findViewById(cn.poco.cloudalbumlibs.R.id.listView);
		initListView(mListView);

		mEditListView = (ListView)findViewById(R.id.edit_list);

		mSelectBar = (RelativeLayout)findViewById(cn.poco.cloudalbumlibs.R.id.select_bar);
		mSelectAll = (TextView)findViewById(cn.poco.cloudalbumlibs.R.id.tv_select_all);
		mSelectCancel = (TextView)findViewById(cn.poco.cloudalbumlibs.R.id.tv_select_cancel);
		initSelectBar(mSelectBar, mSelectAll, mSelectCancel);
		mSelectCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		mSelectAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (getMaxSelectCount() == 0) {
					return;
				}

				if (mPosition == 0 && mEditUplaodAdapter != null) {

					if (mEditUplaodAdapter.getSelectCount() >= getMaxSelectCount()) {
						mEditUplaodAdapter.cancelSelectAll();
					} else {
						mEditUplaodAdapter.selectAll();
					}
				} else if (mEditDownloadAdapter != null){
					if (mEditDownloadAdapter.getSelectCount() >= getMaxSelectCount()) {
						mEditDownloadAdapter.cancelSelectAll();
					} else {
						mEditDownloadAdapter.selectAll();
					}
				}
			}
		});
		mSelectCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editCancel();
			}
		});

		mBottomBar = (FrameLayout)findViewById(R.id.bottom_bar);
		mDeleteImage = (ImageView)findViewById(R.id.iv_delete);
		mDeleteText = (TextView)findViewById(R.id.tv_delete);
		initBottomBar(mBottomBar, mDeleteImage, mDeleteText);
		mDeleteView = findViewById(R.id.delete);
		mDeleteView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((mEditUplaodAdapter != null && mEditUplaodAdapter.getSelectCount() > 0) || (mEditDownloadAdapter != null && mEditDownloadAdapter.getSelectCount() > 0)) {
					delete();
				}
			}
		});

		initAdapter();

		changeAdapter();

		mTabLine.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				mTabLine.getViewTreeObserver().removeOnGlobalLayoutListener(this);

				mTabLine.getLayoutParams().width = mTabs[0].getWidth();
				mTabLine.requestLayout();

				float x = mTabs[1].getX();
				x += (mTabs[1].getWidth() - mTabs[0].getWidth()) / 2f;


				mTabLineAnimators[0] = ObjectAnimator.ofFloat(mTabLine, "translationX", x, 0);
				mTabLineAnimators[0].setDuration(200);

				mTabLineAnimators[1] = ObjectAnimator.ofFloat(mTabLine, "translationX", 0, x);
				mTabLineAnimators[1].setDuration(200);

				if (mDownload) {
					changeTab(1);
				}
			}
		});

		mTransportImgs.addListener(mDataChange);
	}

	private void initAdapter() {

		List<TransportImgInfo> uploadingImgs = mTransportImgs.mUploadingList;
		if (!uploadingImgs.isEmpty()) {
			TransportItem uploadItem = new TransportItem();
			uploadItem.setStatus(TransportItem.UPLOADING);
			uploadItem.setTitle("正在上传（" + uploadingImgs.size() + "）");
			uploadItem.setItems(uploadingImgs);
			mUploadItems.add(uploadItem);
		} else if (mTransportImgs.getUploadWaittingSize() != 0) {
			TransportItem uploadItem = new TransportItem();
			uploadItem.setStatus(TransportItem.WAITTING);
			uploadItem.setTitle("等待上传（" + mTransportImgs.getUploadWaittingSize() + "）");
			uploadItem.setItems(mTransportImgs.getUploadWaittingList());
			mUploadItems.add(uploadItem);
		}

		List<TransportImgInfo> uploadErrorImgs = mTransportImgs.mUploadErrorList;
		if (!uploadErrorImgs.isEmpty()) {
			TransportItem uploadItem = new TransportItem();
			uploadItem.setStatus(TransportItem.ERROR);
			uploadItem.setTitle("上传失败（" + uploadErrorImgs.size() + "）");
			uploadItem.setItems(uploadErrorImgs);
			mUploadItems.add(uploadItem);
		}

		List<TransportImgInfo> downloadingImgs = mTransportImgs.mDownloadingList;
		if (!downloadingImgs.isEmpty()) {
			TransportItem downloadItem = new TransportItem();
			downloadItem.setStatus(TransportItem.DOWNLOADING);
			downloadItem.setTitle("正在下载（" + downloadingImgs.size() + "）");
			downloadItem.setItems(downloadingImgs);
			mDownloadItems.add(downloadItem);
		} else if (!mTransportImgs.mDownloadWaittingList.isEmpty()) {
			TransportItem downloadItem = new TransportItem();
			downloadItem.setStatus(TransportItem.WAITTING);
			downloadItem.setTitle("等待下载（" + mTransportImgs.mDownloadWaittingList.size() + "）");
			downloadItem.setItems(mTransportImgs.mDownloadWaittingList);
			mDownloadItems.add(downloadItem);
		}

		List<TransportImgInfo> downloadErrorImgs = mTransportImgs.mDownloadErrorList;
		if (!downloadErrorImgs.isEmpty()) {
			TransportItem downloadItem = new TransportItem();
			downloadItem.setStatus(TransportItem.ERROR);
			downloadItem.setTitle("下载失败（" + downloadErrorImgs.size() + "）");
			downloadItem.setItems(downloadErrorImgs);
			mDownloadItems.add(downloadItem);
		}

		mUploadAdapter = new UploadAdapter(mContext, mUploadItems, AppInterface.GetInstance(mContext));
		mUploadAdapter.setSelectListener(new UploadAdapter.SelectListener() {
			@Override
			public void onChange(int number) {
				changeSelectState(number);
			}
		});
		mDownloadAdapter = new DownloadAdapter(mContext, mDownloadItems, AppInterface.GetInstance(mContext));
		mDownloadAdapter.setSelectListener(new DownloadAdapter.SelectListener() {
			@Override
			public void onChange(int number) {
				changeSelectState(number);
			}
		});
	}

	private void changeEditState() {
		if (getMaxSelectCount() > 0) {
			mEditView.setAlpha(1);
		} else {
			mEditView.setAlpha(0.3f);
		}
	}

	private void changeSelectState(int number) {
		if (getMaxSelectCount() == 0) {
			mSelectAll.setText("全选");
			mSelectAll.setAlpha(0.3f);
		} else if (number >= getMaxSelectCount()) {
			mSelectAll.setText("取消全选");
			mSelectAll.setAlpha(1);
		} else {
			mSelectAll.setText("全选");
			mSelectAll.setAlpha(1);
		}

		if (number == 0) {
			haveSelect(false);
		} else {
			haveSelect(true);
		}
	}

	/**
	 * 编辑-删除操作
	 */
	private void delete() {
		int messageId;
		if (mPosition == 0) {
			messageId = cn.poco.cloudalbumlibs.R.string.cloud_album_ensure_to_cancel_upload;
		} else {
			messageId = cn.poco.cloudalbumlibs.R.string.cloud_album_ensure_to_cancel_download;
		}

		final CloudAlbumDialog dialog = new CloudAlbumDialog(mContext, ViewPager.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT);
		ImageUtils.AddSkin(mContext, dialog.getOkButtonBg());
		dialog.setCancelButtonText(cn.poco.cloudalbumlibs.R.string.cloud_album_cancel).setOkButtonText(cn.poco.cloudalbumlibs.R.string.cloud_album_ok).setMessage(messageId).setListener(new CloudAlbumDialog.OnButtonClickListener() {
			@Override
			public void onOkButtonClick() {
				dialog.dismiss();
				if (mPosition == 0 && mEditUplaodAdapter != null) {
					cancelUpload(mEditUplaodAdapter.getSelectItems());
				} else if (mEditDownloadAdapter != null) {
					cancelDownload(mEditDownloadAdapter.getSelectItems());
				}

				mTransportImgs.transportState();
				countChange();

				if (mTotalCount == 0) {
					editCancel();
				}

				if (mEditting && mEditInfos.isEmpty()) {
					editCancel();
				}
			}

			@Override
			public void onCancelButtonClick() {
				dialog.dismiss();
			}
		}).show();
	}

	private void cancelUpload(List<TransportImgInfo> infos) {
		if (infos == null || infos.isEmpty()) {
			return;
		}

		TransportItem item;
		for (TransportImgInfo info : infos) {
			switch (info.getStatus()) {
				case TransportItem.UPLOADING:
					info.setProgressListener(null);
//					StorageService.CancelUploadTask(mContext, info.getAcid());
					mTransportImgs.mUploadingList.remove(info);
					mEditInfos.remove(info);
					item = findItem(false, TransportItem.UPLOADING);
					if (item != null) {
						processUploading(item, false);
					}

					if (mEditting) {
						for (TransportItem item1 : mEditItems) {
							if (item1.getStatus() == TransportItem.UPLOADING) {
								if (mEditInfos.isEmpty()) {
									mEditItems.remove(item1);
								} else {
									item1.setTitle("正在上传（" + mEditInfos.size() + "）");
								}
								break;
							}
						}
					}

					break;
				case TransportItem.WAITTING:
					List<TransportImgInfo> waittingInfos = mTransportImgs.mUploadWaittingMap.get(info.getFolderId());
					if (waittingInfos != null) {
						waittingInfos.remove(info);
					}
					item = findItem(false, TransportItem.WAITTING);
					if (item != null) {
//						item.getItems().remove(info);
						processUploadWaitting(item);
					}
					break;
				case TransportItem.ERROR:
					mTransportImgs.mUploadErrorList.remove(info);
					StorageService.ClearTransportInfo(mContext, info.getAcid());
					item = findItem(false, TransportItem.ERROR);
					if (item != null) {
						processUploadError(item);
					}
					break;
			}
		}

		infos.clear();
		haveSelect(false);
		mSelectAll.setText("全选");
		if (getMaxSelectCount() == 0) {
			mSelectAll.setAlpha(0.3f);
		}

		if (mEditUplaodAdapter != null) {
			mEditUplaodAdapter.notifyDataSetChanged();
		}
		mUploadAdapter.notifyDataSetChanged();
	}

	private void cancelDownload(List<TransportImgInfo> infos) {
		if (infos == null || infos.isEmpty()) {
			return;
		}

		TransportItem item;
		for (TransportImgInfo info : infos) {
			switch (info.getStatus()) {
				case TransportItem.DOWNLOADING:
					info.setProgressListener(null);
//					StorageService.CancelDownloadTask(mContext, info.getAcid());
					mTransportImgs.mDownloadingList.remove(info);
					mEditInfos.remove(info);
					item = findItem(true, TransportItem.DOWNLOADING);
					if (item != null) {
						processDownloading(item, false);
					}

					if (mEditting) {
						for (TransportItem item1 : mEditItems) {
							if (item1.getStatus() == TransportItem.DOWNLOADING) {
								if (mEditInfos.isEmpty()) {
									mEditItems.remove(item1);
								} else {
									item1.setTitle("正在下载（" + mEditInfos.size() + "）");
								}
								break;
							}
						}
					}
					break;
				case TransportItem.WAITTING:
					mTransportImgs.mDownloadWaittingList.remove(info);
					item = findItem(true, TransportItem.WAITTING);
					if (item != null) {
						processDownloadWaitting(item);
					}
					break;
				case TransportItem.ERROR:
					mTransportImgs.mDownloadErrorList.remove(info);
					StorageService.ClearTransportInfo(mContext, info.getAcid());
					item = findItem(true, TransportItem.ERROR);
					if (item != null) {
						processDownloadError(item);
					}
					break;
			}
		}

		infos.clear();
		haveSelect(false);
		mSelectAll.setText("全选");
		if (getMaxSelectCount() == 0) {
			mSelectAll.setAlpha(0.3f);
		}
		if (mEditDownloadAdapter != null) {
			mEditDownloadAdapter.notifyDataSetChanged();
		}
		mDownloadAdapter.notifyDataSetChanged();
	}

	private TransportItem findItem(boolean download, int type) {

		if (!download) {
			if (mUploadItems != null) {
				for (TransportItem item : mUploadItems) {
					if (item.getStatus() == type) {
						return item;
					}
				}
			}
		} else {
			if (mDownloadItems != null) {
				for (TransportItem item : mDownloadItems) {
					if (item.getStatus() == type) {
						return item;
					}
				}
			}
		}

		return null;
	}

	private void haveSelect(boolean select) {
		if (select) {
			mDeleteText.setAlpha(1);
			mDeleteImage.setImageResource(cn.poco.cloudalbumlibs.R.drawable.cloudalbum_red_delete);
			mDeleteView.setEnabled(true);
		} else {
			mDeleteText.setAlpha(0.2f);
			mDeleteImage.setImageResource(cn.poco.cloudalbumlibs.R.drawable.cloudalbum_red_delete_default);
			mDeleteView.setEnabled(false);
		}

		haveSelect(mDeleteImage, mDeleteText, select);
	}

	/**
	 * 编辑模式
	 */
	private void edit() {

		if (mEditting) {
			return;
		}

		// 由于mAdapter.startEdit()并没有触发回调，需要自己改变状态
		if (getMaxSelectCount() == 0) {
			mSelectAll.setAlpha(0.3f);
		}

		mEditting = true;

		haveSelect(false);
		Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.cloudalbum_slide_in_top);
		Animation animation1 = AnimationUtils.loadAnimation(mContext, R.anim.cloudalbum_slide_in_bottom);

		mSelectBar.setVisibility(View.VISIBLE);
		mSelectBar.startAnimation(animation);

		mBottomBar.setVisibility(View.VISIBLE);
		mBottomBar.startAnimation(animation1);

//		mAdapter.startEdit();
		mSelectAll.setText("全选");
		haveSelect(false);

		cancelAllTransport();
	}

	/**
	 * 取消所有还没有开始传输的任务
	 */
	private void cancelAllTransport() {
		mEditItems.clear();
		mEditInfos.clear();
		if (mPosition == 0) {
			for (TransportImgInfo info : mTransportImgs.mUploadingList) {
				if (info.isCanEdit()) {
					// 去除监听器，以免更新界面显示
//					info.setProgressListener(null);
					// 上面的代码要注释，因为可能无法取消传输
					info.setStop(true);
					StorageService.CancelUploadTask(mContext, info.getAcid());
					mEditInfos.add(info);
				}
			}

			if (!mEditInfos.isEmpty()) {
				TransportItem uploadItem = new TransportItem();
				uploadItem.setStatus(TransportItem.UPLOADING);
				uploadItem.setTitle("正在上传（" + mEditInfos.size() + "）");
				uploadItem.setItems(mEditInfos);
				mEditItems.add(uploadItem);
			} else if (mTransportImgs.getUploadWaittingSize() != 0) {
				TransportItem uploadItem = new TransportItem();
				uploadItem.setStatus(TransportItem.WAITTING);
				uploadItem.setTitle("等待上传（" + mTransportImgs.getUploadWaittingSize() + "）");
				uploadItem.setItems(mTransportImgs.getUploadWaittingList());
				mEditItems.add(uploadItem);
			}

			List<TransportImgInfo> uploadErrorImgs = mTransportImgs.mUploadErrorList;
			if (!uploadErrorImgs.isEmpty()) {
				TransportItem uploadItem = new TransportItem();
				uploadItem.setStatus(TransportItem.ERROR);
				uploadItem.setTitle("上传失败（" + uploadErrorImgs.size() + "）");
				uploadItem.setItems(uploadErrorImgs);
				mEditItems.add(uploadItem);
			}

			mEditUplaodAdapter = new UploadAdapter(mContext, mEditItems, AppInterface.GetInstance(mContext));
			mEditUplaodAdapter.setSelectListener(new AbsTransportAdapter.SelectListener() {
				@Override
				public void onChange(int number) {
					changeSelectState(number);
				}
			});
			mEditUplaodAdapter.startEdit();
			mEditListView.setAdapter(mEditUplaodAdapter);
			mEditListView.setVisibility(VISIBLE);
			mListView.setVisibility(GONE);

		} else {
			for (TransportImgInfo info : mTransportImgs.mDownloadingList) {
				if (info.isCanEdit()) {
					// 去除监听器，以免更新界面显示
//					info.setProgressListener(null);
					info.setStop(true);
					StorageService.CancelDownloadTask(mContext, info.getAcid());
					mEditInfos.add(info);
				}
			}

			if (!mEditInfos.isEmpty()) {
				TransportItem downloadItem = new TransportItem();
				downloadItem.setStatus(TransportItem.DOWNLOADING);
				downloadItem.setTitle("正在下载（" + mEditInfos.size() + "）");
				downloadItem.setItems(mEditInfos);
				mEditItems.add(downloadItem);
			} else if (!mTransportImgs.mDownloadWaittingList.isEmpty()) {
				TransportItem downloadItem = new TransportItem();
				downloadItem.setStatus(TransportItem.WAITTING);
				downloadItem.setTitle("等待下载（" + mTransportImgs.mDownloadWaittingList.size() + "）");
				downloadItem.setItems(mTransportImgs.mDownloadWaittingList);
				mEditItems.add(downloadItem);
			}

			List<TransportImgInfo> downloadErrorImgs = mTransportImgs.mDownloadErrorList;
			if (!downloadErrorImgs.isEmpty()) {
				TransportItem downloadItem = new TransportItem();
				downloadItem.setStatus(TransportItem.ERROR);
				downloadItem.setTitle("下载失败（" + downloadErrorImgs.size() + "）");
				downloadItem.setItems(downloadErrorImgs);
				mEditItems.add(downloadItem);
			}

			mEditDownloadAdapter = new DownloadAdapter(mContext, mEditItems, AppInterface.GetInstance(mContext));
			mEditDownloadAdapter.setSelectListener(new AbsTransportAdapter.SelectListener() {
				@Override
				public void onChange(int number) {
					changeSelectState(number);
				}
			});
			mEditDownloadAdapter.startEdit();
			mEditListView.setAdapter(mEditDownloadAdapter);
			mEditListView.setVisibility(VISIBLE);
			mListView.setVisibility(GONE);
		}
	}

	/**
	 * 取消编辑模式
	 */
	private void editCancel() {

		if (!mEditting) {
			return;
		}

		mEditting = false;
		Animation animation = AnimationUtils.loadAnimation(mContext, cn.poco.cloudalbumlibs.R.anim.cloudalbum_slide_out_top);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mSelectBar.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});

		Animation animation1 = AnimationUtils.loadAnimation(mContext, cn.poco.cloudalbumlibs.R.anim.cloudalbum_slide_out_bottom);
		animation1.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mBottomBar.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});

		mSelectBar.startAnimation(animation);

		mBottomBar.startAnimation(animation1);

//		mAdapter.exitEdit();

		mSelectAll.setAlpha(1);

		if (mEditUplaodAdapter != null) {
			mEditUplaodAdapter.exitEdit();
			mEditUplaodAdapter = null;
		}

		if (mEditDownloadAdapter != null) {
			mEditDownloadAdapter.exitEdit();
			mEditDownloadAdapter = null;
		}
		mEditInfos.clear();
		mEditListView.setVisibility(GONE);
		mListView.setVisibility(VISIBLE);
		changeEditState();

		continueTransport();
	}

	/**
	 * 继续传输暂停的任务
	 */
	private void continueTransport() {
		List<TransportImgInfo> infos = new ArrayList<>();
		if (mPosition == 0) {
			for (TransportImgInfo info : mTransportImgs.mUploadingList) {
				if (info.isCanEdit()) {
					info.setStop(false);
					infos.add(info);
				}
			}
			if (!infos.isEmpty()) {
				mTransportImgs.uploadImgs(infos);
			}
		} else {
			for (TransportImgInfo info : mTransportImgs.mDownloadingList) {
				if (info.isCanEdit()) {
					info.setStop(false);
					infos.add(info);
				}
			}
			mTransportImgs.downloadImgs(infos);
		}
	}

	/**
	 * 切换Tab
	 */
	private void changeTab(int position) {
		if (mPosition != position) {

			mTabs[mPosition].setTextColor(Color.parseColor("#e6000000"));
			mTabs[position].setTextColor(ImageUtils.GetSkinColor(0xffe75988));

			mTabLineAnimators[position].start();

			mPosition = position;

			changeAdapter();
		}
	}

	/**
	 * 切换Adapter
	 */
	private void changeAdapter() {
		if (mPosition == 0) {
			mAdapter = mUploadAdapter;
		} else {
			mAdapter = mDownloadAdapter;
		}

		mListView.setAdapter(mAdapter);
		countChange();
	}

	private void countChange() {
		mTotalCount = getTotalCount();

		changeEditState();
	}

	/**
	 * 获得当前Adapter中item的数量
	 */
	private int getTotalCount() {
		int count = 0;
		if (mPosition == 0) {
			for (TransportItem item : mUploadItems) {
				count += item.getItems().size();
			}
		} else {
			for (TransportItem item : mDownloadItems) {
				count += item.getItems().size();
			}
		}

		return count;
	}

	private int getMaxSelectCount() {
		int count = 0;
		List<TransportItem> items;
		if (mPosition == 0) {
			items = mUploadItems;
		} else {
			items = mDownloadItems;
		}

		for (TransportItem item : items) {
			for (TransportImgInfo info : item.getItems()) {
				if (info.isStop() || info.getProgress() <= 0 || info.getStatus() == TransportItem.ERROR || info.getStatus() == TransportItem.WAITTING) {
					count++;
				}
			}
		}

		return count;
	}

	private TransportImgs.IDataChange mDataChange = new TransportImgs.IDataChange() {

		@Override
		public void onDataChange(int type, boolean download) {

			if (!download) {
				processUpload(type);
			} else {
				processDownload(type);
			}

			countChange();
			if (mTotalCount == 0) {
				editCancel();
			}
		}
	};

	private void processUpload(int type) {

		switch (type) {
			case TransportImgs.PROGRESS:
//				if (mUploadItems.size() == 1) {
//					TransportItem item = mUploadItems.get(0);
//					if (item.getStatus() == TransportItem.UPLOADING) {
//						processUploading(item);
//					} else if (item.getStatus() == TransportItem.WAITTING) {
//						mUploadItems.remove(item);
//						processAddUploading();
//					} else if (item.getStatus() == TransportItem.ERROR) {
//						processUploadError(item);
//						processAddUploading();
//					}
//				} else if (mUploadItems.size() == 2) {
//					TransportItem item = mUploadItems.get(0);
//
//					if (item.getStatus() == TransportItem.UPLOADING) {
//						processUploading(item);
//					} else if (item.getStatus() == TransportItem.WAITTING) {
//						mUploadItems.remove(item);
//						processAddUploading();
//					}
//				}

				if (!mUploadItems.isEmpty()) {
					TransportItem item = mUploadItems.get(0);
					if (item.getStatus() == TransportItem.UPLOADING) {
						processUploading(item, true);
					} else if (item.getStatus() == TransportItem.WAITTING) {
						if (mTransportImgs.getUploadWaittingSize() == 0) {
							mUploadItems.remove(item);
						}
						processAddUploading();
					} else if (item.getStatus() == TransportItem.ERROR) {
						processUploadError(item);
						processAddUploading();
					}
				} else {
					processAddUploading();
				}
				break;
			case TransportImgs.COMPLETE:
				if (!mUploadItems.isEmpty()) {
					processUploading(mUploadItems.get(0), true);
				}
				break;
			case TransportImgs.WAITTING:
				if (!mUploadItems.isEmpty()) {
					TransportItem item = mUploadItems.get(0);
					if (item.getStatus() == TransportItem.WAITTING) {
						processUploadWaitting(item);
					} else if (item.getStatus() == TransportItem.ERROR) {
						processUploadError(item);
						processAddUploadWaitting();
					}
				}
				break;
			case TransportImgs.ERROR:
				if (mUploadItems.size() == 1) {

					if (mUploadItems.get(0).getStatus() == TransportItem.UPLOADING) {
						processUploading(mUploadItems.get(0), true);
					}
					processAddUploadError();
				} else if (mUploadItems.size() == 2) {
					processUploading(mUploadItems.get(0), true);
					if (mUploadItems.get(mUploadItems.size() - 1).getStatus() == TransportItem.WAITTING) {
						processAddUploadError();
					} else {
						processUploadError(mUploadItems.get(mUploadItems.size() - 1));
					}
				} else if (mUploadItems.size() == 3) {
					processUploading(mUploadItems.get(0), true);
					processUploadError(mUploadItems.get(mUploadItems.size() - 1));
				}
				break;
		}
		changeEditState();
		mUploadAdapter.notifyDataSetChanged();
	}

	private void processDownload(int type) {
		switch (type) {
			case TransportImgs.PROGRESS:
				if (!mDownloadItems.isEmpty()) {
					TransportItem item = mDownloadItems.get(0);
					if (item.getStatus() == TransportItem.DOWNLOADING) {
						processDownloading(item, true);
					} else if (item.getStatus() == TransportItem.WAITTING) {
						if (mTransportImgs.mDownloadWaittingList.isEmpty()) {
							mDownloadItems.remove(item);
						}
						processAddDownloading();
					} else if (item.getStatus() == TransportItem.ERROR) {
						processDownloadError(item);
						processAddDownloading();
					}
				} else {
					processAddDownloading();
				}
				break;
			case TransportImgs.COMPLETE:
				if (!mDownloadItems.isEmpty()) {
					processDownloading(mDownloadItems.get(0), true);
				}
				break;
			case TransportImgs.WAITTING:
				if (!mDownloadItems.isEmpty()) {
					TransportItem item = mDownloadItems.get(0);
					if (item.getStatus() == TransportItem.WAITTING) {
						processDownloadWaitting(item);
					} else if (item.getStatus() == TransportItem.ERROR) {
						processDownloadError(item);
						processAddDownloadWaitting();
					}
				}
				break;
			case TransportImgs.ERROR:
				if (mDownloadItems.size() == 1) {
					if (mDownloadItems.get(0).getStatus() == TransportItem.DOWNLOADING) {
						processDownloading(mDownloadItems.get(0), true);
					}
					processAddDownloadError();
				} else if (mDownloadItems.size() == 2) {
					processDownloading(mDownloadItems.get(0), true);
					if (mDownloadItems.get(mDownloadItems.size() - 1).getStatus() == TransportItem.WAITTING) {
						processAddDownloadError();
					} else {
						processDownloadError(mDownloadItems.get(mDownloadItems.size() - 1));
					}
				} else if (mDownloadItems.size() == 3) {
					processDownloading(mDownloadItems.get(0), true);
					processDownloadError(mDownloadItems.get(mDownloadItems.size() - 1));
				}
				break;
		}
		changeEditState();
		mDownloadAdapter.notifyDataSetChanged();
	}

	private void processUploading(TransportItem item, boolean success) {

		if (mTransportImgs.mUploadingList.isEmpty()) {

			if (mTransportImgs.mUploadErrorList.isEmpty() && success) {
				SToast.makeText(mContext, "上传完成", SToast.LENGTH_SHORT).show();
			}
			mUploadItems.remove(item);
		} else {
			item.setTitle("正在上传（" + mTransportImgs.mUploadingList.size() + "）");
		}

	}

	private void processUploadError(TransportItem item) {
		if (mTransportImgs.mUploadErrorList.isEmpty()) {
			mUploadItems.remove(item);
		} else {
			item.setTitle("上传失败（" + mTransportImgs.mUploadErrorList.size() + "）");
		}
	}

	private void processAddUploading() {
		if (!mTransportImgs.mUploadingList.isEmpty()) {
			TransportItem uploadItem = new TransportItem();
			uploadItem.setStatus(TransportItem.UPLOADING);
			uploadItem.setTitle("正在上传（" + mTransportImgs.mUploadingList.size() + "）");
			uploadItem.setItems(mTransportImgs.mUploadingList);
			mUploadItems.add(0, uploadItem);
		}
	}

	private void processUploadWaitting(TransportItem item) {
		if (mTransportImgs.getUploadWaittingSize() == 0) {
			mUploadItems.remove(item);
		} else {
			item.setTitle("等待上传（" + mTransportImgs.getUploadWaittingSize() + "）");
			item.setItems(mTransportImgs.getUploadWaittingList());
		}
	}

	private void processAddUploadWaitting() {
		if (!mTransportImgs.mUploadWaittingMap.isEmpty()) {
			TransportItem uploadItem = new TransportItem();
			uploadItem.setStatus(TransportItem.WAITTING);
			uploadItem.setTitle("等待上传（" + mTransportImgs.getUploadWaittingSize() + "）");
			uploadItem.setItems(mTransportImgs.getUploadWaittingList());
			mUploadItems.add(0, uploadItem);
		}
	}

	private void processAddUploadError() {
		if (!mTransportImgs.mUploadErrorList.isEmpty()) {
			TransportItem uploadItem = new TransportItem();
			uploadItem.setStatus(TransportItem.ERROR);
			uploadItem.setTitle("上传失败（" + mTransportImgs.mUploadErrorList.size() + "）");
			uploadItem.setItems(mTransportImgs.mUploadErrorList);
			mUploadItems.add(uploadItem);
		}
	}

	private void processDownloading(TransportItem item, boolean success) {

		if (mTransportImgs.mDownloadingList.isEmpty()) {

			if (mTransportImgs.mDownloadErrorList.isEmpty() && success) {
				SToast.makeText(mContext, "下载完成", SToast.LENGTH_SHORT).show();
			}
			mDownloadItems.remove(item);
		} else {
			item.setTitle("正在下载（" + mTransportImgs.mDownloadingList.size() + "）");
		}

	}

	private void processAddDownloading() {
		if (!mTransportImgs.mDownloadingList.isEmpty()) {
			TransportItem downloadItem = new TransportItem();
			downloadItem.setStatus(TransportItem.DOWNLOADING);
			downloadItem.setTitle("正在下载（" + mTransportImgs.mDownloadingList.size() + "）");
			downloadItem.setItems(mTransportImgs.mDownloadingList);
			mDownloadItems.add(0, downloadItem);
		}
	}

	private void processAddDownloadError() {
		if (!mTransportImgs.mDownloadErrorList.isEmpty()) {
			TransportItem downloadItem = new TransportItem();
			downloadItem.setStatus(TransportItem.ERROR);
			downloadItem.setTitle("下载失败（" + mTransportImgs.mDownloadErrorList.size() + "）");
			downloadItem.setItems(mTransportImgs.mDownloadErrorList);
			mDownloadItems.add(downloadItem);
		}
	}

	private void processDownloadError(TransportItem item) {
		if (mTransportImgs.mDownloadErrorList.isEmpty()) {
			mDownloadItems.remove(item);
		} else {
			item.setTitle("下载失败（" + mTransportImgs.mDownloadErrorList.size() + "）");
		}
	}

	private void processDownloadWaitting(TransportItem item) {
		if (mTransportImgs.mDownloadWaittingList.isEmpty()) {
			mDownloadItems.remove(item);
		} else {
			item.setTitle("等待下载（" + mTransportImgs.mDownloadWaittingList.size() + "）");
		}
	}

	private void processAddDownloadWaitting() {
		if (!mTransportImgs.mDownloadWaittingList.isEmpty()) {
			TransportItem downloadItem = new TransportItem();
			downloadItem.setStatus(TransportItem.WAITTING);
			downloadItem.setTitle("等待下载（" + mTransportImgs.mDownloadWaittingList.size() + "）");
			downloadItem.setItems(mTransportImgs.mDownloadWaittingList);
			mDownloadItems.add(0, downloadItem);
		}
	}

	protected final void back() {

		mTransportImgs.removeListener(mDataChange);

		// 避免内存泄漏
		for (TransportImgInfo info : mTransportImgs.mUploadingList) {
			info.setProgressListener(null);
		}

		for (TransportImgInfo info : mTransportImgs.mDownloadingList) {
			info.setProgressListener(null);
		}

		onBack();
	}

	protected void initContainer(RelativeLayout container) {
	}

	protected void initAppBar(RelativeLayout appBar, ImageView backView, TextView editView) {
		ImageUtils.AddSkin(mContext, backView);
		editView.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
	}

	protected void initTab(TextView uploadTab, TextView downloadTab, View tabLine) {
		uploadTab.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
//		downloadTab.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
		tabLine.setBackgroundColor(ImageUtils.GetSkinColor(0xffe75988));
	}

	protected void initListView(ListView listView) {
	}

	protected void initSelectBar(RelativeLayout selectBar, TextView selectAll, TextView selectCancel) {
		selectAll.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
		selectCancel.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
	}

	protected void initBottomBar(FrameLayout bottomBar, ImageView deleteImage, TextView deleteText) {
	}

	protected void haveSelect(ImageView deleteImage, TextView deleteText, boolean select) {
	}

	/**
	 * 退出时回调
	 */
	protected void onBack() {
		mIAlbumPage.onFrameBack(this);
	}

	@Override
	public boolean onBackPress() {

		if (mEditting) {
			editCancel();
		} else {
			back();
		}
		return true;
	}

	@Override
	public void onResume() {
		TongJiUtils.onPageResume(getContext(), R.string.云相册_设置_传输列表);
	}

	@Override
	public void onPause() {
		TongJiUtils.onPagePause(getContext(), R.string.云相册_设置_传输列表);
	}

	@Override
	public void onClose() {
		TongJiUtils.onPageEnd(getContext(), R.string.云相册_设置_传输列表);
	}
}
