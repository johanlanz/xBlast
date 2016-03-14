package ch.epfl.xblast.server;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;
public class BonusTest {
    @Test
    public void increaseBombCorrectly(){
        int maxBombs = 3; 
        int bombRange = 4;
        int lives = 4;
        Player p1 = new Player(PlayerID.PLAYER_1, lives, new Cell(1,1), maxBombs, bombRange);
        Player pWithBombs = Bonus.INC_BOMB.applyTo(p1);
        Player pWithRange = Bonus.INC_RANGE.applyTo(p1);
        assertEquals(maxBombs+1, pWithBombs.maxBombs());
        assertEquals(bombRange+1, pWithRange.bombRange());
        
    }
    @Test
    public void maxRangeAndBombDontIncrease(){
        int maxBombs = 9; 
        int bombRange = 9;
        int lives = 4;
        Player p1 = new Player(PlayerID.PLAYER_1, lives, new Cell(1,1), maxBombs, bombRange);
        Player pWithBombs = Bonus.INC_BOMB.applyTo(p1);
        Player pWithRange = Bonus.INC_RANGE.applyTo(p1);
        assertEquals(maxBombs, pWithBombs.maxBombs());
        assertEquals(bombRange, pWithRange.bombRange());
    }
    
}
