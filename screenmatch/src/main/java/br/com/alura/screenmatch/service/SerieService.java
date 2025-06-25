package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.model.dto.EpisodioDto;
import br.com.alura.screenmatch.model.dto.SerieDto;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {

    @Autowired
    private SerieRepository repositorio;

    public List<SerieDto> obterTodasAsSeries() {
        return converteDados(repositorio.findAll());
    }

    public List<SerieDto> obterTop5Series() {
        return converteDados(repositorio.findTop5ByOrderByAvaliacaoDesc());

    }

    public List<SerieDto> obterLancamentos() {
        return converteDados(repositorio.encontrarEpisodiosMaisRecentes());
    }

    private List<SerieDto> converteDados(List<Serie> series) {
        return series.stream()
                .map(s -> new SerieDto(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero()
                        , s.getAtores(), s.getPoster(), s.getSinopse()))
                .collect(Collectors.toList());
    }


    public SerieDto obterPorId(Long id) {
        Optional<Serie> serie = repositorio.findById(id);
        Serie s = serie.get();

        if (serie.isPresent()) {
            return new SerieDto(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero()
                    , s.getAtores(), s.getPoster(), s.getSinopse());
        }
        return null;

    }

    public List<EpisodioDto> obterTodasTemporadas(Long id) {
        Optional<Serie> serie = repositorio.findById(id);
        Serie s = serie.get();

        if (serie.isPresent()) {
            return s.getEpisodios().stream()
                    .map(e -> new EpisodioDto(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public List<EpisodioDto> obterTemporadasPorNumero(Long id, Long numero) {

        return repositorio.obterEpisodiosPorTemporada(id, numero)
                .stream()
                .map(e -> new EpisodioDto(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
                .collect(Collectors.toList());
    }

    public List<SerieDto> obterSeriesPorCategoria(String nomeGenero) {
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        return converteDados(repositorio.findByGenero(categoria));
    }

    private List<EpisodioDto> converteDadosEpisodios(List<Episodio> episodios) {
        return episodios.stream()
                .map(e -> new EpisodioDto(
                        e.getTemporada(),
                        e.getNumeroEpisodio(),
                        e.getTitulo()))
                .collect(Collectors.toList());
    }


    public List<EpisodioDto> obterTop5Episodios(Long id) {
        return  converteDadosEpisodios(repositorio.top5EpisodiosPorId(id));
    }


}
