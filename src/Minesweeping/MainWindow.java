package Minesweeping;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Main
 * 
 * 
 * 
 * Version 3
 * - Improve Performance
 * - Add Flexible Size
 * 
 * 28. Feb 2024
 * 
 * @author CodingBnnuy
 *
 *
 * Version History:
 * Version 1
 * - Add base functionality
 * 
 * Version 2
 * - Added Easy Start
 */

public class MainWindow {
	private int minefield [][];
	private int cols[] = {9, 16, 30, 0}; //x axis
	private int rows[] = {9, 16, 16, 0}; //y axis
	private int mines[] = {10, 40, 99, 0};
	private int dir[][] = { {-1, -1}, {0, -1}, {1, -1}, {-1, 0}, {1, 0}, {-1, 1}, {0, 1}, {1, 1}}; //2D Array to point to every surrounding field
	private int safeFields;
	private boolean tempField [][];
	private ArrayList<ZeroHelper> zeroes = new ArrayList<ZeroHelper>();
	
	public static void main(String[] args) {
		
		/*
		 * difficulty 0: easy
		 * 				 9x9 field
		 * 				 10 mines
		 * 
		 * difficulty 1: medium
		 * 				 16x16 field
		 * 				 40 mines
		 * 
		 * difficulty 2: hard
		 * 				 30x16 field
		 * 				 99 mines 
		 * 
		 * 
		 * field values:
		 * 0-8: not revealed, not a mine, amount of mines in surrounding eight fields
		 * 9: not revealed, is a mine
		 * 10-18: revealed, modulo 10 is the amount of mines in surrounding eight fields
		 * 19: revealed mine
		 */
		boolean validInput = false;
		boolean gameOver = false;
		int row = 0;
		int col = 0;
		int difficulty = 0;
		
		Scanner userInput = new Scanner(System.in);
		System.out.println("Choose Difficulty: 0 = Easy; 1 = Medium; 2 = Hard; 3 = Custom");
		do {
			int input = 0;
			validInput = false;
			try {
				input = userInput.nextInt();
				if(input < 4 && input >= 0) {
					validInput = true;
					difficulty = input;
				} else {
					System.out.println("Input was not within bounds. Choose from: 0 = Easy; 1 = Medium; 2 = Hard; 3 = Custom!");
				}
			} catch (Exception e) {
				System.out.println("Input was not a valid Integer!");
			}
		} while(!validInput);
		
		MainWindow main = new MainWindow();
		
		if(difficulty == 3) {
			System.out.println("Choose amount of rows!");
			do {
				int input = 0;
				validInput = false;
				try {
					input = userInput.nextInt();
					if (input > 0) {
						validInput = true;
						main.rows[3] = input;
					}
				} catch(Exception e) {}
			} while(!validInput);
			
			System.out.println("Choose amount of columnns!");
			do {
				int input = 0;
				validInput = false;
				try {
					input = userInput.nextInt();
					if (input > 0) {
						validInput = true;
						main.cols[3] = input;
					}
				} catch(Exception e) {}
			} while(!validInput);
			
			System.out.println("Choose amount of mines!");
			do {
				int input = 0;
				validInput = false;
				try {
					input = userInput.nextInt();
					if (input > 0) {
						validInput = true;
						main.mines[3] = input;
					}
				} catch(Exception e) {}
			} while(!validInput);
		}
		
		main.setField(difficulty);
		main.easyStarter();		
		
		main.outputMinefield();
		
		while(!gameOver) {			//Game ends either when a mine is revealed, or once all fields without mines have been revealed
			do {					//Get the row of the field the user wants to reveal
				validInput = false;
				try {
					System.out.println("Input row!");
					row = userInput.nextInt();
					if(row < main.rows[difficulty] && row >= 0) {		//Do not reveal if the given integer is out of bounds
						validInput = true;
					} else {
						System.out.println("Input is out of bounds. Valid values for row are between 0 and " + (main.rows[difficulty]-1));
					}
				} catch (Exception e) {
					System.out.println("Input is not an integer.");
				}
			} while (!validInput);
			
			do {					//Get the column of the field the user wants to reveal
				validInput = false;
				try {
					System.out.println("Input column!");
					col = userInput.nextInt();
					if(col < main.cols[difficulty] && col >= 0) {
						validInput = true;
					} else {
						System.out.println("Input is out of bounds. Valid values for column are between 0 and " + (main.cols[difficulty]-1));
					}
				} catch (Exception e) {
					System.out.println("Input is not an integer.");
				}
			} while (!validInput);
			
			//Reveal the given field
			if(main.minefield[col][row] == 9) { //If it's a mine: End game
				gameOver = true;
				main.revealMines();
				main.outputMinefield();
				System.out.println("You lost!");
			} else if(main.minefield[col][row] < 10) { //Check if field is not yet revealed
				main.checkField(col, row);
				main.outputMinefield();
				if(main.safeFields == 0) {
					gameOver = true;
					System.out.println("You won!");
				}
			} else {
				System.out.println("Field has already been revealed!");
			}
		}
		
		userInput.close();
	}
	
	
	//Set the Size of the 2D-Array and place mines. Counts mines in surrounding fields at the end and sets field values
	private void setField(int diff) {
		int iMines = mines[diff];
		int iRows = rows[diff];
		int iCols = cols[diff];
		
		safeFields = (iRows * iCols) - iMines;
		
		
		minefield = new int[iCols][iRows];
		
		for (int i = 0; i<iMines; i++) {
			int row = (int)(Math.random() * iRows);
			int col = (int)(Math.random() * iCols);
			
			//choose random field until empty one has been found
			while(minefield[col][row] == 9) {
				row = (int)(Math.random() * iRows);
				col = (int)(Math.random() * iCols);
			}
			
			//Turn field into mine
			minefield[col][row] = 9;
			for (int j = 0; j<dir.length; j++) { //Increase the value of each surrounding field by 1
				try {
					if(minefield[col+dir[j][0]][row+dir[j][1]] != 9) {
						minefield[col+dir[j][0]][row+dir[j][1]] += 1;
					}
				} catch(Exception e) {}
			}
		}
	}
	
