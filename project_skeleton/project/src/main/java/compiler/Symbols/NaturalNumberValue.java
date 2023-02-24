package compiler.Symbols;

public class NaturalNumberValue extends Value {
    NaturalNumberValue(String value){
        this.value = Integer.valueOf(value);
    }
}
