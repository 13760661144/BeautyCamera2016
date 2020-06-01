package cn.poco.album;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.poco.album.model.FolderInfo;
import cn.poco.album.model.PhotoInfo;
import cn.poco.beautify4.Beautify4Page;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/12/12
 */
public class PhotoStore {

	private static final int DEFAULT_SIZE = 101;
	private static final int DEFAULT_GAP = 5;

	// 加volatile避免重排序，确保单例
	private static volatile PhotoStore sInstance;

	private Context mContext;

	private List<FolderInfo> mFolderInfos = new ArrayList<>();

	/**
	 * 记录缓存图片数据的开始下标
	 */
	private int mStart;

	/**
	 * 记录当前缓存的图片数据的数量
	 */
	private int mCount;

	/**
	 * 默认缓存图片的大小
	 */
	private int mCacheSize;

	/**
	 * 当图片下标距离缓存边界大多时提前加载数据
	 */
	private int mCacheGap;

	/**
	 * 缓存的图片数据
	 */
	private List<PhotoInfo> mCachePhotoInfos;

	private int mFolderIndex = 0;

	public static int sPosition;
	public static int sOffset;

	public static int sLastFolderIndex = 0;

	private List<ILoadComplete> mILoadCompletes = new ArrayList<>();
	private boolean mLoadCompeletd = false;

	private AtomicBoolean mLoading;

	private String[] mColumns = {
			MediaStore.Images.Media.DATA, // 图片路径
			MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // 所属文件夹
			MediaStore.Images.Media.DATE_MODIFIED, // 修改时间
			MediaStore.Images.Media.ORIENTATION, // 图片方向
			MediaStore.Images.Media.SIZE, // 图片大小
			MediaStore.Images.Media._ID, // 图片id
	};

	private PhotoStore(Context context) {
		mContext = context.getApplicationContext();

		mCachePhotoInfos = new ArrayList<>();
		mStart = mCount = 0;

		mCacheSize = DEFAULT_SIZE;
		mCacheGap = DEFAULT_GAP;

		mLoading = new AtomicBoolean(false);
	}

	public boolean isLoading() {
		return mLoading.get();
	}

	public synchronized void initFolderInfos(boolean reLoad) {
		if (reLoad || !mLoadCompeletd) {
			initFolderInfos();
		}
	}

	/**
	 * 初始化文件夹信息
	 */
	private void initFolderInfos() {

		mLoading.set(true);

		List<FolderInfo> folderInfos = new ArrayList<>();

		ArrayMap<String, FolderInfo> groups = new ArrayMap<>();

		Cursor cursor = null;

		String[] columns = new String[] {
				MediaStore.Images.Media.DATA, // 图片路径
				MediaStore.Images.Media.BUCKET_DISPLAY_NAME // 所属文件夹
		};

		ContentResolver cr = mContext.getContentResolver();

		try {
			// 查询所有图片数据，按修改时间排序（降序）
			cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, MediaStore.Images.Media.DATE_MODIFIED + " desc");

		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		String albumCover = null;

		if (cursor != null) {
			String folderName, curFolder = null;
			FolderInfo folderInfo = null;
			String path;
			int indexOfCamera = -1;
			int indexOfDCIM = -1;
			File file;
			if (cursor.moveToFirst()) {
				do {
					path = cursor.getString(cursor.getColumnIndex(columns[0]));

					if (TextUtils.isEmpty(path)) {
						continue;
					}

					file = new File(path);
					if (!file.exists() || !file.isFile()) {
						continue;
					}

					if (albumCover == null) {
						albumCover = path;
					}

					folderName = cursor.getString(cursor.getColumnIndex(columns[1]));
					if (!TextUtils.isEmpty(folderName)) {
						if (curFolder == null || !curFolder.equals(folderName)) {
							folderInfo = groups.get(folderName);
							if (folderInfo == null) {
								folderInfo = new FolderInfo();
								folderInfo.setName(folderName);
								folderInfo.setCover(path);
								groups.put(folderName, folderInfo);
								folderInfos.add(folderInfo);

								if (folderName.equals("Camera")) {
									indexOfCamera = folderInfos.size() - 1;
								}

								if (folderName.equals("DCIM")) {
									indexOfDCIM = folderInfos.size() - 1;
								}
							}
						}
						folderInfo.addCount();
						curFolder = folderName;
					}

				} while (cursor.moveToNext());

				if (indexOfDCIM > -1) {
					folderInfo = folderInfos.get(indexOfDCIM);
					folderInfos.remove(indexOfDCIM);
					folderInfos.add(0, folderInfo);
				}

				if (indexOfCamera > -1) {
					if (indexOfCamera < indexOfDCIM) {
						indexOfCamera++;
					}
					folderInfo = folderInfos.get(indexOfCamera);
					folderInfos.remove(indexOfCamera);
					folderInfos.add(0, folderInfo);
				}
			}
			cursor.close();
		}

		int totalCount = 0;
		for (FolderInfo info : folderInfos) {
			totalCount += info.getCount();
		}

		// 新增系统相册文件夹，包含所有的图片数据
		String album = mContext.getResources().getString(R.string.system_album);
		FolderInfo folderInfo = new FolderInfo();
		folderInfo.setName(album);
		folderInfo.setCover(albumCover);
		folderInfo.setCount(totalCount);
		folderInfos.add(0, folderInfo);

		synchronized (this) {
			mLoadCompeletd = true;
			boolean update = true;
			if (mFolderInfos.isEmpty()) {
				update = false;
				mFolderInfos.addAll(folderInfos);
				folderInfos.clear();
			}

			notify(folderInfos, update);
		}

		mLoading.set(false);
	}

