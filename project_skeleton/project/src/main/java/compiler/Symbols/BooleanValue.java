package compiler.Symbols;

public class BooleanValue extends Value{
    private static String[] booleanValues = new String[]{"true", "false"};
    public BooleanValue(String value){
        this.value = Boolean.valueOf(value);
    }

    /**
     * Determines whether @candidate is a BooleanValue or not.
     * @param candidate a candidate string
     * @return true if @candidate is a BooleanValue, false otherwise
     */
    public static boolean isABooleanValue(String candidate){
        return candidate.equals("true") || candidate.equals("false");
    }

    public static String[] getBooleanValues(){
        return booleanValues;
    }
}
