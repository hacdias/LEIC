#ifndef __OG_AST_PTR_INDEX_NODE_H__
#define __OG_AST_PTR_INDEX_NODE_H__

#include <cdk/ast/expression_node.h>

namespace og {

  /**
   * Class for describing ptr_index nodes.
   */
  class ptr_index_node: public cdk::basic_node {
    cdk::expression_node *_pointer, *_index;

  public:
    inline ptr_index_node(int lineno, cdk::expression_node *pointer, cdk::expression_node *index) :
        cdk::basic_node(lineno), _pointer(pointer), _index(index) {
    }

  public:
    inline cdk::expression_node *pointer() {
      return _pointer;
    }
    inline cdk::expression_node *index() {
      return _index;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_ptr_index_node(this, level);
    }

  };

} // og

#endif