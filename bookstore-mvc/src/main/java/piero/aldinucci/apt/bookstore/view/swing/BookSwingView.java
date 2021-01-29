package piero.aldinucci.apt.bookstore.view.swing;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import piero.aldinucci.apt.bookstore.controller.BookstoreController;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.view.BookView;
import java.awt.Color;

/**
 * Swing JPanel implementation of BookView
 * 
 * @author Piero Aldinucci
 *
 */
public class BookSwingView extends JPanel implements BookView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private transient BookstoreController controller;
	/**
	 * 
	 */
	private DefaultListModel<Book> bookModelList;
	/**
	 * 
	 */
	private JButton btnDelete;
	/**
	 * 
	 */
	private JList<Book> bookJList;
	/**
	 * 
	 */
	private JLabel errorLabel;

	/**
	 * Create the panel.
	 * 
	 * @param controller unit for the MVC architecture.
	 */
	@Inject
	public BookSwingView(@Assisted BookstoreController controller) {
		this.controller = controller;

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

	/**
	 * 
	 */
	private void createErrorLabel() {
		errorLabel = new JLabel("");
		errorLabel.setForeground(Color.RED);
		errorLabel.setName("BookErrorLabel");
		GridBagConstraints gbclblNewLabel = new GridBagConstraints();
		gbclblNewLabel.gridwidth = 2;
		gbclblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbclblNewLabel.gridx = 1;
		gbclblNewLabel.gridy = 3;
		add(errorLabel, gbclblNewLabel);
	}

	/**
	 * 
	 */
	private void createDeleteButton() {
		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(e -> {
			Book book = bookJList.getSelectedValue();
			controller.deleteBook(book);
		});
		btnDelete.setEnabled(false);
		btnDelete.setName("DeleteBook");
		GridBagConstraints gbcbtnDelete = new GridBagConstraints();
		gbcbtnDelete.insets = new Insets(0, 0, 5, 5);
		gbcbtnDelete.gridx = 2;
		gbcbtnDelete.gridy = 2;
		add(btnDelete, gbcbtnDelete);
	}

	/**
	 * 
	 */
	private void createNewButton() {
		JButton btnNewButton = new JButton("New");
		btnNewButton.setName("NewBook");
		btnNewButton.addActionListener(e -> controller.composeBook());
		GridBagConstraints gbcbtnNewButton = new GridBagConstraints();
		gbcbtnNewButton.insets = new Insets(0, 0, 5, 5);
		gbcbtnNewButton.gridx = 1;
		gbcbtnNewButton.gridy = 2;
		add(btnNewButton, gbcbtnNewButton);
	}

	/**
	 * 
	 */
	private void createList() {
		bookModelList = new DefaultListModel<>();
		bookJList = new JList<>();
		bookJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		bookJList.addListSelectionListener(e -> btnDelete.setEnabled(!bookJList.isSelectionEmpty()));
		bookJList.setModel(getBookModelList());
		bookJList.setName("BookJList");
		GridBagConstraints gbclist = new GridBagConstraints();
		gbclist.gridwidth = 2;
		gbclist.insets = new Insets(0, 0, 5, 5);
		gbclist.fill = GridBagConstraints.BOTH;
		gbclist.gridx = 1;
		gbclist.gridy = 1;

		bookJList.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				return super.getListCellRendererComponent(list, getDisplayString((Book) value), index, isSelected,
						cellHasFocus);
			}
		});
		add(bookJList, gbclist);
	}

	/**
	 * Generate a string to be displayed in the book list
	 * 
	 * @param book object to be displayed
	 * @return resulting string
	 */
	private String getDisplayString(Book book) {
		return book.toString() + "; Authors: "
				+ book.getAuthors().stream().map(Author::getName).collect(Collectors.joining(" - "));
	}

	@Override
	public void showAllBooks(List<Book> books) {
		bookModelList.clear();
		books.stream().forEach(b -> bookModelList.addElement(b));
	}

	@Override
	public void bookAdded(Book book) {
		bookModelList.addElement(book);
		clearErrorLabel();
	}

	@Override
	public void bookRemoved(Book book) {
		bookModelList.removeElement(book);
		clearErrorLabel();
	}

	@Override
	public void showError(String message, Book book) {
		errorLabel.setText(message + ": " + book);
	}

	/**
	 * Made for testing purposes
	 * 
	 * @return underlying model list of alla vailable books
	 */
	DefaultListModel<Book> getBookModelList() {
		return bookModelList;
	}

	/**
	 * 
	 */
	private void clearErrorLabel() {
		errorLabel.setText(" ");
	}
}
