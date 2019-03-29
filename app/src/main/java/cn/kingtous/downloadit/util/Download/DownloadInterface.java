package cn.kingtous.downloadit.util.Download;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public class DownloadInterface {

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
        Observable<ResponseBody> download(@Url String url);
    }

}
