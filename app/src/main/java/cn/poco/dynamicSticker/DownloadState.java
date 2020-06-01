package cn.poco.dynamicSticker;

public class DownloadState
{
    public static final int NEED_DOWNLOAD = 0; // 未下载
    public static final int HAVE_DOWNLOADED = 1; // 已下载
    public static final int WAITTING_FOR_DOWNLOAD = 2; // 等待下载
    public static final int DOWNLOADING = 3; // 正在下载
    public static final int DOWNLOAD_SUCCESS = 4; // 下载成功
    public static final int DOWNLOAD_FAILED = 5; // 下载失败
}
