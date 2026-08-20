package com.highconcurrency.ticketing.presentation;

import com.highconcurrency.ticketing.application.usecase.concert.ConcertCreateRequest;
import com.highconcurrency.ticketing.application.usecase.concert.ConcertResponse;
import com.highconcurrency.ticketing.application.usecase.concert.ConcertUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/concerts")
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertUseCase concertUseCase;

    @PostMapping
    public ResponseEntity<Long> createConcert(@RequestBody ConcertCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(concertUseCase.createConcert(request));
    }

    @GetMapping("/{concertId}")
    public ConcertResponse getConcert(@PathVariable Long concertId) {
        return concertUseCase.getConcertResponse(concertId);
    }

    @GetMapping
    public List<ConcertResponse> getConcertList() {
        return concertUseCase.getConcertList();
    }

    @DeleteMapping("/{concertId}")
    public ResponseEntity<Void> deleteConcert(@PathVariable Long concertId) {
        concertUseCase.delete(concertId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
