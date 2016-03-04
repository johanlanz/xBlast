package ch.epfl.xblast.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BlockTest {
    @Test
    public void isFreeWorksAsIntended(){
        assertTrue(Block.FREE.isFree());
        assertFalse(Block.CRUMBLING_WALL.isFree());
        assertFalse(Block.DESTRUCTIBLE_WALL.isFree());
        assertFalse(Block.INDESTRUCTIBLE_WALL.isFree());
        
    }
    
    @Test
    public void canHostWorksAsIntended(){
        assertTrue(Block.FREE.canHostPlayer());
        assertFalse(Block.CRUMBLING_WALL.canHostPlayer());
        assertFalse(Block.DESTRUCTIBLE_WALL.canHostPlayer());
        assertFalse(Block.INDESTRUCTIBLE_WALL.canHostPlayer());
        
    }
    @Test 
    public void castsShadowWorksCorrectly(){
        assertFalse(Block.FREE.castsShadow());
        assertTrue(Block.CRUMBLING_WALL.castsShadow());
        assertTrue(Block.DESTRUCTIBLE_WALL.castsShadow());
        assertTrue(Block.INDESTRUCTIBLE_WALL.castsShadow());
    }
}
