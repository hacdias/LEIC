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
  if (_inside_function) {
    _pf.INT(node->value());
  } else {
    _pf.SINT(node->value());
  }
}

void og::postfix_writer::do_double_node(cdk::double_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  if (_inside_function) {
    _pf.DOUBLE(node->value());
  } else {
    _pf.SDOUBLE(node->value());
  }
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

  node->argument()->accept(this, lvl);
  if (node->is_typed(cdk::TYPE_DOUBLE)) {
    _pf.DNEG();
  } else {
    _pf.NEG();
  }
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_add_node(cdk::add_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  node->left()->accept(this, lvl + 2);
  if (node->is_typed(cdk::TYPE_DOUBLE) && node->left()->is_typed(cdk::TYPE_INT)) {
    _pf.I2D();
  } else if (node->is_typed(cdk::TYPE_POINTER) && node->left()->is_typed(cdk::TYPE_INT)) {
    _pf.INT(3);
    _pf.SHTL();
  }

  node->right()->accept(this, lvl + 2);
  if (node->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT)) {
    _pf.I2D();
  } else if (node->is_typed(cdk::TYPE_POINTER) && node->right()->is_typed(cdk::TYPE_INT)) {
    _pf.INT(3);
    _pf.SHTL();
  }

  if (node->is_typed(cdk::TYPE_DOUBLE)) {
    _pf.DADD();
  } else {
    _pf.ADD();
  }
}

void og::postfix_writer::do_sub_node(cdk::sub_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  node->left()->accept(this, lvl + 2);
  if (node->is_typed(cdk::TYPE_DOUBLE) && node->left()->is_typed(cdk::TYPE_INT)) {
    _pf.I2D();
  }

  node->right()->accept(this, lvl + 2);
  if (node->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT)) {
    _pf.I2D();
  } else if (node->is_typed(cdk::TYPE_POINTER) && node->right()->is_typed(cdk::TYPE_INT)) {
    _pf.INT(3);
    _pf.SHTL();
  }

  if (node->is_typed(cdk::TYPE_DOUBLE)) {
    _pf.DSUB();
  } else {
    _pf.SUB();
  }
}

void og::postfix_writer::do_mul_node(cdk::mul_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  node->left()->accept(this, lvl + 2);
  if (node->is_typed(cdk::TYPE_DOUBLE) && node->left()->is_typed(cdk::TYPE_INT)) {
    _pf.I2D();
  }

  node->right()->accept(this, lvl + 2);
  if (node->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT)) {
    _pf.I2D();
  }

  if (node->is_typed(cdk::TYPE_DOUBLE)) {
    _pf.DMUL();
  } else {
    _pf.MUL();
  }
}

void og::postfix_writer::do_div_node(cdk::div_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  node->left()->accept(this, lvl + 2);
  if (node->is_typed(cdk::TYPE_DOUBLE) && node->left()->is_typed(cdk::TYPE_INT)) {
    _pf.I2D();
  }

  node->right()->accept(this, lvl + 2);
  if (node->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT)) {
    _pf.I2D();
  }

  if (node->is_typed(cdk::TYPE_DOUBLE)) {
    _pf.DDIV();
  } else {
    _pf.DIV();
  }
}

void og::postfix_writer::do_mod_node(cdk::mod_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.MOD();
}

void og::postfix_writer::do_binary_int_double_helper(cdk::binary_operation_node* const node, int lvl) {
  node->left()->accept(this, lvl + 2);
  if (node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
    _pf.I2D();
  }

  node->right()->accept(this, lvl + 2);
  if (node->right()->is_typed(cdk::TYPE_INT) && node->left()->is_typed(cdk::TYPE_DOUBLE)) {
    _pf.I2D();
  }
}

void og::postfix_writer::do_lt_node(cdk::lt_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  do_binary_int_double_helper(node, lvl);
  _pf.LT();
}

void og::postfix_writer::do_le_node(cdk::le_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  do_binary_int_double_helper(node, lvl);
  _pf.LE();
}

void og::postfix_writer::do_ge_node(cdk::ge_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  do_binary_int_double_helper(node, lvl);
  _pf.GE();
}

void og::postfix_writer::do_gt_node(cdk::gt_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  do_binary_int_double_helper(node, lvl);
  _pf.GT();
}

void og::postfix_writer::do_ne_node(cdk::ne_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  do_binary_int_double_helper(node, lvl);
  _pf.NE();
}

void og::postfix_writer::do_eq_node(cdk::eq_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  do_binary_int_double_helper(node, lvl);
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
    _pf.LDINT(); // integers, strings, pointers and structs
  }
}

