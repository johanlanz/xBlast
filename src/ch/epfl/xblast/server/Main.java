package ch.epfl.xblast.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerAction;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.server.painting.BoardPainter;

public final class Main {

    public static void main(String[] args) {
        int players = 4;
        if(args.length != 0){
        Integer i = Integer.parseInt(args[0]);
        players = i;
        }
        System.out.println(players);
        
        Map<PlayerID, SocketAddress> playerChannel = new HashMap<PlayerID, SocketAddress>();
        int index = 0;
        try {
        DatagramChannel channel;
        channel = DatagramChannel.open(StandardProtocolFamily.INET);
        channel.bind(new InetSocketAddress(2016));
        
        while (playerChannel.size() < players) {
                ByteBuffer buffer = ByteBuffer.allocate(100);//TODO comment choisir le nombre ? 
                SocketAddress senderAddress = channel.receive(buffer);
                for (byte b : buffer.array()) {
                    if (b == 0 && !playerChannel.containsValue(senderAddress)) {
                        playerChannel.put(PlayerID.values()[index],
                                senderAddress);
                        index++;
                        System.out.println("got player : "+index+" address : " +senderAddress.toString());
                    }
                }

            } 
            channel.close();
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        GameState game = Level.DEFAULT_LEVEL.getGameState();
        System.out.println("Got all players needed");
        final long time0 = System.nanoTime();   

        try{
        
        DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);
        
        BoardPainter painter = Level.DEFAULT_LEVEL.getBoardPainter();
        
            while (!game.isGameOver()) {
                // SENDING PART :
                List<Byte> serialized = GameStateSerializer.serialize(painter,
                        game);
                ByteBuffer bufferToSend = ByteBuffer
                        .allocate(serialized.size());

                for (byte b : serialized) {
                    bufferToSend.put(b);
                }
                bufferToSend.flip();

                for (SocketAddress client : playerChannel.values()) {
                    System.out.println("Sending gs to : "+client.toString());
                    channel.send(bufferToSend, client);
                }
                bufferToSend.clear();
                
                
                //SLEEPING PART . 
                final long time1Actual = System.nanoTime();
                final long time1Theory = time0 + game.ticks()* Ticks.TICK_NANOSECOND_DURATION;
                channel.configureBlocking(false);

                System.out.println(" T0 : "+ time0 + "\n T1 theory :  "+time1Theory+"\n T1 actual : "+time1Actual);
                if(time1Actual < time1Theory){
                    System.out.println("Sleeping for : "+(time1Theory-time1Actual));
                    Thread.sleep(200/*time1Theory- time1Actual*/);
                }
                System.out.println("Got out of sleep");
                
                // RECEIVING PART :
                Map<PlayerID, Optional<Direction>> speedChangeEvents = new HashMap<PlayerID, Optional<Direction>>();
                Set<PlayerID> bombDropEvents = new HashSet<PlayerID>();
                
                //TODO : code receiving part 
                do{
                    channel.configureBlocking(false);
                    channel.receive(bufferToSend);
                    for (byte b : bufferToSend.array()) {
                        
                    }
                }while(channel.receive(bufferToSend)!=null);
                System.out.println("Got out of receiving"); 
                
                // NEXT GAMESTATE PART : 
                game = game.next(speedChangeEvents, bombDropEvents);
                System.out.println("Ticks : " +game.ticks());
                

            }
            
            channel.close();
            if (!game.winner().isPresent()) {
                System.out.println(game.winner());
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
