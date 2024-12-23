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
    const tourPrice = parseInt(document.getElementById("toruPrice").value, 10)
    if (isNaN(maximum) || maximum <= 0){
        alert("최대 인원은 양수로 입력해주세요.");
        getElement("maximum").focus();
        return false;
    }
    if (isNaN(tourPrice) || tourPrice < 0){
        alert("가격은 양수로 입력해주세요.");
        getElement("tourPrice").focus();
        return false;
    }

    return true;
}