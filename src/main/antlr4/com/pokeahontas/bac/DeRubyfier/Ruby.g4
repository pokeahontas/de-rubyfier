grammar Ruby;

program : expression_list;

expression_list : expression terminator
                | expression_list expression terminator
                | terminator
                ;

expression : function_definition
           //| function_call_chain
           | function_inline_call
           | require_block
           | if_statement
           | unless_statement
           | rvalue
           | return_statement
           | while_statement
           | for_statement

           | class_definition
           | module_definition
           | each_do_statement
           | begin_statement
           | rescue_statement
           ;

class_definition: CLASS class_name crlf class_definition_body END
                //| CLASS class_name crlf END
                  | CLASS BIT_SHL self crlf class_definition_body END
                ;

class_name: id_class class_name_include_list? LESS super_class_name //module und class
          | id_class class_name_include_list?;

super_class_name: id_class class_name_include_list?;

class_name_include_list : (DCOLON id_class)+;

class_definition_body: expression_list; // für module und class

module_definition: MODULE class_name crlf class_definition_body END
                //| CLASS class_name crlf END
                  | MODULE BIT_SHL self crlf class_definition_body END
                ;

self: SELF;

id_class: ID_CLASS;

id_spaceship : SPACESHIP;

id_instance_var : ID_INSTANCE_VAR;

id_class_var : ID_CLASS_VAR;

plus : PLUS;

minus : MINUS;

equal : EQUAL;

empty_array : LEFT_SBRACKET RIGHT_SBRACKET;

bit_shl: BIT_SHL;

symbol_result : COLON (id | CLASS | MODULE | SELF)
              ;

hash_assignment : symbol_result op=HASH ( int_result | float_result | string_result | var_list | dynamic_result | symbol_result | bool_t | function_call | array_selector | id_class_var | id_instance_var | array_definition | ternary_statement)
            | string_result op=HASH ( int_result | float_result | string_result | var_list | dynamic_result | symbol_result | bool_t | function_call | array_selector | id_class_var | id_instance_var | array_definition | ternary_statement)
            ;
hash_result : id COLON LEFT_CBRACKET hash_result RIGHT_CBRACKET
            | id COLON string_result
            | id COLON int_result
            | id COLON bool_t
            | id COLON id_constant
            | id COLON float_result
            | id COLON symbol_result
            | id COLON function_call_chain
            | id COLON array_definition
            | id COLON id_instance_var
            | id COLON id_class_var
            | id COLON hour_result
            | CLASS COLON string_result   // NUR WEGEN PROBLEM MIT 'class: blabla' WEIL PARSER KLASSENDEFINITION ERWARTET
            | symbol_result HASH string_result
            | symbol_result HASH int_result
            | symbol_result HASH bool_t
            | symbol_result HASH float_result
            ;

hour_result : int_t DOT id;

each_do_statement : each_do_statement_header crlf statement_body END;

each_do_statement_header : (id | id_instance_var | array_selector) DOT EACH DO BIT_OR id_list BIT_OR;

id_list : id (COMMA id)*;

var_list : (id | id_class_var | id_instance_var | id_class | self) (DOT id)*? ;

begin_statement : BEGIN crlf statement_body END;

rescue_statement : RESCUE ((id_class class_name_include_list?) | (id_class? HASH id))? crlf statement_body;

ternary_statement : LEFT_RBRACKET? cond_expression '?' (all_result | function_call_chain) COLON crlf? (all_result | function_call_chain) RIGHT_RBRACKET?;

global_get : var_name=lvalue op=ASSIGN global_name=id_global;

global_set : global_name=id_global op=ASSIGN result=all_result;

global_result : id_global;

function_inline_call : function_call;

require_block : REQUIRE literal_t;

function_definition : function_definition_header function_definition_body END;

function_definition_body : expression_list;

function_definition_header : DEF function_name crlf
                           | DEF function_name function_definition_params crlf
                           | DEF self DOT function_name crlf
                           | DEF self DOT function_name function_definition_params crlf
                           | DEF function_name ASSIGN function_definition_params crlf


                           /*
                           | DEF self DOT LEFT_SBRACKET RIGHT_SBRACKET NOT? function_definition_params crlf
                           | DEF LEFT_SBRACKET RIGHT_SBRACKET function_definition_params crlf
                           | DEF id_class DOT function_name NOT? function_definition_params? crlf
                           | DEF function_name function_definition_params //TODO STUPID CASE WERE COMMENT BESIDES DECLARATION
                           | DEF function_name*/
                           ;

function_name : id_function
              | id_function_mark
              | id_spaceship
              | plus
              | minus
              | equal
              | empty_array
              | bit_shl
              | id_class
              | id
              ;

