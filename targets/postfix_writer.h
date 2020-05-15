#ifndef __OG_TARGETS_POSTFIX_WRITER_H__
#define __OG_TARGETS_POSTFIX_WRITER_H__

#include "targets/basic_ast_visitor.h"
#include "targets/frame_size_calculator.h"

#include <sstream>
#include <cdk/emitters/basic_postfix_emitter.h>
#include <stack>

namespace og {

  //!
  //! Traverse syntax tree and generate the corresponding assembly code.
  //!
  class postfix_writer: public basic_ast_visitor {
    cdk::symbol_table<og::symbol> &_symtab;
    std::shared_ptr<og::symbol> _function;
    cdk::basic_postfix_emitter &_pf;
    int _lbl;

    std::stack<int> _for_cond;
    std::stack<int> _for_incr;
    std::stack<int> _for_end;

    bool _inside_function = false;
    bool _in_function_args = false;
    int _offset = 0;


  public:
    postfix_writer(std::shared_ptr<cdk::compiler> compiler, cdk::symbol_table<og::symbol> &symtab,
                   cdk::basic_postfix_emitter &pf) :
        basic_ast_visitor(compiler), _symtab(symtab), _pf(pf), _lbl(0) {
    }

  public:
    ~postfix_writer() {
      os().flush();
    }

  private:
    /** Method used to generate sequential labels. */
    inline std::string mklbl(int lbl) {
      std::ostringstream oss;
      if (lbl < 0)
        oss << ".L" << -lbl;
      else
        oss << "_L" << lbl;
      return oss.str();
    }

  void do_var_decl_node_helper(std::shared_ptr<og::symbol> symbol, cdk::expression_node* expression, int lvl);
  void do_binary_int_double_helper(cdk::binary_operation_node * const node, int lvl);

  public:
  // do not edit these lines
#define __IN_VISITOR_HEADER__
#include "ast/visitor_decls.h"       // automatically generated
#undef __IN_VISITOR_HEADER__
  // do not edit these lines: end

  };

} // og

#endif
