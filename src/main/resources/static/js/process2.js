/* 작업 예정 페이지*/

function search() {
    var selectedOption = document.getElementById("dropdown").value;
    var searchKeyword = document.getElementById("search").value;

    // 여기에서 검색 동작을 수행하거나 필요한 로직을 추가하세요.

    // 예를 들어, 콘솔에 선택된 옵션과 검색어를 출력하는 경우:
    console.log("선택된 옵션: " + selectedOption);
    console.log("검색어: " + searchKeyword);
}

//표 부분
var table = document.getElementById("myTable3");
var rowCount = 4; // 초기 행 개수

function addRow() {
    var newRow = table.insertRow();

    for (var i = 0; i < rowCount; i++) {
        var cell = newRow.insertCell();
    }

}
