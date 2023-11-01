#include "Animal.hpp"
#include "Cat.hpp"
#include "Dog.hpp"
#include <iostream>

int main () {
  Animal a1 = Animal(20);
  Animal a2 = Animal("Toby", 3);

  Cat c1 = Cat("Estrôncio", 3, 7);
  Cat c2 = Cat("Tareco", 2, 5);

  Dog d1 = Dog("Bolinha", 7, 42.5);
  Dog d2 = Dog("Gstar", 3, 23);

  std::cout << a1 << std::endl << a2 << std::endl;
  std::cout << c1 << std::endl << c2 << std::endl;
  std::cout << d1 << std::endl << d2 << std::endl;

  a1.sleep();
  a2.sleep();

  c1.sleep();
  c1.climb();
  d2.bark();
  d2.sleep();

  std::cout << "a1 == a1 ? " << (a1 == a1 ? "true" :  "false") << std::endl;
  std::cout << "a1 == a2 ? " << (a1 == a2 ? "true" :  "false") << std::endl;

  std::cout << "c1 == c1 ? " << (c1 == c1 ? "true" :  "false") << std::endl;
  std::cout << "c1 == c2 ? " << (c1 == c2 ? "true" :  "false") << std::endl;

  return 0;
}