function_definition_params : LEFT_RBRACKET RIGHT_RBRACKET
                           | LEFT_RBRACKET function_definition_params_list RIGHT_RBRACKET
                           | function_definition_params_list
                           ;

function_definition_params_list : function_definition_param_id
                                | function_definition_params_list COMMA function_definition_param_id
                                ;

function_definition_param_id : id;

return_statement : RETURN (all_result | function_call_chain | array_selector) (IF cond_expression)?;
//vielleicht so? getBla().getMe.fuckYou(1,2,3)

function_call_chain_pre : id_class_var DOT
                        | id_instance_var DOT
                        | id_class class_name_include_list? DOT
                        | self DOT
                        | string_result DOT
                        | array_definition DOT
                        | array_selector DOT
                        ;

function_call_chain : function_call_chain_pre? (function_call DOT)* function_call (IF cond_expression)?;

function_call : name=function_name LEFT_RBRACKET crlf? params=function_call_param_list crlf? RIGHT_RBRACKET
              | name=function_name params=function_call_param_list
              | name=function_name LEFT_RBRACKET RIGHT_RBRACKET

              | name=function_name
              ;

function_call_param_list : function_call_params;

function_call_params : function_param
                     | function_call_params COMMA crlf? function_param
                     ;

function_param : ( function_unnamed_param | function_named_param );

function_unnamed_param : ( int_result | float_result | string_result | dynamic_result
                       | bool_t | symbol_result | hash_assignment | hash_result | id_class_var | id_instance_var | var_list);

function_named_param : id op=ASSIGN ( int_result | float_result | string_result | dynamic_result
                     | bool_t | symbol_result | id_class_var | id_instance_var | var_list );

function_call_assignment : function_call;

all_result : ( int_result | float_result | string_result | dynamic_result | global_result | symbol_result);

elsif_statement : if_elsif_statement;

if_elsif_statement : ELSIF cond_expression crlf statement_body
                   | ELSIF cond_expression crlf statement_body else_token crlf statement_body
                   | ELSIF cond_expression crlf statement_body if_elsif_statement
                   ;

if_statement : IF cond_expression crlf statement_body END
             | IF cond_expression crlf statement_body else_token crlf statement_body END
             | IF cond_expression crlf statement_body elsif_statement END
             ;

unless_statement : UNLESS cond_expression crlf statement_body END
                 | UNLESS cond_expression crlf statement_body else_token crlf statement_body END
                 | UNLESS cond_expression crlf statement_body elsif_statement END
                 ;

while_statement : WHILE cond_expression crlf statement_body END;

for_statement : FOR LEFT_RBRACKET init_expression SEMICOLON cond_expression SEMICOLON loop_expression RIGHT_RBRACKET crlf statement_body END
              | FOR init_expression SEMICOLON cond_expression SEMICOLON loop_expression crlf statement_body END
              ;

init_expression : for_init_list;

all_assignment : ( int_assignment | float_assignment | string_assignment | dynamic_assignment );

for_init_list : for_init_list COMMA all_assignment
              | all_assignment
              ;

cond_expression : comparison_list;

loop_expression : for_loop_list;

for_loop_list : for_loop_list COMMA all_assignment
              | all_assignment
              ;

statement_body : statement_expression_list;

statement_expression_list : expression terminator
                          | RETRY terminator
                          | break_expression terminator
                          | statement_expression_list expression terminator
                          | statement_expression_list RETRY terminator
                          | statement_expression_list break_expression terminator
                          ;

assignment : var_id=lvalue op=ASSIGN rvalue
           | var_id=lvalue op=( PLUS_ASSIGN | MINUS_ASSIGN | MUL_ASSIGN | DIV_ASSIGN | MOD_ASSIGN | EXP_ASSIGN | BIT_SHL) rvalue
           ;

dynamic_assignment : var_id=lvalue op=ASSIGN dynamic_result
                   | var_id=lvalue op=( PLUS_ASSIGN | MINUS_ASSIGN | MUL_ASSIGN | DIV_ASSIGN | MOD_ASSIGN | EXP_ASSIGN | BIT_SHL) dynamic_result
                   ;

int_assignment : var_id=lvalue op=ASSIGN int_result
               | var_id=lvalue op=( PLUS_ASSIGN | MINUS_ASSIGN | MUL_ASSIGN | DIV_ASSIGN | MOD_ASSIGN | EXP_ASSIGN | BIT_SHL) int_result
               ;

float_assignment : var_id=lvalue op=ASSIGN float_result
                 | var_id=lvalue op=( PLUS_ASSIGN | MINUS_ASSIGN | MUL_ASSIGN | DIV_ASSIGN | MOD_ASSIGN | EXP_ASSIGN | BIT_SHL) float_result
                 ;

