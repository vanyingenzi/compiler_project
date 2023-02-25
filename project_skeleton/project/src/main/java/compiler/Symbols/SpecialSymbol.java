package compiler.Symbols;

import compiler.Lexer.Symbol;

public abstract class SpecialSymbol extends Symbol {
    public static Symbol createSymbol(String string){
        return switch (string)
        {
            case "+", "-", "*", "/", "%", "==", "<>", "<", ">", "<=", ">=" -> new OperatorSpecialSymbol(string);
            case "(", ")", "{", "}", "[", "]", ".", ";", "," -> new SyntaxSpecialSymbol(string);
            default -> throw new UnsupportedOperationException("Got unexpected symbol : " + string);
        };
    }
}
