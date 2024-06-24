package com.example.task1;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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
    private GameManager gameManager;
    private final Handler handler = new Handler();

    private static final int ROWS = 5;
    private static final int COLS = 3;
    private static final int PLAYER_ROW = ROWS - 1;
    private static final long OBSTACLE_DELAY = 1000; // 1 second

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        gameManager = new GameManager(main_IMG_hearts.length);
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
        main_LAY_board = new AppCompatImageView[][]{
                {findViewById(R.id.main_IMG_board1), findViewById(R.id.main_IMG_board2), findViewById(R.id.main_IMG_board3), findViewById(R.id.main_IMG_board4), findViewById(R.id.main_IMG_board5)},
                {findViewById(R.id.main_IMG_board6), findViewById(R.id.main_IMG_board7), findViewById(R.id.main_IMG_board8), findViewById(R.id.main_IMG_board9), findViewById(R.id.main_IMG_board10)},
                {findViewById(R.id.main_IMG_board11), findViewById(R.id.main_IMG_board12), findViewById(R.id.main_IMG_board13), findViewById(R.id.main_IMG_board14), findViewById(R.id.main_IMG_board15)},
        };
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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gameManager.tick();
                if (gameManager.checkCollision()) {
                    gameManager.hitObstacle();
                    updateHeartDisplay();
                    if (!gameManager.isGameOver()) {
                        Toast.makeText(MainActivity.this, "Ouch! You've been hit!", Toast.LENGTH_SHORT).show();
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
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (row == PLAYER_ROW && col == gameManager.getPlayerCol()) {
                    main_LAY_board[col][row].setImageResource(R.drawable.police);
                    main_LAY_board[col][row].setVisibility(View.VISIBLE);
                } else {
                    main_LAY_board[col][row].setImageResource(R.drawable.bomb);
                    main_LAY_board[col][row].setVisibility(
                            gameManager.hasObstacle(row, col) ? View.VISIBLE : View.INVISIBLE
                    );
                }
            }
        }
    }

    private void gameOver() {
        handler.removeCallbacksAndMessages(null);
        Toast.makeText(this, "Game Over!", Toast.LENGTH_LONG).show();
    }
}