package com.example.task1;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.task1.Model.GameScore;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.task1.Enums.GameSpeed;
import com.example.task1.Logic.GameManager;
import com.example.task1.Utilities.MoveDetector;
import com.example.task1.Utilities.SharePreferencesManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.example.task1.Enums.GameModes;
import com.example.task1.Enums.GameSpeed;
import com.google.android.material.textview.MaterialTextView;
import java.lang.reflect.Type;
import java.util.ArrayList;


public class GameActivity extends AppCompatActivity implements GameManager.GameManagerListener {

    private MaterialButton main_BTN_left;
    private MaterialButton main_BTN_right;
    private AppCompatImageView[] main_IMG_hearts;
    private AppCompatImageView[][] main_LAY_board;
    private TextView coinsTextView;
    private GameManager gameManager;
    private final Handler handler = new Handler();

    public static final int ROWS = 8;
    public static final int COLS = 5;
    private static final int PLAYER_ROW = ROWS - 1;
    private static final long OBSTACLE_DELAY = 1000;
    private GameModes gameMode;
    private GameSpeed gameSpeed;
    private boolean gamePaused = false;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private MaterialTextView scoreTextView;
    private boolean gameEnded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameMode = (GameModes) getIntent().getSerializableExtra("GAME_MODE");
        gameSpeed = (GameSpeed) getIntent().getSerializableExtra("GAME_SPEED");
        boolean playStartSound = getIntent().getBooleanExtra("PLAY_START_SOUND", false);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        findViews();
        gameManager = new GameManager(3, this, gameMode);
        gameManager.setListener(this);


        if (playStartSound) {
            gameManager.playStartSound();
        }

        initViews();
        gameManager.startGame();
        startGame();

