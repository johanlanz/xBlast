package ch.epfl.xblast.client;

import java.awt.Container;
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
 * Cette classe est la classe principale qui gère une partie du côté du client. 
 * 
 * @author Johan Lanzrein (257221)
 *
 */
public final class Main {
    /**
     * Constantes pour le client comme le numéro du port ou le nom par défaut du host
     */
    private static final int PORT = 2016;
    private static final String DEFAULT_HOST = "localhost";
    /**
     * Le component qui sera dessiné
     */
    private static XblastComponent comp = new XblastComponent();
    /**
     * Méthode main qui contrôle le côte controleur de la classe. 
     * 
     * On vérifie si les args sont vide si non on modifie le hostName par les args. 
     * Ensuite on ouvre un channel que l'on lie avec le port 2017 ( afin que si le serveur et le client tourne sur la même machine il n'y ait pas de problème de port. ) 
     * On crée une nouvelle address qui est basée sur le hostName et le port 2016. 
     * 
     * Ensuite et tant que le channel est vide, on envoie le byte correspondant à la volonté de se joindre (0) au serveur une fois par seconde. 
     * 
     * Dès que le channel recoit quelque chose du serveur, on transfere cela à un bytebuffer (bufferReceive).  On prend le 1er byte qui correspond à l'id du joueur, 
     * et le deuxième qui correspond à la taille de la séquence qui suuis ( l'état serialisé.). On transfère tout les byte du buffer dans une liste de byte qui sera ensuite
     * déserializer en un état de jeu. 
     * A ce moment on peut modifier notre comp pour l'ajuster en fonction de l'état actuel et de l'id. On ajoute au comp un keyListener qui est créer avec une méthode auxiliaire. 
     * 
     * Dès ce moment on peut lancer le deuxième fil d'exécution à travers le SwingUtilities invoke later. 
     * 
     * 
     * Ensuite et tant que serialized n'est pas vide le programme va boucler en déserialisant les byte recu par le serveur et en modifiant comp en fonction du nouvel état de jeu. 
     * Dès que la partie est terminé il se bloque car le channel est mis en mode bloquant. 
     * 
     * @param args Si il y en a c'est l'addresse ip du serveur. 
     * @throws InterruptedException vient de invoke later
     * @throws InvocationTargetException vient de invoke later
     * 
     */
    public static void main(String[] args) throws InvocationTargetException, InterruptedException {
        try{
            
            DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);
            String hostName = DEFAULT_HOST;
            
            if (args.length != 0) {
                hostName = args[0];
            }
            
                        
            
            InetSocketAddress address = new InetSocketAddress(hostName, PORT);
            
            ByteBuffer bufferSend = ByteBuffer.allocate(1);
            ByteBuffer bufferReceive = ByteBuffer.allocate(410);
            channel.configureBlocking(false);
            
            bufferSend.put((byte)PlayerAction.JOIN_GAME.ordinal());
            bufferSend.flip();
            
            while(channel.receive(bufferReceive)==null) {
                //Sending intention to join
                channel.send(bufferSend, address);
                bufferSend.flip();
                Thread.sleep(Time.MS_PER_S);
            }
            
            bufferSend.clear();
            channel.configureBlocking(true);
            
            
            comp.addKeyListener(keyboardCreator(channel, address, bufferSend));
            //comp.setGameState(game, id);
            SwingUtilities.invokeAndWait(()->createUI());
            boolean empty = false;
            
            do{
                
            List<Byte> serialized = new ArrayList<Byte>();
            
            channel.receive(bufferReceive);
            
            bufferReceive.flip();

            PlayerID id = PlayerID.values()[bufferReceive.get()];
            
            while(bufferReceive.hasRemaining()){
                serialized.add(bufferReceive.get());
            }
            
            GameState game = GameStateDeserializer.deserializeGameState(serialized);

            comp.setGameState(game, id);
            
            bufferReceive.clear();
            empty = serialized.isEmpty();
            }while(!empty);
            
            System.out.println("Game Over");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Cette méthode construit un keyboardevent handler. 
     * On créer une map qui fait correspondre au touche les actions des joueurs. 
     * 
     * Un consommateur de PlayerAction qui stock les byte des ordinals des actions dans le bytebuffer 
     * passé en argument puis en les envoyant à l'addresse passée en argument par le channel passé en argument. 
     * Ensuite on clear le buffer pour pouvoir y réécrire à volonté. 
     *
     * On créer un keyboardevent handler en fonction de cette map et ce consommateur. 
     * @param channel le channel par ou on envoie les byte
     * @param address l'address ou on envoie les byte
     * @param bufferActions le buffer qui ou sont stocké les byte d'actions
     * @return le KeyBoardEventHandler ainsi créer 
     */
    private static KeyboardEventHandler keyboardCreator(DatagramChannel channel,
            InetSocketAddress address, ByteBuffer bufferActions) {
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
        
        return new KeyboardEventHandler(kb,c);
    }
    
    /**
     * Cette méthode gère le côté vue du programme. 
     * On créer un JFrame et un Container qui contiendra le comp ( de la classe Main ). 
     * on ajoute au contentPane la taille du comp et le comp. On set la comp en focusable. 
     * Ensuite on pack le frame et on le rend visible. 
     * 
     */
    private static void createUI() {

        JFrame frame = new JFrame("Xblast");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = frame.getContentPane();
        contentPane.setSize(comp.getPreferredSize());
        contentPane.add(comp);
        comp.setFocusable(true);

        frame.pack();
        frame.setVisible(true);
    }
}
