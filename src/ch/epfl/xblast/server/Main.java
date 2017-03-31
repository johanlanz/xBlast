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
 * Classe Main qui gère le coté serveur d'une partie. 
 * @author Johan Lanzrein (257221)
 *
 */
public final class Main {
    
    
    private static final int PORT = 2016;

    /**
     * On vérifie les args au début et adapte le nombre de joueur en conséquence. Ce nombre est stocké dans la variable players
     * 
     * Ensuite on crée une map associant SocketAddress au PlayerID, ouvre un channel que l'on bind au port 2016. 
     * On boucle tant que la taille de la map < players. Dans la boucle, on recoit les éventuels byte du channel que l'on transfère dans une byte buffer, et on stock aussi l'address. 
     * Si l'address n'est pas dans la map et le byte = byte pour joindre une partie. on ajoute l'address à la map en l'associant à un playerID en fonction de l'ordre arrivé ( avec un index qui 
     * permet d'aller à travers les values de PlayerID. Dès qu'on a tout les joueurs la partie peut commencer. 
     * On initialise le GameState au niveau par défaut de la classe Level. et on note le temps time0 du system.nanotime.
     * 
     * Tant que la partie n'est pas terminée on exécute les actions suivantes : 
     *      - On sérialize l'état de jeu et on met l'état de jeu dans un byte buffer ayant la taille de l'état serialisé +2. Les deux supplémentaire sont pour un byte qui indique la taille de létat sérialisé, et un byte pour l'id du joueur. 
     *      - On envoie l'état de jeu à chaque addresse en prenant soin d'insérer le byte de l'id dans le buffer auparavant. 
     *      - Si nécessaire le serveur entre dans une phase de "sleep" ou il s'arrête si il est plus rapide que le temps nécessaire pour un tick ( 20 nanosec). 
     *      - Ensuite on vérifie si le channel a recu des byte ( des actions ) de la part des clients. S'il y en as on fait correspondre chaque byte à l'action correspondante. ( On utilise un if-else if chainé car le switch n'admet pas des variables ) 
     *        Pour chaque action on met soit dans la map speedChangeEvent ou le set droppedBombs.
     *      - On calcule le prochaine état de jeu en fonction de speedChangeEvent et le set droppedBombs. 
     * Une fois la partie terminée on affiche l'ID du vainqueur à l'écran si il y en a un .  
     * @param args le nombre de joueur par défaut il est égal à 4. Si il y a des args et que il est entre 1 et 4 alors ce sera le nombre de joueurs. 
     * 
     */
    
    
    public static void main(String[] args) {
        
        int players =PlayerID.values().length;
        if(args.length>0){
            if(Integer.parseInt(args[0]) > 0 && Integer.parseInt(args[0])<4){
                players = Integer.parseInt(args[0]);
            }
        }
       
        
        Map<SocketAddress, PlayerID> playerChannel = new HashMap<SocketAddress, PlayerID>();

        try {

            DatagramChannel channel;
            channel = DatagramChannel.open(StandardProtocolFamily.INET);
            channel = channel.bind(new InetSocketAddress(PORT));
            boolean empty = true;
            int index = 0;

            while (playerChannel.size() < players) {
                ByteBuffer buffer = ByteBuffer.allocate(players);
                SocketAddress senderAddress = channel.receive(buffer);
                empty = (senderAddress == null);

                if (!empty) {
                    for (byte b : buffer.array()) {
                        if (b == (byte) PlayerAction.JOIN_GAME.ordinal()&& !playerChannel.containsKey(senderAddress)) {
                            playerChannel.put(senderAddress,PlayerID.values()[index]);
                            System.out.println(" Address : " +senderAddress.toString() +" joins the game as " +PlayerID.values()[index]);

                            index++;
                        }
                    }
                }

                buffer.clear();

            }

            channel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        GameState game = Level.DEFAULT_LEVEL.getGameState();
        final BoardPainter painter = Level.DEFAULT_LEVEL.getBoardPainter();

        final long time0 = System.nanoTime();   

        try{
        
        DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);
        channel.bind(new InetSocketAddress(2016));
        
        
        
            while (!game.isGameOver()) {
                // SERIALIZING PART :
                List<Byte> serialized = GameStateSerializer.serialize(painter,
                        game);
                
                
                //IN BUFFER PART
                
                ByteBuffer buffer = ByteBuffer.allocate(serialized.size()+1);
                buffer.put((byte) -1);
                
                for (byte b : serialized) {
                    buffer.put(b);
                }
                
                
                //SENDING PART
                for (SocketAddress client : playerChannel.keySet()) {
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
            //Game over print winner if there are any
            if (game.winner().isPresent()) {
                System.out.println("Le gagnant est : " +game.winner().toString());
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
