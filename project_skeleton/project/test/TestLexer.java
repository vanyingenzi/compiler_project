import compiler.Lexer.Symbol;
import compiler.Lexer.UnauthorizedLangTokenException;
import compiler.Symbols.*;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;

import compiler.Lexer.Lexer;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Suite;


import static org.junit.Assert.*;


public class TestLexer {
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    @Test
    public void testKeyword_simpleCase() {
        String[] keywords = Keyword.getKeywords();
        for (String input : keywords) {
            StringReader reader = new StringReader(input);
            Lexer lexer = new Lexer(reader);
            try {
                Symbol symbol = lexer.getNextSymbol();
                assertEquals(new Keyword(input), symbol);
            } catch (Exception e){
                fail("Exception was thrown: "+ e);
            }
        }
    }

    @Test
    public void testKeyword_notKeywordsCase(){
        String[] notKeywords = new String[]{
                "constt","recordd", "varr", "valval", "procfor", "forto", "too", "bye", "whileif", "ifelse",
                "elsee", "returnn", "andif", "oror", "_var", "const_", "\"records\""
        };
        for (String input: notKeywords) {
            StringReader reader = new StringReader(input);
            Lexer lexer = new Lexer(reader);
            try {
                Symbol symbol = lexer.getNextSymbol();
                assertNotNull(symbol);
                assertFalse(symbol instanceof Keyword);
            } catch (Exception e){
                fail("Exception was thrown: "+ e);
            }
        }
    }

    @Test
    public void testBooleanValue_simpleCase(){
        String[] booleanValues = BooleanValue.getBooleanValues();
        for (String input : booleanValues) {
            StringReader reader = new StringReader(input);
            Lexer lexer = new Lexer(reader);
            try{
                Symbol symbol = lexer.getNextSymbol();
                assertEquals(new BooleanValue(input), symbol);
            } catch (Exception e){
                fail("Exception was thrown: "+ e);
            }
        }
    }

    @Test
    public void testBooleanValue_notBooleanValueCase(){
        String[] notBooleanValues = new String[]{
                "ffalse", "truee", "\"true\"", "_true", "false_"
        };
        for (String input : notBooleanValues){
            StringReader reader = new StringReader(input);
            Lexer lexer = new Lexer(reader);
            try{
                Symbol symbol = lexer.getNextSymbol();
                assertNotNull(symbol);
                assertFalse(symbol instanceof BooleanValue);
            } catch (Exception e){
                fail("Exception was thrown: "+ e);
            }
        }
    }

    @Test
    public void testIdentifier_simpleCase(){
        String[] identifiers = new String[]{
                "_underscoreStart", "underscoreEnd_", "underscore_Middle", "numberEnd42", "number69Middle", "_", "_42",
                "_42_69_", "simplecase",
        };
        for (String input : identifiers){
            StringReader reader = new StringReader(input);
            Lexer lexer = new Lexer(reader);
            try{
                Symbol symbol = lexer.getNextSymbol();
                assertEquals(new Identifier(input), symbol);
            } catch (Exception e){
                fail("Exception was thrown: "+ e);
            }
        }
    }

    @Test
    public void testIdentifier_notIdentifier(){
        String[] notIdentifiers = new String[]{
                "432",
                "\"aString\"",
                "=",
                "689_startwithnumber"
        };
        for (String input : notIdentifiers) {
            StringReader reader = new StringReader(input);
            Lexer lexer = new Lexer(reader);
            try{
                Symbol symbol = lexer.getNextSymbol();
                assertNotNull(symbol);
                assertFalse(symbol instanceof Identifier);
            } catch (Exception e){
                fail("Exception was thrown: "+ e);
            }
        }
    }

    @Test
    public void testNaturalNumberValue_simpleCase(){
        String[] naturalNumberValues = new String[]{
                "123",
                "0",
                "1234."
        };
        String[] expectedValues = new String[]{
                "123",
                "0",
                "1234"
        };
        for (int i = 0; i < naturalNumberValues.length; i++){
            StringReader reader = new StringReader(naturalNumberValues[i]);
            Lexer lexer = new Lexer(reader);
            try{
                Symbol symbol = lexer.getNextSymbol();
                assertEquals(new NaturalNumberValue(expectedValues[i]), symbol);
            } catch (Exception e){
                fail("Exception was thrown: "+ e);
            }
        }
    }

    @Test
    public void testNaturalNumberValue_notNaturalNumberValue(){
        String[] notNaturalNumberValues = new String[]{
                "_13", "\"13\"", "321.091"
        };
        for (String input : notNaturalNumberValues) {
            StringReader reader = new StringReader(input);
            Lexer lexer = new Lexer(reader);
            try{
                Symbol symbol = lexer.getNextSymbol();
                assertNotNull(symbol);
                assertFalse(symbol instanceof NaturalNumberValue);
            } catch (Exception e){
                fail("Exception was thrown: "+ e);
            }
        }
    }

