package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Lists;
/**
 * Classe Board qui définit le plateau de jeu sur lequel le jeu se déroulera. 
 * A des méthode pour construire un Board à partir d'une liste donnée 
 * @author Johan Lanzrein (257221) 
 *
 */
public final class Board {
    
    /**
     * On note que l'index de chaque Sq<Block> renvoie a l'index en row major order de 
     * chaque Cell 
     */
    
    private List<Sq<Block>> blockList = new ArrayList<Sq<Block>>(); 
    
    /**
     * Le constructeur construits un board. 
     * 
     * @param blocks List d'une séquence de Block. On va l'utiliser comme attributs pour le Board. 
     * @throws IllegalArgumentException si la taille de la List blocks est différente de 195
     */
    public Board(List<Sq<Block>> blocks)throws IllegalArgumentException{
        
            if(blocks.size()!=Cell.COLUMNS*Cell.ROWS){
                throw new IllegalArgumentException();
            }
            
            blockList = new ArrayList<Sq<Block>>(blocks); 
        
    }
    /**
     * A partir d'une liste de liste, on construit un tableau. Au début on vérifie
     * que la matrice paramètre ai des valeurs acceptables ( ici 13x15). Ensuite on 
     * construits le plateau : Pour cela on transforme chaque block en une
     * Sq constante de block. qu'on ajoute a une liste paramètre qui sera le paramètre de notre
     * constructeur utilisé plus bas. 
     * 
     * @param rows List de list de block. 
     * @return le board ainsi crée
     * @throws IllegalArgumentException si la taille n'est pas 13x15 ( col x rows ) 
     */
    public static Board ofRows(List<List<Block>> rows)throws IllegalArgumentException{
            checkBlockMatrix(rows, 13, 15);
            
            List<Sq<Block>> blockList = new ArrayList<Sq<Block>>();

            for(int i = 0; i<rows.size();i++){
                for(int j = 0; j<rows.get(i).size();j++){
                    Sq<Block>bSq = Sq.constant(rows.get(i).get(j));
                    blockList.add(bSq);
                }
            }
            
            return new Board(blockList);
            
        
    }
    
    /**
     * Crée un board 13x15 complétement emmuré et l'intérieur du board est crée en fonction du paramètre
     * innerBlocks. Au début on vérifie que la liste paramètre ait des valeurs
     * acceptable avec checkBlockMatrix. ( ici 11,13) 
     * Pour ce faire, on crée une liste de Sq<Block> qui contiendra les blocks. 
     * 
     * on s'aide de la méthode fillRowWithIndestructibleWall ( expliquée plus bas ) 
     * ensuite on complète la liste avec Sq.constant. 
     * sans oublier de placer un mur avant et après chaque ligne ( pour bien emmurer le board ) 
     * 
     * ensuite on crée notre board avec la liste ainsi obtenue
     * @param innerBlocks list de list de block ( 11x13 ) qui servira a construire
     * l'intérieur du tableau
     * 
     * @return board ainsi créer
     * @throws IllegalArgumentException si la taille de innerBlocks n'est pas 11x13
     */
    public static Board ofInnerBlocksWalled(List<List<Block>> innerBlocks)throws IllegalArgumentException{
        checkBlockMatrix(innerBlocks, 11, 13);
        List<Sq<Block>> blockList = new ArrayList<Sq<Block>>();
        
        fillRowWithIndestructibleWall(blockList);
        
        for(int i = 0; i < innerBlocks.size(); i++){
            blockList.add(Sq.constant(Block.INDESTRUCTIBLE_WALL));
            for(int j = 0; j<innerBlocks.get(i).size(); j++){
                blockList.add(Sq.constant(innerBlocks.get(i).get(j)));
            }
            blockList.add(Sq.constant(Block.INDESTRUCTIBLE_WALL));
        }
        fillRowWithIndestructibleWall(blockList);
        
        return new Board(blockList);
    }
    /**
     * Crée un board qui sera la copie/mirroir de la liste passée en paramètre. 
     * Ainsi la liste passée en paramètre sera le NW(nord-ouest) du plateau. Son mirroir par l'axe y
     * sera le NE, le miroir par l'axe x du coin NW sera le coin SW, et le mirroir par l'axe x de SW
     * sera le coin SE. 
     * 
     * On s'aide de la méthode mirrored définie dans la classe Lists. 
     * Au début on vérifie que la liste paramètre ait des valeurs
     * acceptable avec checkBlockMatrix. ( ici 6,7) 
     * On utilise une liste adjuvante blockList qui contiendra les valeurs que l'on trouvera. 
     * On commence par faire une ligne de murs indestructibles. 
     * Ensuite on itére sur chaque ligne de quadrantNWBlocks pour construire la 1ère moitié :
     * -> on crée une liste mirrored qui sera le miroir de QuadrantNWBlocks, 
     * -> on ajoute les instance du mirroir à notre block liste. 
     * 
     * Une fois arrivée au bout de la liste ( 6 itérations ). on fait la même chose hormis qu'on commence à l'avant
     * dernière ligne ( 4) jusqua 0. 
     * Sans oublier de mettre un mur indestructible avant et a la fin de chaque ligne
     * 
     * On conclut en faisant une ligne de mur indestructible
     * 
     * 
     * 
     * @param quadrantNWBlocks liste paramètre. représente le coin Nord-ouest d'un hypothétique plateau de jeu
     * @return Board ainsi crée 
     * @throws IllegalArgumentException si l'argument quadrantNWBlocks ne correspond pas a 6x7 
     */
    public static Board ofQuadrantNWBlocksWalled(List<List<Block>> quadrantNWBlocks)throws IllegalArgumentException{
        checkBlockMatrix(quadrantNWBlocks, 6, 7);
        List<Sq<Block>> blockList = new ArrayList<Sq<Block>>();
        
        fillRowWithIndestructibleWall(blockList);
        
        for(int i = 0; i< 6; i++){
            blockList.add(Sq.constant(Block.INDESTRUCTIBLE_WALL));
            List<Block> check = new ArrayList<Block>(quadrantNWBlocks.get(i));
            List<Block> mirroredRows = Lists.mirrored(check);
            for(int j = 0 ; j<Cell.COLUMNS-2; j++){
                blockList.add(Sq.constant(mirroredRows.get(j)));
            }
            blockList.add(Sq.constant(Block.INDESTRUCTIBLE_WALL));
        }
        
        for(int i = 4; i>=0; i--){
            blockList.add(Sq.constant(Block.INDESTRUCTIBLE_WALL));
            List<Block> check = new ArrayList<Block>(quadrantNWBlocks.get(i));
            List<Block> mirroredRows = Lists.mirrored(check);
            for(int j = 0 ; j<Cell.COLUMNS-2; j++){
                blockList.add(Sq.constant(mirroredRows.get(j)));
            }
            blockList.add(Sq.constant(Block.INDESTRUCTIBLE_WALL));
        }
            
        fillRowWithIndestructibleWall(blockList);
        
        
        return new Board(blockList);
        
        
    }
    /**
     * Retourne une Sq<Block> correspond à la Cell donnée. 
     * On utilise la méthode rowMajorIndex. et la propriété que la liste stoque les Block en ordre row major. 
     * @param c Cell d'ou on veut connaitre la Séquence
     * @return une Sq<Block> de la Cell donnée
     */
    public Sq<Block> blocksAt(Cell c){
        return blockList.get(c.rowMajorIndex());
    }
    /**
     * Comme la méthode précédente, hormis que l'on prend un block. C'est à dire la head. 
     * (avec la méthode de Sq. ) 
     * @param c Cell d'ou on souhaite savoir le block. 
     * @return block trouvé
     */
    public Block blockAt(Cell c){
        
        return blocksAt(c).head();
    }
    
