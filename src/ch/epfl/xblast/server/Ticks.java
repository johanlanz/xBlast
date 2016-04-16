package ch.epfl.xblast.server;

import ch.epfl.xblast.Time;

/**
 * Interface pour les différents type de Ticks
 * 
 * @author Johan Lanzrein(257221)
 *
 */
public interface Ticks {
    /**
     * Différents "ticks" donnés par la consigne
     */
    public static final int PLAYER_DYING_TICKS = 8;
    public static final int PLAYER_INVULNERABLE_TICKS = 64;
    public static final int BOMB_FUSE_TICKS = 100;
    public static final int EXPLOSION_TICKS = 30;
    public static final int WALL_CRUMBLING_TICKS = EXPLOSION_TICKS;
    public static final int BONUS_DISAPPEARING_TICKS = EXPLOSION_TICKS;
    public static final int TICKS_PER_SECOND = 20;
    public static final int TICK_NANOSECOND_DURATION = Time.NS_PER_S/TICKS_PER_SECOND; // (NanoSec / sec )/( Ticks/ sec )  = Ticks / NanoSec (unités ok)   
    public static final int TOTAL_TICKS = TICKS_PER_SECOND*Time.S_PER_MIN*2; //Ticks/sec * Sec / min * 2min = Ticks (unités ok ) 
    
    
    
    
}
