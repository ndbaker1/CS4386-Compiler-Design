abstract class BaseToken {
  protected static SymTable table;

  protected String getTabs(int num) {
    String tabs = "";
    for (int i = 0; i < num; i++)
      tabs += "\t";
    return tabs;
  }

  protected SymTable.Type getType(String t) {
    return
      t.equals("int") ? SymTable.Type.INT :
      t.equals("bool") ? SymTable.Type.BOOL :
      t.equals("char") ? SymTable.Type.CHAR :
      t.equals("float") ? SymTable.Type.FLOAT :
      t.equals("string") ? SymTable.Type.STRING :
      t.equals("void") ? SymTable.Type.VOID :
      null;
  }


  protected boolean canAssignFrom(Checkable startType, Checkable targetType) throws Exception {
    return
      startType.isArray() && targetType.isArray() && startType.typeCheck().equals(targetType.typeCheck()) ||
      !startType.isArray() && !targetType.isArray() && canConvertFrom(startType.typeCheck(), targetType.typeCheck());
  }
    
  protected boolean canConvertFrom(SymTable.Type RHStype, SymTable.Type LHStype) {
    return RHStype.equals(SymTable.Type.INT)
      ? LHStype.equals(SymTable.Type.BOOL) || LHStype.equals(SymTable.Type.FLOAT) || LHStype.equals(SymTable.Type.INT)
      : RHStype.equals(LHStype);
  }
    
  protected boolean canBeBool(Checkable expr) throws Exception {
    return !expr.isArray() && ( expr.typeCheck().equals(SymTable.Type.BOOL) || expr.typeCheck().equals(SymTable.Type.INT) );
  }
  protected boolean canBeFloat(Checkable expr) throws Exception {
    return !expr.isArray() && ( expr.typeCheck().equals(SymTable.Type.FLOAT) || expr.typeCheck().equals(SymTable.Type.INT) );
  }


  class AssignmentTypeMismatchException extends Exception {
    public AssignmentTypeMismatchException(SymTable.Type lhs, boolean lhsArr, SymTable.Type rhs, boolean rhsArr) {
      super("lhs type:<"+lhs+(lhsArr?"[]":"")+"> does not match rhs type:<"+rhs+(rhsArr?"[]":"")+">.");
    }
    public AssignmentTypeMismatchException(SymTable.Type lhs, SymTable.Type rhs) {
      this(lhs, false, rhs, false);
    }
  }

  class ReturnTypeMismatchException extends Exception {
    public ReturnTypeMismatchException(String function, SymTable.Type returnType, SymTable.Type expectedType) {
      super("return type:<"+returnType+"> does not match expected type:<"+expectedType+"> for function:<"+function+">.");
    }
  }

  class MissingReturnException extends Exception {
    public MissingReturnException(String function) {
      super("return statement required for function:<"+function+">.");
    }
  }

  class RedefinedVariableException extends Exception {
    public RedefinedVariableException(String id) {
      super("id:<"+id+"> is already defined within the current scope.");
    }
  }

  class CannotModifyFinalException extends Exception {
    public CannotModifyFinalException(String id) {
      super("id:<"+id+"> is final and cannot be modified.");
    }
  }

  class ConditionExpectedException extends Exception {
    public ConditionExpectedException(SymTable.Type type, boolean isArray) {
      super("condition type:<"+type+(isArray?"[]":"")+"> is not coercible to type:<"+SymTable.Type.BOOL+">.");
    }
  }

  class UnaryArithmeticException extends Exception {
    public UnaryArithmeticException() {
      super("type:<"+SymTable.Type.INT+"|"+SymTable.Type.FLOAT+"> expected for unary arithmetic operators.");
    }
  }
  class UnaryLogicalException extends Exception {
    public UnaryLogicalException() {
      super("type:<"+SymTable.Type.BOOL+"> expected for unary logical operators.");
    }
  }

  class SignedValueException extends Exception {
    public SignedValueException() {
      super("type:<"+SymTable.Type.INT+"|"+SymTable.Type.INT+" expected in order to utilize a sign prefix.");
    }
  }

  class TernaryResultException extends Exception {
    public TernaryResultException() {
      super("ternary expression results must be homogeneous.");
    }
  }
  class UndefinedFunctionException extends Exception {
    public UndefinedFunctionException(String s) {
      super("function <"+s+"> has not been defined.");
    }
  }

  class ArgListException extends Exception {
    public ArgListException(String func) {
      super("function <"+func+"> does not have the correct number of arguments.");
    }
    public ArgListException(String func, int index, SymTable.Type idType, boolean idArr, SymTable.Type argType, boolean argArr) {
      super("argument["+index+"] of function:<"+func+"> expects type:<"+idType+(idArr?"[]":"")+">, but got type:<"+argType+(argArr?"[]":"")+">.");
    }
    public ArgListException(String func, int index, SymTable.Type idType, SymTable.Type argType) {
      this(func, index, idType, false, argType, false);
    }
  }

  class StringOperationException extends Exception {
    public StringOperationException() {
      super("invalid string operation.");
    }
  }

  class BinaryExpressionException extends Exception {
    public BinaryExpressionException(String op, SymTable.Type type, boolean isArray) {
      super("binary operator:<"+op+"> does not accept type:<"+type+(isArray?"[]":"")+">.");
    }
  }

}