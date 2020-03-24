#ifndef __OG_AST_TUPLE_INDEX_NODE_H__
#define __OG_AST_TUPLE_INDEX_NODE_H__

#include <cdk/ast/integer_node.h>
#include <cdk/ast/lvalue_node.h>

namespace og {

  /**
   * Class for describing tuple index nodes.
   */
  class tuple_index_node: public cdk::lvalue_node {
    std::string _identifier;
    cdk::integer_node* _index;

  public:
    inline tuple_index_node(int lineno, std::string *identifier, cdk::integer_node *index) :
        cdk::lvalue_node(lineno), _identifier(*identifier), _index(index) {
    }

  public:
    inline std::string identifier() {
      return _identifier;
    }

    inline cdk::integer_node *index() {
      return _index;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_tuple_index_node(this, level);
    }

  };

} // og

#endif