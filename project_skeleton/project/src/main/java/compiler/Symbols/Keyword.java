package compiler.Symbols;

import compiler.Lexer.Symbol;
import compiler.Lexer.TrieST;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Keyword extends Symbol {
    private static TrieST trieST;
    private static final String[] keywords = new String[]{
            "const","record", "var", "val", "proc", "for", "to", "by", "while", "if",
            "else", "return", "and", "or"
    };
    private static final Set<String> keywordsSet = new HashSet<>(Arrays.asList(keywords));
    public Keyword(String value){ this.value = value; }
    private static void initTrie(){
        trieST = new TrieST();
        for (String keyword: keywords) {
            trieST.insert(keyword);
        }
    }
    /**
     * This indicates whether there's a keyword that starts with the given prefix.
     * @param prefix : The prefix to check
     * @return true if there's a keyword that has a prefix the given prefix else false.
     */
    public static boolean keywordStartsWidth(String prefix){
        if (trieST == null) initTrie(); // Lazy initialisation
        return trieST.startsWith(prefix);
    }

    /**
     * Determines whether @candidate is a Keyword.
     * @param candidate
     * @return true if @candidate is a Keyword, false otherwise
     */
    public static boolean isAKeyword(String candidate){
        return keywordsSet.contains(candidate);
    }
}