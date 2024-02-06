package com.pokemonreview.api.service.impl;

import com.pokemonreview.api.dto.PageResponse;
import com.pokemonreview.api.dto.PokemonDto;
import com.pokemonreview.api.exceptions.ResourceNotFoundException;
import com.pokemonreview.api.models.Pokemon;
import com.pokemonreview.api.repository.PokemonRepository;
import com.pokemonreview.api.service.PokemonService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PokemonServiceImpl implements PokemonService {
    private PokemonRepository pokemonRepository;

    //Constructor Injection
    public PokemonServiceImpl(PokemonRepository pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
    }

    @Override
    public PokemonDto createPokemon(PokemonDto pokemonDto) {
        //Dto => Entity
        Pokemon pokemon = mapToEntity(pokemonDto);

        Pokemon newPokemon = pokemonRepository.save(pokemon);

        //Entity => Dto
        return mapToDto(newPokemon);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<?> getAllPokemon(int pageNo, int pageSize) {
        Pageable pageable =
                PageRequest.of(pageNo, pageSize, Sort.by("id").descending());

        Page<Pokemon> pokemonPage = pokemonRepository.findAll(pageable);
        List<Pokemon> listOfPokemon = pokemonPage.getContent();

        List<PokemonDto> content = listOfPokemon
                .stream() //Stream<Pokemon>
                //.map(entity -> mapToDto(entity)) //Stream<PokemonDto>
                .map(this::mapToDto)
                //.collect(Collectors.toList());
                .toList(); //List<PokemonDto>

        PageResponse<PokemonDto> pokemonResponse = new PageResponse<>();
        pokemonResponse.setContent(content);
        pokemonResponse.setPageNo(pokemonPage.getNumber());
        pokemonResponse.setPageSize(pokemonPage.getSize());
        pokemonResponse.setTotalElements(pokemonPage.getTotalElements());
        pokemonResponse.setTotalPages(pokemonPage.getTotalPages());
        pokemonResponse.setLast(pokemonPage.isLast());
        pokemonResponse.setFirst(pokemonPage.isFirst());

        return pokemonResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public PokemonDto getPokemonById(int id) {
        Pokemon pokemon = getExistPokemon(id);
        return mapToDto(pokemon);
    }

    private Pokemon getExistPokemon(int id) {
        return pokemonRepository
                .findById(id) //Optional<Pokemon>
                .orElseThrow(() -> new ResourceNotFoundException("Pokemon could not be found"));
    }

    @Override
    public PokemonDto updatePokemon(PokemonDto pokemonDto, int id) {
        Pokemon pokemon = getExistPokemon(id);

        //Entity의 setter method 호출을 해도 update query가 실행됨 ( Dirty Checking )
        if(pokemonDto.getName() != null) pokemon.setName(pokemonDto.getName());
        if(pokemonDto.getType() != null) pokemon.setType(pokemonDto.getType());

        //Pokemon updatedPokemon = pokemonRepository.save(pokemon);
        return mapToDto(pokemon);
    }

    @Override
    public void deletePokemonId(int id) {
        Pokemon pokemon = getExistPokemon(id);
        pokemonRepository.delete(pokemon);
    }

    private PokemonDto mapToDto(Pokemon pokemon) {
        PokemonDto pokemonDto = new PokemonDto();
        pokemonDto.setId(pokemon.getId());
        pokemonDto.setName(pokemon.getName());
        pokemonDto.setType(pokemon.getType());
        return pokemonDto;
    }

    private Pokemon mapToEntity(PokemonDto pokemonDto) {
        Pokemon pokemon = new Pokemon();
        pokemon.setName(pokemonDto.getName());
        pokemon.setType(pokemonDto.getType());
        return pokemon;
    }
}