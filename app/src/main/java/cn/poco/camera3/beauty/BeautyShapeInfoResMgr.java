package cn.poco.camera3.beauty;

import android.content.Context;

import java.util.ArrayList;

import cn.poco.camera3.beauty.data.BaseShapeResMgr;
import cn.poco.camera3.beauty.data.ShapeDataType;
import cn.poco.camera3.beauty.data.ShapeInfo;
import cn.poco.camera3.beauty.data.ShapeResMgr;
import cn.poco.camera3.beauty.data.ShapeSyncResMgr;
import cn.poco.camera3.beauty.data.SuperShapeData;
import cn.poco.camera3.beauty.recycler.ShapeExAdapter;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2018-01-16.
 */

public class BeautyShapeInfoResMgr
{

    public static <T extends ShapeExAdapter.ShapeExItemInfo> T HasExItemInfo(ArrayList<T> resArr, int id)
    {
        if (resArr != null)
        {
            int len = resArr.size();
            for (int i = 0; i < len; i++)
            {
                if (resArr.get(i).m_uri == id)
                {
                    return resArr.get(i);
                }
            }
        }
        return null;
    }

    public static ArrayList<ShapeExAdapter.ShapeExItemInfo> GetShapeInfoList(Context context)
    {
        ArrayList<ShapeExAdapter.ShapeExItemInfo> out = new ArrayList<>();

        //内置预设数据
        ArrayList<ShapeInfo> shapeInfos = ShapeResMgr.getInstance().GetResArrByInfoFilter(context, null);

        //无数据绑定，只是作为view bind，可共用
        ArrayList<ShapeExAdapter.ShapeSubInfo> subInfos = GetShapeSubInfo(context);

        if (shapeInfos != null && shapeInfos.size() > 0)
        {
            //无
            ShapeExAdapter.ShapeExItemInfo nonItemInfo = new ShapeExAdapter.ShapeExItemInfo();
            nonItemInfo.m_name = context.getString(R.string.shape_cus_defaule_non);
            nonItemInfo.m_logo = R.drawable.ic_shape_non;
            nonItemInfo.m_uri = SuperShapeData.ID_NON_SHAPE;
            nonItemInfo.m_data = ShapeResMgr.HasItem(shapeInfos, SuperShapeData.ID_NON_SHAPE);
            out.add(nonItemInfo);

            //自然修饰
            ShapeExAdapter.ShapeExItemInfo itemInfo = new ShapeExAdapter.ShapeExItemInfo();
            itemInfo.m_uri = SuperShapeData.ID_ZIRANXIUSHI;
            itemInfo.m_logo = R.drawable.ic_shape_1_ziranxiushi;
            itemInfo.m_name = context.getString(R.string.shape_cus_id_ziranxiushi);
            itemInfo.m_data = ShapeResMgr.HasItem(shapeInfos, SuperShapeData.ID_ZIRANXIUSHI);
            itemInfo.m_subs = subInfos;
            out.add(itemInfo);

            //呆萌甜心
            itemInfo = new ShapeExAdapter.ShapeExItemInfo();
            itemInfo.m_uri = SuperShapeData.ID_DAIMENGTIANXIN;
            itemInfo.m_logo = R.drawable.ic_shape_6_daimengtianxin;
            itemInfo.m_name = context.getString(R.string.shape_cus_id_daimengtianxin);
            itemInfo.m_data = ShapeResMgr.HasItem(shapeInfos, SuperShapeData.ID_DAIMENGTIANXIN);
            itemInfo.m_subs = subInfos;
            out.add(itemInfo);

            //激萌少女
            itemInfo = new ShapeExAdapter.ShapeExItemInfo();
            itemInfo.m_uri = SuperShapeData.ID_JIMENGSHAONV;
            itemInfo.m_logo = R.drawable.ic_shape_4_jimengshaonv;
            itemInfo.m_name = context.getString(R.string.shape_cus_id_jimengshaonv);
            itemInfo.m_data = ShapeResMgr.HasItem(shapeInfos, SuperShapeData.ID_JIMENGSHAONV);
            itemInfo.m_subs = subInfos;
            out.add(itemInfo);

            //摩登女王
            itemInfo = new ShapeExAdapter.ShapeExItemInfo();
            itemInfo.m_uri = SuperShapeData.ID_MODENGNVWANG;
            itemInfo.m_logo = R.drawable.ic_shape_5_modengnvwang;
            itemInfo.m_name = context.getString(R.string.shape_cus_id_modengnvwang);
            itemInfo.m_data = ShapeResMgr.HasItem(shapeInfos, SuperShapeData.ID_MODENGNVWANG);
            itemInfo.m_subs = subInfos;
            out.add(itemInfo);

            //芭比公主
            itemInfo = new ShapeExAdapter.ShapeExItemInfo();
            itemInfo.m_uri = SuperShapeData.ID_BABIGONGZHU;
            itemInfo.m_logo = R.drawable.ic_shape_2_babiwawa;
            itemInfo.m_name = context.getString(R.string.shape_cus_id_babigongzhu);
            itemInfo.m_data = ShapeResMgr.HasItem(shapeInfos, SuperShapeData.ID_BABIGONGZHU);
            itemInfo.m_subs = subInfos;
            out.add(itemInfo);

            //精致网红
            itemInfo = new ShapeExAdapter.ShapeExItemInfo();
            itemInfo.m_uri = SuperShapeData.ID_JINGZHIWANGHONG;
            itemInfo.m_logo = R.drawable.ic_shape_3_jingzhiwanghong;
            itemInfo.m_name = context.getString(R.string.shape_cus_id_jingzhiwanghong);
            itemInfo.m_data = ShapeResMgr.HasItem(shapeInfos, SuperShapeData.ID_JINGZHIWANGHONG);
            itemInfo.m_subs = subInfos;
            out.add(itemInfo);

            //小脸女神
            itemInfo = new ShapeExAdapter.ShapeExItemInfo();
            itemInfo.m_uri = SuperShapeData.ID_XIAOLIANNVSHEN;
            itemInfo.m_logo = R.drawable.ic_shape_8_xiaoliannvshen;
            itemInfo.m_name = context.getString(R.string.shape_cus_id_xiaoliannvshen);
            itemInfo.m_data = ShapeResMgr.HasItem(shapeInfos, SuperShapeData.ID_XIAOLIANNVSHEN);
            itemInfo.m_subs = subInfos;
            out.add(itemInfo);

            //嘟嘟童颜
            itemInfo = new ShapeExAdapter.ShapeExItemInfo();
            itemInfo.m_uri = SuperShapeData.ID_DUDUTONGYAN;
            itemInfo.m_logo = R.drawable.ic_shape_7_dudulianton;
            itemInfo.m_name = context.getString(R.string.shape_cus_id_dudutongyan);
            itemInfo.m_data = ShapeResMgr.HasItem(shapeInfos, SuperShapeData.ID_DUDUTONGYAN);
            itemInfo.m_subs = subInfos;
            out.add(itemInfo);
        }

        //自定义脸型数据
        ArrayList<ShapeInfo> shapeSyncInfos = ShapeSyncResMgr.getInstance().SyncGetSdcardArr(context);

        if (shapeSyncInfos != null && shapeSyncInfos.size() > 0)
        {
            //我的脸型（app同步数据）
            ShapeExAdapter.ShapeExItemInfo itemInfo = new ShapeExAdapter.ShapeExItemInfo();
            itemInfo.m_uri = SuperShapeData.ID_MINE_SYNC;
            itemInfo.m_logo = R.drawable.ic_shape_0_wo_sync;
            itemInfo.m_name = context.getString(R.string.shape_cus_mine_sync);
            itemInfo.m_data = BaseShapeResMgr.HasItem(shapeSyncInfos, SuperShapeData.ID_MINE_SYNC);
            itemInfo.m_subs = subInfos;
            if (out.size() > 0)
            {
                out.add(1, itemInfo);
            }
            else
            {
                out.add(itemInfo);
            }
        }
        return out;
    }


