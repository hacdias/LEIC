#ifndef __OG_AST_TUPLE_NODE_H__
#define __OG_AST_TUPLE_NODE_H__

#include <vector>
#include <cdk/ast/expression_node.h>

namespace og {

  /**
   * Class representing a tuple (sequence of expressions).
   */
  class tuple_node: public cdk::expression_node {
    std::vector<cdk::expression_node*> _nodes;

  public:
    tuple_node(int lineno) :
        cdk::expression_node(lineno) {
    }

    tuple_node(int lineno, cdk::expression_node *item, tuple_node *tuple = nullptr) :
        cdk::expression_node(lineno) {
      if (tuple != nullptr)
        _nodes = tuple->nodes();
      _nodes.push_back(item);
    }

  public:
    ~tuple_node() {
      for (auto node : _nodes)
        delete node;
      _nodes.clear();
    }

  public:
    cdk::expression_node *node(size_t i) {
      return _nodes[i];
    }

    std::vector<cdk::expression_node*> &nodes() {
      return _nodes;
    }

    size_t size() {
      return _nodes.size();
    }

    void accept(basic_ast_visitor *av, int level) {
      av->do_tuple_node(this, level);
    }

  };

} // og

#endif
