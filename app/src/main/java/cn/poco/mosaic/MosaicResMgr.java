package cn.poco.mosaic;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;

import cn.poco.MaterialMgr2.MgrUtils;
import cn.poco.image.PocoOilMask;
import cn.poco.resource.BaseRes;
import cn.poco.resource.MosaicRecommendResMgr2;
import cn.poco.resource.MosaicRes;
import cn.poco.resource.RecommendRes;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.widget.recycle.RecommendAdapter;
import my.beautyCamera.R;

/**
 * Created by lgd on 2017/7/3.
 */

public class MosaicResMgr {

    public static ArrayList<RecommendAdapter.ItemInfo> getMosaicRes(Context context) {
        ArrayList<RecommendAdapter.ItemInfo> out = new ArrayList<RecommendAdapter.ItemInfo>();
        RecommendAdapter.ItemInfo tempInfo;

        ArrayList<RecommendRes> arr = null;


        Object[] logos = null;
        String[] names = null;
        boolean isHasRecommend = false;
        //下载按钮
        tempInfo = new RecommendAdapter.DownloadItemInfo();
        ((RecommendAdapter.DownloadItemInfo) tempInfo).setNum(cn.poco.resource.MosaicResMgr2.getInstance().GetNoDownloadedCount(context));
        ArrayList<RecommendRes> tempArr = MosaicRecommendResMgr2.getInstance().sync_ar_GetCloudCacheRes(context, null);
        if (tempArr != null && tempArr.size() > 0) {
            arr = new ArrayList<>();
            for (RecommendRes temp : tempArr) {
                if (MgrUtils.hasDownloadMosaic(context, temp.m_id) == 0 && TagMgr.CheckTag(context, Tags.ADV_RECO_MOSAIC_FLAG + temp.m_id)) {
                    arr.add(temp);
                }
            }
            int len = arr.size();
            if (len > 0) {
                logos = new Object[len];
                names = new String[len];
                for (int i = 0; i < len; i++) {
                    RecommendRes recommendRes = arr.get(i);
                    Object thumb = recommendRes.m_thumb;
                    if (thumb == null && !TextUtils.isEmpty(recommendRes.url_thumb)) {
                        thumb = recommendRes.url_thumb;
                    }
                    logos[i] = thumb;
                    names[i] = recommendRes.m_name;
                }
                isHasRecommend = true;
            }
        }
        ((RecommendAdapter.DownloadItemInfo) tempInfo).setLogos(logos);
        tempInfo.m_ex = arr;
        out.add(tempInfo);
        //推荐按钮
        if(isHasRecommend) {
            tempInfo = new RecommendAdapter.RecommendItemInfo();
            tempInfo.m_ex = arr;
            ((RecommendAdapter.RecommendItemInfo) tempInfo).setLogo(logos[0], names[0]);
            out.add(tempInfo);
        }

        for (MosaicRes res : cn.poco.resource.MosaicResMgr2.getInstance().GetResArr()) {
            if (res != null) {
                tempInfo = new RecommendAdapter.ItemInfo();
                tempInfo.m_canDrag = true;
                tempInfo.m_logo = res.m_thumb;
                tempInfo.m_name = res.m_name;
                tempInfo.m_uri = res.m_id;
                tempInfo.m_ex = res;
                switch (res.m_type) {
                    case BaseRes.TYPE_NETWORK_URL:
                        //没下载,排队中,下载中
                        tempInfo.m_style = RecommendAdapter.ItemInfo.Style.NEED_DOWNLOAD;
                        break;
                    case BaseRes.TYPE_LOCAL_RES:
                    case BaseRes.TYPE_LOCAL_PATH:
                    default:
                        tempInfo.m_style = RecommendAdapter.ItemInfo.Style.NORMAL;
                        break;
                }
                if (cn.poco.resource.MosaicResMgr2.getInstance().IsNewRes(tempInfo.m_uri)) {
                    tempInfo.m_style = RecommendAdapter.ItemInfo.Style.NEW;
                }
                out.add(tempInfo);
            }
        }

        return out;
    }

