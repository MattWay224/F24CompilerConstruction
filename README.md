# F24CompilerConstruction
### Our team:
- **Matthew Rusakov:** Responsible for writing the compiler code and testing it based on previously written tests.
- **Aliia Bogapova:** Responsible for writing the compiler code and testing it based on previously written tests.
- **Polina Pushkareva:** Responsible for organizational aspects, presentations, and reports.

### Project Overview:
**Project-F:** 

The goal of this project is to develop an interpreter for a Lisp-like functional programming language. The project will be completed in several stages, starting with lexical analysis and progressing through syntax analysis, semantic analysis, and finally code interpretation.

**Technologies Used:** 

- *Language:* Java
- *Lexer:* Handwritten lexer
- *Parser:* Handwritten parser

**Project stages:**

The project will be implemented in four stages:

1. Lexical Analyzer (Lexer)
2. Syntax Analyzer (Parser)
3. Semantic Analyzer
4. Code Interpretation

### 1. *Lexical Analyser:*

**Purpose:**

The lexical analyzer (also called the "lexer" or "tokenizer") reads the input code and breaks it down into tokens. Tokens are the smallest units of meaning in the code, such as parentheses, operators, keywords, numbers, and literals. The lexical analyzer is responsible for recognizing these tokens and categorizing them into different types.

**Technical Details:**

Class: ```Flexer```

The ```Flexer``` class is responsible for implementing the lexical analyzer in Java. It takes the input source code, processes it character by character, and generates a sequence of tokens.

Inner Class: ```Token```

The ```Token``` class represents a single token, which contains:

- *type*: The type of the token (e.g., keyword, operator, literal, etc.).
- *value*: The actual content of the token (e.g., +, 42, true, etc.).

Ennumeration: ```TokenType```

We use an enumeration called ```TokenType``` to classify various token types. Some of the primary token types in our language are:

- *Parentheses*: Open ```(``` and close ```)```.
- *Keywords*: Reserved words in the language like ```func```, ```cond```, ```while```, ```lambda```, etc.
- *Operators*: Arithmetic and logical operators like ```+```, ```-```, ```*```, ```/```, ```and```, ```or```.
- *Literals*: Constants like numbers: integers and reals (```42```, ```3.14```) and booleans (```true```, ``false``).
- *Quoted Expressions*: Expressions starting with a single quote, such as ```'expr```.

Key Methods in Flexer:

- ```tokenize()```: This is the main method that takes the input string and processes it to generate a list of tokens. It handles the breakdown of the input code by analyzing each character and determining whether it forms part of a token.
- ```parseNumber()```: This method identifies and processes numeric values (integers and real numbers) in the input.
- ```parseIdOrKeyword()```: This method processes identifiers, which can be function names, function names, or keywords.
- *Error Handling*: The lexical analyzer also includes mechanisms for identifying and handling invalid tokens. If an unknown character or sequence is encountered, an error is thrown.

### 2. *Syntax Analyser*

Codebase Tree (will be written before 15.10)

### 3. *Semantic Analyser*

To be written after this stage is implemented.

### 4. *Code Interpretation*

To be written after this stage is implemented.

### Future Work

- *Semantic analysis*, which will check the correctness of the code in terms of types, scopes, and function definitions.
- *Code interpretation*, where the program will evaluate and run the functional language code.
