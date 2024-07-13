package com.example.task1;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.task1.Enums.GameModes;
import com.example.task1.Enums.GameSpeed;
import com.example.task1.Utilities.SharePreferencesManager;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    private MaterialButton playButton;
    private MaterialButton recordsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharePreferencesManager.init(getApplicationContext());

        playButton = findViewById(R.id.play_button);
        recordsButton = findViewById(R.id.records_button);

        playButton.setOnClickListener(v -> showGameModeDialog());
        recordsButton.setOnClickListener(v -> openRecordsActivity());
    }

    private void openRecordsActivity() {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }

    private void showGameModeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Game Mode")
                .setItems(new CharSequence[]{"Buttons Mode", "Sensor Mode"}, (dialog, which) -> {
                    if (which == 0) {
                        showSpeedDialog(GameModes.BUTTONS);
                    } else {
                        showSpeedDialog(GameModes.SENSOR);
                    }
                });
        builder.create().show();
    }

    private void showSpeedDialog(GameModes mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Game Speed")
                .setItems(new CharSequence[]{"Slow", "Fast"}, (dialog, which) -> {
                    GameSpeed speed = (which == 0) ? GameSpeed.SLOW : GameSpeed.FAST;
                    startGame(mode, speed);
                });
        builder.create().show();
    }

    private void startGame(GameModes mode, GameSpeed speed) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("GAME_MODE", mode);
        intent.putExtra("GAME_SPEED", speed);
        intent.putExtra("PLAY_START_SOUND", true);  // Add this line
        startActivity(intent);
    }
}