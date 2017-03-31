package ch.epfl.xblast.client;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.RunLengthEncoder;
import ch.epfl.xblast.SubCell;
import ch.epfl.xblast.client.GameState.Player;
/**
 * Classe qui va créer un GameState à partir d'une list de Byte serialisée. 
 * Elle est final public et non instanciable ( C-à-d le constructeur est privé et vide ) 
 * Elle offre une seule méthode public static. 
 * Sinon il y a 4 attributs privé static qui correspondent au 4 ImageCollection possible. 
 * 
 * @author Johan Lanzrein (257221)
 *
 */
public final class GameStateDeserializer {
    /**
     * ImageCollection des blocks du board
     */
    private final static ImageCollection BLOCK = imageCollectionOf("block");
    /**
     * ImageCollection des Explosions
     */
    private final static ImageCollection EXPLOSIONS = imageCollectionOf("explosion");
    /**
     * ImageCollection des joueurs
     */
    private final static ImageCollection PLAYERSIMAGE = imageCollectionOf("player");
    /**
     * ImageCollection du Score / et aussi du temps 
     */
    private final static ImageCollection SCORE = imageCollectionOf("score");
    
    private GameStateDeserializer(){};
    /**
     * Unique méthode publique et statique de la classe. Elle va à partir d'une List de byte passé en paramètre associer l'état de jeu correspondant. ( Un état de jeu GameState du client ) 
     * Pour ce faire on décompose cette méthode en plusieurs méthodes privées. Pour simplifier. 
     * On commence par vérifier que serialized n'est pas vide. Si oui on lève une exception. 
     * Ensuite on prend le premier byte de la liste qui est la taille de la première séquence. 
     * On identifie la taille de la deuxième séquence (Bombes et explosions) en cherchant le byte à l'index fstIndex + 1. 
     * On créer une liste boardImages en utilisant la méthode privée boardImage en passant la list de byte et le fstIndex comme paramètres
     * On instancie ensuite deux attributs fstBoundBomb et sndBoundBomb qui représente les index ou sont les byte encodé de bombes est explosions ( C-à-d de quel index à lequel ) 
     * On créer une List<Image> bombsImage qui est définie par la méthode bombsImage en passant serialized, fstBoundBomb et sdnBoundBomb comme argument. 
     * Ensuite On créer la liste des players en utilisant la méthode players en passant comme argument serialized et sdnBoundBomb car les byte des players commence dès que les byte de la bombes et explosions sont finis
     * 
     * Ensuite on créer la list d'image scoresImage en utilisant la méthode scoresImage en passant players comme argument. 
     * Et pour finir on créer la list d'image timeImage en utilisant la méthode timeImage en passant comme argument le dernier byte de serialized. 
     * 
     * On retourne un nouveau GameState créer à partir de toutes ces listes que l'on à créer
     * @param serialized une list de byte encodée à transformer en GameState
     * @return Le GameState actuel du point du vue du client 
     */
    public static GameState deserializeGameState(List<Byte> serialized){
        
        if(serialized.isEmpty()){
            throw new IllegalArgumentException();
            
        }
        
        int fstIndex = Byte.toUnsignedInt(serialized.get(0));
        int sndIndex = Byte.toUnsignedInt(serialized.get(fstIndex+1));
        List<Image> boardImages = boardImage(serialized, fstIndex);
        
        int fstBoundBombs = fstIndex+2;
        int sdnBoundBombs = fstIndex+sndIndex+2;
        List<Image> bombsImage = bombsImage(serialized, fstBoundBombs, sdnBoundBombs);
        
        List<Player> players = players(serialized, sdnBoundBombs);
        
        List<Image> scoresImage = scoresImage(players);
        int imageIndex = sdnBoundBombs + 4*PlayerID.values().length;
        List<Image> timeImage = timeImage(serialized.get(imageIndex));
        
        
        return new GameState(players, boardImages, bombsImage, scoresImage, timeImage);
    }
    