        View rootView = findViewById(android.R.id.content);
        rootView.setOnClickListener(v -> {
            if (gameEnded && gameManager == null) { // Add check for null gameManager
                returnToMainMenu();
            }
        });

    }

    private void findViews() {
        main_BTN_left = findViewById(R.id.main_BTN_left);
        main_BTN_right = findViewById(R.id.main_BTN_right);
        main_IMG_hearts = new AppCompatImageView[]{
                findViewById(R.id.main_IMG_heart1),
                findViewById(R.id.main_IMG_heart2),
                findViewById(R.id.main_IMG_heart3),
        };
        main_LAY_board = new AppCompatImageView[COLS][ROWS];
        for (int col = 0; col < COLS; col++) {
            for (int row = 0; row < ROWS; row++) {
                int id = getResources().getIdentifier("main_IMG_board" + (col * ROWS + row + 1), "id", getPackageName());
                main_LAY_board[col][row] = findViewById(id);
            }
        }

        scoreTextView = findViewById(R.id.score_text_view);
    }

    private void initViews() {
        if (gameMode == GameModes.BUTTONS) {
            main_BTN_left.setOnClickListener(v -> {
                gameManager.movePlayer(-1);
                updateUI();
            });
            main_BTN_right.setOnClickListener(v -> {
                gameManager.movePlayer(1);
                updateUI();
            });
        } else {
            main_BTN_left.setVisibility(View.GONE);
            main_BTN_right.setVisibility(View.GONE);
        }

        updateUI();
        updateHeartDisplay();
        updateScoreDisplay(0);
    }

    private void updateScoreDisplay(int score) {
        scoreTextView.setText("Score: " + score);
    }

    @Override
    public void onScoreUpdated(int newScore) {
        runOnUiThread(() -> updateScoreDisplay(newScore));
    }


    private void startGame() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gameManager.tick();
                if (gameManager.checkCollision()) {
                    GameManager.ItemType collidedItemType = gameManager.getCollidedItemType();
                    gameManager.handleCollision(collidedItemType);
                    updateHeartDisplay();
                    if (!gameManager.isGameOver()) {
                        switch (collidedItemType) {
                            case BOMB:
                                Toast.makeText(GameActivity.this, "Ouch! You've been hit!", Toast.LENGTH_SHORT).show();
                                break;
                            case COIN:
                                Toast.makeText(GameActivity.this, "You got a coin!", Toast.LENGTH_SHORT).show();
                                break;
                            case HEART:
                                Toast.makeText(GameActivity.this, "Extra life!", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    } else {
                        gameOver();
                        return;
                    }
                }
                updateUI();
                if (!gameManager.isGameOver()) {
                    handler.postDelayed(this, gameSpeed.getDelay());
                }
            }
        }, gameSpeed.getDelay());
    }


    private void updateHeartDisplay() {
        for (int i = 0; i < main_IMG_hearts.length; i++) {
            main_IMG_hearts[i].setVisibility(i < gameManager.getLives() ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void updateUI() {
        for (int col = 0; col < COLS; col++) {
            for (int row = 0; row < ROWS; row++) {
                if (row == PLAYER_ROW && col == gameManager.getPlayerCol()) {
                    main_LAY_board[col][row].setImageResource(R.drawable.duke);
                    main_LAY_board[col][row].setVisibility(View.VISIBLE);
                } else {
                    GameManager.ItemType itemType = gameManager.getItemType(row, col);
                    switch (itemType) {
                        case BOMB:
                            main_LAY_board[col][row].setImageResource(R.drawable.alien);
                            main_LAY_board[col][row].setVisibility(View.VISIBLE);
                            break;
                        case HEART:
                            main_LAY_board[col][row].setImageResource(R.drawable.heart);
                            main_LAY_board[col][row].setVisibility(View.VISIBLE);
                            break;
                        case COIN:
                            main_LAY_board[col][row].setImageResource(R.drawable.coin);
                            main_LAY_board[col][row].setVisibility(View.VISIBLE);
                            break;
                        case EMPTY:
                            main_LAY_board[col][row].setVisibility(View.INVISIBLE);
                            break;
                    }
                }
            }
        }
        updateHeartDisplay();
    }

    @Override
    public void onPlayerMoved() {
        runOnUiThread(this::updateUI);
    }

    private void saveScore(int score) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    SharePreferencesManager spm = SharePreferencesManager.getInstance();
                    ArrayList<GameScore> scores = spm.getScores();
                    if (location != null) {
                        scores.add(new GameScore(score, location.getLatitude(), location.getLongitude()));
                    } else {
                        scores.add(new GameScore(score, 0, 0));
                    }
                    spm.saveScores(scores);
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveScore(gameManager.getCoins()); // Assuming coins are the score
            } else {
                SharePreferencesManager spm = SharePreferencesManager.getInstance();
                ArrayList<GameScore> scores = spm.getScores();
                scores.add(new GameScore(gameManager.getCoins(), 0, 0));
                spm.saveScores(scores);
            }
        }
    }


    private void resumeGame() {
        gamePaused = false;
        startGame(); // This will restart the game loop
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameManager != null && !gameEnded) {
            gameManager.startSensor();
            if (gamePaused) {
                resumeGame();
            }
        }
    }

    private void pauseGame() {
        handler.removeCallbacksAndMessages(null);
        gamePaused = true;

    }
    @Override
    protected void onPause() {
        super.onPause();
        if (gameManager != null && !gameEnded) {
            gameManager.stopSensor();
            pauseGame();
        }
    }


    private void cleanupGame() {
        handler.removeCallbacksAndMessages(null);
        if (gameManager != null) {
            gameManager.stopSounds();
            gameManager.stopSensor();
            gameManager.stopGame();
            if (!gameEnded) {
                saveScore(gameManager.getScore()); // Save the score if the game wasn't properly ended
            }
            gameManager = null;
        }
        gameEnded = true;
    }

    private void gameOver() {
        handler.removeCallbacksAndMessages(null);
        int finalScore = 0;
        if (gameManager != null) {
            finalScore = gameManager.getScore();
            gameManager.stopGame();
            gameManager.stopSounds();
            gameManager.stopSensor();
        }

        String message = "Game Over! Your score: " + finalScore;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        saveScore(finalScore);
        gameEnded = true;
        gameManager = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanupGame();
    }





    private void returnToMainMenu() {
        if (gameManager != null) {
            gameManager.stopSounds();
            gameManager.stopSensor(); // Add this line to ensure the sensor is stopped
        }
        gameManager = null; // Set to null after stopping everything
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}