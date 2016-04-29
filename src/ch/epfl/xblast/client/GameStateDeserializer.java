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
    private final static ImageCollection BLOCK = imageCollectionBoard();
    /**
     * ImageCollection des Explosions
     */
    private final static ImageCollection EXPLOSIONS = imageCollectionExplosion();
    /**
     * ImageCollection des joueurs
     */
    private final static ImageCollection PLAYERS = imageCollectionPlayer();
    /**
     * ImageCollection du Score / et aussi du temps 
     */
    private final static ImageCollection SCORE = imageCollectionScore();
    
    private GameStateDeserializer(){};
    /**
     * Unique méthode public et static 
     * @param serialized
     * @return
     */
    public static GameState deserializeGameState(List<Byte> serialized){
        
        if(serialized.isEmpty()){
            throw new IllegalArgumentException();
            
        }
        
        int fstIndex = serialized.get(0);
        int sndIndex = serialized.get(fstIndex+1);

        List<Image> boardImages = boardImage(serialized, fstIndex);
        int fstBoundBombs = fstIndex+2;
        int sdnBoundBombs = fstIndex+sndIndex+2;
        
        List<Image> bombsImage = bombsImage(serialized, fstBoundBombs, sdnBoundBombs);
        

        
        
        List<Player> players = players(serialized, sdnBoundBombs);
        
        
        List<Image> scoresImage = scoresImage(players);
        
        
        List<Image> timeImage = timeImage(serialized.get(serialized.size()-1));
        
        
        return new GameState(players, boardImages, bombsImage, scoresImage, timeImage);
    }
    
    private static List<Image> boardImage(List<Byte> serialized, int fstIndex){
        List<Byte> board = new ArrayList<Byte>(serialized.subList(1, fstIndex+1));
        board = RunLengthEncoder.decode(board);
        byte[] boardRowMajor = new byte[Cell.COLUMNS*Cell.ROWS];
        List<Cell> spiralOrder = Cell.SPIRAL_ORDER;
        int i = 0;
        
        for(Cell c : spiralOrder){
            boardRowMajor[c.rowMajorIndex()] = board.get(i);
            i++;
        }
        
        List<Image> boardImages = new ArrayList<Image>();
        for(byte b : boardRowMajor){
            boardImages.add(BLOCK.image(b));
        }
        
        return Collections.unmodifiableList(boardImages);
    }
    
    private static List<Image> bombsImage(List<Byte> serialized, int fstBound, int sdnBound){
        List<Byte> bombsExplosions = new ArrayList<Byte>(serialized.subList(fstBound, sdnBound));
        bombsExplosions = RunLengthEncoder.decode(bombsExplosions);
        List<Image> bombsImage = new ArrayList<Image>();

        for(byte b : bombsExplosions){
            bombsImage.add(EXPLOSIONS.imageOrNull(b));
        }
        
        return Collections.unmodifiableList(bombsImage);
    }
    
    
    private static List<Player> players(List<Byte> serialized, int fstBound){
        List<Player> players = new ArrayList<Player>();

        for(int j = 0; j<4; j++){
            int lives = serialized.get(fstBound +(j*4));
            int x = Byte.toUnsignedInt(serialized.get(fstBound+1 +(j*4)));
            int y = Byte.toUnsignedInt(serialized.get(fstBound+2 +(j*4)));
            byte img = serialized.get(fstBound+3+ (j*4));
            Image playerImg = PLAYERS.imageOrNull(img);
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
    
    private static List<Image> timeImage(int time){
        List<Image> timeImages = new ArrayList<Image>();

        timeImages.addAll(Collections.nCopies(time, SCORE.image(21)));
        timeImages.addAll(Collections.nCopies(60-time, SCORE.image(20)));
        
        return Collections.unmodifiableList(timeImages);
    }
    
    
    private static ImageCollection imageCollectionBoard(){
        return new ImageCollection("block");
    }
    private static ImageCollection imageCollectionPlayer(){
        return new ImageCollection("player");
    }
    
    private static ImageCollection imageCollectionExplosion(){
        return new ImageCollection("explosion");
    }
    
    private static ImageCollection imageCollectionScore(){
        return new ImageCollection("score");
    }
    
    
}
