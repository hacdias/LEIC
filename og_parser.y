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
  int                         i;            /* integer value */
  double                      r;            /* real value */
  std::string                 *s;           /* symbol name or string literal */
  cdk::basic_node             *node;        /* node pointer */
  std::vector<std::string*>   *strings;
  cdk::sequence_node          *sequence;
  cdk::expression_node        *expression;  /* expression nodes */
  cdk::lvalue_node            *lvalue;
  cdk::basic_type             *type;        /* a type */
  og::block_node              *block;       /* a block */
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
%left tEQ tNE
%left tGE tLE '>' '<'
%left '+' '-'
%left '*' '/' '%'
%nonassoc tUNARY
%nonassoc '@'
%nonassoc '(' '['

%type <s> string
%type <strings> ids
%type <node> inst program var icond iiter func proc inst decl param bvar fvar
%type <sequence> exps decls insts args params bvars fvars_aux fvars
%type <expression> expr tuple
%type <lvalue> lval
%type <type> type
%type <block> block

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
          |              tTPAUTO ids '=' tuple                   { $$ = new og::var_decl_node(LINE, false, false, new cdk::primitive_type(), $2, $4); }
          | tPUBLIC      tTPAUTO ids '=' tuple                   { $$ = new og::var_decl_node(LINE, true, false, new cdk::primitive_type(), $3, $5); }
          ;

ids       : tIDENTIFIER                                          { $$ = new std::vector<std::string*>(); $$->push_back($1); }
          | ids ',' tIDENTIFIER                                  { $1->push_back($3); $$ = $1; }
          ;

func      :          type     tIDENTIFIER '(' args ')'           { $$ = new og::func_decl_node(LINE, false, false, $1, $2, $4); }
          | tPUBLIC  type     tIDENTIFIER '(' args ')'           { $$ = new og::func_decl_node(LINE, true, false, $2, $3, $5); }
          | tREQUIRE type     tIDENTIFIER '(' args ')'           { $$ = new og::func_decl_node(LINE, false, true, $2, $3, $5); }
          |          type     tIDENTIFIER '(' args ')' block     { $$ = new og::func_def_node(LINE, false, false, $1, $2, $4, $6); }
          | tPUBLIC  type     tIDENTIFIER '(' args ')' block     { $$ = new og::func_def_node(LINE, true, false, $2, $3, $5, $7); }
          | tREQUIRE type     tIDENTIFIER '(' args ')' block     { $$ = new og::func_def_node(LINE, false, true, $2, $3, $5, $7); }
          |          tTPAUTO  tIDENTIFIER '(' args ')'           { $$ = new og::func_decl_node(LINE, false, false, new cdk::primitive_type(), $2, $4); }
          | tPUBLIC  tTPAUTO  tIDENTIFIER '(' args ')'           { $$ = new og::func_decl_node(LINE, true, false, new cdk::primitive_type(), $3, $5); }
          | tREQUIRE tTPAUTO  tIDENTIFIER '(' args ')'           { $$ = new og::func_decl_node(LINE, false, true, new cdk::primitive_type(), $3, $5); }
          |          tTPAUTO  tIDENTIFIER '(' args ')' block     { $$ = new og::func_def_node(LINE, false, false, new cdk::primitive_type(), $2, $4, $6); }
          | tPUBLIC  tTPAUTO  tIDENTIFIER '(' args ')' block     { $$ = new og::func_def_node(LINE, true, false, new cdk::primitive_type(), $3, $5, $7); }
          | tREQUIRE tTPAUTO  tIDENTIFIER '(' args ')' block     { $$ = new og::func_def_node(LINE, false, true, new cdk::primitive_type(), $3, $5, $7); }
          ;

proc      :          tPROCEDURE tIDENTIFIER '(' args ')'         { $$ = new og::func_decl_node(LINE, false, false, new cdk::primitive_type(0, cdk::TYPE_VOID), $2, $4); }
          | tPUBLIC  tPROCEDURE tIDENTIFIER '(' args ')'         { $$ = new og::func_decl_node(LINE, true, false, new cdk::primitive_type(0, cdk::TYPE_VOID), $3, $5); }
          | tREQUIRE tPROCEDURE tIDENTIFIER '(' args ')'         { $$ = new og::func_decl_node(LINE, false, true, new cdk::primitive_type(0, cdk::TYPE_VOID), $3, $5); }
          |          tPROCEDURE tIDENTIFIER '(' args ')' block   { $$ = new og::func_def_node(LINE, false, false, new cdk::primitive_type(0, cdk::TYPE_VOID), $2, $4, $6); }
          | tPUBLIC  tPROCEDURE tIDENTIFIER '(' args ')' block   { $$ = new og::func_def_node(LINE, true, false, new cdk::primitive_type(0, cdk::TYPE_VOID), $3, $5, $7); }
          | tREQUIRE tPROCEDURE tIDENTIFIER '(' args ')' block   { $$ = new og::func_def_node(LINE, false, true, new cdk::primitive_type(0, cdk::TYPE_VOID), $3, $5, $7); }
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
          | tTPPTR '<' tTPAUTO '>'                               { $$ = new cdk::reference_type(4, std::shared_ptr<cdk::basic_type>(new cdk::primitive_type())); }
          ;

