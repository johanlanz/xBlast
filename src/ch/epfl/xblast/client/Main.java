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
import ch.epfl.xblast.Time;
/**
 * TODO comment
 * @author Johan Lanzrein (257221)
 *
 */
public final class Main {
    
    static String hostName = "localhost";
    static XblastComponent comp = new XblastComponent();
    public static void main(String[] args) {
        try {
            
            DatagramChannel channel;
            channel = DatagramChannel.open(StandardProtocolFamily.INET);
            
            if (args.length != 0) {
                hostName = args[0];
                channel.bind(new InetSocketAddress(2017));
            }else{
                channel.bind(new InetSocketAddress(2017));

            }
            

            System.out.println(hostName);
            
            
            InetSocketAddress address = new InetSocketAddress(hostName, 2016);
            
            ByteBuffer bufferSend = ByteBuffer.allocate(1);
            ByteBuffer bufferReceive = ByteBuffer.allocate(190);
            //TODO Demander si juste
            channel.configureBlocking(false);
            
            bufferSend.put((byte)1);//(byte)PlayerAction.JOIN_GAME.ordinal());TODO
            bufferSend.flip();
            while(channel.receive(bufferReceive)==null) {
                //Sending intention to join
                
                System.out.println(bufferSend.hasRemaining()+ " : " +bufferSend.get(0));
                
                channel.send(bufferSend, address);
                bufferSend.clear();
                Thread.sleep(Time.MS_PER_S);
            }
            
            bufferSend.clear();
            channel.configureBlocking(true);
            
            PlayerID id = PlayerID.values()[bufferReceive.get()];
            int limit = Byte.toUnsignedInt(bufferReceive.get(1));
            
            System.out.println(id);
            List<Byte> serialized = new ArrayList<Byte>();
            for(int i = 2; i<limit+2; i++){
                serialized.add(bufferReceive.get(i));

            }
            GameState game = GameStateDeserializer.deserializeGameState(serialized);
            
            ByteBuffer bufferActions = ByteBuffer.allocate(1);
            
            Map<Integer, PlayerAction> kb = new HashMap<>();
            
            kb.put(KeyEvent.VK_UP, PlayerAction.MOVE_N);
            kb.put(KeyEvent.VK_DOWN, PlayerAction.MOVE_S);
            kb.put(KeyEvent.VK_LEFT, PlayerAction.MOVE_W);
            kb.put(KeyEvent.VK_RIGHT, PlayerAction.MOVE_E);
            kb.put(KeyEvent.VK_SPACE, PlayerAction.DROP_BOMB);
            kb.put(KeyEvent.VK_SHIFT, PlayerAction.STOP);
            kb.put(KeyEvent.VK_ENTER, PlayerAction.JOIN_GAME);
            
            Consumer<PlayerAction> c = a -> {
                
                bufferActions.put((byte)a.ordinal());
                bufferActions.flip();
                
                try {
                    channel.send(bufferActions, address);
                } catch (Exception e) {
                   
                    e.printStackTrace();
                }
                bufferActions.clear();
            };
            
            comp.addKeyListener(new KeyboardEventHandler(kb,c));
            comp.setFocusable(true);
            
            
            comp.setGameState(game, id);
            
            SwingUtilities.invokeAndWait(()->createUI());
            
            do{
                
            serialized.clear();
            
            channel.receive(bufferReceive);
            id = PlayerID.values()[bufferReceive.get(0)];
            limit = Byte.toUnsignedInt(bufferReceive.get(1));
            for(int i = 2; i<limit+2; i++){
                serialized.add(bufferReceive.get(i));
            }
            
            game = GameStateDeserializer.deserializeGameState(serialized);

            comp.setGameState(game, id);
            
            bufferReceive.clear();
            
            }while(!serialized.isEmpty());
            
            System.out.println("Game Over");

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
