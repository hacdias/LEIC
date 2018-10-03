#include "Dog.hpp"
#include <iostream>

Dog::Dog (std::string n, int a, double w): Animal(n, a), _weight(w) {};

void Dog::bark () {
  std::cout << "Dog " << getName() << " is barking" << std::endl;
}

double Dog::getWeight () const {
  return _weight;
}

void Dog::setWeight (double w) {
  _weight = w;
}

bool Dog::operator==(const Dog &dog) {
  return (Animal)*this == (Animal)dog &&
    getWeight() == dog.getWeight();
}

std::ostream &operator<<(std::ostream &o, const Dog &dog) {
  o << (Animal)dog << ", (Dog), weight: " << dog.getWeight();
  return o;
}

