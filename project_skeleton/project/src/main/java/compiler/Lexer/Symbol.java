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
        return objCasted.getValue().equals(this.getValue());
    }
}