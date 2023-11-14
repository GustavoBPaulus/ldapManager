package br.edu.ifrs.ibiruba.ldapmanager.exceptions;

import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@ControllerAdvice
public class GerenciadorDeExcecoes implements ErrorViewResolver {


    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
        ModelAndView modelAndView = new ModelAndView("problema");
        modelAndView.addObject("status", status.value());
        switch (status.value()){
            case 404:
                modelAndView.addObject("titulo", "Página não encontrada");
                modelAndView.addObject("mensagem", "A página que você procura não existe!");
                modelAndView.addObject("causa", "A url para a página '" + model.get("path") +
                        "' não existe.");
                modelAndView.addObject("cssClass","text-warning");
                break;

            case 500:
                modelAndView.addObject("titulo", "erro interno");
                modelAndView.addObject("mensagem", "Alguma coisa deu errado");
                modelAndView.addObject("causa", "Ocorreu um erro inesperado, tente mais tarde.");
                modelAndView.addObject("cssClass","text-warning");
                break;
        }

        return modelAndView;
    }
}
