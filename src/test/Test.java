package test;

import java.awt.EventQueue;
import view.SelectFormView;

public class Test {

  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      SelectFormView ex = new SelectFormView();
      ex.setVisible(true);
    });
  }
}
