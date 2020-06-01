package cn.poco.makeup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import cn.poco.resource.MakeupResMgr2;
import cn.poco.utils.Utils;
import my.beautyCamera.R;



public class MakeupADUtil {


    //ysl商业
    public static boolean isYSLId(int id)
    {
         boolean out = false;
        if(id == 57512 || id == 57520 || id == 57528 || id == 57536 || id == 57544
                || id == 57552 || id == 57560 || id == 57568 || id == 57576 || id == 57584
                || id == 57592 || id == 57600)
        {
            out = true;
        }
        return out;
    }

    public static int[] getYSLColorById(int id)
    {
        if(id == 57512)
        {
            int[] out = {0xdb876d};
            return out;
        }
        else if(id == 57520)
        {
           int[] out = {0xfd909a};
            return out;
        }
         else if(id == 57528)
        {
           int[] out = {0xff948f};
            return out;
        }
         else if(id == 57536)
        {
           int[] out = {0xda092b};
            return out;
        }
         else if(id == 57544)
        {
           int[] out = {0xb9141b};
            return out;
        }
         else if(id == 57552)
        {
           int[] out = {0xe50117};
            return out;
        }
         else if(id == 57560)
        {
           int[] out = {0xd47557};
            return out;
        }
         else if(id == 57568)
        {
           int[] out = {0xc42500};
            return out;
        }
         else if(id == 57576)
        {
           int[] out = {0xe53c4c};
            return out;
        }
         else if(id == 57584)
        {
           int[] out = {0x9a2b40};
            return out;
        }
         else if(id == 57592)
        {
           int[] out = {0xe22552};
            return out;
        }
         else if(id == 57600)
        {
           int[] out = {0xc0001b};
            return out;
        }
        return null;
    }

    //是否是兰蔻商业
    public static boolean isLCId(int id)
    {
        boolean out = false;
        if(id == 57632 || id == 57640 || id == 57648 || id == 57656 || id == 57664 || id == 57672 || id == 57680 || id == 57688 || id == 57696 || id == 57704)
        {
            out = true;
        }
        return out;
    }

    //兰蔻底层滤镜图片
    public static Bitmap getLancuoNeedBmpById(Context context,int id)
    {
        Bitmap out = null;
        int res = -1;
//        switch (id)
//        {
//             //132,172,191
//            case 57664:
//            case 57672:
//            case 57680:
//                {
//                    //丝缎
//                res = R.drawable.ad66_lc_needbmp1;
//                break;
//            }
//
//            //122,105,192
//               case 57696:
//            case 57688:
//            case 57704:
//                {
//                    //丝亮
//                res = R.drawable.ad66_lc_needbmp2;
//                break;
//            }
// //189,397,193,184
//               case 57640:
//            case 57656:
//            case 57648:
//                case 57632:
//                {
//                    //丝绒哑光
//                res = R.drawable.ad66_lc_needbmp3;
//                break;
//            }
//        }
        if(res != -1)
        {
            out = BitmapFactory.decodeResource(context.getResources(),res);
        }
        return out;
    }


    //兰蔻颜色值
    public static int[] getlancuoColorById(int id)
    {
        if(id == 57664)
        {
            int[] out = {0xa9000b};
            return out;
        }
        else if(id == 57672)
        {
            int[] out = {0xf20003};
            return out;
        }
        else if(id == 57680)
        {
            int[] out = {0x360934};
            return out;
        }
        else if(id == 57696)
        {
            int[] out = {0xde1330};
            return out;
        }
        else if(id == 57688)
        {
            int[] out = {0xff4a4c};
            return out;
        }
        else if(id == 57704)
        {
            int[] out = {0xcd0a19};
            return out;
        }
        else if(id == 57640)
        {
            int[] out = {0xd02122};
            return out;
        }
        else if(id == 57656)
        {
            int[] out = {0x881f3a};
            return out;
        }
        else if(id == 57648)
        {
            int[] out = {0xad0c23};
            return out;
        }
        else if(id == 57632)
        {
            int[] out = {0xff5043};
            return out;
        }
        return null;
    }


    //兰蔻唇膏混合模式
    public static int getlcheType(int id)
    {
        int out = 0;
         //132,172,191
          //122,105,192
        if(id == 57664 || id == 57672 || id == 57680 ||
                id == 57696 || id == 57688 || id ==57704)
        {
            out = 46;
        }
        else
        {
            out = 41;
        }
        return out;
    }

    //上唇混合模式
    public static int getlancuoMixTypeUById(int id)
    {
        int out = 1;
          //132,172,191
          //122,105,192
        if(id == 57664 || id == 57672 || id == 57680 ||
                id == 57696 || id == 57688 || id ==57704)
        {
            out = 45;
        }
        return out;
    }


