import java.io.*;
import java_cup.runtime.*;

public class LexerRules {
	public static void main (String args[]) {
		Symbol sym;
		try {
			Lexer lexer = new Lexer(new FileReader(args[0]));
			for (sym = lexer.next_token(); sym.sym != 0; sym = lexer.next_token()) {
				System.out.println(	"Token " + sym + ", has value of: " + sym.value + " lin: " + sym.left + " col: " + sym.right); 
			}
		} catch (Exception e) {
			System.err.println("Error!");
		}
	}
}
