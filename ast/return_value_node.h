#ifndef __OG_AST_RETURN_VALUE_NODE_H__
#define __OG_AST_RETURN_VALUE_NODE_H__

#include <cdk/ast/expression_node.h>

namespace og {

  /**
   * Class for describing return value nodes.
   */
  class return_value_node: public cdk::basic_node {
    cdk::expression_node *_value;

  public:
    inline return_value_node(int lineno, cdk::expression_node *value) :
        cdk::basic_node(lineno), _value(value) {
    }

  public:
    inline cdk::expression_node *value() {
      return _value;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_return_value_node(this, level);
    }

  };

} // og

#endif
