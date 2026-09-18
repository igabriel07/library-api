package com.library.service;

import com.library.dto.MemberRequest;
import com.library.dto.MemberResponse;
import com.library.model.Member;
import com.library.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<MemberResponse> getAllMembers() {
        return memberRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public MemberResponse getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        return toResponse(member);
    }

    public MemberResponse createMember(MemberRequest request) {
        Member member = new Member();

        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setEmail(request.getEmail());
        member.setPhone(request.getPhone());
        member.setRegistrationDate(request.getRegistrationDate());

        Member savedMember = memberRepository.save(member);

        return toResponse(savedMember);
    }

    private MemberResponse toResponse(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                member.getPhone(),
                member.getRegistrationDate()
        );
    }
}