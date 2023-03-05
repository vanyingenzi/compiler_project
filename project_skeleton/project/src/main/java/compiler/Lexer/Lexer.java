package compiler.Lexer;
import compiler.Symbols.*;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.rmi.UnexpectedException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;


public class Lexer {
    private final PushbackReader reader;

    final private Set<Character> special_symbols = new HashSet<>(Arrays.asList(new Character[]{'=', '+', '-', '*', '/', '%', '<', '>', '(', ')', '{', '}', '[', ']', '.', ';', ','}));
    final private Set<Character> starting_special_symbols = new HashSet<>(Arrays.asList(new Character[]{'=', '<', '>', '/'})); // Also '/' because '//' is for comment !
    public Lexer(Reader input) {
        this.reader = new PushbackReader(input);
    }
    public Symbol getNextSymbol(){
        try{
            StringBuilder stringBuilder = new StringBuilder();
            LexerState state = new LexerState();
            int character = reader.read();
            boolean shouldContinue = initState(state, stringBuilder, (char) character);
            character = reader.read();
            while (shouldContinue && character != -1) {
                shouldContinue = updateState(state, stringBuilder, (char) character);
                character = reader.read();
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
        if (state.nbOfPossibilities() == 0) {
            if (stringBuilder.isEmpty())
                return new EOFSymbol(); //TODO: Add state EOF to the state, to avoid the case of an error with empty stringbuilder
            else
                throw new UnexpectedException("The state has no possibilities left but the string is not empty :" + stringBuilder.toString());
        }

        String symbol = stringBuilder.toString();
        if (state.isSomePossible(LexerState.KEYWORD) && Keyword.isAKeyword(symbol)) {
            return new Keyword(symbol);
        } else if (state.isSomePossible(LexerState.BOOLEAN) && BooleanValue.isABooleanValue(symbol)) {
            return new BooleanValue(symbol);
        } else if (state.isSomePossible(LexerState.IDENTIFIER)) {
            return new Identifier(stringBuilder.toString());
        } else if (state.isSomePossible(LexerState.NATURAL)) {
            return new NaturalNumberValue(symbol);
        } else if (state.isSomePossible(LexerState.REAL)) {
            return new RealNumberValue(symbol);
        } else if (state.isSomePossible(LexerState.STRING)) {
            return new StringValue(symbol);
        } else if (state.isSomePossible(LexerState.SPECIAL_SYMBOL)) {
            return SpecialSymbol.createSymbol(symbol);
        } else {
            throw new UnexpectedException("The state does not match any possibility.");
        }
    }

    private void setupState(LexerState state, StringBuilder stringBuilder) throws IOException{
        int character = reader.read();
        if (character == -1){
            state.limitPossibilityTo(LexerState.EOF);
        }
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
        // Remove white space, tab and new line
        if (Character.isWhitespace(character)) {
            int next_character = reader.read();
            while (next_character != -1 && Character.isWhitespace((char) next_character)) {
                next_character = reader.read();
            }
            return initState(state, stringBuilder, (char) next_character);
        }

        stringBuilder.append(character); //If not WhiteSpace, character is always added to the StringBuilder
        if (special_symbols.contains(character)){
            state.limitPossibilityTo(LexerState.SPECIAL_SYMBOL);
            if (starting_special_symbols.contains(character)){
                int next_character = reader.read();
                if(character == '/' && (char) next_character == '/'){  // Skip comments
                    while (next_character != -1 && (char) next_character != '\n'){
                        next_character = reader.read();
                    }
                    state.removePossibilities(LexerState.SPECIAL_SYMBOL); //Reset state
                    stringBuilder.deleteCharAt(0); // Character is removed if it was a comment
                    return initState(state, stringBuilder, (char) next_character);
                } else if ((character == '=' && next_character == '=') || (character == '<' && (next_character == '=' || next_character == '>')) || (character == '>' && next_character == '=')) {
                    stringBuilder.append(next_character);
                    return false;
                }
                reader.unread(next_character); // It's a single special character
            }
            return false;
        } else if (Character.isDigit(character)) {
            state.limitPossibilityTo(LexerState.NATURAL); // REAL is only possible if there is a dot (.)
            return true;
        } else if (Character.isAlphabetic(character)) {
            state.limitPossibilityTo(LexerState.KEYWORD, LexerState.BOOLEAN, LexerState.IDENTIFIER);
            return true;
        } else if (character == '_') {
            state.limitPossibilityTo(LexerState.IDENTIFIER);
            return true;
        } else if (character == '"') { //TODO: for string also do all in one since we know it's a string
            stringBuilder.deleteCharAt(0); // " not need since we know it's a string
            state.limitPossibilityTo(LexerState.STRING);
            return true;
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
        stringBuilder.append(character);
        if((Character.isWhitespace(character) || special_symbols.contains(character)) && !state.isSomePossible(LexerState.STRING)) { // Stopping characters (except in Strings)
            if (character == '.' && state.isSomePossible(LexerState.NATURAL)) { // dot (.) can be an exception for Natural
                int next_character = reader.read();
                reader.unread(next_character);
                if (Character.isDigit(next_character)) { // Will be a Real
                    state.limitPossibilityTo(LexerState.REAL);
                    return true;
                }
            }
            // Character is part of a new symbol
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            reader.unread(character);
            return false;
        } else if (character == '\\' && state.isSomePossible(LexerState.STRING)) { //TODO: Check if we have to do it so or not (maybe just rush the string)
            int next_character = reader.read();
            if (next_character == '"'){
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                stringBuilder.append('\"');
                return true;
            }
            return true;
        } else if (character == '"' && state.isSomePossible(LexerState.STRING)) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1); // Not needed since we are in a String
            return false;
        } else if (Character.isDigit(character) && state.isSomePossible(LexerState.IDENTIFIER, LexerState.NATURAL, LexerState.REAL, LexerState.STRING)) {
            return true;
        } else if (Character.isAlphabetic(character) && state.isSomePossible(LexerState.KEYWORD, LexerState.BOOLEAN, LexerState.IDENTIFIER, LexerState.STRING)){
            return true;
        } else if (character == '_' && state.isSomePossible(LexerState.IDENTIFIER)) {
            return true;
        } else if (state.isSomePossible(LexerState.STRING)) {
            return true;
        } // Something went wrong
        state.limitPossibilityTo();
        return false;
    }

    private void skipWhiteSpace(LexerState state) throws IOException{

    }
}

class LexerState{
    private final BitSet bitSet;
    static final int NUMBERS_OF_SUPPORTED_SYMBOLS = 7;
    // The order of the indexes are very important because it indicates the priority
    static final int KEYWORD = 0;
    static final int BOOLEAN = 1;
    static final int IDENTIFIER = 2;
    static final int NATURAL = 3;
    static final int REAL = 4;
    static final int STRING = 5;
    static final int SPECIAL_SYMBOL = 6;
    static final int EOF = 7;
    LexerState(){
        bitSet = new BitSet(NUMBERS_OF_SUPPORTED_SYMBOLS);
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
        bitSet.clear(0, NUMBERS_OF_SUPPORTED_SYMBOLS);
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
    boolean isSomePossible(int... possibilities){
        for (Integer possibility: possibilities)
            if (bitSet.get(possibility)) return true;
        return false;
    }

    /**
     * Checks if all the given possibilities is possible in the state.
     * @param possibilities : variable arguments constant as defined above
     * @return boolean indicating
     */
    boolean isAllPossible(int... possibilities){
        for (Integer possibility: possibilities)
            if (!bitSet.get(possibility)) return false;
        return true;
    }
    /**
     * Returns the highest symbol constant that is possible in this state
     * @return int, representing a symbol possibility. (c.f constants defined above)
     */
    int highestPrioritySymbol(){
        return bitSet.nextSetBit(0);
    }
}