package cn.kingtous.downloadit.util;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * @Description: 描述
 * @AUTHOR 刘楠  Create By 2016/10/27 0027 15:56
 */
public class FileUtils {

    String songName;

    public File createFile(Context context,String songName){
        File file=null;
        String state = Environment.getExternalStorageState();

        if(state.equals(Environment.MEDIA_MOUNTED)){

            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/"+songName+".m4a");
        }else {
            file = new File(context.getCacheDir().getAbsolutePath()+"/Download/"+songName+".m4a");
        }

//        L.d("vivi","file "+file.getAbsolutePath());

        return file;

    }

    public void writeFile2Disk(Response<ResponseBody> response, File file){

        long currentLength = 0;
        OutputStream os =null;

        InputStream is = response.body().byteStream();
        long totalLength =response.body().contentLength();

        try {

            os = new FileOutputStream(file);

            int len ;

            byte [] buff = new byte[1024];

            while((len=is.read(buff))!=-1){

                os.write(buff,0,len);
                currentLength+=len;
//                L.d("vivi","当前进度:"+currentLength);
//                httpCallBack.onLoading(currentLength,totalLength);
            }
            // httpCallBack.onLoading(currentLength,totalLength,true);

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if(os!=null){
                try {
                    os.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
            if(is!=null){
                try {
                    is.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
