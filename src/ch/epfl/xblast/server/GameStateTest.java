package ch.epfl.xblast.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
    
    /*@Test
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
        
        
    }*/
    
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
    
    
    @Test
    public void easyMethodsOfEtape5(){
        
    }
    @Test
    public void particulesOfBlast(){
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
        int bombRange = 5;
        int lives = 4;
        Player p1 = new Player(PlayerID.PLAYER_1, lives, new Cell(1,1), maxBombs, bombRange);
        Player p2 = new Player(PlayerID.PLAYER_2, lives, new Cell (13, 1), maxBombs, bombRange);
        Player p3 = new Player(PlayerID.PLAYER_3, lives, new Cell(1, 11), maxBombs, bombRange);
        Player p4 = new Player(PlayerID.PLAYER_4, lives, new Cell(13,11), maxBombs, bombRange);
        List<Player> players = new ArrayList<Player>();
        players.add(p1);players.add(p2);players.add(p3);players.add(p4);
        List<Bomb> bombs = new ArrayList<Bomb>();
        Bomb b1 = new Bomb(PlayerID.PLAYER_1, new Cell (7,5), Ticks.BOMB_FUSE_TICKS, bombRange);
        Bomb b2 = new Bomb(PlayerID.PLAYER_2, new Cell (13,9), Ticks.BOMB_FUSE_TICKS, bombRange);
        Bomb b3 = new Bomb(PlayerID.PLAYER_3, new Cell (1,10), Ticks.BOMB_FUSE_TICKS, bombRange);
        Bomb b4 = new Bomb(PlayerID.PLAYER_4, new Cell (13,10), Ticks.BOMB_FUSE_TICKS, bombRange);
        Bomb bExplosedByB1 =new Bomb(PlayerID.PLAYER_4, new Cell (7,7), Ticks.BOMB_FUSE_TICKS, bombRange);
        Bomb bForBonus = new Bomb(PlayerID.PLAYER_4, new Cell (1,7), Ticks.BOMB_FUSE_TICKS+40, bombRange);

        /*bombs.add(b1);bombs.add(b2)*/;bombs.add(b3);bombs.add(b4);bombs.add(bExplosedByB1);bombs.add(bForBonus);
        
        
        
        
        
        List<Sq<Sq<Cell>>> explosions = new ArrayList<Sq<Sq<Cell>>>();
        explosions.addAll(b1.explosion());//explosions.addAll(b2.explosion());
        GameState gs = new GameState(0, board, players, bombs, explosions, new ArrayList<Sq<Cell>>());
        for(int i = 0; i< 220; i++ ){
            GameStatePrinter.printGameState(gs);
            gs = gs.next(null, new HashSet<PlayerID>());
            System.out.println(i);
            
        }
        
        
    }
    
    
    
    
}
