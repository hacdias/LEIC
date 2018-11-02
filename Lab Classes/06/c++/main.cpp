#include <vector>
#include <iostream>
#include <cstdlib>

class Beneficiary {
  int _helpLevel;
public:
  Beneficiary (int level): _helpLevel(level) {}
  virtual         ~Beneficiary () {}
  virtual void    help (double amount) = 0;
  virtual double  help () = 0;
  int             helpLevel() const { return _helpLevel; }
};

class Individual: public Beneficiary {
  double _helpReceived = 0;
public:
  Individual (int level): Beneficiary(level) {}
  void    help (double amount)  override { _helpReceived += amount; }
  double  help ()               override { return _helpReceived; }
};

class Collective: public Beneficiary {
protected:
  std::vector<Beneficiary*> _ben;
public:
  Collective (int level): Beneficiary(level) {}
  void help (double amount) override {
    std::vector<Beneficiary*>::iterator it;
    amount = amount / _ben.size();
    for (it = _ben.begin(); it != _ben.end(); it++) (*it)->help(amount);
  }
  double help () override {
    double value = 0;
    std::vector<Beneficiary*>::iterator it;
    for (it = _ben.begin(); it != _ben.end(); it++) value += (*it)->help();
    return value;
  }
};

class Village: public Collective {
public:
  Village (int level): Collective(level) {}
  void addBeneficiary (Individual *b) { _ben.push_back(b); }
};

class Region: public Collective {
public:
  Region (int level): Collective(level) {}
  void addBeneficiary (Village *b) { _ben.push_back(b); }
  void addBeneficiary (Individual *b) { _ben.push_back(b); }
};

class Agency {
  double _funds = 0;
  std::vector<Beneficiary*> _ben;
public:
  Agency () = default;
  Agency (double funds): _funds(funds) {}
  void fund (double funds) { _funds += funds; }
  void addBeneficiary (Beneficiary *b) { _ben.push_back(b); }
  void help (double baseAmount = 100) {
    std::vector<Beneficiary*>::iterator it;

    for (it = _ben.begin(); it != _ben.end(); it++) {
      int level = (*it)->helpLevel();
      double amount = baseAmount + level * 10;

      if (_funds < amount) {
        std::cout << "INSUFFICIENT FUNDS" << std::endl;
        return;
      }

      (*it)->help(amount);
      _funds -= amount;
    }
  }
};

int main () {
  const int REGIONS = 10;
  const int VILLAGES = 10;
  const int INDIVIDUALS = 100;

  Agency a(25000);

  Region      *regions[REGIONS];
  Village     *villages[VILLAGES];
  Individual  *individuals[INDIVIDUALS];

  for (int i = 0; i < REGIONS; i++) {
    regions[i] = new Region(rand() % 10);
    a.addBeneficiary(regions[i]);
  }

  for (int i = 0; i < VILLAGES; i++) {
    villages[i] = new Village(rand() % 10);
    if (i % 3 == 0) a.addBeneficiary(villages[i]);
    else regions[rand() % REGIONS]->addBeneficiary(villages[i]);
  }

  for (int i = 0; i < INDIVIDUALS; i++) {
    individuals[i] = new Individual(rand() % 10);
    if (i % 2 == 0) regions[i % 10]->addBeneficiary(individuals[i]);
    else villages[i % VILLAGES]->addBeneficiary(individuals[i]);
  }

  a.help();

  for (int i = 0; i < REGIONS; i++) std::cout << "Region " << i << ": " << regions[i]->help() << std::endl;
  for (int i = 0; i < VILLAGES; i++) std::cout << "Village " << i << ": " << villages[i]->help() << std::endl;
  for (int i = 0; i < INDIVIDUALS; i++) std::cout << "Individual " << i << ": " << individuals[i]->help() << std::endl;

  for (int i = 0; i < REGIONS; i++) delete regions[i];
  for (int i = 0; i < VILLAGES; i++) delete villages[i];
  for (int i = 0; i < INDIVIDUALS; i++) delete individuals[i];

  return 0;
}
