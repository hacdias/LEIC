#ifndef __OG_TARGETS_SYMBOL_H__
#define __OG_TARGETS_SYMBOL_H__

#include <string>
#include <memory>
#include <cdk/types/basic_type.h>

namespace og {

  class symbol {
    std::shared_ptr<cdk::basic_type> _type;
    std::string _name;
    bool _is_public;
    bool _is_required;
    bool _is_function;
    int _offset = 0;
    std::vector<std::shared_ptr<symbol>> _params; // a function parameters :)

  public:
    symbol(std::shared_ptr<cdk::basic_type> type, const std::string &name, bool is_public, bool is_required, bool is_function) :
        _type(type), _name(name), _value(0), _is_public(is_public), _is_required(is_required), _is_function(is_function) {
    }

    virtual ~symbol() {
      // EMPTY
    }

    std::shared_ptr<cdk::basic_type> type() const {
      return _type;
    }
    void type(std::shared_ptr<cdk::basic_type> type) {
      _type = type;
    }
    bool is_typed(cdk::typename_type name) const {
      return _type->name() == name;
    }
    const std::string &name() const {
      return _name;
    }
    long value() const {
      return _value;
    }
    long value(long v) {
      return _value = v;
    }
    int offset() const {
      return _offset;
    }
    void offset(int offset) {
      _offset = offset;
    }
    bool is_public() {
      return _is_public;
    }
    bool is_required() {
      return _is_required;
    }
    bool is_function() {
      return _is_function;
    }
    std::vector<std::shared_ptr<symbol>> *params() {
      return &_params;
    }
  };

  // this function simplifies symbol creation in the type_checker visitor (see below)
  inline auto make_symbol(std::shared_ptr<cdk::basic_type> type, const std::string &name, bool is_public, bool is_required, bool is_function) {
    return std::make_shared<symbol>(type, name, is_public, is_required, is_function);
  }

} // og

#endif
