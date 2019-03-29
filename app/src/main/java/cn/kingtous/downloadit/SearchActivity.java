package cn.kingtous.downloadit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import androidx.annotation.Nullable;
import cn.kingtous.downloadit.Model.SearchResultModel;
import cn.kingtous.downloadit.Model.fcgModel;
import cn.kingtous.downloadit.util.Download.DownloadInterface;
import cn.kingtous.downloadit.util.Download.DownloadUtils;
import cn.kingtous.downloadit.util.Download.JsDownloadListener;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Subscriber;

public class SearchActivity extends Activity {

    SearchResultModel searchResultModel;
    fcgModel fcgModel;
    String name;
    String guid="8208467632";
    String extName=".m4a";
    Dialog dialog;

    //view
    View view;
    TextView text_progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent=getIntent();
        name=intent.getStringExtra("songName");
        view= LayoutInflater.from(this).inflate(R.layout.progress_show, null,false);
        text_progress=view.findViewById(R.id.text_progress);
        text_progress.setText(getString(R.string.progressnow)+"正在搜索");
        dialog=new Dialog(this);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        //设置dialog宽度
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int)(display.getWidth() * 0.8); //设置宽度
        dialog.getWindow().setAttributes(lp);
        //取消严格模式
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        search();
    }

    private void search(){
        Retrofit retrofit=new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl("https://c.y.qq.com/soso/fcgi-bin/")
                .build();
        DownloadInterface.SearchProtocol protocol=retrofit.create(DownloadInterface.SearchProtocol.class);
        Call<ResponseBody> call=protocol.getSongResponce(name);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String model=response.body().string();
                    int end=model.length()-1;
                    model=model.substring(9,end);
                    Gson gson=new Gson();
                    searchResultModel=gson.fromJson(model,SearchResultModel.class);
                    fcg();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(t.toString(),t.getMessage());
            }
        });
    }


    public void fcg(){
        Retrofit retrofit=new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl("https://c.y.qq.com/base/fcgi-bin/")
                .build();
        DownloadInterface.fcgProtocol protocol=retrofit.create(DownloadInterface.fcgProtocol.class);
        Call<ResponseBody> call=protocol.getfcpResponce(searchResultModel.getData().getSong().getList().get(0).getSongmid(),
                "C400"+searchResultModel.getData().getSong().getList().get(0).getMedia_mid()+".m4a",guid);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String s=response.body().string();
                    s=s.substring(34,s.length()-1);
                    Gson gson=new Gson();
                    fcgModel=gson.fromJson(s, fcgModel.class);
                    down();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(t.toString(),t.getMessage());
            }
        });

    }


    public void down(){
        if (fcgModel.getData().getItems().get(0).getVkey().equals("")){
            Toast.makeText(SearchActivity.this,"没有获取到有效的vkey,该歌曲无法下载",Toast.LENGTH_LONG).show();
            finish();
        }
        final String songName=searchResultModel.getData().getSong().getList().get(0).getSongname();
        //歌曲api-key
        String s="C400"+searchResultModel.getData().getSong().getList().get(0).getMedia_mid()+".m4a" +
                        "?vkey=" +
                        fcgModel.getData().getItems().get(0).getVkey() +
                        "&guid=8208467632&uin=0&fromtag=66";
        String state = Environment.getExternalStorageState();
        final String path;
        if(state.equals(Environment.MEDIA_MOUNTED)){
            path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/";
        }else {
            path=(this.getCacheDir().getAbsolutePath()+"/Download/");
        }
        final JsDownloadListener downloadListener=new JsDownloadListener() {
            @Override
            public void onStartDownload() {
                String message=getString(R.string.progressnow)+"开始下载";
                text_progress.setText(message);
            }
            @Override
            public void onProgress(int progress) {
                String message=getString(R.string.progressnow)+String.valueOf(progress);
                text_progress.setText(message);
            }
            @Override
            public void onFinishDownload() {
                dialog.dismiss();
                Toast.makeText(SearchActivity.this,"下载完成，存放位置：\n"+path+songName+extName,Toast.LENGTH_LONG).show();
                        new AlertDialog.Builder(SearchActivity.this)
                                .setTitle("下载完成")
                                .setMessage("需要立即打开吗？")
                                .setPositiveButton("打开", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        File file=new File(path+songName+extName);
                                        Intent intent = new Intent("android.intent.action.VIEW");
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("oneshot", 0);
                                        intent.putExtra("configchange", 0);
                                        Uri uri = Uri.fromFile(file);
                                        intent.setDataAndType(uri, "audio/*");
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .show();
            }

            @Override
            public void onFail(String errorInfo) {
                dialog.dismiss();
                Toast.makeText(SearchActivity.this,"下载失败",Toast.LENGTH_LONG).show();

            }
        };
        DownloadUtils downloadUtils=new DownloadUtils("http://dl.stream.qqmusic.qq.com/",downloadListener,songName+extName);
        downloadUtils.download(s, path, new Subscriber() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
                downloadListener.onFail(e.getMessage());
            }

            @Override
            public void onNext(Object o) {
                downloadListener.onFinishDownload();
            }
        });
    }
}
