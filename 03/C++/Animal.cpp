#include "Animal.hpp"
#include <iostream>

Animal::Animal (int a): _age(a), _name("") {};
Animal::Animal (std::string n, int a): _age(a), _name(n) {};

void Animal::sleep () {
  std::cout << "Animal " << (!_name.compare("") ? "UNKNOWN" : _name) << " is sleeping" << std::endl;
}

std::string Animal::getName () const {
  return _name;
}

void Animal::setName (std::string n) {
  _name = n;
}

int Animal::getAge () const {
  return _age;
}

void Animal::setAge (int a) {
  _age = a;
}

bool Animal::operator==(const Animal &animal) {
  return _name.compare(animal.getName()) == 0 &&
    _age == animal.getAge();
}

std::ostream &operator<<(std::ostream &o, const Animal &animal) {
  o << "Animal(" << animal.getName() << "), age: " << animal.getAge();
  return o;
}
