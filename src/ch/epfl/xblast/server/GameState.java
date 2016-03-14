package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.ArgumentChecker;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;
/**
 * Classe qui définit un état de jeu. Elle a plusieurs attributs qui vont régir l'état du jeu à 
 * chaque ticks. 
 * 
 * @author Johan Lanzrein (257221) 
 *
 */
public final class GameState {
    private int ticks;
    private Board board;
    private List<Player> players;
    private List<Bomb> bombs;
    private List<Sq<Sq<Cell>>> explosions;
    private List<Sq<Cell>> blasts;
    
    public GameState(int ticks, Board board, List<Player> players, List<Bomb> bombs, List<Sq<Sq<Cell>>> explosions, List<Sq<Cell>> blasts){
        this.ticks = ArgumentChecker.requireNonNegative(ticks);
        this.board = Objects.requireNonNull(board); // TODO constructeur de copie 
        this.players = Objects.requireNonNull(players); //TODO new arraylist<Player> -> a faire pour toute les liste pour immuable et ensuite entouré de colelctions unmodifiablelist !!
        this.bombs = Objects.requireNonNull(bombs);
        this.explosions = Objects.requireNonNull(explosions);
        this.blasts = Objects.requireNonNull(blasts);
    }
    
    public GameState(Board board, List<Player> players){
        this(0, board, players, new ArrayList<Bomb>(), new ArrayList<Sq<Sq<Cell>>>(), new ArrayList<Sq<Cell>>());
    }
    
    public int ticks(){
        return this.ticks;
    }
    
    public boolean isGameOver(){
        if(Ticks.TOTAL_TICKS == this.ticks){
            return true;
        }
        
        return alivePlayers().size()<=1 ? true : false; //TODO : tester
    }
    
    public double remainingTime(){
       return (Ticks.TOTAL_TICKS-ticks)/Ticks.TICKS_PER_SECOND; //TODO : tester 
    }
    
    public Optional<PlayerID> winner(){
        
        List<Player> playerLeft = alivePlayers();
        if(playerLeft.size()!=1){
            return Optional.empty();
        }
        return Optional.of(playerLeft.get(0).id());
    }
    
    public Board board(){
        return board;
    }
    
    public List<Player> players(){
        return new ArrayList<Player>(players);
    }
    
    public List<Player> alivePlayers(){
        List<Player> alivePlayers = new ArrayList<Player>();
        for(Player p : players){
            if(p.isAlive()){
                alivePlayers.add(p);
            }
        }
        return alivePlayers;
    }
}
