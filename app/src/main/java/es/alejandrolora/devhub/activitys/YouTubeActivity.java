package es.alejandrolora.devhub.activitys;

import android.os.Bundle;
import android.widget.Toast;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import es.alejandrolora.devhub.R;
import es.alejandrolora.devhub.Util.Util;

public class YouTubeActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {

    private YouTubePlayerView mYouTubePlayerView;
    private YouTubePlayer mYouTubePlayer;

    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_player);

        Bundle b = getIntent().getExtras();
        if (b != null){
            code = b.getString("code");
        }

        mYouTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtubeplayer);
        mYouTubePlayerView.initialize("AIzaSyAPhBZas7sSJ1TOjbZ1LnK4fsKufV4_voI", this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        mYouTubePlayer = youTubePlayer;
        mYouTubePlayer.setFullscreen(true);
        mYouTubePlayer.loadVideo(code);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Initialization Fail, try again", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
