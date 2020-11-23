import java.util.Arrays;
import java.util.HashMap;

/**
 * An intelligent player for the game of TicTacToe.
 */
public class AI extends Player {
    private HashMap<Board, Outcome[]> outcomeData;
    private int[] savedMoveStateHistory;
    private Board[] savedBoardHistory;
    private int index;


    public AI() {
        savedBoardHistory = new Board[10];
        savedMoveStateHistory = new int[10];
        outcomeData = new HashMap<>();
        index = 0;
    }

    /**
     * <p>
     * This method is called by the TicTacToe class when a new game begins.
     * Any work that is done to prepare for a new game should be done here.
     * However, you should *not* reset any outcome data that you have already
     * obtained.
     * <p>
     * That is, outcome data should persists across multiple individual games
     * (otherwise your statistics will be really boring!), but there may
     * still be things you need to do when a new game starts. If so, do them
     * here.
     *
     * @param dim       the width (also height) of the board
     * @param playernum 1 if player 1; 2 if player 2
     * @param q         true if the player should be "quiet" during play (to minimize output)
     */

    public void startGame(int dim, int playernum, boolean q) {
        //Initialize a empty hashMap, hashMap size default is 16, we can just leave it like this
        super.startGame(dim, playernum, q);
        index = 0;
        Arrays.fill(savedMoveStateHistory, -1); //Fill up the values of the Array to negative one so I know what to skip
        Arrays.fill(savedBoardHistory, null); //Fill it up with nulls as well, same logic as above
        return;
    }

    /**
     * <p>
     * This method is called by the TicTacToe class when the
     * player is being asked for a move.  The current Board
     * (state) is passed in, and the player should respond
     * with an integer value corresponding to the cell it
     * would like to move into. (cell numbers start at 0
     * and increase from left to right and top to bottom).
     *
     * @param board the current board
     * @return the cell the player would like to move into
     */
    public int requestMove(Board board) {
        //Look through the hashtable if there a board state saved and make a move there, else you would want to store the board state
        //Make a random move
        //BoardState is the hashCode which is the key
        //This is like the get function
        int randomMove = super.requestMove(board);


        if (board.getMostRecentCellOccupied() == -1) { //If the AI goes first
            savedBoardHistory[index] = board; //Insert that board
            index++;

        } else if (index == 0) { //If the player goes first, insert the empty board to my array
            Board insertEmptyBoard = new Board();
            savedBoardHistory[index] = insertEmptyBoard;
            index++;
        }
        if (index == 1 && board.getMostRecentCellOccupied() == -1) { // If we are at index one and the AI goes, record the AI and Player move into the AI arrays
            Board oneAhead = board.move(getMarker(), randomMove); //Call the other player and steal their data because we are mean and can do that
            savedBoardHistory[index] = oneAhead;
            savedMoveStateHistory[index] = randomMove;
            index++;


        } else {
            copyAndPasteBoard(board, randomMove);
        }

        return randomMove;
    }


    public void copyAndPasteBoard(Board board, int move) {
        savedBoardHistory[index] = board; //Player
        savedMoveStateHistory[index] = board.getMostRecentCellOccupied(); //AI
        index++;
        Board oneAhead = board.move(getMarker(), move);
        savedBoardHistory[index] = oneAhead; //Player
        savedMoveStateHistory[index] = move; //AI
        index++;
    }


    /**
     * <p>
     * This method is called by the TicTacToe game when a
     * game completes.  It passes in the final board state
     * (since this player may not know what it is, if the
     * opponent moved last) and the winner.  Note that
     * the argument winner, is not necessarily consistent
     * with the winner obtianed via b.getWinner() because
     * it is possible that the winner was declared by
     * disqualification. In that case, the TicTacToe game
     * declares the winner (passed here as the argument winner)
     * but b.getWinner() will return -1 (since the game appears
     * incomplete).
     * <p>
     * Any work to compute outcomes should probably happen here
     * once you know how the game ended.
     *
     * @param b
     * @param winner
     */
    public void endGame(Board b, int winner) {
        Outcome checkOutcome;


        if (winner == 2) {
            savedBoardHistory[index] = b; //Player
            savedMoveStateHistory[index] = b.getMostRecentCellOccupied();
            index++;
        }
        super.endGame(b, winner);

        for (int i = 0; i < savedBoardHistory.length; i++) { //If the move is already there in our data
            if (savedMoveStateHistory[i] == -1 && (i != 0)) { //Skip the first index and the -1 in my Arrays
                continue;
            }
            if (savedBoardHistory[i] != null) { // Make sure the board history is in our game

                if (outcomeData.get(savedBoardHistory[i]) != null) { //Need an AND STATEMENT FOR THIS FOR LOOP in order to make sure the index is filled with an actual object
                    Outcome[] tempOutcome = outcomeData.get(savedBoardHistory[i]);
                    if (i != 9) { //To keep it from going index out of bounds
                        if (savedMoveStateHistory[i + 1] != -1) { //Make sure the thing one ahead is not = -1
                            checkOutcome = tempOutcome[(savedMoveStateHistory[i + 1])];
                            checkOutcome.attempts += 1; //This is where the null is
                            if (winner == 1) { //Edit the values of the outcome
                                checkOutcome.p1wins += 1;
                            } else if (winner == 2) {
                                checkOutcome.p2wins += 1;
                            }
                        }
                    }
                } else if (outcomeData.get(savedBoardHistory[i]) == null) { // If the move is not already in our data

                    Outcome[] finalFINALoutcome = new Outcome[9];
                    for (int jt = 0; jt < 9; jt++) { //Put outcomes in my Array
                        finalFINALoutcome[jt] = new Outcome();
                    }
                    outcomeData.put(savedBoardHistory[i], finalFINALoutcome);
                    Outcome[] tempOutcome = outcomeData.get(savedBoardHistory[i]);
                    if (i != 9) {
                        if (savedMoveStateHistory[i + 1] != -1) {
                            checkOutcome = tempOutcome[(savedMoveStateHistory[i + 1])];
                            checkOutcome.attempts += 1; //Then I will edit the values for in my new outcome objects
                            if (winner == 1) {
                                checkOutcome.p1wins += 1;
                            } else if (winner == 2) {
                                checkOutcome.p2wins += 1;
                            }
                        }
                    }

                }
            }



        }


        return;

    }


    /**
     * <p>
     * Retrieve an outcome for a particular move from a particular board
     * state. If that board/move combination was never encountered, return
     * null.
     *
     * @param state
     * @param move
     * @return
     */
    public Outcome getOutcomeForMove(Board state, int move) {
        //If the outcome ever encounted, else return null
        if (outcomeData.get(state) != null) {
            Outcome[] tempOutcome = outcomeData.get(state);
            return tempOutcome[move];
        }

        return null;
    }


}