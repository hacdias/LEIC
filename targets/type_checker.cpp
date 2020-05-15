#include <string>
#include "targets/type_checker.h"
#include "ast/all.h"  // automatically generated
#include <cdk/types/primitive_type.h>

#define ASSERT_UNSPEC { if (node->type() != nullptr && !node->is_typed(cdk::TYPE_UNSPEC)) return; }

//---------------------------------------------------------------------------

void og::type_checker::do_sequence_node(cdk::sequence_node *const node, int lvl) {
  for (size_t i = 0; i < node->size(); i++)
    node->node(i)->accept(this, lvl);
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
  node->type(cdk::make_primitive_type(8, cdk::TYPE_DOUBLE));
}

//---------------------------------------------------------------------------

void og::type_checker::processUnaryExpression(cdk::unary_operation_node *const node, int lvl, bool acceptInt, bool acceptDouble) {
  ASSERT_UNSPEC;
  node->argument()->accept(this, lvl + 2);

  if (acceptInt && node->argument()->is_typed(cdk::TYPE_INT))
    node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

  else if (acceptDouble && node->argument()->is_typed(cdk::TYPE_DOUBLE))
    node->type(cdk::make_primitive_type(8, cdk::TYPE_DOUBLE));

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

void og::type_checker::processBinaryExpression(cdk::binary_operation_node *const node, int lvl, bool acceptInt, bool acceptDouble, bool acceptPointer, bool isAdd) {
  ASSERT_UNSPEC;
  node->left()->accept(this, lvl + 2);
  node->right()->accept(this, lvl + 2);

  if (acceptInt && node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_INT))
    node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

  else if (acceptDouble && node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_DOUBLE))
    node->type(cdk::make_primitive_type(8, cdk::TYPE_DOUBLE));

  else if (acceptDouble && node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT))
    node->type(cdk::make_primitive_type(8, cdk::TYPE_DOUBLE));

  else if (acceptDouble && node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE))
    node->type(cdk::make_primitive_type(8, cdk::TYPE_DOUBLE));

  else if (acceptPointer && !isAdd && node->left()->is_typed(cdk::TYPE_POINTER) && node->right()->is_typed(cdk::TYPE_POINTER))
    node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

  else if (acceptPointer && isAdd && node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_POINTER))
    node->type(node->right()->type());

  else if (acceptPointer && isAdd && node->left()->is_typed(cdk::TYPE_POINTER) && node->right()->is_typed(cdk::TYPE_INT))
    node->type(node->left()->type());

  else throw std::string("wrong type for binary expression");
}

void og::type_checker::do_add_node(cdk::add_node *const node, int lvl) {
  // int, real, pointer
  processBinaryExpression(node, lvl, true, true, true, true);
}

void og::type_checker::do_sub_node(cdk::sub_node *const node, int lvl) {
  // int, real, pointer
  processBinaryExpression(node, lvl, true, true, true, false);
}

void og::type_checker::do_mul_node(cdk::mul_node *const node, int lvl) {
  // int, real
  processBinaryExpression(node, lvl, true, true, false, false);
}

void og::type_checker::do_div_node(cdk::div_node *const node, int lvl) {
  // int, real
  processBinaryExpression(node, lvl, true, true, false, false);
}

void og::type_checker::do_mod_node(cdk::mod_node *const node, int lvl) {
  // int, real
  processBinaryExpression(node, lvl, true, true, false, false);
}

void og::type_checker::do_lt_node(cdk::lt_node *const node, int lvl) {
  // int, real
  processBinaryExpression(node, lvl, true, true, false, false);
}

void og::type_checker::do_le_node(cdk::le_node *const node, int lvl) {
  // int, real
  processBinaryExpression(node, lvl, true, true, false, false);
}

void og::type_checker::do_ge_node(cdk::ge_node *const node, int lvl) {
  // int, real
  processBinaryExpression(node, lvl, true, true, false, false);
}

void og::type_checker::do_gt_node(cdk::gt_node *const node, int lvl) {
  // int, real
  processBinaryExpression(node, lvl, true, true, false, false);
}

void og::type_checker::do_ne_node(cdk::ne_node *const node, int lvl) {
  // int, real, pointer
  processBinaryExpression(node, lvl, true, true, true, false);
}

