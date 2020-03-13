#ifndef __OG_AST_SIZEOF_NODE_H__
#define __OG_AST_SIZEOF_NODE_H__

#include <cdk/ast/unary_operation_node.h>

namespace og {

  /**
   * Class for describing sizeof nodes.
   */
  class sizeof_node: public cdk::expression_node {
    cdk::sequence_node *_value;

  public:
    inline sizeof_node(int lineno, cdk::sequence_node *value) :
        cdk::expression_node(lineno), _value(value) {
    }

  public:
    inline cdk::sequence_node *value() {
      return _value;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_sizeof_node(this, level);
    }

  };

} // og

#endif