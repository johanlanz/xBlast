package ch.epfl.xblast.server.debug;

import java.util.List;
import java.util.Set;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.server.Block;
import ch.epfl.xblast.server.Board;
import ch.epfl.xblast.server.GameState;
import ch.epfl.xblast.server.Player;

public final class GameStatePrinter {
    private GameStatePrinter() {}

    public static void printGameState(GameState s) {
        List<Player> ps = s.alivePlayers();
        Board board = s.board();
        Set<Cell> blastedCells = s.blastedCells();
        Set<Cell> bombedCell = s.bombedCells().keySet();

        for (int y = 0; y < Cell.ROWS; ++y) {
            xLoop: for (int x = 0; x < Cell.COLUMNS; ++x) {
                Cell c = new Cell(x, y);
                for (Player p: ps) {
                    if (p.position().containingCell().equals(c)) {
                        System.out.print(stringForPlayer(p));
                        continue xLoop;
                    }
                }
                for(Cell blastedC : blastedCells){
                    Block b = board.blockAt(c);
                    if(blastedC.equals(c)&&b.isFree()){
                        System.out.print(stringForBlast());
                        continue xLoop;
                    }
                }
                
                for(Cell bombedC : bombedCell){
                    if(bombedC.equals(c)){
                        System.out.print(stringForBomb());
                        continue xLoop;
                    }
                }
                
                Block b = board.blockAt(c);
                System.out.print(stringForBlock(b));
            }
            System.out.println();
        }
    }

    private static String stringForPlayer(Player p) {
        StringBuilder b = new StringBuilder();
        b.append("\u001b[44m"+(p.id().ordinal() + 1));
        switch (p.direction()) {
        case N: b.append('^'); break;
        case E: b.append('>'); break;
        case S: b.append('v'); break;
        case W: b.append('<'); break;
        }
        b.append("\u001b[m");
        return b.toString();
    }

    private static String stringForBlock(Block b) {
        switch (b) {
        case FREE: return "  ";
        case INDESTRUCTIBLE_WALL: return "\u001b[47m  \u001b[m";
        case DESTRUCTIBLE_WALL: return "\u001b[47m\u001b[30m??\u001b[m";
        case CRUMBLING_WALL: return "\u001b[47m\u001b[30m¿¿\u001b[m";
        case BONUS_BOMB: return "\u001b[41m+b\u001b[m";
        case BONUS_RANGE: return "\u001b[41m+r\u001b[m";
        default: throw new Error();
        }
    }
    
    private static String stringForBomb(){
        return "\u001b[42mBB\u001b[m";
    }
    
    private static String stringForBlast(){
        return "\u001b[43m**\u001b[m";
    }
}
