import javax.swing.*;

public class Main {
    public static void main(String[] args) throws Exception {
        int boardWidth = 1920;
        int boardHeight = 1040;

        JFrame frame = new JFrame("Flappy Bird");
        // frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Pikmin picmin = new Pikmin();
        frame.add(picmin);
        frame.pack();
        picmin.requestFocus();
        frame.setVisible(true);
    }
}