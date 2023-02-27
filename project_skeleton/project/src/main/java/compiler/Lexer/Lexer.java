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
            return getSymbolFromState(state, stringBuilder);
        } catch (IOException ignored){
            return new EOFSymbol();
        }
    }

    /**
     * Initializes the symbol based on the LexerState given in parameter and respects the priority in case of multiple
     * possibilities.
     * @param state The LexerState
     * @param stringBuilder The stringBuilder
     * @return Symbol
     * @throws UnexpectedException
     */
    private Symbol getSymbolFromState(LexerState state, StringBuilder stringBuilder) throws UnexpectedException {
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
    /**
     * Initializes the state based on the first character read from the reader. This function is the one responsible for
     * indicating the possibilities given the first read character. The updateState updates the state by eliminating possibilities
     * only. This function returns true if we should continue reading from the reader.
     * @param state The LexerState that will be modified
     * @param stringBuilder The stringBuilder normally empty.
     * @param character The character that was read from the PushBackReader
     * @return A boolean indicating if we should continue reading from the reader.
     * @throws IOException
     */
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
        } else if (Character.isAlphabetic(character)) {
            stringBuilder.append(character);
            if (Keyword.keywordStartsWidth(String.valueOf(character)))
                state.addPossibility(LexerState.KEYWORD);
            if (BooleanValue.booleanStartsWith(String.valueOf(character)))
                state.addPossibility(LexerState.BOOLEAN);
            state.addPossibility(LexerState.IDENTIFIER);
            return true;
        } else if (Character.isDigit(character)) {
            stringBuilder.append(character);
            state.limitPossibilityTo(LexerState.NATURAL, LexerState.REAL);
            return true;
        } else if (character == '_'){
            stringBuilder.append(character);
            state.limitPossibilityTo(LexerState.IDENTIFIER);
            return true;
        } else if (character == ' ') { // To remove whitespace
            int next = reader.read();
            while (next != -1 && Character.isWhitespace(next)) next = reader.read();
            return initState(state, stringBuilder, (char) next);
        }
        return false;
    }

    /**
     * This function implements the logic of updating the state, stringBuilder according to the character. It returns a boolean indicating if
     * we should continue reading from the reader.
     * @param state The LexerState that has been initialised using the initState method. This function can only update the state by removing possibilities.
     * @param stringBuilder The stringBuilder.
     * @param character The character that was just read from the reader.
     * @return A boolean indicating if we should continue reading from the PushBackReader (object attribute)
     * @throws IOException
     */
    private boolean updateState(LexerState state, StringBuilder stringBuilder, char character) throws IOException {
        if (character == '/' && state.isPossible(LexerState.STRING)){
            stringBuilder.append(character);
            return true;
        } else if (Character.isAlphabetic(character)) {
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
            }
        } else if (Character.isDigit(character) && state.isPossible(LexerState.IDENTIFIER, LexerState.NATURAL, LexerState.REAL, LexerState.STRING)) {
            stringBuilder.append(character);
            return true;
        } else if (character == '_' && state.isPossible(LexerState.IDENTIFIER, LexerState.STRING)){
            stringBuilder.append(character);
            return true;
        }
        reader.unread(character);
        return false;
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