
(function() { // avoid variables ending up in the global scope

  document.getElementById("loginbutton").addEventListener('click', (e) => {
	let errorMessage;
    var form = e.target.closest("form");
	while(document.getElementById("errormessage").firstChild){
		document.getElementById("errormessage").removeChild(document.getElementById("errormessage").firstChild);
	}
    if (form.checkValidity()) {
      makeCall("POST", 'GetAccessRIA', e.target.closest("form"),
        function(req) {
          if (req.readyState == XMLHttpRequest.DONE) {
            switch (req.status) {
              case 200:
				 let user=JSON.parse(req.responseText);
            	 sessionStorage.setItem('username', user.personId);
				 if(user.accessRights=='Student') window.location.href = "HomeStudent.html";
				 if(user.accessRights=='Professor') window.location.href = "HomeRIA.html";
                 break;
              default: // error
				 errorMessage = document.createTextNode(req.responseText);
				 document.getElementById("errormessage").appendChild(errorMessage);
				 break;
            }
          }
        }
      );
    } else {
    	 form.reportValidity();
    }
  });

})();