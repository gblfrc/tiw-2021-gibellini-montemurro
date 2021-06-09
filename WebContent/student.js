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
		while(document.querySelector("p.error").firstChild){
			document.querySelector("p.error").removeChild(document.querySelector("p.error").firstChild);
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
					document.querySelector("p.error").appendChild(textContent);
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
				let title = e.target.parentElement.previousSibling.innerText;
				appealList.show(e.target.getAttribute("courseId"),title);
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
		while(document.querySelector("p.error").firstChild){
			document.querySelector("p.error").removeChild(document.querySelector("p.error").firstChild);
		}
		}
		this.clear();
		this.hide();
		this.show=function show(courseId,title){
		document.querySelector("div[class='appeals']").removeAttribute("style");
		document.querySelector("div[class='appeals']>span").innerText = "Here are the appeals for the course: " + title.toUpperCase();
		makeCall("GET","GetAppealsRIA?courseId="+ courseId,function(req){
			if(req.readyState==4){
				var message = req.responseText;
				if(req.status==200){
					let appeals=JSON.parse(req.responseText);
					self.update(appeals);
				}
				else{
					let textContent=document.createTextNode(message);
					document.querySelector("p.error").appendChild(textContent);
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
				let date =e.target.innerText;
				gradeDetails.show(e.target.getAttribute("appealId"),date);
			});
			document.querySelector("div[class='appeals']>table>tbody").appendChild(newRow);
		}
	}
		
	}
	
 	  function GradeDetails(){
	 	this.element = document.querySelector("div[class='gradeDetails']");
		this.message = document.querySelector("div[class='gradeDetails']>span");
		this.mainContent=document.getElementById("mainContent");
		this.studentId=document.getElementById("studentId");
		this.studentName=document.getElementById("studentName");
		this.studentSurname=document.getElementById("studentSurname");
		this.email=document.getElementById("email");
		this.degreeCourse=document.getElementById("degreeCourse");
		this.courseId=document.getElementById("courseId");
		this.date=document.getElementById("date");
		this.courseTitle=document.getElementById("courseTitle");
		this.grade=document.getElementById("grade");
		
		this.hide = function hide(){
      		this.element.style.display = "none";
			if (button !== undefined) button.hide();
    	}
    	this.show = function show(appealId,date){
      		makeCall("GET", "GetResultRIA?appeal=" + appealId, this.update,null);
      		this.element.removeAttribute("style");
			this.message.innerText = "Here is the result of the exam you took on " + date.toUpperCase();
			if (button !== undefined) button.hide();
    	}
		this.clear = function clear() {
			while(document.querySelector("p.error").children.length>0){
		 		document.querySelector("p.error").removeChild(document.querySelector("p.error").children[0]);
			}
			this.mainContent.innerText="";
			this.studentId.innerText="";
			this.studentName.innerText="";
			this.studentSurname.innerText="";
			this.email.innerText="";
			this.degreeCourse.innerText="";
			this.courseId.innerText="";
			this.date.innerText="";
			this.courseTitle.innerText="";
			this.grade.innerText="";
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
					gradeDetails.mainContent.innerText="Grade not entered yet";
				}       
				else{
					document.querySelector("div.fields").removeAttribute("style");
					gradeDetails.studentId.innerText=gr.studentId;
					gradeDetails.studentName.innerText=gr.studentName;
					gradeDetails.studentSurname.innerText=gr.studentSurname;
					gradeDetails.email.innerText=gr.email;
					gradeDetails.degreeCourse.innerText=gr.degreeCourse;
					gradeDetails.courseId.innerText=appeal.courseId;
					gradeDetails.date.innerText=appeal.date;
		        	gradeDetails.courseTitle.innerText=appeal.courseTitle;
					gradeDetails.grade.innerText=gr.grade;
				}
			}
			else{
				let textContent=document.createTextNode(message);
				document.querySelector("p.error").appendChild(textContent);
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
					while(document.querySelector("p.error").firstChild){
						document.querySelector("p.error").removeChild(document.querySelector("p.error").firstChild);
					}
					let textContent=document.createTextNode(req.responseText);
					document.querySelector("p.error").appendChild(textContent);
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
