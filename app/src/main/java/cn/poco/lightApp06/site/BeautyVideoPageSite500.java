package cn.poco.lightApp06.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.arWish.ARHideWishPrePage;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * Created by zwq on 2018/01/24 14:01.<br/><br/>
 * AR送祝福
 */
public class BeautyVideoPageSite500 extends BeautyVideoPageSite {

    public void onSave(Context context, String path) {
        HashMap<String, Object> params = new HashMap<>();
//        BaseSite site = MyFramework.GetLinkSite(context, ARHideWishPrePageSite.class);
        params.put(ARHideWishPrePage.KEY_VIDEO_PATH, path);
//        if (site != null){
//            ARHideWishPrePageSite arPrePageSite = (ARHideWishPrePageSite) site;
//            params.put(ARPrePage.KEY_VIDEO_PATH, path);
//            if (arPrePageSite.m_myParams != null){
//                if (arPrePageSite.m_myParams.containsKey(ARPrePage.KEY_IMAGE_PATH))
//                {
//                    params.put(ARPrePage.KEY_IMAGE_PATH, arPrePageSite.m_myParams.get(ARPrePage.KEY_IMAGE_PATH));
//                }
//
//                if (arPrePageSite.m_myParams.containsKey(ARPrePage.KEY_IMAGE_THUMB))
//                {
//                    params.put(ARPrePage.KEY_IMAGE_THUMB, arPrePageSite.m_myParams.get(ARPrePage.KEY_IMAGE_THUMB));
//                }
//            }
            MyFramework.SITE_ClosePopup(context, params, Framework2.ANIM_NONE);
//        }
    }
}
