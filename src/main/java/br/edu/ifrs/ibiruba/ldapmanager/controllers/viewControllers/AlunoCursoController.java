package br.edu.ifrs.ibiruba.ldapmanager.controllers.viewControllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import br.edu.ifrs.ibiruba.ldapmanager.entities.Aluno;
import br.edu.ifrs.ibiruba.ldapmanager.services.AlunoCrudService;
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
import br.edu.ifrs.ibiruba.ldapmanager.dtos.PesquisaAlunoDto;
import br.edu.ifrs.ibiruba.ldapmanager.entities.AlunoCurso;
import br.edu.ifrs.ibiruba.ldapmanager.services.AlunoCursoService;
import br.edu.ifrs.ibiruba.ldapmanager.services.CsvExportaAlunosCursoService;
import br.edu.ifrs.ibiruba.ldapmanager.useful.CriptografiaUtil;
import br.edu.ifrs.ibiruba.ldapmanager.validators.AlunoCursoValidator;


@Controller
@RequestMapping("/matriculas")
public class AlunoCursoController {

	@Autowired
	private AlunoCursoService alunoCursoService;
	@Autowired
	private AlunoCrudService alunoCrudService;

	@Autowired
	private AlunoCursoValidator alunoCursoValidator;

	List<AlunoCurso> ultimaListaDeAlunos = new ArrayList<AlunoCurso>();

