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
import ch.epfl.xblast.SubCell;
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
    private final int ticks;
    private final Board board;
    private final List<Player> players;
    private final List<Bomb> bombs;
    private final List<Sq<Sq<Cell>>> explosions;
    private final List<Sq<Cell>> blasts;
    private static final List<List<PlayerID>> playerPriority = playerPrioritiesList();
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
        if(players.size()!= PlayerID.values().length){
            throw new IllegalArgumentException();
        }
        this.ticks = ArgumentChecker.requireNonNegative(ticks);
        this.board = Objects.requireNonNull(board); 
        
        Objects.requireNonNull(players);
        Objects.requireNonNull(bombs);
        Objects.requireNonNull(explosions);
        Objects.requireNonNull(blasts);
        
        this.players = Collections.unmodifiableList(new ArrayList<Player>(players));
        this.bombs = Collections.unmodifiableList(new ArrayList<Bomb>(bombs));
        this.explosions = Collections.unmodifiableList(new ArrayList<Sq<Sq<Cell>>>(explosions));
        this.blasts = Collections.unmodifiableList(new ArrayList<Sq<Cell>>(blasts));

       
        
    }
    /**
     * Construit un état de jeu avec comme paramètre de base imposé : 
     * ticks = 0 et des liste de bombs, explosions et blast vide 
     * --> utilisé pour une nouvelle partie principalement
     * @param board le board du gameState
     * @param players la liste des joueurs qui vont participer a la partie
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
        return ticks>Ticks.TOTAL_TICKS || alivePlayers().size()<=1; 
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
        return Collections.unmodifiableList(players);
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
        return Collections.unmodifiableList(alivePlayers);
    }
    /**
     * 1ère partie : 
     * On prend les blasts actuels ( blasts0) et on ajoute la queue de chaque blast si le block ou la particule est est libre. ( ainsi on vérifie
     * que la particule ne traverse pas les murs ) 
     * 2ème partie : on prend la tête de chaque explosions qui représente en fait une particule. 
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
        
        return Collections.unmodifiableList(nextBlasts);
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
        return Collections.unmodifiableMap(bombedCells);
    }
    /**
     * 
     * @return un ensemble de toute les cellules touchées par des particules d'explosions au coup d'horloge actuel
     */
    public Set<Cell> blastedCells(){
        return GameState.blastedCells(this.blasts);
    }
    /**
     * Cette méthode privée permet de calculer les cellules blasté a partir d'une liste de Sq<Cell> blasté. 
     * @param blasts d'un état de jeu 
     * @return un ensemble de cellule qui seront blaster durant cet état
     */
    private static Set<Cell> blastedCells(List<Sq<Cell>> blasts){
        Set<Cell> blastedCells = new HashSet<Cell>();
        for(Sq<Cell> blast : blasts){
            if(!blast.isEmpty()){
                blastedCells.add(blast.head());
            }
        }
        return Collections.unmodifiableSet(blastedCells);
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
     * 8) on incrémente les ticks de 1
     * 9) on créer un nouveau GameState avec les nouveau paramètres
     * @param speedChangeEvents une Map contenant l'ID des joueur souhaitant changer de direction et leur future direction désirée
     * @param bombDropEvents un set contenant l'ID des joueurs désirant posé une bombe
     * @return le nouvel état de jeu
     */
    public GameState next(Map<PlayerID, Optional<Direction>> speedChangeEvents, Set<PlayerID> bombDropEvents ){
    
    
        
    List<Player> priority = playerPriority();
    Set<Cell> consumedBonuses = new HashSet<Cell>();
    Map<PlayerID, Bonus> playerBonuses = new HashMap<PlayerID, Bonus>();
    
    List<Sq<Cell>> blasts1 = nextBlasts(blasts, board, explosions);
    
      
    for(Player p : priority){
        if(board.blockAt(p.position().containingCell()).isBonus()){
            if(!consumedBonuses.contains(p.position().containingCell())){
            consumedBonuses.add(p.position().containingCell());
            playerBonuses.put(p.id(),board.blockAt(p.position().containingCell()).associatedBonus());
            }
        }
    }
    
    Set<Cell> blastedCells1 = GameState.blastedCells(blasts1);
    Board board1 = nextBoard(this.board, consumedBonuses, blastedCells1);
    List<Sq<Sq<Cell>>> explosions1Provisoire = nextExplosions(explosions);
     
    
    List<Bomb> bombs1Provisoire = new ArrayList<Bomb>(bombs);
    List<Bomb> bombs1 = new ArrayList<Bomb>();
    
    bombs1Provisoire.addAll(newlyDroppedBombs(priority, bombDropEvents, bombs));
    
    for(Bomb b : bombs1Provisoire){
        if(blastedCells1.contains(b.position())||b.fuseLengths().tail().isEmpty()){
            explosions1Provisoire.addAll(b.explosion());
            
        }else{
           bombs1.add(new Bomb(b.ownerId(), b.position(), b.fuseLengths().tail(), b.range()));
           
        }
    }
    
    
    
    List<Sq<Sq<Cell>>> explosions1 = Collections.unmodifiableList(explosions1Provisoire);

    
    
    bombs1 = Collections.unmodifiableList(bombs1);
    
    List<Player> players1 = nextPlayers(this.players, playerBonuses, GameState.bombedCells(bombs1).keySet(), board1, blastedCells1, speedChangeEvents);
    int ticks1 = ticks+1;
    return new GameState(ticks1, board1, players1, bombs1, explosions1, blasts1);
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

            if (blastedCells1.contains(c)){
                
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
                    
                    
                    blocksNextBoard.add(crumbling);
                    
                    
                }else if(board0.blockAt(c).isBonus()){
                    

                    Sq<Block> verify = board0.blocksAt(c);
                    for(int i = 0 ; i<=Ticks.BONUS_DISAPPEARING_TICKS; i++){
                        verify = verify.tail();
                    }
                    // 
                    if(!(verify.head() == Block.FREE)){
                        Sq<Block> bonusDisapearing = Sq.repeat(Ticks.BONUS_DISAPPEARING_TICKS, board0.blockAt(c));
                        bonusDisapearing = bonusDisapearing.concat(Sq.constant(Block.FREE));
                        blocksNextBoard.add(bonusDisapearing);
                        
                        
                    }else{
                        blocksNextBoard.add(board0.blocksAt(c).tail());
                    }
                    
                }else{
                    
                    blocksNextBoard.add(board0.blocksAt(c).tail());
                }
                
                
            }else if (consumedBonus.contains(c)) {

                blocksNextBoard.add(Sq.constant(Block.FREE));

            }else{
                
                blocksNextBoard.add(board0.blocksAt(c).tail());
            
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
        
        for (Player p : players0) {
            if (bombDropEvents.contains(p.id()) && p.isAlive() && !reachedMax(bombs0, p)) {
                boolean canPut = true;
                for (Bomb b : bombs0) {
                    if (b.position().equals(p.position().containingCell())) {
                        canPut = false;
                    }

                }
                for(Bomb b : bombs1){
                    if(b.position().equals(p.position().containingCell())){
                        canPut = false;
                    }
                }
                if (canPut) {
                    bombs1.add(p.newBomb());
                }
            }
        }
        return Collections.unmodifiableList(bombs1);
        
        
        
        
    }
    /**
     * Cette méthode calcule si un joueur a atteint le nombre max de bombes qu'il a le droit de poser. 
     * On compte toutes les bombes de bombs0 et on a un compteur qu'on incrémente d'une unité pour chaque bombe. 
     * Si a la fin le compteur == maxBombs du joueur -> on retourne true else false
     * 
     * @param bombs0 les bombes posées actuellement sur le plateau
     * @param p le joueur en question
     * @return true -> il a atteint son max, false sinon
     */
    private static boolean reachedMax(List<Bomb>bombs0, Player p){
        
        
        int count = 0;
        for(Bomb b : bombs0){
            if(b.ownerId()==p.id()){
                count++;
            }
        }
        return count==p.maxBombs();
    }
    /**
     * Cette méthode s'occupe du développement des joueurs d'un tick à l'autre. 
     * Pour simplifier la tâche on a décomposer l'évolution des joueurs en plusieurs étape : 
     * 1) la prochaine position dirigée ( en fonction du speedChangeEvents et avec la méhode nextPlayerDirectedPos();)
     * 2) vérifier si il a le droit ou non d'avancer ( si oui on prend la tail de la pos.dirigée désirée si non elle reste inchangée)
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
            
                Sq<DirectedPosition> directedPositions1 = nextDirectedPositions(p,speedChangeEvents);
                
                if(canMoveForward(p, directedPositions1, board1, bombedCells1)){
                    directedPositions1 = directedPositions1.tail();
                }
            
                Sq<LifeState> lifestate1 = nextPlayerLifeState(p, blastedCells1, directedPositions1);
            
                Player player1 = new Player(p.id(), lifestate1, directedPositions1, p.maxBombs(), p.bombRange());
            
            if(playerBonuses.containsKey(p.id())){
                    players1.add(playerBonuses.get(p.id()).applyTo(player1));
                }else{
                    players1.add(player1);
                }
            
        }
        return Collections.unmodifiableList(players1);
            
    }
            
    /**
     * Cette méthode va calculer le prochain directedPositions du joueur. 
     * 
     * On commence par vérifier si la clé id du joueur est dans la Map des speedChangeEvent si non -> on a le cas trivial ou il n'y a pas de changement et 
     * on return simplement la directedPositions actuelle du joueur. 
     * Si oui : 
     * 
     * Cas 1) C'est un optional empty : représente le souhait de s'arrêter. Cependant le joueur ne peut pas s'arrêter à sa guise partout. Il doit s'arrêter sur une sous case centrale. 
     * C'est pourquoi on commence a chercher la premiere sous case centrale sur la trajectoire du joueur à l'aide de findFirst. 
     * Ensuite on prend une Sq<DirectedPosition> jusqu'à cette sous case centrale. et on retourne une concaténation de la Sq<DirectedPosition> et de la séquence obtenue par la méthode static DirecetedPosition.stopped en lui passant en argument la directedPosition ou il s'arrête. 
     * 
     * Cas 2) C'est un optional parralèle a la direction actuelle. Si la direction est identique à la direction actuelle du joueur -> On crée une nouvelle directedPosition avec la position actuelle du joueur et la nouvelle direction.
     * et on return une nouvelle Sq<directedPosition>a partir de cette dernière directedPosition et la méthode DirectedPosition.moving
     *
     * Cas 3) C'est un optional non parralèle à la direction actuelle du joueur. Dans ce cas, on prends la Sq<DirectedPosition> jusqu'à la 1ère sous case centrale ( a l'aide de la méthode takeWhile) 
     * Ensuite on cherche la DirectedPosition contennant cette sous-case centrale ( avec la méthode findFirst). 
     * On créer une 2ème Sq<DirectedPosition> qui représente le déplacement depuis cette sous case centrale. en déplacement avec la nouvelle direction ( on le ais a laide de la méthode DirectedPosition.moving()) 
     * Et on finit par return la concaténation de la 1ère Sq ( jusqu'à la sous-case centrale ) et la 2ème ( depuis la sous-case centrale) 
     * 
     * @param player le joueur qui se déplace
     * @param speedChangeEvents une map contennant les ID des players qui ont un changement de direction et le changement en question. 
     * @return la nouvelle Sq<DirectedPosition> du joueur
     */
    private static Sq<DirectedPosition> nextDirectedPositions(Player player, Map<PlayerID, Optional<Direction>> speedChangeEvents){
        
        if(speedChangeEvents.containsKey(player.id())){
            
        if(!speedChangeEvents.get(player.id()).isPresent()){
            DirectedPosition centralDirPos = player.directedPositions().findFirst(p->p.position().isCentral());
            Sq<DirectedPosition> untilCentralCell = player.directedPositions().takeWhile(p->!p.position().isCentral());
            return untilCentralCell.concat(DirectedPosition.stopped(centralDirPos));
        }
            
        Direction intendedDir = speedChangeEvents.get(player.id()).get();
        if(intendedDir.isParallelTo(player.direction())){
            DirectedPosition placeToGo = new DirectedPosition(player.position(), intendedDir);
            return DirectedPosition.moving(placeToGo);
        }
        
        Sq<DirectedPosition> untilCentralCell = player.directedPositions().takeWhile(p -> !p.position().isCentral());
        DirectedPosition dirPosafterCentralCell = player.directedPositions().findFirst(p->p.position().isCentral()); 
        Sq<DirectedPosition> afterCentralCell = DirectedPosition.moving(dirPosafterCentralCell.withDirection(intendedDir));
        return untilCentralCell.concat(afterCentralCell);
        }
        return player.directedPositions();
            
    }
    
    /**
     * Méthode pour déterminer si le joueur peut avancer ou non. On commence par vérifier si son lifeState lui permet pas d'avancer -> true return false, sinon : 
     * - Si la prochaine cellule que le joueur vas atteindre peut être atteinte : on vérifie si sa cellule actuelle contient une bombe : Si true : 
     *          -> True : on se référe a la méthode canMoveForwardCaseBomb(en ajoutant comme paramètre false pour le paramètre blockedByWall), False : return true;
     * -> Si la prochaine cellule n'est pas atteignable , on vérifie si la case actuelle est contenue dans le set bombedCells : 
     *          -> True : on se référe a la méthode canMoveForwardCaseBomb(en ajoutant comme paramètre true pour le paramètre blockedByWall),
     *          Sinon : si la position du joueur est central on retourne false, sinon true 
     * @param player le joueur pour lequel on décide si il peut avancer ou non
     * @param directedPositions1 la prochaine Sq de position dirigée du joueur 
     * @param board1 le board du prochain tick
     * @param bombedCells1 les cellules contennant des bombes au prochain tick
     * @return true : il peut avancer, false : il ne peut pas avancer
     */
    private static boolean canMoveForward(Player player, Sq<DirectedPosition> directedPositions1, Board board1, Set<Cell> bombedCells1){
        DirectedPosition firstCentralPosition = directedPositions1.findFirst(p-> p.position().isCentral());
        Cell actualCell = player.position().containingCell();
        Cell nextCell = player.position().containingCell().neighbor(firstCentralPosition.direction());
        
        if(!player.lifeState().canMove()){
            return false;
        }
        
        if(board1.blockAt(nextCell).canHostPlayer()){
            return bombedCells1.contains(actualCell) ? canMoveForwardCaseBomb(player, firstCentralPosition, actualCell, false) : true;
        }
        
        if(bombedCells1.contains(actualCell)){
            return canMoveForwardCaseBomb(player, firstCentralPosition, actualCell, true);
        }
        return !player.position().isCentral();
        
    
    }
    
    /**
     * Cette méthode détermine si le joueur peut avancer dans le cas ou il est sur une case qui contient aussi une bombe. 
     * Pour commencer on teste si il se dirige vers la bombe ( si la prochaine subCell centrale est identique a la subCell centrale de la bombe ) 
     * Si true : 
     * -On vérifie si la distance du joueur et de cette SubCell centrale == 6 -> si true return false;
     * -Ensuite on vérifie si la position du joueur est centrale -> si true return l'inverse de blockedByWall ( si il est bloqué par un mur il n'avance pas, si il n'est pas bloqué par un mur il peut avancer )
     * -Si il ne satisfait pas les deux dernière conditions il est libre d'avancer -> return true;
     * Si false : 
     * -retourne l'inverse de blockedByWall 
     * @param player le joueur pour qui on détermine si il peut avancer
     * @param nextCentralPos la prochaine position dirigée centrale du joueur. 
     * @param bombedCell la cellule ou il y a la bombe ( aussi la cellule ou est le joueur)
     * @param blockedByWall boolean si le joueur est bloqué dans son prochain déplacement ou non
     * @return true si il peut avancer, false sinon 
     */
     private static boolean canMoveForwardCaseBomb(Player player, DirectedPosition nextCentralPos, Cell bombedCell, boolean blockedByWall){
         SubCell centralBombedCell = SubCell.centralSubCellOf(bombedCell);

         
         if(nextCentralPos.position().equals(centralBombedCell)){
             //Se dirige vers la bombedCell
             if(player.position().distanceToCentral() == 6){
                 //se trouve a 6 unité de la bombe -> bloqué par la bombe
                 return false;
             }
             
             if(player.position().isCentral()){
                 return !blockedByWall;
             }
             
             //se trouve à l'intérieur de la bombe
             return true;
         }
         //Se dirige en dehor de la bombe
         
         return !blockedByWall;
         
     }

    /**
     * Cette méthode calcule le prochain lifeStates du player. 
     * Si il est sur une case qui est touché par un blast (au prochain coup d'horloge ) et il est vulnerable -> On return le statesForNextLife du player. 
     * Sinon on return la tail de ses lifeStates actuels        
     * @param player le joueur concerné
     * @param blastedCells1 les cells touché par une explosion
     * @param directedPos1 la prochaine directedPosition du joueur au prochain coup. 
     * @return la future lifeStates du joueur 
     */
    private static Sq<LifeState> nextPlayerLifeState(Player player, Set<Cell> blastedCells1, Sq<DirectedPosition> directedPos1){
        for(Cell c : blastedCells1){
            Cell centralCell = directedPos1.head().position().containingCell();
            if(c.equals(centralCell)&&player.lifeState().state().equals(LifeState.State.VULNERABLE)){
                return player.statesForNextLife();
            }
        }
        return player.lifeStates().tail();
            
    }
   

    /**
     * Cette méthode 
     * calcule les différentes permutations possibles parmis les ID des joueurs. Pour cela on utilise la méthode permutations de Lists.
     * 
     * @return une list de list de permutations de joueur
     */
    private static List<List<PlayerID>> playerPrioritiesList() {
        List<PlayerID> playersPerm = new ArrayList<PlayerID>();
        playersPerm.add(PlayerID.PLAYER_1);playersPerm.add(PlayerID.PLAYER_2);playersPerm.add(PlayerID.PLAYER_3);playersPerm.add(PlayerID.PLAYER_4);
        
        return (Lists.permutations(playersPerm));
    }
    /**
     * Pour commencer on choisis de quelle liste de playerPriority serait prioritaire ce tick. Une manière facile est ticks mod playerPriority.size -> Ceci 
     * assure que a chaque tick une liste différente sera choisi et il y aura une répartition égale de chaque personne. 
     * Ensuite on créer un arrayList priorities qui sera l'array list final et on ajoute les joueurs du GameState dans l'ordre d'apparition de leur ID dans idPriority. 
     * 
     * @return une liste non modifiable des joueur dans leur ordre de priorité
     */
    private List<Player> playerPriority(){
        int priority = this.ticks%playerPriority.size();
        ArrayList<PlayerID> idPriority = new ArrayList<PlayerID>(playerPriority.get(priority));
        ArrayList<Player> priorities = new ArrayList<Player>();
        
        for(PlayerID id : idPriority){
            
            for(Player p : players){
                if(id.equals(p.id())){
                    priorities.add(p);
                }
            }
        }
        return Collections.unmodifiableList(priorities);
    }
        
}
