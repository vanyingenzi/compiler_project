package compiler.Lexer;

public abstract class Symbol{
    protected Object value;
    public Object getValue(){
        return value;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) return false;
        Symbol objCasted = (Symbol) obj;
        if (objCasted.getValue() == null && this.getValue() == null) return true;
        else if (objCasted.getValue() == null && this.getValue() != null) return false;
        else if (objCasted.getValue() != null && this.getValue() == null) return false;
        return objCasted.getValue().equals(this.getValue());
    }

    @Override
    public String toString() {
        if (this.value == null) return "";
        return this.value.toString();
    }
}