package com.example.bookmanagement.controller;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.User;
import com.example.bookmanagement.repository.BookRepository;
import com.example.bookmanagement.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;
@Controller
@RequestMapping("/books")
public class BookController {
	
	private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BookController(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }
    private User currentUser(Principal principal) {
        return userRepository.findByUsername(principal.getName());
    }
	    // 書籍一覧表示
    @GetMapping
    public String listBooks(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(value="title", required=false) String title,
                            @RequestParam(value="genre", required=false) String genre,
                            @RequestParam(value="author", required=false) String author,
                            Model model, Principal principal) {

        User user = currentUser(principal);
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());

        Page<Book> bookPage;
        boolean hasTitle = title != null && !title.isBlank();
        boolean hasGenre  = genre != null && !genre.isBlank();
        boolean hasAuthor = author != null && !author.isBlank();

        if (!hasTitle && !hasGenre && !hasAuthor) {
            bookPage = bookRepository.findByUser(user, pageable);
        } else if (hasTitle && !hasGenre && !hasAuthor) {
            bookPage = bookRepository.findByUserAndTitleContainingIgnoreCase(user, title, pageable);
        } else if (!hasTitle && hasGenre && !hasAuthor) {
            bookPage = bookRepository.findByUserAndGenreContainingIgnoreCase(user, genre, pageable);
        } else if (!hasTitle && !hasGenre && hasAuthor) {
            bookPage = bookRepository.findByUserAndAuthorContainingIgnoreCase(user, author, pageable);
        } else {
            // タイトル・ジャンル・著者すべてのAND検索（必要な場合）
            bookPage = bookRepository
                .findByUserAndTitleContainingIgnoreCaseAndGenreContainingIgnoreCaseAndAuthorContainingIgnoreCase(
                    user,
                    hasTitle ? title : "",
                    hasGenre ? genre : "",
                    hasAuthor ? author : "",
                    pageable
                );
        }

        model.addAttribute("bookPage", bookPage);
        model.addAttribute("title", title);
        model.addAttribute("genre", genre);
        model.addAttribute("author", author);
        return "book-list";
    } 


	    // 新規登録フォームの表示
	    @GetMapping("/new")
	    public String showCreateForm(Model model) {
	        model.addAttribute("book", new Book());
	        return "book-form";
	    }
	    
	    @PostMapping
	    public String createBook(@ModelAttribute Book book, Principal principal) {
	        book.setUser(currentUser(principal)); // 所有者をセット
	        bookRepository.save(book);
	        return "redirect:/books";
	    }


	    
	    @GetMapping("/{id}/edit")
	    public String editBookForm(@PathVariable Long id, Model model, Principal principal) {
	        Book book = bookRepository.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid book ID:" + id));
	        // 所有者チェック
	        if (!book.getUser().getUsername().equals(principal.getName())) {
	            throw new IllegalArgumentException("あなたの本ではありません");
	        }
	        model.addAttribute("book", book);
	        return "book-edit";
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
	    public String detail(@PathVariable Long id, Model model, Principal principal) {
	        Book book = bookRepository.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid book ID:" + id));
	        if (!book.getUser().getUsername().equals(principal.getName())) {
	            throw new IllegalArgumentException("あなたの本ではありません");
	        }
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
	    public String deleteBook(@PathVariable Long id, Principal principal) {
	        Book book = bookRepository.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid book ID:" + id));
	        if (!book.getUser().getUsername().equals(principal.getName())) {
	            throw new IllegalArgumentException("あなたの本ではありません");
	        }
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
