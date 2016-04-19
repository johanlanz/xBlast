package ch.epfl.xblast;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import ch.epfl.xblast.Direction;

/**
 * Classe Cell qui définit une case
 * 
 * @author Johan Lanzrein (257221)
 *
 */
public final class Cell {
    public static final int COLUMNS = 15;
    public static final int ROWS = 13;
    public static final int COUNT = COLUMNS * ROWS;

    public static final List<Cell> ROW_MAJOR_ORDER = Collections
            .unmodifiableList(rowMajorOrder());
    public static final List<Cell> SPIRAL_ORDER = Collections
            .unmodifiableList(spiralOrder());

    private final int x;
    private final int y;

    /**
     * Construceut de Cell elle a une coordonnée x et une y
     * 
     * @param x
     *            coordonnée x modulo le nombre de colonne et on prend la valeur
     *            positive.
     * @param y
     *            coordonnée y modulo le nombre de lignes et on prend la valeur
     *            positive
     */
    public Cell(int x, int y) {
        this.x = Math.floorMod(x, COLUMNS);
        this.y = Math.floorMod(y, ROWS);
    }

    /**
     * "getter" pour la coordonnée x d'une Cell
     * 
     * @return la coordonnée x ( int )
     */
    public int x() {
        return x;
    }

    /**
     * "getter" pour la coordonnée y d'une Cell
     * 
     * @return la coordonnée y ( int )
     */
    public int y() {
        return y;
    }

    /**
     * Cette méthode retourne l'index d'une Cell (this) Pour ce faire on fait
     * simplement l'indice y * Col et on ajoute l'indice x
     * 
     * @return l'index ainsi obtenu
     */
    public int rowMajorIndex() {

        return this.y * COLUMNS + this.x;

    }

    /**
     * Cette méthode renvoie le voisin de la case en fonction de la direction
     * donnée en paramètre On fait attention néanmoins si la case est sur le
     * bord du tableau de bien renvoyer la valeur demandée ( le plateau est
     * considéré comme un tore)
     * 
     * @param dir
     *            direction dans laquelle on cherche le voisin
     * @return le voisin de la Cell(this)
     */
    public Cell neighbor(Direction dir) {
        switch(dir){
        
        case N: 
            return this.y == 0 ? new Cell(this.x, ROWS - 1) : new Cell(this.x, this.y - 1);
        case S :
            return this.y == ROWS-1 ? new Cell(this.x, 0) : new Cell(this.x, this.y + 1);
        case E : 
            return this.x == COLUMNS-1 ? new Cell(0, this.y) : new Cell(this.x + 1, this.y);
        case W : 
            return this.x == 0 ? new Cell(COLUMNS - 1, this.y) : new Cell(this.x - 1, this.y);
        default : 
        return null;
        
        }
    }

    /**
     * redéfinition de la méthode hérité de object equals on teste : - Si la
     * classe correspon - si les coordonnées sont identiques
     * 
     * @param that
     *            l'objet comparé à this
     * @return true si les deux conditions plus haut sont vrai , false sinon
     */
    @Override
    public boolean equals(Object that) {
        if (that.getClass() == Cell.class) {
            if (this.x == ((Cell) that).x && this.y == ((Cell) that).y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Redéfinition de la méthode toString
     * 
     * @return "(x,y)" ( x et y étant les coordonnées de la Cell(this)
     */
    @Override
    public String toString() {
        String message = "(" + this.x + "," + this.y + ")";
        return message;
    }

    /**
     * Méthode qui va construire un ArrayList selon le row major order C'est à
     * dire l'ordre de lecture
     * 
     * @return le ArrayList ainsi construit
     */
    private static ArrayList<Cell> rowMajorOrder() {

        ArrayList<Cell> rowMajorOrder = new ArrayList<Cell>();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                rowMajorOrder.add(new Cell(col, row));
            }
        }

        return rowMajorOrder;

    }

    /**
     * Méthode qui va construire un ArrayList de Cell selon le spiral order Pour
     * ce faire on crée deux Arraylist (ix et iy) qui contienne les coordonnées
     * possible pour x et y. On met un boolean horizontal originellement true et
     * crée une Arraylist ( vide ) qui contiendra spiralOrder tant que ni ix ou
     * iy sont vide on crée deux autres Arraylist ( i1 et 12 ) qui contiendront
     * ix et iy réciproquement si horizontal et true sinon ce sera l'inverse (
     * c-a-d i1 = iy , i2= ix). Ensuite on prend le premier élément de c2 que
     * l'on supprime ensuite de i2 ( cela le supprimera aussi de ix/iy car c'est
     * la même référence ) Par la suite, on pose c1 de 0 --> la taille de i1, et
     * pour chaque instance de c1 ( 1,2,..) on crée une Cell qui sera soit
     * (c1,c2) si horizontal est true, (c2,c1) sinon. On ajoute la cell obtenue
     * a spiralOrder.
     * 
     * 
     * @return la Arraylist spiralOrder ainsi obtenue
     */
    private static ArrayList<Cell> spiralOrder() {
        ArrayList<Integer> ix = new ArrayList<Integer>();
        ArrayList<Integer> iy = new ArrayList<Integer>();
        for (int x = 0; x < COLUMNS; x++) {
            ix.add(x);
        }
        for (int y = 0; y < ROWS; y++) {
            iy.add(y);
        }

        boolean horizontal = true;
        ArrayList<Cell> spiralOrder = new ArrayList<Cell>();

        while (!ix.isEmpty() || !iy.isEmpty()) {
            List<Integer> i1 = new ArrayList<Integer>();
            List<Integer> i2 = new ArrayList<Integer>();

            if (horizontal) {
                i1 = ix;
                i2 = iy;
            } else {
                i1 = iy;
                i2 = ix;
            }

            if (!i2.isEmpty()) {
                int c2 = i2.get(0);
                i2.remove(0);

                for (int c1 : i1) {
                    Cell c;
                    if (horizontal) {
                        c = new Cell(c1, c2);
                    } else {
                        c = new Cell(c2, c1);
                    }
                    spiralOrder.add(c);
                }
            }
            Collections.reverse(i1);

            horizontal = !horizontal;
        }
        return spiralOrder;
    }

    /**
     * Redéfinission de hashCode On utilise l'index de rowMajorOrder qui est
     * convénient
     * 
     * @return rowMajorIndex de la cell ( this )
     */
    @Override
    public int hashCode() {
        return this.rowMajorIndex();
    }

}
