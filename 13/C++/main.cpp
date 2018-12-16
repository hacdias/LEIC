#include <string>
#include <vector>
#include <iostream>
#include <map>
#include <ctime>

enum class PaymentMethod {
  Cash, DebitCard, CreditCard, FidelityCard
};

class Client;

class Purchase {
  time_t        _date = time(NULL);
  std::string   _product;
  PaymentMethod _paymentMethod;
  double        _value;
  Client*       _client;
public:
  Purchase (std::string product, PaymentMethod payment, double value, Client* client):
    _product(product), _paymentMethod(payment), _value(value), _client(client) {}
  time_t        getDate           () const { return _date; }
  std::string   getProduct        () const { return _product; }
  PaymentMethod getPaymentMethod  () const { return _paymentMethod; }
  Client*       getClient         () const { return _client; }
  double        getValue          () const { return _value; }
};

class Client {
  int _id;
  time_t _birthday;
  std::string _name;
  std::string _phoneNumber;
  std::vector<Purchase*> _purchases;
public:
  Client (int id, std::string name, std::string phoneNumber, time_t birthday):
    _id(id), _name(name), _phoneNumber(phoneNumber), _birthday(birthday) {}
  int         getId             () const  { return _id; }
  std::string getName           () const  { return _name; }
  std::string getPhoneNumber    () const  { return _phoneNumber; }
  time_t      getBirthday       () const  { return _birthday; }
  Purchase*   getFirstPurchase  () const  { return _purchases.size() > 0 ? _purchases[0] : NULL; }
  int         purchaseCount     ()        { return _purchases.size(); }
  void        addPurchase       (Purchase* purchase) { _purchases.push_back(purchase); }
  int         getAge () const {
    return difftime(time(NULL), _birthday) / (60 * 60 * 24 * 365);
  }
};

class Filter {
public:
  Filter () {}
  virtual ~Filter () {};
  virtual bool is(Client*) = 0;
};

class PurchasesFilter: public Filter {
  int _min;
public:
  PurchasesFilter (int min): _min(min) {}
  bool is (Client* client) {
    return client->purchaseCount() >= _min;
  }
};

class AgeFilter: public Filter {
  int _min, _max;
public:
  AgeFilter (int min, int max = 100): _min(min), _max(max) {}
  bool is (Client* client) {
    return client->getAge() >= _min && client->getAge() <= _max;
  }
};

class Store {
  int _nextId = 0;
  std::map<int, Client*> _clients;
  std::vector<Purchase*> _purchases;
public:
  Store () {}
  Client* newClient (std::string name, std::string phoneNumber, time_t birthday) {
    Client* client = new Client(_nextId++, name, phoneNumber, birthday);
    _clients.insert(std::pair<int, Client*>(client->getId(), client));
    return client;
  }
  Client* getClient (int id) {
    return _clients.at(id);
  }
  Purchase* newPurchase (Client* client, std::string product, PaymentMethod pm, double value) {
    Purchase* purchase = new Purchase(product, pm, value, client);
    client->addPurchase(purchase);
    _purchases.push_back(purchase);
    return purchase;
  }
  std::map<int, Client*> getClients (Filter* filter) {
    if (filter == NULL)
      return _clients;
    
    std::map<int, Client*> clients;
    std::map<int, Client*>::iterator it;

    for (it = _clients.begin(); it != _clients.end(); it++)
      if (filter->is(it->second))
        clients.insert(std::pair<int, Client*>(it->second->getId(), it->second));

    
    return clients;
  }
  ~Store () {
    std::map<int, Client*>::iterator it;
    for (it = _clients.begin(); it != _clients.end(); it++)
      delete it->second;

    std::vector<Purchase*>::iterator it2;
    for (it2 = _purchases.begin(); it2 != _purchases.end(); it2++)
      delete *it2;
  }
};

time_t makeDate (int year, int month, int day) {
  time_t now = time(NULL);
  struct tm* time  = localtime(&now);
  time->tm_sec = 0;
  time->tm_min = 0;
  time->tm_hour = 0;
  time->tm_year = year - 1900;
  time->tm_mon = month - 1;
  time->tm_mday = day;
  return mktime(time);
}

int main () {
  Store s;

  Client *c = s.newClient("Henrique", "965856140", makeDate(1999, 10, 22));

  s.newPurchase(c, "Ovos", PaymentMethod::Cash, 4.3);

  std::map<int, Client*> clients = s.getClients(new AgeFilter(10, 11));
  std::map<int, Client*>::iterator it;
  for (it = clients.begin(); it != clients.end(); it++)
    std::cout << it->second->getAge() << std::endl;

  return 0;
}