void og::type_checker::do_eq_node(cdk::eq_node *const node, int lvl) {
  // int, real, pointer
  processBinaryExpression(node, lvl, true, true, true, false);
}

void og::type_checker::do_and_node(cdk::and_node *const node, int lvl) {
  // int
  processBinaryExpression(node, lvl, true, false, false, false);
}

void og::type_checker::do_or_node(cdk::or_node *const node, int lvl) {
  // int
  processBinaryExpression(node, lvl, true, false, false, false);
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
    throw std::string("undeclared variable on the right side '" + id + "'");
  }
}

void og::type_checker::throw_cannot_assign(std::shared_ptr<cdk::basic_type> typel, std::shared_ptr<cdk::basic_type> typer) {
  throw std::string("cannot assign " + cdk::to_string(typel) + " to type " + cdk::to_string(typer));
}

void og::type_checker::do_assignment_node(cdk::assignment_node *const node, int lvl) {
  ASSERT_UNSPEC;

  // For the sake of readability...
  cdk::lvalue_node* lvalue = node->lvalue();
  cdk::expression_node* rvalue = node->rvalue();

  lvalue->accept(this, lvl);
  rvalue->accept(this, lvl + 2);

  // Note to self: wouldn't it just be easier to check the cases we don't want to
  // support? Or would that open a door for unthought cases?

  if (lvalue->is_typed(cdk::TYPE_INT)) {
    if (rvalue->is_typed(cdk::TYPE_INT)) {
      node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));
    } else if (rvalue->is_typed(cdk::TYPE_UNSPEC)) {
      node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));
      rvalue->type(cdk::make_primitive_type(4, cdk::TYPE_INT));
    } else {
      throw_cannot_assign(rvalue->type(), lvalue->type());
    }
  } else if (lvalue->is_typed(cdk::TYPE_DOUBLE)) {
    if (rvalue->is_typed(cdk::TYPE_DOUBLE) || rvalue->is_typed(cdk::TYPE_INT)) {
      node->type(cdk::make_primitive_type(8, cdk::TYPE_DOUBLE));
    } else if (rvalue->is_typed(cdk::TYPE_UNSPEC)) {
      node->type(cdk::make_primitive_type(8, cdk::TYPE_DOUBLE));
      rvalue->type(cdk::make_primitive_type(8, cdk::TYPE_DOUBLE));
    } else {
      throw_cannot_assign(rvalue->type(), lvalue->type());
    }
  } else if (lvalue->is_typed(cdk::TYPE_POINTER)) {
    if (rvalue->is_typed(cdk::TYPE_POINTER)) {
      std::shared_ptr<cdk::reference_type> ltype = std::static_pointer_cast<cdk::reference_type>(lvalue->type());
      std::shared_ptr<cdk::reference_type> rtype = std::static_pointer_cast<cdk::reference_type>(rvalue->type());

      // Iteratively check pointers depth!
      while (true) {
        std::shared_ptr<cdk::basic_type> lref = ltype->referenced();
        std::shared_ptr<cdk::basic_type> rref = rtype->referenced();

        if (lref->name() == cdk::TYPE_UNSPEC && rref->name() != cdk::TYPE_UNSPEC) {
          lvalue->type(rvalue->type());
          node->type(rvalue->type());
          break;
        } else if (rref->name() == cdk::TYPE_UNSPEC && lref->name() != cdk::TYPE_UNSPEC) {
          rvalue->type(lvalue->type());
          node->type(lvalue->type());
          break;
        } else if (lref->name() == cdk::TYPE_POINTER && rref->name() == cdk::TYPE_POINTER) {
          ltype = std::static_pointer_cast<cdk::reference_type>(lref);
          rtype = std::static_pointer_cast<cdk::reference_type>(rref);
        } else if (lref->name() == rref->name()) {
          node->type(lvalue->type());
          break;
        } else {
          throw_cannot_assign(rvalue->type(), lvalue->type());
        }
      }
    } else if (rvalue->is_typed(cdk::TYPE_UNSPEC)) {
      node->type(cdk::make_primitive_type(4, cdk::TYPE_ERROR));
      rvalue->type(cdk::make_primitive_type(4, cdk::TYPE_ERROR));
    } else {
      throw_cannot_assign(rvalue->type(), lvalue->type());
    }
  } else if (lvalue->is_typed(cdk::TYPE_STRING)) {
    if (rvalue->is_typed(cdk::TYPE_STRING)) {
      node->type(cdk::make_primitive_type(4, cdk::TYPE_STRING));
    } else if (rvalue->is_typed(cdk::TYPE_UNSPEC)) {
      node->type(cdk::make_primitive_type(4, cdk::TYPE_STRING));
      rvalue->type(cdk::make_primitive_type(4, cdk::TYPE_STRING));
    } else {
      throw_cannot_assign(rvalue->type(), lvalue->type());
    }
  } else if (lvalue->is_typed(cdk::TYPE_STRUCT)) {
    if (!rvalue->is_typed(cdk::TYPE_STRUCT)) {
      throw_cannot_assign(rvalue->type(), lvalue->type());
    }

    std::shared_ptr<cdk::structured_type> ltype = std::static_pointer_cast<cdk::structured_type>(lvalue->type());
    std::shared_ptr<cdk::structured_type> rtype = std::static_pointer_cast<cdk::structured_type>(rvalue->type());

    if (ltype->length() != rtype->length()) {
      throw_cannot_assign(rvalue->type(), lvalue->type());
    }

    for (size_t i = 0; i < ltype->length(); i++) {
      std::shared_ptr<cdk::basic_type> lt = ltype->component(i);
      std::shared_ptr<cdk::basic_type> rt = rtype->component(i);

      if (lt != rt) {
        throw_cannot_assign(rvalue->type(), lvalue->type());
      }
    }

    // Profit?
    node->type(lvalue->type());
  } else {
    throw std::string("unknown types in assignment");
  }
}

