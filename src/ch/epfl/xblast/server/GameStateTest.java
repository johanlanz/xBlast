package ch.epfl.xblast.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.SubCell;
import ch.epfl.xblast.server.Player.DirectedPosition;
import ch.epfl.xblast.server.Player.LifeState;
import ch.epfl.xblast.server.Player.LifeState.State;
import ch.epfl.xblast.server.debug.GameStatePrinter;
public class GameStateTest {
    
    @Test
    public void constructsAGameStateAtStart(){
        Block __ = Block.FREE;
        Block XX = Block.INDESTRUCTIBLE_WALL;
        Block xx = Block.DESTRUCTIBLE_WALL;
        Board board = Board.ofQuadrantNWBlocksWalled(
                Arrays.asList(
                  Arrays.asList(__, __, __, __, __, xx, __),
                  Arrays.asList(__, XX, xx, XX, xx, XX, xx),
                  Arrays.asList(__, xx, __, __, __, xx, __),
                  Arrays.asList(xx, XX, __, XX, XX, XX, XX),
                  Arrays.asList(__, xx, __, xx, __, __, __),
                  Arrays.asList(xx, XX, xx, XX, xx, XX, __)));

        int maxBombs = 3; 
        int bombRange = 4;
        int lives = 4;
        Player p1 = new Player(PlayerID.PLAYER_1, lives, new Cell(1,1), maxBombs, bombRange);
        Player p2 = new Player(PlayerID.PLAYER_2, lives, new Cell (13, 1), maxBombs, bombRange);
        Player p3 = new Player(PlayerID.PLAYER_3, lives, new Cell(1, 11), maxBombs, bombRange);
        Player p4 = new Player(PlayerID.PLAYER_4, lives, new Cell(13,11), maxBombs, bombRange);
        List<Player> players = new ArrayList<Player>();
        players.add(p1);players.add(p2);players.add(p3);players.add(p4);
        GameState gs = new GameState(board, players);
        GameStatePrinter.printGameState(gs);
        
        
    }
    
    @Test 
    public void methodsWorkCorrectly(){
        Block __ = Block.FREE;
        Block XX = Block.INDESTRUCTIBLE_WALL;
        Block xx = Block.DESTRUCTIBLE_WALL;
        Board board = Board.ofQuadrantNWBlocksWalled(
                Arrays.asList(
                  Arrays.asList(__, __, __, __, __, xx, __),
                  Arrays.asList(__, XX, xx, XX, xx, XX, xx),
                  Arrays.asList(__, xx, __, __, __, xx, __),
                  Arrays.asList(xx, XX, __, XX, XX, XX, XX),
                  Arrays.asList(__, xx, __, xx, __, __, __),
                  Arrays.asList(xx, XX, xx, XX, xx, XX, __)));

        int maxBombs = 3; 
        int bombRange = 4;
        int lives = 4;
        Player p1 = new Player(PlayerID.PLAYER_1, lives, new Cell(1,1), maxBombs, bombRange);
        Player p2 = new Player(PlayerID.PLAYER_2, lives, new Cell (13, 1), maxBombs, bombRange);
        Player p3 = new Player(PlayerID.PLAYER_3, lives, new Cell(1, 11), maxBombs, bombRange);
        Player p4 = new Player(PlayerID.PLAYER_4, lives, new Cell(13,11), maxBombs, bombRange);
        List<Player> players = new ArrayList<Player>();
        players.add(p1);players.add(p2);players.add(p3);players.add(p4);
        GameState gs = new GameState(board, players);
        
        
        assertEquals(0, gs.ticks());
        assertFalse(gs.isGameOver());
        assertEquals(Ticks.TOTAL_TICKS/Ticks.TICKS_PER_SECOND, gs.remainingTime(), 0);
        assertEquals(Optional.empty(), gs.winner());
        assertEquals(players, gs.alivePlayers());
        assertFalse(gs.isGameOver());
        
    }
    
    
    
    
}
