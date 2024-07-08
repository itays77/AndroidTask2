package com.example.task1;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import com.example.task1.Logic.GameManager;
import com.google.android.material.button.MaterialButton;


public class MainActivity extends AppCompatActivity {

    private MaterialButton main_BTN_left;
    private MaterialButton main_BTN_right;
    private AppCompatImageView[] main_IMG_hearts;
    private AppCompatImageView[][] main_LAY_board;
    private TextView coinsTextView;
    private GameManager gameManager;
    private final Handler handler = new Handler();

    private static final int ROWS = 8;
    private static final int COLS = 5;
    private static final int PLAYER_ROW = ROWS - 1;
    private static final long OBSTACLE_DELAY = 1000; // 1 second

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        gameManager = new GameManager(3, this); // 3 initial lives
        initViews();
        startGame();
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
        coinsTextView = findViewById(R.id.coins_text_view);
    }

    private void initViews() {
        main_BTN_left.setOnClickListener(v -> {
            gameManager.movePlayer(-1);
            updateUI();
        });
        main_BTN_right.setOnClickListener(v -> {
            gameManager.movePlayer(1);
            updateUI();
        });

        updateUI();
        updateHeartDisplay();
    }

    private void startGame() {

        gameManager.playStartSound();
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
                                Toast.makeText(MainActivity.this, "Ouch! You've been hit!", Toast.LENGTH_SHORT).show();
                                break;
                            case COIN:
                                Toast.makeText(MainActivity.this, "You got a coin!", Toast.LENGTH_SHORT).show();
                                break;
                            case HEART:
                                Toast.makeText(MainActivity.this, "Extra life!", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    } else {
                        gameOver();
                        return;
                    }
                }
                updateUI();
                if (!gameManager.isGameOver()) {
                    handler.postDelayed(this, OBSTACLE_DELAY);
                }
            }
        }, OBSTACLE_DELAY);
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
                            main_LAY_board[col][row].setImageResource(R.drawable.bomb);
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
        coinsTextView.setText("Coins: " + gameManager.getCoins());
    }

    private void gameOver() {
        handler.removeCallbacksAndMessages(null);
        gameManager.stopSounds();
        Toast.makeText(this, "Game Over!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameManager.stopSounds();
    }
}