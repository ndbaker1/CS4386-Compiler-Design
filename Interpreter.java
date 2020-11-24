import java.io.*;
import java_cup.runtime.*;

public class Interpreter {
  public static void main(String args[]) throws Exception {
    Reader reader = null;

    if (args.length == 1) {
      File input = new File(args[0]);
      if (!input.canRead()) {
        System.out.println("Error: could not read <" + input + ">");
      } reader = new FileReader(input);
    } else {
      reader = new InputStreamReader(System.in);
    }

    Lexer scanner = new Lexer(reader);
    parser parser = new parser(scanner);
    Program program = (Program) parser.parse().value;

    System.out.println(program.toString(0));
    System.out.println();

    try { // type check
      program.typeCheck();
      System.out.println("[SUCCESS] Type checking complete!");
    } catch (Exception e) {
      System.out.println("[ERROR] " + e.getMessage());
    }
    
    
    /* Can be implimented in the future */
    // program.execute();
    
    System.out.println("----------------------------------------------");
  }
}