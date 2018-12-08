#include <string>
#include <iostream>

class TextItem {
public:
  TextItem () {}
  virtual ~TextItem () {}
  virtual std::string render () = 0;
  virtual std::string text () = 0;
};

class Span: public TextItem {
  std::string _txt;
public:
  Span (std::string txt): _txt(txt) {}
  std::string render () {
    return "<span>" + _txt + "</span>";
  }
  std::string text () {
    return _txt;
  }
};

class Formatted: public TextItem {
  TextItem* _textItem;
public:
  Formatted (TextItem* txt): _textItem(txt) {}
  std::string render () {
    return _textItem->render();
  }
  std::string text () {
    return _textItem->text();
  }
};

class Bold: public Formatted {
public:
  Bold (TextItem* txt): Formatted(txt) {}
  std::string render () {
    return "<b>" + Formatted::render() + "</b>";
  }
};

class Italic: public Formatted {
public:
  Italic (TextItem* txt): Formatted(txt) {}
  std::string render () {
    return "<i>" + Formatted::render() + "</i>";
  }
};

class Underline: public Formatted {
public:
  Underline (TextItem* txt): Formatted(txt) {}
  std::string render () {
    return "<u>" + Formatted::render() + "</u>";
  }
};

int main () {
  TextItem* txt1 = new Span("Batata");
  TextItem* txt2 = new Bold(new Italic(txt1));
  std::cout << txt2->render() << std::endl;
  std::cout << txt2->text() << std::endl;
  return 0;
}
