function writeSave(){
	var writeform = document.writeform;
	if(!writeform.writer.value){
		alert("이름을 입력하시오");
		writeform.writer.focus();
		return false;
	}
	if(!writeform.subject.value){
		alert("제목를 입력하시오");
		writeform.subject.focus();
		return false;
	}
	if(!writeform.content.value){
		alert("내용을 입력하시오");
		writeform.content.focus();
		return false;
	}
	if(!writeform.passwd.value){
		alert("비밀번호를 입력하시오");
		writeform.passwd.focus();
		return false;
	}
}
function deleteSave(){
	if(!document.delForm.passwd.value){
	alert("비밀번호를 입력하시오");
	document.delForm.passwd.focus();
	return false;
	}
}