package ch.epfl.xblast.client;

import java.awt.event.KeyAdapter;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import ch.epfl.xblast.PlayerAction;

/**
 * Classe qui définit un gestionnaire en cas d'appui sur les touches. 
 * Il y a deux attributs : une table associative de nombres et de PlayerAction, et un consommateur de PlayerAction
 * @author Johan Lanzrein (257221)
 *
 */
public final class KeyboardEventHandler extends KeyAdapter
        implements KeyListener {
    
        private Map<Integer,PlayerAction> actions;
        private Consumer<PlayerAction> playerActions;
        
        public final static KeyboardEventHandler DEFAULT_KEYBOARD = defaultKeyboard();
    /**
     * Constructeur de KeyboardEventHandler    
     * @param actions la table du KeyboardEventHandler
     * @param playerAction le consommateur de playerActions
     */
    public KeyboardEventHandler(Map<Integer,PlayerAction> actions, Consumer<PlayerAction> playerAction){
        this.actions = new HashMap<Integer,PlayerAction>(actions);
        this.playerActions =playerAction;
        
    }
    
    /**
     * Redéfinition de la méthode keyPressed. 
     * On vérifie si la touche pressée est contenue dans la table actions. 
     * Si oui on l'offre au consommateur qui va accepter l'action associées a la touche appuyée
     */
    @Override
    public void keyPressed(KeyEvent e){
        int keyCode = e.getKeyCode();
        
        if(actions.containsKey(keyCode)){
           playerActions.accept(actions.get(keyCode));
        }
    }
    
    
    private static KeyboardEventHandler defaultKeyboard() {
        Map<Integer, PlayerAction> kb = new HashMap<>();
        
        kb.put(KeyEvent.VK_UP, PlayerAction.MOVE_N);
        kb.put(KeyEvent.VK_DOWN, PlayerAction.MOVE_S);
        kb.put(KeyEvent.VK_LEFT, PlayerAction.MOVE_W);
        kb.put(KeyEvent.VK_RIGHT, PlayerAction.MOVE_E);
        kb.put(KeyEvent.VK_SPACE, PlayerAction.DROP_BOMB);
        kb.put(KeyEvent.VK_SHIFT, PlayerAction.STOP);
        kb.put(KeyEvent.VK_ENTER, PlayerAction.JOIN_GAME);  
        
        Consumer<PlayerAction> playerAction = null;
                
        return new KeyboardEventHandler(kb, playerAction);
    }
    

}
