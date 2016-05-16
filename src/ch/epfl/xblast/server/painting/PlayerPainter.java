package ch.epfl.xblast.server.painting;

import ch.epfl.xblast.Direction;
import ch.epfl.xblast.server.Player;
import ch.epfl.xblast.server.Player.LifeState.State;

/**
 * Classe qui définit un peintre de joueur. 
 * Elle est public final et non instanciable ( c-à-d son constructeur est privé et vide ) 
 * Elle offre une méthode pour peindre un joueur
 * @author Johan Lanzrein (257221)
 *
 */
public final class PlayerPainter {
    /**
     * Byte correspondant à un joueur mort 
     */
    
    private static final byte BYTE_FOR_DEAD = 15;
    /**
     * Constructeur de PlayerPainter il est privé et vide car c'est une classe non-instanciable
     */
    private PlayerPainter(){};
    
    /**
     * On commence par isoler la direction du joueur car elle sera importante pour le calcul. 
     * Pour la suite on vérifie si le joueur est bien en vie -> sinon on retourne BYTE_FOR_DEAD si oui -> on continue 
     * 
     * Ensuite comme les byte d'image sont triées de manière pratique on instaure une variable byteImage qui sera égal à 3*direction du joueur en ordinal + 20 * l'id du joueur en ordinal
     * Ce calcul se base sur les observations qu'on fait en regardant le dossier images\player ( les joueurs sont par tranches de 20 et chaque direction est répartie sur un multiple de 3)
     * 
     * On cherche le State actuel du joueur si il n'est pas en train de mourir : 
     *      On regarde si le state actuel est invulnérable et si le tick est impair -> On modifie de groupe (c-à-d on passe au groupe invulnérable) en changeant la variable byteImage en 80+3*direction du regard)
     *      On déclare une variable remainder qui est X % 4. X est définit en fonction de si la direction est horizontale ou non ( si oui on prend la coordonnées x de la subcell du joueur, si non on prend la coordonnée y de la subCell du joueur      
     *      On regarde ensuite le résultat remainder % 2 : si il vaut 0 on retourne la variable byteImage inchangée ( car on prend la 1ère image) 
     *      Sinon si remainder == 1 on retourne la 2ème image (c-à-d byteImage+1) sinon on retourne la 3ème image ( c-à-d byteImage+2)
     *      
     *      
     * Si il est en train de mourir : 
     * Si ses vies sont strictement supérieur à un on retourne l'image qui représente un joueur qui perd une vie(byteImage+12) sinon on retourne l'image d'un joueur qui perd sa dernière vie ( byteImage+13)
     * 
     *
     * @param tick le tick actuel du GameState
     * @param player joueur dont on cherche le byte d'image
     * @return le byte d'image correspondant au joueur à ce tick précis
     */
    public static byte byteForPlayer(int tick, Player player){
        Direction dir = player.direction();
        
        if (player.isAlive()) {
            int byteImage = 20 * player.id().ordinal();

            State lifeState = player.lifeState().state();

            if (!lifeState.equals(State.DYING)) {
                
                byteImage += 3 * dir.ordinal();
                if (lifeState.equals(State.INVULNERABLE) && tick % 2 == 1) {
                    byteImage = 80 + 3 * dir.ordinal();
                }

                int remainder = (dir.isHorizontal() ? player.position().x() : player.position().y()) % 4;

                if (remainder % 2 == 0) {
                    return (byte) byteImage;
                }
                return (byte) (remainder == 1 ? byteImage + 1 : byteImage + 2);
            }

            return (byte) (player.lives() > 1 ? byteImage + 12 : byteImage + 13);

        }

        return BYTE_FOR_DEAD;
    }
    
   
}
