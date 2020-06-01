package cn.poco.dynamicSticker.v2;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.poco.dynamicSticker.StickerSound;
import cn.poco.dynamicSticker.StickerSoundRes;
import cn.poco.dynamicSticker.StickerType;
import cn.poco.dynamicSticker.TypeValue;
import cn.poco.resource.BaseRes;
import cn.poco.resource.FilterRes;
import cn.poco.utils.FileUtil;
import cn.poco.zip.Zip;

import static cn.poco.utils.FileUtil.getSDString;

/**
 * @author lmx
 *         Created by lmx on 2017/5/17.
 */

public class StickerJsonParse {

    /**
     * zip包 ：/storage/emulated/0/beautyCamera/appdata/resource/video_face/1000/8b406a2d97a7d2a5205e50a54b48641b2017051810.img
     * zip目录：/storage/emulated/0/beautyCamera/appdata/resource/video_face/1000/.zip/8b406a2d97a7d2a5205e50a54b48641b2017051810（隐藏）
     */

    //zip包 主json
    private static final String BASE_STICKER_FILE_NAME = "sticker.json";

    //zip包 滤镜json
    private static final String BASE_FILTER_FILE_NAME = "filter.json";

    //隐藏zip目录
    public static final String BASE_ZIP_FOLDER_NAME = ".zip";

    private static final String TAG = "StickerJsonParse";


    /**
     * @param folderPath zip包所在目录(.../id)
     * @param zipName    zip包名（xxx.img）
     * @param isDelete   解压后是否删除该zip
     * @return
     */
    public static StickerRes parseZipRes(@NonNull String folderPath, @NonNull String zipName, boolean isDelete) {
        ParseInfo parseInfo = new ParseInfo();
        StickerRes stickerRes = null;

        long start = System.currentTimeMillis();

        if (TextUtils.isEmpty(folderPath) || TextUtils.isEmpty(zipName)) {
            parseInfo.m_status = ParseInfo.STATUS_FOLDER_NULL;
            //Log.e(TAG, "parseZipRes: zipName or folderPath is null" + zipName + "\n " + folderPath);
            return null;
        }


        File zipFile = new File(folderPath);
        if (!zipFile.exists())//目标路径不存在
        {
            boolean mkdirs = zipFile.mkdirs();
            if (mkdirs) {
                //Log.d(TAG, "parseZipRes: mkdir success " + folderPath);
            }
        }

        String zipIDFileName = folderPath + File.separator + zipName;
        zipFile = new File(zipIDFileName);
        if (!zipFile.exists())//不存在zip包
        {
            parseInfo.m_status = ParseInfo.STATUS_ZIP_NULL;
            //Log.e(TAG, "parseZipRes: zipFile is no exists" + zipIDFileName);
            return null;
        }


        //解压后存放的目录（隐藏）
        String upzipFolderPath = folderPath + File.separator + BASE_ZIP_FOLDER_NAME;
        File upzipFolderPathFile = new File(upzipFolderPath);
        boolean mkdirs = false;
        if (!upzipFolderPathFile.exists()) {
            mkdirs = upzipFolderPathFile.mkdirs();
            //Log.d(TAG, "StickerJsonParse --> parseZipRes: upzipFolderPath : " + mkdirs);
        } else {
            //已在隐藏目录，删除该目录下的文件
            mkdirs = true;
            FileUtil.deleteSDFile(upzipFolderPath, false);
        }

        boolean isupZip = false;
        try {
            start = System.currentTimeMillis();
            parseInfo.m_status = ParseInfo.STATUS_ZIP_START;
            //解压
            Zip.UnZipFolder(zipIDFileName, upzipFolderPath, false);
            if (isDelete) {
                boolean delete = zipFile.delete();
                if (delete) {
                    //Log.d(TAG, "parseZipRes: delete zip success");
                }
            }
            parseInfo.m_status = ParseInfo.STATUS_ZIP_SUCCESS;
            //Log.d(TAG, "parseZipRes: unzip :" + (System.currentTimeMillis() - start));
            isupZip = true;
        } catch (Exception e) {
            parseInfo.m_status = ParseInfo.STATUS_ZIP_FAIL;
            parseInfo.m_throwable = e;
            e.printStackTrace();
            isupZip = false;
        }


        if (isupZip) {
            start = System.currentTimeMillis();

            //8b406a2d97a7d2a5205e50a54b48641b2017051810.img(.zip)
            String zipId = null;
            String[] split = zipName.split("\\.");
            if (split.length > 1) {
                zipId = split[0];
            }

            // /storage/emulated/0/beautyCamera/appdata/resource/video_face/1000/.zip/8b406a2d97a7d2a5205e50a54b48641b2017051810
            String scrZipPathName = upzipFolderPath + File.separator + zipId;

            //解析zip数据
            stickerRes = parseZipFolder(scrZipPathName);

            if (stickerRes == null) {
                parseInfo.m_status = ParseInfo.STATUS_PARSE_ERROR;
                parseInfo.m_ex = null;
                //Log.e(TAG, "parseZipRes: parse zip stickerRes is null");
            } else {
                parseInfo.m_status = ParseInfo.STATUS_PARSE_SUCCESS;
                parseInfo.m_ex = stickerRes;
            }

            //Log.d(TAG, "parseZipRes: " + (System.currentTimeMillis() - start));
        }
        return stickerRes;
    }


