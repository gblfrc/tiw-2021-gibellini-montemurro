(function() {

  var courseList;

  window.addEventListener("load", () => {
    courseList = new CourseList();
    courseList.clear();
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
  }












}())
