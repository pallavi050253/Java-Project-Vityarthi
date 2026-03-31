
public class Book {
    private int bookId;
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private int totalQty;
    private int availableQty;

    
    public Book() {
    }

    public Book(int bookId, String title, String author, String isbn, String genre, int totalQty, int availableQty) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
        this.totalQty = totalQty;
        this.availableQty = availableQty;
    }

        public Book(String title, String author, String isbn, String genre, int totalQty, int availableQty) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
        this.totalQty = totalQty;
        this.availableQty = availableQty;
    }

        public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getTotalQty() { return totalQty; }
    public void setTotalQty(int totalQty) { this.totalQty = totalQty; }

    public int getAvailableQty() { return availableQty; }
    public void setAvailableQty(int availableQty) { this.availableQty = availableQty; }

    @Override
    public String toString() {
        return "ID: " + bookId + " | Title: " + title + " | Author: " + author + " | Available: " + availableQty;
    }
}
