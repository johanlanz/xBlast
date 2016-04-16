package ch.epfl.xblast.server;

import java.util.NoSuchElementException;

/**
 *
 * Enum pour les différents types de blocs du plateau de jeu
 * 
 * @author Johan Lanzrein (257221)
 *
 */
public enum Block {
    FREE, INDESTRUCTIBLE_WALL, DESTRUCTIBLE_WALL, CRUMBLING_WALL, BONUS_BOMB(
            Bonus.INC_BOMB), BONUS_RANGE(Bonus.INC_RANGE);

    private final Bonus maybeAssociatedBonus;

    /**
     * Construit un bloc. Ce constructeur est fait pour les block bonus. le
     * paramètre passé en argument devient l'attribut bonus de l'instance
     * 
     * @param maybeAssociatedBonus
     *            bonus associé au block
     */
    private Block(Bonus maybeAssociatedBonus) {
        this.maybeAssociatedBonus = maybeAssociatedBonus;
    }

    /**
     * constructeur par défaut pour les blocks non bonus
     */
    private Block() {
        this.maybeAssociatedBonus = null;
    }

    /**
     * Cette méthode return true si le block est FREE, false sinon.
     * 
     * @return lire la ligne précédente
     */
    public boolean isFree() {
        return this ==FREE;
    }

    /**
     *
     * return le résultat de isFree ou isBonus
     * 
     * @return true ->FREE ou bonus, false -> autre cas
     */
    public boolean canHostPlayer() {
        return (isFree() || isBonus());
    }

    /**
     * 
     * return l'inverse de canHostPlayer(); -> True pour FREE, -> False sinon.
     * 
     * @return cf ligne précédente
     */
    public boolean castsShadow() {
        return !this.canHostPlayer();
    }

    /**
     * retourne vraie si la case est un BONUS BOMB ou BONUS RANGE
     * 
     * @return cf ligne précédente
     */
    public boolean isBonus() {
        return this.maybeAssociatedBonus == null ? false : true;
    }

    /**
     * si le Block est un bonus retourne true, sinon false
     * 
     * @return true-> block est un bonus, false -> n'est pas un bonus
     */

    public Bonus associatedBonus() {
        if (!this.isBonus()) {
            throw new NoSuchElementException();
        }
        return this.maybeAssociatedBonus;

    }
}
