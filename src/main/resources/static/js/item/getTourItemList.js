document.addEventListener("DOMContentLoaded", () => {
    let isFetching = false; // 중복 요청 방지
    let cursorId = null; // 현재 커서 ID
    let cursorPrice = null;
    let cursorRate = null;
    const tourList = document.getElementById("tourList");

    const params = new URLSearchParams(window.location.search);
    const keyword = params.get("keyword") || "";


    function fetchToursWithPagination(reset = false) {
        if (isFetching) return; // 이미 요청 중이면 종료
        isFetching = true;

        // 필터가 변경되면 기존 데이터를 초기화
        if (reset) {
            tourList.innerHTML = ""; // 기존 데이터 비우기
            cursorId = null; // 커서 초기화
            cursorPrice = null; // 가격 커서 초기화
        }

        const params = new URLSearchParams();
        params.append("pageSize", 6); // 한 페이지에 가져올 데이터 수

        // 검색어를 표시하거나 유지
        const searchInput = document.querySelector('input[name="keyword"]');
        if (searchInput) {
            searchInput.value = keyword;
        }

        if (keyword) params.append("keyword", keyword);

        // 필터 조건 추가
        const selectedCategories = [];
        const checkboxes = document.querySelectorAll("#categoryCheckBox input[type='checkbox']:checked");
        checkboxes.forEach((checkbox) => {
            const categoryName = document.querySelector(`label[for='category-${checkbox.value}']`).textContent;
            selectedCategories.push(categoryName);
        });
        if (selectedCategories.length > 0) params.append("categories", selectedCategories.join(","));

        const period = document.getElementById("tourPeriod").value;
        if (period) params.append("period", period);

        const region = document.getElementById("tourRegion").value;
        if (region) params.append("region", region);

        const minPrice = document.getElementById("minPrice").value;
        const maxPrice = document.getElementById("maxPrice").value;
        if (minPrice || maxPrice) {
            params.append("minPrice", minPrice || 0);
            params.append("maxPrice", maxPrice || 9999999);
        }

        const sortBy = document.querySelector("#sortBy").value;
        const order = document.querySelector("#sortOrder").value;
        if (sortBy) params.append("sortBy", sortBy);
        if (order) params.append("order", order);

        // 정렬 방식에 따라 커서 설정
        if (sortBy === "price" && cursorPrice !== null) {
            params.append("cursorPrice", cursorPrice);
        } else if(sortBy === "rate" && cursorRate !== null){
            params.append("cursorRate", cursorRate);
        } else if (cursorId !== null) {
            params.append("cursorId", cursorId);
        }

        fetch(`/api/tours?${params.toString()}`)
            .then((response) => response.json())
            .then((data) => {
                renderTours(data.content);

                if (data.content.length > 0) {
                    // 정렬 방식에 따라 커서 업데이트
                    if (sortBy === "price") {
                        cursorPrice = data.content[data.content.length - 1].tourPrice; // 마지막 요소의 가격 저장
                    } else if (sortBy === "rate") {
                        cursorRate = data.content[data.content.length - 1].rate; //마지막 요소의 평점 저장
                    }
                    cursorId = data.content[data.content.length - 1].id; // 마지막 요소의 ID 저장
                }
                else {
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
                        <span> | 평점: ${tour.rate}⭐</span>
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

    // 필터 적용 버튼 클릭 이벤트
    document.getElementById("applyFiltersBtn").addEventListener("click", () => {
        fetchToursWithPagination(true); // 필터가 변경되었으므로 데이터 초기화
    });

    // 초기 데이터 로드
    fetchToursWithPagination();

    // 스크롤 이벤트 등록
    window.addEventListener("scroll", handleScroll);
});