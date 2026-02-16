package vcarry.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.photofusion.holi.videomaker.photo.slideshow.KessiApplication;
import com.photofusion.holi.videomaker.photo.slideshow.VideoThemeActivity;
import com.photofusion.holi.videomaker.photo.slideshow.R;
import com.bumptech.glide.Glide;
import com.photofusion.holi.videomaker.photo.slideshow.util.KSUtil;
import com.photofusion.holi.videomaker.photo.slideshow.util.Prefs;

import java.util.ArrayList;
import java.util.Arrays;

import vcarry.mask.KessiTheme;


public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.Holder> {
    Context mCotext;
    private static KessiApplication application = KessiApplication.getInstance();
    private LayoutInflater inflater;
    private static ArrayList<KessiTheme> list;
    private static VideoThemeActivity activity;
    OnItemClickListener listener;
    public static int position = 0;


    public interface OnItemClickListener {
        void onItemClick(int position, ArrayList<KessiTheme> list);
    }



    public class Holder extends RecyclerView.ViewHolder {
        ImageView cbSelect;
        private View clickableView;
        private ImageView ivThumb;
        private View mainView;
        public FrameLayout lockedContainer;


        public Holder(View v) {
            super(v);
            this.cbSelect = (ImageView) v.findViewById(R.id.cbSelect);
            this.ivThumb = (ImageView) v.findViewById(R.id.ivThumb);
            this.clickableView = v.findViewById(R.id.clickableView);
            this.lockedContainer = v.findViewById(R.id.lockedContainer);
            this.mainView = v;
        }

    }

    public ThemeAdapter(Context context, VideoThemeActivity PVMWSPreviewActivity, OnItemClickListener listener) {
        this.activity = PVMWSPreviewActivity;
        this.list = new ArrayList(Arrays.asList(KessiTheme.values()));
        this.inflater = LayoutInflater.from(PVMWSPreviewActivity);
        this.mCotext = context;
        this.listener = listener;
    }

    public Holder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        View view = this.inflater.inflate(R.layout.theme_items, paramViewGroup, false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((activity.getResources()
                .getDisplayMetrics().widthPixels * 200 / 1080),
                (activity.getResources()
                        .getDisplayMetrics().widthPixels * 200 / 1080));
        params.setMargins(10, 10, 10, 10);

        view.setLayoutParams(params);
        return new Holder(view);
    }

    public void onBindViewHolder(Holder holder, @SuppressLint("RecyclerView") final int pos) {
        KessiTheme PVMWSThemes = (KessiTheme) this.list.get(pos);


        Glide.with(this.application).load(Integer.valueOf(PVMWSThemes.getThemeDrawable())).into(holder.ivThumb);


        if (position == pos ) {
            holder.cbSelect.setVisibility(View.VISIBLE);
        }else {
            holder.cbSelect.setVisibility(View.GONE);
        }

        Prefs prefs = new Prefs(mCotext);
        if (prefs.getPremium() ==0 ) {
            if ((pos + 1) % 3 == 0 && !KSUtil.Themeposs.contains(pos)) {
                holder.lockedContainer.setVisibility(View.VISIBLE);

            } else {
                holder.lockedContainer.setVisibility(View.GONE);
            }
        }
        else
        {
            holder.lockedContainer.setVisibility(View.GONE);
        }


        holder.clickableView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                position = pos;
                listener.onItemClick(pos, list);

            }
        });
    }


    public ArrayList<KessiTheme> getList() {
        return this.list;
    }

    public int getItemCount() {
        return this.list.size();
    }


}
