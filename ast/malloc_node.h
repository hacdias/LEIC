#ifndef __OG_AST_MALLOC_NODE_H__
#define __OG_AST_MALLOC_NODE_H__

#include <cdk/ast/unary_operation_node.h>

namespace og {

  /**
   * Class for describing malloc nodes.
   */
  class malloc_node: public cdk::unary_operation_node {
  public:
    malloc_node(int lineno, cdk::expression_node *argument) :
        cdk::unary_operation_node(lineno, argument) {
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_malloc_node(this, level);
    }

  };

} // og

#endif