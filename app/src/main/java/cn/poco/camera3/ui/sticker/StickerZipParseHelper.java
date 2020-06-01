package cn.poco.camera3.ui.sticker;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;

import cn.poco.camera3.info.sticker.StickerInfo;
import cn.poco.dynamicSticker.v2.StickerJsonParse;
import cn.poco.resource.VideoStickerRes;
import cn.poco.utils.FileUtil;

/**
 * @author gxx
 */

public class StickerZipParseHelper
{
	private static final String ASSET_FILE_BASE_PATH = "stickers";

	//zip包 主json
	public static final String STICKER_JSON = "sticker.json";

	private static ArrayList<VideoStickerRes> mCache;

	/**
	 * 根据id构建目标路径
	 */
	static String GetStickerIdFolderPath(VideoStickerRes res)
	{

		if(res != null)
		{
			String s = res.GetSaveParentPath() + File.separator + res.m_id;
			File file = new File(s);
			if(!file.exists())
			{
				boolean mkdirs = file.mkdirs();
			}
			return s;
		}
		return null;
	}

	/**
	 * sticker zip sd 路径
	 */
	static String GetStickerZipPath(VideoStickerRes res)
	{
		if(res != null)
		{
			String parent = GetStickerIdFolderPath(res);
			if(parent != null)
			{
				return parent + File.separator + res.m_res_name;
			}
		}
		return null;
	}

	/**
	 * sticker zip asset 路径
	 */
	static String GetAssetStickerZipPath(VideoStickerRes res)
	{
		if(res != null)
		{
			return ASSET_FILE_BASE_PATH + File.separator + res.m_res_name;
		}
		return null;
	}

	static boolean isAssetFile(String fileName)
	{
		return !TextUtils.isEmpty(fileName) && fileName.startsWith("file:///android_asset");
	}

	public static synchronized void addCache(VideoStickerRes item)
	{
		if(mCache == null)
		{
			mCache = new ArrayList<>();
		}

		if(mCache.size() >= 5)
		{
			VideoStickerRes remove = mCache.remove(0);
			if(remove != null)
			{
				remove.mStickerRes = null;
			}
		}
		mCache.add(item);
	}

	public static synchronized void clearAll()
	{
		if(mCache != null)
		{
			for(VideoStickerRes videoStickerRes : mCache)
			{
				if(videoStickerRes != null)
				{
					videoStickerRes.mStickerRes = null;
				}
			}
			mCache.clear();
		}
	}

	// ========================================== handler ======================================== //
	public static class ParseHandler extends Handler
	{
		public static final int MSG_PARSE = 1;

		private Context mContext;
		private Handler mUiHandler;

		private volatile boolean isNewParse = false;
		private volatile boolean isParse = false;

		public ParseHandler(Looper looper, Context context, Handler uiHandler)
		{
			super(looper);
			this.mContext = context;
			this.mUiHandler = uiHandler;
		}

		public void clearAll()
		{
			removeMessages(MSG_PARSE);
			mContext = null;
			mUiHandler = null;
		}

		public void setNewParse(boolean newParse)
		{
			this.isNewParse = newParse;
		}

		public boolean isParse()
		{
			return isParse;
		}

		@Override
		public void handleMessage(Message msg)
		{
			if(msg != null)
			{
				switch(msg.what)
				{
					case MSG_PARSE:
					{
						if(msg.obj != null)
						{
							ParseObj obj = (ParseObj)msg.obj;
							msg.obj = null;
							cn.poco.resource.VideoStickerRes res = obj.res;
							if(res != null)
							{
								isParse = true;
								//根据id构建目标路径
								String foldPath = GetStickerIdFolderPath(res);

								//1、assets目录的zip
								//2、已下载目录的zip

								if(isAssetFile(res.m_res_path))
								{
									//先删除旧包
									//asset拷贝到SD卡 file:///android_asset/sticker/xxx.zip
									FileUtil.assets2SD(mContext, GetAssetStickerZipPath(res), GetStickerZipPath(res), true);
								}

								res.mStickerRes = StickerJsonParse.parseZipRes(foldPath, res.m_res_name, false);
								if(res.mStickerRes != null && res.mStickerRes.mStickerSoundRes != null)
								{
									res.mStickerRes.mStickerSoundRes.mStickerId = res.m_id;
								}
								isParse = false;

								if(!isNewParse && mUiHandler != null)
								{
									mUiHandler.obtainMessage(MSG_PARSE, obj).sendToTarget();
								}
								isNewParse = false;
							}

						}
						break;
					}
				}
			}
		}

		public static class ParseObj
		{
			public VideoStickerRes res;
			public boolean repeat;
			public boolean isTabChanged;
			public boolean isMakeupRes;
		}
	}
}
