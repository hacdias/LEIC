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
  ASSERT_SAFE_EXPRESSIONS;
  // EMPTY
}
void og::postfix_writer::do_or_node(cdk::or_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
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

  if (_inside_function) {
    _pf.TEXT();
    _pf.ADDR(mklbl(new_lbl));
  } else {
    _pf.DATA();
    _pf.SADDR(mklbl(new_lbl));
  }
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
  auto symbol = _symtab.find(node->name());
  if (symbol->offset() == 0) { // global
    _pf.ADDR(symbol->name());
  } else {
    _pf.LOCAL(symbol->offset());
  }
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
  ASSERT_SAFE_EXPRESSIONS;

  /*TODO ASSERT_SAFE_EXPRESSIONS;
  _pf.CALL("readi");
  _pf.LDFVAL32();
  node->argument()->accept(this, lvl);
  _pf.STINT(); */
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_for_node(og::for_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  int for_cond = ++_lbl;
  int for_incr = ++_lbl;
  int for_end = ++_lbl;

  _for_cond.push(for_cond);
  _for_incr.push(for_incr);
  _for_end.push(for_end);

  os() << "\t\t\t;; FOR INIT" << std::endl;
  if (node->init()) {
    node->init()->accept(this, lvl);
  }

  _pf.ALIGN();

  os() << "\t\t\t;; FOR CONDITION" << std::endl;
  _pf.LABEL(mklbl(for_cond));
  if (node->condition()) {
    node->condition()->accept(this, lvl);
    _pf.JZ(mklbl(for_end));
  }

  os() << "\t\t\t;; FOR BLOCK" << std::endl;
  node->block()->accept(this, lvl);

  _pf.LABEL(mklbl(for_incr));
  os() << "\t\t\t;; FOR INCR" << std::endl;
  if (node->end()) {
    node->end()->accept(this, lvl);
  }

  os() << "\t\t\t;; FOR END" << std::endl;
  _pf.JMP(mklbl(for_cond));
  _pf.LABEL(mklbl(for_end));

  _for_cond.pop();
  _for_incr.pop();
  _for_end.pop();
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
  ASSERT_SAFE_EXPRESSIONS;
  // TODO

/*
   // should not reach here without returning a value (if not void)
  if (_function->type()->name() != basic_type::TYPE_VOID) {
    node->retval()->accept(this, lvl + 2);

    if (_function->type()->name() == basic_type::TYPE_INT || _function->type()->name() == basic_type::TYPE_STRING
        || _function->type()->name() == basic_type::TYPE_POINTER) {
      _pf.STFVAL32();
    } else if (_function->type()->name() == basic_type::TYPE_DOUBLE) {
      if (node->retval()->type()->name() == basic_type::TYPE_INT)
        _pf.I2D();
      _pf.STFVAL64();
    } else {
      std::cerr << node->lineno() << ": should not happen: unknown return type" << std::endl;
    }
  } */

  // TODO: substituir por valores corretos.
  _pf.INT(0);
  _pf.STFVAL32();

  _pf.LEAVE();
  _pf.RET();
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_id_node(og::id_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl);
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_break_node(og::break_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  _pf.JMP(mklbl(_for_end.top()));
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_continue_node(og::continue_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  _pf.JMP(mklbl(_for_incr.top()));
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_var_decl_node_helper(std::shared_ptr<og::symbol> symbol, cdk::expression_node *expression, int lvl) {
  int offset = 0;
  int typesize = symbol->type()->size();

  os() << "       ;; ARG: " << symbol->name() << ", " << typesize << std::endl;

  if (_inside_function) {
    _offset -= typesize;
    offset = _offset;
  } else if (_in_function_args) {
    offset = _offset;
    _offset += typesize;
  } else {
    offset = 0; // global variable
  }

  os() << "         ;; OFFSET: " << symbol->name() << ", " << offset << std::endl;

  symbol->offset(offset);

  if (symbol->is_required()) {
    // TODO: is external
  } else if (_inside_function) {
    // if we are dealing with local variables, then no action is needed
    // unless an initializer exists
    if (!expression) {
      return;
    }

    expression->accept(this, lvl);
    if (symbol->type()->name() == cdk::TYPE_INT || symbol->type()->name() == cdk::TYPE_STRING || symbol->type()->name() == cdk::TYPE_POINTER) {
      _pf.LOCAL(symbol->offset());
      _pf.STINT();
    } else if (symbol->type()->name() == cdk::TYPE_DOUBLE) {
      _pf.LOCAL(symbol->offset());
      _pf.STDOUBLE();
    } else if (symbol->type()->name() == cdk::TYPE_STRUCT) {
      // TODO: do something
      std::cout << "do sth" << std::endl;
    } else {
      std::cerr << "cannot initialize" << std::endl;
    }
  } else {
    // Global variables
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
      cdk::integer_node* int_node = dynamic_cast<cdk::integer_node*>(expression);
      cdk::double_node double_int(int_node->lineno(), int_node->value());
      double_int.accept(this, lvl);
    } else {
      expression->accept(this, lvl);
    }
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
  ASSERT_SAFE_EXPRESSIONS;

  if(_inside_function)
    _pf.INT(0);
  else
    _pf.SINT(0);
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_mem_alloc_node(og::mem_alloc_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  node->argument()->accept(this, lvl + 2);
  _pf.INT(3);
  _pf.SHTL();
  _pf.ALLOC();
  _pf.SP();
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_mem_addr_node(og::mem_addr_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  node->lvalue()->accept(this, lvl + 2);
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_block_node(og::block_node *const node, int lvl) {
  _symtab.push();
  if (node->declarations()) node->declarations()->accept(this, lvl+2);
  if (node->instructions()) node->instructions()->accept(this, lvl+2);
  _symtab.pop();
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_func_def_node(og::func_def_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  // remember symbol so that args and body know
  // _functions_to_declare.erase(_function->name());
  _function = _symtab.find(node->identifier());

  _offset = 8; // prepare for arguments (4: remember to account for return address)
  _symtab.push(); // scope of args
  if (node->args()) {
    _in_function_args = true; //FIXME really needed?
    for (size_t i = 0; i < node->args()->size(); i++) {
      cdk::basic_node *arg = node->args()->node(i);
      if (arg == nullptr) break; // this means an empty sequence of arguments
      arg->accept(this, 0); // the function symbol is at the top of the stack
    }
    _in_function_args = false; //FIXME really needed?
  }

  // generate the main function (RTS mandates that its name be "_main")
  _pf.TEXT();
  _pf.ALIGN();
  if (node->is_public()) _pf.GLOBAL(_function->name(), _pf.FUNC());
  _pf.LABEL(_function->name());

  // compute stack size to be reserved for local variables
  frame_size_calculator lsc(_compiler, _symtab);
  node->accept(&lsc, lvl);
  _pf.ENTER(lsc.localsize()); // total stack size reserved for local variables

  _inside_function = true;
  _offset = -_function->type()->size();
  os() << "        ;; before body " << std::endl;
  node->block()->accept(this, lvl + 4);
  os() << "        ;; after body " << std::endl;
  _inside_function = false;
  _symtab.pop();

  // end the main function
  // _pf.INT(0);
  // _pf.STFVAL32();
  _pf.LEAVE();
  _pf.RET();

  if (node->identifier() == "_main") {
    // these are just a few library function imports
    _pf.EXTERN("readi");
    _pf.EXTERN("printi");
    _pf.EXTERN("prints");
    _pf.EXTERN("println");
  }
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_sizeof_node(og::sizeof_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  // TODO: string?
  _pf.INT(node->type()->size());
}

void og::postfix_writer::do_ptr_index_node(og::ptr_index_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  // TODO
}

void og::postfix_writer::do_tuple_index_node(og::tuple_index_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  // TODO
}

void og::postfix_writer::do_func_decl_node(og::func_decl_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  // TODO
}

void og::postfix_writer::do_func_call_node(og::func_call_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  // TODO
}

void og::postfix_writer::do_tuple_node(og::tuple_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->nodes()->accept(this, lvl + 2);
}
