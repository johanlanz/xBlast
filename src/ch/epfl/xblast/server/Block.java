package ch.epfl.xblast.server;
/**
 * Enum pour les différents types de blocs du plateau de jeu
 * @author Johan Lanzrein (257221)
 *
 */
public enum Block {
    FREE, INDESTRUCTIBLE_WALL, DESTRUCTIBLE_WALL, CRUMBLING_WALL; 
    /**
     * Cette méthode return true si le block est FREE, false sinon.
     * @return lire la ligne précédente
     */
    public boolean isFree() {
        if(this == FREE){
            return true;
        }else{
            return false;
        }
    }
    /**
     *!\ seulement une solution provisoire!!!
     *return le résultat de isFree
     * @return true ->FREE, false -> autre cas
     */
    public boolean canHostPlayer(){
        return isFree();
    }
    /**
     * solution provisoire!!!!
     * return l'inverse de isFree();
     * -> True pour FREE, -> False sinon.
     * @return cf ligne précédente
     */
    public boolean castsShadow(){
        return !isFree();
    }
}
