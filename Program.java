class Program extends BaseToken {
  String className;
  Memberdecls memberdecls;
  public Program(String id, Memberdecls m) {
    className = id;
    memberdecls = m;
    table = new SymTable();
  }
  public String toString(int depth) {
    return "class " + className + " {\n" + memberdecls.toString(depth+1) + "}";
  }

  public void typeCheck() throws Exception {
    memberdecls.typeCheck();
  }
}