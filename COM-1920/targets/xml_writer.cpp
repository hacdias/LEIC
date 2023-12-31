#include <string>
#include "targets/xml_writer.h"
#include "targets/type_checker.h"
#include "ast/all.h"  // automatically generated

//---------------------------------------------------------------------------

void og::xml_writer::do_nil_node(cdk::nil_node * const node, int lvl) {
  openTag(node, lvl);
  closeTag(node, lvl);
}
void og::xml_writer::do_data_node(cdk::data_node * const node, int lvl) {
  openTag(node, lvl);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void og::xml_writer::do_sequence_node(cdk::sequence_node * const node, int lvl) {
  os() << std::string(lvl, ' ') << "<sequence_node size='" << node->size() << "'>" << std::endl;
  for (size_t i = 0; i < node->size(); i++)
    node->node(i)->accept(this, lvl + 2);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void og::xml_writer::do_integer_node(cdk::integer_node * const node, int lvl) {
  process_literal(node, lvl);
}

void og::xml_writer::do_string_node(cdk::string_node * const node, int lvl) {
  process_literal(node, lvl);
}

void og::xml_writer::do_double_node(cdk::double_node * const node, int lvl) {
  process_literal(node, lvl);
}

//---------------------------------------------------------------------------

void og::xml_writer::do_unary_operation(cdk::unary_operation_node * const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  node->argument()->accept(this, lvl + 2);
  closeTag(node, lvl);
}

void og::xml_writer::do_neg_node(cdk::neg_node * const node, int lvl) {
  do_unary_operation(node, lvl);
}

void og::xml_writer::do_mem_alloc_node(og::mem_alloc_node *const node, int lvl) {
  do_unary_operation(node, lvl);
}

void og::xml_writer::do_not_node(cdk::not_node * const node, int lvl) {
  do_unary_operation(node, lvl);
}

void og::xml_writer::do_id_node(og::id_node *const node, int lvl) {
  do_unary_operation(node, lvl);
}

//---------------------------------------------------------------------------

void og::xml_writer::do_binary_operation(cdk::binary_operation_node * const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  node->left()->accept(this, lvl + 2);
  node->right()->accept(this, lvl + 2);
  closeTag(node, lvl);
}

void og::xml_writer::do_add_node(cdk::add_node * const node, int lvl) {
  do_binary_operation(node, lvl);
}
void og::xml_writer::do_sub_node(cdk::sub_node * const node, int lvl) {
  do_binary_operation(node, lvl);
}
void og::xml_writer::do_mul_node(cdk::mul_node * const node, int lvl) {
  do_binary_operation(node, lvl);
}
void og::xml_writer::do_div_node(cdk::div_node * const node, int lvl) {
  do_binary_operation(node, lvl);
}
void og::xml_writer::do_mod_node(cdk::mod_node * const node, int lvl) {
  do_binary_operation(node, lvl);
}
void og::xml_writer::do_lt_node(cdk::lt_node * const node, int lvl) {
  do_binary_operation(node, lvl);
}
void og::xml_writer::do_le_node(cdk::le_node * const node, int lvl) {
  do_binary_operation(node, lvl);
}
void og::xml_writer::do_ge_node(cdk::ge_node * const node, int lvl) {
  do_binary_operation(node, lvl);
}
void og::xml_writer::do_gt_node(cdk::gt_node * const node, int lvl) {
  do_binary_operation(node, lvl);
}
void og::xml_writer::do_ne_node(cdk::ne_node * const node, int lvl) {
  do_binary_operation(node, lvl);
}
void og::xml_writer::do_eq_node(cdk::eq_node * const node, int lvl) {
  do_binary_operation(node, lvl);
}
void og::xml_writer::do_and_node(cdk::and_node * const node, int lvl) {
  do_binary_operation(node, lvl);
}
void og::xml_writer::do_or_node(cdk::or_node * const node, int lvl) {
  do_binary_operation(node, lvl);
}

//---------------------------------------------------------------------------

void og::xml_writer::do_variable_node(cdk::variable_node * const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  os() << std::string(lvl, ' ') << "<" << node->label() << ">" << node->name() << "</" << node->label() << ">" << std::endl;
}

void og::xml_writer::do_rvalue_node(cdk::rvalue_node * const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  node->lvalue()->accept(this, lvl + 4);
  closeTag(node, lvl);
}

void og::xml_writer::do_assignment_node(cdk::assignment_node * const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);

  node->lvalue()->accept(this, lvl);
  reset_new_symbol();

  node->rvalue()->accept(this, lvl + 4);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void og::xml_writer::do_evaluation_node(og::evaluation_node * const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  node->argument()->accept(this, lvl + 2);
  closeTag(node, lvl);
}

void og::xml_writer::do_write_node(og::write_node * const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  os() << std::boolalpha;
  os() << std::string(lvl, ' ') << "<" << node->label()
    << " has_newline='" << node->has_newline()
    << "'>" << std::endl;

  openTag("argument", lvl + 2);
  node->argument()->accept(this, lvl + 4);
  closeTag("argument", lvl + 2);

  closeTag(node, lvl);
  os() << std::noboolalpha;
}

//---------------------------------------------------------------------------

void og::xml_writer::do_input_node(og::input_node * const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void og::xml_writer::do_for_node(og::for_node *const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);

  openTag("init", lvl + 2);
  if (node->init() != nullptr) {
    node->init()->accept(this, lvl + 4);
  }
  closeTag("init", lvl + 2);

  openTag("condition", lvl + 2);
  if (node->condition() != nullptr) {
    node->condition()->accept(this, lvl + 4);
  }
  closeTag("condition", lvl + 2);

  openTag("end", lvl + 2);
  if (node->end() != nullptr) {
    node->end()->accept(this, lvl + 4);
  }
  closeTag("end", lvl + 2);

  openTag("block", lvl + 2);
  node->block()->accept(this, lvl + 4);
  closeTag("block", lvl + 2);

  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void og::xml_writer::do_if_node(og::if_node * const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  openTag("condition", lvl + 2);
  node->condition()->accept(this, lvl + 4);
  closeTag("condition", lvl + 2);
  openTag("then", lvl + 2);
  node->block()->accept(this, lvl + 4);
  closeTag("then", lvl + 2);
  closeTag(node, lvl);
}

void og::xml_writer::do_if_else_node(og::if_else_node * const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  openTag("condition", lvl + 2);
  node->condition()->accept(this, lvl + 4);
  closeTag("condition", lvl + 2);
  openTag("then", lvl + 2);
  node->thenblock()->accept(this, lvl + 4);
  closeTag("then", lvl + 2);
  openTag("else", lvl + 2);
  node->elseblock()->accept(this, lvl + 4);
  closeTag("else", lvl + 2);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void og::xml_writer::do_return_node(og::return_node *const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);

  if (node->value() != nullptr) {
    node->value()->accept(this, lvl + 4);
  }

  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void og::xml_writer::do_break_node(og::break_node *const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void og::xml_writer::do_continue_node(og::continue_node *const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void og::xml_writer::do_var_decl_node(og::var_decl_node *const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  os() << std::boolalpha;

  os() << std::string(lvl, ' ') << "<" << node->label()
    << " is_public='" << node->is_public()
    << "' is_require='" << node->is_require()
    << "' type='" << (node->type() ? cdk::to_string(node->type()) : "auto")
    << "'>" << std::endl;

  openTag("identifiers", lvl + 2);
  for (std::string *s : node->identifiers()) {
    os() << std::string(lvl + 4, ' ') << "<identifier>" << *s << "</identifier>" << std::endl;
  }
  closeTag("identifiers", lvl + 2);

  openTag("expression", lvl + 2);
  if (node->expression() != nullptr) {
    node->expression()->accept(this, lvl + 4);
  }
  closeTag("expression", lvl+2);

  closeTag(node, lvl);
  os() << std::noboolalpha;
}

//---------------------------------------------------------------------------

void og::xml_writer::do_nullptr_node(og::nullptr_node *const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void og::xml_writer::do_mem_addr_node(og::mem_addr_node *const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  node->lvalue()->accept(this, lvl + 2);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void og::xml_writer::do_block_node(og::block_node *const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);

  openTag("declarations", lvl + 2);
  if (node->declarations() != nullptr) {
    node->declarations()->accept(this, lvl + 4);
  }
  closeTag("declarations", lvl + 2);

  openTag("instructions", lvl + 2);
  if (node->instructions() != nullptr) {
    node->instructions()->accept(this, lvl + 4);
  }
  closeTag("instructions", lvl + 2);

  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void og::xml_writer::do_func_decl_node(og::func_decl_node *const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  os() << std::boolalpha;

  os() << std::string(lvl, ' ') << "<" << node->label()
    << " is_public='" << node->is_public()
    << "' is_required='" << node->is_required()
    << "' id='" << node->identifier()
    << "' type='" << (node->type() ? cdk::to_string(node->type()) : "auto")
    << "'>" << std::endl;

  openTag("args", lvl+2);
  if (node->args() != nullptr) {
    node->args()->accept(this, lvl + 4);
  }
  closeTag("args", lvl+2);

  closeTag(node, lvl);
  os() << std::noboolalpha;
}

void og::xml_writer::do_func_def_node(og::func_def_node *const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  os() << std::boolalpha;

  os() << std::string(lvl, ' ') << "<" << node->label()
    << " is_public='" << node->is_public()
    << "' is_required='" << node->is_required()
    << "' id='" << node->identifier()
    << "' type='" << (node->type() ? cdk::to_string(node->type()) : "auto")
    << "'>" << std::endl;

  openTag("args", lvl + 2);
  if (node->args() != nullptr) {
    node->args()->accept(this, lvl + 4);
  }
  closeTag("args", lvl + 2);

  openTag("block", lvl + 2);
  node->block()->accept(this, lvl + 4);
  closeTag("block", lvl + 2);

  closeTag(node, lvl);
  os() << std::noboolalpha;
}

//---------------------------------------------------------------------------

void og::xml_writer::do_sizeof_node(og::sizeof_node *const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  openTag("value", lvl + 2);
  node->value()->accept(this, lvl + 4);
  closeTag("value", lvl + 2);
  closeTag(node, lvl);
}

void og::xml_writer::do_ptr_index_node(og::ptr_index_node *const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  openTag("pointer", lvl + 2);
  node->pointer()->accept(this, lvl + 4);
  closeTag("pointer", lvl + 2);
  openTag("index", lvl + 2);
  node->index()->accept(this, lvl + 4);
  closeTag("index", lvl + 2);
  closeTag(node, lvl);
}

void og::xml_writer::do_tuple_index_node(og::tuple_index_node *const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  openTag("expression", lvl + 2);
  node->expression()->accept(this, lvl + 4);
  closeTag("expression", lvl + 2);
  openTag("index", lvl + 2);
  node->index()->accept(this, lvl + 4);
  closeTag("index", lvl + 2);
  closeTag(node, lvl);
}

void og::xml_writer::do_func_call_node(og::func_call_node *const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  os() << std::string(lvl, ' ') << "<" << node->label() << " id='" << node->identifier() << "'>" << std::endl;
  openTag("expressions", lvl + 2);

  if (node->expressions() != nullptr) {
    node->expressions()->accept(this, lvl + 4);
  }

  closeTag("expressions", lvl + 2);
  closeTag(node, lvl);
}

void og::xml_writer::do_tuple_node(og::tuple_node *const node, int lvl) {
  // ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  node->nodes()->accept(this, lvl + 2);
  closeTag(node, lvl);
}
