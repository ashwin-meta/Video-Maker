package com.photofusion.holi.videomaker.photo.slideshow.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class KSUtil {

    //selected images path list
    public static ArrayList<String> videoPathList = new ArrayList<>();


    public static ArrayList<Integer> Themeposs = new ArrayList<>();


    public static ArrayList<Integer> Frameposs = new ArrayList<>();



    //image editor path and position
    public static String imgEditorPath;
    public static int imbEditorPos;


    public static boolean fromAlbum = false;

    public static AnimatorSet Swing(View view){
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator object = ObjectAnimator.ofFloat(view,   "rotation", 0, 10, -10, 6, -6, 3, -3, 0);

        animatorSet.playTogether(object);
        return animatorSet;
    }

    public static AnimatorSet InUp(View view){
        AnimatorSet animatorSet = new AnimatorSet();
        ViewGroup parent = (ViewGroup) view.getParent();
        int distance = parent.getHeight() - view.getTop();

        ObjectAnimator object1 = ObjectAnimator.ofFloat(view,    "alpha", 0, 1);
        ObjectAnimator object2 = ObjectAnimator.ofFloat(view,    "translationY", distance, 0);

        animatorSet.playTogether(object1, object2);
        return animatorSet;
    }

    public static AnimatorSet In(View view) {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator object = ObjectAnimator.ofFloat(view, "alpha", 0, 1);

        animatorSet.playTogether(object);
        return animatorSet;
    }
}
