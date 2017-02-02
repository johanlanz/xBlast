package ch.epfl.xblast;

/**
 * Class SubCell qui définie une sous-case.
 * 
 * @author Johan Lanzrein (257221)
 *
 */
public final class SubCell {
    /**
     * Paramètre de SubCell - COLUMNS : total de colonnes 240 - ROWS : total de
     * lignes 208
     * 
     * coordonnées x et y
     */
    public static final int COLUMNS = 240;
    public static final int ROWS = 208;

    private final int x;
    private final int y;

    /**
     * Construit une SubCell en prennant les x et y passés en paramètres on fait
     * x modulo COLUMNS et la valeur positive. idem avec y modulo ROWS
     * 
     * @param x
     *            coordonnée x
     * @param y
     *            coordonnée y
     */
    public SubCell(int x, int y) {
        this.x = Math.floorMod(x, COLUMNS);
        this.y = Math.floorMod(y, ROWS);
    }

    /**
     * Retourne la sous-case d'une cell passée en paramètre. pour ce faire on
     * prend les coordonnées x et y de la cell multiplié par 16 et ajoute 8.
     * C'est car la première sous case central est 8,8. ensuite elles sont
     * séparée linéairement par un facteur 16 pour chaque case
     * 
     * @param cell
     * @return la SubCell central
     */
    public static SubCell centralSubCellOf(Cell cell) {
        int x = 8 + cell.x() * 16;
        int y = 8 + cell.y() * 16;
        return new SubCell(x, y);
    }

    /**
     * Calcule la "distance de Manhatan" d'une SubCell à la SubCell centrale la
     * plus proche pour ce faire on trouve la SubCell centrale la plus proche à
     * l'aide des méthode Cell c = containingCell() et centralSubCellOf(c) on
     * prend la différence entre les coordonnées x (respectivement y ) de la
     * middle SubCell et this. On utilise la valeur absolue
     * 
     * @return la somme de horizontal distance et vertical distance
     */
    public int distanceToCentral() {
        Cell cell = containingCell();
        SubCell middle = centralSubCellOf(cell);
        int horizontalDist = Math.abs(middle.x - x);
        int verticalDist = Math.abs(middle.y - y);
        return horizontalDist + verticalDist;
    }

    /**
     * Détermine si une SubCell est centrale ou non. On utilise les méthode
     * containingCell pour trouvé dans quelle Cell la SubCell est, ensuite la
     * méthode centralSubCellOf(c) pour trouver la SubCell effective. On compare
     * les deux pour voir si elles sont identiques.
     * 
     * @return true si la SubCell effective et this sont equal, false sinon
     */
    public boolean isCentral() {

        Cell cell = containingCell();
        SubCell middle = centralSubCellOf(cell);
        
        return middle.equals(this) ? true : false;
    }

    /**
     * Retrouve la Cell ou est la SubCell on prend les coordonnées de la SubCell
     * divisé 16, car chaque cell contient 16*16 SubCell
     * 
     * @return la Cell ainsi trouvée
     */
    public Cell containingCell() {
        int xCell = (x) / 16;
        int yCell = (y) / 16;
        return new Cell(xCell, yCell);
    }

    public SubCell neighbor(Direction dir) {
        switch (dir) {
        case N:
            return y == 0 ? new SubCell( x, ROWS-1) : new SubCell( x,  y - 1);
        case S:
            return  y == ROWS-1 ? new SubCell( x, 0) : new SubCell( x,  y + 1);
        case E:
            return  x == COLUMNS-1 ? new SubCell(0,  y) : new SubCell( x + 1,  y);
        case W:
           return  x == 0 ? new SubCell(COLUMNS-1,  y) : new SubCell( x - 1,  y);
        default:
            return null;
        }
    }

    /**
     * Redéfinition de la méthode equals On teste si : - Classe est SubCell, -
     * les coordonnées sont identiques
     * 
     * @param that
     *            objet a comparé avec this
     * @return true si les deux conditions précedentes sont vrais, false sinon
     */
    @Override
    public boolean equals(Object that) {
        if (that instanceof SubCell && !that.equals(null)) {
            SubCell expected = (SubCell) that;
            
                return (this.x == expected.x && this.y == expected.y);
            
        }
        
        return false;
    }

    /**
     * Redéfinition de la méthode toString renvoie un message de la forme :
     * "(x,y)" x et y étant les coordonnées de la SubCell
     * 
     * @return message expliqué plus haut
     */
    @Override
    public String toString() {
        String message = "(" + x + "," + y + ")";
        return message;
    }

    public int x() {
        return  x;
    }

    public int y() {
        return  y;
    }

    /**
     * Redéfinition de la méthode hashCode On prend l'index de rowMajorOrder ->
     * Il est différent pour subCell.
     * 
     * Il s'agit s'implement du nombre de lignes multiplié par la cte COLUMNS +
     * l'indide de colonne
     * 
     * @return l'indice obtenu
     */
    @Override
    public int hashCode() {
        return  y * COLUMNS +  x;
    }

}
