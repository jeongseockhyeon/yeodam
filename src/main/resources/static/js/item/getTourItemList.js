document.addEventListener("DOMContentLoaded", () => {
    let isFetching = false; // 중복 요청 방지
    let cursorId = null; // 현재 커서 ID
    let cursorPrice = null;
    let cursorRate = null;
    let cursorReviewCount = null;
    const tourList = document.getElementById("tourList");

    const params = new URLSearchParams(window.location.search);
    const keyword = params.get("keyword") || "";

    // 로딩 중 표시
    function showLoadingIndicator() {
        const loadingIndicator = document.createElement("div");
        loadingIndicator.className = "loading-indicator";
        loadingIndicator.textContent = "로딩 중...";
        tourList.appendChild(loadingIndicator);
    }

    // 로딩 중 표시 제거
    function hideLoadingIndicator() {
        const loadingIndicator = document.querySelector(".loading-indicator");
        if (loadingIndicator) loadingIndicator.remove();
    }

    // 필터 상태 저장
    function saveFilterState() {
        const filters = {
            sortBy: document.getElementById("sortBy").value,
            order: document.getElementById("sortOrder").value,
            minPrice: document.getElementById("minPrice").value,
            maxPrice: document.getElementById("maxPrice").value,
        };
        localStorage.setItem("filters", JSON.stringify(filters));
        localStorage.setItem("selectedRegion", document.getElementById("tourRegion").value);
        localStorage.setItem("selectedPeriod",document.getElementById("tourPeriod").value)
    }

    // 필터 상태 복원
    function restoreFilterState() {
        const savedFilters = JSON.parse(localStorage.getItem("filters")) || {};
        document.getElementById("tourPeriod").value = localStorage.getItem("selectedPeriod") || "";
        document.getElementById("tourRegion").value =  localStorage.getItem("selectedRegion") || "";
        document.getElementById("sortBy").value = savedFilters.sortBy || "";
        document.getElementById("sortOrder").value = savedFilters.order || "";

        const minPrice = savedFilters.minPrice || "";
        const maxPrice = savedFilters.maxPrice || "";
        document.getElementById("minPrice").value = minPrice;
        document.getElementById("maxPrice").value = maxPrice;
    }

    function fetchToursWithPagination(reset = false) {
        if (isFetching) return; // 이미 요청 중이면 종료
        isFetching = true;

        if (reset) {
            tourList.innerHTML = ""; // 기존 데이터 비우기
            cursorId = null; // 커서 초기화
            cursorPrice = null;
            cursorRate = null;
            cursorReviewCount = null;
        }

        // 로딩 중 표시
        showLoadingIndicator();

        const params = new URLSearchParams();
        params.append("pageSize", 6);

        const searchInput = document.querySelector('input[name="keyword"]');
        if (searchInput) {
            searchInput.value = keyword;
        }

        if (keyword) params.append("keyword", keyword);

        // 로컬스토리지에서 필터 값 가져오기
        const regionParam = localStorage.getItem("selectedRegion");
        const themeParam = localStorage.getItem("selectedThemes");
        const periodParam = localStorage.getItem("selectedPeriod");

        // 로컬스토리지에서 가져온 값을 파라미터로 추가
        if (regionParam) {
            params.append("region", regionParam);
        }
        if (themeParam) {
            params.append("categories", themeParam);
        }
        if (periodParam) {
            params.append("period", periodParam);
        }

        const selectedCategories = [];
        const checkboxes = document.querySelectorAll("#categoryCheckBox input[type='checkbox']:checked");
        checkboxes.forEach((checkbox) => {
            const categoryName = document.querySelector(`label[for='category-${checkbox.value}']`).textContent;
            selectedCategories.push(categoryName);
        });
        if (selectedCategories.length > 0) params.append("categories", selectedCategories.join(","));



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

        if (sortBy === "price" && cursorPrice !== null) {
            params.append("cursorPrice", cursorPrice);
        } else if (sortBy === "rate" && cursorRate !== null) {
            params.append("cursorRate", cursorRate);
        } else if (sortBy === "reviews" && cursorReviewCount !== null) {
            params.append("cursorReviewCount", cursorReviewCount);
        }
        if (cursorId !== null) params.append("cursorId", cursorId);


        fetch(`/api/tours?${params.toString()}`)
            .then((response) => response.json())
            .then((data) => {
                hideLoadingIndicator(); // 로딩 중 표시 제거
                renderTours(data.content);

                localStorage.removeItem("selectedThemes")

                if (data.content.length > 0) {
                    // cursorId 및 정렬 기준에 따라 값 갱신
                    if (sortBy === "price") {
                        cursorPrice = data.content[data.content.length - 1].tourPrice;
                    } else if (sortBy === "rate") {
                        cursorRate = data.content[data.content.length - 1].rate;
                    } /*else if (sortBy === "reviews") {
                        cursorReviewCount = data.content[data.content.length - 1].reviewCount;
                    }*/
                    cursorId = data.content[data.content.length - 1].id;

                    // last 값이 true 인 경우 스크롤 이벤트를 제거하여 더 이상 조회하지 않음
                    if (data.last) {
                        window.removeEventListener("scroll", handleScroll);
                    }
                }
                if(data.content.length === 0){
                    window.removeEventListener("scroll", handleScroll);
                }
                isFetching = false;
            })
            .catch((error) => {
                hideLoadingIndicator(); // 로딩 중 표시 제거
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
                        <span>가이드: ${primaryGuide ? primaryGuide.name : "정보 없음"}</span>
                        <span> | ⭐${tour.rate}</span>
                        <span> (${tour.reviewCount}명)</span>
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
            fetchToursWithPagination();
        }
    }

    restoreFilterState(); // 필터 상태 복원
    fetchToursWithPagination();
    window.addEventListener("scroll", handleScroll);

    document.getElementById("applyFiltersBtn").addEventListener("click", () => {
        saveFilterState(); // 필터 상태 저장
        fetchToursWithPagination(true);
        window.addEventListener("scroll", handleScroll);
    });
});