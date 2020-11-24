interface Checkable {
  public boolean isArray() throws Exception;
  public boolean isFinal() throws Exception;
  public boolean isFunction() throws Exception;
  public SymTable.Type typeCheck() throws Exception;
}