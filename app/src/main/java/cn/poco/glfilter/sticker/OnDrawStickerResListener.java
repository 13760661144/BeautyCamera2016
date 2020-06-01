package cn.poco.glfilter.sticker;

/**
 * Created by zwq on 2017/07/17 15:33.<br/><br/>
 */

public interface OnDrawStickerResListener {
    public static final int IDLE = 0;
    public static final int PLAYING = 1;
    public static final int STOP = 2;

    /**
     * 动画触发音乐
     *
     * @param state 0:stop, 1:start, 此状态用于判断是 start 还是 stop 音乐播放
     */
    void onPlayAnimMusic(int state);

    /**
     * 动作触发音乐，可能会触发多次，因此播放前需判断播放状态
     *
     * @param action {@link cn.poco.dynamicSticker.FaceAction}
     */
    void onPlayActionMusic(String action);

    /**
     * 有动作的动画触发音乐，可能会触发多次，因此播放前需判断播放状态
     *
     * @param action {@link cn.poco.dynamicSticker.FaceAction}
     * @param state  0:stop, 1:start, 此状态用于判断是 start 还是 stop 音乐播放
     */
    void onPlayActionAnimMusic(String action, int state);

    /**
     * 此方法会被频繁调用以判断是否需要触发音乐
     *
     * @param type 0:AnimMusic, 1:ActionMusic, 2:ActionAnimMusic
     * @return {@link #IDLE},  {@link #PLAYING},  {@link #STOP}
     */
    int getPlayState(int type);

    void onAnimStateChange(int state);

    void onAnimTrigger(int type);

}
