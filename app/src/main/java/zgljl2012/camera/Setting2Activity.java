package zgljl2012.camera;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.SeekBar;

import com.camera.AzLog;
import com.data.DataManager;

/**
 *
 * Created by 廖金龙 on 2015/9/14.
 */
public class Setting2Activity extends Activity implements SeekBar.OnSeekBarChangeListener{

    private SeekBar mSeekBar;
    private EditText mImgPath;
    private EditText mFacePath;

    private DataManager dataManager = DataManager.getInstance();
    private AzLog azLog = AzLog.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting2);
        mSeekBar  = (SeekBar)  findViewById(R.id.seekBar);
        mImgPath  = (EditText) findViewById(R.id.edt_img_path);
        mFacePath = (EditText) findViewById(R.id.edt_face_path);

        mImgPath.setText(dataManager.imgSavePath);
        mFacePath.setText(dataManager.faceSavePath);

        mSeekBar.setOnSeekBarChangeListener(this);
        float progress = (dataManager.compensateWidthRatio - 0.5f) * 100 / 0.4f;
        mSeekBar.setProgress((int)progress);

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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // 补光范围最小为0.5，最大为0.9，一共是0.4的范围可调
        dataManager.compensateHeightRatio =
                dataManager.compensateWidthRatio = 0.5f + (float)progress * 0.4f / 100.0f;
        azLog.log("SeekBar", ""+progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
