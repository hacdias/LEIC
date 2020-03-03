#ifndef __OG_AST_VAR_DECL_NODE_H__
#define __OG_AST_VAR_DECL_NODE_H__

#include <string>
#include <cdk/types/basic_type.h>
#include <cdk/ast/expression_node.h>
#include <cdk/ast/basic_node.h>

namespace og {

  /**
   * Class for describing variable declation node.
   */
  class var_decl_node: public cdk::basic_node {
    bool _is_public;
    bool _is_require;
    cdk::basic_type *_type;
    std::string _identifier;
    cdk::expression_node *_expression;

  public:

    inline var_decl_node(int lineno, bool is_public, bool is_require, cdk::basic_type *type,
      std::string * identifier, cdk::expression_node *expression) :
        cdk::basic_node(lineno),_is_public(is_public), _is_require(is_require),  _type(type),
        _identifier(*identifier), _expression(expression) {
    }

  public:
    inline cdk::basic_type * type() {
      return _type;
    }

    inline bool is_public () {
      return _is_public;
    }

    inline bool is_require () {
      return _is_require;
    }

    inline std::string& identifier() {
      return _identifier;
    }

    inline cdk::expression_node * expression() {
      return _expression;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_var_decl_node(this, level);
    }

  };

} // og

#endif
