#include <string>
#include "targets/frame_size_calculator.h"
#include "targets/type_checker.h"
#include "targets/symbol.h"
#include "ast/all.h"

og::frame_size_calculator::~frame_size_calculator() {
  os().flush();
}

void og::frame_size_calculator::do_add_node(cdk::add_node * const node, int lvl) {
  // EMPTY
}
void og::frame_size_calculator::do_and_node(cdk::and_node * const node, int lvl) {
  // EMPTY
}
void og::frame_size_calculator::do_assignment_node(cdk::assignment_node * const node, int lvl) {
  // EMPTY
}
void og::frame_size_calculator::do_data_node(cdk::data_node * const node, int lvl) {
  // EMPTY
}
void og::frame_size_calculator::do_div_node(cdk::div_node * const node, int lvl) {
  // EMPTY
}
void og::frame_size_calculator::do_double_node(cdk::double_node * const node, int lvl) {
  // EMPTY
}
void og::frame_size_calculator::do_eq_node(cdk::eq_node * const node, int lvl) {
  // EMPTY
}
void og::frame_size_calculator::do_ge_node(cdk::ge_node * const node, int lvl) {
  // EMPTY
}
void og::frame_size_calculator::do_gt_node(cdk::gt_node * const node, int lvl) {
  // EMPTY
}
void og::frame_size_calculator::do_integer_node(cdk::integer_node * const node, int lvl) {
  // EMPTY
}
void og::frame_size_calculator::do_le_node(cdk::le_node * const node, int lvl) {
  // EMPTY
}
void og::frame_size_calculator::do_lt_node(cdk::lt_node * const node, int lvl) {
  // EMPTY
}
void og::frame_size_calculator::do_mod_node(cdk::mod_node * const node, int lvl) {
  // EMPTY
}
void og::frame_size_calculator::do_mul_node(cdk::mul_node * const node, int lvl) {
  // EMPTY
}
void og::frame_size_calculator::do_ne_node(cdk::ne_node * const node, int lvl) {
  // EMPTY
}
void og::frame_size_calculator::do_neg_node(cdk::neg_node * const node, int lvl) {
  // EMPTY
}
void og::frame_size_calculator::do_nil_node(cdk::nil_node * const node, int lvl) {
  // EMPTY
}
void og::frame_size_calculator::do_not_node(cdk::not_node * const node, int lvl) {
  // EMPTY
}
void og::frame_size_calculator::do_or_node(cdk::or_node * const node, int lvl) {
  // EMPTY
}
void og::frame_size_calculator::do_rvalue_node(cdk::rvalue_node * const node, int lvl) {
  // EMPTY
}
void og::frame_size_calculator::do_string_node(cdk::string_node * const node, int lvl) {
  // EMPTY
}
void og::frame_size_calculator::do_sub_node(cdk::sub_node * const node, int lvl) {
  // EMPTY
}
void og::frame_size_calculator::do_sequence_node(cdk::sequence_node * const node, int lvl) {
  for (size_t i = 0; i < node->size(); i++) {
    cdk::basic_node *n = node->node(i);
    if (n == nullptr) break;
    n->accept(this, lvl + 2);
  }
}
void og::frame_size_calculator::do_break_node(og::break_node *const node, int lvl) {}
void og::frame_size_calculator::do_continue_node(og::continue_node *const node, int lvl) {}
void og::frame_size_calculator::do_evaluation_node(og::evaluation_node *const node, int lvl) {}
void og::frame_size_calculator::do_func_call_node(og::func_call_node *const node, int lvl) {}
void og::frame_size_calculator::do_id_node(og::id_node *const node, int lvl) {}
void og::frame_size_calculator::do_input_node(og::input_node *const node, int lvl) {}
void og::frame_size_calculator::do_mem_addr_node(og::mem_addr_node *const node, int lvl) {}
void og::frame_size_calculator::do_mem_alloc_node(og::mem_alloc_node *const node, int lvl) {}
void og::frame_size_calculator::do_nullptr_node(og::nullptr_node *const node, int lvl) {}
void og::frame_size_calculator::do_ptr_index_node(og::ptr_index_node *const node, int lvl) {}
void og::frame_size_calculator::do_return_node(og::return_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
}
void og::frame_size_calculator::do_sizeof_node(og::sizeof_node *const node, int lvl) {}
void og::frame_size_calculator::do_tuple_index_node(og::tuple_index_node *const node, int lvl) {}
void og::frame_size_calculator::do_tuple_node(og::tuple_node *const node, int lvl) {}
void og::frame_size_calculator::do_write_node(og::write_node *const node, int lvl) {}
void og::frame_size_calculator::do_variable_node(cdk::variable_node * const node, int lvl) {}
void og::frame_size_calculator::do_func_decl_node(og::func_decl_node *const node, int lvl) {}
void og::frame_size_calculator::do_block_node(og::block_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  if (node->declarations()) node->declarations()->accept(this, lvl + 2);
  if (node->instructions()) node->instructions()->accept(this, lvl + 2);
}

void og::frame_size_calculator::do_for_node(og::for_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  if (node->init()) {
    node->init()->accept(this, lvl + 2);
  }
  node->block()->accept(this, lvl + 2);
}

void og::frame_size_calculator::do_if_node(og::if_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->block()->accept(this, lvl + 2);
}

void og::frame_size_calculator::do_if_else_node(og::if_else_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->thenblock()->accept(this, lvl + 2);
  if (node->elseblock()) node->elseblock()->accept(this, lvl + 2);
}

void og::frame_size_calculator::do_var_decl_node(og::var_decl_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  _localsize += node->type()->size();
}

void og::frame_size_calculator::do_func_def_node(og::func_def_node * const node, int lvl) {
  node->block()->accept(this, lvl + 2);
  _localsize += _function->type()->size(); // save space for the function's return type
}
