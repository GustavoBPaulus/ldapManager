package br.edu.ifrs.ibiruba.ldapmanager.controllers.viewControllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrs.ibiruba.ldapmanager.dtos.AlertDTO;
import br.edu.ifrs.ibiruba.ldapmanager.dtos.PesquisaDto;
import br.edu.ifrs.ibiruba.ldapmanager.entities.Servidor;
import br.edu.ifrs.ibiruba.ldapmanager.services.CsvExportServidoresService;
import br.edu.ifrs.ibiruba.ldapmanager.services.ServidorCrudService;
import br.edu.ifrs.ibiruba.ldapmanager.useful.CriptografiaUtil;
import br.edu.ifrs.ibiruba.ldapmanager.validators.ServidorValidator;

@Controller
@RequestMapping("/servidores")
public class ServidorController {

	    @Autowired
	    private ServidorCrudService servidorCrudService;

	    @Autowired
	    private ServidorValidator servidorValidador;
	        
	    List<Servidor> ultimaListaDeServidores = new ArrayList<Servidor>();

	    @InitBinder("servidor")
	    public void initBinder(WebDataBinder binder) {
	        binder.addValidators(servidorValidador);
	    }
	    
	    @GetMapping
	    public ModelAndView home() {
	    	ultimaListaDeServidores = servidorCrudService.findAll();
	        ModelAndView modelAndView = new ModelAndView("servidor/home");
	        modelAndView.addObject("pesquisaDto", new PesquisaDto());
	        modelAndView.addObject("servidores", ultimaListaDeServidores);
	        
	        return modelAndView;
	    }
	    
	    @PostMapping
	    public String pesquisar(PesquisaDto pesquisaDto, Model model) {
	      //  ModelAndView modelAndView = new ModelAndView("servidor/home");
	        
	        ultimaListaDeServidores = servidorCrudService.findByCnStatusAndTipoServidor(pesquisaDto.getPesquisa(), pesquisaDto.getStatus(), pesquisaDto.getTipoServidor());
	        
	        	model.addAttribute("servidores", ultimaListaDeServidores);
	        	if(pesquisaDto.isGerarCsv())
	        		return "redirect:/servidores/gerarcsv";
	        
	        	return "servidor/home";
	    }

	    @GetMapping("/{cpf}")
	    public ModelAndView detalhes(@PathVariable String cpf) {
	        ModelAndView modelAndView = new ModelAndView("servidor/detalhes");

	        modelAndView.addObject("servidor", servidorCrudService.findByCpf(cpf));

	        return modelAndView;
	    }

	    @GetMapping("/cadastrar")
	    public ModelAndView cadastrar() {
	        ModelAndView modelAndView = new ModelAndView("servidor/formulario");

	        modelAndView.addObject("servidor", new Servidor());
	        modelAndView.addObject("perfis", servidorCrudService.retornaPerfis());
	        
	        return modelAndView;
	    }

	    @GetMapping("/{cpf}/editar")
	    public ModelAndView editar(@PathVariable String cpf) {
	        ModelAndView modelAndView = new ModelAndView("servidor/formulario");

	        modelAndView.addObject("servidor", servidorCrudService.findByCpf(cpf));
	        modelAndView.addObject("perfis", servidorCrudService.retornaPerfis());
	        
	        return modelAndView;
	    }

	    @PostMapping("/cadastrar")
	    public String cadastrar(@Valid Servidor servidor, BindingResult resultado, ModelMap model, RedirectAttributes attrs) {
	        
	    	if (resultado.hasErrors()) {
	        	 attrs.addFlashAttribute("alert", new AlertDTO(resultado.getFieldError().toString(), "alert-warning"));

				 return "servidor/formulario";
	        }
	        
	        //System.out.println("cpf digitado para o servidor: "+servidor.getLogin());
	        servidor.setSenha(CriptografiaUtil.encriptar(servidor.getLogin()+"@ibiruba.ifrs"));

	        servidorCrudService.save(servidor);
	        //attrs.addFlashAttribute("alert", new AlertDTO("Servidor cadastrado com sucesso!", "alert-success"));
	        attrs.addFlashAttribute("alert", new AlertDTO("Senha temporária é: "+servidor.getLogin()+"@ibiruba.ifrs" , "alert-success"));
	        servidorCrudService.emailDeNovoUsuário(servidor);
	        return "redirect:/servidores";
	    }

	    @PostMapping("/{cpf}/editar")
	    public String editar(@Valid Servidor servidor, BindingResult resultado, @PathVariable String cpf, ModelMap model, RedirectAttributes attrs) {
	    
	    	if (resultado.hasErrors()) {
	        	 attrs.addFlashAttribute("alert", new AlertDTO(resultado.getFieldError().toString(), "alert-warning"));
	            return "servidor/formulario";
	        }
	        
	        servidor.setSenha(servidor.getSenha());
	        servidorCrudService.save(servidor);
	        attrs.addFlashAttribute("alert", new AlertDTO("Servidor editado com sucesso!", "alert-success"));

	        return "redirect:/servidores";
	    }

	    @GetMapping("/{cpf}/excluir")
	    public String excluir(@PathVariable String cpf, RedirectAttributes attrs) {
	    		Servidor servidor = servidorCrudService.findByCpf(cpf);
	            servidorCrudService.delete(servidor);
	            attrs.addFlashAttribute("alert", new AlertDTO("Servidor excluído com sucesso!", "alert-success"));
	      
	        return "redirect:/servidores";
	    }
	
	    @GetMapping("/{cpf}/inativar")
	    public String inativar(@PathVariable String cpf, RedirectAttributes attrs) {
	    		Servidor servidor = servidorCrudService.findByCpf(cpf);
	    		servidorCrudService.inativar(servidor);
	    		//servidorCrudService.delete(servidor);
	            attrs.addFlashAttribute("alert", new AlertDTO("Servidor inativado com sucesso na base e no ldap!", "alert-success"));
	      
	        return "redirect:/servidores";
	    }
	    
	    @GetMapping("/{cpf}/ativar")
	    public String ativar(@PathVariable String cpf, RedirectAttributes attrs) {
	    		Servidor servidor = servidorCrudService.findByCpf(cpf);
	    		servidorCrudService.ativar(servidor);
	    		//servidorCrudService.delete(servidor);
	            attrs.addFlashAttribute("alert", new AlertDTO("Servidor ativado com sucesso na base e no ldap!", "alert-success"));
	      
	        return "redirect:/servidores";
	    }
	
	
	    @GetMapping("/{cpf}/reset")
	    public String reset(@PathVariable String cpf, RedirectAttributes attrs) {
	    	Servidor servidor = servidorCrudService.findByCpf(cpf);
	    	servidorCrudService.resetarSenhaServidor(servidor);
	    	
	    	attrs.addFlashAttribute("alert", new AlertDTO("Senha resetada para: "+servidor.getLogin()+"@ibiruba.ifrs", "alert-success"));
	      
	        return "redirect:/servidores";
	    }
	    
	    @GetMapping("/gerarcsv")
	    public void getAllEmployeesInCsv(HttpServletResponse servletResponse) throws IOException {
	        servletResponse.setContentType("text/csv");
	        servletResponse.addHeader("Content-Disposition","attachment; filename=\"servidores.csv\"");
	       new  CsvExportServidoresService().writeEmployeesToCsv(servletResponse.getWriter(), ultimaListaDeServidores);
	    }
	
}
