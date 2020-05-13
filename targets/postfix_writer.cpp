#include <string>
#include <sstream>
#include "targets/type_checker.h"
#include "targets/postfix_writer.h"
#include "ast/all.h"  // all.h is automatically generated

//---------------------------------------------------------------------------

void og::postfix_writer::do_nil_node(cdk::nil_node * const node, int lvl) {
  // EMPTY
}
void og::postfix_writer::do_data_node(cdk::data_node * const node, int lvl) {
  // EMPTY
}
void og::postfix_writer::do_not_node(cdk::not_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl);
  _pf.NOT();
}
void og::postfix_writer::do_and_node(cdk::and_node * const node, int lvl) {
  // EMPTY
}
void og::postfix_writer::do_or_node(cdk::or_node * const node, int lvl) {
  // EMPTY
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_sequence_node(cdk::sequence_node * const node, int lvl) {
  for (size_t i = 0; i < node->size(); i++) {
    node->node(i)->accept(this, lvl);
  }
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_integer_node(cdk::integer_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  if (_inside_function) _pf.INT(node->value());
  else _pf.SINT(node->value());
}

void og::postfix_writer::do_double_node(cdk::double_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  if (_inside_function) _pf.DOUBLE(node->value());
  else _pf.SDOUBLE(node->value());
}

void og::postfix_writer::do_string_node(cdk::string_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  int new_lbl;

  _pf.RODATA();
  _pf.ALIGN();
  _pf.LABEL(mklbl(new_lbl = ++_lbl));
  _pf.SSTRING(node->value());

  // if (_function != nullptr) {
  _pf.TEXT();
  _pf.ADDR(mklbl(new_lbl));
  // } else {
  //   _pf.DATA();
  //   _pf.SADDR(mklbl(new_lbl));
  // }
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_neg_node(cdk::neg_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl); // determine the value
  if (node->is_typed(cdk::TYPE_DOUBLE)) {
    _pf.DNEG();
  } else {
    _pf.NEG(); // is int
  }
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_add_node(cdk::add_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.ADD();
}
void og::postfix_writer::do_sub_node(cdk::sub_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.SUB();
}
void og::postfix_writer::do_mul_node(cdk::mul_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.MUL();
}
void og::postfix_writer::do_div_node(cdk::div_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.DIV();
}
void og::postfix_writer::do_mod_node(cdk::mod_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.MOD();
}
void og::postfix_writer::do_lt_node(cdk::lt_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.LT();
}
void og::postfix_writer::do_le_node(cdk::le_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.LE();
}
void og::postfix_writer::do_ge_node(cdk::ge_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.GE();
}
void og::postfix_writer::do_gt_node(cdk::gt_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.GT();
}
void og::postfix_writer::do_ne_node(cdk::ne_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.NE();
}
void og::postfix_writer::do_eq_node(cdk::eq_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.EQ();
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_variable_node(cdk::variable_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  // TODO simplified generation: all variables are global
  _pf.ADDR(node->name());
}

void og::postfix_writer::do_rvalue_node(cdk::rvalue_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->lvalue()->accept(this, lvl);
  if (node->is_typed(cdk::TYPE_DOUBLE)) {
    _pf.LDDOUBLE();
  } else {
    // integers, strings, pointers and structs
    _pf.LDINT();
  }
}

void og::postfix_writer::do_assignment_node(cdk::assignment_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->rvalue()->accept(this, lvl);

  if (node->is_typed(cdk::TYPE_DOUBLE)) {
    if (node->rvalue()->is_typed(cdk::TYPE_INT)) {
      // convert int to double
      _pf.I2D();
    }
    _pf.DUP64();
  } else {
    _pf.DUP32();
  }

  node->lvalue()->accept(this, lvl);

  if (node->is_typed(cdk::TYPE_DOUBLE)) {
    _pf.STDOUBLE();
  } else {
    _pf.STINT();
  }
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_evaluation_node(og::evaluation_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl); // determine the value
  if (node->argument()->is_typed(cdk::TYPE_INT)) {
    _pf.TRASH(4); // delete the evaluated value
  } else if (node->argument()->is_typed(cdk::TYPE_STRING)) {
    _pf.TRASH(4); // delete the evaluated value's address
  } else {
    std::cerr << "ERROR: CANNOT HAPPEN!" << std::endl;
    exit(1);
  }
}

void og::postfix_writer::do_write_node(og::write_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl); // determine the value to print

  if (node->argument()->is_typed(cdk::TYPE_INT)) {
    _pf.CALL("printi");
    _pf.TRASH(4); // delete the printed value
  } else if (node->argument()->is_typed(cdk::TYPE_DOUBLE)) {
    _pf.CALL("printd");
    _pf.TRASH(8); // delete the printed value's address
  } else if (node->argument()->is_typed(cdk::TYPE_STRING)) {
    _pf.CALL("prints");
    _pf.TRASH(4); // delete the printed value's address
  } else {
    std::cerr << "ERROR: CANNOT HAPPEN!" << std::endl;
    exit(1);
  }

  // TODO: print multiple values

  if (node->has_newline()) _pf.CALL("println"); // print a newline */
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_input_node(og::input_node * const node, int lvl) {
  /*TODO ASSERT_SAFE_EXPRESSIONS;
  _pf.CALL("readi");
  _pf.LDFVAL32();
  node->argument()->accept(this, lvl);
  _pf.STINT(); */
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_for_node(og::for_node *const node, int lvl) {
  // TODO
  /* THIS WAS WHILE'S CODE
  ASSERT_SAFE_EXPRESSIONS;
  int lbl1, lbl2;
  _pf.LABEL(mklbl(lbl1 = ++_lbl));
  node->condition()->accept(this, lvl);
  _pf.JZ(mklbl(lbl2 = ++_lbl));
  node->block()->accept(this, lvl + 2);
  _pf.JMP(mklbl(lbl1));
  _pf.LABEL(mklbl(lbl2));
  */
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_if_node(og::if_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  int lbl1;
  node->condition()->accept(this, lvl);
  _pf.JZ(mklbl(lbl1 = ++_lbl));
  node->block()->accept(this, lvl + 2);
  _pf.LABEL(mklbl(lbl1));
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_if_else_node(og::if_else_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  int lbl1, lbl2;
  node->condition()->accept(this, lvl);
  _pf.JZ(mklbl(lbl1 = ++_lbl));
  node->thenblock()->accept(this, lvl + 2);
  _pf.JMP(mklbl(lbl2 = ++_lbl));
  _pf.LABEL(mklbl(lbl1));
  node->elseblock()->accept(this, lvl + 2);
  _pf.LABEL(mklbl(lbl1 = lbl2));
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_return_node(og::return_node *const node, int lvl) {
  // TODO
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_id_node(og::id_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl);
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_break_node(og::break_node *const node, int lvl) {
  // TODO
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_continue_node(og::continue_node *const node, int lvl) {
  // TODO
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_var_decl_node_helper(std::shared_ptr<og::symbol> symbol, cdk::expression_node *expression, int lvl) {
  // TODO actually declare variable here
  // TODO: this assumes it'll always be global
  if (expression == nullptr) {
    _pf.BSS(); // uninitialized vars
  } else {
    _pf.DATA(); // initialized variables
  }

  _pf.ALIGN();
  _pf.GLOBAL(symbol->name(), _pf.OBJ());
  _pf.LABEL(symbol->name());

  if (expression == nullptr) {
    // Allocate required memory for unitiliazed variables.
    _pf.SALLOC(symbol->type()->size());
    return;
  }

  if (symbol->type()->name() == cdk::TYPE_DOUBLE && expression->is_typed(cdk::TYPE_INT)) {
    // TODO: convert int para double.
    expression->accept(this, lvl);
  } else {
    expression->accept(this, lvl);
  }
}

void og::postfix_writer::do_var_decl_node(og::var_decl_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  // TODO

  if (node->identifiers().size() == 1) {
    // Single variable that _MAY_ be a tuple.
    std::string &id = *node->identifiers().at(0);
    std::shared_ptr<og::symbol> symbol = _symtab.find(id);
    do_var_decl_node_helper(symbol, node->expression(), lvl);
    return;
  }

  // Multiple variables!
  og::tuple_node* tuple = (og::tuple_node*)(node->expression());
  for (size_t i = 0; i < node->identifiers().size(); i++) {
    std::string &id = *node->identifiers().at(i);
    cdk::expression_node* exp = (cdk::expression_node*)(tuple->nodes()->node(i));
    std::shared_ptr<og::symbol> symbol = _symtab.find(id);
    do_var_decl_node_helper(symbol, exp, lvl);
  }
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_nullptr_node(og::nullptr_node *const node, int lvl) {
  // TODO
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_mem_alloc_node(og::mem_alloc_node *const node, int lvl) {
  // TODO
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_mem_addr_node(og::mem_addr_node *const node, int lvl) {
  // TODO
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_block_node(og::block_node *const node, int lvl) {
  // _symtab.push();
  if(node->declarations()){ node->declarations()->accept(this, lvl+2); }
  if(node->instructions()){ node->instructions()->accept(this, lvl+2); }
  // _symtab.pop();
  // TODO
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_func_def_node(og::func_def_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  // TODO: THIS IS JUST SIMPLE CODE! CHANGE ASAP

  // Note that Simple doesn't have functions. Thus, it doesn't need
  // a function node. However, it must start in the main function.
  // The ProgramNode (representing the whole program) doubles as a
  // main function node.

  // generate the main function (RTS mandates that its name be "_main")
  _pf.TEXT();
  _pf.ALIGN();
  _pf.GLOBAL(node->identifier(), _pf.FUNC());
  _pf.LABEL(node->identifier());
  _pf.ENTER(0);  // Simple doesn't implement local variables

  _inside_function = true;
  node->block()->accept(this, lvl);
  _inside_function = false;

  // end the main function
  _pf.INT(0);
  _pf.STFVAL32();
  _pf.LEAVE();
  _pf.RET();

  // these are just a few library function imports
  _pf.EXTERN("readi");
  _pf.EXTERN("printi");
  _pf.EXTERN("prints");
  _pf.EXTERN("println");
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_sizeof_node(og::sizeof_node *const node, int lvl) {
  // TODO
}

void og::postfix_writer::do_ptr_index_node(og::ptr_index_node *const node, int lvl) {
  // TODO
}

void og::postfix_writer::do_tuple_index_node(og::tuple_index_node *const node, int lvl) {
  // TODO
}

void og::postfix_writer::do_func_decl_node(og::func_decl_node *const node, int lvl) {
  // TODO
}

void og::postfix_writer::do_func_call_node(og::func_call_node *const node, int lvl) {
  // TODO
}

void og::postfix_writer::do_tuple_node(og::tuple_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->nodes()->accept(this, lvl + 2);
}
