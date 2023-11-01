#ifndef __OG_AST_SIZEOF_NODE_H__
#define __OG_AST_SIZEOF_NODE_H__

#include <cdk/ast/expression_node.h>

namespace og {

  /**
   * Class for describing sizeof nodes.
   */
  class sizeof_node: public cdk::expression_node {
    cdk::expression_node *_expr;

  public:
    inline sizeof_node(int lineno, cdk::expression_node *expr) :
        cdk::expression_node(lineno), _expr(expr) {
    }

  public:
    inline cdk::expression_node *value() {
      return _expr;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_sizeof_node(this, level);
    }

  };

} // og

#endif