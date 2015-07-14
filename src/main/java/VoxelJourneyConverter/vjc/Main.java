package VoxelJourneyConverter.vjc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Main {
	static JFrame frame;
	static JPanel panel;
	static JComboBox<String> modeSelector;
	static JLabel info;
	static JButton continueButton;

	public static void main(String[] args) {
		setupGUI();
	}

	public static void setupGUI() {
		
		// yes this looks horrible, but it's just supposed to work at this point, not to look pretty
		Font bigFont=new Font("big",Font.BOLD,25);
		frame = new JFrame("Mapdataconverter");
		frame.setSize(600, 300);
		panel = new JPanel();
		panel.setLayout(new FlowLayout());
		info = new JLabel("What do you want to do?");
		info.setPreferredSize(new Dimension(400, 50));
		info.setFont(bigFont);
		panel.add(info);
		modeSelector = new JComboBox<String>(getModes());
		modeSelector.setPreferredSize(new Dimension(400, 50));
		panel.add(modeSelector);
		panel.setBackground(new Color(0x99CCFF));
		continueButton = new JButton("Continue");
		continueButton.setPreferredSize(new Dimension(200,50));
		continueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				getGoing();
			}
		});
		panel.add(continueButton);
		frame.add(panel);
		frame.setVisible(true);
	}

	public static String[] getModes() {
		String[] modes = { "Convert Voxelmap data into Journeymap data",
				"Convert Journeymap data into Voxelmap data",
				"Merge Voxelmap data of multiple people" };
		return modes;
	}

	public static void getGoing() {
		File sourcefolder;
		JFileChooser chooser;
		int returnValue;
		switch ((String) modeSelector.getSelectedItem()) {
		case "Convert Voxelmap data into Journeymap data":
			VoxelToJourneyConverter vtjc = new VoxelToJourneyConverter();
			File sourcefile = null;
			chooser = new JFileChooser();
			chooser.setDialogTitle("Please choose the folder containing your Voxel data");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			returnValue = chooser.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				sourcefile = chooser.getSelectedFile();
			}
			if (vtjc.convert(sourcefile)) {
				// TODO success window
				System.exit(0);
			} else {
				// TODO tell them something broke
				System.exit(1);
			}
			break;
		case "Merge Voxelmap data of multiple people":
			VoxelMapMerger vmm = new VoxelMapMerger();
			sourcefolder = null;
			chooser = new JFileChooser();
			chooser.setDialogTitle("Please choose the folder of your Voxel data");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			returnValue = chooser.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				sourcefolder = chooser.getSelectedFile();
			}
			vmm.merge(sourcefolder);
				System.out.println("Success");
				System.exit(0);
			
			break;
		case "Convert Journeymap data into Voxelmap data":
			JourneyToVoxelConverter jtvc = new JourneyToVoxelConverter();
			sourcefolder = null;
			chooser = new JFileChooser();
			chooser.setDialogTitle("Please choose the location of your journeymap data");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			returnValue = chooser.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				sourcefolder = chooser.getSelectedFile();
			}
			jtvc.convert(sourcefolder);
				// TODO success window
				System.exit(0);
			break;
			
		}
	}
	

}
