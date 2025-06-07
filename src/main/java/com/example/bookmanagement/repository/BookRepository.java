package com.example.bookmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bookmanagement.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

}