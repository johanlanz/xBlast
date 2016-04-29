package ch.epfl.xblast.client;

import java.awt.Image;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.imageio.ImageIO;

/**
 * Classe qui représente une collection d'image. 
 * Elle a un unique attribut de type table associative qui associe à un index, l'image correspondante. 
 * @author Johan Lanzrein (257221)
 *
 */
public final class ImageCollection {
    private final Map<Integer,Image> images;
    /**
     * Constructeur d'ImageCollection. A partir d'un nom de dossier on crée une File que l'on parcourt. 
     * Ensuite on met dans images chaque image et l'index correspondant que l'on à lu en isolant les 3 premiers caractères de chaque images. 
     * 
     * @param dirName le nom du dossier ou se trouve les images. 
     */
    public ImageCollection(String dirName){
            images = new HashMap<Integer, Image>();

        try {
            File dir = new File(ImageCollection.class.getClassLoader()
                    .getResource(dirName)
                    .toURI());
            
            
            for(File img : dir.listFiles()){
                int index = Integer.parseInt(img.getName().substring(0,3));
                images.put(index, ImageIO.read(img));
            }
        } catch (Exception e1) {
            // TODO how to handle. 
            e1.printStackTrace();
        }
        
    }
    /**
     * Cette méthode retourne l'index de l'image correspondante à l'index passé en paramètre. 
     * Si l'index est erronée elle renvoie une exception. 
     * @param index l'index de l'image recherchée.
     * @throws NoSuchElementException si l'index est erroné. 
     * @return l'image correspondante à l'index
     */
    public Image image(int index){
        
        if(!images.containsKey(index)){
            throw new NoSuchElementException();
        }
        return images.get(index);
    }
    /**
     * Cette méthdode retourne une image à partir de l'index donnée en paramètre. Si l'index n'est pas dans la table associative
     * elle retourne null
     * @param index l'index de l'image recherchée
     * @return image recherchée / ou null si l'index est introuvable. 
     */
    public Image imageOrNull(int index){
        if(!images.containsKey(index)){
            return null;
        }
        return images.get(index);
    }


}
