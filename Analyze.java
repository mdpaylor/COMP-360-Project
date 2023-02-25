import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.*;

/**
 * 
 * @author Michael Paylor, Adam Abram
 * @Class COMP 360-001
 * @Date 25 February 2023
 * 
 * @Description
 * 		Implements a lexical and syntax analyzer for a custom grammar.
 */


public class Analyze {
	
	/**
	 * Queue that stores the tokens to be read by the lexical analyzer
	 */
	private static Queue<String> tokens = new LinkedList<>();

	// Global variable responsible for keeping track of the syntactic 
	// integrity of the input program.
	public static boolean PASS = true;
	
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		
		System.out.println("Enter the path to the file:");
		String path = s.nextLine();
		System.out.println();
		
		s.close();
		try {
			readLexeme(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//while (!tokens.isEmpty()) System.out.println(tokens.poll());
		
		syntax();

	}
	
	/**
	 * Parent function that appends newline characters to token
	 * @throws FileNotFoundException
	 */
	public static void readLexeme(String fileName) throws FileNotFoundException {
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
        else if (s.equals("*")) {
        	tokens.add("op");
        	System.out.println("Invalid operand \"*\"");
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
		
		/**
		 * Method handles all of the syntax analysis operations involved in the program. The method
		 * will manipulate the PASS variable mentioned above if there is a syntax error in the input
		 * program.
		 */
		public static void syntax() {
			boolean needsKeyword = true;
			boolean needsIdent = false;
			String token;
			
			/* Loop structure iterates over the string until the parameter section is found.
			 * It looks for a keyword and ident token in that respective order for the function 
			 * declaration.
			 */
			while (checkIfCond("l_par")) {
				token = tokens.poll();
				
				// Checks if the program needs a keyword next. If so it checks the token value.
				if (needsKeyword) {
					needsKeyword = false;
					needsIdent = true;
					
					// If token equals keyword, the program moves onto the next mode. Otherwise, an
					// error message is displayed and the PASS variable changed to false;
					if (token.equals("keyword")) continue;
					else {
						System.out.println("Invalid token \""+ token +"\". Needs \"int | void\"");
						PASS = false;
						
						// Checks if the program should be kept in the same mode. This is determined
						// by checking the next value and seeing if it matches up with the one needed
						// currently.
						if (tokens.peek().equals("keyword")) {
							needsKeyword = true;
							needsIdent = false;
						}
					}
				}
				else if (needsIdent){
					needsIdent = false;
					if (token.equals("ident")) continue;
					else {
						System.out.println("Invalid token \""+ token +"\". Needs \"ident\"");
						PASS = false;
						if (!tokens.isEmpty() && tokens.peek().equals("ident")) {
							needsIdent = true;
						}
					}
				}
				else {
					System.out.println("Invalid token \""+ token +"\". Needs \"(\"");
				}
			}
			tokens.poll();
			
			// If there was an unexpected error, l_par should still be removed from the queue.
			if (!tokens.isEmpty() && tokens.peek().equals("l_par")) tokens.poll();
			
			// Different function calls for the unique nonterminals plus 
			// two additional helper methods.
			formal_para_decl();
			
			findLBrack();
			
			declares();
			assign();
			expr();
			
			findRBrack();
			
			// When all of the above methods are finished, if the program is valid, the
			// queue should be empty. If not, there was an error somewhere in the input code.
			if (!tokens.isEmpty()) {
				PASS = false;
				System.out.println("All statements must be within the method");
			}
			
			// Displays the results of the analysis to the user.
			if (PASS) System.out.println("Program is valid!");
			else System.out.println("\nProgram is Invalid!");
		}
		
		/**
		 * Handles the parameter declaration non-terminal. This includes the declaration of
		 * or more parameters.
		 */
		public static void formal_para_decl() {
			if (tokens.isEmpty()) {
				PASS = false;
				return;
			}
			
			boolean needsKeyword = true;
			boolean needsIdent = false;
			boolean needsComma = false;
			
			// Since there must be at least one parameter, this is only changed to false when
			// there is a sign of at least one parameter given.
			boolean needsParameter = true;
			String token;
			
			/*
			 * Structure iterates through the queue until a right parenthesis is found, which
			 * is then removed to move onto the next section. This section just ensures the 
			 * syntactic integrity of the parameter section of the input code.
			 */
			while (checkIfCond("r_par")) {
				token = tokens.poll();
				needsParameter = false;
				
				// Enters when the program needs a keyword.
				if (needsKeyword && !needsIdent && !needsComma) {
					needsKeyword = false;
					needsIdent = true;
					
					// If the token variable is a "keyword", this part is valid, then the program
					// moves onto the next token. Otherwise an error message is printed and the PASS
					// variable is updated.
					if (token.equals("keyword")) continue;
					else {
						PASS = false;
						
						// Checks if the next value is the token required. If so, the program stays in
						// the current mode.
						if (!tokens.isEmpty() && tokens.peek().equals("keyword")) {
							needsKeyword = true;
							needsIdent = false;
						}
						System.out.println("Invalid token type: \""+ token +"\". Needs \"int | void\"");
					}
				}
				else if (!needsKeyword && needsIdent && !needsComma) {
					needsIdent = false;
					needsComma = true;
					
					// Checks for an ident token. If not, the error is handled.
					if (token.equals("ident")) continue;
					else {
						PASS = false;
						
						// If the next value is matched to the current required value, the
						// appropriate steps are taken.
						if (!tokens.isEmpty() && tokens.peek().equals("ident")) {
							needsIdent = true;
							needsComma = false;
						}
						System.out.println("Invalid token type: \""+ token +"\". Needs \"ident\"");
					}
				}
				else {
					needsComma = false;
					needsKeyword = true;
					
					// Checks for a comma and takes the appropriate steps. 
					if (token.equals("comma")) continue;
					else {
						PASS = false;
						
						// Checks if the program should look for the same keyword.
						if (!tokens.isEmpty() && tokens.peek().equals("comma")) {
							needsComma = true;
							needsKeyword = false;
						}
						System.out.println("Invalid token type: \""+ token +"\". Needs \"comma\"");
					}
				}
			}
			
			// If no parameter has been passed the program is invalid.
			if (needsParameter) {
				PASS = false;
				System.out.println("Method needs at least one parameter");
			}
			tokens.poll();
		}
		
		/**
		 * Covers the declares non-terminal to declare a type for a variable. This only
		 * supports the declaration of one variable, according to the grammar.
		 */
		public static void declares() {
			if (tokens.peek().equals("new_line")) tokens.poll();
			if (tokens.isEmpty()) {
				PASS = false;
				return;
			}
			boolean needsKeyword = true;
			boolean needsIdent = false;
			
			// Ensures that there is at least one declaration statement.
			boolean needsDeclares = true;
			
			String token;
			
			// Iterates through the queue until a new line is found or a semi_colon. 
			while (checkIfCond("semi_colon")) {
				token = tokens.poll();
				
				// If the program needs a keyword, the appropriate steps are taken.
				if (needsKeyword && !needsIdent) {
					needsKeyword = false;
					needsIdent = true;
					
					// If the value matches, the program continues onto the next mode.
					// Otherwise, the error is handled.
					if (token.equals("keyword")) continue;
					else {
						PASS = false;
			
						// If the next value is the current required value, the program
						// continues to look for the required token.
						if (!tokens.isEmpty() && tokens.peek().equals("keyword")) {
							needsKeyword = true;
							needsIdent = false;
						}
						System.out.println("Invalid token type: \""+ token +"\". Needs \"int | void\"");
						//preQueue(token);
						return;
					}
				}
				else if (!needsKeyword && needsIdent) {
					needsIdent = false;
					
					// Looks for an ident token and the appropriate statements are taken
					// based on the value of token.
					if (token.equals("ident")) {
						needsDeclares = false;
						continue;
					}
					else {
						PASS = false;
						
						// If the next value is the required token, the program stays in the same mode.
						if (!tokens.isEmpty() && tokens.peek().equals("ident")) {
							needsIdent = true;
						}
						System.out.println("Invalid token type: \""+ token +"\". Needs \"ident\"");
					}
				}
				else {
					PASS = false;
					
					// If there is no semi_colon before a new_line character is met, there is a syntax
					// error and the program is invalid. Also if another value is entered besides a 
					// semi_colon, the program is invalid. 
					if (token.equals("new_line")) {
						System.out.println("Token missing for end line: \"semi_colon\"");
						break;
					}
					else {
						System.out.println("Invalid token type: \""+ token +"\". Needs \";\"");
					}
				}
			}
			
			// Since the program needs at least one declaration statement, if there was one,
			// the needsDeclares variable would be false and not set the PASS variable to false.
			if (needsDeclares) {
				System.out.println("Program must have a declaration statement.");
				PASS = false;
			}
			tokens.poll();
			if (!tokens.isEmpty() && tokens.peek().equals("new_line")) tokens.poll();
		}
		
		/**
		 * Handles the assign nonterminal which leads into the expr non-terminal.
		 * the expr() method is not called in this method for readability purposes, but
		 * it is listed below the function calls for the other non-terminal methods.
		 */
		public static void assign() {
			if (tokens.isEmpty()) {
				PASS = false;
				return;
			}
			
			boolean needsIdent = true;
			
			String token;
			
			// Checks if there is a keyword, ident, and semi_colon token in that order.
			while (checkIfCond("assign")) {
				token = tokens.poll();
				
				// Ensure that the program has an ident at this stage.
				if (needsIdent) {
					needsIdent = false;
					
					// Checks if the token is an ident.
					if (token.equals("ident")) continue;
					else {
						// Looks to see if the program should stay in the same mode.
						if (!tokens.isEmpty() && tokens.peek().equals("ident")){
							needsIdent = true;
						}
						System.out.println("Invalid token type: \""+ token +"\". Needs \"ident\"");
					}
				}
				else {
					PASS = false;
					System.out.println("Invalid token type: \""+ token +"\". Needs \"=\"");
				}
			}
			
			// Checks to see if the assign token is present. If so, it is removed from the 
			// queue and the program returns. Otherwise, there is an error.
			if (!tokens.isEmpty() && tokens.peek().equals("assign")) {
				tokens.poll();
				return;
			}
			else {
				PASS = false;
				return;
			}
		}
		
		/**
		 * Handles the expr non-terminal. For this to be valid there must be at least
		 * an ident, op, ident, and semi_colon in that order. More operations can be added,
		 * but there must be an op then an ident in between the last ident and the semi_colon. 
		 */
		public static void expr() {
			if (tokens.isEmpty()) {
				PASS = false;
				return;
			}
			
			boolean needsIdent = true;
			boolean needsOp = false;
			boolean needsSecondIdent = true;
			
			String token;
			
			// Iterates until the end of the line is found, or a semicolon.
			while (checkIfCond("semi_colon")) {
				token = tokens.poll();
				
				// Enters the ident mode to find an ident in the queue.
				if (needsIdent && !needsOp) {
					needsIdent = false;
					needsOp = true;
					
					// If the token is an ident, the next mode is moved to; otherwise the error
					// is handled.
					if (token.equals("ident")) continue;
					else {
						PASS = false;
						
						// If the next value is the current required value, the program stays in
						// the same mode.
						if (!tokens.isEmpty() && tokens.peek().equals("ident")) {
							needsIdent = true;
							needsOp = false;
						}
						System.out.println("Invalid token type: \""+ token +"\". Needs \"ident\"");
					}
				}
				else if (!needsIdent && needsOp) {
					needsOp = false;
					if (token.equals("op")) continue;
					else {
						PASS = false;
						
						// If the next value is the current required value, the program stays in
						// the same mode.
						if (!tokens.isEmpty() && tokens.peek().equals("op")) {
							needsOp = true;
						}
						System.out.println("invlaid token type: \""+ token +"\". Needs \"+ | -\"");
					}
				}
				else if (!needsIdent && !needsOp && needsSecondIdent) {
					needsSecondIdent = false;
					if (token.equals("ident")) continue;
					else {
						PASS = false;
						
						// If the next value is the current required value, the program stays in
						// the same mode.
						if (!tokens.isEmpty() && tokens.peek().equals("ident")) {
							needsSecondIdent = true;
						}
						System.out.println("invlaid token type: \""+ token +"\". Needs \"ident\"");
					}
				}
				else {
					
					// If this condition is met, the program backs up a mode to look for another ident.
					// This is because another op was entered and must be followed by an ident then a semicolon.
					if (token.equals("op")) {
						needsSecondIdent = true;
					}
					else {
						PASS = false;
						System.out.println("invlaid token type: \""+ token +"\". Needs \"+ | - | ;\"");
					}
				}
			}
			
			tokens.poll();
			
			// Ensures the queue is empty before the program attempts to check the value of the
			// value on the stack.
			if (!tokens.isEmpty() && tokens.peek().equals("new_line")) tokens.poll();
			
		}
		
		/**
		 * Method finds the left bracket on the line where the parameters are declared. This 
		 * method is called directly after the formal_para_decl() method to find where the program
		 * is supposed to start.
		 */
		public static void findLBrack() {
			if (tokens.isEmpty()) {
				PASS = false;
				return;
			}
			
			// Continues to iterate through the queue printing error messages until a left bracket
			// is found.
			while (checkIfCond("l_brack")) {
				System.out.println("Invalid token type: \""+ tokens.poll() +"\". Needs \"{\"");
				PASS = false;
			}
			tokens.poll();
		}
		
		/**
		 * Method finds the right bracket in the program. This is the bracket that signifies the
		 * end of all statements in the program. This is called after the expr() method.
		 */
		public static void findRBrack() {
			if (tokens.isEmpty()) {
				PASS = false;
				return;
			}
			
			// Continues to iterate through the queue printing error messages until a right bracket
			// is found.
			while (checkIfCond("r_brack")) {
				System.out.println("invlaid token type: \""+ tokens.poll() +"\". Needs \"}\"");
				PASS = false;
			}
			
			if (!tokens.isEmpty()) tokens.poll();
			return;
		}
		
		/**
		 * Method serves as a general condition for the majority of the while loops in the program.
		 * This is to improve readability of code and reduce the size of one line of code.
		 * 
		 * @param token
		 * 		Parameter is used for personalization amongst different while loops. It is the
		 * 		token a particular while loop is looking for. 
		 * @return
		 * 		Returns the result of the if statement. 
		 */
		private static boolean checkIfCond(String token) {
			
			// Statement checks if the queue is empty, target token is not found, new line is not found,
			// and semi_colon is not found.
			if (!tokens.isEmpty() && !tokens.peek().equals(token) && !tokens.peek().equals("new_line") 
					&& !tokens.peek().equals("semi_colon")) {
				return true;
			}
			
			return false;
		}
		
		private static void preQueue(String token) {
			Queue<String> tempTokens = new LinkedList<>();
			tempTokens.add(token);
			
			while (!tokens.isEmpty()) {
				tempTokens.add(tokens.poll());
			}
			
			while (!tempTokens.isEmpty()) {
				tokens.add(tempTokens.poll());
			}
			
			
		}
}
