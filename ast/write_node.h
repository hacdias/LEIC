#ifndef __OG_AST_PRINT_NODE_H__
#define __OG_AST_PRINT_NODE_H__

#include <cdk/ast/sequence_node.h>

namespace og {

  /**
   * Class for describing write nodes.
   */
  class write_node: public cdk::basic_node {
    cdk::sequence_node *_argument;

  public:
    inline write_node(int lineno, cdk::sequence_node *argument) :
        cdk::basic_node(lineno), _argument(argument) {
    }

  public:
    inline cdk::sequence_node *argument() {
      return _argument;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_write_node(this, level);
    }

  };

} // og

#endif
