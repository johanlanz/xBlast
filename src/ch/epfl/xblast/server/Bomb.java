package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.ArgumentChecker;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerID;

/**
 * Classe qui décrit une bombe
 * 
 * @author Johan Lanzrein (257221) 
 *
 */
public final class Bomb {
    private PlayerID ownerID; 
    private Cell position;
    private Sq<Integer> fuseLengths;
    private int range;
    /**
     * Construit une bombe avec les paramètre données
     * Ils sont vérifier pour ne pas être nul, et pour les chiffres pas négatifs
     * les paramètre sont ceux qui vont être les caractéristiques de la bombe
     * @param ownerId
     * @param position
     * @param fuseLengths
     * @param range
     */
    public Bomb(PlayerID ownerId, Cell position, Sq<Integer> fuseLengths, int range){
        
        this.ownerID = Objects.requireNonNull(ownerId);
        this.position = Objects.requireNonNull(new Cell(position.x(), position.y()));
        
        this.fuseLengths = Objects.requireNonNull(fuseLengths);
        
        if(fuseLengths.isEmpty()){
            throw new IllegalArgumentException();
        }
        this.range = ArgumentChecker.requireNonNegative(range);
        
    }
    /**
     * Constructeur secondaire qui fait appel au constructeur principal
     * La principale différence est dans l'argument fuseLength, 
     * On fait une Sq<Integer> qui commence avec la taille fuseLength. et avec une fonction lambda
     * On fait une itération décroissante de 1 à chaque itération. 
     * 
     * @param ownerId
     * @param position
     * @param fuseLength
     * @param range
     */
    public Bomb(PlayerID ownerId, Cell position, int fuseLength, int range){
        this(ownerId, position, Sq.iterate(fuseLength, u -> u-1).limit(fuseLength), range);
        ArgumentChecker.requireNonNegative(fuseLength);
        
    }
    /**
     * retourne l'ID du propriétaire de la bomb
     * @return ID du owner
     */
    public PlayerID ownerId(){
        return this.ownerID;
    }
    /**
     * Retourne une copie de la position de la bombe. C'est une Cellule. 
     * @return Cell ou la bombe est
     */
    public Cell position(){
        return new Cell(position.x(), position.y());
    }
    /**
     * retourne la séquence fuseLengths de la bombe
     * @return fuseLentgth
     */
    public Sq<Integer> fuseLengths(){
        return this.fuseLengths;
    }
    /**
     * retourne la téte de la fuse Length c-à-d le 1er élément
     * @return tête de fuseLength
     */
    public int fuseLength(){
        return fuseLengths.head();
    }
    /**
     * 
     * @return range de la bombe
     */
    public int range(){
        return range;
    }
    /**
     * Simule une explosion. 
     * Pour cela on fait une liste de taille 4 
     * On fait pour chaque Direction (N,S,W,E) on fait un "Bras" d'explosion à l'aide de la méthode explosionArmTowards(Direction d).
     * Ensuite on retourne la liste qui contient l'explosion
     * @return ArrayList ainsi obtenue 
     * 
     * TODO : on obtient 4 particules a la fois pour le cnetre de l'explosion -> meilleure manière de codé? 
     */
    public List<Sq<Sq<Cell>>> explosion(){
        List<Sq<Sq<Cell>>> explosion = new ArrayList<Sq<Sq<Cell>>>();
        explosion.add(explosionArmTowards(Direction.N));
        explosion.add(explosionArmTowards(Direction.S));
        explosion.add(explosionArmTowards(Direction.W));
        explosion.add(explosionArmTowards(Direction.E));
        return explosion;
        
    }
    /**
     * Cette méthode simule une explosion depuis la position de la Bomb, en direction d'une Direction passée en paramètre. 
     * La longueur du bras est décidé par la range de la bombe. 
     * On utilise la fonction lambda pour itérer sur une séquence de Cellule et on prend chaque fois le voisin 
     * de la Cell précédente. 
     * 
     * Il faut considérer chaque sous-séquence comme une particule d'explosion et sa trajectoire. Il y a des particules émise
     * durant tout le temps d'explosion.
     * 
     * @param dir Direction ou l'explosion se propage. 
     * @return Séquence de séquence de Cell ainsi obtenue 
     */
    
    private Sq<Sq<Cell>> explosionArmTowards(Direction dir){
        
        Sq<Cell> particule = Sq.iterate(position, p -> p.neighbor(dir)).limit(range);
        
        Sq<Sq<Cell>> arm = Sq.repeat(Ticks.EXPLOSION_TICKS, particule );
        
        return arm;
        
    }
}