//---------------------------------------------------------------------------

void og::type_checker::do_evaluation_node(og::evaluation_node *const node, int lvl) {
  node->argument()->accept(this, lvl + 2);
}

void og::type_checker::do_write_node(og::write_node *const node, int lvl) {
  node->argument()->accept(this, lvl + 2);

  if (node->argument()->is_typed(cdk::TYPE_POINTER)) {
    throw std::string("cannot print pointer");
  }

  if (node->argument()->is_typed(cdk::TYPE_STRUCT)) {
    std::shared_ptr<cdk::structured_type> struct_type = std::static_pointer_cast<cdk::structured_type>(node->argument()->type());

    for (auto type : struct_type->components()) {
      if (type->name() == cdk::TYPE_POINTER) {
        throw std::string("cannot print pointer");
      }
    }
  }
}

//---------------------------------------------------------------------------

void og::type_checker::do_input_node(og::input_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->type(cdk::make_primitive_type(0, cdk::TYPE_UNSPEC));
}

//---------------------------------------------------------------------------

void og::type_checker::do_for_node(og::for_node *const node, int lvl) {
  if (node->init()) node->init()->accept(this, lvl + 4);
  if (node->condition()) node->condition()->accept(this, lvl + 4);
  if (node->end()) node->end()->accept(this, lvl + 4);
  node->block()->accept(this, lvl + 4);
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
  if (!_function) throw std::string("return outside of function");

  if (node->value()) {
    node->value()->accept(this, lvl);

    if (_function->type() == nullptr) {
      _function->type(node->value()->type());
    } else if (_function->is_typed(cdk::TYPE_DOUBLE) && node->value()->is_typed(cdk::TYPE_INT)) {
      _function->type(cdk::make_primitive_type(8, cdk::TYPE_DOUBLE));
    } else if (_function->type() != node->value()->type()) {
      throw std::string("function has wrong return type");
    }
  } else {
    if (_function->type() == nullptr) {
      _function->type(cdk::make_primitive_type(0, cdk::TYPE_VOID));
    } else if (!_function->is_typed(cdk::TYPE_VOID)) {
      throw std::string("function must return void");
    }
  }
}

//---------------------------------------------------------------------------

void og::type_checker::do_break_node(og::break_node *const node, int lvl) {
  // EMPTY
}

//---------------------------------------------------------------------------

void og::type_checker::do_continue_node(og::continue_node *const node, int lvl) {
  // EMPTY
}

//---------------------------------------------------------------------------

