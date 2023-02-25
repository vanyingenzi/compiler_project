package compiler.Symbols;

public class BooleanValue extends Value{
    public BooleanValue(String value){
        this.value = Boolean.valueOf(value);
    }
}
