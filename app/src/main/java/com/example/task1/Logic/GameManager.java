package com.example.task1.Logic;


public class GameManager {
    private int lives;
    private boolean[][] obstacles;
    private static final int ROWS = 5;
    private static final int COLS = 3;
    private int playerCol;
    private int tickCounter;
    private static final int OBSTACLE_SPAWN_INTERVAL = 2;

    public GameManager(int initialLives) {
        this.lives = initialLives;
        this.obstacles = new boolean[ROWS][COLS];
        this.playerCol = 1; // Start in the middle column
        this.tickCounter = 0;
    }

    public void hitObstacle() {
        lives--;
    }

    public boolean isGameOver() {
        return lives <= 0;
    }

    public int getLives() {
        return lives;
    }

    public void setObstacle(int row, int col, boolean hasObstacle) {
        obstacles[row][col] = hasObstacle;
    }

    public boolean hasObstacle(int row, int col) {
        return obstacles[row][col];
    }

    public void moveObstacles() {
        for (int col = 0; col < COLS; col++) {
            for (int row = ROWS - 1; row > 0; row--) {
                obstacles[row][col] = obstacles[row - 1][col];
            }
            obstacles[0][col] = false;
        }
    }

    public void spawnObstacle() {
        int randomCol = (int) (Math.random() * COLS);
        obstacles[0][randomCol] = true;
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
            spawnObstacle();
        }
    }

    public boolean checkCollision() {
        return obstacles[ROWS - 1][playerCol];
    }
}