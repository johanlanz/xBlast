package ch.epfl.xblast;

import java.util.List;

import org.junit.Test;

import ch.epfl.xblast.client.GameStateDeserializer;
import ch.epfl.xblast.server.GameState;
import ch.epfl.xblast.server.GameStateSerializer;
//import ch.epfl.xblast.client.GameState;
import ch.epfl.xblast.server.Level;
import ch.epfl.xblast.server.debug.RandomEventGenerator;

public final class GameStateDeserializerTest {
    @Test
    public void gameStateDeserializerOnRandomGame(){
        GameState s = Level.DEFAULT_LEVEL.getGameState();
        List<Byte> serialized = GameStateSerializer.serialize(Level.DEFAULT_LEVEL.getBoardPainter(), s);
        
        @SuppressWarnings("unused")
        ch.epfl.xblast.client.GameState deserialiezed = GameStateDeserializer.deserializeGameState(serialized); 
        RandomEventGenerator randEvents = new RandomEventGenerator(2016, 30, 100);

        while(!s.isGameOver()){
            s = s.next(randEvents.randomSpeedChangeEvents(), randEvents.randomBombDropEvents());
            
            serialized = GameStateSerializer.serialize(Level.DEFAULT_LEVEL.getBoardPainter(), s);
            GameStateDeserializer.deserializeGameState(serialized); 
        }
    }
}
