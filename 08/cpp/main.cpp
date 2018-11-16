#include <string>
#include <iostream>
#include <sstream>

class Cat {
  double _age = 0;
  std::string _name = "anónimo";
public:
  Cat () = default;
  Cat (double age): _age(age) {}
  Cat (double age, std::string name): _age(age), _name(name) {}

  friend std::ostream &operator<<(std::ostream &o, const Cat &cat) {
    o << cat._name << " " << cat._age;
    return o;
  }

  friend std::istream& operator>>(std::istream& stream, Cat& cat) {
    stream >> cat._name >> cat._age;
    return stream; 
  }
};

int main () {
  Cat a(2, "Tareco"), b(0.5, "Pantufa");
  std::string st;
  std::stringstream ss(st);
  ss << a << b;
  Cat c, d;
  ss >> c >> d;
  std::cout << c << std::endl << d << std::endl;
  return 0;
}
