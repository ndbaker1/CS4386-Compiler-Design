# CS4386-Compiler-Design-Project

Nicholas Baker
CS 4386.001

Full Semester Long Project to Design a Basic Compiler in **Java** with JFlex and Cup

## Running
> All files in the **testfiles** directory are used for the make tests.

`make lexerTests` prints tokens lexed by the compiler

`make parserTests` prints the code back in the structure as it was parsed into an AST

`make typeCheckingTests` prints the parser results plus the result of typechecking the AST

`make interpreterTests` -- can be implimented later, or in favor of a code generation algorithm

## Grammar
**Program** → class id { Memberdecls }  
**Memberdecls** → Fielddecls Methoddecls  
**Fielddecls** → Fielddecl Fielddecls | λ  
**Methoddecls** → Methoddecl Methoddecls | λ  
**Fielddecl** → Optionalfinal Type id Optionalexpr ; | Type id [ intlit ] ;  
**Optionalfinal** → final | λ  
**Optionalexpr** → = Expr | λ  
**Methoddecl** → Returntype id ( Argdecls ) { Fielddecls Stmts } Optionalsemi  
**Optionalsemi** → ; | λ  
**Returntype** → Type | void  
**Type** → int | char | bool | float  
**Argdecls** → ArgdeclList | λ  
**ArgdeclList** → Argdecl , ArgdeclList | Argdecl  
**Argdecl** → Type id | Type id [ ]  
**Stmts** → Stmt Stmts | λ  
**Stmt** → if ( Expr ) Stmt OptionalElse | while ( Expr ) Stmt | Name = Expr ;  
| read ( Readlist ) ; | print ( Printlist ) ; | printline ( Printlinelist ) ;  
| id ( ) ; | id ( Args ) ; | return ; | return Expr ; | Name ++ ; | Name -- ;  
| { Fielddecls Stmts } Optionalsemi  
**OptionalElse** → else Stmt | λ  
**Name** → id | id [ Expr ]  
**Args** → Expr , Args | Expr  
**Readlist** → Name , Readlist | Name  
**Printlist** → Expr , Printlist | Expr  
**Printlinelist** → Printlist | λ  
**Expr** → Name | id ( ) | id ( Args ) | intlit | charlit | strlit | floatlit | true | false  
| ( Expr ) | ~ Expr | - Expr | + Expr | ( Type ) Expr | Expr Binaryop Expr | ( Expr ? Expr : Expr )  
**Binaryop** → * | / | + | - | < | > | <= | >= | == | <> | || | &&  


## Type Rules
**Implicit Coercion**  
- int
  - int → bool (0 → false, !0 → true)
  - int → float
- String
  - All types can be coerced to string with the exception of arrays
- Operators
  - = (assignment)
    - Left hand side type MUST equal Right hand side type (requires expr eval) (coercion is allowed)
  - X() (function)
    - The type of the function when used in an expression is its return type
    - Number of arguments used in call must match number used in definition
      - Each argument in call must have type corresponding to the type in the method definition (coercion is allowed)
  - if()
    - Condition must be a bool(or coercible to bool)
  - while()
    - Condition must be a bool(or coercible to bool)
  - "++"
    - Variable must be int or float (can’t be final)
  - "--"
    - Variable must be int or float (can’t be final)
  - X[Y] (array access)
    - Y must be an int
  - "+"
    - May be used on int or float, produces int or float respectively
  - "-"
    - May be used on int or float, produces int or float respectively
  - "~"
    - Used on bool (or coercible to bool), produces bool
  - " + - * / "
    - Int X int
      - Produces int
    - Int X float (float X int)
      - Produces float
    - Float X float
      - Produces float
  - “String lit” + “String lit”
    - "+" can concatenate 2 strings
  - < > <= >= == <>
    - Compare a combination of int, float
    - Produces a bool
  - || &&
    - Compare two bools (or coercible to bool)
    - Produces bool
  - X ? Y : Z
    - X must be a bool or coercible to bool
    - Type of Y must equal type of Z
  - Read
    - Does not work on final
    - Does not work on an array(non-dereferenced)
    - Does not work on a function
  - Print
    - Does not work on an array(non-dereferenced)
    - Does not work on any void type