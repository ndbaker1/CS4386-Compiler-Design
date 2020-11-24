import java.util.ArrayList;

class Methoddecl extends BaseToken {
  ArrayList<Argdecl> argdecls;
  ArrayList<Fielddecl> fielddecls;
  ArrayList<Stmt> stmts;
  String type, id;
  boolean hasSemi;
  public Methoddecl(String type, String id, ArrayList<Argdecl> as, ArrayList<Fielddecl> fs, ArrayList<Stmt> sts, boolean semi) {
    this.type = type;
    this.id = id;
    argdecls = as;
    fielddecls = fs;
    stmts = sts;
    hasSemi = semi;
  }
  public String toString(int depth) {
    String args = "";
    for (Argdecl a: argdecls) {
      args += a.toString() + ", ";
    } args = args.substring(0, args.length() > 0 ? args.length()-2 : 0);

    String result = getTabs(depth) + type + " " + id + "(" + args + ")" + " {\n";
    for (Fielddecl f: fielddecls) {
      result += f.toString(depth+1) + "\n";
    }
    for (Stmt st: stmts) {
      result += st.toString(depth+1) + "\n";
    } result += getTabs(depth) + "}" + ( hasSemi ? ";" : "" );
    return result;
  }

  public void typeCheck() throws Exception {
    // create list of params for function definition
    ArrayList<SymTable.Arg> params = new ArrayList<SymTable.Arg>();
    for (Argdecl arg: argdecls)
      params.add(table.newArg(getType(arg.type), arg.isArray));
    // define function id in scope with argdecls
    if (!table.add(id, "function", getType(type), params))
      throw new RedefinedVariableException(id);
    // step into new method scope
    // add vars to scope
    table.createScope();
    for (Argdecl arg: argdecls)
      arg.typeCheck();
    for (Fielddecl f: fielddecls)
      f.typeCheck();
    // verify whether return is needed for function
    boolean needsReturn = !getType(type).equals(SymTable.Type.VOID);
    // typecheck inner statements
    for (Stmt stmt: stmts) {
      if (stmt.isReturn()) {
        // satisfy return requirement
        needsReturn = false;
        // check that returntype == function type
        if (!canConvertFrom(stmt.typeCheck(), getType(type)))
          throw new ReturnTypeMismatchException(id, stmt.typeCheck(), getType(type));
      } else stmt.typeCheck();
    }
    // throw exception if a return statement was not found
    if (needsReturn)
      throw new MissingReturnException(id);
    // step out of method scope
    table.destroyScope();
  }
}