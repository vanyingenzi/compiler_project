import compiler.Lexer.Symbol;
import compiler.Symbols.*;
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
        String[] emptyStrings = new String[]{
                " ", "", "\t\t  \t"
        };
        for (String emptyString: emptyStrings) {
            StringReader reader = new StringReader(emptyString);
            Lexer lexer = new Lexer(reader);
            Symbol symbol = lexer.getNextSymbol();
            assertNotNull(lexer.getNextSymbol());
            assertTrue(symbol instanceof EOFSymbol);
        }
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
    @Test
    public void testIdentifiers(){
        // Strings are ignored by our lexer therefore the only symbol we should get is the EOFSymbol
        String[] identifiers = new String[]{
                "_id", "_", "iam_an_identifier", "_another1", "_111_", "id1", "__"
        };
        for (String input: identifiers) {
            StringReader reader = new StringReader(input);
            Lexer lexer = new Lexer(reader);
            Symbol symbol = lexer.getNextSymbol();
            assertNotNull(symbol);
            assertTrue(symbol instanceof Identifier);
            assertEquals(input, symbol.getValue());
        }
        String[] notIdentifiers = new String[]{
                "1_id", "11_", "1eer"
        };
        for (String input: notIdentifiers) {
            StringReader reader = new StringReader(input);
            Lexer lexer = new Lexer(reader);
            Symbol symbol = lexer.getNextSymbol();
            assertNotNull(symbol);
            assertFalse(symbol instanceof Identifier);
        }
    }
    @Test
    public void testNaturalNumbers(){
        // Strings are ignored by our lexer therefore the only symbol we should get is the EOFSymbol
        String[] naturalNumber = new String[]{
                "1233", "0", "01234567890", "111111", "22222", "1211212", "99876612"
        };
        for (String input: naturalNumber) {
            StringReader reader = new StringReader(input);
            Lexer lexer = new Lexer(reader);
            Symbol symbol = lexer.getNextSymbol();
            assertNotNull(symbol);
            assertTrue(symbol instanceof NaturalNumberValue);
            assertEquals(Integer.parseInt(input), symbol.getValue());
        }
        String[] notNaturalNumber = new String[]{
                "e123", "(11", ".01", "po1", "lo11"
        };
        for (String input: notNaturalNumber) {
            StringReader reader = new StringReader(input);
            Lexer lexer = new Lexer(reader);
            Symbol symbol = lexer.getNextSymbol();
            assertNotNull(symbol);
            System.out.println(symbol.getClass());
            assertFalse(symbol instanceof NaturalNumberValue);
        }
    }
    @Test
    public void testSimpleLanguage() {
        String input = "var x int";
        Symbol[] expected = new Symbol[]{
                new Keyword("var"),
                new Identifier("x"),
                new Identifier("int"),
        };
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        for (Symbol value : expected) {
            Symbol symbol = lexer.getNextSymbol();
            assertNotNull(symbol);
            assertEquals(symbol, value);
        }
    }
}
