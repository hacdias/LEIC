#ifndef __OG_AST_BLOCK_NODE_H__
#define __OG_AST_BLOCK_NODE_H__

#include <cdk/ast/basic_node.h>

namespace og {

  /**
   * TODO TODO
   * Class for describing block nodes.
   */
  class block_node: public cdk::basic_node {
  public:
    inline block_node(int lineno) :
        cdk::basic_node(lineno) {
    }

  public:
    void accept(basic_ast_visitor *sp, int level) {
      sp->do_block_node(this, level);
    }

  };

} // og

#endif
