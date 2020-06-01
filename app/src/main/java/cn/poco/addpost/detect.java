package cn.poco.addpost;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.poco.tianutils.MakeBmp;

/**
 * Created by lee on 2016/12/26.
 */

public class detect {

    public static Float[] detect(Bitmap img, int minSize, Context context) {
        Float[] out = null;

        if (img != null) {
            if (img.getWidth() > minSize || img.getHeight() > minSize) {
                img = MakeBmp.CreateBitmap(img, minSize, minSize, -1, 0, Bitmap.Config.ARGB_8888);
            }
            out = MyPost.Post(context, img);
        }

        return out;
    }

    private static final String[] LANDMARK_KEY_ARR = new String[]{
            "contour_chin",
            "contour_left1", "contour_left2", "contour_left3", "contour_left4", "contour_left5",
            "contour_left6", "contour_left7", "contour_left8", "contour_left9",
            "contour_right1", "contour_right2", "contour_right3", "contour_right4", "contour_right5",
            "contour_right6", "contour_right7", "contour_right8", "contour_right9",
            "left_eye_bottom",
            "left_eye_center",
            "left_eye_left_corner",
            "left_eye_lower_left_quarter",
            "left_eye_lower_right_quarter",
            "left_eye_pupil",
            "left_eye_right_corner",
            "left_eye_top",
            "left_eye_upper_left_quarter",
            "left_eye_upper_right_quarter",
            "left_eyebrow_left_corner",
            "left_eyebrow_lower_left_quarter",
            "left_eyebrow_lower_middle",
            "left_eyebrow_lower_right_quarter",
            "left_eyebrow_right_corner",
            "left_eyebrow_upper_left_quarter",
            "left_eyebrow_upper_middle",
            "left_eyebrow_upper_right_quarter",
            "mouth_left_corner",
            "mouth_lower_lip_bottom",
            "mouth_lower_lip_left_contour1",
            "mouth_lower_lip_left_contour2",
            "mouth_lower_lip_left_contour3",
            "mouth_lower_lip_right_contour1",
            "mouth_lower_lip_right_contour2",
            "mouth_lower_lip_right_contour3",
            "mouth_lower_lip_top",
            "mouth_right_corner",
            "mouth_upper_lip_bottom",
            "mouth_upper_lip_left_contour1",
            "mouth_upper_lip_left_contour2",
            "mouth_upper_lip_left_contour3",
            "mouth_upper_lip_right_contour1",
            "mouth_upper_lip_right_contour2",
            "mouth_upper_lip_right_contour3",
            "mouth_upper_lip_top",
            "nose_contour_left1",
            "nose_contour_left2",
            "nose_contour_left3",
            "nose_contour_lower_middle",
            "nose_contour_right1",
            "nose_contour_right2",
            "nose_contour_right3",
            "nose_left",
            "nose_right",
            "nose_tip",
            "right_eye_bottom",
            "right_eye_center",
            "right_eye_left_corner",
            "right_eye_lower_left_quarter",
            "right_eye_lower_right_quarter",
            "right_eye_pupil",
            "right_eye_right_corner",
            "right_eye_top",
            "right_eye_upper_left_quarter",
            "right_eye_upper_right_quarter",
            "right_eyebrow_left_corner",
            "right_eyebrow_lower_left_quarter",
            "right_eyebrow_lower_middle",
            "right_eyebrow_lower_right_quarter",
            "right_eyebrow_right_corner",
            "right_eyebrow_upper_left_quarter",
            "right_eyebrow_upper_middle",
            "right_eyebrow_upper_right_quarter",
    };

    public static Float[] parse(String result, int width, int height) {
        if (TextUtils.isEmpty(result)) {
            return null;
        }

        try {
            JSONObject jsonObj = new JSONObject(result);
            if (jsonObj.has("faces")) {
                JSONArray facesJsonArr = jsonObj.getJSONArray("faces");
                if (facesJsonArr != null && facesJsonArr.length() > 0) {
                    List<Float> faces = new ArrayList<>();
                    for (int i = 0; i < facesJsonArr.length(); i++) {
                        // 暂时只解析一张人脸
                        if (i > 0) {
                            break;
                        }
                        JSONObject faceJsonObj = facesJsonArr.getJSONObject(i);
                        // landmark
                        if (faceJsonObj.has("landmark")) {
                            JSONObject landmarkJsonObj = faceJsonObj.getJSONObject("landmark");
                            for (int y = 0; y < LANDMARK_KEY_ARR.length; y++) {
                                String key = LANDMARK_KEY_ARR[y];
                                if (landmarkJsonObj.has(key)) {
                                    JSONObject subJsonObj = landmarkJsonObj.getJSONObject(key);
                                    faces.add(Float.valueOf(subJsonObj.getInt("x")));
                                    faces.add(Float.valueOf(subJsonObj.getInt("y")));
                                }
                            }
                        }

                        // face rectangle
                        if (faceJsonObj.has("face_rectangle")) {
                            JSONObject faceRectJsonObj = faceJsonObj.getJSONObject("face_rectangle");
                            if (faceRectJsonObj.has("left")) {
                                faces.add(Float.valueOf(faceRectJsonObj.getInt("left")));
                            }
                            if (faceRectJsonObj.has("top")) {
                                faces.add(Float.valueOf(faceRectJsonObj.getInt("top")));
                            }
                            if (faceRectJsonObj.has("width")) {
                                faces.add(Float.valueOf(faceRectJsonObj.getInt("width")));
                            }
                            if (faceRectJsonObj.has("height")) {
                                faces.add(Float.valueOf(faceRectJsonObj.getInt("height")));
                            }
                        }
                    }

                    // convert
                    if (!faces.isEmpty()) {
                        Float[] faceArr = new Float[faces.size()];
                        for (int j = 0; j < faces.size(); j++) {
                            faceArr[j] = faces.get(j) / (j % 2 == 0 ? width : height);
                        }
                        return faceArr;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