    /**
     * Cette méthode va créer une list d'image à partir de byte. 
     * On commence par créer une subList qui va du byte 1 jusqu'au byte fstIndex+1 de serialized. 
     * Ensuite on décode cette liste obtenue avec le RunLengthEncoder.decode.
     * Comme les cellules ont été compressés dans l'ordre en sprial il faut les remettre dans l'ordre. Pour ce faire on créer un tableau primitif de type byte
     * Qui va stocker les attributs dans l'ordre RowMajorOrder. 
     * Pour ce faire on itére simplement sur la liste en Cell.SPIRAL_ORDER. et à chaque instance on ajoute dans la case c.rowMajorIndex le i-ème nombre de board. 
     * 
     * Une fois que c'est fait on créer une liste d'image que l'on remplis en itérant sur le tableau boardRowMajor 
     * et à chaque fois on ajoute l'image correspondantes au byte b du tableau de l'attribut BLOCK
     * 
     * 
     * @param serialized la liste de byte. 
     * @param fstIndex l'index jusqua ou va la liste pour le board 
     * @return une list<Image> qui représente le board. 
     */
    private static List<Image> boardImage(List<Byte> serialized, int fstIndex){
        List<Byte> board = new ArrayList<Byte>(serialized.subList(1, fstIndex+1));
        board = RunLengthEncoder.decode(board);
        byte[] boardRowMajor = new byte[Cell.COLUMNS*Cell.ROWS];
        
        int i = 0;
        for(Cell c : Cell.SPIRAL_ORDER){
            boardRowMajor[c.rowMajorIndex()] = board.get(i);
            i++;
        }
        
        List<Image> boardImages = new ArrayList<Image>();
        for(byte b : boardRowMajor){
            boardImages.add(BLOCK.image(b));
        }
        
        return Collections.unmodifiableList(boardImages);
    }
    /**
     * Cette méthode calcule la list d'image poru les bombes et explosions
     * On commence par isoler la subList de serialized qui est entre les deux bornes données en paramètre. 
     * On décode la liste isolée ( bombsExplosions) avec le RunLengthEncoder.decode. 
     * Ensuite pour chaque byte on ajoute à une liste créer auparavant ( bombsImage) l'image correspondante ( si il y en a une ) au byte b par rapport au ImageCollection EXPLOSIONS
     * 
     * On retourne la list<Image> ainsi obtenue
     * 
     *  
     * @param serialized la liste de byte sérialisée de l'état du jeu
     * @param fstBound la première borne des byte des bombes et explosions
     * @param sdnBound la deuxième borne des byte des bombes et explosions 
     * @return une list<Image> représentant les bombes et explosions 
     */
    private static List<Image> bombsImage(List<Byte> serialized, int fstBound, int sdnBound){
        List<Byte> bombsExplosions = new ArrayList<Byte>(serialized.subList(fstBound, sdnBound));
        bombsExplosions = RunLengthEncoder.decode(bombsExplosions);
        List<Image> bombsImage = new ArrayList<Image>();

        for(byte b : bombsExplosions){
            bombsImage.add(EXPLOSIONS.imageOrNull(b));
        }
        
        return Collections.unmodifiableList(bombsImage);
    }
    
