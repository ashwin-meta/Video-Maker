package vcarry.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.photofusion.holi.videomaker.photo.slideshow.KessiApplication;
import com.photofusion.holi.videomaker.photo.slideshow.VideoThemeActivity;
import com.photofusion.holi.videomaker.photo.slideshow.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.photofusion.holi.videomaker.photo.slideshow.util.KSUtil;
import com.photofusion.holi.videomaker.photo.slideshow.util.Prefs;


public class FrameAdapter extends RecyclerView.Adapter<FrameAdapter.Holder> {
    static VideoThemeActivity activity;

    private KessiApplication application;
    private OnItemClickListner<Object> clickListner;
    private int[] drawable = new int[]{ -1,R.drawable.f_1,
            R.drawable.f_2, R.drawable.f_3, R.drawable.f_4, R.drawable.f_5,
            R.drawable.f_6, R.drawable.f_7, R.drawable.f_8, R.drawable.f_9,
            R.drawable.f_10,R.drawable.f_11,R.drawable.f_12,R.drawable.f_13,R.drawable.f_14,R.drawable.f_15,R.drawable.f_16,R.drawable.f_17,R.drawable.f_18,R.drawable.f_19,R.drawable.f_20,R.drawable.f_21,R.drawable.f_22,R.drawable.f_23,R.drawable.f_24};
    private int[] drawable_thumb = new int[]{ R.drawable.none,R.drawable.f_1,
            R.drawable.f_2, R.drawable.f_3, R.drawable.f_4, R.drawable.f_5,
            R.drawable.f_6, R.drawable.f_7, R.drawable.f_8, R.drawable.f_9,
            R.drawable.f_10,R.drawable.f_11,R.drawable.f_12,R.drawable.f_13,R.drawable.f_14,R.drawable.f_15,R.drawable.f_16,R.drawable.f_17,R.drawable.f_18,R.drawable.f_19,R.drawable.f_20,R.drawable.f_21,R.drawable.f_22,R.drawable.f_23,R.drawable.f_24};
    private RequestManager glide;
    private LayoutInflater inflater;

     Context mCotext;

     OnItemClickListener listener;

   public int position = 0;

    public interface OnItemClickListener {
        void onItemClick(int position,int theme);
    }

    public class Holder extends RecyclerView.ViewHolder {
        ImageView cbSelect;
        private View clickableView;
        private ImageView ivThumb;
        public FrameLayout lockedContainer;


        public Holder(View v) {
            super(v);
            this.cbSelect = (ImageView) v.findViewById(R.id.cbSelect);
            this.ivThumb = (ImageView) v.findViewById(R.id.ivThumb);
            this.clickableView = v.findViewById(R.id.clickableView);
            this.lockedContainer=v.findViewById(R.id.lockedContainer);
        }
    }

    public FrameAdapter(Context context, VideoThemeActivity activity, OnItemClickListener listener) {
        this.activity = activity;
        this.application = KessiApplication.getInstance();
        this.inflater = LayoutInflater.from(activity);
        this.glide = Glide.with(activity);
        this.mCotext=context;
        this.listener = listener;
    }


    public int getItemCount() {
        return this.drawable.length;
    }

    public int getItem(int pos) {
        return this.drawable[pos];
    }

    public int getItem1(int pos) {
        return this.drawable_thumb[pos];
    }

    public void onBindViewHolder(final Holder holder, @SuppressLint("RecyclerView") final int pos) {


        final int themes = getItem(pos);
        final int themes1 = getItem1(pos);
        holder.ivThumb.setScaleType(ScaleType.FIT_XY);

        Log.e( "onBindViewHolder: ","mposs"+pos );

        if (position == pos ) {
            Log.e( "onBindViewHolder: ","poss"+position);
            holder.cbSelect.setVisibility(View.VISIBLE);
        }else
        {
            holder.cbSelect.setVisibility(View.GONE);
        }



        Prefs prefs = new Prefs(mCotext);
        if (prefs.getPremium() ==0 ) {
            if ((pos + 1) % 3 == 0 && !KSUtil.Frameposs.contains(pos)) {
                holder.lockedContainer.setVisibility(View.VISIBLE);
            } else {
                holder.lockedContainer.setVisibility(View.GONE);
            }
        }
        else
        {
            holder.lockedContainer.setVisibility(View.GONE);
        }

        Glide.with(this.application).load(Integer.valueOf(themes1)).into(holder.ivThumb);




        holder.clickableView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                position = pos;
                listener.onItemClick(pos,themes);

            }
        });
    }

    public Holder onCreateViewHolder(ViewGroup parent, int pos) {
        View item = this.inflater.inflate(R.layout.frame_items, parent, false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((activity.getResources()
                .getDisplayMetrics().widthPixels* 200 / 1080 ),
                (activity.getResources()
                        .getDisplayMetrics().widthPixels * 200 / 1080));
        params.setMargins(10,10,10,10);

        item.setLayoutParams(params);
        return new Holder(item);
    }


}
