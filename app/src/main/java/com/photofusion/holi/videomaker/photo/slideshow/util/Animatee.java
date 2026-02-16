package com.photofusion.holi.videomaker.photo.slideshow.util;

import android.app.Activity;
import android.content.Context;

import com.photofusion.holi.videomaker.photo.slideshow.R;

public class Animatee {
    public static void animateSlideUp(Context context) {
        ((Activity) context).overridePendingTransition(R.anim.animate_slide_up_enter, R.anim.animate_slide_up_exit);
    }
}