	public void addLoadCompleteListener(ILoadComplete iLoadComplete) {
		if (iLoadComplete != null) {
			synchronized (this) {
				boolean update = !mFolderInfos.isEmpty();
				mILoadCompletes.add(iLoadComplete);
				if (mLoadCompeletd) {
					iLoadComplete.onCompleted(mFolderInfos, update);
				}
			}
		}
	}

	public void removeLoadCompleteListener(ILoadComplete iLoadComplete) {
		if (iLoadComplete != null) {
			mILoadCompletes.remove(iLoadComplete);
		}
	}

	private void notify(List<FolderInfo> folderInfos, boolean update) {
		if (mILoadCompletes.isEmpty()) {
			setFolderInfos(folderInfos);
			return;
		}

		for (ILoadComplete iLoadComplete : mILoadCompletes) {
			iLoadComplete.onCompleted(folderInfos, update);
		}
	}

	public int getFolderIndex() {
		return mFolderIndex;
	}

	public synchronized void setFolderInfos(List<FolderInfo> folderInfos) {
		if (folderInfos.isEmpty() || mFolderInfos == folderInfos) {
			return;
		}

		mFolderInfos.clear();
		mFolderInfos.addAll(folderInfos);
		folderInfos.clear();
	}

	public FolderInfo getFolderInfo(int index) {
		if (index < 0 || index >= mFolderInfos.size()) {
			index = 0;
		}

		return mFolderInfos.get(index);
	}

	public List<FolderInfo> getFolderInfos() {
		return mFolderInfos;
	}

	/**
	 * 判断是否需要重新加载数据更新图片缓存
	 *
	 * @param folderName 文件夹名字，可为null
	 * @param index       图片下标
	 * @return true: 需要重新加载数据
	 */
	public boolean shouldReloadData(String folderName, int index) {

		int folderIndex = getFolderIndex(folderName);

		return shouldReloadData(folderIndex, index);
	}

	/**
	 * 判断是否需要重新加载数据更新图片缓存
	 *
	 * @param folderIndex 文件夹下标
	 * @param index 图片下标
	 * @return true: 需要重新加载数据
	 */
	public boolean shouldReloadData(int folderIndex, int index) {
		if (mFolderIndex != folderIndex) {
			// 不同文件夹
			return true;
		}

		if (index < 0) {
			index = 0;
		}

		int start = index - mCacheGap;
		int end = index + mCacheGap;
		if (start < 0) {
			start = 0;
		}

		if (end > mFolderInfos.get(folderIndex).getCount()) {
			end = mFolderInfos.get(folderIndex).getCount();
		}

		return start < mStart || end > mStart + mCount;
	}

