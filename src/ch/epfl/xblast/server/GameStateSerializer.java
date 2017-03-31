package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.RunLengthEncoder;
import ch.epfl.xblast.server.painting.BoardPainter;
import ch.epfl.xblast.server.painting.ExplosionPainter;
import ch.epfl.xblast.server.painting.PlayerPainter;
/**
 * Classe final public et non-intanciable qui sérialize un état de jeu en fonction d'un BoardPainter. 
 * Elle offre une méthode serialize qui remplit cette fonction. 
 * 
 * @author Johan Lanzrein (257221)
 *
 */
public final class GameStateSerializer {
    /**
     * Constructeur privé et vide car la classe doit être non-instanciable
     */
    private GameStateSerializer(){};
    
    /**
     * Cette méthode sérialise un état de jeu en une list de Byte.
     * 
     * On commence par crée 4 liste : une spiralOrderCells(qui donne les Cell dans leur ordre spiral) , une rowMajorOrder(qui donne les Cell dans leur ordre row), et deux liste vide serialized et provisoire. 
     * 
     * 1) On parcours la liste spiralOrderCell : 
     *    On ajoute les byte correspondants à provisoire avec la méthode painter.byteForCell en pasant gs.board() et la cellule c. 
     *    Ensuite on ajoute à serialized tous les byte qui arrivent de la méthode addToSerialize en passant l'argument provisoire. 
     *    
     * 2) On crée un set blastedCells qui contient les cellules touchées par les particule d'explosion. 
     *    Et une table associative qui contient les cellules ou il y a des bombes et les bombes correspondantes au GameState. 
     *    
     *    On parcours la liste rowMajorOrder : 
     *    Si la cellule est contenue dans blastedCells : 
     *      -On instaure 4 variable boolean qui sont vraie si la voisine dans la directions correspondante est aussi contenue dans blastedCells. 
     *      -On ajoute a provisoire le byte correspondant avec la méthode ExplosionPainter.byteForExplosion(N,E,S,W). 
     *    Sinon on vérifie si elle est contenue dans les clés de bombedCells : 
     *      si oui : 
     *          - on ajoute à provisoire le byte correspondant à la bombe ( Avec la méthode ExplosionPainter.byteForBomb en passant l'argument la bombe ) 
     *      Sinon : 
     *          - on ajotue à provisoire le byte correspondant à BYTE_FOR_EMPTY. 
     *          
     *    Ensuite on ajoute à serialized tous les byte qui arrivent de la méthode addToSerialize en passant l'argument provisoire. 
     *    
     * 3)On ajoute après un à un les players à serialized de la manière suivante : 
     *      -D'abord les vies, ensuite la position x, ensuite la position y, et pour finir le byteForPlayer correspondant au player à ce tick du GameState. 
     *      
     * 4) On ajoute à serialized le temps restant en divisant le temps restant par deux et en prennant l'arrondi au dessus avec la méthode Math.ceil(x). 
     *   
     * On peut ainsi sérialiser un état de jeu. On retourne une liste non modifiable de la list<Byte> ainsi obtenue. 
     *
     * @param painter le BoardPainter qui va peindre l'état de jeu
     * @param gs le GameState qui sera sérialiser
     * @return une list<Byte> qui représente l'état de jeu sérialiser. 
     */
    public static List<Byte> serialize(BoardPainter painter, GameState gs){
        List<Cell> spiralOrderCells = Cell.SPIRAL_ORDER;
        List<Cell> rowMajorOrder = Cell.ROW_MAJOR_ORDER;
        List<Byte> serialized = new ArrayList<Byte>();
        List<Byte> provisoire = new ArrayList<Byte>();
        
        for(Cell c : spiralOrderCells){
            provisoire.add(painter.byteForCell(gs.board(), c));
        }
        
        serialized.addAll(addToSerialize(provisoire));

        
        Set<Cell> blastedCells = gs.blastedCells();
        Map<Cell, Bomb> bombedCells = gs.bombedCells();
        
        for(Cell c : rowMajorOrder){
            if(blastedCells.contains(c)&&gs.board().blockAt(c).isFree()){
                boolean N = blastedCells.contains(c.neighbor(Direction.N));
                boolean E = blastedCells.contains(c.neighbor(Direction.E));
                boolean S = blastedCells.contains(c.neighbor(Direction.S));
                boolean W = blastedCells.contains(c.neighbor(Direction.W));
                provisoire.add(ExplosionPainter.byteForBlast(N, E, S, W));

            }else if(bombedCells.containsKey(c)){
                provisoire.add(ExplosionPainter.byteForBomb(bombedCells.get(c)));
            }else{
                provisoire.add(ExplosionPainter.BYTE_FOR_EMPTY);
            }
        }
        
        serialized.addAll(addToSerialize(provisoire));
        
        
        for(Player p : gs.players()){
            serialized.add((byte)p.lives());
            serialized.add((byte) p.position().x());
            serialized.add((byte) p.position().y()); 
            serialized.add(PlayerPainter.byteForPlayer(gs.ticks(), p));

        }
        
        
        serialized.add((byte)Math.ceil(gs.remainingTime()/2));
        
        return Collections.unmodifiableList(serialized);
    }
    
    private static List<Byte> addToSerialize(List<Byte> list){
        
        List<Byte> serialized = new ArrayList<Byte>();
        List<Byte>encoded = RunLengthEncoder.encode(list);
        serialized.add((byte) encoded.size());
        serialized.addAll(encoded);
        list.clear();
        return serialized;
    }
}
