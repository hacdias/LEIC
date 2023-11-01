#include "Cat.hpp"
#include <iostream>

Cat::Cat (std::string n, int a, int l): Animal(n, a), _lives(l) {};

void Cat::climb () {
  std::cout << "Cat " << getName() << " is climbing" << std::endl;
}

int Cat::getLives () const {
  return _lives;
}

void Cat::setLives (int l) {
  _lives = l;
}

bool Cat::operator==(const Cat &cat) {
  return (Animal)*this == (Animal)cat &&
    getLives() == cat.getLives();
}

std::ostream &operator<<(std::ostream &o, const Cat &cat) {
  o << (Animal)cat << ", (Cat), lives: " << cat.getLives();
  return o;
}