	//Reveals field (if possible) and reveals surrounding fields if a 0 is revealed.
	public void checkField(int col, int row) {
		minefield[col][row] += 10;
		safeFields -= 1;
		if (minefield[col][row] == 10) { //If the revealed field has no surrounding mines, reveal all surrounding fields
			for(int i = 0; i<dir.length; i++) {
				try {
					if(minefield[col+dir[i][0]][row+dir[i][1]] < 10) {
						checkField(col+dir[i][0], row+dir[i][1]);
					}
				} catch(Exception e) {}
			}
		}
	}
	
	
	//Reveals the location of all mines after Loss
	public void revealMines() {
		for(int col = 0; col<minefield.length; col++) {
			for(int row = 0; row<minefield[0].length; row++) {
				if(minefield[col][row] == 9) {
					minefield[col][row] = 19;
				}
			}
		}
	}
	
	//Draws minefield in Console. Prettied up in place of GUI (Will come in later versions)
	public void outputMinefield() {
		System.out.print("  ");
		for(int col = 0; col<minefield.length; col++) {
			if(col<10) {
				System.out.print(" | " + col);
			} else {
				System.out.print(" |" + col);
			}
		}
		for(int row = 0; row<minefield[0].length; row++) {
			System.out.println();
			System.out.print("---");
			for(int col = 0; col<minefield.length; col++) {
				System.out.print("+---");
			}
			System.out.println();
			if(row<10) {
				System.out.print(" ");
			}
			System.out.print(row);
			for(int col = 0; col<minefield.length; col++) {
				System.out.print(" |");
				if(minefield[col][row] == 19) {
					System.out.print(" @");
				} else if(minefield[col][row] >= 10) {
					System.out.print(" " + (minefield[col][row]%10));
				} else {
					System.out.print("  ");
				}
			}
		}
		System.out.println();
	}
	
	//Reveals a random field with no surrounding mines to reduce random guesses in the beginning
	public void easyStarter() {
		tempField = new boolean[minefield.length][minefield[0].length];
		
		//Create a temporary field to display whether a space is 0 or not
		for(int col = 0; col < minefield.length; col++) {
			for(int row = 0; row < minefield[0].length; row++) {
				if(minefield[col][row] == 0) {
					tempField[col][row] = true;
				} else {
					tempField[col][row] = false;
				}
			}
		}
		
		//Go through the temporary field and count adjacent 0's
		for(int col = 0; col < tempField.length; col++) {
			for(int row = 0; row < tempField[0].length; row++) {
				if(tempField[col][row]) {
					zeroes.add(new ZeroHelper(CountAdjacent(col, row), col, row));
				}
			}
		}
		
		Collections.sort(zeroes);	
		
		boolean done = false;
		int minefieldSize = minefield.length*minefield[0].length; 
		int maxAmount = minefieldSize / 100 * 50;
		int minAmount = minefieldSize / 100 * 5;
		
		
		while(zeroes.size() > 1 && !done) {
			if(zeroes.get(zeroes.size()-1).amount > maxAmount) {
				zeroes.remove(zeroes.size()-1);
			} else if(zeroes.get(0).amount < minAmount) {
				zeroes.remove(0);
			}
		}
		
		int chosenIndex = (int) (Math.random() * zeroes.size());
		checkField(zeroes.get(chosenIndex).col, zeroes.get(chosenIndex).row);
	}
	
	//Recursively counts all adjacent 0's
	public int CountAdjacent(int row, int col) {
		int counter = 1;
		
		tempField[row][col] = false;		//Deactivate the current space (before the recursive calls), so it doesn't get counted twice
		try {
			if(tempField[row-1][col-1]) {
				counter += CountAdjacent(row-1, col-1);
			}
		} catch(Exception e) {}
		try {
			if(tempField[row-1][col]) {
				counter += CountAdjacent(row-1, col);
			}
		} catch(Exception e) {}
		try {
			if(tempField[row-1][col+1]) {
				counter += CountAdjacent(row-1, col+1);
			}
		} catch(Exception e) {}
		try {
			if(tempField[row][col-1]) {
				counter += CountAdjacent(row, col-1);
			}
		} catch(Exception e) {}
		try {
			if(tempField[row][col+1]) {
				counter += CountAdjacent(row, col+1);
			}
		} catch(Exception e) {}
		try {
			if(tempField[row+1][col-1]) {
				counter += CountAdjacent(row+1, col-1);
			}
		} catch(Exception e) {}
		try {
			if(tempField[row+1][col]) {
				counter += CountAdjacent(row+1, col);
			}
		} catch(Exception e) {}
		try {
			if(tempField[row+1][col+1]) {
				counter += CountAdjacent(row+1, col+1);
			}
		} catch(Exception e) {}
		
		return counter;
	}
	
	
	//Class to build the ArrayList with. Count the amount of connected 0's in the minefield
	class ZeroHelper implements Comparable<ZeroHelper>{	
		public int amount;
		public int row;
		public int col;
		
		public ZeroHelper(int iAmount, int iCol, int iRow) {
			amount = iAmount;
			row = iRow;
			col = iCol;
		}

		@Override
		public int compareTo(ZeroHelper other) {
			return Integer.compare(this.amount, other.amount);
		}
	}
}
