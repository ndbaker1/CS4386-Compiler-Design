class Argdecl extends BaseToken {
  String type, id;
  boolean isArray;
  public Argdecl(String type, String id, boolean isArray) {
    this.type = type;
    this.id = id;
    this.isArray = isArray;
  }
  public String toString() {
    return type + " " + id + ( isArray ? "[]" : "" );
  }
  public void typeCheck() throws Exception {
    // define function parameters in current scope
    if (!table.add(id, (isArray ? "array" : ""), getType(type), null))
      throw new RedefinedVariableException(id);
  }
}