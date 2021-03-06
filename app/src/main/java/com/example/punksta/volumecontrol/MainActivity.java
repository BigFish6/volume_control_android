package com.example.punksta.volumecontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.punksta.apps.libs.VolumeControl;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private VolumeControl control;
    private List<TypeListener> volumeListeners = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout scrollView = (LinearLayout) findViewById(R.id.audio_types_holder);

        LayoutInflater inflater = getLayoutInflater();
        control = new VolumeControl(this.getApplicationContext());

        for (final AudioType type: AudioType.values()) {
            View view = inflater.inflate(R.layout.audiu_type_view, scrollView, false);
            final TextView title = (TextView) view.findViewById(R.id.title);
            final TextView currentValue = (TextView) view.findViewById(R.id.current_value);
            final SeekBar seekBar = (SeekBar) view.findViewById(R.id.seek_bar);

            title.setText(type.displayName);

            seekBar.setMax(control.getMaxLevel(type.audioStreamName));

            final TypeListener volumeListener = new TypeListener(type.audioStreamName) {
                @Override public void onChangeIndex(int audioType, int currentLevel, int max) {
                    String str = 100 * currentLevel / max  + "%";
                    currentValue.setText(str);
                    seekBar.setProgress(currentLevel);
                }
            };

            volumeListeners.add(volumeListener);

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    control.setVolumeLevel(type.audioStreamName, progress);
                }

                @Override public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            scrollView.addView(view);
        }
    }

    @Override protected void onStart() {
        super.onStart();
        for (TypeListener listener : volumeListeners)
            control.registerVolumeListener(listener.type, listener, true);
    }

    @Override protected void onStop() {
        super.onStop();
        for (TypeListener volumeListener : volumeListeners)
            control.unRegisterVolumeListener(volumeListener.type, volumeListener);
    }


    private abstract class TypeListener implements VolumeControl.VolumeListener {
        public final int type;

        protected TypeListener(int type) {
            this.type = type;
        }
    }
}
