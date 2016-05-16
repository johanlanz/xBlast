package ch.epfl.xblast.client;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ch.epfl.xblast.PlayerAction;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.client.GraphicInterface.XblastComponent;

public final class Main {
    
    static String hostName = "localhost";
    static XblastComponent comp = new XblastComponent();
    public static void main(String[] args) {
        try {
            
            if (args.length != 0) {
                hostName = args[0];
            }
            DatagramChannel channel;
            channel = DatagramChannel.open(StandardProtocolFamily.INET);

            channel.bind(new InetSocketAddress(2018));
            System.out.println(hostName);
            
            ByteBuffer bufferSend = ByteBuffer.allocate(1);
            InetSocketAddress address = new InetSocketAddress(hostName, 2016);
            ByteBuffer bufferReceive = ByteBuffer.allocate(1000);
            //TODO Demander si juste
            channel.configureBlocking(false);
            bufferSend.put((byte)PlayerAction.JOIN_GAME.ordinal());
            while (channel.receive(bufferReceive)==null) {
                //Sending intention to join
                channel.send(bufferSend, address);
                Thread.sleep(1000);
            }
            
            channel.configureBlocking(true);
            channel.receive(bufferReceive);
            
            List<Byte> serialized = new ArrayList<Byte>();
            for(byte b : bufferReceive.array()){
                serialized.add(b);
            }
            
            GameState game = GameStateDeserializer.deserializeGameState(serialized);
            bindingComponentToKeyBoard(bufferSend);
            
            comp.setGameState(game, PlayerID.PLAYER_1);//TODO how to choose ID
            
            SwingUtilities.invokeAndWait(()->createUI());
            
            do{
                
            for(byte b : bufferReceive.array()){
                serialized.add(b);
            }
            
            game = GameStateDeserializer.deserializeGameState(serialized);

            comp.setGameState(game, PlayerID.PLAYER_1);//TODO how to choose ID
            
            }while(channel.receive(bufferReceive)!= null);
            

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static void bindingComponentToKeyBoard(ByteBuffer buffer) {
        Map<Integer, PlayerAction> kb = new HashMap<>();
        
        kb.put(KeyEvent.VK_UP, PlayerAction.MOVE_N);
        kb.put(KeyEvent.VK_DOWN, PlayerAction.MOVE_S);
        kb.put(KeyEvent.VK_LEFT, PlayerAction.MOVE_W);
        kb.put(KeyEvent.VK_RIGHT, PlayerAction.MOVE_E);
        kb.put(KeyEvent.VK_SPACE, PlayerAction.DROP_BOMB);
        kb.put(KeyEvent.VK_SHIFT, PlayerAction.STOP);
        kb.put(KeyEvent.VK_ENTER, PlayerAction.JOIN_GAME);
        
        Consumer<PlayerAction> c = System.out::println;//a -> buffer.put((byte)a.ordinal());
        
        comp.addKeyListener(new KeyboardEventHandler(kb,c));
        comp.setFocusable(true);
        
    }

    private static void createUI(){
        JFrame frame = new JFrame("Xblast");
        frame.setSize(comp.getPreferredSize());
        frame.add(comp);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        comp.requestFocusInWindow();
        frame.pack();
        
        
        
    }
}
