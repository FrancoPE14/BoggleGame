package main.java.com.anteaters.boggle;
import java.util.*;
import java.nio.file.*;

public class BoggleBoard {

    int boardSize = 4; // static size for now; resizable boards later as enhancement
    Dice[][] grid = new Dice[boardSize][boardSize];
    LinkedList<Dice> currentEntry = new LinkedList<>(); // stores the user's current entry as a list of dice

    public BoggleBoard() {
        for (int i = 0; i < boardSize; i++){
            for (int j = 0; j < boardSize; j++){
                grid[i][j] = new Dice();
                grid[i][j].setCoordinates(i, j);
            }
        }
    }

    /**
     * Method to append a die to the current entry.
     * Includes logic to ensure the new die is valid: a check for its position and a check to make sure
     * it has now already been used. Both checks use the die's coordinates to check.
     * @param d the die looking to be added
     */
    public void addLetter(Dice d){
        boolean valid = true;

        // logic check to see if selection is valid (i.e bordering current letter, and as of yet unused).
        // Does not run if entry is empty; any dice is valid.
        if(!currentEntry.isEmpty()){

            // checking spacial requirements
            if(Math.abs(currentEntry.getLast().xCoordinate - d.xCoordinate) > 1 ||
               Math.abs(currentEntry.getLast().yCoordinate - d.yCoordinate) > 1){
                valid = false;
            }

            // checking to make sure Dice has not been used already
            // currently just iterates through the entries and flags if coordinates have been used.
            // may have better way to implement this.
            for(Dice usedDice : currentEntry){
                if(usedDice.xCoordinate == d.xCoordinate &&
                   usedDice.yCoordinate == d.yCoordinate){
                    valid = false;
                }
            }
        }

        if(valid){
            currentEntry.add(d);
        } else {
            System.out.println("Invalid dice selection"); // can implement messages based off of why dice is invalid.
        }
    }

    /**
     * Method to remove the most recent selection from the current entry. Currently does nothing when called
     * on an empty entry list, but can implement behavior in the future.
     */
    public void removeLetter(){
        try{
            currentEntry.removeLast();
        } catch (NoSuchElementException e) {
            // currently does nothing when trying to remove from an empty list.
        }
    }

     /**
     * Placeholder method to check if entry is in dictionary. Will integrate with James's function at later date.
     * Returns true by default for now.
     * @return Whether the current entry is in the dictionary (currently always true as a placeholder)
     */
    private boolean checkEntryCorrectness(){
        return true;
    }

    /**
     * Extracts the current entry and returns it as a string
     * @return the current entry's string representation
     */
    private String entryToString(){
        StringBuilder entryAsString = new StringBuilder();

        for(Dice d : currentEntry){
            entryAsString.append(d.visibleFace);
        }

        return entryAsString.toString();
    }

    /**
     * Method that returns a String array representation of the current board.
     * @return a String array representation of the current board
     */
    public String[][] boardToStringArray(){
        String[][] stringBoard = new String[boardSize][boardSize];

        for(int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                stringBoard[i][j] = grid[i][j].getVisibleFace();
            }
        }
        return stringBoard;
    }

    /**
     * Prints a text representation of the boggle board.
     */
    public void printBoard(){
        for (Dice[] row : grid){
            for (Dice d : row){
                System.out.printf("%2s ", d.getVisibleFace());
            }
            System.out.println();
        }
    }

    /**
     * Nested class to represent both a Boggle die, as well as its position on the boggle board.
     */
    public class Dice {

        final int NUMBER_OF_SIDES = 6;
        private String[] faces = new String[NUMBER_OF_SIDES];
        private String visibleFace;
        public int xCoordinate;
        public int yCoordinate;
        private Random random = new Random();

        /**
         * Default constructor. Defaults to setting the die faces to "abcdef".
         */
        public Dice(){
            faces[0] = "a";
            faces[1] = "b";
            faces[2] = "c";
            faces[3] = "d";
            faces[4] = "e";
            faces[6] = "f";

            roll();
        }

        /**
         * Constructor. Accepts a list of 6 strings to be assigned to the die faces.
         * If the list has more than 6 entries, only use the first 6. If the list has less,
         * throw an error.
         * @param diceFaces the list of strings to be used as faces
         */
        public Dice(List<String> diceFaces){
            if(diceFaces.size() < 6){
                //ERROR
            } else {
                for (int i = 0; i < NUMBER_OF_SIDES; i++) {
                    faces[i] = diceFaces.get(i);
                }
            }

            roll();
        }

        /**
         * Private method to determine which of a die's face is "visible", i.e. upright.
         * Called as a part of instantiating dice.
         */
        private void roll() {
            visibleFace = faces[random.nextInt(NUMBER_OF_SIDES)];
        }

        /**
         * Method to record a die's position on the boggle board. Called during BoggleBoard instantiation
         * @param x the die's x coordinate
         * @param y the die's y coordinate
         */
        public void setCoordinates(int x, int y){
            xCoordinate = x;
            yCoordinate = y;
        }

        /**
         * Method to "read" the visible face of the die
         * @return String representation of the die's face
         */
        public String getVisibleFace(){
            return this.visibleFace;
        }
    }
}