import java_cup.runtime.*;

%%

%cup
%line
%column
%unicode
%class Lexer

%{

Symbol newSym(int tokenId) {
	return new Symbol(tokenId, yyline, yycolumn);
}

Symbol newSym(int tokenId, Object value) {
	return new Symbol(tokenId, yyline, yycolumn, value);
}

%}

slash	= \\
letter	= [A-Za-z]
digit	= [0-9]
id	= {letter}[{letter}{digit}]*

intlit	= {digit}+
floatlit = {intlit}\.{intlit}
char = [^\\\n\t\"\']|\\.
str = {char}*
charlit = \'{char}\'
strlit = \"{str}\"

comment	= {slash}{slash}.*\n
multi_comment = {slash}\*(\\[^\*]|[^\\])*\*{slash}
whitespace = [ \n\t\r]



%%
//Lex Rules

class					{return newSym(sym.CLASS, "class");}
else					{return newSym(sym.ELSE, "else");}
if						{return newSym(sym.IF, "if");}
while					{return newSym(sym.WHILE, "while");}
return 				{return newSym(sym.RETURN, "return");}
";"						{return newSym(sym.SEMI, ";");}
"="   				{return newSym(sym.ASSIGN, "=");}
","						{return newSym(sym.COMMA, ",");}
"("						{return newSym(sym.LPAREN, "(");}
")"						{return newSym(sym.RPAREN, ")");}
"["						{return newSym(sym.LSQR, "[");}
"]"						{return newSym(sym.RSQR, "]");}
"{"						{return newSym(sym.LCURLY, "{");}
"}"						{return newSym(sym.RCURLY, "}");}
"~"						{return newSym(sym.NOT, "~");}
"?"						{return newSym(sym.QUESTION, "?");}
":"						{return newSym(sym.COLON, ":");}
read					{return newSym(sym.READ, "read");}
print					{return newSym(sym.PRINT, "print");}
printline			{return newSym(sym.PRINTLN, "printline");}
"++"					{return newSym(sym.INC, "++");}
"--"					{return newSym(sym.DEC, "--");}
"*"						{return newSym(sym.MULTI, "*");}
"/"						{return newSym(sym.DIV, "/");}
"+"						{return newSym(sym.PLUS, "+");}
"-"						{return newSym(sym.MINUS, "-");}
"<"						{return newSym(sym.LT, "<");}
">"						{return newSym(sym.GT, ">");}
"=="					{return newSym(sym.EQ, "==");}
"<="					{return newSym(sym.LTE, "<=");}
">="					{return newSym(sym.GTE, ">=");}
"<>"					{return newSym(sym.NE, "<>");}
"||"					{return newSym(sym.OR, "||");}
"&&"					{return newSym(sym.AND, "&&");}
true					{return newSym(sym.TRUE, "true");}
false					{return newSym(sym.FALSE, "false");}
void					{return newSym(sym.VOID, "void");}
int						{return newSym(sym.INT, "int");}
float					{return newSym(sym.FLOAT, "float");}
bool					{return newSym(sym.BOOL, "bool");}
char					{return newSym(sym.CHAR, "char");}
final					{return newSym(sym.FINAL, "final");}
{intlit}			{return newSym(sym.INTLIT, yytext());}
{floatlit}		{return newSym(sym.FLOATLIT, yytext());}
{charlit}			{return newSym(sym.CHARLIT, yytext());}
{strlit}			{return newSym(sym.STRLIT, yytext());}
{id}					{return newSym(sym.ID, yytext());}
{whitespace}	{/* whitespace */}
{comment}			{/* comment */}
{multi_comment}  {/* multiline comment */}

. {System.out.println("Illegal character, " + yytext() + " line:" + yyline + " col:" + yychar);}
 
