package cn.poco.album;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.poco.album.model.VideoInfo;

/**
 * Created by: fwc
 * Date: 2017/9/5
 */
public class VideoStore {

	private static final long LIMIE_SIZE = 60 * 1024 * 1024;

	private Context mContext;

	// 加volatile避免重排序，确保单例
	private static volatile VideoStore sInstance;

	private List<VideoInfo> mVideoInfos = new ArrayList<>();

	private String[] mVideoColumns = {
			MediaStore.Video.Media.DATA, // 视频路径
			MediaStore.Video.Media.ALBUM, // 所属文件夹
			MediaStore.Video.Media.DATE_MODIFIED, // 修改时间
			MediaStore.Video.Media.DURATION, // 视频时长
			MediaStore.Video.Media.SIZE, // 视频大小
			MediaStore.Video.Media._ID, // 视频id
			MediaStore.Video.Media.MIME_TYPE
	};

	private List<OnCompletedListener> mOnCompletedListeners = new ArrayList<>();
	private volatile boolean mLoadCompeletd;
	private volatile boolean isLoading;

	private VideoStore(Context context) {
		mContext = context.getApplicationContext();
	}

	public static VideoStore getInstance(Context context) {
		if (sInstance == null) {
			synchronized (VideoStore.class) {
				if (sInstance == null) {
					sInstance = new VideoStore(context);
				}
			}
		}

		return sInstance;
	}

	public List<VideoInfo> getVideoInfoList() {

//		if (mVideoInfos.isEmpty()) {
//			initAlbums();
//		}

		return mVideoInfos;
	}

	public void init(final Context context) {

		if (isLoading) {
			return;
		}

		isLoading = true;

		new Thread(new Runnable() {
			@Override
			public void run() {
				initAlbums();
			}
		}).start();
	}

	private void initAlbums() {
		List<VideoInfo> videoInfos = getAllVideos();
		mVideoInfos.clear();
		mVideoInfos.addAll(videoInfos);

		isLoading = false;
		synchronized (this) {
			mLoadCompeletd = true;
			for (OnCompletedListener listener : mOnCompletedListeners) {
				listener.onCompleted();
			}
		}
	}

	public void addOnCompletedListener(OnCompletedListener listener) {

		if (listener != null) {
			synchronized (this) {
				mOnCompletedListeners.add(listener);
				if (mLoadCompeletd) {
					listener.onCompleted();
				}
			}
		}
	}

	public void removeOnCompletedListener(OnCompletedListener listener) {
		if (listener != null) {
			mOnCompletedListeners.remove(listener);
		}
	}

	private List<VideoInfo> getAllVideos() {
		return loadData(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mVideoColumns);
	}

	private List<VideoInfo> loadData(Uri uri, String[] columns) {

		List<VideoInfo> out = new ArrayList<>();
		ContentResolver cr = mContext.getContentResolver();
		Cursor cursor = null;
		try {
			// 查询所有图片数据，按修改时间排序（降序）
			cursor = cr.query(uri, columns, null, null, MediaStore.Images.Media.DATE_MODIFIED + " desc");
			if (cursor != null && cursor.moveToFirst()) {

				int columnsIndex[] = new int[columns.length];
				for (int i = 0; i < columns.length; i++) {
					columnsIndex[i] = cursor.getColumnIndex(columns[i]);
				}

				VideoInfo info;
				String path;
				long size, duration;
				do {
					path = cursor.getString(columnsIndex[0]);
					if (!new File(path).exists()) {
						continue;
					}
					size = cursor.getLong(columnsIndex[4]);
					if (size >= LIMIE_SIZE) {
						continue;
					}
					duration = cursor.getLong(columnsIndex[3]);
					if (duration < 1000)
					{
						continue;
					}

					info = new VideoInfo();
					info.setPath(path);
					info.setFolderName(cursor.getString(columnsIndex[1]));
					info.setLastModified(cursor.getLong(columnsIndex[2]));
					info.setDuration(duration);
					info.setSize(size);
					info.setId(cursor.getInt(columnsIndex[5]));
					info.setMimeType(cursor.getString(columnsIndex[6]));

					if (info.getFolderName() != null) {
						out.add(info);
					}
				} while (cursor.moveToNext());
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return out;
	}


	public void clear() {
		mVideoInfos.clear();
		mOnCompletedListeners.clear();
	}

	public interface OnCompletedListener {
		void onCompleted();
	}
}
