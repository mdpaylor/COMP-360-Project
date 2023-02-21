/**
     * Implements the functionality of a lexical analyzer. The method analyzes the string "s" passed in and
     * classifies which type of token the lexeme is.
     * 
     * The types are:
     *  	"{" - l_brack
     *  	"}" - r_brack
     *  	"(" - l_par
     *  	")" - r_par
     *  	"int" - keyword
     *  	"void" - keyword
     *  	"," - comma
     *  	";" - semi_colon
     *  	"+" - op
     *  	"-" - op
     *  	"=" - assign
     *  	Variable Name - ident
     * 
     * @param s
     * 		A lexeme that is passed into the method in need of analysis.
     */
    public static void lex(String s) {
        
        // If statement compares all of the possible token types before defaulting to the "ident" token type.
        if (s.equals("int") || s.equals("void")) {
            q.add("keyword");
            return;
        }
        else if (s.equals(";")) {
            q.add("semi_colon");
            return;
        }
        else if (s.equals("{")) {
            q.add("l_brack");
            return;
        }
        else if (s.equals("}")) {
            q.add("r_brack");
            return;
        }
        else if (s.equals("(")) {
            q.add("l_par");
            return;
        }
        else if (s.equals(")")) {
            q.add("r_par");
            return;
        }
        else if (s.equals(",")) {
        	q.add("comma");
        	return;
        }
        else if (s.equals("+") || s.equals("-")) {
            q.add("op");
            return;
        }
        else if (s.equals("=")) {
            q.add("assign");
            return;
        }
        
        // Check the validity of the variable name, and if it is not valid, prints a message to the user
        // but still adds the "ident" token to the global queue to continue with the program.
        if (ident(s)) q.add("ident");
        else {
            System.out.println("Invalid variable name \""+ s +"\"");
            q.add("ident");
            return;
        }

    }
    
    /**
     * Method identifies whether a given string is a valid variable name, according to the grammar given. It does
     * this by check the ASCII value of each individual character within the string. The lowest and highest
     * lower-case character ASCII encoding is 98 and 122 respectively.
     * 
     * @param s
     * 		The string that will be analyzed to see if it is a valid variable name.
     * @return
     * 		A boolean value is returned. This value is the result of the analysis of the parameter "s". If the 
     * 		string is a valid variable name, the method will return true; otherwise it will return false.
     */
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