    /**
     * 脸型详细参数info对象，用于adapter bind view
     *
     * @param context
     * @return
     */
    private static ArrayList<ShapeExAdapter.ShapeSubInfo> GetShapeSubInfo(Context context)
    {
        ArrayList<ShapeExAdapter.ShapeSubInfo> out = new ArrayList<>();

        //瘦脸
        ShapeExAdapter.ShapeSubInfo info = new ShapeExAdapter.ShapeSubInfo();
        info.m_type = ShapeDataType.THINFACE;
        info.m_sub_name = context.getString(R.string.beauty_selector_view_shape_thinface);
        info.m_sub_logo = R.drawable.tailor_made_face;
        out.add(info);

        //小脸
        info = new ShapeExAdapter.ShapeSubInfo();
        info.m_type = ShapeDataType.LITTLEFACE;
        info.m_sub_name = context.getString(R.string.beauty_selector_view_shape_littleface);
        info.m_sub_logo = R.drawable.tailor_made_littleface;
        out.add(info);

        //削脸
        info = new ShapeExAdapter.ShapeSubInfo();
        info.m_type = ShapeDataType.SHAVEDFACE;
        info.m_sub_name = context.getString(R.string.beauty_selector_view_shape_shavedface);
        info.m_sub_logo = R.drawable.tailor_made_shavedface;
        out.add(info);

        //额头
        info = new ShapeExAdapter.ShapeSubInfo();
        info.m_type = ShapeDataType.FOREHEAD;
        info.m_sub_name = context.getString(R.string.beauty_selector_view_shape_forehead);
        info.m_sub_logo = R.drawable.tailor_made_forehead;
        out.add(info);

        //颧骨
        info = new ShapeExAdapter.ShapeSubInfo();
        info.m_type = ShapeDataType.CHEEKBONES;
        info.m_sub_name = context.getString(R.string.beauty_selector_view_shape_cheekbones);
        info.m_sub_logo = R.drawable.tailor_made_cheekbones;
        out.add(info);

        //大眼
        info = new ShapeExAdapter.ShapeSubInfo();
        info.m_type = ShapeDataType.BIGEYE;
        info.m_sub_name = context.getString(R.string.beauty_selector_view_shape_bigeye);
        info.m_sub_logo = R.drawable.tailor_made_eye;
        out.add(info);

        //眼角
        info = new ShapeExAdapter.ShapeSubInfo();
        info.m_type = ShapeDataType.CANTHUS;
        info.m_sub_name = context.getString(R.string.beauty_selector_view_shape_canthus);
        info.m_sub_logo = R.drawable.tailor_made_canthus;
        out.add(info);

        //眼距
        info = new ShapeExAdapter.ShapeSubInfo();
        info.m_type = ShapeDataType.EYESPAN;
        info.m_sub_name = context.getString(R.string.beauty_selector_view_shape_eyespan);
        info.m_sub_logo = R.drawable.tailor_made_eyespan;
        out.add(info);

        //瘦鼻
        info = new ShapeExAdapter.ShapeSubInfo();
        info.m_type = ShapeDataType.SHRINKNOSE;
        info.m_sub_name = context.getString(R.string.beauty_selector_view_shape_shrinknose);
        info.m_sub_logo = R.drawable.tailor_made_nose;
        out.add(info);

        //鼻翼
        info = new ShapeExAdapter.ShapeSubInfo();
        info.m_type = ShapeDataType.NOSEWING;
        info.m_sub_name = context.getString(R.string.beauty_selector_view_shape_nosewing);
        info.m_sub_logo = R.drawable.tailor_made_nosewing;
        out.add(info);

        //鼻高
        info = new ShapeExAdapter.ShapeSubInfo();
        info.m_type = ShapeDataType.NOSEHEIGHT;
        info.m_sub_name = context.getString(R.string.beauty_selector_view_shape_noseheight);
        info.m_sub_logo = R.drawable.tailor_made_noseheight;
        out.add(info);

        //下巴
        info = new ShapeExAdapter.ShapeSubInfo();
        info.m_type = ShapeDataType.CHIN;
        info.m_sub_name = context.getString(R.string.beauty_selector_view_shape_chin);
        info.m_sub_logo = R.drawable.tailor_made_chin;
        out.add(info);

        //嘴型
        info = new ShapeExAdapter.ShapeSubInfo();
        info.m_type = ShapeDataType.MOUTH;
        info.m_sub_name = context.getString(R.string.beauty_selector_view_shape_mouth);
        info.m_sub_logo = R.drawable.tailor_made_mouth;
        out.add(info);

        //嘴巴整体高度
        info = new ShapeExAdapter.ShapeSubInfo();
        info.m_type = ShapeDataType.OVERALLHEIGHT;
        info.m_sub_name = context.getString(R.string.beauty_selector_view_shape_overallheight);
        info.m_sub_logo = R.drawable.tailor_made_overallheight;
        out.add(info);

        return out;
    }

}
