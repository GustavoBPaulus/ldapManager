/**
 * 
 */
var host = document.location.host;

function isPasswordChanged(){
	var passwordChanged = $('#passwordChanged').val();
	
	if (passwordChanged) {
				alert('senha enviada para o seu e-mail');
			}
			else {
				alert('usuário não encontrado, ou email não vinculado ao usuário, contate a equipe de TI');
			}
}

function changePassword() {
	var alterMyPasswordModel = new Object();
	alterMyPasswordModel.user = $("#user").val();
	alterMyPasswordModel.actualPassword = $('#inputPassword').val();
	alterMyPasswordModel.newPassword = $('#inputNewPassword').val();
	alterMyPasswordModel.typeOfUser = $('#selectTypeOfUser').val();

	var alterMyPasswordModelJson = JSON.stringify(alterMyPasswordModel);
	if (validationPassword(alterMyPasswordModel.newPassword)) {
		$.ajax({
			type: 'POST',
			contentType: 'application/json',
			url: 'http://' + host + '/userpasswordmanager/changepassword',
			dataType: "json",
			data: alterMyPasswordModelJson,
			cache: false,
			async: false,
			success: function(response) {
				console.log(response);
				if (response) {
					alert('senha alterada com sucesso');
				}
				else {
					alert('usuário ou senha incorreto');
				}


			},
			error: function(jqXHR, textStatus, errorThrown) {
				$('#divError').removeClass('messageOff');
				alert('erro ao alterar a senha');
				// ////alert('Erro criando contato: ' + jqXHR.responseText);
			}
		});
	} else{
		alert('sua senha não seguiu as regras de complexidade');
	}
}


function resetPassword() {
	var alterMyPasswordModel = new Object();
	alterMyPasswordModel.user = $("#usernameModal").val();
	alterMyPasswordModel.typeOfUser = $("#selectTypeOfUserModal").val();

	var alterMyPasswordModelJson = JSON.stringify(alterMyPasswordModel);

	$.ajax({
		type: 'POST',
		contentType: 'application/json',
		url: 'http://' + host + '/userpasswordmanager/forgot-password',
		dataType: "json",
		data: alterMyPasswordModelJson,
		cache: false,
		async: false,
		success: function(response) {
			console.log(response);
			if (response) {
				alert('senha enviada para o seu e-mail');
			}
			else {
				alert('usuário não encontrado, ou email não vinculado ao usuário, contate a equipe de TI');
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$('#divError').removeClass('messageOff');
			alert('erro ao resetar a senha');
			// ////alert('Erro criando contato: ' + jqXHR.responseText);
		}
	});
}


function isPasswordReseted(){
	var passwordChanged = $('#passwordChanged').val();
	
	if (passwordChanged) {
				alert('senha enviada para o seu e-mail');
			}
			else {
				alert('usuário não encontrado, ou email não vinculado ao usuário, contate a equipe de TI');
			}
}



function validationPassword(password) {
	//Regular Expressions.
	var regex = new Array();
	regex.push("[A-Z]"); //Uppercase Alphabet.
	regex.push("[a-z]"); //Lowercase Alphabet.
	regex.push("[0-9]"); //Digit.
	regex.push("[$@$!%*#?&]"); //Special Character.

	var passed = 0;
	//Validate for each Regular Expression.
	for (var i = 0; i < regex.length; i++) {
		if (new RegExp(regex[i]).test(	password)) {
			passed++;
		}
	}
	/*
		   //Validate for length of Password.
		   if (passed > 2 && password.length > 8) {
			   passed++;
		   }
	*/
	console.log('password length: ' +password.length);
	console.log('passed: ' +passed);
	
	
	return passed >= 4 && password.length > 8;
}

$("#inputNewPassword").keyup(function() {
	var newPassword = $('#inputNewPassword').val();
	var btn_alterarsenha = $('#btn_alterarsenha');
	var lbl_warning = $('#lbl_warning');
	if(validationPassword(newPassword)){
		btn_alterarsenha.attr("disabled", false);
		lbl_warning.removeClass('alert-danger');
		lbl_warning.addClass('alert-success');
	}else{
		btn_alterarsenha.attr("disabled", true);
		lbl_warning.removeClass('alert-warning');
		lbl_warning.addClass('alert-danger');
	}
  			
});