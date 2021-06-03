(function() {

  var courseList, appealList, subscribers, pageOrchestrator;

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
            appealList.show(e.target.getAttribute('courseId'));
          });
        }
      }
    }
    makeCall("GET", "GetCoursesRIA", this.update);
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
    this.show = function show(courseId){
      makeCall("GET", "GetAppealsRIA?courseId=" + courseId, this.update);
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
          //anchor.addEventListener();
        }
      }
    }
  }

  function Subscribers() {
    this.element = document.querySelector("div[class='subscribers']");
    this.message = document.querySelector("div[class='subscribers']>span");
    this.subs = document.querySelector("div[class='subscribers']>table>tbody");
    this.hide = function hide(){
      this.element.style.display = "none";
    }
    this.show = function show(){
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
      /*
      if (req.readyState === 4 && req.status === 200){
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
          anchor.setAttribute("href", "GetAppealsRIA?courseId=" + array[i].courseId); //certainly needs updating
          appealLinkCell.appendChild(anchor);
          newRow.appendChild(appealLinkCell);
          //may want to check why in this exact moment this.courses is undefined
          this.courses = document.querySelector("div[class='courses']>table>tbody");
          this.courses.appendChild(newRow);
          anchor.addEventListener();//implement*/
        }
  }

  function PageOrchestrator(){
    this.start = function (){
      courseList = new CourseList();
      appealList = new AppealList();
      subscribers = new Subscribers();
      //this.clearAll();
    }
    this.clearAll = function clearAll() {
      let temp = document.querySelector("div[class='appeals']");
      temp.remove();
      temp = document.querySelector("div[class='subscribers']");
      temp.remove();
    }
  }

  function makeCall(method, url, callback) {
	    var req = new XMLHttpRequest();
	    req.onreadystatechange = function() {
	      callback(req)
	    };
	    req.open(method, url);
      req.send();
	  }

}())
