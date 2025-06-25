package br.com.alura.screenmatch.principal;
//"https://www.omdbapi.com/?t=gilmore+girls&apikey=6585022c"

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Optional<Serie> serieBusca;
    private Scanner scanner = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY_2 = "API_KEY_2";
    private List<DadosSerie> dadosSeries = new ArrayList<>();

    private SerieRepository repositorio;
    private List<Serie> series = new ArrayList<>();

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }


    public void exibirMenu() {
        var opcao = -1;

        while (opcao != 0) {
            var menu = """
                    ************Séries***********\n
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar série por título
                    5 - Buscar série por ator
                    6 - Top 5 Séries
                    7 - Buscar séries por categoria
                    8 - Filtrar série
                    9 - Buscar episódio por trecho
                    10 - Top 5 episódios por série
                    11 - Buscar episódios a partir de uma data
                    
                    0 - Sair
                    """;

            System.out.println(menu);
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSerieBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriePorCategoria();
                    break;
                case 8:
                    filtraSeriePorRequisito();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    topEpisodioPorSerie();
                    break;
                case 11:
                    buscarEpisodiosDepoisDeUmaData();
                    break;
                case 0:
                    System.out.println("Saindo do programa...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }

        }

    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        //dadosSeries.add(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Informe o nome da série para busca:");
        var nomeSerie = scanner.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY_2);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSerieBuscadas();
        System.out.println("Informe a serie que deseja buscar pelo nome:");
        var nomeSerie = scanner.nextLine().trim();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serie.isPresent()) {
            var serieEncontrada = serie.get();

            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY_2);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        } else {
            System.out.println("Serie não encontrada!");
        }

    }

    private void listarSerieBuscadas() {
        List<Serie> series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = scanner.nextLine();

        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
        if (serieBusca.isPresent()) {
            System.out.println("Dados série: " + serieBusca.get());
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void buscarSeriePorAtor() {
        System.out.println("Qual o nome para a busca?");
        var nomeAtor = scanner.nextLine();
        System.out.println("Informe a avaliação para a busca:");
        var avaliacao = scanner.nextDouble();

        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingAndAvaliacaoGreaterThanEqualIgnoreCase(nomeAtor, avaliacao);
        System.out.println("Series em que: " + nomeAtor + " trabalhou: ");
        seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }

    private void buscarTop5Series() {
        List<Serie> serieTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        serieTop.forEach(s -> {
            System.out.println(s.getTitulo() + " avaliação " + s.getAvaliacao());
        });
    }

    private void buscarSeriePorCategoria() {
        System.out.println("Qual série deseja buscar por gênero?");
        var nomeGenero = scanner.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Séries da categoria: " + nomeGenero);
        seriesPorCategoria.forEach(System.out::println);
    }

    private void filtraSeriePorRequisito() {
        System.out.println("Informe a quantidade de temporadas da série:");
        var quantidadeTemporada = scanner.nextInt();
        System.out.println("Informe a avaliação da série");
        var avaliacao = scanner.nextDouble();
        scanner.nextLine();

        List<Serie> filtraSerie = repositorio.seriesPorTemporadaEAvalicao(quantidadeTemporada, avaliacao);
        System.out.println("Quantidades de temporada: " + quantidadeTemporada );
       filtraSerie.forEach(t ->{
           System.out.println("Serie: " + t.getTitulo() + " | Total de temporadas: " + t.getTotalTemporadas() + " | avaliação: " + t.getAvaliacao());
       });
    }

    private void buscarEpisodioPorTrecho() {
        System.out.println("Qual o nome para o episódio deseja buscar?");
        var nomeTrecho = scanner.nextLine();
        List<Episodio> episodioEncontrado = repositorio.episodioPorTrecho(nomeTrecho);
        episodioEncontrado.forEach(e -> System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                e.getSerie().getTitulo(),e.getTemporada(),
                e.getNumeroEpisodio(), e.getTitulo()));
    }

    private void topEpisodioPorSerie() {
        buscarSeriePorTitulo();
        if (serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie);
            topEpisodios.forEach(e -> System.out.printf("Serie: %s Temporada %s - Episódio %s  %s - Avaliação: %s\n",
            e.getSerie().getTitulo(), e.getTemporada(),
                    e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));

        }
    }

    private void buscarEpisodiosDepoisDeUmaData() {
        buscarSeriePorTitulo();
        if (serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            System.out.println("Informe o ano que deseja buscar lançamento do episódio:");
            var anoLancamento = scanner.nextInt();
            scanner.nextLine();

            List<Episodio> episodiosAno = repositorio.episodiosPorSerieAno(serie, anoLancamento);
            episodiosAno.forEach(System.out::println);
        }
    }

}


