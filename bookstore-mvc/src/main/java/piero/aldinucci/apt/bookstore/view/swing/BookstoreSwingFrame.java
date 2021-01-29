package piero.aldinucci.apt.bookstore.view.swing;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * JFrame with 2 tabbed panels.
 * 
 * @author Piero Aldinucci
 *
 */
public class BookstoreSwingFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private JPanel contentPane;

	/**
	 * Create the frame.
	 * 
	 * @param authorPanel view class for authors of the MVC architecture
	 * @param bookPanel view class for books of the MVC architecture
	 */
	public BookstoreSwingFrame(JPanel authorPanel, JPanel bookPanel) {
		setTitle("Bookstore View");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 1, 0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		contentPane.add(tabbedPane);
		tabbedPane.setName("MainPane");
		tabbedPane.addTab("Authors", null, authorPanel, null);		
		tabbedPane.addTab("Books", null, bookPanel, null);
	}

}
