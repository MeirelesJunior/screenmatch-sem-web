package br.com.alura.screenmatch.controller;

import br.com.alura.screenmatch.model.dto.EpisodioDto;
import br.com.alura.screenmatch.model.dto.SerieDto;
import br.com.alura.screenmatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;


@RestController
@RequestMapping("/series")
public class SerieController {

    @Autowired
    private SerieService servico;


    @GetMapping
    public List<SerieDto> obterDadosSerie() {
        return servico.obterTodasAsSeries();
    }

    @GetMapping("/top5")
    public List<SerieDto> obterTop5Series() {
        return servico.obterTop5Series();
    }

    @GetMapping("/lancamentos")
    public List<SerieDto> obterLancamentos(){
        return servico.obterLancamentos();
    }

    @GetMapping("/{id}")
    public SerieDto obterId(@PathVariable Long id){
        return  servico.obterPorId(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodioDto> obterTodasTemporadas(@PathVariable Long id){
        return  servico.obterTodasTemporadas(id);

    }

    @GetMapping("/{id}/temporadas/{numero}")
    public List<EpisodioDto>obterTemporadasPorNumero(@PathVariable Long id, @PathVariable Long numero){
        return  servico.obterTemporadasPorNumero(id, numero);
    }

    @GetMapping("/categoria/{nomeGenero}")
    public List<SerieDto> obterSeriesPorCategoria(@PathVariable String nomeGenero){
        return  servico.obterSeriesPorCategoria(nomeGenero);
    }

    @GetMapping("/{id}/temporadas/top")
    public List<EpisodioDto> obterTop5Episodios(@PathVariable Long id){
        return servico.obterTop5Episodios(id);
    }


}
