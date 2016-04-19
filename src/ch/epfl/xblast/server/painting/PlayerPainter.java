package ch.epfl.xblast.server.painting;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.server.Player;
import ch.epfl.xblast.server.Player.LifeState;
import ch.epfl.xblast.server.Player.LifeState.State;

/**
 * 
 * @author Johan Lanzrein (257221)
 *
 */
public final class PlayerPainter {
    private static final byte BYTE_FOR_DEAD = 15;
    private PlayerPainter(){};
    
    public static byte byteForPlayer(int tick, Player player){
        Direction dir = player.direction();
        
        if(player.isAlive()){
            Sq<LifeState> mayBeEmpty = player.lifeStates().takeWhile(l -> l.state() == State.DYING);
            int number =  3*dir.ordinal()+10*player.id().ordinal();

         if(mayBeEmpty.isEmpty()){
            
        
        if(player.lifeState().state() == State.INVULNERABLE && tick % 2 == 1){
            number = 80 + 3*dir.ordinal();
        }
        
        int modular = (dir.isHorizontal() ? player.position().x() : player.position().y()) % 4;
        
            if(modular % 2 == 0){
                return (byte)number;
            }
            return (byte) (modular == 1 ? number+1 : number+2);
        }
         
         return (byte)(player.lives() > 1 ? number+12 : number+13);
        
    }
        
        
        
        return BYTE_FOR_DEAD;
        
        
    }
    
   
}
