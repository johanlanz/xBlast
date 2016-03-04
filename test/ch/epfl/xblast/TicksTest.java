package ch.epfl.xblast.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class TicksTest {
    @Test 
    public void ticksAreCorrect(){
        assertEquals(Ticks.BOMB_FUSE_TICKS, 100);
        assertEquals(Ticks.PLAYER_DYING_TICKS, 8);
        assertEquals(Ticks.PLAYER_INVULNERABLE_TICKS, 64);
        assertEquals(Ticks.EXPLOSION_TICKS, 30);
        assertEquals(Ticks.WALL_CRUMBLING_TICKS, 30);
        assertEquals(Ticks.BONUS_DISAPPEARING_TICKS, 30);
        
    }
}
