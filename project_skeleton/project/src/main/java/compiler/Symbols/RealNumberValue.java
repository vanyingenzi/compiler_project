package compiler.Symbols;

public class RealNumberValue extends Value{

    RealNumberValue(String value){
        this.value = Float.valueOf(value);
    }
}
