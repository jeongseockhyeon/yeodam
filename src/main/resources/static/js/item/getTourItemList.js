document.addEventListener("DOMContentLoaded", () => {
    const categoryCheckBox = document.getElementById("categoryCheckBox");
    const tourList = document.getElementById("tourList");
    const applyFiltersBtn = document.getElementById("applyFiltersBtn");

    function fetchTours(filters = {}) {
        const params = new URLSearchParams();
        Object.keys(filters).forEach((key) => {
            if (filters[key] && filters[key].length > 0){
                params.append(key, filters[key]);
            }
        });

        fetch(`/api/tours?${params.toString()}`)
            .then((response) => response.json())
            .then((data) => {
                renderTours(data.content);
            })
            .catch((error) => console.error("상품 데이터 불러오기 실패:", error));
    }

// 상품 렌더링 함수
    function renderTours(tours) {
        tourList.innerHTML = ""; // 초기화
        const fragment = document.createDocumentFragment(); // DocumentFragment 생성

        tours.forEach((tour) => {
            const tourCard = document.createElement("div");
            tourCard.className = "tour-card";
            const primaryGuide = tour.guideInTourResDtos[0];

            // 첫 번째 이미지를 썸네일로 사용
            const thumbnail = tour.itemImgResDtoList && tour.itemImgResDtoList.length > 0
                ? tour.itemImgResDtoList[0].imgUrl
                : "default-thumbnail.jpg"; // 기본 이미지 경로

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
            <div class="price">${tour.tourPrice.toLocaleString()}원 ~</div>
            <div>최대 인원: ${tour.maximum}명</div>
            <div>
                카테고리: ${tour.categoryResDtoList
                .map((category) => category.categoryName)
                .join(", ")}
            </div>
        </div>
    `;

            fragment.appendChild(tourCard); // Fragment에 추가
        });

        tourList.appendChild(fragment); // Fragment를 DOM에 추가
    }

    // 초기 데이터 로드
    fetchTours();

    // 공통 필터링 로직
    function applyFilters() {
        const filters = {};
        const selectedCategories = [];
        const checkboxes = categoryCheckBox.querySelectorAll("input[type='checkbox']:checked");

        checkboxes.forEach((checkbox) => {
            selectedCategories.push(checkbox.value);
        });

        // 카테고리 필터 추가
        if (selectedCategories.length > 0) {
            filters.categories = selectedCategories;
        }

        // 가격 필터
        const minPrice = document.getElementById("minPrice").value;
        const maxPrice = document.getElementById("maxPrice").value;
        if (minPrice) filters.minPrice = minPrice;
        if (maxPrice) filters.maxPrice = maxPrice;

        // 기간 필터
        const period = document.querySelector(".dropdown-content button[data-period].selected");
        if (period) filters.period = period.value;

        // 지역 필터
        const region = document.getElementById("region").value;
        if (region) filters.region = region;

        // 필터를 기반으로 투어 데이터 가져오기
        fetchTours(filters);
    }

    // 필터 적용 버튼 클릭 이벤트
    applyFiltersBtn.addEventListener("click", (event) => {
        event.preventDefault(); // 폼 자동 제출 방지
        applyFilters();
    });
});
