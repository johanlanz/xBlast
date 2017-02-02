package ch.epfl.xblast.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.Cell;

public class BoardTest{
       
       
       @Test
       public void constructorWorksInNormalCase(){
           List<Sq<Block>> constantFree = new ArrayList<Sq<Block>>();
           for(int i = 0; i<195;i++){
               constantFree.add(Sq.constant(Block.FREE));
               
           }
           
           Board b = new Board(constantFree);
       }
       
       @Test(expected = IllegalArgumentException.class)
       public void constructorThrowsExceptionOnEmptyArray(){
           List<Sq<Block>> emptyList = new ArrayList<Sq<Block>>();
           Board b = new Board(emptyList);
       }
       
       @Test(expected = IllegalArgumentException.class)
       public void constructorThrowsExceptionOnTooBigArray(){
           List<Sq<Block>> bigArray = new ArrayList<Sq<Block>>();
           for(int i = 0; i<200;i++){
               bigArray.add(Sq.constant(Block.FREE));
           }
           Board b = new Board(bigArray);
       }
       
       @Test
       public void ofRowsConstructABoard(){
           List<List<Block>> listOfRows = new ArrayList<List<Block>>();
           
           for(int i = 0; i <Cell.ROWS; i++){
               List<Block> row = new ArrayList<Block>();
               for(int j = 0; j<Cell.COLUMNS; j++){
                   row.add(Block.FREE);
               }
               listOfRows.add(row);
           }
           
           Board b = Board.ofRows(listOfRows);
           for(int i = 0; i<13; i++){
               for(int j = 0; j<15; j++){
                   Cell c = new Cell(i,j);
                   assertEquals(Block.FREE, b.blockAt(c));
               }
           }
       }
       
       @Test (expected = IllegalArgumentException.class)
       public void ofRowsThrowsCorrectExceptionEmptyArray(){
           List<List<Block>> emptyListOfRows = new ArrayList<List<Block>>();
           Board b = Board.ofRows(emptyListOfRows);
       }
       @Test (expected = IllegalArgumentException.class)
       public void ofRowsThrowsCorrectExceptionTooBig(){
           List<List<Block>> listOfRows = new ArrayList<List<Block>>();
           
           
           
           for(int i = 0; i <14; i++){
               List<Block> row = new ArrayList<Block>();
               for(int j = 0; j<16; j++){
                   row.add(Block.FREE);
               }
               listOfRows.add(row);
           }
           
           Board b = Board.ofRows(listOfRows);
       }
       
       @Test
       public void ofInnerBlocksWalledCreatesWalled(){
           List<List<Block>> listOfInnerRows = new ArrayList<List<Block>>();
           
           for(int i = 0; i <11; i++){
               List<Block> row = new ArrayList<Block>();
               for(int j = 0; j<13; j++){
                   row.add(Block.FREE);
               }
               listOfInnerRows.add(row);
               
           }
           
           Board b = Board.ofInnerBlocksWalled(listOfInnerRows);
           for(int i = 1; i<13; i++){
               for(int j = 1; j<11; j++){
                   Cell c = new Cell(i,j);
                   assertEquals(Block.FREE, b.blockAt(c));
               }
           }
           for(int i = 0; i<15; i++){
               Cell cTop = new Cell(i, 0);
               Cell cBottom = new Cell(i,13);
               assertEquals(Block.INDESTRUCTIBLE_WALL, b.blockAt(cTop));
               assertEquals(Block.INDESTRUCTIBLE_WALL, b.blockAt(cBottom));
           }
           for(int j = 0; j<13; j++){
               Cell fstCol = new Cell(0, j);
               Cell lstCol = new Cell(14, j);
               assertEquals(Block.INDESTRUCTIBLE_WALL, b.blockAt(fstCol));
               assertEquals(Block.INDESTRUCTIBLE_WALL, b.blockAt(lstCol));
           }
           
       }
       @Test (expected = IllegalArgumentException.class)
       public void innerRowsThrowsException(){
           List<List<Block>> listOfRows = new ArrayList<List<Block>>();
           
           
           
           for(int i = 0; i <14; i++){
               List<Block> row = new ArrayList<Block>();
               for(int j = 0; j<16; j++){
                   row.add(Block.FREE);
               }
               listOfRows.add(row);
           }
           
           Board b = Board.ofInnerBlocksWalled(listOfRows);
       }
       
