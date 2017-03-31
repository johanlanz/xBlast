package ch.epfl.xblast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Classe qui définit un algorithme de compression relativement similaire au codage par plage. 
 * Elle est public, final et non-instantiable. 
 * Elle offre deux méthodes static et public qui permet d'encoder ou décoder une liste de Byte. 
 * 
 * @author Johan Lanzrein (257221)
 *
 */
public final class RunLengthEncoder {
    /**
     * Constructeur qui est privé et vide car la classe est non-instanciable. 
     */
    private RunLengthEncoder(){};
    /**
     * Cette méthode encode une liste de Byte. Pour commencer on initialise une list vide de Byte et un int count qui est égal à 1. 
     * 
     * On itère ensuite sur la liste donnée en paramètres pour chaque byte.(jusqu'à l'avant dernier )
     *      -Pour chaque byte si il est négatif on throw une IllegalArgumentException. 
     *      - ensuite on l'instance i à l'instance i+1 si elle sont égale : 
     *      Si oui : 
     *          -on incrémente count de 1. 
     *      Si non on vérifie si count est strictement supérieur à 2 : 
     *      Si oui : 
     *          - on crée un byte index qui est égale à 2-count. 
     *          - on ajoute à encoded l'index et l'élément i. 
     *          - on initialise count = 1 
     *      Si non : 
     *          -on ajoute n copies de l'élément i à encoded. 
     *          - on initialise count = 1.
     *      De plus à chaque itérations on vérifie que si count > Byte.MAX_VALUE+2 : 
     *          Si oui : 
     *          - on crée un byte index qui est égale à 2-count. 
     *          - on ajoute à encoded l'index et l'élément i. 
     *          - on initialise count = 0
     *  
     *  Après avoir fait toute la boucle on vérifie le dernier élément lastNumber. 
     *  Si count > 2 : 
     *          - on crée un byte index qui est égale à 2-count. 
     *          - on ajoute à encoded l'index et l'élément lastNumber. 
     *      Sinon : 
     *          -on ajoute n copies de l'élément lastNumber à encoded. 
     *          
     * Finalement on peut retourner une liste non-modifiable de encoded. 
     * 
     *          
     * @param toEncode list à encoder
     * @return liste encodée sous forme de List<Byte>
     */
    public static List<Byte> encode(List<Byte> toEncode){
        
        int count = 1;
        List<Byte> encoded = new ArrayList<Byte>();
        
        for(int i = 0; i<toEncode.size()-1; i++){
            
            if(toEncode.get(i)<0){
                throw new IllegalArgumentException();
            }
            
            if(toEncode.get(i).equals(toEncode.get(i+1))){
                count++;
            }else if(count>2){
                byte index = (byte) (2-count);
                encoded.add(index);
                encoded.add(toEncode.get(i));
                count = 1;
            }else{
                encoded.addAll(Collections.nCopies(count, toEncode.get(i)));
                count = 1;
            }
            
            if(count>Byte.MAX_VALUE+2){
                byte index = (byte)(2-count);
                encoded.add(index);
                encoded.add(toEncode.get(i));
                count = 0;
            }
        }
        
        int lastNumber = toEncode.size()-1;
        if(count>2){
            byte index = (byte) (2-count);
            encoded.add(index);
            encoded.add(toEncode.get(lastNumber));
        }else{
            encoded.addAll(Collections.nCopies(count, toEncode.get(lastNumber)));
        }
        
        return Collections.unmodifiableList(encoded);
    }
    /**
     * Cette méthode décode une liste de Byte
     * On commence par initialisé une list<Byte> decoded vide, et une boolean alreadyDecoded = false. 
     * Ensuite on parcours tous les éléments de toDecode avec une itération for(int i ...) 
     * 
     * On vérifie si on a déja décoder le byte avec alreadyDecoded : 
     * Si non : 
     *  - on pose b = l'élément i de toDecode.
     *  - si b < 0 : 
     *      - on calcule l'index en faisant la valeur absolue de b +2 
     *      - on ajoute index fois la i+1 élément de toDecode à decoded. 
     *      - on set alreadyDecoded = true car le prochaine élément à déja été décoder
     *  - Sinon si on n'est pas au dernier élément ( toDecode.size() <i-1 ) et que b est égal à la prochaine valeur 
     *      - on ajoute deux fois la valeur b à decoded
     *      - on set alreadyDecode = true car le prochain élélment a déjà été décoder
     *  - Sinon : 
     *      - on ajoute une fois la valeur b à decoded. 
     *      
     * Si oui : 
     *   - on set alreadyDecoded = false comme cela on peut décoder le prochain byte. 
     *   
     *   
     * On termine en retournant une list non modifiable de decoded. 
     * 
     * @param toDecode la list à décoder
     * @return une list<Byte> décodée à partir de toDecode
     */
    public static List<Byte> decode(List<Byte> toDecode){
        List<Byte> decoded = new ArrayList<Byte>();
        boolean alreadyDecoded = false;
        for(int i = 0; i<toDecode.size();i++){
            if(!alreadyDecoded){
                Byte b = toDecode.get(i); 
                if(b < 0){
                    int index = Math.abs(b)+2;
                    decoded.addAll(Collections.nCopies(index, toDecode.get(i+1)));
                    alreadyDecoded = true;
                }else if(toDecode.size()<i-1 && b.equals(toDecode.get(i+1))){
                    decoded.add(b);
                    decoded.add(b);
                    alreadyDecoded = true;
                }else{
                    decoded.add(b);
                }
                
            }else{
            
                alreadyDecoded = false;
            }
            
        }
        
        
        return Collections.unmodifiableList(decoded);
    }
}
