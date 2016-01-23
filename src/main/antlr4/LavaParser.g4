parser grammar LavaParser;

@header {
package org.ine5426.lava.generated;
}

options {
tokenVocab=LavaLexer; 
}

// DONE Syntatic expressions: (let's do it incrementally):
 
program
	: NEWLINE? statement+ 
	;
	
statement
	: simple_statement NEWLINE  #MainFunctionStatement
	| block_statement           #MainClassStatement
	| if_stmt        	  				#MainFunctionStatement
	| while_stmt								#MainFunctionStatement
	| class_def                 #ClassDefinition
	;

// Statements that don't start block scopes
simple_statement
	: println
	| expr
	| declaration
	| assignment
	| function_return
	| pass_stmt
	| new_operator
	| method_call
	;
	
block_statement
	: function
//	| if_stmt
	;

// Block delimiter
suite
	: NEWLINE INDENT (statement+ (NEWLINE | INDENT NEWLINE DEDENT)*)+ DEDENT
	;
	
// ----------------------------------------------------------------------------
// Class definitions and operators

method_call
	: objName=ID DOT funcName=ID LPARENT arguments=expression_list RPARENT
	;
	
new_operator
	: NEW className=ID LPARENT expression_list RPARENT
	;
	
class_def
	: CLASS className=ID COLON block=suite
	;
	
// ----------------------------------------------------------------------------
// Loops and conditionals

while_stmt
	: WHILE LPARENT condition=cond_expr RPARENT COLON onTrue=suite
	;

// An If-Elsif-Else clause
if_stmt
	: IF LPARENT condition=cond_expr RPARENT COLON onTrue=suite  #OnlyIF
	| IF LPARENT condition=cond_expr RPARENT COLON onTrue=suite ELSE COLON onFalse=suite #IfElse
	;
	
// ----------------------------------------------------------------------------
// Functions and methods

pass_stmt
	: PASS
	;
	
function_call
	: funcName=ID LPARENT arguments=expression_list RPARENT
	;
	
expression_list
	: expressions+=expr (COMMA expressions+=expr)*
	|
	;
	
function_return
	: RETURN (returnExpr=expr)?
	;
	
// Function definition
function
	: funcType=ftype funcName=ID LPARENT params=parameter_list RPARENT COLON block=suite
	;
	
parameter_list
	: varDeclarations+=declaration (COMMA varDeclarations+=declaration)*
	|
	;
	
// Function type
ftype
	: type | VOID_TYPE
	;
	
// For debugging purposes, we define a println function:
println
	: PRINTLN LPARENT arg=expr RPARENT;
	
// ----------------------------------------------------------------------------
// Variable definition and assigments
	
// Variable assigment
assignment
	: varName=ID ASSIGN expression=expr
	;	

// Variable declaration
declaration
	: typeName=type varName=ID (ASSIGN expression=expr)?
	;	
	
type
	: typeName=PRIMITIVE_TYPE  #typePrimitive
	| typeName=ID              #typeDeclared
	;
	
// ----------------------------------------------------------------------------
// Expressions - Logic and booleans
	
// Condition expression
cond_expr
	: NOT condition=cond_expr				#NotExpr	
	| right=cond_expr AND left=cond_expr 	#AndExpr
	| right=cond_expr OR left=cond_expr		#OrExpr
	| LPARENT cond_expr RPARENT				#SubCondExp
	| bexpr									#Bexpression
	;
	
// Boolean expression
bexpr
	: right=expr EQUAL  left=expr 	#Equal
	| right=expr NEQUAL left=expr 	#NotEqual
	| right=expr LT     left=expr 	#LessThan
	| right=expr LTE    left=expr	#LessThanEqual
	| right=expr GT     left=expr 	#GreaterThan
	| right=expr GTE    left=expr 	#GreaterThanEqual
	| bool_lit						#BoolLiteral
	| LPARENT bexpr RPARENT			#SubBoolExp
	| expr							#ExprRule
	;
	
bool_lit
	: bool=TRUE 					
	| bool=FALSE	
	;
	
// ----------------------------------------------------------------------------
// Expressions - Arithmetics

expr
	: left=expr SLASH right=expr #Div
	| left=expr STAR  right=expr #Mult
	| left=expr PLUS  right=expr #Plus
	| left=expr MINUS right=expr #Minus
	| LPARENT expr RPARENT #Subexpr
	| fac=factor           #exprFactor
	| varName=ID    	     #exprVariable
	| strValue=STRING 		 #exprString
	| op=new_operator	  	 #exprNew
	| function_call        #exprFuncCall
	| m=method_call				 #exprMethodCall
	;
	
// A positive or negative number
factor
	: signal=MINUS? num=number
	| signal=PLUS? num=number
	;	
	
// A Literal number with no signal
number
	: num=INTEGER
	;
