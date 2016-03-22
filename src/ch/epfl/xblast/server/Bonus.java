package ch.epfl.xblast.server;
/**
 * Enum qui définit les bonus. Il y en a deux. 
 * increase bomb et increase range. 
 * Pour l'appliquer on utilise une méthode différente en fonction du bonus obtenu. 
 * 
 * @author Johan Lanzrein (257221) 
 *
 */
public enum Bonus {
    INC_BOMB{
        @Override
        /**
         * Si le player passé en paramètre a moins que 9 bombe -> return un nouveau player avec une bombe de plus
         * Sinon renvoyer le meme player
         * @param player le player a qui on applique le bonus
         * @return le player en fonction de son nombre de bombe. 
         */
        public Player applyTo(Player player){
        if(player.maxBombs()<MAX_RANGE){
            return player.withMaxBombs(player.maxBombs()+1);
        }
        return player;
        }
    },
    
    INC_RANGE{
        @Override
        /**
         * Si le player passé en paramètre a sa porté qui est plus petite que 9 -> return un nouveau player avec une portée 
         * augmenter de 1 unité
         * Sinon renvoyer le meme player
         * @param player le player a qui on applique le bonus
         * @return le player en fonction de la portée de ses bombes. 
         */
        public Player applyTo(Player player){
            if(player.bombRange()<MAX_BOMBS){
                return player.withBombRange(player.bombRange()+1);
            }
            return player;
            }
        };
    private static final int MAX_BOMBS = 9;
    private static final int MAX_RANGE = 9;
    abstract public Player applyTo(Player player);
}
