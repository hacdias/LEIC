#ifndef _DOG_H_
#define _DOG_H_

#include "Animal.hpp"
#include <string>

class Dog: public Animal {
  double _weight;

public:
  Dog (std::string, int, double);
  void bark ();
  double getWeight () const;
  void setWeight (double);
  bool operator==(const Dog&);
  friend std::ostream & operator<<(std::ostream&, const Dog&);
};

#endif
