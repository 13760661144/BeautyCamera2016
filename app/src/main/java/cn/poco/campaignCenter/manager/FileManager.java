package cn.poco.campaignCenter.manager;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import cn.poco.campaignCenter.model.CampaignInfo;
import cn.poco.system.FolderMgr;
import my.beautyCamera.R;


/**
 * Created by admin on 2016/10/17.
 */

public class FileManager {
    public static final String LOG = FileManager.class.getClass().getName();

    public final String CACHE_DIRECTORY = FolderMgr.getInstance().CAMPAIGN_CENTER_PATH;
    public static final String FILE_NAME = "cache.json";
    public final String CACHE_FILE = CACHE_DIRECTORY + File.separator + FILE_NAME;

    private static volatile FileManager Instance = null;
    private Handler mHandler;

    public static final int CREATE_NEW_FILE = 1 << 0;
    public static final int ALREALY_EXIST_FILE = 1 << 1;
    public static final int FAIL_TO_CREATE_FILE = 1 << 2;


    // singleton
    public synchronized static FileManager getInstacne() {
        FileManager localInstance = Instance;
        if (localInstance == null) {
            synchronized (ConnectionsManager.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new FileManager();
                }
            }
        }
        return localInstance;
    }

    private FileManager() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void saveDataToFile(final String name, final String data) {
        new Thread (){
            @Override
            public void run() {
                boolean sdCardState = isSdCardStateValid();
                if (!sdCardState) {
                   return;
                }
                BufferedWriter bufWriter = null;
                try {
                    File cacheDirector = new File(CACHE_DIRECTORY);
                    if (!cacheDirector.exists()) {
                        cacheDirector.mkdirs();
                    }

                    File cacheFile = new File(cacheDirector, name);
                    bufWriter = new BufferedWriter(new FileWriter(cacheFile, false));
                    bufWriter.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (bufWriter != null) {
                        try {
                            bufWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }


    public void getDataFromCacheFile(final Context context, final String name, final Map<Integer, List<CampaignInfo>> map, final FileManager.FileManagerDelegate delegate) {
        final Map<Integer, List<CampaignInfo>> infoMap = map;
        final List<CampaignInfo> autoSlideListData = infoMap.get(0);
        final List<CampaignInfo> displayListData = infoMap.get(1);
        final List<CampaignInfo> listItemData = infoMap.get(2);

        final File file = new File(name);
            Thread getDataThread = new Thread() {
                @Override
                public void run() {
                    boolean sdCardState = isSdCardStateValid();
                    if (!sdCardState) {
                       return;
                    }

                    final String jsonStr;
                    if (!TextUtils.isEmpty(name) && file.exists()) {
                    try {
                        FileInputStream stream = new FileInputStream(file);
                        FileChannel fc = stream.getChannel();

                        try {
                            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                            jsonStr = Charset.defaultCharset().decode(bb).toString();

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
//                                    readJsonAndEncapsulateData(jsonStr, "position_1", autoSlideListData);
//                                    readJsonAndEncapsulateData(jsonStr, "position_2", displayListData);
                                    readJsonAndEncapsulateData(jsonStr, "position_3", listItemData);
                                    delegate.onSuccessFetch(infoMap);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                stream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        autoSlideListData.clear();
                        displayListData.clear();
                        listItemData.clear();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                delegate.failToFetch(context.getString(R.string.fail_get_file));
                            }
                        });
                    }
                } else {
                        delegate.failToFetch(context.getString(R.string.can_not_find_file));
                    }
            };
        };
        getDataThread.start();
    }


    /**
     *
     * @param json 读取到的JSON
     * @param key 目标key
     * @param list 从key中读取到的数据存放的List
     * @return
     */

    private List<CampaignInfo> readJsonAndEncapsulateData(String json, String key, List<CampaignInfo> list) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            boolean hasResult = jsonObject.has(key);
            // 先判断是否存在对应的Key,避免程序崩溃
            if (hasResult) {
                JSONArray data = jsonObject.getJSONArray(key);
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jo = data.getJSONObject(i);
                    String id = jo.getString("id");
                    String coverUrl = jo.getString("img_url");
                    String position = jo.getString("position");
                    String sort = jo.getString("sort");
                    String openUrl = jo.getString("url");
                    String title = jo.getString("title");
                    String type = jo.getString("type");
                    String tj_id = jo.getString("tj_id");
                    String shareTitle = jo.getString("share_title");
                    String shareLink = jo.getString("share_link");
                    String shareDesc = jo.getString("share_desc");
                    String shareImg = jo.getString("share_img");
                    String tryUrl = jo.getString("try_url");
                    String tryNowId = jo.getString("try_tj_id");
                    String shareIconId = jo.getString("share_tj_id");

                    CampaignInfo info = new CampaignInfo();
                    info.setId(id);
                    info.setCoverUrl(coverUrl);
                    info.setPosition(position);
                    info.setSort(sort);
                    info.setOpenUrl(openUrl);
                    info.setTitle(title);
                    info.setCampaignType(CampaignInfo.CampaignType.getCampaignTypeByValue(type));
                    info.setStatisticId(tj_id);
                    info.setShareTitle(shareTitle);
                    info.setShareLink(shareLink);
                    info.setShareDescription(shareDesc);
                    info.setShareImg(shareImg);
                    info.setTryUrl(tryUrl);
                    info.setTryNowId(tryNowId);
                    info.setShareIconId(shareIconId);
                    list.add(info);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     *
     * @param path 图片的路径
     * @return state,返回文件的创建状态
     */

    public int checkCacheFile(String path) {
        boolean sdCardState = isSdCardStateValid();
        if (sdCardState) {
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                if (!file.exists()) {
                    if (file.isDirectory()) {
                        Log.i(LOG, "the input path is directory, can not save in it");
                        return FAIL_TO_CREATE_FILE;
                    } else {
                        File parentFile = file.getParentFile();
                        if (!parentFile.exists()) {
                            parentFile.mkdirs();
                        }
                        try {
                            file.createNewFile();
                            return CREATE_NEW_FILE;
                        } catch (IOException e) {
                            return FAIL_TO_CREATE_FILE;
                        }
                    }
                } else {
                    if (file.isDirectory()) {
                        Log.i(LOG, "the input path is directory, can not save in it");
                        return FAIL_TO_CREATE_FILE;
                    } else if (file.isFile()){
                        return ALREALY_EXIST_FILE;
                    }
                }
            } else {
                Log.i(LOG, "the input path is empty");
                return FAIL_TO_CREATE_FILE;
            }
        } else {
            Log.i(LOG, "SD card is not available");
            return FAIL_TO_CREATE_FILE;
        }
        return FAIL_TO_CREATE_FILE;
    }

    private boolean isSdCardStateValid (){
        String sdCardState = Environment.getExternalStorageState();
        if (sdCardState != null && sdCardState.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public void clear() {
        mHandler.removeCallbacksAndMessages(null);
    }


    public interface FileManagerDelegate {
        void onSuccessFetch(Map<Integer, List<CampaignInfo>> result);

        void failToFetch(String message);
    }





}
