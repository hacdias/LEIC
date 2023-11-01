
#include <vector>

class FlexyIrs {
public:
  virtual double taxPerson  (class Person*)  = 0;
  virtual double taxCompany (class Company*) = 0;
  virtual double taxRegion  (class Region*)  = 0;
};

class Taxpayer {
public:
  virtual double accept (FlexyIrs *irs) = 0;
};

class Person: public Taxpayer {
  int _salary;
public:
  double accept (FlexyIrs *irs) { return irs->taxPerson(this); }
};

class Company: public Taxpayer {
  std::vector<Person*> _persons;
public:
  int employeesCount () { return _persons.size(); }
  Person* getEmployee (int i) { return _persons.at(i); }
  double accept (FlexyIrs *irs) { return irs->taxCompany(this); }
};

class Region: public Taxpayer {
  std::vector<Company*> _companies;
public:
  int companiesCount () {return _companies.size();}
  Company* getCompany (int i) { return _companies.at(i); }
  double accept (FlexyIrs *irs) { return irs->taxRegion(this); }
};

class FastTrack: public FlexyIrs {
  double taxPerson  (Person* p)  {
    return 1.0;
  }
  double taxCompany (Company* c) {
    int employees = c->employeesCount();
    double tax = 0;
    for (int i = 0; i < employees; i++)
      tax += c->getEmployee(i)->accept(this);
    return employees < 4 ? tax * 0.6 : tax;
  }
  double taxRegion  (Region* r)  {
    int companies = r->companiesCount();
    double multiplier = 1, tax = 0;
    for (int i = 0; i < companies; i++) {
      Company* c = r->getCompany(i);
      tax += c->accept(this);
      if (c->employeesCount() < 10) multiplier =  0.8;
    }
    return tax * multiplier;
  }
};
