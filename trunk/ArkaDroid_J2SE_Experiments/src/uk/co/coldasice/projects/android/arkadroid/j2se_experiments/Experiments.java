package uk.co.coldasice.projects.android.arkadroid.j2se_experiments;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Experiments {

	public static int currentDebugValue = -1;
	public static String[] debugFromArkaDroid = new String[] {
			"12-06 14:41:44.606: DEBUG/updateGame() - deadbrick(751): ballPos: Rect(279, 105 - 299, 124), brickPos: Rect(250, 100 - 280, 115), ballDirection: -1.4844966322940416, -0.9291671286122026",
			"12-06 14:41:52.097: DEBUG/updateGame() - deadbrick(751): ballPos: Rect(22, 104 - 42, 123), brickPos: Rect(40, 100 - 70, 115), ballDirection: 1.6794676550187027, -1.2894288284853794"
	};
	
	public static void main(String[] args) throws RuntimeException {
		final ExperimentValues[] values = new ExperimentValues[debugFromArkaDroid.length];
		for (int i=0; i<values.length; i++) {
			values[i] = new ExperimentValues(debugFromArkaDroid[i]);
		}
		final JFrame frame = new JFrame();
		JPanel panel = new JPanel(new BorderLayout());
		final ExperimentCanvas canv = new ExperimentCanvas();
		canv.setPreferredSize(new Dimension(640, 480));
		panel.add(canv, BorderLayout.CENTER);
		JButton nextButton = new JButton("Next values");
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentDebugValue + 1 < debugFromArkaDroid.length) {
					currentDebugValue++;
					canv.setActiveExperimentValues(values[currentDebugValue]);
					canv.repaint();
				}
				else {
					JOptionPane.showMessageDialog(canv, "No more...");
					frame.dispose();
				}
			}
		});
		panel.add(nextButton, BorderLayout.SOUTH);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static class ExperimentValues {
		double ballDx, ballDy;
		Rect ball;
		Rect brick;
		public ExperimentValues(String debug) {
			Pattern parser = Pattern.compile(".*ballPos: Rect\\((.*), (.*) - (.*), (.*)\\), " +
					"brickPos: Rect\\((.*), (.*) - (.*), (.*)\\), " +
					"ballDirection: (.*), (.*)");
			Matcher m = parser.matcher(debug);
			if (!m.find()) throw new RuntimeException("Invalid debug format: " + debug);
			int i = 1;
			ball = new Rect(getInt(m, i++), getInt(m, i++), getInt(m, i++), getInt(m, i++));
			brick = new Rect(getInt(m, i++), getInt(m, i++), getInt(m, i++), getInt(m, i++));
			ballDx = getDouble(m, i++);
			ballDy = getDouble(m, i++);
		}
		public static int getInt(Matcher m, int group) {
			return Integer.parseInt(m.group(group));
		}
		public static double getDouble(Matcher m, int group) {
			return Double.parseDouble(m.group(group));
		}
	}
	
	public static class Rect {
		int left, top, right, bottom;
		public Rect(int left, int top, int right, int bottom) {
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
		}
	}
	
}
