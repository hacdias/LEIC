#ifndef __OG_AST_FUNC_DEF_NODE_H__
#define __OG_AST_FUNC_DEF_NODE_H__

#include <cdk/ast/basic_node.h>
#include <cdk/ast/sequence_node.h>
#include <cdk/types/basic_type.h>

namespace og {

  /**
   * Class for describing function definition nodes.
   */
  class func_def_node: public cdk::typed_node {
    bool _is_public;
    bool _is_required;
    std::string _identifier;
    cdk::sequence_node *_args;
    block_node *_block;

  public:
    inline func_def_node(int lineno, bool is_public, bool is_required,
      cdk::basic_type *tp, std::string *identifier, cdk::sequence_node *args, block_node *block) :
        cdk::typed_node(lineno), _is_public(is_public), _is_required(is_required),
        _identifier(*identifier), _args(args), _block(block) {
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

    inline void identifier(std::string id) {
      _identifier = id;
    }

    inline cdk::sequence_node *args() {
      return _args;
    }

    inline block_node *block() {
      return _block;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_func_def_node(this, level);
    }

  };

} // og

#endif
