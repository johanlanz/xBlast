package ch.epfl.xblast.server;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.SubCell;
import ch.epfl.xblast.server.Player.DirectedPosition;
import ch.epfl.xblast.server.Player.LifeState;
import ch.epfl.xblast.server.Player.LifeState.State;
import ch.epfl.xblast.server.Player.LifeState;

public class PlayerTest {

    @Test
    public void playerIsConstructed(){
        Sq<LifeState> ls = Sq.constant(new LifeState(3, State.VULNERABLE));
        Sq<DirectedPosition> dp = Sq.constant(new DirectedPosition(new SubCell(34,34), Direction.S));
        int maxBombs = 3; 
        int bombRange = 4;
        int lives = 4;
        Cell position = new Cell(3,4);
        Player p = new Player(PlayerID.PLAYER_1, ls, dp, maxBombs, bombRange);
        Player pSecondary = new Player(PlayerID.PLAYER_2, lives, position, maxBombs, bombRange);
        
        assertEquals(State.INVULNERABLE, pSecondary.lifeState().state());
        assertEquals(SubCell.centralSubCellOf(position), pSecondary.directedPositions().head().position());
        assertEquals(Direction.S, pSecondary.direction());
        
    }
    /**
     * commenter/décommenter les lignes pour tout tester
     *ou ajouter/enlever null dans le constucteur
     */
    @Test (expected = NullPointerException.class )
    public void throwsNullPointerException(){
        Sq<LifeState> ls = Sq.constant(new LifeState(3, State.VULNERABLE));
        Sq<DirectedPosition> dp = Sq.constant(new DirectedPosition(new SubCell(34,34), Direction.S));
        int maxBombs = 3; 
        int bombRange = 4;
        int lives = 4;
        Cell position = new Cell(3,4);
        //Player p = new Player(PlayerID.PLAYER_1, ls, dp, maxBombs, bombRange);
        Player pSecondary = new Player(PlayerID.PLAYER_2, lives, null, maxBombs, bombRange);
    }
    
    /*
     * comme avant pour cette méthode
     */
    @Test (expected = IllegalArgumentException.class)
    public void throwsIllegalArgumentException(){
        Sq<LifeState> ls = Sq.constant(new LifeState(3, State.VULNERABLE));
        Sq<DirectedPosition> dp = Sq.constant(new DirectedPosition(new SubCell(34,34), Direction.S));
        int maxBombs = 3; 
        int bombRange = 4;
        int lives = -4;
        Cell position = new Cell(3,4);
        //Player p = new Player(PlayerID.PLAYER_1, ls, dp, maxBombs, bombRange);
        Player pSecondary = new Player(PlayerID.PLAYER_2, lives, position, maxBombs, bombRange);
    }
    
    @Test
    public void statesForNextLifeBasicCase(){
        
        int maxBombs = 3; 
        int bombRange = 4;
        int lives = 4;
        Cell position = new Cell(3,4);
        
        Player pSecondary = new Player(PlayerID.PLAYER_2, lives, position, maxBombs, bombRange);
        Sq<LifeState> newLife = pSecondary.statesForNextLife();
        assertEquals(State.DYING, newLife.head().state());
        assertEquals(pSecondary.lives(), newLife.head().lives());
        //TODO : comment tester la suite ???
        
    }
    
    @Test
    public void modifyingBombsAndRange(){
        int maxBombs = 3; 
        int bombRange = 4;
        int lives = 4;
        Cell position = new Cell(3,4);
        
        Player pSecondary = new Player(PlayerID.PLAYER_2, lives, position, maxBombs, bombRange);
        Player moreBombs = pSecondary.withMaxBombs(5);
        assertEquals(5, moreBombs.maxBombs());
        Player moreRange = pSecondary.withBombRange(6);
        assertEquals(6, moreRange.bombRange());
        
        
    }
    @Test
    public void bombIsPosed(){
        int maxBombs = 3; 
        int bombRange = 4;
        int lives = 4;
        Cell position = new Cell(3,4);
        
        Player pSecondary = new Player(PlayerID.PLAYER_2, lives, position, maxBombs, bombRange);
        Bomb b = pSecondary.newBomb();
        assertEquals(pSecondary.position().containingCell(), b.position());
        assertEquals(pSecondary.bombRange(), b.range());
        assertEquals(Ticks.BOMB_FUSE_TICKS, b.fuseLength());
    }
    @Test
    public void directedPositionMoving(){
        Player p = new Player(PlayerID.PLAYER_1, 4, new Cell(1,1), 3, 4);
        
        Sq<DirectedPosition> moves = DirectedPosition.moving(p.directedPositions().head());
        for(int i = 0; i<10000; i++){
            System.out.println("Dir : "+moves.head().direction().toString()+" Pos : " +moves.head().position().toString());
            moves = moves.tail();
        }
    }
    
    
}
