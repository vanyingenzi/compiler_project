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
    private boolean isAlphanumeric(char c){
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
    private boolean updateState(LexerState state, StringBuilder stringBuilder, char c) throws IOException {
        if (c == '/'){
            if (stringBuilder.isEmpty()) {
                char next = (char) reader.read();
                if (next == '/') {
                    while ( (char) reader.read() != '\n'); //TODO Change
                    return true;
                } else {
                    reader.unread(next);
                    state.exclusive(LexerState.SPECIAL_SYMBOL);
                    return false;
                }
            } else if (state.isPossible(LexerState.STRING)){
                stringBuilder.append(c);
                return true;
            } else {
                reader.unread(c);
                return false;
            }
        }
        return false;
    }
    public Symbol getNextSymbol(){
        try{
            if (!reader.ready()) return new EOFSymbol();
            StringBuilder stringBuilder = new StringBuilder();
            char c;
            LexerState state = new LexerState();
            boolean shouldContinue = true;
            while (reader.ready() && shouldContinue){
                c = (char) reader.read();
                shouldContinue = updateState(state, stringBuilder, c);
            }
            return getSymbol(state, stringBuilder);
        } catch (IOException ignored){
            return new EOFSymbol();
        }
    }
}

class LexerState{
    private BitSet bitSet;
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
    void exclusive(int... possibilities){
        bitSet.clear(0, NUMBERS_SUPPORTED_SYMBOLS);
        for (Integer idx: possibilities)
            bitSet.set(idx);
    }
    boolean isPossible(int symbolClass){
        return bitSet.get(symbolClass);
    }
    int highestPrioritySymbol(){
        return bitSet.nextSetBit(0);
    }
}