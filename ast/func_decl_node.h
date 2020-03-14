#ifndef __OG_AST_FUNC_DECL_NODE_H__
#define __OG_AST_FUNC_DECL_NODE_H__

#include <string>
#include <cdk/ast/sequence_node.h>

namespace og {

  /**
   * Class for describing func_decl nodes.
   */
  class func_decl_node: public cdk::basic_node {
    bool _caracteristic;
    bool _type;
    std::string _identifier;
    cdk::sequence_node *_variables;
    og::block_node *_block;

  public:
    inline func_decl_node(int lineno, bool &caracteristic, bool &type, std::string &identifier, cdk::sequence_node *variables, og::block_node *block) :
        cdk::basic_node(lineno), _caracteristic(caracteristic), _type(type), _identifier(identifier), _variables(variables), _block(block) {
    }
  public:
    inline bool &caracteristic() {
      return _caracteristic;
    }
    inline bool &type() {
      return _type;
    }
    inline std::string &identifier() {
      return _identifier;
    }
    inline cdk::sequence_node *variables() {
      return _variables;
    }
    inline og::block_node *block() {
      return _block;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_func_decl_node(this, level);
    }

  };

} // og

#endif