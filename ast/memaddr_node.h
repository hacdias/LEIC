#ifndef __OG_AST_MEMADDR_NODE_H__
#define __OG_AST_MEMADDR_NODE_H__

#include <cdk/ast/expression_node.h>
#include <cdk/ast/lvalue_node.h>

namespace og {

  /**
   * Class for describing memory address nodes.
   */
  class memaddr_node: public cdk::expression_node {
    cdk::lvalue_node *_lvalue;
  public:
    inline memaddr_node(int lineno, cdk::lvalue_node *lvalue) :
        cdk::expression_node(lineno), _lvalue(lvalue) {
    }

    inline cdk::lvalue_node *lvalue() {
      return _lvalue;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_memaddr_node(this, level);
    }

  };

} // og

#endif