package compiler.Lexer;
import compiler.Symbols.*;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.rmi.UnexpectedException;
import java.util.BitSet;

public class Lexer {
    private final PushbackReader reader;
    public Lexer(Reader input) {
        this.reader = new PushbackReader(input);
    }
    private Symbol getSymbol(LexerState state, StringBuilder stringBuilder) throws UnexpectedException {
        if (state.nbOfPossibilities() == 0){
            if (stringBuilder.isEmpty()) return new EOFSymbol();
            else throw new UnexpectedException("The state has no possibilities left but the string is not empty :" + stringBuilder.toString());
        }
        return switch (state.highestPrioritySymbol())
        {
            case LexerState.KEYWORD -> new Keyword(stringBuilder.toString());
            case LexerState.BOOLEAN -> new BooleanValue(stringBuilder.toString());
            case LexerState.IDENTIFIER -> new Identifier(stringBuilder.toString());
            case LexerState.NATURAL -> new NaturalNumberValue(stringBuilder.toString());
            case LexerState.REAL -> new RealNumberValue(stringBuilder.toString());
            case LexerState.STRING -> new StringValue(stringBuilder.toString());
            case LexerState.SPECIAL_SYMBOL -> SpecialSymbol.createSymbol(stringBuilder.toString());
            default -> new EOFSymbol();
        };
    }
    private boolean isDigit(char c){
        return (c >= '0' && c <= '9');
    }
    private boolean isAlphabet(char c){
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
    private boolean initState(LexerState state, StringBuilder stringBuilder, char character) throws IOException{
        if (character == '/'){
            int next = reader.read();
            if ((char) next == '/') {
                while (next != -1 && (char) next != '\n') next = reader.read();
                return true;
            } else {
                reader.unread(next);
                state.limitPossibilityTo(LexerState.SPECIAL_SYMBOL);
                return false;
            }
        } else if (isAlphabet(character)) {
            stringBuilder.append(character);
            if (Keyword.keywordStartsWidth(String.valueOf(character)))
                state.addPossibility(LexerState.KEYWORD);
            if (BooleanValue.booleanStartsWith(String.valueOf(character)))
                state.addPossibility(LexerState.BOOLEAN);
            state.addPossibility(LexerState.IDENTIFIER);
            return true;
        }
        return false;
    }
    private boolean updateState(LexerState state, StringBuilder stringBuilder, char character) throws IOException {
        if (character == '/'){
            if (state.isPossible(LexerState.STRING)){
                stringBuilder.append(character);
                return true;
            } else {
                reader.unread(character);
                return false;
            }
        } else if (isAlphabet(character)) {
            if (state.isPossible(LexerState.KEYWORD, LexerState.BOOLEAN)) {
                stringBuilder.append(character);
                if (!Keyword.keywordStartsWidth(stringBuilder.toString()))
                    state.removePossibilities(LexerState.KEYWORD);
                if (!BooleanValue.booleanStartsWith(stringBuilder.toString()))
                    state.removePossibilities(LexerState.BOOLEAN);
                return true;
            } else if (state.isPossible(LexerState.IDENTIFIER)) {
                stringBuilder.append(character);
                return true;
            } else {
                reader.unread(character);
                return false;
            }
        }
        return false;
    }
    public Symbol getNextSymbol(){
        try{
            StringBuilder stringBuilder = new StringBuilder();
            LexerState state = new LexerState();
            int character = reader.read();
            boolean shouldContinue = initState(state, stringBuilder, (char) character);
            while (character != -1 && shouldContinue) {
                character = reader.read();
                shouldContinue = updateState(state, stringBuilder, (char) character);
            }
            return getSymbol(state, stringBuilder);
        } catch (IOException ignored){
            return new EOFSymbol();
        }
    }
}

class LexerState{
    private final BitSet bitSet;
    static final int NUMBERS_SUPPORTED_SYMBOLS = 7;
    // The order of the indexes are very important because it indicates the priority
    static final int KEYWORD = 0;
    static final int BOOLEAN = 1;
    static final int IDENTIFIER = 2;
    static final int NATURAL = 3;
    static final int REAL = 4;
    static final int STRING = 5;
    static final int SPECIAL_SYMBOL = 6;
    LexerState(){
        bitSet = new BitSet(NUMBERS_SUPPORTED_SYMBOLS);
    }
    int nbOfPossibilities(){
        return bitSet.cardinality();
    }
    /**
     * This function adds the given possibilities to the state
     * @param possibilities : variable arguments constant as defined above
     */
    void addPossibility(int... possibilities){
        for (Integer idx: possibilities)
            bitSet.set(idx);
    }
    /**
     * This function limits the possibilities of the state to only the ones given as variable arguments
     * @param possibilities : variable arguments constant as defined above
     */
    void limitPossibilityTo(int... possibilities){
        bitSet.clear(0, NUMBERS_SUPPORTED_SYMBOLS);
        addPossibility(possibilities);
    }
    /**
     * This function removes the given variable arguments possibilities from the state
     * @param possibilities : variable arguments constant as defined above
     */
    void removePossibilities(int... possibilities){
        for (Integer idx: possibilities)
            bitSet.clear(idx);
    }
    /**
     * Checks if at least one of the given possibilities is possible in the state.
     * @param possibilities : variable arguments constant as defined above
     * @return boolean indicating
     */
    boolean isPossible(int... possibilities){
        for (Integer possibility: possibilities)
            if (bitSet.get(possibility)) return true;
        return false;
    }
    /**
     * Returns the highest symbol constant that is possible in this state
     * @return int, representing a symbol possibility. (c.f constants defined above)
     */
    int highestPrioritySymbol(){
        return bitSet.nextSetBit(0);
    }
}