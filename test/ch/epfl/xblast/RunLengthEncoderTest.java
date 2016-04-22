package ch.epfl.xblast;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
public final class RunLengthEncoderTest {

    @Test
    public void encodingTest(){
        int[] provisoire = {10, 10, 10, 20, 20, 30, 20, 30, 40, 40, 
                40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,
                40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,
                40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,
                40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,
                40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,
                40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,
                40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40};
        List<Byte> toEncode = new ArrayList<Byte>();
        
        for(int i : provisoire){
            toEncode.add((byte)i);
        }
        System.out.println(toEncode.toString());
        
        List<Byte> encoded = RunLengthEncoder.encode(toEncode);
        System.out.println(encoded.toString());
        
        assertEquals(toEncode, RunLengthEncoder.decode(encoded));
    }
    
    @Test
    public void encodingLimitCaseTest128and2(){
        int[] provisoire = {10, 10, 10, 20, 20, 30, 20, 30, 40, 40, 
                40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,
                40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,
                40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40};
        List<Byte> toEncode = new ArrayList<Byte>();
        
        for(int i : provisoire){
            toEncode.add((byte)i);
        }
        System.out.println(toEncode.toString());
        
        List<Byte> encoded = RunLengthEncoder.encode(toEncode);
        System.out.println(encoded.toString());
        
        assertEquals(toEncode, RunLengthEncoder.decode(encoded));
    }
    
    @Test
    public void encodingLimitCaseTest128and1(){
        int[] provisoire = {40, 40, 
                40, 40,40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,
                40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,
                40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40};
        List<Byte> toEncode = new ArrayList<Byte>();
        
        for(int i : provisoire){
            toEncode.add((byte)i);
        }
        System.out.println(toEncode.toString());
        
        List<Byte> encoded = RunLengthEncoder.encode(toEncode);
        System.out.println(encoded.toString());
        
        assertEquals(toEncode, RunLengthEncoder.decode(encoded));
    }
    
    
    @Test
    public void encodingLimitCaseTest128(){
        int[] provisoire = {40,40, 40,40, 40,40, 40, 40,40, 40, 40, 40, 40, 40,
                40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,
                40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40,40, 40, 40, 40, 40, 40};
        List<Byte> toEncode = new ArrayList<Byte>();
        
        for(int i : provisoire){
            toEncode.add((byte)i);
        }
        System.out.println(toEncode.toString());
        
        List<Byte> encoded = RunLengthEncoder.encode(toEncode);
        System.out.println(encoded.toString());
        System.out.println(toEncode.size() + " : "+RunLengthEncoder.decode(encoded).size());
        assertEquals(toEncode, RunLengthEncoder.decode(encoded));
    }
    @Test
    public void decodingTest(){
        int[] provisoire = {20,20,20,20,20,20,20,20,20,20,20,20,
                20,20,20,20,20,20,20,20,20,20,20,20,
                20,20,20,20,20,20,20,20,20,20,20,20,30,30,10,10,10,10,10,10,5,40,40,40,40,40,50,50,50,50,50,20,20};
        List<Byte> toEncode = new ArrayList<Byte>();
        
        for(int i : provisoire){
            toEncode.add((byte)i);
        }
        
        System.out.println(toEncode.toString());
        
        List<Byte> encoded = RunLengthEncoder.encode(toEncode);
        System.out.println(encoded.toString()); 
        List<Byte> decoded = RunLengthEncoder.decode(encoded);
        System.out.println(decoded.toString());
        assertEquals(toEncode, decoded );
        
    }
    
    
    @Test
    public void encodingOnTrivialTest(){
        byte[] provisoire = {20};
        List<Byte> toEncode = new ArrayList<Byte>();
        
        for(int i : provisoire){
            toEncode.add((byte)i);
        }
        List<Byte> encoded = RunLengthEncoder.encode(toEncode);
        List<Byte> decoded = RunLengthEncoder.decode(encoded);
        
        assertEquals(encoded, decoded);
        assertEquals(toEncode, encoded);
        assertEquals(toEncode, decoded);


        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void throwsCorrectException(){
        
        int[] provisoire = {20,20,20,20,20,20,30,30,10,10,10,-10,10,10,5,40,40,40,40,40,50,50,50,50,50,20,20};
        List<Byte> toEncode = new ArrayList<Byte>();
        
        for(int i : provisoire){
            toEncode.add((byte)i);
        }
        System.out.println(toEncode.toString());
        
        List<Byte> encoded = RunLengthEncoder.encode(toEncode);
        System.out.println(encoded.toString());
    }
    
    @Test
    public void variousCases(){
        byte[] provisoire = {20,20};
        List<Byte> toEncode = new ArrayList<Byte>();
        
        for(int i : provisoire){
            toEncode.add((byte)i);
        }
        List<Byte> encoded = RunLengthEncoder.encode(toEncode);
        List<Byte> decoded = RunLengthEncoder.decode(encoded);
        
        assertEquals(encoded, decoded);
        assertEquals(toEncode, encoded);
        assertEquals(toEncode, decoded);
        
        
        //NEXT CASE 
        byte[] provisoire1 = {20,10,30,50,40,40,80,10,20,30};
        List<Byte> toEncode1 = new ArrayList<Byte>();
        
        for(int i : provisoire1){
            toEncode1.add((byte)i);
        }
        List<Byte> encoded1 = RunLengthEncoder.encode(toEncode1);
        List<Byte> decoded1 = RunLengthEncoder.decode(encoded1);
        
        assertEquals(encoded1, decoded1);
        assertEquals(toEncode1, encoded1);
        assertEquals(toEncode1, decoded1);
        
        //NEXT CASE 
        byte[] provisoire2 = {20,10,30,50,40,40,80,10,20,30,30,40,50};
        List<Byte> toEncode2 = new ArrayList<Byte>();
        
        for(int i : provisoire2){
            toEncode2.add((byte)i);
        }
        List<Byte> encoded2 = RunLengthEncoder.encode(toEncode2);
        List<Byte> decoded2 = RunLengthEncoder.decode(encoded2);
        
        assertEquals(encoded2, decoded2);
        assertEquals(toEncode2, encoded2);
        assertEquals(toEncode2, decoded2);
        
    }
    
    
}
