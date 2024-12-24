const removeCategoryList = new Set();

// 카테고리 체크박스 상태 변경 이벤트 리스너
document.getElementById("categoryCheckBox").addEventListener("change", (event) => {
    const target = event.target;
    if (target.name === "categories") {
        if (!target.checked) {
            removeCategoryList.add(parseInt(target.value)); // 체크 해제된 카테고리 추가
        } else {
            removeCategoryList.delete(parseInt(target.value)); // 다시 체크하면 제거
        }
    }
});

document.getElementById("submitBtn").addEventListener("click", () => {
    if (!validationFormData()) {
        return;
    }

    // 선택된 카테고리
    const selectedCategories = Array.from(
        document.querySelectorAll('input[name="categories"]:checked')
    ).map(checkbox => parseInt(checkbox.value));

    // FormData 객체 생성
    const formData = new FormData();

    // 기본 폼 데이터 추가
    formData.append("tourName", document.getElementById("tourName").value);
    formData.append("tourDesc", document.getElementById("tourDesc").value);
    formData.append("tourPeriod", document.getElementById("tourPeriod").value);
    formData.append("tourRegion", document.getElementById("tourRegion").value);
    formData.append("maximum", parseInt(document.getElementById("maximum").value));
    formData.append("tourPrice", parseInt(document.getElementById("tourPrice").value));

    // 선택된 카테고리 및 제거된 카테고리 추가
    formData.append("addCategoryIds", JSON.stringify(selectedCategories));
    formData.append("removeCategoryIds", JSON.stringify(Array.from(removeCategoryList)));

    // 이미지 파일 추가
    const imageFiles = document.getElementById("tourImages").files;
    for (let i = 0; i < imageFiles.length; i++) {
        formData.append("tourImages", imageFiles[i]);
    }

    const id = getTourItemIdFromUrl();

    fetch(`/api/tours/${id}`, {
        method: "PATCH",
        body: formData,
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("HTTP error " + response.status);
            }
            return response.json();
        })
        .then(data => {
            alert("상품이 성공적으로 수정되었습니다!");
            console.log("Response:", data);
            window.location.href = "/item/manage";
        })
        .catch(error => {
            alert("수정 중 오류가 발생했습니다.");
            console.error("Error:", error);
        });
});
function getTourItemIdFromUrl() {
    const pathSegments = window.location.pathname.split('/');
    return pathSegments[2]; // URL에서 ID 추출
}