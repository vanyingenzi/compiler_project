package compiler.Symbols;

public class BooleanValue extends Value{
    BooleanValue(String value){
        this.value = Boolean.valueOf(value);
    }
}
