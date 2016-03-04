package ch.epfl.xblast;
/**
 * Enum pour les directions 
 * @author Johan Lanzrein (257221)
 *
 */
public enum Direction {
    /**
     * N for nord, E for est, S for sud, W for ouest, x for none
     */
N, E, S , W; 
    /**
     * Retourne la direction opposée pour chaque direction 
     * N->S, S->N, E->W, W->E
     * @return la direction opposée
     */
    public Direction opposite(){
        
        switch(this){
        case N : 
            return S;
        case E : 
            return W;
        case S : 
            return N; 
        case W: 
            return E;
        default : 
            return null;
        }
        
    }
    /**
     * Boolean qui répond si la direction est horizontale ou non
     * true pour : E et W
     * false : sinon 
     * @return cf plus haut
     */
    public boolean isHorizontal(){
        if(this == E || this == W){
            return true;
        }else{
            return false;
        }
            
    }
    /**
     * Méthode qui retourne si la direction(this) est parrallèle a la direction
     * passée en paramètre  
     * @param that la direction a comparée avec this
     * @return true si les deux direction sont parrallèle
     *         false si elles ne le sont pas
     */
    public boolean isParallelTo(Direction that){
        if(this.isHorizontal()&&that.isHorizontal()){
            return true;
        }else{
            if(!this.isHorizontal()&&!that.isHorizontal()){
                return true;
            }else{
                return false;
            }
        }
    }
    
    
}
