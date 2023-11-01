#include <string>
#include <vector>
#include <iostream>

class Element {
public:
  Element () {}
  virtual ~Element () {}
  virtual std::string render () = 0;
};

class Span: public Element {
  std::string _content;
public:
  Span (std::string content): _content(content) {}
  std::string render () {
    return "<span>" + _content + "</span>";
  }
};

class Image: public Element {
  std::string _src;
public:
  Image (std::string src): _src(src) {}
  std::string render () {
    return "<img src=\"" + _src + "\"/>";
  }
};

class Collection: public Element {
protected:
  std::vector<Element*> _els;
  std::string content () {
    std::string ctr = "";
    std::vector<Element*>::iterator it;
    for (it = _els.begin(); it != _els.end(); it++)
      ctr += (*it)->render();
    return ctr;
  }
};

class Paragraph: public Collection {
public:
  void add (Element *s) { _els.push_back(s); }
  std::string render () {
    return "<p>" + content() + "</p>";
  }
};

class Page: public Collection {
public:
  void add (Page *p) { _els.push_back(p); }
  void add (Paragraph *p) { _els.push_back(p); }
  std::string render () {
    return "<page>" + content() + "</page>";
  }
};

int main () {
  Page pg;
  
  Paragraph p1, p2;
  p1.add(new Span("TEXT"));
  p1.add(new Span("TEXT"));
  p2.add(new Image("IMG"));
  pg.add(&p1);
  pg.add(&p2);

  std::cout << pg.render() << std::endl;
  return 0;
}
