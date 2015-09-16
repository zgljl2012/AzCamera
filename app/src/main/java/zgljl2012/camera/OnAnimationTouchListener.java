package zgljl2012.camera;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by 廖金龙 on 2015/9/15.
 */
public class OnAnimationTouchListener implements View.OnTouchListener {

    Animation animation_in;
    Animation animation_out;

    public boolean isIfIn() {
        return ifIn;
    }

    public void setIfIn(boolean ifIn) {
        this.ifIn = ifIn;
    }

    public boolean isIfOut() {
        return ifOut;
    }

    public void setIfOut(boolean ifOut) {
        this.ifOut = ifOut;
    }

    boolean ifIn,ifOut;

    public OnAnimationTouchListener(Context context, int anim_in, int anim_out) {
        animation_in = AnimationUtils.loadAnimation(context, anim_in);
        animation_in.setFillAfter(true);
        animation_out = AnimationUtils.loadAnimation(context, anim_out);
        animation_out.setFillAfter(true);
        ifIn = true;
        ifOut = true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if(isIfIn()) {
                v.startAnimation(animation_in);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if(isIfOut()) {
                v.startAnimation(animation_out);
            }
        }
        return false;
    }
}