void og::type_checker::do_var_decl_node(og::var_decl_node *const node, int lvl) {
  if (node->declared()) {
    return;
  }

  if (node->expression()) {
    node->expression()->accept(this, lvl + 2);
  }

  if (node->identifiers().size() == 1) {
    std::string &id = *node->identifiers().front();

    if (node->type()) {
      // NOTE: if it's a ptr<auto> and the rvalue has a defined type, use it.
      // Otherwise, create an unspecified pointer.
      if (node->is_typed(cdk::TYPE_POINTER)) {
        std::shared_ptr<cdk::reference_type> lptr = std::static_pointer_cast<cdk::reference_type>(node->type());

        // NOTE: fix those unspecs :)
        if (node->expression()) {
          std::shared_ptr<cdk::reference_type> rptr = std::static_pointer_cast<cdk::reference_type>(node->expression()->type());
          if (rptr->referenced()->name() == cdk::TYPE_UNSPEC) {
            node->expression()->type(node->type());
          }
        }

        if (!lptr->referenced() && node->expression() && node->expression()->is_typed(cdk::TYPE_POINTER)) {
          node->type(node->expression()->type());
        } else if (!lptr->referenced()) {
          node->type(cdk::make_reference_type(4, cdk::make_primitive_type(0, cdk::TYPE_UNSPEC)));
        }
      } else if (node->expression() && node->is_typed(cdk::TYPE_DOUBLE) && node->expression()->is_typed(cdk::TYPE_INT)) {
        // Do nothing...
      } else if (node->expression() && node->type() != node->expression()->type()) {
        throw std::string("wrong type for initializer");
      }
    } else {
      if (node->expression()) {
        node->type(node->expression()->type());
      } else {
        throw std::string("auto variable must be initialized: " + id);
      }
    }

    auto symbol = og::make_symbol(node->type(), id, node->is_public(), node->is_require(), false);

    if (!_symtab.insert(id, symbol))
      throw std::string(id + " redeclared");

    node->declared(true);
    // _parent->set_new_symbol(symbol);

    if (_in_function_args) {
      _function->params()->push_back(symbol);
    }

    return;
  }

  if (!node->expression()->is_typed(cdk::TYPE_STRUCT)) {
    throw std::string("wrong number of identifiers");
  }

  og::tuple_node* tuple = (og::tuple_node*)(node->expression());

  if (tuple->nodes()->size() != node->identifiers().size()) {
    throw std::string("mismatching nodes and identifiers");
  }

  node->type(node->expression()->type());

  // TODO: is this working?
  for (size_t i = 0; i < node->identifiers().size(); i++) {
    std::string &id = *node->identifiers().at(i);
    cdk::expression_node* exp = (cdk::expression_node*)(tuple->nodes()->node(i));

    auto symbol = og::make_symbol(exp->type(), id, node->is_public(), node->is_require(), false);

    if (!_symtab.insert(id, symbol))
      throw std::string(id + " redeclared");

    if (_in_function_args) {
      _function->params()->push_back(symbol);
    }

    // _parent->set_new_symbol(symbol);
  }

  node->declared(true);
}

//---------------------------------------------------------------------------

void og::type_checker::do_nullptr_node(og::nullptr_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->type(cdk::make_reference_type(4, cdk::make_primitive_type(0, cdk::TYPE_UNSPEC)));
}

//---------------------------------------------------------------------------

void og::type_checker::do_mem_alloc_node(og::mem_alloc_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->argument()->accept(this, lvl + 2);

  if (!node->argument()->is_typed(cdk::TYPE_INT))
    throw std::string("wrong type in argument of unary expression");

  node->type(cdk::make_reference_type(4, cdk::make_primitive_type(0, cdk::TYPE_UNSPEC)));
}

//---------------------------------------------------------------------------

void og::type_checker::do_mem_addr_node(og::mem_addr_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->lvalue()->accept(this, lvl + 2);
  node->type(cdk::make_reference_type(4, node->lvalue()->type()));
}

//---------------------------------------------------------------------------

void og::type_checker::do_block_node(og::block_node *const node, int lvl) {
  if (node->declarations()) node->declarations()->accept(this, lvl+2);
  if (node->instructions()) node->instructions()->accept(this, lvl+2);
}

//---------------------------------------------------------------------------

void og::type_checker::do_sizeof_node(og::sizeof_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->value()->accept(this, lvl+2);
  node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));
}

