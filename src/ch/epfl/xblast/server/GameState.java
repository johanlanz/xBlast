package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.ArgumentChecker;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.Lists;
import ch.epfl.xblast.PlayerID;
/**
 * Classe qui définit un état de jeu. Elle a plusieurs attributs qui vont régir l'état du jeu à 
 * chaque ticks. 
 * 
 * @author Johan Lanzrein (257221) 
 *
 */
public final class GameState {
    private int ticks;
    private Board board;
    private List<Player> players;
    private List<Bomb> bombs;
    private List<Sq<Sq<Cell>>> explosions;
    private List<Sq<Cell>> blasts;
    private List<List<Player>> playerPriority = new ArrayList<List<Player>>() ;
    private static final Random RANDOM = new Random(2016);
    /**
     * On construit un nouvel état de jeu en fonction des paramètre donnés. 
     * On vérifie qu'ils soit non-nuls et ensuite on utilise unmodifiable list pour éviter des problème d'immuabilités
     * 
     * @param ticks les ticks actuel de l'état de jeu 
     * @param board le board actuel
     * @param players les joueurs en jeu
     * @param bombs les bombs du jeu
     * @param explosions les explosions actuelles du jeu 
     * @param blasts les particules d'explosions actuelles
     */
    public GameState(int ticks, Board board, List<Player> players, List<Bomb> bombs, List<Sq<Sq<Cell>>> explosions, List<Sq<Cell>> blasts){
        this.ticks = ArgumentChecker.requireNonNegative(ticks);
        this.board = Objects.requireNonNull(board); 
        this.players = Objects.requireNonNull(Collections.unmodifiableList(new ArrayList<Player>(players))); //TODO entouré de collections unmodifiableList !!
        this.bombs = Objects.requireNonNull(Collections.unmodifiableList(new ArrayList<Bomb>(bombs)));
        this.explosions = Objects.requireNonNull(Collections.unmodifiableList(new ArrayList<Sq<Sq<Cell>>>(explosions)));
        this.blasts = Objects.requireNonNull(Collections.unmodifiableList(new ArrayList<Sq<Cell>>(blasts)));
        if(playerPriority.isEmpty()){
            playerPriority = new ArrayList<List<Player>>(playerPrioritiesList());
        }
    }
    /**
     * Construit un état de jeu avec comme paramètre de base imposé : 
     * ticks = 0 et des liste de bombs, explosions et blast vide 
     * --> utilisé pour une nouvelle partie (?) 
     * @param board
     * @param players
     */
    public GameState(Board board, List<Player> players){
        this(0, board, players, new ArrayList<Bomb>(), new ArrayList<Sq<Sq<Cell>>>(), new ArrayList<Sq<Cell>>());
    }
    /**
     * retourne les ticks actuels du jeu
     * @return ticks du gamestate
     */
    public int ticks(){
        return this.ticks;
    }
    /**
     * décide si la partie est terminée. 
     * La partie est terminée si 
     * les ticks du GameState sont égaux au nombre maximum des ticks
     * ou si il y a moins ou exactement 1 joueur en vie
     * @return true/ false en fonction de ce qui est expliqué plus haut
     */
    public boolean isGameOver(){
        if(Ticks.TOTAL_TICKS == this.ticks){
            return true;
        }
        
        return alivePlayers().size()<=1 ? true : false; //TODO : vérif
    }
    /**
     * retourne le temps restant en seconde. p
     * @return ticks total - ticks actuel. divisé par les ticks par seconde
     */
    public double remainingTime(){
       return (Ticks.TOTAL_TICKS-ticks)/Ticks.TICKS_PER_SECOND; 
    }
    /**
     * Retourne un vainqueur si il y en a un. 
     * Il y a un vainqueur si il y a uniquemne un joueur en vie 
     * @return optional empty -> pas exactement un joueur en vie -> 
     * @return optional playerID -> l'id du dernier survivant
     */
    public Optional<PlayerID> winner(){
        
        List<Player> playerLeft = alivePlayers();
        if(playerLeft.size()!=1){
            return Optional.empty();
        }
        return Optional.of(playerLeft.get(0).id());
    }
    /**
     * retourne le board actuel du jeu
     * @return le board actuel du jeu
     */
    public Board board(){
        return board;
    }
    /**
     * 
     * @return une liste des joueurs en jeu
     */
    public List<Player> players(){
        return new ArrayList<Player>(players);
    }
    /**
     *
     * @return les joueurs en vie
     */
    public List<Player> alivePlayers(){
        List<Player> alivePlayers = new ArrayList<Player>();
        for(Player p : players){
            if(p.isAlive()){
                alivePlayers.add(p);
            }
        }
        return alivePlayers;
    }
    /**
     * 1ère partie : 
     * On prend les blasts actuels ( blasts0) et on ajoute la queue de chaque blast si le block ou la particule est est libre. ( ainsi on vérifie
     * que la particule ne traverse pas les murs ) 
     * 2ème partie : on prend la tête de chaque explosions qui représente en fait une particule. 
     * 
     * 
     *  
     * @param blasts0 particule d'explosions actuels
     * @param board0 le board actuel
     * @param explosions0 la liste des particules d'explosions futures
     * @return une liste de next blasts
     */
    private static List<Sq<Cell>> nextBlasts(List<Sq<Cell>> blasts0, Board board0, List<Sq<Sq<Cell>>> explosions0){
        List<Sq<Cell>> nextBlasts = new ArrayList<Sq<Cell>>();
        for(Sq<Cell> blast : blasts0){
            if(!blast.isEmpty()){
                Sq<Cell> nextBlast = blast.tail();
                
                if(board0.blockAt(blast.head())==Block.FREE){
                nextBlasts.add(nextBlast);
                }
            }
        }
        for(Sq<Sq<Cell>> explosion : explosions0){
            if(!explosion.isEmpty()){
                nextBlasts.add(explosion.head());
            }
            
        }
        
        return nextBlasts;
    }
    
