import java.util.ArrayList;

public class StudentPlayer extends Player{
    
	// KONSTANSOK
	private int depth = 5;
	private final int HUMAN = 1;
	private final int AI = 2;
	private final int EMPTY = 0;
	
	public StudentPlayer(int playerIndex, int[] boardSize, int nToConnect) {
        super(playerIndex, boardSize, nToConnect);
    }

    @Override
    public int step(Board board) {
    	Board currentState = new Board(board);
    	
        int bestColumn = maxValue(currentState, depth, Integer.MIN_VALUE, Integer.MAX_VALUE).getColumn();
        
    	return bestColumn;
    }
    
    // MAX
    private ColumnValuePair maxValue(Board board, int d, int alpha, int beta) {
    	
    	if(d==0 || board.gameEnded()) 
    		if(board.gameEnded())
    			return gameEndValue(board);
    		else
    			return new ColumnValuePair(0, earnScore(board, AI));
    	
    	
    	ArrayList<Integer> openList = board.getValidSteps();
		int v = Integer.MIN_VALUE;
		int column = openList.get(0);
		for(int i = 0; i<openList.size(); i++) {
			Board clone = new Board(board);
			clone.step(2, openList.get(i));
			int temp_v = minValue(clone, d-1, alpha, beta).getValue();
			if(temp_v > v) { //max
				v = temp_v;
				column = openList.get(i);
			}
			alpha = Math.max(alpha,  v);
			if(alpha >= beta)
				break;
		}
		return new ColumnValuePair(column, v);
    }
    
    
    // MIN
    private ColumnValuePair minValue(Board board, int d, int alpha, int beta) {
    	
    	if(d==0 || board.gameEnded()) 
    		if(board.gameEnded())
    			return gameEndValue(board);
    		else
    			return new ColumnValuePair(0, earnScore(board, AI));
    	
    	
    	ArrayList<Integer> openList = board.getValidSteps();
		int v = Integer.MAX_VALUE;
		int column = openList.get(0);
		for(int i = 0; i<openList.size(); i++) {
			Board clone = new Board(board);
			clone.step(1, openList.get(i));
			int temp_v = maxValue(clone, d-1, alpha, beta).getValue();
			if(temp_v < v) { //minimum
				v = temp_v;
				column = openList.get(i);
			}
			beta = Math.min(beta, v);
			if(alpha >= beta)
				break;
		}
		return new ColumnValuePair(column, v);
    }
    
    // Játék vége pontozás
    private ColumnValuePair gameEndValue(Board board) {
    	if(board.getWinner() == 1)
    		return new ColumnValuePair(0, Integer.MIN_VALUE);
    	if(board.getWinner() == 2)
    		return new ColumnValuePair(0, Integer.MAX_VALUE);
    	return new ColumnValuePair(0,0);
    }
    
    // Játék közbeni pontozás
    private int earnScore(Board board, int playerIndex) {
    	int score = 0;
    	int maxRow = 6;
    	int maxCol = 7;
    	int[][] state = board.getState();
    	
    	//Center
    	Block4 center = new Block4();
    	int centercolumn = 3;
    	for(int row = 0; row < maxRow - 3; row++) {
    		center.setColumnBlock4FromState(state, row, centercolumn);
    		score += center.count(playerIndex) * 300;
    	}
    	
    	//Horizontal
    	Block4 horizontal = new Block4();
    	for(int row = 0; row < maxRow; row++) {
    		for(int column = 0; column < maxCol - 3; column++) {
    			horizontal.setRowBlock4FromState(state, row, column);
    			score += defaultScore(horizontal, playerIndex);
    		}
    	}
    	
    	//Vertical
    	Block4 vertical = new Block4();
    	for(int row = 0; row < maxRow - 3; row++) {
    		for(int column = 0; column < maxCol; column++) {
    			vertical.setColumnBlock4FromState(state, row, column);
    			score += defaultScore(vertical, playerIndex);
    		}
    	}
    	
    	//Diagonal
    	Block4 diagonal = new Block4();
    	for(int row = 0; row < maxRow - 3; row++) {
    		for(int column = 0; column < maxCol -3; column++) {
    			diagonal.setDiagonalBlock4FromState(state, row, column);
    			score += defaultScore(diagonal, playerIndex);
    		}
    	}
    	
    	//SkewDiagonal
    	Block4 skew_diagonal = new Block4();
    	for(int row = 0; row < maxRow - 3; row++) {
    		for(int column = maxCol - 1; column > (0+3-1); column--) {
    			skew_diagonal.setSkewDiagonalBlock4FromState(state, row, column);
    			score += defaultScore(skew_diagonal, playerIndex);
    		}
    	}
    	return score;
    }
    
    // Pontozás mértéke
    private int defaultScore(Block4 block, int playerIndex) {
    	int score = 0;
    	int enemy = HUMAN;
    	if(playerIndex == HUMAN) {
    		enemy = AI;
    	}
    	
    	if(block.count(playerIndex) == 4)
    		score += 10000;
    	else if(block.count(playerIndex) == 3 && block.count(EMPTY) == 1)
    		score += 1000;
    	else if(block.count(playerIndex) == 2 && block.count(EMPTY) == 2)
    		score += 100;
    	
    	if(block.count(enemy) == 3 && block.count(EMPTY) == 1)
    		score -= 500;
    	
    	return score;
    }
    
    // Osztály a lépés, és annak értékének tárolására
    private class ColumnValuePair{
    	private int column;
    	private int value;
    	
    	public ColumnValuePair(int c, int v) {
    		column = c;
    		value = v;
    	}
    	
    	public int getColumn() { return column; }
    	public int getValue() { return value;}
    }
    
    // Osztály a block-ok könnyebb mozgatására
    private class Block4{
    	private int[] block;
    	
    	public Block4() {
        	block = new int[4];
        	for(int i: block)
        		i = 0;
    	}
    	
    	public int count(int playerIndex) {
    		int db = 0;
    		for(int i: block)
    			if(i == playerIndex)
    				++db;
    		return db;
    	}
    	
    	public void setColumnBlock4FromState(int[][] state, int row, int fixColumn) {
    		block[0] = state[row][fixColumn];
    		block[1] = state[row+1][fixColumn];
    		block[2] = state[row+2][fixColumn];
    		block[3] = state[row+3][fixColumn];
    	}
    	
    	public void setRowBlock4FromState(int[][] state, int fixRow, int column) {
    		block[0] = state[fixRow][column];
    		block[1] = state[fixRow][column+1];
    		block[2] = state[fixRow][column+2];
    		block[3] = state[fixRow][column+3];
    	}
    	
    	public void setDiagonalBlock4FromState(int[][] state, int fixRow, int fixColumn) {
    		block[0] = state[fixRow][fixColumn];
    		block[1] = state[fixRow+1][fixColumn+1];
    		block[2] = state[fixRow+2][fixColumn+2];
    		block[3] = state[fixRow+3][fixColumn+3];
    	}
    	
    	public void setSkewDiagonalBlock4FromState(int[][] state, int fixRow, int fixColumn) {
    		block[0] = state[fixRow][fixColumn];
    		block[1] = state[fixRow+1][fixColumn-1];
    		block[2] = state[fixRow+2][fixColumn-2];
    		block[3] = state[fixRow+3][fixColumn-3];
    	}
    }
}
