package compiler.Symbols;

public class RealNumberValue extends Value{

    public RealNumberValue(String value){
        this.value = Float.valueOf(value);
    }
}
