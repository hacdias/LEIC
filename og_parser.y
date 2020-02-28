%{
//-- don't change *any* of these: if you do, you'll break the compiler.
#include <cdk/compiler.h>
#include "ast/all.h"
#define LINE               compiler->scanner()->lineno()
#define yylex()            compiler->scanner()->scan()
#define yyerror(s)         compiler->scanner()->error(s)
#define YYPARSE_PARAM_TYPE std::shared_ptr<cdk::compiler>
#define YYPARSE_PARAM      compiler
//-- don't change *any* of these --- END!
%}

%union {
  int                   i;	          /* integer value */
  double                r;              /* real value */
  std::string           *s;	          /* symbol name or string literal */
  cdk::basic_node       *node;	     /* node pointer */
  cdk::sequence_node    *sequence;
  cdk::expression_node  *expression;    /* expression nodes */
  cdk::lvalue_node      *lvalue;
};

%token <i> tINT
%token <s> tIDENTIFIER tSTRING
%token <r> tREAL
%token tTPPTR tTPINT tTPSTRING tTPREAL tTPAUTO
%token tPUBLIC tREQUIRE
%token tSIZEOF tNULLPTR tPROCEDURE
%token tBREAK tCONTINUE tRETURN tINPUT
%token tIF tTHEN tELIF tELSE tFOR tDO tFOR tDO tWRITE tWRITELN

%nonassoc tIFX
%nonassoc tELIF
%nonassoc tELSE

%right '='
%left tOR
%left tAND
%nonassoc '~'
%left tGE tLE tEQ tNE '>' '<'
%left '+' '-'
%left '*' '/' '%'
%nonassoc tUNARY

%type <node> inst program var icond iiter elif func proc inst decl
%type <sequence> exps vars ids decls insts block
%type <sequence> optexps optvars optdecls optinsts optblock
%type <expression> expr
%type <lvalue> lval
%type <i> optpublic optprop

%{
//-- The rules below will be included in yyparse, the main parsing function.
%}
%%

program : decls { compiler->ast(new og::program_node(LINE, $1)); }
        ;

decls : decl             { $$ = new cdk::sequence_node(LINE, $1); }
      | decl decls       { $$ = new cdk::sequence_node(LINE, $1, $2); }
      ;

decl : var ";"           { $$ = $1; }
     | func              { $$ = $1; }
     | proc              { $$ = $1; }
     ;

var  : optprop type tIDENTIFIER                   { /* TODO */ }
     | optprop type tIDENTIFIER '=' expr          { /* TODO */ }
     | optpublic tTPAUTO ids '=' exps             { /* TODO */ }
     ;

func : optprop type tIDENTIFIER '(' optvars ')' optblock    { /* TODO */ }
     | optprop tAUTO tIDENTIFIER optvars optblock           { /* TODO */ }
     ;

proc : optprop tPROCEDURE tIDENTIFIER '(' optvars ')' optblock   { /* TODO */ }
     ;

exps : expr                                  { $$ = new cdk::sequence_node(LINE, $1);     }
	| exps ',' expr                         { $$ = new cdk::sequence_node(LINE, $3, $1); }
	;

type : tTPINT
     | tTPREAL
     | tTPSTRING
     | tTPPTR '<' type '>'
     | tTPPTR '<' tTPAUTO '>'
     ;

block : '{' optdecls optinsts '}'            { $$ = new cdk::sequence_node(LINE, $2, $3); }
      ;

insts : inst                                 { $$ = new cdk::sequence_node(LINE, $1); }
      | inst insts                           { $$ = new cdk::sequence_node(LINE, $1, $2); }
      ;

inst : expr ';'                              { $$ = new og::evaluation_node(LINE, $1); }
     | tWRITE exps ';'                       { $$ = new og::write_node(LINE, $2); }
     | tWRITELN exps ';'                     { $$ = new og::writeln_node(LINE, $2); }
     | tBREAK                                { $$ = new og::break_node(LINE); }
     | tCONTINUE                             { $$ = new og::continue_node(LINE); }
     | tRETURN ';'                           { $$ = new og::return_node(LINE);}
     | tRETURN expr ';'                      { $$ = new og::return_value_node(LINE, $2); }
     | icond                                 { $$ = $1; }
     | iiter                                 { $$ = $1; }
     | block                                 { $$ = $1; }
     ;

