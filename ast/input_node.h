#ifndef __OG_AST_INPUT_NODE_H__
#define __OG_AST_INPUT_NODE_H__

namespace og {

  /**
   * Class for describing input nodes.
   */
  class input_node: public cdk::basic_node {

  public:
    inline input_node(int lineno) :
        cdk::basic_node(lineno) {
    }

  public:
    void accept(basic_ast_visitor *sp, int level) {
      sp->do_input_node(this, level);
    }

  };

} // og

#endif