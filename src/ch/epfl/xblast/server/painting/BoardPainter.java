
package ch.epfl.xblast.server.painting;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.server.*;

/**
 * Classe qui définit un peintre de tableau . Elle a deux attributs : une Table associative de block et leur image correspondantes ( palette) 
 * Et un blockImage pour le BlockImage pour les block libre qui ont une ombre
 * 
 * @author Johan Lanzrein (257221)
 *
 */
public final class BoardPainter {

    private final Map<Block, BlockImage> palette;
    private final BlockImage shadowForFree;
    /**
     * Constructeur de BoardPainter. Il vérifie que les arguments ne soit pas nul et ensuite pour les objets non-immuable en fait une copie qu'il rend non-modifiable
     * 
     * @param palette la palette du BoardPainter
     * @param shadowForFree le BlockImage pour shadowForFree du BoardPainter
     */
    public BoardPainter(Map<Block, BlockImage> palette, BlockImage shadowForFree){
        this.palette = Objects.requireNonNull(Collections.unmodifiableMap(new HashMap<Block, BlockImage>(palette)));
        this.shadowForFree = Objects.requireNonNull(shadowForFree);
    }
    /**
     * Méthode ou on cherche le byte d'image correspondant à une cell et à un board spécifique. 
     * On commence par vérifier que l'on ait bien des arguments correct(c-à-d non null) TODO vérifier si nécessaire
     * 
     * Ensuite on instaure une variable block qui sera le block ou on va chercher le byte. 
     * Si le block en question est libre on renvoie : 
     * -Si le voisin ouest ( à gauche vu du dessus ) du plateau envoie une ombre on retourne l'ordinal de shadowForFree sinon on retourne l'ordinal de la valeur correspondante à la clé 
     * 
     * Si le block n'est pas libre on retourne simplement l'ordinal de la valeur correspondante à la clé b. 
     * 
     * 
     * @param board Le board ou l'on va chercher le block et le byte d'image correspondant
     * @param cell la cellule ou l'on cherche le block. 
     * @return le byte d'image correspondant. 
     */
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
