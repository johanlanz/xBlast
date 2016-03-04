package ch.epfl.xblast.server;

import java.util.Objects;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.ArgumentChecker;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;

/**
 * 
 * @author Johan Lanzrein (257221) 
 *
 */
public final class Bomb {
    private PlayerID ownerID; 
    private Cell position;
    private Sq<Integer> fuseLengths;
    private int range;
    
    public Bomb(PlayerID ownerId, Cell position, Sq<Integer> fuseLengths, int range){
        
        this.ownerID = Objects.requireNonNull(ownerId);
        this.position = Objects.requireNonNull(position);//TODO : copie plus sure ?: new Cell(position.x(), position.y())
        Objects.requireNonNull(fuseLengths.head()); // TODO : pas la bonne exception..
        this.fuseLengths = Objects.requireNonNull(fuseLengths);//TODO : copie plus sure
        this.range = ArgumentChecker.requireNonNegative(range);
        
    }
}
