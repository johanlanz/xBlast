package ch.epfl.xblast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ArgumentCheckerTest {

    @Test
    public void ArgumentCheckerOnPositive(){
        for(int i = 2; i < 32 ; i++){
            assertEquals(i, ArgumentChecker.requireNonNegative(i));
        }
    }
    @Test
    public void ArgumentCheckerOnNul(){
        assertEquals(0, ArgumentChecker.requireNonNegative(0));
    }
    @Test (expected = IllegalArgumentException.class)
    public void ArgumentCheckerOnNegative(){
        for(int i = -1; i> -3; i--){
            ArgumentChecker.requireNonNegative(i);
        }
    }
}