    //下唇混合模式
     public static int getlancuoMixTypeDById(int id)
    {
        int out = 45;
          //132,172,191
          //122,105,192
        //189,397,193,184
        return out;
    }


     //上唇透明度
    public static int getlancuoAlphaUById(int id)
    {
        int out = 100;
          //132,172,191
          //122,105,192
        if(id == 57664 || id == 57672 || id == 57680 ||
                id == 57696 || id == 57688 || id ==57704)
        {
            out = 60;
        }
        return out;
    };


    //下唇透明度
     public static int getlancuoAlphaDById(int id)
    {
         int out = 0;
          //132,172,191
        if(id == 57664 || id == 57672 || id == 57680)
        {
            out = 45;
        }
          //122,105,192
        else if(id == 57696 || id == 57688 || id ==57704)
        {
            out = 30;
        }
           //189,397,193,184
        else if(id == 57640 || id == 57656 || id == 57648 || id == 57632)
        {
            out = 100;
        }
        return out;
    };

    //兰蔻上唇图片
    public static Bitmap getlcscBmpById(Context context,int id)
    {
        Bitmap out = null;
           //132,172,191
//        if(id == 57664 || id == 57672 || id == 57680)
//        {
//            out = BitmapFactory.decodeResource(context.getResources(),R.drawable.ad66_shangcun_bmp1);
//        }
//         //122,105,192
//        else if(id == 57696 || id == 57688 || id ==57704)
//        {
//            out = BitmapFactory.decodeResource(context.getResources(),R.drawable.ad66_shangcun_bmp2);
//        }
        return out;
    }

     //兰蔻下唇图片
    public static Bitmap getlcxcBmpById(Context context,int id)
    {
        Bitmap out = null;
    //132,172,191
//        if(id == 57664 || id == 57672 || id == 57680)
//        {
//            out = BitmapFactory.decodeResource(context.getResources(),R.drawable.ad66_xiacun_bmp1);
//        }
//          //122,105,192
//        else if(id == 57696 || id == 57688 || id ==5770)
//        {
//            out = BitmapFactory.decodeResource(context.getResources(),R.drawable.ad66_xiacun_bmp2);
//        }
//           //189,397,193,184
//        else if(id == 57640 || id == 57656 || id == 57648 || id == 57632)
//        {
//            out = BitmapFactory.decodeResource(context.getResources(),R.drawable.ad66_xiacun_bmp3);
//        }
        return out;
    }


     //兰蔻上唇坐标
    public static int[] getmouthUParams(int id)
    {
        //132,172,191
         //122,105,192
         //189，397，193，184
            int[] out = {266,158,159,116,37,133,155,144,225,132,194,107,
                    121,101,79,118,99,140
            ,191,149};
            return out;
    }


    //兰蔻下唇坐标
      public static int[] getmouthDParams(int id)
    {
        //132,172,191
         //122,105,192
        //189，397，193，184

            int[] out = {266,158,152,157,37,133,149,224,191,159,98,
                    149,106,215,188,223};

        return out;
    }





    //植树秀唇彩判断
    public static boolean isADLipEffect(int uri)
    {
        boolean isADLipEffect = false;
        if(uri == 56224 || uri == 56232 || uri == 56240 || uri == 56248 || uri == 56256 || uri == 56264 || uri == 56272)
        {
            isADLipEffect = true;
        }
        return isADLipEffect;
    }

    //YSL 套装id判断
    public static boolean isYSLMakeup(int id)
    {
        boolean out = false;
        //2795,2796,2797,2798,2799,2800,2801,2802,2803,2804,2805,2806
        if(id == 2795 || id == 2796 || id == 2797 || id == 2798 || id == 2799 || id == 2800 || id == 2801 || id == 2802
                || id == 2803 || id == 2804 || id == 2805 || id == 2806)
        {
            out = true;
        }
        return out;
    }


    //兰蔻 套装id判断
    public static boolean isLCMakeup(int id)
    {
         boolean out = false;
        //2795,2796,2797,2798,2799,2800,2801,2802,2803,2804,2805,2806
        if(id == 2807 || id == 2808 || id == 2809 || id == 2810 || id == 2811 || id == 2812
        || id == 2813 || id == 2814 || id == 2815 || id == 2816)
        {
            out = true;
        }
        return out;
    }




    //判断是否植树秀套装
    public static boolean isADComboEffect(int uri)
    {
        boolean isADLipEffect = false;
        if(uri == 2757 || uri == 2762 || uri == 2761 || uri == 2760 || uri == 2759 || uri == 2758 || uri == 2763)
        {
            isADLipEffect = true;

        }
        return isADLipEffect;
    }

//    570     #d23d29
//    342     #df7571
//    355     #e05b7c
//    356     #df5093
//    376     #dd4e8f