    /**
     * @param zipIDFileName zip包解压后目录地址（.../id/.zip/zip_id）
     */
    public static StickerRes parseZipFolder(@NonNull String zipIDFileName) {
        StickerRes stickerRes = null;
        if (TextUtils.isEmpty(zipIDFileName)) {
            //Log.e(TAG, "parseZipRes: zipIDFileName is null" + zipIDFileName);
            return null;
        }

        File zipFile = new File(zipIDFileName);
        if (!zipFile.exists()) {
            //Log.e(TAG, "parseZipRes: zipFile is no exists" + zipIDFileName);
            return null;
        }

        // .../id/.zip_id/sticker.json
        String stickJsonFileName = zipIDFileName + File.separator + BASE_STICKER_FILE_NAME;
        zipFile = new File(stickJsonFileName);
        if (!zipFile.exists()) {
            //Log.e(TAG, "parseZipRes: no exists in " + stickJsonFileName);
            return null;
        }

        String jsonContent = getSDString(stickJsonFileName);

        //解析sticker.json
        stickerRes = parseStickerJson(zipIDFileName, jsonContent);
        if (stickerRes != null) {
            //解析subres的json
            parseStickSubRes(zipIDFileName, stickerRes);

            //解析filter的json
            String filterJsonFileName = zipIDFileName + File.separator + BASE_FILTER_FILE_NAME;
            zipFile = new File(filterJsonFileName);
            if (zipFile.exists()) {
                stickerRes.mFilterRes = parseFilterJson(zipIDFileName, getSDString(filterJsonFileName));
                if (stickerRes.mFilterRes != null)
                {
                    stickerRes.mFilterRes.m_isStickerFilter = true;
                }
                if (stickerRes.mFilterRes != null && stickerRes.mFilterRes.m_id == BaseRes.NONE_ID)
                {

                    if (!TextUtils.isEmpty(stickerRes.mId))
                    {
                        stickerRes.mFilterRes.m_id = Integer.parseInt(stickerRes.mId);
                    }
                    else
                    {
                        stickerRes.mFilterRes.m_id = (int) (Math.random() * 10000000);
                    }
                }
            }
        }
        return stickerRes;
    }

