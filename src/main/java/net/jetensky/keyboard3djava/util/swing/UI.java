package net.jetensky.keyboard3djava.util.swing;

import boofcv.gui.image.ImagePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;

public class UI {
    private static BufferedImage img;
    public static JFrame frame;
    public static JSpinner left;
    public static JSpinner bottom;
    private static JTextField debugTextField;

    public void waitForSpace() {
        final CountDownLatch latch = new CountDownLatch(1);
        KeyEventDispatcher dispatcher = new KeyEventDispatcher() {
            // Anonymous class invoked from EDT
            public boolean dispatchKeyEvent(KeyEvent e) {
                /*if (e.getKeyCode() == KeyEvent.VK_SPACE)
                    latch.countDown();*/
                return false;
            }
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(dispatcher);
        try {
            latch.await();  // current thread waits here until countDown() is called
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(dispatcher);
    }

    public static ImagePanel showWindow(BufferedImage img, String title, ActionListener resetDistancesListener) {
        UI.img = img;

        frame = new JFrame(title);
        frame.setLayout(new GridLayout(1,0));

        // JPanel panel1 = new JPanel();

        JPanel controlsPanel = new JPanel(new GridLayout(0,1));
        controlsPanel.setBounds(100, 100, 730, 489);
        addControls(controlsPanel, resetDistancesListener);
        frame.add(controlsPanel);


        JPanel imageJPanel = new JPanel(new GridLayout(0,1));
        frame.add(imageJPanel);
        ImagePanel imagePanel = new ImagePanel(img);
        imageJPanel.add(imagePanel);

        // add(new JLabel(new ImageIcon(img)));

        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        return imagePanel;
    }

    private static void addControls(JPanel panel, ActionListener resetDistancesListener) {
        UI.left = addSpinner(panel, "Left distance of 8pen circle:", 320);
        UI.bottom = addSpinner(panel, "Bottom distance of 8pen circle:", 30);
        JButton resetDistancesBtn = new JButton("Reset distances");
        resetDistancesBtn.addActionListener(resetDistancesListener);
        addComponentToPanel(panel, "Reset distances", resetDistancesBtn);

        debugTextField = new JTextField();
        addComponentToPanel(panel, "Debug", debugTextField);

        /*JLabel lblPhone = new JLabel("Phone #");
        lblPhone.setBounds(65, 68, 46, 14);
        panel.add(lblPhone);

        JTextField textField_1 = new JTextField();
        textField_1.setBounds(128, 65, 86, 20);
        panel.add(textField_1);
        textField_1.setColumns(10);

        JLabel lblEmailId = new JLabel("Email Id");
        lblEmailId.setBounds(65, 115, 46, 14);
        panel.add(lblEmailId);

        JTextField textField_2 = new JTextField();
        textField_2.setBounds(128, 112, 247, 17);
        panel.add(textField_2);
        textField_2.setColumns(10);

        JLabel lblAddress = new JLabel("Address");
        lblAddress.setBounds(65, 162, 46, 14);
        panel.add(lblAddress);

        JTextArea textArea_1 = new JTextArea();
        textArea_1.setBounds(126, 157, 212, 40);
        panel.add(textArea_1);


        JButton btnClear = new JButton("Clear");

        btnClear.setBounds(312, 387, 89, 23);
        panel.add(btnClear);

        JLabel lblSex = new JLabel("Sex");
        lblSex.setBounds(65, 228, 46, 14);
        panel.add(lblSex);

        JLabel lblMale = new JLabel("Male");
        lblMale.setBounds(128, 228, 46, 14);
        panel.add(lblMale);

        JLabel lblFemale = new JLabel("Female");
        lblFemale.setBounds(292, 228, 46, 14);
        panel.add(lblFemale);

        JRadioButton radioButton = new JRadioButton("");
        radioButton.setBounds(337, 224, 109, 23);
        panel.add(radioButton);

        JRadioButton radioButton_1 = new JRadioButton("");
        radioButton_1.setBounds(162, 224, 109, 23);
        panel.add(radioButton_1);

        JLabel lblOccupation = new JLabel("Occupation");
        lblOccupation.setBounds(65, 288, 67, 14);
        panel.add(lblOccupation);

        JComboBox comboBox = new JComboBox();
        comboBox.addItem("Select");
        comboBox.addItem("Business");
        comboBox.addItem("Engineer");
        comboBox.addItem("Doctor");
        comboBox.addItem("Student");
        comboBox.addItem("Others");
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            }
        });
        comboBox.setBounds(180, 285, 91, 20);
        panel.add(comboBox);


        JButton btnSubmit = new JButton("submit");

        btnSubmit.setBackground(Color.BLUE);
        btnSubmit.setForeground(Color.MAGENTA);
        btnSubmit.setBounds(65, 387, 89, 23);
        panel.add(btnSubmit);*/
    }

    private static JSpinner addSpinner(JPanel panel, String label, int initialValue) {
        SpinnerModel model = new SpinnerNumberModel(initialValue, 0, 480, 1);
        JSpinner spinner = new JSpinner(model);
        addComponentToPanel(panel, label, spinner);
        return spinner;
    }

    private static JComponent addComponentToPanel(JPanel panel, String label, JComponent spinner) {
        panel.add(new JLabel(label));
        panel.add(spinner);
        return spinner;
    }

    public void close() {
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }


    public static int leftValue() {
        return (Integer) left.getValue();
    }

    public static int bottomValue() {
        return (Integer) bottom.getValue();
    }

    public void debug(String msg) {
        debugTextField.setText(msg);

    }
}
