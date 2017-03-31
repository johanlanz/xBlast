package ch.epfl.xblast.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JComponent;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.client.GameState.Player;
/**
 * Classe héritant de JComponent. C'est un component qui va dessiner l'état de jeu
 * 
 * @author Johan Lanzrein (257221)
 *
 */
@SuppressWarnings("serial")
public final class XblastComponent extends JComponent {
    
    private GameState gameState = null;
    private PlayerID id = null;
    private static final int Y_COORDINATE_FOR_LIFE = 659;
    private static final int X_COORDINATE_P1_FOR_LIFE = 96;
    private static final int X_COORDINATE_P2_FOR_LIFE = 240;
    private static final int X_COORDINATE_P3_FOR_LIFE = 768;
    private static final int X_COORDINATE_P4_FOR_LIFE = 912;


    /**
     * @return les dimension du component
     */
    @Override
    public Dimension getPreferredSize(){
        return new Dimension(960,688);
    }
    
    /**
     * Redéfinition de la méthode paintComponent. 
     * 
     * On commence par peindre le tableau en ajoutant les uns après les autres les images du board du gameState. 
     * Ensuite les explosions en ajoutant les unes après les autres les images des explosions du gameState. 
     * Afin de ne pas superposer inutilement les images, on créer des index pour la longueur et la largeur que l'on incrémente à chaque image pour index de largeur, 
     * et une fois que l'on atteint un index de largeur >= au nombre de colonnes, on remet l'index de largeur à 0 et on incrémente l'index de hauteur de 1. 
     * Ensuite pour positionner correctement les images on les pose au coordonnées largeur d'image * index largeur, longueur d'image * index longueur
     * 
     * Pour ce qui est du score on le met a la hauteur d'un block * le nombre de ligne ( Cell.ROWS ). Et on les appond comme avant avec l'aide d'un 
     * index de largeur en itérant sur la liste d'image scores du gamestate. 
     * 
     * Ensuite pour le temps on ajoute a la hauteur la hauteur d'une image de score. Et on appond les image de la liste du temps de gamestate. 
     * Avec l'aide d'un index de largeur. 
     * Après cela on ajoute le nombre de vie des joueurs, en suivant la consigne donnée..
     * 
     * Pour la suite, on a définit un comparateur qui vérifie la coordonnées y des joueurs et si le 2ème est plus grande que le 1er il mettra le deuxième avant. 
     * Ainsi on obtient avec ce comparateur une liste triée des joueur du plus petit y au plus grand. 
     * Ensuite on créer un deuxième comparteur qui trie les joueur en cas de coordonnée y égale. Dans ce cas, il vérifie si l'id du joueur est égal au premier ou au deuxième. Si égal au premier -> 1 pour le mettre toute à la fin 
     * sinon -s1 pour le mettre aussi avant. Si aucun des deux est égal on retourne 0. 
     * 
     * Ensuite on trie les joueurs avec ces deux comparateur. ( en faisant le premier.thenComparing(deuxieme)). Et on va dessiner les joueur dans cet ordre. 
     * 
     * Ainsi on a dessiner l'état du jeu 
     */
    @Override
    protected void paintComponent(Graphics g0){
        if(gameState!=null){
        Graphics2D g = (Graphics2D) g0;
        
        int indexW = 0; 
        int indexH = 0; 
        int height = gameState.board().get(0).getHeight(null);
        int width = gameState.board().get(0).getWidth(null);
        
        //Mise en place des images du board 
        for(Image boardImg : gameState.board()){
            g.drawImage(boardImg, width*indexW, height*indexH, null);
            indexW++;
            if(indexW >= Cell.COLUMNS){
                indexW = 0; 
                indexH++;
            }
        }
        indexW = indexH = 0;
        //Mise en place des image d'explosions + bombes
        for(Image explImg : gameState.explosions()){
            g.drawImage(explImg,  width*indexW,height*indexH, null);
            indexW++;
            if(indexW >= Cell.COLUMNS){
                indexW = 0; 
                indexH++;
            }
        }
        
        width = gameState.scores().get(0).getWidth(null);
        height = height*Cell.ROWS;
        
        //Mise en place du score 
        indexW = 0; 
        for(Image scoreImg : gameState.scores()){
            g.drawImage(scoreImg,  width*indexW, height,null);
            indexW++;
        }
        
        

        
        //mise en place du temps
        height = height+gameState.scores().get(0).getHeight(null);
        width = gameState.time().get(0).getWidth(null);
        indexW=0;
        for(Image timeImg : gameState.time()){
            g.drawImage(timeImg, width*indexW,height,  null);
            indexW++;
        }
        //Mise en place du nombre de vie des joueurs. 
        
        for(Player p : gameState.players()){

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 25));
            switch (p.id()) {
            case PLAYER_1:
                g.drawString(p.lives() + "", X_COORDINATE_P1_FOR_LIFE, Y_COORDINATE_FOR_LIFE);
                break;
            case PLAYER_2:
                g.drawString(p.lives() + "", X_COORDINATE_P2_FOR_LIFE, Y_COORDINATE_FOR_LIFE);
                break;
            case PLAYER_3:
                g.drawString(p.lives() + "", X_COORDINATE_P3_FOR_LIFE, Y_COORDINATE_FOR_LIFE);
                break;
            case PLAYER_4:
                g.drawString(p.lives() + "", X_COORDINATE_P4_FOR_LIFE, Y_COORDINATE_FOR_LIFE);
                break;
            default:
                throw new IllegalArgumentException();
            }

            
        }
        
        //Création d'un comparateur de joueur 
        Comparator<Player> playerC = new Comparator<Player>() {

            @Override
            public int compare(Player p1, Player p2) {
                
                return Integer.compare(p1.position().y(), p2.position().y());
            }

        };

        Comparator<Player> caseEqualsY = new Comparator<Player>() {

            @Override
            public int compare(Player o1, Player o2) {
                if (o1.id().equals(id)) {
                    return 1;
                }
                if (o2.id().equals(id)) {
                    return -1;
                }
                return 0;
            }

        };
        
        playerC = playerC.thenComparing(caseEqualsY);
        List<Player> playersOrdered = new ArrayList<Player>(gameState.players());
        Collections.sort(playersOrdered, playerC);
        
        //Mise en place des joueurs. 
        for(Player p : playersOrdered){
            g.drawImage(p.image(), 4*p.position().x()-24, 3*p.position().y()-52, null);
        }
        
        }
        
        
    }
    /**
     * Redéfinit le component en modifiant ses attributs, et on utilise la méthode repaint après
     * @param gs le gs actuel
     * @param id l'id du joueur pour qui on peint. 
     */
    public void setGameState(GameState gs, PlayerID id){
        this.gameState = gs;
        this.id = id;
        
        repaint();
    }
}
