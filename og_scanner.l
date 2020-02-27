%option c++ prefix="og_scanner_" outfile="og_scanner.cpp"
%option stack noyywrap yylineno 8bit
%{ 
// make relevant includes before including the parser's tab file
#include <string>
#include <sstream>
#include <cdk/ast/sequence_node.h>
#include <cdk/ast/expression_node.h>
#include <cdk/ast/lvalue_node.h>
#include "og_parser.tab.h"

static std::ostringstream sstr;

// don't change this
#define yyerror LexerError
%}
%x X_STRING X_INVALSTRING X_COMMENT
%%

"//".*                 ; /* ignore comments */

"/*"                   yy_push_state(X_COMMENT);
<X_COMMENT>"/*"        yy_push_state(X_COMMENT);
<X_COMMENT>"*/"        yy_pop_state();
<X_COMMENT>.           ;

">="                   return tGE; 
"<="                   return tLE; 
"=="                   return tEQ; 
"!="                   return tNE; 

"for"                  return tFOR; 
"if"                   return tIF; 
"else"                 return tELSE; 
"print"                return tPRINT; 
"read"                 return tREAD; 
"begin"                return tBEGIN; 
"end"                  return tEND; 
"return"               return tRETURN;

[A-Za-z][A-Za-z0-9_]*  yylval.s = new std::string(yytext); return tIDENTIFIER;

\"                     yy_push_state(X_STRING); yylval.s = new std::string("");
<X_STRING>\"           yy_pop_state(); return tSTRING;
<X_STRING>\\\"         *yylval.s += yytext + 1;
<X_STRING>\\\\         *yylval.s += yytext + 1;
<X_STRING>\\"n"        *yylval.s += '\n';
<X_STRING>\\"r"        *yylval.s += '\r';      
<X_STRING>\\"t"        *yylval.s += '\t';    
<X_STRING>\\\0         yy_push_state(X_INVALSTRING);
<X_STRING>.            *yylval.s += yytext;
<X_STRING>\\[0-9a-fA-F]{1,2}  { 
    sstr << char(strtol(std::string(yytext + 1).c_str(), NULL, 16));
    *yylval.s += sstr.str().c_str();
    sstr.str("");
  }

<X_STRING>\n           yyerror("newline in string");
<X_INVALSTRING>\"       yy_pop_state(); yy_pop_state(); return tSTRING;
<X_INVALSTRING>\\\"     ;
<X_INVALSTRING>.        ;

[0-9]+                 yylval.i = strtol(yytext, nullptr, 10); return tINTEGER;

[-()<>=+*/%;{}.]       return *yytext;

[ \t\n]+               ; /* ignore whitespace */

.                      yyerror("Unknown character");

%%
