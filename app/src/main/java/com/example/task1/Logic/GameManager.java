package com.example.task1.Logic;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.task1.Enums.GameModes;
import com.example.task1.Interfaces.MoveCallback;
import com.example.task1.R;
import com.example.task1.Utilities.MoveDetector;
import com.example.task1.Utilities.SoundPlayer;

public class GameManager {
    private int lives;
    private boolean[][] obstacles;
    private static final int ROWS = 8;
    private static final int COLS = 5;
    private int playerCol;
    private int tickCounter;
    private static final int OBSTACLE_SPAWN_INTERVAL = 2;
    private ItemType[][] itemTypes;
    private int coins;
    private static final int DRILL_INTERVAL = 7;
    private static final float BOMB_PROBABILITY = 0.85f; // 85% chance for bombs
    private static final float COIN_PROBABILITY = 0.10f; // 10% chance for coin
    private int bombCounter;
    private SoundPlayer soundPlayer;

    private MoveDetector moveDetector;
    private GameModes gameMode;
    private GameManagerListener listener;
    private Handler mainHandler;
    private int score;
    private long gameStartTime;
    private Handler scoreHandler = new Handler();
    private Runnable scoreRunnable;
    private Handler moveHandler;
    private static final long MOVE_DELAY = 100; // Milliseconds


//    public void startGame() {
//
//    }

    public enum ItemType {
        BOMB, HEART, COIN, EMPTY
    }

    public GameManager(int initialLives, Context context, GameModes gameMode) {
        this.lives = initialLives;
        this.obstacles = new boolean[ROWS][COLS];
        this.itemTypes = new ItemType[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                itemTypes[row][col] = ItemType.EMPTY;
            }
        }
        this.playerCol = COLS / 2; // Start in the middle column
        this.tickCounter = 0;
        this.score = 0;
        this.bombCounter = 0;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.soundPlayer = new SoundPlayer(context);
        this.gameMode = gameMode;
        if (gameMode == GameModes.SENSOR) {
            this.moveDetector = new MoveDetector(context, new MoveCallback() {
                @Override
                public void moveLeft() {
                    queueMove(-1);
                }

                @Override
                public void moveRight() {
                    queueMove(1);
                }
            });
            moveHandler = new Handler(Looper.getMainLooper());
        }

    }

    public void startGame() {
        gameStartTime = System.currentTimeMillis();
        startScoreTimer();
    }

    private void startScoreTimer() {
        scoreRunnable = new Runnable() {
            @Override
            public void run() {
                score++;
                if (listener != null) {
                    listener.onScoreUpdated(score);
                }
                scoreHandler.postDelayed(this, 1000);
            }
        };
        scoreHandler.postDelayed(scoreRunnable, 1000);
    }

    public void stopGame() {
        scoreHandler.removeCallbacks(scoreRunnable);
    }





    public interface GameManagerListener {
        void onPlayerMoved();
        void onScoreUpdated(int newScore);
    }

    public void setListener(GameManagerListener listener) {
        this.listener = listener;
    }

    public void hitObstacle() {
        lives--;
        playExplosionSound();
    }

    private void playExplosionSound() {
        soundPlayer.playSound(R.raw.bitch);
    }

    private void playGroovySound() {
        soundPlayer.playSound(R.raw.groovy);
    }


    public boolean isGameOver() {
        return lives <= 0;
    }

    public int getLives() {
        return lives;
    }

    public boolean hasObstacle(int row, int col) {
        return obstacles[row][col];
    }

    public void moveObstacles() {
        for (int col = 0; col < COLS; col++) {
            for (int row = ROWS - 1; row > 0; row--) {
                obstacles[row][col] = obstacles[row - 1][col];
                itemTypes[row][col] = itemTypes[row - 1][col];
            }
            obstacles[0][col] = false;
            itemTypes[0][col] = ItemType.EMPTY;
        }
    }

    public void spawnItem() {
        bombCounter++;
        int randomCol = (int) (Math.random() * COLS);
        ItemType itemType;

        if (bombCounter % DRILL_INTERVAL == 0) {
            itemType = Math.random() < 0.5 ? ItemType.HEART : ItemType.COIN;
        } else {
            double random = Math.random();
            if (random < BOMB_PROBABILITY) {
                itemType = ItemType.BOMB;
            } else if (random < BOMB_PROBABILITY + COIN_PROBABILITY) {
                itemType = ItemType.COIN;
            } else {
                itemType = ItemType.HEART;
            }
        }

        obstacles[0][randomCol] = true;
        itemTypes[0][randomCol] = itemType;
    }

    public void movePlayer(int direction) {
        int newCol = playerCol + direction;
        if (newCol >= 0 && newCol < COLS) {
            playerCol = newCol;
        }
    }

    public int getPlayerCol() {
        return playerCol;
    }

    public void tick() {
        tickCounter++;
        moveObstacles();
        if (tickCounter % OBSTACLE_SPAWN_INTERVAL == 0) {
            spawnItem();
        }
    }

    public boolean checkCollision() {
        return obstacles[ROWS - 1][playerCol];
    }

    public ItemType getItemType(int row, int col) {
        return itemTypes[row][col];
    }

    public ItemType getCollidedItemType() {
        return itemTypes[ROWS - 1][playerCol];
    }

    public void handleCollision(ItemType itemType) {
        switch (itemType) {
            case BOMB:
                lives--;
                playExplosionSound();
                break;
            case HEART:
                lives = Math.min(lives + 1, 3);
                playGroovySound();
                break;
            case COIN:
                score += 30;
                playGroovySound();
                break;
        }
        if (listener != null) {
            listener.onScoreUpdated(score);
        }
    }

    private void queueMove(int direction) {
        moveHandler.removeCallbacksAndMessages(null);
        moveHandler.postDelayed(() -> {
            movePlayer(direction);
            if (listener != null) {
                listener.onPlayerMoved();
            }
        }, MOVE_DELAY);
    }

    public void startSensor() {
        if (moveDetector != null) {
            moveDetector.start();
        }
    }

    public void stopSensor() {
        if (moveDetector != null) {
            moveDetector.stop();
        }
        if (moveHandler != null) {
            moveHandler.removeCallbacksAndMessages(null);
        }
    }



    public void playSound(int resourceId) {
        if (soundPlayer != null) {
            soundPlayer.playSound(resourceId);
        }
    }

    public void stopSounds() {
        if (soundPlayer != null) {
            soundPlayer.release();
            soundPlayer = null;
        }
    }
    public int getScore() {
        return score;
    }


    public int getCoins() {
        return coins;
    }

    public void playStartSound() {
        soundPlayer.playSound(R.raw.start);
    }


}