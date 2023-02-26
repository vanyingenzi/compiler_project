import compiler.Lexer.Symbol;
import compiler.Symbols.BooleanValue;
import compiler.Symbols.EOFSymbol;
import compiler.Symbols.Keyword;
import compiler.Symbols.StringValue;
import org.junit.Test;

import java.io.StringReader;
import compiler.Lexer.Lexer;

import static org.junit.Assert.*;

public class TestLexer {
    @Test
    public void test() {
        String input = "var x int = 2;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertNotNull(lexer.getNextSymbol());
    }
    @Test
    public void emptyCode() {
        String input = "";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Symbol symbol = lexer.getNextSymbol();
        assertNotNull(lexer.getNextSymbol());
        assertTrue(symbol instanceof EOFSymbol);
    }
    @Test
    public void testStrings(){
        // Strings are ignored by our lexer therefore the only symbol we should get is the EOFSymbol
        String[] comments = new String[]{
                "//This is a comment\n",
                "// Yet another \" Comment \t \n",
                "// Hello 123 $# \\ \t for // 23\n",
                "// This correct to since it's a program with just a string",
        };
        for (String input: comments) {
            StringReader reader = new StringReader(input);
            Lexer lexer = new Lexer(reader);
            Symbol symbol = lexer.getNextSymbol();
            assertNotNull(symbol);
            assertTrue(symbol instanceof EOFSymbol);
        }
    }
    @Test
    public void testKeyword(){
        String[] keywords = new String[]{
                "const","record", "var", "val", "proc", "for", "to", "by", "while", "if",
                "else", "return", "and", "or"
        };
        for (String input: keywords) {
            StringReader reader = new StringReader(input);
            Lexer lexer = new Lexer(reader);
            Symbol symbol = lexer.getNextSymbol();
            assertNotNull(symbol);
            assertTrue(symbol instanceof Keyword);
            assertEquals(input, symbol.getValue());
        }
        String[] notKeywords = new String[]{
                "constt","recordd", "varr", "valval", "procfor", "forto", "too", "bye", "whileif", "ifelse",
                "elsee", "returnn", "andif", "oror"
        };
        for (String input: notKeywords) {
            StringReader reader = new StringReader(input);
            Lexer lexer = new Lexer(reader);
            Symbol symbol = lexer.getNextSymbol();
            assertNotNull(symbol);
            assertFalse(symbol instanceof Keyword);
            assertEquals(input, symbol.getValue());
        }
    }
}
