#ifndef __OG_AST_TUPLE_NODE_H__
#define __OG_AST_TUPLE_NODE_H__

#include <vector>
#include <cdk/ast/expression_node.h>

namespace og {

  /**
   * Class representing a tuple (sequence of expressions).
   */
  class tuple_node: public cdk::expression_node {
    cdk::sequence_node *_nodes;

  public:
    tuple_node(int lineno, cdk::sequence_node *nodes) :
        cdk::expression_node(lineno), _nodes(nodes) {
    }

  public:
    inline cdk::sequence_node *nodes() {
      return _nodes;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_tuple_node(this, level);
    }

  };

} // og

#endif
