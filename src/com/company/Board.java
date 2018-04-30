package com.company;

import java.util.Random;

public class Board {

    private static final Random RANDOM_GENERATOR = new Random();

    private int x;
    private int y;
    private Spot[][] board;

    public Board(int x, int y) {
        this.x = x;
        this.y = y;
        board = new Spot[x][y];
        initializeBoard();
    }

    public void mark(int x, int y) throws SpotAlreadyRevealedException {
        Spot s = board[x][y];
        if (s.revealed) {
            throw new SpotAlreadyRevealedException(s);
        }
        s.markerPlaced = !s.markerPlaced;
    }

    public void reveal(int x, int y) throws BombRevealedException, SpotAlreadyRevealedException{
        Spot s = board[x][y];
        if (s.revealed) {
            throw new SpotAlreadyRevealedException(s);
        }
        if (s.isBomb) {
            board[x][y].revealed = true;
            throw new BombRevealedException(board[x][y]);
        }
        reveal(board[x][y]);
    }

    public String renderBoard() {
        StringBuilder builder = new StringBuilder().append("  ");
        for (int i=0;  i < y; i++) {
            builder.append(" " + i);
        }
        builder.append("\n");
        builder.append(renderSeparationRow()).append("\n");

        for (int i=0; i < x; i++) {
            for (int j=0; j < y; j++) {
                if (j==0) {
                    builder.append(i + " |");
                }
                Spot s = board[i][j];
                if (s.markerPlaced) {
                    builder.append("*|");
                } else if (s.revealed) {
                    if (s.isBomb) {
                        builder.append("B|");
                    } else {
                        int numBombsNearby = board[i][j].numBombsWithinSight;
                        builder.append(numBombsNearby>0 ? numBombsNearby : " ").append("|");
                    }
                } else {
                    builder.append("?|");
                }
                if (j==(y-1)) {
                    builder.append(" " + i);
                }
            }
            builder.append("\n").append(renderSeparationRow()).append("\n");
        }
        return builder.toString();
    }

    private String renderSeparationRow() {
        StringBuilder builder = new StringBuilder();
        builder.append("  -");
        for (int j=0; j < y; j++) {
            builder.append("--");
        }
        return builder.toString();
    }

    private int findBombsNearby(Spot s) {
        int bombsNearby=0;
        for (int i=s.x-1; i <= s.x+1; i++) {
            for (int j=s.y-1; j <= s.y+1; j++) {
                if (i >= 0 && j >=0 && i <= x-1 && j <= y-1) {
                    if (board[i][j] != s && board[i][j].isBomb) {
                        bombsNearby++;
                    }
                }
            }
        }
        return bombsNearby;
    }

    private void reveal(Spot s) {
        if (s.revealed) {
            return;
        }
        s.revealed = true;
        s.numBombsWithinSight = findBombsNearby(s);
        if (s.numBombsWithinSight == 0) {
            for (int i=s.x-1; i <= s.x+1; i++) {
                for (int j=s.y-1; j <= s.y+1; j++) {
                    if (i >= 0 && j >=0 && i <= x-1 && j <= y-1) {
                        reveal(board[i][j]);
                    }
                }
            }
        }
    }

    public void revealAll() {
        for (int i=0; i <=this.x-1; i++) {
            for (int j=0; j <= this.y-1; j++) {
                reveal(board[i][j]);
            }
        }
    }

    private void initializeBoard() {
        for (int i=0; i < x; i++) {
            for (int j=0; j < y; j++) {
                if ((Math.abs(RANDOM_GENERATOR.nextInt()) % 20) <= 2) {
                    board[i][j] = new Spot(i, j, true);
                } else {
                    board[i][j] = new Spot(i, j, false);
                }
            }
        }
    }

    private static class Spot {
        private final int x, y;
        private final boolean isBomb;
        private Integer numBombsWithinSight;
        private boolean markerPlaced;
        private boolean revealed = false;

        private Spot(int x, int y, boolean isBomb) {
            this.isBomb = isBomb;
            this.x = x;
            this.y = y;
        }
    }


    public static class SpotAlreadyRevealedException extends Exception {
        private final Spot s;

        private SpotAlreadyRevealedException(Spot s) {
            this.s = s;
        }

        public Spot getSpot() {
            return s;
        }
    }

    public static class BombRevealedException extends Exception {
        private final Spot s;

        private BombRevealedException(Spot s) {
            this.s = s;
        }

        public Spot getSpot() {
            return s;
        }
    }
}
