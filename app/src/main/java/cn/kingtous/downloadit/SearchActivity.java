package cn.kingtous.downloadit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tamic.rx.fastdown.core.Download;
import com.tamic.rx.fastdown.core.RxDownloadManager;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cn.kingtous.downloadit.Model.SearchResultModel;
import cn.kingtous.downloadit.Model.fcgModel;
import cn.kingtous.downloadit.util.DLDownloadListener;
import cn.kingtous.downloadit.util.DLNormalCallback;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class SearchActivity extends AppCompatActivity {


    RxDownloadManager manager;
    SearchResultModel searchResultModel;
    DLNormalCallback normalCallback;
    fcgModel fcgModel;
    String name;


    public interface SearchProtocol {
        /**
         * 用户信息
         * @param page
         * @return
         */
        @Headers("User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.146 Safari/537.36")
        @GET("client_search_cp")
        Call<ResponseBody> getSongResponce(@Query("w") String songName);
    }

    public interface fcgProtocol{
        @Headers("User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.146 Safari/537.36")
        @GET("fcg_music_express_mobile3.fcg?g_tk=5381&jsonpCallback=MusicJsonCallback9239412173137234&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&cid=205361747&callback=MusicJsonCallback9239412173137234&uin=0&guid=8208467632")
        Call<ResponseBody> getfcpResponce(@Query("songmid") String songmid,@Query("filename") String filename);
    }

    public interface downProtocol{
        @Headers("User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.146 Safari/537.36")
        @GET("{}.m4a?vkey={}")
        Call<ResponseBody> getdownResponce(@Query("songmid") String songmid,@Query("filename") String filename);
    }


    private void init() {

        manager = RxDownloadManager.getInstance();
        manager.init(getBaseContext(), null);
        manager.setContext(getBaseContext());
        manager.setListener(new DLDownloadListener(getBaseContext()));


        normalCallback = new DLNormalCallback();
        if (manager.getClient() != null) {
            manager.getClient().setCallback(normalCallback);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        Intent intent=getIntent();
        name=intent.getStringExtra("songName");
        Toast.makeText(this,"正在尝试搜索",Toast.LENGTH_LONG).show();
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
                "C400"+searchResultModel.getData().getSong().getList().get(0).getMedia_mid()+".m4a");
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



        //调用fastdownloader下载
        new Download.Builder()
                .addHeaders("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.146 Safari/537.36")
                .savepath(Environment.getExternalStorageDirectory()+"/Download/")
                .url("http://dl.stream.qqmusic.qq.com/" +
                        "C400"+searchResultModel.getData().getSong().getList().get(0).getMedia_mid()+".m4a" +
                        "?vkey=" +
                        fcgModel.getData().getItems().get(0).getVkey() +
                        "&guid=8208467632&uin=0&fromtag=66")
                .build(SearchActivity.this)
                .start();



    }


}