    @Test
    public void testRealNumberValue_simpleCase(){
        String[] realNumberValues = new String[]{
                "123.0", "0.0", "12348.5874"
        };
        for (String input : realNumberValues){
            StringReader reader = new StringReader(input);
            Lexer lexer = new Lexer(reader);
            try{
                Symbol symbol = lexer.getNextSymbol();
                assertEquals(new RealNumberValue(input), symbol);
            } catch (Exception e){
                fail("Exception was thrown: "+ e);
            }
        }
    }

    @Test
    public void testRealNumberValue_notRealNumberValue(){
        String[] notRealNumberValues = new String[]{
                "_13.0", "\"13.0\"", "321.", ".1548"
        };
        for (String input : notRealNumberValues) {
            StringReader reader = new StringReader(input);
            Lexer lexer = new Lexer(reader);
            try{
                Symbol symbol = lexer.getNextSymbol();
                assertNotNull(symbol);
                assertFalse(symbol instanceof RealNumberValue);
            } catch (Exception e){
                fail("Exception was thrown: "+ e);
            }
        }
    }

    @Test
    public void testStringValue_simpleCase(){
        String[] stringValues = new String[]{
                "\"Test 0f my string\"",
                "\"Test of my string with backslash \\\\\"",
                "\"\"",
                "\"Complex string with tab \\t and returns \\n as you can see even backslash \\\\ and mark quotes \\\" are present.\""
        };
        String[] expectedStringValues = new String[]{
                "Test 0f my string",
                "Test of my string with backslash \\",
                "",
                "Complex string with tab \t and returns \n as you can see even backslash \\ and mark quotes \" are present."
        };
        for (int i = 0; i < stringValues.length; i++){
            StringReader reader = new StringReader(stringValues[i]);
            Lexer lexer = new Lexer(reader);
            try{
                Symbol symbol = lexer.getNextSymbol();
                assertEquals(new StringValue(expectedStringValues[i]), symbol);
            } catch (Exception e) {
                fail("Exception was thrown: " + e);
            }
        }
    }

    @Test
    public void testStringValue_notStringValue(){
        String[] stringValues = new String[]{
                "\"Test of my string",
                "\"Test of my string with backslash \\\"",
        };
        for (String input : stringValues){
            StringReader reader = new StringReader(input);
            Lexer lexer = new Lexer(reader);
            try {
                lexer.getNextSymbol();
                fail("Should have throw an UnauthorizedLangTokenException.");
            } catch (IOException ioException) {
                fail("IOException was thrown: " + ioException);
            } catch (UnauthorizedLangTokenException ignored){
                // Nothing
            }
        }
    }

    @Test
    public void testSpecialSymbol_simpleCaseSingleSymbol(){
        Character[] singleSpecialSymbol = SpecialSymbol.getSingleSpecialSymbol();
        for (Character input : singleSpecialSymbol){
            StringReader reader = new StringReader(input.toString());
            Lexer lexer = new Lexer(reader);
            try{
                Symbol symbol = lexer.getNextSymbol();
                assertEquals(SpecialSymbol.createSymbol(input.toString()), symbol);
            } catch (Exception e){
                fail("Exception was thrown: "+ e);
            }
        }
    }

    @Test
    public void testSpecialSymbol_simpleCaseComplexSymbol(){
        String[] complexSpecialSymbol = SpecialSymbol.getComplexSpecialSymbol();
        for (String input : complexSpecialSymbol){
            StringReader reader = new StringReader(input);
            Lexer lexer = new Lexer(reader);
            try{
                Symbol symbol = lexer.getNextSymbol();
                assertEquals(SpecialSymbol.createSymbol(input), symbol);
            } catch (Exception e){
                fail("Exception was thrown: "+ e);
            }
        }
    }

    @Test
    public void testSpecialSymbol_notSpecialSymbol(){
        String[] notSpecialSymbol = new String[]{
               "\"<=\"", "\"*\""
        };
        for (String input : notSpecialSymbol){
            StringReader reader = new StringReader(input);
            Lexer lexer = new Lexer(reader);
            try{
                Symbol symbol = lexer.getNextSymbol();
                assertNotNull(symbol);
                assertFalse(symbol instanceof SpecialSymbol);
            } catch (Exception e){
                fail("Exception was thrown: "+ e);
            }
        }
    }

