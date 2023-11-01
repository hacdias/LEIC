import java.util.ArrayList;

abstract class Taxpayer {
  abstract public double accept (FriendlyIRS irs);
}

class Person extends Taxpayer {
  @Override
  public double accept (FriendlyIRS irs) {
    return irs.taxPerson(this);
  }
}

class Company extends Taxpayer {
  private ArrayList<Person> _employees;

  public void addEmployee (Person p) {
    _employees.add(p);
  }
  
  public ArrayList<Person> getEmployees () {
    return _employees;
  }

  @Override
  public double accept (FriendlyIRS irs) {
    return irs.taxCompany(this);
  }
}

class Region extends Taxpayer {
  private ArrayList<Company> _companies;

  public void addEmployee (Company c) {
    _companies.add(c);
  }

  public ArrayList<Company> getCompanies () {
    return _companies;
  }

  @Override
  public double accept (FriendlyIRS irs) {
    return irs.taxRegion(this);
  }
}

abstract class FriendlyIRS {
  abstract public double taxPerson   (Person p);
  abstract public double taxCompany  (Company c);
  abstract public double taxRegion   (Region r);
}

class VanillaTaxes extends FriendlyIRS {
  public double taxPerson (Person p) {
    return 1;
  }
  public double taxCompany (Company c) {
    double tax = 0;
    for (Person e : c.getEmployees()) {
      tax += e.accept(this);
    }
    return tax;
  }
  public double taxRegion  (Region r) {
    double tax = 0;
    for (Company c : r.getCompanies()) {
      tax += c.accept(this);
    }
    return tax;
  }
}

class BecauseWeCare extends FriendlyIRS {
  private final int LOW = 1;
  private final int POP = 25;

  public double taxCompany(Company c) {
      double tax = 0;
      ArrayList<Person> em = c.getEmployees();
      for (Person e : em)
        tax += e.accept(this);
      if (em.size() < POP || tax < LOW)
          tax *= .9;
      return tax;
  }
  public double taxPerson(Person p) {
      return 1;
  }
  public double taxRegion(Region r) {
      double tax = 0;
      ArrayList<Company> com = r.getCompanies();
      for (Company e : com)
        tax += e.accept(this);
      if (com.size() < POP || tax < LOW)
          tax *= .9;
      return tax;
  }
}
