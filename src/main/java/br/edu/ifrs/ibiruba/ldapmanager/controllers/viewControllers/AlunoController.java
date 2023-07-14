package br.edu.ifrs.ibiruba.ldapmanager.controllers.viewControllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import br.edu.ifrs.ibiruba.ldapmanager.entities.AlunoCurso;
import br.edu.ifrs.ibiruba.ldapmanager.services.AlunoCursoService;
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
import br.edu.ifrs.ibiruba.ldapmanager.dtos.AlunoDTO;
import br.edu.ifrs.ibiruba.ldapmanager.dtos.PesquisaAlunoDto;
import br.edu.ifrs.ibiruba.ldapmanager.entities.Aluno;
import br.edu.ifrs.ibiruba.ldapmanager.services.AlunoCrudService;
import br.edu.ifrs.ibiruba.ldapmanager.services.CsvExportaAlunosService;
import br.edu.ifrs.ibiruba.ldapmanager.useful.CriptografiaUtil;
import br.edu.ifrs.ibiruba.ldapmanager.validators.AlunoDtoValidator;

@Controller
@RequestMapping("/alunos")
public class AlunoController {

	@Autowired
	private AlunoCrudService alunoCrudService;

	@Autowired
	private AlunoCursoService alunoCursoService;

	@Autowired
	private AlunoDtoValidator alunoValidador;

	List<Aluno> ultimaListaDeAlunos = new ArrayList<Aluno>();

