package ch.epfl.xblast.server;
/**
 * Interface pour les différents type de Ticks ( pour le moment ( 23/02/16) très vide..) 
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
    
    
}
