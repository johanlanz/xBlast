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
    
}
