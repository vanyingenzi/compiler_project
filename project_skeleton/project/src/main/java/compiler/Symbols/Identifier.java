package compiler.Symbols;
import compiler.Lexer.Symbol;

public class Identifier extends Symbol {
    public Identifier(String value){
        this.value = value;
    }
}