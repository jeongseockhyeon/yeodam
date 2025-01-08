document.addEventListener("DOMContentLoaded", () => {
    const id = getTourItemIdFromUrl(); // URL에서 상품 ID 추출

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

    // 기존 가이드 업데이트
    if (data.guideInTourResDtos && data.guideInTourResDtos.length > 0) {
        existingGuides = data.guideInTourResDtos;
    }

    updateSelectedGuidesText();

}

// 이미지 미리보기 처리
const tourImagesInput = document.getElementById('tourImages');
const previewContainer = document.getElementById('previewContainer');
let selectedImages = []; // 새로 업로드된 파일 배열
let existingImages = []; // 서버에서 가져온 기존 이미지 배열

// 미리보기 업데이트 함수
function updatePreview() {
    // 기존 미리보기 내용 초기화
    previewContainer.innerHTML = "";

    // 기존 이미지와 새로 업로드된 파일 모두 처리
    [...existingImages, ...selectedImages].forEach((item) => {
        const imgContainer = createImagePreview(item, item.id ? "existing" : "new");
        previewContainer.appendChild(imgContainer); // 미리보기 컨테이너에 추가
    });
}

// 이미지 미리보기 생성 함수
function createImagePreview(image, type) {
    const imgContainer = document.createElement("div");
    imgContainer.style.position = "relative";

    const img = document.createElement("img");
    img.src = type === "existing" ? image.imgUrl : URL.createObjectURL(image);
    img.alt = image.imgName || image.name;
    img.className = "preview-img"

    const deleteButton = document.createElement("button");
    deleteButton.textContent = "삭제";
    deleteButton.className = "remove-image";
    if (type === "existing") {
        deleteButton.dataset.imageId = image.id; // 기존 이미지의 ID 설정
    } else {
        deleteButton.dataset.imageFile = image; // 새로 업로드된 파일 참조
    }

    imgContainer.appendChild(img);
    imgContainer.appendChild(deleteButton);
    return imgContainer;
}

// 삭제 버튼 이벤트 위임
previewContainer.addEventListener("click", (event) => {
    if (event.target.classList.contains("remove-image")) {
        const imageId = event.target.dataset.imageId;
        const imageFile = event.target.dataset.imageFile;

        if (imageId) {
            // 기존 이미지 삭제
            existingImages = existingImages.filter(img => img.id !== parseInt(imageId));
        } else if (imageFile) {
            // 새로 업로드된 이미지 삭제
            selectedImages = selectedImages.filter(file => file.name !== imageFile.name);
        }

        // 삭제 버튼의 부모 요소 제거
        event.target.closest("div").remove();
    }
});

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

// 기존 코드에서 가이드 선택과 제거 처리
let existingGuides = []; // 기존 가이드 배열
let selectedGuides = []; // 새로 추가된 가이드 배열
let selectedGuideNames = []; // 새로 추가된 가이드 이름 배열

const selectedGuidesText = document.getElementById("selectedGuidesText");
const guideSelect = document.getElementById("guideSelect");

const addGuideList = new Set(); // 새로 추가된 가이드 목록
const removeGuideList = new Set(); // 제거된 가이드 목록

// 가이드 텍스트 업데이트 함수
function updateSelectedGuidesText() {
    selectedGuidesText.innerHTML = ""; // 기존 내용을 초기화

    // 기존 가이드 추가
    existingGuides.forEach((guide) => {
        const guideElement = createGuideElement(guide, "existing");
        selectedGuidesText.appendChild(guideElement);
    });

    // 새로 추가된 가이드 추가
    selectedGuides.forEach((guide, index) => {
        const guideElement = createGuideElement({ id: guide, name: selectedGuideNames[index] }, "new");
        selectedGuidesText.appendChild(guideElement);
    });

    // 선택된 가이드가 없으면 기본 메시지 표시
    if (existingGuides.length === 0 && selectedGuides.length === 0) {
        selectedGuidesText.textContent = "선택된 가이드가 없습니다.";
    }
}

// 가이드 텍스트 생성 함수
function createGuideElement(guide, type) {
    const guideSpan = document.createElement("span");
    guideSpan.style.marginRight = "10px";

    const guideName = document.createElement("span");
    guideName.textContent = guide.name;

    const removeButton = document.createElement("button");
    removeButton.textContent = "X";
    removeButton.style.marginLeft = "5px";
    removeButton.style.cursor = "pointer";

    // 제거 버튼 클릭 이벤트
    removeButton.addEventListener("click", () => {
        if (type === "existing") {
            // 기존 가이드에서 삭제 처리
            existingGuides = existingGuides.filter((existing) => existing.id !== guide.id);
            removeGuideList.add(guide.id); // 제거된 가이드 목록에 추가
        } else {
            const index = selectedGuides.indexOf(guide.id);
            if (index > -1) {
                selectedGuides.splice(index, 1);
                selectedGuideNames.splice(index, 1);
            }
        }
        updateSelectedGuidesText();
    });

    guideSpan.appendChild(guideName);
    guideSpan.appendChild(removeButton);

    return guideSpan;
}

// 새로운 가이드 추가 함수
guideSelect.addEventListener("change", () => {
    const selectedOptions = Array.from(guideSelect.selectedOptions);
    selectedOptions.forEach(option => {
        const guideId = parseInt(option.value); // 선택된 가이드 ID
        const guideName = option.text; // 선택된 가이드 이름

        // 기존 가이드나 이미 선택된 가이드에 포함되어 있지 않으면 추가
        if (!existingGuides.some(guide => guide.id === guideId) && !selectedGuides.includes(guideId)) {
            selectedGuides.push(guideId);
            selectedGuideNames.push(guideName);
            addGuideList.add(guideId); // 새로 추가된 가이드를 addGuideList에 추가
        }
    });

    guideSelect.selectedIndex = -1; // 드롭다운 초기화
    updateSelectedGuidesText();
});