       @Test
       public void ofQuadrantConstructsAsIntended(){
           
           Block __ = Block.FREE;
           Block XX = Block.INDESTRUCTIBLE_WALL;
           Block xx = Block.DESTRUCTIBLE_WALL;
           Board board = Board.ofQuadrantNWBlocksWalled(
             Arrays.asList(
               Arrays.asList(__, __, __, __, __, xx, __),
               Arrays.asList(__, XX, xx, XX, xx, XX, xx),
               Arrays.asList(__, xx, __, __, __, xx, __),
               Arrays.asList(xx, XX, __, XX, XX, XX, XX),
               Arrays.asList(__, xx, __, xx, __, __, __),
               Arrays.asList(xx, XX, xx, XX, xx, XX, __)));
           
           List<List<Block>> verify = Arrays.asList(
                   Arrays.asList(__, __, __, __, __, xx, __, xx, __, __, __, __, __ ),
                   Arrays.asList(__, XX, xx, XX, xx, XX, xx, XX, xx, XX, xx, XX, __),
                   Arrays.asList(__, xx, __, __, __, xx, __, xx, __ , __,__, xx, __),
                   Arrays.asList(xx, XX, __, XX, XX, XX, XX, XX, XX, XX, __, XX, xx),
                   Arrays.asList(__, xx, __, xx, __, __, __, __, __, xx, __, xx, __),
                   Arrays.asList(xx, XX, xx, XX, xx, XX, __, XX, xx, XX, xx, XX, xx),                   
                   Arrays.asList(__, xx, __, xx, __, __, __, __, __, xx, __, xx, __),
                   Arrays.asList(xx, XX, __, XX, XX, XX, XX, XX, XX, XX, __, XX, xx),
                   Arrays.asList(__, xx, __, __, __, xx, __, xx, __ , __,__, xx, __),
                   Arrays.asList(__, XX, xx, XX, xx, XX, xx, XX, xx, XX, xx, XX, __),
                   Arrays.asList(__, __, __, __, __, xx, __, xx, __, __, __, __, __ ));
           
           Board check = Board.ofInnerBlocksWalled(verify);
           for(int i = 0; i<Cell.ROWS; i++){
               for(int j = 0; j<Cell.COLUMNS; j++){
                   assertEquals(check.blockAt(new Cell(i,j)), board.blockAt(new Cell(i,j)));

               }
           }
           
           
           
       }
       
       @Test
       public void blocksAtReturnsSq(){
           
           List<Sq<Block>> constantFree = new ArrayList<Sq<Block>>();
           for(int i = 0; i<195;i++){
               constantFree.add(Sq.constant(Block.FREE));
               
           }
           
           Board b = new Board(constantFree);
           for(int i = 0; i<15; i++){
               for(int j = 0; j<13; j++){
                   Cell c = new Cell(i,j);
                   Sq<Block> sqce = b.blocksAt(c);
                   assertEquals(Sq.constant(Block.FREE).head(), sqce.head());
               }
           }
           
       }
       
       @Test
       public void blockAtWorks(){
           Block xx = Block.FREE;
           List<Sq<Block>> constantFree = new ArrayList<Sq<Block>>();
           for(int i = 0; i<195;i++){
               constantFree.add(Sq.constant(Block.FREE));
               
           }
           
           Board b = new Board(constantFree);
           for(int i = 0; i<15; i++){
               for(int j = 0; j<13; j++){
                   Cell c = new Cell(i,j);
                   Block blck = b.blockAt(c);
                   assertEquals(xx, blck);
               }
           }
           
       }
       
       
}
