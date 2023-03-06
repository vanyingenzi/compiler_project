import compiler.Lexer.Symbol;
import compiler.Lexer.UnauthorizedLangTokenException;
import compiler.Symbols.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import compiler.Lexer.Lexer;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


import static org.junit.Assert.*;


public class TestLexer {
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
                "432", "\"aString\"", "=", "689_startwithnumber"
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
                "123", "0", "1234."
        }; // TODO: Add verification of 32bits
        for (String input : naturalNumberValues){
            StringReader reader = new StringReader(input);
            Lexer lexer = new Lexer(reader);
            try{
                Symbol symbol = lexer.getNextSymbol();
                assertEquals(new NaturalNumberValue(input), symbol);
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
        }; // TODO: Add verification of 64bits
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
    public void testStringValue_simpleCase(){ // TODO: Add unrecognized characters
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

    @Test(expected = UnauthorizedLangTokenException.class)
    public void testStringValue_notStringValue(){
        String[] stringValues = new String[]{
                "\"Test of my string",
                "\"Test of my string with backslash \\\"",
        };
        for (String input : stringValues){
            StringReader reader = new StringReader(input);
            Lexer lexer = new Lexer(reader);
            try {
                System.out.println(input);
                Symbol symbol = lexer.getNextSymbol();
//                assertThrows(UnauthorizedLangTokenException.class, );
            } catch (IOException ioException) {
                fail("IOException was thrown: " + ioException);
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
                System.out.println("Input: "+input);
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




//
//
//    @Test
//    public void test() {
//        String input = "var x int = 2;";
//        StringReader reader = new StringReader(input);
//        Lexer lexer = new Lexer(reader);
//        assertNotNull(lexer.getNextSymbol());
//    }
//    @Test
//    public void emptyCode() {
//        String[] emptyStrings = new String[]{
//                " ", "", "\t\t  \t"
//        };
//        for (String emptyString: emptyStrings) {
//            StringReader reader = new StringReader(emptyString);
//            Lexer lexer = new Lexer(reader);
//            Symbol symbol = lexer.getNextSymbol();
//            assertNotNull(lexer.getNextSymbol());
//            assertTrue(symbol instanceof EOFSymbol);
//        }
//    }
//    @Test
//    public void testStrings(){
//        // Strings are ignored by our lexer therefore the only symbol we should get is the EOFSymbol
//        String[] comments = new String[]{
//                "//This is a comment\n",
//                "// Yet another \" Comment \t \n",
//                "// Hello 123 $# \\ \t for // 23\n",
//                "// This correct to since it's a program with just a string",
//        };
//        for (String input: comments) {
//            StringReader reader = new StringReader(input);
//            Lexer lexer = new Lexer(reader);
//            Symbol symbol = lexer.getNextSymbol();
//            assertNotNull(symbol);
//            assertTrue(symbol instanceof EOFSymbol);
//        }
//    }
//
//    @Test
//    public void testIdentifiers(){
//        // Strings are ignored by our lexer therefore the only symbol we should get is the EOFSymbol
//        String[] identifiers = new String[]{
//                "_id", "_", "iam_an_identifier", "_another1", "_111_", "id1", "__"
//        };
//        for (String input: identifiers) {
//            StringReader reader = new StringReader(input);
//            Lexer lexer = new Lexer(reader);
//            Symbol symbol = lexer.getNextSymbol();
//            assertNotNull(symbol);
//            assertTrue(symbol instanceof Identifier);
//            assertEquals(input, symbol.getValue());
//        }
//        String[] notIdentifiers = new String[]{
//                "1_id", "11_", "1eer"
//        };
//        for (String input: notIdentifiers) {
//            StringReader reader = new StringReader(input);
//            Lexer lexer = new Lexer(reader);
//            Symbol symbol = lexer.getNextSymbol();
//            assertNotNull(symbol);
//            assertFalse(symbol instanceof Identifier);
//        }
//    }
//    @Test
//    public void testNaturalNumbers(){
//        // Strings are ignored by our lexer therefore the only symbol we should get is the EOFSymbol
//        String[] naturalNumber = new String[]{
//                "1233", "0", "01234567890", "111111", "22222", "1211212", "99876612"
//        };
//        for (String input: naturalNumber) {
//            StringReader reader = new StringReader(input);
//            Lexer lexer = new Lexer(reader);
//            Symbol symbol = lexer.getNextSymbol();
//            assertNotNull(symbol);
//            assertTrue(symbol instanceof NaturalNumberValue);
//            assertEquals(Integer.parseInt(input), symbol.getValue());
//        }
//        String[] notNaturalNumber = new String[]{
//                "e123", "(11", ".01", "po1", "lo11"
//        };
//        for (String input: notNaturalNumber) {
//            StringReader reader = new StringReader(input);
//            Lexer lexer = new Lexer(reader);
//            Symbol symbol = lexer.getNextSymbol();
//            assertNotNull(symbol);
//            assertFalse(symbol instanceof NaturalNumberValue);
//        }
//    }
//    @Test
//    public void testSimpleLanguage() {
//        String input = "var x int";
//        Symbol[] expected = new Symbol[]{
//                new Keyword("var"),
//                new Identifier("x"),
//                new Identifier("int"),
//        };
//        StringReader reader = new StringReader(input);
//        Lexer lexer = new Lexer(reader);
//        for (Symbol value : expected) {
//            Symbol symbol = lexer.getNextSymbol();
//            assertNotNull(symbol);
//            assertEquals(symbol, value);
//        }
//    }
//
//    @Test
//    public void numberAndIdentifierWithoutSpace(){
//        String input = "32hello";
//        Symbol[] expected = new Symbol[]{
//                new NaturalNumberValue("32"),
//                new Identifier("hello"),
//        };
//        StringReader reader = new StringReader(input);
//        Lexer lexer = new Lexer(reader);
//        for (Symbol value : expected) {
//            Symbol symbol = lexer.getNextSymbol();
//            assertNotNull(symbol);
//            assertEquals(symbol, value);
//        }
//    }

}
