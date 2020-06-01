package cn.poco.camera3.beauty.page;

import java.util.HashMap;

/**
 * @author lmx
 *         Created by lmx on 2018-01-16.
 */

public interface IFramePager
{
    void setData(HashMap<String, Object> data);

    void onDestroyView();

    void onPageSelected(int position, Object pageTag);

    void onPause();

    void onResume();

    void onClose();

    void notifyDataChanged();
}
