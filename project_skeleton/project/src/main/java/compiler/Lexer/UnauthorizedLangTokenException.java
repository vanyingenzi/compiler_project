package compiler.Lexer;

/**
 * A class representing a RuntimeException this is when we encounter an unknown symbol in our language
 */
public class UnauthorizedLangTokenException extends RuntimeException{
    public UnauthorizedLangTokenException(String message){
        super(message);
    }
}
