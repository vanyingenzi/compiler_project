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
        this.reader = new PushbackReader(input, 2);
    }

    /**
     * Gets the next Symbol from the input reader of the Lexer.
     * @return next Symbol
     */
    public Symbol getNextSymbol() throws UnauthorizedLangException {
        try{
            StringBuilder stringBuilder = new StringBuilder();
            LexerState state = new LexerState();
            boolean shouldContinue = initState(state, stringBuilder);
            while (shouldContinue) {
                shouldContinue = updateState(state, stringBuilder);
            }
            return getSymbolFromState(state, stringBuilder);
        } catch (IOException ignored){
            throw new RuntimeException("An exception occurred with the reader.");
        }
    }

    /**
     * Gets the corresponding Symbol from the @state of the Lexer. Assigns the content of @stringBuilder as its value.
     * @param state The LexerState
     * @param stringBuilder The stringBuilder
     * @return Symbol with as value the content of @stringBuilder.
     * @throws UnexpectedException
     */
    private Symbol getSymbolFromState(LexerState state, StringBuilder stringBuilder) throws UnexpectedException {
        if (state.nbOfPossibilities() == 0) {
            throw new UnexpectedException("Error: The state has no possibilities left. Content of the string buffer: " + stringBuilder.toString());
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
        } else if (state.isSomePossible(LexerState.EOF)) {
            return new EOFSymbol();
        } else {
            throw new UnexpectedException("The state does not match any possibility.");
        }
    }

    /**
     * Initializes the state based on the first characters read from the reader. This function is the one responsible for
     * indicating the possibilities given the first read character(s). Whitespaces, comments, StringValues and SpecialSymbols
     * are read entirely in this function (without passing by the function updateState()).
     * @param state The LexerState that will be modified
     * @param stringBuilder The stringBuilder normally empty
     * @return true if we should continue reading, false otherwise
     * @throws IOException
     */
    private boolean initState(LexerState state, StringBuilder stringBuilder) throws IOException{
        // Verify if EOF or do some cleaning
        int character = reader.read();
        if (character == -1){
            state.limitPossibilityTo(LexerState.EOF);
            return false;
        }
        if (skipIfWhiteSpace(character)) {
            return initState(state, stringBuilder);
        }
        if (skipIfComment(character)){
            return initState(state, stringBuilder);
        }

        stringBuilder.append((char) character); //If not WhiteSpace, character is always added to the StringBuilder

        if(SpecialSymbol.isSpecialSymbol(character)){
            state.limitPossibilityTo(LexerState.SPECIAL_SYMBOL);
            putLongestSpecialSymbol(character, stringBuilder);
            return false;
        } else if (character == '"') {
            stringBuilder.deleteCharAt(0); // Starting/ending '"' should not be included in.
            putString(stringBuilder);
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
        }
        throw new IOException("Error: No initiation of state possible.");
    }

    /**
     * Implements the logic of updating the @state and @stringBuilder according to the next character and
     * the current @state.
     * @param state The LexerState initialised using the initState method that will be updated
     * @param stringBuilder The stringBuilder that may be updated with next character if part of the same symbol
     * @return true if we should continue reading, false otherwise
     * @throws IOException
     */
    private boolean updateState(LexerState state, StringBuilder stringBuilder) throws IOException {
        int character = reader.read();

        if (isStoppingCharacter(state, character)){
            reader.unread(character);
            return false;
        }

        stringBuilder.append((char) character);
        if (Character.isDigit(character) && state.isSomePossible(LexerState.IDENTIFIER, LexerState.NATURAL, LexerState.REAL)){
            return true;
        } else if (Character.isAlphabetic(character) && state.isSomePossible(LexerState.KEYWORD, LexerState.BOOLEAN, LexerState.IDENTIFIER)){
            return true;
        } else if (character == '_' && state.isSomePossible(LexerState.IDENTIFIER)) {
            state.limitPossibilityTo(LexerState.IDENTIFIER);
            return true;
        } else if (character == '.' && state.isSomePossible(LexerState.NATURAL)) {
            state.limitPossibilityTo(LexerState.REAL); // Verifications are done in function isStoppingCharacter()
            return true;
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        reader.unread(character); // No matching. It's a new Symbol.
        return false;
    }

    /**
     * Skips all white spaces (space, tabulation /t and new line /n), if there has.
     * @param character The integer value of the starting character
     * @return true if there was whitespace, false otherwise
     * @throws IOException
     */
    private boolean skipIfWhiteSpace(int character) throws IOException{
        boolean isWhiteSpace = Character.isWhitespace(character);
        if(isWhiteSpace) {
            while (character != -1 && Character.isWhitespace(character)) {
                character = reader.read();
            }
            reader.unread(character);
        }
        return isWhiteSpace;
    }

    /**
     * Skips the comment (starting with //) if there is one.
     * @param character The integer value of the starting character
     * @return true if there was a comment, false otherwise
     * @throws IOException
     */
    private boolean skipIfComment(int character) throws IOException{
        if (character == '/'){
            character = reader.read();
            if (character == '/'){
                while (character != -1 && character != '\n'){
                    character = reader.read();
                }
                return true;
            }
            reader.unread(character);
        }
        return false;
    }

    /**
     * Puts the longest matching special symbol into the @stringBuilder.
     * @param character The starting character, already into the @stringBuilder
     * @param stringBuilder
     * @throws IOException
     */
    private void putLongestSpecialSymbol(int character, StringBuilder stringBuilder) throws IOException{
        if (SpecialSymbol.maybeComplexSpecialSymbol(character)){
            character = reader.read();
            stringBuilder.append((char) character);
            if(!SpecialSymbol.isComplexSpecialSymbol(stringBuilder.toString())) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                reader.unread(character);
            }
        }
    }

    /**
     * Puts the string (starting and ending with '"', not '\"') into the @stringBuilder (without the starting/ending ").
     * @param stringBuilder should be empty
     * @throws IOException
     */
    private void putString(StringBuilder stringBuilder) throws IOException{
        int character = reader.read();
        while(character != -1 && character != '"'){
            if (character == '\\'){ // The sequence \\" should be treated as a '"' inside a string
                int next_character = reader.read();
                if (next_character == '"'){
                    stringBuilder.append('"');
                }
                else {
                    stringBuilder.append((char) character);
                    reader.unread(next_character);
                }
            }
            character = reader.read();
        }
        if (character == -1){
            throw new IOException("Error: EOF reached before ending string.");
        }
    }

    /**
     * Verifies if @character is port of a new Symbol, stopping the current Symbol, based on the current @state.
     * NOTE: Checking that the character dot (.) is part of a REAL or not is done here.
     * @param state
     * @param character
     * @return true if @character is a stopping character, false otherwise
     * @throws IOException
     */
    private boolean isStoppingCharacter(LexerState state, int character) throws IOException{
        if (character == -1){
            return true;
        }
        if (Character.isWhitespace(character)) {
            return true;
        } else if (SpecialSymbol.isSpecialSymbol(character)) {
            if (character == '.' && state.isSomePossible(LexerState.NATURAL)){
                character = reader.read();
                if (Character.isDigit(character)){
                    return false; // dot (.) is part of a REAL, not a stopping character
                }
                reader.unread(character);
            }
            return true;
        } else if (character == '"'){
            return true;
        }
        return false;
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

    /**
     * Gives the number of possibilities for the state of the Lexer.
     * @return number of possible states
     */
    int nbOfPossibilities(){
        return bitSet.cardinality();
    }
    /**
     * Adds the given possibilities to the state.
     * @param possibilities : variable arguments constant as defined above
     */
    void addPossibility(int... possibilities){
        for (Integer idx: possibilities)
            bitSet.set(idx);
    }
    /**
     * Limits the possibilities of the state to only the ones given as variable arguments.
     * @param possibilities : variable arguments constant as defined above
     */
    void limitPossibilityTo(int... possibilities){
        bitSet.clear(0, NUMBERS_OF_SUPPORTED_SYMBOLS);
        addPossibility(possibilities);
    }
    /**
     * Removes the given variable arguments possibilities from the state.
     * @param possibilities : variable arguments constant as defined above
     */
    void removePossibilities(int... possibilities){
        for (Integer idx: possibilities)
            bitSet.clear(idx);
    }
    /**
     * Checks if at least one of the given @possibilities is possible in the state.
     * @param possibilities : variable arguments constant as defined above
     * @return true if at least one @possibilities is possible, false otherwise
     */
    boolean isSomePossible(int... possibilities){
        for (Integer possibility: possibilities)
            if (bitSet.get(possibility)) return true;
        return false;
    }

    /**
     * Checks if all the given @possibilities are possible in the state.
     * @param possibilities : variable arguments constant as defined above
     * @return true if all @possibilities are possible, false otherwise
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