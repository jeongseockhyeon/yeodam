document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("submitBtn").addEventListener("click", () => {
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
        formData.append("maximum", parseInt(document.getElementById("maximum").value))
        formData.append("tourPrice", parseInt(document.getElementById("tourPrice").value));
        formData.append("categoryIdList", JSON.stringify(selectedCategories));

        // 이미지 파일 추가
        const imageFiles = document.getElementById("tourImages").files;
        for (let i = 0; i < imageFiles.length; i++) {
            formData.append("tourImages", imageFiles[i]);
        }

        fetch("/api/tours", {
            method: "POST",
            body: formData, // FormData 사용
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("HTTP error " + response.status);
                }
                return response.json();
            })
            .then(data => {
                alert("상품이 성공적으로 등록되었습니다!");
                console.log("Response:", data);
                window.location.href = "/item/manage";
            })
            .catch(error => {
                alert("등록 중 오류가 발생했습니다.");
                console.error("Error:", error);
            });
    });
});