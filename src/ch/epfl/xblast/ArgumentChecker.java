package ch.epfl.xblast;

/**
 * Classe ArgumentChecker qui contient une méthode qui 
 * vérifie les arguments sont corrects. 
 * 
 * 
 * @author Johan Lanzrein (257221)
 *
 */
public final class ArgumentChecker {
    private ArgumentChecker() {
    };

    /**
     * Cette méthode vérifie si l'argument est bien un nombre >= 0 Si ce nest
     * pas le cas elle lève l'exception IllegalArgumentException Sinon elle
     * renvoie la value passée en argument
     * 
     * @param value
     *            valeur a testé
     * @return value si value >= 0
     * @throws IllegalArgumentException
     *             si value < 0
     */
    public static int requireNonNegative(int value) throws IllegalArgumentException {

        if (value < 0) {
            throw new IllegalArgumentException();
        }

        return value;

    }
}
