package at.gepardec.training.cdi.basic.scopes;

import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class MixedRequestBean implements Serializable {

  private int value = 0;

  public int incrementAndGet() {
    return ++value;
  }
}