    //	植树秀唇彩颜色(#165：0xd61c1e), (#342：0xd96863), (#355：0xff5b83), (#356：0xee5da2), (#376：0xf23496), (#375：0xbb5971), (#570：0xf2503a)
    public static int getColorById(int id)
    {
        int out = 0;
        switch (id)
        {
            case 56224:
                out = 0xd23d29;
                break;
            case 56232:
                out = 0xdf7571;
                break;
            case 56240:
                out = 0xe05b7c;
                break;
            case 56248:
                out = 0xdf5093;
                break;
            case 56256:
                out = 0xbb5971;
                break;
            case 56264:
                out = 0xdd4e8f;
                break;
            case 56272:
                out = 0xd61c1e;
                break;
        }
        return out;
    }

    //桃色狂热 uri:110110
    //炽色游戏 uri:110111
    //红唇欲动 uri:110112
    //粉色悸动 uri:110113

    public static boolean isBenefitEffectLip(int uri)
    {
        boolean out = false;
        if(uri == 110110 || uri == 110111 ||uri == 110112 ||uri == 110113)
        {
            out = true;
        }
        return out;
    }

    //benefit商业
    //桃色狂热 uri:110110
    //炽色游戏 uri:110111
    //红唇欲动 uri:110112
    //粉色悸动 uri:110113
    //    color[]  	:颜色值数组4款 桃色狂热：{0xc80072, 0x6c005c}, 炽色游戏：{0xc81600, 0x860c24},
// 红唇欲动：{0xbf0e4c, 0x891133}, 粉色悸动：{0xe35ba3, 0x820468}
    public static int[] getColorByIdForBenefit(int id)
    {
        int[] out = null;
        switch (id)
        {
            case 110110:
                out = new int[]{0xc80072, 0x6c005c};
                break;
            case 110111:
                out = new int[]{0xc81600, 0x860c24};
                break;
            case 110112:
                out = new int[]{0xbf0e4c, 0x891133};
                break;
            case 110113:
                out = new int[]{0xe35ba3, 0x820468};
                break;
        }
        return out;
    }


    //菲诗小铺商业专用
//  color :颜色值10款(BE03：0xcd8890), (BR01：0x853c14), (CR02：0xfe9d97),
// (OR02：0xff1800), (PK02：0xeb8baf), (PK03：0xdb464d),
// (PK04：0xc81565), (PP01：0x67002e), (RD02：0xad0008), (RD03：0x630007)
    public static int getColorByFeishixiaopu(int uri)
    {
        int out = 0;
        switch (uri)
        {
            case 1:
                out = 0x630007;
                break;
            case 2:
                out = 0xad0008;
                break;
            case 3:
                out = 0xcd8890;
                break;
            case 4:
                out = 0x853c14;
                break;
            case 5:
                out = 0xfe9d97;
                break;
            case 6:
                out = 0xff1800;
                break;
            case 7:
                out = 0xeb8baf;
                break;
            case 8:
                out = 0xdb464d;
                break;
            case 9:
                out = 0xc81565;
                break;
            case 10:
                out = 0x67002e;
                break;
        }

        return out;
    }

    //clalen商业
    public static int isClalenAD(int uri)
    {
        int out = -1;
        if(uri == 2774 || uri == 2775 || uri == 2776 || uri == 56688 || uri == 56696 || uri == 56704)
        {
            out = uri;
        }
        return out;
    }


//    彩妆主题植入入口
//    点击代码:http://cav.adnonstop.com/cav/fe0a01a3d9/0060302970/?url=https://a-m-s-ios.poco.cn/images/blank.gif
//    彩妆植入1（JB）
//    点击代码:http://cav.adnonstop.com/cav/fe0a01a3d9/0060303081/?url=https://a-m-s-ios.poco.cn/images/blank.gif
//    彩妆植入2（RS）
//    点击代码:http://cav.adnonstop.com/cav/fe0a01a3d9/0060303073/?url=https://a-m-s-ios.poco.cn/images/blank.gif
//    彩妆植入3（SB）
//    点击代码:http://cav.adnonstop.com/cav/fe0a01a3d9/0060303076/?url=https://a-m-s-ios.poco.cn/images/blank.gif
//
//    美瞳植入1（JB）
//    点击代码:http://cav.adnonstop.com/cav/fe0a01a3d9/0060302281/?url=https://a-m-s-ios.poco.cn/images/blank.gif
//
//    美瞳植入2（RS）
//    点击代码:http://cav.adnonstop.com/cav/fe0a01a3d9/0060303077/?url=https://a-m-s-ios.poco.cn/images/blank.gif
//
//    美瞳植入3（SB）
//    点击代码:http://cav.adnonstop.com/cav/fe0a01a3d9/0060303079/?url=https://a-m-s-ios.poco.cn/images/blank.gif


