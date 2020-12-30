package piero.aldinucci.apt.bookstore.view.swing;

import javax.swing.JPanel;

import piero.aldinucci.apt.bookstore.controller.BookstoreController;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.view.BookView;

import java.awt.GridBagLayout;
import javax.swing.JList;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

public class BookSwingView extends JPanel implements BookView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private transient BookstoreController controller;

	private DefaultListModel<Book> bookModelList;

	private JButton btnDelete;

	private JList<Book> bookJList;

	/**
	 * Create the panel.
	 */
	public BookSwingView() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 30, 150, 150, 30, 0 };
		gridBagLayout.rowHeights = new int[] { 30, 100, 30, 30, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		createList();
		createNewButton();
		createDeleteButton();
		createErrorLabel();

	}

	private void createErrorLabel() {
		JLabel errorLabel = new JLabel("");
		errorLabel.setName("ErrorLabel");
		GridBagConstraints gbclblNewLabel = new GridBagConstraints();
		gbclblNewLabel.gridwidth = 2;
		gbclblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbclblNewLabel.gridx = 1;
		gbclblNewLabel.gridy = 3;
		add(errorLabel, gbclblNewLabel);
	}

	private void createDeleteButton() {
		btnDelete = new JButton("Delete Book");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Book book = bookJList.getSelectedValue();
				controller.deleteBook(book);
			}
		});
		btnDelete.setEnabled(false);
		btnDelete.setName("DeleteBook");
		GridBagConstraints gbcbtnDelete = new GridBagConstraints();
		gbcbtnDelete.insets = new Insets(0, 0, 5, 5);
		gbcbtnDelete.gridx = 2;
		gbcbtnDelete.gridy = 2;
		add(btnDelete, gbcbtnDelete);
	}

	private void createNewButton() {
		JButton btnNewButton = new JButton("New Book");
		btnNewButton.setName("NewBook");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.composeBook();
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton.gridx = 1;
		gbc_btnNewButton.gridy = 2;
		add(btnNewButton, gbc_btnNewButton);
	}

	private void createList() {
		bookModelList = new DefaultListModel<>();
		bookJList = new JList<>();
		bookJList.addListSelectionListener(e -> btnDelete.setEnabled(!bookJList.isSelectionEmpty()));
		bookJList.setModel(getBookModelList());
		bookJList.setName("BookJList");
		GridBagConstraints gbclist = new GridBagConstraints();
		gbclist.gridwidth = 2;
		gbclist.insets = new Insets(0, 0, 5, 5);
		gbclist.fill = GridBagConstraints.BOTH;
		gbclist.gridx = 1;
		gbclist.gridy = 1;
		add(bookJList, gbclist);
	}

	@Override
	public void showAllBooks(List<Book> books) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bookAdded(Book book) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bookRemoved(Book book) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showError(String message, Book book) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showCreateBook(List<Author> authors) {
		// TODO Auto-generated method stub

	}

	public void setController(BookstoreController controller) {
		this.controller = controller;
	}

	DefaultListModel<Book> getBookModelList() {
		return bookModelList;
	}

}
