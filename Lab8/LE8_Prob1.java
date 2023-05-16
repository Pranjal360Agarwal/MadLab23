import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private int volume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = MediaPlayer.create(this, R.raw.song);
        seekBar = findViewById(R.id.seekBar);

        // set maximum value of seekBar to media duration
        seekBar.setMax(mediaPlayer.getDuration());

        // set onSeekBarChangeListener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // do nothing
            }
        });

        // set volume to maximum
        volume = 100;

        // set onClickListener for volume button
        findViewById(R.id.volumeButton).setOnClickListener(v -> {
            if (volume == 0) {
                volume = 100;
                mediaPlayer.setVolume(1, 1);
                Toast.makeText(this, "Volume: " + volume, Toast.LENGTH_SHORT).show();
            } else {
                volume = 0;
                mediaPlayer.setVolume(0, 0);
                Toast.makeText(this, "Muted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }
}
