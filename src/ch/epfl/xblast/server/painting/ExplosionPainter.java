package ch.epfl.xblast.server.painting;

import ch.epfl.xblast.server.*;

/**
 * Classe qui définit un peintre d'explosion et de bombe. 
 * Elle est publique finale et non-instanciable(Constructeur est privé et vide)
 * 
 * @author Johan Lanzrein (257221)
 *
 */
public final class ExplosionPainter {
    public final static byte BYTE_FOR_EMPTY = 16; //Le byte pour une image vide -> un byte non valable 16 
    
    private ExplosionPainter(){};
    /**
     * Méthode qui calcule le byte d'image d'une bombe. 
     * Assez simplement si le fuseLength est une puissance de deux ( C'est à dire que il n'y a que un seul byte ) on renvoie l'image d'une bombe blanche ( byte 21) 
     * Sinon on retourne l'image d'une bombe noire ( byte 20) 
     * 
     * @param b bombe dont il faut retourner l'image
     * @return byte d'image de la bombe
     */
    public static byte byteForBomb(Bomb b){
        
                return Integer.bitCount(b.fuseLength()) == 1 ? (byte) 21 : (byte) 20;
    }
    /**
     * Méthode qui calcule le byte d'image d'une particule d'explosion. Comme les byte d'image sont donnés dans un ordre particulièrement pratique.
     * ( on peut le comparer à une table de vérité avec les variable dans l'ordre  N, E , S ,W. ) On crée une variable b qui sera le byte, un décompte count et un tableau de boolean avec north, east, south,west
     * et on itère sur ce tableau. Si une des variable est vraie on incrémente b de une unité et si le count est plus petit que 3 c'est à dire qu'on est dans les 3 première itérations, on décale b vers la gauche logique. 
     * Ainsi on obtient le byte d'image correspondant
     * 
     * @param north boolean si il y a une particule au nord
     * @param east boolean si il y a une particule au east
     * @param south boolean si il y a une particule au south
     * @param west boolean si il y a une particule au west
     * @return le byte d'image de la partibule d'explosion. 
     */
    public static byte byteForBlast(boolean north, boolean east, boolean south, boolean west){
       int b = 0b0;
       boolean[] conditions = {north,east,south,west};
       int count =0;
       for(boolean bool : conditions){
           if(bool){
               b++;
           }
           if(count<3){
               b = b<<1;
           }
           count++;
       }
       return (byte)b ;
    }
}
