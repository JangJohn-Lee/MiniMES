    // 달력
    // 현재 날짜 정보 가져오기
    let today = new Date();
    let currentMonth = today.getMonth();
    let currentYear = today.getFullYear();
    let currentDate = today.getDate();

    // 달력 생성 함수
    function generateCalendar(year, month, date) {
        let calendar = document.getElementById("calendar");
        let header = document.getElementById("calendar-header");

        // 기존 달력 초기화
        while (calendar.rows.length > 1) {
            calendar.deleteRow(-1);
        }

        // 월의 첫 번째 날 구하기
        let firstDay = new Date(year, month, 1);
        let startingDay = firstDay.getDay();

        // 월의 총 일수 구하기
        let monthLength = new Date(year, month + 1, 0).getDate();

        // 월 정보 표시
        let monthNames = [
            "1월", "2월", "3월", "4월", "5월", "6월",
            "7월", "8월", "9월", "10월", "11월", "12월"
        ];
        header.innerHTML = monthNames[month] + " 작업 일정"; // 월 정보 변경

        let day = 1;
        let dateSet = false;

        // 달력 행 추가
        for (let i = 0; i < 5; i++) {
            let row = calendar.insertRow(-1);

            // 요일 별 날짜 채우기
            for (let j = 0; j < 7; j++) {
                if (i === 0 && j < startingDay) {
                    let cell = row.insertCell(-1);
                    cell.innerHTML = "";
                } else if (day > monthLength) {
                    // 빈 공간을 네모칸으로 채우기
                    let cell = row.insertCell(-1);
                    cell.innerHTML = "";
                    cell.className = "empty-cell";
                } else {
                    let cell = row.insertCell(-1);
                    cell.innerHTML = day;

                    // 현재 날짜에 표시 스타일 적용
                    if (!dateSet && day === date) {
                        cell.style.backgroundColor = "lightblue";
                        dateSet = true;
                    }
                    // 클릭 이벤트 핸들러 함수
                    function handleClick(localName) {

                        let day = parseInt(this.innerHTML); // 클릭한 일 수 가져오기
                        let modal = document.getElementById("myModal");
                        let modalTitle = document.getElementById("modalTitle");
                        let modalContent = document.getElementById("modalContent");



                        // 모달 창 열기
                        modal.style.display = "block";

                        // 모달 내의 "닫기" 버튼 클릭 이벤트 핸들러 함수
                        let closeButton = document.getElementsByClassName("close")[0];
                        closeButton.addEventListener("click", function() {
                            modal.style.display = "none";
                        });

                        // 모달 창을 팝업 형식으로 띄우기
                        modal.style.top = "50%"; // 화면 세로 중앙에 위치
                        modal.style.transform = "translateY(-50%)"; // 세로 방향으로 50% 이동

                        //모달창 외부 클릭 이벤트 처리
                        window.addEventListener("click", handleOutSideClick);


                            // Ajax 요청 생성
                            let xhr = new XMLHttpRequest();

                            // 요청 설정
                            let url = "/process2/{day}"; // 경로에 변수를 포함하는 형식으로 작성합니다
                            url = url.replace("{day}", day); // 변수 값을 실제 값으로 치환합니다
                            xhr.open("GET", url, true); // 치환된 URL을 사용하여 GET 요청을 보냅니다

                        // 모달 내용 설정
                        modalTitle.innerText = day + "일의 작업 일정";
                        // modalContent.innerHTML = "<p>" + a + "</p><p>" + b + "</p><p>" + c + "</p>";

                        xhr.onreadystatechange = function() {
                            if (xhr.readyState === 4 && xhr.status === 200) {
                                let responseData = JSON.parse(xhr.responseText);

                                // 모달 내용을 동적으로 설정
                                document.getElementById("modalTitle").innerText = day + "일의 작업 일정";
                                document.getElementById("a").innerText = responseData.a;
                                document.getElementById("b").innerText = responseData.b;
                                document.getElementById("c").innerText = responseData.c;
                                document.getElementById("dd").href = "/excel/cal/"+day;

                                // 여기에 modalContent.innerHTML 설정
                                // document.getElementById("modalContent").innerHTML = responseData.content;
                            }
                        };

                        // 요청 전송
                        xhr.send();

                        // 엑셀 다운로드 버튼
                        // document.getElementById("XlsxDownloadBtn").addEventListener("click", function() {
                        //     generateExcel();
                        // });
                    }

                    // 달력 생성 함수 내에서 클릭 이벤트 추가
                    cell.addEventListener("click", handleClick);

                    day++;
                }
            }
        }
    }


    // 초기 달력 생성
    generateCalendar(currentYear, currentMonth, currentDate);

    // 모달창 외부 클릭 이벤트 핸들러 함수
    function handleOutSideClick(event){
        let modal = document.getElementById("myModal");
        if(event.target === modal){
            modal.style.display="none"; //모달창 닫기
        }
    }




    /* ---------------------------------------------------------------------------------------------------- */
    // 데이터 가져오기 함수
    // function getDataFromServer() {
    //     // Ajax 요청 생성
    //     var xhr = new XMLHttpRequest();
    //
    //     // 요청 설정
    //     let url = "/process2/{day}"; // 경로에 변수를 포함하는 형식으로 작성합니다
    //     url = url.replace("{day}", day); // 변수 값을 실제 값으로 치환합니다
    //     xhr.open("GET", url, true); // 치환된 URL을 사용하여 GET 요청을 보냅니다
    //
    //     // 응답 처리
    //     xhr.onreadystatechange = function() {
    //         if (xhr.readyState === 4 && xhr.status === 200) {
    //             var responseData = JSON.parse(xhr.responseText);
    //
    //             // 서버로부터 받은 데이터를 활용하여 원하는 동작 수행
    //             generateCalendar(responseData.year, responseData.month, responseData.date);
    //         }
    //     };
    //
    //     // 요청 전송
    //     xhr.send();
    // }
    //
    // // 페이지 로드 시 서버로부터 데이터 가져오기
    // window.onload = function() {
    //     getDataFromServer();
    // };


    // /* 엑셀 */
    // function generateExcel() {
    //     // 엑셀 워크북 생성
    //     let workbook = XLSX.utils.book_new();
    //
    //     // 엑셀 시트 생성
    //     let sheetData = [
    //         ["일정 내용", "작업자", "시작일", "종료일"],
    //         // 일정 데이터를 추가하거나 동적으로 생성하는 로직을 작성하세요.
    //         ["일정1", "작업자1", "2023-05-26", "2023-05-27"],
    //         //      ["일정2", "작업자2", "2023-05-28", "2023-05-29"],
    //     ];
    //     let sheet = XLSX.utils.aoa_to_sheet(sheetData);
    //
    //     // 워크북에 시트 추가
    //     XLSX.utils.book_append_sheet(workbook, sheet, "작업 일정");
    //
    //     // 엑셀 파일 저장
    //     let excelBuffer = XLSX.write(workbook, { bookType: "xlsx", type: "array" });
    //     let blob = new Blob([excelBuffer], { type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" });
    //     saveAs(blob, "작업일정.xlsx");
    // }





    // function updateCalendar(data) {
    //     // 달력 업데이트 작업 수행
    //
    //     // 달력 초기화
    //     var calendar = document.getElementById("calendar");
    //     while (calendar.rows.length > 1) {
    //         calendar.deleteRow(-1);
    //     }
    //
    //     // 가져온 일정 데이터를 기반으로 달력에 일정 출력
    //     // 예시로 가져온 일정 데이터는 "일정"이라는 키로 각 날짜에 해당하는 일정을 가지고 있는 배열이라고 가정합니다.
    //     for (var i = 0; i < 5; i++) {
    //         var row = calendar.insertRow(-1);
    //         for (var j = 0; j < 7; j++) {
    //             var day = i * 7 + j + 1;
    //             var cell = row.insertCell(-1);
    //             if (data[day] && data[day].length > 0) {
    //                 // 해당 날짜에 일정이 있는 경우
    //                 cell.innerHTML = day + "<br>";
    //                 for (var k = 0; k < data[day].length; k++) {
    //                     cell.innerHTML += data[day][k] + "<br>";
    //                 }
    //             } else {
    //                 // 해당 날짜에 일정이 없는 경우
    //                 cell.innerHTML = day;
    //             }
    //         }
    //     }
    // }


    // $.ajax({
    //     url: '/',
    //     method: 'GET',
    //     dataType: 'json',
    //     success: function(data) {
    //         // 일정 데이터를 성공적으로 가져왔을 때의 처리
    //         // 달력을 업데이트하는 함수 호출
    //         updateCalendar(data);
    //     },
    //     error: function() {
    //         // 일정 데이터를 가져오지 못했을 때의 처리
    //         console.log('일정 데이터를 가져오는데 실패했습니다.');
    //     }
    // });





