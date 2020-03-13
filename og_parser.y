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
  int                   i;            /* integer value */
  double                r;            /* real value */
  std::string           *s;           /* symbol name or string literal */
  cdk::basic_node       *node;        /* node pointer */
  cdk::sequence_node    *sequence;
  cdk::expression_node  *expression;  /* expression nodes */
  cdk::lvalue_node      *lvalue;
  cdk::basic_type       *type;        /* a type */
};

%token <i> tINT
%token <s> tIDENTIFIER tSTRING
%token <r> tREAL
%token tTPPTR tTPINT tTPSTRING tTPREAL tTPAUTO
%token tPUBLIC tREQUIRE
%token tSIZEOF tNULLPTR tPROCEDURE
%token tBREAK tCONTINUE tRETURN tINPUT
%token tIF tTHEN tELIF tELSE tFOR tDO tWRITE tWRITELN

%nonassoc ')'
%nonassoc tIFX
%nonassoc tELSE tELIF

%right '='
%left tOR
%left tAND
%nonassoc '~'
%left tGE tLE tEQ tNE '>' '<'
%left '+' '-'
%left '*' '/' '%'
%nonassoc tUNARY
%nonassoc '(' '['

%type <s> string
%type <node> inst program var icond iiter func proc inst decl param bvar fvar
%type <sequence> exps decls insts block args params bvars fvars_aux fvars ids
%type <expression> expr
%type <lvalue> lval
%type <type> type

%{
//-- The rules below will be included in yyparse, the main parsing function.
%}
%%

program   : decls                                                { compiler->ast($1); }
          ;

decls     : decl                                                 { $$ = new cdk::sequence_node(LINE, $1); }
          | decls decl                                           { $$ = new cdk::sequence_node(LINE, $2, $1); }
          ;

decl      : var ";"                                              { $$ = $1; }
          | func                                                 { $$ = $1; }
          | proc                                                 { $$ = $1; }
          ;

var       :              type tIDENTIFIER                        { $$ = new og::var_decl_node(LINE, false, false, $1, $2, nullptr); }
          | tPUBLIC      type tIDENTIFIER                        { $$ = new og::var_decl_node(LINE, true, false, $2, $3, nullptr); }
          | tREQUIRE     type tIDENTIFIER                        { $$ = new og::var_decl_node(LINE, false, true, $2, $3, nullptr); }
          |              type tIDENTIFIER '=' expr               { $$ = new og::var_decl_node(LINE, false, false, $1, $2, $4); }
          | tPUBLIC      type tIDENTIFIER '=' expr               { $$ = new og::var_decl_node(LINE, true, false, $2, $3, $5); }
          | tREQUIRE     type tIDENTIFIER '=' expr               { $$ = new og::var_decl_node(LINE, false, true, $2, $3, $5); }
          |              tTPAUTO ids '=' exps                    { /* TODO $$ = new og::tuple_node(LINE, ?); */ }
          | tPUBLIC      tTPAUTO ids '=' exps                    { /* TODO $$ = new og::tuple_node(LINE, ?); */ }
          ;

ids       : tIDENTIFIER                                          { /* TODO: strings vector? */ }
          | ids ',' tIDENTIFIER                                  { /* TODO: strings vector? */ }
          ;

