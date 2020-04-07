#ifndef __OG_AST_FUNC_DECL_NODE_H__
#define __OG_AST_FUNC_DECL_NODE_H__

#include <string>
#include <cdk/ast/sequence_node.h>

namespace og {

  /**
   * Class for describing func_decl nodes.
   */
  class func_decl_node: public cdk::typed_node {
    bool _is_public;
    bool _is_required;
    std::string _identifier;
    cdk::sequence_node *_args;

  public:
    inline func_decl_node(int lineno, bool is_public, bool is_required,
      cdk::basic_type *tp, std::string *identifier, cdk::sequence_node *args) :
        cdk::typed_node(lineno), _is_public(is_public), _is_required(is_required),
        _identifier(*identifier), _args(args) {
        type(std::shared_ptr<cdk::basic_type>(tp));
    }
  public:
    inline bool is_public() {
      return _is_public;
    }

    inline bool is_required() {
      return _is_required;
    }

    inline std::string identifier() {
      return _identifier;
    }

    inline cdk::sequence_node *args() {
      return _args;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_func_decl_node(this, level);
    }

  };

} // og

#endif