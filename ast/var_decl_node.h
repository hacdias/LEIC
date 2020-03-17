#ifndef __OG_AST_VAR_DECL_NODE_H__
#define __OG_AST_VAR_DECL_NODE_H__

#include <string>
#include <cdk/types/basic_type.h>
#include <cdk/ast/expression_node.h>
#include <cdk/ast/typed_node.h>

namespace og {

  /**
   * Class for describing variable declation node.
   */
  class var_decl_node: public cdk::typed_node {
    bool _is_public;
    bool _is_require;
    std::vector<std::string*> _identifiers;
    cdk::expression_node *_expression;

  public:

    inline var_decl_node(int lineno, bool is_public, bool is_require, cdk::basic_type *tp,
      std::string *identifier, cdk::expression_node *expression) :
        var_decl_node(lineno, is_public, is_require, tp, new std::vector<std::string*>(1, identifier), expression)
        {
      // EMPTY
    }

    inline var_decl_node(int lineno, bool is_public, bool is_require, cdk::basic_type *tp,
      std::vector<std::string*> *identifiers, cdk::expression_node *expression) :
        cdk::typed_node(lineno),_is_public(is_public), _is_require(is_require),
        _identifiers(*identifiers), _expression(expression) {
        type(std::shared_ptr<cdk::basic_type>(tp));
    }

  public:
    inline bool is_public () {
      return _is_public;
    }

    inline bool is_require () {
      return _is_require;
    }

    inline std::vector<std::string*> identifiers() {
      return _identifiers;
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
