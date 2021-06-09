
(function() { // avoid variables ending up in the global scope

	document.getElementById("loginbutton").addEventListener('click', (e) => {
		e.preventDefault();
		let errorMessage;
		var form = e.target.closest("form");
		let pError = document.querySelector("p.error");
		if (pError !== null) pError.remove();
		if (form.checkValidity()) {
			makeCall("POST", 'GetAccessRIA', e.target.closest("form"),
				function(req) {
					if (req.readyState == XMLHttpRequest.DONE) {
						switch (req.status) {
							case 200:{
								let user = JSON.parse(req.responseText);
								sessionStorage.setItem('username', user.personId);
								if (user.accessRights == 'Student') window.location.href = "StudentHome.html";
								if (user.accessRights == 'Professor') window.location.href = "ProfessorHome.html";
								break;
								}
							default: {// error
								let error = document.createElement("p");
								error.setAttribute("class","error");
								errorMessage = document.createTextNode(req.responseText);
								error.appendChild(errorMessage);
								document.querySelector("div.main").appendChild(error);
								break;
							}
						}
					}
				}
			);
		} else {
			form.reportValidity();
		}
	});

})();