string_assignment : var_id=lvalue op=ASSIGN string_result
                  | var_id=lvalue op=(PLUS_ASSIGN | BIT_SHL) string_result
                  ;

initial_array_assignment : var_id=lvalue op=ASSIGN empty_array;

array_assignment : arr_def=array_selector op=ASSIGN arr_val=all_result;

array_definition : LEFT_SBRACKET array_definition_elements RIGHT_SBRACKET;  // z.b. foo[1,2,3]

array_definition_elements : ( int_result | dynamic_result | symbol_result | string_result | float_result | bool_t | array_definition | array_selector | var_list)
                          | array_definition_elements COMMA ( int_result | dynamic_result | symbol_result | string_result | float_result | bool_t | array_definition | array_selector | var_list)
                          ;

array_selector : (self DOT)? id (LEFT_SBRACKET ( int_result | dynamic_result | symbol_result | string_result | float_result | bool_t | array_definition | var_list | function_call_chain) RIGHT_SBRACKET)+        // z.b. foo[1]
               | id_global (LEFT_SBRACKET ( int_result | dynamic_result | symbol_result | string_result | float_result | bool_t | array_definition | var_list | function_call_chain) RIGHT_SBRACKET)+
               | id_constant (LEFT_SBRACKET ( int_result | dynamic_result | symbol_result | string_result | float_result | bool_t | array_definition | var_list | function_call_chain) RIGHT_SBRACKET)+
               | id_instance_var (LEFT_SBRACKET ( int_result | dynamic_result | symbol_result | string_result | float_result | bool_t | array_definition | var_list | function_call_chain) RIGHT_SBRACKET)+
               ;

dynamic_result : dynamic_result op=( MUL | DIV | MOD ) int_result
               | int_result op=( MUL | DIV | MOD ) dynamic_result
               | dynamic_result op=( MUL | DIV | MOD ) float_result
               | float_result op=( MUL | DIV | MOD ) dynamic_result
               | dynamic_result op=( MUL | DIV | MOD ) dynamic_result
               | dynamic_result op=MUL string_result
               | string_result op=MUL dynamic_result
               | dynamic_result op=( PLUS | MINUS ) int_result
               | int_result op=( PLUS | MINUS ) dynamic_result
               | dynamic_result op=( PLUS | MINUS )  float_result
               | float_result op=( PLUS | MINUS )  dynamic_result
               | dynamic_result op=( PLUS | MINUS ) dynamic_result
               | LEFT_RBRACKET dynamic_result RIGHT_RBRACKET
               | dynamic
               ;

dynamic : /*id                  TODO: Check if this makes sense
        |*/
        array_selector
        | function_call_assignment
        ;

int_result : int_result op=( MUL | DIV | MOD ) int_result
           | int_result op=( PLUS | MINUS ) int_result
           | LEFT_RBRACKET int_result RIGHT_RBRACKET
           | int_t
           ;

float_result : float_result op=( MUL | DIV | MOD ) float_result
             | int_result op=( MUL | DIV | MOD ) float_result
             | float_result op=( MUL | DIV | MOD ) int_result
             | float_result op=( PLUS | MINUS ) float_result
             | int_result op=( PLUS | MINUS )  float_result
             | float_result op=( PLUS | MINUS )  int_result
             | LEFT_RBRACKET float_result RIGHT_RBRACKET
             | float_t
             ;

string_result : string_result op=MUL int_result
              | int_result op=MUL string_result
              | string_result op=PLUS string_result
              | literal_t
              ;

comparison_list : left=comparison op=BIT_AND right=comparison_list
                | left=comparison op=AND right=comparison_list
                | left=comparison op=BIT_OR right=comparison_list
                | left=comparison op=OR right=comparison_list
                | LEFT_RBRACKET comparison_list RIGHT_RBRACKET
                | comparison
                ;

comparison : left=comp_var op=( LESS | GREATER | LESS_EQUAL | GREATER_EQUAL ) right=comp_var
           | left=comp_var op=( EQUAL | NOT_EQUAL ) right=comp_var
           | function_call_chain op=( LESS | GREATER | LESS_EQUAL | GREATER_EQUAL | EQUAL | NOT_EQUAL) right=comp_var
           | function_call_chain
           ;

comp_var : all_result // contains dynamic_result->dynamic-> function_call_assignment
         | array_selector        // id gelöscht
         | id_instance_var
         | id_class_var
         ;

lvalue : id
       //| id_global
         | id_constant
         | id_instance_var
         | id_class_var
       ;