    public static ArrayList<RecommendAdapter.ItemInfo> getPaint2Res(Context context) {
        ArrayList<RecommendAdapter.ItemInfo> out = new ArrayList<>();
        RecommendAdapter.ItemInfo info;
        int res[];

        info = new RecommendAdapter.ItemInfo();
        info.m_uri = 1066808;
        info.m_name = context.getString(R.string.mosaicpage_paint_vangogh);
        info.m_logo = R.drawable.mosaic_page_vangogh_thumb;
        res = new int[2];
        res[0] = R.drawable.mosaic_page_vangogh_icon;
        res[1] = PocoOilMask.Vangogh;
        info.m_ex = res;
        out.add(info);

        info = new RecommendAdapter.ItemInfo();
        info.m_uri = 1066809;
        info.m_name = context.getString(R.string.mosaicpage_paint_crayon);
        info.m_logo = R.drawable.mosaic_page_crayon_thumb;
        res = new int[2];
        res[0] = R.drawable.mosaic_page_crayon_icon;
        res[1] = PocoOilMask.Crayon;
        info.m_ex = res;
        out.add(info);

        info = new RecommendAdapter.ItemInfo();
        info.m_uri = 1066810;
        info.m_name = context.getString(R.string.mosaicpage_paint_charcoal);
        info.m_logo = R.drawable.mosaic_page_charcoal_thumb;
        res = new int[2];
        res[0] = R.drawable.mosaic_page_charcoal_icon;
        res[1] = PocoOilMask.Charcoal;
        info.m_ex = res;
        out.add(info);

        info = new RecommendAdapter.ItemInfo();
        info.m_uri = 1066811;
        info.m_name = context.getString(R.string.mosaicpage_paint_splash);
        info.m_logo = R.drawable.mosaic_page_splash_thumb;
        res = new int[2];
        res[0] = R.drawable.mosaic_page_splash_icon;
        res[1] = PocoOilMask.Splash;
        info.m_ex = res;
        out.add(info);

        info = new RecommendAdapter.ItemInfo();
        info.m_uri = 1066812;
        info.m_name = context.getString(R.string.mosaicpage_paint_sketches1);
        info.m_logo = R.drawable.mosaic_page_sketches01_thumb;
        res = new int[2];
        res[0] = R.drawable.mosaic_page_sketches01_icon;
        res[1] = PocoOilMask.Sketches01;
        info.m_ex = res;
        out.add(info);

        info = new RecommendAdapter.ItemInfo();
        info.m_uri = 1066813;
        info.m_name = context.getString(R.string.mosaicpage_paint_sketches2);
        info.m_logo = R.drawable.mosaic_page_sketches02_thumb;
        res = new int[2];
        res[0] = R.drawable.mosaic_page_sketches02_icon;
        res[1] = PocoOilMask.Sketches02;
        info.m_ex = res;
        out.add(info);

        info = new RecommendAdapter.ItemInfo();
        info.m_uri = 1066814;
        info.m_name = context.getString(R.string.mosaicpage_paint_sketches3);
        info.m_logo = R.drawable.mosaic_page_sketches03_thumb;
        res = new int[2];
        res[0] = R.drawable.mosaic_page_sketches03_icon;
        res[1] = PocoOilMask.Sketches03;
        info.m_ex = res;
        out.add(info);

        info = new RecommendAdapter.ItemInfo();
        info.m_uri = 1066815;
        info.m_name = context.getString(R.string.mosaicpage_paint_sketches4);
        info.m_logo = R.drawable.mosaic_page_sketches04_thumb;
        res = new int[2];
        res[0] = R.drawable.mosaic_page_sketches04_icon;
        res[1] = PocoOilMask.Sketches04;
        info.m_ex = res;
        out.add(info);

        //虽然在画风的分组里，但用的是涂鸦的技术实现
        info = new RecommendAdapter.ItemInfo();
        info.m_uri = 1066816;
        info.m_name = context.getString(R.string.mosaicpage_paint_mosaic);
        info.m_logo = R.drawable.mosaic_page_basic_thumb;

        MosaicRes mosaicBasicRes = new MosaicRes();
        mosaicBasicRes.m_id = 1066816;
        mosaicBasicRes.m_name = context.getString(R.string.mosaicpage_paint_mosaic);
        mosaicBasicRes.m_icon = R.drawable.mosaic_page_basic_icon;
        mosaicBasicRes.m_paintType = MosaicRes.PAINT_TYPE_FILL;
        mosaicBasicRes.horizontal_fill = MosaicRes.POS_CENTER;
        mosaicBasicRes.vertical_fill = MosaicRes.POS_CENTER;
        mosaicBasicRes.m_tjId = 1066816;
        info.m_ex = mosaicBasicRes;
        out.add(info);

        return out;
    }


    public static int getResFirstUri(ArrayList<RecommendAdapter.ItemInfo> arr) {
        int out = RecommendAdapter.ItemInfo.URI_NONE;
        if (arr != null) {
            for (RecommendAdapter.ItemInfo info : arr) {
                if (info.m_uri != RecommendAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI && info.m_uri != RecommendAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI) {
                    out = info.m_uri;
                    break;
                }
            }
        }
        return out;
    }

}
