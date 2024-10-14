import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
    //I imported these to help with scanning the users input of the file and to scan the file. Also to help with exceptions.
enum DecafSymbol {
        // Double character symbols
    LESS_EQUAL("<="), GREATER_EQUAL(">="), EQUAL_EQUAL("=="), NOT_EQUAL("!="),
    AND("&&"), OR("||"), SHIFT_LEFT("<<"), SHIFT_RIGHT(">>"),
// I created enum for the Symbols and labeled them with their appropriate names.
        // These symbols are first because they are to take priority above the single ones for example >= should not be split in two when being scanned.

    // Single character symbols
    OPEN_CURLY("{"), CLOSE_CURLY("}"), OPEN_SQUARE("["), CLOSE_SQUARE("]"),
    COMMA(","), SEMICOLON(";"), OPEN_PAREN("("), CLOSE_PAREN(")"),
    MINUS("-"), EQUAL("="), EXCLAMATION("!"), PLUS("+"),
    STAR("*"), SLASH("/"), LESS("<"), GREATER(">"),
    PERCENT("%"), DOT(".");
// These are the other Symbols that have lower priority than the ones above.
    private final String symbol;

    DecafSymbol(String symbol) {
        this.symbol = symbol;
    }
    public String getSymbol() {
        return symbol;
    }
}
enum DecafKeyword {
    CLASS, VOID, INT, BOOLEAN, IF, ELSE, WHILE, RETURN, TRUE, FALSE, BREAK, CONTINUE, FOR, PRINT;
// This enum is used to recognize the keywords.
    public static boolean isKeyword(String token) {
        for (DecafKeyword keyword : DecafKeyword.values()) {
            if (keyword.name().equalsIgnoreCase(token)) {
                return true;
            }
        }
        return false;
    }
    // THis method checks if they keyword found matches the Keywords in the enum and returns a boolean.
}
public class CustomScanner {
    private final String input;
    private int linenum = 1;
// I created a string input to take in the file name the user inputs, and a linenum to keep track of the line number.
    public CustomScanner(String input) {
        this.input = input;
    }
    public void scanAndRead() {
        // This is my method that scans the contents of the file and checks for keywords, chars, symbols and integers.
        try (BufferedReader reader = new BufferedReader(new FileReader(input))) {
            String line;
            while ((line = reader.readLine()) != null) {
                //I made the loop run as long as the file is not empty, if it is empty it will stop.
                int i = 0;
                // The int i is for traversing the line
                // This loop traverses the line character by character
                while (i < line.length()) {
                    // Check for string constants first, if there is a string constant that starts with " it will run this if statement.
                    if (line.charAt(i) == '"') {
                        int start = i; // This is to keep track of the column, this is the start of the column
                        i++; // Skip the opening quote
                        while (i < line.length() && line.charAt(i) != '"') {
                            i++;
                        }
                        if (i < line.length()) { // This if statement checks for the closing " and adds 1 to i to move past it.
                            i++; // Move past the closing quote
                        }
                        // I then created a new string to keep the line that is found inside the qoutations, so it can then be printed out.
                        String str = line.substring(start, i);
                        System.out.println(str + " line " + linenum + " Cols " + (start + 1) + "-" + i + " is STRING_CONSTANT (value= " + str +")");
                    }
                    // If no String constants are found then the code goes to this else statement which checks for Decaf symbols
                    else {
                        boolean foundSymbol = false;
                        //This loops through the symbols in order defined in the enum
                        for (DecafSymbol symbol : DecafSymbol.values()) {
                            //This checks if the current symbol from DecafSymbol can fit in the part of the line starting at index i.
                            // If it can it compares the substring from the line with the symbol to see if they match.
                            if (i + symbol.getSymbol().length() <= line.length() &&
                                    line.substring(i, i + symbol.getSymbol().length()).equals(symbol.getSymbol())) {
                                int start = i + 1;
                                int end = i + symbol.getSymbol().length();
                                //If a match is found the starting and ending positions of the symbols are stored and then it prints it out with the line column and name.
                                System.out.println(symbol.getSymbol() + " line " + linenum + " Cols " + start + "-" + end + " is '" + symbol.name() + "'");
                                i += symbol.getSymbol().length();
                                //This is to skip to the end of the symbol so the loop continues.
                                foundSymbol = true;
                                break;
                                //If the symbol is found the loop stops checking and sets foundsymbol to true to know that a match was found.
                            }
                        }
                        if (foundSymbol) continue;
                        //If a symbol is found it skips the rest of the else code.

                        // This if statement checks for identifiers and keywords
                        // If the Character at the line its scanning is true it goes on to the code.
                        if (Character.isLetter(line.charAt(i))) {
                            int start = i;
                            // For the starting point of the column
                            while (i < line.length() && Character.isLetterOrDigit(line.charAt(i))) {
                                //The loop continues as long as I is less than the length of the line so out of bounds wont occur,
                                // and if the character at i is a letter.
                                i++;
                                //Add 1 to i so it continues through the line
                            }
                            String token = line.substring(start, i);
                            //Putting the indentifier found into "token" with the start position and i
                            if (DecafKeyword.isKeyword(token)) {
                                System.out.println(token + " line " + linenum + " Cols " + (start + 1) + "-" + i + " T_" + token.toUpperCase());
                                //If the characters found is one of the keywords it prints it out with the name of the keyword, otherwise it prints it as an identifier.
                            } else {
                                System.out.println(token + " line " + linenum + " Cols " + (start + 1) + "-" + i + " is T_IDENTIFIER");
                            }
                        }
                        // This else if checks for integers
                        else if (Character.isDigit(line.charAt(i))) {
                            //If the character at line i is aan integer it continues.
                            int start = i;
                            while (i < line.length() && Character.isDigit(line.charAt(i))) {
                                i++;
                            }
                            String token = line.substring(start, i);
                            // It stores the integer found into a string and then it prints it out with the column, and line.
                            System.out.println(token + " line " + linenum + " Cols " + (start + 1) + "-" + i + " is T_IntConstant (value= " + token + ")");
                        }
                        // If no symbol, keyword, or identifier was found, it moves on to the next character
                        else {
                            i++;
                        }
                    }
                }
                // This is to go on to the next line.
                linenum++;
            }
        } catch (IOException e) {
            //This catch is an exception to catch any of the files not found, that is inputted by the user.
            System.out.println("FILE NOT FOUND");
        }
    }

    public static void main(String[] args) {
        // I prompted the user to enter a file name, a scanner scans the input and sends it to the CustomScanner, and then reads it out based on what is found.
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the filename: ");
        String filePath = scan.nextLine();
        CustomScanner custom = new CustomScanner(filePath);
        custom.scanAndRead();
    }
}