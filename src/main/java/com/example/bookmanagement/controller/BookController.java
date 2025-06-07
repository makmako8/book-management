package com.example.bookmanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.repository.BookRepository;
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
	    public String listBooks(Model model) {
	        List<Book> books = bookRepository.findAll();
	        model.addAttribute("books", books);
	        return "book-list";
	    }

	    // 新規登録フォームの表示
	    @GetMapping("/new")
	    public String showCreateForm(Model model) {
	        model.addAttribute("book", new Book());
	        return "book-form";
	    }
	    @GetMapping("/books/{id}/edit")
	    public String editBookForm(@PathVariable Long id, Model model) {
	        Book book = bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid book ID:" + id));
	        model.addAttribute("book", book);
	        return "book-form"; // 新規登録と同じフォームを使い回せる！
	    }
	    // 編集された本の保存
	    @PostMapping("/{id}")
	    public String saveBook(@PathVariable Long id, @ModelAttribute Book book) {
	        book.setId(id); // IDを設定
	        bookRepository.save(book);
	        return "redirect:/books"; // 編集後は一覧ページにリダイレクト
	    }

	    // 書籍の保存処理
	    @PostMapping("/save")
	    public String saveBook(@ModelAttribute Book book) {
	        bookRepository.save(book);
	        return "redirect:/books";
	    }
	    @PostMapping("/books/update/{id}")
	    public String updateBook(@PathVariable Long id, @ModelAttribute Book book) {
	        book.setId(id); // 上書き
	        bookRepository.save(book);
	        return "redirect:/books";
	    }

	    @GetMapping("/books/delete/{id}")
	    public String deleteBook(@PathVariable Long id) {
	        bookRepository.deleteById(id);
	        return "redirect:/books";
	    }


}
