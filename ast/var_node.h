#ifndef __OG_AST_VAR_NODE_H__
#define __OG_AST_VAR_NODE_H__

#include <string>
#include <cdk/types/basic_type.h>
#include <cdk/ast/expression_node.h>
#include <cdk/ast/basic_node.h>

namespace og {

  /**
   * Class for describing var node.
   */
  class var_node: public cdk::basic_node {
    int _prop;
    cdk::basic_type *_type;
    std::string _identifier;
    cdk::expression_node *_expression;

  public:

    inline var_node(int lineno, int prop, cdk::basic_type *type, std::string identifier, cdk::expression_node *expression) :
        cdk::basic_node(lineno), _type(type), _identifier(identifier), _expression(expression) {
    }

  public:
    inline int prop() {
      return _prop;
    }

    inline cdk::basic_type * type() {
      return _type;
    }

    inline std::string& identifier() {
      return _identifier;
    }

    inline cdk::expression_node * expression() {
      return _expression;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_var_node(this, level);
    }

  };

} // og

#endif