    public static void sendClalenUrlTrigger(Context context,int uri)
    {
        String url = null;
        switch (uri)
        {
            case 2774:
                url = "http://cav.adnonstop.com/cav/fe0a01a3d9/0060303081/?url=https://a-m-s-ios.poco.cn/images/blank.gif";
                break;
            case 2775:
                url = "http://cav.adnonstop.com/cav/fe0a01a3d9/0060303073/?url=https://a-m-s-ios.poco.cn/images/blank.gif";
                break;
            case 2776:
                url = "http://cav.adnonstop.com/cav/fe0a01a3d9/0060303076/?url=https://a-m-s-ios.poco.cn/images/blank.gif";
                break;
            case 56688:
                url = "http://cav.adnonstop.com/cav/fe0a01a3d9/0060302281/?url=https://a-m-s-ios.poco.cn/images/blank.gif";
                break;
            case 56696:
                url = "http://cav.adnonstop.com/cav/fe0a01a3d9/0060303077/?url=https://a-m-s-ios.poco.cn/images/blank.gif";
                break;
            case 56704:
                url = "http://cav.adnonstop.com/cav/fe0a01a3d9/0060303079/?url=https://a-m-s-ios.poco.cn/images/blank.gif";
                break;
            default:
                break;
        }
        if(url != null && url.length() > 0)
        {
            Utils.UrlTrigger(context,url);
        }
    }


    public static int getClalenADResByUri(int uri)
    {
        int out = -1;
        switch (uri)
        {
            case 2774:
            case 56688:
                out = R.drawable.beautify_makeup_ad_clalen_jb1;
                break;
            case 2775:
            case 56696:
                out = R.drawable.beautify_makeup_ad_clalen_rs2;
                break;
            case 2776:
            case 56704:
                out = R.drawable.beautify_makeup_ad_clalen_sb3;
                break;
        }
        return out;
    }

    public static int getClalenADIndex(int uri)
    {
        int out = -1;
        switch (uri)
        {
            case 2774:
            case 56688:
                out = 1;
                break;
            case 2775:
            case 56696:
                out = 2;
                break;
            case 2776:
            case 56704:
                out = 3;
                break;
        }
        return out;
    }


    public static int getClalenADBGColorByUri(int uri)
    {
        int out = -1;
        switch (uri)
        {
            case 2774:
            case 56688:
                out = 0xb2282828;
                break;
            case 2775:
            case 56696:
                out = 0x99251004;
                break;
            case 2776:
            case 56704:
                out = 0x99251004;
                break;
        }
        return out;
    }


    //判断是否弹阿玛尼广告
    public static boolean m_ArManiADShowOnce = false;
    public static boolean isShouldShowArManiAD(int uri)
    {
        boolean out = false;
        long time = System.currentTimeMillis();
        if(uri == 57104 && time < MakeupResMgr2.s_startTime2 && !m_ArManiADShowOnce)
        {
            out = true;
            m_ArManiADShowOnce = true;
        }
        else if((uri == 57112 || uri == 57104)&& time < MakeupResMgr2.s_startTime3 && !m_ArManiADShowOnce)
        {
            out = true;
            m_ArManiADShowOnce = true;
        }
        return out;
    }

    public static boolean isArManiAD(int uri)
    {
        boolean out = false;
        if(uri == 57112 || uri == 57104 || uri == 57120)
        {
            out = true;
        }
        return out;
    }

    //:颜色 #505:0xde5194   #511:0xe071b8   #512:0xda7fb0
    public static int getArmaniColorBy(int uri)
    {
        int out = 0;
        if(uri == 57104)
        {
            out = 0xde5194;
        }
        else if(uri == 57112)
        {
            out = 0xe071b8;
        }
        else if(uri == 57120)
        {
            out = 0xde7fac;
        }
        return out;
    }


    public static int getArManiResByUriAndTime(int uri)
    {
        int out = -1;
//        switch (uri)
//        {
//            case 57104:
//                out = R.drawable.beautify_makeup_armani_ad1;
//                break;
//            case 57112:
//                out = R.drawable.beautify_makeup_armani_ad2;
//                break;
//        }
        long time = System.currentTimeMillis();
        if(uri == 57104 && time < MakeupResMgr2.s_startTime2)
        {
            out = R.drawable.beautify_makeup_armani_ad1;
        }
        else if((uri == 57112 || uri == 57104) && time < MakeupResMgr2.s_startTime3)
        {
            out = R.drawable.beautify_makeup_armani_ad2;
        }
        return out;
    }
}
