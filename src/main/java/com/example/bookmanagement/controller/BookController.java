package com.example.bookmanagement.controller;
import java.io.PrintWriter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.repository.BookRepository;

import jakarta.servlet.http.HttpServletResponse;
@Controller
@RequestMapping("/books")
public class BookController {
	 @Autowired
	    private final BookRepository bookRepository;

	    public BookController(BookRepository bookRepository) {
	        this.bookRepository = bookRepository;
	    }


	    // 書籍一覧表示
	    @GetMapping
	    public String listBooks(@RequestParam(defaultValue = "0") int page, 
	    						@RequestParam(value = "title", required = false) String title,
	                            @RequestParam(value = "genre", required = false) String genre,
	                            @RequestParam(value = "author", required = false) String author,
	                            Model model) {

	        List<Book> books;
	        Page<Book> bookPage = bookRepository.findAll(PageRequest.of(page, 10)); // 10件ずつ表示

	        boolean hasTitle = title != null && !title.isEmpty();
	        boolean hasGenre = genre != null && !genre.isEmpty();
	        boolean hasAuthor = author != null && !author.isEmpty();

	        if (!hasTitle && !hasGenre && !hasAuthor) {
	            books = bookRepository.findAll();
	        } else if (hasTitle && !hasGenre && !hasAuthor) {
	            books = bookRepository.findByTitleContainingIgnoreCase(title);
	        } else if (!hasTitle && hasGenre && !hasAuthor) {
	            books = bookRepository.findByGenreContainingIgnoreCase(genre);
	        } else if (!hasTitle && !hasGenre && hasAuthor) {
	            books = bookRepository.findByAuthorContainingIgnoreCase(author);
	        } else if (hasTitle && hasGenre && !hasAuthor) {
	            books = bookRepository.findByTitleContainingIgnoreCaseAndGenreContainingIgnoreCase(title, genre);
	        } else if (hasTitle && !hasGenre && hasAuthor) {
	            books = bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(title, author);
	        } else if (!hasTitle && hasGenre && hasAuthor) {
	            books = bookRepository.findByGenreContainingIgnoreCaseAndAuthorContainingIgnoreCase(genre, author);
	        } else {
	            books = bookRepository.findByTitleContainingIgnoreCaseAndGenreContainingIgnoreCaseAndAuthorContainingIgnoreCase(title, genre, author);
	        }
	        
	        model.addAttribute("bookPage", bookPage);
	        model.addAttribute("books", books);
	        model.addAttribute("title", title);
	        model.addAttribute("genre", genre);

	        return "book-list";
	    }


	    // 新規登録フォームの表示
	    @GetMapping("/new")
	    public String showCreateForm(Model model) {
	        model.addAttribute("book", new Book());
	        return "book-form";
	    }
	    // 新規登録フォームからのPOSTを処理
	    @PostMapping
	    public String createBook(@ModelAttribute Book book) {
	        bookRepository.save(book);
	        return "redirect:/books";
	    }

	    
	    @GetMapping("/{id}/edit")
	    public String editBookForm(@PathVariable Long id, Model model) {
	        Book book = bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid book ID:" + id));
	        model.addAttribute("book", book);
	        return "book-form"; // 新規登録と同じフォームを使い回せる！
	    }
	    // 編集された本の保存
	    @PostMapping("/{id}")
	    public String saveEditBook(@PathVariable Long id, @ModelAttribute Book book) {
	        book.setId(id); // IDを設定
	        bookRepository.save(book);
	        return "redirect:/books"; // 編集後は一覧ページにリダイレクト
	    }
	 // 書籍詳細画面表示
	    @GetMapping("/{id}")
	    public String bookDetail(@PathVariable Long id, Model model) {
	        Book book = bookRepository.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid book ID:" + id));

	        model.addAttribute("book", book);
	        return "book-detail";
	    }
	    // 書籍の保存処理
	    @PostMapping("/save")
	    public String saveBook(@ModelAttribute Book book) {
	        bookRepository.save(book);
	        return "redirect:/books";
	    }
	    @PostMapping("/update/{id}")
	    public String updateBook(@PathVariable Long id, @ModelAttribute Book book) {
	        book.setId(id); // 上書き
	        bookRepository.save(book);
	        return "redirect:/books";
	    }

	 // 本の削除処理
	    @GetMapping("/{id}/delete")
	    public String deleteBook(@PathVariable Long id) {
	        Book book = bookRepository.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid book ID:" + id));

	        bookRepository.delete(book);
	        return "redirect:/books";
	    }
	 // CSVダウンロード処理
	    @GetMapping("/export/csv")
	    public void exportToCSV(HttpServletResponse response) {
	        try {
	            // ファイル名の指定
	            response.setContentType("text/csv; charset=UTF-8");
	            response.setHeader("Content-Disposition", "attachment; filename=\"books.csv\"");

	            // CSVファイルに書き込み
	            PrintWriter writer = response.getWriter();

	            // CSVヘッダー
	            writer.println("ID,タイトル,著者,ジャンル,メモ");

	            // 書籍データ取得（全件）
	            List<Book> books = bookRepository.findAll();

	            // CSVデータ出力
	            for (Book book : books) {
	                writer.printf("%d,%s,%s,%s,%s%n",
	                    book.getId(),
	                    book.getTitle(),
	                    book.getAuthor(),
	                    book.getGenre(),
	                    book.getMemo().replace("\n", " ").replace(",", " "));
	            }

	            writer.flush();
	            writer.close();

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }      

}
