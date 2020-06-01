package cn.poco.advanced;

/**
 * @author lmx
 *         Created by lmx on 2017/2/4.
 */

public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
