import java.util.*;

public class Syntax {
	
	// Global Queue holding all of the tokens for the lexemes.
	public static Queue<String> q;
	// Global variable responsible for keeping track of the syntactic 
	// integrity of the input program.
	public static boolean PASS = true;
	
	public static void main(String[] args) {
		q = new LinkedList<String>();
		q.add("keyword");
		q.add("ident");
		q.add("new_line");
		q.add("l_par");
		q.add("keyword");
		q.add("ident");
		q.add("ident");
		q.add("comma");
		q.add("keyword");
		q.add("ident");
		q.add("r_par");
		q.add("op");
		q.add("l_brack");
		q.add("keyword");
		q.add("ident");
		q.add("semi_colon");
		q.add("ident");
		q.add("assign");
		q.add("ident");
		q.add("op");
		q.add("ident");
		q.add("op");
		q.add("ident");
		q.add("r_brack");
		q.add("semi_colon");
		q.add("r_brack");
		syntax();
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
			token = q.poll();
			
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
					if (q.peek().equals("keyword")) {
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
					if (!q.isEmpty() && q.peek().equals("ident")) {
						needsIdent = true;
					}
				}
			}
			else {
				System.out.println("Invalid token \""+ token +"\". Needs \"(\"");
			}
		}
		q.poll();
		
		// If there was an unexpected error, l_par should still be removed from the queue.
		if (!q.isEmpty() && q.peek().equals("l_par")) q.poll();
		
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
		if (!q.isEmpty()) {
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
		if (q.isEmpty()) {
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
			token = q.poll();
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
					if (!q.isEmpty() && q.peek().equals("keyword")) {
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
					if (!q.isEmpty() && q.peek().equals("ident")) {
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
					if (!q.isEmpty() && q.peek().equals("comma")) {
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
		q.poll();
	}
	
	/**
	 * Covers the declares non-terminal to declare a type for a variable. This only
	 * supports the declaration of one variable, according to the grammar.
	 */
	public static void declares() {
		if (q.isEmpty()) {
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
			token = q.poll();
			
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
					if (!q.isEmpty() && q.peek().equals("keyword")) {
						needsKeyword = true;
						needsIdent = false;
					}
					System.out.println("Invalid token type: \""+ token +"\". Needs \"int | void\"");
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
					if (!q.isEmpty() && q.peek().equals("ident")) {
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
		q.poll();
		if (!q.isEmpty() && q.peek().equals("new_line")) q.poll();
	}
	
	/**
	 * Handles the assign nonterminal which leads into the expr non-terminal.
	 * the expr() method is not called in this method for readability purposes, but
	 * it is listed below the function calls for the other non-terminal methods.
	 */
	public static void assign() {
		if (q.isEmpty()) {
			PASS = false;
			return;
		}
		
		boolean needsIdent = true;
		
		String token;
		
		// Checks if there is a keyword, ident, and semi_colon token in that order.
		while (checkIfCond("assign")) {
			token = q.poll();
			
			// Ensure that the program has an ident at this stage.
			if (needsIdent) {
				needsIdent = false;
				
				// Checks if the token is an ident.
				if (token.equals("ident")) continue;
				else {
					// Looks to see if the program should stay in the same mode.
					if (q.peek().equals("ident")){
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
		if (q.peek().equals("assign")) {
			q.poll();
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
		if (q.isEmpty()) {
			PASS = false;
			return;
		}
		
		boolean needsIdent = true;
		boolean needsOp = false;
		boolean needsSecondIdent = true;
		
		String token;
		
		// Iterates until the end of the line is found, or a semicolon.
		while (checkIfCond("semi_colon")) {
			token = q.poll();
			
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
					if (q.peek().equals("ident")) {
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
					if (q.peek().equals("op")) {
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
					if (q.peek().equals("ident")) {
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
		
		q.poll();
		
		// Ensures the queue is empty before the program attempts to check the value of the
		// value on the stack.
		if (!q.isEmpty() && q.peek().equals("new_line")) q.poll();
		
	}
	
	/**
	 * Method finds the left bracket on the line where the parameters are declared. This 
	 * method is called directly after the formal_para_decl() method to find where the program
	 * is supposed to start.
	 */
	public static void findLBrack() {
		if (q.isEmpty()) {
			PASS = false;
			return;
		}
		
		// Continues to iterate through the queue printing error messages until a left bracket
		// is found.
		while (checkIfCond("l_brack")) {
			System.out.println("Invalid token type: \""+ q.poll() +"\". Needs \"{\"");
			PASS = false;
		}
		q.poll();
	}
	
	/**
	 * Method finds the right bracket in the program. This is the bracket that signifies the
	 * end of all statements in the program. This is called after the expr() method.
	 */
	public static void findRBrack() {
		if (q.isEmpty()) {
			PASS = false;
			return;
		}
		
		// Continues to iterate through the queue printing error messages until a right bracket
		// is found.
		while (checkIfCond("r_brack")) {
			System.out.println("invlaid token type: \""+ q.poll() +"\". Needs \"}\"");
			PASS = false;
		}
		
		if (!q.isEmpty()) q.poll();
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
		if (!q.isEmpty() && !q.peek().equals(token) && !q.peek().equals("new_line") 
				&& !q.peek().equals("semi_colon")) {
			return true;
		}
		
		return false;
	}
}