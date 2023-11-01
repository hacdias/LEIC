class TrafficLight {
  private State _state;

  /** Initialize traffic light. Starts blinking. */
  public TrafficLight() {
    _state = new Blinking(this);
}
  
  public void panic () {
    _state.panic();
  }

  public void off () {
    _state.off();
  }

  public void on () {
    _state.on();
  }
  
  public void tick () {
    _state.tick();
  }

  public String status () {
    return _state.status();
  }

  public abstract class State {
    public abstract void tick ();
    public abstract void panic ();
    public abstract void off ();
    public void on () {};
    public abstract String status ();

    protected void setState (State newState) {
      _state = newState;
    }

    protected TrafficLight getLight () {
      return TrafficLight.this;
    }
  }
}

abstract class Ticking extends TrafficLight.State {
  Ticking (TrafficLight light) {
    light.super();
  }

  @Override
  public void off () {
    setState(new Blinking(getLight()));
  }

  @Override
  public void panic() {
    setState(new Panic(getLight()));
  }
}

class Red extends Ticking {
  Red (TrafficLight light) {
    super(light);
  }

  public void tick () {
    setState(new Green(getLight()));
  }

  public String status () {
    return "Red";
  }
}

class Yellow extends Ticking {
  Yellow (TrafficLight light) {
    super(light);
  }

  public void tick () {
    setState(new Red(getLight()));
  }

  public String status () {
    return "Yellow";
  }
}

class Green extends Ticking {
  Green (TrafficLight light) {
    super(light);
  }

  public void tick () {
    setState(new Yellow(getLight()));
  }

  public String status () {
    return "Green";
  }
}

class Panic extends TrafficLight.State {
  Panic (TrafficLight light) {
    light.super();
  }

  public void on () {
    setState(new Red(getLight()));
  }
  public void off () {}
  public void panic () {}
  public void tick () {}
  public String status () {
    return "Panic";
  }
}

class Blinking extends TrafficLight.State {
  Blinking (TrafficLight light) {
    light.super();
  }

  public void on () {
    setState(new Red(getLight()));
  }

  
  @Override
  public void panic() {
    setState(new Panic(getLight()));
  }

  public void off () {}
  public void tick () {}

  public String status () {
    return "Blinking";
  }
}

public class Application {
  /**
   * @param args
   */
  public static void main(String[] args) {
      TrafficLight light = new TrafficLight();
      System.out.println("Light status: " + light.status());
      light.off();
      System.out.println("Light status: " + light.status());
      light.panic();
      System.out.println("Light status: " + light.status());
      light.on();
      System.out.println("Light status: " + light.status());
      light.tick();
      System.out.println("Light status: " + light.status());
      light.tick();
      System.out.println("Light status: " + light.status());
      light.tick();
      System.out.println("Light status: " + light.status());
      light.tick();
      System.out.println("Light status: " + light.status());
      light.panic();
      System.out.println("Light status: " + light.status());
      light.off();
      System.out.println("Light status: " + light.status());
      light.on();
      System.out.println("Light status: " + light.status());
      light.off();
      System.out.println("Light status: " + light.status());
  }
}