void og::type_checker::do_ptr_index_node(og::ptr_index_node *const node, int lvl) {
  ASSERT_UNSPEC;

  node->pointer()->accept(this, lvl+2);
  node->index()->accept(this, lvl+2);

  if (!node->index()->is_typed(cdk::TYPE_INT)) {
    throw std::string("wrong index type, must be int");
  }

  if (!node->pointer()->is_typed(cdk::TYPE_POINTER)) {
    throw std::string("cannot index non-pointer type");
  }

  node->type(((std::static_pointer_cast<cdk::reference_type>)(node->pointer()->type()))->referenced());
}

void og::type_checker::do_tuple_index_node(og::tuple_index_node *const node, int lvl) {
  ASSERT_UNSPEC;

  node->expression()->accept(this, lvl+2);
  node->index()->accept(this, lvl+2);

  if (!node->expression()->is_typed(cdk::TYPE_STRUCT)) {
    throw std::string("cannot index non-struct type");
  }

  std::shared_ptr<cdk::structured_type> struct_type = std::static_pointer_cast<cdk::structured_type>(node->expression()->type());
  unsigned int idx = node->index()->value();

  if (idx > struct_type->length()) {
    throw std::string("invalid tuple index");
  }

  node->type(struct_type->component(idx - 1));
}

//---------------------------------------------------------------------------

void og::type_checker::do_func_decl_node(og::func_decl_node *const node, int lvl) {
  //std::cout << "func_decl" << std::endl;

  if (node->identifier() == "og")
    node->identifier("_main");
  else if (node->identifier() == "_main")
    node->identifier("._main");

  _function = make_symbol(node->type(), node->identifier(), node->is_public(), node->is_required(), true);

  auto previous = _symtab.find(node->identifier());
  if (previous != nullptr) {
    if (false /* TODO: check if arguments are correct */) {
      throw std::string(node->identifier() + "redeclared");
    }
  } else {
    _symtab.insert(node->identifier(), _function);
    // _parent->set_new_symbol(_function);
  }

  _in_function_args = true;

  if (node->args()) {
    _symtab.push();
    node->args()->accept(this, lvl + 2);
    _symtab.pop();
  }

  _in_function_args = false;
  _function = nullptr;
}

void og::type_checker::do_func_def_node(og::func_def_node *const node, int lvl) {
  if (node->identifier() == "og")
    node->identifier("_main");
  else if (node->identifier() == "_main")
    node->identifier("._main");

  auto symbol = make_symbol(node->type(), node->identifier(), node->is_public(), node->is_required(), true);

  auto existent = _symtab.find(node->identifier());
  if (existent != nullptr) {
    if(true /* TODO: compare if declarations are the same.*/) {
      _symtab.replace(node->identifier(), symbol);
      // _parent->set_new_symbol(symbol);
    } else {
      throw std::string("declarations are not the same");
    }
  } else {
    _symtab.insert(node->identifier(), symbol);
    //  parent->set_new_symbol(symbol);
  }
}

void og::type_checker::do_func_call_node(og::func_call_node *const node, int lvl) {
  ASSERT_UNSPEC;
  //std::cout << "func_call" << std::endl;
  // TODO

  if (node->identifier() == "og")
    node->identifier("_main");
  else if (node->identifier() == "_main")
    node->identifier("._main");

  auto existent = _symtab.find(node->identifier());

  if (!existent) {
    throw std::string("undeclared function '" + node->identifier() + "'");
  }

  if (!existent->is_function()) {
    throw std::string(node->identifier() + "is not a function");
  }

  node->type(existent->type());
  if(node->expressions()->size() != 0)
    node->expressions()->accept(this, lvl + 4);
}

//---------------------------------------------------------------------------

void og::type_checker::do_tuple_node(og::tuple_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->nodes()->accept(this, lvl + 2);

  if (node->nodes()->size() == 1) {
    node->type(((cdk::expression_node*)node->nodes()->node(0))->type());
    return;
  }

  std::vector<std::shared_ptr<cdk::basic_type>> *types = new std::vector<std::shared_ptr<cdk::basic_type>>();

  for (size_t i = 0; i < node->nodes()->size(); i++)
    types->push_back(((cdk::expression_node*)(node->nodes()->node(i)))->type());

  // TODO: works?
  node->type(cdk::make_structured_type(*types));
}
