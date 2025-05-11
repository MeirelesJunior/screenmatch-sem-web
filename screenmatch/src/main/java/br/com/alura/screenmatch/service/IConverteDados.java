package br.com.alura.screenmatch.service;

public interface IConverteDados {
    //<T>Classe generica type parameter
    <T> T obterDados(String json,Class<T> classe);
}
