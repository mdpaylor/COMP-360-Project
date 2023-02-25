package comp360;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.regex.*;

public class LexInput {
	/**
	 * address of text file to be read in for analysis
	 */
	private static String fileName = "lextest.txt";
	/**
	 * Queue that stores the tokens to be read by the lexical analyzer
	 */
	private static Queue<String> tokens = new LinkedList<>();
	
	/**
	 * Parent function that appends newline characters to token
	 * @throws FileNotFoundException
	 */
	public static void readLexeme() throws FileNotFoundException {
		Scanner scnr = new Scanner(new FileInputStream(fileName));
		while(scnr.hasNextLine()) {
			readLine(scnr.nextLine());
			//eliminates \n at the end of the program
			if(scnr.hasNextLine())	tokens.add("new_line");
		}
		scnr.close();
	}
	/**
	 * Loads lexemes with each detected lexeme in the file. Ignores whitespace.
	 * @param a single line of the code
	 */
	public static void readLine(String line){
		Scanner scnr = new Scanner(line);
		String lex = null;
		
		//Regex for reserved tokens in the EBNF
		Pattern reserved = Pattern.compile("[{}(),;=+-]");
		Matcher m;
		boolean recurse = false;
		
		while(scnr.hasNext() || recurse) {
			//only scans the next word if the current word no longer needs to be re-reviewed
			if(!recurse) {
				lex = scnr.next();
			}
			
			m = reserved.matcher(lex);
			//if the word contains a reserved character (but isn't only that character)
			if(m.find() && lex.length() > 1) {
				//splits off the first lexeme and stores it in lexemes, then repeats as needed
				if(m.start()>0) {
					//nonreserved token is first
					lexAnalyze(lex.substring(0,m.start()));
					lex = lex.substring(m.start());
					recurse = true;
				} else {
					//reserved token is first
					lexAnalyze(lex.substring(m.start(),m.end()));
					lex = lex.substring(m.end());
					recurse = true;
				}

			} else {
				lexAnalyze(lex);
				recurse = false;
			}
		}
		scnr.close();
	}
	
	public static void lexAnalyze(String s) {
        // If statement compares all of the possible token types before defaulting to the "ident" token type.
        if (s.equals("int") || s.equals("void")) {
            tokens.add("keyword");
            return;
        }
        else if (s.equals(";")) {
            tokens.add("semi_colon");
            return;
        }
        else if (s.equals("{")) {
            tokens.add("l_brack");
            return;
        }
        else if (s.equals("}")) {
            tokens.add("r_brack");
            return;
        }
        else if (s.equals("(")) {
            tokens.add("l_par");
            return;
        }
        else if (s.equals(")")) {
            tokens.add("r_par");
            return;
        }
        else if (s.equals(",")) {
        	tokens.add("comma");
        	return;
        }
        else if (s.equals("+") || s.equals("-")) {
            tokens.add("op");
            return;
        }
        else if (s.equals("=")) {
            tokens.add("assign");
            return;
        }
        
        // Check the validity of the variable name, and if it is not valid, prints a message to the user
        // but still adds the "ident" token to the global queue to continue with the program.
        if (ident(s)) tokens.add("ident");
        else {
            System.out.println("Invalid variable name \""+ s +"\"");
            tokens.add("ident");
            return;
        }

    }

	public static boolean ident(String s) {
    	char ch;
    	int len = s.length();
    
    	// Parses the "s" parameter to check if each character is within the correct bounds for its ASCII encoding.
    	for (int i=0; i<len; i++) {
        	ch = s.charAt(i);
        
        // Checks the bounds of the ASCII encoding of the current value of "ch", the character being analyzed.
        	if (ch < 97 || ch > 123) return false;
    	}
    	return true;
	}
	
	public static void main(String[] args) {
		try {
			readLexeme();
			while(tokens.peek() != null) {
				System.out.println(tokens.poll());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
