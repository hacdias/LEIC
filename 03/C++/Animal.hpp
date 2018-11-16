#ifndef ANIMAL_H
#define ANIMAL_H

#include <string>

class Animal {
  int _age;
  std::string _name;

public:
  Animal (int);
  Animal (std::string, int);
  void sleep ();
  std::string getName () const;
  void setName (std::string);
  int getAge () const;
  void setAge (int);
  bool operator==(const Animal&);
  friend std::ostream & operator<<(std::ostream&, const Animal&);
};

#endif
