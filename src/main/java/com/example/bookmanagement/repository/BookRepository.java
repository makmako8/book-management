package com.example.bookmanagement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.User;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
	 // タイトルで部分一致検索（大文字小文字区別なし）
    List<Book> findByTitleContainingIgnoreCase(String title);

    // ジャンルで部分一致検索（大文字小文字区別なし）
    List<Book> findByGenreContainingIgnoreCase(String genre);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    // タイトルとジャンルで検索
    List<Book> findByTitleContainingIgnoreCaseAndGenreContainingIgnoreCase(String title, String genre);
    List<Book> findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(String title, String author);
    List<Book> findByGenreContainingIgnoreCaseAndAuthorContainingIgnoreCase(String genre, String author);
    // タイトル・ジャンル・著者の全てを含む検索
    List<Book> findByTitleContainingIgnoreCaseAndGenreContainingIgnoreCaseAndAuthorContainingIgnoreCase(String title, String genre, String author);
    Page<Book> findByUser(User user, Pageable pageable);
    Page<Book> findAll(Pageable pageable);
    Page<Book> findByUserAndTitleContainingIgnoreCase(User user, String title, Pageable pageable);
    Page<Book> findByUserAndGenreContainingIgnoreCase(User user, String genre, Pageable pageable);
    Page<Book> findByUserAndAuthorContainingIgnoreCase(User user, String author, Pageable pageable);
    Page<Book> findByUserAndTitleContainingIgnoreCaseAndGenreContainingIgnoreCaseAndAuthorContainingIgnoreCase(
            User user, String title, String genre, String author, Pageable pageable);
}