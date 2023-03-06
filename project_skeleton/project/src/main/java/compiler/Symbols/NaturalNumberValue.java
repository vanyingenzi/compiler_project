package compiler.Symbols;

public class NaturalNumberValue extends Value {
    public NaturalNumberValue(String value){
        this.value = Integer.valueOf(value);
    } // TODO: verify 32bits
}
