package com.library.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.library.dto.LoanRequest;
import com.library.dto.LoanResponse;
import com.library.exception.ResourceNotFoundException;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Member;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @InjectMocks
    private LoanService loanService;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BookRepository bookRepository;

    @Test
    void shouldCreateLoan() {

        LoanRequest request = new LoanRequest();
        request.setLoanDate(LocalDate.of(2026, 9, 22));
        request.setDueDate(LocalDate.of(2026, 10, 6));
        request.setMemberId(1L);
        request.setBookId(1L);

        Member member = new Member();
        member.setId(1L);

        Book book = new Book();
        book.setId(1L);
        book.setAvailableCopies(5);

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(member));

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        Loan savedLoan = new Loan();
        savedLoan.setId(1L);
        savedLoan.setLoanDate(request.getLoanDate());
        savedLoan.setDueDate(request.getDueDate());
        savedLoan.setMember(member);
        savedLoan.setBook(book);

        when(loanRepository.save(org.mockito.ArgumentMatchers.any(Loan.class)))
                .thenReturn(savedLoan);

        LoanResponse response = loanService.createLoan(request);

        assertEquals(1L, response.getId());
        assertEquals(1L, response.getMemberId());
        assertEquals(1L, response.getBookId());
        assertEquals(4, book.getAvailableCopies());
    }

    @Test
    void shouldRejectLoanWhenNoCopiesAreAvailable() {

        LoanRequest request = new LoanRequest();
        request.setLoanDate(LocalDate.of(2026, 9, 22));
        request.setDueDate(LocalDate.of(2026, 10, 6));
        request.setMemberId(1L);
        request.setBookId(1L);

        Member member = new Member();
        member.setId(1L);

        Book book = new Book();
        book.setId(1L);
        book.setAvailableCopies(0);

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(member));

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        assertThrows(IllegalArgumentException.class, () -> {
            loanService.createLoan(request);
        });
    }

    @Test
	void shouldRejectLoanWhenDueDateIsBeforeLoanDate() {

		LoanRequest request = new LoanRequest();
		request.setLoanDate(LocalDate.of(2026, 10, 6));
		request.setDueDate(LocalDate.of(2026, 9, 22));
		request.setMemberId(1L);
		request.setBookId(1L);

		assertThrows(IllegalArgumentException.class, () -> {
			loanService.createLoan(request);
		});
	}

	@Test
	void shouldThrowExceptionWhenMemberDoesNotExist() {

		LoanRequest request = new LoanRequest();
		request.setLoanDate(LocalDate.of(2026, 9, 22));
		request.setDueDate(LocalDate.of(2026, 10, 6));
		request.setMemberId(999L);
		request.setBookId(1L);

		when(memberRepository.findById(999L))
				.thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			loanService.createLoan(request);
		});
	}

	@Test
	void shouldThrowExceptionWhenBookDoesNotExist() {

		LoanRequest request = new LoanRequest();
		request.setLoanDate(LocalDate.of(2026, 9, 22));
		request.setDueDate(LocalDate.of(2026, 10, 6));
		request.setMemberId(1L);
		request.setBookId(999L);

		Member member = new Member();
		member.setId(1L);

		when(memberRepository.findById(1L))
				.thenReturn(Optional.of(member));

		when(bookRepository.findById(999L))
				.thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			loanService.createLoan(request);
		});
	}

	@Test
	void shouldReturnLoan() {

		Member member = new Member();
		member.setId(1L);

		Book book = new Book();
		book.setId(1L);
		book.setAvailableCopies(4);

		Loan loan = new Loan();
		loan.setId(1L);
		loan.setLoanDate(LocalDate.of(2026, 9, 22));
		loan.setDueDate(LocalDate.of(2026, 10, 6));
		loan.setMember(member);
		loan.setBook(book);
		loan.setStatus(com.library.model.LoanStatus.ACTIVE);

		when(loanRepository.findById(1L))
				.thenReturn(Optional.of(loan));

		when(loanRepository.save(loan))
				.thenReturn(loan);

		LoanResponse response = loanService.returnLoan(1L);

		assertEquals(com.library.model.LoanStatus.RETURNED, loan.getStatus());
		assertEquals(5, book.getAvailableCopies());
		assertEquals(1L, response.getId());

		verify(loanRepository).save(loan);
	}

	@Test
	void shouldRejectReturningAlreadyReturnedLoan() {

		Member member = new Member();
		member.setId(1L);

		Book book = new Book();
		book.setId(1L);
		book.setAvailableCopies(5);

		Loan loan = new Loan();
		loan.setId(1L);
		loan.setLoanDate(LocalDate.of(2026, 9, 22));
		loan.setDueDate(LocalDate.of(2026, 10, 6));
		loan.setReturnDate(LocalDate.of(2026, 9, 25));
		loan.setMember(member);
		loan.setBook(book);
		loan.setStatus(com.library.model.LoanStatus.RETURNED);

		when(loanRepository.findById(1L))
				.thenReturn(Optional.of(loan));

		assertThrows(IllegalArgumentException.class, () -> {
			loanService.returnLoan(1L);
		});
	}

	@Test
	void shouldThrowExceptionWhenReturningLoanThatDoesNotExist() {

		when(loanRepository.findById(999L))
				.thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			loanService.returnLoan(999L);
		});
	}
}