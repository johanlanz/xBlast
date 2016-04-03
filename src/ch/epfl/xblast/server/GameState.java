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
import ch.epfl.xblast.server.Player.DirectedPosition;
import ch.epfl.xblast.server.Player.LifeState;
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
        this.players = Objects.requireNonNull(Collections.unmodifiableList(new ArrayList<Player>(players)));
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
        
        return alivePlayers().size()<=1 ? true : false; 
    }
    /**
     * retourne le temps restant en seconde. p
     * @return ticks total - ticks actuel. divisé par les ticks par seconde
     */
    public double remainingTime(){
       return ((double)Ticks.TOTAL_TICKS-(double)this.ticks)/((double)Ticks.TICKS_PER_SECOND); 
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
        return GameState.bombedCells(this.bombs);
    }
    /** retourne une table associative de cell et bomb
    * Chaque Cell est associé avec la bombe ou elle est posée
    * @param bombs les bombs du prochaine état de jeu 
    * @return la table associative expliquée 
    */
    private static Map<Cell, Bomb> bombedCells(List<Bomb> bombs){
        Map<Cell, Bomb> bombedCells = new HashMap<Cell, Bomb>();
        for(Bomb b : bombs){
            bombedCells.put(b.position(), b);
        }
        return bombedCells;
    }
    /**
     * 
     * @return un ensemble de toute les cellules touchées par des particules d'explosions au prochain coup d'horloge
     */
    public Set<Cell> blastedCells(){
        return GameState.blastedCells(nextBlasts(this.blasts, board, explosions));
    }
    /**
     * Cette méthode privée permet de calculer les cellules blasté du prochain état de jeu. 
     * @param blasts du prochain état de jeu
     * @return un ensemble de cellule qui seront blaster au prochain état
     */
    private static Set<Cell> blastedCells(List<Sq<Cell>> blasts){
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
    Set<Cell> blastedCells1 = Collections.unmodifiableSet(blastedCells());
    
    List<Sq<Cell>> blasts1 = Collections.unmodifiableList(nextBlasts(this.blasts, board, explosions));
    
      
    for(Player p : priority){
        if(board.blockAt(p.position().containingCell())== Block.BONUS_BOMB||board.blockAt(p.position().containingCell())== Block.BONUS_BOMB){
            if(!consumedBonuses.contains(p.position().containingCell())){
            consumedBonuses.add(p.position().containingCell());
            playerBonuses.put(p.id(),board.blockAt(p.position().containingCell()).associatedBonus());
            }
        }
    }
    
    Board board1 = nextBoard(this.board, consumedBonuses, blastedCells());
    List<Sq<Sq<Cell>>> explosions1Provisoire = nextExplosions(explosions);
     
    
    List<Bomb> bombs1 = new ArrayList<Bomb>();
    for(Bomb b : bombs){
        if(blastedCells1.contains(b.position())||b.fuseLength() == 1){
            explosions1Provisoire.addAll(b.explosion());
            
        }else{
           bombs1.add(new Bomb(b.ownerId(), b.position(), b.fuseLengths().tail(), b.range()));
           
        }
    }
    
    List<Sq<Sq<Cell>>> explosions1 = Collections.unmodifiableList(explosions1Provisoire);

    
    bombs1.addAll(newlyDroppedBombs(priority, bombDropEvents, bombs1));
    bombs1 = Collections.unmodifiableList(bombs1);
    
    List<Player> players1 = Collections.unmodifiableList(nextPlayers(players, playerBonuses, GameState.bombedCells(bombs1).keySet(), board1, blastedCells1, speedChangeEvents));
    ticks++;
    return new GameState(ticks, board1, players1, bombs1, explosions1, blasts1);
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
                

                Sq<Block> verify = board0.blocksAt(c);
                for(int i = 0 ; i<=Ticks.BONUS_DISAPPEARING_TICKS; i++){
                    verify = verify.tail();
                }
                //TODO : verifier si cela fonctionne comme voulu -> a l'air ok 
                if(!(verify.head() == Block.FREE)){
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
                if(!bombs0.isEmpty()){
                    for(Bomb bDrop : bombs0){
                        if(bDrop.position() != b.position()){
                            bombs1.add(b);
                        }
                        
                    }
                }else{
                    bombs1.add(b);
                }
            }
        }
        
        
        return bombs1;
    }
    /**
     * Cette méthode s'occupe du développement des joueurs d'un tick à l'autre. 
     * Pour simplifier la tâche on a décomposer l'évolution des joueurs en plusieurs étape : 
     * 1) la position dirigée désirée ( en fonction du speedChangeEvents) 
     * 2) la position dirigée effective ( qui dépendra du board et des cellules contenant des bombes ) 
     * 3) leur lifeState en fonction de si il ont été touché par des blast ou pas
     * 4) si il y a des bonus sur le joueurs en fonction de si ils sont ramassés des bonus ou pas..
     * 
     * @param players0 les players avant 
     * @param playerBonuses une map des ID des joueurs et des bonus associés
     * @param bombedCells1 les cellules qui ou il y a des bombes
     * @param board1 le board durant le tick actuel
     * @param blastedCells1 les cellules touchées par des explosions. 
     * @param speedChangeEvents les changements de directions possible ou non des joueurs
     * @return une nouvelle liste de joueurs ayant évolués. 
     */
    private static List<Player> nextPlayers(List<Player> players0, Map<PlayerID, Bonus> playerBonuses, Set<Cell> bombedCells1, Board board1, Set<Cell> blastedCells1, Map<PlayerID, Optional<Direction>> speedChangeEvents){

        List<Player> players1 = new ArrayList<Player>();
        for(Player p : players0){
            
            Sq<DirectedPosition> intendedDirPosition = nextPlayerCalculatedDirectedPos(p,speedChangeEvents);
            Sq<DirectedPosition> actualDirPosition = nextPlayerEffectiveDirectedPos(intendedDirPosition, board1, bombedCells1);
            Sq<LifeState> lifestate1 = nextPlayerLifeState(p, blastedCells1, actualDirPosition);
            Player player1 = new Player(p.id(), lifestate1, actualDirPosition, p.maxBombs(), p.bombRange());
            if(playerBonuses.containsKey(p.id())){
                player1 = playerBonuses.get(p.id()).applyTo(player1);
            }
        players1.add(player1);
        }
        return players1;
            
    }
            
    /**
     * Cette méthode va calculer le chemin théorique que le joueur emprunte. 
     * On commence par vérifier si la clé id du joueur est dans la Map des speedChangeEvent si non -> on a le cas trivial ou il n'y a pas de changement et 
     * on return simplement la tail de la directedPositions actuelle du joueur. 
     * Si oui : 
     * Cas 1) C'est un optional empty : représente le souhait de s'arrêter. Cependant le joueur ne peut pas s'arrêter à sa guise partout. Il doit s'arrêter sur une sous case centrale. 
     * C'est pourquoi on commence a chercher la premiere sous case centrale sur la trajectoire du joueur à l'aide de findFirst. 
     * Ensuite on prend une Sq<DirectedPosition> jusqu'à cette sous case centrale. et on retourne une concaténation de la Sq<DirectedPosition> et de la séquence obtenue par la méthode static DirecetedPosition.stopped en lui passant en argument la directedPosition ou il s'arrête. 
     * 
     * Cas 2) C'est un optional parralèle a la direction actuelle. Si la direction est identique à la direction actuelle du joueur -> Cas trivial on renvoie la tail de la directedPositions du player. 
     * Si c'est la direction opposé ( N <-> S et E<->W ) -> On utilise la méthode static de DirectedPosition moving en lui passant comme argument, la directedPosition du player sauf que l'on modifie son attribut direction a laide de la méthode withDirection. . Ensuite on renvoie la tail de la (TODO vérifier) séquence ainsi obtenue
     * 
     * Cas 3) C'est un optional non parralèle à la direction actuelle du joueur. Dans ce cas, on prends la Sq<DirectedPosition> jusqu'à la 1ère sous case centrale ( a l'aide de la méthode takeWhile) 
     * Ensuite on cherche la DirectedPosition contennant cette sous-case centrale ( avec la méthode findFirst). 
     * On créer une 2ème Sq<DirectedPosition> qui représente le déplacement depuis cette sous case centrale. en déplacement avec la nouvelle direction ( on le ais a laide de la méthode DirectedPosition.moving()) 
     * Et on finit par return la concaténation de la 1ère Sq ( jusqu'à la sous-case centrale ) et la 2ème ( depuis la sous-case centrale) 
     * @param player le joueur qui se déplace
     * @param speedChangeEvents une map contennant les ID des players qui ont un changement de direction et le changement en question. 
     * @return la nouvelle Sq<DirectedPosition> du joueur
     */
    private static Sq<DirectedPosition> nextPlayerCalculatedDirectedPos(Player player, Map<PlayerID, Optional<Direction>> speedChangeEvents){
        
        if(speedChangeEvents.containsKey(player.id())){
            
        if(!speedChangeEvents.get(player.id()).isPresent()){
            DirectedPosition centralDirPos = player.directedPositions().findFirst(p->p.position().isCentral());
            Sq<DirectedPosition> untilCentralCell = player.directedPositions().takeWhile(p->!p.position().isCentral());
            return untilCentralCell.concat(DirectedPosition.stopped(centralDirPos));
        }
            
        Direction intendedDir = speedChangeEvents.get(player.id()).get();
        if(intendedDir.isParallelTo(player.direction())){
            if(intendedDir.equals(player.direction())){
                return player.directedPositions().tail();
            }
            return DirectedPosition.moving(player.directedPositions().head().withDirection(intendedDir));//TODO tail ou pas tail...vérifier la question sur piazza
        }
            
        Sq<DirectedPosition> untilCentralCell = DirectedPosition.moving(player.directedPositions().head()).takeWhile(p -> !p.position().isCentral());
        DirectedPosition dirPosafterCentralCell = player.directedPositions().findFirst(p->p.position().isCentral()); 
        Sq<DirectedPosition> afterCentralCell = DirectedPosition.moving(dirPosafterCentralCell.withDirection(intendedDir));
        return untilCentralCell.concat(afterCentralCell);
        }
        
        return player.directedPositions().tail();
            
    }
    /**
     * Cette méhode va calculer la Sq<DirectedPosition> représentant le déplacement effectif du joueur sur le board. 
     * On commence par trouver la prochaine case centrale qui sera atteinte par le joueur durant son déplacement. 
     * Si elle peut accueillir le joueur on vérifie si cette case contient une bombe ou non. 
     * Si elle contient une bombe : 
     * -> On prend la directedPosition ou la distance entre le joueur et la sous case central de la case contenant la bombe == 6. 
     * -> On fait une concaténation de la Sq<DirectedPosition> intendedDirPos jusqu'à cette directedPosition et une Sq<DirectedPosition> stopped ( avec la méthode de DirectedPosition) prennant pour argument la DirectedPosition ou il y a la bombe. 
     * On retourne la Sq ainsi obtenue
     * 
     * Sinon : on return le paramètre intendedDirPos inchangé car le joueur peut se déplacer sans encombre. 
     * 
     * Si elle ne peut pas accueuillir le joueur, on fait une concaténation de intendedDirPos jusqu'à la sous case centrale de la cell actuelle du joueur et une Sq<DirectedPosition> stopped prennant pour argument une DirectedPosition ayant la sous case centrale de la cell actuelle du joueur
     * 
     * @param intendedDirPos la directedPosition théorique du joueur
     * @param board0 le board à l'état actuel
     * @param bombedCells1 les cellules qui ou des bombes sont posées
     * @return la DirectedPositions effective du joueur au prochain coup. 
     */
    private static Sq<DirectedPosition> nextPlayerEffectiveDirectedPos(Sq<DirectedPosition> intendedDirPos, Board board0, Set<Cell> bombedCells1){
        DirectedPosition centralDirPos = intendedDirPos.findFirst(p->p.position().isCentral());
        Cell nextCentralCell = centralDirPos.position().containingCell().neighbor(centralDirPos.direction());
        
        if(board0.blockAt(nextCentralCell).canHostPlayer()){
           if(bombedCells1.contains(nextCentralCell)){
               DirectedPosition bombBlocking = intendedDirPos.findFirst(p-> p.position().distanceToCentral()==6);
               Sq<DirectedPosition> untilStopped = intendedDirPos.takeWhile(p -> p.position().distanceToCentral()==6);
               return untilStopped.concat(DirectedPosition.stopped(bombBlocking));//TODO : si le joueur veut ensuite aller a gauche ou a droite..considérer le cas ou le joueur a posé la bombe ? -> semble ok 
               
           }
           // cas si la case est libre et il n'y a pas de bombe -> on avance sans problème (trivial)
           return intendedDirPos;
        }
        
        //cas ou il fonce contre un mur sans pouvoir tourner pour éviter l'obstacle
        Sq<DirectedPosition> untilStopped = intendedDirPos.takeWhile(p->!p.position().isCentral());
        return untilStopped.concat(DirectedPosition.stopped(centralDirPos)); 
        
        
            
    }
    /**
     * Cette méthode calcule le prochain lifeStates du player. 
     * Si il est sur une case qui est touché par un blast (au prochain coup d'horloge ) -> On return le statesForNextLife du player. 
     * Sinon on return la tail de ses lifeStates actuels        
     * @param player le joueur concerné
     * @param blastedCells1 les cells touché par une explosion
     * @param directedPos1 la prochaine directedPosition du joueur au prochain coup. 
     * @return la future lifeStates du joueur 
     */
    private static Sq<LifeState> nextPlayerLifeState(Player player, Set<Cell> blastedCells1, Sq<DirectedPosition> directedPos1){
        for(Cell c : blastedCells1){
            Cell centralCell = directedPos1.head().position().containingCell();
            if(c.equals(centralCell)){
                return player.statesForNextLife();
            }
        }
        return player.lifeStates().tail();
            
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
