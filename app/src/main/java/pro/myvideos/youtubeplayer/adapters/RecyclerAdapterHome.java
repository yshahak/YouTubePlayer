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

import java.text.SimpleDateFormat;
import java.util.Locale;

import pro.myvideos.youtubeplayer.R;
import pro.myvideos.youtubeplayer.data.SearchVideoHelper;

/**
 * Created by B.E.L on 06/11/2016.
 */

public class RecyclerAdapterHome extends RecyclerView.Adapter<RecyclerAdapterHome.Holder> {

    private final String viewsFormatter;
    private SearchVideoHelper[] videos;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM d, yyyy", Locale.US);

    public RecyclerAdapterHome(SearchVideoHelper[] videos, Context context) {
        this.videos = videos;
        this.viewsFormatter = context.getString(R.string.view_count_formatter);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_home_tab, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        SearchVideoHelper searchVideoHelper = videos[position];
        Picasso.with(holder.itemView.getContext())
                .load(Uri.parse(searchVideoHelper.getImg_src()))
                .fit()
                .into(holder.videoThumbnail);
        holder.videoTitle.setText(searchVideoHelper.getTitle());
        holder.videoViewCount.setText(String.format(viewsFormatter, searchVideoHelper.getCountViews()));
        holder.videoPublishAt.setText(searchVideoHelper.getPublished().substring(0, 10));
        holder.videoPublisher.setText(searchVideoHelper.getChannelTitle());
    }

    @Override
    public int getItemCount() {
        return videos.length;
    }

    class Holder extends RecyclerView.ViewHolder {

        ImageView videoThumbnail;
        TextView videoTitle;
        TextView videoViewCount;
        TextView videoPublisher;
        TextView videoPublishAt;

        public Holder(View itemView) {
            super(itemView);
            videoThumbnail = (ImageView)itemView.findViewById(R.id.video_thumbnail);
            videoTitle = (TextView)itemView.findViewById(R.id.video_title);
            videoViewCount = (TextView)itemView.findViewById(R.id.video_view_count);
            videoPublisher = (TextView)itemView.findViewById(R.id.video_publisher);
            videoPublishAt = (TextView)itemView.findViewById(R.id.video_publish_at);
        }
    }
}
