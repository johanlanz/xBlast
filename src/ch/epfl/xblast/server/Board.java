package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Lists;

/**
 * Classe Board qui définit le plateau de jeu sur lequel le jeu se déroulera. A
 * des méthode pour construire un Board à partir d'une liste donnée
 * 
 * @author Johan Lanzrein (257221)
 *
 */
public final class Board {
    /*
     * Constantes utiles pour la constructions des différents board 
     */
    private final static int COLUMNS = Cell.COLUMNS;
    private final static int ROWS = Cell.ROWS;
    
    private final static int WALLED_COLUMNS = COLUMNS-2;
    private final static int WALLED_ROWS = ROWS-2;
    
    private final static int QUADRANT_COLUMNS = 7;
    private final static int QUADRANT_ROWS = 6;
    /**
     * On note que l'index de chaque Sq<Block> renvoie a l'index en row major
     * order de chaque Cell
     */

    private final List<Sq<Block>> blockList;

    /**
     * Le constructeur construits un board.
     * 
     * @param blocks
     *            List d'une séquence de Block. On va l'utiliser comme attributs
     *            pour le Board.
     * @throws IllegalArgumentException
     *             si la taille de la List blocks est différente de 195
     */
    public Board(List<Sq<Block>> blocks) throws IllegalArgumentException {

        if (blocks.size() != Cell.COUNT) {
            throw new IllegalArgumentException();
        }

        blockList = Collections.unmodifiableList(new ArrayList<Sq<Block>>(blocks));

    }

