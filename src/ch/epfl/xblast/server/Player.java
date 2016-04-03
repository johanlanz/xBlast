package ch.epfl.xblast.server;

import java.util.Objects;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.ArgumentChecker;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.SubCell;
import ch.epfl.xblast.server.Player.LifeState.State;

/**
 * Classe qui définit un joueur avec plusieurs caractéristiques. 
 * De plus cette classe contient plusieurs classes imbriquées : 
 * - LifeState ( Qui contient l'Enum State ) 
 * - DirectedPosition
 * @author Johan Lanzrein (257221) 
 *
 */
public final class Player {
    private PlayerID id; 
    private Sq<LifeState> lifeStates;
    private Sq<DirectedPosition> directedPos;
    private int maxBombs; 
    private int bombRange;
    /**
     * Construit un joueur avec les caractéristiques données en paramètre. 
     * On vérifie aussi si les paramètre sont nuls ou négatifs pour les nombres. 
     * 
     * @param id
     * @param lifeStates
     * @param directedPos
     * @param maxBombs
     * @param bombRange
     */
    public Player(PlayerID id, Sq<LifeState> lifeStates, Sq<DirectedPosition> directedPos, int maxBombs, int bombRange){
        this.id = Objects.requireNonNull(id);
        this.lifeStates = Objects.requireNonNull(lifeStates);
        this.directedPos = Objects.requireNonNull(directedPos);
        if(lifeStates.isEmpty()||directedPos.isEmpty()){
            throw new IllegalArgumentException(); 
        }
        this.maxBombs = ArgumentChecker.requireNonNegative(maxBombs);
        this.bombRange = ArgumentChecker.requireNonNegative(bombRange);
        
    }
    /**
     * Constructeur secondaire de la classe Player
     * Construit aussi un player en faisant appel au constructeur principal. 
     * La différence est principalement pour le 2è et 3è arguments. 
     * Pour lifeStates: 
     * On fait une séquence qui contient d'abord des lifeStates ayant le couple ( lives , invulnerable ) pour 
     * une longueur défini par les ticks. et ensuite une séquence infini contenant le couple (lives, vulnerable)
     * 
     * Pour DirectedPosition : 
     * On fait une séquence infini ayant le couple (Subcell centrale de la position passée en paramètre, Direction S ) 
     * 
     * De plus on prend en compte le cas ou un joueur serait ajouté avec lives == 0 -> mort permanente 
     * @param id
     * @param lives
     * @param position
     * @param maxBombs
     * @param bombRange
     */
    public Player(PlayerID id, int lives, Cell position, int maxBombs, int bombRange){
        this(id,
             Sq.repeat(Ticks.PLAYER_INVULNERABLE_TICKS, new LifeState(lives, State.INVULNERABLE)).concat(Sq.constant(new LifeState(lives, State.VULNERABLE))),
             Sq.constant(new DirectedPosition(SubCell.centralSubCellOf(position), Direction.S)),
             maxBombs,
             bombRange);
        
        ArgumentChecker.requireNonNegative(lives);
        
        if(lives == 0){
            this.lifeStates = Sq.constant(new LifeState(lives, State.DEAD));
        }
        
    }
    /**
     * 
     * @return l'id du joueur
     */
    public PlayerID id(){
        return this.id; 
    }
    /**
     * 
     * @return la caractéristique lifeStates du joueur
     */
    public Sq<LifeState> lifeStates(){
        return lifeStates;
    }
    /**
     * 
     * @return le lifeState en tête de la Sq lifeStates
     */
    public LifeState lifeState(){
        return lifeStates.head();
    }
    /**
     * retourne une Sq<LifeState> au cas si un joueur perd une vie. 
     * Pour commencer la Sq a le couple ( lives, DYING ) pour une longueur Défini par les Ticks. 
     * Ensuite il y a deux cas : 
     * Si le joueur n'a plus de lives ( 0 ) 
     *  -> On retourne une concaténation de la séquence d'avant et d'une séquence constante ayant le couple ( 0, DEAD )
     * 
     * Sinon : 
     * -> On fait deux autres Sq. 
     *      - Une de longueur défini par les Ticks INVULNERABLE avec le couple (lives, INVULNERABLE) 
     *      - Une de longueur infini avec le couple (lives-1, VULNERABLE) 
     * Ensuite on "concatène" les trois séquence dans l'ordre de citation.   
     * @return la Sq ainsi construite 
     */
    public Sq<LifeState> statesForNextLife(){
        Sq<LifeState> dying = Sq.repeat(Ticks.PLAYER_DYING_TICKS, new LifeState(this.lives(), State.DYING));
        
        if(lifeStates.head().lives()  == 1){
            return dying.concat(Sq.constant(new LifeState(0, State.DEAD)));
        }
        Sq<LifeState> beginNewLife = Sq.repeat(Ticks.PLAYER_INVULNERABLE_TICKS, new LifeState(this.lives(), State.INVULNERABLE));
        Sq<LifeState> newLife = Sq.constant(new LifeState(this.lives()-1, State.VULNERABLE));
        
        return dying.concat(beginNewLife).concat(newLife);
    }
    /**
     * on prend la tête de la Sq lifeStates et on prend les lives
     * @return le nombre de lives du joueur. 
     */
    public int lives(){
        return lifeStates.head().lives();
    }
    /**
     * retourne True si lives du joueur est > 0 sinon false
     * @return 
     */
    public boolean isAlive(){
        return this.lives() > 0 ? true : false;
    }
    /**
     * 
     * @return la Sq directedPos du joueur
     */
    public Sq<DirectedPosition> directedPositions(){
        return directedPos;
    }
    /**
     * retourn la position acutelle du joueur 
     * On prend la tête de la Sq DirectedPos et sa position
     * @return
     */
    public SubCell position(){
        return this.directedPositions().head().position();
    }
    /**
     * retourne la direction actuelle du joueur
     * On prend la tête de la Sq DirectedPos et sa direction 
     * @return
     */
    public Direction direction(){
        return this.directedPositions().head().direction();
    }
    /**
     * retourne le nombre max de bomb du joueur
     * @return
     */
    public int maxBombs(){
        return maxBombs;
    }
    /**
     * construit un nouveau joueur, identique au joueur this, la seule différence est le nombre de bomb max. 
     * Il est passé en paramètre un nouveau paramètre pour le maxBombs
     * @param newMaxBombs nouveau maxBombs du joueur
     * @return nouveau joueur identique a this si ce n'est pour maxBombs
     */
    public Player withMaxBombs(int newMaxBombs){
       return new Player(this.id, this.lifeStates(), this.directedPositions(), newMaxBombs, this.bombRange); 
    }
    /**
     * 
     * @return range des bombes du joueur
     */
    public int bombRange(){
        return bombRange;
    }
    /**
     * nouveau joueur identique à this si ce n'est pour le paramètre range qui est modifié. 
     * Il prend la valeur du paramètre de la méthode ( newBombRange ) 
     * @param newBombRange nouveau range du joueur
     * @return nouveau joueur ainsi créer. 
     */
    public Player withBombRange(int newBombRange){
        return new Player(this.id, this.lifeStates(), this.directedPositions(), this.maxBombs, newBombRange); 

    }
    /**
     * place une bombe à la position du joueur, ayant l'appartenance du joueur qui la poser. 
     * Une "méche" de longueur Bomb_fuse_ticks, et la range du joueur
     * @return la bombe posée ainsi
     */
    public Bomb newBomb(){
       return new Bomb(this.id, this.position().containingCell(), Ticks.BOMB_FUSE_TICKS, this.bombRange()); 
    }
    /**
     * Classe lifeState ayant un couple : 
     * - int lives ( nombre de vie ) 
     * - State state (état du joueur )
     * @author Johan Lanzrein (257221) 
     *
     */
    public final static class LifeState{
        private int lives; 
        private State state;
        /**
         * construit un nouveau LifeState avec les valeurs passées en paramètres. 
         * On vérifie qu'elle ne soit pas nulle / négative pour les lives 
         * @param lives
         * @param state
         */
        public LifeState(int lives, State state){
            
            this.lives = ArgumentChecker.requireNonNegative(lives);
            this.state = Objects.requireNonNull(state);
        }
        /**
         * 
         * @return lives du LifeState
         */
        public int lives(){
            return lives;
        }
        /**
         * 
         * @return State du LifeState
         */
        public State state(){
            return state;
        }
        /**
         * si le joueur est Vulnerable ou Invulnerable -> true, 
         * false sinon
         * @return true/false comme expliquée avant
         */
        public boolean canMove(){
            if(state == State.VULNERABLE || state == State.INVULNERABLE){
                return true;
            }
            return false;
        }
        /**
         * Enum qui contient les différents état possible pour le joueur
         * @author Johan Lanzrein (257221) 
         *
         */
        public static enum State{
            INVULNERABLE, VULNERABLE, DYING, DEAD;
        }
    }
    /**
     * Classe DirectedPosition qui contient un couple : 
     * SubCell position ( l'endroit ou le joueur est ) 
     * Direction dir ( la direction ou le joueur se dirige ) 
     * @author Johan Lanzrein (257221) 
     *
     */
    public final static class DirectedPosition{
        private SubCell position;
        private Direction dir;
        /**
         * Construit une nouvelle position dirigée avec les valeurs passées en paramètres 
         * On vérifie bien qu'elle ne soit pas nulle ! 
         * @param position
         * @param dir
         */
        public DirectedPosition(SubCell position, Direction dir){
            this.position = Objects.requireNonNull(position); 
            this.dir = Objects.requireNonNull(dir); 
        }
        /**
         * Retourne une Sq de Directedposition 
         * dans le cas ou le joueur est stoppé. 
         * -> la séquence sera toujours identiques 
         * @param p directedPos qui sera toujours identique
         * @return Sq constante avec la valeur P 
         */
        public static Sq<DirectedPosition> stopped(DirectedPosition p){
            return Sq.constant(p);
        }
        /**
         * retourne une Sq de Directed Position construite a partir du paramètre p 
         * -> On prend le couple de base p ( position, direction) et on fait une itération ( lambda ) 
         *  La prochaine itération est la voisine de la position actuelle et la directions est la même 
         *  c-à-d le couple ( voisine, direction ) 
         * @param p directedPosition de base 
         * @return la Sq ainsi obtenue 
         */
        public static Sq<DirectedPosition> moving(DirectedPosition p){
            return Sq.iterate(p, pNext -> new DirectedPosition(pNext.position.neighbor(p.dir), p.dir));
        }
        /**
         * 
         * @return la position de DirectedPosition
         */
        public SubCell position(){
            return new SubCell(position.x(), position.y());
        }
        /**
         * on creer une nouvelle directedPos ayant la même direction que this mais une position différente ( passée en paramètre ) 
         * @param newPosition nouvelle position de la DirectedPos
         * @return DirectedPosition ainsi obtenue 
         */
        public DirectedPosition withPosition(SubCell newPosition){
            return new DirectedPosition(newPosition, this.dir);
        }
        /**
         * 
         * @return direction de this
         */
        public Direction direction(){
            return dir;
        }
        /**
         * Retourne une nouvelle directedPos a partir de this mais on modifie la Direction ( passée en paramètre ) 
         * @param newDirection nouvelle direction 
         * @return nouvelle DirectedPos ainsi obtenue 
         */
        public DirectedPosition withDirection(Direction newDirection){
            return new DirectedPosition(this.position(), newDirection);
        }
    }
    
}
