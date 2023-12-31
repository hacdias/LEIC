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
  int lbl = ++_lbl;

  node->left()->accept(this, lvl + 2);
  _pf.DUP32();
  _pf.JZ(mklbl(lbl)); // evaluate the second expression only if the first is true

  node->right()->accept(this, lvl + 2);
  _pf.AND();
  _pf.ALIGN();
  _pf.LABEL(mklbl(lbl));
}

void og::postfix_writer::do_or_node(cdk::or_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  int lbl = ++_lbl;

  node->left()->accept(this, lvl + 2);
  _pf.DUP32();
  _pf.JNZ(mklbl(lbl)); // evaluate the second expression only if the first is false

  node->right()->accept(this, lvl + 2);
  _pf.OR();
  _pf.ALIGN();
  _pf.LABEL(mklbl(lbl));
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
    if (symbol->is_typed(cdk::TYPE_STRUCT)) {
      _is_struct_lval = true; // Hack, not proud!
    }
    _pf.LOCAL(symbol->offset());
  }
}

void og::postfix_writer::do_rvalue_node(cdk::rvalue_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->lvalue()->accept(this, lvl);
  if (node->is_typed(cdk::TYPE_DOUBLE)) {
    _pf.LDDOUBLE();
  } else if (!node->is_typed(cdk::TYPE_STRUCT)) {
    _pf.LDINT(); // integers, strings and pointers
  } else {
    // structs must be indexed!
  }
}

void og::postfix_writer::do_assignment_node(cdk::assignment_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  _lvalue_type = node->lvalue()->type();

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
    _symbols_to_declare.insert("printi");
    _pf.CALL("printi");
    _pf.TRASH(4);
  } else if (node->argument()->is_typed(cdk::TYPE_DOUBLE)) {
    _symbols_to_declare.insert("printd");
    _pf.CALL("printd");
    _pf.TRASH(8);
  } else if (node->argument()->is_typed(cdk::TYPE_STRING)) {
    _symbols_to_declare.insert("prints");
    _pf.CALL("prints");
    _pf.TRASH(4);
  } else if (node->argument()->is_typed(cdk::TYPE_STRUCT)) {
    std::shared_ptr<cdk::structured_type> tuple_type = cdk::structured_type_cast(node->argument()->type());
    size_t length = tuple_type->length();

    for (size_t i = 0; i < length; i++) {
      std::shared_ptr<cdk::basic_type> type = tuple_type->component(i);

      if (type->name() == cdk::TYPE_INT) {
        _symbols_to_declare.insert("printi");
        _pf.CALL("printi");
        _pf.TRASH(4);
      } else if (type->name() == cdk::TYPE_DOUBLE) {
        _symbols_to_declare.insert("printd");
        _pf.CALL("printd");
        _pf.TRASH(8);
      } else if (type->name() == cdk::TYPE_STRING) {
        _symbols_to_declare.insert("prints");
        _pf.CALL("prints");
        _pf.TRASH(4);
      } else {
        throw std::string("invalid struct element type");
      }
    }
  } else {
    throw std::string("unsupported type to print");
    exit(1);
  }

  if (node->has_newline()) {
    _symbols_to_declare.insert("println");
    _pf.CALL("println");
  }
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_input_node(og::input_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  if( node->is_typed(cdk::TYPE_INT) ) {
    _symbols_to_declare.insert("readi");
    _pf.CALL("readi");
    _pf.LDFVAL32();
  } else if ( node->is_typed(cdk::TYPE_DOUBLE) ) {
    _symbols_to_declare.insert("readd");
    _pf.CALL("readd");
    _pf.LDFVAL64();
  } else {
    throw std::string("invalid type to input");
  }

  _lvalue_type = cdk::make_primitive_type(0, cdk::TYPE_VOID);
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

  if (node->init()) {
    node->init()->accept(this, lvl);
  }

  _pf.ALIGN();

  _pf.LABEL(mklbl(for_cond));
  if (node->condition()) {
    node->condition()->accept(this, lvl);
    _pf.JZ(mklbl(for_end));
  }

  node->block()->accept(this, lvl);

  _pf.LABEL(mklbl(for_incr));
  if (node->end()) {
    node->end()->accept(this, lvl);
  }

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

  _return_count++;
  _is_true_order = true; // Hack!
  node->value()->accept(this, lvl + 2);
  _is_true_order = false;

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
    std::shared_ptr<cdk::structured_type> tuple_type = cdk::structured_type_cast(node->value()->type());
    std::shared_ptr<cdk::structured_type> func_type = cdk::structured_type_cast(_function->type());
    int offset = func_type->size();
    size_t length = func_type->length();

    for (int i = length - 1; i >= 0; i--) {
      std::shared_ptr<cdk::basic_type> vtype = tuple_type->component(i);
      std::shared_ptr<cdk::basic_type> ftype = func_type->component(i);
      offset -= ftype->size();

      if (vtype->name() == cdk::TYPE_INT && ftype->name() == cdk::TYPE_DOUBLE) {
        _pf.I2D();
      }

      _pf.LOCV(8);
      _pf.INT(offset);
      _pf.ADD();

      if (ftype->name() == cdk::TYPE_INT || ftype->name() == cdk::TYPE_STRING || ftype->name() == cdk::TYPE_POINTER) {
        _pf.STINT();
      } else if (ftype->name() == cdk::TYPE_DOUBLE) {
        _pf.STDOUBLE();
      } else {
        throw std::string("invalid struct element type");
      }
    }
  } else {
    throw std::string("invalid type to return");
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
    // External symbol...
    _symbols_to_declare.insert(symbol->name());
  } else if (_inside_function) {
    // if we are dealing with local variables, then no action is needed
    // unless an initializer exists because we already allocate the
    // memory space when defining a function.
    if (!expression) {
      return;
    }

    _is_struct_lval = false; // Hack!
    expression->accept(this, lvl);

    if (symbol->is_typed(cdk::TYPE_INT) || symbol->is_typed(cdk::TYPE_STRING) || symbol->is_typed(cdk::TYPE_POINTER)) {
      _pf.LOCAL(symbol->offset());
      _pf.STINT();
    } else if (symbol->is_typed(cdk::TYPE_DOUBLE)) {
      if (expression->is_typed(cdk::TYPE_INT)) {
        _pf.I2D();
      }
      _pf.LOCAL(symbol->offset());
      _pf.STDOUBLE();
    } else if (symbol->is_typed(cdk::TYPE_STRUCT)) {
      // Let's just say structs are... interesting!
      std::shared_ptr<cdk::structured_type> tuple_type = cdk::structured_type_cast(symbol->type());
      int offset = symbol->offset();

      for (size_t i = 0; i < tuple_type->length(); i++) {
        std::shared_ptr<cdk::basic_type> type = tuple_type->component(i);

        if (_is_struct_lval) {
          _pf.DUP32();
          _pf.INT(offset - symbol->offset());
          _pf.ADD();
        }

        if (type->name() == cdk::TYPE_INT || type->name() == cdk::TYPE_STRING || type->name() == cdk::TYPE_POINTER) {
          if (_is_struct_lval) _pf.LDINT();
          _pf.LOCAL(offset);
          _pf.STINT();
        } else if (type->name() == cdk::TYPE_DOUBLE) {
          if (_is_struct_lval) _pf.LDDOUBLE();
          _pf.LOCAL(offset);
          _pf.STDOUBLE();
        } else {
          throw std::string("invalid struct element type");
        }
        offset += type->size();
      }
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

    _is_true_order = true; // Hack!

    if (symbol->type()->name() == cdk::TYPE_DOUBLE && expression->is_typed(cdk::TYPE_INT)) {
      cdk::integer_node* int_node = dynamic_cast<cdk::integer_node*>(expression);
      cdk::double_node double_int(int_node->lineno(), int_node->value());
      double_int.accept(this, lvl);
    } else {
      expression->accept(this, lvl);
    }

    _is_true_order = false;
  }
}

