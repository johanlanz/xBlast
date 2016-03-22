package ch.epfl.xblast;
/**
 * Class SubCell qui définie une sous-case. 
 * @author Johan Lanzrein (257221) 
 *
 */
public final class SubCell {
    /**
     * Paramètre de SubCell 
     * - COLUMNS : total de colonnes 240
     * - ROWS : total de lignes 208
     * 
     * coordonnées x et y 
     */
    public static final int COLUMNS = 240; 
    public static final int ROWS = 208; 
    
    private int x;
    private int y;
    /**
     * Construit une SubCell en prennant les x et y passés en paramètres 
     * on fait x modulo COLUMNS et la valeur positive. idem avec y modulo ROWS
     * 
     * @param x coordonnée x 
     * @param y coordonnée y 
     */
    public SubCell(int x, int y){
        this.x = Math.floorMod(x, COLUMNS);
        this.y = Math.floorMod(y, ROWS);
    }
    /**
     * Retourne la sous-case d'une cell passée en paramètre. 
     * pour ce faire on prend les coordonnées x et y de la cell multiplié par 16 et ajoute 8. 
     * C'est car la première sous case central est 8,8. ensuite elles sont séparée linéairement par un facteur 16 pour chaque case
     * @param cell
     * @return
     */
    public static SubCell centralSubCellOf(Cell cell){
        int x = 8 + cell.x()*16;
        int y = 8 + cell.y()*16;
        return new SubCell(x,y);
    }
    /**
     * Calcule la "distance de Manhatan" d'une SubCell à la SubCell centrale la plus proche
     * pour ce faire on trouve la SubCell centrale la plus proche à l'aide des méthode Cell c = containingCell() et centralSubCellOf(c) 
     * on prend la différence entre les coordonnées x (respectivement y ) de la middle SubCell et this. On utilise la valeur absolue 
     * 
     * @return la somme de horizontal distance et vertical distance
     */
    public int distanceToCentral(){
        Cell cell = this.containingCell();
        SubCell middle= centralSubCellOf(cell);
        int horizontalDist = Math.abs(middle.x - this.x);
        int verticalDist = Math.abs(middle.y - this.y);
        return horizontalDist+ verticalDist;
    }
    /**
     * Détermine si une SubCell est centrale ou non. 
     * On utilise les méthode containingCell pour trouvé dans quelle Cell la SubCell est, ensuite la méthode centralSubCellOf(c) 
     * pour trouver la SubCell effective. On compare les deux pour voir si elles sont identiques. 
     * @return true si la SubCell effective et this sont equal, false sinon
     */
    public boolean isCentral(){
        
        Cell cell = this.containingCell();
        SubCell middle = centralSubCellOf(cell);
        if(middle.equals(this)){
            return true;
        }else{
            return false;
        }
    }
    /**
     * Retrouve la Cell ou est la SubCell
     * on prend les coordonnées de la SubCell divisé 16, car chaque cell contient 16*16 SubCell
     * @return la Cell ainsi trouvée
     */
    public Cell containingCell(){
        int x = (this.x) / 16;
        int y = (this.y) / 16;
        return new Cell(x,y);
    }
    
    public SubCell neighbor(Direction dir){
        switch(dir){
        case N : 
            if(this.y == 0){
                return new SubCell(this.x, 207);
            }else{
                return new SubCell(this.x, this.y-1);
            }
        case S : 
            if(this.y == 207){
                return new SubCell(this.x, 0);
            }else{
                return new SubCell(this.x, this.y+1);
            }
        case E : 
            if(this.x == 239){
                return new SubCell(0, this.y);
            }else{
                return new SubCell(this.x+1, this.y);
            }
        case W : 
            if (this.x == 0){
                return new SubCell(239, this.y);
            }else{
                return new SubCell(this.x-1, this.y);
            }
        default : 
            return null;
        }
    }
    /**
     * Redéfinition de la méthode equals TODO : précier que je lai redefinis
     * On teste si : 
     * - Classe est SubCell, 
     * - les coordonnées sont identiques
     * @param that objet a comparé avec this
     * @return true si les deux conditions précedentes sont vrais, false sinon
     */
    @Override
    public boolean equals(Object that){
        if(that.getClass() == SubCell.class){
            SubCell expected = (SubCell) that; 
            if( this.x == expected.x && this.y == expected.y){
                return true;
            }
        }
        return false;
    }
    /**
     * Redéfinition de la méthode toString
     * renvoie un message de la forme : "(x,y)" x et y étant les coordonnées de la SubCell
     * @return message expliqué plus haut
     */
    @Override
    public String toString(){
        String message = "("+x+","+y+")";
        return message;
    }

    public int x() {
        
        return this.x;
    }
    public int y(){
        return this.y;
    }
    
}