    /**
     * méthode qui va throw des exceptions si une matrice donnée ne correspond pas à nos critère de taille 
     * sur les colonnes et lignes 
     * @param matrix quelquonc. ( ici une list<list<Block>> ) 
     * @param rows le nombre souhaité 
     * @param columns le nombre souhaité
     * @throws IllegalArgumentException si les rows ou colonne du paramètres matrix ne correspondent pas au rows et columns des paramètres
     */
    private static void checkBlockMatrix(List<List<Block>> matrix, int rows, int columns)throws IllegalArgumentException{

            if(matrix.size()!=rows){
                throw new IllegalArgumentException();
            }else{
                for(int i = 0; i< matrix.size(); i++){
                    if(matrix.get(i).size()!= columns){
                        throw new IllegalArgumentException();
                    }
                }
            }
        
    }
    
    /**
     * Cette méthode remplie la liste passée en paramètre d'une ligne ( 15 Sq<Block> ) 
     * de murs indestructibles. 
     *  
     * @param blockList la liste ou l'on veut ajouté nos INDESTRUCTIBLE_WALL
     */
    private static void fillRowWithIndestructibleWall(List<Sq<Block>> blockList){
        
        for(int i = 0; i<Cell.COLUMNS;i++){
            blockList.add(Sq.constant(Block.INDESTRUCTIBLE_WALL));
        }
    }
    /**
     * TODO : spéficier quon a redéfini
     * redéfinition dans le readme
     * Redéfinission de la méthode equals. 
     * Utilisé principalement ( pour le moment uniquement ) pour les tests unitaires. 
     * 
     * On compare un objet o avec this. 
     * @param o objet à comparé
     * @return true si c'est les même, false sinon. 
     */
    @Override
    public boolean equals(Object o){
        
        if(o.getClass() == Board.class){
            for(int i = 0 ; i<Cell.COLUMNS; i++){
                for(int j = 0 ; j<Cell.ROWS; j++){
                    Cell c = new Cell(i, j);
                    if(this.blockAt(c)!=((Board) o).blockAt(c)){
                        return false;
                    }
                }
            }
            return true;
        }
        return false;   
    }
    
    


}
