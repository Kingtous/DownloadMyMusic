package cn.kingtous.downloadit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cn.kingtous.downloadit.Model.SearchResultModel;
import cn.kingtous.downloadit.Model.fcgModel;
import cn.kingtous.downloadit.util.FileUtils;
import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;

public class SearchActivity extends AppCompatActivity {

    SearchResultModel searchResultModel;
    fcgModel fcgModel;
    String name;
    String guid="8208467632";


    public interface SearchProtocol {
        @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.146 Safari/537.36")
        @GET("client_search_cp")
        Call<ResponseBody> getSongResponce(@Query("w") String songName);
    }

    public interface fcgProtocol{
        @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.146 Safari/537.36")
        @GET("fcg_music_express_mobile3.fcg?g_tk=5381&jsonpCallback=MusicJsonCallback9239412173137234&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&cid=205361747&callback=MusicJsonCallback9239412173137234&uin=0")
        Call<ResponseBody> getfcpResponce(@Query("songmid") String songmid,@Query("filename") String filename,@Query("guid") String guid);
    }

    public interface DownloadProtocol {
        @GET
        @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.146 Safari/537.36")
        Call<ResponseBody> download(@Url String url);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        name=intent.getStringExtra("songName");
        Toast.makeText(this,"正在尝试搜索",Toast.LENGTH_LONG).show();

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
        SearchProtocol protocol=retrofit.create(SearchProtocol.class);
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
                    //获取第一首搜索结果的mid
//                    mid=searchResultModel.getData().getSong().getList().get(0).getFile().getMedia_mid();
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
        fcgProtocol protocol=retrofit.create(fcgProtocol.class);
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


        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://dl.stream.qqmusic.qq.com/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        DownloadProtocol protocol=retrofit.create(DownloadProtocol.class);
        final String songName=searchResultModel.getData().getSong().getList().get(0).getSongname();
        String s="C400"+searchResultModel.getData().getSong().getList().get(0).getMedia_mid()+".m4a" +
                        "?vkey=" +
                        fcgModel.getData().getItems().get(0).getVkey() +
                        "&guid=8208467632&uin=0&fromtag=66";

        Call<ResponseBody> call=protocol.download(s);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                final FileUtils fileUtils=new FileUtils();
                final File downloadFile= fileUtils.createFile(SearchActivity.this,songName);
                //下载文件放在子线程
                AsyncTask task=new AsyncTask<Void,Void,Void>() {

                    ProgressDialog dialog=new ProgressDialog(SearchActivity.this);

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {

                            }
                        });
                        dialog.setMessage("正在下载中...");
                        dialog.show();
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        fileUtils.writeFile2Disk(response, downloadFile);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        dialog.dismiss();
                        Toast.makeText(SearchActivity.this,"下载完成，存放位置："+downloadFile.getPath(),Toast.LENGTH_LONG).show();
                        new AlertDialog.Builder(SearchActivity.this)
                                .setTitle("下载完成")
                                .setMessage("需要立即打开吗？")
                                .setPositiveButton("打开", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent("android.intent.action.VIEW");
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("oneshot", 0);
                                        intent.putExtra("configchange", 0);
                                        Uri uri = Uri.fromFile(downloadFile);
                                        intent.setDataAndType(uri, "audio/*");
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("取消",null)
                                .show();

                    }
                }.execute();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(SearchActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });




        //调用fastdownloader下载
//        new Download.Builder()
//                .addHeaders("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.146 Safari/537.36")
//                .savepath(Environment.getExternalStorageDirectory()+"/Download/")
//                .url("http://dl.stream.qqmusic.qq.com/" +
//                        "C400"+searchResultModel.getData().getSong().getList().get(0).getMedia_mid()+".m4a" +
//                        "?vkey=" +
//                        fcgModel.getData().getItems().get(0).getVkey() +
//                        "&guid=8208467632&uin=0&fromtag=66")
//                .build(SearchActivity.this)
//                .start();



    }


}
