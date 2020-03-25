%option c++ prefix="og_scanner_" outfile="og_scanner.cpp"
%option stack noyywrap yylineno 8bit
%{
// make relevant includes before including the parser's tab file
#include <string>
#include <vector>
#include <cdk/ast/sequence_node.h>
#include <cdk/ast/expression_node.h>
#include <cdk/ast/lvalue_node.h>
#include "og_parser.tab.h"

// don't change this
#define yyerror LexerError
%}
%x X_STRING X_ENDSTRING X_COMMENT
%%

"//".*                        ; /* ignore comments */

"/*"                          yy_push_state(X_COMMENT);
<X_COMMENT>"/*"               yy_push_state(X_COMMENT);
<X_COMMENT>"*/"               yy_pop_state();
<X_COMMENT>.                  ;

">="                          return tGE;
"<="                          return tLE;
"=="                          return tEQ;
"!="                          return tNE;

"&&"                          return tAND;
"||"                          return tOR;

"auto"                        return tTPAUTO;
"int"                         return tTPINT;
"real"                        return tTPREAL;
"string"                      return tTPSTRING;
"ptr"                         return tTPPTR;

"public"                      return tPUBLIC;
"require"                     return tREQUIRE;
"sizeof"                      return tSIZEOF;
"input"                       return tINPUT;
"nullptr"                     return tNULLPTR;
"procedure"                   return tPROCEDURE;
"break"                       return tBREAK;
"continue"                    return tCONTINUE;
"return"                      return tRETURN;
"if"                          return tIF;
"then"                        return tTHEN;
"elif"                        return tELIF;
"else"                        return tELSE;
"for"                         return tFOR;
"do"                          return tDO;
"write"                       return tWRITE;
"writeln"                     return tWRITELN;

[A-Za-z][A-Za-z0-9_]*         yylval.s = new std::string(yytext); return tIDENTIFIER;

\"                            yy_push_state(X_STRING); yylval.s = new std::string("");
<X_STRING>\"                  yy_pop_state(); return tSTRING;
<X_STRING>\\\"                *yylval.s += "\"";
<X_STRING>\\\\                *yylval.s += "\\";
<X_STRING>\\"n"               *yylval.s += "\n";
<X_STRING>\\"r"               *yylval.s += "\r";
<X_STRING>\\"t"               *yylval.s += "\t";
<X_STRING>\\\0                yy_push_state(X_ENDSTRING);
<X_STRING>.|\n                *yylval.s += yytext;
<X_STRING>\\[0-9a-fA-F]{1,2}  *yylval.s += std::string(1, strtol(yytext, nullptr, 16));
<X_ENDSTRING>\"               yy_pop_state(); yy_pop_state(); return tSTRING;
<X_ENDSTRING>\\\"             ;
<X_ENDSTRING>.                ;

[0-9]+                        {
  yylval.i = strtol(yytext, nullptr, 10);
  if (errno == ERANGE) yyerror("Integer overflow");
  return tINT;
}

0x[0-9A-Fa-f]+                {
  yylval.i = strtol(yytext, nullptr, 16);
  if (errno == ERANGE) yyerror("Integer overflow");
  return tINT;
}

((([0-9]+\.[0-9]*)|([0-9]*\.[0-9]+))([eE][0-9]*)*)|([0-9]+[eE][0-9]+) {
  yylval.r = strtod(yytext, nullptr);
  if (errno == ERANGE) yyerror("Real overflow");
  return tREAL;
}

[-()[\]<>=+*/%;{}.,@?]        return *yytext;

[ \t\n]+                      ; /* ignore whitespace */

.                             yyerror("Unknown character");

%%
