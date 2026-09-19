package com.library.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.library.dto.LoanRequest;
import com.library.dto.LoanResponse;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.LoanStatus;
import com.library.model.Member;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.MemberRepository;
import com.library.exception.ResourceNotFoundException;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    public LoanService(LoanRepository loanRepository,
                       MemberRepository memberRepository,
                       BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
    }

    public List<LoanResponse> getAllLoans() {
        return loanRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public LoanResponse getLoanById(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        return toResponse(loan);
    }

    public LoanResponse createLoan(LoanRequest request) {

        if (request.getDueDate().isBefore(request.getLoanDate())) {
            throw new IllegalArgumentException(
                    "Due date cannot be before loan date");
        }

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new IllegalArgumentException("No available copies");
        }

        Loan loan = new Loan();

        loan.setLoanDate(request.getLoanDate());
        loan.setDueDate(request.getDueDate());
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setMember(member);
        loan.setBook(book);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        Loan savedLoan = loanRepository.save(loan);

        return toResponse(savedLoan);
    }
    
    public LoanResponse returnLoan(Long id) {

        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new IllegalArgumentException("Loan already returned");
        }

        loan.setReturnDate(java.time.LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);

        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);

        bookRepository.save(book);

        Loan savedLoan = loanRepository.save(loan);

        return toResponse(savedLoan);
    }

    private LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getLoanDate(),
                loan.getDueDate(),
                loan.getReturnDate(),
                loan.getStatus(),
                loan.getMember().getId(),
                loan.getBook().getId()
        );
    }
}