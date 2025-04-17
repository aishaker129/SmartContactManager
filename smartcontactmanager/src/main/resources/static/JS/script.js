console.log("This is script file")

const toggleSidebar = () => {
	if($(".sidebar").is(":visible")){
		$(".sidebar").css("display","none");
		$(".content").css("margin-left","0%");
	}
	else{
		$(".sidebar").css("display","block");
		$(".content").css("margin-left","20%");
	}
};
/*
const toggleSidebar = () => {
    $(".sidebar").toggle(); // toggles display
    if ($(".sidebar").is(":visible")) {
        $(".content").css("margin-left", "20%");
    } else {
        $(".content").css("margin-left", "0%");
    }
};*/

