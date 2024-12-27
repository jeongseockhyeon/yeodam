document.addEventListener("DOMContentLoaded", () => {
    const id = getTourItemIdFromUrl(); // URL에서 상품 ID 추출
    const activeBtn = document.getElementById('activeBtn');

    // 상품 데이터 로드 및 폼 초기화
    fetch(`/api/tours/${id}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("상품 데이터를 불러오지 못했습니다.");
            }
            return response.json();
        })
        .then(data => {
            populateForm(data);
            updateButton(data.active); // 초기 버튼 상태 설정
        })
        .catch(error => {
            console.error("Error fetching product data:", error);
            alert("상품 정보를 불러오는 데 문제가 발생했습니다.");
        });

// 버튼 상태 업데이트 함수
    function updateButton(isActive) {
        if (isActive) {
            activeBtn.textContent = '비활성화'; // 활성화 상태 -> 비활성화로 변경 가능
            activeBtn.classList.add('inactive');
            activeBtn.classList.remove('active');
        } else {
            activeBtn.textContent = '활성화'; // 비활성화 상태 -> 활성화로 변경 가능
            activeBtn.classList.add('active');
            activeBtn.classList.remove('inactive');
        }
    }

// 활성화 상태 변경 요청
    async function toggleActiveStatus() {
        try {
            const currentActive = activeBtn.textContent === '비활성화'; // 현재 상태 확인
            const response = await fetch(`/api/items/${id}/active`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ active: !currentActive }), // 반대 상태로 변경 요청
            });

            if (!response.ok) throw new Error('Failed to update active status');

            const updatedActive = await response.json(); // 서버에서 반환된 boolean 값
            updateButton(updatedActive); // 버튼 상태 업데이트
            alert(`상품 상태가 ${updatedActive ? '활성화' : '비활성화'}되었습니다.`);
        } catch (error) {
            console.error(error);
            alert('상태 변경에 실패했습니다.');
        }
    }

// 초기 로딩 및 이벤트 리스너
    activeBtn.addEventListener('click', toggleActiveStatus);
});


// URL에서 상품 ID 추출
function getTourItemIdFromUrl() {
    const pathSegments = window.location.pathname.split('/');
    return pathSegments[2];
}

// 폼 채우기
function populateForm(data) {
    document.getElementById("tourName").value = data.tourName || "";
    document.getElementById("tourDesc").value = data.tourDesc || "";
    document.getElementById("tourPeriod").value = data.tourPeriod || "";
    document.getElementById("tourRegion").value = data.tourRegion || "";
    document.getElementById("maximum").value = data.maximum || "";
    document.getElementById("tourPrice").value = data.tourPrice || "";

    // 카테고리 체크박스 선택 처리
    if (data.categoryResDtoList && data.categoryResDtoList.length > 0) {
        data.categoryResDtoList.forEach(category => {
            const checkbox = document.querySelector(`#categoryCheckBox input[value="${category.id}"]`);
            if (checkbox) checkbox.checked = true;
        });
    }

    // 이미지 미리보기 처리
    populateImagePreview(data.itemImgResDtoList);
}

// 이미지 미리보기 처리
function populateImagePreview(images) {
    const previewContainer = document.getElementById("previewContainer");
    previewContainer.innerHTML = ""; // 초기화

    if (images) {
        images.forEach((image) => {
            const imgContainer = document.createElement("div");
            imgContainer.style.position = "relative";

            const img = document.createElement("img");
            img.src = image.imgUrl; // 서버에서 반환된 이미지 URL
            img.dataset.url = image.imgUrl; // 이미지 URL 저장
            img.alt = image.imgName; // 이미지 이름 설정
            imgContainer.appendChild(img);

            const deleteButton = document.createElement("button");
            deleteButton.textContent = "삭제";
            deleteButton.classList.add("remove-image");
            deleteButton.style.position = "absolute";
            deleteButton.style.top = "5px";
            deleteButton.style.right = "5px";

            // 이미지 ID를 데이터 속성으로 저장
            deleteButton.setAttribute("data-image-id", image.id);

            deleteButton.onclick = () => {
                const imageId = deleteButton.getAttribute("data-image-id"); // 이미지 ID 가져오기

                // 화면에서 삭제
                imgContainer.remove();
            };

            imgContainer.appendChild(deleteButton);
            previewContainer.appendChild(imgContainer);
        });
    }
}


