package cn.poco.dynamicSticker;

import android.content.Context;
import android.support.annotation.NonNull;

import my.beautyCamera.R;

/**
 * Created by zwq on 2016/09/02 16:50.<br/><br/>
 */
public class FaceAction {
    public static final String OpenMouth = "openmouth";        //张嘴
    public static final String Blink = "blink";                //眨眼
    public static final String EyeBrow = "eyebrow";            //挑眉
    public static final String NodHead = "nod";                //点头

    public static boolean isExistAction(String action) {
        if (OpenMouth.equals(action) || Blink.equals(action) || EyeBrow.equals(action) || NodHead.equals(action)){
            return true;
        }
        return false;
    }

    public static String getActionTips(@NonNull Context context, String action) {
        if (OpenMouth.equals(action)){
            return context.getString(R.string.camerapage_sticker_face_action_tip_open_mouth);
        } else if (Blink.equals(action)){
            return context.getString(R.string.camerapage_sticker_face_action_tip_blink);
        } else if (EyeBrow.equals(action)){
            return context.getString(R.string.camerapage_sticker_face_action_tip_eyebrow);
        } else if (NodHead.equals(action)){
            return context.getString(R.string.camerapage_sticker_face_action_tip_nod_head);
        }
        return null;
    }

}
