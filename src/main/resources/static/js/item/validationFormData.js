function validationFormData() {
    const requiredFields = ["tourName", "tourDesc", "tourPeriod", "tourRegion", "maximum", "tourPrice"];
    const koFieldName = ["여행 상품명", "설명", "기간", "지역", "최대 인원", "가격"];

    for (let i = 0; i < requiredFields.length; i++) {
        const fieldId = requiredFields[i];
        const field = document.getElementById(fieldId);
        if (!field.value.trim()) {
            alert(`"${koFieldName[i]}"를 입력해주세요.`);
            field.focus();
            return false;
        }
    }

    const maximum = parseInt(document.getElementById("maximum").value, 10);
    const tourPrice = parseInt(document.getElementById("tourPrice").value, 10);
    if (isNaN(maximum) || maximum <= 0) {
        alert("최대 인원은 양수로 입력해주세요.");
        document.getElementById("maximum").focus();
        return false;
    }
    if (isNaN(tourPrice) || tourPrice < 0) {
        alert("가격은 양수로 입력해주세요.");
        document.getElementById("tourPrice").focus();
        return false;
    }

    // 이미지 파일 이름 유효성 검증
    const tourImagesInput = document.getElementById("tourImages");
    const files = Array.from(tourImagesInput.files);
    const invalidFileNames = files.filter(file => /[%<>:"/\\|?*]/.test(file.name));

    if (invalidFileNames.length > 0) {
        alert(`다음 파일의 이름에 허용되지 않는 특수문자가 포함되어 있습니다:\n${invalidFileNames.map(file => file.name).join("\n")}\n파일 이름을 수정해주세요.`);
        return false;
    }

    return true;
}