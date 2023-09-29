package nl.openvalue.io;

import nl.openvalue.io.model.Pool;
import nl.openvalue.rps.Move;
import nl.openvalue.rps.RpsBot;

import static nl.openvalue.io.GameUtils.oneHotEncoding;
import static nl.openvalue.io.Main.aiMoves;
import static nl.openvalue.io.Main.initializeRun;
import static nl.openvalue.io.Main.nextRound;
import static nl.openvalue.io.Main.playerMoves;

public class NEATBot implements RpsBot {

    private static final Pool pool = Pool.getInstance();

    @Override
    public String getName() {
        return "NEATbot";
    }

    @Override
    public Move nextMove() {
        if (pool.getCurrentRound() == 1){
            initializeRun();
        }
        Move move = nextRound();
        playerMoves.add(oneHotEncoding(move));
        return move;
    }

    @Override
    public void opponentPlayed(Move move) {
        aiMoves.add(oneHotEncoding(move));
    }
}
