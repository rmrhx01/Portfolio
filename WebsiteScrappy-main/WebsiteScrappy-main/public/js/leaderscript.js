var tabs = document.querySelectorAll(".lboard_tabs ul li");
var Semanal = document.querySelector(".Semanal");
var Mensual = document.querySelector(".Mensual");
var Anual = document.querySelector(".Anual");
var items = document.querySelectorAll(".lboard_item");

tabs.forEach(function(tab){
    tab.addEventListener("click", function(){
        var currentdatali= tab.getAttribute("data-li");
        
        tabs.forEach(function(tab){
            tab.classList.remove("inicial");
        })

        tab.classList.add("inicial");

        items.forEach(function(item){
            item.style.display = "none";
        })

        if(currentdatali == "Semanal"){
            Semanal.style.display = "block";
        }
        else if(currentdatali == "Mensual"){
            Mensual.style.display = "block";
        }
        else{
            Anual.style.display = "block";
        }
    });
})