	@InitBinder("alunoDTO")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(alunoValidador);
	}

	@GetMapping
	public ModelAndView home() {
		ultimaListaDeAlunos = alunoCrudService.findAll();
		ModelAndView modelAndView = new ModelAndView("aluno/home");
		modelAndView.addObject("pesquisaAlunoDto", new PesquisaAlunoDto());
		modelAndView.addObject("tiposDeAlunos", alunoCrudService.retornaTiposDeAlunos());
		modelAndView.addObject("alunos", ultimaListaDeAlunos);
		return modelAndView;
	}

	@PostMapping
	public String pesquisar(PesquisaAlunoDto pesquisaDto, Model model) {
		// ModelAndView modelAndView = new ModelAndView("Aluno/home");

		ultimaListaDeAlunos = alunoCrudService.findByNomeStatusAndTipoAluno(pesquisaDto.getPesquisa(),
				pesquisaDto.getStatus(), pesquisaDto.getTipoAluno());

		model.addAttribute("alunos", ultimaListaDeAlunos);
		model.addAttribute("tiposDeAlunos", alunoCrudService.retornaTiposDeAlunos());
		if (pesquisaDto.isGerarCsv())
			return "redirect:/alunos/gerarcsv";

		return "aluno/home";
	}

	@GetMapping("/{cpf}")
	public ModelAndView detalhes(@PathVariable String cpf) {
		ModelAndView modelAndView = new ModelAndView("aluno/detalhes");

		modelAndView.addObject("aluno", alunoCrudService.findByCpf(cpf));

		return modelAndView;
	}

	@GetMapping("/cadastrar")
	public ModelAndView cadastrar() {
		ModelAndView modelAndView = new ModelAndView("aluno/formulario");

		modelAndView.addObject("alunoDTO", new AlunoDTO());
		
		modelAndView.addObject("tiposDeAlunos", alunoCrudService.retornaTiposDeAlunos());
		modelAndView.addObject("cursos", alunoCrudService.retornaCursos());

		return modelAndView;
	}

	@GetMapping("/{cpf}/editar")
	public ModelAndView editar(@PathVariable String cpf) {
		ModelAndView modelAndView = new ModelAndView("aluno/formulario");
		modelAndView.addObject("tiposDeAlunos", alunoCrudService.retornaTiposDeAlunos());
		modelAndView.addObject("cursos", alunoCrudService.retornaCursos());
		modelAndView.addObject("alunoDTO", alunoCrudService.converteAlunoParaAlunoDTO(alunoCrudService.findByCpf(cpf)));

		return modelAndView;
	}

	@PostMapping("/cadastrar")
	public String cadastrar(@Valid AlunoDTO alunoDto, BindingResult resultado, ModelMap model, RedirectAttributes attrs) {

		if (resultado.hasErrors()) {
			attrs.addFlashAttribute("alert", new AlertDTO(resultado.getFieldError().toString(), "alert-warning"));
			return "aluno/formulario";
		}

		System.out.println("cpf digitado para o Aluno: " + alunoDto.getLogin());
		alunoDto.setSenha(CriptografiaUtil.encriptar(alunoDto.getLogin() + "@ibiruba.ifrs"));
		alunoCrudService.insert(alunoDto);
		// attrs.addFlashAttribute("alert", new AlertDTO("Aluno cadastrado com
		// sucesso!", "alert-success"));
		attrs.addFlashAttribute("alert",
				new AlertDTO("Senha temporária é: " + alunoDto.getLogin() + "@ibiruba.ifrs", "alert-success"));
	
		return "redirect:/alunos";
	}

	@PostMapping("/{cpf}/editar")
	public String editar(@Valid AlunoDTO alunoDTO, BindingResult resultado, @PathVariable String cpf, ModelMap model,
			RedirectAttributes attrs) {

		if (resultado.hasErrors()) {
			attrs.addFlashAttribute("alert", new AlertDTO(resultado.getFieldError().toString(), "alert-warning"));
			return "redirect:/alunos/{cpf}/editar";
		}

		alunoDTO.setSenha(alunoDTO.getSenha());
		
		alunoCrudService.edit(alunoDTO);
		attrs.addFlashAttribute("alert", new AlertDTO("Aluno editado com sucesso!", "alert-success"));

		return "redirect:/alunos";
	}

	@GetMapping("/{cpf}/excluir")
	public String excluir(@PathVariable String cpf, RedirectAttributes attrs) {
		Aluno aluno = alunoCrudService.findByCpf(cpf);
		if (aluno.getListaCursosAluno() == null || aluno.getListaCursosAluno().isEmpty()) {
			alunoCrudService.deleteAluno(aluno);
			attrs.addFlashAttribute("alert", new AlertDTO("Aluno excluído com sucesso!", "alert-success"));
		} else {
			attrs.addFlashAttribute("alert", new AlertDTO("Aluno possuí matricula(s)", "alert-warning"));
		}

		return "redirect:/alunos";
	}

	@GetMapping("/{cpf}/inativar")
	public String inativar(@PathVariable String cpf, RedirectAttributes attrs) {
		Aluno Aluno = alunoCrudService.findByCpf(cpf);
		alunoCrudService.inativar(Aluno);
		// AlunoCrudService.delete(Aluno);
		attrs.addFlashAttribute("alert",
				new AlertDTO("Aluno inativado com sucesso na base e no ldap!", "alert-success"));

		return "redirect:/alunos";
	}

	@GetMapping("/{cpf}/ativar")
	public String ativar(@PathVariable String cpf, RedirectAttributes attrs) {
		Aluno Aluno = alunoCrudService.findByCpf(cpf);
		alunoCrudService.ativar(Aluno);
		// AlunoCrudService.delete(Aluno);
		attrs.addFlashAttribute("alert", new AlertDTO("Aluno ativado com sucesso na base e no ldap!", "alert-success"));

		return "redirect:/alunos";
	}


	@GetMapping("/{cpf}/reset")
	public String reset(@PathVariable String cpf, RedirectAttributes attrs) {
		Aluno aluno = alunoCrudService.findByCpf(cpf);
		List<AlunoCurso> listaAc = alunoCursoService.findByAluno(aluno);
		AlunoCurso ultimaMatricula = null;
		for(AlunoCurso ac: listaAc){
 			if (ultimaMatricula == null)
					ultimaMatricula = ac;
			else if( (Integer.parseInt(ac.getMatricula()) > Integer.parseInt(ultimaMatricula.getMatricula()))  )
					ultimaMatricula = ac;
		}

			if (ultimaMatricula.getStatusDiscente().equalsIgnoreCase("ATIVO")) {
				alunoCrudService.resetarSenhaAluno(aluno, ultimaMatricula);
				attrs.addFlashAttribute("alert",
						new AlertDTO("Senha resetada para: " + aluno.getLogin() + "@ibiruba.ifrs", "alert-success"));
			}




		return "redirect:/alunos";
	}

	@GetMapping("/gerarcsv")
	public void getAllEmployeesInCsv(HttpServletResponse servletResponse) throws IOException {
		servletResponse.setContentType("text/csv");
		servletResponse.addHeader("Content-Disposition", "attachment; filename=\"Alunos.csv\"");
		new CsvExportaAlunosService().writeEmployeesToCsv(servletResponse.getWriter(), ultimaListaDeAlunos);
	}

}
