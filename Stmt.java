import java.util.ArrayList;

class Stmt extends BaseToken {
  int typeNumber;
  boolean isLoop, hasSemi;
  ArrayList<Fielddecl> fielddecls;
  ArrayList<Stmt> stmts;
  ArrayList funclist;
  ArrayList<Expr> args;
  Expr expr;
  Stmt stmt, elsestmt;
  Name name;
  String id;
  String unaryOp;
  
  public Stmt(Expr e, Stmt st, Stmt elsest) { // PROD 1 - if
    expr = e;
    stmt = st;
    elsestmt = elsest;
    typeNumber = 0;
  }
  public Stmt(String id, ArrayList<Expr> ag, boolean func) { // PROD 8 - id(args)
    this.id = id;
    args = ag;
    typeNumber = 1;
  }
  public Stmt(ArrayList<Fielddecl> fs, ArrayList<Stmt> sts, boolean semi) { // PROD 12 - block
    fielddecls = fs;
    stmts = sts;
    hasSemi = semi;
    typeNumber = 2;
  }
  public Stmt(Expr e, Stmt st) { // PROD 2 - while
    expr = e;
    stmt = st;
    typeNumber = 3;
  }
  public Stmt(Name n, Expr e) { // PROD 3 - assign
    name = n;
    expr = e;
    typeNumber = 4;
  }
  public Stmt(String func, ArrayList lst) { // PROD 4,5,6 - read, print, printline
    id = func;
    funclist = lst;
    typeNumber = ( func == "read" ? 5 : ( func == "print" ? 6 : 7 ) );
  }
  public Stmt(Name n, String unary) { // PROD 10,11 - INC and DEC
    name = n;
    unaryOp = unary;
    typeNumber = 8;
  }
  public Stmt(Expr e) { // PROD 9 - return expr
    expr = e;
    typeNumber = 9;
  }
  public Stmt(String id) { // PROD 7 - id()
    this.id = id;
    typeNumber = 10;
  }
  public Stmt() { // PROD 8 - return 
    typeNumber = 11;
  }

  public String toString(int depth) {
    switch (this.typeNumber) {
      case 0:
        return getTabs(depth) +
          "if (" + expr.toString() + ")\n" +
          ( stmt.typeNumber == 2 ? stmt.toString(depth) : getTabs(depth) +"{\n" + stmt.toString(depth+1) + "\n"+ getTabs(depth) + "}" ) +
          ( elsestmt != null ? "\n" + getTabs(depth) + "else\n" + ( elsestmt.typeNumber == 2 ? elsestmt.toString(depth) : getTabs(depth) +"{\n" + elsestmt.toString(depth+1) + "\n"+ getTabs(depth) + "}") : "" );    
      case 1:
        String list = "";
        for (Expr e: args) {
          list += e.toString() + ", ";
        } list = list.substring(0, list.length() > 0 ? list.length() - 2 : 0);
        return getTabs(depth) + id + "(" + list + ");";
      case 2:
        String result = "";
        for (Fielddecl f: fielddecls) {
          result += f.toString(depth+1) + "\n";
        }
        for (Stmt st: stmts) {
          result += st.toString(depth+1) + "\n";
        }
        return getTabs(depth) + "{\n" + result + getTabs(depth) + "}";
      case 3:
        return getTabs(depth) +
          "while (" + expr.toString() + ")\n" + stmt.toString(stmt.typeNumber == 2 ? depth : depth+1) + "\n";
      case 4:
        return getTabs(depth) +
          name.toString() + " = " + expr.toString() + ";"; 
      case 5:
        list = "";
        for (Name n: (ArrayList<Name>)funclist) {
          list += n.toString() + ", ";
        } list = list.substring(0, list.length() > 0 ? list.length() - 2 : 0);
        return getTabs(depth) + id + "(" + list + ");";
      case 6:
        list = "";
        for (Expr e: (ArrayList<Expr>)funclist) {
          list += e.toString() + ", ";
        } list = list.substring(0, list.length() > 0 ? list.length() - 2 : 0);
        return getTabs(depth) + id + "(" + list + ");";
      case 7:
        list = "";
        for (Expr e: (ArrayList<Expr>)funclist) {
          list += e.toString() + ", ";
        } list = list.substring(0, list.length() > 0 ? list.length() - 2 : 0);
        return getTabs(depth) + id + "(" + list + ");";
      case 8:
        return getTabs(depth) + name.toString() + unaryOp + ";";
      case 9:
        return getTabs(depth) + "return " + expr.toString() + ";"; 
      case 10:
        return getTabs(depth) + id + "();";
      case 11:
        return getTabs(depth) + "return;";
      default:
        return "";
    }
  }

  public boolean isReturn() {
    return this.typeNumber == 9 | this.typeNumber == 11;
  }

  public SymTable.Type typeCheck() throws Exception {
    switch (this.typeNumber) {
      case 0: // if 
        if (!canBeBool(expr))
          throw new ConditionExpectedException(expr.typeCheck(), expr.isArray());
        table.createScope();
        stmt.typeCheck();
        table.destroyScope();
        if ( elsestmt != null ) {
          table.createScope();
          stmt.typeCheck();
          table.destroyScope();
        }
        break;
      case 1: // function call with args
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
        break;
      case 2: // statement block
        table.createScope();
        for (Fielddecl f: fielddecls)
          f.typeCheck();
        for (Stmt st: stmts)
          st.typeCheck();
        table.destroyScope();
        break;          
      case 3: // while
        if (!canBeBool(expr))
          throw new ConditionExpectedException(expr.typeCheck(), expr.isArray());
        table.createScope();
        stmt.typeCheck();
        table.destroyScope();
        break;
      case 4: // assignment
        if (!canAssignFrom(this.expr, this.name))
          throw new AssignmentTypeMismatchException(
            name.typeCheck(),
            name.isArray(),
            expr.typeCheck(),
            expr.isArray()
          );
        if (table.getVar(name.id).isFinal())
          throw new CannotModifyFinalException(name.id);
        break;
      case 5: // read
        for (Name n: (ArrayList<Name>)funclist) {
          if (n.isArray() || n.isFinal())
            throw new Exception("cannot read an array or final.");
        }
        break;
      case 6: case 7:// print and printline
        for (Expr e: (ArrayList<Expr>)funclist) {
          e.typeCheck();
          if (e.isArray() || e.isFunction())
            throw new Exception("cannot print an array or function.");
        }
        break;
      case 8: // INC and DEC
        if (!canBeFloat(name))
          throw new UnaryArithmeticException();
        if (table.getVar(name.id).isFinal())
          throw new CannotModifyFinalException(name.id);
        break;
      case 9: // return with value
        return expr.typeCheck();
      case 10: // function call no args
        if (!table.getVar(id).isFunction())
          throw new UndefinedFunctionException(id);
        break;
      case 11: // return;
        return SymTable.Type.VOID;
    }
    return null;
  }
}