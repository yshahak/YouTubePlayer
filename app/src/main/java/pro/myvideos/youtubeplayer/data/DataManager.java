package pro.myvideos.youtubeplayer.data;

/**
 * Created by B.E.L on 03/11/2016.
 */

public class DataManager {
    public static final String YOUTUBE_DATA_API_KEY = "AIzaSyAH0Y0Rfd5Pb9pZoBeFmM_sGRcyVUAGXPQ";
//    /**
//     * Define a global variable that identifies the name of a file that
//     * contains the developer's API key.
//     */
//    private static final String PROPERTIES_FILENAME = "youtube.properties";
//
//    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;
//    private static YouTube youtube;
//    private static YouTube.Search.List query;
//
//    public static void connector(Context context) {
//        youtube = new YouTube.Builder(new NetHttpTransport(),
//                new JacksonFactory(), new HttpRequestInitializer() {
//            @Override
//            public void initialize(HttpRequest hr) throws IOException {
//
//            }
//        }).setApplicationName(context.getString(R.string.app_name))
//                .build();
//
//        try{
//            query = youtube.search().list("id,snippet")
//                    .setKey(YOUTUBE_DATA_API_KEY)
//                    .setType("video")
//                    .setMaxResults(10L)
//                    .setFields("items(id/videoId" +
//                            ",snippet/title" +
//                            ",snippet/description" +
//                            ",snippet/publishedAt" +
//                            ",snippet/thumbnails/default/url)");
//        }catch(IOException e){
//            Log.d("YC", "Could not initialize: "+e);
//        }
//    }
//
//    public static List<VideoItem> search(String keywords){
//        query.setQ(keywords);
//        try{
//            SearchListResponse response = query.execute();
//            List<SearchResult> results = response.getItems();
//
//            List<VideoItem> items = new ArrayList<>();
//            List<String> videoIds = new ArrayList<>();
//
//            for(SearchResult result:results){
//                videoIds.add(result.getId().getVideoId());
////
////                YouTube.Videos.List listVideosRequest = youtube.videos().list("snippet, recordingDetails").setId(item.getId());
////                VideoListResponse listResponse = listVideosRequest.execute();
////                List<Video> videoList = listResponse.getItems();
////                if (videoList != null) {
////                    videoList.get(0).getContentDetails().getContentRating();
////                }
////                items.add(item);
//            }
//            Joiner stringJoiner = Joiner.on(',');
//            String videoId = stringJoiner.join(videoIds);
//
//            // Call the YouTube Data API's youtube.videos.list method to
//            // retrieve the resources that represent the specified videos.
//            YouTube.Videos.List listVideosRequest = youtube.videos().list("snippet, recordingDetails").setId(videoId);
//            VideoListResponse listResponse = listVideosRequest.execute();
//
//            List<Video> videoList = listResponse.getItems();
//            for (Video video : videoList){
//                VideoItem item = new VideoItem();
//                item.setTitle(video.getSnippet().getTitle());
//                item.setDescription(video.getSnippet().getDescription());
//                item.setThumbnailURL(video.getSnippet().getThumbnails().getDefault().getUrl());
//                item.setId(video.getId());
//                video.getContentDetails().getDuration();
//            }
//            return items;
//        }catch(IOException e){
//            Log.d("YC", "Could not search: "+e);
//            return null;
//        }
//    }
}