    /**
     * A partir d'une liste de liste, on construit un tableau. Au début on
     * vérifie que la matrice paramètre ai des valeurs acceptables ( ici 13x15).
     * Ensuite on construits le plateau : Pour cela on transforme chaque block
     * en une Sq constante de block. qu'on ajoute a une liste paramètre qui sera
     * le paramètre de notre constructeur utilisé plus bas.
     * 
     * @param rows
     *            List de list de block.
     * @return le board ainsi crée
     * @throws IllegalArgumentException
     *             si la taille n'est pas 13x15 ( col x rows )
     */
    public static Board ofRows(List<List<Block>> rows)throws IllegalArgumentException {
        checkBlockMatrix(rows, ROWS, COLUMNS);

        List<Sq<Block>> blockList = new ArrayList<Sq<Block>>();

        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < rows.get(i).size(); j++) {
                Sq<Block> bSq = Sq.constant(rows.get(i).get(j));
                blockList.add(bSq);
            }
        }

        return new Board(blockList);

    }

    /**
     * Crée un board 13x15 complétement emmuré et l'intérieur du board est crée
     * en fonction du paramètre innerBlocks. Au début on vérifie que la liste
     * paramètre ait des valeurs acceptable avec checkBlockMatrix. ( ici WALLED_COLUMNS,13)
     * Pour ce faire, on crée une liste de Sq<Block> qui contiendra les blocks.
     * 
     * on s'aide de la méthode nCopies de Collection pour créer une ligne complétement emmurée
     *  ensuite on complète la liste avec Sq.constant. sans oublier de
     * placer un mur avant et après chaque ligne ( pour bien emmurer le board )
     * et on finit par réutiliser la méthode nCopies de Collection pour la dernière ligne
     * ensuite on crée notre board avec la liste ainsi obtenue
     * 
     * @param innerBlocks
     *            list de list de block ( WALLED_COLUMNSx13 ) qui servira a construire
     *            l'intérieur du tableau
     * 
     * @return board ainsi créer
     * @throws IllegalArgumentException
     *             si la taille de innerBlocks n'est pas 11x13
     */
    public static Board ofInnerBlocksWalled(List<List<Block>> innerBlocks)
            throws IllegalArgumentException {
        checkBlockMatrix(innerBlocks, WALLED_ROWS, WALLED_COLUMNS);
        List<Sq<Block>> blockList = new ArrayList<Sq<Block>>();

        blockList.addAll(Collections.nCopies(COLUMNS, Sq.constant(Block.INDESTRUCTIBLE_WALL)));

        for (int i = 0; i < innerBlocks.size(); i++) {
            blockList.add(Sq.constant(Block.INDESTRUCTIBLE_WALL));
            for (int j = 0; j < innerBlocks.get(i).size(); j++) {
                blockList.add(Sq.constant(innerBlocks.get(i).get(j)));
            }
            blockList.add(Sq.constant(Block.INDESTRUCTIBLE_WALL));
        }
        blockList.addAll(Collections.nCopies(COLUMNS, Sq.constant(Block.INDESTRUCTIBLE_WALL)));

        return new Board(blockList);
    }

    /**
     * Crée un board qui sera la copie/mirroir de la liste passée en paramètre.
     * Ainsi la liste passée en paramètre sera le NW(nord-ouest) du plateau. Son
     * mirroir par l'axe y sera le NE, le miroir par l'axe x du coin NW sera le
     * coin SW, et le mirroir par l'axe x de SW sera le coin SE.
     * 
     * On s'aide de la méthode mirrored définie dans la classe Lists. Au début
     * on vérifie que la liste paramètre ait des valeurs acceptable avec
     * checkBlockMatrix. ( ici QUADRANT_ROWS,QUADRANT_COLUMNS) On utilise une liste adjuvante blockList qui
     * contiendra les valeurs que l'on trouvera. On commence par faire une ligne
     * de murs indestructibles.(A l'aide la méthode nCopies de Collections) Ensuite on itére sur chaque ligne de
     * quadrantNWBlocks pour construire la 1ère moitié : -> on crée une liste
     * mirrored qui sera le miroir de QuadrantNWBlocks, -> on ajoute les
     * instance du mirroir à notre block liste.
     * 
     * Une fois arrivée au bout de la liste ( 6 itérations ). on fait la même
     * chose hormis qu'on commence à l'avant dernière ligne ( 4) jusqua 0. Sans
     * oublier de mettre un mur indestructible avant et a la fin de chaque ligne
     * 
     * On conclut en faisant une ligne de mur indestructible
     * 
     * TODO : recommenter 
     * 
     * 
     * @param quadrantNWBlocks
     *            liste paramètre. représente le coin Nord-ouest d'un
     *            hypothétique plateau de jeu
     * @return Board ainsi crée
     * @throws IllegalArgumentException
     *             si l'argument quadrantNWBlocks ne correspond pas a 6x7
     */
    public static Board ofQuadrantNWBlocksWalled(List<List<Block>> quadrantNWBlocks)throws IllegalArgumentException {
        checkBlockMatrix(quadrantNWBlocks, QUADRANT_ROWS, QUADRANT_COLUMNS);
        List<List<Block>> blockList = new ArrayList<List<Block>>();

        for (int i = 0; i < quadrantNWBlocks.size(); i++) {
            blockList.add(Lists.mirrored(quadrantNWBlocks.get(i)));
        }

        blockList = Lists.mirrored(blockList);
        

        return ofInnerBlocksWalled(blockList);

    }

    /**
     * Retourne une Sq<Block> correspond à la Cell donnée. On utilise la méthode
     * rowMajorIndex. et la propriété que la liste stoque les Block en ordre row
     * major.
     * 
     * @param c
     *            Cell d'ou on veut connaitre la Séquence
     * @return une Sq<Block> de la Cell donnée
     */
    public Sq<Block> blocksAt(Cell c) {
        return blockList.get(c.rowMajorIndex());
    }

    /**
     * On dit le block actuel du board. C'est a dire la head() de la Sq<Block> a cet endroit la. On s'aide de la méthode blocksAt(c).
     * 
     * @param c
     *            Cell d'ou on souhaite savoir le block.
     * @return block trouvé
     */
    public Block blockAt(Cell c) {

        return blocksAt(c).head();
    }

    /**
     * méthode qui va throw des exceptions si une matrice donnée ne correspond
     * pas à nos critère de taille sur les colonnes et lignes
     * 
     * @param matrix
     *            quelquonc. ( ici une list<list<Block>> )
     * @param rows
     *            le nombre souhaité
     * @param columns
     *            le nombre souhaité
     * @throws IllegalArgumentException
     *             si les rows ou colonne du paramètres matrix ne correspondent
     *             pas au rows et columns des paramètres
     */
    private static void checkBlockMatrix(List<List<Block>> matrix, int rows,
            int columns) throws IllegalArgumentException {

        if (matrix.size() != rows) {
            throw new IllegalArgumentException();
        } else {
            for (int i = 0; i < matrix.size(); i++) {
                if (matrix.get(i).size() != columns) {
                    throw new IllegalArgumentException();
                }
            }
        }

    }

    

}