void og::postfix_writer::do_var_decl_node(og::var_decl_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  _lvalue_type = node->type();

  if (node->identifiers().size() == 1) {
    // Single variable that may be a true tuple.
    std::string &id = *node->identifiers().at(0);
    std::shared_ptr<og::symbol> symbol = _symtab.find(id);
    do_var_decl_node_helper(symbol, node->expression(), lvl);
    return;
  }

  // Collection of variables! There MUST be an initializer (expression).

  if (_inside_function) {
    node->expression()->accept(this, lvl);

    for (size_t i = 0; i < node->identifiers().size(); i++) {
      std::string &id = *node->identifiers().at(i);
      std::shared_ptr<og::symbol> symbol = _symtab.find(id);

      int typesize = symbol->type()->size();
      _offset -= typesize;
      symbol->offset(_offset);

      if (symbol->is_typed(cdk::TYPE_INT) || symbol->is_typed(cdk::TYPE_STRING) || symbol->is_typed(cdk::TYPE_POINTER)) {
        _pf.LOCAL(symbol->offset());
        _pf.STINT();
      } else if (symbol->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.LOCAL(symbol->offset());
        _pf.STDOUBLE();
      } else {
        throw std::string("invalid initializer");
      }

    }
  } else if  (_in_function_args) {
    // Do nothing!
  } else {
    // This is a global declaration of a tuple. Hence, the initializers
    // must be literals.
    og::tuple_node* tuple = (og::tuple_node*)(node->expression());
    for (size_t i = 0; i < node->identifiers().size(); i++) {
      std::string &id = *node->identifiers().at(i);
      cdk::expression_node* exp = (cdk::expression_node*)(tuple->nodes()->node(i));
      std::shared_ptr<og::symbol> symbol = _symtab.find(id);
      do_var_decl_node_helper(symbol, exp, lvl);
    }
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
  std::shared_ptr<cdk::reference_type> type = cdk::reference_type_cast(node->type());
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
  _symbols_to_declare.erase(_function->name());
  reset_new_symbol();

  _return_count = 0;
  _offset = 8; // prepare for arguments (4: remember to account for return address)
  _symtab.push(); // scope of args

  if (!_function->type() || _function->is_typed(cdk::TYPE_STRUCT)) {
    // Update the offset when we're returning a struct!
    _offset += 4;
  }

  if (node->args()) {
    _in_function_args = true;

    for (size_t i = 0; i < node->args()->size(); i++) {
      cdk::basic_node *arg = node->args()->node(i);
      arg->accept(this, 0);
    }

    _in_function_args = false;
  }

  _pf.TEXT();
  _pf.ALIGN();
  if (node->is_public()) {
    _pf.GLOBAL(_function->name(), _pf.FUNC());
  }
  _pf.LABEL(_function->name());

  // compute stack size to be reserved for local variables
  frame_size_calculator lsc(_compiler, _symtab, _function, _in_function_args, _lvalue_type);
  node->accept(&lsc, lvl);
  _pf.ENTER(lsc.localsize()); // total stack size reserved for local variables

  _inside_function = true;
  _offset = 0;
  node->block()->accept(this, lvl + 4);
  _inside_function = false;
  _symtab.pop();

  if (_function->is_typed(cdk::TYPE_VOID)) {
    // only add this to procedures. functions already return.
    _pf.LEAVE();
    _pf.RET();
  }

  if (!_function->is_typed(cdk::TYPE_VOID) && _return_count == 0) {
    // probably this should've been in the type checker, but it was easier this way
    throw std::string("1 or more returns are required");
  }

  if (node->identifier() == "_main") {
    // declare external functions
    for (std::string s : _symbols_to_declare) {
      _pf.EXTERN(s);
    }
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

  unsigned int id = node->index()->value();
  node->expression()->accept(this, lvl);
  std::shared_ptr<cdk::structured_type> struct_type = cdk::structured_type_cast(node->expression()->type());

  size_t offset = 0;
  for (size_t i = 1; i < id; i++) {
    offset += struct_type->component(i - 1)->size();
  }

  _pf.INT(offset);
  _pf.ADD();
}

void og::postfix_writer::do_func_decl_node(og::func_decl_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  std::shared_ptr<og::symbol> function = new_symbol();

  _symbols_to_declare.insert(function->name());
  reset_new_symbol();
}

void og::postfix_writer::do_func_call_node(og::func_call_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  std::shared_ptr<og::symbol> symbol = _symtab.find(node->identifier());
  size_t args_size = 0;

  if (node->expressions()) {
    for (size_t i = node->expressions()->size(); i > 0; i--) {
      cdk::expression_node *arg = dynamic_cast<cdk::expression_node*>(node->expressions()->node(i - 1));
      std::shared_ptr<og::symbol> param = symbol->params()->at(i - 1);

      arg->accept(this, lvl + 2);
      args_size += param->type()->size();

      if (param->is_typed(cdk::TYPE_DOUBLE) && arg->is_typed(cdk::TYPE_INT)) {
        _pf.I2D();
      }
    }
  }

  int ret_lbl = -1;

  // If we're returning a struct a type, we need to allocate space
  // for it beforehand and pass the memory address as the first argument
  // of the function.
  if (node->is_typed(cdk::TYPE_STRUCT)) {
    ret_lbl = ++_lbl;
    _pf.BSS();
    _pf.ALIGN();
    _pf.LABEL(mklbl(ret_lbl));
    _pf.SALLOC(symbol->type()->size());
    _pf.TEXT();
    _pf.ADDR(mklbl(ret_lbl));
  }

  _pf.CALL(node->identifier());
  if (args_size != 0) {
    _pf.TRASH(args_size);
  }

  if (symbol->is_typed(cdk::TYPE_INT) || symbol->is_typed(cdk::TYPE_POINTER) || symbol->is_typed(cdk::TYPE_STRING)) {
    _pf.LDFVAL32();
  } else if (symbol->is_typed(cdk::TYPE_DOUBLE)) {
    _pf.LDFVAL64();
  } else if (symbol->is_typed(cdk::TYPE_STRUCT)) {
    std::shared_ptr<cdk::structured_type> tuple_type = cdk::structured_type_cast(node->type());
    int offset = tuple_type->size();

    for (int i = tuple_type->length() - 1; i >= 0; i--) {
      std::shared_ptr<cdk::basic_type> type = tuple_type->component(i);
      offset -= type->size();

      _pf.ADDR(mklbl(ret_lbl));
      _pf.INT(offset);
      _pf.ADD();

      if (type->name() == cdk::TYPE_INT || type->name() == cdk::TYPE_STRING || type->name() == cdk::TYPE_POINTER) {
        _pf.LDINT();
      } else if (type->name() == cdk::TYPE_DOUBLE) {
        _pf.LDDOUBLE();
      } else {
        throw std::string("invalid struct element type");
      }
    }
    // TODO: how to free the memory in BSS segment?
  }
}

void og::postfix_writer::do_tuple_node(og::tuple_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  if (_is_true_order) {
    node->nodes()->accept(this, lvl + 2);
  } else {
    for (int i = node->nodes()->size() - 1; i >= 0 ; i--) {
      node->nodes()->node(i)->accept(this, lvl + 2);
    }
  }
}
