document.addEventListener("DOMContentLoaded", () => {
    const id = getTourItemIdFromUrl(); // URL에서 상품 ID를 추출
    fetch(`/api/tours/${id}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("상품 데이터를 불러오지 못했습니다.");
            }
            return response.json();
        })
        .then(data => populateForm(data))
        .catch(error => {
            console.error("Error fetching product data:", error);
            alert("상품 정보를 불러오는 데 문제가 발생했습니다.");
        });
});

function getTourItemIdFromUrl() {
    const pathSegments = window.location.pathname.split('/');
    return pathSegments[2];
    }

function populateForm(data) {
    document.getElementById("tourName").value = data.tourName || "";
    document.getElementById("tourDesc").value = data.tourDesc || "";
    document.getElementById("tourPeriod").value = data.tourPeriod || "";
    document.getElementById("tourRegion").value = data.tourRegion || "";
    document.getElementById("maximum").value = data.maximum || "";
    document.getElementById("tourPrice").value = data.tourPrice || "";

    // 카테고리 체크박스 선택 처리
    if (data.categories) {
        data.categories.forEach(categoryId => {
            const checkbox = document.querySelector(`#categoryCheckBox input[value="${categoryId}"]`);
            if (checkbox) checkbox.checked = true;
        });
    }

    // 이미지 미리보기
    if (data.images) {
        data.images.forEach(imageUrl => {
            const img = document.createElement("img");
            img.src = imageUrl; // 서버에서 반환된 이미지 URL
            previewContainer.appendChild(img);
        });
    }
}