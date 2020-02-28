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
%token tTPTR tTINT tTSTRING tTREAL tTAUTO
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

%type <node> stmt program var if elif
%type <sequence> list exps vars ids
%type <expression> expr
%type <lvalue> lval

%{
//-- The rules below will be included in yyparse, the main parsing function.
%}
%%

program : list { compiler->ast(new og::program_node(LINE, $1)); }
        ;

list : stmt	     { $$ = new cdk::sequence_node(LINE, $1); }
	   | list stmt { $$ = new cdk::sequence_node(LINE, $2, $1); }
	   ;

stmt : expr ';'                         { $$ = new og::evaluation_node(LINE, $1); }
     | tWRITE exps ';'                  { $$ = new og::write_node(LINE, $2); }
     | tWRITELN exps ';'                { $$ = new og::writeln_node(LINE, $2); }
     | tBREAK ';'                       { $$ = new og::break_node(LINE); }
     | tCONTINUE ';'                    { $$ = new og::continue_node(LINE); }
     | tRETURN ';'                      { $$ = new og::return_node(LINE);}
     | tRETURN expr ';'                 { $$ = new og::return_value_node(LINE, $2); }
     | tFOR '(' exps ';' exps ';' exps ') tDO' stmt { $$ = new og::for_node(LINE, $3, $5, $7, $9); }
     | tFOR '(' vars ';' exps ';' exps ') tDO' stmt { $$ = new og::for_node(LINE, $3, $5, $7, $9); }
     | '{' list '}'                     { $$ = $2; }
     | if                               { $$ = $1; }
     ;

elif : tELIF expr tTHEN stmt %prec tIFX      { $$ = new og::if_node(LINE, $2, $4); }
     | tELIF expr tTHEN stmt tELSE stmt      { $$ = new og::if_else_node(LINE, $2, $4, $6); }
     | tELIF expr elif                       { $$ = new og::if_node(LINE, $2, $3); }
     ;

if   : tIF expr tTHEN stmt %prec tIFX        { $$ = new og::if_node(LINE, $2, $4); }
     | tIF expr tTHEN stmt tELSE stmt        { $$ = new og::if_else_node(LINE, $2, $4, $6); }
     | tIF expr tTHEN stmt elif              { $$ = new og::if_else_node(LINE, $2, $4, $5); }
     ;

exps : expr                                  { $$ = new cdk::sequence_node(LINE, $1);     }
	| exps ',' expr                         { $$ = new cdk::sequence_node(LINE, $3, $1); }
	;

/*

fd : decls

decls : decl
      | decl decls
      ;

prop : tPUBLIC
     | tREQUIRE
     ;

func : prop typeauto lval vars block
     | typeauto lval vars block
     | typeauto lval vars
     | typeauto lval block
     | prop typeauto lval block
     | prop typeauto block
     | prop typeauto lval
     ;

proc : tPROCEDURE lval
     | tPROCEDURE lval vars
     | tPROCEDURE lval vars block
     | prop tPROCEDURE lval
     | prop tPROCEDURE lval vars
     | prop tPROCEDURE lval vars block
     ;

typeauto : type
         | type typeauto
         | auto
         | auto typeauto
         ;

type : tINT
     | tREAL
     | tSTRING
     | tTPTR "<" typeauto ">"

block : "{" "}"
      | "{" decls"}"
      | "{" insts "}"
      | "{" decls insts "}"
      ;

decl : var ";"
     | func
     | proc
     ;

insts : inst
      | inst insts
      ;

inst : expr ";"
     | tWRITE exprs ";"
     | tWRITELN exprs ";"
     | next exprs ";"
     | next ";"
     | icond
     | iiter
     | block
     ;
      
next : tBREAK
     | tCONTINUE
     | tRETURN
     ;

elif : tELIF expr tTHEN stmt %prec tIFX      { $$ = new og::if_node(LINE, $2, $4); }
     | tELIF expr tTHEN stmt tELSE stmt      { $$ = new og::if_else_node(LINE, $2, $4, $6); }
     | tELIF expr elif                       { $$ = new og::if_node(LINE, $2, $3); }
     ;

icond : tIF expr tTHEN stmt %prec tIFX        { $$ = new og::if_node(LINE, $2, $4); }
      | tIF expr tTHEN stmt tELSE stmt        { $$ = new og::if_else_node(LINE, $2, $4, $6); }
      | tIF expr tTHEN stmt elif              { $$ = new og::if_else_node(LINE, $2, $4, $5); }
      ;

iiter : tFOR '(' exps ';' exps ';' exps ') tDO' stmt { $$ = new og::for_node(LINE, $3, $5, $7, $9); }
      | tFOR '(' vars ';' exps ';' exps ') tDO' stmt { $$ = new og::for_node(LINE, $3, $5, $7, $9); }
      ;


exprs : expr
      | expr "," exprs
      ;

*/

vars : var                                   { $$ = new cdk::sequence_node(LINE, $1);     }
	| vars ',' var                          { $$ = new cdk::sequence_node(LINE, $3, $1); }
	;

var  : 
    /*
       lval
     | prop lval
     | lval "=" expr
     | prop lval "=" expr
     
    */
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
     | lval                                  { $$ = new cdk::rvalue_node(LINE, $1); }  //FIXME
     | lval '=' expr                         { $$ = new cdk::assignment_node(LINE, $1, $3); }
     ;

lval : tIDENTIFIER                           { $$ = new cdk::variable_node(LINE, $1); }
     ;

%%
