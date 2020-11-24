class Fielddecl extends BaseToken {
  boolean isFinal;
  String type, id;
  int arrLength;
  Expr opex;
  int declType;
  public Fielddecl(String type, String id, Expr opex, boolean isFinal) {
    this.type = type;
    this.id = id;
    this.opex = opex;
    this.isFinal = isFinal;
    declType = 0;
  }
  public Fielddecl(String type, String id, int len) {
    this.type = type;
    this.id = id;
    this.arrLength = len;
    declType = 1;
  }
  public String toString(int depth) {
    switch (declType) {
      case 0:
        return getTabs(depth) + ( isFinal ? "final " : "" ) + type + " " + id  + ( opex != null ? " = "+ opex.toString() : "" ) + ";";  
      case 1:
        return getTabs(depth) + type + " " + id + "[" + arrLength + "]" + ";";
      default:
        return "";
    }
  }

  public void typeCheck() throws Exception {
    switch (declType) {
      case 0: // var def with optional assign
        if (opex != null)
          if (!canConvertFrom(opex.typeCheck(), getType(type)))
            throw new AssignmentTypeMismatchException(getType(type), opex.typeCheck());
        if (!table.add(id, (isFinal ? "final" : ""), getType(type), null))
          throw new RedefinedVariableException(id);
        break;
      case 1: // array decl def
        if (!table.add(id, "array", getType(type), null))
          throw new RedefinedVariableException(id);
        break;
    }
  }
}