void og::postfix_writer::do_assignment_node(cdk::assignment_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->rvalue()->accept(this, lvl);

  if (node->is_typed(cdk::TYPE_DOUBLE)) {
    if (node->rvalue()->is_typed(cdk::TYPE_INT)) {
      _pf.I2D(); // int to double
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
  node->argument()->accept(this, lvl);
  _pf.TRASH(node->argument()->type()->size());
}

void og::postfix_writer::do_write_node(og::write_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl);

  if (node->argument()->is_typed(cdk::TYPE_INT)) {
    _functions_to_declare.insert("printi");
    _pf.CALL("printi");
    _pf.TRASH(4);
  } else if (node->argument()->is_typed(cdk::TYPE_DOUBLE)) {
    _functions_to_declare.insert("printd");
    _pf.CALL("printd");
    _pf.TRASH(8);
  } else if (node->argument()->is_typed(cdk::TYPE_STRING)) {
    _functions_to_declare.insert("prints");
    _pf.CALL("prints");
    _pf.TRASH(4);
  } else if (node->argument()->is_typed(cdk::TYPE_STRUCT)) {
    // TODO: print the struct, for each element and print each element.
    std::cerr << node->lineno() << "TODO: WRITE TYPE STRUCT" << std::endl;
  } else {
    throw std::string("unsupported type to print");
    exit(1);
  }

  if (node->has_newline()) {
    _functions_to_declare.insert("println");
    _pf.CALL("println");
  }
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_input_node(og::input_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  // TODO: implement this. Provavelmente é parecido ao do gr8.
  // _pf.CALL("readi");
  // _pf.LDFVAL32();
  // node->argument()->accept(this, lvl);
  // _pf.STINT();
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_for_node(og::for_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  int for_cond = ++_lbl;
  int for_incr = ++_lbl;
  int for_end = ++_lbl;

  // Keep labels information so we can use them on breaks and continues.
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

  if (_function->is_typed(cdk::TYPE_VOID)) {
    _pf.LEAVE();
    _pf.RET();
    return;
  }

  node->value()->accept(this, lvl + 2);

  if (_function->is_typed(cdk::TYPE_INT) ||
    _function->is_typed(cdk::TYPE_STRING) ||
    _function->is_typed(cdk::TYPE_POINTER)) {
    _pf.STFVAL32();
  } else if (_function->is_typed(cdk::TYPE_DOUBLE)) {
    if (node->value()->is_typed(cdk::TYPE_INT)) {
      _pf.I2D();
    }

    _pf.STFVAL64();
  } else if (_function->is_typed(cdk::TYPE_STRUCT)) {
    // TODO: autos e inválidos
    std::cout << "TODO: return type struct" << std::endl;
  } else {
    throw std::string("invalid type toreturn");
  }

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

  if (_inside_function) {
    _offset -= typesize;
    offset = _offset;
  } else if (_in_function_args) {
    offset = _offset;
    _offset += typesize;
  } else {
    offset = 0; // global variable
  }

  symbol->offset(offset);

  if (symbol->is_required()) {
    // TODO: is external
  } else if (_inside_function) {
    // if we are dealing with local variables, then no action is needed
    // unless an initializer exists because we already allocate the
    // memory space when defining a function.
    if (!expression) {
      return;
    }

    expression->accept(this, lvl);

    if (symbol->is_typed(cdk::TYPE_INT) ||
      symbol->is_typed(cdk::TYPE_STRING) ||
      symbol->is_typed(cdk::TYPE_POINTER)) {
      _pf.LOCAL(symbol->offset());
      _pf.STINT();
    } else if (symbol->is_typed(cdk::TYPE_DOUBLE)) {
      if (expression->is_typed(cdk::TYPE_INT)) {
        _pf.I2D();
      }
      _pf.LOCAL(symbol->offset());
      _pf.STDOUBLE();
    } else if (symbol->is_typed(cdk::TYPE_STRUCT)) {
      // TODO: do something
      std::cout << "TODO: initialize type struct" << std::endl;
    } else {
      throw std::string("invalid initializer");
    }
  } else if (_in_function_args) {
    // When these are function arguments, we don't do anything because the space
    // is already allocated elsewhere.
  } else {
    // Global variables
    if (expression == nullptr) {
      _pf.BSS(); // uninitialized vars
    } else {
      _pf.DATA(); // initialized variables
    }

    _pf.ALIGN();
    if (symbol->is_public()) {
      _pf.GLOBAL(symbol->name(), _pf.OBJ());
    }
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

  if (node->identifiers().size() == 1) {
    // Single variable that may be a true tuple.
    std::string &id = *node->identifiers().at(0);
    std::shared_ptr<og::symbol> symbol = _symtab.find(id);
    do_var_decl_node_helper(symbol, node->expression(), lvl);
    return;
  }

  // Just a collection of multiple variables.
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

  if (_inside_function)  {
    _pf.INT(0);
  } else {
    _pf.SINT(0);
  }
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_mem_alloc_node(og::mem_alloc_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  std::shared_ptr<cdk::reference_type> type = std::static_pointer_cast<cdk::reference_type>(node->type());
  node->argument()->accept(this, lvl + 2);
  _pf.INT(type->referenced()->size());
  _pf.MUL();
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
  _function = new_symbol();
  _functions_to_declare.erase(_function->name());
  reset_new_symbol();

  _offset = 8; // prepare for arguments (4: remember to account for return address)
  _symtab.push(); // scope of args

  if (node->args()) {
    _in_function_args = true; //FIXME really needed?

    for (size_t i = 0; i < node->args()->size(); i++) {
      cdk::basic_node *arg = node->args()->node(i);
      arg->accept(this, 0);
    }

    _in_function_args = false; //FIXME really needed?
  }

  _pf.TEXT();
  _pf.ALIGN();
  if (node->is_public()) {
    _pf.GLOBAL(_function->name(), _pf.FUNC());
  }
  _pf.LABEL(_function->name());

  // compute stack size to be reserved for local variables
  frame_size_calculator lsc(_compiler, _symtab, _function, _in_function_args);
  node->accept(&lsc, lvl);
  _pf.ENTER(lsc.localsize()); // total stack size reserved for local variables

  _inside_function = true;
  _offset = -_function->type()->size();
  os() << "        ;; before body " << std::endl;
  node->block()->accept(this, lvl + 4);
  os() << "        ;; after body " << std::endl;
  _inside_function = false;
  _symtab.pop();

  _pf.LEAVE();
  _pf.RET();

  if (node->identifier() == "_main") {
    // declare external functions
    for (std::string s : _functions_to_declare)
      _pf.EXTERN(s);
  }

  _function = nullptr;
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_sizeof_node(og::sizeof_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  _pf.INT(node->value()->type()->size());
}

void og::postfix_writer::do_ptr_index_node(og::ptr_index_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  node->pointer()->accept(this, lvl);
  node->index()->accept(this, lvl);
  _pf.INT(node->type()->size());
  _pf.MUL();
  _pf.ADD(); // add pointer and index
}

void og::postfix_writer::do_tuple_index_node(og::tuple_index_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  // TODO
}

void og::postfix_writer::do_func_decl_node(og::func_decl_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  std::shared_ptr<og::symbol> function = new_symbol();
   if (!function) {
    return;
  }
  _functions_to_declare.insert(function->name());
  reset_new_symbol();
}

void og::postfix_writer::do_func_call_node(og::func_call_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  std::shared_ptr<og::symbol> symbol = _symtab.find(node->identifier());
  if (!symbol) {
    throw std::string("function does not exist");
  }

  if (!symbol->type()) {
    throw std::string("could not infer function return type");
  }

  size_t args_size = 0;

  if (node->expressions()) {
    for (int ax = node->expressions()->size(); ax > 0; ax--) {
      cdk::expression_node *arg = dynamic_cast<cdk::expression_node*>(node->expressions()->node(ax - 1));
      std::shared_ptr<og::symbol> param = symbol->params()->at(ax - 1);

      arg->accept(this, lvl + 2);
      args_size += param->type()->size();

      if (param->is_typed(cdk::TYPE_DOUBLE) && arg->is_typed(cdk::TYPE_INT)) {
        _pf.I2D();
      }
    }
  }

  _pf.CALL(node->identifier());
  if (args_size != 0) {
    _pf.TRASH(args_size);
  }

  if (symbol->is_typed(cdk::TYPE_INT) || symbol->is_typed(cdk::TYPE_POINTER) || symbol->is_typed(cdk::TYPE_STRING) || symbol->is_typed(cdk::TYPE_STRUCT)) {
    _pf.LDFVAL32();
  } else if (symbol->is_typed(cdk::TYPE_DOUBLE)) {
    _pf.LDFVAL64();
  }
}

void og::postfix_writer::do_tuple_node(og::tuple_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->nodes()->accept(this, lvl + 2);
}
