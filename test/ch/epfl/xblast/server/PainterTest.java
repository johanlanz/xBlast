package ch.epfl.xblast.server;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.SubCell;
import ch.epfl.xblast.server.Player.LifeState.State;
import ch.epfl.xblast.server.painting.*;


public final class PainterTest {

    @Test
    public void constructsBoardPainter(){
        Map<Block, BlockImage> palette = new HashMap<Block,BlockImage>();
        palette.put(Block.FREE, BlockImage.IRON_FLOOR);
        palette.put(Block.BONUS_BOMB, BlockImage.BONUS_BOMB);
        palette.put(Block.BONUS_RANGE, BlockImage.BONUS_RANGE);
        palette.put(Block.INDESTRUCTIBLE_WALL, BlockImage.DARK_BLOCK);
        palette.put(Block.DESTRUCTIBLE_WALL, BlockImage.EXTRA);
        palette.put(Block.CRUMBLING_WALL, BlockImage.EXTRA_O);
        BoardPainter boardPainter = new BoardPainter(palette, BlockImage.IRON_FLOOR_S);
        
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
        
        for(int i = 0; i<Cell.COLUMNS; i++){
            for(int j = 0; j<Cell.ROWS;j++){
                Cell c = new Cell(i,j);
                byte b = boardPainter.byteForCell(board, c);
                Block block = board.blockAt(c);
                BlockImage image = palette.get(block);
                if(block.isFree()&&board.blockAt(c.neighbor(Direction.W)).castsShadow()){
                    image = BlockImage.IRON_FLOOR_S;
                }
                
                assertEquals((byte)image.ordinal(), b);
                
                
            }
        }
        
    }
    
    @Test
    public void bombPainterTest(){
        
        for(int i =1; i<=100;i++){
            Bomb b1 = new Bomb(PlayerID.PLAYER_1, new Cell(0,0), i , 1);
            int tick = Integer.bitCount(b1.fuseLength()) == 1 ? (byte)21 : (byte)20;
            assertEquals((byte)tick,ExplosionPainter.byteForBomb(b1));
            
            
        }
    }
    @Test
    public void explosionPainterTest(){
        byte number = 0;
        boolean north = false;boolean east = false;boolean south = false;boolean west = false;
        assertEquals(number, ExplosionPainter.byteForBlast(north, east, south, west));
        number++;west =true;//1
        assertEquals(number, ExplosionPainter.byteForBlast(north, east, south, west));
        number++;west =false;south =true;//2
        assertEquals(number, ExplosionPainter.byteForBlast(north, east, south, west));
        number++;west =true;//3
        assertEquals(number, ExplosionPainter.byteForBlast(north, east, south, west));
        number++;east = false;west = south = false;east = true;//4
        assertEquals(number, ExplosionPainter.byteForBlast(north, east, south, west));
        number++;west = true;//5
        assertEquals(number, ExplosionPainter.byteForBlast(north, east, south, west));
        number++;west = false;south = true;//6
        assertEquals(number, ExplosionPainter.byteForBlast(north, east, south, west));
        number++;west = true;//7
        assertEquals(number, ExplosionPainter.byteForBlast(north, east, south, west));
        number++;west = east = south = false; north = true;//8
        assertEquals(number, ExplosionPainter.byteForBlast(north, east, south, west));
        number++;west = true;//9
        assertEquals(number, ExplosionPainter.byteForBlast(north, east, south, west));
        number++;west = false;south = true;//10
        assertEquals(number, ExplosionPainter.byteForBlast(north, east, south, west));
        number++;west = true;//11
        assertEquals(number, ExplosionPainter.byteForBlast(north, east, south, west));
        number++;south = west = false; east = true;//12
        assertEquals(number, ExplosionPainter.byteForBlast(north, east, south, west));
        number++;west = true;//13
        assertEquals(number, ExplosionPainter.byteForBlast(north, east, south, west));
        number++;west = false;south = true;//14
        assertEquals(number, ExplosionPainter.byteForBlast(north, east, south, west));
        number++;west =true;//15
        assertEquals(number, ExplosionPainter.byteForBlast(north, east, south, west));
        number++;//16
        assertEquals(number,ExplosionPainter.BYTE_FOR_EMPTY);
    }
    
    @Test
    public void playerPainterTest(){
        Player p1 = new Player(PlayerID.PLAYER_1, Sq.constant(new Player.LifeState(1,State.VULNERABLE)), Sq.constant(new Player.DirectedPosition(new SubCell(0,0), Direction.E)), 1, 1);
        
    }
}