rvalue : /*lvalue                   TODO: Check if this makes sense

       |*/ initial_array_assignment
       | array_assignment

       | int_result
       | float_result
       | string_result

       | global_set
       | global_get
       | dynamic_assignment
       | string_assignment
       | float_assignment
       | int_assignment
       | assignment

       | function_call_chain
       | literal_t
       | bool_t
       | float_t
       | int_t
       | nil_t
       | ternary_statement

       | rvalue EXP rvalue

       | ( NOT | BIT_NOT )rvalue

       | rvalue ( MUL | DIV | MOD ) rvalue
       | rvalue ( PLUS | MINUS ) rvalue

       | rvalue ( BIT_SHL | BIT_SHR ) rvalue

       | rvalue BIT_AND rvalue

       | rvalue ( BIT_OR | BIT_XOR )rvalue

       | rvalue ( LESS | GREATER | LESS_EQUAL | GREATER_EQUAL ) rvalue

       | rvalue ( EQUAL | NOT_EQUAL ) rvalue

       | rvalue ( OR | AND ) rvalue

       | LEFT_RBRACKET rvalue RIGHT_RBRACKET
       ;

break_expression : BREAK;

literal_t : LITERAL;

float_t : FLOAT;

int_t : INT;

bool_t : TRUE
       | FALSE
       ;

nil_t : NIL;

id : ID;

id_global : ID_GLOBAL;

id_function : ID_FUNCTION;

id_function_mark : ID_FUNCTION_MARK;

id_constant : ID_CONSTANT;

terminator : terminator SEMICOLON
           | terminator crlf
           | SEMICOLON
           | crlf
           ;

else_token : ELSE;

crlf : CRLF;

fragment ESCAPED_QUOTE : '\\"';
LITERAL : '"' ( ESCAPED_QUOTE | ~('\r') )*? '"'   // war ~('\n'|'\r')
        | '\'' ( ESCAPED_QUOTE | ~('\r') )*? '\'';   // war ~('\n'|'\r')

// BEGIN Added by me
CLASS: 'class';
SELF: 'self';
MODULE: 'module';
ID_CLASS : [A-Z_][a-zA-Z0-9_]*;
COLON : ':';
DCOLON : '::';
DOT : '.';
SPACESHIP : '<=>';
HASH : '=>';
LEFT_CBRACKET:'{';
RIGHT_CBRACKET:'}';
EACH: 'each';
DO: 'do';
BEGIN: 'begin';
RESCUE: 'rescue';
//END Added by me

COMMA : ',';
SEMICOLON : ';';
CRLF : '\r'? '\n';

REQUIRE : 'require';
END : 'end';
DEF : 'def';
RETURN : 'return';
PIR : 'pir';

IF: 'if';
ELSE : 'else';
ELSIF : 'elsif';
UNLESS : 'unless';
WHILE : 'while';
RETRY : 'retry';
BREAK : 'break';
FOR : 'for';

TRUE : 'true';
FALSE : 'false';

PLUS : '+';
MINUS : '-';
MUL : '*';
DIV : '/';
MOD : '%';
EXP : '**';

EQUAL : '==';
NOT_EQUAL : '!=';
GREATER : '>';
LESS : '<';
LESS_EQUAL : '<=';
GREATER_EQUAL : '>=';

ASSIGN : '=';
PLUS_ASSIGN : '+=';
MINUS_ASSIGN : '-=';
MUL_ASSIGN : '*=';
DIV_ASSIGN : '/=';
MOD_ASSIGN : '%=';
EXP_ASSIGN : '**=';

BIT_AND : '&';
BIT_OR : '|';
BIT_XOR : '^';
BIT_NOT : '~';
BIT_SHL : '<<';
BIT_SHR : '>>';

AND : 'and' | '&&';
OR : 'or' | '||';
NOT : 'not' | '!';

LEFT_RBRACKET : '(';
RIGHT_RBRACKET : ')';
LEFT_SBRACKET : '[';
RIGHT_SBRACKET : ']';

NIL : 'nil';

SL_COMMENT : ('#' ~('\r' | '\n')* '\r'?) -> skip; // was ('#' ~('\r' | '\n')* '\r'? '\n') -> skip
ML_COMMENT : ('=begin' .*? '=end' '\r'? '\n') -> skip;
WS : (' '|'\t')+ -> skip;

INT : [0-9]+;
FLOAT : [0-9]*'.'[0-9]+;
ID_CONSTANT : [A-Z_]+;
ID : [a-zA-Z_][a-zA-Z0-9_]*;
ID_GLOBAL : '$'ID;
ID_FUNCTION : ID[?];
ID_FUNCTION_MARK : ID[!];
ID_CLASS_VAR : '@@'ID ;
ID_INSTANCE_VAR : '@'ID;
