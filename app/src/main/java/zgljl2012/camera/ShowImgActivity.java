package zgljl2012.camera;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

/**
 * 显示图片的Activity
 * Created by 廖金龙 on 2015/9/14.
 */
public class ShowImgActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.show_img);
    }
}
