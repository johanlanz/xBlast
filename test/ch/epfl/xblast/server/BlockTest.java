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
        assertTrue(Block.BONUS_BOMB.canHostPlayer());
        assertTrue(Block.BONUS_RANGE.canHostPlayer());
    }
    @Test 
    public void castsShadowWorksCorrectly(){
        assertFalse(Block.FREE.castsShadow());
        assertTrue(Block.CRUMBLING_WALL.castsShadow());
        assertTrue(Block.DESTRUCTIBLE_WALL.castsShadow());
        assertTrue(Block.INDESTRUCTIBLE_WALL.castsShadow());
        assertFalse(Block.BONUS_BOMB.castsShadow());
        assertFalse(Block.BONUS_RANGE.castsShadow());
    }
    
    @Test
    public void isBonusWorksCorrectly(){
        assertFalse(Block.FREE.isBonus());
        assertFalse(Block.CRUMBLING_WALL.isBonus());
        assertFalse(Block.DESTRUCTIBLE_WALL.isBonus());
        assertFalse(Block.INDESTRUCTIBLE_WALL.isBonus());
        assertTrue(Block.BONUS_BOMB.isBonus());
        assertTrue(Block.BONUS_RANGE.isBonus());
    }
}
