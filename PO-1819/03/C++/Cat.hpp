#ifndef _CAT_H_
#define _CAT_H_

#include "Animal.hpp"
#include <string>

class Cat: public Animal {
  int _lives;

public:
  Cat (std::string, int, int);
  void climb ();
  int getLives () const;
  void setLives (int);
  bool operator==(const Cat&);
  friend std::ostream & operator<<(std::ostream&, const Cat&);
};

#endif
