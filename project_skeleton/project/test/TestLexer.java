import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import compiler.Lexer.Symbol;
import compiler.Symbols.BooleanValue;
import compiler.Symbols.EOFSymbol;
import compiler.Symbols.StringValue;
import org.junit.Test;

import java.io.StringReader;
import compiler.Lexer.Lexer;

public class TestLexer {
    @Test
    public void test() {
        String input = "var x int = 2;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertNotNull(lexer.getNextSymbol());
    }
    @Test
    public void testStrings(){
        String input ="//This is a comment";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Symbol symbol = lexer.getNextSymbol();
        assertNotNull(symbol);
        System.out.println(symbol.getClass());
        assertTrue(symbol instanceof EOFSymbol);
    }
}
