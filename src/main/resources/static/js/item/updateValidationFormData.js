function updateValidationFormData() {
    const requiredFields = ["tourName", "tourDesc", "tourPeriod", "tourRegion", "maximum", "tourPrice"];
    const koFieldName = ["여행 상품명", "설명", "기간", "지역", "최대 인원", "가격"];
    let hasError = false;

    // 필수 입력 항목 검사
    for (let i = 0; i < requiredFields.length; i++) {
        const fieldId = requiredFields[i];
        const field = document.getElementById(fieldId);
        if (!field.value.trim()) {
            const errorMessage = `${koFieldName[i]}를 입력해주세요.`;
            displayValidationError(field, errorMessage);
            alert(errorMessage);
            field.focus();
            return false; // 즉시 반환
        }
    }

    // 최대 인원 및 가격 값 검사
    const maximumField = document.getElementById("maximum");
    const tourPriceField = document.getElementById("tourPrice");
    const maximum = parseInt(maximumField.value, 10);
    const tourPrice = parseInt(tourPriceField.value, 10);

    if (isNaN(maximum) || maximum <= 0) {
        const errorMessage = "최대 인원은 양수로 입력해주세요.";
        displayValidationError(maximumField, errorMessage);
        alert(errorMessage);
        maximumField.focus();
        return false;
    }
    if (isNaN(tourPrice) || tourPrice < 0) {
        const errorMessage = "가격은 양수로 입력해주세요.";
        displayValidationError(tourPriceField, errorMessage);
        alert(errorMessage);
        tourPriceField.focus();
        return false;
    }

    // 이미지 파일 이름 유효성 검증
    const tourImagesInput = document.getElementById("tourImages");
    const files = Array.from(tourImagesInput.files);
    const invalidFileNames = files.filter(file => /[%<>:"/\\|?*]/.test(file.name));

    if (invalidFileNames.length > 0) {
        const errorMessage = `다음 파일의 이름에 허용되지 않는 특수문자가 포함되어 있습니다:\n${invalidFileNames.map(file => file.name).join("\n")}\n파일 이름을 수정해주세요.`;
        alert(errorMessage);
        tourImagesInput.focus();
        return false;
    }

    // 선택된 카테고리 검사
    const selectedCategories = Array.from(
        document.querySelectorAll('input[name="categories"]:checked')
    ).map(checkbox => parseInt(checkbox.value));

    if (selectedCategories.length === 0) {
        const errorMessage = "카테고리를 선택해주세요.";
        const categoryField = document.getElementById("categoryCheckBox");
        displayValidationError(categoryField, errorMessage);
        alert(errorMessage);
        categoryField.focus();
        return false;
    }

    // 가이드 선택 검사
    if (existingGuides.length === 0 && selectedGuides.length === 0) {
        const errorMessage = "가이드를 선택해주세요.";
        const guideField = document.getElementById("guideSelect");
        displayValidationError(guideField, errorMessage);
        alert(errorMessage);
        guideField.focus();
        return false;
    }

    // 이미지 업로드 검사
    if (existingImages.length === 0 && selectedImages.length === 0) {
        const errorMessage = "이미지를 업로드해주세요.";
        displayValidationError(tourImagesInput, errorMessage);
        alert(errorMessage);
        tourImagesInput.focus();
        return false;
    }

    return true;
}

function displayValidationError(field, message) {
    const errorElement = document.createElement("p");
    errorElement.textContent = message;
    errorElement.classList.add("error-message");
    errorElement.style.color = "red";

    // 기존 에러 메시지 제거
    const existingError = field.nextElementSibling;
    if (existingError && existingError.classList.contains("error-message")) {
        existingError.remove();
    }

    // 필드 바로 아래에 에러 메시지 삽입
    field.insertAdjacentElement("afterend", errorElement);
}
