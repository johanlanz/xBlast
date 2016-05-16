package ch.epfl.xblast.client;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.SubCell;

/**
 * Classe qui définit un GameState du point de vue d'un client. 
 * Elle contient 5 attributs de type List. 
 * Des joueurs, les images du board, les images des explosions, les images du scores, et les image du temps. 
 * 
 * @author Johan Lanzrein (257221)
 *
 */
public final class GameState {
    private final List<Player> players;
    private final List<Image> board;
    private final List<Image> explosions;
    private final List<Image> scores; 
    private final List<Image> time;
    /**
     * Constructeur de GameState. On copie les attributs passé en paramètres en faisant une copie défensive que l'on rend imuable. 
     * 
     * @param players List des joueurs.
     * @param board List des images du board
     * @param explosions List des images des explosions 
     * @param scores List des images du scores
     * @param time List des images du temps
     */
    public GameState(List<Player> players, List<Image> board, List<Image> explosions, List<Image> scores, List<Image> time){
        
        this.players = Objects.requireNonNull(Collections.unmodifiableList(new ArrayList<Player>(players)));
        this.board = Objects.requireNonNull(Collections.unmodifiableList(new ArrayList<Image>(board)));
        this.explosions = Objects.requireNonNull(Collections.unmodifiableList(new ArrayList<Image>(explosions)));
        this.scores = Objects.requireNonNull(Collections.unmodifiableList(new ArrayList<Image>(scores)));
        this.time = Objects.requireNonNull(Collections.unmodifiableList(new ArrayList<Image>(time)));
        
    }
    
     /**
      * TODO commenter ces méthodes 
      * @return
      */
    public List<Player> players(){
        return new ArrayList<Player>(players);
    }
    
    public List<Image> board(){
        return new ArrayList<Image>(board);
    }
    public List<Image> explosions(){
        return new ArrayList<Image>(explosions);
    }
    public List<Image> scores(){
        return new ArrayList<Image>(scores);
    }
    public List<Image> time(){
        return new ArrayList<Image>(time);
    }
    
    



    /**
     * Une classe qui définit un joueur du point de vue du client. Il est définit par 
     * Son ID, son nombre de vie, sa position ( en SubCell ) et l'image qu'il a.
     * @author Johan Lanzrein (257221)
     *
     */
    public static final class Player{
        
        private PlayerID id;
        private int lives;
        private SubCell position;
        private Image image;
        /**
         * Constructeur du Player. Il construit un joueur à partir des attributs passé en paramètres. 
         * 
         * @param id id du joueur
         * @param lives nombres de vie du joueur
         * @param position la position du joueur
         * @param image l'image du joueur
         */
        public Player(PlayerID id, int lives, SubCell position, Image image){
            this.id = id;
            this.lives = lives;
            this.position = position;
            this.image = image;
            
        }
        
        
        /**
         * 
         * @return le nombre de vie du joueur
         */
        public int lives(){
            return lives;
        }
        /**
         * 
         * @return le PlayerID du joueur. 
         */
        public PlayerID id(){
            return id;
        }
        /**
         * 
         * @return position du joueur
         */
        public SubCell position(){
            return new SubCell(position.x(), position.y());
        }
        /**
         * 
         * @return Image du joueur
         */
        public Image image(){
            return image;
        }
        
    }
}
