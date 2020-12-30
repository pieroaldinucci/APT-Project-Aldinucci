package piero.aldinucci.apt.bookstore.view.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

public class NewBookDialogImpl extends NewBookDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			NewBookDialogImpl dialog = new NewBookDialogImpl();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public NewBookDialogImpl() {
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gblcontentPanel = new GridBagLayout();
		gblcontentPanel.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gblcontentPanel.rowHeights = new int[] { 0, 29, 25, 0, 0 };
		gblcontentPanel.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gblcontentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		contentPanel.setLayout(gblcontentPanel);
		
		createTextField();
		createBookAuthorsList();
		createAddAuthorButton();
		createAvailableAuthorsList();
		createDescriptionLabels();
		createRemoveAuthorButton();

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setEnabled(false);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	private void createRemoveAuthorButton() {
		JButton button = new JButton(">");
		button.setEnabled(false);
		button.setName("buttonRemoveAuthor");
		GridBagConstraints gbcbutton = new GridBagConstraints();
		gbcbutton.insets = new Insets(0, 0, 5, 5);
		gbcbutton.gridx = 2;
		gbcbutton.gridy = 2;
		contentPanel.add(button, gbcbutton);
	}

	private void createDescriptionLabels() {
		JLabel lblTitle = new JLabel("Title");
		GridBagConstraints gbclblTitle = new GridBagConstraints();
		gbclblTitle.insets = new Insets(0, 0, 5, 5);
		gbclblTitle.anchor = GridBagConstraints.EAST;
		gbclblTitle.gridx = 0;
		gbclblTitle.gridy = 0;
		contentPanel.add(lblTitle, gbclblTitle);

		JLabel lblBook = new JLabel("Book");
		GridBagConstraints gbclblBook = new GridBagConstraints();
		gbclblBook.anchor = GridBagConstraints.SOUTH;
		gbclblBook.insets = new Insets(0, 0, 5, 5);
		gbclblBook.gridx = 0;
		gbclblBook.gridy = 1;
		contentPanel.add(lblBook, gbclblBook);

		JLabel lblAuthors = new JLabel("authors");
		GridBagConstraints gbclblAuthors = new GridBagConstraints();
		gbclblAuthors.anchor = GridBagConstraints.NORTH;
		gbclblAuthors.insets = new Insets(0, 0, 5, 5);
		gbclblAuthors.gridx = 0;
		gbclblAuthors.gridy = 2;
		contentPanel.add(lblAuthors, gbclblAuthors);
	}

	private void createAvailableAuthorsList() {
		JList<Book> availableAuthors = new JList<>();
		availableAuthors.setName("AvailableAuthors");
		availableAuthors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		GridBagConstraints gbcavailableAuthors = new GridBagConstraints();
		gbcavailableAuthors.gridheight = 3;
		gbcavailableAuthors.fill = GridBagConstraints.BOTH;
		gbcavailableAuthors.gridx = 3;
		gbcavailableAuthors.gridy = 1;
		contentPanel.add(availableAuthors, gbcavailableAuthors);
	}

	private void createAddAuthorButton() {
		JButton button = new JButton("<");
		button.setName("buttonAddAuthor");
		button.setEnabled(false);
		GridBagConstraints gbcbutton = new GridBagConstraints();
		gbcbutton.insets = new Insets(0, 0, 5, 5);
		gbcbutton.gridx = 2;
		gbcbutton.gridy = 1;
		contentPanel.add(button, gbcbutton);
	}

	private void createBookAuthorsList() {
		JList<Book> bookAuthors = new JList<>();
		bookAuthors.setName("BookAuthors");
		bookAuthors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		GridBagConstraints gbcbookAuthors = new GridBagConstraints();
		gbcbookAuthors.insets = new Insets(0, 0, 0, 5);
		gbcbookAuthors.gridheight = 3;
		gbcbookAuthors.fill = GridBagConstraints.BOTH;
		gbcbookAuthors.gridx = 1;
		gbcbookAuthors.gridy = 1;
		contentPanel.add(bookAuthors, gbcbookAuthors);
	}

	private void createTextField() {
		textField = new JTextField();
		textField.setName("titleTextField");
		GridBagConstraints gbctextField = new GridBagConstraints();
		gbctextField.gridwidth = 3;
		gbctextField.insets = new Insets(0, 0, 5, 0);
		gbctextField.fill = GridBagConstraints.HORIZONTAL;
		gbctextField.gridx = 1;
		gbctextField.gridy = 0;
		contentPanel.add(textField, gbctextField);
		textField.setColumns(10);
	}

	@Override
	public Optional<Book> getReturnValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAuthorList(List<Author> authors) {
		// TODO Auto-generated method stub

	}

}
