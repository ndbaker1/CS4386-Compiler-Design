import java.io.*;
import java_cup.runtime.*;

public class ParserTest {
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
    Program program = null;

    try {
      program = (Program) parser.parse().value;
      // program = (Program) parser.debug_parse().value;
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println(program.toString(0));
  }
}