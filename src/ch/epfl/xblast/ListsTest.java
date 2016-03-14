package ch.epfl.xblast;
import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ListsTest {
    @Test
    public void listIsCorrectlyMirroredOddCase(){
        List<String> testing = new ArrayList<String>();
        List<String> expectedMirror = new ArrayList<String>();
        
        testing.add("sup1");
        testing.add("sup2");
        testing.add("sup3");
        
        expectedMirror.add("sup1");
        expectedMirror.add("sup2");
        expectedMirror.add("sup3");
        expectedMirror.add("sup2");
        expectedMirror.add("sup1");
        
        List<String>mirrored = Lists.mirrored(testing);
        
        assertEquals(expectedMirror, mirrored);
        
    }
    @Test 
    public void listIsCorrectlyMirroredEvenCase(){
        List<String> testing = new ArrayList<String>();
        List<String> expectedMirror = new ArrayList<String>();
        
        testing.add("sup1");
        testing.add("sup2");
        testing.add("sup3");
        testing.add("sup4");
        
        expectedMirror.add("sup1");
        expectedMirror.add("sup2");
        expectedMirror.add("sup3");
        expectedMirror.add("sup4");
        expectedMirror.add("sup3");
        expectedMirror.add("sup2");
        expectedMirror.add("sup1");
        
        List<String>mirrored = Lists.mirrored(testing);
        
        assertEquals(expectedMirror, mirrored);
    }
    
    @Test (expected = IllegalArgumentException.class)
    
    public void throwsExceptionOnEmptyArray(){
        List<Object> emptyArray = new ArrayList<Object>();
        Lists.mirrored(emptyArray);
    }
    
    @Test
    public void reverseTrivialArray(){
        List<Integer> trivialArray = new ArrayList<Integer>();
        trivialArray.add(1);
        List<Integer> checkArray = new ArrayList<Integer>(trivialArray);
        
        List<Integer> mirrored = Lists.mirrored(trivialArray);
        
        assertEquals(checkArray, mirrored);
    }
    
    @Test
    public void permutationsClassicArray(){
        List<String> l = new ArrayList<String>();
        l.add("Joel");l.add("Robert");l.add("Noé");//l.add("Mathilda");//l.add("Jérémie")//;l.add("Léa");l.add("Paul");l.add("Régis");
        //l.add("Bruno");l.add("Nicolas");//l.add("Ellinore");
        List<List<String>> lPerm = Lists.permutations(l);
        System.out.println(lPerm.size());
        for(List<String> list : lPerm){
            System.out.println(list.toString());
        }
        

    }
    @Test
    public void permutationsOnTrivialArray(){
        List<String> l = new ArrayList<String>();
        l.add("Johan");
        List<List<String>> lPerm = Lists.permutations(l);
        List<List<String>> lExpected = new ArrayList<List<String>>();
        lExpected.add(l);
        
        assertEquals(lExpected, lPerm);
    }
    
    @Test
    public void permutationsOnEmptyArray(){
        List<String> l = new ArrayList<String>();
        List<List<String>> lPerm = Lists.permutations(l);
        List<List<String>> lExpected = new ArrayList<List<String>>();
        lExpected.add(l);
        
        assertEquals(lExpected, lPerm);
    }
}
