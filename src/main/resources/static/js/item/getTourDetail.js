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
const tourImagesInput = document.getElementById('tourImages');
const previewContainer = document.getElementById('previewContainer');
let selectedImages = []; // 새로 업로드된 파일 배열
let existingImages = []; // 서버에서 가져온 기존 이미지 배열

// 미리보기 업데이트 함수
function updatePreview() {
    previewContainer.innerHTML = ""; // 기존 미리보기 초기화

    // 기존 이미지와 새로 업로드된 파일 모두 처리
    [...existingImages, ...selectedImages].forEach((item) => {
        createImagePreview(item, item.id ? "existing" : "new");
    });
}

// 이미지 미리보기 생성 함수
function createImagePreview(image, type) {
    const fragment = document.createDocumentFragment(); // DocumentFragment 생성

    const imgContainer = document.createElement("div");
    imgContainer.style.position = "relative";

    const img = document.createElement("img");
    if (type === "existing") {
        img.src = image.imgUrl; // 기존 이미지
        img.alt = image.imgName;
    } else {
        const reader = new FileReader();
        reader.onload = () => {
            img.src = reader.result; // 새로 업로드된 이미지
        };
        reader.readAsDataURL(image);
    }
    imgContainer.appendChild(img);

    const deleteButton = document.createElement("button");
    deleteButton.textContent = "삭제";
    deleteButton.className = "remove-image";
    deleteButton.style.position = "absolute";
    deleteButton.style.top = "5px";
    deleteButton.style.right = "5px";

    if (type === "existing") {
        deleteButton.setAttribute("data-image-id", image.id); // 기존 이미지의 ID 설정
    }

    deleteButton.onclick = () => {
        if (type === "existing") {
            // 기존 이미지 삭제
            existingImages = existingImages.filter((existing) => existing.id !== image.id);
        } else {
            // 새로 업로드된 이미지 삭제
            selectedImages = selectedImages.filter((file) => file !== image);
        }
        imgContainer.remove(); // 화면에서 삭제
    };

    imgContainer.appendChild(deleteButton);
    fragment.appendChild(imgContainer); // Fragment에 추가

    previewContainer.appendChild(fragment); // Fragment를 DOM에 추가
}

// 파일 입력 이벤트 처리
tourImagesInput.addEventListener('change', () => {
    const files = Array.from(tourImagesInput.files);

    // 새로 추가된 파일만 필터링
    const newFiles = files.filter(file =>
        !existingImages.some(existingImage => existingImage.imgName === file.name) &&
        !selectedImages.some(selectedImage => selectedImage.name === file.name)
    );

    selectedImages = [...selectedImages, ...newFiles];
    updatePreview(); // 미리보기 갱신
});

// 초기 데이터로 미리보기 채우기
function populateImagePreview(images) {
    existingImages = images; // 서버에서 가져온 기존 이미지 설정
    updatePreview();
}

