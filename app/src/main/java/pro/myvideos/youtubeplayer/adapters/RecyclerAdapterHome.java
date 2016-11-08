package pro.myvideos.youtubeplayer.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import pro.myvideos.youtubeplayer.R;
import pro.myvideos.youtubeplayer.data.VideoData;
import pro.myvideos.youtubeplayer.fragments.TabHomeFragment;

/**
 * Created by B.E.L on 06/11/2016.
 */

public class RecyclerAdapterHome extends RecyclerView.Adapter<RecyclerAdapterHome.Holder> {

    public static final String BY = "by ";
    public static String viewsFormatter;
    private VideoData[] videos;
    public SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM d, yyyy", Locale.US);
    private WeakReference<TabHomeFragment> weakFragment;


    public RecyclerAdapterHome(VideoData[] videos,TabHomeFragment tabHomeFragment, Context context) {
        this.videos = videos;
        viewsFormatter = context.getString(R.string.view_count_formatter);
        weakFragment = new WeakReference<>(tabHomeFragment);

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_home_tab, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        VideoData videoData = videos[position];
        Picasso.with(holder.itemView.getContext())
                .load(Uri.parse(videoData.getImg_src()))
                .fit()
                .into(holder.videoThumbnail);
        holder.videoTitle.setText(videoData.getTitle());
        holder.videoViewCount.setText(String.format(viewsFormatter, videoData.getCountViews()));
        holder.videoPublishAt.setText(videoData.getPublished().substring(0, 10));
        holder.videoPublisher.setText(BY + videoData.getChannelTitle());
        holder.like.setText(formatNumberExample(videoData.getLikes()));
        holder.dislike.setText(formatNumberExample(videoData.getDislikes()));
        holder.videoThumbnail.setTag(R.string.tag_video_data, videoData);
    }

    private static char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
    public static DecimalFormat formatSmall = new DecimalFormat("#0");
    public static DecimalFormat formatBig = new DecimalFormat("#,##0");

    public static String formatNumberExample(Long numValue) {
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return formatSmall.format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return formatBig.format(numValue);
        }
    }

    @Override
    public int getItemCount() {
        return videos.length;
    }

    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView videoThumbnail;
        TextView videoTitle;
        TextView videoViewCount;
        TextView videoPublisher;
        TextView videoPublishAt;
        TextView like, dislike;

        Holder(View itemView) {
            super(itemView);
            videoThumbnail = (ImageView)itemView.findViewById(R.id.video_thumbnail);
            videoThumbnail.setOnClickListener(this);
            videoTitle = (TextView)itemView.findViewById(R.id.video_title);
            videoViewCount = (TextView)itemView.findViewById(R.id.video_view_count);
            videoPublisher = (TextView)itemView.findViewById(R.id.video_publisher);
            videoPublishAt = (TextView)itemView.findViewById(R.id.video_publish_at);
            like = (TextView)itemView.findViewById(R.id.video_like);
            dislike = (TextView)itemView.findViewById(R.id.video_dislike);
        }

        @Override
        public void onClick(View view) {
            VideoData videoData = (VideoData) videoThumbnail.getTag(R.string.tag_video_data);
            if (videoData != null) {
                TabHomeFragment tabHomeFragment = weakFragment.get();
                if (tabHomeFragment != null) {
                    tabHomeFragment.playVideoInFragment(videoData);

                }
            }
        }

    }


}