    /**
     * sticker.json
     *
     * @param zipIDFileName zip包完整目录地址（.../id/.zip/zip_id）
     * @param json          InputStream / String(json)
     * @return
     */
    public static StickerRes parseStickerJson(@NonNull String zipIDFileName, Object json) {
        String jsonString = null;
        StickerRes out = null;

        if (json == null) return null;
        if (json instanceof InputStream) {
            jsonString = readJsonString((InputStream) json);
        } else if (json instanceof String) {
            jsonString = (String) json;
        }

        if (!TextUtils.isEmpty(jsonString)) {
            try {
                JSONObject stickJson = new JSONObject(jsonString);
                String temp = null;
                String action = null;
                boolean is3DRes = false;

                if (stickJson.has("res")) {
                    JSONArray resArr = stickJson.getJSONArray("res");
                    if (resArr != null) {
                        HashMap<String, StickerSubRes> subResHashMap = null;
                        StickerSubRes subRes = null;

                        for (int i = 0, size = resArr.length(); i < size; i++) {
                            JSONObject resJson = resArr.getJSONObject(i);
                            if (resJson == null) {
                                continue;
                            }
                            if (resJson.has("type")) {
                                int layer = -1;
                                temp = resJson.getString("type");
                                if (TextUtils.isEmpty(temp)) {
                                    continue;
                                } else if (temp.startsWith(StickerType.Face3D)) {
                                    layer = StickerType.getLayer(StickerType.Face3D);
                                    is3DRes = true;
                                } else if (temp.startsWith(StickerType.Face))//可能多人脸不同素材
                                {
                                    layer = StickerType.getLayer(StickerType.Face);
                                } else if (temp.startsWith(StickerType.Head)) {
                                    layer = StickerType.getLayer(StickerType.Head);
                                } else if (temp.startsWith(StickerType.Ear)) {
                                    layer = StickerType.getLayer(StickerType.Ear);
                                } else if (temp.startsWith(StickerType.Eye)) {
                                    layer = StickerType.getLayer(StickerType.Eye);
                                } else if (temp.startsWith(StickerType.Nose)) {
                                    layer = StickerType.getLayer(StickerType.Nose);
                                } else if (temp.startsWith(StickerType.Mouth)) {
                                    layer = StickerType.getLayer(StickerType.Mouth);
                                } else if (temp.startsWith(StickerType.Chin)) {
                                    layer = StickerType.getLayer(StickerType.Chin);
                                } else if (temp.startsWith(StickerType.Shoulder)) {
                                    layer = StickerType.getLayer(StickerType.Shoulder);
                                } else if (temp.startsWith(StickerType.Foreground)) {
                                    layer = StickerType.getLayer(StickerType.Foreground);
                                } else if (temp.startsWith(StickerType.Frame)) {
                                    layer = StickerType.getLayer(StickerType.Frame);
                                } else if (temp.startsWith(StickerType.Full)) {//用于测试
                                    layer = StickerType.getLayer(StickerType.Full);
                                } else {
                                    continue;
                                }

                                if (layer != -1) {
                                    subRes = new StickerSubRes();
                                    subRes.setTypeName(temp);
                                    subRes.setLayer(layer);
                                }
                            }
                            if (subRes == null) {
                                continue;
                            }

                            if (resJson.has("gif")) {
                                subRes.setGifEnable(resJson.getBoolean("gif"));
                            }

                            if (resJson.has("tier")) {
                                subRes.setTier(resJson.getInt("tier"));
                            }

                            if (resJson.has("scale")) {
                                subRes.setScale((float) resJson.getDouble("scale"));
                            }

                            if (resJson.has("sfull")) {//用于测试
                                subRes.setSFullEnable(resJson.getBoolean("sfull"));
                            }
                            if (resJson.has("offset")) {
                                JSONArray offsetArr = resJson.getJSONArray("offset");
                                if (offsetArr != null) {
                                    for (int j = 0, s = offsetArr.length(); j < s; j++) {
                                        float offset = (float) offsetArr.getDouble(j);
                                        if (j == 0) {
                                            subRes.setOffsetX(offset);
                                        } else {
                                            subRes.setOffsetY(offset);
                                        }
                                    }
                                }
                            }

                            if (resJson.has("gif_offset")) {
                                JSONArray gifOffsetArr = resJson.getJSONArray("gif_offset");
                                if (gifOffsetArr != null) {
                                    int len = gifOffsetArr.length();
                                    if (len > 0) {
                                        float[] gifOffsets = new float[len];
                                        for (int j = 0; j < len; j++) {
                                            gifOffsets[j] = (float) gifOffsetArr.getDouble(j);
                                        }
                                        subRes.setGifOffset(gifOffsets);
                                    }
                                }
                            }

                            //是否支持4:3比例
                            if (resJson.has("s34")) {
                                subRes.setS43Enable(resJson.getBoolean("s34"));
                            }

                            //4:3坐标偏移量
                            if (resJson.has("s34_offset")) {
                                JSONArray s34_offset = resJson.getJSONArray("s34_offset");
                                if (s34_offset != null) {
                                    int length = s34_offset.length();
                                    if (length > 0) {
                                        float[] s34_offsets = new float[length];
                                        for (int j = 0; j < length; j++) {
                                            s34_offsets[j] = (float) s34_offset.getDouble(j);
                                        }
                                        subRes.setS43Offset(s34_offsets);
                                    }
                                }
                            }

                            //是否支持9:16比例   height/width
                            if (resJson.has("s169")) {
                                subRes.setS916Enable(resJson.getBoolean("s169"));
                            }
                            //9:16偏移
                            if (resJson.has("s169_offset")) {
                                JSONArray s169_offset = resJson.getJSONArray("s169_offset");
                                if (s169_offset != null) {
                                    int length = s169_offset.length();
                                    if (length > 0) {
                                        float[] s169_offsets = new float[length];
                                        for (int j = 0; j < length; j++) {
                                            s169_offsets[j] = (float) s169_offset.getDouble(j);
                                        }
                                        subRes.setS916Offset(s169_offsets);
                                    }
                                }
                            }

                            if (resJson.has("action")) {
                                temp = resJson.getString("action");
                                if (!TextUtils.isEmpty(temp)) {
                                    subRes.setAction(temp);
                                    action = temp;
                                }
                            }

                            if (resJson.has("layerCompositeMode")) {
                                temp = resJson.getString("layerCompositeMode");
                                if (!TextUtils.isEmpty(temp)) {
                                    subRes.setLayerCompositeMode(Integer.parseInt(temp));
                                }
                            }

                            if (resJson.has("layerOpaqueness")) {
                                temp = resJson.getString("layerOpaqueness");
                                if (!TextUtils.isEmpty(temp)) {
                                    subRes.setLayerOpaqueness(Float.parseFloat(temp));
                                }
                            }

                            if (resJson.has("i")) {
                                JSONArray iArr = resJson.getJSONArray("i");
                                if (iArr != null) {
                                    // "[stbtk511head1.json，stbtk511head2.json}"
                                    ArrayList<String> iNames = null;
                                    ArrayList<String> modelData = null;
                                    for (int j = 0, s = iArr.length(); j < s; j++) {
                                        temp = iArr.getString(j);
                                        if (TextUtils.isEmpty(temp)) {
                                            continue;
                                        }
                                        if (temp.endsWith(".json")) {
                                            if (iNames == null) {
                                                iNames = new ArrayList<>();
                                            }
                                            // .../id/zip_id/stbtk511head1.json
                                            iNames.add(zipIDFileName + File.separator + temp);
                                        } else {
                                            if (modelData == null) {
                                                modelData = new ArrayList<>();
                                            }
                                            //3d模型文件
                                            // .../id/zip_id/xx.fbx(xx.obj/xx.mtl)
                                            modelData.add(zipIDFileName + File.separator + temp);
                                        }
                                    }
                                    if (iNames != null) {
                                        subRes.setJsonNames(iNames);
                                    }
                                    if (modelData != null) {
                                        subRes.set3DModelData(modelData);
                                    }
                                }
                            }

                            if (resJson.has("d")) {
                                JSONArray dArr = resJson.getJSONArray("d");
                                if (dArr != null) {
                                    // "[stbtk511head1.png,stbtk511head2.png]"
                                    ArrayList<String> dNames = null;
                                    ArrayList<String> modelData = null;

                                    boolean needAddModel = true;//兼容4.1.0版本
                                    if (subRes.get3DModelData() != null) {
                                        needAddModel = false;
                                    }

                                    for (int j = 0, s = dArr.length(); j < s; j++) {
                                        temp = dArr.getString(j);
                                        if (TextUtils.isEmpty(temp)) {
                                            continue;
                                        }
                                        if (temp.endsWith(".png") || temp.endsWith(".jpg")) {
                                            if (dNames == null) {
                                                dNames = new ArrayList<>();
                                            }
                                            // .../id/zip_id/stbtk511head1.png
                                            dNames.add(zipIDFileName + File.separator + temp);

                                        } else if (needAddModel) {
                                            if (modelData == null) {
                                                modelData = new ArrayList<>();
                                            }
                                            //3d模型文件
                                            // .../id/zip_id/xx.fbx(xx.obj/xx.mtl)
                                            modelData.add(zipIDFileName + File.separator + temp);
                                        }
                                    }
                                    if (modelData != null) {
                                        subRes.set3DModelData(modelData);
                                    }
                                    if (dNames != null) {
                                        subRes.setImgNames(dNames);
                                    }
                                }
                            }

                            if (resJson.has("d2")) {
                                JSONArray dArr = resJson.getJSONArray("d2");
                                if (dArr != null) {
                                    // "[[0_stbtk511head1.png,0_stbtk511head2.png],[1_stbtk511head1.png,1_stbtk511head2.png]]"
                                    JSONArray subArr = null;
                                    ArrayList<StickerSubResARGroupItemImg> arGroupItemImgs = null;
                                    StickerSubResARGroupItemImg itemImg = null;
                                    for (int j = 0, s = dArr.length(); j < s; j++) {
                                        subArr = dArr.getJSONArray(j);
                                        if (subArr == null) {
                                            continue;
                                        }
                                        itemImg = null;
                                        for (int k = 0; k < subArr.length(); k++) {
                                            temp = subArr.getString(k);
                                            if (TextUtils.isEmpty(temp)) {
                                                continue;
                                            }
                                            // .../id/zip_id/0_stbtk511head1.png
                                            if (itemImg == null) {
                                                itemImg = new StickerSubResARGroupItemImg();
                                                itemImg.setItemIndex(j);
                                            }
                                            itemImg.addImg(zipIDFileName + File.separator + temp);
                                        }
                                        if (itemImg != null) {
                                            if (arGroupItemImgs == null) {
                                                arGroupItemImgs = new ArrayList<>();
                                            }
                                            arGroupItemImgs.add(itemImg);
                                        }
                                    }
                                    if (arGroupItemImgs != null) {
                                        subRes.setStickerSubResARGroupItemImgs(arGroupItemImgs);
                                    }
                                }
                            }

                            if (resJson.has("sub_type")) {
                                try {
                                    subRes.setSubType(resJson.getInt("sub_type"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (resJson.has("desc")) {
                                //3d desc
                                JSONArray arr = resJson.getJSONArray("desc");
                                if (arr != null) {
                                    ArrayList<StickerSubResDesc> descList = null;
                                    StickerSubResDesc subResDesc = null;
                                    JSONObject descObj = null;
                                    for (int j = 0; j < arr.length(); j++) {
                                        descObj = arr.getJSONObject(j);
                                        if (descObj == null) continue;

                                        if (descObj.has("type")) {
                                            temp = descObj.getString("type");
                                            if (!TextUtils.isEmpty(temp)) {
                                                subResDesc = new StickerSubResDesc();
                                                subResDesc.setType(temp);

                                                if (descObj.has("a")) {
                                                    subResDesc.setA(descObj.getInt("a"));
                                                }
                                                if (descObj.has("b")) {
                                                    subResDesc.setB(descObj.getInt("b"));
                                                }
                                                if (descList == null) {
                                                    descList = new ArrayList<>();
                                                }
                                                descList.add(subResDesc);
                                            }
                                        }
                                    }

                                    if (descList != null) {
                                        subRes.setStickerSubResDescs(descList);
                                    }
                                }
                            }

                            if (subResHashMap == null) {
                                subResHashMap = new HashMap<String, StickerSubRes>();
                            }
                            subResHashMap.put(subRes.getTypeName(), subRes);
                        }

                        if (subResHashMap != null) {
                            out = new StickerRes();
                            out.setStickerSubRes(subResHashMap);
                        }
                    }
                }

                if (out == null) return null;

                out.mIs3DRes = is3DRes;

                if (!TextUtils.isEmpty(action)) {
                    out.mAction = action;
                }

                if (stickJson.has("id")) {
                    temp = stickJson.getString("id");
                    if (!TextUtils.isEmpty(temp)) {
                        out.mId = temp;
                    }
                }

                if (stickJson.has("name")) {
                    temp = stickJson.getString("name");
                    if (!TextUtils.isEmpty(temp)) {
                        out.mName = temp;
                    }
                }

                if (stickJson.has("sw")) {
                    out.mSWidth = stickJson.getInt("sw");
                }
                if (stickJson.has("sh")) {
                    out.mSHeight = stickJson.getInt("sh");
                }

                //sound effect
                if (stickJson.has("music")) {
                    JSONArray musicArr = stickJson.getJSONArray("music");
                    if (musicArr != null) {
                        int len = musicArr.length();
                        StickerSoundRes soundRes = null;
                        if (len > 0) {
                            soundRes = new StickerSoundRes();
                        }

                        for (int i = 0; i < len; i++) {
                            JSONObject musicObj = musicArr.getJSONObject(i);
                            if (musicObj != null) {
                                StickerSound sound = new StickerSound();

                                //type
                                if (musicObj.has("type")) {
                                    temp = musicObj.getString("type");
                                    TypeValue.SoundType soundType = TypeValue.SoundType.HasType(temp);
                                    if (soundType != null) {
                                        sound.setSoundType(soundType);
                                    }
                                    sound.setType(temp);
                                } else {
                                    sound.setSoundType(TypeValue.SoundType.NONE);
                                }

                                //d
                                if (musicObj.has("d")) {
                                    temp = musicObj.getString("d");
                                    sound.setResourceName(temp);
                                    if (!TextUtils.isEmpty(temp)) {
                                        sound.setResourcePath(zipIDFileName + File.separator + temp);
                                    }
                                }

                                //delay
                                if (musicObj.has("delay")) {
                                    double delay = musicObj.getDouble("delay");
                                    delay *= 1000D;//单位毫秒
                                    sound.setDelayDuration(delay);
                                }

                                //action
                                if (musicObj.has("action")) {
                                    temp = musicObj.getString("action");
                                    sound.setActionTriggerType(TypeValue.TriggerType.HasType(temp));
                                    sound.setActionTrigger(TypeValue.TriggerType.IsExistAction(temp));
                                    sound.setAction(temp);
                                }

                                //solo
                                if (musicObj.has("solo")) {
                                    boolean solo = musicObj.getBoolean("solo");
                                    sound.setSolo(solo);
                                }

                                //bgm_continue
                                if (musicObj.has("bgm_continue")) {
                                    boolean bgm_continue = musicObj.getBoolean("bgm_continue");
                                    sound.setBgmContinue(bgm_continue);
                                }

                                if (soundRes != null) {
                                    soundRes.add(sound);
                                }
                            }
                        }
                        if (soundRes != null) {
                            out.setStickerSoundRes(soundRes);
                        }
                    }
                }

                if (stickJson.has("splitScreen")) {//分屏数据
                    JSONObject obj = null;
                    try {
                        obj = stickJson.getJSONObject("splitScreen");
                    } catch (JSONException e) {
                        //e.printStackTrace();
                    }
                    if (obj != null) {
                        StickerSplitScreen stickerSplitScreen = new StickerSplitScreen();
                        if (obj.has("type")) {
                            stickerSplitScreen.setType(obj.getInt("type"));
                        }
                        if (obj.has("action")) {
                            stickerSplitScreen.setAction(obj.getString("action"));
                            if (!TextUtils.isEmpty(stickerSplitScreen.getAction())) {
                                out.mAction = stickerSplitScreen.getAction();
                            }
                        }
                        if (obj.has("d")) {
                            JSONArray arr = obj.getJSONArray("d");
                            if (arr != null) {
                                ArrayList<StickerSplitScreen.SplitData> mSplitDatas = new ArrayList<>();
                                StickerSplitScreen.SplitData splitData = null;
                                for (int i = 0; i < arr.length(); i++) {
                                    obj = arr.getJSONObject(i);
                                    if (obj != null) {
                                        splitData = new StickerSplitScreen.SplitData();
                                        if (obj.has("s")) {
                                            splitData.s = obj.getInt("s");
                                            splitData.calculateRowColumn();
                                        }
                                        if (obj.has("from")) {
                                            splitData.from = obj.getInt("from");
                                        }
                                        if (obj.has("count")) {
                                            splitData.count = obj.getInt("count");
                                        }
                                        if (obj.has("mask")) {//色块数据
                                            JSONArray subArr = obj.getJSONArray("mask");
                                            if (splitData.s > 0 && subArr != null) {
                                                splitData.maskIndex = new int[splitData.s];
                                                Arrays.fill(splitData.maskIndex, -1);

                                                StickerSplitScreen.MaskData[] maskDataArr = new StickerSplitScreen.MaskData[subArr.length()];
                                                JSONObject subObj = null;
                                                StickerSplitScreen.MaskData maskData = null;
                                                for (int j = 0; j < subArr.length(); j++) {
                                                    subObj = subArr.getJSONObject(j);
                                                    if (subObj != null) {
                                                        maskData = new StickerSplitScreen.MaskData();
                                                        if (subObj.has("p")) {
                                                            maskData.pic = zipIDFileName + File.separator + subObj.getString("p");
                                                        }
                                                        if (subObj.has("a")) {
                                                            try {
                                                                JSONArray subArr1 = subObj.getJSONArray("a");
                                                                if (subArr1 != null) {
                                                                    int index = -1;
                                                                    for (int k = 0; k < subArr1.length(); k++) {
                                                                        index = subArr1.getInt(k) - 1;//index = 0,1,2....
                                                                        if (index >= 0 && index < splitData.maskIndex.length) {
                                                                            splitData.maskIndex[index] = j;
                                                                        }
                                                                    }
                                                                }
                                                            } catch (Throwable e) {
                                                            }
                                                        }
                                                        if (subObj.has("layerCompositeMode")) {
                                                            maskData.compositeMode = subObj.getInt("layerCompositeMode");
                                                        }
                                                        if (subObj.has("layerOpaqueness")) {
                                                            int opaqueness = subObj.getInt("layerOpaqueness");
                                                            if (opaqueness > 0 && opaqueness <= 100) {
                                                                maskData.opaqueness = opaqueness / 100.f;
                                                            }
                                                        }
                                                        maskDataArr[j] = maskData;
                                                    }
                                                }
                                                splitData.maskData = maskDataArr;
                                            }
                                        }

                                        mSplitDatas.add(splitData);
                                    }
                                }
                                stickerSplitScreen.setSplitDatas(mSplitDatas);
                            }
                        }
                        out.mStickerSplitScreen = stickerSplitScreen;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        return out;
    }


    /**
     * @param zipIDFileName zip包解压后目录地址（.../id/.zip/zip_id）
     * @param stickerRes
     */
    public static void parseStickSubRes(@NonNull String zipIDFileName, @NonNull StickerRes stickerRes) {
        if (stickerRes == null || stickerRes.mOrderStickerRes == null) return;

        StickerSubRes subRes = null;
        for (Map.Entry<String, StickerSubRes> entry : stickerRes.mOrderStickerRes) {
            if (entry == null || (subRes = entry.getValue()) == null) {
                continue;
            }

            if (subRes.mStickerMetas != null) {
                continue;//已解析过json
            }

            ArrayList<String> jsonNames = subRes.getJsonNames();
            if (jsonNames == null || jsonNames.isEmpty()) {
                continue;
            }

            StickerMeta stickerMeta = null;
            ArrayList<StickerSpriteFrame> stickerSpriteFrames = null;

            int startIndex = 0;
            for (String fileName : jsonNames)//face type json parse
            {
                if (TextUtils.isEmpty(fileName)) {
                    continue;
                }

                String jsonContent = FileUtil.getSDString(fileName); //NOTE 注意字符串编码格式问题
                if (TextUtils.isEmpty(jsonContent)) {
                    continue;
                }

                //meta
                stickerMeta = parseStickerMeta(zipIDFileName, jsonContent);
                if (stickerMeta != null) {
                    if (subRes.mStickerMetas == null) {
                        subRes.mStickerMetas = new ArrayList<>();
                    }
                    subRes.mStickerMetas.add(stickerMeta);
                }

                //frame
                int frameCount = 0;
                stickerSpriteFrames = parseStickerSpriteFrames(jsonContent);
                if (stickerSpriteFrames != null) {
                    if (subRes.mFrames == null) {
                        subRes.mFrames = new ArrayList<>();
                    }

                    startIndex = subRes.mFrames.size();
                    frameCount = stickerSpriteFrames.size();

                    subRes.mFrames.addAll(stickerSpriteFrames);

                }

                if (stickerMeta != null) {
                    stickerMeta.mStartIndex = startIndex;
                    stickerMeta.mFrameCount = frameCount;
                }
                subRes.mAllFrameCount += frameCount;
            }
        }

        //动画最大时长（单位毫秒）
        float maxFrameDurations = stickerRes.calculateMaxDuration();
        maxFrameDurations *= 1000D;
        stickerRes.setMaxFrameDurations((int) maxFrameDurations);
        if (stickerRes.mStickerSoundRes != null && stickerRes.mStickerSoundRes.mStickerSounds != null) {
            for (StickerSound stickerSound : stickerRes.mStickerSoundRes.mStickerSounds) {
                if (stickerSound != null) {
                    stickerSound.setFrameDuration(maxFrameDurations);
                }
            }
        }
    }

    //"meta": {"image": "eye.png","size": {"w": 900,"h": 950}
    public static StickerMeta parseStickerMeta(@NonNull String zipIDFileName, Object json) {
        String jsonString = null;
        StickerMeta out = null;

        if (json == null) return out;
        if (json instanceof InputStream) {
            jsonString = readJsonString((InputStream) json);
        } else if (json instanceof String) {
            jsonString = (String) json;
        }


        if (!TextUtils.isEmpty(jsonString)) {
            out = new StickerMeta();
            try {
                JSONObject jsonObject = new JSONObject(jsonString);

                if (jsonObject.has("meta")) {
                    jsonObject = jsonObject.getJSONObject("meta");

                    if (jsonObject.has("image")) {
                        String temp = jsonObject.getString("image");
                        if (!TextUtils.isEmpty(temp)) {
                            out.mImage = zipIDFileName + File.separator + temp.replaceAll(" ", "");
                        }
                    }

                    if (jsonObject.has("size")) {
                        JSONObject sizeJson = jsonObject.getJSONObject("size");
                        if (sizeJson != null) {
                            if (sizeJson.has("w")) {
                                out.mImgWidth = sizeJson.getInt("w");
                            }
                            if (sizeJson.has("h")) {
                                out.mImgHeight = sizeJson.getInt("h");
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }

        }
        return out;
    }

    //"frames": [{"filename": "nose0000","d": 0.09,"frame": {"x": 2,"y": 2,"w": 62,"h": 104}},,,,]
    public static ArrayList<StickerSpriteFrame> parseStickerSpriteFrames(Object json) {
        ArrayList<StickerSpriteFrame> out = null;
        String jsonString = null;

        if (json == null) return out;
        if (json instanceof InputStream) {
            jsonString = readJsonString((InputStream) json);
        } else if (json instanceof String) {
            jsonString = (String) json;
        }

        if (!TextUtils.isEmpty(jsonString)) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject.has("frames")) {
                    out = new ArrayList<>();
                    StickerSpriteFrame frame = null;
                    JSONArray framesArr = jsonObject.getJSONArray("frames");
                    for (int i = 0, size = framesArr.length(); i < size; i++) {
                        frame = parseStickerSpriteFrame(framesArr.getJSONObject(i));
                        if (frame == null) continue;
                        out.add(frame);
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        return out;
    }

    //{"filename": "eye0032","d": 0.1,"frame": {"x": 532,"y": 676,"w": 247,"h": 60}}
    public static StickerSpriteFrame parseStickerSpriteFrame(JSONObject jsonObject) {
        StickerSpriteFrame out = null;

        if (jsonObject != null) {
            try {
                out = new StickerSpriteFrame();

                //时间戳
                if (jsonObject.has("d")) {
                    out.setDuration((float) jsonObject.getDouble("d"));
                }

                if (jsonObject.has("frame")) {
                    JSONObject frameJson = jsonObject.getJSONObject("frame");
                    if (frameJson != null) {
                        Rect frame = new Rect();
                        if (frameJson.has("x")) {
                            frame.left = frameJson.getInt("x");
                        }
                        if (frameJson.has("y")) {
                            frame.top = frameJson.getInt("y");
                        }
                        if (frameJson.has("w")) {
                            frame.right = frameJson.getInt("w");
                        }
                        if (frameJson.has("h")) {
                            frame.bottom = frameJson.getInt("h");
                        }
                        out.setFrame(frame);
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return out;
    }

    /**
     * filter.json
     *
     * @param zipIDFileName zip包完整目录地址（.../id/.zip/zip_id）
     * @param json          InputStream / String(json)
     * @return
     */
    private static final FilterRes parseFilterJson(@NonNull String zipIDFileName, Object json) {
        FilterRes out = null;
        String jsonString = null;
        if (json == null) return null;
        if (json instanceof InputStream) {
            jsonString = readJsonString((InputStream) json);
        } else if (json instanceof String) {
            jsonString = (String) json;
        }

        if (!TextUtils.isEmpty(jsonString)) {
            try {
                out = new FilterRes();

                JSONObject filterJson = new JSONObject(jsonString);
                String temp = null;
                if (filterJson.has("id")) {
                    temp = filterJson.getString("id");
                    if (!TextUtils.isEmpty(temp)) {
                        out.m_id = Integer.parseInt(temp);
                    } else {
                        out.m_id = (int) (Math.random() * 10000000);
                    }
                }
                temp = null;
                if (filterJson.has("pushID")) {
                    temp = filterJson.getString("pushID");
                    if (!TextUtils.isEmpty(temp)) {
                        out.m_tjId = Integer.parseInt(temp);
                    }
                }
                temp = null;

                if (filterJson.has("name")) {
                    out.m_name = filterJson.getString("name");
                }

                if (filterJson.has("camera")) {
                    out.m_isUpDateToCamera = filterJson.getBoolean("camera");
                }

                if (filterJson.has("alpha")) {
                    temp = filterJson.getString("alpha");
                    if (!TextUtils.isEmpty(temp)) {
                        out.m_filterAlpha = Integer.parseInt(temp);
                    }
                }
                temp = null;

                if (filterJson.has("thumb")) {
                    out.m_thumb = zipIDFileName + File.separator + filterJson.getString("thumb");
                }

                if (filterJson.has("watermark")) {
                    out.m_isHaswatermark = filterJson.getBoolean("watermark");
                }

                if (filterJson.has("vignette")) {
                    out.m_isHasvignette = filterJson.getBoolean("vignette");
                }

                if (filterJson.has("skipFace")) {
                    out.m_isSkipFace = filterJson.getBoolean("skipFace");
                }

                if (filterJson.has("res")) {
                    JSONArray resArr = filterJson.getJSONArray("res");
                    if (resArr != null) {
                        int len = resArr.length();
                        if (len > 0) {
                            FilterRes.FilterData[] filterDatas = new FilterRes.FilterData[len];
                            for (int i = 0; i < len; i++) {
                                filterDatas[i] = new FilterRes.FilterData();
                                Object obj = resArr.get(i);
                                if (obj instanceof JSONObject) {
                                    JSONObject resJson = (JSONObject) obj;
                                    if (resJson.has("img")) {
                                        filterDatas[i].m_res = zipIDFileName + File.separator + resJson.getString("img");

                                    }

                                    if (resJson.has("params")) {
                                        if (resJson.get("params") instanceof JSONArray) {
                                            JSONArray paramsArr = resJson.getJSONArray("params");
                                            int len1 = paramsArr.length();
                                            int[] tempParams = new int[len1];
                                            for (int j = 0; j < len1; j++) {
                                                tempParams[j] = paramsArr.getInt(j);
                                            }
                                            filterDatas[i].m_params = tempParams;
                                        }
                                    }

                                    if (resJson.has("skipFace")) {
                                        filterDatas[i].m_isSkipFace = resJson.getBoolean("skipFace");
                                    }

                                    if (i == 0) {
                                        out.m_isSkipFace = filterDatas[i].m_isSkipFace;
                                    }
                                }
                            }
                            out.m_datas = filterDatas;
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
                out = null;
            }
        }

        return out;
    }


    public static String readJsonString(InputStream inputStream) {
        StringBuffer buffer = new StringBuffer();
        try {
            InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader buffReader = new BufferedReader(reader);
            String line = null;
            while ((line = buffReader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return buffer.toString();
    }


    public static class ParseInfo {
        public static final int STATUS_INIT = 0;
        public static final int STATUS_FOLDER_NULL = 1; //zip包名或目标目录为空
        public static final int STATUS_ZIP_NULL = 1 << 1;//zip包不存在
        public static final int STATUS_ZIP_START = 1 << 2;//解压zip开始
        public static final int STATUS_ZIP_SUCCESS = 1 << 3;//解压成功
        public static final int STATUS_ZIP_FAIL = 1 << 4;//解压失败
        public static final int STATUS_PARSE_ERROR = 1 << 5;//解析异常
        public static final int STATUS_PARSE_SUCCESS = 1 << 6;//解析成功


        public Object m_ex; //out StickerRes
        public int m_status = STATUS_INIT;
        public Throwable m_throwable;

        public Object getEx() {
            return m_ex;
        }

        public void setEx(Object m_ex) {
            this.m_ex = m_ex;
        }

        public int getStatus() {
            return m_status;
        }

        public void setStatus(int m_status) {
            this.m_status = m_status;
        }

        public Throwable getThrowable() {
            return m_throwable;
        }

        public void setThrowable(Throwable m_throwable) {
            this.m_throwable = m_throwable;
        }
    }
}
