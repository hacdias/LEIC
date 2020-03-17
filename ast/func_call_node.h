#ifndef __OG_AST_FUNC_CALL_NODE_H__
#define __OG_AST_FUNC_CALL_NODE_H__

#include <string>
#include <cdk/ast/sequence_node.h>

namespace og {

  /**
   * Class for describing func_call nodes.
   */
  class func_call_node: public cdk::expression_node {
    std::string _identifier;
    cdk::sequence_node *_expressions;

  public:
    inline func_call_node(int lineno, std::string *identifier, cdk::sequence_node *expressions) :
        cdk::expression_node(lineno), _identifier(*identifier), _expressions(expressions) {
    }

  public:
    inline std::string &identifier() {
      return _identifier;
    }
    inline cdk::sequence_node *expressions() {
      return _expressions;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_func_call_node(this, level);
    }

  };

} // og

#endif