    /**
     * retourne une table associative de cell et bomb
     * Chaque Cell est associé avec la bombe ou elle est posée
     * @return la table associative expliquée 
     */
    public Map<Cell, Bomb> bombedCells(){
        Map<Cell, Bomb> bombedCells = new HashMap<Cell, Bomb>();
        for(Bomb b : bombs){
            bombedCells.put(b.position(), b);
        }
        return bombedCells;
    }
    /**
     * 
     * @return un ensemble de toute les cellules touchées par des particules d'explosions
     */
    public Set<Cell> blastedCells(){
        Set<Cell> blastedCells = new HashSet<Cell>();
        for(Sq<Cell> blast : blasts){
            if(!blast.isEmpty()){
            blastedCells.add(blast.head());
                }
            }
        
        return blastedCells;
    }
    
    /**
     * Cette méthode calcule l'évolution d'un jeu en un ticks on effectue plusieurs opérations et appels a des méthode privées
     * 
     * 1) les particules d'explosions évoluent avec la méthode nextBlasts(...)
     * 2) On compute consumedBonus et playerBonuses pour voir si des bonus on été consommés et par qui 
     * 3) le plateau évolue avec la méthode nextBoard(...)
     * 4) les explosions évoluent avec la méthode nextExplosions(...)
     * 5) les bombes actuelles évoluent -> si elles sont en contact d'une particule d'explosion ou que la mèche atteint 1 elles explosent
     * sinon on les ajoute a bombs1 en raccourcicant la méche d'une unité. 
     * 6) on ajoute les bombes posées par les joueurs à bombs1 avvec a méthode newlyDroppedBombs(...)
     * 7) les joueurs évoluent avec la méthode nextPlayers(...)
     * @param speedChangeEvents
     * @param bombDropEvents
     * @return le nouvel état de jeu
     */
    public GameState next(Map<PlayerID, Optional<Direction>> speedChangeEvents, Set<PlayerID> bombDropEvents ){
    List<Player> priority = priorityPlayer();
    Set<Cell> consumedBonuses = new HashSet<Cell>();
    Map<PlayerID, Bonus> playerBonuses = new HashMap<PlayerID, Bonus>();
          
    List<Sq<Cell>> blasts1 = nextBlasts(this.blasts, board, explosions);
    
      
    for(Player p : priority){
        if(board.blockAt(p.position().containingCell())== Block.BONUS_BOMB||board.blockAt(p.position().containingCell())== Block.BONUS_BOMB){
            if(!consumedBonuses.contains(p.position().containingCell())){
            consumedBonuses.add(p.position().containingCell());
            playerBonuses.put(p.id(),board.blockAt(p.position().containingCell()).associatedBonus());
            }
        }
    }
    Board board1 = nextBoard(this.board, consumedBonuses, blastedCells());
    List<Sq<Sq<Cell>>> explosions1 = nextExplosions(explosions);
     
    
    List<Bomb> bombs1 = new ArrayList<Bomb>();
    for(Bomb b : bombs){
        if(blastedCells().contains(b.position())||b.fuseLength() == 1){
            explosions1.addAll(b.explosion());
            
        }else{
           bombs1.add(new Bomb(b.ownerId(), b.position(), b.fuseLengths().tail(), b.range()));
           
        }
    }
    
    bombs1.addAll(newlyDroppedBombs(priority, bombDropEvents, bombs1));
    
    
    
    List<Player> players1 = nextPlayers(players, playerBonuses, consumedBonuses, board1, consumedBonuses, speedChangeEvents);
    
    return new GameState(this.ticks++, board1, players1, bombs1, explosions1, blasts1);
    }
    /**
     * Cette méthode s'occupe de gérer le prochain Board du GameState
     * 
     * On commence par faire une nouvelle list pour le futur board. Cette liste contiendra chaque Sq<Block> du board0, dans l'ordre row major order.
     * 
     * ensuite on vérifie si des bonus on été consommés. Si c'est le cas on modifie la case de blocksNextBoard correspondante pour qu'elle devienne libre.
     * 
     * ensuite on vérifie si des particules d'explosions on touchée soit un mur destructible ou un bonus
     * Cas de mur destructible : on prépare une séquence "crumbling" de longueur WALL_CRUMBLING_TICKS qui contient des block CRUMBLING, 
     * ensuite on génére un nombre aléatoire entre 0(compris) et 3(non-compris) suivant le nombre obtenu on appond à la Sq crumbling une séquence de longueur
     * infini ( 0 = Bonus bomb, 1 = Bonus Range, 2 = FREE ). Ensuite on set la case associé de blocksNextBoard à crumbling
     * 
     * Cas de Bonus : On prépare une séquence verify qui va nous aider par la suite. Cette Sq vas prendre tous les cas ou la Cellule blastée comprend un Block.FREE. 
     * Ensuite si elle n'en contient aucun on créer une autre séquence qui sera de longueur BONUS_DISAPPEARING_BLOCK et contiendra le bonus de la cellule c. 
     * On set la cellule associé de blocksNextBoard à cette séquence. 
     * 
     * l'avantage de la Sq verify c'est quelle nous permet de manière efficace de tester si le bonus à déja été touché par une explosion. En effet s'il 
     * a été touché par une explosion il contiendra des blocks.FREE dans sa séquence. Sinon aucun. Ainsi si la séquence verify est vide ( il n'y a pas de block.FREE) 
     * on sait que le block n'a pas été touché par une explosion.
     * 
     * 
     * @param board0 le board à l'état actuel
     * @param consumedBonus les bonus consommés par des joueur
     * @param blastedCells1 les cellules touché par une particul d'explosion
     * @return le nouveau board pour le tick suivant a partir de la list blocksNextBoard
     */
    private static Board nextBoard(Board board0, Set<Cell> consumedBonus, Set<Cell> blastedCells1){
        List<Sq<Block>> blocksNextBoard = new ArrayList<Sq<Block>>();
        for(Cell c : Cell.ROW_MAJOR_ORDER){
            blocksNextBoard.add(board0.blocksAt(c).tail());
        }
        
        for(Cell c : consumedBonus){
            Sq<Block> free = Sq.constant(Block.FREE);
            blocksNextBoard.set(c.rowMajorIndex(), free);
        }
        
        for(Cell c : blastedCells1){
            
            if(board0.blockAt(c).equals(Block.DESTRUCTIBLE_WALL)){
                Sq<Block> crumbling = Sq.repeat(Ticks.WALL_CRUMBLING_TICKS, Block.CRUMBLING_WALL);
                
                int rand = RANDOM.nextInt(3);
                if(rand == 0){
                  
                  crumbling=crumbling.concat(Sq.constant(Block.BONUS_BOMB)); 
                }else if(rand == 1){
                    
                    crumbling=crumbling.concat(Sq.constant(Block.BONUS_RANGE));
                }else if (rand == 2){
                  
                  crumbling=crumbling.concat(Sq.constant(Block.FREE));
                }
                
                
                blocksNextBoard.set(c.rowMajorIndex(), crumbling);
                
                
            }else if(board0.blockAt(c).equals(Block.BONUS_BOMB)||board0.blockAt(c).equals(Block.BONUS_RANGE)){
                

                Sq<Block> verify = board0.blocksAt(c).takeWhile(p->p.isFree());
                //TODO : verifier si cela fonctionne comme voulu
                if(verify.isEmpty()){
                    Sq<Block> bonusDisapearing = Sq.repeat(Ticks.BONUS_DISAPPEARING_TICKS, board0.blockAt(c));
                    bonusDisapearing = bonusDisapearing.concat(Sq.constant(Block.FREE));
                    blocksNextBoard.set(c.rowMajorIndex(), bonusDisapearing);
                    
                    
                }
            }
        }
        
        return new Board(blocksNextBoard); 
        
    }
    
