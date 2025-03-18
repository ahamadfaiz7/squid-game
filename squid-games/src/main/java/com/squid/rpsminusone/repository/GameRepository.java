package com.squid.rpsminusone.repository;

import com.squid.rpsminusone.entity.Game;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GameRepository extends CrudRepository<Game, Long> {
    Optional<Game> findByPlayerOneAndPlayerTwoIsNull(String playerOne);
}
