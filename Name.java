class Name extends BaseToken implements Checkable {
  String id;
  Expr index;
  public Name(String id) {
    this.id = id;
    this.index = null;
  }
  public Name(String id, Expr e) {
    this.id = id;
    this.index = e;
  }
  public String toString() {
    return id + ( hasIndex() ? "[" + index.toString() + "]" : "" );
  }

  private boolean hasIndex() {
    return index != null;
  }

  public boolean isArray() throws Exception {
    return table.getVar(id).isArray() && !hasIndex();
  }
  public boolean isFinal() throws Exception {
    return table.getVar(id).isFinal();
  }
  public boolean isFunction() throws Exception {
    return table.getVar(id).isFunction();
  }
  
  
  public SymTable.Type typeCheck() throws Exception {
    if (!hasIndex() || index.typeCheck().equals(SymTable.Type.INT))
      return table.getVar(id).typeCheck();
    else 
      throw new Exception("array index must be an INT!");
  }
}