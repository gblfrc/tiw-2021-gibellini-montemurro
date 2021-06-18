(function() {

	var courseList, appealList, gradeDetails, pageOrchestrator, button;

	//add event listener on load to window object
	//assign objects to DOM elements
	window.addEventListener("load", () => {
		pageOrchestrator = new PageOrchestrator();
		pageOrchestrator.start();
	});

	function CourseList() {
		var self = this;
		//function to remove present courses from current table
		this.clear = function clear() {
			while (document.querySelector("div[class='courses']>table>tbody").children.length > 0) {
				document.querySelector("div[class='courses']>table>tbody").removeChild(document.querySelector("div[class='courses']>table>tbody").children[0]);
			}
			removeError();
		}
		this.clear();
		//function to request courses from the server and show table
		this.show = function show() {
			makeCall("GET", "GetCoursesRIA", null, function(req) {
				if (req.readyState == 4) {
					if (req.status == 200) {
						let courses = JSON.parse(req.responseText);
						self.update(courses);
					}
					else {
						document.querySelector("div.main").appendChild(errorManager(req));
						document.querySelector("div[class='courses']").style.display = "none";
					}
				}
			});
		}
		this.show();
		//function to update courses table
		this.update = function update(courses) {
			var i = 0;
			for (i; i < courses.length; i++) {
				let newRow = document.createElement("tr");
				let newElement = document.createElement("td");
				newElement.textContent = courses[i].title;
				newRow.appendChild(newElement);
				let newLink = document.createElement("td");
				let anchor = document.createElement("a");
				newLink.appendChild(anchor);
				let textContent = document.createTextNode("Show Appeals");
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
					appealList.show(e.target.getAttribute("courseId"), title);
				});
				document.querySelector("div[class='courses']>table>tbody").appendChild(newRow);
			}
		}

	}

	function AppealList() {
		var self = this;
		// function to hide appeals table
		this.hide = function hide() {
			document.querySelector("div[class='appeals']").style.display = "none";
		}
		//function to remove present appeals from current table
		this.clear = function clear() {
			while (document.querySelector("div[class='appeals']>table>tbody").children.length > 0) {
				document.querySelector("div[class='appeals']>table>tbody").removeChild(document.querySelector("div [class='appeals']>table>tbody").children[0]);
			}
			removeError();
		}
		this.clear();
		this.hide();
		//function to request appeals from the server and show table
		this.show = function show(courseId, title) {
			document.querySelector("div[class='appeals']").removeAttribute("style");
			document.querySelector("div[class='appeals']>span").innerText = "Here are the appeals for the course: " + title.toUpperCase();
			makeCall("GET", "GetAppealsRIA?courseId=" + courseId, null, function(req) {
				if (req.readyState == 4) {
					if (req.status == 200) {
						let appeals = JSON.parse(req.responseText);
						self.update(appeals);
					}
					else {
						document.querySelector("div.main").appendChild(errorManager(req));
						document.querySelector("div[class='appeals']").style.display = "none";
					}
				}
			});
		}
		//function to update appeals table
		this.update = function update(appeals) {
			var i = 0;
			for (i; i < appeals.length; i++) {
				let newRow = document.createElement("tr");
				let newLink = document.createElement("td");
				let anchor = document.createElement("a");
				newLink.appendChild(anchor);
				let textContent = document.createTextNode(appeals[i].date);
				anchor.appendChild(textContent);
				newRow.appendChild(newLink);
				anchor.setAttribute("href", "#");
				anchor.setAttribute("appeal", appeals[i].appealId);
				anchor.addEventListener("click", (e) => {
					gradeDetails.clear();
					gradeDetails.hide();
					let date = e.target.innerText;
					gradeDetails.show(e.target.getAttribute("appeal"), date);
				});
				document.querySelector("div[class='appeals']>table>tbody").appendChild(newRow);
			}
		}
	}

	function GradeDetails() {
		//get all DOM elements of the details div
		this.element = document.querySelector("div[class='gradeDetails']");
		this.message = document.querySelector("div[class='gradeDetails']>span");
		this.mainContent = document.getElementById("mainContent");
		this.studentId = document.getElementById("studentId");
		this.studentName = document.getElementById("studentName");
		this.studentSurname = document.getElementById("studentSurname");
		this.email = document.getElementById("email");
		this.degreeCourse = document.getElementById("degreeCourse");
		this.courseId = document.getElementById("courseId");
		this.date = document.getElementById("date");
		this.courseTitle = document.getElementById("courseTitle");
		this.grade = document.getElementById("grade");
		// function to hide result content
		this.hide = function hide() {
			this.element.style.display = "none";
			if (button !== undefined) button.hide();
		}
		//function to request grade content from the server and show details
		this.show = function show(appealId, date) {
			makeCall("GET", "GetResultRIA?appeal=" + appealId, null, this.update);
			this.element.removeAttribute("style");
			this.message.innerText = "Here is the result of the exam you took on " + date.toUpperCase();
			if (button !== undefined) button.hide();
		}
		
		this.clear = function clear() {
			removeError();
			this.mainContent.style.display = "none";
			this.studentId.innerText = "";
			this.studentName.innerText = "";
			this.studentSurname.innerText = "";
			this.email.innerText = "";
			this.degreeCourse.innerText = "";
			this.courseId.innerText = "";
			this.date.innerText = "";
			this.courseTitle.innerText = "";
			this.grade.innerText = "";
			document.querySelector("div.fields").style.display = "none";
		}
		this.hide();
		this.clear();
		//function to update details of result section
		this.update = function update(req) {
			if (req.readyState === 4) {
				if (req.status === 200) {
					let objectStrings = req.responseText.split("}");
					let gr = JSON.parse(objectStrings[0] + "}");
					let appeal = JSON.parse(objectStrings[1] + "}");
					if (gr.grade >= 18 && gr.state == 'published') { button.show(appeal.appealId); }
					if (gr.state == "not entered") {
						gradeDetails.mainContent.removeAttribute("style");
					}
					else {
						document.querySelector("div.fields").removeAttribute("style");
						gradeDetails.studentId.innerText = gr.studentId;
						gradeDetails.studentName.innerText = gr.studentName;
						gradeDetails.studentSurname.innerText = gr.studentSurname;
						gradeDetails.email.innerText = gr.email;
						gradeDetails.degreeCourse.innerText = gr.degreeCourse;
						gradeDetails.courseId.innerText = appeal.courseId;
						gradeDetails.date.innerText = appeal.date;
						gradeDetails.courseTitle.innerText = appeal.courseTitle;
						gradeDetails.grade.innerText = gr.grade.toUpperCase();
					}
				}
				else {
					document.querySelector("div.main").appendChild(errorManager(req));
					document.querySelector("div[class='gradeDetails']").style.display = "none";
				}
			}
		}
	}

	function Button() {
		this.element = document.querySelector("div.Button");
		// function to hide refuse button
		this.hide = function hide() {
			this.element.style.display = "none";
		}
		//function to show refuse button
		this.show = function show(appealId) {
			document.querySelector("div.Button form.refuse input[type='hidden']").setAttribute("value", appealId);
			this.element.removeAttribute("style");
		}
		this.hide();
		// function used on object creation to add event listener to refuse button
		this.registerEvents = function registerEvents(appealId) {
			document.querySelector("div.Button form.refuse input[type='submit']").addEventListener('click', (e) => {
				var form = e.target.closest("form");
				if (form.checkValidity()) {
					e.preventDefault();
					makeCall("POST", 'RefuseGradeRIA', new FormData(form), function(req) {
						if (req.readyState === 4) {
							if (req.status === 200) {
								gradeDetails.clear();
								gradeDetails.show(document.querySelector("div.Button form.refuse input[type='hidden']").getAttribute("value"));
							}
							else {
								removeError();
								document.querySelector("div.main").appendChild(errorManager(req));
							}
						}
					});
				}
			});
		}
		this.registerEvents();
	}

	function PageOrchestrator() {
		this.start = function() {
			courseList = new CourseList();
			appealList = new AppealList();
			gradeDetails = new GradeDetails();
			button = new Button();
			//button.update();
			//this.clearAll();
			document.querySelector("a[href='LogoutRIA']").addEventListener('click', () => {
				window.sessionStorage.removeItem('username');
			})
		}

	}

}())
