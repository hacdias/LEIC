#ifndef __OG_AST_WRITE_NODE_H__
#define __OG_AST_WRITE_NODE_H__

#include <cdk/ast/sequence_node.h>

namespace og {

  /**
   * Class for describing write nodes.
   */
  class write_node: public cdk::basic_node {
    cdk::expression_node *_argument;
    bool _has_newline;

  public:
    inline write_node(int lineno, cdk::expression_node *argument, bool has_newline = false) :
        cdk::basic_node(lineno), _argument(argument), _has_newline(has_newline) {
    }

  public:
    inline cdk::expression_node *argument() {
      return _argument;
    }

    inline bool has_newline() {
      return _has_newline;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_write_node(this, level);
    }

  };

} // og

#endif
