package adrian.roca.voipfuture.connect4.player;

import com.voipfuture.connectfour.Board;
import com.voipfuture.connectfour.GameState;
import com.voipfuture.connectfour.IInputProvider;
import com.voipfuture.connectfour.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ComputerPlayer implements IInputProvider {

    private static       ScoreManager SCORE_MANAGER    = new ScoreManager();

    private Player maxPlayer;
    private Player minPlayer;

    @Override
    public Optional<InputEvent> readInput(final GameState gameState) {
        //not thread safe, but it should not be a problem
        maxPlayer = gameState.currentPlayer();
        minPlayer = gameState.nextPlayer();
        return Optional.of(new IInputProvider.MoveEvent(this.maxPlayer, calculateNextMove(gameState.board)));
    }

    private List<Integer> getAvailableColumns(final Board board) {
        final List<Integer> availableColumns = new ArrayList<>(board.width);
        for (int column = 0; column < board.width; ++column) {
            if (board.hasSpaceInColumn(column)) {
                availableColumns.add(column);
            }
        }
        return availableColumns;
    }

    private int calculateNextMove(final Board board) {
        int maxScore = 0;

        final Board boardCopy = board.createCopy();
        final List<Integer> availableMoves = getAvailableColumns(board);
        // set the first move as the best, hoping it will always have an element
        int bestMove = availableMoves.remove(0);
        for (final Integer move : availableMoves) {
            final int column = boardCopy.move(move, this.maxPlayer);
            final int score = - negamax(boardCopy, this.minPlayer, 0);
            boardCopy.clear(move, column);
            if (score > maxScore) {
                bestMove = move;
                maxScore = score;
            }
        }

        return bestMove;
    }

    private int negamax(final Board board, final Player player, final int depth) {
        final Optional<Board.WinningCondition> winningConditionOptional = board.getState();

        if (winningConditionOptional.isPresent()) {
            return SCORE_MANAGER.getWinningConditionScore(player, winningConditionOptional.get());
        }

        if (depth >= maxPlayer.maxThinkDepth()) {
            return SCORE_MANAGER.calculateScoreForPlayer(board, player);
        }

        final List<Integer> moves = getAvailableColumns(board);
        final Board boardCopy = board.createCopy();
        int maxScore = Integer.MIN_VALUE;

        for (final int move : moves) {
            final int y = boardCopy.move(move, player);
            final int score = -negamax(boardCopy, getOtherPlayer(player), depth + 1);
            boardCopy.clear(move, y);
            maxScore = Math.max(maxScore, score);
        }

        return maxScore;
    }

    private Player getOtherPlayer(final Player currentPlayer) {
        return (currentPlayer == this.maxPlayer) ? this.minPlayer : this.maxPlayer;
    }
}
