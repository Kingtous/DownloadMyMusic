package cn.kingtous.downloadit.util.Download;
public interface JsDownloadListener {
    //开始下载
    void onStartDownload();
    //进度
    void onProgress(int progress);
    //完成下载
    void onFinishDownload();
    //下载失败
    void onFail(String errorInfo);
}