	/**
	 * 根据图片index获取一定范围的图片数据，并相应做缓存
	 *
	 * @param folderName 文件夹名字，可为null
	 * @param start      获取图片数据的开始下标
	 * @param count      获取图片的数量
	 * @return PhotoInfo列表
	 */
	public List<PhotoInfo> getPhotoInfos(@Nullable String folderName, int start, int count) {

		return getPhotoInfos(folderName, start + count / 2);
	}

	/**
	 * 根据图片index获取一定范围的图片数据，并相应做缓存
	 * 可能存在误差，需要修正
	 *
	 * @param folderName 文件名名字，可为null
	 * @param index      图片下标，注意获取的图片数据范围以该图片数据为中心
	 * @return PhotoInfo列表
	 */
	public List<PhotoInfo> getPhotoInfos(@Nullable String folderName, int index) {

		int folderIndex = getFolderIndex(folderName);
		if (!shouldReloadData(folderName, index)) {
			return mCachePhotoInfos;
		}

		mFolderIndex = folderIndex;

		boolean hasFolderInfo = true;
		String album = mContext.getResources().getString(R.string.system_album);
		if (TextUtils.isEmpty(folderName) || album.equals(folderName)) {
			hasFolderInfo = false;
		}

		List<PhotoInfo> out = new ArrayList<>();

		ContentResolver cr = mContext.getContentResolver();
		Cursor cursor = null;

		int start = index - mCacheSize / 2;
		if (start < 0) {
			start = 0;
		}

		try {
			// 分页查询数据，按修改时间排序（降序）
			cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mColumns, hasFolderInfo ? MediaStore.Images.Media.BUCKET_DISPLAY_NAME + "=?" : null, hasFolderInfo ? new String[] {folderName} : null, MediaStore.Images.Media.DATE_MODIFIED + " desc " +
					" limit " + mCacheSize + " offset " + start);

		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		int realStart = -1;

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				int columnsIndex[] = new int[mColumns.length];
				for (int i = 0; i < mColumns.length; i++) {
					columnsIndex[i] = cursor.getColumnIndex(mColumns[i]);
				}

				PhotoInfo imgInfo;
				String imagePath;
				int i = start-1;
				File file;
				do {
					i++;

					imagePath = cursor.getString(columnsIndex[0]);
					if (TextUtils.isEmpty(imagePath)) {
						continue;
					}
					file = new File(imagePath);
					if (!file.exists() || !file.isFile()) {
						continue;
					}

					if (realStart == -1) {
						realStart = i;
					}

					imgInfo = new PhotoInfo();
					imgInfo.setImagePath(imagePath);
					imgInfo.setFolderName(cursor.getString(columnsIndex[1]));
					imgInfo.setLastModified(cursor.getLong(columnsIndex[2]));
					imgInfo.setRotation(cursor.getInt(columnsIndex[3]));
					imgInfo.setSize(cursor.getLong(columnsIndex[4]));
					imgInfo.setId(cursor.getInt(columnsIndex[5]));
					if (Beautify4Page.sCacheDatas.containsKey(imgInfo.getImagePath())) {
						imgInfo.setShowEdit(true);
					}

					if (imgInfo.getFolderName() != null) {
						out.add(imgInfo);
					}
				} while (cursor.moveToNext());
			}

