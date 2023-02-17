# COMP 360 Project
This project outlines the details needed for completion of project 1
in COMP 360. This README file also outlines the stpes that need to be taken 
to finish the project.

# Steps/Ideas
Major Areas:
 - Global Queue included
 - Input
   - Read in words, and special characters (“(“,”{“,etc)
     - regexp
 - Lexical Analyzer (Lexeme is actual string; Token is identifier)
 - Top-down Parser (Syntax Analyzer)

Input - COVERED
Lexical Analyzer
 - Return type is void
 - Using a queue (store the tokens)
 - Analyze the variables as they are parsed
 - Create tokens for punctuation
 - All outputs are terminal tokens
 - Needed Functions:
   - ident()

Syntax Analyzer
 - Syntax analyzer is basically “main”, returns boolean
 - At least one parameter in <formal_para_decl>
 - Look at book for analyzing lexemes right to left
 - Needed Functions:
   - formalParameterDecl(), declares(), assign(), expr(), 


Possible error handling
errorHandler(String s, char mode)
