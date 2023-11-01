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
  std::string _tag;
public:
  Formatted (TextItem* txt, std::string tag): _textItem(txt), _tag(tag) {}
  std::string render () {
    return "<" + _tag + ">" + _textItem->render() + "</" + _tag + ">";
  }
  std::string text () {
    return _textItem->text();
  }
};

class Bold: public Formatted {
public:
  Bold (TextItem* txt): Formatted(txt, "b") {}
};

class Italic: public Formatted {
public:
  Italic (TextItem* txt): Formatted(txt, "i") {}
};

class Underline: public Formatted {
public:
  Underline (TextItem* txt): Formatted(txt, "u") {}
};

int main () {
  TextItem* txt1 = new Span("Batata");
  TextItem* txt2 = new Bold(new Italic(txt1));
  std::cout << txt2->render() << std::endl;
  std::cout << txt2->text() << std::endl;
  return 0;
}