			cursor.close();
		}

		if (!out.isEmpty()) {
			mCachePhotoInfos.clear();
			mCachePhotoInfos.addAll(out);
			mStart = realStart != -1 ? realStart : 0;
			mCount = out.size();
		}

		return out;
	}

	private int getFolderIndex(String folderName) {
		if (TextUtils.isEmpty(folderName)) {
			return 0;
		}
		FolderInfo info;
		for (int i = 0; i < mFolderInfos.size(); i++) {
			info = mFolderInfos.get(i);
			if (folderName.equals(info.getName())) {
				return i;
			}
		}

		return 0;
	}

	/**
	 * 根据图片信息获取图片下标
	 *
	 * @param folderName 文件名名字，可为null
	 * @param photoInfo  图片信息
	 * @return 图片下标
	 */
	public int getPhotoInfoIndex(@Nullable String folderName, PhotoInfo photoInfo) {
		return getPhotoInfoIndex(folderName, photoInfo.getImagePath());
	}

	/**
	 * 根据图片路径获取图片下标，并相应做缓存
	 * 首先需要根据路径找到在原始图片数据下的index
	 * 接着调用 {@link #getPhotoInfos(String, int)}
	 *
	 * @param folderName 文件名名字，可为null
	 * @param imagePath  图片路径
	 * @return 图片下标
	 */
	public int getPhotoInfoIndex(@Nullable String folderName, String imagePath) {

		boolean hasFolderInfo = true;
		String album = mContext.getResources().getString(R.string.system_album);
		if (TextUtils.isEmpty(folderName) || album.equals(folderName)) {
			hasFolderInfo = false;
		}

		int index = -1;
		boolean init = false;

		ContentResolver cr = mContext.getContentResolver();
		Cursor cursor = null;

		if (!TextUtils.isEmpty(imagePath))
		{
			File file = new File(imagePath);
			if (!TextUtils.isEmpty(imagePath) && file.exists() && file.isFile())
			{
				String[] columns = new String[]{MediaStore.Images.Media.DATA, // 图片路径
				};

				try
				{
					// 查询所有图片数据，按修改时间排序（降序）
					cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, hasFolderInfo ? MediaStore.Images.Media.BUCKET_DISPLAY_NAME + "=?" : null, hasFolderInfo ? new String[]{folderName} : null, MediaStore.Images.Media.DATE_MODIFIED + " desc");

				}
				catch (SQLiteException e)
				{
					e.printStackTrace();
				}

				if (cursor != null)
				{

					String path;
					if (cursor.moveToFirst())
					{

						do
						{
							index++;
							path = cursor.getString(cursor.getColumnIndex(columns[0]));
							if (TextUtils.isEmpty(path))
							{
								if (init)
								{
									index--;
								}
								continue;
							}
							file = new File(path);
							if (!file.exists() || !file.isFile())
							{
								if (init)
								{
									index--;
								}
								continue;
							}

							init = true;
							if (imagePath.equals(path))
							{
								break;
							}
						}
						while (cursor.moveToNext());
					}

					cursor.close();
				}
			}

			getPhotoInfos(folderName, index);
		}
		return index;
	}

	/**
	 * 根据图片下标获取图片信息，系统相册使用
	 *
	 * @param index 图片下标
	 * @return 图片信息
	 */
	public PhotoInfo getPhotoInfo(int index) {
		String folderName = "";
		if (mFolderInfos.size() > mFolderIndex) {
			folderName = mFolderInfos.get(mFolderIndex).getName();
		}
		if (shouldReloadData(folderName, index)) {
			getPhotoInfos(folderName, index);
		}

		int cacheIndex = index - mStart;
		if (cacheIndex < 0) {
			cacheIndex = 0;
		}
		if (mCachePhotoInfos.size() > cacheIndex) {
			return mCachePhotoInfos.get(cacheIndex);
		}

		return null;
	}

	/**
	 * 获取第一个PhotoInfo对象
	 *
	 * @return PhotoInfo对象
	 */
	public PhotoInfo getFirstPhotoInfo() {

		ContentResolver cr = mContext.getContentResolver();
		Cursor cursor = null;

		try {
			cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mColumns, null, null, MediaStore.Images.Media.DATE_MODIFIED + " desc");
		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		if (cursor != null) {

			if (cursor.moveToFirst()) {
				int columnsIndex[] = new int[mColumns.length];
				for (int i = 0; i < mColumns.length; i++) {
					columnsIndex[i] = cursor.getColumnIndex(mColumns[i]);
				}

				PhotoInfo imgInfo;
				String imagePath;
				File file;
				do {
					imagePath = cursor.getString(columnsIndex[0]);
					if (TextUtils.isEmpty(imagePath)) {
						continue;
					}
					file = new File(imagePath);
					if (!file.exists() || !file.isFile()) {
						continue;
					}

					imgInfo = new PhotoInfo();
					imgInfo.setImagePath(imagePath);
					imgInfo.setFolderName(cursor.getString(columnsIndex[1]));
					imgInfo.setLastModified(cursor.getLong(columnsIndex[2]));
					imgInfo.setRotation(cursor.getInt(columnsIndex[3]));
					imgInfo.setSize(cursor.getLong(columnsIndex[4]));
					imgInfo.setId(cursor.getInt(columnsIndex[5]));

					return imgInfo;

				} while (cursor.moveToNext());

				cursor.close();
			}
		}

		return null;
	}

	/**
	 * 将图片下标转换成缓存数据相应的下标
	 *
	 * @param index 图片下标
	 * @return 缓存数据相应的下标
	 */
	public int mapToCacheIndex(int index) {
		return index - mStart < 0 ? 0 : index - mStart;
	}

	/**
	 * 将缓存数据相应的下标转换成真实图片下标
	 *
	 * @param cacheIndex 缓存数据相应的下标
	 * @return 图片下标
	 */
	public int mapFromCacheIndex(int cacheIndex) {
		int count = mFolderInfos.get(mFolderIndex).getCount();
		return cacheIndex + mStart > count - 1 ? count - 1 : cacheIndex + mStart;
	}

	public static PhotoStore getInstance(Context context) {
		if (sInstance == null) {
			synchronized (PhotoStore.class) {
				if (sInstance == null) {
					sInstance = new PhotoStore(context);
				}
			}
		}

		return sInstance;
	}

	public void setSelected(boolean selected) {
		for (PhotoInfo info : mCachePhotoInfos) {
			info.setSelected(selected);
		}
	}

	/**
	 * 清除缓存数据
	 */
	public void clearCache() {
		mCachePhotoInfos.clear();

		mStart = 0;
		mCount = 0;
	}

	public void setCacheSize(int cacheSize) {
		mCacheSize = cacheSize;
	}

	public int getCacheSize() {
		return mCacheSize;
	}

	public void setCacheGap(int cacheGap) {
		mCacheGap = cacheGap;
	}

	public int getCacheGap() {
		return mCacheGap;
	}

	/**
	 * 解释图片信息返回缩略图
	 *
	 * @param info 图片信息
	 * @return Bitmap对象
	 */
	public static Bitmap loadThumb(PhotoInfo info, int size) {

		if (info == null) {
			return null;
		}

		Bitmap bmp, thumb = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(info.getImagePath(), opts);
		opts.inJustDecodeBounds = false;

		if (opts.outWidth > 0 && opts.outHeight > 0) {

			int ref = opts.outWidth < opts.outHeight ? opts.outWidth : opts.outHeight;
			float r = (float)opts.outWidth / (float)opts.outHeight;
			if (r > 1) {
				r = 1 / r;
			}

			int big = opts.outWidth > opts.outHeight ? opts.outWidth : opts.outHeight;
			if (big > 1024 && r < 0.3) {
				ref = (int)((float)big / (r * 20));
			}
			opts.inSampleSize = ref / size;
			bmp = BitmapFactory.decodeFile(info.getImagePath(), opts);
			if (bmp != null && info.getRotation() % 360 != 0) {
				Matrix m = new Matrix();
				m.setRotate(info.getRotation());
				bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, false);
			}

			thumb = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(thumb);
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
			int w = bmp.getWidth();
			int h = bmp.getHeight();
			Rect rcSrc = new Rect();
			if (w < h) {
				rcSrc.set(0, (h - w) / 2, w, (h - w) / 2 + w);
			} else {
				rcSrc.set((w - h) / 2, 0, (w - h) / 2 + h, h);
			}
			canvas.drawBitmap(bmp, rcSrc, new Rect(0, 0, size, size), null);
		}

		return thumb;
	}

	public interface ILoadComplete {
		void onCompleted(List<FolderInfo> folderInfos, boolean update);
	}
}
