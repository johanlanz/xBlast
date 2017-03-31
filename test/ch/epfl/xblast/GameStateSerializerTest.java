package ch.epfl.xblast;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ch.epfl.xblast.server.GameStateSerializer;
import ch.epfl.xblast.server.Level;

public class GameStateSerializerTest{

    @Test
    public void serializesInitialGSTest(){
        Level level = Level.DEFAULT_LEVEL;
        
        List<Byte> serialized = GameStateSerializer.serialize(level.getBoardPainter(), level.getGameState());
        System.out.println(serialized.toString());
        
        List<Integer> expectedProvisoire = Arrays.asList(121, -50, 2, 1, -2, 0,
                3, 1, 3, 1, -2, 0, 1, 1, 3, 1, 3, 1, 3, 1, 1, -2, 0, 1, 3, 1, 3,
                -2, 0, -1, 1, 3, 1, 3, 1, 3, 1, 1, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3,
                2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2,
                3, 2, 3, 2, 3, 1, 0, 0, 3, 1, 3, 1, 0, 0, 1, 1, 3, 1, 1, 0, 0,
                1, 3, 1, 3, 0, 0, -1, 1, 3, 1, 1, -5, 2, 3, 2, 3, -5, 2, 3, 2,
                3, 1, -2, 0, 3, -2, 0, 1, 3, 2, 1, 2,

                4, -128, 16, -63, 16,

                3, 24, 24, 6, 3, -40, 24, 26, 3, -40, -72, 46, 3, 24, -72, 66,
                60);
        
        List<Byte> expected = new ArrayList<Byte>();
        for(int i : expectedProvisoire){
            expected.add((byte)i);
        }
        
        
        assertEquals(expected, serialized);
    }
}
