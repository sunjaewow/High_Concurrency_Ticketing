package com.highconcurrency.ticketing.presentation;

import com.highconcurrency.ticketing.application.usecase.concert.ConcertCreateRequest;
import com.highconcurrency.ticketing.application.usecase.concert.ConcertResponse;
import com.highconcurrency.ticketing.application.usecase.concert.ConcertUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/concerts")
@RequiredArgsConstructor
@Tag(name = "Concert")
public class ConcertController {

    private final ConcertUseCase concertUseCase;

    @PostMapping
    @Operation(summary = "콘서트 생성")
    public ResponseEntity<Long> createConcert(@RequestBody ConcertCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(concertUseCase.createConcert(request));
    }

    @GetMapping("/{concertId}")
    @Operation(summary = "콘서트 단건 조회")
    public ConcertResponse getConcert(@Parameter(description = "콘서트 ID") @PathVariable Long concertId) {
        return concertUseCase.getConcertResponse(concertId);
    }

    @GetMapping
    @Operation(summary = "콘서트 목록 조회")
    public List<ConcertResponse> getConcertList() {
        return concertUseCase.getConcertList();
    }

    @DeleteMapping("/{concertId}")
    @Operation(summary = "콘서트 삭제")
    public ResponseEntity<Void> deleteConcert(@Parameter(description = "콘서트 ID") @PathVariable Long concertId) {
        concertUseCase.delete(concertId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
