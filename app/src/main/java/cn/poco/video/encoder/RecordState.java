package cn.poco.video.encoder;

/**
 * Created by zwq on 2016/07/05 10:57.<br/><br/>
 */
public class RecordState {

    /**
     * -1:idle, 0:prepare, 1:wait, 2:start, 3:recording, 4:stop
     */

    public static final int IDLE = 0;
    public static final int PREPARE = 1;
    public static final int WAIT = 2;
    public static final int START = 3;
    public static final int RESUME = 4;
    public static final int RECORDING = 5;
    public static final int PAUSE = 6;
    public static final int STOP = 7;
    public static final int ENDING = 8;//片尾

    public static final int CAPTURE_A_FRAME = 10;
}
