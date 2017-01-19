grammar Ruby;

program: stmt*;
stmt: class_define | function_define;

//class
class_define: CLASS class_name;

class_name: IDENTIFIER | IDENTIFIER LESS IDENTIFIER;

//functions
function_define: DEF function_name CRLF
               | DEF function_name function_params CRLF;

function_name: IDENTIFIER;

function_params: LEFT_RBRACKET RIGHT_RBRACKET
               | LEFT_RBRACKET function_params_list RIGHT_RBRACKET
               | function_params_list;

function_params_list: function_define_param_id
                    | function_params_list COMMA function_define_param_id;

function_define_param_id: IDENTIFIER;

//Terminals
CLASS: 'class';
DEF: 'def';

CRLF: '\r'? '\n';
IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_]*;
TERM: ';';
WS:   [ \t\r\n]+ -> skip ;
INT: [0-9]+;

LESS: '<';
ASSIGN: '=';
LEFT_RBRACKET: '(';
RIGHT_RBRACKET: ')';
LEFT_SBRACKET: '[';
RIGHT_SBRACKET: ']';
COMMA: ',';


SL_COMMENT: ('#' ~('\r' | '\n')* '\r'? '\n') -> skip;
ML_COMMENT: ('=begin' .*? '=end' '\r'? '\n') -> skip;
