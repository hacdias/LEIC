#include <string>
#include <iostream>

class WashingMachine {
    class DoorState *_st;
public:
    WashingMachine ();
    void setState (class DoorState *st) { _st = st; }
    void power  ();
    void open   ();
    void close   ();
    void tick   ();
};

class DoorState {
protected:
    WashingMachine *_ws;
public:
    DoorState (WashingMachine *ws): _ws(ws) {};
    virtual void power  () = 0;
    virtual void open   () = 0;
    virtual void close  () = 0;
    virtual void tick   () = 0;
};

class OpenDoor: public DoorState {
public:
    OpenDoor (WashingMachine *wm) : DoorState(wm) {
        std::cout << "Door Opened" << std::endl;
    };

    void power  () { /* does nothing */ }
    void tick   () { /* does nothing */ }
    void open   () { /* does nothing */ }
    void close  ();
};

class WaitingDoor: public DoorState {
    int _ticks = 0;
public:
    WaitingDoor (WashingMachine *wm) : DoorState(wm) {
        std::cout << "Door Waiting" << std::endl;
    };
    void power  () { /* does nothing */ }
    void close  () { /* does nothing */ }
    void open   () { /* does nothing */ }
    void tick   () {
        _ticks++;
        if (_ticks >= 120) _ws->setState(new OpenDoor(_ws));
    }
};

class ClosedDoor: public DoorState {
    int _ticks = 0;
    bool _started = false;
public:
    ClosedDoor (WashingMachine *wm) : DoorState(wm) {
        std::cout << "Door Closed" << std::endl;
    };

    void tick () {
        _ticks++;
        if (_ticks == 5400) _ws->setState(new WaitingDoor(_ws));
    }

    void power () { _started = !_started; }
    void open  () {
        if (!_started && _ticks == 0) _ws->setState(new OpenDoor(_ws));
        else if (!_started || _ticks >= 5400) _ws->setState(new WaitingDoor(_ws));
    }
    void close () { /* does nothing */ }
};

void OpenDoor::close () {
    _ws->setState(new ClosedDoor(_ws));
}

void WashingMachine::power      () { _st->power(); }
void WashingMachine::open       () { _st->open(); }
void WashingMachine::close      () { _st->close(); }
void WashingMachine::tick       () { _st->tick(); }

WashingMachine::WashingMachine () : _st(new OpenDoor(this)) {}
