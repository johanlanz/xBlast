package ch.epfl.xblast;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.server.Bomb;
public class BombTest {
    
    @Test
    public void BombMainConstructed(){
        Bomb b = new Bomb(PlayerID.PLAYER_1, new Cell(12,12), Sq.constant(1), 4);
        assertEquals(PlayerID.PLAYER_1, b.ownerId());
        Cell c = new Cell(12,12);
        assertEquals(c.x(), b.position().x());
        assertEquals(c.y(), b.position().y());
        assertEquals(Sq.constant(1).head(), b.fuseLengths().head());
        assertEquals(4, b.range());
    }
    @Test
    public void BombSecondaryConstructed(){
        Bomb b = new Bomb(PlayerID.PLAYER_1, new Cell(12,12), 30, 4);
        assertEquals(1, b.fuseLengths().tail());
        
    }
    @Test (expected = IllegalArgumentException.class) //IllegalArgumentException.class NullPointerException
    public void BombSecondaryConstructorThrowsException(){
        Bomb b = new Bomb(PlayerID.PLAYER_1, new Cell(12,12), -4, 4);
    }
    
    @Test
    public void explosionWorksAsIntended(){
        Bomb b = new Bomb(PlayerID.PLAYER_1, new Cell(2,3), 4, 4);
        List<Sq<Sq<Cell>>> expl = b.explosion();
        for(Sq<Sq<Cell>> outerSq : expl){
            assertEquals(b.position(), outerSq.head().head());
            System.out.println(outerSq.toString());
        }
    }

}