elif : tELIF expr tTHEN inst %prec tIFX      { $$ = new og::if_node(LINE, $2, $4); }
     | tELIF expr tTHEN inst tELSE inst      { $$ = new og::if_else_node(LINE, $2, $4, $6); }
     | tELIF expr elif                       { $$ = new og::if_node(LINE, $2, $3); }
     ;

icond : tIF expr tTHEN inst %prec tIFX        { $$ = new og::if_node(LINE, $2, $4); }
      | tIF expr tTHEN inst tELSE inst        { $$ = new og::if_else_node(LINE, $2, $4, $6); }
      | tIF expr tTHEN inst elif              { $$ = new og::if_else_node(LINE, $2, $4, $5); }
      ;

iiter : tFOR optvars ';' optexps ';' optexps tDO optblock { $$ = new og::for_node(LINE, $2, $4, $6, $8); }
      | tFOR optexps ';' optexps ';' optexps tDO optblock { $$ = new og::for_node(LINE, $2, $4, $6, $8); }
      ;

vars : var                                   { $$ = new cdk::sequence_node(LINE, $1);     }
	| vars ',' var                          { $$ = new cdk::sequence_node(LINE, $3, $1); }
	;

ids  : lval                                  { $$ = new cdk::sequence_node(LINE, $1);     }
     | ids ',' lval                          { $$ = new cdk::sequence_node(LINE, $3, $1); }
     ;

expr : tINT                                  { $$ = new cdk::integer_node(LINE, $1); }
     | tSTRING                               { $$ = new cdk::string_node(LINE, $1); }
     | tREAL                                 { $$ = new cdk::double_node(LINE, $1);       }
     | '-' expr %prec tUNARY                 { $$ = new cdk::neg_node(LINE, $2); }
     | '+' expr %prec tUNARY                 { /* TODO: $$ = og::id_node(LINE, $2); */ }
     | '~' expr                              { $$ = new cdk::not_node(LINE, $2); }
     | expr tAND expr	                    { $$ = new cdk::and_node(LINE, $1, $3); }
     | expr tOR expr	                    { $$ = new cdk::or_node(LINE, $1, $3); }
     | expr '+' expr	                    { $$ = new cdk::add_node(LINE, $1, $3); }
     | expr '-' expr	                    { $$ = new cdk::sub_node(LINE, $1, $3); }
     | expr '*' expr	                    { $$ = new cdk::mul_node(LINE, $1, $3); }
     | expr '/' expr	                    { $$ = new cdk::div_node(LINE, $1, $3); }
     | expr '%' expr	                    { $$ = new cdk::mod_node(LINE, $1, $3); }
     | expr '<' expr	                    { $$ = new cdk::lt_node(LINE, $1, $3); }
     | expr '>' expr	                    { $$ = new cdk::gt_node(LINE, $1, $3); }
     | expr tGE expr	                    { $$ = new cdk::ge_node(LINE, $1, $3); }
     | expr tLE expr                         { $$ = new cdk::le_node(LINE, $1, $3); }
     | expr tNE expr	                    { $$ = new cdk::ne_node(LINE, $1, $3); }
     | expr tEQ expr	                    { $$ = new cdk::eq_node(LINE, $1, $3); }
     | '(' expr ')'                          { $$ = $2; }
     | '[' expr ']'                          { /* TODO */ }
     | lval                                  { $$ = new cdk::rvalue_node(LINE, $1); }  //FIXME
     | lval '=' expr                         { $$ = new cdk::assignment_node(LINE, $1, $3); }
     ;

lval : tIDENTIFIER                           { $$ = new cdk::variable_node(LINE, $1); }
     ;

/*
*             [ optional ]
*/

optvars : /* empty */    { $$ = nullptr; }
     | vars              { $$ = $1; }
     ;

optexps : /* empty */    { $$ = nullptr; }
     | exps              { $$ = $1; }
     ;

optblock : /*empty*/     { $$ = nullptr; }
     | block             { $$ = $1; }
     ;

optinsts : /* empty */   { $$ = nullptr; }
     | insts             { $$ = $1; }
     ;

optdecls : /* empty */   { $$ = nullptr; }
     | decls             { $$ = $1; }
     ;

optpublic: /* empty */   { $$ = 0; }
     | tPUBLIC           { $$ = 1; }
     ;

optprop : /* empty */    { $$ = 0; }
     | tPUBLIC           { $$ = 1; }
     | tREQUIRE          { $$ = 2; }
     ;

%%
