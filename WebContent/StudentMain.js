(function() {

  var courseList, appealList, gradeDetails, pageOrchestrator , button;

  window.addEventListener("load", () => {
    pageOrchestrator = new PageOrchestrator();
    pageOrchestrator.start();
  });


  function CourseList(){
	var self=this;
	this.clear=function clear(){
		while(document.querySelector("div[class='courses']>table>tbody").children.length>0){
		document.querySelector("div[class='courses']>table>tbody").removeChild(document.querySelector("div[class='courses']>table>tbody").children[0]);
		}
		while(document.getElementById("idAlert").firstChild){
			document.getElementById("idAlert").removeChild(document.getElementById("idAlert").firstChild);
		}
	}
	this.clear();
	this.show=function show(){
		makeCall("GET","GetCoursesRIA",function(req){
			if(req.readyState==4){
				var message = req.responseText;
				if(req.status==200){
					let courses=JSON.parse(req.responseText);
					self.update(courses);
				}
				else{
					let textContent=document.createTextNode(message);
					document.getElementById("idAlert").appendChild(textContent);
					document.querySelector("div[class='courses']").style.display="none";
				}
			}
		},null);
	}
	this.show();
	this.update=function update(courses){
		var i=0;
		for(i;i<courses.length;i++){
			let newRow=document.createElement("tr");
			let newElement=document.createElement("td");
			newElement.textContent=courses[i].title;
			newRow.appendChild(newElement);
			let newLink=document.createElement("td");
			let anchor=document.createElement("a");
			newLink.appendChild(anchor);
			let textContent=document.createTextNode("Show Appeals");
			anchor.appendChild(textContent);
			newRow.appendChild(newLink);
			anchor.setAttribute("href", "#");
          	anchor.setAttribute("courseId", courses[i].courseId);
			anchor.addEventListener("click", (e) => {
				appealList.clear();
				appealList.hide();
				gradeDetails.clear();
				gradeDetails.hide();
				appealList.show(e.target.getAttribute("courseId"));
			});
			document.querySelector("div[class='courses']>table>tbody").appendChild(newRow);
		}
	}
	
}

	function AppealList(){
		var self=this;
		this.hide=function hide(){
			document.querySelector("div[class='appeals']").style.display="none";
		}
		this.clear=function clear(){
		while(document.querySelector("div[class='appeals']>table>tbody").children.length>0){
		document.querySelector("div[class='appeals']>table>tbody").removeChild(document.querySelector("div [class='appeals']>table>tbody").children[0]);
		}
		while(document.getElementById("idAlert").firstChild){
			document.getElementById("idAlert").removeChild(document.getElementById("idAlert").firstChild);
		}
		}
		this.clear();
		this.hide();
		this.show=function show(courseId,title){
		document.querySelector("div[class='appeals']").removeAttribute("style");
		makeCall("GET","GetAppealsRIA?courseId="+ courseId,function(req){
			if(req.readyState==4){
				var message = req.responseText;
				if(req.status==200){
					let appeals=JSON.parse(req.responseText);
					self.update(appeals);
				}
				else{
					let textContent=document.createTextNode(message);
					document.getElementById("idAlert").appendChild(textContent);
					document.querySelector("div[class='appeals']").style.display="none";
				}
			}
		},null);
	}

	this.update=function update(appeals){
		var i=0;
		for(i;i<appeals.length;i++){
			let newRow=document.createElement("tr");
			let newLink=document.createElement("td");
			let anchor=document.createElement("a");
			newLink.appendChild(anchor);
			let textContent=document.createTextNode(appeals[i].date);
			anchor.appendChild(textContent);
			newRow.appendChild(newLink);
			anchor.setAttribute("href", "#");
          	anchor.setAttribute("appealId", appeals[i].appealId);
			anchor.addEventListener("click", (e) => {
				gradeDetails.clear();
				gradeDetails.hide();
				gradeDetails.show(e.target.getAttribute("appealId"));
			});
			document.querySelector("div[class='appeals']>table>tbody").appendChild(newRow);
		}
	}
		
	}
	
 	  function GradeDetails(){
	 	this.element = document.querySelector("div[class='gradeDetails']");
		
		this.hide = function hide(){
      		this.element.style.display = "none";
			if (button !== undefined) button.hide();
    	}
    	this.show = function show(appealId){
      		makeCall("GET", "GetResultRIA?appeal=" + appealId, this.update,null);
      		this.element.removeAttribute("style");
			
			if (button !== undefined) button.hide();
    	}
		this.clear = function clear() {
			while(document.getElementById("idAlert").firstChild){
			document.getElementById("idAlert").removeChild(document.getElementById("idAlert").firstChild);
			}
			while(document.getElementsByClassName("gradeDetails").firstChild){
				document.getElementById("gradeDetails").removeChild(document.getElementById("gradeDetails").firstChild);
			}
			while(document.getElementById("mainContent").firstChild){
				document.getElementById("mainContent").removeChild(document.getElementById("mainContent").firstChild);
			}
			while(document.getElementById("studentId").firstChild){
				document.getElementById("studentId").removeChild(document.getElementById("studentId").firstChild);
			}
			while(document.getElementById("studentName").firstChild){
				document.getElementById("studentName").removeChild(document.getElementById("studentName").firstChild);
			}
			while(document.getElementById("studentSurname").firstChild){
				document.getElementById("studentSurname").removeChild(document.getElementById("studentSurname").firstChild);
			}
			while(document.getElementById("email").firstChild){
				document.getElementById("email").removeChild(document.getElementById("email").firstChild);
			}
			while(document.getElementById("degreeCourse").firstChild){
				document.getElementById("degreeCourse").removeChild(document.getElementById("degreeCourse").firstChild);
			}
			while(document.getElementById("courseId").firstChild){
				document.getElementById("courseId").removeChild(document.getElementById("courseId").firstChild);
			}
			while(document.getElementById("date").firstChild){
				document.getElementById("date").removeChild(document.getElementById("date").firstChild);
			}
			while(document.getElementById("courseTitle").firstChild){
				document.getElementById("courseTitle").removeChild(document.getElementById("courseTitle").firstChild);
			}
			while(document.getElementById("grade").firstChild){
				document.getElementById("grade").removeChild(document.getElementById("grade").firstChild);
			}
			document.querySelector("div.fields").style.display = "none";
	   	}
		this.hide();
    	this.clear();
		this.update = function update(req) {
      	if (req.readyState === 4){
			var message = req.responseText;
			if(req.status === 200){
				let objectStrings = req.responseText.split("}");
	        	let gr = JSON.parse(objectStrings[0]+"}");
	        	let appeal = JSON.parse(objectStrings[1]+"}");
	
				var i=0;
				if(gr.grade>=18 && gr.state=='published'){button.show(appeal.appealId);}   
				if(gr.state=="not entered"){
					let text= document.createTextNode("Grade not entered yet");
					document.getElementById("mainContent").appendChild(text);
				}       
				else{
					document.querySelector("div.fields").removeAttribute("style");
		        	let text = document.createTextNode(gr.studentId);
					document.getElementById("studentId").appendChild(text);
					text = document.createTextNode(gr.studentName);
					document.getElementById("studentName").appendChild(text);
					text = document.createTextNode(gr.studentSurname);
					document.getElementById("studentSurname").appendChild(text);
					text = document.createTextNode(gr.email);
					document.getElementById("email").appendChild(text);
					text = document.createTextNode(gr.degreeCourse);
					document.getElementById("degreeCourse").appendChild(text);
					text = document.createTextNode(appeal.courseId);
					document.getElementById("courseId").appendChild(text);
					text = document.createTextNode(appeal.date);
					document.getElementById("date").appendChild(text);
					text = document.createTextNode(appeal.courseTitle);
					document.getElementById("courseTitle").appendChild(text);
					text = document.createTextNode(gr.grade);
					document.getElementById("grade").appendChild(text);
				}
			}
			else{
				let textContent=document.createTextNode(message);
				document.getElementById("idAlert").appendChild(textContent);
				document.querySelector("div[class='gradeDetails']").style.display="none";
			}
        }
	}
	}

	function Button(){
    this.element = document.querySelector("div.Button");
    this.hide = function hide(){
      this.element.style.display = "none";
    }
    this.show = function show(appealId){
      document.querySelector("div.Button form.refuse input[type='hidden']").setAttribute("value", appealId);
      this.element.removeAttribute("style");
    }
    this.hide();
    this.update = function update(appealId){
    document.querySelector("div.Button form.refuse input[type='submit']").addEventListener('click', (e) => {
	 		var form = e.target.closest("form");
	        if (form.checkValidity()) {
			  e.preventDefault();
	          makeCall("POST", 'RefuseGradeRIA',
	            function(req) {
	              if (req.readyState === 4){
					if(req.status === 200){
						gradeDetails.clear();
						gradeDetails.show( document.querySelector("div.Button form.refuse input[type='hidden']").getAttribute("value"));
					}
					else{
					while(document.getElementById("idAlert").firstChild){
						document.getElementById("idAlert").removeChild(document.getElementById("idAlert").firstChild);
					}
					let textContent=document.createTextNode(req.responseText);
					document.getElementById("idAlert").appendChild(textContent);
				}
				}
        	},form);
      	}
    });
  }
}

  function PageOrchestrator(){
    this.start = function (){
      courseList = new CourseList();
      appealList = new AppealList();
      gradeDetails = new GradeDetails();
	  button = new Button();
      button.update();
      //this.clearAll();
	  document.querySelector("a[href='LogoutRIA']").addEventListener('click', () => {
	      window.sessionStorage.removeItem('username');
	  })
    }
   
  }

  function makeCall(method, url,cback ,formElement , reset = true) {
	    var req = new XMLHttpRequest(); // visible by closure
	    req.onreadystatechange = function() {
	      cback(req)
	    }; // closure
	    req.open(method, url);
	    if (formElement == null) {
	      req.send();
	    } else {
	      req.send(new FormData(formElement));
	    }
	    if (formElement !== null && reset === true) {
	      formElement.reset();
	    }
	  }
}())
