(function() {

  var courseList, pageOrchestrator;

  window.addEventListener("load", () => {
    pageOrchestrator = new PageOrchestrator();
    pageOrchestrator.start();
    courseList = new CourseList();
    courseList.clear();
    courseList.start();
  });


  function CourseList(){
    this.message = document.querySelector("div[class='courses']>span");
    this.courses = document.querySelector("div[class='courses']>table>tbody");
    //introduce function to clear the table but the header row
    this.clear = function clear() {
      while(this.courses.children.length>0){
        this.courses.removeChild(this.courses.children[0]);
      }
    }
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
          anchor.setAttribute("href", "GetAppealsRIA"); //certainly needs updating
          appealLinkCell.appendChild(anchor);
          newRow.appendChild(appealLinkCell);
          //may want to check why in this exact moment this.courses is undefined
          this.courses = document.querySelector("div[class='courses']>table>tbody");
          this.courses.appendChild(newRow);
        }
      }
    }
    this.start = function start(){
      makeCall("GET", "GetCoursesRIA", this.update);
    }

  }



  function PageOrchestrator(){
    this.start = function (){
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
