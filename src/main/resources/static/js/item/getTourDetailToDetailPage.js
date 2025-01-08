const id = getTourItemIdFromUrl();
let guideId = null;
const slider = document.querySelector(".slides");
const prevButton = document.querySelector(".prev");
const nextButton = document.querySelector(".next");
async function fetchTourData() {
    try {
        const response = await fetch(`/api/tours/${id}`);
        if (!response.ok) {
            throw new Error('데이터를 가져오는데 실패했습니다.');
        }
        const data = await response.json();
        renderTourData(data);
        initializeCalendar(data)
    } catch (error) {
        console.error(error.message);
    }
}

function renderTourData(data) {
    // 최대 5개의 이미지를 추가
    data.itemImgResDtoList.slice(0, 5).forEach(image => {
        const imgElement = document.createElement("img");
        imgElement.src = image.imgUrl || "/images/default-thumbnail.jpg";
        slider.appendChild(imgElement);
    });

    let currentIndex = 0;

    // 슬라이드 이동 업데이트 함수
    const updateSlidePosition = () => {
        const width = slider.querySelector("img").clientWidth;
        slider.style.transform = `translateX(-${currentIndex * width}px)`;

        // 버튼 표시 상태 업데이트
        prevButton.style.display = currentIndex === 0 ? "none" : "block";
        nextButton.style.display = currentIndex === data.itemImgResDtoList.length - 1 ? "none" : "block";
    };

    // 초기 버튼 상태 업데이트
    updateSlidePosition();

    // 이전 버튼 클릭 이벤트
    prevButton.addEventListener("click", () => {
        if (currentIndex > 0) {
            currentIndex--;
            updateSlidePosition();
        }
    });

    // 다음 버튼 클릭 이벤트
    nextButton.addEventListener("click", () => {
        if (currentIndex < data.itemImgResDtoList.length - 1) {
            currentIndex++;
            updateSlidePosition();
        }
    });

    document.getElementById('tourName').textContent = data.tourName;

    document.getElementById('guideName').textContent =
        data.guideInTourResDtos.length > 0 ? data.guideInTourResDtos[0].name : '없음';
    document.getElementById('tourPeriod').textContent = data.tourPeriod;
    document.getElementById('maximum').textContent = data.maximum;

    const categoryContainer = document.getElementById('categories');
    categoryContainer.textContent = data.categoryResDtoList.map(category => category.name).join(', ');

    document.getElementById('tourDesc').textContent = data.tourDesc;
    document.getElementById('tourPrice').textContent = `가격: ${data.tourPrice.toLocaleString()}원`;

    const guideList = document.getElementById('guideList');
    guideList.innerHTML = ''; // 기존 가이드를 초기화
    data.guideInTourResDtos.forEach(guide => {
        const listItem = document.createElement('li');
        const button = document.createElement('button');
        button.textContent = guide.name;
        button.setAttribute('data-name', guide.name); // 가이드 이름
        button.setAttribute('data-id', guide.id); // 가이드 ID
        button.classList.add('guide-option');
        button.style.cssText = 'display: block; width: 100%; text-align: left; padding: 5px;';
        button.onclick = () => selectGuide(button); // 클릭 시 selectGuide 호출
        listItem.appendChild(button);
        guideList.appendChild(listItem);
    });

    // 슬라이더 이미지가 로드되었을 때 크기 업데이트
    window.addEventListener("resize", updateSlidePosition);
}

function toggleGuideDropdown() {
    const dropdown = document.getElementById('guideDropdown');
    dropdown.style.display = dropdown.style.display === 'none' ? 'block' : 'none';
}
function selectGuide(button) {
    guideId = button.getAttribute('data-id'); // 클릭한 버튼의 가이드 ID
    const guideName = button.getAttribute('data-name'); // 클릭한 버튼의 가이드 이름

    if (!guideId || !guideName) {
        alert('유효하지 않은 가이드 정보입니다.');
        return;
    }

    // 선택된 가이드 표시 및 데이터 설정
    const selectedGuideElement = document.getElementById('selectedGuide');
    selectedGuideElement.innerText = `선택된 가이드: ${guideName}`;
    selectedGuideElement.setAttribute('data-guide-id', guideId); // 선택된 가이드 ID 설정

    // 예약 일정 불러오기
    fetchAndDisplayReservations(guideId); // 해당 가이드의 예약 일정을 가져와서 달력에 표시

    // 선택된 버튼 스타일 업데이트
    document.querySelectorAll('.guide-option').forEach(btn => {
        btn.classList.remove('selected-guide'); // 기존 선택 스타일 제거
    });
    button.classList.add('selected-guide'); // 선택된 가이드 스타일 적용
}

// 데이터 가져오기 실행
fetchTourData();

function getTourItemIdFromUrl() {
    const pathSegments = window.location.pathname.split('/');
    return pathSegments[2];
}



