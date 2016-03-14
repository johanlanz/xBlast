package ch.epfl.xblast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe générique ayant pour le moment une unique méthode qui renvoie la liste mirroir d'une liste passée
 * en argument. 
 * @author Johan Lanzrein (257221)
 *
 */
public final class Lists {
    private Lists(){}
    /**
     * Cette méthode va mirroiter la liste l passée en paramètre pour ce faire : 
     * - On commence par ajouter les objets de la liste ( on crée une copie de l ) 
     * - On utilise la méthode reverse de collections sur l , puis on supprime le premier élément
     * de l ( celui qui est au milieu du miroir) 
     * - On ajoute en faisant une itération for sur la liste l ( qui a été reverse ) 
     * 
     * @param l list d'objet <T> qui va être "mirrored"
     * @return la liste obtenue par le "mirroitage"
     * @throws IllegalArgumentException si la liste est vide
     */
    public static <T> List<T> mirrored(List<T> l)throws IllegalArgumentException{
        
            if(l.isEmpty()){
                throw new IllegalArgumentException();
            }
            List<T> lmirror = new ArrayList<T>(l);
            
            Collections.reverse(l);
            
            
            l.remove(0);
            
            for(T o : l){
                lmirror.add(o);
            }
            return lmirror;
        
    }
    /**
     * On commence par créer une liste vide qui va accueillir les permutations de l. 
     * Ensuite on vérifie si la taille est strictement inférieure à 2 si oui -> on ajoute simplement l à lPermutations et on return lPermutations
     * Sinon : 
     * On fait une récursion sur la subList de l en prennant tous les éléments sauf le premier. 
     * Ensuite pour chaque permutations obtenue sur la subList de l on la prend et on ajoute le premier élément que l'on a omis de la subList
     * on l'ajoute à chaque position de 0 à permutation.size et à chaque fois après l'ajout du premier élément
     * on ajoute la liste obtenue à notre liste final de permutations (lPermutations)
     * 
     * pour finir on return la liste obtenue après les récursion et boucles for. 
     * 
     * 
     * @param l liste à partir de laquel on va faire toute les permutations. 
     * @return une list qui contient des list de toute les permutations possible
     */
    public static <T> List<List<T>> permutations (List<T> l){
        
        List<List<T>> lPermutations = new ArrayList<List<T>>();
        if(l.size() < 2){
            lPermutations.add(l);
        }else{
            
            List<List<T>> permutationsSubList = permutations(l.subList(1, l.size()));
            
            for(List<T> permutation : permutationsSubList){
                for(int i = 0; i<= permutation.size();i++){
                    List<T> newPermutation = new ArrayList<T>(permutation);
                    newPermutation.add(i,l.get(0));
                    lPermutations.add(newPermutation);
                }
            }
         }
        return lPermutations;
    }
    
}
