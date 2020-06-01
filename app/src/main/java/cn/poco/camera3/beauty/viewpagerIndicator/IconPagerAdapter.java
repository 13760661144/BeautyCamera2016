package cn.poco.camera3.beauty.viewpagerIndicator;

import android.support.annotation.Size;

public interface IconPagerAdapter
{
    /**
     * Get icon representing the page at {@code index} in the adapter.
     */
    @Size(2)
    int[] getIconResId(int index);

    // From PagerAdapter
    int getCount();
}
