package ch.epfl.xblast.server.debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.server.Block;
import ch.epfl.xblast.server.Board;
import ch.epfl.xblast.server.GameState;
import ch.epfl.xblast.server.Player;

public class RandomGame {

    public static void main(String[] args) throws InterruptedException{
        RandomEventGenerator randomness =new RandomEventGenerator(2016, 30, 100);
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
        
        int maxBombs = 2; 
        int bombRange = 3;
        int lives = 4;
        Player p1 = new Player(PlayerID.PLAYER_1, lives, new Cell(1,1), maxBombs, bombRange);
        Player p2 = new Player(PlayerID.PLAYER_2, lives, new Cell (13, 1), maxBombs, bombRange);
        Player p3 = new Player(PlayerID.PLAYER_3, lives, new Cell(13, 11), maxBombs, bombRange);
        Player p4 = new Player(PlayerID.PLAYER_4, lives, new Cell(1,11), maxBombs, bombRange);
        List<Player> players = new ArrayList<Player>();
        players.add(p1);players.add(p2);players.add(p3);players.add(p4);
        
        
        GameState game = new GameState(board, players);
        int j = 0;
        while(!game.isGameOver()){
            GameStatePrinter.printGameState(game);
            int i = 1;
            /*for(Player p : game.players()){
            System.out.println("J"+i+" : "+p.lives()+" vies ("+p.lifeState().state()+")" );
            System.out.println("    bombes max : "+p.maxBombs()+", portée : "+p.bombRange());
            System.out.println("position : "+p.position().containingCell().toString()+"Distance de la sous case centrale = " + p.position().distanceToCentral());
            
            i++;
            }*/
            
            System.out.println("Temps restant : "+game.remainingTime()+" s.");
            game = game.next(randomness.randomSpeedChangeEvents(), randomness.randomBombDropEvents());
            
            
           
            
            
            System.out.println("Tick : "+j);
            j++;
            Thread.sleep(40);
            System.out.print("\033[2J\033[;H");// effacement de la console si nécessaire enlever les // devant la ligne
            
        };
        
        System.out.println("Game over");

}
}
