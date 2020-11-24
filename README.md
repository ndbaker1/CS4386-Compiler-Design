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