    @Test
    public void testCornerCase_NumberAndLettersWithoutSpace() throws IOException {
        String input = "32hello";
        Symbol[] expected = new Symbol[]{
                new NaturalNumberValue("32"),
                new Identifier("hello"),
                new EOFSymbol(),
        };
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        for (Symbol value : expected) {
                Symbol symbol = lexer.getNextSymbol();
                assertEquals(symbol, value);

        }
    }

    @Test
    public void testGeneral_comments(){
        String input = "some code //This is a comment \\t \\n # this is food";
        Symbol[] expected = new Symbol[]{
                new Identifier("some"),
                new Identifier("code"),
                new EOFSymbol(),
        };
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        for (Symbol value : expected) {
            try {
                Symbol symbol = lexer.getNextSymbol();
                assertEquals(symbol, value);
            } catch (Exception e){
                fail("Exception was thrown: "+ e);
            }
        }
    }

    @Test
    public void testCornerCase_NumbersAndDots(){
        // Digits and 1 dot
        String input = "90.";
        Symbol[] expected = new Symbol[]{
                new NaturalNumberValue("90"),
                SpecialSymbol.createSymbol("."),
                new EOFSymbol(),
        };
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        for (Symbol value : expected) {
            try {
                Symbol symbol = lexer.getNextSymbol();
                assertEquals(symbol, value);
            } catch (Exception e){
                fail("Exception was thrown: "+ e);
            }
        }
        //Digits and 2 dots
        input = "90..";
        expected = new Symbol[]{
                new NaturalNumberValue("90"),
                SpecialSymbol.createSymbol("."),
                SpecialSymbol.createSymbol("."),
                new EOFSymbol(),
        };
        reader = new StringReader(input);
        lexer = new Lexer(reader);
        for (Symbol value : expected) {
            try {
                Symbol symbol = lexer.getNextSymbol();
                assertEquals(symbol, value);
            } catch (Exception e){
                fail("Exception was thrown: "+ e);
            }
        }
        //Alternation between digits ans dots
        input = ".90.81.50..689.";
        expected = new Symbol[]{
                SpecialSymbol.createSymbol("."),
                new RealNumberValue("90.81"),
                SpecialSymbol.createSymbol("."),
                new NaturalNumberValue("50"),
                SpecialSymbol.createSymbol("."),
                SpecialSymbol.createSymbol("."),
                new NaturalNumberValue("689"),
                SpecialSymbol.createSymbol("."),
                new EOFSymbol(),
        };
        reader = new StringReader(input);
        lexer = new Lexer(reader);
        for (Symbol value : expected) {
            try {
                Symbol symbol = lexer.getNextSymbol();
                assertEquals(symbol, value);
            } catch (Exception e){
                fail("Exception was thrown: "+ e);
            }
        }
    }
    public void correspondenceHelperFunction(String input, Symbol[] expectedSymbols) throws IOException {
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        for (Symbol value : expectedSymbols) {
            Symbol symbol = lexer.getNextSymbol();
            assertNotNull(symbol);
            assertEquals(value, symbol);
        }
    }
    @Test
    public void testInitialising_1() throws IOException {
        String input = "const i int = 3;";
        Symbol[] expected = new Symbol[]{
                new Keyword("const"),  new Identifier("i"), new Identifier("int"),
                new OperatorSpecialSymbol("="), new NaturalNumberValue("3"), new SyntaxSpecialSymbol(";"),
                new EOFSymbol()
        };
        correspondenceHelperFunction(input, expected);
    }
    @Test
    public void testInitialising_2() throws IOException {
        String input = "const j real = 3.2*5.0;";
        Symbol[] expected = new Symbol[]{
                new Keyword("const"), new Identifier("j"), new Identifier("real"),
                new OperatorSpecialSymbol("="), new RealNumberValue("3.2"), new OperatorSpecialSymbol("*"),
                new RealNumberValue("5.0"), new SyntaxSpecialSymbol(";"), new EOFSymbol()
        };
        correspondenceHelperFunction(input, expected);
    }
    @Test
    public void testInitialising_3() throws IOException {
        String input = "const j real = i*5.0;";
        Symbol[] expected = new Symbol[]{
                new Keyword("const"), new Identifier("j"), new Identifier("real"),
                new OperatorSpecialSymbol("="), new Identifier("i"), new OperatorSpecialSymbol("*"),
                new RealNumberValue("5.0"), new SyntaxSpecialSymbol(";"), new EOFSymbol()
        };
        correspondenceHelperFunction(input, expected);
    }
    @Test
    public void testInitialising_4() throws IOException {
        String input = "const message string = \"Hello\";";
        Symbol[] expected = new Symbol[]{
                new Keyword("const"), new Identifier("message"), new Identifier("string"),
                new OperatorSpecialSymbol("="), new StringValue("Hello"), new SyntaxSpecialSymbol(";"), new EOFSymbol()
        };
        correspondenceHelperFunction(input, expected);
    }