    /**
     * Méthode qui créer une liste de joueur à partir de la liste de byte. 
     * Les joueurs devant être stocké dans l'ordre de leur ID. ( En sachant qu'ils ont été serialisé dans cet ordre ) 
     * 
     * On itére 4 fois en ayant un paramètre j qui permettra d'efficacement créer les joueurs. 
     * On note que à chaque fois qu'il intervient on a utilisé j*4 enfait de cette manière on accède exactement au byte désiré. 
     * 
     * On commence par trouver les vies ( qui sont à partir de fstBound ) ensuite la coordonnée x de la position, puis la coordonnées y de la position 
     * et pour finir le byte d'image du joueurs si il en a une associé à partir de l'attribut static PLAYERSIMAGE
     * Ils sont stocké à la suite donc on prend simplement les byte les uns après les autres. 
     * Ensuite pour déterminer l'id on se base sur le paramètre j. Son nombre actuel n'est autre que l'ordinal de l'id. Ce qui rend simple de trouver l'id correspondant. 
     * 
     * On créer ensuite un nouveau joueur à partir des informations obtenues et on l'ajoute à la liste players. 
     * 
     * Une fois l'itération terminée sur les 4 joueurs on retourne la list obtenue de 4 joueurs. 
     * 
     * @param serialized la liste de byte qui contient entre autres l'état des joueurs. 
     * @param fstBound l'endroit d'ou on commence a observer la liste de byte. 
     * @return la list<Player> des joueurs. 
     */
    private static List<Player> players(List<Byte> serialized, int fstBound){
        List<Player> players = new ArrayList<Player>();

        for(int j = 0; j<4; j++){
            
            int lives = serialized.get(fstBound +(j*4));
            int x = Byte.toUnsignedInt(serialized.get(fstBound+1 +(j*4)));
            int y = Byte.toUnsignedInt(serialized.get(fstBound+2 +(j*4)));
            byte img = serialized.get(fstBound+3+ (j*4));
            Image playerImg = PLAYERSIMAGE.imageOrNull(img);
            PlayerID id;
            if(j > 1){
                id = (j==3 ? PlayerID.PLAYER_4 : PlayerID.PLAYER_3);
            }else{
                id = (j==0 ? PlayerID.PLAYER_1 : PlayerID.PLAYER_2);

            }
            
            players.add(new Player(id, lives, new SubCell(x,y), playerImg));
        }
        
        return Collections.unmodifiableList(players);
    }
    
    /**
     * Cette méthode créer un tableau des scores. 
     * On itére sur les joueurs. On instancie un index qui nous permettra de trouver l'image désirée. Si le joueur est mort ( ses vies sont à 0 ) 
     * Alors on incrémente l'index de 1. 
     * Ensuite on ajoute à scoresImage les image associés avec SCORE avec les nombres ( dans l'ordre ) index, 10 ,11. 
     * Ensuite si il s'agit du joueur 2 on ajoute à scoresImage 8 copie de l'image ayant l'index 12 de SCORE. 
     * 
     * On retourne la liste ainsi obtenue
     * 
     * @param players les joueurs de qui on doit construire les scores. 
     * @return List<Image> du score. 
     */
    private static List<Image> scoresImage(List<Player> players){
        List<Image> scoresImage = new ArrayList<Image>();
        for(Player p : players){
            int index = p.id().ordinal()*2;
            if(p.lives()==0){
                index++;
            }
            
            scoresImage.add(SCORE.image(index));
            scoresImage.add(SCORE.image(10));
            scoresImage.add(SCORE.image(11));
            
            if(p.id().equals(PlayerID.PLAYER_2)){
                scoresImage.addAll(Collections.nCopies(8, SCORE.image(12)));
            }

        }
        
        return Collections.unmodifiableList(scoresImage);
        
    }
    /**
     * Les image du temps sont découpé en tranches de 2 secondes. Et notre temps que l'on recoit est arrondi au multiple de deux supérieur. 
     * Dès lors il s'agit simplement d'ajout time copie de l'image correspondant à l'index 21 de SCORE ( temps restant ) 
     * et ensuite ajouté 60 - time copie de l'image correspondant à l'index 20 de SCORE ( temps écoulé ) 
     * On retourne esuite la liste ainsi obtenue
     * 
     * @param time le temps restant au jeu 
     * @return list<Image> du temps 
     */
    private static List<Image> timeImage(byte time){
        List<Image> timeImages = new ArrayList<Image>();

        timeImages.addAll(Collections.nCopies(time, SCORE.image(21)));
        timeImages.addAll(Collections.nCopies(60-time, SCORE.image(20)));
        
        return Collections.unmodifiableList(timeImages);
    }
    
    /**
     * 
     * @param string Nom du dossier ou on cherche l'image collection
     * @return une nouvelle image collection basée sur le dossier ayant le nom passé en argument
     */
    private static ImageCollection imageCollectionOf(String string){
        return new ImageCollection(string);
    }
    
    
}
