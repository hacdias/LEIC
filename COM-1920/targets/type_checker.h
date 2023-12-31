#ifndef __OG_TARGETS_TYPE_CHECKER_H__
#define __OG_TARGETS_TYPE_CHECKER_H__

#include "targets/basic_ast_visitor.h"

namespace og {

  /**
   * Print nodes as XML elements to the output stream.
   */
  class type_checker: public basic_ast_visitor {
    cdk::symbol_table<og::symbol> &_symtab;
    std::shared_ptr<og::symbol> _function;
    bool _in_function_args;
    std::shared_ptr<cdk::basic_type> _lvalue_type;
    basic_ast_visitor *_parent;

  public:
    type_checker(std::shared_ptr<cdk::compiler> compiler, cdk::symbol_table<og::symbol> &symtab, std::shared_ptr<og::symbol> function, bool in_function_args, std::shared_ptr<cdk::basic_type> lvalue_type,  basic_ast_visitor *parent) :
        basic_ast_visitor(compiler), _symtab(symtab), _function(function), _in_function_args(in_function_args), _lvalue_type(lvalue_type), _parent(parent) {
    }

  public:
    ~type_checker() {
      os().flush();
    }

  protected:
    void processUnaryExpression(cdk::unary_operation_node *const node, int lvl, bool acceptInt, bool acceptDouble);
    void processBinaryExpression(cdk::binary_operation_node *const node, int lvl, bool acceptInt, bool acceptDouble, bool acceptPointer, bool isAdd);
    void throw_cannot_assign(std::shared_ptr<cdk::basic_type> typel, std::shared_ptr<cdk::basic_type> typer);
    template<typename T>
    void process_literal(cdk::literal_node<T> *const node, int lvl) {
    }

  public:
    // do not edit these lines
#define __IN_VISITOR_HEADER__
#include "ast/visitor_decls.h"       // automatically generated
#undef __IN_VISITOR_HEADER__
    // do not edit these lines: end

  };

} // og

//---------------------------------------------------------------------------
//     HELPER MACRO FOR TYPE CHECKING
//---------------------------------------------------------------------------

#define CHECK_TYPES(compiler, symtab, function, in_func_args, lvalue_type, node) { \
  try { \
    og::type_checker checker(compiler, symtab, function, in_func_args, lvalue_type, this); \
    (node)->accept(&checker, 0); \
  } \
  catch (const std::string &problem) { \
    std::cerr << (node)->lineno() << ": " << problem << std::endl; \
    return; \
  } \
}

#define ASSERT_SAFE_EXPRESSIONS CHECK_TYPES(_compiler, _symtab, _function, _in_function_args, _lvalue_type, node)

#endif
