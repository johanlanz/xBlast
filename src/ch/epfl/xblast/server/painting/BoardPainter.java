
package ch.epfl.xblast.server.painting;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.server.*;

/**
 * 
 * @author Johan Lanzrein (257221)
 *
 */
public final class BoardPainter {

    private final Map<Block, BlockImage> palette;
    private final BlockImage shadowForFree;
    
    public BoardPainter(Map<Block, BlockImage> palette, BlockImage shadowForFree){
        this.palette = Objects.requireNonNull(Collections.unmodifiableMap(new HashMap<Block, BlockImage>(palette)));
        this.shadowForFree = Objects.requireNonNull(shadowForFree);
    }
    
    public byte byteForCell(Board board, Cell cell){
        
        if(board == null || cell == null){//TODO demander si nécessaire
            throw new NullPointerException();
        }
        Block b = board.blockAt(cell);
        
        if(b.isFree()){
            return board.blockAt(cell.neighbor(Direction.W)).castsShadow() ? (byte) shadowForFree.ordinal() : (byte)palette.get(b).ordinal();
        }
        
        return (byte)palette.get(b).ordinal();
    }
}
