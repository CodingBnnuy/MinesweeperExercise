package Minesweeping;

import java.util.Scanner;

/**
 * 
 */

/**
 * Main
 * 
 * Version 1
 * 
 * 19. Feb 2024
 * 
 * @author Sam
 *
 */

public class Main {
	private int minefield [][];
	private int rows[] = {9, 16, 16};
	private int cols[] = {9, 16, 30};
	private int mines[] = {10, 40, 99};
	private int safeFields;
	
	
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
		 * 				 16x30 field
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
		System.out.println("Choose Difficulty: 0 = Easy; 1 = Medium; 2 = Hard");
		do {
			int input = 0;
			validInput = false;
			try {
				input = userInput.nextInt();
				if(input < 3 && input >= 0) {
					validInput = true;
					difficulty = input;
				} else {
					System.out.println("Input was not within bounds. Choose from: 0 = Easy; 1 = Medium; 2 = Hard!");
				}
			} catch (Exception e) {
				System.out.println("Input was not a valid Integer!");
			}
		} while(!validInput);
		
		Main main = new Main();
		
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
			if(main.minefield[row][col] == 9) { //If it's a mine: End game
				gameOver = true;
				main.revealMines();
				main.outputMinefield();
				System.out.println("You lost!");
			} else if(main.minefield[row][col] < 10) { //Check if field is not yet revealed
				main.checkField(row, col);
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
		
		
		minefield = new int[iRows][iCols];
		
		for (int i = 0; i<iMines; i++) {
			int row = (int)(Math.random() * iRows);
			int col = (int)(Math.random() * iCols);
			
			//choose random field until empty one has been found
			while(minefield[row][col] != 0) {
				row = (int)(Math.random() * iRows);
				col = (int)(Math.random() * iCols);
			}
			
			//Turn field into mine
			minefield[row][col] = 9;
		}
		
		for (int i = 0; i<iRows; i++) {
			for (int j = 0; j<iCols; j++) {
				if (minefield[i][j] != 9) { //Don't check fields that have mines
					//Checks field to the top left for mines
					try {
						if(minefield[i-1][j-1] == 9) {
							minefield[i][j] += 1;
						}
					} catch(Exception e) {} //Empty Catch to continue after "Array Index out of Bounds" Exception
					
					//Checks field to the top for mines
					try {
						if(minefield[i-1][j] == 9) {
							minefield[i][j] += 1;
						}
					} catch(Exception e) {}
					
					//Checks field to the top right for mines
					try {
						if(minefield[i-1][j+1] == 9) {
							minefield[i][j] += 1;
						}
					} catch(Exception e) {}
					
					//Checks field to the left for mines
					try {
						if(minefield[i][j-1] == 9) {
							minefield[i][j] += 1;
						}
					} catch(Exception e) {}
					
					//Checks field to the right for mines
					try {
						if(minefield[i][j+1] == 9) {
							minefield[i][j] += 1;
						}
					} catch(Exception e) {}
					
					//Checks field to the bottom left for mines
					try {
						if(minefield[i+1][j-1] == 9) {
							minefield[i][j] += 1;
						}
					} catch(Exception e) {}
					
					//Checks field to the bottom for mines
					try {
						if(minefield[i+1][j] == 9) {
							minefield[i][j] += 1;
						}
					} catch(Exception e) {}
					
					//Checks field to the bottom right for mines
					try {	
						if(minefield[i+1][j+1] == 9) {
							minefield[i][j] += 1;
						}						
					} catch(Exception e) {}
				}
			}
		}
	}
	
	//Reveals field (if possible) and reveals surrounding fields if a 0 is revealed.
	public void checkField(int row, int col) {
		minefield[row][col] += 10;
		safeFields -= 1;
		if (minefield[row][col] == 10) { //If the revealed field has no surrounding mines, reveal all surrounding fields
			try {
				if(minefield[row-1][col-1] < 10) {
					checkField(row-1, col-1);
				}
			} catch (Exception e) {}
			try {
				if(minefield[row-1][col] < 10) {
					checkField(row-1, col);
				}
			} catch (Exception e) {}
			try {
				if(minefield[row-1][col+1] < 10) {
					checkField(row-1, col+1);
				}
			} catch (Exception e) {}
			try {
				if(minefield[row][col-1] < 10) {
					checkField(row, col-1);
				}
			} catch (Exception e) {}
			try {
				if(minefield[row][col+1] < 10) {
					checkField(row, col+1);
				}
			} catch (Exception e) {}
			try {
				if(minefield[row+1][col-1] < 10) {
					checkField(row+1, col-1);
				}
			} catch (Exception e) {}
			try {
				if(minefield[row+1][col] < 10) {
					checkField(row+1, col);
				}
			} catch (Exception e) {}
			try {
				if(minefield[row+1][col+1] < 10) {
					checkField(row+1, col+1);
				}
			} catch (Exception e) {}
		}
	}
	
	
	//Reveals the location of all mines after Loss
	public void revealMines() {
		for(int i = 0; i<minefield.length; i++) {
			for(int j = 0; j<minefield[0].length; j++) {
				if(minefield[i][j] == 9) {
					minefield[i][j] = 19;
				}
			}
		}
	}
	
	//Draws minefield in Console. Prettied up in place of GUI (Will come in later versions)
	public void outputMinefield() {
		System.out.print("  ");
		for(int i = 0; i<minefield.length; i++) {
			if(i<10) {
				System.out.print(" | " + i);
			} else {
				System.out.print(" |" + i);
			}
		}
		for(int i = 0; i<minefield.length; i++) {
			System.out.println();
			System.out.print("---");
			for(int j = 0; j<minefield[0].length; j++) {
				System.out.print("+---");
			}
			System.out.println();
			if(i<10) {
				System.out.print(" ");
			}
			System.out.print(i);
			for(int j = 0; j<minefield[0].length; j++) {
				System.out.print(" |");
				if(minefield[i][j] == 19) {
					System.out.print(" @");
				} else if(minefield[i][j] >= 10) {
					System.out.print(" " + (minefield[i][j]%10));
				} else {
					System.out.print("  ");
				}
			}
		}
		System.out.println();
	}
	
	//Reveals a random field with no surrounding mines to reduce random guesses in the beginning
	public void easyStarter() {
		int row = (int)(Math.random() * minefield.length);
		int col = (int)(Math.random() * minefield[0].length);
		
		//choose random field until a 0 has been found
		while(minefield[row][col] != 0) {
			row = (int)(Math.random() * minefield.length);
			col = (int)(Math.random() * minefield[0].length);
		}
		
		checkField(row, col);
	}
}
