package main;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class JLight3 extends JFrame {

	private static JLightPanel lightPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JLight3 frame = new JLight3();
					frame.setVisible(true);
					lightPanel.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public JLight3() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setBounds(100, 100, 450, 300);
		lightPanel = new JLightPanel();
		lightPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		lightPanel.setLayout(new BorderLayout(0, 0));
		setContentPane(lightPanel);
	}

}