func      :          type     tIDENTIFIER '(' args ')'           { /* TODO $$ = new og::fun_decl_node(LINE, public, required, type, id, args); */ }
          | tPUBLIC  type     tIDENTIFIER '(' args ')'           { /* TODO $$ = new og::fun_decl_node(LINE, public, required, type, id, args); */ }
          | tREQUIRE type     tIDENTIFIER '(' args ')'           { /* TODO $$ = new og::fun_decl_node(LINE, public, required, type, id, args); */ }
          |          type     tIDENTIFIER '(' args ')' block     { /* TODO $$ = new og::fun_def_node(LINE, public, required, type, id, args, block); */ }
          | tPUBLIC  type     tIDENTIFIER '(' args ')' block     { /* TODO $$ = new og::fun_def_node(LINE, public, required, type, id, args, block); */ }
          | tREQUIRE type     tIDENTIFIER '(' args ')' block     { /* TODO $$ = new og::fun_def_node(LINE, public, required, type, id, args, block); */ }
          |          tTPAUTO  tIDENTIFIER '(' args ')'           { /* TODO $$ = new og::fun_decl_node(LINE, public, required, type, id, args); */ }
          | tPUBLIC  tTPAUTO  tIDENTIFIER '(' args ')'           { /* TODO $$ = new og::fun_decl_node(LINE, public, required, type, id, args); */ }
          | tREQUIRE tTPAUTO  tIDENTIFIER '(' args ')'           { /* TODO $$ = new og::fun_decl_node(LINE, public, required, type, id, args); */ }
          |          tTPAUTO  tIDENTIFIER '(' args ')' block     { /* TODO $$ = new og::fun_def_node(LINE, public, required, type, id, args, block); */ }
          | tPUBLIC  tTPAUTO  tIDENTIFIER '(' args ')' block     { /* TODO $$ = new og::fun_def_node(LINE, public, required, type, id, args, block); */ }
          | tREQUIRE tTPAUTO  tIDENTIFIER '(' args ')' block     { /* TODO $$ = new og::fun_def_node(LINE, public, required, type, id, args, block); */ }
          ;

proc      :          tPROCEDURE tIDENTIFIER '(' args ')'         { /* TODO $$ = new og::fun_decl_node(LINE, public, required, type, id, args); */ }
          | tPUBLIC  tPROCEDURE tIDENTIFIER '(' args ')'         { /* TODO $$ = new og::fun_decl_node(LINE, public, required, type, id, args); */ }
          | tREQUIRE tPROCEDURE tIDENTIFIER '(' args ')'         { /* TODO $$ = new og::fun_decl_node(LINE, public, required, type, id, args); */ }
          |          tPROCEDURE tIDENTIFIER '(' args ')' block   { /* TODO $$ = new og::fun_def_node(LINE, public, required, type, id, args, block); */ }
          | tPUBLIC  tPROCEDURE tIDENTIFIER '(' args ')' block   { /* TODO $$ = new og::fun_def_node(LINE, public, required, type, id, args, block); */ }
          | tREQUIRE tPROCEDURE tIDENTIFIER '(' args ')' block   { /* TODO $$ = new og::fun_def_node(LINE, public, required, type, id, args, block); */ }
          ;

params    : param                                                { $$ = new cdk::sequence_node(LINE, $1); }
          | params ',' param                                     { $$ = new cdk::sequence_node(LINE, $3, $1); }
          ;

param     : type tIDENTIFIER                                     { $$ = new og::var_decl_node(LINE, false, false, $1, $2, nullptr); }
          ;

args      : /* empty */                                          { $$ = new cdk::sequence_node(LINE); }
          | params                                               { $$ = $1;}
          ;

type      : tTPINT                                               { $$ = new cdk::primitive_type(4, cdk::TYPE_INT); }
          | tTPREAL                                              { $$ = new cdk::primitive_type(8, cdk::TYPE_DOUBLE); }
          | tTPSTRING                                            { $$ = new cdk::primitive_type(4, cdk::TYPE_STRING); }
          | tTPPTR '<' type '>'                                  { $$ = new cdk::reference_type(4, std::shared_ptr<cdk::basic_type>($3)); }
          | tTPPTR '<' tTPAUTO '>'                               { /* TODO $$ = new cdk::reference_type(4, ??); */ }
          ;

bvar      : type tIDENTIFIER                                     { $$ = new og::var_decl_node(LINE, false, false, $1, $2, nullptr); }
          | type tIDENTIFIER '=' expr                            { $$ = new og::var_decl_node(LINE, false, false, $1, $2, $4); }
          | tTPAUTO ids '=' exps                                 { /* TODO $$ = new og::tuple_node(LINE, ?); */ }
          ;

