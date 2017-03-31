package ch.epfl.xblast.server;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.SubCell;
import ch.epfl.xblast.server.Player.DirectedPosition;
import ch.epfl.xblast.server.Player.LifeState;
import ch.epfl.xblast.server.Player.LifeState.State;
public final class DirectedDirAndLifeStateTest {

    @Test
    public void DirectedPosAndLifeStateConstructs(){
        DirectedPosition dir = new DirectedPosition(new SubCell(44,44), Direction.S);
        LifeState ls = new LifeState(4, State.INVULNERABLE);
    }
    
    @Test
    public void methodDirectedPositionWork(){
        DirectedPosition dir = new DirectedPosition(new SubCell(44,44), Direction.S);
        Sq<DirectedPosition> stop = DirectedPosition.stopped(dir);
        assertEquals(dir, stop.head());
        Sq<DirectedPosition> moving = DirectedPosition.moving(dir);
        assertEquals(dir, moving.head());
        assertEquals(Direction.S, dir.direction());
        DirectedPosition newPos = dir.withPosition(new SubCell(44,45));
        assertEquals(dir.direction(), newPos.direction());
        SubCell sc = new SubCell(44,45);
        assertEquals(sc.x(), newPos.position().x());
        assertEquals(sc.y(), newPos.position().y());
        DirectedPosition newDir = dir.withDirection(Direction.N);
        assertEquals(Direction.N, newDir.direction());
    }
    
    @Test
    public void methodLifeStateWork(){
        LifeState ls = new LifeState(4, State.INVULNERABLE);
        assertTrue(ls.canMove());
        assertEquals(4, ls.lives());
        assertEquals(State.INVULNERABLE, ls.state());
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void ThrowsIllegalArgumentException(){
        LifeState ls = new LifeState(-4, State.DEAD);
        
    }
    /**
     * commenter ou décommenter les différentes instances pour tester
     */
    @Test (expected = NullPointerException.class)
    public void ThrowsNullPointerException(){
        //LifeState ls = new LifeState(4, null);
        //DirectedPosition nullSubCell = new DirectedPosition(null, Direction.N);
        DirectedPosition nullDirection = new DirectedPosition(new SubCell(32,32), null);
    }
}
