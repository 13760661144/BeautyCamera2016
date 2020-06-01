package cn.poco.resource;

import java.util.ArrayList;

import my.beautyCamera.R;

/**
 * Created by zwq on 2017/06/12 15:56.<br/><br/>
 */

public class RealTimeMakeUpResMgr {

    public static ArrayList<RealTimeMakeUpRes> mMakeUpResList;

    public static void clearAll() {
        if (mMakeUpResList != null) {
            mMakeUpResList.clear();
        }
        mMakeUpResList = null;
    }
    
    public static void initLocalRes() {
        if (mMakeUpResList != null) return;
        
        mMakeUpResList = new ArrayList<>();

        RealTimeMakeUpRes makeUpRes = null;
        RealTimeMakeUpSubRes makeUpSubRes = null;
        ArrayList<RealTimeMakeUpSubRes> mMakeUpSubResList = null;

        makeUpRes = new RealTimeMakeUpRes();
        //彩妆出现的帧范围
        makeUpRes.setMakeUpFrameArr(new int[]{0, 200});//// FIXME: 2017/10/13
        mMakeUpResList.add(makeUpRes);
        mMakeUpSubResList = new ArrayList<>();
        makeUpRes.setSubResList(mMakeUpSubResList);

        //YSL  2017.10.20-2017.10.31
        makeUpSubRes = new RealTimeMakeUpSubRes();
        makeUpSubRes.mName = "YSL";

        makeUpSubRes.mLip = R.drawable.__rtmu__ysl_lip_table;
        makeUpSubRes.mLipColor = 0xc772af00;
        makeUpSubRes.mLipBlendType = 46;
        makeUpSubRes.mLipOpaqueness = 1.0f;
        makeUpSubRes.mLipCValue = 131;
        makeUpSubRes.mLipAValue = 229;

        makeUpSubRes.mLipHighLight = R.drawable.__rtmu__ysl_lip_highlight_01;
        makeUpSubRes.mLipHighLightBlendType = 0;
        makeUpSubRes.mLipHighLightOpaqueness = 0.5f;

        makeUpSubRes.mLipHighLightUp = R.drawable.__rtmu__ysl_lip_highlight_up;
        makeUpSubRes.mLipHighLightUpBlendType = 0;
        makeUpSubRes.mLipHighLightUpOpaqueness = 0.28f;

        mMakeUpSubResList.add(makeUpSubRes);


//        //--------------------------------
//        //2364
//        makeUpRes = new RealTimeMakeUpRes();
//        //彩妆出现的帧范围
//        makeUpRes.setMakeUpFrameArr(new int[]{7, 17, 17, 27});//// FIXME: 2017/06/12
//        mMakeUpResList.add(makeUpRes);
//        mMakeUpSubResList = new ArrayList<>();
//        makeUpRes.setSubResList(mMakeUpSubResList);
//
//        //森女
//        makeUpSubRes = new RealTimeMakeUpSubRes();
//        makeUpSubRes.mNeedReset = true;
//        makeUpSubRes.mName = "森女";
//        makeUpSubRes.mEyeLash = R.drawable.__rtmu__sennv_lash;
//        makeUpSubRes.mEyeLine = R.drawable.__rtmu__sennv_line;
//        makeUpSubRes.mEyeShadow = R.drawable.__rtmu__sennv_shadow;
//        makeUpSubRes.mEyeLashColor = 0x44312900;
//        makeUpSubRes.mEyeLineColor = 0x44312900;
//        makeUpSubRes.mEyeShadowBlendType = 38;
//        makeUpSubRes.mEyeShadowOpaqueness = 0.4f;
//
//        makeUpSubRes.mBlushLeft = R.drawable.__rtmu__sennv_blush_l;
//        makeUpSubRes.mBlushRight = R.drawable.__rtmu__sennv_blush_r;
//        makeUpSubRes.mBlushColor = 0xff763300;
//        makeUpSubRes.mBlushOpaqueness = 0.15f;
//
//        makeUpSubRes.mLip = R.drawable.__rtmu__sennv_lip_table;
//        makeUpSubRes.mLipColor = 0xe1807700;
//        makeUpSubRes.mLipOpaqueness = 1.0f;
//        makeUpSubRes.mLipBlendType = 41;
//        makeUpSubRes.mLipCValue = 255;
//        makeUpSubRes.mLipAValue = 190;
//        mMakeUpSubResList.add(makeUpSubRes);
//
//        //哥特
//        makeUpSubRes = new RealTimeMakeUpSubRes();
//        makeUpSubRes.mName = "哥特";
//        makeUpSubRes.mEyeLash = R.drawable.__rtmu__gete_lash;
//        makeUpSubRes.mEyeLine = R.drawable.__rtmu__gete_line;
//        makeUpSubRes.mEyeShadow = R.drawable.__rtmu__gete_shadow;
//        makeUpSubRes.mEyeLashColor = 0x200c0c00;
//        makeUpSubRes.mEyeLineColor = 0x200c0c00;
//        makeUpSubRes.mEyeShadowBlendType = 38;
//        makeUpSubRes.mEyeShadowOpaqueness = 0.4f;
//
//        makeUpSubRes.mBlushLeft = R.drawable.__rtmu__gete_blush_l;
//        makeUpSubRes.mBlushRight = R.drawable.__rtmu__gete_blush_r;
//        makeUpSubRes.mBlushColor = 0xeb5f2100;
//        makeUpSubRes.mBlushOpaqueness = 0.15f;
//
//        makeUpSubRes.mLip = R.drawable.__rtmu__gete_lip_table;
//        makeUpSubRes.mLipColor = 0xdc002500;
//        makeUpSubRes.mLipOpaqueness = 1.0f;
//        makeUpSubRes.mLipBlendType = 46;
//        makeUpSubRes.mLipCValue = 175;
//        makeUpSubRes.mLipAValue = 255;
//        mMakeUpSubResList.add(makeUpSubRes);
//
//        //--------------------------------
//        //2529
//        makeUpRes = new RealTimeMakeUpRes();
//        makeUpRes.setMakeUpFrameArr(new int[]{7, 15, 15, 25});//// FIXME: 2017/06/12
//        mMakeUpResList.add(makeUpRes);
//        mMakeUpSubResList = new ArrayList<>();
//        makeUpRes.setSubResList(mMakeUpSubResList);
//
//        //学院风
//        makeUpSubRes = new RealTimeMakeUpSubRes();
//        makeUpSubRes.mNeedReset = true;
//        makeUpSubRes.mName = "学院风";
//        makeUpSubRes.mEyeLash = R.drawable.__rtmu__xueyuanfeng_lash;
//        makeUpSubRes.mEyeLine = R.drawable.__rtmu__xueyuanfeng_line;
//        makeUpSubRes.mEyeShadow = R.drawable.__rtmu__xueyuanfeng_shadow;
//        makeUpSubRes.mEyeLashColor = 0x44312900;
//        makeUpSubRes.mEyeLineColor = 0x44312900;
//        makeUpSubRes.mEyeShadowBlendType = 38;
//        makeUpSubRes.mEyeShadowOpaqueness = 0.6f;
//
//        makeUpSubRes.mBlushLeft = R.drawable.__rtmu__xueyuanfeng_blush_l;
//        makeUpSubRes.mBlushRight = R.drawable.__rtmu__xueyuanfeng_blush_r;
//        makeUpSubRes.mBlushColor = 0xfda8ab00;
//        makeUpSubRes.mBlushOpaqueness = 0.2f;
//
//        makeUpSubRes.mLip = R.drawable.__rtmu__xueyuanfeng_lip_table;
//        makeUpSubRes.mLipColor = 0x67092800;
//        makeUpSubRes.mLipOpaqueness = 1.0f;
//        makeUpSubRes.mLipBlendType = 46;
//        makeUpSubRes.mLipCValue = 245;
//        makeUpSubRes.mLipAValue = 184;
//
//        makeUpSubRes.mLipHighLight = R.drawable.__rtmu__xueyuanfeng_lip_highlight_01;
//        makeUpSubRes.mLipHighLightBlendType = 1;
//        makeUpSubRes.mLipHighLightOpaqueness = 175 / 255.0f;
//        mMakeUpSubResList.add(makeUpSubRes);
//
//        //运动
//        makeUpSubRes = new RealTimeMakeUpSubRes();
//        makeUpSubRes.mName = "运动";
//        makeUpSubRes.mEyeLash = R.drawable.__rtmu__yundong_lash;
//        makeUpSubRes.mEyeLine = R.drawable.__rtmu__yundong_line;
//        makeUpSubRes.mEyeShadow = R.drawable.__rtmu__yundong_shadow;
//        makeUpSubRes.mEyeLashColor = 0x44312900;
//        makeUpSubRes.mEyeLineColor = 0x44312900;
//        makeUpSubRes.mEyeShadowBlendType = 38;
//        makeUpSubRes.mEyeShadowOpaqueness = 0.9f;
//
//        makeUpSubRes.mLip = R.drawable.__rtmu__yundong_lip_table;
//        makeUpSubRes.mLipColor = 0xa45e4c00;
//        makeUpSubRes.mLipOpaqueness = 1.0f;
//        makeUpSubRes.mLipBlendType = 41;
//        makeUpSubRes.mLipCValue = 249;
//        makeUpSubRes.mLipAValue = 165;
//
//        makeUpSubRes.mLipHighLight = R.drawable.__rtmu__yundong_lip_highlight_01;
//        makeUpSubRes.mLipHighLightBlendType = 1;//100%曝光
//        makeUpSubRes.mLipHighLightOpaqueness = 175 / 255.0f;
//        mMakeUpSubResList.add(makeUpSubRes);
//
//        //--------------------------------
//        //2530
//        makeUpRes = new RealTimeMakeUpRes();
//        makeUpRes.setMakeUpFrameArr(new int[]{7, 15, 15, 25});//// FIXME: 2017/06/12
//        mMakeUpResList.add(makeUpRes);
//        mMakeUpSubResList = new ArrayList<>();
//        makeUpRes.setSubResList(mMakeUpSubResList);
//
//        //洛丽塔
//        makeUpSubRes = new RealTimeMakeUpSubRes();
//        makeUpSubRes.mNeedReset = true;
//        makeUpSubRes.mName = "洛丽塔";
//        makeUpSubRes.mEyeLash = R.drawable.__rtmu__luolita_lash;
//        makeUpSubRes.mEyeLine = R.drawable.__rtmu__luolita_line;
//        makeUpSubRes.mEyeShadow = R.drawable.__rtmu__luolita_shadow;
//        makeUpSubRes.mEyeLashColor = 0x44312900;
//        makeUpSubRes.mEyeLineColor = 0x44312900;
//        makeUpSubRes.mEyeShadowBlendType = 38;
//        makeUpSubRes.mEyeShadowOpaqueness = 0.9f;
//
//        makeUpSubRes.mBlushLeft = R.drawable.__rtmu__luolita_blush_l;
//        makeUpSubRes.mBlushRight = R.drawable.__rtmu__luolita_blush_r;
//        makeUpSubRes.mBlushColor = 0xff897600;
//        makeUpSubRes.mBlushOpaqueness = 0.3f;
//
//        makeUpSubRes.mLip = R.drawable.__rtmu__luolita_lip_table;
//        makeUpSubRes.mLipColor = 0xaf003000;
//        makeUpSubRes.mLipOpaqueness = 1.0f;
//        makeUpSubRes.mLipBlendType = 41;
//        makeUpSubRes.mLipCValue = 255;
//        makeUpSubRes.mLipAValue = 95;
//
//        makeUpSubRes.mLipHighLight = R.drawable.__rtmu__luolita_lip_highlight_01;
//        makeUpSubRes.mLipHighLightBlendType = 1;
//        makeUpSubRes.mLipHighLightOpaqueness = 175 / 255.0f;
//        mMakeUpSubResList.add(makeUpSubRes);
//
//        //御姐
//        makeUpSubRes = new RealTimeMakeUpSubRes();
//        makeUpSubRes.mName = "御姐";
//        makeUpSubRes.mEyeLash = R.drawable.__rtmu__yujie_lash;
//        makeUpSubRes.mEyeLine = R.drawable.__rtmu__yujie_line;
//        makeUpSubRes.mEyeShadow = R.drawable.__rtmu__yujie_shadow;
//        makeUpSubRes.mEyeLashColor = 0x44312900;
//        makeUpSubRes.mEyeLineColor = 0x3b240d00;
//        makeUpSubRes.mEyeShadowBlendType = 34;
//        makeUpSubRes.mEyeShadowOpaqueness = 0.6f;
//
//        makeUpSubRes.mLip = R.drawable.__rtmu__yujie_lip_table;
//        makeUpSubRes.mLipColor = 0xa3000800;
//        makeUpSubRes.mLipOpaqueness = 1.0f;
//        makeUpSubRes.mLipBlendType = 46;
//        makeUpSubRes.mLipCValue = 236;
//        makeUpSubRes.mLipAValue = 180;
//
//        makeUpSubRes.mLipHighLight = R.drawable.__rtmu__yujie_lip_highlight_01;
//        makeUpSubRes.mLipHighLightBlendType = 1;
//        makeUpSubRes.mLipHighLightOpaqueness = 175 / 255.0f;
//        mMakeUpSubResList.add(makeUpSubRes);

    }
}
