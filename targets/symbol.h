#ifndef __OG_TARGETS_SYMBOL_H__
#define __OG_TARGETS_SYMBOL_H__

#include <string>
#include <memory>
#include <cdk/types/basic_type.h>

namespace og {

  class symbol {
    std::shared_ptr<cdk::basic_type> _type;
    std::string _name;
    long _value; // hack!
    // TODO: remove value?

  public:
    symbol(std::shared_ptr<cdk::basic_type> type, const std::string &name) :
        _type(type), _name(name), _value(0) {
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
  };

  // this function simplifies symbol creation in the type_checker visitor (see below)
  inline auto make_symbol(std::shared_ptr<cdk::basic_type> type, const std::string &name) {
    return std::make_shared<symbol>(type, name);
  }

} // og

#endif
