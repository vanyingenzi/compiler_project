package compiler.Symbols;

import compiler.Lexer.Symbol;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class SpecialSymbol extends Symbol {

    private static final Character[] singleSpecialSymbol = new Character[]{'=', '+', '-', '*', '/', '%', '<', '>', '(', ')', '{', '}', '[', ']', '.', ';', ','};
    private static final Set<Character> singleSpecialSymbolSet = new HashSet<>(Arrays.asList(singleSpecialSymbol));
    private static final String[] complexSpecialSymbol = new String[]{"==", "<>", "<=", ">="};
    private static final Set<String> complexSpecialSymbolSet = new HashSet<>(Arrays.asList(complexSpecialSymbol));
    public static Symbol createSymbol(String string){
        return switch (string)
        {
            case "=", "+", "-", "*", "/", "%", "==", "<>", "<", ">", "<=", ">=" -> new OperatorSpecialSymbol(string);
            case "(", ")", "{", "}", "[", "]", ".", ";", "," -> new SyntaxSpecialSymbol(string);
            default -> throw new UnsupportedOperationException("Got unexpected symbol : " + string);
        };
    }

    public static Character[] getSingleSpecialSymbol(){
        return singleSpecialSymbol;
    }
    public static String[] getComplexSpecialSymbol(){
        return complexSpecialSymbol;
    }

    /**
     * Determines whether @candidate is a SpecialSymbol.
     * @param candidate The integer value of the candidate character
     * @return true if @candidate is a SpecialSymbol, false otherwise
     */
    public static boolean isSpecialSymbol(int candidate){
        return singleSpecialSymbolSet.contains((char) candidate);
    }

    /**
     * Determines whether @candidate could be the start of a complex SpecialSymbol.
     * @param candidate The integer value of the candidate character
     * @return true if @candidate is the start of a complex SpecialSymbol, false otherwise
     */
    public static boolean maybeComplexSpecialSymbol(int candidate){
        return candidate == '=' || candidate == '<' || candidate == '>';
    }

    /**
     * Determines whether @candidate is a complex SpecialSymbol.
     * @param candidate a candidate string
     * @return true if @candidate is a complex SpecialSymbol, false otherwise
     */
    public static boolean isComplexSpecialSymbol(String candidate){
        return complexSpecialSymbolSet.contains(candidate);
    }
}
