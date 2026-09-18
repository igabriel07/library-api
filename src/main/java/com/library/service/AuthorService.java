package com.library.service;

import com.library.dto.AuthorRequest;
import com.library.dto.AuthorResponse;
import com.library.model.Author;
import com.library.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<AuthorResponse> getAllAuthors() {
        return authorRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AuthorResponse getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        return toResponse(author);
    }

    public AuthorResponse createAuthor(AuthorRequest request) {
        Author author = new Author();

        author.setFirstName(request.getFirstName());
        author.setLastName(request.getLastName());
        author.setBiography(request.getBiography());

        Author savedAuthor = authorRepository.save(author);

        return toResponse(savedAuthor);
    }

    private AuthorResponse toResponse(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getFirstName(),
                author.getLastName(),
                author.getBiography()
        );
    }
}