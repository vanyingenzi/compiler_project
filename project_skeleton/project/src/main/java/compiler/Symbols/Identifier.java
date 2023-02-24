package compiler.Symbols;
import compiler.Lexer.Symbol;

public class Identifier extends Symbol {
    Identifier(String value){
        this.value = value;
    }
}