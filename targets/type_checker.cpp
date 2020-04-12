#include <string>
#include "targets/type_checker.h"
#include "ast/all.h"  // automatically generated
#include <cdk/types/primitive_type.h>

#define ASSERT_UNSPEC { if (node->type() != nullptr && !node->is_typed(cdk::TYPE_UNSPEC)) return; }

//---------------------------------------------------------------------------

void og::type_checker::do_sequence_node(cdk::sequence_node *const node, int lvl) {
  // EMPTY
}

//---------------------------------------------------------------------------

void og::type_checker::do_nil_node(cdk::nil_node *const node, int lvl) {
  // EMPTY
}

void og::type_checker::do_data_node(cdk::data_node *const node, int lvl) {
  // EMPTY
}

//---------------------------------------------------------------------------

void og::type_checker::do_integer_node(cdk::integer_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));
}

void og::type_checker::do_string_node(cdk::string_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->type(cdk::make_primitive_type(4, cdk::TYPE_STRING));
}

void og::type_checker::do_double_node(cdk::double_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->type(cdk::make_primitive_type(4, cdk::TYPE_DOUBLE));
}

//---------------------------------------------------------------------------

void og::type_checker::processUnaryExpression(cdk::unary_operation_node *const node, int lvl, bool acceptInt, bool acceptDouble) {
  ASSERT_UNSPEC;
  node->argument()->accept(this, lvl + 2);

  if (acceptInt && node->argument()->is_typed(cdk::TYPE_INT))
    node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

  else if (acceptDouble && node->argument()->is_typed(cdk::TYPE_DOUBLE))
    node->type(cdk::make_primitive_type(4, cdk::TYPE_DOUBLE));

  else
    throw std::string("wrong type in argument of unary expression");
}

void og::type_checker::do_neg_node(cdk::neg_node *const node, int lvl) {
  // int, double
  processUnaryExpression(node, lvl, true, true);
}

void og::type_checker::do_id_node(og::id_node *const node, int lvl) {
  // int, double
  processUnaryExpression(node, lvl, true, true);
}

void og::type_checker::do_not_node(cdk::not_node *const node, int lvl) {
  // int
  processUnaryExpression(node, lvl, true, false);
}

//---------------------------------------------------------------------------

void og::type_checker::processBinaryExpression(cdk::binary_operation_node *const node, int lvl, bool acceptInt, bool acceptDouble, bool acceptPointer) {
  ASSERT_UNSPEC;
  node->left()->accept(this, lvl + 2);

  if (acceptInt && node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_INT))
    node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

  else if (acceptDouble && node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_DOUBLE))
    node->type(cdk::make_primitive_type(4, cdk::TYPE_DOUBLE));

  else if (acceptPointer && node->left()->is_typed(cdk::TYPE_POINTER) && node->right()->is_typed(cdk::TYPE_POINTER))
    node->type(cdk::make_primitive_type(4, cdk::TYPE_POINTER));

  else if (acceptInt && acceptPointer && node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_POINTER))
    node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

  else if (acceptInt && acceptPointer && node->left()->is_typed(cdk::TYPE_POINTER) && node->right()->is_typed(cdk::TYPE_INT))
    node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

  else throw std::string("wrong type for binary expression");
}

void og::type_checker::do_add_node(cdk::add_node *const node, int lvl) {
  // int, real, pointer
  processBinaryExpression(node, lvl, true, true, true);
}

void og::type_checker::do_sub_node(cdk::sub_node *const node, int lvl) {
  // int, real, pointer
  processBinaryExpression(node, lvl, true, true, true);
}

void og::type_checker::do_mul_node(cdk::mul_node *const node, int lvl) {
  // int, real
  processBinaryExpression(node, lvl, true, true, false);
}

void og::type_checker::do_div_node(cdk::div_node *const node, int lvl) {
  // int, real
  processBinaryExpression(node, lvl, true, true, false);
}

void og::type_checker::do_mod_node(cdk::mod_node *const node, int lvl) {
  // int, real
  processBinaryExpression(node, lvl, true, true, false);
}

void og::type_checker::do_lt_node(cdk::lt_node *const node, int lvl) {
  // int, real
  processBinaryExpression(node, lvl, true, true, false);
}

void og::type_checker::do_le_node(cdk::le_node *const node, int lvl) {
  // int, real
  processBinaryExpression(node, lvl, true, true, false);
}

void og::type_checker::do_ge_node(cdk::ge_node *const node, int lvl) {
  // int, real
  processBinaryExpression(node, lvl, true, true, false);
}

void og::type_checker::do_gt_node(cdk::gt_node *const node, int lvl) {
  // int, real
  processBinaryExpression(node, lvl, true, true, false);
}

void og::type_checker::do_ne_node(cdk::ne_node *const node, int lvl) {
  // int, real, pointer
  processBinaryExpression(node, lvl, true, true, true);
}

void og::type_checker::do_eq_node(cdk::eq_node *const node, int lvl) {
  // int, real, pointer
  processBinaryExpression(node, lvl, true, true, true);
}

