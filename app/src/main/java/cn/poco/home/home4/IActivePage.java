package cn.poco.home.home4;

/**
 * Created by lgd on 2017/10/12.
 */

public interface IActivePage
{
    /**
     * 该页面完全显示的时候
     * @param lastActiveMode  上一个显示页面的mode  参考Home4page
     */
    void onPageActive(int lastActiveMode);

    /**
     * 页面完全离开的时候
     * @param nextActiveMode  下个显示页面的mode
     */
    void onPageInActive(int nextActiveMode);

    /**
     * 页面按钮的响应，应当判断该值是否true，页面切换时或主页做动画时会设置false
     * @param uiEnable
     */
    void setUiEnable(boolean uiEnable);
}
