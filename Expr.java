import java.util.ArrayList;

class Expr extends BaseToken implements Checkable {
  int typeNumber;
  String charStr, id;
  int intlit;
  float floatlit;
  Name name;
  boolean bool;
  Expr expr[];
  BinaryOp binOp;
  String unaryOp, castType;
  ArrayList<Expr> args;

  public Expr(String i, char isStr) { // PROD 5,6 - strlit
    charStr = i;
    typeNumber = isStr == 's' ? 0 : 12;
  }
  public Expr(int i) { // PROD 4 - intlit
    intlit = i;
    typeNumber = 1;
  }
  public Expr(float i) { // PROD 7 - floatlit
    floatlit = i;
    typeNumber = 2;
  }
  public Expr(Name n) { // PROD 1 - name
    name = n;
    typeNumber = 3;
  }
  public Expr(boolean b) { // PROD 8,9 - booleans
    bool = b;
    typeNumber = 4;
  }
  public Expr(Expr e) { // PROD 10 - paren
    expr = new Expr[]{e};
    typeNumber = 5;
  }
  public Expr(Expr e, String op) { // PROD 11,12,13 - prefix expr
    unaryOp = op;
    expr = new Expr[]{e};
    typeNumber = 6;
  }
  public Expr(String ct, Expr e) { // PROD 14 - cast
    castType = ct;
    expr = new Expr[]{e};
    typeNumber = 7;
  }
  public Expr(Expr e1, BinaryOp bOp, Expr e2) { // PROD 15 - binop
    expr = new Expr[]{e1, e2};
    binOp = bOp;
    typeNumber = 8;
  }
  public Expr(Expr e1, Expr e2, Expr e3) { // PROD 16 - ternary
    expr = new Expr[]{e1, e2, e3};
    typeNumber = 9;
  }
  public Expr(String id, ArrayList<Expr> args) { // PROD 3 - func(args)
    this.id = id;
    this.args = args;
    typeNumber = 10; 
  }
  public Expr(String id, boolean isMethod) { // PROD 2 - func()
    this.id = id;
    typeNumber = 11; 
  }

  public String toString() {
    switch (this.typeNumber) {
      case 0: case 12:
        return charStr;
      case 1:
        return ""+intlit;
      case 2:
        return ""+floatlit;
      case 3:
        return name.toString();
      case 4:
        return bool ? "true" : "false";
      case 5:
        return expr[0].toString();
      case 6:
        return "(" + unaryOp + " " + expr[0].toString() + ")";
      case 7:
        return "(" + castType + ")" + expr[0].toString();
      case 8:
        return "(" + expr[0].toString() + " " + binOp.toString() + " " + expr[1].toString() + ")";
      case 9:
        return "( " + expr[0].toString() + " ? " + expr[1].toString() + " : " + expr[2].toString() + " )";
      case 10:
        String ret = "";
        for (Expr e: args) {
          ret += e.toString() + ", ";
        } ret = ret.substring(0, ret.length() > 0 ? ret.length() - 2 : 0 );
        return id + "(" + ret + ")";
      case 11:
        return id +"()";
      default:
        return "";
    }
  }
  

  public boolean isArray() throws Exception {
    return name != null && name.isArray() || typeNumber == 5 && expr[0].isArray();
  }
  public boolean isFinal() throws Exception {
    return name != null && name.isFinal() || typeNumber == 5 && expr[0].isFinal();
  }
  public boolean isFunction() throws Exception {
    return name != null && name.isFunction() || typeNumber == 5 && expr[0].isFunction();
  }

  public SymTable.Type typeCheck() throws Exception {
    switch (this.typeNumber) {
      case 0: // strlit
        return SymTable.Type.STRING;
      case 12: // charlit
        return SymTable.Type.CHAR;
      case 1: // intlit
        return SymTable.Type.INT;
      case 2: // floatlit
        return SymTable.Type.FLOAT;
      case 3: // var or array index
        return name.typeCheck();
      case 4: // boollit
        return SymTable.Type.BOOL;
      case 5: // value in parenthesis
        return expr[0].typeCheck();
      case 6: // unary ops
        if (unaryOp.equals("~")) {
          if (!canBeBool(expr[0]))
            throw new UnaryLogicalException();
          return SymTable.Type.BOOL;
        } else {
          if (!canBeFloat(expr[0]))
            throw new SignedValueException();
          return expr[0].typeCheck();
        }        
      case 7: // casting
        return getType(castType); // casting special case?
      case 8: // binary ops
        if (expr[0].typeCheck().equals(SymTable.Type.STRING) || expr[1].typeCheck().equals(SymTable.Type.STRING)) {
          if (!binOp.op.equals("+"))
            throw new StringOperationException();
          return SymTable.Type.STRING;
        }
        if (binOp.isArithmetic()) {
          if ( !canBeFloat(expr[0]) && !canBeFloat(expr[1]) )
            throw new BinaryExpressionException(
              binOp.op,
              ( canBeFloat(expr[0]) ? expr[0].typeCheck() : expr[1].typeCheck() ),
              ( canBeFloat(expr[0]) ? expr[0].isArray() : expr[1].isArray() )
            );
          return expr[0].typeCheck().equals(SymTable.Type.FLOAT) || expr[1].typeCheck().equals(SymTable.Type.FLOAT)
            ? SymTable.Type.FLOAT
            : SymTable.Type.INT;
        }
        if (binOp.isRelational()) {
          if ( !canBeFloat(expr[0]) && !canBeFloat(expr[1]) )
            throw new BinaryExpressionException(
              binOp.op, 
              ( canBeFloat(expr[0]) ? expr[0].typeCheck() : expr[1].typeCheck() ),
              ( canBeFloat(expr[0]) ? expr[0].isArray() : expr[1].isArray() )
            );
          return SymTable.Type.BOOL;
        }
        if (binOp.isLogical()) {
          if ( !canBeBool(expr[0]) && !canBeBool(expr[1]) )
            throw new BinaryExpressionException(
              binOp.op, 
              ( canBeBool(expr[0]) ? expr[0].typeCheck() : expr[1].typeCheck() ),
              ( canBeBool(expr[0]) ? expr[0].isArray() : expr[1].isArray() )
            );
          return SymTable.Type.BOOL;
        }
      case 9: // ternary expression
        if ( !canBeBool(expr[0]) )
          throw new ConditionExpectedException(expr[0].typeCheck(), expr[0].isArray());
        if ( !expr[1].typeCheck().equals(expr[2].typeCheck()))
          throw new TernaryResultException();
        return expr[1].typeCheck(); 
      case 10: // function call with args return value
        SymTable.Var fun = table.getVar(id);
        if (!fun.isFunction())
          throw new UndefinedFunctionException(id);
        if (fun.arguments.size() != args.size())
          throw new ArgListException(id);
        for (int i = 0; i < args.size(); i++) {
          if (!canAssignFrom(args.get(i), fun.arguments.get(i)))
            throw new ArgListException(
              id, i,
              fun.arguments.get(i).typeCheck(),
              fun.arguments.get(i).isArray(),
              args.get(i).typeCheck(), 
              args.get(i).isArray()
            );
        }
        return table.getVar(id).typeCheck();
      case 11: // function call no args return value
        if (!table.getVar(id).isFunction())
          throw new UndefinedFunctionException(id);
        return table.getVar(id).typeCheck();
    }
    return null;
  }
}