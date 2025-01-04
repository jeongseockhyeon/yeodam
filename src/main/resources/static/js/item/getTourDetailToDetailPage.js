const id = getTourItemIdFromUrl();

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
    document.getElementById('tourName').textContent = data.tourName;
    document.getElementById('mainImage').src = data.itemImgResDtoList[0]?.imgUrl || "/images/default-thumbnail.jpg";
    document.getElementById('additionalImage').style.display = data.itemImgResDtoList.length > 1 ? 'block' : 'none';
    if (data.itemImgResDtoList[1]) {
        document.getElementById('additionalImage').src = data.itemImgResDtoList[1].imgUrl;
    }

    document.getElementById('guideName').textContent = data.guideInTourResDtos.length > 0 ? data.guideInTourResDtos[0].name : '없음';
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
        button.setAttribute('data-name', guide.name);  // 가이드 이름
        button.setAttribute('data-id', guide.id);  // 가이드 ID
        button.classList.add('guide-option');
        button.style.cssText = 'display: block; width: 100%; text-align: left; padding: 5px;';
        button.onclick = () => selectGuide(button);  // 클릭 시 selectGuide 호출
        listItem.appendChild(button);
        guideList.appendChild(listItem);
    });
}

function toggleGuideDropdown() {
    const dropdown = document.getElementById('guideDropdown');
    dropdown.style.display = dropdown.style.display === 'none' ? 'block' : 'none';
}
function selectGuide(button) {
    const guideId = button.getAttribute('data-id'); // 클릭한 버튼의 가이드 ID
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



