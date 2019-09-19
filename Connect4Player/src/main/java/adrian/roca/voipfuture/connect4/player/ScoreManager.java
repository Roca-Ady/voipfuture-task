package adrian.roca.voipfuture.connect4.player;

import com.voipfuture.connectfour.Board;
import com.voipfuture.connectfour.Player;

class ScoreManager {

    private static final int WIN_SCORE_VALUE  = 1000000;
    private static final int LOSS_SCORE_VALUE = -1000000;
    private static final int DRAW_SCORE_VALUE = 500000;

    int getWinningConditionScore(final Player player, final Board.WinningCondition winningCondition) {
        int score;
        if (winningCondition.isDraw) {
            score = DRAW_SCORE_VALUE;
        } else {
            score = winningCondition.player().equals(player) ? WIN_SCORE_VALUE : LOSS_SCORE_VALUE;
        }
        return score;
    }

    int calculateScoreForPlayer(final Board board, final Player player) {
        //TODO check also the diagonals and whatever is missing

        final SequenceCounter sequenceCounter = new SequenceCounter(player);
        //check horizontal
        for (int row = 0; row < board.height; row++) {
            sequenceCounter.countPoints(board.get(0, row));
            for (int col = 1; col < board.width; col++) {
                final Player current = board.get(col, row);
                if (sequenceCounter.isWinner(current)) {
                    return WIN_SCORE_VALUE;
                }
            }
        }

        //check vertical
        for (int col = 0; col < board.width; ++col) {
            sequenceCounter.countPoints(board.get(col, 0));
            for (int row = 1; row < board.height; row++) {
                if (sequenceCounter.isWinner(board.get(col, row))) {
                    return WIN_SCORE_VALUE;
                }
            }
        }

        if (board.isFull()) {
            return DRAW_SCORE_VALUE;
        }
        sequenceCounter.countPoints(null);


        return sequenceCounter.getTotalNumberPoints();
    }

    //got inspiration from com.voipfuture.connectfour.Board.Counter
    private static class SequenceCounter {

        private final Player tileOwner;
        private       int    numberOfTilesOfSameOwner;
        private       int    totalNumberPoints;

        private int currentNumberOfPoints;

        SequenceCounter(final Player tileOwner) {
            this.tileOwner = tileOwner;
        }

        void countPoints(final Player currentTile) {
            if (this.numberOfTilesOfSameOwner >= 2) {
                this.totalNumberPoints += this.currentNumberOfPoints;
            }
            this.numberOfTilesOfSameOwner = 0;
            this.currentNumberOfPoints = 0;
            this.checkTile(currentTile);
        }

        private void checkTile(final Player tile) {
            if (tile == null) {
                if (this.numberOfTilesOfSameOwner == 0) {
                    this.currentNumberOfPoints = 5;
                } else {
                    this.currentNumberOfPoints += 5;
                }
            } else if (this.tileOwner.equals(tile)) {
                this.numberOfTilesOfSameOwner++;
                this.currentNumberOfPoints += 10;
            } else {
                if (this.numberOfTilesOfSameOwner >= 2) {
                    this.totalNumberPoints += this.currentNumberOfPoints;
                }
                this.numberOfTilesOfSameOwner = 0;
                this.currentNumberOfPoints = 0;
            }
        }

        boolean isWinner(final Player currentTile) {
            this.checkTile(currentTile);
            if (this.numberOfTilesOfSameOwner >= 4) {
                this.totalNumberPoints = WIN_SCORE_VALUE;
                return true;
            }
            if (currentTile == null) {
                this.checkTile(null);
            }
            return false;
        }

        int getTotalNumberPoints() {
            return totalNumberPoints;
        }
    }
}
