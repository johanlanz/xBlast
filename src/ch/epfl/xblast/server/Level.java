package ch.epfl.xblast.server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.server.painting.BlockImage;
import ch.epfl.xblast.server.painting.BoardPainter;
/**
 * Classe qui définit un niveau de xBlast. Elle a un attribut BoardPainter qui est le peintre du niveau et un GameState
 * 
 * @author Johan Lanzrein (257221)
 *
 */
public final class Level {
    private final BoardPainter painter;
    private final GameState initialGameState;
    /**
     * Le level par défaut il est crée avec la méthode defaultLevel. 
     * Ce level par défaut est une partie nouvelle avec aucune explosion et aucune bombe et 4 joueurs
     */
    public static final Level DEFAULT_LEVEL = defaultLevel();
    /**
     * Le constructeur de level. Il crée un nouveau level avec les attributs passé en paramètre 
     * De plus on vérifie que ces paramètres ne soit pas nuls. 
     * 
     * @param painter le BoardPainter du level
     * @param gs le gameState du level
     */
    public Level(BoardPainter painter, GameState gs){
        this.painter = Objects.requireNonNull(painter);
        this.initialGameState = Objects.requireNonNull(gs);
    }
    /**
     * Un accesseur de BoardPainter
     * @return le BoardPainter du Level
     */
    public BoardPainter getBoardPainter(){
        return this.painter;
    }
    
    /**
     * Un accesseur du GameState
     * @return le GameState du level
     */
    public GameState getGameState(){
        return this.initialGameState;
    }
    /**
     * Cette méthode privée sert à crée le DEFAULT_LEVEL 
     * On commence par crée une palette qui est celle par défaut. 
     * Ensuite le Board montré dans la vidéo d'introduction. 
     * Ensuite une liste players de 4 players à leur position initial avec life = 3 , maxBombs = 2, range = 3. 
     * On crée un initialGameState en passant les paramètres board et players
     * 
     * Pour finir on retourne un nouveau level avec les argument palette et initialGameState
     * 
     * @return le DEFAULT_LEVEL un Level par défaut
     */
    private static Level defaultLevel(){
        Map<Block, BlockImage> palette = new HashMap<Block,BlockImage>();
        palette.put(Block.FREE, BlockImage.IRON_FLOOR);
        palette.put(Block.BONUS_BOMB, BlockImage.BONUS_BOMB);
        palette.put(Block.BONUS_RANGE, BlockImage.BONUS_RANGE);
        palette.put(Block.INDESTRUCTIBLE_WALL, BlockImage.DARK_BLOCK);
        palette.put(Block.DESTRUCTIBLE_WALL, BlockImage.EXTRA);
        palette.put(Block.CRUMBLING_WALL, BlockImage.EXTRA_O);
        BoardPainter boardPainter = new BoardPainter(palette, BlockImage.IRON_FLOOR_S);
        
        Block __ = Block.FREE;
        Block XX = Block.INDESTRUCTIBLE_WALL;
        Block xx = Block.DESTRUCTIBLE_WALL;
        Board board = Board.ofQuadrantNWBlocksWalled(
          Arrays.asList(
            Arrays.asList(__, __, __, __, __, xx, __),
            Arrays.asList(__, XX, xx, XX, xx, XX, xx),
            Arrays.asList(__, xx, __, __, __, xx, __),
            Arrays.asList(xx, XX, __, XX, XX, XX, XX),
            Arrays.asList(__, xx, __, xx, __, __, __),
            Arrays.asList(xx, XX, xx, XX, xx, XX, __)));
        
        int lives = 3;
        int maxBombs = 2;
        int range = 3;
        
        Player p1 = new Player(PlayerID.PLAYER_1, lives, new Cell(1,1), maxBombs, range);
        Player p2 = new Player(PlayerID.PLAYER_2, lives, new Cell(13,1), maxBombs, range);
        Player p3 = new Player(PlayerID.PLAYER_3, lives, new Cell(13,11), maxBombs, range);
        Player p4 = new Player(PlayerID.PLAYER_4, lives, new Cell(1,11), maxBombs, range);
        List<Player> players = Arrays.asList(p1,p2,p3,p4);

        GameState initialGame = new GameState(board, players);
        return new Level(boardPainter, initialGame);
    }
    
}