bvar      : type tIDENTIFIER                                     { $$ = new og::var_decl_node(LINE, false, false, $1, $2, nullptr); }
          | type tIDENTIFIER '=' expr                            { $$ = new og::var_decl_node(LINE, false, false, $1, $2, $4); }
          | tTPAUTO ids '=' tuple                                { $$ = new og::var_decl_node(LINE, false, false, new cdk::primitive_type(), $2, $4); }
          ;

bvars     : bvar ';'                                             { $$ = new cdk::sequence_node(LINE, $1); }
          | bvars bvar ';'                                       { $$ = new cdk::sequence_node(LINE, $2, $1); }
          ;

block     : '{' '}'                                              { $$ = new og::block_node(LINE, nullptr, nullptr); }
          | '{' bvars '}'                                        { $$ = new og::block_node(LINE, $2, nullptr); }
          | '{' insts '}'                                        { $$ = new og::block_node(LINE, nullptr, $2); }
          | '{' bvars insts '}'                                  { $$ = new og::block_node(LINE, $2, $3); }
          ;

insts     : inst                                                 { $$ = new cdk::sequence_node(LINE, $1); }
          | insts inst                                           { $$ = new cdk::sequence_node(LINE, $2, $1); }
          ;

inst      : expr ';'                                             { $$ = new og::evaluation_node(LINE, $1); }
          | tWRITE tuple ';'                                     { $$ = new og::write_node(LINE, $2, false); }
          | tWRITELN tuple ';'                                   { $$ = new og::write_node(LINE, $2, true); }
          | tBREAK                                               { $$ = new og::break_node(LINE); }
          | tCONTINUE                                            { $$ = new og::continue_node(LINE); }
          | tRETURN ';'                                          { $$ = new og::return_node(LINE, nullptr); }
          | tRETURN tuple ';'                                    { $$ = new og::return_node(LINE, $2); }
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

fvars     : tTPAUTO ids '=' tuple                                { $$ = new cdk::sequence_node(LINE, new og::var_decl_node(LINE, false, false, new cdk::primitive_type(), $2, $4)); }
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
          | tINPUT                                               { $$ = new og::input_node(LINE); }
          | tSIZEOF '(' tuple ')'                                { $$ = new og::sizeof_node(LINE, $3); }
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
          | '[' expr ']'                                         { $$ = new og::mem_alloc_node(LINE, $2); }
          | lval                                                 { $$ = new cdk::rvalue_node(LINE, $1); }  //FIXME
          | lval '?'                                             { $$ = new og::mem_addr_node(LINE, $1); }
          | lval '=' expr                                        { $$ = new cdk::assignment_node(LINE, $1, $3); }
          | tIDENTIFIER '(' ')'                                  { $$ = new og::func_call_node(LINE, $1, new cdk::sequence_node(LINE)); }
          | tIDENTIFIER '(' exps ')'                             { $$ = new og::func_call_node(LINE, $1, $3); }
          ;

exps      : expr                                                 { $$ = new cdk::sequence_node(LINE, $1); }
          | exps ',' expr                                        { $$ = new cdk::sequence_node(LINE, $3, $1); }
          ;

tuple     : exps                                                 { $$ = new og::tuple_node(LINE, $1); }
          ;

lval      : tIDENTIFIER                                          { $$ = new cdk::variable_node(LINE, $1); }
          | expr '[' expr ']'                                    { $$ = new og::ptr_index_node(LINE, $1, $3); }
          | expr '@' tINT                                        { $$ = new og::tuple_index_node(LINE, $1, new cdk::integer_node(LINE, $3)); }
          ;

string    : tSTRING                                              { $$ = $1; }
          | string tSTRING                                       { $$ = new std::string(*$1 + *$2); delete $1; delete $2; }
          ;
%%
