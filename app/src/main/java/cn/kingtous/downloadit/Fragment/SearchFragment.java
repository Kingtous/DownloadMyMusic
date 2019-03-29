package cn.kingtous.downloadit.Fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.kingtous.downloadit.R;
import cn.kingtous.downloadit.SearchActivity;

public class SearchFragment extends Fragment {

    private static int SONG_SEARCH_CODE=1;

    private Button btn_search;
    private EditText text_Edit;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.search_fragment,container,false);
        btn_search=view.findViewById(R.id.toSearch);
        text_Edit=view.findViewById(R.id.text_search_song);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String songName=text_Edit.getText().toString();
                if (!songName.equals("")){
                    Intent intent=new Intent(getActivity(), SearchActivity.class);
                    intent.putExtra("songName",songName);
                    startActivityForResult(intent,SONG_SEARCH_CODE);
                }
                else Toast.makeText(getContext(),"名字不能为空",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==SONG_SEARCH_CODE){
            //后期添加功能
            return;
        }

    }
}
