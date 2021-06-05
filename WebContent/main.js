(function() {

  var courseList, appealList, subscribers, editForm, pageOrchestrator;

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
    }
    this.show = function show(appealId, date){
      makeCall("GET", "GetSubscribersRIA?appealId=" + appealId, null, this.update);
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
            let button = document.createElement("button");
            button.innerText = "Edit";
            //button.appendChild(text);  //it seems appendChild doesn't to work with button
            let anchor = document.createElement("a");
            anchor.appendChild(button);
            anchor.setAttribute("href", "#");
            editCell.appendChild(anchor);
            newRow.appendChild(editCell);
            anchor.addEventListener("click", (e) => {
              e.preventDefault();
              let appealId = subscribers.element.getAttribute("appealId");
              let studentId = e.target.closest("tr").children[0].innerText;
              editForm.show(appealId, studentId);
            });
          }
          subscribers.subs.appendChild(newRow);
        }
      }
    }
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

/**/


  function PageOrchestrator(){
    this.start = function (){
      courseList = new CourseList();
      appealList = new AppealList();
      subscribers = new Subscribers();
      editForm = new EditForm();
      //this.clearAll();
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
