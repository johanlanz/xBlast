package ch.epfl.xblast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
    
    public static <T> List<List<T>> permutations (List<T> l){
        
        int length = l.size();
        List<List<T>> lPermutations = new ArrayList<List<T>>();
        if(length == 0){
            return lPermutations;
        }
        
        for(T object : l){
            ArrayList<T> anArray = new ArrayList<T>();
            anArray.add(object);
            lPermutations.add(anArray);
        }
        
        ArrayList<ArrayList<T>> lPermutationsProvisoire = new ArrayList<ArrayList<T>>();
        
        while(length>1){
          for(List<T> element : lPermutations){
              for(T t : l){
                  ArrayList<T> el = new ArrayList<T>(element);
                  if(!element.contains(t)){
                      el.add(t);
                      lPermutationsProvisoire.add(el);
                  }
              }
          }
          lPermutations = new ArrayList<List<T>>(lPermutationsProvisoire);
          lPermutationsProvisoire = new ArrayList<ArrayList<T>>();
          --length;
        }
        return lPermutations;
        
        /*if(length < 2){
            lPermutations.add(l);
            return lPermutations;
        }else if(length == 2){
            lPermutations.add(l);
            List<T> lSwitched = new ArrayList<T>(l);
            Collections.reverse(lSwitched);
            lPermutations.add(lSwitched);
            return lPermutations;
        }else if (length > 2){
            
            List<T> lSubList = l.subList(1, length);
            lPermutations.addAll(Lists.permutations(lSubList));
            int size = lPermutations.size();
            for(int i = 0; i< size;i++){
                
                List<T> l2 = new ArrayList<T>(lPermutations.get(i));
                
                for(int j = 0; j<=l2.size(); j++){
                    List<T> l3 = new ArrayList<T>(l2);
                    l3.add(j,l.get(0));
                    lPermutations.add(l3);
                }
            }
        }
        List<List<T>> lPermutationFinal = new ArrayList<List<T>>();
        for(List list : lPermutations){
            if(list.size()== l.size()){
               lPermutationFinal.add(list);
            }
        }
         
        
        
        return lPermutationFinal;*/
        
        
    }
    
}
