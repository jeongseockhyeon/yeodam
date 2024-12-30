document.addEventListener("DOMContentLoaded", () => {
    let isFetching = false; // 중복 요청 방지
    let cursorId = null; // 현재 커서 ID
    const pageSize = 10; // 한 페이지에 가져올 데이터 수

    // 여행 상품 목록을 fetch API로 로드하는 함수
    function loadTours() {
        if (isFetching) return; // 이미 요청 중이면 종료
        isFetching = true;

        const params = new URLSearchParams();
        params.append("pageSize", pageSize);
        if (cursorId) params.append("cursorId", cursorId);

        fetch(`/api/tours/seller-tour?${params.toString()}`)
            .then((response) => response.json())
            .then((data) => {
                renderTours(data.content); // 응답의 content에 여행 상품 데이터가 있다고 가정
                if (data.content.length > 0) {
                    cursorId = data.content[data.content.length - 1].id; // 마지막 요소의 ID 저장
                } else {
                    // 더 이상 데이터가 없으면 이벤트 제거
                    window.removeEventListener("scroll", handleScroll);
                }
                isFetching = false;
            })
            .catch((error) => {
                console.error("여행 상품을 가져오는 중 오류 발생:", error);
                isFetching = false;
            });
    }

    // 여행 상품 데이터를 테이블에 렌더링하는 함수
    function renderTours(tours) {
        const tableBody = document.querySelector("#tourTable tbody");
        const fragment = document.createDocumentFragment(); // DocumentFragment 생성

        tours.forEach((tour, index) => {
            const row = document.createElement("tr");

            row.innerHTML = `
            <td>${index + 1}</td>
            <td>${tour.tourName}</td>
            <td>${tour.guideInTourResDtos && tour.guideInTourResDtos.length > 0 ? tour.guideInTourResDtos[0].name : '없음'}</td>
            <td>${tour.tourPrice}원</td>
            <td>
                <button class="edit-button" onclick="location.href='/tours/${tour.id}/update'">수정</button>
                <button class="delete-button" data-id="${tour.id}">삭제</button>
            </td>
            <td>${tour.active ? 'O' : 'X'}</td> 
        `;

            fragment.appendChild(row); // Fragment에 row 추가
        });

        tableBody.appendChild(fragment); // 한 번에 DOM에 추가
    }

    // 페이지 로드 시 여행 상품 목록을 불러옵니다.
    loadTours();

    // 스크롤 이벤트 처리 (무한 스크롤)
    function handleScroll() {
        const scrollY = window.scrollY || window.pageYOffset;
        const documentHeight = document.documentElement.scrollHeight;
        const windowHeight = window.innerHeight;

        if (scrollY + windowHeight >= documentHeight - 200) {
            loadTours();
        }
    }

    // 스크롤 이벤트 리스너 추가
    window.addEventListener("scroll", handleScroll);
});