void og::type_checker::do_and_node(cdk::and_node *const node, int lvl) {
  // int
  processBinaryExpression(node, lvl, true, false, false);
}

void og::type_checker::do_or_node(cdk::or_node *const node, int lvl) {
  // int
  processBinaryExpression(node, lvl, true, false, false);
}

//---------------------------------------------------------------------------

void og::type_checker::do_variable_node(cdk::variable_node *const node, int lvl) {
  ASSERT_UNSPEC;
  const std::string &id = node->name();
  std::shared_ptr<og::symbol> symbol = _symtab.find(id);

  if (symbol != nullptr) {
    node->type(symbol->type());
  } else {
    throw id;
  }
}

void og::type_checker::do_rvalue_node(cdk::rvalue_node *const node, int lvl) {
  ASSERT_UNSPEC;
  try {
    node->lvalue()->accept(this, lvl);
    node->type(node->lvalue()->type());
  } catch (const std::string &id) {
    throw "undeclared variable '" + id + "'";
  }
}

void og::type_checker::do_assignment_node(cdk::assignment_node *const node, int lvl) {
  ASSERT_UNSPEC;

  try {
    node->lvalue()->accept(this, lvl);
  } catch (const std::string &id) {
    auto symbol = std::make_shared<og::symbol>(cdk::make_primitive_type(4, cdk::TYPE_INT), id, 0);
    _symtab.insert(id, symbol);
    _parent->set_new_symbol(symbol);  // advise parent that a symbol has been inserted
    node->lvalue()->accept(this, lvl);  //DAVID: bah!
  }

  if (!node->lvalue()->is_typed(cdk::TYPE_INT)) throw std::string("wrong type in left argument of assignment expression");

  node->rvalue()->accept(this, lvl + 2);
  if (!node->rvalue()->is_typed(cdk::TYPE_INT)) throw std::string("wrong type in right argument of assignment expression");

  // in Simple, expressions are always int
  node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));
}

//---------------------------------------------------------------------------

void og::type_checker::do_evaluation_node(og::evaluation_node *const node, int lvl) {
  node->argument()->accept(this, lvl + 2);
}

void og::type_checker::do_write_node(og::write_node *const node, int lvl) {
  // TODO
  node->argument()->accept(this, lvl + 2);
}

//---------------------------------------------------------------------------

void og::type_checker::do_input_node(og::input_node *const node, int lvl) {
  /* try {
    node->argument()->accept(this, lvl);
  } catch (const std::string &id) {
    throw "undeclared variable '" + id + "'";
  } */
}

//---------------------------------------------------------------------------

void og::type_checker::do_for_node(og::for_node *const node, int lvl) {
  // TODO
  // THIS WAS WHILE'S CODE
  // node->condition()->accept(this, lvl + 4);
}

//---------------------------------------------------------------------------

void og::type_checker::do_if_node(og::if_node *const node, int lvl) {
  node->condition()->accept(this, lvl + 4);
}

void og::type_checker::do_if_else_node(og::if_else_node *const node, int lvl) {
  node->condition()->accept(this, lvl + 4);
}

//---------------------------------------------------------------------------

void og::type_checker::do_return_node(og::return_node *const node, int lvl) {
  // TODO
}

//---------------------------------------------------------------------------

void og::type_checker::do_break_node(og::break_node *const node, int lvl) {
  // TODO
}

//---------------------------------------------------------------------------

void og::type_checker::do_continue_node(og::continue_node *const node, int lvl) {
  // TODO
}

//---------------------------------------------------------------------------

void og::type_checker::do_var_decl_node(og::var_decl_node *const node, int lvl) {
  // TODO
}

//---------------------------------------------------------------------------

void og::type_checker::do_nullptr_node(og::nullptr_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->type(cdk::make_primitive_type(4, cdk::TYPE_POINTER));
}

//---------------------------------------------------------------------------

void og::type_checker::do_mem_alloc_node(og::mem_alloc_node *const node, int lvl) {
  // TODO
}

//---------------------------------------------------------------------------

void og::type_checker::do_mem_addr_node(og::mem_addr_node *const node, int lvl) {
  // TODO
}

//---------------------------------------------------------------------------

void og::type_checker::do_block_node(og::block_node *const node, int lvl) {
  // TODO
}

//---------------------------------------------------------------------------

void og::type_checker::do_func_def_node(og::func_def_node *const node, int lvl) {
  // TODO
}

//---------------------------------------------------------------------------

void og::type_checker::do_sizeof_node(og::sizeof_node *const node, int lvl) {
  // TODO
}

void og::type_checker::do_ptr_index_node(og::ptr_index_node *const node, int lvl) {
  // TODO
}

void og::type_checker::do_tuple_index_node(og::tuple_index_node *const node, int lvl) {
  // TODO
}

void og::type_checker::do_func_decl_node(og::func_decl_node *const node, int lvl) {
  // TODO
}

void og::type_checker::do_func_call_node(og::func_call_node *const node, int lvl) {
  // TODO
}

void og::type_checker::do_tuple_node(og::tuple_node *const node, int lvl) {
  // TODO
}
