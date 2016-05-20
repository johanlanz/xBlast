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
import ch.epfl.xblast.Time;
import ch.epfl.xblast.server.painting.BoardPainter;
/**
 * TODO : comment
 * @author Johan Lanzrein (257221)
 *
 */
public final class Main {

    public static void main(String[] args) {
        int players = 4;
        if(args.length > 0 && args.length<4){
            players = Integer.parseInt(args[0]);
        }
        
        System.out.println(players);
        
        Map<SocketAddress, PlayerID> playerChannel = new HashMap<SocketAddress, PlayerID>();
        
        try {
        DatagramChannel channel;
        channel = DatagramChannel.open(StandardProtocolFamily.INET);
        channel = channel.bind(new InetSocketAddress(2016));
        boolean empty = true;
        int index = 0;
        while (playerChannel.size() < players) {
                ByteBuffer buffer = ByteBuffer.allocate(1);//TODO comment choisir le nombre ? 
                SocketAddress senderAddress = channel.receive(buffer);
                empty = (senderAddress == null);
                
                if (!empty) {
                    for (byte b : buffer.array()) {
                        System.out.println(senderAddress.toString() + " : "+ b);
                        if (b == 1 && !playerChannel.containsKey(senderAddress)) {
                            playerChannel.put(senderAddress, PlayerID.values()[index]);
                            index++;
                        }
                    }
                }
                    
                
                buffer.clear();

            }
            
            channel.close();
            
        }catch (IOException e) {
            e.printStackTrace();
        }
        
        
        GameState game = Level.DEFAULT_LEVEL.getGameState();
        System.out.println("Got all players needed");
        final long time0 = System.nanoTime();   

        try{
        
        DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);
        channel.bind(new InetSocketAddress(2016));
        
        
        BoardPainter painter = Level.DEFAULT_LEVEL.getBoardPainter();
        
            while (!game.isGameOver()) {
                // SENDING PART :
                List<Byte> serialized = GameStateSerializer.serialize(painter,
                        game);
                
                ByteBuffer buffer = ByteBuffer.allocate(serialized.size()+2);
                buffer.put((byte) -1);
                buffer.put((byte)serialized.size());
                
                for (byte b : serialized) {
                    buffer.put(b);
                }
                
                

                for (SocketAddress client : playerChannel.keySet()) {
                    //System.out.println("Sending gs to : "+client.toString());
                    buffer.put(0, (byte)playerChannel.get(client).ordinal());
                    buffer.flip();
                    channel.send(buffer, client);
                }
                buffer.clear();
                
                
                //SLEEPING PART 
                long time1Actual = System.nanoTime();
                long time1Theory = time0 + ((long)game.ticks() * (long)Ticks.TICK_NANOSECOND_DURATION);

                if(time1Actual < time1Theory){
                    long sleepingTime = time1Theory-time1Actual;
                    long sleepingTimeMillis = sleepingTime/Time.US_PER_S;
                    int sleepingTimeNanos = (int)sleepingTime%Time.US_PER_S;
                    Thread.sleep(sleepingTimeMillis, sleepingTimeNanos);
                }
                
                // RECEIVING PART :
                Map<PlayerID, Optional<Direction>> speedChangeEvents = new HashMap<PlayerID, Optional<Direction>>();
                Set<PlayerID> bombDropEvents = new HashSet<PlayerID>();
                
                
                ByteBuffer bufferReceive = ByteBuffer.allocate(4);
                channel.configureBlocking(false);
                
                boolean notEmpty = true;
                do{
                    SocketAddress address = channel.receive(bufferReceive);
                    if(address == null){
                        notEmpty = false;
                    }else{
                        notEmpty = true;
                        for (byte b : bufferReceive.array()) {

                            if (b == PlayerAction.MOVE_N.ordinal()) {
                                speedChangeEvents.put(
                                        playerChannel.get(address),
                                        Optional.of(Direction.N));
                            } else if (b == PlayerAction.MOVE_E.ordinal()) {
                                speedChangeEvents.put(
                                        playerChannel.get(address),
                                        Optional.of(Direction.E));
                            } else if (b == PlayerAction.MOVE_S.ordinal()) {
                                speedChangeEvents.put(
                                        playerChannel.get(address),
                                        Optional.of(Direction.S));
                            } else if (b == PlayerAction.MOVE_W.ordinal()) {
                                speedChangeEvents.put(
                                        playerChannel.get(address),
                                        Optional.of(Direction.W));
                            } else if (b == PlayerAction.STOP.ordinal()) {
                                speedChangeEvents.put(
                                        playerChannel.get(address),
                                        Optional.empty());
                            } else if (b == PlayerAction.DROP_BOMB.ordinal()) {
                                bombDropEvents.add(playerChannel.get(address));
                            }
                        }

                    }
                    buffer.clear();
                }while(notEmpty);
                
                // NEXT GAMESTATE PART : 
                game = game.next(speedChangeEvents, bombDropEvents);
                

            }
            
            channel.close();
            if (game.winner().isPresent()) {
                System.out.println("Le gagnant est : " +game.winner());
            }else if(game.alivePlayers().size()>1){
                System.out.println("Egalité entre : ");
                for(Player p : game.alivePlayers()){
                    System.out.print("Joueur "+(p.id().ordinal()+1)+" ");
                }
            }else{
                System.out.println("Aucun gagnant..");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
