package com.company;

import java.util.Scanner;

public class Minesweeper {

    private Board board;
    private GameState state = GameState.INITIAL;

    private Minesweeper() {}

    private void run() {
        while (state != null) {
            state = state.execute(this);
        }
    }

    public static void main(String[] args) throws Exception {
	    Minesweeper game = new Minesweeper();
	    game.run();
    }

    private enum GameState {
        INITIAL {
            @Override
            GameState execute(Minesweeper game) {
                System.out.println("Welcome to Minesweeper. What size of board should we use (x,y)?");
                Scanner scanner = new Scanner(System.in);
                String s = scanner.next();
                String[] size = s.split(",");
                //String[] size = scanner.next("[0-9]{1},[0-9]{1}").split(",");
                game.board = new Board(Integer.valueOf(size[0]), Integer.valueOf(size[1]));
                return WAIT_FOR_MOVE;
            }
        }, WAIT_FOR_MOVE {
            @Override
            GameState execute(Minesweeper game) {
                System.out.println("********************************\n\n");
                System.out.println(game.board.renderBoard());
                System.out.println("Where to go next? (x,y),*; '*' can be R==reveal, M==mark'");
                String[] move = new Scanner(System.in).next("[0-9],[0-9],[RM]").split(",");
                int x = Integer.valueOf(move[0]);
                int y = Integer.valueOf(move[1]);
                GameState next = null;
                if ("R".equals(move[2])) {
                    try {
                        game.board.reveal(x, y);
                        next = WAIT_FOR_MOVE;
                    } catch (Board.BombRevealedException bre) {
                        next = GameState.FAILED;
                    } catch (Board.SpotAlreadyRevealedException sare) {
                        System.out.println(" That spot has already been played");
                        next = WAIT_FOR_MOVE;
                    }
                } else {
                    try {
                        game.board.mark(x, y);
                    } catch (Board.SpotAlreadyRevealedException sare) {
                        System.out.println(" That spot has already been played");
                    }
                    next = WAIT_FOR_MOVE;
                }
                return next;
            }
        }, FAILED {
            @Override
            GameState execute(Minesweeper game) {
                game.board.revealAll();
                System.out.println("You Lost!!!");
                System.out.println(game.board.renderBoard());
                return null;
            }
        }, SUCCESS {
            @Override
            GameState execute(Minesweeper game) {
                return null;
            }
        };

        abstract GameState execute(Minesweeper game);

    }

}
