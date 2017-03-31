package ch.epfl.xblast.server;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.junit.Test;

import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.client.GameStateDeserializer;
import ch.epfl.xblast.client.KeyboardEventHandler;
import ch.epfl.xblast.client.XblastComponent;
import ch.epfl.xblast.server.debug.RandomEventGenerator;
import ch.epfl.xblast.PlayerAction;

public final class XblastComponentTests {

    @Test
    public void initialGS(){
        
        System.out.println("1");
        
        
        
        RandomEventGenerator randEvents = new RandomEventGenerator(2016, 30, 100);  
        GameState gs = Level.DEFAULT_LEVEL.getGameState();
        XblastComponent comp = new XblastComponent();
        Map<Integer, PlayerAction> kb = new HashMap<>();
        
        kb.put(KeyEvent.VK_UP, PlayerAction.MOVE_N);
        kb.put(KeyEvent.VK_DOWN, PlayerAction.MOVE_S);
        kb.put(KeyEvent.VK_LEFT, PlayerAction.MOVE_W);
        kb.put(KeyEvent.VK_RIGHT, PlayerAction.MOVE_E);
        kb.put(KeyEvent.VK_SPACE, PlayerAction.DROP_BOMB);
        kb.put(KeyEvent.VK_SHIFT, PlayerAction.STOP);
        kb.put(KeyEvent.VK_ENTER, PlayerAction.JOIN_GAME);
        
        Consumer<PlayerAction> c = System.out::println;
        comp.addKeyListener(new KeyboardEventHandler(kb,c));
        comp.setFocusable(true);

        
        
        
        
        
        List<Byte> serialized0 = GameStateSerializer.serialize(Level.DEFAULT_LEVEL.getBoardPainter(), gs);
        
        ch.epfl.xblast.client.GameState deserialized0 = GameStateDeserializer.deserializeGameState(serialized0);
        
        comp.setGameState(deserialized0, PlayerID.PLAYER_1);
        JFrame frame = new JFrame("Xblast");
                frame.setSize(comp.getPreferredSize());
        frame.add(comp);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        comp.requestFocusInWindow();

        frame.pack();
       
        

        
        
        while(!gs.isGameOver()){    
        gs = gs.next(randEvents.randomSpeedChangeEvents(), randEvents.randomBombDropEvents());
        List<Byte> serialized = GameStateSerializer.serialize(Level.DEFAULT_LEVEL.getBoardPainter(), gs);
        
        ch.epfl.xblast.client.GameState deserialized = GameStateDeserializer.deserializeGameState(serialized);
        
       
        comp.setGameState(deserialized, PlayerID.PLAYER_1);
        
        try {
            Thread.sleep(25);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        }
        //SwingUtilities.invokeLater(()-> createUI());
        System.out.println("2");
    }
    
    private void createUI(){
        
        GameState gs = Level.DEFAULT_LEVEL.getGameState();
        List<Byte> serialized = GameStateSerializer.serialize(Level.DEFAULT_LEVEL.getBoardPainter(), gs);
        
        
        ch.epfl.xblast.client.GameState deserialized = GameStateDeserializer.deserializeGameState(serialized);
        
        
            XblastComponent comp = new XblastComponent();
            
            
            
            JFrame frame = new JFrame("Xblast");
            frame.setSize(comp.getSize());
            frame.add(comp);
            comp.setGameState(deserialized, PlayerID.PLAYER_1);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
            
            
        
    }
}
