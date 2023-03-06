package compiler.Symbols;

import compiler.Lexer.Symbol;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Keyword extends Symbol {
    private static final String[] keywords = new String[]{
            "const","record", "var", "val", "proc", "for", "to", "by", "while", "if",
            "else", "return", "and", "or"
    };
    private static final Set<String> keywordsSet = new HashSet<>(Arrays.asList(keywords));
    public Keyword(String value){ this.value = value; }

    /**
     * Determines whether @candidate is a Keyword.
     * @param candidate a candidate string
     * @return true if @candidate is a Keyword, false otherwise
     */
    public static boolean isAKeyword(String candidate){
        return keywordsSet.contains(candidate);
    }

    public static String[] getKeywords(){
        return keywords;
    }
}