bvars     : bvar ';'                                             { $$ = new cdk::sequence_node(LINE, $1); }
          | bvars bvar ';'                                       { $$ = new cdk::sequence_node(LINE, $2, $1); }
          ;

block     : '{' '}'                                              { /* TODO: $$ = new og::block_node(LINE, nullptr, nullptr); */ }
          | '{' bvars '}'                                        { /* TODO: $$ = new og::block_node(LINE, nullptr, $2); */ }
          | '{' insts '}'                                        { /* TODO: $$ = new og::block_node(LINE, $2, nullptr); */ }
          | '{' bvars insts '}'                                  { /* TODO: $$ = new og::block_node(LINE, $2, $3); */ }
          ;

insts     : inst                                                 { $$ = new cdk::sequence_node(LINE, $1); }
          | insts inst                                           { $$ = new cdk::sequence_node(LINE, $2, $1); }
          ;

inst      : expr ';'                                             { $$ = new og::evaluation_node(LINE, $1); }
          | tWRITE exps ';'                                      { $$ = new og::write_node(LINE, $2); }
          | tWRITELN exps ';'                                    { $$ = new og::writeln_node(LINE, $2); }
          | tBREAK                                               { $$ = new og::break_node(LINE); }
          | tCONTINUE                                            { $$ = new og::continue_node(LINE); }
          | tRETURN ';'                                          { $$ = new og::return_node(LINE);}
          | tRETURN exps ';'                                     { $$ = new og::return_value_node(LINE, $2); }
          | tIF icond                                            { $$ = $2; }
          | iiter                                                { $$ = $1; }
          | block                                                { $$ = $1; }
          ;

icond     : expr tTHEN inst %prec tIFX                           { $$ = new og::if_node(LINE, $1, $3); }
          | expr tTHEN inst tELSE inst %prec tIFX                { $$ = new og::if_else_node(LINE, $1, $3, $5); }
          | expr tTHEN inst tELIF icond                          { $$ = new og::if_else_node(LINE, $1, $3, $5); }
          ;

fvar      : type tIDENTIFIER                                     { $$ = new og::var_decl_node(LINE, false, false, $1, $2, nullptr); }
          | type tIDENTIFIER '=' expr                            { $$ = new og::var_decl_node(LINE, false, false, $1, $2, $4); }
          ;

fvars_aux : fvar                                                 { $$ = new cdk::sequence_node(LINE, $1); }
          | fvars_aux ',' fvar                                   { $$ = new cdk::sequence_node(LINE, $3, $1); }
          ;

fvars     : tTPAUTO ids '=' exps                                 { /* TODO $$ = new og::tuple_node(LINE, ?); */ }
          | fvars_aux                                            { $$ = $1; }
          ;

iiter     : tFOR exps    ';' exps  ';' exps  tDO inst            { $$ = new og::for_node(LINE, $2, $4, $6, $8); }
          | tFOR exps    ';' exps  ';'       tDO inst            { $$ = new og::for_node(LINE, $2, $4, nullptr, $7); }
          | tFOR exps    ';'       ';' exps  tDO inst            { $$ = new og::for_node(LINE, $2, nullptr, $5, $7); }
          | tFOR exps    ';'       ';'       tDO inst            { $$ = new og::for_node(LINE, $2, nullptr, nullptr, $6); }
          | tFOR         ';' exps  ';' exps  tDO inst            { $$ = new og::for_node(LINE, nullptr, $3, $5, $7); }
          | tFOR         ';' exps  ';'       tDO inst            { $$ = new og::for_node(LINE, nullptr, $3, nullptr, $6); }
          | tFOR         ';'       ';' exps  tDO inst            { $$ = new og::for_node(LINE, nullptr, nullptr, $4, $6); }
          | tFOR         ';'       ';'       tDO inst            { $$ = new og::for_node(LINE, nullptr, nullptr, nullptr, $5); }
          | tFOR fvars   ';' exps  ';' exps  tDO inst            { $$ = new og::for_node(LINE, $2, $4, $6, $8); }
          | tFOR fvars   ';' exps  ';'       tDO inst            { $$ = new og::for_node(LINE, $2, $4, nullptr, $7); }
          | tFOR fvars   ';'       ';' exps  tDO inst            { $$ = new og::for_node(LINE, $2, nullptr, $5, $7); }
          | tFOR fvars   ';'       ';'       tDO inst            { $$ = new og::for_node(LINE, $2, nullptr, nullptr, $6); }
          ;

