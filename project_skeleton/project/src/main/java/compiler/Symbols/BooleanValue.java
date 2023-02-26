package compiler.Symbols;

public class BooleanValue extends Value{
    public BooleanValue(String value){
        this.value = Boolean.valueOf(value);
    }
    /**
     * This indicates whether there's a boolean that starts with the given prefix.
     * @param prefix : The prefix to check
     * @return true if there's a boolean that has a prefix the given prefix, else false.
     */
    public static boolean booleanStartsWith(String prefix){
        return "true".startsWith(prefix) || "false".startsWith(prefix);
    }
}
