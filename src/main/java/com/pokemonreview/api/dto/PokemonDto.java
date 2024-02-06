package com.pokemonreview.api.dto;

import com.pokemonreview.api.models.PokemonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PokemonDto {
    private Integer id;
    private String name;
    private PokemonType type;
}