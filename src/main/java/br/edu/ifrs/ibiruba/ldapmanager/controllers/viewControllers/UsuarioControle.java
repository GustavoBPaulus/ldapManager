package br.edu.ifrs.ibiruba.ldapmanager.controllers.viewControllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrs.ibiruba.ldapmanager.dtos.AlertDTO;
import br.edu.ifrs.ibiruba.ldapmanager.dtos.AlterarSenhaDTO;
import br.edu.ifrs.ibiruba.ldapmanager.entities.Servidor;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.ServidorRepository;
import br.edu.ifrs.ibiruba.ldapmanager.useful.CriptografiaUtil;


@Controller
public class UsuarioControle {

    @Autowired
    private ServidorRepository servidorRepository;
    
    @Autowired
    private CriptografiaUtil passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "usuario/login";
    }

    @GetMapping("/perfil")
    public ModelAndView perfil(Principal principal) {
        ModelAndView modelAndView = new ModelAndView("usuario/perfil");

        Servidor servidor = servidorRepository.findBycn(principal.getName()).get();
        modelAndView.addObject("usuario", servidor);
        modelAndView.addObject("alterarSenhaForm", new AlterarSenhaDTO());

        return modelAndView;
    }

    @PostMapping("/alterar-senha")
    public String alterarSenha(AlterarSenhaDTO form, Principal principal, RedirectAttributes attrs) {
        Servidor servidor = servidorRepository.findBycn(principal.getName()).get();

        if (passwordEncoder.matches(form.getSenhaAtual(), servidor.getSenha())) {
            	servidor.setSenha(passwordEncoder.encode(form.getNovaSenha()));

            servidorRepository.save(servidor);

            attrs.addFlashAttribute("alert", new AlertDTO("Senha alterada com sucesso!", "alert-success"));
        } else {
            attrs.addFlashAttribute("alert", new AlertDTO("Senha atual está incorreta!", "alert-danger"));
        }

        return "redirect:/perfil";
    }

}