expr      : tINT                                                 { $$ = new cdk::integer_node(LINE, $1); }
          | string                                               { $$ = new cdk::string_node(LINE, $1); }
          | tREAL                                                { $$ = new cdk::double_node(LINE, $1); }
          | tNULLPTR                                             { $$ = new og::nullptr_node(LINE); }
          | tINPUT                                               { /* TODO: $$ = new og::input_node(LINE); */ }
          | tSIZEOF '(' exps ')'                                 { /* TODO: $$ = new og::sizeof_node(LINE, $3); */ }
          | '-' expr %prec tUNARY                                { $$ = new cdk::neg_node(LINE, $2); }
          | '+' expr %prec tUNARY                                { $$ = new og::id_node(LINE, $2); }
          | '~' expr                                             { $$ = new cdk::not_node(LINE, $2); }
          | expr tAND expr                                       { $$ = new cdk::and_node(LINE, $1, $3); }
          | expr tOR expr                                        { $$ = new cdk::or_node(LINE, $1, $3); }
          | expr '+' expr                                        { $$ = new cdk::add_node(LINE, $1, $3); }
          | expr '-' expr                                        { $$ = new cdk::sub_node(LINE, $1, $3); }
          | expr '*' expr                                        { $$ = new cdk::mul_node(LINE, $1, $3); }
          | expr '/' expr                                        { $$ = new cdk::div_node(LINE, $1, $3); }
          | expr '%' expr                                        { $$ = new cdk::mod_node(LINE, $1, $3); }
          | expr '<' expr                                        { $$ = new cdk::lt_node(LINE, $1, $3); }
          | expr '>' expr                                        { $$ = new cdk::gt_node(LINE, $1, $3); }
          | expr tGE expr                                        { $$ = new cdk::ge_node(LINE, $1, $3); }
          | expr tLE expr                                        { $$ = new cdk::le_node(LINE, $1, $3); }
          | expr tNE expr                                        { $$ = new cdk::ne_node(LINE, $1, $3); }
          | expr tEQ expr                                        { $$ = new cdk::eq_node(LINE, $1, $3); }
          | '(' expr ')'                                         { $$ = $2; }
          | '[' expr ']'                                         { $$ = new og::malloc_node(LINE, $2); }
          | lval                                                 { $$ = new cdk::rvalue_node(LINE, $1); }  //FIXME
          | lval '?'                                             { $$ = new og::memaddr_node(LINE, $1); }
          | lval '=' expr                                        { $$ = new cdk::assignment_node(LINE, $1, $3); }
          | tIDENTIFIER '(' ')'                                  { /* TODO: $$ = new og::func_call_node(LINE, $1, new cdk::sequence_node(LINE)); */ }
          | tIDENTIFIER '(' exps ')'                             { /* TODO: $$ = new og::func_call_node(LINE, $1, $3); */ }
          ;

exps      : expr                                                 { $$ = new cdk::sequence_node(LINE, $1);     }
          | exps ',' expr                                        { $$ = new cdk::sequence_node(LINE, $3, $1); }
          ;

lval      : tIDENTIFIER                                          { $$ = new cdk::variable_node(LINE, $1); }
          | expr '[' expr ']'                                    { /* TODO: $$ = new og::ptr_index_node(LINE, $1, $3): */ }
          | tIDENTIFIER '@' tINT                                 { /* TODO: $$ = new og::tuple_index_node(LINE, $1, $3): */ }
          ;

string    : tSTRING                                              { $$ = $1; }
          | string tSTRING                                       { $$ = new std::string(*$1 + *$2); delete $1; delete $2; }
          ;
%%
