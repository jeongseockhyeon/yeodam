document.addEventListener("DOMContentLoaded", () => {
    let isFetching = false; // 중복 요청 방지
    let cursorId = null; // 현재 커서 ID
    const tourList = document.getElementById("tourList");

    function fetchToursWithPagination() {
        if (isFetching) return; // 이미 요청 중이면 종료
        isFetching = true;

        const params = new URLSearchParams();
        if (cursorId) params.append("cursorId", cursorId);
        params.append("pageSize", 10); // 한 페이지에 가져올 데이터 수 (예: 10)

        // 기존 필터가 있다면 여기에 추가
        const filters = {};
        const selectedCategories = [];
        const checkboxes = document.querySelectorAll("#categoryCheckBox input[type='checkbox']:checked");
        checkboxes.forEach((checkbox) => selectedCategories.push(checkbox.value));
        if (selectedCategories.length > 0) filters.categories = selectedCategories.join(",");

        // 필터 조건 추가
        Object.keys(filters).forEach((key) => {
            params.append(key, filters[key]);
        });

        fetch(`/api/tours?${params.toString()}`)
            .then((response) => response.json())
            .then((data) => {
                renderTours(data.content);
                if (data.content.length > 0) {
                    cursorId = data.content[data.content.length - 1].id; // 마지막 요소의 ID 저장
                } else {
                    // 더 이상 데이터가 없으면 이벤트 제거
                    window.removeEventListener("scroll", handleScroll);
                }
                isFetching = false;
            })
            .catch((error) => {
                console.error("상품 데이터 불러오기 실패:", error);
                isFetching = false;
            });
    }

    function renderTours(tours) {
        const fragment = document.createDocumentFragment();
        tours.forEach((tour) => {
            const tourCard = document.createElement("div");
            tourCard.className = "tour-card";
            const primaryGuide = tour.guideInTourResDtos[0];

            const thumbnail = tour.itemImgResDtoList?.[0]?.imgUrl || "/images/default-thumbnail.jpg";
            tourCard.addEventListener("click", () => {
                window.location.href = `/tours/${tour.id}`;
            });

            tourCard.innerHTML = `
                <div class="thumbnail">
                    <img src="${thumbnail}" alt="${tour.tourName}">
                </div>
                <div class="content">
                    <h4>${tour.tourName}</h4>
                    <p>${tour.tourDesc}</p>
                    <div>
                        <span>기간: ${tour.tourPeriod}</span>
                        <span> | 지역: ${tour.tourRegion}</span>
                    </div>
                    <div>
                        <span>가이드: ${
                primaryGuide ? primaryGuide.name : "정보 없음"
            }</span>
                        <span> | 평점: ${
                primaryGuide ? primaryGuide.rating : "N/A"
            }⭐</span>
                        <span> (${primaryGuide ? primaryGuide.reviews : 0}명)</span>
                    </div>
                    <div class="price">${tour.tourPrice.toLocaleString()}원</div>
                    <div>최대 인원: ${tour.maximum}명</div>
                    <div>테마: ${tour.categoryResDtoList
                .map((category) => category.name)
                .join(", ")}</div>
                </div>
            `;
            fragment.appendChild(tourCard);
        });
        tourList.appendChild(fragment);
    }

    function handleScroll() {
        const { scrollTop, scrollHeight, clientHeight } = document.documentElement;
        if (scrollTop + clientHeight >= scrollHeight - 10) {
            // 스크롤이 끝에 도달하면 다음 데이터를 요청
            fetchToursWithPagination();
        }
    }

    // 초기 데이터 로드
    fetchToursWithPagination();

    // 스크롤 이벤트 등록
    window.addEventListener("scroll", handleScroll);
});
