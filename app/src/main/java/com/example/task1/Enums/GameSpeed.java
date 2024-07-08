package com.example.task1;


    public enum GameSpeed {
        SLOW(1000), FAST(500);

        private final long delay;

        GameSpeed(long delay) {
            this.delay = delay;
        }

        public long getDelay() {
            return delay;
        }
    }