    @Test
    public void testInitialising_6() throws IOException {
        String input = """
                        record Point {
                            x int;
                            y int;
                        }""";
        Symbol[] expected = new Symbol[]{
                new Keyword("record"), new Identifier("Point"), new SyntaxSpecialSymbol("{"),
                new Identifier("x"), new Identifier("int"), new SyntaxSpecialSymbol(";"),
                new Identifier("y"), new Identifier("int"), new SyntaxSpecialSymbol(";"),
                new SyntaxSpecialSymbol("}"), new EOFSymbol()
        };
        correspondenceHelperFunction(input, expected);
    }
    @Test
    public void testInitialising_7() throws IOException {
        String input = """
                        record Person {
                            name string;
                            location Point;
                            history int[];
                        }""";
        Symbol[] expected = new Symbol[]{
                new Keyword("record"), new Identifier("Person"), new SyntaxSpecialSymbol("{"),
                new Identifier("name"), new Identifier("string"), new SyntaxSpecialSymbol(";"),
                new Identifier("location"), new Identifier("Point"), new SyntaxSpecialSymbol(";"),
                new Identifier("history"), new Identifier("int"), new SyntaxSpecialSymbol("["),
                new SyntaxSpecialSymbol("]"), new SyntaxSpecialSymbol(";"), new SyntaxSpecialSymbol("}")
        };
        correspondenceHelperFunction(input, expected);
    }
    @Test
    public void testInitialising_8() throws IOException {
        String input = "var a int = 3;";
        Symbol[] expected = new Symbol[]{
                new Keyword("var"), new Identifier("a"), new Identifier("int"),
                new OperatorSpecialSymbol("="), new NaturalNumberValue("3"), new SyntaxSpecialSymbol(";"),
                new EOFSymbol()
        };
        correspondenceHelperFunction(input, expected);
    }
    @Test
    public void testInitialising_9() throws IOException {
        String input = "val e int = a*2;";
        Symbol[] expected = new Symbol[]{
                new Keyword("val"), new Identifier("e"), new Identifier("int"),
                new OperatorSpecialSymbol("="), new Identifier("a"), new OperatorSpecialSymbol("*"),
                new NaturalNumberValue("2"), new SyntaxSpecialSymbol(";"), new EOFSymbol()
        };
        correspondenceHelperFunction(input, expected);
    }
    @Test
    public void testInitialising_10() throws IOException {
        String input = "val e int = a*2;";
        Symbol[] expected = new Symbol[]{
                new Keyword("val"), new Identifier("e"), new Identifier("int"),
                new OperatorSpecialSymbol("="), new Identifier("a"), new OperatorSpecialSymbol("*"),
                new NaturalNumberValue("2"), new SyntaxSpecialSymbol(";")
        };
        correspondenceHelperFunction(input, expected);
    }

    @Test
    public void testInitialising_11() throws IOException {
        String input = "var c int[] = int[](5);";
        Symbol[] expected = new Symbol[]{
                new Keyword("var"), new Identifier("c"), new Identifier("int"), new SyntaxSpecialSymbol("["),
                new SyntaxSpecialSymbol("]"), new OperatorSpecialSymbol("="), new Identifier("int"),
                new SyntaxSpecialSymbol("["), new SyntaxSpecialSymbol("]"), new SyntaxSpecialSymbol("("),
                new NaturalNumberValue("5"), new SyntaxSpecialSymbol(")"), new SyntaxSpecialSymbol(";")
        };
        correspondenceHelperFunction(input, expected);
    }

    @Test
    public void testFunctionDefinition_1() throws IOException {
        String input = """
                        proc square(v int) int {
                            return v*v;
                        }""";
        Symbol[] expected = new Symbol[]{
                new Keyword("proc"), new Identifier("square"), new SyntaxSpecialSymbol("("),
                new Identifier("v"), new Identifier("int"), new SyntaxSpecialSymbol(")"),
                new Identifier("int"), new SyntaxSpecialSymbol("{"), new Keyword("return"), new Identifier("v"),
                new OperatorSpecialSymbol("*"), new Identifier("v"), new SyntaxSpecialSymbol(";"),
                new SyntaxSpecialSymbol("}")
        };
        correspondenceHelperFunction(input, expected);
    }



    @Test
    public void emptyCode() throws IOException {
        String[] emptyStrings = new String[]{
//                " ",
//                "",
                "\t\t  \t"
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
    public void testStrings() throws IOException {
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


}
