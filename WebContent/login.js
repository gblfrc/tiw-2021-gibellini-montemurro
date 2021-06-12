
(function() { // avoid variables ending up in the global scope

	document.getElementById("loginbutton").addEventListener('click', (e) => {
		e.preventDefault();
		var form = e.target.closest("form");
		removeError();
		if (form.checkValidity()) {
			makeCall("POST", 'GetAccessRIA', new FormData(e.target.closest("form")),
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
								document.querySelector("div.main").appendChild(errorManager(req));
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