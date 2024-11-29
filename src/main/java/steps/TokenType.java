package steps;

public enum TokenType {
    LPAREN,//left parenthesis
    RPAREN,//right parenthesis
    SETQ,
    FUNC,
    LAMBDA,
    PROG,
    COND,
    WHILE,
    RETURN,
    BREAK,
    PLUS,
    MINUS,
    TIMES,
    DIVIDE,
    INTEGER,//literal
    REAL,//literal
    BOOLEAN,//literal
    NULL,
    ATOM,
    HEAD,
    TAIL,
    CONS,
    EQUAL,
    NONEQUAL,
    LESS,
    LESSEQ,
    GREATER,
    GREATEREQ,
    ISINT,
    ISREAL,
    ISBOOL,
    ISNULL,
    ISATOM,
    ISLIST,
    AND,
    OR,
    XOR,
    NOT,
    EVAL,
    QUOTE, //NO NEED TO EVALUATE
    EOF,//end of file
}
