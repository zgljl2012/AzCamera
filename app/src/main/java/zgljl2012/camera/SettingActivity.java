package zgljl2012.camera;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 设置页面
 * Created by 廖金龙 on 2015/9/14.
 */
public class SettingActivity extends Activity {

    private ListView listView_btn;
    private SimpleAdapter adapter;
    private String[] name = {
            "image_btn",
            "text_btn"
    };
    private int[] image_btn = {
            R.mipmap.photo,
            R.mipmap.face
    };
    private String[] text_btn = {
            "查看已拍照片",
            "查看人脸图片"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        listView_btn = (ListView) findViewById(R.id.listview_btn);
        List<Map<String, Object>> data=new LinkedList<Map<String,Object>>();
        for(int i = 0; i < image_btn.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(name[0], image_btn[i]);
            map.put(name[1], text_btn[i]);
            data.add(map);
        }
        adapter = new SimpleAdapter(this,data, R.layout.main_item,name, new int[]{
                R.id.ListItem_imageView,
                R.id.ListItem_textView
        });
        listView_btn.setAdapter(adapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK&& event.getRepeatCount() == 0) {
            finish();
            this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