    /**
     * Cette méthode gère les futures explosions
     * Assez simplement les explosions se contente de vieillir. 
     * C'est pourquoi on prend la queue de chaque explosion de explosions0 qu'on ajoute a la future explosions ( explosions1) 
     * Et on retroune la liste explosions ainsi obtenue 
     * @param explosions0 les explosions actuelles du GameState
     * @return les explosions future explosions1
     */
    private static List<Sq<Sq<Cell>>> nextExplosions(List<Sq<Sq<Cell>>>explosions0){
        List<Sq<Sq<Cell>>> explosions1 = new ArrayList<Sq<Sq<Cell>>>();
        
        for(Sq<Sq<Cell>> explosion : explosions0){
            if(!explosion.isEmpty()){
                explosions1.add(explosion.tail());
            }
        }
        
        return explosions1;
        
    }
    /**
     * Cette méthode retourne une liste de bombe comprennant les bombes qui ont été posées. 
     * Pour ce faire on initialise une liste vide de bomb ( bombs 1 ) 
     * Ensuite en prennant les players dans l'ordre de priorité players0 on vérifie s'ils sont dans l'ensemble bombDropEvents et si'il sont en vie. 
     * Si les deux dernier sont vrais en meme temps alors on ajoute la bombe en vérifiant que il n'y ai pas de bombe déja à cette position. 
     * 
     * @param players0 liste des joueur dans leur ordre de priorité
     * @param bombDropEvents ensembled des PlayerID qui veulent poser une bombe
     * @param bombs0 liste des bombes actuelles. 
     * @return ArrayList<Bomb> des bombes posées par les joueurs. 
     */
    private static List<Bomb> newlyDroppedBombs(List<Player> players0, Set<PlayerID> bombDropEvents, List<Bomb>bombs0){
        List<Bomb> bombs1 = new ArrayList<Bomb>();
        
        for(Player p : players0){
            if(bombDropEvents.contains(p.id())&& p.isAlive()){
                Bomb b = p.newBomb();
                for(Bomb bDrop : bombs0){
                    if(bDrop.position() != b.position()){
                        bombs1.add(b);
                    }
                }
            }
        }
        
        
        return bombs1;
    }
    /**
     * TODO étape 6
     * @param players0
     * @param playerBonuses
     * @param bombedCells1
     * @param board1
     * @param blastedCells1
     * @param speedChangeEvents
     * @return
     */
    private static List<Player> nextPlayers(List<Player> players0, Map<PlayerID, Bonus> playerBonuses, Set<Cell> bombedCells1, Board board1, Set<Cell> blastedCells1, Map<PlayerID, Optional<Direction>> speedChangeEvents){
        return players0;
    }
    
    
    
    /**
     * cette méthode calcule une liste de priorité pour les joueurs. 
     * On prend le premier de la liste playerPriority
     * ensuite on fait une rotation dans playerPriority l'ordre FIFO ( le premier élément est mis a la fin ) 
     * @return l'ordre de priorité actuel des players
     */
    private List<Player> priorityPlayer(){
        List<Player> priorities = new ArrayList<Player>(playerPriority.get(0));
        
        playerPriority.add(playerPriority.get(0));
        playerPriority.remove(0);
        
        return priorities;
        
    }
    /**
     * Cette méthode ( utilise une fois ) 
     * calcule les différentes permutations possibles parmis les joueurs
     * @return une list de list de permutations de joueur
     */
    private List<List<Player>> playerPrioritiesList() {
        List<Player> playersPerm = new ArrayList<Player>(this.players);
        
        return Lists.permutations(playersPerm);
    }
        
}