	@InitBinder("alunoCurso")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(alunoCursoValidator);
	}

	@GetMapping
	public ModelAndView home() {
		ultimaListaDeAlunos = alunoCursoService.findAll();
		ModelAndView modelAndView = new ModelAndView("aluno_curso/home");
		//modelAndView.addObject("alunoCurso", new AlunoCurso());
		modelAndView.addObject("pesquisaAlunoDto", new PesquisaAlunoDto());
		modelAndView.addObject("tiposDeAlunos", alunoCursoService.retornaTiposDeAlunos());
		modelAndView.addObject("alunosCurso", ultimaListaDeAlunos);
		modelAndView.addObject("cursos", alunoCursoService.retornaCursos());
		return modelAndView;
	}

	@PostMapping
	public String pesquisar(PesquisaAlunoDto pesquisaDto, Model model) {
		// ModelAndView modelAndView = new ModelAndView("Aluno/home");

		ultimaListaDeAlunos = alunoCursoService.findByNomeStatusAndCurso(pesquisaDto.getPesquisa(),
				pesquisaDto.getStatus(), pesquisaDto.getCurso());

		model.addAttribute("alunosCurso", ultimaListaDeAlunos);
		model.addAttribute("cursos", alunoCursoService.retornaCursos());
		if (pesquisaDto.isGerarCsv())
			return "redirect:/matriculas/gerarcsv";

		return "/aluno_curso/home";
	}

	@GetMapping("/{matricula}")
	public ModelAndView detalhes(@PathVariable String matricula) {
		ModelAndView modelAndView = new ModelAndView("aluno_curso/detalhes");

		modelAndView.addObject("alunoCurso", alunoCursoService.findByMatricula(matricula));

		return modelAndView;
	}

	@GetMapping("/cadastrar")
	public ModelAndView cadastrar() {
		ModelAndView modelAndView = new ModelAndView("aluno_curso/formulario");

		modelAndView.addObject("alunoCurso", new AlunoCurso());
		
		modelAndView.addObject("tiposDeAlunos", alunoCursoService.retornaTiposDeAlunos());
		modelAndView.addObject("cursos", alunoCursoService.retornaCursos());

		return modelAndView;
	}

	@GetMapping("/{matricula}/editar")
	public ModelAndView editar(@PathVariable String matricula) {
		ModelAndView modelAndView = new ModelAndView("aluno_curso/formulario");
		modelAndView.addObject("tiposDeAlunos", alunoCursoService.retornaTiposDeAlunos());
		modelAndView.addObject("cursos", alunoCursoService.retornaCursos());
		modelAndView.addObject("alunoCurso", alunoCursoService.findByMatricula(matricula));

		return modelAndView;
	}

	@PostMapping( value ={"/cadastrar", "/cadastrar/{cpf}"})
	public String cadastrar(@Valid AlunoCurso alunoCurso, BindingResult resultado, ModelMap model, RedirectAttributes attrs) {

		if (resultado.hasErrors()) {
			attrs.addFlashAttribute("alert", new AlertDTO(resultado.getFieldError().toString(), "alert-warning"));
			attrs.addAttribute("cursos", alunoCursoService.retornaCursos());
			return "aluno_curso/formulario";
		}

		//System.out.println("matricula digitado para o Aluno: " + alunoCurso.getMatricula());
		alunoCurso.getAluno().setSenha(CriptografiaUtil.encriptar(alunoCurso.getAluno().getLogin() + "@ibiruba.ifrs"));
		alunoCursoService.insert(alunoCurso);
		// attrs.addFlashAttribute("alert", new AlertDTO("Aluno cadastrado com
		// sucesso!", "alert-success"));
		attrs.addFlashAttribute("alert",
				new AlertDTO("Senha temporária é: " + alunoCurso.getAluno().getLogin() + "@ibiruba.ifrs", "alert-success"));


		return "redirect:/matriculas";
	}

	@PostMapping("/{matricula}/editar")
	public String editar(@Valid AlunoCurso alunoCurso, BindingResult resultado, @PathVariable String matricula, ModelMap model,
			RedirectAttributes attrs) {

		if (resultado.hasErrors()) {
			attrs.addFlashAttribute("alert", new AlertDTO(resultado.getFieldError().toString(), "alert-warning"));
			attrs.addAttribute("cursos", alunoCursoService.retornaCursos());
			return "redirect:/matriculas/{matricula}/editar";
		}

		alunoCurso.getAluno().setSenha(alunoCurso.getAluno().getSenha());
		
		alunoCursoService.edit(alunoCurso);
		attrs.addFlashAttribute("alert", new AlertDTO("Matricula editada com sucesso!", "alert-success"));

		return "redirect:/matriculas";
	}

	@GetMapping("/{matricula}/excluir")
	public String excluir(@PathVariable String matricula, RedirectAttributes attrs) {
		AlunoCurso alunoCurso = alunoCursoService.findByMatricula(matricula);
	
			alunoCursoService.deleteAlunoCurso(alunoCurso);
			attrs.addFlashAttribute("alert", new AlertDTO("Matricula excluída com sucesso!", "alert-success"));
		
		return "redirect:/matriculas";
	}

	@GetMapping("/{matricula}/inativar")
	public String inativar(@PathVariable String matricula, RedirectAttributes attrs) {
		AlunoCurso alunoCurso = alunoCursoService.findByMatricula(matricula);
		alunoCursoService.inativar(alunoCurso);
		// AlunoCrudService.delete(Aluno);
		attrs.addFlashAttribute("alert",
				new AlertDTO("Matricula inativada com sucesso na base e no ldap!", "alert-success"));

		return "redirect:/matriculas";
	}

	@GetMapping("/{matricula}/ativar")
	public String ativar(@PathVariable String matricula, RedirectAttributes attrs) {
		AlunoCurso alunoCurso = alunoCursoService.findByMatricula(matricula);
		alunoCursoService.ativar(alunoCurso);
		// AlunoCrudService.delete(Aluno);
		attrs.addFlashAttribute("alert", new AlertDTO("Matricula ativada com sucesso na base e no ldap!", "alert-success"));

		return "redirect:/matriculas";
	}
	@GetMapping("/cadastrar/{cpf}")
	public ModelAndView adicionarNovaMatricula(@PathVariable String cpf, RedirectAttributes attrs) {
		Aluno aluno = alunoCrudService.findByCpf(cpf);
		AlunoCurso alunoCurso = new AlunoCurso();
		alunoCurso.setAluno(aluno);
		ModelAndView modelAndView = new ModelAndView("aluno_curso/formulario");

		modelAndView.addObject("alunoCurso", alunoCurso);

		modelAndView.addObject("tiposDeAlunos", alunoCursoService.retornaTiposDeAlunos());
		modelAndView.addObject("cursos", alunoCursoService.retornaCursos());

		return modelAndView;
	}

	@GetMapping("/{matricula}/reset")
	public String reset(@PathVariable String matricula, RedirectAttributes attrs) {
		AlunoCurso ac = alunoCursoService.findByMatricula(matricula);

		
			if (ac.getStatusDiscente().equalsIgnoreCase("ATIVO")) {
				alunoCursoService.resetarSenhaAluno(ac.getAluno(), ac);
				attrs.addFlashAttribute("alert",
						new AlertDTO("Senha resetada para: " + ac.getAluno().getLogin() + "@ibiruba.ifrs", "alert-success"));
			}

		return "redirect:/matriculas";
	}

	@GetMapping("/gerarcsv")
	public void getAllEmployeesInCsv(HttpServletResponse servletResponse) throws IOException {
		servletResponse.setContentType("text/csv");
		servletResponse.addHeader("Content-Disposition", "attachment; filename=\"matriculas.csv\"");
		new CsvExportaAlunosCursoService().exportMatriculasToCsv(servletResponse.getWriter(), ultimaListaDeAlunos);
	}
	

}
