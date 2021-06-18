(function() {

	var courseList, appealList, subscribers, editForm,
		buttons, reports, multipleEdit, pageOrchestrator;

	//add event listener on load to window object
	//assign objects to DOM elements
	window.addEventListener("load", () => {
		pageOrchestrator = new PageOrchestrator();
		pageOrchestrator.start();
	});


	function CourseList() {
		this.element = document.querySelector("div[class='courses']");
		this.message = document.querySelector("div[class='courses']>span");
		this.courses = document.querySelector("div[class='courses']>table>tbody");
		//function to request courses from the server and show table
		this.show = function() {
			makeCall("GET", "GetCoursesRIA", null, this.update);
		}
		//function to remove present courses from current table
		this.clear = function clear() {
			while (this.courses.children.length > 0) {
				this.courses.removeChild(this.courses.children[0]);
			}
			removeError();
		}
		this.clear();
		//function to update courses table
		this.update = function update(req) {
			if (req.readyState === 4) {
				if (req.status === 200) {
					courseList.clear();
					let i = 0;
					let array = JSON.parse(req.responseText);
					for (i = 0; i < array.length; i++) {
						let newRow = document.createElement("tr");
						let titleCell = document.createElement("td");
						titleCell.textContent = array[i].title;
						newRow.appendChild(titleCell);
						let appealLinkCell = document.createElement("td");
						let anchor = document.createElement("a");
						anchor.textContent = "Show Appeals";
						anchor.setAttribute("href", "#");
						anchor.setAttribute("courseId", array[i].courseId);
						appealLinkCell.appendChild(anchor);
						newRow.appendChild(appealLinkCell);
						courseList.courses.appendChild(newRow);
						//add event listener to show appeals list after clicking on one anchor
						anchor.addEventListener("click", (e) => {
							e.preventDefault();
							appealList.hide();
							appealList.clear();
							subscribers.hide();
							subscribers.clear();
							reports.hide();
							reports.clear();
							let title = e.target.parentElement.previousSibling.textContent;
							appealList.show(e.target.getAttribute('courseId'), title);
						});
					}
				}
				else {
					document.querySelector("div.main").appendChild(errorManager(req));
				}
			}
		}
		this.show();
	}

	function AppealList() {
		this.element = document.querySelector("div[class='appeals']");
		this.message = document.querySelector("div[class='appeals']>span");
		this.appeals = document.querySelector("div[class='appeals']>table>tbody");
		// function to hide appeals table
		this.hide = function hide() {
			this.element.style.display = "none";
		}
		//function to request appeals from the server and show table
		this.show = function show(courseId, title) {
			makeCall("GET", "GetAppealsRIA?courseId=" + courseId, null, this.update);
			this.message.textContent = "Here are the appeals for the course: " + title.toUpperCase();
			this.element.setAttribute("courseId", courseId);
			this.element.removeAttribute("style");
		}
		//function to remove present appeals from current table
		this.clear = function clear() {
			while (this.appeals.children.length > 0) {
				this.appeals.removeChild(this.appeals.children[0]);
			}
			removeError();
		}
		this.hide();
		this.clear();
		//function to update appeals table
		this.update = function update(req) {
			if (req.readyState === 4) {
				if (req.status === 200) {
					appealList.clear();
					let i = 0;
					let array = JSON.parse(req.responseText);
					for (i = 0; i < array.length; i++) {
						let newRow = document.createElement("tr");
						let dateCell = document.createElement("td");
						let anchor = document.createElement("a");
						anchor.textContent = array[i].date;
						anchor.setAttribute("href", "#");
						anchor.setAttribute("appeal", array[i].appealId);
						dateCell.appendChild(anchor);
						newRow.appendChild(dateCell);
						appealList.appeals.appendChild(newRow);
						//add event listener to show subscribers list after clicking on one anchor
						anchor.addEventListener("click", (e) => {
							e.preventDefault();
							subscribers.hide();
							subscribers.clear();
							reports.hide();
							let date = e.target.textContent;
							subscribers.show(e.target.getAttribute('appeal'), date);
						});
					}
				}
				else {
					document.querySelector("div.main").appendChild(errorManager(req));
					appealList.hide();
				}
			}
		}
	}

	function Subscribers() {
		this.element = document.querySelector("div.subscribers");
		this.message = document.querySelector("div.subscribers>span");
		this.subs = document.querySelector("div.subscribers>table>tbody");
		// function to hide subscribers table
		this.hide = function hide() {
			this.element.style.display = "none";
			if (buttons !== undefined) buttons.hide();
		}
		//function to request subscribers from the server and show table
		this.show = function show(appealId, date) {
			editForm.hide();
			reports.hide();
			makeCall("GET", "GetSubscribersRIA?appeal=" + appealId, null, this.update);
			buttons.show(appealId);
			this.message.textContent = "Here is the list of students who took part the appeal on " + date.toUpperCase();
			this.element.setAttribute("appeal", appealId); //may want to save appealId in a different var
			this.element.removeAttribute("style");
		}
		//function to remove present appeals from current table
		this.clear = function clear() {
			while (this.subs.children.length > 0) {
				this.subs.removeChild(this.subs.children[0]);
			}
			removeError();
		}
		this.hide();
		this.clear();
		//function to update subscribers table
		this.update = function update(req) {
			if (req.readyState === 4) {
				if (req.status === 200) {
					subscribers.clear();
					let i = 0;
					let array = JSON.parse(req.responseText);
					for (i = 0; i < array.length; i++) {
						let newRow = document.createElement("tr");
						//studentId
						let studentIdCell = document.createElement("td");
						studentIdCell.textContent = array[i].studentId;
						newRow.appendChild(studentIdCell);
						//studentSurname
						let surnameCell = document.createElement("td");
						surnameCell.textContent = array[i].studentSurname;
						newRow.appendChild(surnameCell);
						//studentName
						let nameCell = document.createElement("td");
						nameCell.textContent = array[i].studentName;
						newRow.appendChild(nameCell);
						//email
						let emailCell = document.createElement("td");
						emailCell.textContent = array[i].email;
						newRow.appendChild(emailCell);
						//degreeCourse
						let degreeCourseCell = document.createElement("td");
						degreeCourseCell.textContent = array[i].degreeCourse;
						newRow.appendChild(degreeCourseCell);
						//grade
						let gradeCell = document.createElement("td");
						gradeCell.textContent = array[i].grade.toUpperCase();
						if (array[i].grade == "failed" || array[i].grade == "recalled" ||
							array[i].grade == "absent") {
							//may want to use a more formal method to compare strings
							gradeCell.setAttribute("class", "fail");
						}
						else if (array[i].state === "not entered") {
							gradeCell.textContent = "";
						}
						else gradeCell.setAttribute("class", "passed");
						newRow.appendChild(gradeCell);
						//state
						let stateCell = document.createElement("td");
						stateCell.textContent = array[i].state.toUpperCase();
						newRow.appendChild(stateCell);
						//edit button
						let editCell = document.createElement("td");
						if (stateCell.textContent == "ENTERED" || stateCell.textContent == "NOT ENTERED") {
							let form = document.createElement("form");
							//set student input
							let studentInput = document.createElement("input");
							studentInput.setAttribute("type", "hidden");
							studentInput.setAttribute("name", "studentId");
							studentInput.setAttribute("value", array[i].studentId)
							form.appendChild(studentInput);
							//set appeal input
							let appealInput = document.createElement("input");
							appealInput.setAttribute("type", "hidden");
							appealInput.setAttribute("name", "appeal");
							appealInput.setAttribute("value", subscribers.element.getAttribute("appeal"));
							form.appendChild(appealInput);
							//create button
							let button = document.createElement("input");
							button.setAttribute("type", "submit");
							button.setAttribute("value", "Edit");
							form.appendChild(button);
							//insert button in td and add event listener to it
							editCell.appendChild(form);
							//add event listener to show edit form after clicking an edit button
							button.addEventListener("click", (e) => {
								e.preventDefault();
								editForm.clear();
								let appealId = e.target.closest("form").children[1].getAttribute("value");
								let studentId = e.target.closest("form").children[0].getAttribute("value");
								editForm.show(appealId, studentId);
							});
						}
						newRow.appendChild(editCell);
						subscribers.subs.appendChild(newRow);
					}
				}
				else {
					document.querySelector("div.main").appendChild(errorManager(req));
					buttons.hide();
					subscribers.hide();
				}
			}
		}
	}

	function EditForm() {
		//get all DOM elements of the form to edit a single grade
		this.studentId = document.querySelector("div.Edit div.studentId");
		this.appealId = document.querySelector("div.Edit input[name='appeal']");
		this.name = document.querySelector("div.Edit div.name");
		this.surname = document.querySelector("div.Edit div.surname");
		this.courseTitle = document.querySelector("div.Edit div.courseTitle");
		this.appealDate = document.querySelector("div.Edit div.appealDate");
		this.grade = document.querySelector("div.Edit div.grade");
		this.element = document.querySelector("div.Edit");
		this.button = document.querySelector("div.Edit input[type='submit']")
		// function to hide form
		this.hide = function hide() {
			this.element.style.display = "none";
		}
		//function to request form content to the server and show it
		this.show = function show(appealId, studentId) {
			makeCall("GET", "GetModifyRIA?appeal=" + appealId + "&studentId=" + studentId, null, this.update);
			this.studentId.setAttribute("value", studentId);
			this.appealId.setAttribute("value", appealId);
			this.element.removeAttribute("style");
		}
		//function to clear form content
		this.clear = function clear() {
			this.studentId.children[2].textContent = "";
			this.name.children[1].textContent = "";
			this.surname.children[1].textContent = "";
			this.courseTitle.children[1].textContent = "";
			this.appealDate.children[1].textContent = "";
			removeError();
		}
		this.hide();
		this.clear();
		//function to update form content
		this.update = function update(req) {
			if (req.readyState === 4) {
				if (req.status === 200) {
					//little hack needed to parse two different objects
					let objectStrings = req.responseText.split("}");
					let grade = JSON.parse(objectStrings[0] + "}");
					let appeal = JSON.parse(objectStrings[1] + "}");
					//introducing content in <nobr> tags
					editForm.studentId.children[2].textContent = grade.studentId;
					editForm.name.children[1].textContent = grade.studentName;
					editForm.surname.children[1].textContent = grade.studentSurname;
					editForm.courseTitle.children[1].textContent = appeal.courseTitle;
					editForm.appealDate.children[1].textContent = appeal.date;
					editForm.studentId.children[1].setAttribute("value", grade.studentId);
					//add event listener to edit grade after clicking on edit button
					editForm.button.addEventListener("click", (e) => {
						e.preventDefault();
						let form = e.target.closest("form");
						if (form.checkValidity()) {
							makeCall("POST", "EditRIA", new FormData(form), function(req) {
								if (req.readyState === 4) {
									if (req.status === 200) {
										let appeal = JSON.parse(req.responseText);
										subscribers.show(appeal.appealId, appeal.date);
									}
									else {
										removeError();
										document.querySelector("div.main").appendChild(errorManager(req));
									}
								}
							});
						}
					})
				}
				else {
					document.querySelector("div.main").appendChild(errorManager(req));
					editForm.hide();
				}
			}
		}
	}

	function Buttons() {
		//get all DOM button elements
		this.element = document.querySelector("div.Buttons");
		this.publish = document.querySelector("div.Buttons form.publish input[type='hidden']");
		this.report = document.querySelector("div.Buttons form.report input[type='hidden']");
		this.allReports = document.querySelector("div.Buttons form.allReports input[type='hidden']");
		this.multipleEdit = document.querySelector("div.Buttons form.multipleEdit input[type='hidden']");
		// function to hide buttons
		this.hide = function hide() {
			this.element.style.display = "none";
			this.publish.removeAttribute("value");
			this.report.removeAttribute("value");
			this.allReports.removeAttribute("value");
			this.multipleEdit.removeAttribute("value");
		}
		// function to show button and link to them and appealId
		this.show = function show(appealId) {
			this.publish.setAttribute("value", appealId);
			this.report.setAttribute("value", appealId);
			this.allReports.setAttribute("value", appealId);
			this.multipleEdit.setAttribute("value", appealId);
			this.element.removeAttribute("style");
		}
		this.hide();
		// function used on object creation to add event listeners to buttons
		this.registerEvents = function registerEvents() {
			//add event listener to Publish button
			let publishSubmit = this.publish.nextSibling.nextSibling; //line to get the buttons
			publishSubmit.addEventListener("click", (e) => {
				e.preventDefault();
				editForm.hide();
				reports.hide();
				reports.clear();
				let form = e.target.closest("form");
				makeCall("POST", "PublishRIA", new FormData(form), function(req) {
					if (req.readyState === 4) {
						if (req.status === 200) {
							let appeal = JSON.parse(req.responseText);
							subscribers.show(appeal.appealId, appeal.date);
						}
						else {
							removeError();
							document.querySelector("div.main").appendChild(errorManager(req));
						}
					}
				});
			});
			//add event listener to Report button
			let reportSubmit = this.report.nextSibling.nextSibling; //line to get the buttons
			reportSubmit.addEventListener("click", (e) => {
				e.preventDefault();
				editForm.hide();
				let form = e.target.closest("form");
				makeCall("POST", "ReportRIA", new FormData(form), function(req) {
					if (req.readyState === 4) {
						if (req.status === 200) {
							let appeal = JSON.parse(req.responseText);
							subscribers.show(appeal.appealId, appeal.date);
							reports.show("last", appeal.appealId);
						}
						else {
							removeError();
							document.querySelector("div.main").appendChild(errorManager(req));
						}
					}
				});
			});
			//add event listener to allReports button
			let allReportsSubmit = this.allReports.nextSibling.nextSibling; //line to get the buttons
			allReportsSubmit.addEventListener("click", (e) => {
				e.preventDefault();
				editForm.hide();
				appealId = this.allReports.getAttribute("value");
				reports.show("all", appealId);
			});
			//add event listener to multipleEdit button
			let multipleEditSubmit = this.multipleEdit.nextSibling.nextSibling; //line to get the buttons
			multipleEditSubmit.addEventListener("click", (e) => {
				e.preventDefault();
				editForm.hide();
				reports.hide();
				multipleEdit.clear();
				multipleEdit.show(buttons.multipleEdit.getAttribute("value"));
				editForm.hide();
			});
		}
		this.registerEvents();
	}

	function Reports() {
		//get all DOM elements of the reports div
		this.element = document.querySelector("div.Reports");
		this.summary = document.querySelector("div.Reports div.summary");
		this.courseTitle = document.querySelector("div.Reports div.summary div.courseTitle");
		this.appealDate = document.querySelector("div.Reports div.summary div.appealDate");
		this.message = document.querySelector("div.Reports div.summary p.message");
		this.details = document.querySelector("div.Reports div.details");
		this.reportId = document.querySelector("div.Reports div.details div.reportId");
		this.reportDate = document.querySelector("div.Reports div.details div.reportDate");
		this.reportHour = document.querySelector("div.Reports div.details div.reportHour");
		this.reportGrades = document.querySelector("div.Reports div.details div.reportGrades tbody");
		// function to hide reports
		this.hide = function hide() {
			this.element.style.display = "none";
		}
		// function to change message to reports section based on request type
		this.showMessage = function showMessage(msgType) {
			if (msgType === "last") this.message.textContent = "Created report for the appeal:";
			if (msgType === "all") this.message.textContent = "Here is shown all information about reports for the appeal:";
		}
		// function to show reports
		this.show = function show(msgType, appealId) {
			reports.clear();
			makeCall("GET", "GetReportsRIA?type=" + msgType + "&appeal=" + appealId, null, this.update);
			this.showMessage(msgType);
			this.element.removeAttribute("style");
		}
		// function to clear reports (and delete the ones after the first)
		this.clear = function clear() {
			removeError();
			//get all report details
			let allDetails = document.querySelectorAll("div.Reports div.details");
			//delete all but first report details
			if (allDetails.length > 1) {
				for (let i = 1; i < allDetails.length; i++) {
					allDetails[i].remove();
				}
			}
			//clear all static (or previously-shown) report content
			this.courseTitle.children[1].textContent = "";
			this.appealDate.children[1].textContent = "";
			this.reportId.children[1].textContent = "";
			this.reportDate.children[1].textContent = "";
			this.reportHour.children[1].textContent = "";
			//remove all static (or previously-shown) rows in the table
			while (this.reportGrades.children.length > 0) {
				this.reportGrades.removeChild(this.reportGrades.children[0]);
			}
		}
		this.hide();
		this.clear();
		// function to update reports
		this.update = function update(req) {
			if (req.readyState === 4) {
				if (req.status === 200) {
					let reportsArr = JSON.parse(req.responseText);
					let course = reportsArr[0].appeal.courseTitle;
					reports.courseTitle.children[1].textContent = reportsArr[0].appeal.courseTitle;
					reports.appealDate.children[1].textContent = reportsArr[0].appeal.date;
					for (let i = 0; i < reportsArr.length; i++) {
						let currentReport = reportsArr[i];
						//select report details section or duplicate an existent one
						let detailsElement;
						if (i === 0) detailsElement = reports.details;
						if (i > 0) {
							detailsElement = reports.details.cloneNode("deep");
							reports.element.appendChild(detailsElement);
						}
						//get details elements (get nobr tags)
						let reportId = detailsElement.children[1].children[1];
						let reportDate = detailsElement.children[2].children[1];
						let reportHour = detailsElement.children[3].children[1];
						let reportGrades = detailsElement.children[4].children[1].children[1];
						reportId.textContent = currentReport.reportId;
						reportDate.textContent = currentReport.creationDate;
						reportHour.textContent = currentReport.creationTime;
						//add grades as rows of the table
						let grades = currentReport.grades;
						for (let j = 0; j < grades.length; j++) {
							let newRow = document.createElement("tr");
							//studentId
							let studentIdCell = document.createElement("td");
							studentIdCell.textContent = grades[j].studentId;
							newRow.appendChild(studentIdCell);
							//studentSurname
							let surnameCell = document.createElement("td");
							surnameCell.textContent = grades[j].studentSurname;
							newRow.appendChild(surnameCell);
							//studentName
							let nameCell = document.createElement("td");
							nameCell.textContent = grades[j].studentName;
							newRow.appendChild(nameCell);
							//grade
							let gradeCell = document.createElement("td");
							gradeCell.textContent = grades[j].grade.toUpperCase();
							if (gradeCell.textContent == "FAILED" || gradeCell.textContent == "RECALLED" ||
								gradeCell.textContent == "ABSENT") {
								gradeCell.setAttribute("class", "fail");
							}
							else gradeCell.setAttribute("class", "passed");
							newRow.appendChild(gradeCell);
							reportGrades.appendChild(newRow);
						}
					}
				}
				else {
					document.querySelector("div.main").appendChild(errorManager(req));
					document.querySelector("div.Reports").style.display = "none";
				}
			}
		}
	}

	function MultipleEdit() {
		this.element = document.querySelector("div.MultipleEdit");
		this.closer = document.querySelector("div.MultipleEdit span.closer");
		this.enter = document.querySelector("div.MultipleEdit input[type='submit']");
		//function to request the server "not entered" grades
		this.show = function show(appealId) {
			makeCall("GET", "GetNotEnteredRIA?appeal=" + appealId, null, multipleEdit.update);
		}
		//function to hide modal window
		this.hide = function hide() {
			let mebg = this.element.closest("div.mebg");
			mebg.classList.remove("active");
		}
		//function to clear list of grades
		this.clear = function clear() {
			removeError();
			let tbody = this.element.children[2].children[1];
			while (tbody.children.length > 1) {
				tbody.removeChild(tbody.children[1]);
			}
			for (let i = 0; i < 5; i++) {
				tbody.children[0].children[i].textContent = "";
			}
			let form = tbody.children[0].children[5].children[0];
			form.children[0].selectedIndex = 0;
			form.children[1].removeAttribute("value");
			form.children[2].removeAttribute("value");
		}
		// function used on object creation to add event listeners to buttons
		this.registerEvents = function registerEvents() {
			//add event listener to close modal window
			this.closer.addEventListener("click", function() {
				multipleEdit.hide();
			})
			//add event listener to submit multiple edit request
			this.enter.addEventListener("click", function(e) {
				e.preventDefault();
				let forms = document.querySelectorAll("div.MultipleEdit tbody tr form");
				//create array of objects to send to the server
				let array = new Array(forms.length);
				for (let i = 0; i < forms.length; i++) {
					let content = new Object();
					content.appealId = forms[i].children[1].getAttribute("value");
					content.studentId = forms[i].children[2].getAttribute("value");
					let select = forms[i].children[0];
					content.gradeValue = select.children[select.selectedIndex].getAttribute("value");
					array[i] = content;
				}
				let toSend = JSON.stringify(array);
				makeCall("POST", "MultipleEditRIA", toSend, (req) => {
					if (req.readyState === 4) {
						multipleEdit.hide();
						if (req.status === 200 || req.status === 206) {
							let appeal = JSON.parse(req.responseText);
							subscribers.show(appeal.appealId, appeal.date);
						}
						else {
							document.querySelector("div.main").appendChild(errorManager(req));
						}
					}
				})
			})
		}
		this.clear();
		this.registerEvents();
		//function to update list of "not entered" grades
		this.update = function update(req) {
			if (req.readyState === 4) {
				if (req.status === 200) {
					let tbody = multipleEdit.element.children[2].children[1];
					let i = 0;
					let array = JSON.parse(req.responseText);
					for (i = 0; i < array.length; i++) {
						let newRow;
						if (i === 0) {
							newRow = tbody.children[0];
							//needed when multipleEdit is used repeatedly (not to add multiple listeners to a single row)
							newRow.removeEventListener("click", rowSelector);
						}
						else {
							newRow = tbody.children[0].cloneNode("deep");
							//used cloning to avoid creation of select forms
						}
						newRow.children[0].textContent = array[i].studentId;
						newRow.children[1].textContent = array[i].studentSurname;
						newRow.children[2].textContent = array[i].studentName;
						newRow.children[3].textContent = array[i].email;
						newRow.children[4].textContent = array[i].degreeCourse;
						let form = newRow.children[5].children[0];
						form.children[1].setAttribute("value", array[i].appealId);
						form.children[2].setAttribute("value", array[i].studentId);
						let select = form.children[0];
						//add event listener to select element to enter selection for multiple lines (selected ones)
						select.addEventListener("change", (e) => {
							if (e.target.closest("tr").getAttribute("selected") === "true") {
								let allSelects = document.querySelectorAll("div.MultipleEdit tr[selected='true'] select");
								for (let i = 0; i < allSelects.length; i++) {
									allSelects[i].selectedIndex = e.target.selectedIndex;
								}
							}
						})
						tbody.appendChild(newRow);
						//add event listener to row to allow selection of multiple rows
						newRow.addEventListener("click", rowSelector);
					}
					multipleEdit.element.setAttribute("appeal", array[0].appealId);
					let mebg = multipleEdit.element.closest("div.mebg");
					mebg.classList.add("active");
				}
				else {
					document.querySelector("div.main").appendChild(errorManager(req));
				}
			}
		}
	}

	//function to select multiple rows in multiple edit
	function rowSelector(e) {
		if (!(e.target instanceof HTMLSelectElement)) {
			if (e.target.closest("tr").getAttribute("selected") === "true")
				e.target.closest("tr").removeAttribute("selected");
			else e.target.closest("tr").setAttribute("selected", "true");
		}
	}

	//object used to start the behavior of the website
	function PageOrchestrator() {
		this.start = function() {
			courseList = new CourseList();
			appealList = new AppealList();
			subscribers = new Subscribers();
			editForm = new EditForm();
			buttons = new Buttons();
			reports = new Reports();
			multipleEdit = new MultipleEdit();
			document.querySelector("a[href='LogoutRIA']").addEventListener('click', () => {
				window.sessionStorage.removeItem('username');
			})
		}
	}

}())
