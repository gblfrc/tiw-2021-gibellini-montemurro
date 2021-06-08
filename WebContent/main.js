(function() {

  var courseList, appealList, subscribers, editForm,
      buttons, reports, multipleEdit, pageOrchestrator;

  window.addEventListener("load", () => {
    pageOrchestrator = new PageOrchestrator();
    pageOrchestrator.start();
  });


  function CourseList(){
    this.element = document.querySelector("div[class='courses']");
    this.message = document.querySelector("div[class='courses']>span");
    this.courses = document.querySelector("div[class='courses']>table>tbody");
    //introduce function to clear the table but the header row
    this.clear = function clear() {
      while(this.courses.children.length>0){
        this.courses.removeChild(this.courses.children[0]);
      }
    }
    this.clear();
    this.update = function update(req) {
      if (req.readyState === 4 && req.status === 200){
		courseList.clear(); //should be useless; line written to create analogy with other update methods
        let i = 0;
        let array = JSON.parse(req.responseText);
        for (i = 0; i<array.length; i++){
          let newRow = document.createElement("tr");
          let titleCell = document.createElement("td");
          let text = document.createTextNode(array[i].title);
          titleCell.appendChild(text);
          newRow.appendChild(titleCell);
          let appealLinkCell = document.createElement("td");
          let anchor = document.createElement("a");
          text = document.createTextNode("Show Appeals");
          anchor.appendChild(text);
          anchor.setAttribute("href", "#");
          anchor.setAttribute("courseId", array[i].courseId);
          appealLinkCell.appendChild(anchor);
          newRow.appendChild(appealLinkCell);
          //may want to check why in this exact moment this.courses is undefined
          //this.courses = document.querySelector("div[class='courses']>table>tbody");
          courseList.courses.appendChild(newRow);
          anchor.addEventListener("click", (e) => {
            e.preventDefault();
            appealList.hide();
            appealList.clear();
            subscribers.hide();
            subscribers.clear();
            let title = e.target.parentElement.previousSibling.innerText;
            appealList.show(e.target.getAttribute('courseId'), title);
          });
        }
      }
    }
    makeCall("GET", "GetCoursesRIA", null, this.update);
  }

  function AppealList(){
    this.element = document.querySelector("div[class='appeals']");
    this.message = document.querySelector("div[class='appeals']>span");
    this.appeals = document.querySelector("div[class='appeals']>table>tbody");
    //this.message = "Here are the appeals for the course \"" + title + "\":";
    //this.appeals = appeals;
    this.hide = function hide(){
      this.element.style.display = "none";
    }
    this.show = function show(courseId, title){
      makeCall("GET", "GetAppealsRIA?courseId=" + courseId, null, this.update);
      this.message.innerText = "Here are the appeals for the course: " + title.toUpperCase();
      this.element.setAttribute("courseId", courseId);
      this.element.removeAttribute("style");
    }
    this.clear = function clear() {
      while(this.appeals.children.length>0){
        this.appeals.removeChild(this.appeals.children[0]);
      }
    }
    this.hide();
    this.clear();
    this.update = function update(req) {
      if (req.readyState === 4 && req.status === 200){
		appealList.clear();
        let i = 0;
        let array = JSON.parse(req.responseText);
        for (i = 0; i<array.length; i++){
          let newRow = document.createElement("tr");
          let dateCell = document.createElement("td");
          let anchor = document.createElement("a");
          let text = document.createTextNode(array[i].date);
          anchor.appendChild(text);
          anchor.setAttribute("href", "#");
          anchor.setAttribute("appealId", array[i].appealId);
          dateCell.appendChild(anchor);
          newRow.appendChild(dateCell);
          appealList.appeals.appendChild(newRow);
          anchor.addEventListener("click", (e) => {
            e.preventDefault();
            subscribers.hide();
            subscribers.clear();
            reports.hide();
            let date = e.target.innerText;
            subscribers.show(e.target.getAttribute('appealId'), date);
          });
        }
      }
    }
  }

  function Subscribers() {
    this.element = document.querySelector("div.subscribers");
    this.message = document.querySelector("div.subscribers>span");
    this.subs = document.querySelector("div.subscribers>table>tbody");
    this.hide = function hide(){
      this.element.style.display = "none";
      if (buttons !== undefined) buttons.hide();
    }
    this.show = function show(appealId, date){
      makeCall("GET", "GetSubscribersRIA?appealId=" + appealId, null, this.update);
      buttons.show(appealId);
      this.message.innerText = "Here is the list of students who took part the appeal on " + date.toUpperCase();
      this.element.setAttribute("appealId", appealId); //may want to save appealId in a different var
      this.element.removeAttribute("style");
    }
    this.clear = function clear() {
      while(this.subs.children.length>0){
        this.subs.removeChild(this.subs.children[0]);
      }
    }
    this.hide();
    this.clear();
    this.update = function update(req) {
      if (req.readyState === 4 && req.status === 200){
        subscribers.clear();
        editForm.hide();
        let i = 0;
        let array = JSON.parse(req.responseText);
        for (i = 0; i<array.length; i++){
          let newRow = document.createElement("tr");
          //studentId
          let studentIdCell = document.createElement("td");
          let text = document.createTextNode(array[i].studentId);
          studentIdCell.appendChild(text);
          newRow.appendChild(studentIdCell);
          //studentSurname
          let surnameCell = document.createElement("td");
          text = document.createTextNode(array[i].studentSurname);
          surnameCell.appendChild(text);
          newRow.appendChild(surnameCell);
          //studentName
          let nameCell = document.createElement("td");
          text = document.createTextNode(array[i].studentName);
          nameCell.appendChild(text);
          newRow.appendChild(nameCell);
          //email
          let emailCell = document.createElement("td");
          text = document.createTextNode(array[i].email);
          emailCell.appendChild(text);
          newRow.appendChild(emailCell);
          //degreeCourse
          let degreeCourseCell = document.createElement("td");
          text = document.createTextNode(array[i].degreeCourse);
          degreeCourseCell.appendChild(text);
          newRow.appendChild(degreeCourseCell);
          //grade
          let gradeCell = document.createElement("td");
          text = document.createTextNode(array[i].grade.toUpperCase());
          gradeCell.appendChild(text);
          if (array[i].grade == "failed" || array[i].grade == "recalled" ||
              array[i].grade == "absent"){
                //may want to use a more formal method to compare strings
                gradeCell.setAttribute("class", "fail");
              }
          else gradeCell.setAttribute("class", "passed");
          newRow.appendChild(gradeCell);
          //state
          let stateCell = document.createElement("td");
          text = document.createTextNode(array[i].state.toUpperCase());
          stateCell.appendChild(text);
          newRow.appendChild(stateCell);
          //edit button
          if (stateCell.innerText == "ENTERED" || stateCell.innerText == "NOT ENTERED"){
            let editCell = document.createElement("td");
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
            appealInput.setAttribute("name", "appealId");
            appealInput.setAttribute("value", subscribers.element.getAttribute("appealId"));
            form.appendChild(appealInput);
            //create button
            let button = document.createElement("input");
            button.setAttribute("type", "submit");
            button.setAttribute("value", "Edit");
            form.appendChild(button);
            //button.innerText = "Edit";
            //button.appendChild(text);  //it seems appendChild doesn't to work with button
          /*let anchor = document.createElement("a");
            anchor.appendChild(button);
            anchor.setAttribute("href", "#");*/
            editCell.appendChild(form);
            newRow.appendChild(editCell);
            button.addEventListener("click", editButtonCallback);
          }
          subscribers.subs.appendChild(newRow);
        }
      }
    }
    this.toCheckbox = function toCheckbox(){
      let forms = document.querySelectorAll("div.subscribers tbody tr form");
      if (forms.length>0){
        for (let i=0; i<forms.length; i++){
          forms[i].closest("td").setAttribute("class", "checkbox");
          //remove both of the event listeners in case this function got called twice in a row
          forms[i].children[2].removeEventListener("click", editButtonCallback);
          forms[i].children[2].removeEventListener("click", editCheckboxCallback);
          forms[i].children[2].removeAttribute("value");
          forms[i].children[2].setAttribute("type", "checkbox");
          forms[i].children[2].addEventListener("click", editCheckboxCallback);
        }
      }
    }
    this.toButton = function toButton(){
      let forms = document.querySelectorAll("div.subscribers tbody tr form");
      if (forms.length>0){
        for (let i=0; i<forms.length; i++){
          forms[i].closest("td").removeAttribute("class");
          //remove both of the event listeners in case this function got called twice in a row
          forms[i].children[2].removeEventListener("click", editCheckboxCallback);
          forms[i].children[2].removeEventListener("click", editButtonCallback);
          forms[i].children[2].removeAttribute("value");
          forms[i].children[2].setAttribute("type", "submit");
          forms[i].children[2].setAttribute("value", "Edit");
          forms[i].children[2].addEventListener("click", editButtonCallback);
        }
      }
    }
  }

  function editButtonCallback(e){
    e.preventDefault();
    let appealId = e.target.closest("form").children[1].getAttribute("value");
    let studentId = e.target.closest("form").children[0].getAttribute("value");
    editForm.show(appealId, studentId);
  }

  function editCheckboxCallback(e){
    //e.preventDefault();
    //correct implmentation
    let appealId = e.target.closest("form").children[1].getAttribute("value");
    let studentId = e.target.closest("form").children[0].getAttribute("value");
    //editForm.show(appealId, studentId);
  }


  function EditForm(){
    this.studentId = document.querySelector("div.Edit div.studentId");
    this.appealId = document.querySelector("div.Edit input[name='appealId']");
    this.name = document.querySelector("div.Edit div.name");
    this.surname = document.querySelector("div.Edit div.surname");
    this.courseTitle = document.querySelector("div.Edit div.courseTitle");
    this.appealDate = document.querySelector("div.Edit div.appealDate");
    this.grade = document.querySelector("div.Edit div.grade");
    this.element = document.querySelector("div.Edit");
    this.button = document.querySelector("div.Edit input[type='submit']")
    this.hide = function hide(){
      this.element.style.display = "none";
    }
    this.show = function show(appealId, studentId){
      makeCall("GET", "GetModifyRIA?appealId=" + appealId + "&studentId=" + studentId, null, this.update);
      //may want to find a better way to express the url
      this.studentId.setAttribute("value", studentId);
      this.appealId.setAttribute("value", appealId);
      this.element.removeAttribute("style");
    }
    this.clear = function clear() {
      this.studentId.children[2].innerText = "";
      this.name.children[1].innerText = "";
      this.surname.children[1].innerText = "";
      this.courseTitle.children[1].innerText = "";
      this.appealDate.children[1].innerText = "";
    }
    this.hide();
    this.clear();
    this.update = function update(req){
      if (req.readyState === 4 && req.status === 200){
        let objectStrings = req.responseText.split("}"); //needed to parse two different objects
        let grade = JSON.parse(objectStrings[0]+"}");
        let appeal = JSON.parse(objectStrings[1]+"}");
        //introducing content in <nobr> tags
        editForm.studentId.children[2].innerText = grade.studentId;
        editForm.name.children[1].innerText = grade.studentName;
        editForm.surname.children[1].innerText = grade.studentSurname;
        editForm.courseTitle.children[1].innerText = appeal.courseTitle;
        editForm.appealDate.children[1].innerText = appeal.date;
        editForm.studentId.children[1].setAttribute("value", grade.studentId);
        editForm.button.addEventListener("click", (e) => {
          e.preventDefault();
          let form = e.target.closest("form");
          makeCall("POST", "EditRIA", form, function(req){
            if (req.readyState === 4 && req.status === 200){
              let appeal = JSON.parse(req.responseText);
              subscribers.show(appeal.appealId, appeal.date);
            }
          });
        })
      }
    }
  }

  function Buttons(){
    this.element = document.querySelector("div.Buttons");
    this.publish = document.querySelector("div.Buttons form.publish input[type='hidden']");
    this.report = document.querySelector("div.Buttons form.report input[type='hidden']");
    this.allReports = document.querySelector("div.Buttons form.allReports input[type='hidden']");
    this.multipleEdit = document.querySelector("div.Buttons form.multipleEdit input[type='hidden']");
    this.hide = function hide(){
      this.element.style.display = "none";
      this.publish.removeAttribute("value");
      this.report.removeAttribute("value");
      this.allReports.removeAttribute("value");
      this.multipleEdit.removeAttribute("value");
    }
    this.show = function show(appealId){
      this.publish.setAttribute("value", appealId);
      this.report.setAttribute("value", appealId);
      this.allReports.setAttribute("value", appealId);
      this.multipleEdit.setAttribute("value", appealId);
      this.element.removeAttribute("style");
    }
    this.hide();
    this.registerEvents = function registerEvents(){
      let publishSubmit = this.publish.nextSibling.nextSibling; //line to get the buttons
      //may want to find a better way to get the button
      publishSubmit.addEventListener("click", (e) => {
      e.preventDefault();
      editForm.hide();
      let form = e.target.closest("form");
      makeCall("POST", "PublishRIA", form, function(req){
        if (req.readyState === 4 && req.status === 200){
          let appeal = JSON.parse(req.responseText);
          subscribers.show(appeal.appealId, appeal.date);
          }
        });
      });
      let reportSubmit = this.report.nextSibling.nextSibling; //line to get the buttons
      //may want to find a better way to get the button
      reportSubmit.addEventListener("click", (e) => {
      e.preventDefault();
      editForm.hide();
      let form = e.target.closest("form");
      makeCall("POST", "ReportRIA", form, function(req){
        if (req.readyState === 4 && req.status === 200){
          let appeal = JSON.parse(req.responseText);
          subscribers.show(appeal.appealId, appeal.date);
          reports.show("last", appeal.appealId);
          }
        });
      });
      //add event listener to allReports button
      let allReportsSubmit = this.allReports.nextSibling.nextSibling; //line to get the buttons
      //may want to find a better way to get the button
      allReportsSubmit.addEventListener("click", (e) => {
      e.preventDefault();
      editForm.hide();
      appealId = this.allReports.getAttribute("value");
      reports.show("all", appealId);
      });
      //add event listener to multipleEdit button
      let multipleEditSubmit = this.multipleEdit.nextSibling.nextSibling; //line to get the buttons
      //may want to find a better way to get the button
      multipleEditSubmit.addEventListener("click", (e) => {
      e.preventDefault();
      multipleEdit.clear();
      multipleEdit.show(buttons.multipleEdit.getAttribute("value"));
      editForm.hide();
      });
    }
    this.registerEvents();
  }

  function Reports(){
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
    //methods
    this.hide = function hide(){
      this.element.style.display = "none";
    }
    this.showMessage = function showMessage(msgType){
      if (msgType === "last") this.message.innerText="Created report for the appeal:";
      if (msgType === "all") this.message.innerText="Here is shown all information about reports for the appeal:";
    }
    this.show = function show(msgType, appealId){
      reports.clear();
      makeCall("GET", "GetReportsRIA?type=" + msgType + "&appealId=" + appealId, null, this.update);
      //may want to find a better way to express the url
      this.showMessage(msgType);
      this.element.removeAttribute("style");
    }
    this.clear = function clear() {
      //get all report details
      let allDetails = document.querySelector("div.Reports div.details");
      //delete all but first report details
      if (allDetails.length > 1){
        for (let i=1; i<allDetails.length; i++){
          allDetails[i].remove();
        }
      }
      //clear all static (or previously-shown) report content
      this.courseTitle.children[1].innerText = "";
      this.appealDate.children[1].innerText = "";
      this.reportId.children[1].innerText = "";
      this.reportDate.children[1].innerText = "";
      this.reportHour.children[1].innerText = "";
      //remove all static (or previously-shown) rows in the table
      while(this.reportGrades.children.length>0){
        this.reportGrades.removeChild(this.reportGrades.children[0]);
      }
    }
    this.hide();
    this.clear();
    this.update = function update(req){
      if (req.readyState === 4 && req.status === 200){
        let reportsArr = JSON.parse(req.responseText);
        let course = reportsArr[0].appeal.courseTitle;
        reports.courseTitle.children[1].innerText = reportsArr[0].appeal.courseTitle;
        reports.appealDate.children[1].innerText = reportsArr[0].appeal.date;
        for (let i=0; i<reportsArr.length; i++){
          let currentReport = reportsArr[i];
          //select report details section or duplicate an existent one
          let detailsElement;
          if (i===0) detailsElement = reports.details;
          if (i>0) {
            detailsElement = reports.details.cloneNode();
            this.element.appendChild(detailsElement);
          }
          //get details elements (get nobr tags)
          let reportId = detailsElement.children[1].children[1];
          let reportDate = detailsElement.children[2].children[1];
          let reportHour = detailsElement.children[3].children[1];
          let reportGrades = detailsElement.children[4].children[1].children[1];
          reportId.innerText = currentReport.reportId;
          reportDate.innerText = currentReport.creationDate;
          reportHour.innerText = currentReport.creationTime;
          //add grades as rows of the table
          let grades = currentReport.grades;
          for (let j=0; j<grades.length; j++){
            let newRow = document.createElement("tr");
            //studentId
            let studentIdCell = document.createElement("td");
            studentIdCell.innerText = grades[j].studentId;
            newRow.appendChild(studentIdCell);
            //studentSurname
            let surnameCell = document.createElement("td");
            surnameCell.innerText = grades[j].studentSurname;
            newRow.appendChild(surnameCell);
            //studentName
            let nameCell = document.createElement("td");
            nameCell.innerText = grades[j].studentName;
            newRow.appendChild(nameCell);
            //grade
            let gradeCell = document.createElement("td");
            gradeCell.innerText = grades[j].grade.toUpperCase();
            if (gradeCell.innerText == "FAILED" || gradeCell.innerText == "RECALLED" ||
                gradeCell.innerText == "ABSENT"){
                  //may want to use a more formal method to compare strings
                  gradeCell.setAttribute("class", "fail");
                }
            else gradeCell.setAttribute("class", "passed");
            newRow.appendChild(gradeCell);
            reportGrades.appendChild(newRow);
          }
        }
      }
    }
  }

  function MultipleEdit(){
    this.element = document.querySelector("div.MultipleEdit");
    this.closer = document.querySelector("div.MultipleEdit span.closer");
    this.show = function show(appealId){
      makeCall("GET", "GetNotEnteredRIA?appealId=" + appealId, null, multipleEdit.update);
      let mebg = this.element.closest("div.mebg");
      mebg.classList.add("active");
    }
    this.hide = function hide(){
      let mebg = this.element.closest("div.mebg");
      mebg.classList.remove("active");
    }
    this.clear = function clear() {
      let tbody=this.element.children[2].children[1];
      while(tbody.children.length>1){
        tbody.removeChild(tbody.children[1]);
      }
      for (let i=0; i<5; i++){
        tbody.children[0].children[i].innerText = "";
      }
    }
    this.registerEvents = function registerEvents(){
      this.closer.addEventListener("click", function(){
        multipleEdit.hide();
      })
    }
    this.clear();
    this.registerEvents();
    this.update = function update(req){
      if (req.readyState === 4 && req.status === 200){
        let tbody=multipleEdit.element.children[2].children[1];
        let i = 0;
        let array = JSON.parse(req.responseText);
        for (i = 0; i<array.length; i++){
          let newRow;
          if (i===0) {
            newRow = tbody.children[0];
            newRow.removeEventListener("click", rowSelector);
            //may be useful when multipleEdit is used repeatedly
          }
          else {
            newRow = tbody.children[0].cloneNode("deep");
            //used cloning to avoid creation of select forms
          }
          newRow.children[0].innerText = array[i].studentId;
          newRow.children[1].innerText = array[i].studentSurname;
          newRow.children[2].innerText = array[i].studentName;
          newRow.children[3].innerText = array[i].email;
          newRow.children[4].innerText = array[i].degreeCourse;
          let select = newRow.children[5].children[0].children[0];
          select.addEventListener("change", (e) => {
            if (e.target.closest("tr").getAttribute("selected")==="true"){
              let allSelects = document.querySelectorAll("div.MultipleEdit tr[selected='true'] select");
              for (let i=0; i<allSelects.length; i++){
                allSelects[i].selectedIndex = e.target.selectedIndex;
              }
            }
          })
          tbody.appendChild(newRow);
          newRow.addEventListener("click", rowSelector);
        }
        multipleEdit.element.setAttribute("appealId", array[0].appealId);
      }
    }
  }

  function autoclick(grade) {
  let e = new Event("click");
  let options = document.querySelectorAll("MultipleEdit tr[selected=true] option[value=" + grade + "]");
  for (let i=0; i<options.length; i++){
    options[i].dispatchEvent(e);
  }
}

  function autochange(grade){

  }


  function rowSelector(e){
    if (!(e.target instanceof HTMLSelectElement)){
      if (e.target.closest("tr").getAttribute("selected")==="true")
        e.target.closest("tr").removeAttribute("selected");
      else e.target.closest("tr").setAttribute("selected", "true");
    }
  }

  function PageOrchestrator(){
    this.start = function (){
      courseList = new CourseList();
      appealList = new AppealList();
      subscribers = new Subscribers();
      editForm = new EditForm();
      buttons = new Buttons();
      reports = new Reports();
      multipleEdit = new MultipleEdit();
    }
    this.clearAll = function clearAll() {
      let temp = document.querySelector("div[class='appeals']");
      temp.remove();
      temp = document.querySelector("div[class='subscribers']");
      temp.remove();
    }
  }



  function makeCall(method, url, form, callback) {
	    var req = new XMLHttpRequest();
	    req.onreadystatechange = function(e) {
		  e.preventDefault();
	      callback(req)
	    };
	    req.open(method, url);
		if (form !== null) req.send(new FormData(form));
		else req.send();
  }

}())
