package ch.epfl.xblast.server.painting;

import ch.epfl.xblast.server.*;

/**
 * 
 * @author Johan Lanzrein (257221)
 *
 */
public final class ExplosionPainter {
    private final static byte BYTE_FOR_EMPTY = 16;
    
    private ExplosionPainter(){};
    
    public static byte byteForBomb(Bomb b){
        //TODO mieux faire -> enum aussi ? ... 
        return Integer.bitCount(b.fuseLength()) == 1 ? (byte) 20 : (byte) 21;
    }
    
    public static byte byteForBlast(boolean north, boolean east, boolean south, boolean west){
       int b = 0b0;
       if(north)
           b++;
       b =b << 1;
       if(east)
           b++;
       b= b<<1;
       if(south)
           b++;
       b = b<<1;
       if(west)
           b++;
       b = b<<1;
       return b != 0 ? (byte) b : BYTE_FOR_EMPTY;
    }
}
